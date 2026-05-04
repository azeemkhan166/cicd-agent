


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.realcoderz.model.Allowance;
import com.realcoderz.repository.AllowanceRepository;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.OtherSectionApprovedRepository;
import com.realcoderz.repository.OtherSectionRepository;
import com.realcoderz.repository.StandardDeductionRepository;
import com.realcoderz.service.ExemptionsService;
import com.realcoderz.service.TaxService;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 * edited By Astha
 */
@Service
public class ExemptionsServiceImpl implements ExemptionsService {

    static final Logger logger = LoggerFactory.getLogger(ExemptionsServiceImpl.class);

    @Autowired
    private InvestmentDeclarationRepository investmentDeclarationRepo;

    @Autowired
    private OtherSectionRepository otherSectionRepository;
    
    @Autowired
    private OtherSectionApprovedRepository otherSectionApprovedRepository;

    @Autowired
    private TaxService taxService;

    @Autowired
    private AllowanceRepository allowanceRepo;

    @Autowired
    private StandardDeductionRepository standardDeductionrepo;

//    Method to get the Exemptions
    @Override
    public Map getExemptions(Map map) {
      System.out.println("getExemptions "+map);
        Map resultMap = new HashMap<>();
        Map taxResult = new HashMap<>();
        try {
            long eid = 0;
            long oid = 0;
            double Exemptions_under_sec_VIA = 0;
            double Income_loss_house_property = 0;
            double Exemptions_under_sec_10 = 0;
            double standard = 0.0;
            double standardAmount = 0.0;
            double Exemptions_under_sec_VIA_useOnlyCalculation=0.0;
            int totalMonth = Integer.parseInt(map.get("total_month").toString());
            double otherDeduction=0;
            double incomeFromPrevEmp=0;
            boolean monthFlag = false;
//            if(Integer.parseInt(map.get("month").toString())==4){
//                monthFlag = true;
//            }
            
            LinkedCaseInsensitiveMap standardDeduction = standardDeductionrepo.getstandDeductionAmount();
            if (standardDeduction != null) {
                String standardDeductionLocal = standardDeduction.get("standard_deduction").toString();
                standardAmount = Math.round(Double.parseDouble(standardDeduction.get("standard_deduction").toString()));
//                int totalOneYear_Month = 12;
                 standard = Math.round((Double.parseDouble(standardDeductionLocal)));
//                double oneMonthAmount = (standardDedu / totalOneYear_Month);
//                standard = (Math.round((oneMonthAmount * totalMonth)));
                
            } else {
                resultMap.clear();
                resultMap.put("msg", "please fill standard deduction form ");
                resultMap.put("status", "error");
                return resultMap;
            }
//            Organization id exist
            if (map.containsKey("organization_id") && map.get("organization_id") != null) {
                oid = Long.parseLong(map.get("organization_id").toString());
                int month= Integer.parseInt(map.get("month").toString());
                int year=0;
                if(month==1||month==2||month==3){
                    year= Integer.parseInt(map.get("year").toString())-1;
                }else{
                    year= Integer.parseInt(map.get("year").toString()); 
                }
                System.out.println("year"+" "+" "+year);
              //  int year= Integer.parseInt(map.get("year").toString());
                 
                List<Allowance> allowance = allowanceRepo.findApprovedAllowances(Long.parseLong(map.get("organization_id").toString()), new Date(year-1900,month,1),map.get("employeeType").toString());
                 

                for (Allowance a : allowance) {
                    if (a.getAllowance_name().equals("Transport Allowance")) {
                        Exemptions_under_sec_10 += (0.0);

                    }
                }
                  

//                Employee id exist
                if (map.containsKey("emp_id") && map.get("emp_id") != null) {
                    eid = Long.parseLong(map.get("emp_id").toString());
//                    Total of 80c

                    LinkedCaseInsensitiveMap total = investmentDeclarationRepo.get80cTotal(eid, oid,year);
                    System.out.println("80c total==="+total);
                    double total_DeclaredValue=total !=null? Double.parseDouble(total.get("total_allowances").toString()) :0;
                    

                     total_DeclaredValue= map.get("exemptions_sec_10_Total")!=null?Double.parseDouble(map.get("exemptions_sec_10_Total").toString()):total_DeclaredValue;

                    if (total != null) {
                        if (total.containsKey("total_allowances") && total.get("total_allowances") != null) {
                            double total_ExemptionValue=150000.0;
                            if(total_DeclaredValue >=total_ExemptionValue)
                            {
                              Exemptions_under_sec_VIA_useOnlyCalculation += Math.round(total_ExemptionValue);
                             
                            }
                            else
                            {
//                              Exemptions_under_sec_VIA_useOnlyCalculation += Math.round((Math.round(((double) total.get("total_allowances")) * 100.0)) / 100.0);
                                                              Exemptions_under_sec_VIA_useOnlyCalculation += total_DeclaredValue;

                              
                            }
                            
//                              Exemptions_under_sec_VIA += Math.round((Math.round(((double) total.get("total_allowances")) * 100.0)) / 100.0);
                              Exemptions_under_sec_VIA += total_DeclaredValue;

 
//                        Interest on Housing Loan
                            LinkedCaseInsensitiveMap interest = otherSectionRepository.getInterestOnHousingLoanOfParticularYear(eid, oid,year);
                                                        LinkedCaseInsensitiveMap otherSec = otherSectionApprovedRepository.getOtherSectionPreviousVersion(eid, oid,year);
                                                        if(otherSec!=null ){
                                                        incomeFromPrevEmp=Double.parseDouble(otherSec.get("income_from_previous_employer").toString());
                                                        if(map.get("otherSec")!=null)
                                                            otherDeduction=Double.parseDouble(map.get("otherSec").toString());
                                                        else{                                                     
                                                          double nps=Double.parseDouble(otherSec.get("national_pension_scheme").toString());
                                                          nps=nps>50000?50000:nps;
                                                          
                                                          double sec80d=Double.parseDouble(otherSec.get("sec80d").toString());

                                                      // String type=otherSec.get("sec80d_type").toString();
                                                          String type=otherSec.get("sec80d_type") !=null ? otherSec.get("sec80d_type").toString():"";
                                                         // String type=otherSec.get("sec80d_type") !=null ? otherSec.get("sec80d_type").toString():"";
                                                          double limit=50000;

                                                          if(type.equals("self&family"))
                                                              limit=25000;
                                                          sec80d=sec80d>limit?limit:sec80d;
                                                            otherDeduction=sec80d+Double.parseDouble(otherSec.get("sec80g").toString())+Double.parseDouble(otherSec.get("sec80e").toString())+Double.parseDouble(otherSec.get("sec80u").toString())+Double.parseDouble(otherSec.get("sec80dd").toString())+nps;

                                                        }
                                                        }

                            
                           
                            double housingLoan_Exemption=200000;
                            if(interest !=null && interest.containsKey("interest_on_housing_loan_before") && interest.get("interest_on_housing_loan_before")!=null){
                            double housingLoan_Declared=Double.parseDouble(interest.get("interest_on_housing_loan_before").toString());
                                                        System.out.println("house loan from db===="+housingLoan_Declared);

                                                         housingLoan_Declared=map.get("homeLoanTotal")!=null? Double.parseDouble(map.get("homeLoanTotal").toString()):housingLoan_Declared;

                            
                            System.out.println("house loan from db===="+housingLoan_Declared);
                             if((housingLoan_Declared) >=(housingLoan_Exemption))
                                {
                                  
                                    Income_loss_house_property += (Math.round(housingLoan_Exemption));
                                
                             
                                }
                             else
                             {
                               if (interest != null) {
                                   System.out.println("intrest==="+interest);
                                if (interest.containsKey("interest_on_housing_loan_before") && interest.get("interest_on_housing_loan_before") != null) {
//                                    Income_loss_house_property += (Math.round(((double) interest.get("interest_on_housing_loan_before")) * 100) / 100);
                                    Income_loss_house_property += housingLoan_Declared;

                                }
                            }  
                             }
                            }                            
                        }
                    } else {
                        logger.info("Problem in ExemptionsServiceImpl -> getExemptions() :: Kindly fill the investment declaration form..!");
                    }

                } else {
                    resultMap.clear();
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Employee doesn't exist..!");
                    return resultMap;
                }
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Organization doesn't exist..!");
                return resultMap;
            }
            Map taxData = new HashMap();
            taxData.put("emp_id", map.get("emp_id"));
            taxData.put("organization_id", map.get("organization_id"));
            taxData.put("Exemptions_under_sec_VIA", !monthFlag ? Exemptions_under_sec_VIA : 0);
            taxData.put("Exemptions_under_sec_VIA_useOnlyCalculation", !monthFlag ? Exemptions_under_sec_VIA_useOnlyCalculation : 0);
            taxData.put("Exemptions_under_sec_10", !monthFlag ? Exemptions_under_sec_10 : 0);
            taxData.put("Income_loss_house_property", !monthFlag ? Income_loss_house_property : 0);
            taxData.put("where", map.get("where"));
            taxData.put("Deduction16", standard);
            taxData.put("JoiningMonth", map.get("JoiningMonth"));
            taxData.put("sub_total", map.get("sub_total"));
            taxData.put("standardAmount", standardAmount);
            taxData.put("totalMonth", totalMonth);
            taxData.put("taxableHRA", map.get("taxableHRA"));
            //taxData.put("taxableHRA", 168000);
            taxData.put("Least_of_above_is_exempt", map.get("Least_of_above_is_exempt"));
            taxData.put("tillDateDedMonth",map.get("tillDateDedMonth"));
            taxData.put("month", map.get("month"));
            taxData.put("year", map.get("year"));
            taxData.put("empJoinYear",  map.get("empJoinYear"));
            taxData.put("isSaved",  map.get("isSaved"));
            taxData.put("TaxSlabType",  map.get("TaxSlabType"));
            taxData.put("diffrence_age",  map.get("diffrence_age"));
            taxData.put("exemptions_sec_10_Total", !monthFlag ? map.get("exemptions_sec_10_Total") : 0);
            taxData.put("homeLoanTotal", !monthFlag ? map.get("homeLoanTotal") : 0);
            taxData.put("InversmentDeclaration_key", map.get("InversmentDeclaration_key"));
//            taxData.put("current_employee_age",  map.get("current_employee_age"));           
            resultMap.put("Exemptions_under_sec_VIA", !monthFlag ? Math.round(Exemptions_under_sec_VIA) : 0);
//            resultMap.put("Exemptions_under_sec_10", Math.round(Exemptions_under_sec_10));
            resultMap.put("Exemptions_under_sec_10", !monthFlag ? Math.round(otherDeduction) : 0);

            resultMap.put("Deduction16", Math.round(standard));
            resultMap.put("Income_loss_house_property", !monthFlag ? Math.round(Income_loss_house_property) : 0);
                        taxData.put("otherSec", !monthFlag ? Math.round(otherDeduction): 0);
                                                taxData.put("income_from_previous_employer", !monthFlag ? Math.round(incomeFromPrevEmp): 0);


                                System.out.println("result map===="+ taxData);

            taxResult = taxService.getTax(taxData);
            resultMap.put("tax", taxResult);
            resultMap.put("status", "success");
                    System.out.println("result map===="+ taxData);
                    System.out.println("taxResult"+" "+taxResult.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            logger.info("Problem in ExemptionsServiceImpl -> getExemptions() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map saveExemptions(Map map) {
        Map resultMap = new HashMap<>();
        return resultMap;
    }
    
    @Override
    public Map getExemptionsPreviousVersion(Map map) {
        System.out.println("getExemptions " + map);
        Map resultMap = new HashMap<>();
        Map taxResult = new HashMap<>();
        try {
            long eid = 0;
            long oid = 0;
            double Exemptions_under_sec_VIA = 0;
            double Income_loss_house_property = 0;
            double Exemptions_under_sec_10 = 0;
            double standard = 0.0;
            double standardAmount = 0.0;
            double Exemptions_under_sec_VIA_useOnlyCalculation = 0.0;
            int totalMonth = Integer.parseInt(map.get("total_month").toString());
            double otherDeduction = 0;
            double incomeFromPrevEmp = 0;
            int year = Integer.parseInt(map.get("year").toString());
            if (map.get("TaxSlabType").toString().equalsIgnoreCase("NewTaxSlabKey")) {
                if (Integer.parseInt(map.get("month").toString()) < 3) {
                    year = year - 1;
                }
            }
//            
            LinkedCaseInsensitiveMap standardDeduction = standardDeductionrepo.getstandDeductionAmount();
            if (standardDeduction != null) {
                String standardDeductionLocal = standardDeduction.get("standard_deduction").toString();
                standardAmount = Math.round(Double.parseDouble(standardDeduction.get("standard_deduction").toString()));
//                int totalOneYear_Month = 12;
                standard = Math.round((Double.parseDouble(standardDeductionLocal)));
//                double oneMonthAmount = (standardDedu / totalOneYear_Month);
//                standard = (Math.round((oneMonthAmount * totalMonth)));

            } else {
                resultMap.clear();
                resultMap.put("msg", "please fill standard deduction form ");
                resultMap.put("status", "error");
                return resultMap;
            }
//            Organization id exist
            if (map.containsKey("organization_id") && map.get("organization_id") != null) {
                oid = Long.parseLong(map.get("organization_id").toString());
                int month = Integer.parseInt(map.get("month").toString());
                List<Allowance> allowance = allowanceRepo.findApprovedAllowances(Long.parseLong(map.get("organization_id").toString()), new Date(year - 1900, month, 1), map.get("employeeType").toString());

                for (Allowance a : allowance) {
                    if (a.getAllowance_name().equals("Transport Allowance")) {
                        Exemptions_under_sec_10 += (0.0);

                    }
                }

//                Employee id exist
                if (map.containsKey("emp_id") && map.get("emp_id") != null) {
                    eid = Long.parseLong(map.get("emp_id").toString());
//                    Total of 80c
                    LinkedCaseInsensitiveMap total = investmentDeclarationRepo.get80cTotal(eid, oid, year);
                    System.out.println("80c total===" + total);
                    double total_DeclaredValue = total != null ? Double.parseDouble(total.get("total_allowances").toString()) : 0;

                    total_DeclaredValue = map.get("exemptions_sec_10_Total") != null ? Double.parseDouble(map.get("exemptions_sec_10_Total").toString()) : total_DeclaredValue;

                    if (total_DeclaredValue != 0 && !map.get("TaxSlabType").toString().equalsIgnoreCase("NewTaxSlabKey")) {
//                        if (total.containsKey("total_allowances") && total.get("total_allowances") != null) {
                        double total_ExemptionValue = 150000.0;
                        if (total_DeclaredValue >= total_ExemptionValue) {
                            Exemptions_under_sec_VIA_useOnlyCalculation += Math.round(total_ExemptionValue);

                        } else {
//                              Exemptions_under_sec_VIA_useOnlyCalculation += Math.round((Math.round(((double) total.get("total_allowances")) * 100.0)) / 100.0);
                            Exemptions_under_sec_VIA_useOnlyCalculation += total_DeclaredValue;

                        }

//                              Exemptions_under_sec_VIA += Math.round((Math.round(((double) total.get("total_allowances")) * 100.0)) / 100.0);
                        Exemptions_under_sec_VIA += total_DeclaredValue;

//                        Interest on Housing Loan
                        LinkedCaseInsensitiveMap interest = otherSectionRepository.getInterestOnHousingLoanPreviousVersion(eid, oid, year);
                        LinkedCaseInsensitiveMap otherSec = otherSectionApprovedRepository.getOtherSectionPreviousVersion(eid, oid, year);
                        if (map.get("otherSec") != null) {
                            otherDeduction = Double.parseDouble(map.get("otherSec").toString());
                        } else {
                            double nps = Double.parseDouble(otherSec.get("national_pension_scheme").toString());
                            nps = nps > 50000 ? 50000 : nps;

                            double sec80d = Double.parseDouble(otherSec.get("sec80d").toString());
                            //   String type=otherSec.get("sec80d_type").toString();
                            //demochanges
                            String type = otherSec.get("sec80d_type") != null ? otherSec.get("sec80d_type").toString() : "";
                            double limit = 50000;
                            if (type.equals("self&family")) {
                                limit = 25000;
                            }
                            sec80d = sec80d > limit ? limit : sec80d;
                            otherDeduction = sec80d + Double.parseDouble(otherSec.get("sec80g").toString()) + Double.parseDouble(otherSec.get("sec80e").toString()) + Double.parseDouble(otherSec.get("sec80u").toString()) + Double.parseDouble(otherSec.get("sec80dd").toString()) + nps;

                        }
                        if (otherSec != null) {
                            incomeFromPrevEmp = Double.parseDouble(otherSec.get("income_from_previous_employer").toString());
                        }else{
                            if(map.get("incomeFromPreviousEmployer")!=""){
                            incomeFromPrevEmp = Double.parseDouble(map.get("incomeFromPreviousEmployer").toString());
                            }
                        }

                        double housingLoan_Exemption = 200000;
                            double housingLoan_Declared = interest!=null && interest.containsKey("interest_on_housing_loan_before") ? Double.parseDouble(interest.get("interest_on_housing_loan_before").toString()) : Double.parseDouble(map.get("homeLoanTotal").toString());
                            System.out.println("house loan from db====" + housingLoan_Declared);

                            housingLoan_Declared = map.get("homeLoanTotal") != null ? Double.parseDouble(map.get("homeLoanTotal").toString()) : housingLoan_Declared;

                            System.out.println("house loan from db====" + housingLoan_Declared);
                            if ((housingLoan_Declared) >= (housingLoan_Exemption)) {

                                Income_loss_house_property += (Math.round(housingLoan_Exemption));

                            } else {
                                if (interest != null) {
                                    System.out.println("intrest===" + interest);
                                    if (interest.containsKey("interest_on_housing_loan_before") && interest.get("interest_on_housing_loan_before") != null) {
//                                    Income_loss_house_property += (Math.round(((double) interest.get("interest_on_housing_loan_before")) * 100) / 100);
                                        Income_loss_house_property += housingLoan_Declared;

                                    }
                                }else{
                                    Income_loss_house_property+=housingLoan_Declared;
                                }
                            }
//                        }
                    } else {
                        logger.info("Problem in ExemptionsServiceImpl -> getExemptions() :: Kindly fill the investment declaration form..!");
                    }

                } else {
                    resultMap.clear();
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Employee doesn't exist..!");
                    return resultMap;
                }
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Organization doesn't exist..!");
                return resultMap;
            }
            Map taxData = new HashMap();
            taxData.put("emp_id", map.get("emp_id"));
            taxData.put("organization_id", map.get("organization_id"));
            taxData.put("Exemptions_under_sec_VIA", Exemptions_under_sec_VIA);
            taxData.put("Exemptions_under_sec_VIA_useOnlyCalculation", Exemptions_under_sec_VIA_useOnlyCalculation);
            taxData.put("Exemptions_under_sec_10", Exemptions_under_sec_10);
            taxData.put("Income_loss_house_property", Income_loss_house_property);
            taxData.put("where", map.get("where"));
            taxData.put("Deduction16", standard);
            taxData.put("JoiningMonth", map.get("JoiningMonth"));
            taxData.put("sub_total", map.get("sub_total"));
            taxData.put("standardAmount", standardAmount);
            taxData.put("totalMonth", totalMonth);
            taxData.put("taxableHRA", map.get("taxableHRA"));
            taxData.put("Least_of_above_is_exempt", map.get("Least_of_above_is_exempt"));
            taxData.put("tillDateDedMonth", map.get("tillDateDedMonth"));
            taxData.put("month", map.get("month"));
            taxData.put("year", map.get("year"));
            taxData.put("empJoinYear", map.get("empJoinYear"));
            taxData.put("isSaved", map.get("isSaved"));
            taxData.put("TaxSlabType", map.get("TaxSlabType"));
            taxData.put("diffrence_age", map.get("diffrence_age"));
            taxData.put("exemptions_sec_10_Total", map.get("exemptions_sec_10_Total"));
            taxData.put("homeLoanTotal", map.get("homeLoanTotal"));
            taxData.put("InversmentDeclaration_key", map.get("InversmentDeclaration_key"));
//            taxData.put("current_employee_age",  map.get("current_employee_age"));           
            resultMap.put("Exemptions_under_sec_VIA", Math.round(Exemptions_under_sec_VIA));
//            resultMap.put("Exemptions_under_sec_10", Math.round(Exemptions_under_sec_10));
            resultMap.put("Exemptions_under_sec_10", Math.round(otherDeduction));

            resultMap.put("Deduction16", Math.round(standard));
            resultMap.put("Income_loss_house_property", Math.round(Income_loss_house_property));
            taxData.put("otherSec", Math.round(otherDeduction));
            taxData.put("income_from_previous_employer", Math.round(incomeFromPrevEmp));

            System.out.println("result map====" + taxData);

            taxResult = taxService.getTaxPreviousVersion(taxData);
            resultMap.put("tax", taxResult);
            resultMap.put("status", "success");
            System.out.println("result map====" + taxData);

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            logger.info("Problem in ExemptionsServiceImpl -> getExemptions() :: ", ex);
        }
        return resultMap;
    }

}

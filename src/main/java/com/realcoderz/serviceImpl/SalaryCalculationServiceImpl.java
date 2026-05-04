package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Deduction;
import com.realcoderz.model.IncomeTax;
import com.realcoderz.repository.DeductionRepository;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.repository.IncomeTaxRepository;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.OtherSectionRepository;
import com.realcoderz.repository.PercentageOfBasicRepository;
import com.realcoderz.repository.RentOfBasicRepository;
import com.realcoderz.repository.RunPayRollRepository;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.repository.StandardDeductionRepository;
import com.realcoderz.repository.TotalWorkingMonthRepository;
import com.realcoderz.service.SalaryCalculationService;
import com.realcoderz.util.DateUtils;
import com.realcoderz.util.EncryptDecryptUtils;
import com.realcoderz.util.MapValidation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Level;
import net.minidev.json.JSONObject;

/**
 *
 * @author Lalit raghav edited by Astha
 */
@Service
public class SalaryCalculationServiceImpl implements SalaryCalculationService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(SalaryCalculationServiceImpl.class);

    @Autowired
    private InvestmentDeclarationRepository inverstmentdeclarationrepo;

    @Autowired
    private TaxServiceImpl taxServiceImpl;

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    @Autowired
    private RentOfBasicRepository rentOfBasicrepo;

    @Autowired
    private PercentageOfBasicRepository percentageOfBasicRepo;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SalaryBreakupServiceImpl breakupServiceImpl;

    @Autowired
    private ExemptionsServiceImpl exemptionServImpl;

    @Autowired
    private IncomeTaxRepository incomeTaxRepo;

    @Autowired
    private OtherSectionRepository otherSectionRepository;

    @Autowired
    private StandardDeductionRepository standardDeductionrepo;

    @Autowired
    private TotalWorkingMonthRepository totalWorkingMonthRepo;

    @Autowired
    private DeductionRepository deduction;

    @Autowired
    private EmployeeDeductionRepository emplDeduction;

    @Autowired
    private SalaryBreakuprepo salalrybreakuprepo;

    @Autowired
    private RunPayRollRepository runPayrollRepo;

    @Override
    public Map calculateyearWorkDay(String data, HttpServletRequest request) {
        System.out.println("received data: " + data + "/n request /n" + request);
        Map resultMap = new LinkedHashMap();
        try {
            int totalMonth = 0;
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            LocalDate Fy_startYearDate;
            LocalDate FY_endDate;
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("calculateyearWorkDay method  -> Payload show here :: " + map);
            String[] keys = {"selected_month", "selected_year", "emp_id", "joining_date", "organization_id", "email_id", "employeeType"};
            if (MapValidation.containsAllKeys(keys, map)) {
                if (MapValidation.notContainsNull(map)) {
                    int startYear = Integer.parseInt(map.get("selected_year").toString());
                     //int startYear = 2022;
                     int selected_month=0;
                     if(Integer.parseInt(map.get("selected_month").toString())==1||Integer.parseInt(map.get("selected_month").toString())==2||Integer.parseInt(map.get("selected_month").toString())==3){
                          selected_month = Integer.parseInt(map.get("selected_month").toString());
                     }else{
                          selected_month = Integer.parseInt(map.get("selected_month").toString()) + 1;
                     }
                    
                   //   int selected_month = 1;
                    int endYear = startYear + 1;
                    long organizationId = Long.parseLong(map.get("organization_id").toString());
                    long empId = Long.parseLong(map.get("emp_id").toString());
                    String email = map.get("email_id").toString();
                    String employeeType = map.get("employeeType").toString();
                    int[] months = new int[12];
                    List<Integer> isSalarySaved = new ArrayList<>();
                    Date empJoining = DateUtils.convertStringToDate(map.get("joining_date").toString(), "yyyy-MM-dd");

                    int empJoinYear = empJoining.getYear() + 1900;
                    int empJoinMonth = empJoining.getMonth() + 1;
                    System.out.println("join month===== " + empJoinMonth + "   " + empJoining.getMonth());
                    System.out.println("START YEAR===== " + startYear + " empjoinyear   " + empJoinYear + " endyear " + endYear);

                    if (empJoinYear < startYear) {
                        int loopLength = -1;
                        for (int i = 4; i < 13; i++) {

                            months[i - 4] = i;
                            loopLength++;
                        }
                        months[loopLength + 1] = 1;
                        months[loopLength + 2] = 2;
                        months[loopLength + 3] = 3;
                    } else if ((endYear == empJoinYear) && (empJoinMonth > 0 && empJoinMonth < 4)) {
                        for (int i = empJoinMonth; i < 4; i++) {
                            months[i - empJoinMonth] = i;
                        }
                    } else if ((startYear == empJoinYear) && (empJoinMonth < 4)) {
                        int loopLength = -1;
                        for (int i = 4; i < 13; i++) {
                            months[i - 4] = i;
                            loopLength++;
                        }
                        months[loopLength + 1] = 1;
                        months[loopLength + 2] = 2;
                        months[loopLength + 3] = 3;
                    } else if ((startYear == empJoinYear) && (empJoinMonth >= 4)) {
                        int loopLength = -1;
                        for (int i = empJoinMonth; i < 13; i++) {
                            months[i - empJoinMonth] = i;
                            loopLength++;
                        }
                        months[loopLength + 1] = 1;
                        months[loopLength + 2] = 2;
                        months[loopLength + 3] = 3;
                    }
                    JSONObject json = new JSONObject();
                    json.put("organization"
                            + ""
                            + ""
                            + ""
                            + "_id", organizationId);
                    json.put("emp_id", empId);
                    json.put("email_id", email);
                    json.put("employee_Type", employeeType);
                    //total amount of allowances and deductions
                    LinkedCaseInsensitiveMap allowances = new LinkedCaseInsensitiveMap();
                    LinkedCaseInsensitiveMap allowanceAmounts = new LinkedCaseInsensitiveMap();
                    LinkedCaseInsensitiveMap deductions = new LinkedCaseInsensitiveMap();
                   // LinkedCaseInsensitiveMap allowanceForPriviousAndCurrentMonth = new LinkedCaseInsensitiveMap();
                   // LinkedCaseInsensitiveMap otherAllowancesOfYTDAmount = new LinkedCaseInsensitiveMap();
                    int tillDateDedMonth = 0;
                    System.out.println(months.length+" "+" 192");

                    Double otherAllowanceAmount = 0.0;
                    for (int i = 0; i < months.length; i++) {
                        int currLoopMonth = months[i];
                        //Put all code inside this loop
                        if (currLoopMonth > 0) {

                            if (selected_month >= 5 && selected_month <= 12) {
                                if (currLoopMonth >= 5 && currLoopMonth <= 12) {
                                    if (selected_month >= currLoopMonth) {
                                        tillDateDedMonth = tillDateDedMonth + 1;
                                    }
                                }
                            } else if (selected_month >= 1 && selected_month <= 3) {
                                if (currLoopMonth >= 1 && currLoopMonth <= 3) {
                                    if (selected_month >= currLoopMonth) {
                                        tillDateDedMonth = tillDateDedMonth + 1;
                                    }
                                }
                            }
                            int currLoopYear = startYear;
                            if (currLoopMonth > 0 && currLoopMonth < 4) {
                                currLoopYear++;
                            }
                            JSONObject obj = json;
                            obj.put("month", (currLoopMonth - 1));
                            obj.put("year", currLoopYear);
                            obj.put("selected_month", selected_month);
                            obj.put("selected_year", map.get("financial_year"));
                            obj.put("percentage_Change", map.get("percentage_Change"));
                            obj.put("orgState", map.get("orgState"));
                            if (Integer.parseInt(map.get("selected_month").toString()) != currLoopMonth) {
                                obj.put("flagTax", "true");
                            } else {
                                obj.remove("flagTax");
                            }
//                            obj.put("currentMonthTax", Integer.parseInt(map.get("selected_month").toString()) - 1);
//                            obj.put("currentYearTax", Integer.parseInt(map.get("selected_year").toString()));
//                            obj.put("calculateSalaryTax", "Yes");

                            Map salaryBreakup = new HashMap<>();

                            //check if already salary breakup available for current month and year.
                     //       salaryBreakup = breakupServiceImpl.isSalaryBreakUpSavedPreviousVersion(String.valueOf(currLoopMonth), String.valueOf(currLoopYear), String.valueOf(empId), String.valueOf(organizationId), String.valueOf(email), employeeType);
//                            logger.info("This Data come from Salary Breakup already save data--> Working day and pay for month " + String.valueOf(currLoopMonth) + "::::" + salaryBreakup.get("WorkingDay") + "::::" + salaryBreakup.get("AllowancePayableAmount"));
//                            System.out.println(" Working day and pay for month " + String.valueOf(currLoopMonth) + "::::" +  salaryBreakup.get("WorkingDay") + "::::" + salaryBreakup.get("AllowancePayableAmount"));
                            Date newDate = new Date();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(newDate);
                            int month = cal.get(Calendar.MONTH);
                            if (Integer.parseInt(map.get("selected_month").toString()) != currLoopMonth) {
                                obj.put("forProjection", "true");
                            } else {
                                obj.remove("forProjection");
                            }
                            if ((Integer.parseInt(map.get("selected_month").toString()) > currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == currLoopYear) || (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == (currLoopYear + 1))) {
                                obj.put("currentMonthTax", "true");
                            } else {
                                obj.remove("currentMonthTax");
                            }
                        //    System.out.println("obj=====" + obj);
                            String breakupData = EncryptDecryptUtils.encrypt(obj.toString());
                            isSalarySaved.add(currLoopMonth);
                //            System.out.println("currLoopMonth258"+" "+currLoopMonth+" "+currLoopYear+" "+" "+map.get("selected_month").toString()+" "+map.get("selected_year"));
//                          if ( (Integer.parseInt(map.get("selected_month").toString()) > currLoopMonth && Integer.parseInt(map.get("selected_year").toString()) < currLoopYear)) {
//
//                         //   if (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth || (Integer.parseInt(map.get("selected_month").toString()) > currLoopMonth && Integer.parseInt(map.get("selected_year").toString()) < currLoopYear)) {
//                                Map allowanceName = new HashMap<>();
//                                List<String> names = new ArrayList<>(allowances.keySet());
//                                String keyToExclude = "Other";
//                                names.remove(keyToExclude);
//                                Map payableAmount = new HashMap<>();
//                                Map amount = new HashMap<>();
//                                allowanceName.put("allowanceName", names);
//                                salaryBreakup.put("AllowanceName", allowanceName);
//                                amount.put("allowanceAmount", allowanceAmounts.get("amount"));
//                              
//                                salaryBreakup.put("AllowanceAmount", amount);
//                                payableAmount.put("allowancePayableAmount", allowanceAmounts.get("amount"));
//                                salaryBreakup.put("AllowancePayableAmount", payableAmount);
//                                salaryBreakup.put("OtherPayableAllowances", otherAllowanceAmount);
//                                salaryBreakup.put("OtherAllowances", otherAllowanceAmount);
//                                salaryBreakup.put("status", "success");
//                                  System.out.println("265 "+" "+salaryBreakup.toString());
//                            } else {
                                //System.out.println("else");
                                salaryBreakup = breakupServiceImpl.calculateSalaryData(breakupData, request);
                                System.out.println("salaryBreakup 287"+" "+salaryBreakup.toString());

                       //     }
                            
                
                            
                            

                            logger.info("This Data come from Salary Breakup current Month --> Working day and pay for month " + String.valueOf(currLoopMonth) + "::::" + salaryBreakup.get("WorkingDay") + "::::" + salaryBreakup.get("AllowancePayableAmount"));

                            //  all ytd amount calculation from previous to current month (excluding current tax which is calculting in createPdfData() in bottom  )
                            if ((Integer.parseInt(map.get("selected_month").toString()) >= currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == currLoopYear) || (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == (currLoopYear + 1))) {

                                if (salaryBreakup.containsKey("DeductionPayableAmount")) {

                                    Map deductionPayableAmount = (Map) salaryBreakup.get("DeductionPayableAmount");
                                    Map deductionMap = (Map) salaryBreakup.get("DeductionName");
                                    List<String> deductionNames = (List<String>) deductionMap.get("deductionName");
                                    List<Double> deductionPayable = (List<Double>) deductionPayableAmount.get("deductionPayableAmount");

                                    for (int a = 0; a < deductionNames.size(); a++) {
                                        if (deductions.containsKey(deductionNames.get(a).toString())) {
                                            deductions.put(deductionNames.get(a).toString(), (Double.parseDouble(deductions.get(deductionNames.get(a).toString()).toString()) + Math.round(deductionPayable.get(a))));
                                        } else {
                                            deductions.put(deductionNames.get(a).toString(), Math.round(deductionPayable.get(a)));
                                        }
                                    }

                                }
                            }

                            // code start here
                            //  all ytd amount calculation for Allowance from previous to current month (excluding current tax which is calculting in createPdfData() in bottom  )
//                            if ((Integer.parseInt(map.get("selected_month").toString()) >= currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == currLoopYear) || (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == (currLoopYear + 1))) {
//                              
//                                
//                                Map allowancePayableAmount = (Map) salaryBreakup.get("AllowancePayableAmount");
//                                Map allowanceMap = (Map) salaryBreakup.get("AllowanceName");
//                                Double otherAllowance=Double.parseDouble(salaryBreakup.get("OtherPayableAllowances").toString());
//                                
//                                List<String> allowanceNames = (List<String>) allowanceMap.get("allowanceName");
//                                List<Double> allowancePayable = (List<Double>) allowancePayableAmount.get("allowancePayableAmount");
//                              
//                                for (int a = 0; a < allowanceNames.size(); a++) {
//                                    
//                                    if (allowanceForPriviousAndCurrentMonth.containsKey(allowanceNames.get(a))) {
//                                        allowanceForPriviousAndCurrentMonth.put(allowanceNames.get(a), (Double.parseDouble(allowanceForPriviousAndCurrentMonth.get(allowanceNames.get(a)).toString()) + Math.round(allowancePayable.get(a))));
//                                    } 
////                                    
//                                    else {
//                                        allowanceForPriviousAndCurrentMonth.put(allowanceNames.get(a), Math.round(allowancePayable.get(a)));
//                                    
//                                    }
//                           
//                                }
//                                
//                                if(otherAllowancesOfYTDAmount.containsKey("Other Allowance")){
//                                  
//                                    otherAllowancesOfYTDAmount.put("Other Allowance",Double.parseDouble(otherAllowancesOfYTDAmount.get("Other Allowance").toString()) + Math.round(otherAllowance));
//                                }
//                                else{
//                                otherAllowancesOfYTDAmount.put("Other Allowance", Math.round(otherAllowance));
//                                }
//                            }
                            // code end here
                            if (!salaryBreakup.isEmpty() && salaryBreakup.containsKey("status") && salaryBreakup.get("status").toString().equalsIgnoreCase("success")) {
                                Map allowanceAmountMap = (Map) salaryBreakup.get("AllowanceAmount");
                                List<Double> allowanceAmount = (List<Double>) allowanceAmountMap.get("allowanceAmount");
                                otherAllowanceAmount = Double.parseDouble(salaryBreakup.get("OtherAllowances").toString());
                                allowanceAmounts.put("amount", allowanceAmount);
                                Map allowancePayableMap = (Map) salaryBreakup.get("AllowancePayableAmount");
                                Map allowanceMap = (Map) salaryBreakup.get("AllowanceName");
                                List<String> allowanceNames = (List<String>) allowanceMap.get("allowanceName");
                                int travelAllowanceIndex = allowanceNames.indexOf("Travel Allowance");
                                List<Double> allowancePayable = (List<Double>) allowancePayableMap.get("allowancePayableAmount");
                                if (travelAllowanceIndex != -1) {
                                    allowancePayable.remove(travelAllowanceIndex);
                                    allowanceNames.remove(travelAllowanceIndex);
                                }
                                for (int a = 0; a < allowanceNames.size(); a++) {
                                    if (allowances.containsKey(allowanceNames.get(a).toString())) {
                                        allowances.put(allowanceNames.get(a).toString(), Math.round((Double.parseDouble(allowances.get(allowanceNames.get(a).toString()).toString()) + Math.round(allowancePayable.get(a)))));
                                    } else {
                                        allowances.put(allowanceNames.get(a).toString(), Math.round(allowancePayable.get(a)));
                                    }
                                }
                                if (allowances.containsKey("Other")) {
                                    allowances.put("Other", Math.round((Double.parseDouble(allowances.get("Other").toString()) + Double.parseDouble(salaryBreakup.get("OtherPayableAllowances").toString()))));
                                } else {
                                    allowances.put("Other", salaryBreakup.get("OtherPayableAllowances"));
                                }
                            } else {
                                logger.info("SalaryCalculationServiceImpl :: calculateyearWorkDay() => Getting error while getting the salary breakup of month : " + currLoopMonth + " , Year : " + currLoopYear);
                            }
                        }
                    }
                    int currentMonthIndex = isSalarySaved.indexOf(Integer.parseInt(map.get("selected_month").toString()));
                    for (int i = currentMonthIndex - 1; i >= 0; i--) {
                        isSalarySaved.remove(i);
                    }
                    totalMonth = isSalarySaved.size();
                    //subtotal of allowances
                    double subtotal = 0.0;
                    for (Object key : allowances.keySet()) {
                        
                        subtotal = subtotal + Double.parseDouble(allowances.get(key).toString());
                        System.out.println("subtotal 375"+" "+subtotal);
                    }
                    allowances.put("Sub Total", Math.round(subtotal));

                    LinkedCaseInsensitiveMap totalRent = inverstmentdeclarationrepo.getchDeclarationDataById(Long.parseLong(map.get("emp_id").toString()), startYear);
                   //   System.out.println("totalRent392"+" "+totalRent.toString());
                    String[] allowanceCheck = {"HRA", "Basic Salary"};
                  //  System.out.println("allowances 400"+" "+allowances.toString());
                     
                    if (MapValidation.containsAllKeys(allowanceCheck, allowances)) {
                        double tRent = totalRent != null ? totalRent.get("total_rent") != null ? Double.parseDouble(totalRent.get("total_rent").toString()) : 0 : 0;
                       

                        double Hra = Double.parseDouble(allowances.get("HRA").toString());
                        String metroFlag = totalRent != null ? totalRent.get("status") != null ? totalRent.get("status").toString() : null : null;
                        String taxType = "OldTaxSlabKey";
                        String taxType1 = "NewTaxSlabKey";
                        if ((map.get("TaxSlabType").toString()).equals(taxType) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                            tRent = Double.parseDouble(map.get("totalRent").toString());
                        } else if ((map.get("TaxSlabType").toString()).equals(taxType1) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                            tRent = 0.0;
                        } else {
                            allowances.put("Rent Paid", Math.round(tRent));
                        }
                      

                        allowances.put("1.HRA Received", tRent == 0 ? 0 : Math.round(Hra));
                        LinkedCaseInsensitiveMap metroPerc = percentageOfBasicRepo.getPercentageDataById();
                        if (metroPerc != null) {
                            if (metroPerc.get("metro_basicpercentage") != null && metroPerc.get("non_metro_basicpercentage") != null) {
                                int metroPercentage = Integer.parseInt(metroPerc.get("metro_basicpercentage").toString());
                                int nonMetroPercentage = Integer.parseInt(metroPerc.get("non_metro_basicpercentage").toString());
                                double basicSalaryAllowance = Double.parseDouble(allowances.get("basic salary").toString());
                                double basic = 0.0;
                                if ((map.get("TaxSlabType").toString()).equals(taxType) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                                    if (map.containsKey("InversmentDeclaration_key")) {
                                        String metroFlaglocal = map.get("metroandNonMetroKey").toString();
                                        basic = metroFlaglocal != null ? metroFlaglocal.equalsIgnoreCase("metro") ? ((basicSalaryAllowance * metroPercentage) / 100) : ((basicSalaryAllowance * nonMetroPercentage) / 100) : 0;
                                        allowances.put("2.40% or 50% of Basic", tRent == 0 ? 0 : Math.round(basic));
                                    }

                                } else if ((map.get("TaxSlabType").toString()).equals(taxType1) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                                    if (map.containsKey("InversmentDeclaration_key")) {
                                        String metroFlaglocal = map.get("metroandNonMetroKey").toString();
                                        basic = metroFlaglocal != null ? metroFlaglocal.equalsIgnoreCase("metro") ? ((basicSalaryAllowance * metroPercentage) / 100) : ((basicSalaryAllowance * nonMetroPercentage) / 100) : 0;
                                        allowances.put("2.40% or 50% of Basic", tRent == 0 ? 0 : Math.round(basic));
                                    }

                                } else {
                                    basic = metroFlag != null ? metroFlag.equalsIgnoreCase("metro") ? ((basicSalaryAllowance * metroPercentage) / 100) : ((basicSalaryAllowance * nonMetroPercentage) / 100) : 0;
                                    allowances.put("2.40% or 50% of Basic", tRent == 0 ? 0 : Math.round(basic));
                                }

                                LinkedCaseInsensitiveMap rentPerc = rentOfBasicrepo.getBasicPercentageDataById();
                                if (rentPerc != null) {
                                    if (rentPerc.get("basic_percentage") != null) {
                                        int rentPercentage = Integer.parseInt(rentPerc.get("basic_percentage").toString());
                                        double rentBasic = (tRent - ((basicSalaryAllowance * rentPercentage) / 100));
                                        allowances.put("3.Rent > 10% Basic", tRent == 0 ? 0 : Math.round(rentBasic));
                                        double[] leastArray = {Hra, basic, rentBasic};
                                        Arrays.sort(leastArray);
                                        allowances.put("Least of above is exempt", tRent == 0 ? 0 : Math.round(leastArray[0]));
                                        allowances.put("Taxable HRA", tRent == 0 ? 0 : Math.round((Hra - leastArray[0])));
                                        Map exempData = new HashMap();
                                        exempData.put("total_month", totalMonth);
                                        exempData.put("organization_id", organizationId);
                                        exempData.put("emp_id", empId);
                                        exempData.put("hra_Recived", allowances.get("HRA").toString());
                                        exempData.put("JoiningMonth", empJoinMonth);
                                        exempData.put("sub_total", Math.round(subtotal));
                                        exempData.put("taxableHRA", allowances.get("Taxable HRA"));
                                        exempData.put("Least_of_above_is_exempt", allowances.get("Least of above is exempt"));
                                        exempData.put("tillDateDedMonth", tillDateDedMonth);
                                        exempData.put("month", map.get("selected_month"));
                                        exempData.put("year", map.get("financial_year"));
                                        exempData.put("employeeid", empId);
                                        exempData.put("employeeType", employeeType);
                                        exempData.put("fy_year", startYear);
                                        exempData.put("empJoinYear", empJoinYear);
                                        exempData.put("TaxSlabType", map.get("TaxSlabType").toString());
                                        exempData.put("isSaved", map.get("isSaved").toString());
                                        exempData.put("diffrence_age", map.get("diffrence_age").toString());
                                        exempData.put("exemptions_sec_10_Total", map.get("exemptions_sec_10_Total"));
                                        exempData.put("homeLoanTotal", map.get("homeLoanTotal"));
                                        exempData.put("where", map.get("where"));
                                        exempData.put("InversmentDeclaration_key", map.get("InversmentDeclaration_key"));

                                        exempData.put("otherSec", map.get("otherSec"));

                                        // Other Allowance Added in Privious and current YtD Allowance calculation 
                                        // code start here
//                                        otherAllowancesOfYTDAmount.forEach((key, value) -> {
//
//                                            if (key.equals("Other Allowance")) {
//
//                                                allowanceForPriviousAndCurrentMonth.put("Other Allowance", value);
//                                            }
//                                        });
//
//                                        Double sumOfAllowance[] = new Double[1];
//                                        sumOfAllowance[0] = 0.0;
//
//                                        allowanceForPriviousAndCurrentMonth.forEach((key, value) -> {
//                                            sumOfAllowance[0] = sumOfAllowance[0] + Double.parseDouble(value.toString());
//                                        });
//
//                                        allowanceForPriviousAndCurrentMonth.put("sumOfYTDAllowance", sumOfAllowance[0]);
//                                        System.out.println("YTD Allowances For Previous And Current Month >>>>>> " + allowanceForPriviousAndCurrentMonth);
//                                        logger.info("YTD Allowances For Previous And Current Month >>>>>> ", allowanceForPriviousAndCurrentMonth);
//                                        // code end here

                                        //   System.out.println("deductions????????????????????????????"+deductions);
                                     
                                        return this.createPdfData(allowances, exempData, deductions);
                                    } else {
                                        resultMap.put("status", "error");
                                        resultMap.put("msg", "Please add valid rent percentage for basic cut.!");
                                    }
                                } else {
                                    resultMap.put("status", "error");
                                    resultMap.put("msg", "Please add rent percentage for basic cut.!");
                                }
                            } else {
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Please add valid value of metro and non metro percentage.!");
                            }
                        } else {
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Please add metro and non metro percentage.!");
                        }

                    } else {
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Please add basic salary and hra allowances.!");
                    }

                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Please provide valid values.!");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid json.!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            logger.error("Problem in SalaryCalculationServiceImpl :: calculateyearWorkDay() => " + ex);
        }
        System.out.println("resultmap final===" + resultMap);
        System.out.println(resultMap);

        return resultMap;
    }

    public Double calculateTotalReimbursForMonth(HttpEntity<?> entity) {
        try {
            Map reimburshmentData = restTemplate.exchange(reimburshment_url + "/reimbursement/getAllReimForMonthByEmployeeId", HttpMethod.POST, entity, HashMap.class).getBody();
            Map reimburshement = mapper.readValue(EncryptDecryptUtils.decrypt(reimburshmentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
            if (reimburshement.get("status").equals("success")) {
                return Double.parseDouble(reimburshement.get("amount").toString());
            }
        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(SalaryCalculationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0.0;
    }

    public Map calculateSalaryData(HttpEntity<?> entity, int month, int year, Long orgId, List<Deduction> deductions, double basicSalaryPercentage) {
        Map returnMap = new HashMap<>();
        try {
            int daysOfMonth = breakupServiceImpl.getnumberOfDaysInMonth(month + 1, year);
            Map leavesData = restTemplate.exchange(reimburshment_url + "/leave/leavesById", HttpMethod.POST, entity, HashMap.class).getBody();
            Map workingDay = mapper.readValue(EncryptDecryptUtils.decrypt(leavesData.get("data").toString()), LinkedCaseInsensitiveMap.class);
            if (workingDay.containsKey("status") && workingDay.get("status").toString().equalsIgnoreCase("success")) {
                double salary = Double.parseDouble(workingDay.get("gross_salary").toString());
                returnMap.put("salary", salary);
                double payableSalary;
                if (workingDay.containsKey("total_working_days") && workingDay.get("total_working_days") != null) {
                    int totalWorkingDays = (int) workingDay.get("total_working_days");
                    double oneDaySalary = salary / daysOfMonth;
                    payableSalary = totalWorkingDays * oneDaySalary;
                    returnMap.put("payableSalary", Math.round(payableSalary));
                } else {
                    double oneDaySalary = salary / daysOfMonth;
                    payableSalary = daysOfMonth * oneDaySalary;
                    returnMap.put("payableSalary", Math.round(payableSalary));
                }
                deductions.stream().forEach(deduct -> {
                    double basicSalary = (salary * basicSalaryPercentage) / 100.0;
                    if (deduct.getDeduction_name().equalsIgnoreCase("epf")) {
                        if (basicSalary < 15000) {
                            returnMap.put("epf", Math.round(basicSalary * deduct.getPercentage()) / 100.0);
                        } else {
                            returnMap.put("epf", Math.round(15000 * deduct.getPercentage()) / 100.0);
                        }
                    } else if (deduct.getDeduction_name().equalsIgnoreCase("esic")) {
                        returnMap.put("esic", Math.round(payableSalary * deduct.getPercentage()) / 100.0);
                    }
                });
                double netAmount = payableSalary - (Double.parseDouble(returnMap.get("epf").toString()) + Double.parseDouble(returnMap.get("esic").toString()));
                returnMap.put("netPayableAmount", netAmount);
                returnMap.put("status", "success");
            } else {
                returnMap.put("status", "error");
                returnMap.put("msg", "no gross salary available!");
            }

        } catch (JsonProcessingException ex) {
            java.util.logging.Logger.getLogger(SalaryCalculationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnMap;
    }

    private Map createPdfData(LinkedCaseInsensitiveMap allowances, Map exempData, LinkedCaseInsensitiveMap deductions) {

        // System.out.println("deductions>>>>>>>>>>>>>>>>>>>>>>>>"+deductions);
        Map result = new HashMap<>();
        try {
            List<String[]> allowancesArray = new LinkedList<>();
            for (Object key : allowances.keySet()) {
                String[] arr = {key.toString(), allowances.get(key.toString()).toString()};
                allowancesArray.add(arr);
            }
            System.out.println("exempData 598"+" "+exempData.toString());
            Map exemptions = exemptionServImpl.getExemptions(exempData);
            
            System.out.println("incomingdata===" + exempData);
            System.out.println("returnData===" + exemptions);

            if (exemptions.get("status").toString().equalsIgnoreCase("success")) {
                List<String[]> exempArray = new LinkedList<>();
                String[] exempRow1 = {"Deductions under Chapter VI-A", "Declared Amount", "Exempted Amount"};
                exempArray.add(exempRow1);
                int startYear = Integer.parseInt(exempData.get("fy_year").toString());
                LinkedCaseInsensitiveMap totalAllowance80c = inverstmentdeclarationrepo.getchDeclarationDataByIdChanges(Long.parseLong(exempData.get("employeeid").toString()), startYear);
                double total = totalAllowance80c != null ? Double.parseDouble(totalAllowance80c.get("total_allowances").toString()) : 0;
                double total80C = totalAllowance80c != null ? 150000.0 : 0;
                String msg = totalAllowance80c != null ? total >= total80C ? "150000" : totalAllowance80c.get("total_allowances").toString() : "0";
                System.out.println("totla 80c=======" + totalAllowance80c);
                String[] exempRow2 = {"Exemption u/s VI A", exemptions.get("Exemptions_under_sec_VIA").toString(), msg};
                exempArray.add(exempRow2);
                String[] exempRow3 = {"Deductions under Section 10", "Exempted Amount", ""};
                exempArray.add(exempRow3);
                String[] exempRow4 = {"Other section(80D,80U,80G,80E,80DD,Nps)", exemptions.get("Exemptions_under_sec_10").toString(), ""};

                exempArray.add(exempRow4);
                String[] exempRow5 = {"Deductions u/s 16 ", "Exempted Amount", ""};
                exempArray.add(exempRow5);
                String[] exempRow6 = {"Deduction u/s 16", exemptions.get("Deduction16").toString(), ""};
                exempArray.add(exempRow6);
                String[] exempRow7 = {"Other Income u/s 192(2B)", "Declared Amount", "Exempted Amount"};
                exempArray.add(exempRow7);

                //fy_year
                LinkedCaseInsensitiveMap interest = otherSectionRepository.getInterestOnHousingLoanPreviousVersion(Long.parseLong(exempData.get("employeeid").toString()), Long.parseLong(exempData.get("organization_id").toString()),Integer.parseInt(exempData.get("fy_year").toString()));

                LinkedCaseInsensitiveMap prevIncome = otherSectionRepository.getIncomeFromPreviousEmployer(Long.parseLong(exempData.get("employeeid").toString()), Long.parseLong(exempData.get("organization_id").toString()),Integer.parseInt(exempData.get("fy_year").toString()));
                System.out.println("previous emp========" + prevIncome);

                double houseLoanProperty = interest != null ? Double.parseDouble(interest.get("interest_on_housing_loan_before").toString()) : 0;
                double homeLoanMaxiumValue = interest != null ? 200000.0 : 0;
                String houseLoan_msg = interest != null ? houseLoanProperty >= homeLoanMaxiumValue ? "200000" : interest.get("interest_on_housing_loan_before").toString() : "0";
                String[] exempRow8 = {"Income / Loss From House Property", interest != null ? interest.get("interest_on_housing_loan_before").toString() : "0", houseLoan_msg};
                exempArray.add(exempRow8);
                String[] exempRow9 = {"Income from previous employee", prevIncome != null ? prevIncome.get("income_from_previous_employer").toString() : "0", ""};
                exempArray.add(exempRow9);
                System.out.println("exemp arrrrrrrr========" + exempArray);

                int allowSize = allowancesArray.size();
                int exempSize = exempArray.size();
                int iterate = 0;
                if (allowSize > exempSize) {
                    int empty = allowSize - exempSize;
                    iterate = allowSize;
                    for (int i = 0; i < empty; i++) {
                        String[] exempRow = {"", "", ""};
                        exempArray.add(exempRow);
                    }
                } else {
                    int empty = exempSize - allowSize;
                    iterate = exempSize;
                    for (int i = 0; i < empty; i++) {
                        String[] allowRow = {"", ""};
                        allowancesArray.add(allowRow);
                    }
                }
                List<String[]> rows = new LinkedList<>();
                for (int i = 0; i < iterate; i++) {
                    String[] ar = {allowancesArray.get(i)[0], allowancesArray.get(i)[1], exempArray.get(i)[0], exempArray.get(i)[1], exempArray.get(i)[2]};
                    rows.add(ar);
                }
                Map tax = (Map) exemptions.get("tax");
                if (tax.get("status").toString().equalsIgnoreCase("success")) {
                    tax.remove("status");
                    List<String[]> taxRow = new LinkedList<>();
                    for (Object key : tax.keySet()) {
                        String[] arr = {key.toString(), tax.get(key.toString()).toString()};
                        taxRow.add(arr);
                    }
                    int previousRow = rows.size();
                    int taxSize = tax.size();
                    int iterate1 = 0;
                    if (previousRow > taxSize) {
                        int empty = previousRow - taxSize;
                        iterate1 = previousRow;
                        for (int i = 0; i < empty; i++) {
                            String[] empRow = {"", ""};
                            taxRow.add(empRow);
                        }
                    } else {
                        int empty = taxSize - previousRow;
                        iterate1 = taxSize;
                        for (int i = 0; i < empty; i++) {
                            String[] empRow = {"", "", "", "", ""};
                            rows.add(empRow);
                        }
                    }
                    List<String[]> returnRows = new LinkedList<>();
                    for (int i = 0; i < iterate1; i++) {
                        String[] arr = {rows.get(i)[0], rows.get(i)[1], rows.get(i)[2], rows.get(i)[3], rows.get(i)[4], taxRow.get(i)[0], taxRow.get(i)[1]};
                        returnRows.add(arr);
                    }
                    Map taxSaved = this.istaxSaved(exempData);
                
                    if (taxSaved.get("status").equals("true")) {
                        result.put("rows", taxSaved.get("Income_Tax"));
                        List<IncomeTax> taxIncome = new ArrayList<>();
                        List<String[]> taxR = (List<String[]>) taxSaved.get("Income_Tax");
                        for (int i = 0; i < taxR.size(); i++) {
                            String[] current = taxR.get(i);
                            IncomeTax incomeTax = new IncomeTax();
                            incomeTax.setSalary_hra_name(current[0]);
                            incomeTax.setSalary_hra_amount(current[1]);
                            incomeTax.setExemption_name(current[2]);
                            incomeTax.setExemption_declared_amount(current[3]);
                            incomeTax.setExemption_exempted_amount(current[4]);
                            incomeTax.setTax_name(current[5]);
                            incomeTax.setTax_amount(current[6]);
                            incomeTax.setOrganization_id(Long.parseLong(exempData.get("organization_id").toString()));
                            incomeTax.setEmployee_id(Long.parseLong(exempData.get("emp_id").toString()));
                            incomeTax.setMonth(Integer.parseInt(exempData.get("month").toString()) + 1);
                            incomeTax.setYear(Integer.parseInt(exempData.get("year").toString()));
                            taxIncome.add(incomeTax);
                        }
                        result.put("allTaxObj", taxIncome);
                        result.put("ytdDeduction", deductions);
                       // result.put("ytdAmountForPriviousAndCurrentMonth", allowanceForPriviousAndCurrentMonth);
                    } else {

                        //  calculateing current month tax and addedd to previous deduction 
                        if (deductions.containsKey("Income Tax")) {
                            deductions.put("Income Tax", Double.parseDouble(deductions.get("Income Tax").toString()) + Double.parseDouble(tax.get("Tax Deduction for this month").toString()));
                            System.out.println("Income Tax"+" "+Double.parseDouble(deductions.get("Income Tax").toString()) + Double.parseDouble(tax.get("Tax Deduction for this month").toString()));
                        } else {
                             System.out.println("Income Tax"+" "+ Double.parseDouble(tax.get("Tax Deduction for this month").toString()));
                            deductions.put("Income Tax", Double.parseDouble(tax.get("Tax Deduction for this month").toString()));
                        }
                        List<IncomeTax> taxIncome = new ArrayList<>();
                        for (int i = 0; i < returnRows.size(); i++) {
                            String[] current = returnRows.get(i);
                            if (((exempData.get("isSaved").toString())) == "true") {
                                IncomeTax incomeTax = new IncomeTax();
                                incomeTax.setSalary_hra_name(current[0]);
                                incomeTax.setSalary_hra_amount(current[1]);
                                incomeTax.setExemption_name(current[2]);
                                incomeTax.setExemption_declared_amount(current[3]);
                                incomeTax.setExemption_exempted_amount(current[4]);
                                incomeTax.setTax_name(current[5]);
                                incomeTax.setTax_amount(current[6]);
                                incomeTax.setOrganization_id(Long.parseLong(exempData.get("organization_id").toString()));
                                incomeTax.setEmployee_id(Long.parseLong(exempData.get("emp_id").toString()));
                                incomeTax.setMonth(Integer.parseInt(exempData.get("month").toString()) + 1);
                                incomeTax.setYear(Integer.parseInt(exempData.get("year").toString()));
                                taxIncome.add(incomeTax);
                            }
                        }
//                        if(((exempData.get("isSaved").toString()))== "true")
//                       {
//                        incomeTaxRepo.saveAll(taxIncome);
//                       }
                        result.put("rows", returnRows);
                        result.put("allTaxObj", taxIncome);
                        result.put("ytdDeduction", deductions);
                       // result.put("ytdAmountForPriviousAndCurrentMonth", allowanceForPriviousAndCurrentMonth);
                    }
                    result.put("status", "success");
                    System.out.println("result*****************= " + result);
                } else {
                    result = tax;
                }
            } else {
                result = exemptions;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Problem in SalaryCalculationServiceImpl :: createPdfData() => " + ex);
            result.put("status", "exception");
            return result;
        }
        System.out.println("result===" + result);
        return result;
    }

    private Map istaxSaved(Map map) {
        Map resultMap = new HashMap<>();
        List<String[]> arr = new ArrayList<>();
        System.out.println(map.get("emp_id")+" "+"emplif");
         System.out.println(map.get("organization_id"));
         System.out.println(map.get("month")+" month");
         System.out.println( map.get("year")+" "+" year" );
         
        int isSaved = incomeTaxRepo.isTaxSavedAlready(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()) + 1, Integer.parseInt(map.get("year").toString()));
        if (isSaved > 0) {
            System.out.println("inside"+" "+isSaved);
            List<String[]> tax = incomeTaxRepo.isTaxSave(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()) + 1, Integer.parseInt(map.get("year").toString()));
            for (int i = 0; i < tax.size(); i++) {
                arr.add(tax.get(i));
            }
            resultMap.put("Income_Tax", arr);
            resultMap.put("status", "true");
        } else {
            resultMap.put("status", "false");
        }
        return resultMap;
    }

    @Override
    public Map saveAllTax(Map map) {
        Map resultMap = new HashMap<>();
        try {

            List<IncomeTax> taxObj = mapper.convertValue(map.get("taxObj"), new TypeReference<List<IncomeTax>>() {
            });

      //  List<IncomeTax> taxObj = (List<IncomeTax>) map.get("taxObj");

 

            System.out.println("taxObj"+" "+taxObj);
            incomeTaxRepo.saveAll(taxObj);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.put("status", "error");
            ex.printStackTrace();
            resultMap.put("msg", "Exception occured while saving tax");
        }
        return resultMap;
    }

    private static int totalMonthBetween(Date startDate, Date endDate) {
        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();
        start.setTime(startDate);
        end.setTime(endDate);
        int yearsInBetween = start.get(Calendar.YEAR) - end.get(Calendar.YEAR);
        return yearsInBetween;
    }

    @Override
    public Map updateAllowanceTax(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("employeeId") && map.containsKey("month") && map.containsKey("year") && map.containsKey("allowances")) {
                List<LinkedCaseInsensitiveMap> empAllowances = mapper.convertValue(map.get("allowances"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                });
                double standardAmount = 0.0;
                LinkedCaseInsensitiveMap standardDeduction = standardDeductionrepo.getstandDeductionAmount();
                if (standardDeduction != null) {
                    String standardDeductionLocal = standardDeduction.get("standard_deduction").toString();
                    standardAmount = Math.round(Double.parseDouble(standardDeduction.get("standard_deduction").toString()));
                } else {
                    resultMap.clear();
                    resultMap.put("msg", "please fill standard deduction form ");
                    resultMap.put("status", "error");
                    return resultMap;
                }
                if (empAllowances != null) {
                    Long empId = Long.parseLong(map.get("employeeId").toString());
                    Long orgId = Long.parseLong(map.get("orgId").toString());
                    Integer month = Integer.parseInt(map.get("month").toString())+1 ;
                    Integer year = Integer.parseInt(map.get("year").toString());
                   
                    List<IncomeTax> taxObj = incomeTaxRepo.isTaxSavedAlreadyGet(empId, orgId, month, year);
                  
                    // total month find here
                  
                    int remain_total_month_exception = totalWorkingMonthRepo.getTotalWorking(empId, orgId, month, year);
                    int diff_age = totalWorkingMonthRepo.getTotalAge(empId, orgId, month, year);
                    //        fetch run payroll allowances add in list
                    List<Double> allAllownceValue = new ArrayList<>();
                    empAllowances.stream().forEach(data -> {
                        data.forEach((key, value) -> {
                            Double val = Double.parseDouble(value.toString());
                            allAllownceValue.add(val);
                        });
                    });
                    double totalAllowanceSum = 0.0;
                    String finalValue = "";
                    String Exemption_VI = "";
                    String Exemption_s10 = "";
                    String Deduction16 = "";
                    String Income_house_Property = "";
                    String Least_of_above_is_exemptdata = "";
                    String otherSec = "";
                    String IFPE = "";
                    String[] key = {"Exemption u/s VI A", "Exemption u/s s10", "Deduction u/s 16", "Income / Loss From House Property", "Least of above is exempt", "Other section(80D,80U,80G,80E,80DD,Nps)", "Income from previous employee"};
                    for (IncomeTax tax : taxObj) {
                        Optional<LinkedCaseInsensitiveMap> data = empAllowances.stream().filter(d -> d.containsKey(tax.getSalary_hra_name())).findFirst();
                        if (data.isPresent()) {
                            LinkedCaseInsensitiveMap allowanceData = data.get();
                            allowanceData.forEach((keys, value) -> {
                                if (keys.toString().equals(tax.getSalary_hra_name())) {
                                    Double allowanceAdd = (Double.parseDouble(tax.getSalary_hra_amount()) + Double.parseDouble(allowanceData.get(tax.getSalary_hra_name()).toString()));

                                  //  Double allowanceAdd =  Double.parseDouble(allowanceData.get(tax.getSalary_hra_name()).toString());
                                    tax.setSalary_hra_amount((allowanceAdd).toString());
                                    tax.setTax_allowance_id(tax.getTax_allowance_id());
                                }

                            });
//                            tax.setSalary_hra_amount(allowanceData.get(tax.getSalary_hra_name()).toString());
//                            tax.setTax_allowance_id(tax.getTax_allowance_id());
                        }
                        if (tax.getSalary_hra_name().trim().equalsIgnoreCase("sub total")) {
                            for (double i : allAllownceValue) {
                                totalAllowanceSum += i;
                            }
                            finalValue = String.valueOf(totalAllowanceSum + Double.parseDouble(tax.getSalary_hra_amount()));
                            tax.setSalary_hra_amount(finalValue);
                        }
                    }
                    System.out.println("taxObj923"+" "+taxObj);
                    List<IncomeTax> afterSaveAll = incomeTaxRepo.saveAll(taxObj);
                    for (IncomeTax taxObj_savepickup : taxObj) {
                        if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[0].trim())) {
                            Exemption_VI = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_exempted_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[1].trim())) {
                            Exemption_s10 = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[2].trim())) {
                            Deduction16 = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[3].trim())) {
                            Income_house_Property = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_exempted_amount())));
                        } else if (taxObj_savepickup.getSalary_hra_name().trim().equalsIgnoreCase(key[4].trim())) {
                            Least_of_above_is_exemptdata = String.valueOf((Double.parseDouble(taxObj_savepickup.getSalary_hra_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[5].trim())) {
                            otherSec = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[6].trim())) {
                            IFPE = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        }
                    }
                    //     variable defined here
                    Double Exemptions_under_sec_VIA_useOnlyCalculation = Double.parseDouble(Exemption_VI);
                    Double Income_loss_house_property = Double.parseDouble(Income_house_Property);
                    Double otherSection = Double.parseDouble(otherSec);
                    Double income_from_previous_employee = Double.parseDouble(IFPE);

                    Boolean isSaved = false;
                    Integer diffrence_age = (diff_age);
                    long emp_id = empId;
                    long organization_id = orgId;
                    String TaxSlabType = "NewTaxSlabKey";
                    int isInvestmentDeclared = inverstmentdeclarationrepo.isInvestmentDeclared(emp_id, organization_id);
                    if (isInvestmentDeclared > 0) {
                        TaxSlabType = "OldTaxSlabKey";
                    }
                    Integer totalMonth = remain_total_month_exception;
                    Double Least_of_above_is_exempt = Double.parseDouble(Least_of_above_is_exemptdata);
                    Double homeLoanTotal = Double.parseDouble(Income_house_Property);
                    Double Exemptions_under_sec_VIA = Double.parseDouble(Exemption_VI);
                    Double totalAllowance = Double.parseDouble(finalValue);
                    int months = Integer.parseInt(map.get("month").toString());
                    int years = year;

                    Map getTaxMap = new HashMap();
                    getTaxMap.put("sub_total", finalValue);
                    getTaxMap.put("standardAmount", standardAmount);
                    getTaxMap.put("Deduction16", standardAmount);
                    getTaxMap.put("Exemptions_under_sec_VIA_useOnlyCalculation", Exemptions_under_sec_VIA_useOnlyCalculation);
                    getTaxMap.put("Income_loss_house_property", Income_loss_house_property);
                    getTaxMap.put("isSaved", isSaved);
                    getTaxMap.put("diffrence_age", diffrence_age);
                    getTaxMap.put("emp_id", emp_id);
                    getTaxMap.put("organization_id", organization_id);
                    getTaxMap.put("TaxSlabType", TaxSlabType);
                    getTaxMap.put("totalMonth",totalMonth );
                     //getTaxMap.put("totalMonth",0 );
                    getTaxMap.put("Least_of_above_is_exempt", Least_of_above_is_exempt);
                    getTaxMap.put("homeLoanTotal", homeLoanTotal);
                    getTaxMap.put("otherSec", otherSection);
                    getTaxMap.put("income_from_previous_employer", income_from_previous_employee);
                    getTaxMap.put("Exemptions_under_sec_VIA", Exemptions_under_sec_VIA);
                    getTaxMap.put("month", months);
                    getTaxMap.put("year", years);

                    Map taxdataOverRide = taxServiceImpl.getTax(getTaxMap);
                    Double tax_deduction_this_month = 0.0;
                    for (IncomeTax tax : taxObj) {
                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Income(rounded off)")) {
                            tax.setTax_amount(taxdataOverRide.get("Total Income(rounded off)").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax on Total Income")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax on Total Income").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Surcharge on Income")) {
                            tax.setTax_amount(taxdataOverRide.get("Surcharge on Income ").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Education Cess")) {
                            tax.setTax_amount(taxdataOverRide.get("Education Cess").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Payable")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax Payable").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Relief u/s 89")) {
                            tax.setTax_amount(taxdataOverRide.get("Relief u/s 89").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Tax Liability")) {
                            tax.setTax_amount(taxdataOverRide.get("Total Tax Liability").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deducted(Previous Employer)")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax Deducted(Previous Employer)").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Tax Deducted Till Date")) {
                            tax.setTax_amount(taxdataOverRide.get("Total Tax Deducted Till Date").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Remaining Tax/Remaining months")) {
                            tax.setTax_amount(taxdataOverRide.get("Remaining Tax/Remaining months").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deduction for this month")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax Deduction for this month").toString());
                            tax_deduction_this_month = Double.parseDouble(taxdataOverRide.get("Tax Deduction for this month").toString());

                        }
                    }
                    //        fetch deduction id from deduction table 
//                    Long deductions = deduction.fetchDeductionId(orgId);
//
//                    // update data in employeeDeduction table for Monthly Tax
//                    emplDeduction.updateMonthlyTax(tax_deduction_this_month, deductions, empId, orgId);

                    List<IncomeTax> afterSaveAlls = incomeTaxRepo.saveAll(taxObj);
                    System.out.println("taxObj 1031"+" "+taxObj.toString());
                    resultMap.put("tdsupdate", taxdataOverRide.get("Tax Deduction for this month"));

//                     //        Get SalaryBreakup
//                    List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetchSalaryBreakupData(empId,orgId,(Integer.parseInt(map.get("month").toString())),year);
//                    System.out.println("SalaryBreakup data"+salaryBreakup);
//                    Map salaryBreakupData =new HashMap();
//                    if (!salaryBreakup.isEmpty()) {
//                        salaryBreakup.stream().forEach(salbreakup -> {
//                                salaryBreakupData.put("working_day", salbreakup.get("working_day"));
//                                salaryBreakupData.put("employee_type", salbreakup.get("employee_type"));
//                                salaryBreakupData.put("payable_basic", salbreakup.get("payable_basic"));
//                                salaryBreakupData.put("payable_gross", salbreakup.get("payable_gross"));
//                                salaryBreakupData.put("total_days", salbreakup.get("total_days"));
//                                salaryBreakupData.put("gross_salary", salbreakup.get("gross_salary"));
//                                salaryBreakupData.put("basic", salbreakup.get("basic"));
//                        });
//                    }
                    // update Deduction in Salary Breakup Deduction Part
//                    return this.salaryBreakupDeductionUpdateFromTax(orgId,empId,(Integer.parseInt(map.get("month").toString()) + 1),year,tax_deduction_this_month,salaryBreakupData);
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Allowance Object mapping error");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "error");
            resultMap.put("msg", "Exception occured while updating allowance in Income Tax model");
        }
        return resultMap;
    }

//    public Map salaryBreakupDeductionUpdateFromTax(long orgId,long empId,int month,int year,double income_tax,Map salaryBreakupData)
//    {
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start");
//        Map salBreakup=new HashMap();
//        
//        salBreakup.put("organization_id",orgId);
//        salBreakup.put("emp_id",empId);
//        salBreakup.put("month",month);
//        salBreakup.put("year",year);
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232---");
//        salBreakup.put("income_tax",income_tax);
//        salBreakup.put("employee_Type",salaryBreakupData.get("employee_type").toString());
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232***---");
//        String  PayableGross=(salaryBreakupData.get("payable_gross").toString());
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232&&&&&^^^^^---");
//        String PayableBasic=(salaryBreakupData.get("payable_basic").toString());
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232&&&&&%%%%%---");
//        String Gross=(salaryBreakupData.get("gross_salary").toString());
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232&&&&000000&%%%%%---");
//        String Basic=(salaryBreakupData.get("basic").toString());
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232&&&23456&&%%%%%---");
//        double working_day=Double.parseDouble(salaryBreakupData.get("working_day").toString());
//        System.out.println("salaryBreakupDeductionUpdateFromTax---");
//        Double day=Double.parseDouble(salaryBreakupData.get("total_days").toString());
//        long days = (new Double(day)).longValue();
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start212323232&&&&&---");
//        int currYear= year;
//        int currMonth= month;
//       
//        System.out.println("salaryBreakupDeductionUpdateFromTax method start12");
//         try
//         {
//             System.out.println("salaryBreakupDeductionUpdateFromTax method start1234");
//           Map deductionData = breakupServiceImpl.deductionCalculated(salBreakup, working_day,days, String.valueOf(PayableGross), String.valueOf(PayableBasic), String.valueOf(Gross), String.valueOf(Basic),currYear,currMonth);
//           System.out.println("Deduction Data------------"+deductionData);
//           
//    
//         }
//         catch(Exception ex)
//         {
//             
//         }
//        return salBreakup;
//    }
    @Override
    public Map updateTds(Map map) {
        System.out.println("Enter in updateTds " + map);
        Map resultMap = new HashMap();
        try {
            Long empId = Long.parseLong(map.get("employee_id").toString());
            Long orgId = Long.parseLong(map.get("organization_id").toString());
            Integer month = Integer.parseInt(map.get("month").toString()) + 1;
            Integer year = Integer.parseInt(map.get("year").toString());
            String finalRemValue = "";
            Double deductedTax = Double.parseDouble(map.get("deductedTax").toString());
            Double totalmonthlyTax = Double.parseDouble(map.get("tds").toString());
            Double remainingMonthlyTax = 0.00;
            List<IncomeTax> taxObj = incomeTaxRepo.isTaxSavedAlreadyGet(empId, orgId, month, year);
            System.out.println("taxObj Tax Data" + taxObj);
            remainingMonthlyTax = totalmonthlyTax - deductedTax;
            for (IncomeTax tax : taxObj) {
                if (tax.getTax_name().trim().equalsIgnoreCase("Remaining Tax/Remaining months")) {
                    finalRemValue = String.valueOf(Double.parseDouble(tax.getTax_amount()));
                    tax.setTax_amount(finalRemValue.toString());
                }
                if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deduction for this month")) {
                    tax.setTax_amount(deductedTax.toString());
                }
            }
            List<IncomeTax> afterSaveAll = incomeTaxRepo.saveAll(taxObj);

            //        fetch deduction id from deduction table 
            Long deductions = deduction.fetchDeductionId(orgId);

            // update data in employeeDeduction table for Monthly Tax
            emplDeduction.updateMonthlyTax(deductedTax, deductions, empId, orgId, month, year);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.put("status", "error");
            resultMap.put("msg", "Exception occured while updating allowance in Income Tax model");
        }
        return resultMap;
    }

    @Override
     public Map calculateyearWorkDayPreviousVersion(String data, HttpServletRequest request) {
      
        Map resultMap = new LinkedHashMap();
        try {

            int totalMonth = 0;
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            LocalDate Fy_startYearDate;
            LocalDate FY_endDate;
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            logger.info("calculateyearWorkDay method  -> Payload show here :: " + map);
            String[] keys = {"selected_month", "selected_year", "emp_id", "joining_date", "organization_id", "email_id", "employeeType"};
            if (MapValidation.containsAllKeys(keys, map)) {
                if (MapValidation.notContainsNull(map)) {
                    int startYear = Integer.parseInt(map.get("selected_year").toString());
                    int selected_month = Integer.parseInt(map.get("selected_month").toString()) + 1;
                    int endYear = startYear + 1;
                    long organizationId = Long.parseLong(map.get("organization_id").toString());
                    long empId = Long.parseLong(map.get("emp_id").toString());
                    String email = map.get("email_id").toString();
                    String employeeType = map.get("employeeType").toString();
                    int[] months = new int[12];
                    List<Integer> isSalarySaved = new ArrayList<>();
                    Date empJoining = DateUtils.convertStringToDate(map.get("joining_date").toString(), "yyyy-MM-dd");

                    int empJoinYear = empJoining.getYear() + 1900;
                    int empJoinMonth = empJoining.getMonth() + 1;
                    System.out.println("join month===== " + empJoinMonth + "   " + empJoining.getMonth());
                    System.out.println("START YEAR===== " + startYear + " empjoinyear   " + empJoinYear + " endyear " + endYear);

                    if (empJoinYear < startYear) {
                        int loopLength = -1;
                        for (int i = 4; i < 13; i++) {

                            months[i - 4] = i;
                            loopLength++;
                        }
                        months[loopLength + 1] = 1;
                        months[loopLength + 2] = 2;
                        months[loopLength + 3] = 3;
                    } else if ((endYear == empJoinYear) && (empJoinMonth > 0 && empJoinMonth < 4)) {
                        for (int i = empJoinMonth; i < 4; i++) {
                            months[i - empJoinMonth] = i;
                        }
                    } else if ((startYear == empJoinYear) && (empJoinMonth < 4)) {
                        int loopLength = -1;
                        for (int i = 4; i < 13; i++) {
                            months[i - 4] = i;
                            loopLength++;
                        }
                        months[loopLength + 1] = 1;
                        months[loopLength + 2] = 2;
                        months[loopLength + 3] = 3;
                    } else if ((startYear == empJoinYear) && (empJoinMonth >= 4)) {
                        int loopLength = -1;
                        for (int i = empJoinMonth; i < 13; i++) {
                            months[i - empJoinMonth] = i;
                            loopLength++;
                        }
                        months[loopLength + 1] = 1;
                        months[loopLength + 2] = 2;
                        months[loopLength + 3] = 3;
                    }
                    JSONObject json = new JSONObject();
                    json.put("organization"
                            + ""
                            + ""
                            + ""
                            + "_id", organizationId);
                    json.put("emp_id", empId);
                    json.put("email_id", email);
                    json.put("employee_Type", employeeType);
                    //total amount of allowances and deductions
                    LinkedCaseInsensitiveMap allowances = new LinkedCaseInsensitiveMap();
                    LinkedCaseInsensitiveMap deductions = new LinkedCaseInsensitiveMap();
                    LinkedCaseInsensitiveMap allowanceForPriviousAndCurrentMonth = new LinkedCaseInsensitiveMap();
                    LinkedCaseInsensitiveMap otherAllowancesOfYTDAmount = new LinkedCaseInsensitiveMap();
                    
                  
                    int tillDateDedMonth = 0;
                    System.out.println(months[0]+" month1203");
                    System.out.println(months.length+" mponth1204");
                    int count=0;
                    for (int i = 0; i < months.length; i++) {
                        int currLoopMonth = months[i];
                        System.out.println("count 1208 "+count++);
                        //Put all code inside this loop
                        if (currLoopMonth > 0) {

                            if (selected_month >= 5 && selected_month <= 12) {
                                if (currLoopMonth >= 5 && currLoopMonth <= 12) {
                                    if (selected_month >= currLoopMonth) {
                                        tillDateDedMonth = tillDateDedMonth + 1;
                                        System.out.println("tillDateDedMonth1215"+" "+tillDateDedMonth);
                                    }
                                }
                            } else if (selected_month >= 1 && selected_month <= 3) {
                                if (currLoopMonth >= 1 && currLoopMonth <= 3) {
                                    if (selected_month >= currLoopMonth) {
                                        tillDateDedMonth = tillDateDedMonth + 1;
                                           System.out.println("tillDateDedMonth1222"+" "+tillDateDedMonth);
                                    }
                                }
                            }
                            int currLoopYear = startYear;
                            if (currLoopMonth > 0 && currLoopMonth < 4) {
                                currLoopYear++;
                            }
                            System.out.println("currLoopYear"+" "+currLoopYear);
                            JSONObject obj = json;
                            int sMonth;
                            if(map.containsKey("from")){
                                sMonth= Integer.parseInt(map.get("selected_month").toString())+1;
                                obj.put("ID", "true");
                            }else{
                                sMonth= Integer.parseInt(map.get("selected_month").toString());
                                obj.remove("ID");
                            }
                           // obj.put("month", (currLoopMonth - 1));
                              obj.put("month", (currLoopMonth ));
                            obj.put("year", currLoopYear);
                            obj.put("selected_month", selected_month);
                            obj.put("selected_year", map.get("financial_year"));
                            obj.put("percentage_Change", map.get("percentage_Change"));
                            obj.put("orgState", map.get("orgState"));
                            if (sMonth != currLoopMonth) {
                                obj.put("flagTax", "true");
                            } else {
                                obj.remove("flagTax");
                            }
//                            obj.put("currentMonthTax", Integer.parseInt(map.get("selected_month").toString()) - 1);
//                            obj.put("currentYearTax", Integer.parseInt(map.get("selected_year").toString()));
//                            obj.put("calculateSalaryTax", "Yes");

                            Map salaryBreakup = new HashMap<>();
                            //check if already salary breakup available for current month and year.
                            salaryBreakup = breakupServiceImpl.isSalaryBreakUpSavedPreviousVersion(String.valueOf(currLoopMonth), String.valueOf(currLoopYear), String.valueOf(empId), String.valueOf(organizationId), String.valueOf(email), employeeType);
                            logger.info("This Data come from Salary Breakup already save data--> Working day and pay for month " + String.valueOf(currLoopMonth) + "::::" + salaryBreakup.get("WorkingDay") + "::::" + salaryBreakup.get("AllowancePayableAmount"));
//                            System.out.println(" Working day and pay for month " + String.valueOf(currLoopMonth) + "::::" +  salaryBreakup.get("WorkingDay") + "::::" + salaryBreakup.get("AllowancePayableAmount"));

                           //  System.out.println("salaryBreakup1261"+" "+salaryBreakup.toString());
                            if (salaryBreakup.isEmpty()) {
                                Date newDate = new Date();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(newDate);
                                int month = cal.get(Calendar.MONTH);
                                if (Integer.parseInt(map.get("selected_month").toString()) != currLoopMonth) {
                                    obj.put("forProjection", "true");
                                } else {
                                    obj.remove("forProjection");
                                }
                                if ((Integer.parseInt(map.get("selected_month").toString()) > currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == currLoopYear) || (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == (currLoopYear + 1))) {
                                    obj.put("currentMonthTax", "true");
                                } else {
                                    obj.remove("currentMonthTax");
                                }
                                System.out.println("obj=====" + obj);
                                String breakupData = EncryptDecryptUtils.encrypt(obj.toString());
                                System.out.println("breakupData1279"+" "+breakupData);
                                isSalarySaved.add(currLoopMonth);
                                salaryBreakup = breakupServiceImpl.calculateSalaryDataPreviousVersion(breakupData, request);

                                System.out.println("salaryBreakup data****** >>>>>>>>>>>>>>>>>>>>>>>>>>" + salaryBreakup);
                                logger.info("This Data come from Salary Breakup current Month --> Working day and pay for month " + String.valueOf(currLoopMonth) + "::::" + salaryBreakup.get("WorkingDay") + "::::" + salaryBreakup.get("AllowancePayableAmount"));
                            }

                            //  all ytd amount calculation from previous to current month (excluding current tax which is calculting in createPdfData() in bottom  )
                            if ((Integer.parseInt(map.get("selected_month").toString()) >= currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == currLoopYear) || (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == (currLoopYear + 1))) {
                                Map deductionPayableAmount = (Map) salaryBreakup.get("DeductionPayableAmount");
                                Map deductionMap = (Map) salaryBreakup.get("DeductionName");
                                List<String> deductionNames = (List<String>) deductionMap.get("deductionName");
                                List<Double> deductionPayable = (List<Double>) deductionPayableAmount.get("deductionPayableAmount");

                                for (int a = 0; a < deductionNames.size(); a++) {
                                    if (deductions.containsKey(deductionNames.get(a).toString())) {
                                        deductions.put(deductionNames.get(a).toString(), (Double.parseDouble(deductions.get(deductionNames.get(a).toString()).toString()) + Math.round(deductionPayable.get(a))));
                                    } else {
                                        deductions.put(deductionNames.get(a).toString(), Math.round(deductionPayable.get(a)));
                                    }
                                }

                            }

                             // code start here
                             //  all ytd amount calculation for Allowance from previous to current month (excluding current tax which is calculting in createPdfData() in bottom  )
                            if ((Integer.parseInt(map.get("selected_month").toString()) >= currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == currLoopYear) || (Integer.parseInt(map.get("selected_month").toString()) < currLoopMonth && Integer.parseInt(map.get("financial_year").toString()) == (currLoopYear + 1))) {
                              
                                Map allowancePayableAmount = (Map) salaryBreakup.get("AllowancePayableAmount");
                                Map allowanceMap = (Map) salaryBreakup.get("AllowanceName");
                                Double otherAllowance=Double.parseDouble(salaryBreakup.get("OtherPayableAllowances").toString());
                                
                                List<String> allowanceNames = (List<String>) allowanceMap.get("allowanceName");
                                List<Double> allowancePayable = (List<Double>) allowancePayableAmount.get("allowancePayableAmount");
                              
                                for (int a = 0; a < allowanceNames.size(); a++) {
                                    
                                    if (allowanceForPriviousAndCurrentMonth.containsKey(allowanceNames.get(a).toString())) {
                                        allowanceForPriviousAndCurrentMonth.put(allowanceNames.get(a).toString(), (Double.parseDouble(allowanceForPriviousAndCurrentMonth.get(allowanceNames.get(a).toString()).toString()) + Math.round(allowancePayable.get(a))));
                                    }
//
                                    else {
                                        allowanceForPriviousAndCurrentMonth.put(allowanceNames.get(a).toString(), Math.round(allowancePayable.get(a)));
                                    
                                    }
                           
                                }
                                
                                if(otherAllowancesOfYTDAmount.containsKey("Other Allowance")){
                                  
                                    otherAllowancesOfYTDAmount.put("Other Allowance",Double.parseDouble(otherAllowancesOfYTDAmount.get("Other Allowance").toString()) + Math.round(otherAllowance));
                                }
                                else{
                                otherAllowancesOfYTDAmount.put("Other Allowance", Math.round(otherAllowance));
                                }
                            }
                          
                            // code end here
                            
                            if (!salaryBreakup.isEmpty() && salaryBreakup.containsKey("status") && salaryBreakup.get("status").toString().equalsIgnoreCase("success")) {
                                Map allowancePayableMap = (Map) salaryBreakup.get("AllowancePayableAmount");
                                System.out.println("SalaryBreakup 1.." + salaryBreakup.get("AllowancePayableAmount"));
                                Map allowanceMap = (Map) salaryBreakup.get("AllowanceName");
                                List<String> allowanceNames = (List<String>) allowanceMap.get("allowanceName");
                                int travelAllowanceIndex = allowanceNames.indexOf("Travel Allowance");
                                List<Double> allowancePayable = (List<Double>) allowancePayableMap.get("allowancePayableAmount");
                                if (travelAllowanceIndex != -1) {
                                    allowancePayable.remove(travelAllowanceIndex);
                                    allowanceNames.remove(travelAllowanceIndex);
                                }
                                for (int a = 0; a < allowanceNames.size(); a++) {
                                    System.out.println("1350");
                                    System.out.println("allowanceNames 1351"+" "+allowanceNames.get(a));
                                    if (allowances.containsKey(allowanceNames.get(a).toString())) {
                                        allowances.put(allowanceNames.get(a).toString(),Math.round((Double.parseDouble(allowances.get(allowanceNames.get(a).toString()).toString()) + Math.round(allowancePayable.get(a)))));
                                    } else {
                                        allowances.put(allowanceNames.get(a).toString(), Math.round(allowancePayable.get(a)));
                                    }
                                }
                                if (allowances.containsKey("Other")) {
                                    allowances.put("Other", Math.round((Double.parseDouble(allowances.get("Other").toString()) + Double.parseDouble(salaryBreakup.get("OtherPayableAllowances").toString()))));
                                } else {
                                    allowances.put("Other", salaryBreakup.get("OtherPayableAllowances"));
                                }
                            } else {
                                logger.info("SalaryCalculationServiceImpl :: calculateyearWorkDay() => Getting error while getting the salary breakup of month : " + currLoopMonth + " , Year : " + currLoopYear);
                            }
                        }
                    }
                    //looop ends heres
                    
                    
                    int currentMonthIndex = isSalarySaved.indexOf(Integer.parseInt(map.get("selected_month").toString()));
                    for (int i = currentMonthIndex - 1; i >= 0; i--) {
                        isSalarySaved.remove(i);
                    }
                    totalMonth = isSalarySaved.size();
                    //subtotal of allowances
                    double subtotal = 0.0;
                    for (Object key : allowances.keySet()) {
                        subtotal = subtotal + Double.parseDouble(allowances.get(key).toString());
                    }
                    allowances.put("Sub Total", Math.round(subtotal));

                    LinkedCaseInsensitiveMap totalRent = inverstmentdeclarationrepo.getchDeclarationDataById(Long.parseLong(map.get("emp_id").toString()), startYear);
                    System.out.println("total=======" + totalRent);
                    String[] allowanceCheck = {"HRA", "Basic Salary"};
                    System.out.println("allowance 141s"+" "+allowances.toString());

                    if (MapValidation.containsAllKeys(allowanceCheck, allowances)) {
                        double tRent = totalRent != null ? totalRent.get("total_rent") != null ? Double.parseDouble(totalRent.get("total_rent").toString()) : 0 : 0;
                        System.out.println("tRent=======" + tRent);

                        double Hra = Double.parseDouble(allowances.get("HRA").toString());
                        String metroFlag = totalRent != null ? totalRent.get("status") != null ? totalRent.get("status").toString() : null : null;
                        String taxType = "OldTaxSlabKey";
                        String taxType1 = "NewTaxSlabKey";
                        if ((map.get("TaxSlabType").toString()).equals(taxType) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                            tRent = Double.parseDouble(map.get("totalRent").toString());
                        } else if ((map.get("TaxSlabType").toString()).equals(taxType1) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                            tRent = 0.0;
                        } else {
                            allowances.put("Rent Paid", Math.round(tRent));
                        }
                        System.out.println("tRent=======" + tRent);

                        allowances.put("1.HRA Received", tRent == 0 ? 0 : Math.round(Hra));
                        LinkedCaseInsensitiveMap metroPerc = percentageOfBasicRepo.getPercentageDataById();
                        if (metroPerc != null) {
                            if (metroPerc.get("metro_basicpercentage") != null && metroPerc.get("non_metro_basicpercentage") != null) {
                                int metroPercentage = Integer.parseInt(metroPerc.get("metro_basicpercentage").toString());
                                int nonMetroPercentage = Integer.parseInt(metroPerc.get("non_metro_basicpercentage").toString());
                                double basicSalaryAllowance = Double.parseDouble(allowances.get("basic salary").toString());
                                double basic = 0.0;
                                if ((map.get("TaxSlabType").toString()).equals(taxType) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                                    if (map.containsKey("InversmentDeclaration_key")) {
                                        String metroFlaglocal = map.get("metroandNonMetroKey").toString();
                                        basic = metroFlaglocal != null ? metroFlaglocal.equalsIgnoreCase("metro") ? ((basicSalaryAllowance * metroPercentage) / 100) : ((basicSalaryAllowance * nonMetroPercentage) / 100) : 0;
                                        allowances.put("2.40% or 50% of Basic", tRent == 0 ? 0 : Math.round(basic));
                                    }

                                } else if ((map.get("TaxSlabType").toString()).equals(taxType1) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                                    if (map.containsKey("InversmentDeclaration_key")) {
                                        String metroFlaglocal = map.get("metroandNonMetroKey").toString();
                                        basic = metroFlaglocal != null ? metroFlaglocal.equalsIgnoreCase("metro") ? ((basicSalaryAllowance * metroPercentage) / 100) : ((basicSalaryAllowance * nonMetroPercentage) / 100) : 0;
                                        allowances.put("2.40% or 50% of Basic", tRent == 0 ? 0 : Math.round(basic));
                                    }

                                } else {
                                    basic = metroFlag != null ? metroFlag.equalsIgnoreCase("metro") ? ((basicSalaryAllowance * metroPercentage) / 100) : ((basicSalaryAllowance * nonMetroPercentage) / 100) : 0;
                                    allowances.put("2.40% or 50% of Basic", tRent == 0 ? 0 : Math.round(basic));
                                }

                                LinkedCaseInsensitiveMap rentPerc = rentOfBasicrepo.getBasicPercentageDataById();
                                if (rentPerc != null) {
                                    if (rentPerc.get("basic_percentage") != null) {
                                        int rentPercentage = Integer.parseInt(rentPerc.get("basic_percentage").toString());
                                        double rentBasic = (tRent - ((basicSalaryAllowance * rentPercentage) / 100));
                                        allowances.put("3.Rent > 10% Basic", tRent == 0 ? 0 : Math.round(rentBasic));
                                        double[] leastArray = {Hra, basic, rentBasic};
                                        Arrays.sort(leastArray);
                                        allowances.put("Least of above is exempt", tRent == 0 ? 0 : Math.round(leastArray[0]));
                                        allowances.put("Taxable HRA", tRent == 0 ? 0 : Math.round((Hra - leastArray[0])));
                                        Map exempData = new HashMap();
                                        exempData.put("total_month", totalMonth);
                                        exempData.put("organization_id", organizationId);
                                        exempData.put("emp_id", empId);
                                        exempData.put("hra_Recived", allowances.get("HRA").toString());
                                        exempData.put("JoiningMonth", empJoinMonth);
                                        exempData.put("sub_total", Math.round(subtotal));
                                        exempData.put("taxableHRA", allowances.get("Taxable HRA"));
                                        exempData.put("Least_of_above_is_exempt", allowances.get("Least of above is exempt"));
                                        exempData.put("tillDateDedMonth", tillDateDedMonth);
                                        exempData.put("month", map.get("selected_month"));
                                        exempData.put("year", map.get("financial_year"));
                                        exempData.put("employeeid", empId);
                                        exempData.put("employeeType", employeeType);
                                        exempData.put("fy_year", startYear);
                                        exempData.put("empJoinYear", empJoinYear);
                                        exempData.put("TaxSlabType", map.get("TaxSlabType").toString());
                                        exempData.put("isSaved", map.get("isSaved").toString());
                                        exempData.put("diffrence_age", map.get("diffrence_age").toString());
                                        exempData.put("exemptions_sec_10_Total", map.get("exemptions_sec_10_Total"));
                                        exempData.put("homeLoanTotal", map.get("homeLoanTotal"));
                                        exempData.put("where", map.get("where"));
                                        exempData.put("InversmentDeclaration_key", map.get("InversmentDeclaration_key"));
                                        exempData.put("otherSec", map.get("otherSec"));
                                        exempData.put("incomeFromPreviousEmployer", map.get("incomeFromPreviousEmployer"));
                                        exempData.put("houseLoan", map.get("houseLoan"));
                                     
                                        // Other Allowance Added in Privious and current YtD Allowance calculation 
                                        // code start here
                                         otherAllowancesOfYTDAmount.forEach((key,value)->{
                                            
                                            if(key.equals("Other Allowance")){
                                            
                                                allowanceForPriviousAndCurrentMonth.put("Other Allowance", value);
                                            }
                                        });
                                         
                                       Double sumOfAllowance[]=new Double[1];
                                       sumOfAllowance[0]=0.0;
                            
                                       allowanceForPriviousAndCurrentMonth.forEach((key,value)->{
                                       sumOfAllowance[0]=sumOfAllowance[0]+Double.parseDouble(value.toString());
                                        });
                                       
                                       allowanceForPriviousAndCurrentMonth.put("sumOfYTDAllowance", sumOfAllowance[0]);
                                        System.out.println("YTD Allowances For Previous And Current Month >>>>>> "+allowanceForPriviousAndCurrentMonth);
                                       logger.info("YTD Allowances For Previous And Current Month >>>>>> ",allowanceForPriviousAndCurrentMonth);
                                       // code end here
                                     
                                        return this.createPdfDataPreviousVersion(allowances, exempData, deductions,allowanceForPriviousAndCurrentMonth);
                                    } else {
                                        resultMap.put("status", "error");
                                        resultMap.put("msg", "Please add valid rent percentage for basic cut.!");
                                    }
                                } else {
                                    resultMap.put("status", "error");
                                    resultMap.put("msg", "Please add rent percentage for basic cut.!");
                                }
                            } else {
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Please add valid value of metro and non metro percentage.!");
                            }
                        } else {
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Please add metro and non metro percentage.!");
                        }

                    } else {
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Please add basic salary and hra allowances.!");
                    }

                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Please provide valid values.!");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid json.!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            logger.error("Problem in SalaryCalculationServiceImpl :: calculateyearWorkDay() => " + ex);
        }
        System.out.println("resultmap final===" + resultMap);
        System.out.println(resultMap);

        return resultMap;
    }
     
     @Override
      public Map createPdfDataPreviousVersion(LinkedCaseInsensitiveMap allowances, Map exempData, LinkedCaseInsensitiveMap deductions,LinkedCaseInsensitiveMap allowanceForPriviousAndCurrentMonth) {

        // System.out.println("deductions>>>>>>>>>>>>>>>>>>>>>>>>"+deductions);
        Map result = new HashMap<>();
        try {
            List<String[]> allowancesArray = new LinkedList<>();
            for (Object key : allowances.keySet()) {
                String[] arr = {key.toString(), allowances.get(key.toString()).toString()};
                allowancesArray.add(arr);
            }
            Map exemptions = exemptionServImpl.getExemptionsPreviousVersion(exempData);
            System.out.println("incomingdata===" + exempData);
            System.out.println("returnData===" + exemptions);

            if (exemptions.get("status").toString().equalsIgnoreCase("success")) {
                List<String[]> exempArray = new LinkedList<>();
                String[] exempRow1 = {"Deductions under Chapter VI-A", "Declared Amount", "Exempted Amount"};
                exempArray.add(exempRow1);
                int startYear = Integer.parseInt(exempData.get("fy_year").toString());
                LinkedCaseInsensitiveMap totalAllowance80c = inverstmentdeclarationrepo.getchDeclarationDataByIdChanges(Long.parseLong(exempData.get("employeeid").toString()), startYear);
                double total = totalAllowance80c != null ? Double.parseDouble(totalAllowance80c.get("total_allowances").toString()) : 0;
                double total80C = totalAllowance80c != null ? 150000.0 : 0;
                String msg = totalAllowance80c != null ? total >= total80C ? "150000" : totalAllowance80c.get("total_allowances").toString() : "0";
                System.out.println("totla 80c=======" + totalAllowance80c);
                String[] exempRow2 = {"Exemption u/s VI A", exemptions.get("Exemptions_under_sec_VIA").toString(), msg};
                exempArray.add(exempRow2);
                String[] exempRow3 = {"Deductions under Section 10", "Exempted Amount", ""};
                exempArray.add(exempRow3);
                String[] exempRow4 = {"Other section(80D,80U,80G,80E,80DD,Nps)", exemptions.get("Exemptions_under_sec_10").toString(), ""};

                exempArray.add(exempRow4);
                String[] exempRow5 = {"Deductions u/s 16 ", "Exempted Amount", ""};
                exempArray.add(exempRow5);
                String[] exempRow6 = {"Deduction u/s 16", exemptions.get("Deduction16").toString(), ""};
                exempArray.add(exempRow6);
                String[] exempRow7 = {"Other Income u/s 192(2B)", "Declared Amount", "Exempted Amount"};
                exempArray.add(exempRow7);
                LinkedCaseInsensitiveMap interest = otherSectionRepository.getInterestOnHousingLoanPreviousVersion(Long.parseLong(exempData.get("employeeid").toString()), Long.parseLong(exempData.get("organization_id").toString()),Integer.parseInt(exempData.get("fy_year").toString()));
                LinkedCaseInsensitiveMap prevIncome = otherSectionRepository.getIncomeFromPreviousEmployer(Long.parseLong(exempData.get("employeeid").toString()), Long.parseLong(exempData.get("organization_id").toString()),Integer.parseInt(exempData.get("fy_year").toString()));
                double houseLoanProperty = interest != null ? Double.parseDouble(interest.get("interest_on_housing_loan_before").toString()) : 0;
                double homeLoanMaxiumValue = interest != null ? 200000.0 : 0;
                String houseLoan_msg = interest != null ? houseLoanProperty >= homeLoanMaxiumValue ? "200000" : interest.get("interest_on_housing_loan_before").toString() : "0";
                String[] exempRow8 = {"Income / Loss From House Property", interest != null ? interest.get("interest_on_housing_loan_before").toString() : "0", houseLoan_msg};
                exempArray.add(exempRow8);
                String[] exempRow9 = {"Income from previous employee", prevIncome != null ? prevIncome.get("income_from_previous_employer").toString() : "0", ""};
                exempArray.add(exempRow9);
                System.out.println("exemp arrrrrrrr========" + exempArray);

                int allowSize = allowancesArray.size();
                int exempSize = exempArray.size();
                int iterate = 0;
                if (allowSize > exempSize) {
                    int empty = allowSize - exempSize;
                    iterate = allowSize;
                    for (int i = 0; i < empty; i++) {
                        String[] exempRow = {"", "", ""};
                        exempArray.add(exempRow);
                    }
                } else {
                    int empty = exempSize - allowSize;
                    iterate = exempSize;
                    for (int i = 0; i < empty; i++) {
                        String[] allowRow = {"", ""};
                        allowancesArray.add(allowRow);
                    }
                }
                List<String[]> rows = new LinkedList<>();
                for (int i = 0; i < iterate; i++) {
                    String[] ar = {allowancesArray.get(i)[0], allowancesArray.get(i)[1], exempArray.get(i)[0], exempArray.get(i)[1], exempArray.get(i)[2]};
                    rows.add(ar);
                }
                Map tax = (Map) exemptions.get("tax");
                if (tax.get("status").toString().equalsIgnoreCase("success")) {
                    tax.remove("status");
                    List<String[]> taxRow = new LinkedList<>();
                    for (Object key : tax.keySet()) {
                        String[] arr = {key.toString(), tax.get(key.toString()).toString()};
                        taxRow.add(arr);
                    }
                    int previousRow = rows.size();
                    int taxSize = tax.size();
                    int iterate1 = 0;
                    if (previousRow > taxSize) {
                        int empty = previousRow - taxSize;
                        iterate1 = previousRow;
                        for (int i = 0; i < empty; i++) {
                            String[] empRow = {"", ""};
                            taxRow.add(empRow);
                        }
                    } else {
                        int empty = taxSize - previousRow;
                        iterate1 = taxSize;
                        for (int i = 0; i < empty; i++) {
                            String[] empRow = {"", "", "", "", ""};
                            rows.add(empRow);
                        }
                    }
                    List<String[]> returnRows = new LinkedList<>();
                    for (int i = 0; i < iterate1; i++) {
                        String[] arr = {rows.get(i)[0], rows.get(i)[1], rows.get(i)[2], rows.get(i)[3], rows.get(i)[4], taxRow.get(i)[0], taxRow.get(i)[1]};
                        returnRows.add(arr);
                    }
//                    Map taxSaved = this.istaxSaved(exempData);
//                    if (taxSaved.get("status").equals("true")) {
//                        result.put("rows", taxSaved.get("Income_Tax"));
//                        result.put("ytdDeduction", deductions);
//                        result.put("ytdAmountForPriviousAndCurrentMonth", allowanceForPriviousAndCurrentMonth);
//                     } else {

                        //  calculateing current month tax and addedd to previous deduction 
                        if (deductions.containsKey("Income Tax")) {
                            deductions.put("Income Tax", Double.parseDouble(deductions.get("Income Tax").toString()) + Double.parseDouble(tax.get("Tax Deduction for this month").toString()));
                        } else {       
                            deductions.put("Income Tax", Double.parseDouble(tax.get("Tax Deduction for this month").toString()));
                        }

                        List<IncomeTax> taxIncome = new ArrayList<>();
                        for (int i = 0; i < returnRows.size(); i++) {
                            String[] current = returnRows.get(i);
                            if (((exempData.get("isSaved").toString())) == "true") {
                                IncomeTax incomeTax = new IncomeTax();
                                incomeTax.setSalary_hra_name(current[0]);
                                incomeTax.setSalary_hra_amount(current[1]);
                                incomeTax.setExemption_name(current[2]);
                                incomeTax.setExemption_declared_amount(current[3]);
                                incomeTax.setExemption_exempted_amount(current[4]);
                                incomeTax.setTax_name(current[5]);
                                incomeTax.setTax_amount(current[6]);
                                incomeTax.setOrganization_id(Long.parseLong(exempData.get("organization_id").toString()));
                                incomeTax.setEmployee_id(Long.parseLong(exempData.get("emp_id").toString()));
                                incomeTax.setMonth(Integer.parseInt(exempData.get("month").toString()) + 1);
                                incomeTax.setYear(Integer.parseInt(exempData.get("year").toString()));
                                taxIncome.add(incomeTax);
                            }
                        }
//                        if(((exempData.get("isSaved").toString()))== "true")
//                       {
//                        incomeTaxRepo.saveAll(taxIncome);
//                       }
                        result.put("rows", returnRows);
                        result.put("allTaxObj", taxIncome);
                        result.put("ytdDeduction", deductions);
                        result.put("ytdAmountForPriviousAndCurrentMonth", allowanceForPriviousAndCurrentMonth);
              //      }
                    result.put("status", "success");
                    System.out.println("result*****************= " + result);
                } else {
                    result = tax;
                }
            } else {
                result = exemptions;
            }
        } catch (Exception ex) {
            logger.error("Problem in SalaryCalculationServiceImpl :: createPdfData() => " + ex);
            result.put("status", "exception");
            return result;
        }
        System.out.println("result===" + result);
        return result;
    }

//    @Override
//    public Map updateAllowanceTaxNew(Map map) {
//        Map resultMap = new HashMap<>();
//        try {
//            if (map.containsKey("employeeId") && map.containsKey("month") && map.containsKey("year") && map.containsKey("allowances")) {
//                List<LinkedCaseInsensitiveMap> empAllowances = mapper.convertValue(map.get("allowances"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
//                });
//                double standardAmount = 0.0;
//                LinkedCaseInsensitiveMap standardDeduction = standardDeductionrepo.getstandDeductionAmount();
//                if (standardDeduction != null) {
//                    String standardDeductionLocal = standardDeduction.get("standard_deduction").toString();
//                    standardAmount = Math.round(Double.parseDouble(standardDeduction.get("standard_deduction").toString()));
//                } else {
//                    resultMap.clear();
//                    resultMap.put("msg", "please fill standard deduction form ");
//                    resultMap.put("status", "error");
//                    return resultMap;
//                }
//                if (empAllowances != null) {
//                    Long empId = Long.parseLong(map.get("employeeId").toString());
//                    Long orgId = Long.parseLong(map.get("orgId").toString());
//                    Integer month = Integer.parseInt(map.get("month").toString())+1;
//                    Integer year = Integer.parseInt(map.get("year").toString());
//                   
//                    List<IncomeTax> taxObj = incomeTaxRepo.isTaxSavedAlreadyGet(empId, orgId, month, year);
//                  
//                    // total month find here
//                  
//                    int remain_total_month_exception = totalWorkingMonthRepo.getTotalWorking(empId, orgId, month, year);
//                    int diff_age = totalWorkingMonthRepo.getTotalAge(empId, orgId, month, year);
//                    //        fetch run payroll allowances add in list
//                    List<Double> allAllownceValue = new ArrayList<>();
//                    empAllowances.stream().forEach(data -> {
//                        data.forEach((key, value) -> {
//                            Double val = Double.parseDouble(value.toString());
//                            allAllownceValue.add(val);
//                        });
//                    });
//                    double totalAllowanceSum = 0.0;
//                  
//                    String finalValue = "";
//                    String Exemption_VI = "";
//                    String Exemption_s10 = "";
//                    String Deduction16 = "";
//                    String Income_house_Property = "";
//                    String Least_of_above_is_exemptdata = "";
//                    String otherSec = "";
//                    String IFPE = "";
//                    String[] key = {"Exemption u/s VI A", "Exemption u/s s10", "Deduction u/s 16", "Income / Loss From House Property", "Least of above is exempt", "Other section(80D,80U,80G,80E,80DD,Nps)", "Income from previous employee"};
//                    for (IncomeTax tax : taxObj) {
//                        Optional<LinkedCaseInsensitiveMap> data = empAllowances.stream().filter(d -> d.containsKey(tax.getSalary_hra_name())).findFirst();
//                        if (data.isPresent()) {
//                            LinkedCaseInsensitiveMap allowanceData = data.get();
//                            allowanceData.forEach((keys, value) -> {
//                                if (keys.toString().equals(tax.getSalary_hra_name())) {  
//                                    
//                                    Double allowanceAdd = (Double.parseDouble(tax.getSalary_hra_amount()) + Double.parseDouble(allowanceData.get(tax.getSalary_hra_name()).toString()));
//                                   
//                                    tax.setSalary_hra_amount((allowanceAdd).toString());
//                                    tax.setTax_allowance_id(tax.getTax_allowance_id());
//                                }
//
//                            });
////                            tax.setSalary_hra_amount(allowanceData.get(tax.getSalary_hra_name()).toString());
////                            tax.setTax_allowance_id(tax.getTax_allowance_id());
//                        }  
//                        if (tax.getSalary_hra_name().trim().equalsIgnoreCase("sub total")) {
//                          
//                             for (double i : allAllownceValue) {
//                                totalAllowanceSum += i;
//                            }
//                               
//                           finalValue = String.valueOf(totalAllowanceSum + Double.parseDouble(tax.getSalary_hra_amount()));
//                           // finalValue = String.valueOf(Double.parseDouble(tax.getSalary_hra_amount()));
//
//                            tax.setSalary_hra_amount(finalValue);
//                        }
//                    }
//                    System.out.println("taxObj923"+" "+taxObj);
//                    List<IncomeTax> afterSaveAll = incomeTaxRepo.saveAll(taxObj);
//                    for (IncomeTax taxObj_savepickup : taxObj) {
//                        if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[0].trim())) {
//                            Exemption_VI = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_exempted_amount())));
//                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[1].trim())) {
//                            Exemption_s10 = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
//                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[2].trim())) {
//                            Deduction16 = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
//                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[3].trim())) {
//                            Income_house_Property = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_exempted_amount())));
//                        } else if (taxObj_savepickup.getSalary_hra_name().trim().equalsIgnoreCase(key[4].trim())) {
//                            Least_of_above_is_exemptdata = String.valueOf((Double.parseDouble(taxObj_savepickup.getSalary_hra_amount())));
//                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[5].trim())) {
//                            otherSec = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
//                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[6].trim())) {
//                            IFPE = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
//                        }
//                    }
//                    //     variable defined here
//                    Double Exemptions_under_sec_VIA_useOnlyCalculation = Double.parseDouble(Exemption_VI);
//                    Double Income_loss_house_property = Double.parseDouble(Income_house_Property);
//                    Double otherSection = Double.parseDouble(otherSec);
//                    Double income_from_previous_employee = Double.parseDouble(IFPE);
//
//                    Boolean isSaved = false;
//                    Integer diffrence_age = (diff_age);
//                    long emp_id = empId;
//                    long organization_id = orgId;
//                    String TaxSlabType = "NewTaxSlabKey";
//                    int isInvestmentDeclared = inverstmentdeclarationrepo.isInvestmentDeclared(emp_id, organization_id);
//                    if (isInvestmentDeclared > 0) {
//                        TaxSlabType = "OldTaxSlabKey";
//                    }
//                    Integer totalMonth = remain_total_month_exception;
//                    Double Least_of_above_is_exempt = Double.parseDouble(Least_of_above_is_exemptdata);
//                    Double homeLoanTotal = Double.parseDouble(Income_house_Property);
//                    Double Exemptions_under_sec_VIA = Double.parseDouble(Exemption_VI);
//                    Double totalAllowance = Double.parseDouble(finalValue);
//                    int months = Integer.parseInt(map.get("month").toString());
//                    int years = year;
//
//                    Map getTaxMap = new HashMap();
//                    getTaxMap.put("sub_total", finalValue);
//                    getTaxMap.put("standardAmount", standardAmount);
//                    getTaxMap.put("Deduction16", standardAmount);
//                    getTaxMap.put("Exemptions_under_sec_VIA_useOnlyCalculation", Exemptions_under_sec_VIA_useOnlyCalculation);
//                    getTaxMap.put("Income_loss_house_property", Income_loss_house_property);
//                    getTaxMap.put("isSaved", isSaved);
//                    getTaxMap.put("diffrence_age", diffrence_age);
//                    getTaxMap.put("emp_id", emp_id);
//                    getTaxMap.put("organization_id", organization_id);
//                    getTaxMap.put("TaxSlabType", TaxSlabType);
//                    getTaxMap.put("totalMonth",totalMonth );
//                     //getTaxMap.put("totalMonth",0 );
//                    getTaxMap.put("Least_of_above_is_exempt", Least_of_above_is_exempt);
//                    getTaxMap.put("homeLoanTotal", homeLoanTotal);
//                    getTaxMap.put("otherSec", otherSection);
//                    getTaxMap.put("income_from_previous_employer", income_from_previous_employee);
//                    getTaxMap.put("Exemptions_under_sec_VIA", Exemptions_under_sec_VIA);
//                    getTaxMap.put("month", months);
//                    getTaxMap.put("year", years);
//
//                    Map taxdataOverRide = taxServiceImpl.getTax(getTaxMap);
//                    Double tax_deduction_this_month = 0.0;
//                    for (IncomeTax tax : taxObj) {
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Income(rounded off)")) {
//                            tax.setTax_amount(taxdataOverRide.get("Total Income(rounded off)").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax on Total Income")) {
//                            tax.setTax_amount(taxdataOverRide.get("Tax on Total Income").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Surcharge on Income")) {
//                            tax.setTax_amount(taxdataOverRide.get("Surcharge on Income ").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Education Cess")) {
//                            tax.setTax_amount(taxdataOverRide.get("Education Cess").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Payable")) {
//                            tax.setTax_amount(taxdataOverRide.get("Tax Payable").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Relief u/s 89")) {
//                            tax.setTax_amount(taxdataOverRide.get("Relief u/s 89").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Tax Liability")) {
//                            tax.setTax_amount(taxdataOverRide.get("Total Tax Liability").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deducted(Previous Employer)")) {
//                            tax.setTax_amount(taxdataOverRide.get("Tax Deducted(Previous Employer)").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Tax Deducted Till Date")) {
//                            tax.setTax_amount(taxdataOverRide.get("Total Tax Deducted Till Date").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Remaining Tax/Remaining months")) {
//                            tax.setTax_amount(taxdataOverRide.get("Remaining Tax/Remaining months").toString());
//                        }
//                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deduction for this month")) {
//                            tax.setTax_amount(taxdataOverRide.get("Tax Deduction for this month").toString());
//                            tax_deduction_this_month = Double.parseDouble(taxdataOverRide.get("Tax Deduction for this month").toString());
//
//                        }
//                    }
//                    //        fetch deduction id from deduction table 
////                    Long deductions = deduction.fetchDeductionId(orgId);
////
////                    // update data in employeeDeduction table for Monthly Tax
////                    emplDeduction.updateMonthlyTax(tax_deduction_this_month, deductions, empId, orgId);
//
//                    List<IncomeTax> afterSaveAlls = incomeTaxRepo.saveAll(taxObj);
//                    System.out.println("taxObj 1031"+" "+taxObj.toString());
//                    resultMap.put("tdsupdate", taxdataOverRide.get("Tax Deduction for this month"));
//
////                     //        Get SalaryBreakup
////                    List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetchSalaryBreakupData(empId,orgId,(Integer.parseInt(map.get("month").toString())),year);
////                    System.out.println("SalaryBreakup data"+salaryBreakup);
////                    Map salaryBreakupData =new HashMap();
////                    if (!salaryBreakup.isEmpty()) {
////                        salaryBreakup.stream().forEach(salbreakup -> {
////                                salaryBreakupData.put("working_day", salbreakup.get("working_day"));
////                                salaryBreakupData.put("employee_type", salbreakup.get("employee_type"));
////                                salaryBreakupData.put("payable_basic", salbreakup.get("payable_basic"));
////                                salaryBreakupData.put("payable_gross", salbreakup.get("payable_gross"));
////                                salaryBreakupData.put("total_days", salbreakup.get("total_days"));
////                                salaryBreakupData.put("gross_salary", salbreakup.get("gross_salary"));
////                                salaryBreakupData.put("basic", salbreakup.get("basic"));
////                        });
////                    }
//                    // update Deduction in Salary Breakup Deduction Part
////                    return this.salaryBreakupDeductionUpdateFromTax(orgId,empId,(Integer.parseInt(map.get("month").toString()) + 1),year,tax_deduction_this_month,salaryBreakupData);
//                } else {
//                    resultMap.put("status", "error");
//                    resultMap.put("msg", "Allowance Object mapping error");
//                }
//            } else {
//                resultMap.put("status", "error");
//                resultMap.put("msg", "Please provide valid key and value");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            resultMap.put("status", "error");
//            resultMap.put("msg", "Exception occured while updating allowance in Income Tax model");
//        }
//        return resultMap;
//    }
      
      @Override
    public Map updateAllowanceTaxNew(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("employeeId") && map.containsKey("month") && map.containsKey("year") && map.containsKey("allowances")) {
                List<LinkedCaseInsensitiveMap> empAllowances = mapper.convertValue(map.get("allowances"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                });
                double standardAmount = 0.0;
                LinkedCaseInsensitiveMap standardDeduction = standardDeductionrepo.getstandDeductionAmount();
                if (standardDeduction != null) {
                    String standardDeductionLocal = standardDeduction.get("standard_deduction").toString();
                    standardAmount = Math.round(Double.parseDouble(standardDeduction.get("standard_deduction").toString()));
                } else {
                    resultMap.clear();
                    resultMap.put("msg", "please fill standard deduction form ");
                    resultMap.put("status", "error");
                    return resultMap;
                }
                System.out.println("map 1971"+map.toString());
                if (empAllowances != null) {
                    
                    Long empId = Long.parseLong(map.get("employeeId").toString());
                    Long orgId = Long.parseLong(map.get("orgId").toString());
                    Integer fyYear = Integer.parseInt(map.get("year").toString());
                    if(Integer.parseInt(map.get("month").toString())==1|| Integer.parseInt(map.get("month").toString())==2 || Integer.parseInt(map.get("month").toString())==3 ){
                        fyYear=fyYear-1;
                    }
                    Integer month = Integer.parseInt(map.get("month").toString())+1;
                    Integer year = Integer.parseInt(map.get("year").toString());
                    
                    List<IncomeTax> taxObj = incomeTaxRepo.isTaxSavedAlreadyGet(empId, orgId, month, year);
                  
                    // total month find here
                  
                    int remain_total_month_exception = totalWorkingMonthRepo.getTotalWorking(empId, orgId, month, year);
                    int diff_age = totalWorkingMonthRepo.getTotalAge(empId, orgId, month, year);
                    //        fetch run payroll allowances add in list
                    List<Double> allAllownceValue = new ArrayList<>();
                    empAllowances.stream().forEach(data -> {
                        data.forEach((key, value) -> {
                            Double val = Double.parseDouble(value.toString());
                            allAllownceValue.add(val);
                        });
                    });
                    double totalAllowanceSum = 0.0;
                  
                    String finalValue = "";
                    String Exemption_VI = "";
                    String Exemption_s10 = "";
                    String Deduction16 = "";
                    String Income_house_Property = "";
                    String Least_of_above_is_exemptdata = "";
                    String otherSec = "";
                    String IFPE = "";
                    String[] key = {"Exemption u/s VI A", "Exemption u/s s10", "Deduction u/s 16", "Income / Loss From House Property", "Least of above is exempt", "Other section(80D,80U,80G,80E,80DD,Nps)", "Income from previous employee"};
                    for (IncomeTax tax : taxObj) {
                        Optional<LinkedCaseInsensitiveMap> data = empAllowances.stream().filter(d -> d.containsKey(tax.getSalary_hra_name())).findFirst();
                        if (data.isPresent()) {
                            LinkedCaseInsensitiveMap allowanceData = data.get();
                            allowanceData.forEach((keys, value) -> {
                                if (keys.toString().equals(tax.getSalary_hra_name())) {  
                                    
                                    Double allowanceAdd = (Double.parseDouble(tax.getSalary_hra_amount()) + Double.parseDouble(allowanceData.get(tax.getSalary_hra_name()).toString()));
                                   
                                    tax.setSalary_hra_amount((allowanceAdd).toString());
                                    tax.setTax_allowance_id(tax.getTax_allowance_id());
                                }

                            });
//                            tax.setSalary_hra_amount(allowanceData.get(tax.getSalary_hra_name()).toString());
//                            tax.setTax_allowance_id(tax.getTax_allowance_id());
                        }  
                        if (tax.getSalary_hra_name().trim().equalsIgnoreCase("sub total")) {
                          
                             for (double i : allAllownceValue) {
                                totalAllowanceSum += i;
                            }
                               
                           finalValue = String.valueOf(totalAllowanceSum + Double.parseDouble(tax.getSalary_hra_amount()));
                           // finalValue = String.valueOf(Double.parseDouble(tax.getSalary_hra_amount()));

                            tax.setSalary_hra_amount(finalValue);
                        }
                    }
                    System.out.println("taxObj2033"+" "+taxObj);
                    List<IncomeTax> afterSaveAll = incomeTaxRepo.saveAll(taxObj);
                    for (IncomeTax taxObj_savepickup : taxObj) {
                        if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[0].trim())) {
                            Exemption_VI = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_exempted_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[1].trim())) {
                            Exemption_s10 = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[2].trim())) {
                            Deduction16 = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[3].trim())) {
                            Income_house_Property = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_exempted_amount())));
                        } else if (taxObj_savepickup.getSalary_hra_name().trim().equalsIgnoreCase(key[4].trim())) {
                            Least_of_above_is_exemptdata = String.valueOf((Double.parseDouble(taxObj_savepickup.getSalary_hra_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[5].trim())) {
                            otherSec = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        } else if (taxObj_savepickup.getExemption_name().trim().equalsIgnoreCase(key[6].trim())) {
                            IFPE = String.valueOf((Double.parseDouble(taxObj_savepickup.getExemption_declared_amount())));
                        }
                    }
                    //     variable defined here
                    Double Exemptions_under_sec_VIA_useOnlyCalculation = Double.parseDouble(Exemption_VI);
                    Double Income_loss_house_property = Double.parseDouble(Income_house_Property);
                    Double otherSection = Double.parseDouble(otherSec);
                    Double income_from_previous_employee = Double.parseDouble(IFPE);

                    Boolean isSaved = false;
                    Integer diffrence_age = (diff_age);
                    long emp_id = empId;
                    long organization_id = orgId;
                    String TaxSlabType = "NewTaxSlabKey";
                    int isInvestmentDeclared = inverstmentdeclarationrepo.isInvestmentDeclared(emp_id, organization_id,fyYear);
                    if (isInvestmentDeclared > 0) {
                        TaxSlabType = "OldTaxSlabKey";
                    }
                    Integer totalMonth = remain_total_month_exception;
                    Double Least_of_above_is_exempt = Double.parseDouble(Least_of_above_is_exemptdata);
                    Double homeLoanTotal = Double.parseDouble(Income_house_Property);
                    Double Exemptions_under_sec_VIA = Double.parseDouble(Exemption_VI);
                    Double totalAllowance = Double.parseDouble(finalValue);
                    int months = Integer.parseInt(map.get("month").toString());
                    int years = year;

                    Map getTaxMap = new HashMap();
                    getTaxMap.put("sub_total", finalValue);
                    getTaxMap.put("standardAmount", standardAmount);
                    getTaxMap.put("Deduction16", standardAmount);
                    getTaxMap.put("Exemptions_under_sec_VIA_useOnlyCalculation", Exemptions_under_sec_VIA_useOnlyCalculation);
                    getTaxMap.put("Income_loss_house_property", Income_loss_house_property);
                    getTaxMap.put("isSaved", isSaved);
                    getTaxMap.put("diffrence_age", diffrence_age);
                    getTaxMap.put("emp_id", emp_id);
                    getTaxMap.put("organization_id", organization_id);
                    getTaxMap.put("TaxSlabType", TaxSlabType);
                    getTaxMap.put("totalMonth",totalMonth );
                     //getTaxMap.put("totalMonth",0 );
                    getTaxMap.put("Least_of_above_is_exempt", Least_of_above_is_exempt);
                    getTaxMap.put("homeLoanTotal", homeLoanTotal);
                    getTaxMap.put("otherSec", otherSection);
                    getTaxMap.put("income_from_previous_employer", income_from_previous_employee);
                    getTaxMap.put("Exemptions_under_sec_VIA", Exemptions_under_sec_VIA);
                    getTaxMap.put("month", months);
                    getTaxMap.put("year", years);

                    Map taxdataOverRide = taxServiceImpl.getTax(getTaxMap);
                    Double tax_deduction_this_month = 0.0;
                    for (IncomeTax tax : taxObj) {
                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Income(rounded off)")) {
                            tax.setTax_amount(taxdataOverRide.get("Total Income(rounded off)").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax on Total Income")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax on Total Income").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Surcharge on Income")) {
                            tax.setTax_amount(taxdataOverRide.get("Surcharge on Income ").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Education Cess")) {
                            tax.setTax_amount(taxdataOverRide.get("Education Cess").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Payable")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax Payable").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Relief u/s 89")) {
                            tax.setTax_amount(taxdataOverRide.get("Relief u/s 89").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Tax Liability")) {
                            tax.setTax_amount(taxdataOverRide.get("Total Tax Liability").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deducted(Previous Employer)")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax Deducted(Previous Employer)").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Total Tax Deducted Till Date")) {
                            tax.setTax_amount(taxdataOverRide.get("Total Tax Deducted Till Date").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Remaining Tax/Remaining months")) {
                            tax.setTax_amount(taxdataOverRide.get("Remaining Tax/Remaining months").toString());
                        }
                        if (tax.getTax_name().trim().equalsIgnoreCase("Tax Deduction for this month")) {
                            tax.setTax_amount(taxdataOverRide.get("Tax Deduction for this month").toString());
                            tax_deduction_this_month = Double.parseDouble(taxdataOverRide.get("Tax Deduction for this month").toString());

                        }
                    }
                    //        fetch deduction id from deduction table 
//                    Long deductions = deduction.fetchDeductionId(orgId);
//
//                    // update data in employeeDeduction table for Monthly Tax
//                    emplDeduction.updateMonthlyTax(tax_deduction_this_month, deductions, empId, orgId);

                    List<IncomeTax> afterSaveAlls = incomeTaxRepo.saveAll(taxObj);
                    System.out.println("taxObj 1031"+" "+taxObj.toString());
                    resultMap.put("tdsupdate", taxdataOverRide.get("Tax Deduction for this month"));

//                     //        Get SalaryBreakup
//                    List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetchSalaryBreakupData(empId,orgId,(Integer.parseInt(map.get("month").toString())),year);
//                    System.out.println("SalaryBreakup data"+salaryBreakup);
//                    Map salaryBreakupData =new HashMap();
//                    if (!salaryBreakup.isEmpty()) {
//                        salaryBreakup.stream().forEach(salbreakup -> {
//                                salaryBreakupData.put("working_day", salbreakup.get("working_day"));
//                                salaryBreakupData.put("employee_type", salbreakup.get("employee_type"));
//                                salaryBreakupData.put("payable_basic", salbreakup.get("payable_basic"));
//                                salaryBreakupData.put("payable_gross", salbreakup.get("payable_gross"));
//                                salaryBreakupData.put("total_days", salbreakup.get("total_days"));
//                                salaryBreakupData.put("gross_salary", salbreakup.get("gross_salary"));
//                                salaryBreakupData.put("basic", salbreakup.get("basic"));
//                        });
//                    }
                    // update Deduction in Salary Breakup Deduction Part
//                    return this.salaryBreakupDeductionUpdateFromTax(orgId,empId,(Integer.parseInt(map.get("month").toString()) + 1),year,tax_deduction_this_month,salaryBreakupData);
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Allowance Object mapping error");
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "error");
            resultMap.put("msg", "Exception occured while updating allowance in Income Tax model");
        }
        return resultMap;
    }
   
}

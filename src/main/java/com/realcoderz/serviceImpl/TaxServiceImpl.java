package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Cess;
import com.realcoderz.model.Relief87A;
import com.realcoderz.model.SurCharge;
import com.realcoderz.model.Tax;
import com.realcoderz.model.TaxSlab;
import com.realcoderz.repository.CessRepository;
import com.realcoderz.repository.OtherSectionRepository;
import com.realcoderz.repository.Relief87ARepository;
import com.realcoderz.repository.SurChargeRepository;
import com.realcoderz.repository.TaxRepository;
import com.realcoderz.repository.TaxSlabRepository;
import com.realcoderz.service.TaxService;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.realcoderz.repository.TotalWorkingMonthRepository;
import com.realcoderz.repository.IncomeTaxRepository;
import com.realcoderz.model.TotalWorkingMonth;
import com.realcoderz.model.IncomeTax;
import com.realcoderz.model.NewTaxRegimeSlab;
import com.realcoderz.model.Relief87ANewRegime;
import com.realcoderz.model.StandardDeduction;
import com.realcoderz.model.TempararyAllowance;
import com.realcoderz.repository.AllowanceRepository;

import com.realcoderz.repository.EmployeeAllowanceRepository;
import com.realcoderz.repository.EmployeeRepository;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.NewTaxRegimeSlabRepository;
import com.realcoderz.repository.PayrollSettingRepository;
import com.realcoderz.repository.PercentageOfBasicRepository;
import com.realcoderz.repository.Relief87ANewRegimeRepo;
import com.realcoderz.repository.RentOfBasicRepository;
import com.realcoderz.repository.RunPayRollRepository;
import com.realcoderz.repository.StandardDeductionRepository;
import com.realcoderz.repository.TempararyAllowanceRepository;
import com.realcoderz.repository.TempararyDeductionRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank edited by Astha
 */
@Service
public class TaxServiceImpl implements TaxService {

    private static final ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(TaxServiceImpl.class);

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private OtherSectionRepository otherSectionRepo;

    @Autowired
    private TaxSlabRepository taxSlabRepo;

    @Autowired
    private SurChargeRepository surChargeRepo;

    @Autowired
    private Relief87ARepository relief87Repo;

    @Autowired
    private CessRepository cessRepo;

    @Autowired
    private Relief87ANewRegimeRepo relief87ANewRegimeRepo;
    @Autowired
    private IncomeTaxRepository incomeRepo;

    @Autowired
    private TotalWorkingMonthRepository totalWorkingMonthRepo;

    @Autowired
    private NewTaxRegimeSlabRepository newTaxRegimeSlabRepo;

    @Autowired
    private EmployeeAllowanceRepository employeeAllowance;

    @Autowired
    private IncomeTaxRepository incomeTaxRepo;

    @Autowired
    private InvestmentDeclarationRepository investentRepo;

    @Autowired
    private PercentageOfBasicRepository percentaceOfBasic;

    @Autowired
    private RentOfBasicRepository rentOfBasicRepo;

    @Autowired
    private RunPayRollRepository runPayRepo;

    @Autowired
    private EmployeeRepository employeeRepo;

    @Autowired
    private PayrollSettingRepository payRollRepo;

    @Autowired
    private StandardDeductionRepository standardDeduction;

    @Autowired
    private TempararyAllowanceRepository tempAllowancesRepo;

    @Autowired
    private TempararyDeductionRepository tempDeductionRepo;

    @Autowired
    private AllowanceRepository allowanceRepo;

//    Get Tax
    @Override
    public Map getTax(Map map) {
        Map resultMap = new LinkedHashMap<>();
        TotalWorkingMonth totalWorkingMonth = new TotalWorkingMonth();
        try {
            double totalMonth = Double.parseDouble(map.get("totalMonth").toString());
            boolean flag = false;
            boolean check = false;
            double exemptions = 0;
            double total_income = 0;
            double sub_total = 0;
            double standard_deduction = 0.0;
            double tax_deducted_till_date = 0;
            double taxable_income = 0;
            double tds = 0;
            double remaining_tax = 0;
            double tax_deduction_this_month = 0;
            double cess_leived = 0;
            double tax_liability = 0;
            double upper = 0;
            double tax_payable = 0;
            double relief_87A = 0;
            double sur_charge_leived = 0;
            double percent = 0;
            double sur_charge_percent = 0;
            Double tdsOfPreviousEmployeer = 0.0;
            double net_pay = 0;
            double taxableHra_exemptions = 0;
            double exemptions_sec_10 = 0;
            double least_exemptions = 0;
            double tdsOfPreviousTillDate = 0.0;
            double reliefIncome = 0;
            int saveMonth = 0;
            int saveYear = 0;
            int getMonth = 0;
            int getYear = 0;
            String taxType_old = "OldTaxSlabKey";
            String taxType_new = "NewTaxSlabKey";
            double saveTotalMonth = 0;
            int remain_total_monthLess = 1;
            boolean monthFlag = false;
//            if(Integer.parseInt(map.get("month").toString())==4){
//                monthFlag=true;
//            }
//        Checking Sub Total is Available or not
            int yearPrevious = 0;
            if (Integer.parseInt(map.get("month").toString()) == 1 || Integer.parseInt(map.get("month").toString()) == 2 || Integer.parseInt(map.get("month").toString()) == 3) {
                yearPrevious = Integer.parseInt(map.get("year").toString()) - 1;
            } else {
                yearPrevious = Integer.parseInt(map.get("year").toString());
            }
            System.out.println("map======" + map);
            if (map.containsKey("sub_total") && map.get("sub_total") != null) {
                if (map.containsKey("income_from_previous_employer") && !monthFlag) {
                    sub_total = Double.parseDouble(map.get("sub_total").toString()) + Double.parseDouble(map.get("income_from_previous_employer").toString());
                } else {
                    sub_total = Double.parseDouble(map.get("sub_total").toString());
                }
            } else {
                logger.info("Problem in TaxServiceImpl -> getTax() :: Sub Total is not Available");
            }

//           Standard Deduction divides into the working month
            if (map.containsKey("standardAmount") && map.get("standardAmount") != null) {
                standard_deduction = Double.parseDouble(map.get("standardAmount").toString()) / totalMonth;
            } else {
                logger.info("TaxServiceImpl -> getTax() :: Standard deduction is not Available");
            }

//        Checking Deduction u/s 16 is available or not
            if (map.containsKey("Deduction16") && map.get("Deduction16") != null) {
                exemptions += (double) map.get("Deduction16");

            }
//        Checking Exemptions under sec VIA is available or not
            double declaredExemptions = Double.parseDouble(map.get("Exemptions_under_sec_VIA").toString());
            double only_Exemptions = Double.parseDouble(map.get("Exemptions_under_sec_VIA_useOnlyCalculation").toString());
            if ((int) (declaredExemptions) >= (int) (only_Exemptions) && !monthFlag) {
                if (map.containsKey("Exemptions_under_sec_VIA_useOnlyCalculation") && map.get("Exemptions_under_sec_VIA_useOnlyCalculation") != null) {
                    exemptions += (double) map.get("Exemptions_under_sec_VIA_useOnlyCalculation");
//                exemptions += (double) map.get("Exemptions_under_sec_VIA");

                }
            } else {
                if (map.containsKey("Exemptions_under_sec_VIA") && map.get("Exemptions_under_sec_VIA") != null && !monthFlag) {
                    exemptions += (double) map.get("Exemptions_under_sec_VIA");
                }
            }
//        Checking Income loss house property is availabe or not
            if (map.containsKey("Income_loss_house_property") && map.get("Income_loss_house_property") != null && !monthFlag) {
                exemptions += (double) map.get("Income_loss_house_property");
            }
            if (map.containsKey("otherSec") && map.get("otherSec") != null && !monthFlag) {
                System.out.println("other*****=====" + map.get("otherSec"));
                exemptions += Double.parseDouble(map.get("otherSec").toString());
            }
            if ((map.get("TaxSlabType").toString()).equals(taxType_old) && !monthFlag) {
                least_exemptions = Double.parseDouble(map.get("Least_of_above_is_exempt").toString());
                if (least_exemptions < 0.0) {
                    least_exemptions = 0;
                }
            } else if ((map.get("TaxSlabType").toString()).equals(taxType_new)) {
                least_exemptions = 0;
            } else {
                if (map.containsKey("Least_of_above_is_exempt") && map.get("Least_of_above_is_exempt") != null) {

                    double leastofAbove = !monthFlag ? Double.parseDouble(map.get("Least_of_above_is_exempt").toString()) : 0;
                    least_exemptions = leastofAbove + exemptions;

                }
            }

//            if (map.containsKey("taxableHRA") && map.get("taxableHRA") != null) {
//
//                double taxableHra_exemp = Double.parseDouble(map.get("taxableHRA").toString());
//                taxableHra_exemptions = taxableHra_exemp + exemptions;
//                System.out.println("taxableHra" + taxableHra_exemptions);
//            }
            if (map.containsKey("Exemptions_under_sec_10") && map.get("Exemptions_under_sec_10") != null && !monthFlag) {
                double exemp_10 = Double.parseDouble(map.get("Exemptions_under_sec_10").toString());
                exemptions_sec_10 = exemp_10 + least_exemptions;

            }
            double standardAmount = Double.parseDouble(map.get("standardAmount").toString());
            if (sub_total != 0) {

                if ((map.get("TaxSlabType").toString()).equals(taxType_old) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                    double total_80C = 0;
                    if (map.containsKey("exemptions_sec_10_Total") && !monthFlag) {
                        total_80C = Double.parseDouble(map.get("exemptions_sec_10_Total").toString());
                        System.out.println("80c total=====" + total_80C);
                    }
                    double total_houseLoan = 0;
                    if (map.containsKey("homeLoanTotal") && !monthFlag) {
                        total_houseLoan = Double.parseDouble(map.get("homeLoanTotal").toString());
                    }

                    total_income = (Math.round((sub_total - (least_exemptions + exemptions)) * 100.0)) / 100.0;
                    taxable_income = total_income;
                    upper = taxable_income;

                } else if ((map.get("TaxSlabType").toString()).equals(taxType_new) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                    double total_80C = 0.0;
                    double total_houseLoan = 0.0;

                    total_income = (Math.round((sub_total - (least_exemptions + exemptions)) * 100.0)) / 100.0;
                    taxable_income = total_income;
                    upper = taxable_income;
                } else {

                    total_income = (Math.round((sub_total - (exemptions_sec_10 + exemptions)) * 100.0)) / 100.0;
                    taxable_income = total_income;
                    upper = taxable_income;
                }
            }

            //        Get the value of SurCharge
            List<SurCharge> surCharge = surChargeRepo.findSurCharge();
//        Get the values of Relief87A
            // List<Relief87A> relief87A = relief87Repo.findRelief87A();

            Relief87A relief87A = relief87Repo.findRelief87AOfOldRegime();

            Relief87ANewRegime relief87ANewRegime = relief87ANewRegimeRepo.relief87ANewReime(yearPrevious);

            String taxSlabUse = "OldTaxSlabKey";
//            Object obj=TaxSlabUse;
            String compare_TaxSlabUse = map.get("TaxSlabType").toString();
//            // employee Current Age get
//            
//            double current_employee_age=Double.parseDouble(map.get("current_employee_age").toString());

            // Here we check taX based on Old Tax Slab and New Tax Slab
            if ((taxSlabUse).equals(compare_TaxSlabUse)) {
                // Get the values of TaxSlab
                System.out.println("diffrence_age" + " " + map.get("diffrence_age"));

                List<TaxSlab> taxSlab = taxSlabRepo.findTaxSlab_byAge(Integer.parseInt(map.get("diffrence_age").toString()), Integer.parseInt(map.get("diffrence_age").toString()));
//                List<TaxSlab> taxSlab = taxSlabRepo.findTaxSlab(Long.parseLong(map.get("organization_id").toString()),Integer.parseInt(map.get("current_employee_age").toString()));
                //        SurCharge is Available

//        TaxSlab is Available
                if (!taxSlab.isEmpty()) {

                    int size = taxSlab.size();
                    TaxSlab last = taxSlab.get(size - 1);
                    for (TaxSlab t : taxSlab) {
                        if (t.getStart() != 0) {
//                    Check the taxable income is lie in the last tax slab
                            if (upper >= (last.getStart() - 1)) {
                                if (upper >= t.getEnd()) {

                                    tds += (Math.round((((t.getEnd() - (t.getStart() - 1)) * t.getRate()) / 100) * 100.0)) / 100.0;

//                                    tds += (Math.round(((t.getEnd() - (t.getStart()-1) * t.getRate()) / 100) * 100.0)) / 100.0;
                                } else {
                                    taxable_income = upper - (last.getStart() - 1);

                                    tds += (Math.round(((taxable_income * t.getRate()) / 100) * 100.0)) / 100.0;
                                }
                                check = true;
                            }
                            if (!check) {
//                        Get the percentage of TaxSlab
                                if (upper >= (t.getStart() - 1) && upper <= t.getEnd() || upper >= t.getEnd()) {
                                    percent = t.getRate();

////                            Relief87A is Available
//                                    if (!relief87A.isEmpty()) {
//                                        for (Relief87A r : relief87A) {
//                                            reliefIncome = r.getRate();
//                                            if (upper <= r.getIncome()) {
//                                                upper -= (t.getStart() - 1);
//                                                relief_87A = (Math.round(((upper * percent) / 100) * 100.0)) / 100.0;
//                                                tds = (Math.round(relief_87A * 100.0)) / 100.0;
//
//                                                flag = true;
//                                            }
//                                        }
//                                    }
                                }

                                if (!flag) {
                                    System.out.println("relief87A67" + " " + relief87A.toString());
                                    //percent = t.getRate();
                                    if ((upper > t.getEnd()) && (upper > t.getStart())) {
                                        double taxSlabDiff = (t.getEnd() - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                    } else if (upper <= (t.getEnd())) {
                                        double taxSlabDiff = (upper - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                        break;
                                    } else {
                                        System.out.println("Invalid tax on total income");
                                    }
                                    System.out.println(relief87A != null);

                                }
                            }
                        }

                    }

                    if (relief87A != null) {
                        System.out.println("upper 317" + " " + upper + " rate " + relief87A.getRate() + " " + relief87A.getIncome());
                        reliefIncome = relief87A.getRate();
                        if (upper <= relief87A.getIncome() + 1) {
                            if (tds < relief87A.getRate()) {
                                relief_87A = tds;
                            } else if (tds == relief87A.getRate()) {
                                relief_87A = relief87A.getRate();
                            } else {
                                relief_87A = 0;
                            }
                        }
                    }
                } else {
                    logger.info("Problem in TaxServiceImpl -> getTax() :: Tax Slab is not Available");
                }

                if (!surCharge.isEmpty()) {
                    for (SurCharge s : surCharge) {

                        if (total_income > s.getStart() && total_income < s.getEnd()) {
                            sur_charge_leived = (Math.round(((tds * s.getRate()) / 100) * 100.0)) / 100.0;
                            sur_charge_percent = s.getRate();

                        }
                    }
                }

            } else {
                System.out.println("tax slab map====== inside 363");

                List<NewTaxRegimeSlab> taxNewSlab = newTaxRegimeSlabRepo.findNewTaxSlab_byAge(Integer.parseInt(map.get("diffrence_age").toString()), Integer.parseInt(map.get("diffrence_age").toString()), String.valueOf(yearPrevious));

                //        SurCharge is Available
//        TaxSlab is Available
                if (!taxNewSlab.isEmpty()) {
                    // check size of taxslab 
                    int size = taxNewSlab.size();

                    // last tax slab get
                    NewTaxRegimeSlab last = taxNewSlab.get(size - 1);

                    for (NewTaxRegimeSlab t : taxNewSlab) {
                        //     here we check fisrt to last tax slab is zero or not  
                        if (t.getStart() != 0) {
//                    Check the taxable income is lie in the last tax slab
                            if (upper >= (last.getStart() - 1)) {
                                if (upper >= t.getEnd()) {
                                    tds += (Math.round((((t.getEnd() - (t.getStart() - 1)) * t.getRate()) / 100) * 100.0)) / 100.0;
                                } else {
                                    taxable_income = upper - (last.getStart() - 1);
                                    tds += (Math.round(((taxable_income * t.getRate()) / 100) * 100.0)) / 100.0;
                                }
                                check = true;
                            }
                            //      this condition run when  upper(tax on total income) is less than last tax slab value  
                            if (!check) {
//                        Get the percentage of TaxSlab
                                if (upper >= (t.getStart() - 1) && upper <= t.getEnd() || upper >= t.getEnd()) {
                                    percent = t.getRate();
//                            Relief87A is Available
//                                    if (!relief87A.isEmpty()) {
//                                        for (Relief87A r : relief87A) {
//                                            reliefIncome = r.getRate();
//                                            if (upper <= r.getIncome()) {
//                                                upper -= (t.getStart() - 1);
//                                                relief_87A = (Math.round(((upper * percent) / 100) * 100.0)) / 100.0;
//                                                tds = (Math.round(relief_87A * 100.0)) / 100.0;
//
//                                                flag = true;
//                                            }
//                                        }
//                                    }
                                }

                                if (!flag) {
                                    //  percent = t.getRate();
                                    if ((upper > t.getEnd()) && (upper > t.getStart())) {
                                        double taxSlabDiff = (t.getEnd() - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                    } else if (upper <= (t.getEnd())) {
                                        double taxSlabDiff = 0;
                                        if (upper > (t.getStart() - 1)) {
                                            taxSlabDiff = (upper - (t.getStart() - 1));
                                        } else {
                                            taxSlabDiff = ((t.getStart() - 1) - upper);
                                        }

                                        // double taxSlabDiff = ((t.getStart() - 1) - upper);
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                        break;
                                    } else {
                                        System.out.println("Invalid tax on total income");
                                    }

                                }
                            }
                        }

                    }

                    if (relief87ANewRegime != null) {
                        System.out.println("upper 424" + " " + upper + " rate " + relief87ANewRegime.getRate() + " " + relief87ANewRegime.getIncome());
                        reliefIncome = relief87ANewRegime.getRate();
                        if (upper <= relief87ANewRegime.getIncome() + 1) {
                            if (tds < relief87ANewRegime.getRate()) {
                                relief_87A = tds;
                            } else if (tds == relief87ANewRegime.getRate()) {
                                relief_87A = relief87ANewRegime.getRate();
                            } else {
                                relief_87A = 0;
                            }
                        }
                    }
                } else {

                    logger.info("Problem in TaxServiceImpl -> getTax() :: Tax Slab is not Available");
                }
            }

            if (!surCharge.isEmpty()) {
                for (SurCharge s : surCharge) {
                    if (total_income > s.getStart() && total_income < s.getEnd()) {
                        System.out.println("taxable_income 416" + " " + taxable_income);
                        sur_charge_leived = (Math.round(((tds * s.getRate()) / 100) * 100.0)) / 100.0;
                        System.out.println("sur_charge_leived" + tds);
                        sur_charge_percent = s.getRate();
                        System.out.println("sur_charge_percent 419" + " " + sur_charge_percent);
                    }
                }
            }

//        Get the values of Cess
            List<Cess> cess = cessRepo.findCess();
//        Cess is Available
            if (!cess.isEmpty()) {
                for (Cess c : cess) {
                    if (tds > reliefIncome) {
                        cess_leived = (Math.round(((c.getRate() * tds) / 100) * 100.0)) / 100.0;
                    } else {
                        cess_leived = 0;
                    }
                    double rate = 100 + c.getRate();
                    tax_payable = tds + cess_leived + sur_charge_leived;

                }
            }
            System.out.println("tds 477" + " " + tds);

            tax_liability = (Math.round((tax_payable - relief_87A) * 100.0)) / 100.0;

            System.out.println("yearPrevious 450" + " " + yearPrevious);

//        Get the TDS of Previous Employer
            tdsOfPreviousEmployeer = otherSectionRepo.getTdsOfPreviousEmployerPreviousVersion(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), yearPrevious);
            if (tdsOfPreviousEmployeer == null || monthFlag) {
                tdsOfPreviousEmployeer = 0.0;
            }

            // tax deduction till date here...........................
            if ((Integer.parseInt(map.get("month").toString())) == 0) {

                saveMonth = 0;
                getMonth = 12;
                getYear = Integer.parseInt(map.get("year").toString()) - 1;
                saveYear = Integer.parseInt(map.get("year").toString());

            } else if ((Integer.parseInt(map.get("month").toString())) == 1) {
                saveMonth = 1;
                getMonth = 13;
                if (map.get("where") != null) {
                    getYear = Integer.parseInt(map.get("year").toString());
                } else {
                    getYear = Integer.parseInt(map.get("year").toString()) - 1;
                }
                saveYear = Integer.parseInt(map.get("year").toString());
            } else if ((Integer.parseInt(map.get("month").toString())) == 2) {
                saveMonth = 2;
                getMonth = 2;
                getYear = Integer.parseInt(map.get("year").toString());
                saveYear = Integer.parseInt(map.get("year").toString());
            } else {
                saveMonth = Integer.parseInt(map.get("month").toString());
                saveYear = Integer.parseInt(map.get("year").toString());
                getMonth = Integer.parseInt(map.get("month").toString());
                getYear = Integer.parseInt(map.get("year").toString());

            }
            try {

                // till deduction tax     
                List<IncomeTax> incomeTaxValue = incomeRepo.isTaxSavedAlreadyGet(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
                if (getMonth == 4) {
                    incomeTaxValue = null;
                }
                tax_deducted_till_date = Double.parseDouble(incomeTaxValue.get(8).getTax_amount().toString());
                List<IncomeTax> incomeTaxValues = incomeRepo.isTaxSavedAlreadyGet(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
                tax_deduction_this_month = Double.parseDouble(incomeTaxValues.get(10).getTax_amount().toString());
                tax_deducted_till_date = tax_deduction_this_month + tax_deducted_till_date;

                // remaining tax
                List<IncomeTax> incomeTaxValue_remainTax = incomeRepo.isTaxSavedAlreadyGet(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
//                double remain_tax = Double.parseDouble(incomeTaxValue_remainTax.get(9).getTax_amount().toString());
//                remaining_tax = (Math.round((remain_tax- tax_deduction_this_month) * 100.0)) / 100.0;
                remaining_tax = (Math.round((tax_liability - (tdsOfPreviousEmployeer + tax_deducted_till_date)) * 100.0)) / 100.0;
                // tax deduction this month 
                int remain_total_month = totalWorkingMonthRepo.getTotalWorking(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
                remain_total_monthLess = (remain_total_month - 1);
                tax_deduction_this_month = (Math.round((remaining_tax / (remain_total_monthLess)) * 100.0)) / 100.0;

                // save remaining month
                // checked data all ready save or not
                int remain_total_month_SaveOrNo = totalWorkingMonthRepo.getTotalWorkingSaveOrNo(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth + 1, getYear);
                if (remain_total_month_SaveOrNo == 0) {
                    totalWorkingMonth.setRemainTotalMonth(remain_total_monthLess);
                    totalWorkingMonth.setEmployee_id(Long.parseLong(map.get("emp_id").toString()));
                    totalWorkingMonth.setOrganization_id(Long.parseLong(map.get("organization_id").toString()));
                    totalWorkingMonth.setYear(saveYear);
                    totalWorkingMonth.setMonth(saveMonth + 1);
                    totalWorkingMonth.setDiff_age(Integer.parseInt(map.get("diffrence_age").toString()));
//                    totalWorkingMonthRepo.save(totalWorkingMonth);

                    if (((map.get("isSaved").toString())) == "true") {
                        totalWorkingMonthRepo.save(totalWorkingMonth);
                    }

                }
            } catch (Exception ex) {

                tax_deducted_till_date = 0;
                tdsOfPreviousTillDate = (tdsOfPreviousEmployeer + tax_deducted_till_date);
                remaining_tax = (Math.round((tax_liability - tdsOfPreviousTillDate) * 100.0)) / 100.0;
                // tax deduction this month 
                tax_deduction_this_month = (Math.round((remaining_tax / (totalMonth)) * 100.0)) / 100.0;

                // save remaining month
                int remain_total_month_exception = totalWorkingMonthRepo.getTotalWorkingSaveOrNo(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth + 1, getYear);
                if (remain_total_month_exception == 0) {
                    saveTotalMonth = (saveTotalMonth + totalMonth);
                    int save_Month = (int) saveTotalMonth;
                    totalWorkingMonth.setRemainTotalMonth(save_Month);
                    totalWorkingMonth.setEmployee_id(Long.parseLong(map.get("emp_id").toString()));
                    totalWorkingMonth.setOrganization_id(Long.parseLong(map.get("organization_id").toString()));
                    totalWorkingMonth.setYear(saveYear);
                    totalWorkingMonth.setMonth(saveMonth + 1);
                    totalWorkingMonth.setDiff_age(Integer.parseInt(map.get("diffrence_age").toString()));
//                    totalWorkingMonthRepo.save(totalWorkingMonth);
                    if (((map.get("isSaved").toString())) == "true") {
                        totalWorkingMonthRepo.save(totalWorkingMonth);
                    }
                }

            }

            resultMap.put("status", "success");
            System.out.println("Total Income(rounded off) 544" + " " + total_income);
            resultMap.put("Total Income(rounded off)", Math.round(total_income));
            resultMap.put("Tax on Total Income", Math.round(tds));
            resultMap.put("Surcharge on Income ", Math.round(sur_charge_leived));
            resultMap.put("Education Cess", Math.round(cess_leived));
            resultMap.put("Tax Payable", Math.round(tax_payable));
            resultMap.put("Relief u/s 89", Math.round(relief_87A));
            resultMap.put("Total Tax Liability", Math.round(tax_liability));
            resultMap.put("Tax Deducted(Previous Employer)", Math.round(tdsOfPreviousEmployeer));
            resultMap.put("Total Tax Deducted Till Date", Math.round(tax_deducted_till_date));
//            resultMap.put("Remaining Tax/Remaining months", Math.round(remaining_tax));
            resultMap.put("Remaining Tax/Remaining months", remaining_tax < 0 ? 0 : Math.round(remaining_tax));

            // resultMap.put("Tax Deduction for this month", Math.round(tax_deduction_this_month));
            resultMap.put("Tax Deduction for this month", tax_deduction_this_month < 0 ? 0 : Math.round(tax_deduction_this_month));
            System.out.println("resultMap 603" + " " + resultMap.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Problem in TaxServiceImpl :: getTax() => " + ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @Override
    public Map saveTax(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Tax tax = mapper.convertValue(map, Tax.class);
            if (tax != null) {
                taxRepository.save(tax);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in TaxServiceImpl -> saveTax() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getTaxPreviousVersion(Map map) {
        Map resultMap = new LinkedHashMap<>();
        TotalWorkingMonth totalWorkingMonth = new TotalWorkingMonth();
        try {
            double totalMonth = Double.parseDouble(map.get("totalMonth").toString());
            System.out.println("totalMonth 637" + " " + totalMonth);
            boolean flag = false;
            boolean check = false;
            double exemptions = 0;
            double total_income = 0;
            double sub_total = 0;
            double standard_deduction = 0.0;
            double tax_deducted_till_date = 0;
            double taxable_income = 0;
            double tds = 0;
            double remaining_tax = 0;
            double tax_deduction_this_month = 0;
            double cess_leived = 0;
            double tax_liability = 0;
            double upper = 0;
            double tax_payable = 0;
            double relief_87A = 0;
            double sur_charge_leived = 0;
            double percent = 0;
            double sur_charge_percent = 0;
            Double tdsOfPreviousEmployeer = 0.0;
            double net_pay = 0;
            double taxableHra_exemptions = 0;
            double exemptions_sec_10 = 0;
            double least_exemptions = 0;
            double tdsOfPreviousTillDate = 0.0;
            double reliefIncome = 0;
            int saveMonth = 0;
            int saveYear = 0;
            int getMonth = 0;
            int getYear = 0;
            int yearForTdsOfPreviousEmployer = 0;
            String taxType_old = "OldTaxSlabKey";
            String taxType_new = "NewTaxSlabKey";
            double saveTotalMonth = 0;
            int remain_total_monthLess = 1;
            boolean monthFlag = false;
            if (Integer.parseInt(map.get("month").toString()) == 4) {
                monthFlag = true;
            }
//        Checking Sub Total is Available or not
            System.out.println("map======" + map);
            if (map.containsKey("sub_total") && map.get("sub_total") != null) {
                if (map.containsKey("income_from_previous_employer")) {
                    sub_total = Double.parseDouble(map.get("sub_total").toString()) + Double.parseDouble(map.get("income_from_previous_employer").toString());
                } else {
                    sub_total = Double.parseDouble(map.get("sub_total").toString());
                }
            } else {
                logger.info("Problem in TaxServiceImpl -> getTax() :: Sub Total is not Available");
            }

//           Standard Deduction divides into the working month
            if (map.containsKey("standardAmount") && map.get("standardAmount") != null) {
                standard_deduction = Double.parseDouble(map.get("standardAmount").toString()) / totalMonth;
            } else {
                logger.info("TaxServiceImpl -> getTax() :: Standard deduction is not Available");
            }

//        Checking Deduction u/s 16 is available or not
            if (map.containsKey("Deduction16") && map.get("Deduction16") != null) {
                exemptions += (double) map.get("Deduction16");

            }
//        Checking Exemptions under sec VIA is available or not
            double declaredExemptions = Double.parseDouble(map.get("Exemptions_under_sec_VIA").toString());
            double only_Exemptions = Double.parseDouble(map.get("Exemptions_under_sec_VIA_useOnlyCalculation").toString());
            if ((int) (declaredExemptions) >= (int) (only_Exemptions)) {
                if (map.containsKey("Exemptions_under_sec_VIA_useOnlyCalculation") && map.get("Exemptions_under_sec_VIA_useOnlyCalculation") != null) {
                    exemptions += (double) map.get("Exemptions_under_sec_VIA_useOnlyCalculation");
//                exemptions += (double) map.get("Exemptions_under_sec_VIA");

                }
            } else {
                if (map.containsKey("Exemptions_under_sec_VIA") && map.get("Exemptions_under_sec_VIA") != null) {
                    exemptions += (double) map.get("Exemptions_under_sec_VIA");
                }
            }
//        Checking Income loss house property is availabe or not
            if (map.containsKey("Income_loss_house_property") && map.get("Income_loss_house_property") != null) {
                exemptions += (double) map.get("Income_loss_house_property");
            }
            if (map.containsKey("otherSec") && map.get("otherSec") != null) {
                System.out.println("other*****=====" + map.get("otherSec"));
                exemptions += Double.parseDouble(map.get("otherSec").toString());
            }
            if ((map.get("TaxSlabType").toString()).equals(taxType_old)) {
                least_exemptions = Double.parseDouble(map.get("Least_of_above_is_exempt").toString());
                if (least_exemptions < 0.0) {
                    least_exemptions = 0;
                }
                System.out.println("least_exemptions 765" + " " + least_exemptions);
            } else if ((map.get("TaxSlabType").toString()).equals(taxType_new)) {
                least_exemptions = 0;
            } else {
                if (map.containsKey("Least_of_above_is_exempt") && map.get("Least_of_above_is_exempt") != null) {

                    double leastofAbove = Double.parseDouble(map.get("Least_of_above_is_exempt").toString());

                    least_exemptions = leastofAbove + exemptions;

                }
            }

//            if (map.containsKey("taxableHRA") && map.get("taxableHRA") != null) {
//
//                double taxableHra_exemp = Double.parseDouble(map.get("taxableHRA").toString());
//                taxableHra_exemptions = taxableHra_exemp + exemptions;
//                System.out.println("taxableHra" + taxableHra_exemptions);
//            }
            if (map.containsKey("Exemptions_under_sec_10") && map.get("Exemptions_under_sec_10") != null) {
                double exemp_10 = Double.parseDouble(map.get("Exemptions_under_sec_10").toString());
                exemptions_sec_10 = exemp_10 + least_exemptions;

            }
            double standardAmount = Double.parseDouble(map.get("standardAmount").toString());
            if (sub_total != 0) {

                if ((map.get("TaxSlabType").toString()).equals(taxType_old) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                    double total_80C = 0;
                    if (map.containsKey("exemptions_sec_10_Total")) {
                        total_80C = Double.parseDouble(map.get("exemptions_sec_10_Total").toString());
                        System.out.println("80c total=====" + total_80C);
                    }
                    double total_houseLoan = 0;
                    if (map.containsKey("homeLoanTotal")) {
                        total_houseLoan = Double.parseDouble(map.get("homeLoanTotal").toString());
                    }
                    System.out.println("house loan total=====" + total_houseLoan);

                    total_income = (Math.round((sub_total - (least_exemptions + exemptions)) * 100.0)) / 100.0;
                    taxable_income = total_income;
                    upper = taxable_income;

                } else if ((map.get("TaxSlabType").toString()).equals(taxType_new) && map.get("isSaved").toString().equalsIgnoreCase("false")) {
                    double total_80C = 0.0;
                    double total_houseLoan = 0.0;
                    total_income = (Math.round((sub_total - (least_exemptions + exemptions)) * 100.0)) / 100.0;
                    taxable_income = total_income;
                    upper = taxable_income;
                } else {
                    total_income = (Math.round((sub_total - (exemptions_sec_10 + exemptions)) * 100.0)) / 100.0;
                    taxable_income = total_income;
                    upper = taxable_income;
                }
            }

            //        Get the value of SurCharge
            List<SurCharge> surCharge = surChargeRepo.findSurCharge();
//        Get the values of Relief87A
            List<Relief87A> relief87A = relief87Repo.findRelief87A();

            String taxSlabUse = "OldTaxSlabKey";
//            Object obj=TaxSlabUse;
            String compare_TaxSlabUse = map.get("TaxSlabType").toString();
//            // employee Current Age get
//            
//            double current_employee_age=Double.parseDouble(map.get("current_employee_age").toString());

            // Here we check taX based on Old Tax Slab and New Tax Slab
            if ((taxSlabUse).equals(compare_TaxSlabUse)) {
                // Get the values of TaxSlab

                List<TaxSlab> taxSlab = taxSlabRepo.findTaxSlab_byAge(Integer.parseInt(map.get("diffrence_age").toString()), Integer.parseInt(map.get("diffrence_age").toString()));
//                List<TaxSlab> taxSlab = taxSlabRepo.findTaxSlab(Long.parseLong(map.get("organization_id").toString()),Integer.parseInt(map.get("current_employee_age").toString()));
                //        SurCharge is Available
//                if (!surCharge.isEmpty()) {
//                    for (SurCharge s : surCharge) {
//                        if (taxable_income > s.getStart() && taxable_income < s.getEnd()) {
//                            sur_charge_leived = (Math.round(((taxable_income * s.getRate()) / 100) * 100.0)) / 100.0;
//                            sur_charge_percent = s.getRate();
//                            System.out.println("sur_charge_percent" + sur_charge_percent);
//                        }
//                    }
//                }
//        TaxSlab is Available
                if (!taxSlab.isEmpty()) {

                    int size = taxSlab.size();
                    TaxSlab last = taxSlab.get(size - 1);
                    for (TaxSlab t : taxSlab) {
                        if (t.getStart() != 0) {
//                    Check the taxable income is lie in the last tax slab
                            if (upper >= (last.getStart() - 1)) {
                                if (upper >= t.getEnd()) {
                                    tds += (Math.round((((t.getEnd() - (t.getStart() - 1)) * t.getRate()) / 100) * 100.0)) / 100.0;

//                                    tds += (Math.round(((t.getEnd() - (t.getStart()-1) * t.getRate()) / 100) * 100.0)) / 100.0;
                                } else {
                                    taxable_income = upper - (last.getStart() - 1);
                                    tds += (Math.round(((taxable_income * t.getRate()) / 100) * 100.0)) / 100.0;
                                }
                                check = true;
                            }
                            if (!check) {
//                        Get the percentage of TaxSlab
                                if (upper >= (t.getStart() - 1) && upper <= t.getEnd() || upper >= t.getEnd()) {
                                    percent = t.getRate();
//                            Relief87A is Available
                                    if (!relief87A.isEmpty()) {
                                        for (Relief87A r : relief87A) {
                                            reliefIncome = r.getRate();
                                            if (upper <= r.getIncome()) {
                                                upper -= (t.getStart() - 1);
                                                relief_87A = (Math.round(((upper * percent) / 100) * 100.0)) / 100.0;
                                                tds = (Math.round(relief_87A * 100.0)) / 100.0;
//                                            if ((upper - (t.getStart() - 1)) != 0) {
//                                                tds = (Math.round(relief_87A * 100.0)) / 100.0;
//                                            } else {
//                                                tds = 0;
//                                            }
                                                flag = true;
                                            }
                                        }
                                    }
                                }

                                if (!flag) {
//                                    if (taxable_income > t.getEnd()) {
//                                        taxable_income -= t.getEnd();
//                                        tds += (Math.round((((t.getStart() - 1) * t.getRate()) / 100) * 100.0)) / 100.0;
//                                         System.out.println("tds 1."+tds);
//                                    } else {
//                                        tds += (Math.round(((taxable_income * percent) / 100) * 100.0)) / 100.0;
//                                          System.out.println("tds 1."+tds);
//                                        break;
//                                    }

                                    if ((upper > t.getEnd()) && (upper > t.getStart())) {
                                        double taxSlabDiff = (t.getEnd() - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                    } else if (upper <= (t.getEnd())) {
                                        double taxSlabDiff = (upper - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                        break;
                                    } else {
                                        System.out.println("Invalid tax on total income");
                                    }

                                }
                            }
                        }

                    }
                } else {
                    logger.info("Problem in TaxServiceImpl -> getTax() :: Tax Slab is not Available");
                }
                if (!surCharge.isEmpty()) {
                    for (SurCharge s : surCharge) {
                        if (total_income > s.getStart() && total_income < s.getEnd()) {
                            sur_charge_leived = (Math.round(((tds * s.getRate()) / 100) * 100.0)) / 100.0;
                            sur_charge_percent = s.getRate();
                            System.out.println("sur_charge_percent" + sur_charge_percent);
                        }
                    }
                }

            } else {

                String Year = map.get("year").toString();
                int previousYear = Integer.parseInt(Year);

                if (Integer.parseInt(map.get("month").toString()) == 0 || Integer.parseInt(map.get("month").toString()) == 1 || Integer.parseInt(map.get("month").toString()) == 2) {

                    System.out.println("inside if");

                    previousYear = previousYear - 1;

                }

                List<NewTaxRegimeSlab> taxNewSlab = newTaxRegimeSlabRepo.findNewTaxSlab_byAge(Integer.parseInt(map.get("diffrence_age").toString()), Integer.parseInt(map.get("diffrence_age").toString()), Integer.toString(previousYear));

                //        SurCharge is Available
//                if (!surCharge.isEmpty()) {
//                    for (SurCharge s : surCharge) {
//                        if (taxable_income > s.getStart() && taxable_income < s.getEnd()) {
//                            sur_charge_leived = (Math.round(((taxable_income * s.getRate()) / 100) * 100.0)) / 100.0;
//                            System.out.println("sur_charge_leived" + sur_charge_leived);
//                            sur_charge_percent = s.getRate();
//                        }
//                    }
//                }
//        TaxSlab is Available
                if (!taxNewSlab.isEmpty()) {
                    // check size of taxslab 
                    int size = taxNewSlab.size();

                    // last tax slab get
                    NewTaxRegimeSlab last = taxNewSlab.get(size - 1);

                    for (NewTaxRegimeSlab t : taxNewSlab) {
                        //     here we check fisrt to last tax slab is zero or not  
                        if (t.getStart() != 0) {
//                    Check the taxable income is lie in the last tax slab
                            if (upper >= (last.getStart() - 1)) {
                                if (upper >= t.getEnd()) {
                                    tds += (Math.round((((t.getEnd() - (t.getStart() - 1)) * t.getRate()) / 100) * 100.0)) / 100.0;
                                } else {
                                    taxable_income = upper - (last.getStart() - 1);
                                    tds += (Math.round(((taxable_income * t.getRate()) / 100) * 100.0)) / 100.0;
                                }
                                check = true;
                            }
                            //      this condition run when  upper(tax on total income) is less than last tax slab value  
                            if (!check) {
//                        Get the percentage of TaxSlab
                                if (upper >= (t.getStart() - 1) && upper <= t.getEnd() || upper >= t.getEnd()) {
                                    percent = t.getRate();
//                            Relief87A is Available
                                    if (!relief87A.isEmpty()) {
                                        for (Relief87A r : relief87A) {
                                            reliefIncome = r.getRate();
                                            if (upper <= r.getIncome()) {
                                                upper -= (t.getStart() - 1);
                                                relief_87A = (Math.round(((upper * percent) / 100) * 100.0)) / 100.0;
                                                tds = (Math.round(relief_87A * 100.0)) / 100.0;
//                                            if ((upper - (t.getStart() - 1)) != 0) {
//                                                tds = (Math.round(relief_87A * 100.0)) / 100.0;
//                                            } else {
//                                                tds = 0;
//                                            }
                                                flag = true;
                                            }
                                        }
                                    }
                                }

                                if (!flag) {

//                                if (taxable_income > t.getEnd()) {
//                                    taxable_income -= t.getEnd();
//                                    tds += (Math.round((((t.getStart() - 1) * t.getRate()) / 100) * 100.0)) / 100.0;
//                                } else {
                                    if ((upper > t.getEnd()) && (upper > t.getStart())) {
                                        double taxSlabDiff = (t.getEnd() - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                    } else if (upper <= (t.getEnd())) {
                                        double taxSlabDiff = (upper - (t.getStart() - 1));
                                        tds += (Math.round(((taxSlabDiff * percent) / 100) * 100.0)) / 100.0;

                                        break;
                                    } else {
                                        System.out.println("Invalid tax on total income");
                                    }
//                                    break;
//                                }

                                }
                            }
                        }

                    }
                } else {
                    logger.info("Problem in TaxServiceImpl -> getTax() :: Tax Slab is not Available");
                }
                if (!surCharge.isEmpty()) {
                    for (SurCharge s : surCharge) {
                        if (total_income > s.getStart() && total_income < s.getEnd()) {
                            sur_charge_leived = (Math.round(((tds * s.getRate()) / 100) * 100.0)) / 100.0;
                            System.out.println("sur_charge_leived" + sur_charge_leived);
                            sur_charge_percent = s.getRate();
                        }
                    }
                }

            }

//          SurCharge percent is not equal to zero
//            if (sur_charge_percent != 0) {
//                tds += (Math.round((tds * sur_charge_percent) / 100) * 100.0) / 100.0;
//
//            }
//        Get the values of Cess
            List<Cess> cess = cessRepo.findCess();
//        Cess is Available
            if (!cess.isEmpty()) {
                for (Cess c : cess) {
                    if (tds >= reliefIncome) {
                        cess_leived = (Math.round(((c.getRate() * tds) / 100) * 100.0)) / 100.0;
                    } else {
                        cess_leived = 0;
                    }
                    double rate = 100 + c.getRate();
                    tax_payable = tds + cess_leived + sur_charge_leived;

                }
            }

            tax_liability = (Math.round((tax_payable - relief_87A) * 100.0)) / 100.0;

            // tax deduction till date here...........................
            if ((Integer.parseInt(map.get("month").toString())) == 0) {

                saveMonth = 0;
                getMonth = 12;
                getYear = Integer.parseInt(map.get("year").toString()) - 1;
                yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString()) - 1;
                saveYear = Integer.parseInt(map.get("year").toString());

            } else if ((Integer.parseInt(map.get("month").toString())) == 1) {
                saveMonth = 1;
                getMonth = 13;
                if (map.get("where") != null) {
                    getYear = Integer.parseInt(map.get("year").toString());
                    yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString());
                } else {
                    getYear = Integer.parseInt(map.get("year").toString()) - 1;
                    yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString()) - 1;
                }
                saveYear = Integer.parseInt(map.get("year").toString());
            } else if ((Integer.parseInt(map.get("month").toString())) == 2) {
                saveMonth = 2;
                getMonth = 2;
                if (map.get("where") != null) {
                    yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString());
                } else {
                    yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString()) - 1;
                }
                getYear = Integer.parseInt(map.get("year").toString());
                saveYear = Integer.parseInt(map.get("year").toString());
            } else if ((Integer.parseInt(map.get("month").toString())) == 3) {
                saveMonth = Integer.parseInt(map.get("month").toString());
                saveYear = Integer.parseInt(map.get("year").toString());
                getMonth = Integer.parseInt(map.get("month").toString());
                getYear = Integer.parseInt(map.get("year").toString());
                if (map.get("where") != null) {
                    yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString());
                } else {
                    yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString()) - 1;
                }
            } else {
                saveMonth = Integer.parseInt(map.get("month").toString());
                saveYear = Integer.parseInt(map.get("year").toString());
                getMonth = Integer.parseInt(map.get("month").toString());
                getYear = Integer.parseInt(map.get("year").toString());
                yearForTdsOfPreviousEmployer = Integer.parseInt(map.get("year").toString());

            }
            //        Get the TDS of Previous Employer
            tdsOfPreviousEmployeer = otherSectionRepo.getTdsOfPreviousEmployerPreviousVersion(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), yearForTdsOfPreviousEmployer);
            if (tdsOfPreviousEmployeer == null) {
                tdsOfPreviousEmployeer = 0.0;
            }
            try {

                // till deduction tax     
                List<IncomeTax> incomeTaxValue = incomeRepo.isTaxSavedAlreadyGet(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
                if (getMonth == 4) {
                    incomeTaxValue = null;
                }
                tax_deducted_till_date = Double.parseDouble(incomeTaxValue.get(8).getTax_amount().toString());
                List<IncomeTax> incomeTaxValues = incomeRepo.isTaxSavedAlreadyGet(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
                tax_deduction_this_month = Double.parseDouble(incomeTaxValues.get(10).getTax_amount().toString());
                tax_deducted_till_date = tax_deduction_this_month + tax_deducted_till_date;

                // remaining tax
                List<IncomeTax> incomeTaxValue_remainTax = incomeRepo.isTaxSavedAlreadyGet(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
//                double remain_tax = Double.parseDouble(incomeTaxValue_remainTax.get(9).getTax_amount().toString());
//                remaining_tax = (Math.round((remain_tax- tax_deduction_this_month) * 100.0)) / 100.0;
                remaining_tax = (Math.round((tax_liability - (tdsOfPreviousEmployeer + tax_deducted_till_date)) * 100.0)) / 100.0;
                // tax deduction this month 
                int remain_total_month = totalWorkingMonthRepo.getTotalWorking(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth, getYear);
                if (remain_total_month == 1) {
                    remain_total_month = 13;
                }
                remain_total_monthLess = (remain_total_month - 1);
                tax_deduction_this_month = (Math.round((remaining_tax / (remain_total_monthLess)) * 100.0)) / 100.0;

                // save remaining month
                // checked data all ready save or not
                int remain_total_month_SaveOrNo = totalWorkingMonthRepo.getTotalWorkingSaveOrNo(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth + 1, getYear);
                if (remain_total_month_SaveOrNo == 0) {
                    totalWorkingMonth.setRemainTotalMonth(remain_total_monthLess);
                    totalWorkingMonth.setEmployee_id(Long.parseLong(map.get("emp_id").toString()));
                    totalWorkingMonth.setOrganization_id(Long.parseLong(map.get("organization_id").toString()));
                    totalWorkingMonth.setYear(saveYear);
                    totalWorkingMonth.setMonth(saveMonth + 1);
                    totalWorkingMonth.setDiff_age(Integer.parseInt(map.get("diffrence_age").toString()));
//                    totalWorkingMonthRepo.save(totalWorkingMonth);

                    if (((map.get("isSaved").toString())) == "true") {
                        totalWorkingMonthRepo.save(totalWorkingMonth);
                    }

                }
            } catch (Exception ex) {

                tax_deducted_till_date = 0;
                tdsOfPreviousTillDate = (tdsOfPreviousEmployeer + tax_deducted_till_date);
                remaining_tax = (Math.round((tax_liability - tdsOfPreviousTillDate) * 100.0)) / 100.0;
                // tax deduction this month 
                tax_deduction_this_month = (Math.round((remaining_tax / (totalMonth)) * 100.0)) / 100.0;

                // save remaining month
                int remain_total_month_exception = totalWorkingMonthRepo.getTotalWorkingSaveOrNo(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), getMonth + 1, getYear);
                if (remain_total_month_exception == 0) {
                    saveTotalMonth = (saveTotalMonth + totalMonth);
                    int save_Month = (int) saveTotalMonth;
                    totalWorkingMonth.setRemainTotalMonth(save_Month);
                    totalWorkingMonth.setEmployee_id(Long.parseLong(map.get("emp_id").toString()));
                    totalWorkingMonth.setOrganization_id(Long.parseLong(map.get("organization_id").toString()));
                    totalWorkingMonth.setYear(saveYear);
                    totalWorkingMonth.setMonth(saveMonth + 1);
                    totalWorkingMonth.setDiff_age(Integer.parseInt(map.get("diffrence_age").toString()));
//                    totalWorkingMonthRepo.save(totalWorkingMonth);
                    if (((map.get("isSaved").toString())) == "true") {
                        totalWorkingMonthRepo.save(totalWorkingMonth);
                    }
                }

            }

            resultMap.put("status", "success");
            resultMap.put("Total Income(rounded off)", Math.round(total_income));
            resultMap.put("Tax on Total Income", Math.round(tds));
            resultMap.put("Surcharge on Income ", Math.round(sur_charge_leived));
            resultMap.put("Education Cess", Math.round(cess_leived));
            resultMap.put("Tax Payable", Math.round(tax_payable));
            resultMap.put("Relief u/s 89", Math.round(relief_87A));
            resultMap.put("Total Tax Liability", Math.round(tax_liability));
            resultMap.put("Tax Deducted(Previous Employer)", Math.round(tdsOfPreviousEmployeer));
            resultMap.put("Total Tax Deducted Till Date", Math.round(tax_deducted_till_date));
            resultMap.put("Remaining Tax/Remaining months", Math.round(remaining_tax));
            resultMap.put("Tax Deduction for this month", Math.round(tax_deduction_this_month));
        } catch (Exception ex) {
            logger.error("Problem in TaxServiceImpl :: getTax() => " + ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @Override
    public Map saveTaxInBulk(List<Tax> incometax) {
        Map resultMap = new HashMap<>();
        try {

            if (incometax != null) {
                taxRepository.saveAll(incometax);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in TaxServiceImpl -> saveTax() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map calcuteTaxInBulk(List<LinkedHashMap> list, int month, int year, Long orgnizationId) {
        Map response = new HashMap();
        //  System.out.println("Calculate tax in bulk started"+" "+System.currentTimeMillis());
        logger.info("calcuteTaxInBulk called with incoming data " + " list" + " " + list.toString() + " month " + " " + month + " year " + year);
        List<Map> resultList = new ArrayList<>();
        List<Map> tdsList = new ArrayList<>();
        try {
//           we have to fetch all the paid allowances of financial year for that
//           we are creating  financial year for eg:- 2023 -2024     

            int startYear = year;
            int endYear = year + 1;
            int projectedCalculation[] = new int[1];
            projectedCalculation[0] = 15 - month;
            int remaingingMonth[] = new int[1];
            remaingingMonth[0] = projectedCalculation[0] + 1;
            int paymentMonth = month - 1;
            int paymentYear = year;

//          here we handling condition for the month oj Jan,Feb and Mar.because if the financial year is 2023 - 2024 we are getting 
//          year 2024         
            if (month == 1 || month == 2 || month == 3) {
                startYear = year - 1;
                endYear = year;
                projectedCalculation[0] = 3 - month;
                remaingingMonth[0] = projectedCalculation[0] + 1;
            }
            if (month == 1) {
                paymentMonth = 12;
                paymentYear = year - 1;
            }

            //  System.out.println("1240" + projectedCalculation[0]);
            // we are fethcing previous given allowances of employees
            List<LinkedCaseInsensitiveMap> allOrganization = employeeAllowance.alreadyPaidAllowances(orgnizationId, startYear, endYear);
            List<LinkedCaseInsensitiveMap> allOrganizationOtherAllowance = employeeAllowance.alreadyPaidOtherAllowance(orgnizationId, startYear, endYear);
            allOrganization.addAll(allOrganizationOtherAllowance);
            List<TempararyAllowance> tempAllowance = tempAllowancesRepo.getOrganizationMonthlyTempAllowance(orgnizationId, month, year);
            List<Long> allEmployeeId = new ArrayList<>();
            List<Long> allLatestSalaryBreaup = new ArrayList<>();

            list.stream().forEach(data -> {
                if (!allLatestSalaryBreaup.contains(Long.parseLong(data.get("sid").toString()))) {
                    allLatestSalaryBreaup.add(Long.parseLong(data.get("sid").toString()));
                }
                if (!allEmployeeId.contains(Long.parseLong(data.get("employee_id").toString()))) {
                    allEmployeeId.add(Long.parseLong(data.get("employee_id").toString()));
                }
            });

            // we are adding all the previous given allowances in this list
            List<Map> allowanceList = new ArrayList<>();

            allEmployeeId.stream().forEach(employeeId -> {
                Map employeeObject = new HashMap();
                allOrganization.stream().forEach(data -> {
                    if (Objects.equals(employeeId, Long.parseLong(data.get("employee_Id").toString()))) {
                        if (employeeObject.isEmpty()) {
                            employeeObject.put("employee_Id", data.get("employee_Id"));
                            employeeObject.put(data.get("allowance_name"), data.get("allowance_payable_amount"));
                        } else {
                            String allowance_name = data.get("allowance_name").toString();
                            if (employeeObject.containsKey(data.get("allowance_name").toString())) {
                                Double totalAmount = Double.parseDouble(employeeObject.get(allowance_name).toString()) + Double.parseDouble(data.get("allowance_payable_amount").toString());
                                employeeObject.put("employee_Id", data.get("employee_Id"));
                                employeeObject.put(data.get("allowance_name"), totalAmount);
                            } else {
                                employeeObject.put("employee_Id", data.get("employee_Id"));
                                employeeObject.put(data.get("allowance_name"), data.get("allowance_payable_amount"));
                            }
                        }
                    }
                });
                if (!employeeObject.isEmpty()) {
                    allowanceList.add(employeeObject);
                }

            });

            logger.info("paid allowances of organization=> " + allowanceList.toString());
            //System.out.println("allowanceList 1304"+" "+allowanceList.toString());

            //we are fetching starndard allowances of employees so that we can calculate the future projection
            List<LinkedCaseInsensitiveMap> organizationStandard = employeeAllowance.allOrganizationStandardAllowance(allLatestSalaryBreaup);
            List<LinkedCaseInsensitiveMap> allOrganizationStandardOtherAllowance = employeeAllowance.allOrganizationStandardOtherAllowance(allLatestSalaryBreaup);
            organizationStandard.addAll(allOrganizationStandardOtherAllowance);
            List<Map> employeeProjectedAndCurrentMonthTax = new ArrayList<>();
            Map closingAllowanceResponse = new HashMap();
            List<LinkedCaseInsensitiveMap> closingAllowances = new ArrayList<>();

            // here we are calculating future allowances projections and current month allowances and adding to a list
            list.stream().forEach(employees -> {
                Map employeeObject = new HashMap();
                organizationStandard.stream().forEach(data -> {
                    if (Objects.equals(Long.parseLong(employees.get("employee_id").toString()), Long.parseLong(data.get("employee_id").toString()))) {
                        Double projectedAmount = Double.parseDouble(data.get("allowance_amount").toString()) * projectedCalculation[0];

                        Double currentMonthAmount[] = new Double[1];
                        if (Double.parseDouble(employees.get("actual_duration").toString()) != 0) {
                            currentMonthAmount[0] = (Double.parseDouble(data.get("allowance_amount").toString()) / Double.parseDouble(employees.get("actual_duration").toString())) * (Double.parseDouble(employees.get("value").toString()) - Double.parseDouble(employees.get("absentDays").toString()));

                            if (data.get("allowance_name").toString().equalsIgnoreCase("Overtime Allowance") || data.get("allowance_name").toString().equalsIgnoreCase("Overtime Allowances")) {
 
                                Double overtime_vale = 0.0;
                                Double standard_hours = employees.get("standard_hours") != null ? Double.parseDouble(employees.get("standard_hours").toString()) : 0;
 
                                Double actualDays = employees.get("actual_duration") != null ? Double.parseDouble(employees.get("actual_duration").toString()) : 0;
                                Double rateOfPaymentPolicy = employees.get("rateOfPaymentPolicy") != null ? Double.parseDouble(employees.get("rateOfPaymentPolicy").toString()) : 1;
                                Double rateSaveInStandard=employees.get("rate") != null ? Double.parseDouble(employees.get("rate").toString()) : 0;
                                Double rate = 0.0;
                                Double grossSalary=0.0;
                                if(rateSaveInStandard>0){ 
                                grossSalary=(rateSaveInStandard*30)/actualDays;
                               }
                                else{
                             grossSalary = Double.parseDouble(employees.get("gross_salary").toString()) / actualDays;
                                }
                                //Double grossSalary = Double.parseDouble(employees.get("gross_salary").toString()) / actualDays;
                               // Double rate = 0.0;
                                if (standard_hours > 0) {
                                    rate = grossSalary / standard_hours;
                                }
                                rate = rate * rateOfPaymentPolicy;
                                rate = Math.round(rate * 100.0) / 100.0;
                                overtime_vale = rate * Double.parseDouble(employees.get("over_time").toString());
 
                                currentMonthAmount[0] = currentMonthAmount[0] + overtime_vale;
                            }
                        }

                        tempAllowance.stream().forEach(temp -> {
                            if (data.get("allowance_id") != null) {
                                if (Objects.equals(Long.parseLong(data.get("allowance_id").toString()), temp.getAllowanceId()) && Objects.equals(Long.parseLong(data.get("employee_id").toString()), temp.getEmployeeId())) {
                                    currentMonthAmount[0] = temp.getAmount();
                                }
                            }

                        });
                        Double totalAmount = projectedAmount + currentMonthAmount[0];
                        if (data.get("allowance_name") != null) {
                            employeeObject.put("employee_Id", data.get("employee_Id"));
                            employeeObject.put(data.get("allowance_name"), Math.round(totalAmount));
                        }

                    }
                });
                employeeProjectedAndCurrentMonthTax.add(employeeObject);

            });

            try {
                closingAllowanceResponse = this.getClosingAllowances(organizationStandard, month, year);
                if (closingAllowanceResponse.containsKey("status") && closingAllowanceResponse.get("status").toString().equalsIgnoreCase("success")) {
                    closingAllowances = (List<LinkedCaseInsensitiveMap>) closingAllowanceResponse.get("overtimeClosingAllowance");
                    List<LinkedCaseInsensitiveMap> currentAllowanceList = closingAllowances;
                    employeeProjectedAndCurrentMonthTax.forEach(projected -> {
                        currentAllowanceList.stream()
                                .filter(closing -> projected.get("employee_Id").toString()
                                .equals(closing.get("employee_id").toString()))
                                .findFirst()
                                .ifPresent(closing -> {
                                    // Replace values dynamically
                                    closing.forEach((key, value) -> {
                                        if (projected.containsKey(key)) {
                                            projected.put(key, value);
                                        }
                                    });
                                });
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        
          

            if (allowanceList.isEmpty()) {
                for (Map<String, Object> data : employeeProjectedAndCurrentMonthTax) {
                    long employeeIds = Long.parseLong(data.get("employee_Id").toString());

                    allEmployeeId.forEach(employeeId -> {
                        if (employeeIds == employeeId) {
                            double sum = data.entrySet().stream()
                                    .filter(entry -> !entry.getKey().equals("employee_Id"))
                                    .mapToDouble(entry -> Double.parseDouble(entry.getValue().toString()))
                                    .sum();

                            data.put("Sub Total", sum);
                        }

                    });
                }

            } else {
                employeeProjectedAndCurrentMonthTax.stream().forEach(currentAllowance -> {
                    Set keys = currentAllowance.keySet();
                    allowanceList.stream().forEach(previosuAlloeance -> {

                        if (!currentAllowance.toString().isEmpty() && currentAllowance.toString() != null && currentAllowance.toString().length() != 0 && !currentAllowance.toString().equalsIgnoreCase("{}")) {

                            if (Objects.equals(Long.parseLong(previosuAlloeance.get("employee_Id").toString()), Long.parseLong(currentAllowance.get("employee_Id").toString()))) {
                                Double sutotal[] = new Double[1];
                                sutotal[0] = 0.0;
                                keys.stream().forEach(key -> {

                                    if (!key.toString().equalsIgnoreCase("employee_Id")) {

                                        Double previosAmount = Double.parseDouble(currentAllowance.get(key).toString());
                                        Double currentAmount = previosuAlloeance.get(key) != null ? Double.parseDouble(previosuAlloeance.get(key).toString()) : 0;
                                        currentAllowance.put(key, Math.round(previosAmount + currentAmount));
                                        sutotal[0] += previosAmount + currentAmount;
                                    }

                                });

                                currentAllowance.put("Sub Total", sutotal[0]);

                            }
                        }

                    });
                });
                
            }
            
            //incase of allowance exsits in previous months but not in current months
            if(!allowanceList.isEmpty()){
            allowanceList.forEach(allowance -> {
            Long allowanceEmployeeId = Long.parseLong(allowance.get("employee_Id").toString());

            employeeProjectedAndCurrentMonthTax.stream()
                .filter(emp -> Objects.equals(Long.parseLong(emp.get("employee_Id").toString()), allowanceEmployeeId))
                .findFirst()
                .ifPresent(emp -> {
                    Double[] subTotal= new Double[1];
                    subTotal[0]= Double.parseDouble(emp.get("Sub Total").toString());
                    allowance.forEach((key, value) -> {
                        if (!emp.containsKey(key)) {
                           
                            subTotal[0]+=Double.parseDouble(value.toString());
                            emp.put(key, value);
                        }
                    });
                    
                    emp.put("Sub Total", subTotal[0]);
   
                });
        });
            }

            
            

            
            
            logger.info("employeeProjectedAndCurrentMonthTax=> 1464 " + employeeProjectedAndCurrentMonthTax.toString());
            //this is case when some employee may have previous allowance but some has no previous allowances.
            employeeProjectedAndCurrentMonthTax.forEach(data -> {
                if (!data.containsKey("Sub Total")) {
                    double subtotal = data.keySet().stream()
                            .filter(key -> !"employee_id".equalsIgnoreCase(key.toString()))
                            .mapToDouble(key -> Double.parseDouble(data.get(key).toString()))
                            .sum();
                    data.put("Sub Total", subtotal);
                }
            });

// Print the updated list
            List<LinkedCaseInsensitiveMap> taxDeductedTillDate = incomeTaxRepo.totalTaxTillDate(orgnizationId, startYear, endYear);

            employeeProjectedAndCurrentMonthTax.forEach(data -> {
                taxDeductedTillDate.forEach(tax -> {
                    if (Objects.equals(Long.parseLong(data.get("employee_Id").toString()), Long.parseLong(tax.get("employee_Id").toString()))) {
                        data.put("taxDeductedTillDate", tax.get("tax_amount"));
                    }
                });
            });
            List<LinkedCaseInsensitiveMap> oldTaxSlab = taxSlabRepo.oldTaxSlab();
            List<LinkedCaseInsensitiveMap> surCharge = incomeTaxRepo.surCharge();
            List<LinkedCaseInsensitiveMap> newTaxSlab = newTaxRegimeSlabRepo.newTaxSlab(startYear);
            LinkedCaseInsensitiveMap reliefList = incomeTaxRepo.relief();
            Relief87ANewRegime relief87ANewRegime = relief87ANewRegimeRepo.relief87ANewReime(startYear);
            LinkedCaseInsensitiveMap percentageOfBasic = percentaceOfBasic.getPercentageDataById();
            LinkedCaseInsensitiveMap rentOfBasic = rentOfBasicRepo.getBasicPercentageDataById();
            LinkedCaseInsensitiveMap cess = cessRepo.getRateOfCess();

            List<LinkedCaseInsensitiveMap> employeeInvestement = this.investmentDeclarationCalculation(startYear, orgnizationId, allEmployeeId);

            employeeProjectedAndCurrentMonthTax.forEach(data -> {

                if (!data.toString().equalsIgnoreCase("{}")) {
                    employeeInvestement.forEach(investment -> {
                        if (Objects.equals(Long.parseLong(data.get("employee_Id").toString()), Long.parseLong(investment.get("employeeid").toString()))) {
                            data.put("Rent Paid", investment.get("total_rent") != null ? Double.parseDouble(investment.get("total_rent").toString()) : 0);
                            if (investment.get("status") != null) {
                                //   System.out.println("Rent > 10% Basic"+" to "+investment.get("total_rent")+" "+data.get("employee_Id"));
                                Double basicPercentRent = rentOfBasic.get("basic_percentage") != null ? Double.parseDouble(rentOfBasic.get("basic_percentage").toString()) : 0.0;
                                Long basic10 = Math.round(Double.parseDouble(data.get("Basic Salary").toString()) / 100 * basicPercentRent);
                                Double rentActul = investment.get("total_rent") != null ? Double.parseDouble(investment.get("total_rent").toString()) - basic10 : 0;
                                data.put("Rent > 10% Basic", Math.round(rentActul));
                                if (investment.get("status").toString().equalsIgnoreCase("Metro")) {
                                    Double basicPercent = percentageOfBasic.get("metro_basicpercentage") != null ? Double.parseDouble(percentageOfBasic.get("metro_basicpercentage").toString()) : 0.0;
                                    double basicSalary = Double.parseDouble(data.get("Basic Salary").toString());
                                    double result = (basicSalary / 100) * basicPercent;
                                    data.put("40% or 50% of Basic", Math.round(result));
                                } else if (investment.get("status").toString().equalsIgnoreCase("Non Metro") || investment.get("status").toString().equalsIgnoreCase("non-metro")) {
                                    Double basicPercent = percentageOfBasic.get("non_metro_basicpercentage") != null ? Double.parseDouble(percentageOfBasic.get("non_metro_basicpercentage").toString()) : 0.0;
                                    double basicSalary = Double.parseDouble(data.get("Basic Salary").toString());
                                    double result = (basicSalary / 100) * basicPercent;
                                    data.put("40% or 50% of Basic", Math.round(result));
                                } else {
                                    data.put("40% or 50% of Basic", 0);
                                    data.put("Rent > 10% Basic", 0);
                                }
                            } else {

                                data.put("40% or 50% of Basic", 0);
                                data.put("Rent > 10% Basic", 0);
                            }
                            //  System.out.println(data.get("employee_Id")+" emp09");
                            //   System.out.println(data.get("HRA")+" emp09");
                            Double actualPaid = data.get("HRA") != null ? Double.parseDouble(data.get("HRA").toString()) : 0.0;
                            Double rentActul = data.get("40% or 50% of Basic") != null ? (Double.parseDouble(data.get("40% or 50% of Basic").toString())) : 0.0;
                            Double basic10 = Double.parseDouble(data.get("Rent > 10% Basic").toString()) < 0 ? 0.0 : Double.parseDouble(data.get("Rent > 10% Basic").toString());

                            Double leastValue = actualPaid;

                            if (basic10 < leastValue) {
                                leastValue = basic10;
                            }

                            if (rentActul < leastValue) {
                                leastValue = rentActul;
                            }

                            if (investment.get("totalDeduction") != null) {
                                data.put("Total income", Double.parseDouble(data.get("Sub Total").toString()) - Double.parseDouble(investment.get("totalDeduction").toString()) - leastValue);
                            } else {
                                data.put("Total income", Double.parseDouble(data.get("Sub Total").toString()) - 0 - leastValue);
                            }

                            //  actualPaid-leastValue
                            // data.get("HRA")
                            data.put("HRA Recived", investment.get("tax_slab_tpye") != null && investment.get("tax_slab_tpye").toString().equalsIgnoreCase("OldTaxSlabKey") ? data.get("HRA") : 0);
                            data.put("Least of above is exempt", leastValue);
                            data.put("Taxable HRA", investment.get("tax_slab_tpye") != null && investment.get("tax_slab_tpye").toString().equalsIgnoreCase("OldTaxSlabKey") ? actualPaid - leastValue : 0);
                            data.put("Other section(sec80d,sec80u,sec80g,sec80e,sec80dd,nps)", investment.get("Other section(sec80d,sec80u,sec80g,sec80e,sec80dd,nps)"));
                            data.put("income_from_previous_employer", investment.get("income_from_previous_employer"));
                            data.put("interest_on_housing_loan_before", investment.get("interest_on_housing_loan_before"));
                            data.put("Deduction u/s 16", investment.get("Deduction u/s 16"));
                            data.put("total_allowances", investment.get("total_allowances"));

                            data.put("tax_slab_tpye", investment.get("tax_slab_tpye"));
                            data.put("previousEmployerTds", investment.get("previousEmployerTds"));

                        }

                    });
                }

            });

            // System.out.println("employeeProjectedAndCurrentMonthTax 1413" + " " + employeeProjectedAndCurrentMonthTax.toString());
            employeeProjectedAndCurrentMonthTax.forEach(data -> {
                Double reimburse = data.get("Reimbursement") != null ? Double.parseDouble(data.get("Reimbursement").toString()) : 0.0;
                data.put("Total income", Double.parseDouble(data.get("Total income").toString()) - reimburse);
                Double taxLiability = 0.0;
                Map tdsMap = new HashMap();
                if (data.get("tax_slab_tpye") != null) {

                    Double taxableIncome = Double.parseDouble(data.get("Total income").toString());

                    if (data.get("tax_slab_tpye").toString().equalsIgnoreCase("OldTaxSlabKey")) {
                        Double reliefAmount = Double.parseDouble(reliefList.get("income").toString());
                        for (LinkedCaseInsensitiveMap slab : oldTaxSlab) {
                            Double slabStart = Double.parseDouble(slab.get("start").toString());
                            Double slabEnd = Double.parseDouble(slab.get("end").toString());
                            Double slabRate = Double.parseDouble(slab.get("rate").toString());

                            if (taxableIncome > slabStart) {
                                double currentSlabTaxableIncome = Math.min(taxableIncome, slabEnd) - slabStart;
                                taxLiability += currentSlabTaxableIncome * (slabRate / 100);

                            } else {
                                break; // No need to continue calculating if income doesn't fall into the slab
                            }
                        }
                        if (taxableIncome >= reliefAmount) {
                            data.put("Relief u/s 89", 0);
                            Double cessRate = cess.get("rate") != null ? Double.parseDouble(cess.get("rate").toString()) : 0.0;
                            Double educationCess = (taxLiability / 100) * cessRate;
                            data.put("Education Cess", Math.round(educationCess));
                        } else {
                            data.put("Relief u/s 89", Math.round(taxLiability));

                            data.put("Education Cess", 0);
                        }
                        data.put("Tax on total income", Math.round(taxLiability));

                    } else if (data.get("tax_slab_tpye").toString().equalsIgnoreCase("NewTaxSlabKey")) {
                        // System.out.println("inside 1447");
                        for (LinkedCaseInsensitiveMap slab : newTaxSlab) {
                            Double slabStart = Double.parseDouble(slab.get("start").toString());
                            Double slabEnd = Double.parseDouble(slab.get("end").toString());
                            Double slabRate = Double.parseDouble(slab.get("rate").toString());

                            if (taxableIncome > slabStart) {
                                double currentSlabTaxableIncome = Math.min(taxableIncome, slabEnd) - slabStart;
                                taxLiability += currentSlabTaxableIncome * (slabRate / 100);
                                //    System.out.println("taxLiability 1454" + " " + taxLiability);
                            } else {
                                break; // No need to continue calculating if income doesn't fall into the slab
                            }
                        }

                        if (taxableIncome >= relief87ANewRegime.getIncome()) {
                            data.put("Relief u/s 89", 0);
                            Double cessRate = cess.get("rate") != null ? Double.parseDouble(cess.get("rate").toString()) : 0.0;
                            Double educationCess = (taxLiability / 100) * cessRate;
                            data.put("Education Cess", Math.round(educationCess));

                        } else {
                            data.put("Relief u/s 89", Math.round(taxLiability));

                            data.put("Education Cess", 0);
                        }
                        data.put("Tax on total income", Math.round(taxLiability));

                        //  System.out.println("taxLiability 1454" + " " + taxLiability);
                        // Double educationCess = (taxLiability / 100) * 4;
                        //   data.put("Education Cess", educationCess);
                    }
                    double surcharge = 0;
                    for (LinkedCaseInsensitiveMap map : surCharge) {

                        Double startIncome = Double.parseDouble(map.get("start").toString());
                        Double endIncome = Double.parseDouble(map.get("end").toString());
                        if (taxableIncome >= startIncome && taxableIncome <= endIncome) {
                            Double rate = Double.parseDouble(map.get("rate").toString());
                            surcharge = (taxLiability / 100) * rate;

                        }

                    }
                    data.put("Surcharge on Income", Math.round(surcharge));
                    //   System.out.println("edeucation" + "" + data.get("Education Cess") + " " + " " + data.get("Tax on total income") + " " + data.get("Surcharge on Income"));
                    Double payBleTax = Double.parseDouble(data.get("Education Cess").toString()) + Double.parseDouble(data.get("Tax on total income").toString()) + Double.parseDouble(data.get("Surcharge on Income").toString());
                    data.put("Total Tax", Math.round(payBleTax));
                    double taxDeductedTill = data.get("taxDeductedTillDate") != null ? Double.parseDouble(data.get("taxDeductedTillDate").toString()) : 0.0;
                    data.put("taxDeductedTillDate", data.get("taxDeductedTillDate"));
                    double taxRemaining = (Math.round(payBleTax) - Double.parseDouble(data.get("previousEmployerTds").toString())) - taxDeductedTill;
                    if (taxRemaining < 0) {
                        taxRemaining = 0;
                    }
                    Double taxForThisMonth = taxRemaining / remaingingMonth[0];
                    data.put("previousEmployerTds", data.get("previousEmployerTds"));

                    if (Double.parseDouble(data.get("Relief u/s 89").toString()) > 0.0) {
                        data.put("Tax Liablity", 0);
                        data.put("Tax Deduction for this month", 0);
                        tdsMap.put("tds", 0);
                        tdsMap.put("employee_id", data.get("employee_Id"));
                        tdsList.add(tdsMap);
                        data.put("Remaining Tax", 0);
                    } else {
                        data.put("Tax Liablity", Math.round(payBleTax));
                        data.put("Tax Deduction for this month", Math.round(taxForThisMonth));
                        tdsMap.put("tds", Math.round(taxForThisMonth));
                        tdsMap.put("employee_id", data.get("employee_Id"));
                        tdsList.add(tdsMap);
                        data.put("Remaining Tax", taxRemaining);

                    }

                }

            });
            //  System.out.println("employeeProjectedAndCurrentMonthTax" + " " + employeeProjectedAndCurrentMonthTax.toString());
            List<LinkedCaseInsensitiveMap> updatedTds = tempDeductionRepo.getUpdatedMonthlyTdsOfOrg(orgnizationId, month, year);
            resultList = this.sortListOfTaxAccordingToPaySlip(employeeProjectedAndCurrentMonthTax, orgnizationId, month, year);
            List<Map> taxListTosave = new ArrayList<>();
            resultList.forEach(taxObject -> {
                List<Map> taxToSave = (List<Map>) taxObject.get("allList");
                if (taxToSave.size() > 0) {
                    taxListTosave.addAll(taxToSave);
                }

            });

            taxListTosave.stream().forEach(data -> {
                updatedTds.stream().forEach(tds -> {
                    if (data.get("employee_id") != null && Objects.equals(Long.parseLong(data.get("employee_id").toString()), Long.parseLong(tds.get("employee_id").toString()))) {
                        if (data.get("tax_name") != null && data.get("tax_name").toString().trim().equalsIgnoreCase("Tax Deduction for this month")) {
                            data.put("tax_amount", tds.get("amount"));
                        }
                    }
                });
            });

            tdsList.stream().forEach(data -> {
                updatedTds.stream().forEach(tds -> {
                    if (Objects.equals(Long.parseLong(data.get("employee_id").toString()), Long.parseLong(tds.get("employee_id").toString()))) {
                        data.put("tds", tds.get("amount"));
                    }
                });
            });

            response.put("status", "success");
            response.put("taxList", resultList);
            response.put("tds", tdsList);
            response.put("overtimeClosingAllowance", closingAllowances);

        } catch (Exception ex) {
            ex.printStackTrace();
            response.put("status", "exception");

        }

        logger.info(" output from calcuteTaxInBulk ->" + " " + response.toString());
        return response;
    }

    @Override
    public List<LinkedCaseInsensitiveMap> investmentDeclarationCalculation(int year, Long organizationId, List<Long> allEmployees) {
        List<LinkedCaseInsensitiveMap> finalList = new ArrayList<>();
        try {
            logger.info(" investmentDeclarationCalculation called with incoming data-> " + " allEmployees" + " " + allEmployees.toString() + " organizationId " + organizationId + " year " + year);
            finalList = employeeAllowance.allOrganizationInvesmentDecleration(organizationId, year);
            List<StandardDeduction> standard = standardDeduction.getStandardDeductionFinancialYear(year);
            Long oldTaxDeduction[] = new Long[1];
            Long newTaxDeduction[] = new Long[1];
            oldTaxDeduction[0] = 0L;
            newTaxDeduction[0] = 0L;
            standard.forEach(data -> {
                if (data.getTypeOfRegime() != null) {
                    if (data.getTypeOfRegime().equalsIgnoreCase("OldTaxSlabKey")) {
                        oldTaxDeduction[0] = data.getStandard_deduction();
                    } else {
                        newTaxDeduction[0] = data.getStandard_deduction();
                    }

                }

            });

            finalList.stream().forEach(data -> {
                Double sec80d = data.get("sec80d") != null ? Double.parseDouble(data.get("sec80d").toString()) : 0;
                Double sec80dd = data.get("sec80dd") != null ? Double.parseDouble(data.get("sec80dd").toString()) : 0;
                Double sec80e = data.get("sec80e") != null ? Double.parseDouble(data.get("sec80e").toString()) : 0;
                Double sec80u = data.get("sec80u") != null ? Double.parseDouble(data.get("sec80u").toString()) : 0;
                Double sec80g = data.get("sec80g") != null ? Double.parseDouble(data.get("sec80g").toString()) : 0;
                Double national_pension_scheme = data.get("national_pension_scheme") != null ? Double.parseDouble(data.get("national_pension_scheme").toString()) : 0;
                national_pension_scheme = national_pension_scheme > 50000 ? 50000 : national_pension_scheme;
                if (data.get("sec80d_type") != null) {
                    if (data.get("sec80d_type").toString().equalsIgnoreCase("parent")) {
                        sec80d = sec80d > 50000 ? 50000 : sec80d;
                    } else if (data.get("sec80d_type").toString().equalsIgnoreCase("self&family")) {
                        sec80d = sec80d > 25000 ? 25000 : sec80d;
                    }
                }

                Double otherSections = sec80d + sec80dd + sec80e + sec80u + sec80g + national_pension_scheme;
                Double intrest = 0.0;
                if (data.get("interest_on_housing_loan_before") != null) {
                    intrest = Double.parseDouble(data.get("interest_on_housing_loan_before").toString()) >= 200000 ? 200000 : Double.parseDouble(data.get("interest_on_housing_loan_before").toString());
                }
                data.put("Other section(sec80d,sec80u,sec80g,sec80e,sec80dd,nps)", otherSections);
                data.put("Deduction u/s 16", oldTaxDeduction[0]);
                // Double houseRent=data.get("total_rent")!=null?Double.parseDouble(data.get("total_rent").toString()):0;
                Double totalAllwoance = 0.0;
                if (data.get("total_allowances") != null) {
                    totalAllwoance = Double.parseDouble(data.get("total_allowances").toString()) <= 150000.0 ? Double.parseDouble(data.get("total_allowances").toString()) : 150000;
                }
                Double previousIncome = data.get("income_from_previous_employer") != null ? Double.parseDouble(data.get("income_from_previous_employer").toString()) : 0;
                data.put("Deduction u/s 16", oldTaxDeduction[0]);
                data.put("totalDeduction", otherSections + totalAllwoance + intrest + oldTaxDeduction[0] - previousIncome);
                data.put("tax_slab_tpye", data.get("tax_slab_tpye"));
                Double previousEmployerTds = data.get("tdsPreviousEmployer") != null ? Double.parseDouble(data.get("tdsPreviousEmployer").toString()) : 0;
                data.put("previousEmployerTds", previousEmployerTds);
            });
            List<LinkedCaseInsensitiveMap> employeeWithInvestMent = finalList;

            List<Long> missingIds = allEmployees.stream()
                    .filter(id -> employeeWithInvestMent.stream()
                    .noneMatch(map -> id.equals(Long.parseLong(map.get("employeeid").toString()))))
                    .collect(Collectors.toList());
            missingIds.forEach(data -> {

                LinkedCaseInsensitiveMap map = new LinkedCaseInsensitiveMap();
                map.put("employeeid", data);
                map.put("total_allowances", 0);
                map.put("total_rent", 0);
                map.put("income_from_previous_employer", 0);
                map.put("interest_on_housing_loan_before", 0);
                map.put("national_pension_scheme", 0);
                map.put("sec80d", 0);
                map.put("pf", 0);
                map.put("sec80d", 0);
                map.put("sec80dd", 0);
                map.put("sec80e", 0);
                map.put("sec80u", 0);
                map.put("sec80g", 0);
                map.put("status", null);
                map.put("Other section(sec80d,sec80u,sec80g,sec80e,sec80dd,nps)", 0);
                map.put("Deduction u/s 16", newTaxDeduction[0]);
                map.put("totalDeduction", newTaxDeduction[0]);
                map.put("tax_slab_tpye", "NewTaxSlabKey");
                map.put("previousEmployerTds", 0);
                employeeWithInvestMent.add(map);

            });

            finalList = employeeWithInvestMent;

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("output from investmentDeclarationCalculation ->" + " " + finalList.toString());
        return finalList;
    }

    @Override
    public List<Map> sortListOfTaxAccordingToPaySlip(List<Map> employeeProjectedAndCurrentMonthTax, Long organizationId, int month, int year) {
        logger.info(" sortListOfTaxAccordingToPaySlip methood called with incoming data -> organizationId " + organizationId + " month " + month + " year " + year + " list " + employeeProjectedAndCurrentMonthTax.toString());
        try {
            employeeProjectedAndCurrentMonthTax.forEach(data -> {
                List<LinkedCaseInsensitiveMap> allList = new ArrayList<>();
                Set keys = data.keySet();
                //  System.out.println("key852" + " " + keys.toString());

                keys.forEach(key -> {
                    if (key != null) {

                        if (!key.toString().equalsIgnoreCase("employee_Id") && !key.toString().equalsIgnoreCase("income_from_previous_employer")
                                && !key.toString().equalsIgnoreCase("Deduction u/s 16") && !key.toString().equalsIgnoreCase("Total income")
                                && !key.toString().equalsIgnoreCase("Other section(sec80d,sec80u,sec80g,sec80e,sec80dd,nps)")
                                && !key.toString().equalsIgnoreCase("interest_on_housing_loan_before")
                                && !key.toString().equalsIgnoreCase("totalDeduction") && !key.toString().equalsIgnoreCase("Surcharge on Income") && !key.toString().equalsIgnoreCase("Education Cess")
                                && !key.toString().equalsIgnoreCase("tax_slab_tpye") && !key.toString().equalsIgnoreCase("Relief u/s 89") && !key.toString().equalsIgnoreCase("Total Tax")
                                && !key.toString().equalsIgnoreCase("Tax on total income") && !key.toString().equalsIgnoreCase("previousEmployerTds") && !key.toString().equalsIgnoreCase("Tax Deduction for this month")
                                && !key.toString().equalsIgnoreCase("Remaining Tax") && !key.toString().equalsIgnoreCase("Tax Liablity") && !key.toString().equalsIgnoreCase("taxDeductedTillDate") && !key.toString().equalsIgnoreCase("total_allowances")) {
                            LinkedCaseInsensitiveMap mapObject = new LinkedCaseInsensitiveMap();
                            mapObject.put("salary_hra_name", key);
                            mapObject.put("salary_hra_amount", data.get(key));
                            //keyList.add(mapObject);
                            //data.put("list", keyList);data.get("total_allowances")
                            if (key.toString().equalsIgnoreCase("Basic Salary")) {
                                mapObject.put("exemption_name", "Deductions under Chapter VI-A");
                                mapObject.put("exemption_exempted_amount", "Excepted Amount");
                                mapObject.put("exemption_declared_amount", "Declared Amount");

                                mapObject.put("tax_name", "Total Income(rounded off)");
                                mapObject.put("tax_amount", data.get("Total income"));

                                allList.add(0, mapObject); // Add basicSalary at the first index data.get("total_allowances")
                            } else if (key.toString().equalsIgnoreCase("HRA")) {
                                mapObject.put("exemption_name", "Exemption u/s VI A");
                                mapObject.put("exemption_exempted_amount", Double.parseDouble(data.get("total_allowances").toString()) <= 150000.0 ? data.get("total_allowances") : 150000);
                                mapObject.put("exemption_declared_amount", data.get("total_allowances"));
                                mapObject.put("tax_name", "Tax on Total Income");
                                mapObject.put("tax_amount", data.get("Tax on total income"));
                                allList.add(0, mapObject);

                            } else {
                                allList.add(mapObject);
                            }

                        }
                    }

                });

                data.put("allList", allList);

            });

            employeeProjectedAndCurrentMonthTax.stream().forEach(data -> {

                List<LinkedCaseInsensitiveMap> allList = (List<LinkedCaseInsensitiveMap>) data.get("allList");
                if (allList.size() > 0) {

                    // List<Map<String, Object>> allList = new ArrayList<>();
// Populate the allList with the existing entries
                    LinkedCaseInsensitiveMap taxableHraEntry = null;
                    LinkedCaseInsensitiveMap exemptEntry = null;
                    LinkedCaseInsensitiveMap rent10Percent = null;
                    LinkedCaseInsensitiveMap basic40Percent = null;
                    LinkedCaseInsensitiveMap hraRecived = null;
                    LinkedCaseInsensitiveMap rentPaid = null;
                    LinkedCaseInsensitiveMap subTotal = null;

// Find the entries to be moved
                    for (LinkedCaseInsensitiveMap entry : allList) {
                        if (entry.get("salary_hra_name").toString().equalsIgnoreCase("Taxable HRA")) {
                            taxableHraEntry = entry;
                        } else if (entry.get("salary_hra_name").toString().equalsIgnoreCase("Least of above is exempt")) {
                            exemptEntry = entry;
                        } else if ((entry.get("salary_hra_name").toString().equalsIgnoreCase("Rent > 10% Basic"))) {
                            rent10Percent = entry;
                        } else if ((entry.get("salary_hra_name").toString().equalsIgnoreCase("40% or 50% of Basic"))) {
                            basic40Percent = entry;
                        } //                 else if((entry.get("salary_hra_name").toString().equalsIgnoreCase("40% or 50% of Basic"))){
                        //                    basic40Percent=entry;
                        //                }
                        else if ((entry.get("salary_hra_name").toString().equalsIgnoreCase("HRA Recived"))) {
                            hraRecived = entry;
                        } else if ((entry.get("salary_hra_name").toString().equalsIgnoreCase("Rent Paid"))) {
                            rentPaid = entry;
                        } else if ((entry.get("salary_hra_name").toString().equalsIgnoreCase("Sub Total"))) {
                            subTotal = entry;
                        }
                    }

                    if (taxableHraEntry != null) {
                        allList.remove(taxableHraEntry); // Remove the Taxable HRA entry
                        allList.add(taxableHraEntry); // Add it to the last index
                    }

                    if (exemptEntry != null) {
                        allList.remove(exemptEntry); // Remove the Least of above is exempt entry
                        allList.add(allList.size() - 1, exemptEntry); // Add it to the second-to-last index
                    }
                    if (rent10Percent != null) {
                        allList.remove(rent10Percent); // Remove the Least of above is exempt entry
                        allList.add(allList.size() - 2, rent10Percent); // Add it to the second-to-last index
                    }
                    if (basic40Percent != null) {
                        allList.remove(basic40Percent); // Remove the Least of above is exempt entry
                        allList.add(allList.size() - 3, basic40Percent); // Add it to the second-to-last index
                    }
                    if (hraRecived != null) {
                        allList.remove(hraRecived); // Remove the Least of above is exempt entry
                        allList.add(allList.size() - 4, hraRecived); // Add it to the second-to-last index
                    }
                    if (rentPaid != null) {
                        allList.remove(rentPaid); // Remove the Least of above is exempt entry
                        allList.add(allList.size() - 5, rentPaid); // Add it to the second-to-last index
                    }
                    if (subTotal != null) {
                        allList.remove(subTotal); // Remove the Least of above is exempt entry
                        allList.add(allList.size() - 6, subTotal); // Add it to the second-to-last index
                    }

                }

            });

            employeeProjectedAndCurrentMonthTax.stream().forEach(data -> {
                List<LinkedCaseInsensitiveMap> listTOEdit = (List<LinkedCaseInsensitiveMap>) data.get("allList");
                if (listTOEdit.size() > 2) {

                    LinkedCaseInsensitiveMap salarySlip3rdRow = listTOEdit.get(2);
                    salarySlip3rdRow.put("exemption_name", "Deductions under Section 10");

                    salarySlip3rdRow.put("exemption_declared_amount", "Exempted Amount");
                    salarySlip3rdRow.put("exemption_exempted_amount", null);
                    salarySlip3rdRow.put("tax_name", "Surcharge on Income");
                    salarySlip3rdRow.put("tax_amount", data.get("Surcharge on Income"));
                    listTOEdit.set(2, salarySlip3rdRow);

                    LinkedCaseInsensitiveMap salarySlip4thRow = listTOEdit.get(3);
                    salarySlip4thRow.put("exemption_name", "Other section(80D,80U,80G,80E,80DD,Nps)");
                    salarySlip4thRow.put("exemption_declared_amount", data.get("Other section(sec80d,sec80u,sec80g,sec80e,sec80dd,nps)"));
                    salarySlip4thRow.put("exemption_exempted_amount", null);
                    salarySlip4thRow.put("tax_name", "Education Cess");
                    salarySlip4thRow.put("tax_amount", data.get("Education Cess"));
                    listTOEdit.set(3, salarySlip4thRow);

                    LinkedCaseInsensitiveMap salarySlip5thRow = listTOEdit.get(4);
                    salarySlip5thRow.put("exemption_name", "Deductions u/s 16");
                    salarySlip5thRow.put("exemption_declared_amount", "Exempted Amount");
                    salarySlip5thRow.put("exemption_exempted_amount", null);
                    salarySlip5thRow.put("tax_name", "Tax Payable");
                    salarySlip5thRow.put("tax_amount", data.get("Total Tax"));
                    listTOEdit.set(4, salarySlip5thRow);

                    LinkedCaseInsensitiveMap salarySlip6thRow = listTOEdit.get(5);
                    salarySlip6thRow.put("exemption_name", "Deductions u/s 16");
                    salarySlip6thRow.put("exemption_declared_amount", data.get("Deduction u/s 16"));
                    salarySlip6thRow.put("exemption_exempted_amount", null);
                    salarySlip6thRow.put("tax_name", "Relief u/s 89");
                    salarySlip6thRow.put("tax_amount", data.get("Relief u/s 89"));
                    listTOEdit.set(5, salarySlip6thRow);
                    //"Declared Amount"
                    LinkedCaseInsensitiveMap salarySlip7thRow = listTOEdit.get(6);
                    salarySlip7thRow.put("exemption_name", "Other Income u/s 192(2B");
                    salarySlip7thRow.put("exemption_declared_amount", "Declared Amount");
                    salarySlip7thRow.put("exemption_exempted_amount", "Exempted Amount");
                    salarySlip7thRow.put("tax_name", "Total Tax Liability");
                    salarySlip7thRow.put("tax_amount", data.get("Tax Liablity"));
                    listTOEdit.set(6, salarySlip7thRow);

                    LinkedCaseInsensitiveMap salarySlip8thRow = listTOEdit.get(7);
                    salarySlip8thRow.put("exemption_name", "Income / Loss From House Property");
                    salarySlip8thRow.put("exemption_declared_amount", data.get("interest_on_housing_loan_before"));
                    if (data.get("interest_on_housing_loan_before") != null) {
                        salarySlip8thRow.put("exemption_exempted_amount", Double.parseDouble(data.get("interest_on_housing_loan_before").toString()) <= 200000.0 ? data.get("interest_on_housing_loan_before") : 200000);
                    } else {
                        salarySlip8thRow.put("exemption_exempted_amount", "-");
                    }
                    salarySlip8thRow.put("tax_name", "Tax Deducted(Previous Employer)");
                    salarySlip8thRow.put("tax_amount", data.get("previousEmployerTds"));
                    listTOEdit.set(7, salarySlip8thRow);

                    LinkedCaseInsensitiveMap salarySlip9thRow = listTOEdit.get(8);
                    salarySlip9thRow.put("exemption_name", "Income from previous employee");
                    salarySlip9thRow.put("exemption_declared_amount", data.get("income_from_previous_employer") != null ? data.get("income_from_previous_employer") : "-");
                    salarySlip9thRow.put("exemption_exempted_amount", null);
                    salarySlip9thRow.put("tax_name", "Total Tax Deducted Till Date");
                    salarySlip9thRow.put("tax_amount", data.get("taxDeductedTillDate") != null ? data.get("taxDeductedTillDate") : 0);
                    listTOEdit.set(8, salarySlip9thRow);

                    LinkedCaseInsensitiveMap salarySlip10thRow = listTOEdit.get(9);
                    salarySlip10thRow.put("tax_name", "Remaining Tax/Remaining months");
                    salarySlip10thRow.put("tax_amount", data.get("Remaining Tax"));
                    salarySlip10thRow.put("exemption_name", "Reimbursement Exempted");
                    salarySlip10thRow.put("exemption_declared_amount", data.get("Reimbursement") != null ? data.get("Reimbursement") : "-");
                    salarySlip10thRow.put("exemption_exempted_amount", null);
                    listTOEdit.set(9, salarySlip10thRow);

                    LinkedCaseInsensitiveMap salarySlip11thRow = listTOEdit.get(10);
                    salarySlip11thRow.put("tax_name", "Tax Deduction for this month");
                    salarySlip11thRow.put("tax_amount", data.get("Tax Deduction for this month"));
                    listTOEdit.set(10, salarySlip11thRow);

                }
                for (LinkedCaseInsensitiveMap map : listTOEdit) {
                    if (!map.containsKey("employee_id")) {
                        map.put("employee_id", data.get("employee_Id")); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("exemption_name")) {
                        map.put("exemption_name", null); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("exemption_exempted_amount")) {
                        map.put("exemption_exempted_amount", null); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("exemption_declared_amount")) {
                        map.put("exemption_declared_amount", null); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("tax_name")) {
                        map.put("tax_name", null); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("tax_amount")) {
                        map.put("tax_amount", null); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("organization_id")) {
                        map.put("organization_id", organizationId); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("month")) {
                        map.put("month", month); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }
                    if (!map.containsKey("year")) {
                        map.put("year", year); // Set the value for the employee_id key (you can replace "" with the desired value)
                    }

                }

            });

            logger.info("output from sortListOfTaxAccordingToPaySlip() " + " " + employeeProjectedAndCurrentMonthTax.toString());
            return employeeProjectedAndCurrentMonthTax;
        } catch (Exception e) {
            logger.error("error in sortListOfTaxAccordingToPaySlip()-> " + " " + e.getMessage());
            e.printStackTrace();
            return employeeProjectedAndCurrentMonthTax;
        }
    }

    @Override
    public Map getTaxSlip(Long employeeId, int month, int year) {
        Map response = new HashMap();
        try {
            List<LinkedCaseInsensitiveMap> taxList = incomeTaxRepo.getTaxList(employeeId, month, year);
            List<LinkedHashMap> taxListOrder = new ArrayList<>();
            List<String> keyOrder = Arrays.asList(
                    "salary_hra_name",
                    "salary_hra_amount",
                    "exemption_name",
                    "exemption_declared_amount",
                    "exemption_exempted_amount",
                    "tax_name",
                    "tax_amount"
            );
            for (LinkedCaseInsensitiveMap linkedMap : taxList) {

                LinkedHashMap<String, Object> regularMap = reorderKeys(linkedMap, keyOrder);
                taxListOrder.add(regularMap);
            }
            // Define the desired order of keys

//         
//           // Create a custom comparator to order the maps based on the desired key order
//        Comparator<Map> customComparator = (map1, map2) -> {
//            int index1 = keyOrder.indexOf(map1.firstKey());
//            int index2 = keyOrder.indexOf(map2.firstKey());
//            return Integer.compare(index1, index2);
//        };
//
//        // Sort the list based on the custom comparator
//        taxList.sort(customComparator);
            //  System.out.println("taxListOrder 1995" + " " + taxListOrder.toString());
            response.put("rows", taxListOrder);
            response.put("status", "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private static LinkedHashMap<String, Object> reorderKeys(Map<String, Object> originalMap, List<String> keyOrder) {
        LinkedHashMap<String, Object> reorderedMap = new LinkedHashMap<>();
        for (String key : keyOrder) {
            if (originalMap.containsKey(key)) {
                reorderedMap.put(key, originalMap.get(key));
            }
        }
        return reorderedMap;
    }

    @Override
    public Map calculateTaxWhileUpdatingAllowance(Double previosAllowance, Double updatedAllowance, List<Map> employeeAloowancesUi, Long employeeId, int month, int year, Double previousExpense) {
        Map response = new HashMap();
        try {

            int startYear = year;
            int remaingMonths = 16 - month;
            int previousMonth = month - 1;
            int previousYaer = year;
            if (month == 1 || month == 2 || month == 3) {
                startYear = year - 1;
                remaingMonths = 4 - month;
            }
            if (month == 1) {
                previousMonth = 12;
                previousYaer = year - 1;
            }

            Double previousExpenses[] = new Double[1];
            previousExpenses[0] = 0.0;

            Double expense[] = new Double[1];
            expense[0] = 0.0;
            employeeAloowancesUi.stream().filter(data -> data.get("allowance_name").toString().equalsIgnoreCase("Reimbursement")).forEach(data -> {

                expense[0] = Double.parseDouble(data.get("allowance_payable_amount").toString());
              
            });

            List<LinkedCaseInsensitiveMap> employeePreviousAllowances = new ArrayList<>();
            if (month != 4) {
                employeePreviousAllowances = incomeRepo.previousEmployeeAllowances(previousYaer, previousMonth, employeeId);
              
            }
            employeePreviousAllowances.stream().filter(data -> data.get("salary_hra_name").toString().equalsIgnoreCase("Reimbursement")).forEach(data -> {

                previousExpenses[0] = Double.parseDouble(data.get("salary_hra_amount").toString());
               
            });
            if (employeePreviousAllowances != null && !employeePreviousAllowances.isEmpty() && employeePreviousAllowances.size() != 0) {
                for (Map updatedAllowances : employeeAloowancesUi) {
                    if (updatedAllowances != null) {
                        for (LinkedCaseInsensitiveMap previousAllowance : employeePreviousAllowances) {

                            if (updatedAllowances.get("allowance_name").toString().equalsIgnoreCase(previousAllowance.get("salary_hra_name").toString())) {
                                Double allowances = Double.parseDouble(updatedAllowances.get("allowance_payable_amount").toString())
                                        + Double.parseDouble(previousAllowance.get("salary_hra_amount").toString());
                                updatedAllowances.put("allowance_payable_amount", allowances);

                            }
                        }
                    }

                }
            }

            Double updatedPreviousAllowance[] = new Double[1];

            updatedPreviousAllowance[0] = employeeAloowancesUi.stream()
                    .filter(data -> data != null && data.get("allowance_payable_amount") != null)
                    .mapToDouble(data -> Double.parseDouble(data.get("allowance_payable_amount").toString()))
                    .sum();

            logger.info("calculateTaxWhileUpdatingAllowance called with this input-> previosAllowancesum: " + previosAllowance + " updatedAllowancesum " + updatedAllowance + "employeeAloowancesUi " + employeeAloowancesUi.toString() + " employeeId " + employeeId + " month" + month + "year " + year);

            List<IncomeTax> incomeTax = incomeRepo.employeeIncomeTax(year, month, employeeId);
            List<LinkedCaseInsensitiveMap> oldTaxSlab = taxSlabRepo.oldTaxSlab();
            List<LinkedCaseInsensitiveMap> surCharge = incomeTaxRepo.surCharge();
            List<LinkedCaseInsensitiveMap> newTaxSlab = newTaxRegimeSlabRepo.newTaxSlab(startYear);
            LinkedCaseInsensitiveMap reliefList = incomeTaxRepo.relief();
            Relief87ANewRegime relief87ANewRegime = relief87ANewRegimeRepo.relief87ANewReime(startYear);

            LinkedCaseInsensitiveMap investment = investentRepo.employeeInvestment(employeeId, startYear);
            String taxSlabType = investment != null ? investment.get("tax_slab_tpye").toString() : "NewTaxSlabKey";
            Double taxableIncome[] = new Double[1];
            taxableIncome[0] = 0.0;
            Double taxLiability[] = new Double[1];
            taxLiability[0] = 0.0;
            Double relief[] = new Double[1];
            relief[0] = 0.0;
            Double educationCess[] = new Double[1];
            educationCess[0] = 0.0;
            Double previosuEmployerTax[] = new Double[1];
            previosuEmployerTax[0] = 0.0;
            Double taxDeductedTillDate[] = new Double[1];
            taxDeductedTillDate[0] = 0.0;
            Double taxThisMonth[] = new Double[1];
            taxThisMonth[0] = 0.0;

            //5000 -0
            Double expenseExemted[] = new Double[1];
            expenseExemted[0] = 0.0;

            incomeTax.forEach(tax -> {
                employeeAloowancesUi.forEach(allowances -> {

                    if (allowances != null && tax.getSalary_hra_name().equalsIgnoreCase(allowances.get("allowance_name").toString())) {

                        if (tax.getSalary_hra_name().equalsIgnoreCase("Reimbursement")) {
                            expenseExemted[0] = Double.parseDouble(allowances.get("allowance_payable_amount").toString());
                        }
                        tax.setSalary_hra_amount(String.valueOf(Math.round(Double.parseDouble(allowances.get("allowance_payable_amount").toString()))));

                    }
                });

                if (tax.getSalary_hra_name() != null && (tax.getSalary_hra_name().equalsIgnoreCase("Sub Total") || tax.getSalary_hra_name().equalsIgnoreCase("SubTotal"))) {
                    Double subTotal = Double.parseDouble(tax.getSalary_hra_amount()) - previosAllowance + updatedAllowance;
                    tax.setSalary_hra_amount(String.valueOf(Math.round(subTotal)));
                }
                if (tax.getTax_name() != null && tax.getTax_name().equalsIgnoreCase("Total Income(rounded off)")) {

                    // 6000-3000
                    expense[0] = expense[0] - previousExpense;

                    Double totalIncome = Double.parseDouble(tax.getTax_amount()) - previosAllowance + updatedAllowance - expense[0];
                    tax.setTax_amount(String.valueOf(Math.round(totalIncome)));
                    taxableIncome[0] = totalIncome;
                }
                if (tax.getTax_name() != null && tax.getTax_name().equalsIgnoreCase("Tax Deducted(Previous Employer)")) {
                    previosuEmployerTax[0] = Double.parseDouble(tax.getTax_amount());
                }
                if (tax.getTax_name() != null && tax.getTax_name().equalsIgnoreCase("Total Tax Deducted Till Date")) {
                    taxDeductedTillDate[0] = Double.parseDouble(tax.getTax_amount());
                }
                if (tax.getExemption_name() != null && tax.getExemption_name().equalsIgnoreCase("Reimbursement Exempted")) {
                    tax.setExemption_declared_amount(String.valueOf(Math.round(expenseExemted[0])));
                }

            });

            if (taxSlabType.equalsIgnoreCase("OldTaxSlabKey")) {
                Double reliefAmount = Double.parseDouble(reliefList.get("income").toString());
                for (LinkedCaseInsensitiveMap slab : oldTaxSlab) {
                    Double slabStart = Double.parseDouble(slab.get("start").toString());
                    Double slabEnd = Double.parseDouble(slab.get("end").toString());
                    Double slabRate = Double.parseDouble(slab.get("rate").toString());

                    if (taxableIncome[0] > slabStart) {
                        double currentSlabTaxableIncome = Math.min(taxableIncome[0], slabEnd) - slabStart;
                        taxLiability[0] += currentSlabTaxableIncome * (slabRate / 100);

                    } else {
                        break; // No need to continue calculating if income doesn't fall into the slab
                    }
                }
                if (taxableIncome[0] > reliefAmount) {

                    educationCess[0] = (taxLiability[0] / 100) * 4;
                } else {
                    relief[0] = taxLiability[0];

                }
            } else {

                for (LinkedCaseInsensitiveMap slab : newTaxSlab) {
                    Double slabStart = Double.parseDouble(slab.get("start").toString());
                    Double slabEnd = Double.parseDouble(slab.get("end").toString());
                    Double slabRate = Double.parseDouble(slab.get("rate").toString());

                    if (taxableIncome[0] > slabStart) {
                        double currentSlabTaxableIncome = Math.min(taxableIncome[0], slabEnd) - slabStart;
                        taxLiability[0] += currentSlabTaxableIncome * (slabRate / 100);
                    } else {
                        break; // No need to continue calculating if income doesn't fall into the slab
                    }
                }

                if (taxableIncome[0] > relief87ANewRegime.getIncome()) {
                    educationCess[0] = (taxLiability[0] / 100) * 4;

                } else {
                    relief[0] = taxLiability[0];
                }
            }
            long surcharge = 0;
            for (LinkedCaseInsensitiveMap map : surCharge) {

                Double startIncome = Double.parseDouble(map.get("start").toString());
                Double endIncome = Double.parseDouble(map.get("end").toString());
                if (taxableIncome[0] >= startIncome && taxableIncome[0] <= endIncome) {
                    Double rate = Double.parseDouble(map.get("rate").toString());
                    surcharge = Math.round((taxLiability[0] / 100) * rate);

                }

            }


            Long payBleTax = Math.round(educationCess[0] + taxLiability[0] + surcharge);

            double taxRemaining = (Math.round(payBleTax) - previosuEmployerTax[0] - taxDeductedTillDate[0]);
            if (taxRemaining < 0) {
                taxRemaining = 0;
            }
            Double taxForThisMonth = taxRemaining / remaingMonths;
            taxThisMonth[0] = taxForThisMonth;

            Long taxActul = payBleTax;
            if (relief[0] > 0.0) {
                taxActul = 0l;
                taxThisMonth[0] = 0.0;
                taxRemaining = 0;

            }

            for (IncomeTax incomeUpdate : incomeTax) {
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Tax on Total Income")) {
                    incomeUpdate.setTax_amount(String.valueOf(Math.round(taxLiability[0])));
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Surcharge on Income")) {
                    incomeUpdate.setTax_amount(String.valueOf(surcharge));
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Education Cess")) {
                    incomeUpdate.setTax_amount(String.valueOf(Math.round(educationCess[0])));
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Tax Payable")) {
                    incomeUpdate.setTax_amount(payBleTax.toString());
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Relief u/s 89")) {
                    incomeUpdate.setTax_amount(String.valueOf(Math.round(relief[0])));
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Total Tax Liability")) {
                    incomeUpdate.setTax_amount(taxActul.toString());
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Remaining Tax/Remaining months")) {
                    incomeUpdate.setTax_amount(String.valueOf(Math.round(taxRemaining)));
                }
                if (incomeUpdate.getTax_name() != null && incomeUpdate.getTax_name().equalsIgnoreCase("Tax Deduction for this month")) {
                    incomeUpdate.setTax_amount(String.valueOf(Math.round(taxThisMonth[0])));
                }

            }

            incomeTaxRepo.saveAll(incomeTax);
            response.put("status", "success");
            response.put("Income Tax", Math.round(taxThisMonth[0]));

        } catch (Exception e) {
            response.put("status", "success");
            e.printStackTrace();
        }
        logger.info("calculateTaxWhileUpdatingAllowance output -> " + response.toString());
        return response;
    }

    @Override
    public Map updateTds(Long employeeId, int month, int year, Double taxForThisMonth) {
        Map response = new HashMap();
        try {
            logger.info("updateTds called -> employeeId " + employeeId + " month " + month + " year " + year + " taxForThisMonth " + taxForThisMonth);
            List<IncomeTax> incomeTax = incomeRepo.employeeIncomeTax(year, month, employeeId);
            String remainingTaxAmountOld = incomeTax.stream()
                    .filter(tax -> tax.getTax_name().equalsIgnoreCase("Remaining Tax/Remaining months"))
                    .map(IncomeTax::getTax_amount)
                    .findFirst()
                    .orElse(null); // You can change this to a default value if needed
            String monthlyTaxOld = incomeTax.stream()
                    .filter(tax -> tax.getTax_name().equalsIgnoreCase("Tax Deduction for this month"))
                    .map(IncomeTax::getTax_amount)
                    .findFirst()
                    .orElse(null); // You can change this to a default value if needed
            Double oldRemaingTax = remainingTaxAmountOld != null ? Double.parseDouble(remainingTaxAmountOld) : 0;
            Double oldMonthlyTax = monthlyTaxOld != null ? Double.parseDouble(monthlyTaxOld) : 0;
            oldRemaingTax = oldRemaingTax + (oldMonthlyTax - taxForThisMonth);
            String updatedRemaingTax = String.valueOf(Math.round(oldRemaingTax));
            String updatedTaxForThisMonth = String.valueOf(Math.round(taxForThisMonth));
            incomeTax.stream()
                    .filter(tax -> tax != null && tax.getTax_name() != null && tax.getTax_name().equalsIgnoreCase("Remaining Tax/Remaining months"))
                    .forEach(tax -> {
                        if (tax != null) {
                            // Replace the tax_amount with your desired value
                            tax.setTax_amount(updatedRemaingTax);
                        }
                    });
            incomeTax.stream()
                    .filter(tax -> tax != null && tax.getTax_name() != null && tax.getTax_name().equalsIgnoreCase("Tax Deduction for this month"))
                    .forEach(tax -> {
                        // Replace the tax_amount with your desired value
                        if (tax != null) {
                            // Replace the tax_amount with your desired value
                            tax.setTax_amount(updatedTaxForThisMonth);
                        }
                    });
            incomeTaxRepo.saveAll(incomeTax);
            response.put("status", "success");
// Now, the tax_amount for matching tax names has been updated in the incomeTax list
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Map calculateTaxWhileComparingTax(Map map) {
        Map response = new HashMap();
        try {
            Long employeeId = Long.parseLong(map.get("emp_id").toString());
            int startYear = Integer.parseInt(map.get("financial_year").toString());
            int endYear = startYear + 1;
            Map paidAllowances = this.alreadyPiadAllowances(employeeId, startYear, endYear);

            Double taxableIncome = 0.0;
            Double alreadyPaidSubtotal = 0.0;
            Double basic = 0.0;
            Double hra = 0.0;
            Double reimburs = 0.0;

            Map predictedAllowances = this.employeeCurrentAndFutureAllowance(employeeId, startYear, endYear);

            List<StandardDeduction> standard = standardDeduction.getStandardDeductionFinancialYear(startYear);

            Long oldTaxDeduction[] = new Long[1];
            Long newTaxDeduction[] = new Long[1];
            oldTaxDeduction[0] = 0L;
            newTaxDeduction[0] = 0L;

            standard.forEach(data -> {
                if (data.getTypeOfRegime() != null) {
                    if (data.getTypeOfRegime().equalsIgnoreCase("OldTaxSlabKey")) {
                        oldTaxDeduction[0] = data.getStandard_deduction();
                    } else {
                        newTaxDeduction[0] = data.getStandard_deduction();
                    }

                }

            });

            if (paidAllowances.get("status") != null && paidAllowances.get("status").toString().equalsIgnoreCase("success")) {
                alreadyPaidSubtotal = paidAllowances.get("subtotal") != null ? Double.parseDouble(paidAllowances.get("subtotal").toString()) : 0.0;
                basic = paidAllowances.get("basicSalary") != null ? Double.parseDouble(paidAllowances.get("basicSalary").toString()) : 0.0;
                hra = paidAllowances.get("hra") != null ? Double.parseDouble(paidAllowances.get("hra").toString()) : 0.0;
                reimburs = paidAllowances.get("Reimbursement") != null ? Double.parseDouble(paidAllowances.get("Reimbursement").toString()) : 0.0;
            }
            if (predictedAllowances.get("status") != null && predictedAllowances.get("status").toString().equalsIgnoreCase("success")) {
                Double subtotal = predictedAllowances.get("subtotal") != null ? Double.parseDouble(predictedAllowances.get("subtotal").toString()) : 0.0;
                Double predictedBasic = predictedAllowances.get("basicSalary") != null ? Double.parseDouble(predictedAllowances.get("basicSalary").toString()) : 0.0;
                Double predictedHra = predictedAllowances.get("hra") != null ? Double.parseDouble(predictedAllowances.get("hra").toString()) : 0.0;
                basic = basic + predictedBasic;
                hra = hra + predictedHra;
                alreadyPaidSubtotal = alreadyPaidSubtotal + subtotal;
            }
            map.put("basic", basic);
            map.put("hra", hra);

            if (map.get("TaxSlabType") != null && map.get("TaxSlabType").toString().equalsIgnoreCase("OldTaxSlabKey")) {
                Map totalInvestement = this.employeeTotalInvestemt(map);
                Double totalInvestment = 0.0;
                Double previousIncome = 0.0;
                if (totalInvestement.get("status") != null && totalInvestement.get("status").toString().equalsIgnoreCase("success")) {
                    totalInvestment = totalInvestement.get("totalInvestment") != null ? Double.parseDouble(totalInvestement.get("totalInvestment").toString()) : 0.0;
                    previousIncome = totalInvestement.get("incomeFromPreviousEmployer") != null ? Double.parseDouble(totalInvestement.get("incomeFromPreviousEmployer").toString()) : 0.0;

                }
                taxableIncome = (alreadyPaidSubtotal - totalInvestment - reimburs - oldTaxDeduction[0]) + previousIncome;

            } else {

                taxableIncome = (alreadyPaidSubtotal - reimburs - newTaxDeduction[0]);

            }

            response = this.calucluteCommonTax(taxableIncome, map.get("TaxSlabType").toString(), startYear);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Map alreadyPiadAllowances(Long employeeId, int startYer, int endYear) {
        Map response = new HashMap();
        try {
            List<LinkedCaseInsensitiveMap> paidAllowances = employeeAllowance.alreadyPaidEmployeeAllowances(employeeId, startYer, endYear);
            List<LinkedCaseInsensitiveMap> otherAllowances = employeeAllowance.alreadyPaidOtherployeeAllowance(employeeId, startYer, endYear);
            paidAllowances.addAll(otherAllowances);
            double subtotal = paidAllowances.stream()
                    .mapToDouble(employee -> Double.parseDouble(employee.get("allowance_payable_amount").toString()))
                    .sum();
            response.put("status", "success");
            Double basicSalary = paidAllowances.stream()
                    .filter(allowance -> "Basic Salary".equalsIgnoreCase(allowance.get("allowance_name").toString()))
                    .mapToDouble(allowance -> Double.parseDouble(allowance.get("allowance_payable_amount").toString()))
                    .sum();
            Double hra = paidAllowances.stream()
                    .filter(allowance -> "HRA".equalsIgnoreCase(allowance.get("allowance_name").toString()))
                    .mapToDouble(allowance -> Double.parseDouble(allowance.get("allowance_payable_amount").toString()))
                    .sum();
            double reimburs = 0.0;
            reimburs = paidAllowances.stream()
                    .filter(allowance -> "Reimbursement".equalsIgnoreCase(allowance.get("allowance_name").toString()))
                    .mapToDouble(allowance -> Double.parseDouble(allowance.get("allowance_payable_amount").toString()))
                    .sum();
            response.put("basicSalary", basicSalary);
            response.put("hra", hra);
            response.put("Reimbursement", reimburs);
            response.put("subtotal", subtotal);
        } catch (Exception e) {
            response.put("status", "exception");
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Map employeeCurrentAndFutureAllowance(Long employeeId, int startYear, int endYear) {
        Map response = new HashMap();
        try {

            Double leftdaysSubtotal = 0.0;
            Double leftdayhra = 0.0;
            Double leftdayBasic = 0.0;
            List<LinkedCaseInsensitiveMap> employeeStandard = employeeAllowance.employeeApplicableStandard(employeeId);
            List<LinkedCaseInsensitiveMap> employeeOtherStandard = employeeAllowance.employeeApplicableStandardOther(employeeId);
            LinkedCaseInsensitiveMap maxPayRun = runPayRepo.maxRunpayRollMonth(employeeId, startYear, endYear);
            LinkedCaseInsensitiveMap payrollSetting = payRollRepo.getSalaryDatesCycle(Long.parseLong(employeeStandard.get(0).get("organization_id").toString()));
            employeeStandard.addAll(employeeOtherStandard);
            double subtotal = employeeStandard.stream()
                    .mapToDouble(employee -> Double.parseDouble(employee.get("allowance_payable_amount").toString()))
                    .sum();
            // Add the subtotal to the first item in the list

            Double basicSalary = employeeStandard.stream()
                    .filter(allowance -> "Basic Salary".equalsIgnoreCase(allowance.get("allowance_name").toString()))
                    .mapToDouble(allowance -> Double.parseDouble(allowance.get("allowance_payable_amount").toString()))
                    .findFirst()
                    .orElse(0.0); // Default value if not found
            Double hra = employeeStandard.stream()
                    .filter(allowance -> "HRA".equalsIgnoreCase(allowance.get("allowance_name").toString()))
                    .mapToDouble(allowance -> Double.parseDouble(allowance.get("allowance_payable_amount").toString()))
                    .findFirst()
                    .orElse(0.0);
            LinkedCaseInsensitiveMap joining = employeeRepo.employeeJoiningDate(employeeId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDate = startYear + "-04-01";
            LocalDate startDateLocal = LocalDate.parse(startDate, formatter);

            LocalDate joingDate = LocalDate.parse(joining.get("joining_date").toString(), formatter);

            int monthLeft = 0;
            if (maxPayRun != null) {
                monthLeft = maxPayRun.get("maxpayrun") != null ? Integer.parseInt(maxPayRun.get("maxpayrun").toString()) : 0;

            }

            int projectedMonth = 0;
            if (maxPayRun != null) {
                projectedMonth = 15 - monthLeft;
                if (monthLeft == 1 || monthLeft == 2 || monthLeft == 3) {
                    projectedMonth = 3 - monthLeft;
                }
            }

            if (maxPayRun == null || monthLeft == 0) {
                String endDate = endYear + "-03-" + payrollSetting.get("end_date");
                LocalDate endDateLocal = LocalDate.parse(endDate, formatter);
                int leftDays = 0;
                if (joingDate.isBefore(startDateLocal)) {
                    projectedMonth = 12;
                } else {
                    int monthOfJoining = joingDate.getMonthValue();
                    if (joingDate.getDayOfMonth() == 1 && joingDate.getDayOfMonth() < endDateLocal.getDayOfMonth()) {
                        projectedMonth = 16 - monthOfJoining;
                        if (monthOfJoining == 1 || monthOfJoining == 2 || monthOfJoining == 3) {
                            projectedMonth = 4 - monthOfJoining;
                        }
                    } else {
                        projectedMonth = 15 - monthOfJoining;
                        if (joingDate.getDayOfMonth() > endDateLocal.getDayOfMonth()) {
                            int monthNew = joingDate.getMonthValue();
                            if (monthNew == 12) {
                                monthNew = 1;
                            } else {
                                monthNew = joingDate.getMonthValue() + 1;
                            }

                            String joinNew = joingDate.getYear() + "-0" + monthNew + "-" + joingDate.getDayOfMonth();
                            if (monthNew == 10 || monthNew == 11 || monthNew == 12) {
                                joinNew = joingDate.getYear() + "-" + monthNew + "-" + joingDate.getDayOfMonth();
                            } else if (monthNew == 1) {
                                int startYearn = joingDate.getYear() + 1;
                                joinNew = startYearn + "-0" + monthNew + "-" + joingDate.getDayOfMonth();
                            }
                            LocalDate joinngAccodingToPayCycle = LocalDate.parse(joinNew, formatter);
                            projectedMonth = 15 - joinngAccodingToPayCycle.getMonthValue();
                            if (joinngAccodingToPayCycle.getMonthValue() == 1 || joinngAccodingToPayCycle.getMonthValue() == 2 || joinngAccodingToPayCycle.getMonthValue() == 3) {
                                projectedMonth = 3 - joinngAccodingToPayCycle.getMonthValue();
                            }
                        }

                        String newJoingDate = null;
                        if (monthOfJoining == 1 || monthOfJoining == 2 || monthOfJoining == 3) {
                            if (joingDate.getDayOfMonth() > endDateLocal.getDayOfMonth()) {
                                projectedMonth = 2 - monthOfJoining;
                                projectedMonth = projectedMonth < 0 ? 0 : projectedMonth;
                            } else {
                                projectedMonth = 3 - monthOfJoining;
                            }

                        }
                        
                        if (Integer.parseInt(payrollSetting.get("start_date").toString()) == 1) {
                            leftDays = joingDate.lengthOfMonth() - joingDate.getDayOfMonth() + 1;
                            leftdaysSubtotal = (subtotal / joingDate.lengthOfMonth()) * leftDays;
                            leftdayhra = (hra / joingDate.lengthOfMonth()) * leftDays;
                            leftdayBasic = (basicSalary / joingDate.lengthOfMonth()) * leftDays;
                        } else {
                            String dateTest = startYear + "-01" + "-" + payrollSetting.get("end_date").toString();
                            LocalDate dateLocal = LocalDate.parse(dateTest, formatter);
                            if (joingDate.getDayOfMonth() > endDateLocal.getDayOfMonth()) {
                                if (joingDate.getMonthValue() == 12 || joingDate.getMonthValue() == 1 || joingDate.getMonthValue() == 2 || joingDate.getMonthValue() == 3) {
                                    int monthValue = joingDate.getMonthValue() + 1;
                                    if (joingDate.getMonthValue() == 12) {
                                        monthValue = 1;
                                    }
                                    dateTest = endYear + "-0" + monthValue + "-" + payrollSetting.get("end_date").toString();
                                    dateLocal = LocalDate.parse(dateTest, formatter);
                                    leftDays = (int) (DAYS.between(joingDate, dateLocal) + 1);
                                } else {
                                    int monthValue = joingDate.getMonthValue() + 1;
                                    dateTest = startYear + "-0" + monthValue + "-" + payrollSetting.get("end_date").toString();
                                    if (monthValue == 10 || monthValue == 11 || monthValue == 12) {
                                        dateTest = startYear + "-" + monthValue + "-" + payrollSetting.get("end_date").toString();
                                    }
                                    dateLocal = LocalDate.parse(dateTest, formatter);
                                    leftDays = (int) (DAYS.between(joingDate, dateLocal) + 1);
                                }
                                leftDays = leftDays < 0 ? 0 : leftDays;
                                leftdaysSubtotal = (subtotal / joingDate.lengthOfMonth()) * leftDays;
                                leftdayhra = (hra / joingDate.lengthOfMonth()) * leftDays;
                                leftdayBasic = (basicSalary / joingDate.lengthOfMonth()) * leftDays;

                            } else {
                                dateTest = startYear + "-0" + joingDate.getMonthValue() + "-" + payrollSetting.get("end_date").toString();
                                if (joingDate.getMonthValue() == 10 || joingDate.getMonthValue() == 11 || joingDate.getMonthValue() == 12) {
                                    dateTest = startYear + "-" + joingDate.getMonthValue() + "-" + payrollSetting.get("end_date").toString();
                                }
                                if (joingDate.getMonthValue() == 1 || joingDate.getMonthValue() == 2 || joingDate.getMonthValue() == 3) {
                                    dateTest = endYear + "-0" + joingDate.getMonthValue() + "-" + payrollSetting.get("end_date").toString();

                                }
                                dateLocal = LocalDate.parse(dateTest, formatter);
                                leftDays = (int) (DAYS.between(dateLocal, joingDate));
                                if (leftDays < 0) {
                                    leftDays = leftDays * -1;
                                }
                                leftDays = leftDays + 1;
                                leftdaysSubtotal = (subtotal / joingDate.minusMonths(1).lengthOfMonth()) * leftDays;
                                leftdayhra = (hra / joingDate.minusMonths(1).lengthOfMonth()) * leftDays;
                                leftdayBasic = (basicSalary / joingDate.minusMonths(1).lengthOfMonth()) * leftDays;
                            }

                        }

                    }
                }

            }

            logger.info("projected month-> " + projectedMonth + " leftdaysSubtotal" + leftdaysSubtotal);
            Double ActualSuTotal = subtotal * projectedMonth + leftdaysSubtotal;
            Double actualHra = (hra * projectedMonth) + leftdayhra;
            Double actualbasic = (basicSalary * projectedMonth) + leftdayBasic;
            response.put("subtotal", Math.round(ActualSuTotal));
            // response.put("projected", employeeStandard);
            response.put("basicSalary", Math.round(actualbasic));
            response.put("hra", Math.round(actualHra));
            response.put("status", "success");

        } catch (Exception e) {
            response.put("status", "exception");
            e.printStackTrace();
        }
        return response;

    }

    @Override
    public Map employeeTotalInvestemt(Map map) {
        Map response = new HashMap();
        try {
            Double totalRent = (map.get("totalRent") != null
                    && !map.get("totalRent").toString().isEmpty()) ? Double.parseDouble(map.get("totalRent").toString()) : 0.0;
            Double basic10 = totalRent - ((Double.parseDouble(map.get("basic").toString()) / 100) * 10);
            basic10 = basic10 < 0.0 ? 0 : basic10;
            Double percentOfBasic = 0.0;
            if (map.get("metroandNonMetroKey") != null && map.get("metroandNonMetroKey").toString().equalsIgnoreCase("metro")) {
                percentOfBasic = (Double.parseDouble(map.get("basic").toString()) / 100) * 50;
            } else if (map.get("metroandNonMetroKey") != null && map.get("metroandNonMetroKey").toString().equalsIgnoreCase("non-metro")) {
                // Code to execute when "status" is "non-metro"
                percentOfBasic = (Double.parseDouble(map.get("basic").toString()) / 100) * 40;
            }
            Double hra = map.get("hra") != null ? Double.parseDouble(map.get("hra").toString()) : 0.0;
            Double leastValue = hra;
            if (basic10 < leastValue) {
                leastValue = basic10;
            }

            if (percentOfBasic < leastValue) {
                leastValue = percentOfBasic;
            }
            leastValue = totalRent == 0 ? 0 : leastValue;
            Double section80c = (map.get("exemptions_sec_10_Total") != null
                    && !map.get("exemptions_sec_10_Total").toString().isEmpty()) ? Double.parseDouble(map.get("exemptions_sec_10_Total").toString()) : 0.0;
            section80c = section80c <= 150000 ? section80c : 150000;
            Double homeLoanTotal = (map.get("homeLoanTotal") != null
                    && !map.get("homeLoanTotal").toString().isEmpty()) ? Double.parseDouble(map.get("homeLoanTotal").toString()) : 0.0;
            homeLoanTotal = homeLoanTotal <= 200000 ? homeLoanTotal : 200000;
            Double otherSec = (map.get("otherSec") != null
                    && !map.get("otherSec").toString().isEmpty()) ? Double.parseDouble(map.get("otherSec").toString()) : 0.0;

            Double incomeFromPreviousEmployer = (map.get("incomeFromPreviousEmployer") != null
                    && !map.get("incomeFromPreviousEmployer").toString().isEmpty())
                    ? Double.parseDouble(map.get("incomeFromPreviousEmployer").toString()) : 0.0;
            response.put("totalInvestment", section80c + homeLoanTotal + otherSec + leastValue);
            response.put("incomeFromPreviousEmployer", incomeFromPreviousEmployer);
            response.put("leastOfAboveExcepted", leastValue);
            response.put("status", "success");

        } catch (Exception e) {
            response.put("status", "exception");
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Map calucluteCommonTax(Double taxableIncome, String taxslabType, int startYear) {

        Map response = new HashMap();
        Double taxLiability = 0.0;
        Double educationCess = 0.0;
        Double rebate = 0.0;
        Long surcharge = 0L;
        try {
            LinkedCaseInsensitiveMap cess = cessRepo.getRateOfCess();
            List<LinkedCaseInsensitiveMap> surCharge = incomeTaxRepo.surCharge();
            if (taxslabType.equalsIgnoreCase("OldTaxSlabKey")) {
                List<LinkedCaseInsensitiveMap> oldTaxSlab = taxSlabRepo.oldTaxSlab();
                LinkedCaseInsensitiveMap reliefList = incomeTaxRepo.relief();

                for (LinkedCaseInsensitiveMap slab : oldTaxSlab) {
                    Double slabStart = Double.parseDouble(slab.get("start").toString());
                    Double slabEnd = Double.parseDouble(slab.get("end").toString());
                    Double slabRate = Double.parseDouble(slab.get("rate").toString());

                    if (taxableIncome > slabStart) {
                        double currentSlabTaxableIncome = Math.min(taxableIncome, slabEnd) - slabStart;
                        taxLiability += currentSlabTaxableIncome * (slabRate / 100);

                    } else {
                        break; // No need to continue calculating if income doesn't fall into the slab
                    }
                }
                Double reliefAmount = Double.parseDouble(reliefList.get("income").toString());
                if (taxableIncome > reliefAmount) {
                    Double cessRate = cess.get("rate") != null ? Double.parseDouble(cess.get("rate").toString()) : 0.0;
                    educationCess = (taxLiability / 100) * cessRate;

                } else {
                    if (taxableIncome < reliefAmount) {
                        rebate = taxableIncome;
                    } else {
                        rebate = reliefAmount;
                    }

                }
            } else {
                System.out.println("taxableIncome 2798"+" "+taxableIncome.toString());
                List<LinkedCaseInsensitiveMap> newTaxSlab = newTaxRegimeSlabRepo.newTaxSlab(startYear);
                Relief87ANewRegime relief87ANewRegime = relief87ANewRegimeRepo.relief87ANewReime(startYear);
                for (LinkedCaseInsensitiveMap slab : newTaxSlab) {
                    Double slabStart = Double.parseDouble(slab.get("start").toString());
                    Double slabEnd = Double.parseDouble(slab.get("end").toString());
                    Double slabRate = Double.parseDouble(slab.get("rate").toString());

                    if (taxableIncome > slabStart) {
                        double currentSlabTaxableIncome = Math.min(taxableIncome, slabEnd) - slabStart;
                        taxLiability += currentSlabTaxableIncome * (slabRate / 100);
                    } else {
                        break; // No need to continue calculating if income doesn't fall into the slab
                    }
                }
                
                System.out.println("taxableIncome 2813"+" "+taxableIncome.toString());

                if (taxableIncome > relief87ANewRegime.getIncome()) {

                    Double cessRate = cess.get("rate") != null ? Double.parseDouble(cess.get("rate").toString()) : 0.0;
                    educationCess = (taxLiability / 100) * cessRate;
                } else {

                    if (taxableIncome < relief87ANewRegime.getIncome()) {
                        rebate = taxableIncome;
                    } else {
                        rebate = relief87ANewRegime.getIncome();
                    }
                }
            }
            for (LinkedCaseInsensitiveMap charge : surCharge) {

                Double startIncome = Double.parseDouble(charge.get("start").toString());
                Double endIncome = Double.parseDouble(charge.get("end").toString());
                if (taxableIncome >= startIncome && taxableIncome <= endIncome) {
                    Double rate = Double.parseDouble(charge.get("rate").toString());
                    surcharge = Math.round((taxLiability / 100) * rate);

                }

            }
            response.put("status", "success");
            response.put("taxLiability", Math.round(taxLiability));
            response.put("educationCess", Math.round(educationCess));
            response.put("surcharge", surcharge);
            response.put("rebate", Math.round(rebate));

        } catch (Exception e) {
            response.put("status", "exception");
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public Map getClosingAllowances(List<LinkedCaseInsensitiveMap> organizationStandard, int month, int year) {
        Map response = new HashMap();
        try {
            logger.info("getClosingAllowances called" + " " + organizationStandard.toString() + " " + month + " " + year);
            //Closing Allowance
            Optional<Long> closingAllowanceIdOptional = organizationStandard.stream()
                    .filter(data -> data.get("allowance_name") != null
                    && (data.get("allowance_name").toString().equalsIgnoreCase("Overtime Closing Allowance")
                    || data.get("allowance_name").toString().equalsIgnoreCase("Overtime Closing Allowances")))
                    .map(data -> data.get("allowance_id"))
                    .map(Object::toString) // Convert to String to avoid potential ClassCastException
                    .map(Long::valueOf) // Convert to Long
                    .findFirst();
            Optional<String> closingAllowanceNameOptional = organizationStandard.stream()
                    .filter(data -> data.get("allowance_name") != null
                    && (data.get("allowance_name").toString().equalsIgnoreCase("Overtime Closing Allowance")
                    || data.get("allowance_name").toString().equalsIgnoreCase("Overtime Closing Allowances")))
                    .map(data -> data.get("allowance_name"))
                    .map(Object::toString) // Convert to String to avoid potential ClassCastException
                    .findFirst();

            Double closingAllowancePercentage[] = new Double[1];
            Double excludeAmount[] = new Double[1];
            Double includeAmount[] = new Double[1];
            includeAmount[0] = 0.0;
            excludeAmount[0] = 0.0;
            closingAllowancePercentage[0] = 0.0;
            Long closingAllowannceId =  0L;
            String closingAllowanceCreatedName = closingAllowanceNameOptional.isPresent() ? closingAllowanceNameOptional.get() : null;
            List<LinkedCaseInsensitiveMap> closingAllowanceData = allowanceRepo.closingAllowanceData(closingAllowannceId);
            Set<Long> allowancesIds = new HashSet<>();
            closingAllowanceData.stream().forEach(data -> {
                if (month == Integer.parseInt(data.get("month").toString())) {
                    allowancesIds.add(Long.parseLong(data.get("allowance_id").toString()));
                    excludeAmount[0] = data.get("exclude_amount") != null ? Double.parseDouble(data.get("exclude_amount").toString()) : 0;
                    includeAmount[0] = data.get("include_amount") != null ? Double.parseDouble(data.get("include_amount").toString()) : 0;
                    closingAllowancePercentage[0] = Double.parseDouble(data.get("percentage").toString());
                }
            });
            int paymentMonth = month - 1;
            int paymentYear = year;
            if (month == 1) {
                paymentMonth = 12;
                paymentYear = year - 1;
            }

            List<LinkedCaseInsensitiveMap> allowancesSum = new ArrayList<>();
            if (!allowancesIds.isEmpty()) {
                allowancesSum = employeeAllowance.sumOfAllowanceGroupByEmployeeId(allowancesIds, paymentMonth, paymentYear);
            }
            
            allowancesSum.stream().forEach(sum -> {
                Double closingAllowances = ((Double.parseDouble(sum.get("allowance_amount").toString()) - excludeAmount[0]) * closingAllowancePercentage[0] / 100) + includeAmount[0];
                //closingAllowances = Math.round(closingAllowances * 100.0) / 100.0;
                sum.put(closingAllowanceCreatedName, Math.round(closingAllowances));
            });
            response.put("status", "success");
            response.put("overtimeClosingAllowance", allowancesSum);

        } catch (Exception e) {
            logger.error("error in getClosingAllowances" + " " + e.getMessage());
            e.printStackTrace();
        }
        logger.info("response from getClosingAllowances-> " + " " + response.toString());
        return response;
    }

}

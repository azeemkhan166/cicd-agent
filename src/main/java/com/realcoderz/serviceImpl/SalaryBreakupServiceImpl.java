package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.realcoderz.common.NumberToWords;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.Allowance;
import com.realcoderz.model.Deduction;
import com.realcoderz.model.OrganizationSetUp;
import com.realcoderz.repository.NewEmployeeRepository;
import com.realcoderz.service.SalaryBreakupService;
import com.realcoderz.repository.SalaryBreakuprepo;
import java.util.HashMap;
import java.util.Map;
import com.realcoderz.model.TravelAllowance;
import com.realcoderz.repository.TravelAllowanceRepository;
import com.realcoderz.model.SalaryBreakUp;
import com.realcoderz.repository.AllowanceRepository;
import com.realcoderz.repository.DeductionAllowanceMappingRepository;
import com.realcoderz.repository.DeductionRepository;
import com.realcoderz.repository.EmployeeAllowanceRepository;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.repository.EmployeeLoanRepository;
import com.realcoderz.repository.OrganizationIncentiveRepository;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.repository.OtherAllowancesRepository;
import com.realcoderz.repository.OtherDeductionRepository;
import com.realcoderz.repository.PayrollSettingRepository;
import com.realcoderz.repository.ProfessionalTaxSlabRepo;
import com.realcoderz.repository.SalaryHistoryRecordRepository;
import com.realcoderz.util.DateUtils;
import com.realcoderz.util.EncryptDecryptUtils;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.realcoderz.repository.RunPayRollRepository;
import com.realcoderz.repository.WorkerLoanRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;

/**
 *
 * @author Lalit Raghav edited By Astha
 */
@Service
public class SalaryBreakupServiceImpl implements SalaryBreakupService {

    static final Logger LOGGER = LoggerFactory.getLogger(SalaryBreakupServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper();

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    private JWTAuthenticationFilter authenticationFilter;

    @Value("${reimburshment_url}")
    private String reimburshment_url;
    
    @Value("${orgIdForSymbol}")
    private String orgIdForSymbol;

//     @Value("${stagging}")
//    private String stagging;
    @Value("${bucketName}")
    String bucketName;

    @Value("${akron_organization_id}")
    Long akron_organization_id;

    @Value("${gcp.config.file}")
    private String gcpConfigFile;

    @Value("${assessment_url}")
    private String assessment_url;

    Storage storage = StorageOptions.getDefaultInstance().getService();

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AllowanceRepository allowanceRepo;

    @Autowired
    private DeductionRepository deductionRepo;

    @Autowired
    private SalaryBreakuprepo salalrybreakuprepo;

    @Autowired
    private OrganizationIncentiveRepository organizationIncentiveRepo;

    @Autowired
    private EmployeeAllowanceRepository employeeAllowanceRepo;

    @Autowired
    private TravelAllowanceRepository travelAllowanceRepository;

    @Autowired
    private EmployeeDeductionRepository employeeDeductionRepo;

    @Autowired
    private OtherAllowancesRepository employeeOtherAllowanceRepo;

    @Autowired
    private OrganizationSetUpRepository orgRepo;

    @Autowired
    private OtherDeductionRepository otherDeductionRepo;

    @Autowired
    private NewEmployeeRepository empRepo;

    @Autowired
    private EmployeeLoanRepository employeeLoanRepo;

    @Autowired
    private RunPayServiceImpl runPayServiceImpl;

    @Autowired
    private PayrollSettingRepository payrollSettingRepo;

    @Autowired
    private OrganizationSetUpRepository organizationSetupRepo;

    @Autowired
    private SalaryHistoryRecordRepository salaryHistoryRecord;

    @Autowired
    private ProfessionalTaxSlabRepo professionalTaxSlabRepo;

    @Autowired
    private RunPayRollRepository runPayRollRepository;

    @Autowired
    private WorkerLoanRepository workerLoanRepo;

    @Autowired
    private DeductionAllowanceMappingRepository deductionAllowanceMappingRepo;
    
    @Autowired
    private employeeDetailsRepository employeeDetailsRepo;

    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
            SalaryBreakUp salarybreakup = mapper.convertValue(map, SalaryBreakUp.class);
            if (salarybreakup != null) {
                salalrybreakuprepo.save(salarybreakup);
                resultMap.clear();
                resultMap.put("status", "success");

            } else {
                resultMap.clear();
                resultMap.put("status", "error");

            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> save() :: ", ex);
        }

        return resultMap;
    }

    //--------------------update ---------------------------
    @Override
    public Map findSalaryDetailsById(Long id) {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap salarybreakup = salalrybreakuprepo.findBySalaryBreakUpId(id);
            if (salarybreakup != null && !salarybreakup.isEmpty()) {
                resultMap.put("List", salarybreakup);
//            resultMap.put("exist", true);
                resultMap.put("status", "success");

            } else {

                resultMap.put("status", "error");

            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> findSalaryDetailsById() :: ", ex);
        }

        return resultMap;
    }

//    SalaryBreakup Calculation
    public String checkSalaryBreakUp(String month, String year, String empId, String employee_Type) {
        SalaryBreakUp salaryBreakUp = salalrybreakuprepo.findByMonthAndYearAndEmpId(Integer.parseInt(month) + 1, Integer.parseInt(year), Integer.parseInt(empId), employee_Type);
        if (salaryBreakUp != null) {
            return "true";
        } else {
            return "false";
        }
    }

    //    SalaryBreakup Saved or not
    public Map isSalaryBreakUpSaved(String month, String year, String empId, String orgId, String email, String employee_Type, Double Gross) {
        Map resultMap = new HashMap<>();
        List<Integer> all_allowance_id = new ArrayList<>();
        List<String> all_allowance_name = new ArrayList<>();
        List<Double> all_allowance_amount = new ArrayList<>();
        List<Double> all_allowance_payable_amount = new ArrayList<>();
        Map<String, List<Integer>> map_allowance_id = new HashMap<>();
        Map<String, List<String>> map_allowance_name = new HashMap<>();
        Map<String, List<Double>> map_allowance_amount = new HashMap<>();
        Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
        List<Integer> all_deduction_id = new ArrayList<>();
        List<Double> all_ytd_deduction = new ArrayList<>();
        List<String> all_deduction_name = new ArrayList<>();
        List<Double> all_deduction_amount = new ArrayList<>();
        List<Double> all_deduction_payable_amount = new ArrayList<>();
        Map<String, List<Integer>> map_deduction_id = new HashMap<>();
        Map<String, List<String>> map_deduction_name = new HashMap<>();
        Map<String, List<Double>> map_deduction_amount = new HashMap<>();
        Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();
        Map<String, List<Double>> map_ytd_deduction_amount = new HashMap<>();
        System.out.println("month 258"+" "+month);
        if (employee_Type.equalsIgnoreCase("Worker")) {
            employee_Type = "Worker";
        } else if (!employee_Type.equalsIgnoreCase("Consultant") && !employee_Type.equalsIgnoreCase("Intern")) {
            employee_Type = "Full time";
        }

        List<LinkedCaseInsensitiveMap> salaryBreakup;
        List<LinkedCaseInsensitiveMap> employeeAllowances = null;
        List<LinkedCaseInsensitiveMap> employeeDeductions = null;
        List<LinkedCaseInsensitiveMap> employeeOtherAllowance = null;

        System.out.println("isSalaryBreakUpSavedAlready method execution starts with month " + month + " and year " + year + " and employee id " + empId + " and organization id " + orgId + " and gross " + Gross + " and employee type " + employee_Type);
        LOGGER.info("isSalaryBreakUpSavedAlready method execution starts with month " + month + " and year " + year + " and employee id " + empId + " and organization id " + orgId + " and gross " + Gross + " and employee type " + employee_Type);
        
        
         Integer monthPass=Integer.parseInt(month);
       if(Integer.parseInt(month)!=4){
            
         monthPass=Integer.parseInt(month)-1;
        }
       
        salaryBreakup = salalrybreakuprepo.getSavedSalaryBreakupbasedonMonth(Long.parseLong(empId), Long.parseLong(orgId),monthPass , Integer.parseInt(year), employee_Type);
        System.out.println("salaryBreakup 281"+" "+salaryBreakup.toString());
        if (!salaryBreakup.isEmpty()) {

            LOGGER.info("Salary Breakup Data is " + salaryBreakup);
            employeeAllowances = employeeAllowanceRepo.getSavedEmployeeAllowancesByMonth(Long.parseLong(empId), Long.parseLong(orgId), monthPass, Integer.parseInt(year));
            LOGGER.info("Employee Allowance Data is " + employeeAllowances);

            employeeDeductions = employeeDeductionRepo.getSavedEmployeeDeductionsByMonth(Long.parseLong(empId), Long.parseLong(orgId), monthPass, Integer.parseInt(year));
            LOGGER.info("Employee Deduction Data is " + employeeDeductions);

            employeeOtherAllowance = employeeOtherAllowanceRepo.getSavedEmployeeOtherAllowancesByMonth(Long.parseLong(empId), Long.parseLong(orgId), monthPass, Integer.parseInt(year));
            LOGGER.info("Employee Other Allowances Data is " + employeeOtherAllowance);
            
            resultMap.put("isSalaryBreakupSaved", true);

        }

        if (salaryBreakup.isEmpty()) {
            salaryBreakup = salalrybreakuprepo.getSavedEmployeeSalaryBreakup(Long.parseLong(empId), Long.parseLong(orgId), employee_Type, Gross);

            resultMap.put("isSalaryBreakupSaved", false);
            
            if (!salaryBreakup.isEmpty()) {

                LOGGER.info("Salary Breakup Data is " + salaryBreakup);
                employeeAllowances = employeeAllowanceRepo.getSavedEmployeeAllowances(Long.parseLong(salaryBreakup.get(0).get("sid").toString()));
                LOGGER.info("Employee Allowance Data is " + employeeAllowances);

                employeeDeductions = employeeDeductionRepo.getSavedEmployeeDeductions(Long.parseLong(salaryBreakup.get(0).get("sid").toString()));
                LOGGER.info("Employee Deduction Data is " + employeeDeductions);

                employeeOtherAllowance = employeeOtherAllowanceRepo.getSavedEmployeeOtherAllowances(Long.parseLong(salaryBreakup.get(0).get("sid").toString()));
                LOGGER.info("Employee Other Allowances Data is " + employeeOtherAllowance);
            }
        }

        if (!salaryBreakup.isEmpty()) {

//        Check Employee Allowance is Empty or not
            if (!employeeAllowances.isEmpty()) {
                for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {
                    all_allowance_id.add(Integer.parseInt(allowance.get("allowance_id").toString()));
                    all_allowance_name.add(allowance.get("allowance_name").toString());
                    all_allowance_amount.add((Double) allowance.get("allowance_amount"));
                    all_allowance_payable_amount.add((Double) allowance.get("allowance_payable_amount"));
                    if (allowance.get("allowance_name").toString().equalsIgnoreCase("Reimburs/Arrears")) {
                        resultMap.put("reimburs", allowance.get("allowance_payable_amount"));
                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Referral Allowance")) {
                        resultMap.put("referral", allowance.get("allowance_payable_amount"));
                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Bonus/Incentive")) {
                        resultMap.put("bonus", allowance.get("allowance_payable_amount"));
                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Overtime Allowance") || allowance.get("allowance_name").toString().equalsIgnoreCase("Overtime")) {
                        resultMap.put("overtime", allowance.get("allowance_payable_amount"));
                    }
                }
                map_allowance_id.put("allowanceId", all_allowance_id);
                map_allowance_name.put("allowanceName", all_allowance_name);
                map_allowance_amount.put("allowanceAmount", all_allowance_amount);
                map_allowance_payable_amount.put("allowancePayableAmount", all_allowance_payable_amount);
                resultMap.put("AllowanceId", map_allowance_id);
                resultMap.put("AllowanceName", map_allowance_name);
                resultMap.put("AllowanceAmount", map_allowance_amount);
                resultMap.put("AllowancePayableAmount", map_allowance_payable_amount);

            }
//        Check Employee Deduction is Empty or not
            if (!employeeDeductions.isEmpty()) {
                for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {
                    all_deduction_id.add(Integer.parseInt(deduction.get("deduction_id").toString()));
                    all_deduction_name.add(deduction.get("deduction_name").toString());
                    all_deduction_amount.add((Double) deduction.get("deduction_amount"));
                    all_deduction_payable_amount.add((Double) deduction.get("deduction_payable_amount"));
                    all_ytd_deduction.add((Double) deduction.get("ytd_deduction"));
                    if (deduction.get("deduction_name").toString().equalsIgnoreCase("epf")) {
                        resultMap.put("epf", deduction.get("deduction_payable_amount"));
                    } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("esic")) {
                        resultMap.put("esic", deduction.get("deduction_payable_amount"));
                    } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Advance")) {
                        resultMap.put("advance", deduction.get("deduction_payable_amount"));
                    }else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Income Tax")) {
                        resultMap.put("tds", deduction.get("deduction_payable_amount"));
                    } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Professional Tax")) {
                        resultMap.put("professional_tax", deduction.get("deduction_payable_amount"));
                    } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Other Deductions")) {
                        resultMap.put("other_deduction", deduction.get("deduction_payable_amount"));
                    }
                }
                map_deduction_id.put("deductionId", all_deduction_id);
                map_deduction_name.put("deductionName", all_deduction_name);
                map_deduction_amount.put("deductionAmount", all_deduction_amount);
                map_deduction_payable_amount.put("deductionPayableAmount", all_deduction_payable_amount);
                map_ytd_deduction_amount.put("ytdAmount", all_ytd_deduction);
                resultMap.put("DeductionId", map_deduction_id);
                resultMap.put("DeductionName", map_deduction_name);
                resultMap.put("DeductionAmount", map_deduction_amount);
                resultMap.put("DeductionPayableAmount", map_deduction_payable_amount);
                resultMap.put("YtdDeduction", map_ytd_deduction_amount);
            }
//        Check Employee Other Allowance is Empty or not
            if (!employeeOtherAllowance.isEmpty()) {
                for (LinkedCaseInsensitiveMap otherAllowances : employeeOtherAllowance) {
                    resultMap.put("OtherAllowances", otherAllowances.get("amount"));
                    resultMap.put("OtherPayableAllowances", otherAllowances.get("payable_amount"));
                }
            }
//        Check salarybreakup is empty or not
             System.out.println("salaryBreakup 381"+" "+salaryBreakup.toString());
            if (!salaryBreakup.isEmpty()) {
                for (LinkedCaseInsensitiveMap salary : salaryBreakup) {
                    resultMap.put("salary_break_up_id", salary.get("sid"));
                    resultMap.put("wages", salary.get("gross_salary"));
                    resultMap.put("rate", salary.get("rate"));
                    resultMap.put("working_day", salary.get("working_day"));
                    resultMap.put("percentage_change", salary.get("percentage_change"));
                    resultMap.put("NetPayableAmount", salary.get("net_amount"));
                    resultMap.put("WorkingDay", salary.get("working_day"));
                    resultMap.put("TotalPayableDeduction", salary.get("total_deduction"));
                    resultMap.put("salary", salary.get("total_earning"));
                    resultMap.put("payableSalary", salary.get("total_payable_earning"));
                    resultMap.put("payable_gross", salary.get("total_payable_earning"));
                    resultMap.put("payableSalaryForRunPayroll", salary.get("payable_salary"));
                    resultMap.put("totalHours", salary.get("total_hours"));
                    resultMap.put("overTimeHours", salary.get("over_time"));
                    resultMap.put("actual_day", salary.get("actual_day") != null ? salary.get("actual_day") : 0);
                    resultMap.put("approved_leave", salary.get("approved_leave") != null ? salary.get("approved_leave") : 0);
                    resultMap.put("holidays", salary.get("holidays") != null ? salary.get("holidays") : 0);
                    resultMap.put("present_day", salary.get("present_day") != null ? salary.get("present_day") : 0);
                    resultMap.put("week_off", salary.get("week_off") != null ? salary.get("week_off") : 0);
                    resultMap.put("total_day", salary.get("total_day") != null ? salary.get("total_day") : 0);
                    resultMap.put("epf", salary.get("epf") != null ? salary.get("epf") : "No");
                    resultMap.put("ytd_total_deduction", salary.get("ytd_total_deduction") != null ? salary.get("ytd_total_deduction") : 0);
                    resultMap.put("lwp", salary.get("lwp") != null ? salary.get("lwp") : 0);
                }
                resultMap.put("status", "success");
            }
        }
        System.out.println("resultMap 417"+" "+resultMap.toString());
        return resultMap;
    }

    private Integer getTravelAllowanceAmount(HttpServletRequest request, Long employeeId) {
        try {
            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            JSONObject data = new JSONObject();
            data.put("employeeId", employeeId);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
            HttpEntity<?> entity = new HttpEntity<>(EncryptDecryptUtils.encrypt(data.toString()), header);
            Map employeeListReq = restTemplate.exchange(reimburshment_url + "/groupmapping/getgroupByEmployee", HttpMethod.POST, entity, HashMap.class).getBody();
            Map employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()), LinkedCaseInsensitiveMap.class);
            System.out.println("employeeListResp ::" + employeeListResp);
            if (employeeListResp.containsKey("status") && employeeListResp.get("status").equals("success")) {
                List ids = (List) employeeListResp.get("groupEmployee");
                List<Long> gropuIdList = new ArrayList<>();
                ids.stream().forEach(val -> gropuIdList.add(Long.parseLong(val.toString())));
                List<TravelAllowance> allowanceList = travelAllowanceRepository.findByGroupIdList(gropuIdList);
                if (!allowanceList.isEmpty()) {
                    return Integer.parseInt(allowanceList.get(0).getAllowanceAmount());
                }
            }
        } catch (Exception ex) {
        }
        return 0;
    }

    //    SalaryBreakup Saved or not
    public Map isSalaryBreakUpSavedforConsultant(String month, String year, String empId, String orgId, String email, String employee_Type, Double Gross) {
        Map resultMap = new HashMap<>();
        List<String> all_allowance_name = new ArrayList<>();
        List<Double> all_allowance_amount = new ArrayList<>();
        List<Double> all_allowance_payable_amount = new ArrayList<>();
        Map<String, List<String>> map_allowance_name = new HashMap<>();
        Map<String, List<Double>> map_allowance_amount = new HashMap<>();
        Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
        List<String> all_deduction_name = new ArrayList<>();
        List<Double> all_deduction_amount = new ArrayList<>();
        List<Double> all_deduction_payable_amount = new ArrayList<>();
        Map<String, List<String>> map_deduction_name = new HashMap<>();
        Map<String, List<Double>> map_deduction_amount = new HashMap<>();
        Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();

        LOGGER.info("isSalaryBreakUpSavedforConsultant method execution starts with month " + month + " and year " + year + " and employee id " + empId + " and organization id " + orgId + " and gross " + Gross + " and employee type " + employee_Type);

        List<LinkedCaseInsensitiveMap> salaryBreakup;
        List<LinkedCaseInsensitiveMap> employeeAllowances = null;
        List<LinkedCaseInsensitiveMap> employeeDeductions = null;

        salaryBreakup = salalrybreakuprepo.fetchListConsultantData(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);

        if (!salaryBreakup.isEmpty()) {

            LOGGER.info("Salary Breakup Data is " + salaryBreakup);
            employeeAllowances = employeeAllowanceRepo.getSavedConsultantAllowancesByMonth(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year));
            LOGGER.info("Employee Allowance Data is " + employeeAllowances);

            employeeDeductions = employeeDeductionRepo.getSavedConsultantDeductionsByMonth(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year));
            LOGGER.info("Employee Deduction Data is " + employeeDeductions);
            
            resultMap.put("isSalaryBreakupSaved", true);

        }

        if (salaryBreakup.isEmpty()) {
            
            resultMap.put("isSalaryBreakupSaved", false);
            
            salaryBreakup = salalrybreakuprepo.getSavedEmployeeSalaryBreakup(Long.parseLong(empId), Long.parseLong(orgId), employee_Type, Gross);

            if (!salaryBreakup.isEmpty()) {

                LOGGER.info("Salary Breakup Data is " + salaryBreakup);
                employeeAllowances = employeeAllowanceRepo.getSavedConsultantAllowances(Long.parseLong(salaryBreakup.get(0).get("sid").toString()));
                LOGGER.info("Employee Allowance Data is " + employeeAllowances);

                employeeDeductions = employeeDeductionRepo.getSavedConsultantDeductions(Long.parseLong(salaryBreakup.get(0).get("sid").toString()));
                LOGGER.info("Employee Deduction Data is " + employeeDeductions);
            }
        }

        if (!employeeAllowances.isEmpty()) {
            for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {
                all_allowance_name.add(allowance.get("consultant_allowance_name").toString());
                all_allowance_amount.add((Double) allowance.get("consultant_allowance_amount"));
                all_allowance_payable_amount.add((Double) allowance.get("consultnat_allowance_payable_amount"));
            }
        }
        map_allowance_name.put("allowanceName", all_allowance_name);
        map_allowance_amount.put("allowanceAmount", all_allowance_amount);
        map_allowance_payable_amount.put("allowancePayableAmount", all_allowance_payable_amount);
        resultMap.put("AllowanceName", map_allowance_name);
        resultMap.put("AllowanceAmount", map_allowance_amount);
        resultMap.put("AllowancePayableAmount", map_allowance_payable_amount);

        if (!employeeDeductions.isEmpty()) {
            for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {
                all_deduction_name.add(deduction.get("consultant_deduction_name").toString());
                all_deduction_amount.add((Double) deduction.get("consultant_deduction_amount"));
                all_deduction_payable_amount.add((Double) deduction.get("consultnat_deduction_payable_amount"));
            }
        }
        map_deduction_name.put("deductionName", all_deduction_name);
        map_deduction_amount.put("deductionAmount", all_deduction_amount);
        map_deduction_payable_amount.put("deductionPayableAmount", all_deduction_payable_amount);
        resultMap.put("DeductionName", map_deduction_name);
        resultMap.put("DeductionAmount", map_deduction_amount);
        resultMap.put("DeductionPayableAmount", map_deduction_payable_amount);

        if (!salaryBreakup.isEmpty()) {
            for (LinkedCaseInsensitiveMap salaryup : salaryBreakup) {
                LinkedHashMap data = new LinkedHashMap();
                resultMap.put("salary_break_up_id", salaryup.get("sid"));
                resultMap.put("NetPayableAmount", salaryup.get("net_amount"));
                resultMap.put("WorkingDay", salaryup.get("working_day"));
                resultMap.put("salary", salaryup.get("total_earning"));
                resultMap.put("TotalDeduction", salaryup.get("total_deduction"));
                resultMap.put("totalEarning", salaryup.get("total_earning"));
                resultMap.put("actual_day", salaryup.get("actual_day") != null ? salaryup.get("actual_day") : 0);
                resultMap.put("totalEarningPayable", salaryup.get("total_payable_earning"));
                resultMap.put("presentDay", salaryup.get("present_day"));
                resultMap.put("approvedLeave", salaryup.get("approved_leave"));
                resultMap.put("holidays", salaryup.get("holidays"));
                resultMap.put("Lwp", salaryup.get("lwp"));
                resultMap.put("weekOff", salaryup.get("week_off"));
                resultMap.put("payableSalary", salaryup.get("payable_salary"));
            }
            resultMap.put("status", "success");
        }
        return resultMap;
    }

    //    SalaryBreakup Saved or not
    public Map isSalaryBreakUpSavedforIntern(String month, String year, String empId, String orgId, String email, String employee_Type) {
        Map resultMap = new HashMap<>();
        List<LinkedHashMap> list = new ArrayList<>();
        //        Get Employee Allowances
        List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.fetchListDataInPdfForConsultantAllowance(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);
        if (employeeAllowances.isEmpty()) {
            return resultMap;
        }
        //        Get Employee Deduction
//        List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.fetchListDataInPdfforInternDeduction(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);
//        System.out.println("deduction" + employeeDeductions);
        //        Get Consultant SalaryBreakup
        List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetchListConsultantData(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);

//        Check Employee Allowance is Empty or not
        if (!employeeAllowances.isEmpty()) {
            for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {
                LinkedHashMap data = new LinkedHashMap();
                data.put("StipendAmount", (Double) allowance.get("consultant_allowance_amount"));
                data.put("StipendpayableSalary", (Double) allowance.get("consultnat_allowance_payable_amount"));
                list.add(data);

            }
        }

//        Check Employee Deduction is Empty or not
//        if (!employeeDeductions.isEmpty()) {
//                for (LinkedCaseInsensitiveMap deduction : employeeAllowances) {
//              LinkedHashMap data = new LinkedHashMap();
//            data.put("esic_InternOnGross", (Double) deduction.get("consultant_deduction_amount"));
//            data.put("esic_InternOnPayableGross", (Double) deduction.get("consultnat_deduction_payable_amount"));
//            list.add(data);
//        }
//        }
//        Check salarybreakup is empty or not
        if (!salaryBreakup.isEmpty()) {
            for (LinkedCaseInsensitiveMap salary : salaryBreakup) {
                LinkedHashMap data = new LinkedHashMap();
                data.put("InternGrossSalary", salary.get("gross_salary"));
                data.put("WorkingDay", salary.get("working_day"));
                data.put("NetPayable_Amount", salary.get("net_amount"));
                data.put("InternTotalEarningAmount", salary.get("total_earning"));
                data.put("InternTotalEarningPayableAmount", salary.get("total_payable_earning"));
                list.add(data);
                resultMap.put("presentDay", salary.get("present_day"));
                resultMap.put("approvedLeave", salary.get("approved_leave"));
                resultMap.put("holidays", salary.get("holidays"));
                resultMap.put("Lwp", salary.get("lwp"));
                resultMap.put("actual_day", salary.get("actual_day") != null ? salary.get("actual_day") : 0);
                resultMap.put("weekOff", salary.get("week_off"));

            }
        }
        resultMap.put("internBreakup", list);
        return resultMap;
    }

    @Override
    public Map calculateSalaryData(String data, HttpServletRequest request) {
        
      
        Map resultMap = new HashMap<>();
        try {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            String employeeType = map.containsKey("employee_Type") ? map.get("employee_Type").toString() : map.containsKey("employeeType") ? map.get("employeeType").toString() : null;
            if (employeeType.equalsIgnoreCase("Worker")) {
                employeeType = "Worker";
            } else if (!employeeType.equalsIgnoreCase("Consultant") && !employeeType.equalsIgnoreCase("Intern")) {
                employeeType = "Full time";
            }

            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
//            Get Leaves
            int currMonth = Integer.parseInt(map.get("month").toString());
            int currYear = 0; 
         //   System.out.println("map 628"+" "+map.toString());
            int selected_month=map.get("selected_month")!=null?Integer.parseInt(map.get("selected_month").toString()):Integer.parseInt(map.get("month").toString());
           // int selected_month=Integer.parseInt(map.get("selected_month").toString());
            if(selected_month==1||selected_month==2||selected_month==3){
                currYear = Integer.parseInt(map.get("year").toString()); 
            }
            else{
                   currYear = Integer.parseInt(map.get("year").toString()); 
            }

            JSONObject json = new JSONObject();
            json.put("employeeId", map.get("emp_id").toString());
            json.put("organizationId", map.get("organization_id").toString());
            List<LinkedCaseInsensitiveMap> salaryDates;
            salaryDates = payrollSettingRepo.getSalaryDates(Long.parseLong(map.get("organization_id").toString()));
            String start_date = "0";
            String end_date = "0";
            if (!salaryDates.isEmpty()) {
                for (LinkedCaseInsensitiveMap l : salaryDates) {
                    if (l.containsKey("start_date") && l.get("start_date") != null) {
                        start_date = l.get("start_date").toString();
                    }
                    if (l.containsKey("end_date") && l.get("end_date") != null) {
                        end_date = l.get("end_date").toString();
                    }
                }
                json.put("startDate", start_date);
                json.put("endDate", end_date);
            } else {
                LOGGER.info("Start Date and End Date in missing");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                return resultMap;
            }
            json.put("year", currYear);
            boolean checkStartDate = false;
            if (Integer.parseInt(start_date) == 1) {
                checkStartDate = true;
                json.put("month", currMonth + 1);
            } else {
                if (currMonth == 0) {
                    json.put("month", 12);
                    json.put("year", currYear - 1);
                } else {
                    json.put("month", currMonth);
                }
            }
            String leaveData = EncryptDecryptUtils.encrypt(json.toString());
        //    System.out.println("leaveData" + " " + leaveData);
            HttpEntity<?> leaveEntity = new HttpEntity<>(leaveData, header);
//            AtomicInteger working_day = new AtomicInteger();
            double working_day = 0.0;
//            int days = this.getnumberOfDaysInMonth(currMonth + 1, currYear);
            if (!map.containsKey("where")) {
                currMonth += 1;
            }
            double Gross = 0.00;
            double PayableGross = 0.00;
            double PayableBasic = 0;
            double Basic = 0.00;
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.MONTH, 0);
            int mon = ca.get(Calendar.MONTH);
            double gross_salary = 0.0;
            Integer month;
            if (Integer.parseInt(start_date) == 1) {
                month = Integer.parseInt(json.get("month").toString());
            } else {
                month = Integer.parseInt(json.get("month").toString()) + 1;
            }
            

            LinkedCaseInsensitiveMap grossSalary = salalrybreakuprepo.getGrossSalary(Integer.parseInt(json.get("employeeId").toString()), json.get("year").toString(), month.toString());
      
            if (grossSalary != null && grossSalary.containsKey("gross_salary")) {
                gross_salary = Double.parseDouble(grossSalary.get("gross_salary").toString());
                resultMap.put("salary", Math.round(gross_salary));
            }

            Map workingDay = this.getWorkingDay(map, leaveEntity);
            System.out.println("workingDay 713"+" "+workingDay.toString());
            if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
                working_day = Double.parseDouble(workingDay.get("working_day").toString());
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", workingDay.get("msg"));
                return resultMap;
            }
            Long days = Long.parseLong(workingDay.get("total_days").toString());
        
            Map salaryBreakupData = this.isSalaryBreakUpSaved(String.valueOf(currMonth + 1), String.valueOf(currYear), map.get("emp_id").toString(), map.get("organization_id").toString(), map.get("email_id").toString(), employeeType, gross_salary);
          
            if (salaryBreakupData.size() > 1) {
               
                Double day = map.containsKey("working_day") && map.get("working_day") != "" ? Double.parseDouble(map.get("working_day").toString()) : 0;
                Double workDay = day != 0.0 ? day : salaryBreakupData.get("working_day") != null ? Double.parseDouble(salaryBreakupData.get("working_day").toString()) : working_day;
                Map salaryData;
                if (salaryBreakupData.get("isSalaryBreakupSaved").equals(false) || day!=0) {
                    salaryBreakupData.put("WorkingDay", workDay);
                    salaryData = this.calculationBasedOnWorkingDayNew(workDay, days, salaryBreakupData, map);
                }else{
                salaryData = salaryBreakupData;
                }
                if (salaryData.get("status").equals("success")) {
                    salaryBreakupData.put("employee_id", map.get("emp_id"));
                    salaryBreakupData.put(("employeeType"), employeeType);
                    salaryBreakupData.put("WorkingDay", workDay);
                    salaryBreakupData.put("advance", salaryData.get("advance"));
                    salaryBreakupData.put("salaryAvailable", "true");
                    return salaryBreakupData;
                }
            } else {
                salaryBreakupData.put("salaryAvailable", "false");
                return salaryBreakupData;
            }

            Double presentDay = 0.0;
            Double approvedLeave = 0.0;
            Double holidays = 0.0;
            Double weekOff = 0.0;
            Double Lwp = 0.0;
            Double actualDays = 0.0;
            if (!map.containsKey("flagTax")) {
               
                Map TimesheetattendanceDetails = this.getattendanceDetails(map, leaveEntity);
            
                LOGGER.info("Input for TimesheetattendanceDetails having employee ID --->> :" + map.get("emp_id").toString() + "Get TimesheetattendanceDetails from timesheet for full time employee time :::" + TimesheetattendanceDetails);
                presentDay = TimesheetattendanceDetails.containsKey("presentDay") && TimesheetattendanceDetails.get("presentDay") != null ? Double.parseDouble(TimesheetattendanceDetails.get("presentDay").toString()) : 0.0;
                approvedLeave = TimesheetattendanceDetails.containsKey("approvedLeave") && TimesheetattendanceDetails.get("approvedLeave") != null ? Double.parseDouble(TimesheetattendanceDetails.get("approvedLeave").toString()) : 0.0;
                weekOff = TimesheetattendanceDetails.containsKey("weekOff") && TimesheetattendanceDetails.get("weekOff") != null ? Double.parseDouble(TimesheetattendanceDetails.get("weekOff").toString()) : 0.0;
                holidays = TimesheetattendanceDetails.containsKey("holidays") && TimesheetattendanceDetails.get("holidays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("holidays").toString()) : 0.0;
                Lwp = TimesheetattendanceDetails.containsKey("Lwp") && TimesheetattendanceDetails.get("Lwp") != null ? Double.parseDouble(TimesheetattendanceDetails.get("Lwp").toString()) : 0.0;
                actualDays = TimesheetattendanceDetails.containsKey("actualDays") && TimesheetattendanceDetails.get("actualDays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("actualDays").toString()) : 0.0;

                LOGGER.info("Present Data from timesheet ==> " + presentDay);
                LOGGER.info("approvedLeave Data from timesheet ==> " + approvedLeave);
                LOGGER.info("weekOff Data from timesheet ==> " + weekOff);
                LOGGER.info("holidays Data from timesheet ==> " + holidays);
                LOGGER.info("Lwp Data from timesheet ==> " + Lwp);
            }

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryData() :: ", ex);
        }
      
        return resultMap;
        
      
    }

    public Map calculationBasedOnWorkingDay(Double working_day, Long days, Map salarybreakupData, Map map) {
        LOGGER.info("calculationBasedOnWorkingDay method execution starts with Working Day " + working_day + " and days " + days + " and Salary Breakup Data " + salarybreakupData);
        Map resultMap = new HashMap<>();
        Map<String, Double> a_amount = new HashMap<>();
        Map<String, List<Double>> a_payable_amount = new HashMap<>();
        Map<String, List<Double>> d_payable_amount = new HashMap<>();
        List<Double> allowancePayableAmount=new ArrayList<>();
        List<Double> deduction_payable_amount = new ArrayList<>();

        try {
            System.out.println("working_day 796"+" "+" "+working_day+" "+"days "+" "+days);
            List<Double> allowanceAmount = (List<Double>) ((HashMap) salarybreakupData.get("AllowanceAmount")).get("allowanceAmount");
               if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                           allowancePayableAmount = allowanceAmount.stream().map(a -> (a * working_day) / 26).collect(Collectors.toList());
  
               }else{
                allowancePayableAmount = allowanceAmount.stream().map(a -> (a * working_day) / days).collect(Collectors.toList());
 
               }
            
          
            double payableGross = 0;

            if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                payableGross = (Math.round(((Double.parseDouble(salarybreakupData.get("salary").toString())) / 26) * working_day));
                //payableGross=Math.round(Double.parseDouble(salarybreakupData.get("payableSalary").toString()));
            } else {
                payableGross = (Math.round(Double.parseDouble(salarybreakupData.get("salary").toString()) * working_day) / days);
                // payableGross=Math.round(Double.parseDouble(salarybreakupData.get("payableSalary").toString()));
                
            }

           if ( map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
               // double otherPayableAllowances = Math.round(((Double.parseDouble(salarybreakupData.get("OtherPayableAllowances").toString())) * working_day) / 26);

 

                salarybreakupData.put("OtherPayableAllowances", salarybreakupData.get("OtherPayableAllowances"));
            }else if(map.get("employee_Type").toString().equalsIgnoreCase("Full time")){
                //System.out.println("salarybreakupData 523"+" "+salarybreakupData.toString());
          //  double otherPayableAllowances = Math.round(((Double.parseDouble(salarybreakupData.get("OtherPayableAllowances").toString())) * working_day) / days);
               salarybreakupData.put("OtherPayableAllowances", salarybreakupData.get("OtherPayableAllowances"));
            }

            List<Allowance> allowances;
            int selected_month;
            int selected_year;
            if (map.containsKey("selected_month")) {
                selected_month = Integer.parseInt(map.get("selected_month").toString()) - 1;
                selected_year = Integer.parseInt(map.get("selected_year").toString());
            } else {
                selected_month = Integer.parseInt(map.get("month").toString()) - 1;
                selected_year = Integer.parseInt(map.get("year").toString());
            }
            allowances = allowanceRepo.findApprovedAllowances(Long.parseLong(map.get("organization_id").toString()), new Date(selected_year - 1900, selected_month - 1, 28), map.get("employee_Type").toString());
            //    System.out.println("myallowances============>>>>>>>>>"+ allowances+"   date===="+new Date(selected_year - 1900, selected_month-1, 28)+"  organization===="+map.get("organization_id").toString()+"icoming values==== "+map+" employeeeeeeeetype"+ employeeType);

            for (int idx = 0; idx < allowancePayableAmount.size(); idx++) {
                double rounded = Math.round(allowancePayableAmount.get(idx));
                allowancePayableAmount.set(idx, rounded);
            }
            a_payable_amount.put("allowancePayableAmount", allowancePayableAmount);
            salarybreakupData.put("AllowancePayableAmount", a_payable_amount);

            List<String> deductionName = (List<String>) ((HashMap) salarybreakupData.get("DeductionName")).get("deductionName");
            List<Double> deductionAmount = (List<Double>) ((HashMap) salarybreakupData.get("DeductionAmount")).get("deductionAmount");
            int idx = -1;
            for (String name : deductionName) {
                idx++;
                if (name.equalsIgnoreCase("Advance")) {
                    Double advanceAmount = 0.0;
                    List<LinkedCaseInsensitiveMap> employeeLoan;
                    if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                        employeeLoan = workerLoanRepo.getLoanForSalaryBreakup(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString())+1, Integer.parseInt(map.get("year").toString()));
                    } else {
                        employeeLoan = employeeLoanRepo.getLoanForSalaryBreakup(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString())+1, Integer.parseInt(map.get("year").toString()));
                    }
                    if (!employeeLoan.isEmpty()) {
                        for (LinkedCaseInsensitiveMap emp_loan : employeeLoan) {
                            if (emp_loan.containsKey("remaining_amount") && emp_loan.get("remaining_amount") != null) {
                                if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > 0) {
                                    if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > Double.parseDouble(emp_loan.get("installment_amount").toString())) {
                                        advanceAmount += Double.parseDouble(emp_loan.get("installment_amount").toString());
                                    } else {
                                        advanceAmount += Double.parseDouble(emp_loan.get("remaining_amount").toString());

                                    }
                                } else {
                                    advanceAmount += 0;
                                }
                            }
                        }
                        deduction_payable_amount.add(advanceAmount);
                    } else {
                        deduction_payable_amount.add(0.0);
                    }
                    resultMap.put("advance", advanceAmount);
                } else if (name.equalsIgnoreCase("Income Tax")) {
                    List<Double> values = (List<Double>) ((HashMap) (salarybreakupData.get("DeductionPayableAmount"))).get("deductionPayableAmount");
                    deduction_payable_amount.add(values.get(idx));
                } else if (name.equalsIgnoreCase("Professional Tax")) {
                    deduction_payable_amount.add(deductionAmount.get(idx));
                } else {
                    if(map.get("employee_Type").toString().equalsIgnoreCase("Worker")){
                        deduction_payable_amount.add((deductionAmount.get(idx) * working_day) / 26);
                        
                    }else{
                        deduction_payable_amount.add((deductionAmount.get(idx) * working_day) / days); 
                    }
                }
            }

            double deductionPayableAmount = 0.0;
            for (int idxx = 0; idxx < deduction_payable_amount.size(); idxx++) {
                double rounded = Math.round(deduction_payable_amount.get(idxx));
                deductionPayableAmount += deduction_payable_amount.get(idxx);
                deduction_payable_amount.set(idxx, rounded);
            }

            d_payable_amount.put("deductionPayableAmount", deduction_payable_amount);
            salarybreakupData.put("DeductionPayableAmount", d_payable_amount);
            salarybreakupData.put("payable_gross", Math.round(payableGross));
            //NetPayableAmount
            salarybreakupData.put("NetPayableAmount", Math.round(payableGross - deductionPayableAmount));
           // salarybreakupData.put("NetPayableAmount", Math.round(payableGross ));
            salarybreakupData.put("total_deduction", Math.round(deductionPayableAmount));
            resultMap.put("salaryCalculation", salarybreakupData);

            resultMap.put("status", "success");

        } catch (Exception ex) {
            LOGGER.error("Problem in SalaryBreakupServiceImpl :: calculationBasedOnWorkingDay() => " + ex);
            resultMap.put("status", "exception");
        }
       
        return resultMap;
    }

    public Map allowanceCalculated(Map map, List<Allowance> allowances, Double Basic, Double PayableBasic, Double travelAllowanceAmount, Double Gross, Double PayableGross, Double working_day, Long days, HttpServletRequest request, Double presentDay, Double gross_salary, Double weekOff) {
        Map resultMap = new HashMap<>();
        List<String> all_allowance_name = new ArrayList<>();
        List<Double> all_allowance_amount = new ArrayList<>();
        List<Double> all_allowance_payable_amount = new ArrayList<>();
        Map<String, List<String>> map_allowance_name = new HashMap<>();
        Map<String, List<Double>> map_allowance_amount = new HashMap<>();
        Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
        List<Double> all_allowance_basic_percentage = new ArrayList<>();
        List<String> all_deduction_name = new ArrayList<>();
        List<Double> all_deduction_amount = new ArrayList<>();
        List<Double> all_deduction_payable_amount = new ArrayList<>();
        Map<String, List<String>> map_deduction_name = new HashMap<>();
        Map<String, List<Double>> map_deduction_amount = new HashMap<>();
        Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();
        Map<String, List<Double>> map_allowance_basic_percentage = new HashMap<>();
        Double percentageChange = null;
        try {
           

            Optional<Allowance> present = allowances.stream().filter(a -> a.getAllowance_name().equalsIgnoreCase("Basic Salary")).findFirst();
            if (present.isPresent()) {
                Allowance basic = present.get();
                Double percentageChangeTax = salalrybreakuprepo.getPercentage(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()));

                Double percentages = percentageChangeTax != null ? percentageChangeTax : basic.getPercentage();
                percentageChange = (map.containsKey("percentage_Change") && (map.get("percentage_Change") != null ? !String.valueOf(map.get("percentage_Change")).equals("") : false) ? Double.parseDouble(map.get("percentage_Change").toString()) : percentages);
                all_allowance_name.add("Basic Salary");
                all_allowance_amount.add(Math.round(Math.round((Gross / 100) * percentageChange) * 100.00) / 100.00);
                all_allowance_payable_amount.add(Math.round(Math.round((PayableGross / 100) * percentageChange) * 100.00) / 100.00);
                Basic += ((Gross / 100) * percentageChange);
                all_allowance_basic_percentage.add(percentageChange);
                PayableBasic += ((PayableGross / 100) * percentageChange);
            }

            for (Allowance a : allowances) {
//                    Calculate Payable Basic
                if (!a.getAllowance_name().equalsIgnoreCase("Basic Salary") && !a.getAllowance_name().equalsIgnoreCase("Incentive")) {
                    {
//                        Calculate Payable FixedAmount Allowances  
                        if (a.getAmount() != null) {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(Math.round(a.getAmount() * 100.00) / 100.00);
                            all_allowance_payable_amount.add(Math.round(Math.round((a.getAmount() * working_day) / days) * 100.00) / 100.00);
                        } //                        Calculate Payable VariablAmount Allowances
                        else if (a.getSalary().equals("Basic")) {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(Math.round(Math.round((Basic / 100) * a.getPercentage()) * 100.00) / 100.00);
                            all_allowance_payable_amount.add(Math.round(Math.round((PayableBasic / 100) * a.getPercentage()) * 100.00) / 100.00);
                        } else if (a.getSalary().equals("Gross")) {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(Math.round(Math.round((Gross / 100) * a.getPercentage()) * 100.00) / 100.00);
                            all_allowance_payable_amount.add(Math.round(Math.round((PayableGross / 100) * a.getPercentage()) * 100.00) / 100.00);
                        } else {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(0.0);
                            all_allowance_payable_amount.add(0.0);
                        }
                    }
                }
            };
            int travelAllowanceIndex = all_allowance_name.indexOf("Travel Allowance");
            double ta = 0;
            for (int i = 0; i < all_allowance_amount.size(); i++) {
                if (travelAllowanceIndex != i) {
                    ta += all_allowance_amount.get(i);
                }
            }
            double tpa = 0;
            for (int i = 0; i < all_allowance_payable_amount.size(); i++) {
                if (travelAllowanceIndex != i) {
                    tpa += all_allowance_payable_amount.get(i);
                }
            }
            double otherPayableAllowance = Math.round((PayableGross - tpa) * 100.00) / 100.00;
            double otherAllowance = Math.round((Gross - ta) * 100.00) / 100.00;

            if (travelAllowanceIndex != -1) {
                int travelamount = this.getTravelAllowanceAmount(request, Long.parseLong(map.get("emp_id").toString()));
                if (!map.containsKey("currentMonthTax")) {
                    travelAllowanceAmount = new Double(Math.round((travelamount * presentDay) / (days - weekOff)));
                }
                if (travelamount != 0) {
                    all_allowance_amount.set(travelAllowanceIndex, 0.0);
                    all_allowance_payable_amount.set(travelAllowanceIndex, travelAllowanceAmount);
                } else {
                    all_allowance_amount.remove(travelAllowanceIndex);
                    all_allowance_payable_amount.remove(travelAllowanceIndex);
                    all_allowance_name.remove(travelAllowanceIndex);
                }
            }

            resultMap.put("basic", Basic);
            resultMap.put("percentage_change", percentageChange);
            resultMap.put("payableBasic", PayableBasic);
            resultMap.put("salary", Math.round(gross_salary));
            resultMap.put("payableSalary", ((Math.round(((gross_salary * working_day) / days)) * 100.0) / 100.0) + travelAllowanceAmount);
            resultMap.put("OtherPayableAllowances", Math.round(otherPayableAllowance));
            resultMap.put("OtherAllowances", Math.round(otherAllowance));
            map_allowance_name.put("allowanceName", all_allowance_name);
            map_allowance_amount.put("allowanceAmount", all_allowance_amount);
            map_allowance_payable_amount.put("allowancePayableAmount", all_allowance_payable_amount);
            resultMap.put("AllowanceName", map_allowance_name);
            resultMap.put("AllowanceAmount", map_allowance_amount);
            resultMap.put("AllowancePayableAmount", map_allowance_payable_amount);
            map_allowance_basic_percentage.put("allowancePercentage", all_allowance_basic_percentage);
            resultMap.put("AllowancePercentage", map_allowance_basic_percentage);
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> deductionCalculated() :: ", ex);
        }
        return resultMap;
    }

    public Map allowanceCalculatedForWorker(Map map, List<Allowance> allowances, Double Basic, Double PayableBasic, Double travelAllowanceAmount, Double Gross, Double PayableGross, Double working_day, Long days, HttpServletRequest request, Double presentDay, Double gross_salary, Double weekOff, HttpEntity leaveEntity) {
        Map resultMap = new HashMap<>();
        List<String> all_allowance_name = new ArrayList<>();
        List<Long> all_allowance_id = new ArrayList<>();
        List<Double> all_allowance_amount = new ArrayList<>();
        List<Double> all_allowance_payable_amount = new ArrayList<>();
        Map<String, List<String>> map_allowance_name = new HashMap<>();
        Map<String, List<Double>> map_allowance_amount = new HashMap<>();
        Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
        LinkedCaseInsensitiveMap allowanceWithAmount = new LinkedCaseInsensitiveMap<>();
        List<LinkedCaseInsensitiveMap> subAllowanceIds = new ArrayList<>();
        Double monthlyOvertime = 0.0;
        Double overTimeValue = 0.0;
        Double gratuityAmount = 0.0;
        try {
            Optional<Allowance> present = allowances.stream().filter(a -> a.getAllowance_name().equalsIgnoreCase("Basic Salary")).findFirst();
            if (present.isPresent()) {
                Allowance basic = present.get();
                all_allowance_id.add(basic.getAllowance_id());
                all_allowance_name.add("Basic Salary");
                if (basic.getAmount() != null) {
                    all_allowance_amount.add(basic.getAmount());
                    all_allowance_payable_amount.add(basic.getAmount() * working_day);
                    Basic += basic.getAmount();
                    PayableBasic += basic.getAmount() * working_day;
                } else {
                    all_allowance_amount.add(Math.round(Math.round((Gross / 100) * basic.getPercentage()) * 100.00) / 100.00);
                    all_allowance_payable_amount.add(Math.round(Math.round((PayableGross / 100) * basic.getPercentage()) * 100.00) / 100.00);
                    Basic += ((Gross / 100) * basic.getPercentage());
                    PayableBasic += ((PayableGross / 100) * basic.getPercentage()) * working_day;
                }
                allowanceWithAmount.put("Basic Salary", PayableBasic);
            }

            for (Allowance a : allowances) {

//                if(a.getAllowance_name().equalsIgnoreCase("Gratuity")){
//                    gratuityAmount= PayableBasic*a.getPercentage();
//                }
//                    Calculate Payable Basic
                if (!a.getAllowance_name().equals("Basic Salary")) {
                    all_allowance_id.add(a.getAllowance_id());
                    {
                        if (a.getAllowance_name().equalsIgnoreCase("Overtime") || a.getAllowance_name().equalsIgnoreCase("Overtime Allowance")) {
                            Map getOvertime = null;
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(0.0);
                            all_allowance_payable_amount.add(0.0);
                            subAllowanceIds = salalrybreakuprepo.getIdOfSubAllowance(Long.parseLong(map.get("organization_id").toString()));
                            String payrollBasedOn = organizationSetupRepo.fetchWorkingDay(Long.parseLong(map.get("organization_id").toString()));
                            try {
                                getOvertime = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getovertimeofemployee", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                            } catch (Exception ex) {
                                LOGGER.info("Problem getting Overtime from timesheet" + ex);
                                resultMap.clear();
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Problem getting Overtime from timesheet");
                                return resultMap;
                            }
                            Map overTime = mapper.readValue(EncryptDecryptUtils.decrypt(getOvertime.get("data").toString()), LinkedCaseInsensitiveMap.class);

                            if (overTime.containsKey("overtime_value")) {
                                if (payrollBasedOn.equalsIgnoreCase("Leave")) {
                                    monthlyOvertime = 0.0;
                                } else {
                                    monthlyOvertime = Double.parseDouble(overTime.get("overtime_value").toString().split(":")[0]);
                                }

                            }
                        } else if (a.getAmount() != null) {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(Math.round(a.getAmount() * 100.00) / 100.00);
                            all_allowance_payable_amount.add(Math.round((a.getAmount() * working_day) * 100.00) / 100.00);
                            allowanceWithAmount.put(a.getAllowance_name(), Math.round((a.getAmount() * working_day) * 100.00) / 100.00);
                        } //                        Calculate Payable VariablAmount Allowances
                        else if (a.getSalary().equals("Basic")) {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(Math.round(Math.round((Basic / 100) * a.getPercentage()) * 100.00) / 100.00);
                            all_allowance_payable_amount.add(Math.round(Math.round((PayableBasic / 100) * a.getPercentage()) * 100.00) / 100.00);
                            allowanceWithAmount.put(a.getAllowance_name(), Math.round(Math.round((PayableBasic / 100) * a.getPercentage()) * 100.00) / 100.00);
                        } else if (a.getSalary().equals("Gross")) {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(Math.round(Math.round((Gross / 100) * a.getPercentage()) * 100.00) / 100.00);
                            all_allowance_payable_amount.add(Math.round(Math.round((PayableGross / 100) * a.getPercentage()) * 100.00) / 100.00);
                            allowanceWithAmount.put(a.getAllowance_name(), Math.round(Math.round((PayableGross / 100) * a.getPercentage()) * 100.00) / 100.00);
                        } else {
                            all_allowance_name.add(a.getAllowance_name());
                            all_allowance_amount.add(0.0);
                            all_allowance_payable_amount.add(0.0);
                            allowanceWithAmount.put(a.getAllowance_name(), 0);
                        }
                    }
                }
            };
            Double amount = 0.0;
            Double standard_hours = 1.0;
            if (subAllowanceIds.size() == 1) {
                amount = Gross;
            }
            for (int idx = 0; idx < all_allowance_id.size(); idx++) {
                for (LinkedCaseInsensitiveMap id : subAllowanceIds) {
                    if (all_allowance_id.get(idx) == Long.parseLong(id.get("allowance_id").toString())) {
                        if (amount == 0) {
                            amount = all_allowance_amount.get(idx);
                        } else {
                            amount = amount - all_allowance_amount.get(idx);
                        }
                    }
                }
                if (all_allowance_name.get(idx).equalsIgnoreCase("Overtime") || all_allowance_name.get(idx).equalsIgnoreCase("Overtime Allowance")) {
                    standard_hours = allowanceRepo.getStandardHours(all_allowance_name.get(idx).toString(), "Worker", Long.parseLong(map.get("organization_id").toString()));
                    overTimeValue = (amount / standard_hours) * monthlyOvertime;
                    all_allowance_payable_amount.set(idx, overTimeValue);
                }
            }
            int travelAllowanceIndex = all_allowance_name.indexOf("Travel Allowance");
            double ta = 0;
            for (int i = 0; i < all_allowance_amount.size(); i++) {
                if (travelAllowanceIndex != i) {
                    ta += all_allowance_amount.get(i);
                }
            }
            double tpa = 0;
            for (int i = 0; i < all_allowance_payable_amount.size(); i++) {
                if (!all_allowance_name.get(i).equalsIgnoreCase("Overtime") && !all_allowance_name.get(i).equalsIgnoreCase("Overtime Allowance")) {
                    if (travelAllowanceIndex != i) {
                        tpa += all_allowance_payable_amount.get(i);
                    }
                }
            }
            double otherPayableAllowance = Math.round((PayableGross - tpa) * 100.00) / 100.00;

            double otherAllowance = Math.round((Gross - ta) * 100.00) / 100.00;

            if (travelAllowanceIndex != -1) {
                int travelamount = this.getTravelAllowanceAmount(request, Long.parseLong(map.get("emp_id").toString()));
                if (!map.containsKey("currentMonthTax")) {
                    travelAllowanceAmount = new Double(Math.round((travelamount * presentDay) / (days - weekOff)));
                }

                if (travelamount != 0) {
                    all_allowance_amount.set(travelAllowanceIndex, 0.0);
                    all_allowance_payable_amount.set(travelAllowanceIndex, travelAllowanceAmount);
                } else {
                    all_allowance_amount.remove(travelAllowanceIndex);
                    all_allowance_payable_amount.remove(travelAllowanceIndex);
                    all_allowance_name.remove(travelAllowanceIndex);
                }
                allowanceWithAmount.put("Travel Allowance", travelAllowanceAmount);
            }
            resultMap.put("over_time", monthlyOvertime);
            resultMap.put("rate", amount / standard_hours);
            resultMap.put("basic", Basic);
            resultMap.put("overTimeValue", overTimeValue);
            resultMap.put("payableBasic", PayableBasic);
            resultMap.put("wages", Math.round(gross_salary));
            resultMap.put("salary", Math.round(gross_salary * days));
            resultMap.put("payableSalary", ((Math.round(((gross_salary * working_day))) * 100.0) / 100.0) + travelAllowanceAmount + overTimeValue);
            resultMap.put("OtherPayableAllowances", Math.round(otherPayableAllowance));
            resultMap.put("OtherAllowances", Math.round(otherAllowance));
            map_allowance_name.put("allowanceName", all_allowance_name);
            map_allowance_amount.put("allowanceAmount", all_allowance_amount);
            map_allowance_payable_amount.put("allowancePayableAmount", all_allowance_payable_amount);
            resultMap.put("AllowanceName", map_allowance_name);
            resultMap.put("AllowanceAmount", map_allowance_amount);
            resultMap.put("AllowancePayableAmount", map_allowance_payable_amount);
            resultMap.put("allowance_amount", allowanceWithAmount);
        } catch (Exception ex) {

            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> allowanceCalculatedForWorker() :: ", ex);
        }
        return resultMap;
    }

    public Map deductionCalculated(Map map, double workingDay, Long days, String PayableGross, String PayableBasic, String Gross, String Basic, int currYear, int currMonth, boolean checkDate) {

        Map resultMap = new HashMap();

        try {
            //       Get EPF Key From Organization SetUp
            LinkedCaseInsensitiveMap organization_epf = orgRepo.fetchEpf(Long.parseLong(map.get("organization_id").toString()));
            if (organization_epf.containsKey("epf") && organization_epf.get("epf") != null) {
                resultMap.put("organization_epf", organization_epf.get("epf"));
            } else {
                resultMap.put("organization_epf", "noepf");
            }

            String employeeType = "Full time";
            List<Deduction> deductions = deductionRepo.findApprovedDeductions(Long.parseLong(map.get("organization_id").toString()), new Date(currYear - 1900, currMonth - 1, 28), employeeType);
            List<String> all_deduction_name = new ArrayList<>();
            List<Double> all_deduction_amount = new ArrayList<>();
            List<Double> all_deduction_payable_amount = new ArrayList<>();
            Map<String, List<String>> map_deduction_name = new HashMap<>();
            Map<String, List<Double>> map_deduction_amount = new HashMap<>();
            Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();
            Double advanceAmount = 0.0;
            //                 Calculate Payable Amount of Deductions
            for (Deduction d : deductions) {
                if (d.getDeduction_name().equalsIgnoreCase("Other Deductions")) {
                    all_deduction_amount.add(0.0);
                    all_deduction_payable_amount.add(0.0);
                }
                if (d.getDeduction_name().equalsIgnoreCase("Advance")) {
                    int month = currMonth;
                    if (!checkDate) {
                        month = currMonth - 1;
                    }
                    List<LinkedCaseInsensitiveMap> employeeLoan = employeeLoanRepo.getLoanForSalaryBreakup(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), month, Integer.parseInt(map.get("year").toString()));
                    if (!employeeLoan.isEmpty()) {
                        for (LinkedCaseInsensitiveMap emp_loan : employeeLoan) {
                            if (emp_loan.containsKey("remaining_amount") && emp_loan.get("remaining_amount") != null) {
                                if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > 0) {
//                                            all_deduction_name.add("Advance");
                                    if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > Double.parseDouble(emp_loan.get("installment_amount").toString())) {
                                        advanceAmount += Double.parseDouble(emp_loan.get("installment_amount").toString());
                                    } else {
                                        advanceAmount += Double.parseDouble(emp_loan.get("remaining_amount").toString());

                                    }
                                } else {
                                    advanceAmount += 0;
                                }
                            }
                        }
                        all_deduction_payable_amount.add(advanceAmount);
                    } else {
                        all_deduction_payable_amount.add(0.0);
                    }
                } //                        Check Income Tax is present or not
                else if (d.getDeduction_name().equals("Income Tax")) {
                    all_deduction_payable_amount.add(map.get("income_tax") != null ? Double.parseDouble(map.get("income_tax").toString()) : 0);
                }
//                    Claculate Payable ESIC

                all_deduction_name.add(d.getDeduction_name());
                if (d.getDeduction_name().equals("ESIC")) {

                    if ((Double.parseDouble(Gross)) <= 21000.00) {
                        if (d.getSalary().equals("Gross")) {
                            all_deduction_amount.add(Math.round(Math.round(((Double.parseDouble(Gross)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                            all_deduction_payable_amount.add(Math.round(Math.round(((Double.parseDouble(PayableGross)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                        } else if (d.getSalary().equals("Basic")) {
                            all_deduction_amount.add(Math.round(Math.round(((Double.parseDouble(Basic)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                            all_deduction_payable_amount.add(Math.round(Math.round(((Double.parseDouble(PayableBasic)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                        }
                    } else {
                        all_deduction_amount.add(0.0);
                        all_deduction_payable_amount.add(0.0);
                    }
                } //                    Calculate Payable EPF
                else if (d.getDeduction_name().equals("EPF")) {
                    System.out.println("EPFKEY:--->" + organization_epf);
                    if (organization_epf.get("epf") != null && organization_epf.get("epf").equals("yesepf")) {
                        if ((Double.parseDouble(Basic) <= 15000 || (map.get("epf") == "yesepf") && (Double.parseDouble(Basic)) > 15000)) {
                            if (d.getSalary().equals("Basic")) {
                                all_deduction_amount.add(Math.round(Math.round(((Double.parseDouble(Basic)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                                all_deduction_payable_amount.add(Math.round(Math.round(((Double.parseDouble(PayableBasic)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                            } else if (d.getSalary().equals("Gross")) {
                                all_deduction_amount.add(Math.round(Math.round(((Double.parseDouble(Gross)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                                all_deduction_payable_amount.add(Math.round(Math.round(((Double.parseDouble(PayableGross)) / 100) * d.getPercentage()) * 100.00) / 100.00);
                            }
                            resultMap.put("epf", "false");
                        } else {
                            all_deduction_amount.add(0.0);
                            all_deduction_payable_amount.add(0.0);
                            resultMap.put("epf", "true");
                        }

                    } else {
                        all_deduction_amount.add(0.0);
                        all_deduction_payable_amount.add(0.0);
                        resultMap.put("epf", "true");
                    }
                } else if (d.getDeduction_name().equalsIgnoreCase("Professional Tax")) {

                    /**
                     * ************* calculate professional tax- pooja
                     * ***********
                     */
                    String orgState = (String) map.get("orgState");
                    String gender = (String) map.get("gender");
                    if (gender != null && gender.equalsIgnoreCase("female") && orgState.equalsIgnoreCase("Maharashtra") && Double.parseDouble(Gross) <= Double.valueOf(10000)) {
                        all_deduction_payable_amount.add(0.0D);
                    } else {
                        Double taxAmount = professionalTaxSlabRepo.fecthTaxAmountByStateAndGrossSalary(orgState, Double.parseDouble(Gross));

                        if (taxAmount == null) {
                            all_deduction_payable_amount.add(0.0D);
                        } else {
                            if (workingDay == 0) {
                                all_deduction_payable_amount.add(0.0D);
                            } else {
                                all_deduction_payable_amount.add(taxAmount);
                            }

                        }

                    }
                } else {
                    if (d.getAmount() != null && !d.getDeduction_name().equalsIgnoreCase("Advance") && !d.getDeduction_name().equalsIgnoreCase("Income Tax")) {
                        all_deduction_amount.add(d.getAmount());
                        all_deduction_payable_amount.add(Math.round(Math.round((d.getAmount()) * workingDay) / days * 100.00) / 100.00);
                    } else if (d.getSalary().equals("Basic")) {
                        all_deduction_amount.add(Math.round(Math.round((Double.parseDouble(Basic) / 100) * d.getPercentage()) * 100.00) / 100.00);
                        all_deduction_payable_amount.add(Math.round((Double.parseDouble(PayableBasic) / 100) * d.getPercentage()) * 100.00 / 100.00);
                    } else if (d.getSalary().equals("Gross")) {
                        all_deduction_amount.add(Math.round(Math.round((Double.parseDouble(Gross) / 100) * d.getPercentage()) * 100.00) / 100.00);
                        all_deduction_payable_amount.add(Math.round(Math.round((Double.parseDouble(PayableGross) / 100) * d.getPercentage()) * 100.00) / 100.00);
                    }
                }

            };
            double td = 0;
            for (int i = 0; i < all_deduction_amount.size(); i++) {
                td += all_deduction_amount.get(i);
            }
            double tpd = 0;
            for (int i = 0; i < all_deduction_payable_amount.size(); i++) {
                tpd += all_deduction_payable_amount.get(i);
            }
            resultMap.put("employeeType", map.get("employee_Type").toString());
            resultMap.put("TotalDeduction", Math.round((td * 100.00) / 100.00));
            resultMap.put("TotalPayableDeduction", Math.round((tpd * 100.00) / 100.00));
            resultMap.put("deductionName", all_deduction_name);
            resultMap.put("deductionAmount", all_deduction_amount);
            resultMap.put("deductionPayableAmount", all_deduction_payable_amount);

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> deductionCalculated() :: ", ex);
        }
        return resultMap;
    }

    public Map deductionCalculatedForWorker(Map map, double workingDay, String PayableGross, String PayableBasic, String Gross, String Basic, int currYear, int currMonth, Map allowanceData, Long days) {

        Map resultMap = new HashMap();

        try {
            List<Long> deductionId = new ArrayList<>();
            List<Long> allowanceId = new ArrayList<>();
            LinkedCaseInsensitiveMap organization_epf = orgRepo.fetchEpf(Long.parseLong(map.get("organization_id").toString()));
            if (organization_epf.containsKey("epf") && organization_epf.get("epf") != null) {
                resultMap.put("organization_epf", organization_epf.get("epf"));
            } else {
                resultMap.put("organization_epf", "noepf");
            }
            String employeeType = "Worker";
//            Double gratuityAmount= 0.0;
            List<Deduction> deductions = deductionRepo.findApprovedDeductionsexceptGratuity(Long.parseLong(map.get("organization_id").toString()), new Date(currYear - 1900, currMonth, 1), employeeType);
            deductions.stream().forEach(d -> {
                deductionId.add(d.getDeduction_id());
            });
            List<String> all_deduction_name = new ArrayList<>();
            List<Double> all_deduction_amount = new ArrayList<>();
            List<Double> all_deduction_payable_amount = new ArrayList<>();

            List<LinkedCaseInsensitiveMap> allowances = deductionAllowanceMappingRepo.getAllowancesForDeduction(deductionId);

            for (Deduction d : deductions) {

//                if(d.getDeduction_name().equalsIgnoreCase("Gratuity")){
//                    gratuityAmount=(Double.parseDouble(PayableBasic)*d.getPercentage()/100.0);
//                }
//                if (!d.getDeduction_name().equalsIgnoreCase("Gratuity")) {
                all_deduction_name.add(d.getDeduction_name());

                List<LinkedCaseInsensitiveMap> allowance_amount = allowances.stream().filter(a -> Long.parseLong(a.get("deduction_id").toString()) == Long.parseLong(d.getDeduction_id().toString())).collect(Collectors.toList());

                Double payableAllowance = 0.0;

                for (LinkedCaseInsensitiveMap l : allowance_amount) {
                    payableAllowance += Double.parseDouble((((LinkedCaseInsensitiveMap) allowanceData.get("allowance_amount")).get(l.get("allowance_name"))).toString());
                }
                if (d.getDeduction_name().equalsIgnoreCase("Advance")) {
                    List<LinkedCaseInsensitiveMap> employeeLoan = workerLoanRepo.getLoanForSalaryBreakup(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()) + 1, Integer.parseInt(map.get("year").toString()));
                    if (!employeeLoan.isEmpty()) {
                        for (LinkedCaseInsensitiveMap emp_loan : employeeLoan) {
                            if (emp_loan.containsKey("remaining_amount") && emp_loan.get("remaining_amount") != null) {
                                if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > 0) {
//                                            all_deduction_name.add("Advance");
                                    if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > Double.parseDouble(emp_loan.get("installment_amount").toString())) {
                                        all_deduction_payable_amount.add(Double.parseDouble(emp_loan.get("installment_amount").toString()));
                                    } else {
                                        all_deduction_payable_amount.add(Double.parseDouble(emp_loan.get("remaining_amount").toString()));
                                    }
                                } else {
                                    all_deduction_payable_amount.add(0.0);
                                }
                            }
                        }
                    } else {
                        all_deduction_payable_amount.add(0.0);
                    }
                } else if (d.getDeduction_name().equalsIgnoreCase("EPF")) {
                    if (payableAllowance <= 15000) {
                        all_deduction_amount.add(Math.round(Math.round((payableAllowance / 100) * d.getPercentage()) * 100.00) / 100.00);
                        all_deduction_payable_amount.add(Math.round((payableAllowance / 100) * d.getPercentage()) * 100.00 / 100.00);
                    } else {
                        all_deduction_amount.add(Math.round(Math.round((15000 / 100) * d.getPercentage()) * 100.00) / 100.00);
                        all_deduction_payable_amount.add(Math.round((15000 / 100) * d.getPercentage()) * 100.00 / 100.00);
                    }
                } else if (d.getDeduction_name().equalsIgnoreCase("ESIC")) {
                    if (Double.parseDouble(PayableGross) <= 21000) {
                        all_deduction_amount.add(Math.round(Math.round((Double.parseDouble(PayableGross) / 100) * d.getPercentage()) * 100.00) / 100.00);
                        all_deduction_payable_amount.add(Math.round((Double.parseDouble(PayableGross) / 100) * d.getPercentage()) * 100.00 / 100.00);
                    } else {
                        all_deduction_amount.add(0.0);
                        all_deduction_payable_amount.add(0.0);
                    }
                } else {
                    if (d.getAmount() != null) {
                        all_deduction_amount.add(d.getAmount());
                        all_deduction_payable_amount.add(Math.round(Math.round((d.getAmount()) * workingDay) / days * 100.00) / 100.00);
                    } else {
                        all_deduction_amount.add(Math.round(Math.round((payableAllowance / 100) * d.getPercentage()) * 100.00) / 100.00);
                        all_deduction_payable_amount.add(Math.round((payableAllowance / 100) * d.getPercentage()) * 100.00 / 100.00);
                    }
                }
//                }
            };
            double td = 0;
            for (int i = 0; i < all_deduction_amount.size(); i++) {
                td += all_deduction_amount.get(i);
            }
            double tpd = 0;
            for (int i = 0; i < all_deduction_payable_amount.size(); i++) {
                tpd += all_deduction_payable_amount.get(i);
            }
            resultMap.put("employeeType", map.get("employee_Type").toString());
            resultMap.put("TotalDeduction", Math.round((td * 100.00) / 100.00));
            resultMap.put("TotalPayableDeduction", Math.round((tpd * 100.00) / 100.00));
            resultMap.put("deductionName", all_deduction_name);
            resultMap.put("deductionAmount", all_deduction_amount);
            resultMap.put("deductionPayableAmount", all_deduction_payable_amount);

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> deductionCalculated() :: ", ex);
        }
        return resultMap;
    }

    public Map getWorkingDayOfWorker(Map map, HttpEntity leaveEntity) {
        Map resultMap = new HashMap<>();
        try {
            double workingDay = 0.0;
            Map getTotalDays;
            Long totalDays = null;
            Long actualDays = null;
            double employeeAbsents = 0.0;
            String payrollBasedOn = organizationSetupRepo.fetchWorkingDay(Long.parseLong(map.get("organization_id").toString()));

            if (payrollBasedOn != null) {
                if (payrollBasedOn.equals("Attendance & Leave") || payrollBasedOn.equals("Attendance")) {

                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getCompanyWorkingDayOfEmployee", HttpMethod.POST, leaveEntity, HashMap.class).getBody();

                    } catch (Exception ex) {
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("companyWorkingDay") && noOfDays.get("companyWorkingDay") != null) {
                            totalDays = Long.parseLong(noOfDays.get("companyWorkingDay").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("Absent") && noOfDays.get("Absent") != null) {
                            employeeAbsents = Double.parseDouble(noOfDays.get("Absent").toString());
                            LOGGER.info("Absent " + employeeAbsents + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Absents");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Absents");
                            return resultMap;
                        }
                    }
                    workingDay = totalDays - employeeAbsents;
                } else {
                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getCompanyWorkingDayOfEmployee", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                    } catch (Exception ex) {
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("companyWorkingDay") && noOfDays.get("companyWorkingDay") != null) {
                            totalDays = Long.parseLong(noOfDays.get("companyWorkingDay").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
                    workingDay = totalDays;
                }
            } else {
                LOGGER.info("Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                return resultMap;
            }
            resultMap.put("working_day", workingDay);
            resultMap.put("total_days", actualDays);
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getWorkingDayOfWorker() :: ", ex);
        }
        return resultMap;
    }

    //      Method of Deduction Cal ------>
    public Map getWorkingDay(Map map, HttpEntity leaveEntity) {
        Map resultMap = new HashMap<>();
        try {
            double workingDay = 0.0;
            Map getTotalDays;
            Long totalDays = null;
            Long actualDays = null;
            double employeeAbsents = 0.0;
            System.out.println("map1580"+" "+map.toString());
            String payrollBasedOn = organizationSetupRepo.fetchWorkingDay(Long.parseLong(map.get("organization_id").toString()));
            if (payrollBasedOn != null) {
                if (payrollBasedOn.equals("Attendance & Leave")) {
                    System.out.println("total days in payroll=== before");
                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getcompanyworkingday", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                        System.out.println("total days in payroll=== " + getTotalDays);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                     System.out.println("noOfDays 1619"+" "+noOfDays.toString());
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                            totalDays = Long.parseLong(noOfDays.get("value").toString());
                           // actualDays=Long.parseLong(noOfDays.get("value").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
                    if (!map.containsKey("flagTax")) {
                        Map absentData;
                        if (Long.parseLong(map.get("organization_id").toString()) == akron_organization_id) {
                            try {
                                absentData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getAbsentDetailsForAkron", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                LOGGER.info("Problem getting Absents if Employee with employee id " + map.get("emp_id").toString() + "" + ex);
                                resultMap.clear();
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Problem getting Employee Absents");
                                return resultMap;
                            }
                            Map absent = mapper.readValue(EncryptDecryptUtils.decrypt(absentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
                            if (absent.containsKey("status") && absent.get("status").equals("success")) {
                                if (absent.containsKey("attendance_count") && absent.get("attendance_count") != null) {
                                    workingDay = 0;
                                    LOGGER.info("Absent of Employee 0 fetching from timesheet");
                                } else {
                                    if (absent.containsKey("value") && absent.get("value") != null) {
                                        employeeAbsents = Double.parseDouble(absent.get("value").toString());
                                        LOGGER.info("Absent of Employee " + employeeAbsents + " fetching from timesheet");
                                    } else {
                                        LOGGER.info("Problem getting Absent of Employee");
                                        resultMap.clear();
                                        resultMap.put("status", "error");
                                        resultMap.put("msg", "Problem getting Absent of Employee");
                                        return resultMap;
                                    }
                                    workingDay = totalDays - employeeAbsents;
                                }
                            }
                        } else {
                            try {
                                absentData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getemployeeabsentdetails", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                LOGGER.info("Problem getting Absents if Employee with employee id " + map.get("emp_id").toString() + "" + ex);
                                resultMap.clear();
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Problem getting Employee Absents");
                                return resultMap;
                            }
                            Map absent = mapper.readValue(EncryptDecryptUtils.decrypt(absentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
                            if (absent.containsKey("status") && absent.get("status").equals("success")) {
                                if (absent.containsKey("attendance_count") && absent.get("attendance_count") != null) {
                                    workingDay = 0;
                                    LOGGER.info("Absent of Employee 0 fetching from timesheet");
                                } else {
                                    if (absent.containsKey("value") && absent.get("value") != null) {
                                        employeeAbsents = Double.parseDouble(absent.get("value").toString());
                                        LOGGER.info("Absent of Employee " + employeeAbsents + " fetching from timesheet");
                                    } else {
                                        LOGGER.info("Problem getting Absent of Employee");
                                        resultMap.clear();
                                        resultMap.put("status", "error");
                                        resultMap.put("msg", "Problem getting Absent of Employee");
                                        return resultMap;
                                    }
                                    workingDay = totalDays - employeeAbsents;
                                }
                            }

                        }
                    } else {
                        workingDay = totalDays;
                    }
                } else if (payrollBasedOn.equals("Leave")) {
                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getcompanyworkingday", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                       

                    } catch (Exception ex) {
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        ex.printStackTrace();
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                            totalDays = Long.parseLong(noOfDays.get("value").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
//                    working_day.addAndGet(Integer.parseInt(totalDays.toString()));
                    workingDay = totalDays;
//                      workingDay=30;
//                      totalDays=30L;

                } else if (payrollBasedOn.equals("Attendance")) {

                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getcompanyworkingday", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                            totalDays = Long.parseLong(noOfDays.get("value").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
                    if (!map.containsKey("flagTax")) {

                        Map absentData;
                        try {
                            absentData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getemployeeabsentdetails", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            LOGGER.info("Problem getting Absents if Employee with employee id " + map.get("emp_id").toString() + "" + ex);
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Employee Absents");
                            return resultMap;
                        }
                        Map absent = mapper.readValue(EncryptDecryptUtils.decrypt(absentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
                        if (absent.containsKey("status") && absent.get("status").equals("success")) {
                            if (absent.containsKey("attendance_count") && absent.get("attendance_count") != null) {
                                workingDay = 0;
                                LOGGER.info("Absent of Employee 0 fetching from timesheet");
                            } else {
                                if (absent.containsKey("value") && absent.get("value") != null) {
                                    employeeAbsents = Double.parseDouble(absent.get("value").toString());
                                    LOGGER.info("Absent of Employee " + employeeAbsents + " fetching from timesheet");
                                } else {
                                    LOGGER.info("Problem getting Absent of Employee");
                                    resultMap.clear();
                                    resultMap.put("status", "error");
                                    resultMap.put("msg", "Problem getting Absent of Employee");
                                    return resultMap;
                                }
                                workingDay = totalDays - employeeAbsents;
                            }

                        }
                    } else {
                        workingDay = totalDays;
                    }

                } else {
                    LOGGER.info("Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                    return resultMap;
                }
            } else {
                LOGGER.info("Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                return resultMap;
            }
            resultMap.put("working_day", workingDay);
            resultMap.put("total_days", actualDays);
           //  resultMap.put("actualDays", actualDays); 
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getWorkingDay() :: ", ex);
        }
        return resultMap;
    }

    public Map getattendanceDetails(Map map, HttpEntity leaveEntity) {
        Map resultMap = new HashMap<>();
        Map attendanceData;
        try {
            attendanceData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getpayrolldata", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
            Map attendanceDetails = mapper.readValue(EncryptDecryptUtils.decrypt(attendanceData.get("data").toString()), LinkedCaseInsensitiveMap.class);
           
            
            resultMap.put("presentDay", attendanceDetails.get("present "));
            resultMap.put("approvedLeave", attendanceDetails.get("approvedLeave"));
            resultMap.put("holidays", attendanceDetails.get("holidays"));
            resultMap.put("weekOff", attendanceDetails.get("weekOff"));
            resultMap.put("Lwp", attendanceDetails.get("Lwp"));
            resultMap.put("actualDays", attendanceDetails.get("actualDays"));
            LOGGER.info("Timesheet Attendance Details:-" + attendanceDetails);
        } catch (Exception ex) {
            LOGGER.info("Problem getting Present Days of the employee from timesheet" + ex);
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Problem getting Present Days of the employee");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getattendanceDetails() :: ", ex);
        }

        return resultMap;
    }

    public int getnumberOfDaysInMonth(int month, int year) {
        int days = YearMonth.of(year, month).lengthOfMonth();
        return days;
    }

    //fetching salary calculation data in PDF format
    @Override
    public Map calculateSalaryDataInPDF(String data) {
        Map resultMap = new HashMap<>();
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            List<LinkedHashMap> all = new ArrayList<>();
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            //Query for fetching employee allowance of salary data
            List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
//Query for fetching employee deduction of salary data
            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
//Checking the size of allowance and putting into their correspondence key
            NumberFormat myFormat = NumberFormat.getInstance();
            
            double currentAllowanceSum=0.0;
	    double currentdeductonsum=0.0;
            
            if (employeeAllowances.size() > employeeDeductions.size()) {
                for (int i = 0; i < employeeAllowances.size(); i++) {
                    LinkedHashMap combined = new LinkedHashMap();
                    LinkedCaseInsensitiveMap employeeAllowance = employeeAllowances.get(i);
                    combined.put("allowance_name", employeeAllowance.get("allowance_name"));
                    combined.put("allowance_amount", myFormat.format(employeeAllowance.get("allowance_amount")));
                    combined.put("allowance_payable_amount", myFormat.format(employeeAllowance.get("allowance_payable_amount")));
                    currentAllowanceSum += ((Number) employeeAllowance.get("allowance_payable_amount")).doubleValue();
                    try {
                        LinkedCaseInsensitiveMap employeeDeduction = employeeDeductions.get(i);
                        combined.put("deduction_name", employeeDeduction.get("deduction_name"));
                        combined.put("deduction_amount", myFormat.format(employeeDeduction.get("deduction_amount")));
//                        combined.put("deduction_payable_amount", myFormat.format(employeeDeduction.get("deduction_payable_amount")));
			  currentdeductonsum += ((Number) employeeDeduction.get("deduction_payable_amount")).doubleValue();
                        combined.put("deduction_payable_amount", employeeDeduction.get("deduction_payable_amount") != null ? myFormat.format(employeeDeduction.get("deduction_payable_amount")) : 0);
                        combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    } catch (IndexOutOfBoundsException ex) {
                        combined.put("deduction_name", "");
                        combined.put("deduction_amount", "");
                        combined.put("deduction_payable_amount", "");
                        combined.put("ytd_deduction", "");

                    }
                    all.add(combined);
//                    combined.put("allowance", allowance);
//                    combined.put("deduction", deduction);

                }
                //Checking the size of deductions and putting into their correspondence key
            } else if (employeeDeductions.size() > employeeAllowances.size()) {
                for (int i = 0; i < employeeDeductions.size(); i++) {
                    LinkedHashMap combined = new LinkedHashMap();
                    LinkedCaseInsensitiveMap employeeDeduction = employeeDeductions.get(i);
                    combined.put("deduction_name", employeeDeduction.get("deduction_name"));
                    combined.put("deduction_amount", myFormat.format(employeeDeduction.get("deduction_amount")));
                    combined.put("deduction_payable_amount", myFormat.format(employeeDeduction.get("deduction_payable_amount")));
                      currentdeductonsum += ((Number) employeeDeduction.get("deduction_payable_amount")).doubleValue();
                       
		    combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    try {
                        LinkedCaseInsensitiveMap employeeAllowance = employeeAllowances.get(i);
                        combined.put("allowance_name", employeeAllowance.get("allowance_name"));
                        combined.put("allowance_amount", myFormat.format(employeeAllowance.get("allowance_amount")));
                        combined.put("allowance_payable_amount", myFormat.format(employeeAllowance.get("allowance_payable_amount")));
                        currentAllowanceSum += ((Number) employeeAllowance.get("allowance_payable_amount")).doubleValue();
                        combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    } catch (IndexOutOfBoundsException ex) {
                        combined.put("allowance_name", "");
                        combined.put("allowance_amount", "");
                        combined.put("allowance_payable_amount", "");
//                        combined.put("allowancee", allowance);
//                        combined.put("deduction", deduction);
                    }
                    all.add(combined);
//                    combined.put("allowance", allowance);
//                    combined.put("deduction", deduction);;
                }
            } else {
                for (int i = 0; i < employeeAllowances.size(); i++) {
                    LinkedHashMap combined = new LinkedHashMap();
                    LinkedCaseInsensitiveMap employeeAllowance = employeeAllowances.get(i);
                    LinkedCaseInsensitiveMap employeeDeduction = employeeDeductions.get(i);
                    combined.put("allowance_name", employeeAllowance.get("allowance_name"));
                    combined.put("allowance_amount", employeeAllowance.get("allowance_amount"));
                    combined.put("allowance_payable_amount", employeeAllowance.get("allowance_payable_amount"));
                    currentAllowanceSum += ((Number) employeeAllowance.get("allowance_payable_amount")).doubleValue();
                    combined.put("deduction_name", employeeDeduction.get("deduction_name"));
                    combined.put("deduction_amount", myFormat.format(employeeDeduction.get("deduction_amount")));
                    currentdeductonsum += ((Number) employeeDeduction.get("deduction_payable_amount")).doubleValue();
                    combined.put("deduction_payable_amount", myFormat.format(employeeDeduction.get("deduction_payable_amount")));
                    combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    all.add(combined);
                }
            }
            //Checking the size of other allowances and putting into their correspondence key
            List<LinkedCaseInsensitiveMap> employeeOtherAllowance = employeeOtherAllowanceRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            for (int i = 0; i < employeeOtherAllowance.size(); i++) {
                LinkedCaseInsensitiveMap Other = employeeOtherAllowance.get(i);
                LinkedHashMap combined = new LinkedHashMap();
                combined.put("allowance_name", Other.get("name").toString());
                combined.put("allowance_amount", myFormat.format(Other.get("amount")));
                combined.put("allowance_payable_amount", myFormat.format(Other.get("payable_amount")));
                currentAllowanceSum += ((Number) Other.get("payable_amount")).doubleValue();
                  
                all.add(combined);
            }
            
            //Checking the size of other deduction and putting into their correspondence key
            List<LinkedCaseInsensitiveMap> employeeOtherDeduction = otherDeductionRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            for (int i = 0; i < employeeOtherDeduction.size(); i++) {
                LinkedCaseInsensitiveMap Other = employeeOtherDeduction.get(i);
                LinkedHashMap combined = new LinkedHashMap();
                combined.put("deduction_name", Other.get("deduction_name").toString());
                combined.put("deduction_payable_amount", myFormat.format(Other.get("amount")));
                all.add(combined);
            }
            List<LinkedCaseInsensitiveMap> runPayroll = runPayRollRepository.fetchArrears(Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()),
                    Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()),
                    map.get("employeeType").toString());

            //Query for fetching employee calculated salary on the bases of their employee ID
            List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Checking the size of salary Break Up(allowances & deductions ) and putting into their correspondence key
            for (int i = 0; i < salaryBreakup.size(); i++) {
                LinkedCaseInsensitiveMap salry = salaryBreakup.get(i);
                if (!runPayroll.isEmpty()) {
                    LinkedCaseInsensitiveMap arr_ded = runPayroll.get(i);
                    resultMap.put("arrearAddedd", arr_ded.get("reimburs"));
                    resultMap.put("arrearDeduction", arr_ded.get("other_deductions"));
                }
                LinkedHashMap combined = new LinkedHashMap();

                combined.put("gross_salary", myFormat.format(salry.get("gross_salary")));
                combined.put("allowance_name", "Total Earning");

                combined.put("allowance_amount", myFormat.format(salry.get("gross_salary")));
                //combined.put("allowance_payable_amount", myFormat.format(salry.get("total_payable_earning")));
                combined.put("allowance_payable_amount",myFormat.format(currentAllowanceSum));
                combined.put("deduction_name", "Total Deduction");
               // combined.put("deduction_payable_amount", ((salry.get("total_deduction"))));
	        combined.put("deduction_payable_amount", myFormat.format(currentdeductonsum));
                combined.put("ytd_total_deduction", (salry.get("ytd_total_deduction")));
                resultMap.put("payableAmount", salry.get("net_amount"));
                resultMap.put("presentDay", salry.get("present_day"));
                resultMap.put("approvedLeave", salry.get("approved_leave"));
                resultMap.put("holidays", salry.get("holidays"));
                resultMap.put("weekOff", salry.get("week_off"));
                resultMap.put("Lwp", salry.get("lwp"));
                resultMap.put("actualDay", salry.get("actual_day"));
                resultMap.put("workingDay", salry.get("working_day"));
                LOGGER.info("Present Day from timesheet ==> " + salry.get("present_day") + "  having employee id-->" + map.get("employee_id").toString());
                LOGGER.info("approvedLeave Data from timesheet ==> " + salry.get("approved_leave") + "  having employee id-->" + map.get("employee_id").toString());
                LOGGER.info("weekOff Data from timesheet ==> " + salry.get("week_off") + "  having employee id-->" + map.get("employee_id").toString());
                LOGGER.info("holidays Data from timesheet ==> " + salry.get("holidays") + "  having employee id-->" + map.get("employee_id").toString());
                LOGGER.info("Lwp Data from timesheet ==> " + salry.get("lwp") + "  having employee id-->" + map.get("employee_id").toString());
//                combined.put("num", NumberToWords.convertToWords("152005"));
                all.add(combined);
            }
            String a = "";

            for (int i = 0; i < salaryBreakup.size(); i++) {
                LinkedCaseInsensitiveMap salry = salaryBreakup.get(i);
                LinkedHashMap combined = new LinkedHashMap();
                combined.put("deduction_name", "Net Amount");
                combined.put("deduction_payable_amount", myFormat.format(salry.get("net_amount")));
                a = NumberToWords.convertToWords(salry.get("net_amount").toString());
                all.add(combined);
            }
            
            
                  // YTD Allowance Calculation Start From Here
                  
                  
            // YTD Allowance  Calculation For Current and PreVious Month
            
               List<LinkedCaseInsensitiveMap> getYTDAllowanceOFCurrentAndPriviousMonth= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getYTDAllowanceOFPreviousYear= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getYTDAllowanceOFJanToMarch= new ArrayList<>();
              
               // For Other Allowance Calculation
               
               List<LinkedCaseInsensitiveMap> getOtherAllowanceOFCuurentAndPreviousMonth= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getOtherAllowanceOFPreviousYear= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getOtherAllowanceOFJanToMarch= new ArrayList<>();
            
                  
               // if month is Jan Feb And March
               
            if(Integer.parseInt(map.get("month").toString()) ==1 || Integer.parseInt(map.get("month").toString()) ==2 ||Integer.parseInt(map.get("month").toString()) ==3){
           
               getYTDAllowanceOFPreviousYear=employeeAllowanceRepo.getYTDAllowanceOFPreviousYear(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()),Integer.parseInt(map.get("year").toString())-1);
               getYTDAllowanceOFJanToMarch=employeeAllowanceRepo.getYTDAllowanceOFJanToMarch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
               getOtherAllowanceOFPreviousYear=employeeOtherAllowanceRepo.getOtherAllowanceOFPreviousYear(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()),Integer.parseInt(map.get("year").toString())-1);
               getOtherAllowanceOFJanToMarch=employeeOtherAllowanceRepo.getOtherAllowanceOFJanToMarch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
              
                
                   //sum of Allwance  start
                   
              //  Adding Privious Year And Current year All Allowance Type Value
              
                for (Map<String, Object> allowanceOfPriviousYear : getYTDAllowanceOFPreviousYear) {
                   
                    
                       LinkedCaseInsensitiveMap tempValue = new LinkedCaseInsensitiveMap<>();
                   
                    // Retrieve the value for the particular key you want to sum
                    String previousValue = allowanceOfPriviousYear.get("allowance_name").toString();
                    Double deduction_Amount=Double.parseDouble(allowanceOfPriviousYear.get("allowance_payable_amount").toString());
                    for(Map<String, Object> deductionOfJanToMarch : getYTDAllowanceOFJanToMarch){
                    
                         double YTDAllAllowanceSum = 0.0; // Initialize the sum to zero
                         String currentValue = deductionOfJanToMarch.get("allowance_name").toString();
                          Double deduction_Amount1=Double.parseDouble(deductionOfJanToMarch.get("allowance_payable_amount").toString());
                   
                         if(currentValue.equals(previousValue)){
                             
                             YTDAllAllowanceSum=deduction_Amount+deduction_Amount1;
                          
                             tempValue.put("allowance_name", previousValue);
                             tempValue.put("allowance_payable_amount", YTDAllAllowanceSum);
                             
                            
                             getYTDAllowanceOFCurrentAndPriviousMonth.add(tempValue);
                                
                         }
                    }
                    
                    

                }
                
                
                //other allwance sum of privious Year
                
                   double sumOfPrivious = 0.0; 
                   for (Map<String, Object> allowance : getOtherAllowanceOFPreviousYear) {
                   
                    Double value = Double.parseDouble(allowance.get("payable_amount").toString());

                    if (value instanceof Number) {
                        sumOfPrivious += ((Number) value).doubleValue();
                    }
                }
                   
                   // Other Allwance Sum From Jan to March
                   
                    double sumOfJanToMarch = 0.0; 
                   for (Map<String, Object> allowance : getOtherAllowanceOFJanToMarch) {
                  
                    Double value = Double.parseDouble(allowance.get("payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        sumOfJanToMarch += ((Number) value).doubleValue();
                    }
                }
               
               // Adding Other Allwance Key in Response
               
                LinkedCaseInsensitiveMap otherallwance = new LinkedCaseInsensitiveMap<>();
                
                otherallwance.put("allowance_name", "Other Allowance");
                otherallwance.put("employee_id",Long.parseLong(map.get("employee_id").toString()));
                otherallwance.put("allowance_payable_amount",sumOfPrivious+sumOfJanToMarch);
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(otherallwance);
                
                
                // sum of All Allowance  
                
                  double YTDAllowanceSum = 0.0; 
               
                for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("allowance_payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDAllowanceSum += ((Number) value).doubleValue();
                    }
                }
               
                // Adding All Allowance sum in Response
                
                LinkedCaseInsensitiveMap YTDAllowanceSumKey = new LinkedCaseInsensitiveMap<>();
                YTDAllowanceSumKey.put("sumOfYTDAllowance",myFormat.format(YTDAllowanceSum));
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(YTDAllowanceSumKey);
                
                  // Iterate through the array and format in (18,000) the 'allowance_payable_amount' values
        for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
            if (allowance.containsKey("allowance_payable_amount")) {
                Object amountObject = allowance.get("allowance_payable_amount");
                if (amountObject instanceof Number) {
                    double amount = ((Number) amountObject).doubleValue();
                    String formattedAmount = myFormat.format(amount);
                    allowance.put("allowance_payable_amount", formattedAmount);
                }
            }
        }
                
                
                resultMap.put("ytdAllowanceCalculationPriviousAndCurrentMonth", getYTDAllowanceOFCurrentAndPriviousMonth);
                 
              
                
            }
            else{
                 getYTDAllowanceOFCurrentAndPriviousMonth=employeeAllowanceRepo.getYTDAllowanceOFCurrentAndPriviousMonth(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
                 getOtherAllowanceOFCuurentAndPreviousMonth=employeeOtherAllowanceRepo.getOtherAllowanceOFCuurentAndPreviousMonth(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
               
                 double sum = 0.0; // Initialize the sum to zero
                 
                 //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getOtherAllowanceOFCuurentAndPreviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("payable_amount").toString());

                  

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        sum += ((Number) value).doubleValue();
                    }
                }
                //  end
                
                LinkedCaseInsensitiveMap otherallwance = new LinkedCaseInsensitiveMap<>();
                
                otherallwance.put("allowance_name", "Other Allowance");
                otherallwance.put("employee_id",Long.parseLong(map.get("employee_id").toString()));
                otherallwance.put("allowance_payable_amount",sum);
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(otherallwance);
                
                  double YTDAllowanceSum = 0.0; // Initialize the sum to zero
                 
                 //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("allowance_payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDAllowanceSum += ((Number) value).doubleValue();
                    }
                }
               
                
                LinkedCaseInsensitiveMap YTDAllowanceSumKey = new LinkedCaseInsensitiveMap<>();
                YTDAllowanceSumKey.put("sumOfYTDAllowance", YTDAllowanceSum);
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(YTDAllowanceSumKey);
                
                
                resultMap.put("ytdAllowanceCalculationPriviousAndCurrentMonth", getYTDAllowanceOFCurrentAndPriviousMonth);
                    
            
            }
            
             //  End Here
             
                         //  Start From Here
            // YTD Deduction  Calculation For Current and PreVious Month
            
               List<LinkedCaseInsensitiveMap> getYTDDeductionOFCurrentAndPriviousMonth= new ArrayList<>();
                List<LinkedCaseInsensitiveMap> getYTDDeductionOFPreviousYear= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getYTDDeductionOFJanToMarch= new ArrayList<>();
              
              
            if(Integer.parseInt(map.get("month").toString()) ==1 || Integer.parseInt(map.get("month").toString()) ==2 ||Integer.parseInt(map.get("month").toString()) ==3){
               
                 getYTDDeductionOFPreviousYear=employeeDeductionRepo.getYTDDeductionOFPreviousYear(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("year").toString())-1);
                 getYTDDeductionOFJanToMarch=employeeDeductionRepo.getYTDDeductionOFJanToMarch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
                  
                    //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> deductionOfPriviousYear : getYTDDeductionOFPreviousYear) {
                   
                    
                       LinkedCaseInsensitiveMap tempValue = new LinkedCaseInsensitiveMap<>();
                   
                    // Retrieve the value for the particular key you want to sum
                    String previousValue = deductionOfPriviousYear.get("deduction_name").toString();
                    Double deduction_Amount=Double.parseDouble(deductionOfPriviousYear.get("deduction_payable_amount").toString());
                    for(Map<String, Object> deductionOfJanToMarch : getYTDDeductionOFJanToMarch){
                    
                         double YTDAllDeductionSum = 0.0; // Initialize the sum to zero
                         String currentValue = deductionOfJanToMarch.get("deduction_name").toString();
                          Double deduction_Amount1=Double.parseDouble(deductionOfJanToMarch.get("deduction_payable_amount").toString());
                   
                         if(currentValue.equals(previousValue)){
                             
                             YTDAllDeductionSum=deduction_Amount+deduction_Amount1;
                          
                             tempValue.put("deduction_name", previousValue);
                             tempValue.put("deduction_payable_amount", YTDAllDeductionSum);
                             
                             getYTDDeductionOFCurrentAndPriviousMonth.add(tempValue);
                                
                         }
                    }
                    

                }
                
                  double YTDDeductionSum = 0.0; 
                  
                   //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("deduction_payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDDeductionSum += ((Number) value).doubleValue();
                    }
                }
               
                
                LinkedCaseInsensitiveMap YTDDeductionSumKey = new LinkedCaseInsensitiveMap<>();
                YTDDeductionSumKey.put("sumOfYTDDeduction",myFormat.format(YTDDeductionSum));
                
                getYTDDeductionOFCurrentAndPriviousMonth.add(YTDDeductionSumKey);
                
               
//                             // Iterate through the array and format in (18,000) the 'allowance_payable_amount' values
//        for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
//            if (allowance.containsKey("allowance_payable_amount")) {
//                Object amountObject = allowance.get("allowance_payable_amount");
//                if (amountObject instanceof Number) {
//                    double amount = ((Number) amountObject).doubleValue();
//                    String formattedAmount = myFormat.format(amount);
//                    allowance.put("allowance_payable_amount", formattedAmount);
//                }
//            }
//        }
                
                
                resultMap.put("ytdDeductionCalculationPriviousAndCurrentMonth", getYTDDeductionOFCurrentAndPriviousMonth);
                
                 
            }
            else{
                 getYTDDeductionOFCurrentAndPriviousMonth=employeeDeductionRepo.getYTDDeductionOFCurrentAndPriviousMonth(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
                
                
                  double YTDDeductionSum = 0.0; // Initialize the sum to zero
                 
                 //sum of Allwance  start
                 
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("deduction_payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDDeductionSum += ((Number) value).doubleValue();
                    }
                }
               
                
                LinkedCaseInsensitiveMap YTDDeductionSumKey = new LinkedCaseInsensitiveMap<>();
                YTDDeductionSumKey.put("sumOfYTDDeduction", myFormat.format(YTDDeductionSum));
                
                getYTDDeductionOFCurrentAndPriviousMonth.add(YTDDeductionSumKey);
                
              
                
                //                             // Iterate through the array and format in (18,000) the 'allowance_payable_amount' values
//        for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
//            if (allowance.containsKey("allowance_payable_amount")) {
//                Object amountObject = allowance.get("allowance_payable_amount");
//                if (amountObject instanceof Number) {
//                    double amount = ((Number) amountObject).doubleValue();
//                    String formattedAmount = myFormat.format(amount);
//                    allowance.put("allowance_payable_amount", formattedAmount);
//                }
//            }
//        }
                
                
                resultMap.put("ytdDeductionCalculationPriviousAndCurrentMonth", getYTDDeductionOFCurrentAndPriviousMonth);
                    
            
            }
            
             //  End Here

            LinkedHashMap combined1 = new LinkedHashMap();
            combined1.put("allowance_name", "Amount In Words" + " :-" + " " + a);
            all.add(combined1);
            List<OrganizationSetUp> org = orgRepo.findSetUpById(orgId);
            resultMap.put("list", all);
            resultMap.put("orgAddress", org.size() > 0 ? org.get(0).getOrganization_address() : "");
            resultMap.put("status", "success");

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryDataInPDF() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map calculateEPF(String res, Long org_id, Double basic, Double payableBasic, Double payableDeduction, Double netAmount, Double gross, String data, HttpServletRequest request, Integer month, Integer year, Double working_day, Double total_days) {
        LOGGER.info("In SalaryBreakupServiceImpl -> calculateEPF() excution :: ");
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap organization_epf = orgRepo.fetchEpf(org_id);
            if (organization_epf.containsKey("epf") && organization_epf.get("epf") != null || organization_epf.get("epf") != "") {
                resultMap.put("organization_epf", organization_epf.get("epf"));
            } else {
                resultMap.put("organization_epf", "noepf");
            }
            List<Deduction> deduction = deductionRepo.findDeductionForFullTime(org_id);
            double epfValue = 0;
            if (res.equals("yesepf")) {
                for (Deduction d : deduction) {
                    if (d.getDeduction_name().equals("EPF")) {
                        int days = this.getnumberOfDaysInMonth(month + 1, year);
                        if (d.getSalary().equals("Basic")) {
                            if (basic > 15000) {
                                if (payableBasic > 15000) {
                                    epfValue = Math.round((15000 * d.getPercentage()) / 100);
                                    epfValue = (epfValue * working_day) / total_days;
                                } else {
                                    epfValue = Math.round((payableBasic * d.getPercentage()) / 100);
                                }
                            } else {
                                epfValue = Math.round((basic * d.getPercentage()) / 100);
                                epfValue = (epfValue * working_day) / total_days;
                            }
                        } else if (d.getSalary().equals("Gross")) {
                            if (gross > 15000) {
                                epfValue = Math.round((15000 * d.getPercentage()) / 100);
                                epfValue = (epfValue * working_day) / total_days;
                            } else {
                                epfValue = Math.round((gross * d.getPercentage()) / 100);
                                epfValue = (epfValue * working_day) / total_days;
                            }
                        }

                    }
                }
                payableDeduction += epfValue;
                netAmount -= epfValue;
            } else {
                if (payableDeduction != 0) {
                    payableDeduction -= epfValue;
                    netAmount += epfValue;
                }
            }
            resultMap.put("epf", Math.round(epfValue));
            resultMap.put("payableDeduction", Math.round(payableDeduction));
            resultMap.put("netAmount", Math.round(netAmount));
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateEPF() :: ", ex);
        }
        return resultMap;
    }

    // get cuurnet Salary in Employee Dashboard
    @Override
    public Map getEmployeeCurrentDeatils(String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            List<LinkedCaseInsensitiveMap> employeeDashBoard_currentDeatils = salalrybreakuprepo.fetch(Long.parseLong(map.get("employeeId").toString()), Long.parseLong(map.get("organizationId").toString()), Integer.parseInt(map.get("current_month").toString()), Integer.parseInt(map.get("current_year").toString()), map.get("employeeType").toString());
            resultMap.put("status", "success");
            resultMap.put("list", employeeDashBoard_currentDeatils);

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getEmployeeCurrentDeatils() :: ", ex);
        }
        return resultMap;
    }

//Fetching Gross Salary from offer letter and Payroll 
    @Override
    public Map getGrossSalary(String data, HttpServletRequest request) {
        LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excution..!");
        Map resultMap = new HashMap<>();
        try {
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excution :: Request Data :-" + map);
            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
            HttpEntity<?> entity = new HttpEntity<>(data, header);
//            Get Offer letter salary
            List<LinkedCaseInsensitiveMap> salarys = salaryHistoryRecord.findEmployeeSalaryById(Long.parseLong(map.get("emp_id").toString()));
            LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excution :: Response Data :-" + salarys);
            double gross_salary = 0.0;
            if (!salarys.isEmpty()) {
                Stream<LinkedCaseInsensitiveMap> optionalSalaryList;
                Optional<LinkedCaseInsensitiveMap> optionalSalary = null;
                Integer month = Integer.parseInt(map.get("month").toString()) + 1;
                Integer year = Integer.parseInt(map.get("year").toString());
                Integer selected_month = map.containsKey("selected_month") ? Integer.parseInt(map.get("selected_month").toString()) - 1 : month;
                Integer selected_year = map.containsKey("selected_year") ? Integer.parseInt(map.get("selected_year").toString()) : year;
//                if (map.containsKey("currentMonthTax") && map.get("currentMonthTax") != null) {
//                    month = Integer.parseInt(map.get("currentMonthTax").toString()) + 1;
//                    year = Integer.parseInt(map.get("currentYearTax").toString());
//                }
                try {
                    optionalSalaryList = salarys.stream().filter(sal -> (sal.get("month") != null && sal.get("year") != null) ? sal.get("month").toString().equals(map.get("month").toString()) && sal.get("year").toString().equals(map.get("year").toString()) : false);
                    optionalSalary = optionalSalaryList.count() > 0 ? optionalSalaryList.findFirst() : null;
                    optionalSalaryList.close();
                } catch (Exception ex) {
                }
                if (optionalSalary != null ? optionalSalary.isPresent() : false) {
                    LinkedCaseInsensitiveMap salary = optionalSalary.get();
                    if (salary.get("effective_date") != null) {
                        Date effectiveDate = DateUtils.convertStringToDate(salary.get("effective_date").toString(), "yyyy-MM-dd");
                        Date currentDate = DateUtils.convertStringToDate(year + "-" + month + "-01", "yyyy-MM-dd");
                        Date selectedDate = DateUtils.convertStringToDate(selected_year + "-" + selected_month + "-01", "yyyy-MM-dd");
                        if ((selectedDate.after(effectiveDate) || selectedDate.equals(effectiveDate)) && (effectiveDate.equals(currentDate) || effectiveDate.before(currentDate)) && salary.get("appraisal_salary") != null) {
                            Double appSalary = Double.parseDouble(salary.get("appraisal_salary").toString());
                            resultMap.put("salary", (appSalary > 0) ? Math.round(appSalary) : Math.round(Double.parseDouble(salary.get("gross_salary").toString())));
                        } else {
                            resultMap.put("salary", Math.round(Double.parseDouble(salary.get("gross_salary").toString())));
                        }
                    } else {
                        resultMap.put("salary", Math.round(Double.parseDouble(salary.get("gross_salary").toString())));
                    }
                } else {
                    for (int i = 0; i < salarys.size(); i++) {
                        LinkedCaseInsensitiveMap salary = salarys.get(i);
                        if (salary.get("effective_date") != null) {
                            Date effectiveDate = DateUtils.convertStringToDate(salary.get("effective_date").toString(), "yyyy-MM-dd");
                            Date currentDate = DateUtils.convertStringToDate(year + "-" + month + "-01", "yyyy-MM-dd");
                            Date selectedDate = DateUtils.convertStringToDate(selected_year + "-" + selected_month + "-01", "yyyy-MM-dd");
                            if ((selectedDate.after(effectiveDate) || selectedDate.equals(effectiveDate)) && (effectiveDate.equals(currentDate) || effectiveDate.before(currentDate)) && salary.get("appraisal_salary") != null) {
                                Double appSalary = Double.parseDouble(salary.get("appraisal_salary").toString());
                                resultMap.put("salary", (appSalary > 0) ? Math.round(appSalary) : Math.round(Double.parseDouble(salary.get("gross_salary").toString())));
                                break;
                            } else {
                                resultMap.put("salary", Math.round(Double.parseDouble(salary.get("gross_salary").toString())));
                            }
                        } else {
                            resultMap.put("salary", Math.round(Double.parseDouble(salary.get("gross_salary").toString())));
                        }
                    }
                }
            } else {
                Map offerletter = restTemplate.exchange(assessment_url + "/offerletter/salary", HttpMethod.POST, entity, HashMap.class).getBody();
                Map offerlettersalary = mapper.readValue(EncryptDecryptUtils.decrypt(offerletter.get("data").toString()), LinkedCaseInsensitiveMap.class);
                LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excution :: Response Data :-" + offerlettersalary);
                if (offerlettersalary.containsKey("salary") && offerlettersalary.get("salary") != null) {
//             if(employee.containsKey("gross_salary")&&employee.get("gross_salary")!= null){ 
                    gross_salary = Double.parseDouble(offerlettersalary.get("salary").toString());
                    resultMap.put("salary", Math.round(gross_salary));
                    LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method :: Fetching Gross Salary from offer Letter :-" + gross_salary);
//            }
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Gross salary is not available in offer letter !");
                }
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getGrossSalary() :: ", ex);
        }
        LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excuted ..!" + resultMap);
        LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excuted succesfully ..!");
        return resultMap;
    }

    public Storage getGCPStorage() throws IOException {
        InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
        Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(inputStream))
                // .setProjectId("production-303006")
                .build().getService();
        return storage;
    }

    @Override
    public Map savePdf(MultipartFile fileStream, int empId, int month, int year, Long orgId) {
        Map map = new HashMap<>();
        try {
            BlobId blobId = null;
            String PdfObject = "SalarySlip.pdf";
            Storage storage = this.getGCPStorage();
            byte[] resfile = fileStream.getBytes();
            String url = salalrybreakuprepo.getPdfurl(empId, orgId, month, year);
            if (url == null) {
                blobId = BlobId.of(bucketName, ("employee_pdf/" + year + "-" + month + "/" + empId + "/" + PdfObject));
            } else {
                blobId = BlobId.of(bucketName, url.substring(46));
            }
            BlobInfo blobInfo = BlobInfo.
                    newBuilder(blobId).
                    setCacheControl("Cache-Control: max-age=0, no-cache").
                    build();
            storage.create(blobInfo, resfile);
            String path = "employee_pdf/" + year + "-" + month + "/" + empId + "/" + PdfObject;
            salalrybreakuprepo.updateSalaryBreakUp(empId, path, orgId, month, year);
            map.put("status", "success");
        } catch (Exception excep) {
            map.put("status", "exception");
            LOGGER.error("Getting Exception: " + excep);
        }
        return map;
    }

    @Override
    public Map financialYearDropdown(String date) {
        Map resultMap = new HashMap<>();
        List<LinkedCaseInsensitiveMap> l = new ArrayList<>();
        LinkedCaseInsensitiveMap lMap;
        try {
            if (date != null) {
                LocalDate currentDate = LocalDate.now();
                LocalDate Date = LocalDate.parse(date);
                int joiningYear = Date.getYear();
                int joiningMonth = Date.getMonthValue();
                int currentYear = currentDate.getYear();
                if (joiningMonth <= 3) {
                    joiningYear--;
                }
                int noOfYears = currentYear - joiningYear;
                for (int i = 0; i <= noOfYears; i++) {
                    lMap = new LinkedCaseInsensitiveMap();
                    lMap.put("year", joiningYear + "-" + (joiningYear + 1));
                    l.add(lMap);
                    joiningYear++;
                }
                resultMap.clear();
                resultMap.put("finaicial_year", l);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("msg", "Joining Date not found");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> financialYearDropdown() :: ", ex);
        }
        return resultMap;
    }

    private static String getMonthName(int month) {

        String monthName = null;
        switch (month) {
            case 1:
                monthName = "January";
                break;
            case 2:
                monthName = "February";
                break;
            case 3:
                monthName = "March";
                break;
            case 4:
                monthName = "April";
                break;
            case 5:
                monthName = "May";
                break;
            case 6:
                monthName = "June";
                break;
            case 7:
                monthName = "July";
                break;
            case 8:
                monthName = "August";
                break;
            case 9:
                monthName = "September";
                break;
            case 10:
                monthName = "October";
                break;
            case 11:
                monthName = "November";
                break;
            case 12:
                monthName = "December";
                break;
        }

        return monthName;
    }

    @Override
    public Map financialMonthDropdown(String date, String financialYear) {
        Map resultMap = new HashMap<>();
        LinkedCaseInsensitiveMap l;
        List<LinkedCaseInsensitiveMap> month = new ArrayList<>();
        try {
            if (financialYear != null) {
                String fyYear = financialYear.split("-")[0];
                int year = Integer.parseInt(fyYear);
                if (date != null) {
                    LocalDate Date = LocalDate.parse(date);
                    LocalDate currentDate = LocalDate.now();
                    int joiningMonth = Date.getMonthValue();
                    int joiningYear = Date.getYear();
                    int currentYear = currentDate.getYear();
                    int currentMonth = currentDate.getMonthValue();
                    if (year < currentYear) {
                        if (year == joiningYear) {
                            if (joiningMonth > 3) {
                                for (int i = joiningMonth; i <= 12; i++) {
                                    l = new LinkedCaseInsensitiveMap();
                                    l.put("month", getMonthName(i) + " " + joiningYear);
                                    month.add(l);
                                }
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(1) + " " + (joiningYear + 1));
                                month.add(l);
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(2) + " " + (joiningYear + 1));
                                month.add(l);
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(3) + " " + (joiningYear + 1));
                                month.add(l);
                            } else {
                                for (int i = 4; i <= 12; i++) {
                                    l = new LinkedCaseInsensitiveMap();
                                    l.put("month", getMonthName(i) + " " + joiningYear);
                                    month.add(l);
                                }
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(1) + " " + (joiningYear + 1));
                                month.add(l);
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(2) + " " + (joiningYear + 1));
                                month.add(l);
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(3) + " " + (joiningYear + 1));
                                month.add(l);
                            }
                        } else if (year < joiningYear) {
                            for (int i = joiningMonth; i <= 3; i++) {
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(i) + " " + joiningYear);
                                month.add(l);
                            }
                        } else {
                            for (int i = 4; i <= 12; i++) {
                                l = new LinkedCaseInsensitiveMap();
                                l.put("month", getMonthName(i) + " " + year);
                                month.add(l);
                            }
                            l = new LinkedCaseInsensitiveMap();
                            l.put("month", getMonthName(1) + " " + (year + 1));
                            month.add(l);
                            l = new LinkedCaseInsensitiveMap();
                            l.put("month", getMonthName(2) + " " + (year + 1));
                            month.add(l);
                            l = new LinkedCaseInsensitiveMap();
                            l.put("month", getMonthName(3) + " " + (year + 1));
                            month.add(l);
                        }
                    } else {
                        for (int i = 4; i < currentMonth+1; i++) {
                            l = new LinkedCaseInsensitiveMap();
                            l.put("month", getMonthName(i) + " " + year);
                            month.add(l);
                        }
                    }
                    resultMap.clear();
                    resultMap.put("financial_month", month);
                    resultMap.put("status", "success");
                } else {
                    resultMap.clear();
                    resultMap.put("msg", "Joining Date not found");
                    resultMap.put("status", "error");
                }
            } else {
                resultMap.clear();
                resultMap.put("msg", "Financial Year not found");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> financialMonthDropdown() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map getPdfurl(String data) {
        Map resultMap = new HashMap<>();
        Map payrun = null;
        String url = null;
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            int year = 0;
            if (map.containsKey("financial_year") && map.get("financial_year") != null) {
                String financialYear = map.get("financial_year").toString();
                int startingYear = Integer.parseInt(financialYear.split("-")[0]);
                int endYear = Integer.parseInt(financialYear.split("-")[1]);
                if (map.containsKey("month") && map.get("month") != null) {
                    if (Integer.parseInt(map.get("month").toString()) > 3) {
                        year = startingYear;
                    } else {
                        year = endYear;
                    }
                } else {
                    resultMap.clear();
                    resultMap.put("msg", "Month not found");
                    resultMap.put("status", "error");
                    return resultMap;
                }
            } else {
                resultMap.clear();
                resultMap.put("msg", "Financial Year not found");
                resultMap.put("status", "error");
                return resultMap;
            }
            if (map.containsKey("organization_id") && map.get("organization_id") != null) {
                payrun = runPayServiceImpl.isPayrollSaved(Integer.parseInt(map.get("month").toString()), year, Long.parseLong(map.get("organization_id").toString()));
            } else {
                resultMap.clear();
                resultMap.put("msg", "Organization Id not found");
                resultMap.put("status", "error");
                return resultMap;
            }
            if (map.containsKey("employee_id") && map.get("employee_id") != null) {
                url = salalrybreakuprepo.getPdfurl(Integer.parseInt(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), year);
            } else {
                resultMap.clear();
                resultMap.put("msg", "Employee Id not found");
                resultMap.put("status", "error");
                return resultMap;
            }
            if (url == null) {
                resultMap.put("msg", "Salary Slip is not available for this month");
                resultMap.put("status", "error");
            } else {
                LOGGER.info("Employee get the pdf of " + year + "-" + map.get("month") + "with employee id " + map.get("employee_id"));
                resultMap.put("status", "success");
                resultMap.put("url", url);
            }
//            } else {
//                resultMap.put("msg", "Salary Slip is not available for this month");
//                resultMap.put("status", "error");
//            }

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getPdfurl() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map SalaryBreakUporConsultant(String map, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Map<String, Object> maps = mapper.readValue(EncryptDecryptUtils.decrypt(map), LinkedCaseInsensitiveMap.class);
            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
            int currMonth = Integer.parseInt(maps.get("month").toString());

            int currYear = Integer.parseInt(maps.get("year").toString());
//            int days = this.getnumberOfDaysInMonth(currMonth + 1, currYear);
            //int mo = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
            Double working_day = null;
            //            Leave is taken or not

            JSONObject json = new JSONObject();
            json.put("employeeId", maps.get("emp_id").toString());
            json.put("organizationId", maps.get("organization_id").toString());
            json.put("year", currYear);
            List<LinkedCaseInsensitiveMap> salaryDates;
            salaryDates = payrollSettingRepo.getSalaryDates(Long.parseLong(maps.get("organization_id").toString()));
            if (!salaryDates.isEmpty()) {
                String start_date = "0";
                String end_date = "0";
                for (LinkedCaseInsensitiveMap l : salaryDates) {
                    if (l.containsKey("start_date") && l.get("start_date") != null) {
                        start_date = l.get("start_date").toString();
                    }
                    if (l.containsKey("end_date") && l.get("end_date") != null) {
                        end_date = l.get("end_date").toString();
                    }
                }
                json.put("startDate", start_date);
                json.put("endDate", end_date);
                if (Integer.parseInt(start_date) == 1) {
                    json.put("month", currMonth + 1);
                } else {
                    if (currMonth == 0) {
                        json.put("month", 12);
                        json.put("year", currYear - 1);
                    } else {
                        json.put("month", currMonth);
                    }
                }
            } else {
                LOGGER.info("Start Date and End Date in missing");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                return resultMap;
            }
            String leaveData = EncryptDecryptUtils.encrypt(json.toString());
            HttpEntity<?> leaveEntity = new HttpEntity<>(leaveData, header);
            if (!maps.containsKey("where")) {
                currMonth += 1;
            }

//            double Gross = 0.00;
//            double payableSalary = 0.00;
//            double grossSalary = 0.0;
//            Map gross = this.getGrossSalary(map, request);
//            if (gross.containsKey("salary") && gross.get("salary") != null) {
//                grossSalary = Double.parseDouble(gross.get("salary").toString());
//                System.out.println("gross_salary for con and Intn" + grossSalary);
//                resultMap.put("GrossSalary", Math.round(grossSalary));
//                resultMap.put("professionalAmount", Math.round(grossSalary));
////                resultMap.put("totalEarningPayable", Math.round((grossSalary * working_day) / days));
////                resultMap.put("professionalpayable", Math.round((grossSalary * working_day) / days));
////                payableSalary += ((grossSalary * working_day) / days);
////                resultMap.put("professionalpayable", Math.round((grossSalary * working_day) / days));
//            }
            Map workingDay = this.getWorkingDay(maps, leaveEntity);
            LOGGER.info("Timesheet Data response show in payroll consultant Working day and total day::::" + workingDay);
            if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
//                working_day.addAndGet((int) (workingDay.get("working_day")));
                working_day = Double.parseDouble(workingDay.get("working_day").toString());
                LOGGER.info("Timesheet data like working day show in payroll::::" + working_day);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", workingDay.get("msg"));
                return resultMap;
            }
            Long days = Long.parseLong(workingDay.get("total_days").toString());
            LOGGER.info("Timesheet data like total days show in payroll::::" + days);
            if ((Integer.parseInt(maps.get("month").toString())) == currentMonth && (Integer.parseInt(maps.get("year").toString())) == currentYear) {
                days = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
            }
            double gross_salary = 0.0;
            double payableSalary = 0.0;
            LinkedCaseInsensitiveMap grossSalary = salalrybreakuprepo.getGrossSalary(Integer.parseInt(json.get("employeeId").toString()), json.get("year").toString(), json.get("month").toString());
            if (grossSalary != null && grossSalary.containsKey("gross_salary")) {
                gross_salary = Double.parseDouble(grossSalary.get("gross_salary").toString());
                payableSalary += ((gross_salary * working_day) / days);
                resultMap.put("salary", Math.round(gross_salary));
            }

            //            Check Salary Break Up is already saved or not
            Map salaryBreakupData = this.isSalaryBreakUpSavedforConsultant(String.valueOf(currMonth + 1), String.valueOf(currYear), maps.get("emp_id").toString(), maps.get("organization_id").toString(), maps.get("email_id").toString(), maps.get("employee_Type").toString(), gross_salary);
            if (!salaryBreakupData.isEmpty()) {

                Map salaryData = this.calculationBasedOnWorkingDay(working_day, days, salaryBreakupData, maps);
                if (salaryData.get("status").equals("success")) {
                    salaryBreakupData.put("employee_id", maps.get("emp_id"));
                    salaryBreakupData.put(("employeeType"), maps.get("employee_Type"));
                    salaryBreakupData.put("WorkingDay", working_day);
                    salaryBreakupData.put("salaryAvailable", "true");
                    salaryBreakupData.put("isPayrollRun", "true");
                    return salaryBreakupData;
                    
                    
                    
                }
            } else {
                salaryBreakupData.put("salaryAvailable", "false");
                salaryBreakupData.put("isPayrollRun", "true");
                return salaryBreakupData;
            }
//            Object salaryCon=salaryBreakupData.get("listSalaryBreakUp").toString();
//               for (LinkedCaseInsensitiveMap salaryConst : salaryBreakupData) {

//              if((salaryBreakupData.key )== null)
//              {
            List a = (List) salaryBreakupData.get("consultantBreakup");

            if (salaryBreakupData.containsKey("consultantBreakup") && !a.isEmpty()) {

                salaryBreakupData.put("salaryAvailable1", "true");
//                salaryBreakupData.put("WorkingDay", working_day);
                salaryBreakupData.put("status", "success");
                return salaryBreakupData;

            }

//                Gross += professional_Fee;
            int professionalFee_Max = 2501;
            double tds_forConsultant = 0.0;
            if (payableSalary > professionalFee_Max) {
                int percentage = 10;
                tds_forConsultant = (percentage * (payableSalary / 100));

            }
            Double presentDay = 0.0;
            Double approvedLeave = 0.0;
            Double holidays = 0.0;
            Double weekOff = 0.0;
            Double Lwp = 0.0;
            Double actualDays = 0.0;
            if (!maps.containsKey("flagTax")) {
                Map TimesheetattendanceDetails = this.getattendanceDetails(maps, leaveEntity);
                LOGGER.info("Input for TimesheetattendanceDetails having employee ID::::" + maps.get("emp_id").toString() + "Get TimesheetattendanceDetails from timesheet for full time employee time :::" + TimesheetattendanceDetails);
                presentDay = TimesheetattendanceDetails.containsKey("presentDay") && TimesheetattendanceDetails.get("presentDay") != null ? Double.parseDouble(TimesheetattendanceDetails.get("presentDay").toString()) : 0.0;
                approvedLeave = TimesheetattendanceDetails.containsKey("approvedLeave") && TimesheetattendanceDetails.get("approvedLeave") != null ? Double.parseDouble(TimesheetattendanceDetails.get("approvedLeave").toString()) : 0.0;
                weekOff = TimesheetattendanceDetails.containsKey("weekOff") && TimesheetattendanceDetails.get("weekOff") != null ? Double.parseDouble(TimesheetattendanceDetails.get("weekOff").toString()) : 0.0;
                holidays = TimesheetattendanceDetails.containsKey("holidays") && TimesheetattendanceDetails.get("holidays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("holidays").toString()) : 0.0;
                Lwp = TimesheetattendanceDetails.containsKey("Lwp") && TimesheetattendanceDetails.get("Lwp") != null ? Double.parseDouble(TimesheetattendanceDetails.get("Lwp").toString()) : 0.0;
                actualDays = TimesheetattendanceDetails.containsKey("actualDays") && TimesheetattendanceDetails.get("actualDays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("actualDays").toString()) : 0.0;
                LOGGER.info("Present Data from timesheet ==> " + presentDay);
                LOGGER.info("approvedLeave Data from timesheet ==> " + approvedLeave);
                LOGGER.info("weekOff Data from timesheet ==> " + weekOff);
                LOGGER.info("holidays Data from timesheet ==> " + holidays);
                LOGGER.info("Lwp Data from timesheet ==> " + Lwp);
            }
            double netpayableAmount = Math.round(((payableSalary - tds_forConsultant) * 100.00) / 100.00);
            resultMap.put("NetPayable_Amount", netpayableAmount);
            resultMap.put("totalEarning", grossSalary);
            resultMap.put("employeeType", "Consultant");
            resultMap.put("WorkingDay", working_day);
            resultMap.put("tdsConsultantDeduction", Math.round(tds_forConsultant));
            resultMap.put("TotalDeduction", Math.round(tds_forConsultant));
            resultMap.put("presentDay", presentDay);
            resultMap.put("approvedLeave", approvedLeave);
            resultMap.put("Lwp", Lwp);
            resultMap.put("holidays", holidays);
            resultMap.put("actualDay", actualDays);
            resultMap.put("weekOff", weekOff);
            resultMap.put("salaryAvailable1", "true");
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> SalaryBreakUporConsultant() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map SalaryBreakUporIntern(String map, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
          
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Map<String, Object> maps = mapper.readValue(EncryptDecryptUtils.decrypt(map), LinkedCaseInsensitiveMap.class);
            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
            int currMonth = Integer.parseInt(maps.get("month").toString());
            int currYear = Integer.parseInt(maps.get("year").toString());
//            int days = this.getnumberOfDaysInMonth(currMonth + 1, currYear);

            Double working_day = null;

            JSONObject json = new JSONObject();
            json.put("employeeId", maps.get("emp_id").toString());
            json.put("organizationId", maps.get("organization_id").toString());
            json.put("year", currYear);
            List<LinkedCaseInsensitiveMap> salaryDates;
            salaryDates = payrollSettingRepo.getSalaryDates(Long.parseLong(maps.get("organization_id").toString()));
            if (!salaryDates.isEmpty()) {
                String start_date = "0";
                String end_date = "0";
                for (LinkedCaseInsensitiveMap l : salaryDates) {
                    if (l.containsKey("start_date") && l.get("start_date") != null) {
                        start_date = l.get("start_date").toString();
                    }
                    if (l.containsKey("end_date") && l.get("end_date") != null) {
                        end_date = l.get("end_date").toString();
                    }
                }
                json.put("startDate", start_date);
                json.put("endDate", end_date);
                if (Integer.parseInt(start_date) == 1) {
                    json.put("month", currMonth + 1);
                } else {
                    if (currMonth == 0) {
                        json.put("month", 12);
                        json.put("year", currYear - 1);
                    } else {
                        json.put("month", currMonth);
                    }
                }
            } else {
                LOGGER.info("Start Date and End Date in missing");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                return resultMap;
            }
            String leaveData = EncryptDecryptUtils.encrypt(json.toString());
            HttpEntity<?> leaveEntity = new HttpEntity<>(leaveData, header);
            if (!maps.containsKey("where")) {
                currMonth += 1;
            }

            Map workingDay = this.getWorkingDay(maps, leaveEntity);
            LOGGER.info("Timesheet Data response show in payroll Intern Working day and total day::::" + workingDay);
            if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
//                working_day.addAndGet((int) (workingDay.get("working_day")));
                working_day = Double.parseDouble(workingDay.get("working_day").toString());
                LOGGER.info("Timesheet data like working day show in payroll::::" + working_day);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", workingDay.get("msg"));
                return resultMap;
            }
            Long days = Long.parseLong(workingDay.get("total_days").toString());
            LOGGER.info("Timesheet data like total days show in payroll::::" + days);
            if ((Integer.parseInt(maps.get("month").toString())) == currentMonth && (Integer.parseInt(maps.get("year").toString())) == currentYear) {
                days = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
            }
            double PayableGross = 0.00;
            double stipendGross = 0.00;
            Map gross = this.getGrossSalary(map, request);
            if (gross.containsKey("salary") && gross.get("salary") != null) {
                stipendGross = Double.parseDouble(gross.get("salary").toString());
                resultMap.put("StipendAmount", Math.round(stipendGross));
            }
            PayableGross = PayableGross + ((stipendGross * working_day) / days);
            System.out.println("currMonth3065"+" "+currMonth);

            //            Check Salary Break Up is already saved or not"
            Map salaryBreakupData = this.isSalaryBreakUpSavedforConsultant(String.valueOf(currMonth), String.valueOf(currYear), maps.get("emp_id").toString(), maps.get("organization_id").toString(), maps.get("email_id").toString(), maps.get("employee_Type").toString(), stipendGross);
           
            List a = (List) salaryBreakupData.get("internBreakup");
            if (salaryBreakupData.containsKey("internBreakup") && !a.isEmpty()) {
                salaryBreakupData.put("salaryAvailable2", "true");
               // salaryBreakupData.put("isPayrollRun", "true");
//                salaryBreakupData.put("WorkingDay", working_day);
                salaryBreakupData.put("status", "success");
                return salaryBreakupData;
            }

            Double presentDay = 0.0;
            Double approvedLeave = 0.0;
            Double holidays = 0.0;
            Double weekOff = 0.0;
            Double Lwp = 0.0;
            Double actualDays = 0.0;
            if (!maps.containsKey("flagTax")) {
                Map TimesheetattendanceDetails = this.getattendanceDetails(maps, leaveEntity);
                LOGGER.info("Input for TimesheetattendanceDetails having employee ID::::" + maps.get("emp_id").toString() + "Get TimesheetattendanceDetails from timesheet for full time employee time :::" + TimesheetattendanceDetails);
                presentDay = TimesheetattendanceDetails.containsKey("presentDay") && TimesheetattendanceDetails.get("presentDay") != null ? Double.parseDouble(TimesheetattendanceDetails.get("presentDay").toString()) : 0.0;
                approvedLeave = TimesheetattendanceDetails.containsKey("approvedLeave") && TimesheetattendanceDetails.get("approvedLeave") != null ? Double.parseDouble(TimesheetattendanceDetails.get("approvedLeave").toString()) : 0.0;
                weekOff = TimesheetattendanceDetails.containsKey("weekOff") && TimesheetattendanceDetails.get("weekOff") != null ? Double.parseDouble(TimesheetattendanceDetails.get("weekOff").toString()) : 0.0;
                holidays = TimesheetattendanceDetails.containsKey("holidays") && TimesheetattendanceDetails.get("holidays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("holidays").toString()) : 0.0;
                Lwp = TimesheetattendanceDetails.containsKey("Lwp") && TimesheetattendanceDetails.get("Lwp") != null ? Double.parseDouble(TimesheetattendanceDetails.get("Lwp").toString()) : 0.0;
                actualDays = TimesheetattendanceDetails.containsKey("actualDays") && TimesheetattendanceDetails.get("actualDays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("actualDays").toString()) : 0.0;
                LOGGER.info("Present Data from timesheet ==> " + presentDay);
                LOGGER.info("approvedLeave Data from timesheet ==> " + approvedLeave);
                LOGGER.info("weekOff Data from timesheet ==> " + weekOff);
                LOGGER.info("holidays Data from timesheet ==> " + holidays);
                LOGGER.info("Lwp Data from timesheet ==> " + Lwp);
            }
            resultMap.put("StipendpayableSalary", Math.round(stipendGross * working_day) / days);
//                    Claculate Payable ESIC
//                        double percentage = 0.75;
//                        if (PayableGross <= 21000.00) {
//                            
//                           
//                                resultMap.put("esic_InternOnGross",Math.round(Math.round((stipendGross / 100) * percentage) * 100.00) / 100.00);
//                                resultMap.put("esic_InternOnPayableGross",Math.round(Math.round((PayableGross / 100) *percentage) * 100.00) / 100.00);
//                        } 
//          double esic_InternOnPayableGross= ((PayableGross ) * percentage);
//          double netpayableAmount = Math.round(((PayableGross - esic_InternOnPayableGross) * 100.00) / 100.00);
            System.out.println(maps.get("month")+" "+"months3110");
              LinkedCaseInsensitiveMap runPayroll = runPayRollRepository.getPayrollRecord(Long.parseLong(maps.get("emp_id").toString()),
                    Long.parseLong(maps.get("organization_id").toString()),
                    Integer.parseInt(maps.get("month").toString())+1, Integer.parseInt(maps.get("year").toString()),"Intern");
           
                    if(runPayroll.size()>0){
                  resultMap.put("isPayrollRun",true);
                  salaryBreakupData.put("salaryAvailable", "true");
                    }
//            resultMap.put("isPayrollRun",true);
//            salaryBreakupData.put("salaryAvailable", "true");
            resultMap.put("InternTotalEarningAmount", stipendGross);
            resultMap.put("InternTotalEarningPayableAmount", Math.round(PayableGross));
            resultMap.put("InternGrossSalary", Math.round(stipendGross));
            resultMap.put("NetPayable_Amount", Math.round(PayableGross));
//          resultMap.put("TotalDeduction", esic_InternOnPayableGross);
            //resultMap.put("salaryAvailable2", "false");
            resultMap.put("WorkingDay", working_day);
            resultMap.put("employeeType", "Intern");
            resultMap.put("presentDay", presentDay);
            resultMap.put("approvedLeave", approvedLeave);
            resultMap.put("actualDay", actualDays);
            resultMap.put("holidays", holidays);
            resultMap.put("weekOff", weekOff);
            resultMap.put("Lwp", Lwp);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> SalaryBreakUporIntern() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map calSalaryDataInPdfForConsultant(String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class
            );
            System.out.println("pdf con" + map);
            List<LinkedHashMap> all = new ArrayList<>();
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            //Query for fetching employee allowance of salary data
            LinkedCaseInsensitiveMap employeeAllowances = employeeAllowanceRepo.fetchDataInPdfForConsultantAllowance(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Query for fetching employee deduction of salary data
            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.fetchDataInPdfforInternDeduction(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            
            String abs = "true";
//            if (employeeAllowances != null) {
//                if (abs == "true") {
//                    LinkedHashMap resultData = new LinkedHashMap();
//                   
//                    all.add(resultData);
//                }
//            }
            double allowance_payable_amount=0.0;
            int index=0;
            for (LinkedCaseInsensitiveMap d : employeeDeductions) {
                 LinkedHashMap resultData = new LinkedHashMap();
                
                if(index==0){
                
                if (employeeAllowances != null) {
                    resultData.put("allowance_name", (employeeAllowances.get("consultant_allowance_name")));
                    resultData.put("allowance_amount", (employeeAllowances.get("consultant_allowance_amount")));
                    resultData.put("allowance_payable_amount", (employeeAllowances.get("consultnat_allowance_payable_amount")));
                    allowance_payable_amount=Math.round(Double.parseDouble(employeeAllowances.get("consultnat_allowance_payable_amount").toString()));
                
                }
                }
                
                resultData.put("deduction_name", (d.get("consultant_deduction_name")));
                resultData.put("deduction_payable_amount", (d.get("consultnat_deduction_payable_amount")));
               
                all.add(resultData);
                
                index++;
            }

            //Query for fetching employee calculated salary on the bases of their employee ID
            LinkedCaseInsensitiveMap salaryBreakup = salalrybreakuprepo.fetchConsultantData(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Checking the size of salary Break Up(allowances & deductions ) and putting into their correspondence key
            String abs1 = "true";

            if (salaryBreakup != null) {
                if (abs1 == "true") {
                    LinkedHashMap resultData = new LinkedHashMap();
                    resultData.put("gross_salary", (salaryBreakup.get("gross_salary")));
                    resultData.put("allowance_name", "Total Earning");
                    resultData.put("allowance_amount", (salaryBreakup.get("gross_salary")));
                   // resultData.put("allowance_payable_amount", (salaryBreakup.get("total_payable_earning")));
                    resultData.put("allowance_payable_amount", allowance_payable_amount);
                    resultData.put("deduction_name", "Total Deduction");
                    resultData.put("deduction_payable_amount", (salaryBreakup.get("total_deduction")));
                    resultMap.put("presentDay", salaryBreakup.get("present_day"));
                    resultMap.put("approvedLeave", salaryBreakup.get("approved_leave"));
                    resultMap.put("holidays", salaryBreakup.get("holidays"));
                    resultMap.put("weekOff", salaryBreakup.get("week_off"));
                    resultMap.put("Lwp", salaryBreakup.get("lwp"));
                    resultMap.put("actualDay", salaryBreakup.get("actual_day"));
                    resultMap.put("workingDay", salaryBreakup.get("working_day"));                    
                    LOGGER.info("Present Day from timesheet ==> " + salaryBreakup.get("present_day") + "  having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("approvedLeave Data from timesheet ==> " + salaryBreakup.get("approved_leave") + "  having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("weekOff Data from timesheet ==> " + salaryBreakup.get("week_off") + "  having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("holidays Data from timesheet ==> " + salaryBreakup.get("holidays") + "  having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("Lwp Data from timesheet ==> " + salaryBreakup.get("lwp") + "  having employee id-->" + map.get("employee_id").toString());
                    all.add(resultData);
                }
            }

            String abs2 = "true";
            String a = "";
            NumberFormat myFormat = NumberFormat.getInstance();
            if (abs2 == "true") {

                LinkedHashMap resultData = new LinkedHashMap();
                resultData.put("deduction_name", "Net Amount");
                resultData.put("deduction_payable_amount", Math.round((double) salaryBreakup.get("net_amount")));
                a = NumberToWords.convertToWords(salaryBreakup.get("net_amount").toString());
                all.add(resultData);

            }            
            LinkedHashMap resultData = new LinkedHashMap();
            resultData.put("allowance_name", "Amount In Words" + " :-" + " " + a);
            all.add(resultData);
            List<OrganizationSetUp> org = orgRepo.findSetUpById(orgId);
            resultMap.put("payableAmount", salaryBreakup.get("net_amount"));
            resultMap.put("list", all);
            resultMap.put("orgAddress", org.size() > 0 ? org.get(0).getOrganization_address() : "");
             resultMap.put("status", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryDataInPDF() :: ", ex);
        }
        return resultMap;

    }

    @Override
    public Map calSalaryDataInPdfForIntern(String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class
            );
            System.out.println("pdf con" + map);
            List<LinkedHashMap> all = new ArrayList<>();
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            //Query for fetching employee allowance of salary data
            LinkedCaseInsensitiveMap employeeAllowances = employeeAllowanceRepo.fetchDataInPdfForConsultantAllowance(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            System.out.println("allwance" + employeeAllowances);
            //Query for fetching employee deduction of salary data
//            LinkedCaseInsensitiveMap employeeDeductions = employeeDeductionRepo.fetchDataInPdfforInternDeduction(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()),map.get("employeeType").toString());
//            System.out.println("deduction"+employeeDeductions);
            double allowance_payable_amount=0.0;
            String abs = "true";
            if (abs == "true") {
                if (employeeAllowances != null) {
                    if (employeeAllowances.containsKey("consultant_allowance_name") && employeeAllowances.containsKey("consultant_allowance_amount") && employeeAllowances.containsKey("consultnat_allowance_payable_amount")) {
                        LinkedHashMap resultData = new LinkedHashMap();
                        resultData.put("allowance_name", (employeeAllowances.get("consultant_allowance_name")));
                        resultData.put("allowance_amount", (employeeAllowances.get("consultant_allowance_amount")));
                        resultData.put("allowance_payable_amount", (employeeAllowances.get("consultnat_allowance_payable_amount")));
//                      resultData.put("deduction_name",(employeeDeductions.get("consultant_deduction_name")));
//                      resultData.put("deduction_payable_amount",(employeeDeductions.get("consultnat_deduction_payable_amount")));
                        allowance_payable_amount=Math.round(Double.parseDouble(employeeAllowances.get("consultnat_allowance_payable_amount").toString()));
                        all.add(resultData);
                    }
                }
            }
            //Query for fetching employee calculated salary on the bases of their employee ID
            LinkedCaseInsensitiveMap salaryBreakup = salalrybreakuprepo.fetchConsultantData(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Checking the size of salary Break Up(allowances & deductions ) and putting into their correspondence key
            if (salaryBreakup != null) {
                String abs1 = "true";
                if (abs1 == "true") {
                    LinkedHashMap resultData = new LinkedHashMap();
                    resultData.put("gross_salary", (salaryBreakup.get("gross_salary")));
                    resultData.put("allowance_name", "Total Earning");
                    resultData.put("allowance_amount", (salaryBreakup.get("gross_salary")));
                    //resultData.put("allowance_payable_amount", (salaryBreakup.get("total_payable_earning")));
                    resultData.put("allowance_payable_amount",allowance_payable_amount);
                    resultMap.put("presentDay", salaryBreakup.get("present_day"));
                    resultMap.put("approvedLeave", salaryBreakup.get("approved_leave"));
                    resultMap.put("holidays", salaryBreakup.get("holidays"));
                    resultMap.put("weekOff", salaryBreakup.get("week_off"));
                    resultMap.put("actualDay", salaryBreakup.get("actual_day"));
                    resultMap.put("Lwp", salaryBreakup.get("lwp"));
                    resultMap.put("workingDay", salaryBreakup.get("working_day"));
                    
                    LOGGER.info("Present Day from timesheet ==> " + salaryBreakup.get("present_day") + "  having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("approvedLeave Data from timesheet ==> " + salaryBreakup.get("approved_leave") + "  having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("weekOff Data from timesheet ==> " + salaryBreakup.get("week_off") + " having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("holidays Data from timesheet ==> " + salaryBreakup.get("holidays") + " having employee id-->" + map.get("employee_id").toString());
                    LOGGER.info("Lwp Data from timesheet ==> " + salaryBreakup.get("lwp") + "  having employee id-->" + map.get("employee_id").toString());
//                  resultData.put("deduction_name","Total Deduction");
//                  resultData.put("deduction_payable_amount",(salaryBreakup.get("total_deduction")));
                    all.add(resultData);
                }
                String abs2 = "true";
                String a = "";
                NumberFormat myFormat = NumberFormat.getInstance();
                if (abs2 == "true") {
                    LinkedHashMap resultData = new LinkedHashMap();
                    resultData.put("deduction_name", "Net Amount");
                    resultData.put("deduction_payable_amount", (salaryBreakup.get("net_amount")));
                    a = NumberToWords.convertToWords(salaryBreakup.get("net_amount").toString());
                    all.add(resultData);
                }
                LinkedHashMap resultData = new LinkedHashMap();
                resultData.put("allowance_name", "Amount In Words" + " :-" + " " + a);
                all.add(resultData);
            }
            List<OrganizationSetUp> org = orgRepo.findSetUpById(orgId);
            resultMap.put("payableAmount", salaryBreakup.get("net_amount"));
            resultMap.put("list", all);
            resultMap.put("orgAddress", org.size() > 0 ? org.get(0).getOrganization_address() : "");
            resultMap.put("status", "success");

        } catch (Exception ex) {
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryDataInPDF() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map fetchOrganizationEpfStatus(Long organization_id) {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap organization_epf = orgRepo.fetchEpf(organization_id);
            if (organization_epf.containsKey("epf") && organization_epf.get("epf") != null) {
                resultMap.put("organization_epf", organization_epf.get("epf"));
                resultMap.put("status", "success");
            } else {
                resultMap.put("organization_epf", "noepf");
//                 resultMap.put("status", "error");
//                 resultMap.put("msg", "Problem getting Epf Status .");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> fetchOrganizationEpfStatus() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map fetchOrganizationEsicStatus(Long organization_id) {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap organization_esic = orgRepo.fetchEsic(organization_id);
            if (organization_esic.containsKey("esic") && organization_esic.get("esic") != null) {
                resultMap.put("organization_esic", organization_esic.get("esic"));
                resultMap.put("status", "success");
            } else {
                resultMap.put("organization_epf", "no");
//                resultMap.put("status", "error");
//                resultMap.put("msg", "Problem getting Esic Status .");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> fetchOrganizationEsicStatus() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map calculateBifurcationOnGross(Map map) {
        Map resultMap = new HashMap<>();

        Date currentDate = new Date();
        LinkedCaseInsensitiveMap amount = new LinkedCaseInsensitiveMap<>();
        LinkedCaseInsensitiveMap name = new LinkedCaseInsensitiveMap<>();
        Long organization_id;
        Double gross;
        String employee_type;

        if (map.get("organization_id") != null) {
            organization_id = Long.parseLong(map.get("organization_id").toString());
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Organization Id is not available");
            return resultMap;
        }

        if (map.get("gross") != null) {
            gross = Double.parseDouble(map.get("gross").toString());
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Gross Salary is not available");
            return resultMap;
        }

        if (map.get("employee_type") != null) {
            employee_type = map.get("employee_type").toString();
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Employee Type is not available");
            return resultMap;
        }

        Double Basic = 0.0;
        Double amounts = 0.0;
        Double totalAllowance = 0.0;
        Double sectionA = 0.0;
        Double sectionB = 0.0;
        Double sectionC = 0.0;
        Double totalCtcAB = 0.0;
        Double totalCtcABC = 0.0;
        Double minIncentive = 0.0;
        Double maxIncentive = 0.0;
        
        /**
         handle for consultant employee type
         **/ 
        if(map.get("employee_type") != null && map.get("employee_type").toString().equalsIgnoreCase("consultant")){
            
            double deductionpermonthsum=0.0;
            double deductionperyearsum=0.0;
            
            amount.put("perMonth", gross);
            amount.put("perAnnum", gross * 12);
            name.put("Consultant Fees", amount);
            
            amount = new LinkedCaseInsensitiveMap<>();
            amount.put("perMonth", gross);
            amount.put("perAnnums", gross * 12);
            name.put("sectionA", amount);
            
            amount = new LinkedCaseInsensitiveMap<>();
            amount.put("perMonth", 0.0);
            amount.put("perAnnums", 0.0);
            name.put("sectionB", amount);
            
            amount = new LinkedCaseInsensitiveMap<>();
            amount.put("perMonth", gross);
            amount.put("perAnnums", gross * 12);
            name.put("totalCTCAB", amount);
            
            amount = new LinkedCaseInsensitiveMap<>();
            double tds=Math.round(Math.round((gross / 100) * 10) * 100.00) / 100.00;
            amount.put("perMonth", tds);
            amount.put("perAnnum", tds * 12);
            name.put("TDS", amount);
            
            deductionpermonthsum=tds;
            deductionperyearsum=tds*12;
            
            List<Deduction> deductions = deductionRepo.findApprovedDeduction(Long.parseLong(map.get("organization_id").toString()), map.get("employee_Type").toString());
            
             if (!deductions.isEmpty()) {
                 
                    for (Deduction d : deductions) {
                       amount = new LinkedCaseInsensitiveMap<>();
                    if(d.getAmount() ==null){
                        
                    double deduction=Math.round(Math.round((gross / 100) * d.getPercentage()) * 100.00) / 100.00;
                    amount.put("perMonth", deduction);
                    amount.put("perAnnum", deduction * 12);
                    name.put(d.getDeduction_name(), amount);
                    deductionpermonthsum=deductionpermonthsum+deduction;
                    deductionperyearsum=deductionperyearsum+(deduction*12);
                    }
                    else{
                      
                    double deduction=Math.round( d.getAmount());
                    amount.put("perMonth", deduction);
                    amount.put("perAnnum", deduction * 12);
                    name.put(d.getDeduction_name(), amount);
                    deductionpermonthsum=deductionpermonthsum+deduction;
                    deductionperyearsum=deductionperyearsum+(deduction*12);
                   
                    }
                    
                
                }
            }
             
             amount = new LinkedCaseInsensitiveMap<>();
             amount.put("perMonth", deductionpermonthsum);
             amount.put("perAnnum", deductionperyearsum);
             name.put("sectionC", amount);
              
             amount = new LinkedCaseInsensitiveMap<>();
             amount.put("perMonth", gross+deductionpermonthsum);
             amount.put("perAnnum", (gross+deductionpermonthsum)*12);
              name.put("totalCTCABC", amount);
             
            // System.out.println("AllBifurcations "+name);
             resultMap.put("data", name);
             resultMap.put("status", "success");

             return resultMap;  
            
        }
        
        List<Allowance> allowances = allowanceRepo.findApprovedAllowances(organization_id, currentDate, employee_type);
        LOGGER.info("Allowanes are" + allowances);
        if (allowances.isEmpty()) {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Allowances are not available");
            return resultMap;
        }

        Optional<Allowance> present = allowances.stream().filter(a -> a.getAllowance_name().equalsIgnoreCase("Basic Salary")).findFirst();
        if (present.isPresent()) {
            Allowance basic = present.get();
            Basic += ((gross / 100) * basic.getPercentage());
            totalAllowance += Basic;
            amount.put("perMonth", Basic);
            amount.put("perAnnum", Basic * 12);
            name.put("basicSalary", amount);
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Basic Salary is not available");
            return resultMap;
        }

        for (Allowance a : allowances) {
            amount = new LinkedCaseInsensitiveMap<>();
            if (a.getAllowance_name().equalsIgnoreCase("HRA")) {
                amounts = (Math.round((Basic / 100) * a.getPercentage()) * 100.00) / 100.00;
                totalAllowance += amounts;
                amount.put("perMonth", amounts);
                amount.put("perAnnum", amounts * 12);
                name.put("hra", amount);
            } else if (a.getAllowance_name().equalsIgnoreCase("Transport Allowance")) {
                amounts = Math.round(a.getAmount() * 100.00) / 100.00;
                totalAllowance += amounts;
                amount.put("perMonth", amounts);
                amount.put("perAnnum", amounts * 12);
                name.put("conveyance", amount);
            } else if (a.getAllowance_name().equalsIgnoreCase("Medical Allowance")) {
                amounts = Math.round(a.getAmount() * 100.00) / 100.00;
                totalAllowance += amounts;
                amount.put("perMonth", amounts);
                amount.put("perAnnum", amounts * 12);
                name.put("medicalAllowance", amount);
            } else if (a.getAllowance_name().equalsIgnoreCase("Incentive")) {
                amounts = Math.round(a.getIncentive_min() * 100.00) / 100.00;
                minIncentive = amounts;
                amount.put("perMonth", minIncentive);
                amount.put("perAnnum", minIncentive * 12);
                name.put("incentiveMin", amount);
                amount = new LinkedCaseInsensitiveMap<>();
                amounts = Math.round(a.getIncentive_max() * 100.00) / 100.00;
                maxIncentive = amounts;
                amount.put("perMonth", maxIncentive);
                amount.put("perAnnum", maxIncentive * 12);
                name.put("incentiveMax", amount);
            }
        }
        amount = new LinkedCaseInsensitiveMap<>();
        double otherAllowance = Math.round((gross - totalAllowance) * 100.00) / 100.00;
        sectionA += otherAllowance + totalAllowance;
        amount.put("perMonth", otherAllowance);
        amount.put("perAnnum", otherAllowance * 12);
        name.put("otherAllowance", amount);

        amount = new LinkedCaseInsensitiveMap<>();
        amount.put("perMonth", sectionA);
        amount.put("perAnnum", sectionA * 12);
        name.put("sectionA", amount);
        sectionB += minIncentive + maxIncentive;

        amount = new LinkedCaseInsensitiveMap<>();
        amount.put("perMonth", sectionB);
        amount.put("perAnnum", sectionB * 12);
        name.put("sectionB", amount);
        totalCtcAB += sectionA + sectionB;

        amount = new LinkedCaseInsensitiveMap<>();
        amount.put("perMonth", totalCtcAB);
        amount.put("perAnnums", totalCtcAB * 12);
        name.put("totalCTCAB", amount);

        Double employerEPF = 0.0;
        Double employerESIC = 0.0;

        List<Deduction> deductions = deductionRepo.findApprovedDeductions(organization_id, currentDate, employee_type);
        LOGGER.info("Deductions are" + deductions);

        if (deductions.isEmpty()) {
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Deductions are not available");
            return resultMap;
        }

        for (Deduction d : deductions) {
            amount = new LinkedCaseInsensitiveMap<>();
            if (d.getDeduction_name().equalsIgnoreCase("EPF")) {
                if (Basic <= 15000) {
                    amounts = Math.round(((Basic / 100) * d.getEmployer_percentage()) * 100.0) / 100.0;
                    amount.put("perMonth", amounts);
                    amount.put("perAnnum", amounts * 12);
                } else {
                    amounts = Math.round(((15000 / 100) * d.getEmployer_percentage()) * 100.0) / 100.0;
                    amount.put("perMonth", amounts);
                    amount.put("perAnnum", amounts * 12);
                }
                employerEPF = amounts;
                name.put("employersProvidentFund", amount);
            } else if (d.getDeduction_name().equalsIgnoreCase("ESIC")) {
                if (gross <= 21000) {
                    amounts = Math.round(((gross / 100) * d.getEmployer_percentage()) * 100.0) / 100.0;
                    amount.put("perMonth", amounts);
                    amount.put("perAnnum", amounts * 12);
                } else {
                    amounts = 0.0;
                    amount.put("perMonth", 0);
                    amount.put("perAnnum", 0);
                }
                employerESIC = amounts;
                name.put("employersesic", amount);
            }
        }

        amount = new LinkedCaseInsensitiveMap<>();
        sectionC += employerEPF + employerESIC;
        amount.put("perMonth", sectionC);
        amount.put("perAnnum", sectionC * 12);
        name.put("sectionC", amount);

        amount = new LinkedCaseInsensitiveMap<>();
        totalCtcABC += sectionA + sectionB + sectionC;
        amount.put("perMonth", totalCtcABC);
        amount.put("perAnnum", totalCtcABC * 12);
        name.put("totalCTCABC", amount);
        LOGGER.info("Calculation of AllBifurcations are" + name);

        resultMap.put("data", name);
        resultMap.put("status", "success");

        return resultMap;
    }
    
    @Override
     public Map calculateSalaryDataPreviousVersion(String data, HttpServletRequest request) { 
        Map resultMap = new HashMap<>();
        System.out.println("calculateSalaryDataPreviousVersioncalled3498");
        try {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            String employeeType = map.containsKey("employee_Type") ? map.get("employee_Type").toString() : map.containsKey("employeeType") ? map.get("employeeType").toString() : null;
            if (employeeType.equalsIgnoreCase("Worker")) {
                employeeType = "Worker";
            } else {
                employeeType = "Full time";
            }
             
            System.out.println("map 3602"+" "+map.toString());
            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
//            Get Leaves
            int currMonth = Integer.parseInt(map.get("month").toString());
            int currYear = Integer.parseInt(map.get("year").toString());

            JSONObject json = new JSONObject();
            json.put("employeeId", map.get("emp_id").toString());
            json.put("organizationId", map.get("organization_id").toString());
            List<LinkedCaseInsensitiveMap> salaryDates;
            System.out.println("organization_id 3261"+" "+map.get("organization_id"));
            salaryDates = payrollSettingRepo.getSalaryDates(Long.parseLong(map.get("organization_id").toString()));
            System.out.println("salaryDates 3622"+" "+salaryDates.toString());
            String start_date = "0";
            String end_date = "0";
            if (!salaryDates.isEmpty()) {
                for (LinkedCaseInsensitiveMap l : salaryDates) {
                    if (l.containsKey("start_date") && l.get("start_date") != null) {
                        start_date = l.get("start_date").toString();
                    }
                    if (l.containsKey("end_date") && l.get("end_date") != null) {
                        end_date = l.get("end_date").toString();
                    }
                }
                json.put("startDate", start_date);
                json.put("endDate", end_date);
            } else {
                LOGGER.info("Start Date and End Date in missing");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                return resultMap;
            }
            json.put("year", currYear);
            boolean checkStartDate = false;
            if (Integer.parseInt(start_date) == 1) {
                checkStartDate = true;
                json.put("month", currMonth + 1 ==13?currMonth:currMonth + 1);
            } else {
                if (currMonth == 0) {
                    json.put("month", 12);
                    json.put("year", currYear - 1);
                } else {
                    json.put("month", currMonth);
                }
            }
            String leaveData = EncryptDecryptUtils.encrypt(json.toString());
            System.out.println("leaveData" + " " + leaveData);
            HttpEntity<?> leaveEntity = new HttpEntity<>(leaveData, header);
//            AtomicInteger working_day = new AtomicInteger();
            double working_day = 0.0;
//            int days = this.getnumberOfDaysInMonth(currMonth + 1, currYear);
            if (!map.containsKey("where")) {
                currMonth += 1;
            }
//            Check Salary Break Up is already saved or not
            Map salaryBreakupData = this.isSalaryBreakUpSavedPreviousVersion(String.valueOf(currMonth), String.valueOf(currYear), map.get("emp_id").toString(), map.get("organization_id").toString(), map.get("email_id").toString(), employeeType);
             System.out.println("salaryBreakupData 12"+" "+" "+salaryBreakupData.toString());
            if (!salaryBreakupData.isEmpty()) {
                salaryBreakupData.put("salaryAvailable", "true");
//                salaryBreakupData.put("WorkingDay", working_day);
                salaryBreakupData.put("status", "success");
                return salaryBreakupData;
            }
            
            System.out.println("json.toString()3574"+" "+json.toString());

            Map workingDay = this.getWorkingDayPreviousVersion(map, leaveEntity);
            System.out.println("Input for woking days ::::" + json.toString() + "Get working days from timesheet :::" + workingDay);
            if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
//                working_day.addAndGet((int) (workingDay.get("working_day")));
                working_day = Double.parseDouble(workingDay.get("working_day").toString());
               
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", workingDay.get("msg"));
                return resultMap;
            }
            Long days = Long.parseLong(workingDay.get("total_days").toString());
//            if (map.containsKey("currentMonth") && (Integer.parseInt(map.get("currentMonth").toString()) == currentMonth)) {
//                days = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
//            }
//            if (map.containsKey("currentMonthTax") && (Integer.parseInt(map.get("currentMonthTax").toString()) == currentMonth)) {
//                days = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
//            }
//            if (map.containsKey("currentMonthTax") && (Integer.parseInt(map.get("currentMonthTax").toString()) >= Integer.parseInt(map.get("month").toString())) && Integer.parseInt(map.get("year").toString()) == currentYear) {
//                working_day = 0;
//            }

            Date newDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(newDate);
            int month = cal.get(Calendar.MONTH);
            if (map.containsKey("forProjection") && map.get("forProjection").toString().equals("true")) {
                if (month == Integer.parseInt(map.get("month").toString()))
                {
                    working_day = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
                    days = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
                }

            }
//            else if (!map.containsKey("where")) {
//
//                days = Long.parseLong(this.getnumberOfDaysInMonth(currMonth, currYear) + "");
//            }

            Double presentDay = 0.0;
            Double approvedLeave = 0.0;
            Double holidays = 0.0;
            Double weekOff = 0.0;
            Double Lwp = 0.0;
            Double actualDays = 0.0;
            if (!map.containsKey("flagTax")) {
                Map TimesheetattendanceDetails = this.getattendanceDetails(map, leaveEntity);
                LOGGER.info("Input for TimesheetattendanceDetails having employee ID --->> :" + map.get("emp_id").toString() + "Get TimesheetattendanceDetails from timesheet for full time employee time :::" + TimesheetattendanceDetails);
                presentDay = TimesheetattendanceDetails.containsKey("presentDay") && TimesheetattendanceDetails.get("presentDay") != null ? Double.parseDouble(TimesheetattendanceDetails.get("presentDay").toString()) : 0.0;
                approvedLeave = TimesheetattendanceDetails.containsKey("approvedLeave") && TimesheetattendanceDetails.get("approvedLeave") != null ? Double.parseDouble(TimesheetattendanceDetails.get("approvedLeave").toString()) : 0.0;
                weekOff = TimesheetattendanceDetails.containsKey("weekOff") && TimesheetattendanceDetails.get("weekOff") != null ? Double.parseDouble(TimesheetattendanceDetails.get("weekOff").toString()) : 0.0;
                holidays = TimesheetattendanceDetails.containsKey("holidays") && TimesheetattendanceDetails.get("holidays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("holidays").toString()) : 0.0;
                Lwp = TimesheetattendanceDetails.containsKey("Lwp") && TimesheetattendanceDetails.get("Lwp") != null ? Double.parseDouble(TimesheetattendanceDetails.get("Lwp").toString()) : 0.0;
                actualDays = TimesheetattendanceDetails.containsKey("actualDays") && TimesheetattendanceDetails.get("actualDays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("actualDays").toString()) : 0.0;

                LOGGER.info("Present Data from timesheet ==> " + presentDay);
                LOGGER.info("approvedLeave Data from timesheet ==> " + approvedLeave);
                LOGGER.info("weekOff Data from timesheet ==> " + weekOff);
                LOGGER.info("holidays Data from timesheet ==> " + holidays);
                LOGGER.info("Lwp Data from timesheet ==> " + Lwp);
            }
            List<String> all_allowance_name = new ArrayList<>();
            List<Double> all_allowance_amount = new ArrayList<>();
            List<Double> all_allowance_payable_amount = new ArrayList<>();
            Map<String, List<String>> map_allowance_name = new HashMap<>();
            Map<String, List<Double>> map_allowance_amount = new HashMap<>();
            Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
            List<String> all_deduction_name = new ArrayList<>();
            List<Double> all_deduction_amount = new ArrayList<>();
            List<Double> all_deduction_payable_amount = new ArrayList<>();
            Map<String, List<String>> map_deduction_name = new HashMap<>();
            Map<String, List<Double>> map_deduction_amount = new HashMap<>();
            Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();

            double Gross = 0.00;
            double PayableGross = 0.00;
            double PayableBasic = 0;
            double Basic = 0.00;
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.MONTH, 0);
            int mon = ca.get(Calendar.MONTH);
            double gross_salary = 0.0;
             
            
           LinkedCaseInsensitiveMap grossSalary = salalrybreakuprepo.getGrossSalary(Integer.parseInt(json.get("employeeId").toString()), map.get("year").toString(), map.get("month").toString());
           System.out.println("grossSalary 3662"+" "+json.get("employeeId")+" month"+" "+map.get("month")+" year"+map.get("year"));
            System.out.println("grossSalary 3662"+" "+grossSalary);
             if (grossSalary != null && grossSalary.containsKey("gross_salary")) {
                gross_salary = Double.parseDouble(grossSalary.get("gross_salary").toString());
                resultMap.put("salary", Math.round(gross_salary));
            }


            if (employeeType.equalsIgnoreCase("Worker")) {
                workingDay = this.getWorkingDayOfWorker(map, leaveEntity);
                System.out.println("Input for woking days ::::" + json.toString() + "Get working days from timesheet :::" + workingDay);
                if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
//                working_day.addAndGet((int) (workingDay.get("working_day")));
                    working_day = Double.parseDouble(workingDay.get("working_day").toString());
                } else {
                    resultMap.clear();
                    resultMap.put("status", "error");
                    resultMap.put("msg", workingDay.get("msg"));
                    return resultMap;
                }
                days = Long.parseLong(workingDay.get("total_days").toString());
                Gross += gross_salary;
                if (map.containsKey("currentMonthTax") && map.get("currentMonthTax").equals("true")) {
                    Gross = 0;
                    gross_salary = 0;
                    working_day = 0;
                }
                PayableGross += (gross_salary * working_day);
            } else {
                workingDay = this.getWorkingDayPreviousVersion(map, leaveEntity);
                System.out.println("Input for woking days ::::" + json.toString() + "Get working days from timesheet :::" + workingDay);
                if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
//                working_day.addAndGet((int) (workingDay.get("working_day")));
                    working_day = Double.parseDouble(workingDay.get("working_day").toString());
                } else {
                    resultMap.clear();
                    resultMap.put("status", "error");
                    resultMap.put("msg", workingDay.get("msg"));
                    return resultMap;
                }
                days = Long.parseLong(workingDay.get("total_days").toString());
                Gross += gross_salary;
                if (map.containsKey("currentMonthTax") && map.get("currentMonthTax").equals("true")) {
                    Gross = 0;
                    gross_salary = 0;
                    working_day = 0;
                }
                System.out.println("gross_salary 3707"+" "+gross_salary);
                   System.out.println("working_day 3709"+" "+working_day);
                    System.out.println("days 3709"+" "+days);
                PayableGross += (gross_salary * working_day) / days;
                System.out.println("PayableGross 3712"+" "+PayableGross);
            }

            String date = currYear + "-" + currMonth + "-" + 1;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dddd");
            Date currentDate = formatter.parse(date);

          
            List<Allowance> allowances;
            if (map.containsKey("forProjection")) {
                int selected_month = Integer.parseInt(map.get("selected_month").toString()) - 1;
                int selected_year = Integer.parseInt(map.get("selected_year").toString());
                  System.out.println("new Date(selected_year - 1900, selected_month, 28)");
                
                allowances = allowanceRepo.findApprovedAllowancess(Long.parseLong(map.get("organization_id").toString()), new Date(selected_year - 1900, selected_month, 28).toString(), employeeType);
                System.out.println("myallowances============>>>>>>>>>" + allowances + "   date====" + new Date(selected_year - 1900, selected_month, 28) + "  organization====" + map.get("organization_id").toString() + "icoming values==== " + map + " employeeeeeeeetype" + employeeType);

            } else {
                allowances = allowanceRepo.findApprovedAllowances(Long.parseLong(map.get("organization_id").toString()), new Date(currYear - 1900, currMonth - 1, 28), employeeType);
            }

//               Get Approved Deductions
//            List<Deduction> deductions = deductionRepo.findApprovedDeductions(Long.parseLong(map.get("organization_id").toString()), new Date(currYear-1900,currMonth,1));
            Map allowanceData;
            double travelAllowanceAmount = 0.0;
            Double overTimeValue = 0.0;

            if (employeeType.equalsIgnoreCase("Worker")) {
                allowanceData = this.allowanceCalculatedForWorker(map, allowances, Basic, PayableBasic, travelAllowanceAmount, Gross, PayableGross, working_day, days, request, presentDay, gross_salary, weekOff, leaveEntity);
                overTimeValue = allowanceData.containsKey("overTimeValue") ? Double.parseDouble(allowanceData.get("overTimeValue").toString()) : 0;
            } else {
                allowanceData = this.allowanceCalculated(map, allowances, Basic, PayableBasic, travelAllowanceAmount, Gross, PayableGross, working_day, days, request, presentDay, gross_salary, weekOff);
                resultMap.put("percentage_change", ((ArrayList) (((HashMap) allowanceData.get("AllowancePercentage")).get("allowancePercentage"))).get(0));
            }

            resultMap.put("percentage_change", allowanceData.get("percentage_change"));
            resultMap.put("rate", allowanceData.get("rate"));
            resultMap.put("over_time", allowanceData.get("over_time"));
            resultMap.put("wages", allowanceData.get("wages"));
            resultMap.put("salary", allowanceData.get("salary"));
            resultMap.put("payableSalary", allowanceData.get("payableSalary"));
            resultMap.put("OtherPayableAllowances", allowanceData.get("OtherPayableAllowances"));
            resultMap.put("OtherAllowances", allowanceData.get("OtherAllowances"));
            resultMap.put("AllowanceName", allowanceData.get("AllowanceName"));
            resultMap.put("AllowanceAmount", allowanceData.get("AllowanceAmount"));
            resultMap.put("AllowancePayableAmount", allowanceData.get("AllowancePayableAmount"));
            resultMap.put("AllowancePercentage", allowanceData.get("AllowancePercentage"));

            Map<String, Object> map_deduction_name_data = new HashMap<>();
            Map<String, Object> map_deduction_amount_data = new HashMap<>();
            Map<String, Object> map_deduction_payable_amount_data = new HashMap<>();
            Map deductionData;

            if (employeeType.equalsIgnoreCase("Worker")) {
                deductionData = this.deductionCalculatedForWorker(map, working_day, String.valueOf(PayableGross), allowanceData.get("payableBasic").toString(), String.valueOf(Gross), allowanceData.get("basic").toString(), currYear, currMonth, allowanceData, days);
            } else {
                deductionData = this.deductionCalculated(map, working_day, days, String.valueOf(PayableGross), allowanceData.get("payableBasic").toString(), String.valueOf(Gross), allowanceData.get("basic").toString(), currYear, currMonth, checkStartDate);
            }
            
//            System.out.println(">>>>>>>>>>>Jadu     Jadu>>>>>>>>>>>>");
//            
//            String epfValueHai=null;
//            
//            epfValueHai=salalrybreakuprepo.getEPfFlag(Integer.parseInt(map.get("emp_id").toString()),Integer.parseInt(map.get("month").toString()),Integer.parseInt(map.get("year").toString()));
//            
//            
//            
//            LOGGER.info("epfValueHai   ><<<<<<<<<<<<<<<<<<<"+epfValueHai);
//            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>. ");
//            LOGGER.info("Present Data from timesheet ==> " + presentDay);

            resultMap.put("NetAmount", Math.round(((Gross - Double.parseDouble(deductionData.get("TotalDeduction").toString())) * 100.00) / 100.00));
            resultMap.put("NetPayableAmount", Math.round(((PayableGross - Double.parseDouble(deductionData.get("TotalPayableDeduction").toString())) * 100.00) / 100.00) + travelAllowanceAmount + overTimeValue);
            map_deduction_name_data.put("deductionName", deductionData.get("deductionName"));
            map_deduction_amount_data.put("deductionAmount", deductionData.get("deductionAmount"));
            map_deduction_payable_amount_data.put("deductionPayableAmount", deductionData.get("deductionPayableAmount"));
            resultMap.put("employeeType", map.get("employee_Type").toString());
            resultMap.put("TotalDeduction", Math.round((Double.parseDouble(deductionData.get("TotalDeduction").toString()) * 100.00) / 100.00));
            resultMap.put("TotalPayableDeduction", Math.round((Double.parseDouble(deductionData.get("TotalPayableDeduction").toString()) * 100.00) / 100.00));
            resultMap.put("DeductionName", map_deduction_name_data);
            resultMap.put("DeductionAmount", map_deduction_amount_data);
            resultMap.put("DeductionPayableAmount", map_deduction_payable_amount_data);
            resultMap.put("WorkingDay", working_day);
            resultMap.put("bonus", deductionData.get("bonusAmount"));
            resultMap.put("variable", deductionData.get("variableAmount"));
            resultMap.put("totalDays", days);
            resultMap.put("epf", deductionData.get("epf"));
            resultMap.put("presentDay", presentDay);
            resultMap.put("approvedLeave", approvedLeave);
            resultMap.put("holidays", holidays);
            resultMap.put("weekOff", weekOff);
            resultMap.put("Lwp", Lwp);
            resultMap.put("actualDay", actualDays);
            resultMap.put("salaryAvailable", this.checkSalaryBreakUp(map.get("month").toString(), map.get("year").toString(), map.get("emp_id").toString(), map.get("employee_Type").toString()));
            //  resultMap.put("epfFlag", salalrybreakuprepo.getEPfFlag(Integer.parseInt(map.get("emp_id").toString()),Integer.parseInt(map.get("month").toString()),Integer.parseInt(map.get("year").toString())));
            resultMap.put("status", "success");

            //  salalrybreakuprepo.getEPfFlag(Integer.parseInt(map.get("emp_id").toString()),Integer.parseInt(map.get("month").toString()),Integer.parseInt(map.get("year").toString()));
            //  Integer.parseInt(month) + 1, Integer.parseInt(year), Integer.parseInt(empId)
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("problemcalulatesalarydata>>>>>>>>>>>" + resultMap + "exception========" + ex);
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryData() :: ", ex);
        }
        System.out.println("resultMap 3923"+" "+resultMap.toString());
        return resultMap;
    }
     
     @Override
      public Map isSalaryBreakUpSavedPreviousVersion(String month, String year, String empId, String orgId, String email, String employee_Type) {
        Map resultMap = new HashMap<>();
        List<Integer> all_allowance_id = new ArrayList<>();
        List<String> all_allowance_name = new ArrayList<>();
        List<Double> all_allowance_amount = new ArrayList<>();
        List<Double> all_allowance_payable_amount = new ArrayList<>();
        Map<String, List<Integer>> map_allowance_id = new HashMap<>();
        Map<String, List<String>> map_allowance_name = new HashMap<>();
        Map<String, List<Double>> map_allowance_amount = new HashMap<>();
        Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
        List<Integer> all_deduction_id = new ArrayList<>();
        List<Double> all_ytd_deduction = new ArrayList<>();
        List<String> all_deduction_name = new ArrayList<>();
        List<Double> all_deduction_amount = new ArrayList<>();
        List<Double> all_deduction_payable_amount = new ArrayList<>();
        Map<String, List<Integer>> map_deduction_id = new HashMap<>();
        Map<String, List<String>> map_deduction_name = new HashMap<>();
        Map<String, List<Double>> map_deduction_amount = new HashMap<>();
        Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();
        Map<String, List<Double>> map_ytd_deduction_amount = new HashMap<>();

        if (employee_Type.equalsIgnoreCase("Worker")) {
            employee_Type = "Worker";
        } else {
            employee_Type = "Full time";
        }

//        Get Employee Allowances
        List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.fetchDataInPdf(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);

        if (employeeAllowances.isEmpty()) {
            return resultMap;
        }
//        Get EmployeeDeductions
        List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.fetchDataInPdf(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);
//        Get EmployeeOther Allowances
        List<LinkedCaseInsensitiveMap> employeeOtherAllowance = employeeOtherAllowanceRepo.fetchDataInPdf(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year));
//        Get SalaryBreakup
        List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetch(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year), employee_Type);

//         Get EmployeeOtherDeductions
        List<LinkedCaseInsensitiveMap> employeeOtherDeduction = otherDeductionRepo.fetchDataInPdf(Long.parseLong(empId), Long.parseLong(orgId), Integer.parseInt(month), Integer.parseInt(year));

//        Check Employee Other Deduction is Empty or not
        if (!employeeOtherDeduction.isEmpty()) {
            for (LinkedCaseInsensitiveMap otherDeduction : employeeOtherDeduction) {
                if (otherDeduction.containsKey("deduction_name") && otherDeduction.get("deduction_name") != null) {
                    if (otherDeduction.get("deduction_name").toString() != "Income Tax") {
                        all_deduction_name.add(otherDeduction.get("deduction_name").toString());
                        all_deduction_payable_amount.add((Double) otherDeduction.get("amount"));
                    }
                }
            }
        }
//        Check Employee Allowance is Empty or not
        if (!employeeAllowances.isEmpty()) {
            for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {
                all_allowance_id.add(Integer.parseInt(allowance.get("allowance_id").toString()));
                all_allowance_name.add(allowance.get("allowance_name").toString());
                all_allowance_amount.add((Double) allowance.get("allowance_amount"));
                all_allowance_payable_amount.add((Double) allowance.get("allowance_payable_amount"));
                if (allowance.get("allowance_name").toString().equalsIgnoreCase("Reimburs/Arrears")) {
                    resultMap.put("reimburs", allowance.get("allowance_payable_amount"));
                } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Referral Allowance")) {
                    resultMap.put("referral", allowance.get("allowance_payable_amount"));
                } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Bonus/Incentive")) {
                    resultMap.put("bonus", allowance.get("allowance_payable_amount"));
                } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Overtime Allowance") || allowance.get("allowance_name").toString().equalsIgnoreCase("Overtime")) {
                    resultMap.put("overtime", allowance.get("allowance_payable_amount"));
                }
            }
            map_allowance_id.put("allowanceId", all_allowance_id);
            map_allowance_name.put("allowanceName", all_allowance_name);
            map_allowance_amount.put("allowanceAmount", all_allowance_amount);
            map_allowance_payable_amount.put("allowancePayableAmount", all_allowance_payable_amount);
            resultMap.put("AllowanceId", map_allowance_id);
            resultMap.put("AllowanceName", map_allowance_name);
            resultMap.put("AllowanceAmount", map_allowance_amount);
            resultMap.put("AllowancePayableAmount", map_allowance_payable_amount);
            resultMap.put("status", "success");
        }
//        Check Employee Deduction is Empty or not
        if (!employeeDeductions.isEmpty()) {
            for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {
                all_deduction_id.add(Integer.parseInt(deduction.get("deduction_id").toString()));
                all_deduction_name.add(deduction.get("deduction_name").toString());
                all_deduction_amount.add((Double) deduction.get("deduction_amount"));
                all_deduction_payable_amount.add((Double) deduction.get("deduction_payable_amount"));
                all_ytd_deduction.add((Double) deduction.get("ytd_deduction"));
                if (deduction.get("deduction_name").toString().equalsIgnoreCase("epf")) {
                    resultMap.put("epf", deduction.get("deduction_payable_amount"));
                } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("esic")) {
                    resultMap.put("esic", deduction.get("deduction_payable_amount"));
                } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Income Tax")) {
                    resultMap.put("tds", deduction.get("deduction_payable_amount"));
                } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Advance")) {
                    resultMap.put("advance", deduction.get("deduction_payable_amount"));
                } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Professional Tax")) {
                    resultMap.put("professional_tax", deduction.get("deduction_payable_amount"));
                } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Other Deductions")) {
                    resultMap.put("other_deduction", deduction.get("deduction_payable_amount"));
                }
            }
            map_deduction_id.put("deductionId", all_deduction_id);
            map_deduction_name.put("deductionName", all_deduction_name);
            map_deduction_amount.put("deductionAmount", all_deduction_amount);
            map_deduction_payable_amount.put("deductionPayableAmount", all_deduction_payable_amount);
            map_ytd_deduction_amount.put("ytdAmount", all_ytd_deduction);
            resultMap.put("DeductionId", map_deduction_id);
            resultMap.put("DeductionName", map_deduction_name);
            resultMap.put("DeductionAmount", map_deduction_amount);
            resultMap.put("DeductionPayableAmount", map_deduction_payable_amount);
            resultMap.put("YtdDeduction", map_ytd_deduction_amount);
        }
//        Check Employee Other Allowance is Empty or not
        if (!employeeOtherAllowance.isEmpty()) {
            for (LinkedCaseInsensitiveMap otherAllowances : employeeOtherAllowance) {
                resultMap.put("OtherAllowances", otherAllowances.get("amount"));
                resultMap.put("OtherPayableAllowances", otherAllowances.get("payable_amount"));
            }
        }
//        Check salarybreakup is empty or not
        if (!salaryBreakup.isEmpty()) {
            for (LinkedCaseInsensitiveMap salary : salaryBreakup) {
                resultMap.put("wages", salary.get("gross_salary"));
                resultMap.put("rate", salary.get("rate"));
                resultMap.put("percentage_change", salary.get("percentage_change"));
                resultMap.put("NetPayableAmount", salary.get("net_amount"));
                resultMap.put("WorkingDay", salary.get("working_day"));
                resultMap.put("TotalPayableDeduction", salary.get("total_deduction"));
                resultMap.put("salary", salary.get("total_earning"));
                resultMap.put("payableSalary", salary.get("total_payable_earning"));
                resultMap.put("payableSalaryForRunPayroll", salary.get("payable_salary"));
                resultMap.put("totalHours", salary.get("total_hours"));
                resultMap.put("overTimeHours", salary.get("over_time"));
                resultMap.put("actual_day", salary.get("actual_day") != null ? salary.get("actual_day") : 0);
                resultMap.put("approved_leave", salary.get("approved_leave") != null ? salary.get("approved_leave") : 0);
                resultMap.put("holidays", salary.get("holidays") != null ? salary.get("holidays") : 0);
                resultMap.put("present_day", salary.get("present_day") != null ? salary.get("present_day") : 0);
                resultMap.put("week_off", salary.get("week_off") != null ? salary.get("week_off") : 0);
                resultMap.put("ytd_total_deduction", salary.get("ytd_total_deduction") != null ? salary.get("ytd_total_deduction") : 0);
                resultMap.put("lwp", salary.get("lwp") != null ? salary.get("lwp") : 0);
                resultMap.put("epfFlag", salary.get("epf") != null ? salary.get("epf") : "No");
                resultMap.put("total_day", salary.get("total_day") != null ? salary.get("total_day") : 0);
            }
        }
        return resultMap;
    }
      
      @Override
      public Map getWorkingDayPreviousVersion( Map map, HttpEntity leaveEntity) {
        Map resultMap = new HashMap<>();
        try {
            double workingDay = 0.0;
            Map getTotalDays;
            Long totalDays = null;
            Long actualDays = null;
            double employeeAbsents = 0.0;
            String payrollBasedOn = organizationSetupRepo.fetchWorkingDay(Long.parseLong(map.get("organization_id").toString()));
            if (payrollBasedOn != null) {
                if (payrollBasedOn.equals("Attendance & Leave")) {
                    System.out.println("total days in payroll=== before");
                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getcompanyworkingday", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                        
                        System.out.println("total days in payroll===1790 " + getTotalDays);
                    } catch (Exception ex) {
                        
                        ex.printStackTrace();
                                LOGGER.info("Problem getting Total no. of days from timesheet 134" + ex);
                        
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                            totalDays = Long.parseLong(noOfDays.get("value").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
                    if (!map.containsKey("flagTax")) {
                        Map absentData;
                        if (Long.parseLong(map.get("organization_id").toString()) == akron_organization_id) {
                            try {
                                absentData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getAbsentDetailsForAkron", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                            } catch (Exception ex) {
                                LOGGER.info("Problem getting Absents if Employee with employee id " + map.get("emp_id").toString() + "" + ex);
                                resultMap.clear();
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Problem getting Employee Absents");
                                return resultMap;
                            }
                            Map absent = mapper.readValue(EncryptDecryptUtils.decrypt(absentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
                            if (absent.containsKey("status") && absent.get("status").equals("success")) {
                                if (absent.containsKey("attendance_count") && absent.get("attendance_count") != null) {
                                    workingDay = 0;
                                    LOGGER.info("Absent of Employee 0 fetching from timesheet");
                                } else {
                                    if (absent.containsKey("value") && absent.get("value") != null) {
                                        employeeAbsents = Double.parseDouble(absent.get("value").toString());
                                        LOGGER.info("Absent of Employee " + employeeAbsents + " fetching from timesheet");
                                    } else {
                                        LOGGER.info("Problem getting Absent of Employee");
                                        resultMap.clear();
                                        resultMap.put("status", "error");
                                        resultMap.put("msg", "Problem getting Absent of Employee");
                                        return resultMap;
                                    }
                                    workingDay = totalDays - employeeAbsents;
                                }
                            }
                        } else {
                            try {
                                absentData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getemployeeabsentdetails", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                            } catch (Exception ex) {
                                LOGGER.info("Problem getting Absents if Employee with employee id " + map.get("emp_id").toString() + "" + ex);
                                resultMap.clear();
                                resultMap.put("status", "error");
                                resultMap.put("msg", "Problem getting Employee Absents");
                                return resultMap;
                            }
                            Map absent = mapper.readValue(EncryptDecryptUtils.decrypt(absentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
                            if (absent.containsKey("status") && absent.get("status").equals("success")) {
                                if (absent.containsKey("attendance_count") && absent.get("attendance_count") != null) {
                                    workingDay = 0;
                                    LOGGER.info("Absent of Employee 0 fetching from timesheet");
                                } else {
                                    if (absent.containsKey("value") && absent.get("value") != null) {
                                        employeeAbsents = Double.parseDouble(absent.get("value").toString());
                                        LOGGER.info("Absent of Employee " + employeeAbsents + " fetching from timesheet");
                                    } else {
                                        LOGGER.info("Problem getting Absent of Employee");
                                        resultMap.clear();
                                        resultMap.put("status", "error");
                                        resultMap.put("msg", "Problem getting Absent of Employee");
                                        return resultMap;
                                    }
                                    workingDay = totalDays - employeeAbsents;
                                }
                            }

                        }
                    } else {
                        workingDay = totalDays;
                    }
                } else if (payrollBasedOn.equals("Leave")) {
                    try {

                        
                        System.out.println(reimburshment_url+"/payrollleavecount/getcompanyworkingday");
                        System.out.println("leaveEntity"+""+leaveEntity);
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getcompanyworkingday", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
 
                    } catch (Exception ex) {
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        ex.printStackTrace();
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                            totalDays = Long.parseLong(noOfDays.get("value").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
                   // working_day.addAndGet(Integer.parseInt(totalDays.toString()));

                    workingDay = totalDays;
                } else if (payrollBasedOn.equals("Attendance")) {

                    try {
                        getTotalDays = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getcompanyworkingday", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                    } catch (Exception ex) {
                        LOGGER.info("Problem getting Total no. of days from timesheet" + ex);
                        resultMap.clear();
                        resultMap.put("status", "error");
                        resultMap.put("msg", "Problem getting Total no. of days");
                        return resultMap;
                    }
                    Map noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()), LinkedCaseInsensitiveMap.class);
                    if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
                        if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                            totalDays = Long.parseLong(noOfDays.get("value").toString());
                            LOGGER.info("Total No. of Days" + totalDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Total No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Total No. of Days");
                            return resultMap;
                        }
                        if (noOfDays.containsKey("actual_duration") && noOfDays.get("actual_duration") != null) {
                            actualDays = Long.parseLong(noOfDays.get("actual_duration").toString());
                            LOGGER.info("Actual No. of Days" + actualDays + " fetching from timesheet");
                        } else {
                            LOGGER.info("Problem getting Actual No. of Days");
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Actual No. of Days");
                            return resultMap;
                        }
                    }
                    if (!map.containsKey("flagTax")) {

                        Map absentData;
                        try {
                            absentData = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getemployeeabsentdetails", HttpMethod.POST, leaveEntity, HashMap.class).getBody();
                        } catch (Exception ex) {
                            LOGGER.info("Problem getting Absents if Employee with employee id " + map.get("emp_id").toString() + "" + ex);
                            resultMap.clear();
                            resultMap.put("status", "error");
                            resultMap.put("msg", "Problem getting Employee Absents");
                            return resultMap;
                        }
                        Map absent = mapper.readValue(EncryptDecryptUtils.decrypt(absentData.get("data").toString()), LinkedCaseInsensitiveMap.class);
                        if (absent.containsKey("status") && absent.get("status").equals("success")) {
                            if (absent.containsKey("attendance_count") && absent.get("attendance_count") != null) {
                                workingDay = 0;
                                LOGGER.info("Absent of Employee 0 fetching from timesheet");
                            } else {
                                if (absent.containsKey("value") && absent.get("value") != null) {
                                    employeeAbsents = Double.parseDouble(absent.get("value").toString());
                                    LOGGER.info("Absent of Employee " + employeeAbsents + " fetching from timesheet");
                                } else {
                                    LOGGER.info("Problem getting Absent of Employee");
                                    resultMap.clear();
                                    resultMap.put("status", "error");
                                    resultMap.put("msg", "Problem getting Absent of Employee");
                                    return resultMap;
                                }
                                workingDay = totalDays - employeeAbsents;
                            }

                        }
                    } else {
                        workingDay = totalDays;
                    }

                } else {
                    LOGGER.info("Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                    return resultMap;
                }
            } else {
                LOGGER.info("Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly Choose Payroll Based on Attendance or Leave from OrganizationSetUp");
                return resultMap;
            }
//            resultMap.put("working_day", workingDay);
//            resultMap.put("total_days", actualDays);
            resultMap.put("working_day", 30);
            resultMap.put("total_days", 30);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getWorkingDay() :: ", ex);
        }
        return resultMap;
    }
      
      
      public Map getPdfData(String data) {
          
        Map resultMap = new HashMap<>();
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
           
            System.out.println(" map  >>>>>>>>."+map);
                  
            LinkedCaseInsensitiveMap runpayrollcheck=runPayRollRepository.employeeWorkingDay(Long.parseLong(map.get("employee_id").toString()), (int)map.get("month"),(int) map.get("year"),Long.parseLong(map.get("organization_id").toString()));
                        
            if(runpayrollcheck ==null){
                resultMap.put("status", "error");
                return resultMap;
            }
            
            LinkedCaseInsensitiveMap employeeType=runPayRollRepository.employeeTypeOfEmployee(Long.parseLong(map.get("employee_id").toString()), (int)map.get("month"),(int) map.get("year"));
            
            map.put("employeeType", employeeType.get("employee_type").toString());
            
            if(map.get("employeeType").toString().equalsIgnoreCase("full time") || map.get("employeeType").toString().equalsIgnoreCase("probation") || map.get("employeeType").toString().equalsIgnoreCase("permanent") || map.get("employeeType").toString().equalsIgnoreCase("worker")){
           
               resultMap = this.calculateSalaryDataInPDFForMobile(data,employeeType);
            }
            else if(map.get("employeeType").toString().equalsIgnoreCase("consultant") || map.get("employeeType").toString().equalsIgnoreCase("contract")){
            
               resultMap = this.calSalaryDataInPdfForConsultantForMobile(data,employeeType);
            }
            else if(map.get("employeeType").toString().equalsIgnoreCase("intern")){
            
               resultMap = this.calSalaryDataInPdfForInternForMobile(data,employeeType);
            }
            
        }
        catch(Exception e){
            
            e.printStackTrace();
        }
        
        return resultMap;
        }
     //fetching salary calculation data in PDF format
    
    public Map calculateSalaryDataInPDFForMobile(String data,LinkedCaseInsensitiveMap employeeType) {
        Map resultMap = new HashMap<>();
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            List<LinkedHashMap> all = new ArrayList<>();
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            //Query for fetching employee allowance of salary data
           
            System.out.println("Map value >>>>>>>>>>>>>>>");
            System.out.println(map);
            map.put("employeeType", employeeType.get("employee_type").toString());
            if(map.get("employeeType").equals("Permanent") || map.get("employeeType").equals("Probation")){
                map.put("employeeType", "Full time");
            }
            
            List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
//Query for fetching employee deduction of salary data
            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
//Checking the size of allowance and putting into their correspondence key
            NumberFormat myFormat = NumberFormat.getInstance();
            
            double currentAllowanceSum=0.0;
	    double currentdeductonsum=0.0;
            
            List<LinkedCaseInsensitiveMap> employeeOtherAllowance = employeeOtherAllowanceRepo.getOtherAllowane(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
          
            employeeAllowances.addAll(employeeOtherAllowance);
            
            if (employeeAllowances.size() > employeeDeductions.size()) {                              
                for (int i = 0; i < employeeAllowances.size(); i++) {
                    LinkedHashMap combined = new LinkedHashMap();
                    LinkedCaseInsensitiveMap employeeAllowance = employeeAllowances.get(i);
                    combined.put("allowance_name", employeeAllowance.get("allowance_name"));
                    combined.put("allowance_amount", myFormat.format(employeeAllowance.get("allowance_amount")));
                    combined.put("allowance_payable_amount", myFormat.format(employeeAllowance.get("allowance_payable_amount")));
                    currentAllowanceSum += ((Number) employeeAllowance.get("allowance_payable_amount")).doubleValue();
                    try {
                        LinkedCaseInsensitiveMap employeeDeduction = employeeDeductions.get(i);
                        combined.put("deduction_name", employeeDeduction.get("deduction_name"));
                        combined.put("deduction_amount", myFormat.format(employeeDeduction.get("deduction_amount")));
//                        combined.put("deduction_payable_amount", myFormat.format(employeeDeduction.get("deduction_payable_amount")));
			  currentdeductonsum += ((Number) employeeDeduction.get("deduction_payable_amount")).doubleValue();
                        combined.put("deduction_payable_amount", employeeDeduction.get("deduction_payable_amount") != null ? myFormat.format(employeeDeduction.get("deduction_payable_amount")) : 0);
                        combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    } catch (IndexOutOfBoundsException ex) {
                        combined.put("deduction_name", "");
                        combined.put("deduction_amount", "");
                        combined.put("deduction_payable_amount", "");
                        combined.put("ytd_deduction", "");

                    }
                    all.add(combined);
//                    combined.put("allowance", allowance);
//                    combined.put("deduction", deduction);

                }
                
                //Checking the size of deductions and putting into their correspondence key
            } else if (employeeDeductions.size() > employeeAllowances.size()) {                             
                for (int i = 0; i < employeeDeductions.size(); i++) {
                    LinkedHashMap combined = new LinkedHashMap();
                    LinkedCaseInsensitiveMap employeeDeduction = employeeDeductions.get(i);
                    combined.put("deduction_name", employeeDeduction.get("deduction_name"));
                    combined.put("deduction_amount", myFormat.format(employeeDeduction.get("deduction_amount")));
                    combined.put("deduction_payable_amount", myFormat.format(employeeDeduction.get("deduction_payable_amount")));
                      currentdeductonsum += ((Number) employeeDeduction.get("deduction_payable_amount")).doubleValue();
                       
		    combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    try {
                        LinkedCaseInsensitiveMap employeeAllowance = employeeAllowances.get(i);
                        combined.put("allowance_name", employeeAllowance.get("allowance_name"));
                        combined.put("allowance_amount", myFormat.format(employeeAllowance.get("allowance_amount")));
                        combined.put("allowance_payable_amount", myFormat.format(employeeAllowance.get("allowance_payable_amount")));
                        currentAllowanceSum += ((Number) employeeAllowance.get("allowance_payable_amount")).doubleValue();
                        combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    } catch (IndexOutOfBoundsException ex) {
                        combined.put("allowance_name", "");
                        combined.put("allowance_amount", "");
                        combined.put("allowance_payable_amount", "");
                    }
                    all.add(combined);
                }
            } else {            
                for (int i = 0; i < employeeAllowances.size(); i++) {
                    LinkedHashMap combined = new LinkedHashMap();
                    LinkedCaseInsensitiveMap employeeAllowance = employeeAllowances.get(i);
                    LinkedCaseInsensitiveMap employeeDeduction = employeeDeductions.get(i);
                    combined.put("allowance_name", employeeAllowance.get("allowance_name"));
                    combined.put("allowance_amount",myFormat.format(employeeAllowance.get("allowance_amount")));
                    combined.put("allowance_payable_amount",myFormat.format( employeeAllowance.get("allowance_payable_amount")));
                    currentAllowanceSum += ((Number) employeeAllowance.get("allowance_payable_amount")).doubleValue();
                    combined.put("deduction_name", employeeDeduction.get("deduction_name"));
                    combined.put("deduction_amount", myFormat.format(employeeDeduction.get("deduction_amount")));
                    currentdeductonsum += ((Number) employeeDeduction.get("deduction_payable_amount")).doubleValue();
                    combined.put("deduction_payable_amount", myFormat.format(employeeDeduction.get("deduction_payable_amount")));
                    combined.put("ytd_deduction", employeeDeduction.get("ytd_deduction") != null ? myFormat.format(employeeDeduction.get("ytd_deduction")) : 0);
                    all.add(combined);
                }
            }

            
            //Checking the size of other deduction and putting into their correspondence key
            List<LinkedCaseInsensitiveMap> employeeOtherDeduction = otherDeductionRepo.fetchDataInPdf(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            for (int i = 0; i < employeeOtherDeduction.size(); i++) {
                LinkedCaseInsensitiveMap Other = employeeOtherDeduction.get(i);
                LinkedHashMap combined = new LinkedHashMap();
                combined.put("deduction_name", Other.get("deduction_name").toString());
                combined.put("deduction_payable_amount", myFormat.format(Other.get("amount")));
                all.add(combined);
            }
            List<LinkedCaseInsensitiveMap> runPayroll = runPayRollRepository.fetchArrears(Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()),
                    Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()),
                    map.get("employeeType").toString());

            //Query for fetching employee calculated salary on the bases of their employee ID
            List<LinkedCaseInsensitiveMap> salaryBreakup = salalrybreakuprepo.fetch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Checking the size of salary Break Up(allowances & deductions ) and putting into their correspondence key
             Map attendanceDetails = new HashMap<>();
            for (int i = 0; i < salaryBreakup.size(); i++) {
                LinkedCaseInsensitiveMap salry = salaryBreakup.get(i);
                if (!runPayroll.isEmpty()) {
                    LinkedCaseInsensitiveMap arr_ded = runPayroll.get(i);
                    attendanceDetails.put("arrearAddedd",arr_ded.get("reimburs")!=null? myFormat.format(arr_ded.get("reimburs")):0);
                    attendanceDetails.put("arrearDeduction", myFormat.format(arr_ded.get("other_deductions")));
                }
                LinkedHashMap combined = new LinkedHashMap();

                combined.put("gross_salary", myFormat.format(salry.get("gross_salary")));
                combined.put("allowance_name", "Total Earning");

                combined.put("allowance_amount", myFormat.format(salry.get("gross_salary")));
                //combined.put("allowance_payable_amount", myFormat.format(salry.get("total_payable_earning")));
                combined.put("allowance_payable_amount",myFormat.format(currentAllowanceSum));
                combined.put("deduction_name", "Total Deduction");
               // combined.put("deduction_payable_amount", ((salry.get("total_deduction"))));
	        combined.put("deduction_payable_amount", myFormat.format(currentdeductonsum));
                combined.put("ytd_total_deduction", (salry.get("ytd_total_deduction")));
                attendanceDetails.put("payableAmount", salry.get("net_amount"));
                attendanceDetails.put("presentDay", myFormat.format(salry.get("present_day")));
                attendanceDetails.put("approvedLeave", myFormat.format(salry.get("approved_leave")));
                attendanceDetails.put("holidays", myFormat.format(salry.get("holidays")));
                attendanceDetails.put("weekOff", myFormat.format(salry.get("week_off")));
                attendanceDetails.put("Lwp", myFormat.format(salry.get("lwp")));               
                 if(map.get("employeeType").toString().equalsIgnoreCase("worker")){
                 attendanceDetails.put("actualDay", myFormat.format(Double.parseDouble(salry.get("actual_day").toString()) - Double.parseDouble(salry.get("week_off").toString())));  
                }
                else{
                  attendanceDetails.put("actualDay", myFormat.format(salry.get("actual_day")));    
                }
               
                attendanceDetails.put("workingDay", myFormat.format(salry.get("working_day")));
               
             //               combined.put("num", NumberToWords.convertToWords("152005"));
                all.add(combined);
            }
            String a = "";

            for (int i = 0; i < salaryBreakup.size(); i++) {
                LinkedCaseInsensitiveMap salry = salaryBreakup.get(i);
                LinkedHashMap combined = new LinkedHashMap();
                
                
                
                if(Objects.equals(Long.parseLong(map.get("organization_id").toString()), Long.parseLong(orgIdForSymbol))){
                      combined.put("deduction_name", "Net Amount : AED ");
                }
                else{
                    combined.put("deduction_name", "Net Amount : Rs ");
                }
                
                combined.put("deduction_payable_amount", myFormat.format(salry.get("net_amount")));
                a = NumberToWords.convertToWords(salry.get("net_amount").toString());
                all.add(combined);
            }
                       
               /**
                YTD Allowance Calculation Start From Here
                **/
               LinkedCaseInsensitiveMap org = orgRepo.getOrganizationAddress(orgId);  
               
               if(org.get("template").toString().equalsIgnoreCase("genconnect")){
                   
                  
            // YTD Allowance  Calculation For Current and PreVious Month
            
               List<LinkedCaseInsensitiveMap> getYTDAllowanceOFCurrentAndPriviousMonth= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getYTDAllowanceOFPreviousYear= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getYTDAllowanceOFJanToMarch= new ArrayList<>();
               
               // For Other Allowance Calculation
               
               List<LinkedCaseInsensitiveMap> getOtherAllowanceOFCuurentAndPreviousMonth= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getOtherAllowanceOFPreviousYear= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getOtherAllowanceOFJanToMarch= new ArrayList<>();
            
                  
               // if month is Jan Feb And March
               
            if(Integer.parseInt(map.get("month").toString()) ==1 || Integer.parseInt(map.get("month").toString()) ==2 ||Integer.parseInt(map.get("month").toString()) ==3){
           
               getYTDAllowanceOFPreviousYear=employeeAllowanceRepo.getYTDAllowanceOFPreviousYear(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()),Integer.parseInt(map.get("year").toString())-1);
               getYTDAllowanceOFJanToMarch=employeeAllowanceRepo.getYTDAllowanceOFJanToMarch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
               getOtherAllowanceOFPreviousYear=employeeOtherAllowanceRepo.getOtherAllowanceOFPreviousYear(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()),Integer.parseInt(map.get("year").toString())-1);
               getOtherAllowanceOFJanToMarch=employeeOtherAllowanceRepo.getOtherAllowanceOFJanToMarch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
             
                   //sum of Allwance  start
                   
              //  Adding Privious Year And Current year All Allowance Type Value
              
                for (Map<String, Object> allowanceOfPriviousYear : getYTDAllowanceOFPreviousYear) {
                   
                    
                       LinkedCaseInsensitiveMap tempValue = new LinkedCaseInsensitiveMap<>();
                   
                    // Retrieve the value for the particular key you want to sum

                    String previousValue =allowanceOfPriviousYear.get("allowance_name") !=null ?allowanceOfPriviousYear.get("allowance_name").toString():null;
                    Double deduction_Amount=allowanceOfPriviousYear.get("allowance_payable_amount") !=null ? Double.parseDouble(allowanceOfPriviousYear.get("allowance_payable_amount").toString()):0.0;
                    for(Map<String, Object> deductionOfJanToMarch : getYTDAllowanceOFJanToMarch){

                    
                         double YTDAllAllowanceSum = 0.0; // Initialize the sum to zero
                         String currentValue = deductionOfJanToMarch.get("allowance_name").toString();
                          Double deduction_Amount1=Double.parseDouble(deductionOfJanToMarch.get("allowance_payable_amount").toString());
                   
                         if(currentValue.equals(previousValue)){
                             
                             YTDAllAllowanceSum=deduction_Amount+deduction_Amount1;
                          
                             tempValue.put("allowance_name", previousValue);
                             tempValue.put("allowance_payable_amount", YTDAllAllowanceSum);
                             
                             System.out.println(previousValue);
                             System.out.println(YTDAllAllowanceSum);
                             
                             getYTDAllowanceOFCurrentAndPriviousMonth.add(tempValue);
                                
                         }
                    }
                    
                    

                }
                
                
                //other allwance sum of privious Year
                
                   double sumOfPrivious = 0.0; 
                   for (Map<String, Object> allowance : getOtherAllowanceOFPreviousYear) {
                   
                    Double value =allowance.get("payable_amount") !=null? Double.parseDouble(allowance.get("payable_amount").toString()):0.0;

                    if (value instanceof Number) {
                        sumOfPrivious += ((Number) value).doubleValue();
                    }
                }
                   
                   // Other Allwance Sum From Jan to March
                   
                    double sumOfJanToMarch = 0.0; 
                   for (Map<String, Object> allowance : getOtherAllowanceOFJanToMarch) {
                  
                    Double value = Double.parseDouble(allowance.get("payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        sumOfJanToMarch += ((Number) value).doubleValue();
                    }
                }
               
               // Adding Other Allwance Key in Response
               
                LinkedCaseInsensitiveMap otherallwance = new LinkedCaseInsensitiveMap<>();
                
                otherallwance.put("allowance_name", "Other Allowance");
                otherallwance.put("employee_id",Long.parseLong(map.get("employee_id").toString()));
                otherallwance.put("allowance_payable_amount",Math.round(sumOfPrivious+sumOfJanToMarch));
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(otherallwance);
                
                
                // sum of All Allowance  
                
                  double YTDAllowanceSum = 0.0; 
               
                for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("allowance_payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDAllowanceSum += ((Number) value).doubleValue();
                    }
                }
               
                // Adding All Allowance sum in Response
                
                LinkedCaseInsensitiveMap YTDAllowanceSumKey = new LinkedCaseInsensitiveMap<>();
                YTDAllowanceSumKey.put("sumOfYTDAllowance", myFormat.format(YTDAllowanceSum));
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(YTDAllowanceSumKey);
                
                
                  // Iterate through the array and format in (18,000) the 'allowance_payable_amount' values
        for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
            if (allowance.containsKey("allowance_payable_amount")) {
                Object amountObject = allowance.get("allowance_payable_amount");
                if (amountObject instanceof Number) {
                    double amount = ((Number) amountObject).doubleValue();
                    String formattedAmount = myFormat.format(amount);
                    allowance.put("allowance_payable_amount", formattedAmount);
                }
            }
        }
                
                resultMap.put("ytdAllowanceCalculationPriviousAndCurrentMonth", getYTDAllowanceOFCurrentAndPriviousMonth);
                
            }
            else{
                 getYTDAllowanceOFCurrentAndPriviousMonth=employeeAllowanceRepo.getYTDAllowanceOFCurrentAndPriviousMonth(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
                 getOtherAllowanceOFCuurentAndPreviousMonth=employeeOtherAllowanceRepo.getOtherAllowanceOFCuurentAndPreviousMonth(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
               
                 double sum = 0.0; // Initialize the sum to zero
                 
                 //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getOtherAllowanceOFCuurentAndPreviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("payable_amount").toString());

                    System.out.println(value);

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        sum += ((Number) value).doubleValue();
                    }
                }
                //  end
                
                LinkedCaseInsensitiveMap otherallwance = new LinkedCaseInsensitiveMap<>();
                
                otherallwance.put("allowance_name", "Other Allowance");
                otherallwance.put("employee_id",Long.parseLong(map.get("employee_id").toString()));
                otherallwance.put("allowance_payable_amount",Math.round(sum));
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(otherallwance);
                
                  double YTDAllowanceSum = 0.0; // Initialize the sum to zero
                 
                 //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = allowance.get("allowance_payable_amount") !=null? Double.parseDouble(allowance.get("allowance_payable_amount").toString()):0.0;

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDAllowanceSum += ((Number) value).doubleValue();
                    }
                }
               
                
                LinkedCaseInsensitiveMap YTDAllowanceSumKey = new LinkedCaseInsensitiveMap<>();
                YTDAllowanceSumKey.put("sumOfYTDAllowance", myFormat.format(YTDAllowanceSum));
                
                getYTDAllowanceOFCurrentAndPriviousMonth.add(YTDAllowanceSumKey);
                
                
        // Iterate through the array and format in (18,000) the 'allowance_payable_amount' values
        for (Map<String, Object> allowance : getYTDAllowanceOFCurrentAndPriviousMonth) {
            if (allowance.containsKey("allowance_payable_amount")) {
                Object amountObject = allowance.get("allowance_payable_amount");
                if (amountObject instanceof Number) {
                    double amount = ((Number) amountObject).doubleValue();
                    String formattedAmount = myFormat.format(amount);
                    allowance.put("allowance_payable_amount", formattedAmount);
                }
            }
        }
                
                
                resultMap.put("ytdAllowanceCalculationPriviousAndCurrentMonth", getYTDAllowanceOFCurrentAndPriviousMonth);
                    
            
            }
            
             //  End Here
             
                         //  Start From Here
            // YTD Deduction  Calculation For Current and PreVious Month
            
               List<LinkedCaseInsensitiveMap> getYTDDeductionOFCurrentAndPriviousMonth= new ArrayList<>();
                List<LinkedCaseInsensitiveMap> getYTDDeductionOFPreviousYear= new ArrayList<>();
               List<LinkedCaseInsensitiveMap> getYTDDeductionOFJanToMarch= new ArrayList<>();
              
              
            if(Integer.parseInt(map.get("month").toString()) ==1 || Integer.parseInt(map.get("month").toString()) ==2 ||Integer.parseInt(map.get("month").toString()) ==3){
               
                 getYTDDeductionOFPreviousYear=employeeDeductionRepo.getYTDDeductionOFPreviousYear(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("year").toString())-1);
                 getYTDDeductionOFJanToMarch=employeeDeductionRepo.getYTDDeductionOFJanToMarch(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
                  
                    //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> deductionOfPriviousYear : getYTDDeductionOFPreviousYear) {
                   
                    
                       LinkedCaseInsensitiveMap tempValue = new LinkedCaseInsensitiveMap<>();
                   
                    // Retrieve the value for the particular key you want to sum

                    String previousValue =deductionOfPriviousYear.get("deduction_name") !=null ? deductionOfPriviousYear.get("deduction_name").toString():"";

                    Double deduction_Amount=deductionOfPriviousYear.get("deduction_payable_amount") !=null? Double.parseDouble(deductionOfPriviousYear.get("deduction_payable_amount").toString()):0.0;
                    for(Map<String, Object> deductionOfJanToMarch : getYTDDeductionOFJanToMarch){
                    
                         double YTDAllDeductionSum = 0.0; // Initialize the sum to zero
                         String currentValue = deductionOfJanToMarch.get("deduction_name") != null ? deductionOfJanToMarch.get("deduction_name").toString():"0";
                          Double deduction_Amount1=Double.parseDouble(deductionOfJanToMarch.get("deduction_payable_amount").toString());
                   
                         if(currentValue.equals(previousValue)){
                             
                             YTDAllDeductionSum=deduction_Amount+deduction_Amount1;
                          
                             tempValue.put("deduction_name", previousValue);
                             tempValue.put("deduction_payable_amount", Math.round(YTDAllDeductionSum));
                             
                             getYTDDeductionOFCurrentAndPriviousMonth.add(tempValue);
                                
                         }
                    }
                    

                }
                
                  double YTDDeductionSum = 0.0; 
                  
                   //sum of Allwance  start
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value = Double.parseDouble(allowance.get("deduction_payable_amount").toString());

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDDeductionSum += ((Number) value).doubleValue();
                    }
                }
               
                
                LinkedCaseInsensitiveMap YTDDeductionSumKey = new LinkedCaseInsensitiveMap<>();
                YTDDeductionSumKey.put("sumOfYTDDeduction", myFormat.format(YTDDeductionSum));
                
                getYTDDeductionOFCurrentAndPriviousMonth.add(YTDDeductionSumKey);
                
              
                
//                          // Iterate through the array and format in (18,000) the 'deduction_payable_amount' values
        for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
            if (allowance.containsKey("deduction_payable_amount")) {
                Object amountObject = allowance.get("deduction_payable_amount");
                if (amountObject instanceof Number) {
                    double amount = ((Number) amountObject).doubleValue();
                    String formattedAmount = myFormat.format(amount);
                    allowance.put("deduction_payable_amount", formattedAmount);
                }
            }
        }
                
                resultMap.put("ytdDeductionCalculationPriviousAndCurrentMonth", getYTDDeductionOFCurrentAndPriviousMonth);
                
                 
            }
            else{
                 getYTDDeductionOFCurrentAndPriviousMonth=employeeDeductionRepo.getYTDDeductionOFCurrentAndPriviousMonth(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
                
                
                  double YTDDeductionSum = 0.0; // Initialize the sum to zero
                 
                 //sum of Allwance  start
                 
                // Iterate through the list of allowances
                for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
                    // Retrieve the value for the particular key you want to sum
                    Double value =allowance.get("deduction_payable_amount") !=null? Double.parseDouble(allowance.get("deduction_payable_amount").toString()):0.0;

                    // Check if the value is a number and add it to the sum
                    if (value instanceof Number) {
                        YTDDeductionSum += ((Number) value).doubleValue();
                    }
                }
               
                
                LinkedCaseInsensitiveMap YTDDeductionSumKey = new LinkedCaseInsensitiveMap<>();
                YTDDeductionSumKey.put("sumOfYTDDeduction", myFormat.format(YTDDeductionSum));
                
                getYTDDeductionOFCurrentAndPriviousMonth.add(YTDDeductionSumKey);
                
                
                             // Iterate through the array and format in (18,000) the 'allowance_payable_amount' values
        for (Map<String, Object> allowance : getYTDDeductionOFCurrentAndPriviousMonth) {
            if (allowance.containsKey("deduction_payable_amount")) {
                Object amountObject = allowance.get("deduction_payable_amount");
                if (amountObject instanceof Number) {
                    double amount = ((Number) amountObject).doubleValue();
                    String formattedAmount = myFormat.format(amount);
                    allowance.put("deduction_payable_amount", formattedAmount);
                }
            }
        }
                
                
                resultMap.put("ytdDeductionCalculationPriviousAndCurrentMonth", getYTDDeductionOFCurrentAndPriviousMonth);
                    
            
            }
       }      
             /**
              making year and month Formate like April 2023
              **/            
            int y=Integer.parseInt(map.get("year").toString());
            int m=Integer.parseInt(map.get("month").toString());
             // Create a Calendar instance and set the year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m - 1); // Calendar month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

          // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            String formattedDate = sdf.format(calendar.getTime());
            
             //  End Here
            LinkedCaseInsensitiveMap userdetails=employeeDetailsRepo.getEmployeeDetails(Long.parseLong(map.get("employee_id").toString()));
            LinkedHashMap combined1 = new LinkedHashMap();
            combined1.put("allowance_name", "Amount In Words" + " :-" + " " + a);
            all.add(combined1);
             
            Long orgdetails = runPayRollRepository.getOrgDetailsId(Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()),
                    Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            
            System.out.println("orgdetails "+orgdetails);
            
             LinkedCaseInsensitiveMap orgDetailsforEmp=new LinkedCaseInsensitiveMap();
            
            if(orgdetails != null){
             orgDetailsforEmp = orgRepo.getOrganizationAddress2(orgId,orgdetails);
            }           
            System.out.println("orgDetailsforEmp "+orgDetailsforEmp);
            
            Map companyAddress=new HashMap<>();
//            LinkedCaseInsensitiveMap org = orgRepo.getOrganizationAddress(orgId);
            companyAddress.put("companyAddress", (orgDetailsforEmp != null && !orgDetailsforEmp.isEmpty()) ?orgDetailsforEmp:org);
            companyAddress.put("monthAndYear", formattedDate);
            
            resultMap.put("userdetails", userdetails);
            resultMap.put("attendanceDetails", attendanceDetails);
            resultMap.put("list", all);
            resultMap.put("companyDetails", companyAddress);
            resultMap.put("org_id", Long.parseLong(map.get("organization_id").toString()));
            resultMap.put("status", "success");            
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryDataInPDF() :: ", ex);
        }
        return resultMap;
    }
    
    
    
    public Map calSalaryDataInPdfForConsultantForMobile(String data,LinkedCaseInsensitiveMap employeeType) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class
            );
            System.out.println("pdf con" + map);
            map.put("employeeType", employeeType.get("employee_type").toString());
            List<LinkedHashMap> all = new ArrayList<>();
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            //Query for fetching employee allowance of salary data
            LinkedCaseInsensitiveMap employeeAllowances = employeeAllowanceRepo.fetchDataInPdfForConsultantAllowance(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Query for fetching employee deduction of salary data
            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.fetchDataInPdfforInternDeduction(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            NumberFormat myFormat = NumberFormat.getInstance();
            
            String abs = "true";
            double allowance_payable_amount=0.0;
            int index=0;
            for (LinkedCaseInsensitiveMap d : employeeDeductions) {
                 LinkedHashMap resultData = new LinkedHashMap();
                
                if(index==0){
                
                if (employeeAllowances != null) {
                    resultData.put("allowance_name", (employeeAllowances.get("consultant_allowance_name")));
                    resultData.put("allowance_amount", myFormat.format(employeeAllowances.get("consultant_allowance_amount")));
                    resultData.put("allowance_payable_amount", myFormat.format(employeeAllowances.get("consultnat_allowance_payable_amount")));
                    allowance_payable_amount=Math.round(Double.parseDouble(employeeAllowances.get("consultnat_allowance_payable_amount").toString()));
                }
                }
                
                resultData.put("deduction_name", (d.get("consultant_deduction_name")));
                resultData.put("deduction_payable_amount", myFormat.format(d.get("consultnat_deduction_payable_amount")));
               
                all.add(resultData);
                
                index++;
            }

            //Query for fetching employee calculated salary on the bases of their employee ID
            LinkedCaseInsensitiveMap salaryBreakup = salalrybreakuprepo.fetchConsultantData(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Checking the size of salary Break Up(allowances & deductions ) and putting into their correspondence key
            String abs1 = "true";

             Map attendanceDetails = new HashMap<>();
                          
            if (salaryBreakup != null) {
                if (abs1 == "true") {
                    LinkedHashMap resultData = new LinkedHashMap();
                    resultData.put("gross_salary", myFormat.format(salaryBreakup.get("gross_salary")));
                    resultData.put("allowance_name", "Total Earning");
                    resultData.put("allowance_amount", myFormat.format(salaryBreakup.get("gross_salary")));
                   // resultData.put("allowance_payable_amount", myFormat.format(salaryBreakup.get("total_payable_earning")));
                    resultData.put("allowance_payable_amount",myFormat.format(allowance_payable_amount));
                    resultData.put("deduction_name", "Total Deduction");
                    resultData.put("deduction_payable_amount", myFormat.format(salaryBreakup.get("total_deduction")));
                    
                  
                attendanceDetails.put("presentDay", myFormat.format(salaryBreakup.get("present_day")));
                attendanceDetails.put("approvedLeave", myFormat.format(salaryBreakup.get("approved_leave")));
                attendanceDetails.put("holidays", myFormat.format(salaryBreakup.get("holidays")));
                attendanceDetails.put("weekOff", myFormat.format(salaryBreakup.get("week_off")));
                attendanceDetails.put("Lwp", myFormat.format(salaryBreakup.get("lwp")));
                attendanceDetails.put("actualDay", myFormat.format(salaryBreakup.get("actual_day")));
                attendanceDetails.put("workingDay", myFormat.format(salaryBreakup.get("working_day")));
                all.add(resultData);
                }
            }

            String abs2 = "true";
            String a = "";
          
            if (abs2 == "true") {

                LinkedHashMap resultData = new LinkedHashMap();
                resultData.put("deduction_name", "Net Amount : Rs ");
                resultData.put("deduction_payable_amount", myFormat.format(salaryBreakup.get("net_amount")));
                a = NumberToWords.convertToWords(salaryBreakup.get("net_amount").toString());
                all.add(resultData);

            }     
            
            
            int y=Integer.parseInt(map.get("year").toString());
            int m=Integer.parseInt(map.get("month").toString());
             // Create a Calendar instance and set the year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m - 1); // Calendar month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

        
          // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            String formattedDate = sdf.format(calendar.getTime());

            LinkedCaseInsensitiveMap userdetails=employeeDetailsRepo.getEmployeeDetails(Long.parseLong(map.get("employee_id").toString()));
           
            
            LinkedHashMap resultData = new LinkedHashMap();
            resultData.put("allowance_name", "Amount In Words" + " :-" + " " + a);
            all.add(resultData);
            
            Map companyAddress=new HashMap<>();
            
            LinkedCaseInsensitiveMap org = orgRepo.getOrganizationAddress(orgId);
            
           Long orgdetails = runPayRollRepository.getOrgDetailsId(Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()),
                    Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            
            System.out.println("orgdetails "+orgdetails);
            
            LinkedCaseInsensitiveMap orgDetailsforEmp=new LinkedCaseInsensitiveMap();
            
            if(orgdetails != null){
             orgDetailsforEmp = orgRepo.getOrganizationAddress2(orgId,orgdetails);
            }
            
            System.out.println("orgDetailsforEmp "+orgDetailsforEmp);
            
           // companyAddress.put("companyAddress", orgDetailsforEmp.size()>0 ? orgDetailsforEmp :org);
           companyAddress.put("companyAddress", (orgDetailsforEmp != null && !orgDetailsforEmp.isEmpty()) ?orgDetailsforEmp:org); 
           companyAddress.put("monthAndYear", formattedDate);
          
            resultMap.put("userdetails", userdetails);
            resultMap.put("attendanceDetails", attendanceDetails);
            resultMap.put("companyDetails", companyAddress);
            resultMap.put("payableAmount", salaryBreakup.get("net_amount"));
            resultMap.put("list", all);
            resultMap.put("org_id", Long.parseLong(map.get("organization_id").toString()));
            resultMap.put("status", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryDataInPDF() :: ", ex);
        }
        return resultMap;

    }
    
    
    
    public Map calSalaryDataInPdfForInternForMobile(String data,LinkedCaseInsensitiveMap employeeType) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class
            );
            map.put("employeeType", employeeType.get("employee_type").toString());
            List<LinkedHashMap> all = new ArrayList<>();
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            LinkedCaseInsensitiveMap employeeAllowances = employeeAllowanceRepo.fetchDataInPdfForConsultantAllowance(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
           
            NumberFormat myFormat = NumberFormat.getInstance();
            Map attendanceDetails = new HashMap<>();
            double allowance_payable_amount=0.0;
            String abs = "true";
            if (abs == "true") {
                if (employeeAllowances != null) {
                    if (employeeAllowances.containsKey("consultant_allowance_name") && employeeAllowances.containsKey("consultant_allowance_amount") && employeeAllowances.containsKey("consultnat_allowance_payable_amount")) {
                        LinkedHashMap resultData = new LinkedHashMap();
                        resultData.put("allowance_name", (employeeAllowances.get("consultant_allowance_name")));
                        resultData.put("allowance_amount", myFormat.format(employeeAllowances.get("consultant_allowance_amount")));
                        resultData.put("allowance_payable_amount", myFormat.format(employeeAllowances.get("consultnat_allowance_payable_amount")));
                        allowance_payable_amount=Math.round(Double.parseDouble(employeeAllowances.get("consultnat_allowance_payable_amount").toString()));
         
                        all.add(resultData);
                    }
                }
            }
            //Query for fetching employee calculated salary on the bases of their employee ID
            LinkedCaseInsensitiveMap salaryBreakup = salalrybreakuprepo.fetchConsultantData(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employeeType").toString());
            //Checking the size of salary Break Up(allowances & deductions ) and putting into their correspondence key
            if (salaryBreakup != null) {
                String abs1 = "true";
                if (abs1 == "true") {
                    LinkedHashMap resultData = new LinkedHashMap();
                    resultData.put("gross_salary", myFormat.format(salaryBreakup.get("gross_salary")));
                    resultData.put("allowance_name", "Total Earning");
                    resultData.put("allowance_amount", myFormat.format(salaryBreakup.get("gross_salary")));
//                    resultData.put("allowance_payable_amount", myFormat.format(salaryBreakup.get("total_payable_earning")));
                    resultData.put("allowance_payable_amount",myFormat.format(allowance_payable_amount));
                    attendanceDetails.put("presentDay", myFormat.format(salaryBreakup.get("present_day")));
                    attendanceDetails.put("approvedLeave", myFormat.format(salaryBreakup.get("approved_leave")));
                    attendanceDetails.put("holidays", myFormat.format(salaryBreakup.get("holidays")));
                    attendanceDetails.put("weekOff", myFormat.format(salaryBreakup.get("week_off")));
                    attendanceDetails.put("Lwp", myFormat.format(salaryBreakup.get("lwp")));
                    attendanceDetails.put("actualDay", myFormat.format(salaryBreakup.get("actual_day")));
                    attendanceDetails.put("workingDay", myFormat.format(salaryBreakup.get("working_day")));
               

                    all.add(resultData);
                }
                String abs2 = "true";
                String a = "";
                if (abs2 == "true") {
                    LinkedHashMap resultData = new LinkedHashMap();
                    resultData.put("deduction_name", "Net Amount : Rs ");
                    resultData.put("deduction_payable_amount", myFormat.format(salaryBreakup.get("net_amount")));
                    a = NumberToWords.convertToWords(salaryBreakup.get("net_amount").toString());
                    all.add(resultData);
                }
                LinkedHashMap resultData = new LinkedHashMap();
                resultData.put("allowance_name", "Amount In Words" + " :-" + " " + a);
                all.add(resultData);
            }
            
            int y=Integer.parseInt(map.get("year").toString());
            int m=Integer.parseInt(map.get("month").toString());
             // Create a Calendar instance and set the year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m - 1); // Calendar month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

        
          // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            String formattedDate = sdf.format(calendar.getTime());

          

            LinkedCaseInsensitiveMap userdetails=employeeDetailsRepo.getEmployeeDetails(Long.parseLong(map.get("employee_id").toString()));
           
            
            Map companyAddress=new HashMap<>();
            LinkedCaseInsensitiveMap org = orgRepo.getOrganizationAddress(orgId);
            
            Long orgdetails = runPayRollRepository.getOrgDetailsId(Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()),
                    Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));
            
            System.out.println("orgdetails "+orgdetails);
            
             LinkedCaseInsensitiveMap orgDetailsforEmp=new LinkedCaseInsensitiveMap();
            
            if(orgdetails != null){
             orgDetailsforEmp = orgRepo.getOrganizationAddress2(orgId,orgdetails);
            }
            
            System.out.println("orgDetailsforEmp "+orgDetailsforEmp);
            
            
           // companyAddress.put("companyAddress", orgDetailsforEmp.size()>0 ?orgDetailsforEmp:org);
           companyAddress.put("companyAddress", (orgDetailsforEmp != null && !orgDetailsforEmp.isEmpty()) ?orgDetailsforEmp:org); 
           companyAddress.put("monthAndYear", formattedDate);
          
            resultMap.put("userdetails", userdetails);
            resultMap.put("attendanceDetails", attendanceDetails);
            resultMap.put("companyDetails", companyAddress);
            resultMap.put("payableAmount", salaryBreakup.get("net_amount"));
            resultMap.put("list", all);
            resultMap.put("org_id", Long.parseLong(map.get("organization_id").toString()));
            resultMap.put("status", "success");

        } catch (Exception ex) {
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryDataInPDF() :: ", ex);
        }
        return resultMap;
    }
    
     @Override
    public Map calculateSalaryDataNew(String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            String employeeType = map.containsKey("employee_Type") ? map.get("employee_Type").toString() : map.containsKey("employeeType") ? map.get("employeeType").toString() : null;
            if (employeeType.equalsIgnoreCase("Worker")) {
                employeeType = "Worker";
            } else if (!employeeType.equalsIgnoreCase("Consultant") && !employeeType.equalsIgnoreCase("Intern")) {
                employeeType = "Full time";
            }
            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);
//            Get Leaves
            int currMonth = Integer.parseInt(map.get("month").toString());
            int currYear = 0; 
         //   System.out.println("map 628"+" "+map.toString());
            int selected_month=map.get("selected_month")!=null?Integer.parseInt(map.get("selected_month").toString()):Integer.parseInt(map.get("month").toString());
           // int selected_month=Integer.parseInt(map.get("selected_month").toString());
            if(selected_month==1||selected_month==2||selected_month==3){
                currYear = Integer.parseInt(map.get("year").toString()); 
            }
            else{
                   currYear = Integer.parseInt(map.get("year").toString()); 
            }
            JSONObject json = new JSONObject();
            json.put("employeeId", map.get("emp_id").toString());
            json.put("organizationId", map.get("organization_id").toString());
            List<LinkedCaseInsensitiveMap> salaryDates;
            salaryDates = payrollSettingRepo.getSalaryDates(Long.parseLong(map.get("organization_id").toString()));
            String start_date = "0";
            String end_date = "0";
            if (!salaryDates.isEmpty()) {
                for (LinkedCaseInsensitiveMap l : salaryDates) {
                    if (l.containsKey("start_date") && l.get("start_date") != null) {
                        start_date = l.get("start_date").toString();
                    }
                    if (l.containsKey("end_date") && l.get("end_date") != null) {
                        end_date = l.get("end_date").toString();
                    }
                }
                json.put("startDate", start_date);
                json.put("endDate", end_date);
            } else {
                LOGGER.info("Start Date and End Date in missing");
                resultMap.put("status", "error");
                resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                return resultMap;
            }
            json.put("year", currYear);
      
            boolean checkStartDate = false;
            if (Integer.parseInt(start_date) == 1) {
                checkStartDate = true;
                json.put("month", currMonth );
            } else {
                if (currMonth == 0) {
                    json.put("month", 12);
                    json.put("year", currYear - 1);
                } else {
                    json.put("month", currMonth-1);
                }
            }
            
            String leaveData = EncryptDecryptUtils.encrypt(json.toString());
        //    System.out.println("leaveData" + " " + leaveData);
            HttpEntity<?> leaveEntity = new HttpEntity<>(leaveData, header);
//            AtomicInteger working_day = new AtomicInteger();
            double working_day = 0.0;
//            int days = this.getnumberOfDaysInMonth(currMonth + 1, currYear);
            if (!map.containsKey("where")) {
                currMonth += 1;
            }
            double Gross = 0.00;
            double PayableGross = 0.00;
            double PayableBasic = 0;
            double Basic = 0.00;
            Calendar ca = Calendar.getInstance();
            ca.add(Calendar.MONTH, 0);
            int mon = ca.get(Calendar.MONTH);
            double gross_salary = 0.0;
            Integer month;
            if (Integer.parseInt(start_date) == 1) {
                month = Integer.parseInt(json.get("month").toString())-1;
            } else {
                month = Integer.parseInt(json.get("month").toString()) ;
            }
            LinkedCaseInsensitiveMap grossSalary = salalrybreakuprepo.getGrossSalary(Integer.parseInt(json.get("employeeId").toString()), json.get("year").toString(), month.toString());

            if (grossSalary != null && grossSalary.containsKey("gross_salary")) {
                gross_salary = Double.parseDouble(grossSalary.get("gross_salary").toString());
                resultMap.put("salary", Math.round(gross_salary));
            }

           // map.put("month", ca)
            Map workingDay = this.getWorkingDay(map, leaveEntity);
            System.out.println("workingDay 713"+" "+workingDay.toString());
            if (workingDay.containsKey("working_day") && workingDay.get("working_day") != null) {
                working_day = Double.parseDouble(workingDay.get("working_day").toString());
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", workingDay.get("msg"));
                return resultMap;
            }
            Long days = Long.parseLong(workingDay.get("total_days").toString());

            Map salaryBreakupData = this.isSalaryBreakUpSaved(String.valueOf(currMonth + 1), String.valueOf(currYear), map.get("emp_id").toString(), map.get("organization_id").toString(), map.get("email_id").toString(), employeeType, gross_salary);

            if (salaryBreakupData.size() > 1) {

                Double day = map.containsKey("working_day") && map.get("working_day") != "" ? Double.parseDouble(map.get("working_day").toString()) : 0;
                Double workDay = day != 0.0 ? day : salaryBreakupData.get("working_day") != null ? Double.parseDouble(salaryBreakupData.get("working_day").toString()) : working_day;
                Map salaryData;
                if (salaryBreakupData.get("isSalaryBreakupSaved").equals(false) || day!=0) {
                    salaryBreakupData.put("WorkingDay", workDay);
                    salaryData = this.calculationBasedOnWorkingDay(workDay, days, salaryBreakupData, map);
                }else{
                salaryData = salaryBreakupData;
                }
                if (salaryData.get("status").equals("success")) {
                    salaryBreakupData.put("employee_id", map.get("emp_id"));
                    salaryBreakupData.put(("employeeType"), employeeType);
                    salaryBreakupData.put("WorkingDay", workDay);
                    salaryBreakupData.put("advance", salaryData.get("advance"));
                    salaryBreakupData.put("salaryAvailable", "true");
                    return salaryBreakupData;
                }
            } else {
                salaryBreakupData.put("salaryAvailable", "false");
                return salaryBreakupData;
            }

 

            Double presentDay = 0.0;
            Double approvedLeave = 0.0;
            Double holidays = 0.0;
            Double weekOff = 0.0;
            Double Lwp = 0.0;
            Double actualDays = 0.0;
            if (!map.containsKey("flagTax")) {

                Map TimesheetattendanceDetails = this.getattendanceDetails(map, leaveEntity);

                LOGGER.info("Input for TimesheetattendanceDetails having employee ID --->> :" + map.get("emp_id").toString() + "Get TimesheetattendanceDetails from timesheet for full time employee time :::" + TimesheetattendanceDetails);
                presentDay = TimesheetattendanceDetails.containsKey("presentDay") && TimesheetattendanceDetails.get("presentDay") != null ? Double.parseDouble(TimesheetattendanceDetails.get("presentDay").toString()) : 0.0;
                approvedLeave = TimesheetattendanceDetails.containsKey("approvedLeave") && TimesheetattendanceDetails.get("approvedLeave") != null ? Double.parseDouble(TimesheetattendanceDetails.get("approvedLeave").toString()) : 0.0;
                weekOff = TimesheetattendanceDetails.containsKey("weekOff") && TimesheetattendanceDetails.get("weekOff") != null ? Double.parseDouble(TimesheetattendanceDetails.get("weekOff").toString()) : 0.0;
                holidays = TimesheetattendanceDetails.containsKey("holidays") && TimesheetattendanceDetails.get("holidays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("holidays").toString()) : 0.0;
                Lwp = TimesheetattendanceDetails.containsKey("Lwp") && TimesheetattendanceDetails.get("Lwp") != null ? Double.parseDouble(TimesheetattendanceDetails.get("Lwp").toString()) : 0.0;
                actualDays = TimesheetattendanceDetails.containsKey("actualDays") && TimesheetattendanceDetails.get("actualDays") != null ? Double.parseDouble(TimesheetattendanceDetails.get("actualDays").toString()) : 0.0;

 

                LOGGER.info("Present Data from timesheet ==> " + presentDay);
                LOGGER.info("approvedLeave Data from timesheet ==> " + approvedLeave);
                LOGGER.info("weekOff Data from timesheet ==> " + weekOff);
                LOGGER.info("holidays Data from timesheet ==> " + holidays);
                LOGGER.info("Lwp Data from timesheet ==> " + Lwp);
            }

 

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> calculateSalaryData() :: ", ex);
        }
       
        return resultMap;


    }

    @Override
    public Map calculationBasedOnWorkingDayNew(Double working_day, Long days, Map salarybreakupData, Map map) {
          LOGGER.info("calculationBasedOnWorkingDay method execution starts with Working Day " + working_day + " and days " + days + " and Salary Breakup Data " + salarybreakupData);
        Map resultMap = new HashMap<>();
        Map<String, Double> a_amount = new HashMap<>();
        Map<String, List<Double>> a_payable_amount = new HashMap<>();
        Map<String, List<Double>> d_payable_amount = new HashMap<>();
        List<Double> allowancePayableAmount=new ArrayList<>();
        List<Double> deduction_payable_amount = new ArrayList<>();

        try {
            System.out.println("working_day 796"+" "+" "+working_day+" "+"days "+" "+days);
            List<Double> allowanceAmount = (List<Double>) ((HashMap) salarybreakupData.get("AllowanceAmount")).get("allowanceAmount");
               if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                           allowancePayableAmount = allowanceAmount.stream().map(a -> (a * working_day) / 26).collect(Collectors.toList());
  
               }else{
                allowancePayableAmount = allowanceAmount.stream().map(a -> (a * working_day) / days).collect(Collectors.toList());
 
               }
            
           
            
            double payableGross = 0;

            if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                payableGross = (Math.round(((Double.parseDouble(salarybreakupData.get("payableSalary").toString())) / 26) * working_day));
            } else {
                payableGross = (Math.round(Double.parseDouble(salarybreakupData.get("payableSalary").toString()) * working_day) / days);
            }

           if ( map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                double otherPayableAllowances = Math.round(((Double.parseDouble(salarybreakupData.get("OtherPayableAllowances").toString())) * working_day) / 26);
                salarybreakupData.put("OtherPayableAllowances", otherPayableAllowances);
            }else if(map.get("employee_Type").toString().equalsIgnoreCase("Full time")){
                //System.out.println("salarybreakupData 523"+" "+salarybreakupData.toString());
            double otherPayableAllowances = Math.round(((Double.parseDouble(salarybreakupData.get("OtherPayableAllowances").toString())) * working_day) / days);
               salarybreakupData.put("OtherPayableAllowances",otherPayableAllowances );
            }

            List<Allowance> allowances;
            int selected_month;
            int selected_year;
            if (map.containsKey("selected_month")) {
                selected_month = Integer.parseInt(map.get("selected_month").toString()) - 1;
                selected_year = Integer.parseInt(map.get("selected_year").toString());
            } else {
                selected_month = Integer.parseInt(map.get("month").toString()) - 1;
                selected_year = Integer.parseInt(map.get("year").toString());
            }
            allowances = allowanceRepo.findApprovedAllowances(Long.parseLong(map.get("organization_id").toString()), new Date(selected_year - 1900, selected_month - 1, 28), map.get("employee_Type").toString());
            //    System.out.println("myallowances============>>>>>>>>>"+ allowances+"   date===="+new Date(selected_year - 1900, selected_month-1, 28)+"  organization===="+map.get("organization_id").toString()+"icoming values==== "+map+" employeeeeeeeetype"+ employeeType);

            for (int idx = 0; idx < allowancePayableAmount.size(); idx++) {
                double rounded = Math.round(allowancePayableAmount.get(idx));
                allowancePayableAmount.set(idx, rounded);
            }
            a_payable_amount.put("allowancePayableAmount", allowancePayableAmount);
            salarybreakupData.put("AllowancePayableAmount", a_payable_amount);

            List<String> deductionName = (List<String>) ((HashMap) salarybreakupData.get("DeductionName")).get("deductionName");
            List<Double> deductionAmount = (List<Double>) ((HashMap) salarybreakupData.get("DeductionAmount")).get("deductionAmount");
            int idx = -1;
            for (String name : deductionName) {
                idx++;
                if (name.equalsIgnoreCase("Advance")) {
                    Double advanceAmount = 0.0;
                    List<LinkedCaseInsensitiveMap> employeeLoan;
                    if (map.get("employee_Type").toString().equalsIgnoreCase("Worker")) {
                        employeeLoan = workerLoanRepo.getLoanForSalaryBreakup(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString())+1, Integer.parseInt(map.get("year").toString()));
                    } else {
                        employeeLoan = employeeLoanRepo.getLoanForSalaryBreakup(Long.parseLong(map.get("emp_id").toString()), Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("month").toString())+1, Integer.parseInt(map.get("year").toString()));
                    }
                    if (!employeeLoan.isEmpty()) {
                        for (LinkedCaseInsensitiveMap emp_loan : employeeLoan) {
                            if (emp_loan.containsKey("remaining_amount") && emp_loan.get("remaining_amount") != null) {
                                if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > 0) {
                                    if (Double.parseDouble(emp_loan.get("remaining_amount").toString()) > Double.parseDouble(emp_loan.get("installment_amount").toString())) {
                                        advanceAmount += Double.parseDouble(emp_loan.get("installment_amount").toString());
                                    } else {
                                        advanceAmount += Double.parseDouble(emp_loan.get("remaining_amount").toString());

                                    }
                                } else {
                                    advanceAmount += 0;
                                }
                            }
                        }
                        deduction_payable_amount.add(advanceAmount);
                    } else {
                        deduction_payable_amount.add(0.0);
                    }
                    resultMap.put("advance", advanceAmount);
                } else if (name.equalsIgnoreCase("Income Tax")) {
                    List<Double> values = (List<Double>) ((HashMap) (salarybreakupData.get("DeductionPayableAmount"))).get("deductionPayableAmount");
                    deduction_payable_amount.add(values.get(idx));
                } else if (name.equalsIgnoreCase("Professional Tax")) {
                    deduction_payable_amount.add(deductionAmount.get(idx));
                } else {
                    if(map.get("employee_Type").toString().equalsIgnoreCase("Worker")){
                        deduction_payable_amount.add((deductionAmount.get(idx) * working_day) / 26);
                        
                    }else{
                        deduction_payable_amount.add((deductionAmount.get(idx) * working_day) / days); 
                    }
                }
            }

            double deductionPayableAmount = 0.0;
            for (int idxx = 0; idxx < deduction_payable_amount.size(); idxx++) {
                double rounded = Math.round(deduction_payable_amount.get(idxx));
                deductionPayableAmount += deduction_payable_amount.get(idxx);
                deduction_payable_amount.set(idxx, rounded);
            }

            d_payable_amount.put("deductionPayableAmount", deduction_payable_amount);
            salarybreakupData.put("DeductionPayableAmount", d_payable_amount);
            salarybreakupData.put("payable_gross", Math.round(payableGross));
            salarybreakupData.put("NetPayableAmount", Math.round(payableGross - deductionPayableAmount));
            salarybreakupData.put("total_deduction", Math.round(deductionPayableAmount));
            resultMap.put("salaryCalculation", salarybreakupData);

            resultMap.put("status", "success");

        } catch (Exception ex) {
            LOGGER.error("Problem in SalaryBreakupServiceImpl :: calculationBasedOnWorkingDay() => " + ex);
            resultMap.put("status", "exception");
        }
      
        return resultMap;

    }

    @Override
    public Map getGrossSalaryOfEmployee(String data, HttpServletRequest request) {
        
      LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalaryOfEmployee method excution..!");
        Map resultMap = new HashMap<>();
        try {
            Map<String, Object> map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalaryOfEmployee method excution :: Request Data :-" + map);          
//            Get Gross salary
            LinkedCaseInsensitiveMap salarys = salalrybreakuprepo.getGrossSalaryOFEmployee(Long.parseLong(map.get("emp_id").toString()));
           
            if(salarys ==null){
                  resultMap.put("salary", 0);
            }
            else{
               resultMap.put("salary", Math.round(Double.parseDouble(salarys.get("gross_salary").toString())));
            }
            LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excution :: Response Data :-" + salarys);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in SalaryBreakupServiceImpl -> getGrossSalary() :: ", ex);
        }
        LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excuted ..!" + resultMap);
        LOGGER.info("In SalaryBreakupServiceImpl ->getGrossSalary method excuted succesfully ..!");
       
        return resultMap;
    
    }
    
        public Map fetchingSingedUrlFromRecruit(HttpServletRequest request, Long id) {
            
            
         String bearerToken = authenticationFilter.getJwtFromRequest(request);
         HttpHeaders header = new HttpHeaders();
         header.setBearerAuth(bearerToken);
         header.setContentType(MediaType.TEXT_PLAIN); 
         
         
        JSONObject payload = new JSONObject();        
       
        payload.put("organizationId", id);
        System.out.println("******Payload for Get signed Url from Recruit*****");
        System.out.println(payload);
       
        String encryptedPayload = EncryptDecryptUtils.encrypt(payload.toString());

        Map employeeListResp = new HashMap();

        HttpEntity<?> entity = new HttpEntity<>(encryptedPayload, header);

         try {
        employeeListResp = restTemplate.exchange(assessment_url + "/orgs/getorgimg", HttpMethod.POST, entity, HashMap.class).getBody();

       
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListResp.get("data").toString()), LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
//            logger.info("Unable to employee list from the manage :: ", ex);
        }
        System.out.println("response Of Signed url from Recruit : " + employeeListResp);

        return employeeListResp;

    }
        
        public Map fetchingLeaveDetailsFromManage(HttpServletRequest request, Long id,Long employeeId,int month,int year) {
            
            
         String bearerToken = authenticationFilter.getJwtFromRequest(request);
         HttpHeaders header = new HttpHeaders();
         header.setBearerAuth(bearerToken);
         header.setContentType(MediaType.TEXT_PLAIN); 
         
         
        JSONObject payload = new JSONObject();        
       
        payload.put("organizationId", id);
        payload.put("employeeId", employeeId);
        payload.put("month", month);
        payload.put("year", year);
        System.out.println("******Payload for Leave from Manage*****");
        System.out.println(payload);
       
        String encryptedPayload = EncryptDecryptUtils.encrypt(payload.toString());

        Map employeeListResp = new HashMap();

        HttpEntity<?> entity = new HttpEntity<>(encryptedPayload, header);

         try {
        employeeListResp = restTemplate.exchange(reimburshment_url + "/leaveyearly/getLeavesByMonthAndYear", HttpMethod.POST, entity, HashMap.class).getBody();

            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListResp.get("data").toString()), LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
//            logger.info("Unable to employee list from the manage :: ", ex);
        }
        System.out.println("response Of Leave from Manage : " + employeeListResp);

        return employeeListResp;

    }


}

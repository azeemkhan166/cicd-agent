/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.AdvanceRcm;
import com.realcoderz.model.AdvanceRcmAdjustment;
import com.realcoderz.model.AllowanceTemplatePayPlan;
import com.realcoderz.model.BonusYearly;
import com.realcoderz.model.CustomAllowance;
import com.realcoderz.model.CustomAllowanceAmount;
import com.realcoderz.model.CustomDeduction;
import com.realcoderz.model.CustomRunPayroll;
import com.realcoderz.model.DeductionDependsOnAllowance;
import com.realcoderz.model.DeductionLoan;
import com.realcoderz.model.DeductionTemplatePayPlan;
import com.realcoderz.model.EmployeeAllowance;
import com.realcoderz.model.EmployeeDeduction;
import com.realcoderz.model.EmployeeNetPay;
import com.realcoderz.model.IncomeTax;
import com.realcoderz.model.LabourLawDeduction;
import com.realcoderz.model.PayPlan;
import com.realcoderz.model.Relief87ANewRegime;
import com.realcoderz.service.RunPayService;
import com.realcoderz.model.RunPayRoll;
import com.realcoderz.model.SalaryBreakUp;
import com.realcoderz.model.TempararyAllowance;
import com.realcoderz.model.TempararyDeduction;
import com.realcoderz.repository.AdvanceRcmAdjustmentRepository;
import com.realcoderz.repository.AdvanceRcmRepository;
import com.realcoderz.repository.AllowanceRepository;
import com.realcoderz.repository.BonusAmountRepository;
import com.realcoderz.repository.BonusDeductionRepository;
import com.realcoderz.repository.CustomAllowanceAmountRepository;
import com.realcoderz.repository.CustomAllowanceRepository;
import com.realcoderz.repository.CustomDeductionRepository;
import com.realcoderz.repository.CustomRunPayrollRepository;
import com.realcoderz.repository.DeductionLoanRepository;
import com.realcoderz.repository.DeductionRepository;
import com.realcoderz.repository.EmployeeAllowanceRepository;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.repository.EmployeeLoanRepository;
import com.realcoderz.repository.EmployeeNetPayRepository;
import com.realcoderz.repository.IncomeTaxRepository;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.LabourLawDeductionRepo;
import com.realcoderz.repository.NewTaxRegimeSlabRepository;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.repository.OtherAllowancesRepository;
import com.realcoderz.repository.PayPlanRepository;
import com.realcoderz.repository.PayrollSettingRepository;
import com.realcoderz.repository.Relief87ANewRegimeRepo;
import com.realcoderz.repository.RunPayRollRepository;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.repository.TaxSlabRepository;
import com.realcoderz.repository.TempararyAllowanceRepository;
import com.realcoderz.repository.TempararyDeductionRepository;
import com.realcoderz.repository.VariableDeductionRepository;
import com.realcoderz.repository.WorkerLoanRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.BalanceSummaryService;
import com.realcoderz.service.EmployeeLoanService;
import com.realcoderz.service.EmployeeService;
import com.realcoderz.service.TaxService;
import com.realcoderz.service.WorkerLoanService;
import static com.realcoderz.serviceImpl.SalaryBreakupServiceImpl.LOGGER;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author bipulsingh
 */
@Service
public class RunPayServiceImpl implements RunPayService {

    private final static Logger logger = LoggerFactory.getLogger(RunPayServiceImpl.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    @Autowired
    private RunPayRollRepository runPayRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PayrollSettingRepository payrollRepo;

    @Autowired
    private JWTAuthenticationFilter authenticationFilter;

    @Autowired
    private EmployeeNetPayRepository employeeNetPayRepo;

    @Autowired
    private EmployeeLoanService employeeLoanService;

    @Autowired
    private WorkerLoanService workerLoanService;

    @Autowired
    private EmployeeLoanRepository employeeLoanRepo;

    @Autowired
    private DeductionRepository deductionRepo;

    @Autowired
    private SalaryBreakuprepo salaryBreakupRepo;

    @Autowired
    private PayrollSettingRepository payrollSettingRepo;

    @Autowired
    private BonusDeductionRepository bonusDeductionRepo;

    @Autowired
    private VariableDeductionRepository variableDeductionRepo;

    @Autowired
    private OrganizationSetUpRepository organizationSetupRepo;

    @Autowired
    private EmployeeAllowanceRepository employeeAllowanceRepo;

    @Autowired
    private EmployeeDeductionRepository employeeDeductionRepo;

    @Autowired
    private OtherAllowancesRepository employeeOtherAllowanceRepo;

    @Autowired
    private WorkerLoanRepository workerLoanRepo;

    @Autowired
    private TaxService taxService;

    @Autowired
    private BalanceSummaryService balanceService;

    @Autowired
    private employeeDetailsRepository empdetailsrepo;

    @Autowired
    private IncomeTaxRepository incomeRepo;

    @Autowired
    private TaxSlabRepository taxSlabRepo;

    @Autowired
    private Relief87ANewRegimeRepo relief87ANewRegimeRepo;

    @Autowired
    private NewTaxRegimeSlabRepository newTaxRegimeSlabRepo;

    @Autowired
    private InvestmentDeclarationRepository investentRepo;

    @Autowired
    private LabourLawDeductionRepo labourLawRepo;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TempararyAllowanceRepository tempAllowanceRepo;

    @Autowired
    private TempararyDeductionRepository tempDeductionRepo;

    @Autowired
    private DeductionLoanRepository deductionLoanRepository;

    @Autowired
    private BonusAmountRepository bonusAmountRepository;

    @Autowired
    private AllowanceRepository allowanceRepo;

    @Autowired
    private CustomAllowanceRepository customAllowanceRepository;

    @Autowired
    private CustomDeductionRepository customDeductionRepository;

    @Autowired
    private CustomRunPayrollRepository customRunPayrollRepository;

    @Autowired
    private AdvanceRcmRepository advanceRcmRepository;

    @Autowired
    private PayPlanRepository PayPlanRepository;

    @Autowired
    private CustomAllowanceAmountRepository customAllowanceAmountRepository;

    @Autowired
    private AdvanceRcmAdjustmentRepository advanceRcmAdjustmentRepository;

    @Override
    @Transactional
    public Map saveAll(Map map) {
        Map resultMap = new HashMap<>();
        Map response = new HashMap<>();
        try {
            if (map.containsKey("list")) {

                /**
                 * get Employee whose Run Payroll is Saved.
                 *
                 */
                List<LinkedCaseInsensitiveMap> isRunPayrollisSaved = runPayRepo.isRunPayrollSaved(
                        Long.parseLong(map.get("organizationId").toString()),
                        Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()));

                List<LinkedHashMap> dataList = (List<LinkedHashMap>) map.get("list");

                /**
                 * removing Employee whose payroll is done
                 *
                 */
                dataList.removeIf(data -> data.get("employeeId") == null);

                if (!dataList.isEmpty() && !isRunPayrollisSaved.isEmpty()) {
                    dataList = dataList.stream()
                            .filter(data -> isRunPayrollisSaved.stream()
                                    .noneMatch(isRunPayroll -> Long.parseLong(data.get("employeeId").toString()) == Long
                                            .parseLong(isRunPayroll.get("employee_id").toString())))
                            .collect(Collectors.toList());
                }

                if (dataList.isEmpty()) {

                    resultMap.put("status", "success");
                    resultMap.put("msg", "Data  Saved!");
                    return resultMap;
                }

                List<RunPayRoll> list = new ArrayList<>();
                int month = Integer.parseInt(map.get("month").toString());

                int year = Integer.parseInt(map.get("year").toString());

                long orgId = Long.parseLong(map.get("organizationId").toString());
                response.put("organization_id", orgId);
                String netPay = map.get("netPay").toString();

                dataList.stream().forEach(data -> {

                    response.put("employee_id", data.get("employeeId"));
                    response.put("other_deductions", data.get("advance"));
                    String employee_type = data.get("employee_type") != null ? data.get("employee_type").toString()
                            : "";

                    RunPayRoll runPayRollData = mapper.convertValue(data, RunPayRoll.class);

                    if (employee_type.equalsIgnoreCase("Probation") || employee_type.equalsIgnoreCase("Permanent")) {

                        runPayRollData.setEmployee_type("Full time");

                    }

                    runPayRollData.setPayRunMonth(month);
                    runPayRollData.setPayRunYear(year);
                    runPayRollData.setOrganizationId(orgId);
                    list.add(runPayRollData);
                    employeeLoanService.remainingAmount(response);
                    workerLoanService.remainingAmount(response);
                });

                runPayRepo.saveAll(list);
                payrollRepo.updateLastPayRun(month, year, orgId);

                EmployeeNetPay employeeNetPay = new EmployeeNetPay();

                employeeNetPay.setMonth(month);
                employeeNetPay.setNet_pay(netPay);
                employeeNetPay.setYear(year);
                employeeNetPay.setOrganizationId(orgId);
                employeeNetPayRepo.save(employeeNetPay);
                // if(Objects.equals(orgId, arriersOrgId)){
                new Thread(() -> {
                    balanceService.saveBalanceSummary(list);
                }).start();
                // }

                /**
                 * salary hold code*
                 */
                List<Long> salaryholdEmployee = (List<Long>) map.get("salaryHold");
                if (salaryholdEmployee.size() > 0) {

                    List<Long> sids = salaryBreakupRepo.getSidForSalaryHold(salaryholdEmployee);
                    salaryBreakupRepo.holdAllEmployeeSalary(sids);
                }

                /**
                 * end *
                 */
                resultMap.put("status", "success");
                resultMap.put("msg", "Data Saved!");
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid key and value!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Problem in RunPayServiceImpl :: saveAll() => " + ex);
            resultMap.put("status", "exception");
            throw ex;
        }
        return resultMap;
    }

    public boolean isAlreadyExist(RunPayRoll runPayRoll) {
        return runPayRepo.havingSameData(runPayRoll.getPayRunMonth(), runPayRoll.getPayRunYear(),
                runPayRoll.getEmployeeId()) > 0;
    }

    @Override
    public Map updateRunPayRoll(Map map) {
        Map resultMap = new HashMap<>();
        if (map.containsKey("data") && map.get("data") != null) {
            RunPayRoll runPayRollData = mapper.convertValue(map.get("data"), RunPayRoll.class);
            resultMap.put("data", runPayRepo.save(runPayRollData));
            resultMap.put("status", "success");
        } else {
            resultMap.put("status", "error");
            resultMap.put("msg", "Please provide value and key!̛");
        }
        return resultMap;
    }

    @Override
    public RunPayRoll findDataByMonthYearAndEmpId(Integer month, Integer year, Long empId) {
        return runPayRepo.findDataByMonthYearAndEmpId(month, year, empId);
    }

    @Override
    public Map getPreviousPayRunData(Map map) {
        Map resultMap = new HashMap<>();
        if (map.containsKey("month") && map.get("month") != null && map.containsKey("year") && map.get("year") != null
                && map.containsKey("organizationId") && map.get("organizationId") != null) {
            int firstMonth = Integer.parseInt(map.get("month").toString()) - 1, firstMonthExpense;
            int secondMonth = firstMonth - 1, secondMonthExpense;
            int thirdMonth = secondMonth - 1, thirdMonthExpense;
            int year = Integer.parseInt(map.get("year").toString());
            if (firstMonth != 0) {
                firstMonthExpense = runPayRepo.findPreviousPayRunData(firstMonth, year,
                        Long.parseLong(map.get("organizationId").toString()));
            } else {
                firstMonth = 12;
                year -= 1;
                firstMonthExpense = runPayRepo.findPreviousPayRunData(firstMonth, year,
                        Long.parseLong(map.get("organizationId").toString()));
            }
            if (secondMonth != 0) {
                secondMonthExpense = runPayRepo.findPreviousPayRunData(secondMonth, year,
                        Long.parseLong(map.get("organizationId").toString()));
            } else {
                secondMonth = 12;
                year -= 1;
                secondMonthExpense = runPayRepo.findPreviousPayRunData(secondMonth, year,
                        Long.parseLong(map.get("organizationId").toString()));
            }
            if (thirdMonth != 0) {
                thirdMonthExpense = runPayRepo.findPreviousPayRunData(thirdMonth, year,
                        Long.parseLong(map.get("organizationId").toString()));
            } else {
                thirdMonth = 12;
                year -= 1;
                thirdMonthExpense = runPayRepo.findPreviousPayRunData(thirdMonth, year,
                        Long.parseLong(map.get("organizationId").toString()));
            }
            resultMap.put("firstMonthExpense", firstMonthExpense);
            resultMap.put("secondMonthExpense", secondMonthExpense);
            resultMap.put("thirdMonthExpense", thirdMonthExpense);
            resultMap.put("status", "success");

        } else {
            resultMap.put("status", "error");
            resultMap.put("msg", "Please provide value and key!̛");
        }
        return resultMap;
    }

    public Map FetchAllEmployeesOfOrganization(HttpHeaders header, Map map, String search) {
        JSONObject payload = new JSONObject();
        Integer month = map.get("start_date").toString().equals("1") ? Integer.parseInt(map.get("month").toString()) + 1
                : Integer.parseInt(map.get("month").toString());
        Integer year = Integer.parseInt(map.get("year").toString());
        if (month.equals(0)) {
            month = 12;
            year = year - 1;
        }
        payload.put("organizationId", map.get("organizationId").toString());
        payload.put("month", month);
        payload.put("year", year);
        payload.put("startDate", map.get("start_date").toString());
        payload.put("endDate", map.get("end_date").toString());

        String encryptedPayload = EncryptDecryptUtils.encrypt(payload.toString());

        Map employeeListResp = null;

        HttpEntity<?> entity = new HttpEntity<>(encryptedPayload, header);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(reimburshment_url + "/payrollleavecount/getAllEmployeeAbsentDetailsInBulk")
                .queryParam("search", search);
        String url = builder.toUriString();

        Map employeeListReq = restTemplate.exchange(url, HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()),
                    LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to employee list from the manage :: ", ex);
        }
        logger.info("response Timesheet=== " + employeeListResp);

        return employeeListResp;

    }

    public Map FetchAllEmployeesFromManageWithSite(HttpHeaders header, Map map, String search) {
        JSONObject payload = new JSONObject();
        Integer month = map.get("start_date").toString().equals("1") ? Integer.parseInt(map.get("month").toString()) + 1
                : Integer.parseInt(map.get("month").toString());
        Integer year = Integer.parseInt(map.get("year").toString());
        if (month.equals(0)) {
            month = 12;
            year = year - 1;
        }
        payload.put("organizationId", map.get("organizationId").toString());
        payload.put("month", month);
        payload.put("year", year);
        payload.put("startDate", map.get("start_date").toString());
        payload.put("endDate", map.get("end_date").toString());
        payload.put("siteId", map.get("siteId").toString());

        String encryptedPayload = EncryptDecryptUtils.encrypt(payload.toString());

        Map employeeListResp = null;

        HttpEntity<?> entity = new HttpEntity<>(encryptedPayload, header);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(reimburshment_url + "/attendance/getEmployeeAttendanceDetailsBySite")
                .queryParam("search", search);
        String url = builder.toUriString();

        Map employeeListReq = restTemplate.exchange(url, HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()),
                    LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to employee list from the manage :: ", ex);
        }
        logger.info("response Timesheet=== " + employeeListResp);

        return employeeListResp;

    }

    public Map findAllSavedPayRunDataByMonthYearOfEmployees(Integer month, Integer year, Long organizationId,
            String searchWord) {

        Map resultMap = new HashMap<>();
        double tds = 0;
        Double advance = 0.0;
        double total_esic = 0;
        double total_epf = 0;
        double total_professional_tax = 0;
        double total_tds = 0;
        double total_advance = 0;
        double totalPay = 0.0;
        double totalNetPay = 0.0;
        double taxes = 0.0;
        double preTax = 0.0;

        List<LinkedHashMap> finalEmpList = new ArrayList<>();
        try {
            List<LinkedCaseInsensitiveMap> SavedRunpay = runPayRepo.findAllSavedDataByMonthYearOfEmployee(month, year,
                    organizationId, searchWord);

            for (LinkedCaseInsensitiveMap employee : SavedRunpay) {
                double salary = Double.parseDouble(employee.get("salary").toString());
                totalPay = totalPay + salary;
                employee.put("runPayRollId", employee.get("run_pay_roll_id"));
                employee.put("name", employee.get("name"));
                employee.put("salary", employee.get("salary"));
                employee.put("payable", employee.get("payable"));
                employee.put("epf", employee.get("epf"));
                employee.put("esic", employee.get("esic"));
                employee.put("tds", employee.get("tds"));
                employee.put("advance", employee.get("advance"));
                employee.put("other_deductions", employee.get("other_deductions"));
                employee.put("adhoc", employee.get("adhoc"));
                employee.put("overtimePay", Math.round(Double.parseDouble(employee.get("overtime_pay").toString())));
                employee.put("Reimbursement", employee.get("reimburs"));
                employee.put("arrears", employee.get("arrears"));
                employee.put("totalHours", employee.get("total_hours"));
                employee.put("overTime", employee.get("over_time"));
                employee.put("bonus", employee.get("bonus"));
                employee.put("employee_type", employee.get("employee_type"));
                employee.put("professional_tax", employee.get("professional_tax"));
                employee.put("organizationId", employee.get("organization_id"));
                employee.put("email", employee.get("email"));
                employee.put("employer_epf", employee.get("employer_epf"));
                employee.put("working_day", employee.get("working_day"));
                employee.put("employee_code", employee.get("employee_code"));
                employee.put("employer_esic", employee.get("employer_esic"));
                employee.put("gratuity", employee.get("gratuity"));
                employee.put("bonus_deduction", employee.get("bonus_deduction"));
                employee.put("variable", employee.get("variable"));
                employee.put("ctc", employee.get("ctc"));
                employee.put("rate", employee.get("rate"));
                employee.put("bonus", employee.get("bonus"));
                employee.put("attendanceIncentives", employee.get("attendance_incentives"));
                employee.put("labourWelfareFund", employee.get("labour_welfare_fund"));
                employee.put("joiningDate", employee.get("joining_date"));

                double netSalary = Double.parseDouble(employee.get("net_payable").toString());
                totalNetPay = totalNetPay + netSalary;
                employee.put("net_payable", Math.round(netSalary));

                taxes = taxes + Double.parseDouble(employee.get("tds").toString())
                        + Double.parseDouble(employee.get("epf").toString())
                        + Double.parseDouble(employee.get("esic").toString())
                        + Double.parseDouble(employee.get("professional_tax").toString())
                        + Double.parseDouble(employee.get("advance").toString());
                total_epf += Double.parseDouble(employee.get("epf").toString());
                total_esic += Double.parseDouble(employee.get("esic").toString());
                total_advance += Double.parseDouble(employee.get("advance").toString());
                total_tds += Double.parseDouble(employee.get("tds").toString());
                total_professional_tax += Double.parseDouble(employee.get("professional_tax").toString());

                preTax = preTax + Double.parseDouble(employee.get("epf").toString())
                        + Double.parseDouble(employee.get("professional_tax").toString());
                employee.put("payRunMonth", employee.get("pay_run_month"));
                employee.put("payRunYear", employee.get("pay_run_year"));
                employee.put("alreadyRun", true);
                LinkedHashMap newEmployee = new LinkedHashMap();

                employee.forEach(newEmployee::put);

                finalEmpList.add(newEmployee);

            }
            resultMap.put("total_epf", total_epf);
            resultMap.put("total_esic", total_esic);
            resultMap.put("total_professional_tax", total_professional_tax);
            resultMap.put("total_tds", total_tds);
            resultMap.put("total_advance", total_advance);
            resultMap.put("tds", tds);
            resultMap.put("totalPay", totalPay);
            resultMap.put("totalNetPay", totalNetPay);
            resultMap.put("taxes", taxes);
            resultMap.put("preTax", preTax);
            resultMap.put("advance", advance);
            resultMap.put("finalEmpList", finalEmpList);
            resultMap.put("savedRunPay", SavedRunpay);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public void savedEmployeeAllowance(Map resultMap, Long sid, String employee_type, Double incentivesWorkingDay) {

        try {

            List<Integer> all_allowance_id = new ArrayList<>();
            List<String> allowanceTemplateId = new ArrayList<>();
            List<String> all_allowance_name = new ArrayList<>();
            List<Double> all_allowance_amount = new ArrayList<>();
            List<Double> all_allowance_payable_amount = new ArrayList<>();
            List<Double> minimumWorkingDays = new ArrayList<>();
            List<Boolean> aiFlags = new ArrayList<>();
            Map<String, List<Integer>> map_allowance_id = new HashMap<>();
            Map<String, List<String>> map_allowance_name = new HashMap<>();
            Map<String, List<Double>> map_allowance_amount = new HashMap<>();
            Map<String, List<Double>> map_allowance_payable_amount = new HashMap<>();
            Map attendanceIncentives = new HashMap();

            List<LinkedCaseInsensitiveMap> employeeAllowances;

            if (employee_type.equalsIgnoreCase("Consultant") || employee_type.equalsIgnoreCase("Intern")
                    || employee_type.equalsIgnoreCase("Contract")) {
                employeeAllowances = employeeAllowanceRepo.getSavedConsultantAllowances(sid);

                for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {
                    all_allowance_name.add(allowance.get("consultant_allowance_name").toString());
                    all_allowance_amount.add((Double) allowance.get("consultant_allowance_amount"));
                    all_allowance_payable_amount.add((Double) allowance.get("consultnat_allowance_payable_amount"));

                }
            } else {

                employeeAllowances = employeeAllowanceRepo.getSavedEmployeeAllowances(sid);

                for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {
                    if (allowance.get("minimum_working_day") != null) {
                        Double minumDays = Double.parseDouble(allowance.get("minimum_working_day").toString());
                        minimumWorkingDays.add(minumDays);

                    } else {
                        minimumWorkingDays.add(0.0);
                    }
                    if (allowance.get("ai_flag") != null) {
                        Boolean aiflag = Boolean.valueOf(allowance.get("ai_flag").toString());
                        aiFlags.add(aiflag);

                    } else {
                        aiFlags.add(false);
                    }

                    all_allowance_id.add(Integer.parseInt(allowance.get("allowance_id").toString()));
                    allowanceTemplateId.add(allowance.get("allowance_template_id") != null
                            ? allowance.get("allowance_template_id").toString()
                            : null);

                    all_allowance_name.add(allowance.get("allowance_name").toString());
                    if (allowance.get("allowance_name").toString().equalsIgnoreCase("Attendance Incentives")) {

                        Double actualPaybel = Double.parseDouble(allowance.get("allowance_payable_amount").toString());

                        all_allowance_amount.add((Double) allowance.get("allowance_amount"));
                        all_allowance_payable_amount.add((Double) allowance.get("allowance_payable_amount"));
                        resultMap.put("AttendanceIncentives", allowance.get("allowance_payable_amount"));

                    } else {
                        all_allowance_amount.add((Double) allowance.get("allowance_amount"));
                        all_allowance_payable_amount.add((Double) allowance.get("allowance_payable_amount"));
                    }

                    if (allowance.get("allowance_name").toString().equalsIgnoreCase("Reimbursement")) {
                        resultMap.put("Reimbursement", allowance.get("allowance_payable_amount"));

                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Arrears")) {
                        resultMap.put("arrears", allowance.get("allowance_payable_amount"));
                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Referral Allowance")) {
                        resultMap.put("referral", allowance.get("allowance_payable_amount"));

                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Bonus/Incentive")) {
                        resultMap.put("bonus", allowance.get("allowance_payable_amount"));

                    } else if (allowance.get("allowance_name").toString().equalsIgnoreCase("Overtime Allowance")
                            || allowance.get("allowance_name").toString().equalsIgnoreCase("Overtime")) {
                        resultMap.put("overtime", allowance.get("allowance_payable_amount"));
                    }

                }
            }
            if (!employee_type.equalsIgnoreCase("Consultant") && !employee_type.equalsIgnoreCase("Intern")) {
                map_allowance_id.put("allowanceId", all_allowance_id);
                resultMap.put("AllowanceId", map_allowance_id);
            }

            map_allowance_name.put("allowanceName", all_allowance_name);
            map_allowance_amount.put("allowanceAmount", all_allowance_amount);
            map_allowance_payable_amount.put("allowancePayableAmount", all_allowance_payable_amount);

            resultMap.put("AllowanceName", map_allowance_name);
            resultMap.put("AllowanceAmount", map_allowance_amount);
            resultMap.put("AllowancePayableAmount", map_allowance_payable_amount);
            resultMap.put("minimumWorkingDays", minimumWorkingDays);
            resultMap.put("aiFlags", aiFlags);
            resultMap.put("allowanceTemplateId", allowanceTemplateId);

        } catch (Exception e) {
            logger.error("error in savedEmployeeAllowance()" + " " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void savedEmployeeDeductions(Map resultMap, Long sid, String employee_type, Long organization_id) {
        List<Integer> all_deduction_id = new ArrayList<>();
        List<Double> all_ytd_deduction = new ArrayList<>();
        List<String> all_deduction_name = new ArrayList<>();
        List<Double> employerPercentage = new ArrayList<>();
        List<Long> deductionTemplateIds = new ArrayList<>();
        List<Double> all_deduction_amount = new ArrayList<>();
        List<Double> all_deduction_payable_amount = new ArrayList<>();
        Map<String, List<Integer>> map_deduction_id = new HashMap<>();
        Map<String, List<String>> map_deduction_name = new HashMap<>();
        Map<String, List<Double>> map_deduction_amount = new HashMap<>();
        Map<String, List<Double>> employee_deduction = new HashMap<>();
        Map<String, List<Long>> deductionTemplate = new HashMap<>();
        Map<String, List<Double>> map_deduction_payable_amount = new HashMap<>();
        Map<String, List<Double>> map_ytd_deduction_amount = new HashMap<>();

        List<LinkedCaseInsensitiveMap> employeeDeductions;
        if (employee_type.equalsIgnoreCase("Consultant") || employee_type.equalsIgnoreCase("Intern")
                || employee_type.equalsIgnoreCase("Contract")) {
            employeeDeductions = employeeDeductionRepo.getSavedConsultantDeductions(sid);

            for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {

                all_deduction_name.add(deduction.get("consultant_deduction_name").toString());
                all_deduction_amount.add((Double) deduction.get("consultant_deduction_amount"));
                all_deduction_payable_amount.add((Double) deduction.get("consultnat_deduction_payable_amount"));
            }

        } else {
            /// changes deduction code
            employeeDeductions = employeeDeductionRepo.getSavedEmployeeDeductions(sid);

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
                } else if (deduction.get("deduction_name").toString().equalsIgnoreCase("Labour Welfare Fund")) {
                    resultMap.put("Labour Welfare Fund", deduction.get("deduction_payable_amount"));
                }
                if (deduction.get("employer_percentage") != null) {
                    employerPercentage.add(Double.parseDouble(deduction.get("employer_percentage").toString()));
                } else {
                    employerPercentage.add(0.0);
                }
                if (deduction.get("deduction_template_id") != null) {
                    deductionTemplateIds.add(Long.parseLong(deduction.get("deduction_template_id").toString()));
                }

            }

        }

        if (!employee_type.equalsIgnoreCase("Consultant") && !employee_type.equalsIgnoreCase("Intern")) {
            map_deduction_id.put("deductionId", all_deduction_id);
            map_ytd_deduction_amount.put("ytdAmount", all_ytd_deduction);
            employee_deduction.put("employerPercentage", employerPercentage);
            resultMap.put("DeductionId", map_deduction_id);
            resultMap.put("employerPercentage", employee_deduction);
            resultMap.put("YtdDeduction", map_ytd_deduction_amount);
        }
        deductionTemplate.put("deduction_template_id", deductionTemplateIds);

        map_deduction_name.put("deductionName", all_deduction_name);
        map_deduction_amount.put("deductionAmount", all_deduction_amount);
        map_deduction_payable_amount.put("deductionPayableAmount", all_deduction_payable_amount);

        resultMap.put("DeductionName", map_deduction_name);
        resultMap.put("deduction_template_id", deductionTemplate);
        // resultMap.put("employerPercentage", employerPercentage);
        resultMap.put("DeductionAmount", map_deduction_amount);
        resultMap.put("DeductionPayableAmount", map_deduction_payable_amount);
        resultMap.put("tempDeduction", employeeDeductions);

        if (employee_type.equalsIgnoreCase("Consultant") || employee_type.equalsIgnoreCase("contract")) {

            List<LinkedCaseInsensitiveMap> typeoodeduction = employeeDeductionRepo.getTypeOfDeduction(organization_id,
                    employee_type);

            employeeDeductions.stream().forEach(e -> {

                if (e.get("consultant_deduction_name").toString().equalsIgnoreCase("tds")) {

                    e.put("type_of_deduction", "variable");
                }

                typeoodeduction.stream().forEach(e1 -> {

                    if (e.get("consultant_deduction_name").toString()
                            .equalsIgnoreCase(e1.get("deduction_name").toString())) {

                        e.put("type_of_deduction", e1.get("type_of_deduction"));

                    }

                });

            });
        }

    }

    public void savedEmployeeOtherAllowances(Map resultMap, Long sid) {
        List<LinkedCaseInsensitiveMap> employeeOtherAllowance = employeeOtherAllowanceRepo
                .getSavedEmployeeOtherAllowances(sid);
        for (LinkedCaseInsensitiveMap otherAllowances : employeeOtherAllowance) {
            resultMap.put("OtherAllowances", otherAllowances.get("amount"));
            resultMap.put("OtherPayableAllowances", otherAllowances.get("payable_amount"));
        }
    }

    public void getSalarydDetails(Map resultMap, LinkedHashMap employeeDetails) {
        resultMap.put("projectionMonth", employeeDetails.get("month"));
        resultMap.put("projectionYear", employeeDetails.get("year"));
        resultMap.put("salary_break_up_id", employeeDetails.get("sid"));
        resultMap.put("wages", employeeDetails.get("total_earning"));
        resultMap.put("rate", employeeDetails.get("rate"));
        resultMap.put("working_day", employeeDetails.get("working_day"));
        resultMap.put("percentage_change", employeeDetails.get("percentage_change"));
        resultMap.put("NetPayableAmount", employeeDetails.get("net_amount"));
        resultMap.put("WorkingDay", employeeDetails.get("working_day"));
        resultMap.put("TotalPayableDeduction", employeeDetails.get("total_deduction"));
        resultMap.put("salary", employeeDetails.get("total_earning"));
        resultMap.put("payableSalary", employeeDetails.get("total_payable_earning"));
        resultMap.put("payable_gross", employeeDetails.get("total_payable_earning"));
        resultMap.put("payableSalaryForRunPayroll", employeeDetails.get("payable_salary"));
        resultMap.put("totalHours", employeeDetails.get("total_hours"));
        resultMap.put("overTimeHours", employeeDetails.get("over_time"));
        resultMap.put("actual_day", employeeDetails.get("value") != null ? employeeDetails.get("value") : 0);
        resultMap.put("employeeActualDay", employeeDetails.get("value") != null ? employeeDetails.get("value") : 0);
        resultMap.put("approved_leave",
                employeeDetails.get("approved_leave") != null ? employeeDetails.get("approved_leave") : 0);
        resultMap.put("holidays", employeeDetails.get("holidays") != null ? employeeDetails.get("holidays") : 0);
        resultMap.put("present_day",
                employeeDetails.get("present_day") != null ? employeeDetails.get("present_day") : 0);
        resultMap.put("week_off", employeeDetails.get("week_off") != null ? employeeDetails.get("week_off") : 0);
        String mop = "Not Specified";
        if (employeeDetails.get("modeofpayment") != null) {
            if (!"".equals(employeeDetails.get("modeofpayment").toString())) {
                mop = employeeDetails.get("modeofpayment").toString();
            }
        }
        resultMap.put("modeofpayment", mop);
        resultMap.put("orgIds", employeeDetails.get("orgIds") != null ? employeeDetails.get("orgIds") : 0);
        resultMap.put("ytd_total_deduction",
                employeeDetails.get("ytd_total_deduction") != null ? employeeDetails.get("ytd_total_deduction") : 0);
        resultMap.put("lwp", employeeDetails.get("lwp") != null ? employeeDetails.get("lwp") : 0);
        resultMap.put("status", "success");
    }

    public Map performCalculationBasedOnWorkingDayOfEmployees(Double companyWorkingDays, Double employeeWorkingDays,
            Double absentDays, Map salaryData, Map map, LinkedHashMap employeeDetails) {

        // System.out.println("employeeDetails 854");
        // System.out.println(employeeDetails);
        // System.out.println(salaryData);

        String stateName = map.get("orgState") != null ? map.get("orgState").toString() : null;
        String lwfFlag = map.get("orgLwf") != null ? map.get("orgLwf").toString() : null;
        String ptFlag = map.get("orgPt") != null ? map.get("orgPt").toString() : null;

        String gender = employeeDetails.get("gender") != null ? employeeDetails.get("gender").toString() : null;
        Double payableSalary = salaryData.get("payableSalary") != null
                ? Double.parseDouble(salaryData.get("payableSalary").toString())
                : 0.0;
        // Double grossSalary=
        // salaryData.get("gross_salary")!=null?Double.parseDouble(salaryData.get("gross_salary").toString()):0.0;
        int selectedMonth = Integer.parseInt(map.get("month").toString());
        int fyYear = Integer.parseInt(map.get("year").toString());
        Map<String, List<Double>> a_payable_amount = new HashMap<>();
        Map<String, List<Double>> d_payable_amount = new HashMap<>();
        Long employeeId = Long.parseLong(employeeDetails.get("employee_id").toString());
        List<Double> deduction_payable_amount = new ArrayList<>();
        Double weekOff = salaryData.get("week_off") != null ? Double.parseDouble(salaryData.get("week_off").toString())
                : 0.0;
        Double gross_salary = employeeDetails.get("gross_salary") != null
                ? Double.parseDouble(employeeDetails.get("gross_salary").toString())
                : 0.0;

        Map resultMap = new HashMap<>();
        try {
            List<Double> allowanceAmount = (List<Double>) ((HashMap) salaryData.get("AllowanceAmount"))
                    .get("allowanceAmount");
            List<Double> minimumWorkingDay = (List<Double>) salaryData.get("minimumWorkingDays");
            List<Boolean> aiFlags = (List<Boolean>) salaryData.get("aiFlags");
            List<TempararyAllowance> tempSavedAllowances = tempAllowanceRepo.getEmployeeMonthlyTempAllowance(employeeId,
                    selectedMonth + 1, fyYear);
            List<TempararyDeduction> tempSavedDeductions = tempDeductionRepo.getUpdatedDeductionsofEmployee(employeeId,
                    selectedMonth + 1, fyYear);
            // List<Double> allowancePayableAmount = allowanceAmount.stream().map(a -> (a *
            // employeeWorkingDays) / companyWorkingDays).collect(Collectors.toList());
            int bonusYear = fyYear;
            if (selectedMonth == 0 || selectedMonth == 1 || selectedMonth == 2) {
                bonusYear = bonusYear - 1;
            }
            BonusYearly bonusAmount = bonusAmountRepository.bonusAmount(employeeId, bonusYear);
            int indexForAttendanceIncentive = -1;
            int indexForCLBAllowances = -1;
            int washingIndex = -1;
            int basicIndex = -1;
            Double washingAmount = 0.0;
            // double attendanceIncentiveValue=0.0;

            System.out.println("aiFlags");
            System.out.println(aiFlags);

            List<String> allowanceName = (List<String>) ((HashMap) salaryData.get("AllowanceName"))
                    .get("allowanceName");

            if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")
                    || employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")) {

                String keyToFind = "Attendance Incentives";
                indexForAttendanceIncentive = allowanceName.indexOf(keyToFind);
            }

            if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")
                    || employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")) {

                String keyToFind = "CLB Allowance";
                indexForCLBAllowances = allowanceName.indexOf(keyToFind);
            }

            List<String> allowanceNameLowerCase = allowanceName.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            // Define the terms to search for
            String term1 = "Washing Allowance".toLowerCase();
            String term2 = "Washing".toLowerCase();
            String term3 = "Washing Allowances".toLowerCase();
            String term4 = "Basic Salary".toLowerCase();

            // Find the index of the first matching term
            for (int i = 0; i < allowanceNameLowerCase.size(); i++) {
                if (allowanceNameLowerCase.get(i).equals(term1) || allowanceNameLowerCase.get(i).equals(term2)
                        || allowanceNameLowerCase.get(i).equals(term3)) {
                    washingIndex = i;
                    break;
                }
            }

            // find index of basic salary
            for (int i = 0; i < allowanceNameLowerCase.size(); i++) {
                if (allowanceNameLowerCase.get(i).equals(term4)) {
                    basicIndex = i;
                    break;
                }
            }

            salaryData.put("attendannceToMinus", 0);
            salaryData.put("ClbAllowance", 0);
            salaryData.put("attendanceIncentiveFlag", "false");
            Long cblAmount = 0l;

            List<Double> allowancePayableAmount = new ArrayList<>();

            for (int i = 0; i < allowanceAmount.size(); i++) {

                Double minumDay = 0.0;
                Boolean aiFlag = false;
                if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")
                        || employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")) {
                    minumDay = minimumWorkingDay.get(i);
                    aiFlag = aiFlags.get(i);
                }

                Double allowancePayable = allowanceAmount.get(i);
                if (indexForAttendanceIncentive == i) {

                    if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")) {
                        salaryData.put("attendannceToMinus", Math.round(gross_salary / companyWorkingDays));
                        salaryData.put("attendanceIncentiveFlag", aiFlag);
                    } else {
                        salaryData.put("attendannceToMinus",
                                Math.round((allowancePayable * employeeWorkingDays) / companyWorkingDays));
                        salaryData.put("attendanceIncentiveFlag", aiFlag);
                    }

                }

                if (indexForCLBAllowances == i) {

                    if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")) {
                        salaryData.put("ClbAllowance", Math.round(gross_salary / companyWorkingDays));
                        cblAmount = Math.round(gross_salary / companyWorkingDays);

                    } else {
                        salaryData.put("ClbAllowance",
                                Math.round((allowancePayable * employeeWorkingDays) / companyWorkingDays));
                        cblAmount = Math.round((allowancePayable * employeeWorkingDays) / companyWorkingDays);
                    }

                }

                if (washingIndex == i) {
                    washingAmount = (allowancePayable * employeeWorkingDays) / companyWorkingDays;
                }

                if (Objects.equals(minumDay, 0.0)) {
                    allowancePayableAmount
                            .add((double) Math.round((allowancePayable * employeeWorkingDays) / companyWorkingDays));
                } else {

                    Double tempEmployeeWorkingDays = employeeWorkingDays;
                    // if(employeeDetails.get("employee_type").toString().equalsIgnoreCase("full
                    // time")){
                    // tempEmployeeWorkingDays=tempEmployeeWorkingDays-weekOff;
                    // }

                    if (minumDay < tempEmployeeWorkingDays) {
                        allowancePayableAmount.add(
                                (double) Math.round((allowancePayable * employeeWorkingDays) / companyWorkingDays));
                    } else {
                        System.out.println("inside else");
                        if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")) {

                            if (Double.compare(tempEmployeeWorkingDays, companyWorkingDays) == 0) {
                                allowancePayableAmount.add((double) Math.round(gross_salary / companyWorkingDays));
                            } else {
                                allowancePayableAmount.add(0.0);
                            }

                        } else {
                            allowancePayableAmount.add(0.0);
                        }

                    }
                }
            }

            double payableGross = 0;

            if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {
                payableGross = (Math
                        .round(((Double.parseDouble(salaryData.get("salary").toString())) / 26) * employeeWorkingDays));
            } else {
                payableGross = (Math.round((Double) salaryData.get("salary") * employeeWorkingDays)
                        / companyWorkingDays);
            }

            if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Full time")
                    || employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {

                double otherPayableAllowances = salaryData.get("OtherAllowances") != null
                        ? Math.round(
                                ((Double) salaryData.get("OtherAllowances") * employeeWorkingDays) / companyWorkingDays)
                        : 0;
                salaryData.put("OtherPayableAllowances", otherPayableAllowances);
            }

            a_payable_amount.put("allowancePayableAmount", allowancePayableAmount);

            salaryData.put("AllowancePayableAmount", a_payable_amount);

            /**
             * calculating additional allowance given to employee (working day
             * excluded). *
             */
            Double additional_allowance = 0.0;
            try {
                additional_allowance = (Double) salaryData.get("bonus") + (Double) salaryData.get("referral")
                        + (Double) salaryData.get("Reimbursement") + (Double) salaryData.get("overtime");
            } catch (Exception e) {
                // e.printStackTrace();
                additional_allowance = 0.0;
            }

            // calculation of overtime for worker
            if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {

                List<String> allwanceName = (List<String>) ((HashMap) salaryData.get("AllowanceName"))
                        .get("allowanceName");

                int idx1 = -1;
                int incentivesIndex = 0;
                int clbIndex = 0;
                for (String name : allwanceName) {
                    idx1++;
                    if (name.equalsIgnoreCase("Overtime Allowance") || name.equalsIgnoreCase("Overtime")
                            || name.equalsIgnoreCase("OvertimeAllowance")) {

                        Double overtime_vale = 0.0;
                        Double dailyWages = Double.parseDouble(salaryData.get("wages").toString()) / 26;

                        Double monthlyOvertime = 0.0;
                        monthlyOvertime = Double.parseDouble(salaryData.get("overTimeHours").toString().split(":")[0]);

                        // getting total Hours hours
                        Double totalOvertimeHours = 0.0;
                        totalOvertimeHours = salaryData.get("totalHours") != null
                                ? Double.parseDouble(salaryData.get("totalHours").toString().split(":")[0])
                                : 0.0;
                        // Double previousOvertime= salaryData.get("overtime")!=null?
                        // Double.parseDouble(salaryData.get("overtime").toString()):0.0;
                        // Double finalDailyWages = dailyWages - amount;
                        Double rateForMange = employeeDetails.get("rateOfPaymentPolicy") != null
                                ? Double.parseDouble(employeeDetails.get("rateOfPaymentPolicy").toString())
                                : 1.0;
                        if (rateForMange == 0.0) {
                            rateForMange = 1.0;
                        }
                        Double rate = employeeDetails.get("rate") != null
                                ? Double.parseDouble(employeeDetails.get("rate").toString())
                                : 1.0;
                        overtime_vale = (rate * monthlyOvertime * rateForMange);
                        Double tempOverTime[] = new Double[1];
                        tempOverTime[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Overtime Allowance")
                                    || data.getName().equalsIgnoreCase("Overtime")
                                    || data.getName().equalsIgnoreCase("OvertimeAllowance")) {
                                tempOverTime[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            overtime_vale = tempOverTime[0];
                        }
                        salaryData.put("rate", rate * rateForMange);
                        salaryData.put("overtimeManage", overtime_vale);
                        salaryData.put("overTimeHours", monthlyOvertime);
                        salaryData.put("totalHours", totalOvertimeHours);

                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");

                        allowancePayableAmountForOvertime.get(idx1);

                        allowancePayableAmountForOvertime.set(idx1, overtime_vale);

                        Map<String, Object> finalvalue = new HashMap<>();
                        finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                        salaryData.put("AllowancePayableAmount", finalvalue);

                        // adding addition allowance
                        additional_allowance = additional_allowance + overtime_vale;
                    } else if (name.equalsIgnoreCase("Attendance Incentives")) {
                        incentivesIndex = idx1;
                        salaryData.put("AttendanceIncentives", allowancePayableAmount.get(incentivesIndex));
                        additional_allowance = additional_allowance + allowancePayableAmount.get(incentivesIndex);
                    } else if (name.equalsIgnoreCase("CLB Allowance")) {

                        List<Double> allowancePayableAmountForClb = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");

                        allowancePayableAmountForClb.get(idx1);

                        allowancePayableAmountForClb.set(idx1, (double) cblAmount);

                        Map<String, Object> finalvalue = new HashMap<>();
                        finalvalue.put("allowancePayableAmount", allowancePayableAmountForClb);
                        salaryData.put("AllowancePayableAmount", finalvalue);

                        // adding addition allowance
                        additional_allowance = additional_allowance + cblAmount;

                        clbIndex = idx1;
                        // salaryData.put("AttendanceIncentives",
                        // allowancePayableAmount.get(incentivesIndex));
                        additional_allowance = additional_allowance + allowancePayableAmount.get(incentivesIndex);
                    } else if (name.equalsIgnoreCase("Reimbursement") || name.equalsIgnoreCase("Reimburs")) {
                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Reimbursement")
                                    || data.getName().equalsIgnoreCase("Reimburs")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);
                            salaryData.put("Reimbursement", tempReimbursement[0]);
                            additional_allowance = additional_allowance + tempReimbursement[0];
                        }
                    } else if (name.equalsIgnoreCase("Bonus/Incentive")) {
                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        List<Long> allowanceIdForBonus = (List<Long>) ((HashMap) salaryData.get("AllowanceId"))
                                .get("allowanceId");
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Bonus/Incentive")
                                    || data.getName().equalsIgnoreCase("Bonus")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);

                            salaryData.put("bonus", tempReimbursement[0]);
                            additional_allowance = additional_allowance + tempReimbursement[0];
                        } else {

                            if (bonusAmount != null) {

                                Number value = (Number) allowanceIdForBonus.get(idx1);
                                Long bonusIds = value.longValue();

                                List<LinkedCaseInsensitiveMap> closingAllowanceData = allowanceRepo
                                        .closingAllowanceData(bonusIds);

                                boolean found = closingAllowanceData.stream()
                                        .anyMatch(data -> (selectedMonth + 1) == Integer
                                                .parseInt(data.get("month").toString()));

                                if (found) {
                                    tempReimbursement[0] = bonusAmount.getBonus() + bonusAmount.getExgratia();
                                }

                                allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                                Map<String, Object> finalvalue = new HashMap<>();
                                finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                                salaryData.put("AllowancePayableAmount", finalvalue);

                                salaryData.put("bonus", tempReimbursement[0]);
                                additional_allowance = additional_allowance + tempReimbursement[0];
                            }
                        }

                    } else if (name.equalsIgnoreCase("Arrears")) {
                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Arrears")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);

                            salaryData.put("arrears", tempReimbursement[0]);
                            additional_allowance = additional_allowance + tempReimbursement[0];
                        }
                    }

                }

                Double manageOvertime = salaryData.get("overtimeManage") != null
                        ? Double.parseDouble(salaryData.get("overtimeManage").toString())
                        : 0.0;

                Double previousOvertime = salaryData.get("overtime") != null
                        ? Double.parseDouble(salaryData.get("overtime").toString())
                        : 0.0;
                if (previousOvertime != 0) {

                    salaryData.put("overtime", previousOvertime);
                } else {
                    salaryData.put("overtime", previousOvertime + manageOvertime);
                }

            } // calculation of overtime for full time
            else if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("full time")
                    || employeeDetails.get("employee_type").toString().equalsIgnoreCase("probation")
                    || employeeDetails.get("employee_type").toString().equalsIgnoreCase("permanent")) {
                List<String> allwanceName = (List<String>) ((HashMap) salaryData.get("AllowanceName"))
                        .get("allowanceName");

                int idx1 = -1;
                int incentivesIndex = 0;
                for (String name : allwanceName) {
                    idx1++;
                    if (name.equalsIgnoreCase("Overtime Allowance") || name.equalsIgnoreCase("Overtime")
                            || name.equalsIgnoreCase("OvertimeAllowance")) {
                        Double overtime_vale = 0.0;
                        if (salaryData.get("projectionMonth") != null) {

                            List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                    .get("AllowancePayableAmount")).get("allowancePayableAmount");

                            allowancePayableAmountForOvertime.get(idx1);
                            overtime_vale = Double.parseDouble(salaryData.get("overtime").toString());
                            allowancePayableAmountForOvertime.set(idx1, overtime_vale);

                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);
                            salaryData.put("rate", salaryData.get("rate") != null ? salaryData.get("rate") : 0);
                            salaryData.put("overTimeHours",
                                    salaryData.get("overTimeHours") != null ? salaryData.get("overTimeHours") : 0);
                            salaryData.put("totalHours",
                                    salaryData.get("overTimeHours") != null ? salaryData.get("overTimeHours") : 0);

                            // adding addition allowance
                            additional_allowance = additional_allowance + overtime_vale;
                        } else {

                            Double standard_hours = employeeDetails.get("standard_hours") != null
                                    ? Double.parseDouble(employeeDetails.get("standard_hours").toString())
                                    : 0;
                            Double actualDays = employeeDetails.get("actual_duration") != null
                                    ? Double.parseDouble(employeeDetails.get("actual_duration").toString())
                                    : 0;
                            Double rateOfPaymentPolicy = employeeDetails.get("rateOfPaymentPolicy") != null
                                    ? Double.parseDouble(employeeDetails.get("rateOfPaymentPolicy").toString())
                                    : 1;
                            Double rateSaveInStandard = employeeDetails.get("rate") != null
                                    ? Double.parseDouble(employeeDetails.get("rate").toString())
                                    : 0;
                            Double rate = 0.0;
                            Double grossSalary = 0.0;
                            if (rateSaveInStandard > 0) {

                                grossSalary = (rateSaveInStandard * 30) / actualDays;
                            } else {
                                grossSalary = Double.parseDouble(salaryData.get("wages").toString()) / actualDays;
                            }

                            if (standard_hours > 0) {
                                rate = grossSalary / standard_hours;
                            }

                            rate = rate * rateOfPaymentPolicy;
                            rate = Math.round(rate * 100.0) / 100.0;
                            overtime_vale = rate * Double.parseDouble(employeeDetails.get("over_time").toString());
                            overtime_vale = Math.round(overtime_vale * 100.0) / 100.0;
                            List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                    .get("AllowancePayableAmount")).get("allowancePayableAmount");

                            allowancePayableAmountForOvertime.get(idx1);

                            Double tempOverTime[] = new Double[1];
                            tempOverTime[0] = 0.0;

                            tempSavedAllowances.stream().forEach(data -> {
                                if (data.getName().equalsIgnoreCase("Overtime Allowance")
                                        || data.getName().equalsIgnoreCase("Overtime")
                                        || data.getName().equalsIgnoreCase("OvertimeAllowance")) {
                                    tempOverTime[0] = data.getAmount();
                                }
                            });

                            if (!tempSavedAllowances.isEmpty()) {
                                overtime_vale = tempOverTime[0];
                            }

                            allowancePayableAmountForOvertime.set(idx1, overtime_vale);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);
                            salaryData.put("rate", rate);
                            salaryData.put("overTimeHours", employeeDetails.get("over_time").toString());
                            salaryData.put("totalHours", employeeDetails.get("over_time").toString());
                            salaryData.put("overtime", overtime_vale);

                            // adding addition allowance
                            additional_allowance = additional_allowance + overtime_vale;

                        }
                    } else if (name.equalsIgnoreCase("Attendance Incentives")) {
                        incentivesIndex = idx1;
                        salaryData.put("AttendanceIncentives", allowancePayableAmount.get(incentivesIndex));
                        additional_allowance = additional_allowance + allowancePayableAmount.get(incentivesIndex);
                    }

                    else if (name.equalsIgnoreCase("CLB Allowance")) {

                        List<Double> allowancePayableAmountForClb = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");

                        allowancePayableAmountForClb.get(idx1);

                        allowancePayableAmountForClb.set(idx1, (double) cblAmount);

                        Map<String, Object> finalvalue = new HashMap<>();
                        finalvalue.put("allowancePayableAmount", allowancePayableAmountForClb);
                        salaryData.put("AllowancePayableAmount", finalvalue);

                        // adding addition allowance
                        additional_allowance = additional_allowance + cblAmount;

                    }

                    else if (name.equalsIgnoreCase("Reimbursement") || name.equalsIgnoreCase("Reimburs")) {
                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Reimbursement")
                                    || data.getName().equalsIgnoreCase("Reimburs")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);

                            salaryData.put("Reimbursement", tempReimbursement[0]);
                            additional_allowance = additional_allowance + tempReimbursement[0];
                        }
                    } else if (name.equalsIgnoreCase("Bonus/Incentive")) {
                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        List<Long> allowanceIdForBonus = (List<Long>) ((HashMap) salaryData.get("AllowanceId"))
                                .get("allowanceId");
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Bonus/Incentive")
                                    || data.getName().equalsIgnoreCase("Bonus")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);

                            salaryData.put("bonus", tempReimbursement[0]);
                            additional_allowance = additional_allowance + tempReimbursement[0];
                        } else {

                            if (bonusAmount != null) {
                                Number value = (Number) allowanceIdForBonus.get(idx1);
                                Long bonusIds = value.longValue();

                                List<LinkedCaseInsensitiveMap> closingAllowanceData = allowanceRepo
                                        .closingAllowanceData(bonusIds);

                                boolean found = closingAllowanceData.stream()
                                        .anyMatch(data -> (selectedMonth + 1) == Integer
                                                .parseInt(data.get("month").toString()));

                                if (found) {
                                    tempReimbursement[0] = bonusAmount.getBonus() + bonusAmount.getExgratia();
                                }

                                allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                                Map<String, Object> finalvalue = new HashMap<>();
                                finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                                salaryData.put("AllowancePayableAmount", finalvalue);

                                salaryData.put("bonus", tempReimbursement[0]);
                                additional_allowance = additional_allowance + tempReimbursement[0];
                            }
                        }

                    } else if (name.equalsIgnoreCase("Arrears")) {
                        List<Double> allowancePayableAmountForOvertime = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedAllowances.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Arrears")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedAllowances.isEmpty()) {
                            allowancePayableAmountForOvertime.set(idx1, tempReimbursement[0]);
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableAmountForOvertime);
                            salaryData.put("AllowancePayableAmount", finalvalue);

                            salaryData.put("arrears", tempReimbursement[0]);
                            additional_allowance = additional_allowance + tempReimbursement[0];
                        }
                    } else if (name.equalsIgnoreCase("Overtime Closing Allowance")
                            || name.equalsIgnoreCase("Overtime Closing Allowances")) {
                        List<Double> allowancePayableClosingAllowance = (List<Double>) ((HashMap) salaryData
                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                        if (employeeDetails.containsKey("Overtime Closing Allowance")) {
                            allowancePayableClosingAllowance.set(idx1,
                                    Double.parseDouble(employeeDetails.get("Overtime Closing Allowance").toString()));
                            Map<String, Object> finalvalue = new HashMap<>();
                            finalvalue.put("allowancePayableAmount", allowancePayableClosingAllowance);
                            salaryData.put("AllowancePayableAmount", finalvalue);

                            salaryData.put(name,
                                    Double.parseDouble(employeeDetails.get("Overtime Closing Allowance").toString()));
                            additional_allowance = additional_allowance
                                    + Double.parseDouble(employeeDetails.get("Overtime Closing Allowance").toString());
                        }

                    }

                }

            }
            // calculation of overtime for full time

            List<String> deductionName = (List<String>) ((HashMap) salaryData.get("DeductionName"))
                    .get("deductionName");
            List<Double> deductionAmount = (List<Double>) ((HashMap) salaryData.get("DeductionAmount"))
                    .get("deductionAmount");
            List<Double> deductionPayableAmountForAdvance = (List<Double>) ((HashMap) salaryData
                    .get("DeductionPayableAmount")).get("deductionPayableAmount");
            List<LinkedCaseInsensitiveMap> tempDeduction = (List<LinkedCaseInsensitiveMap>) salaryData
                    .get("tempDeduction");

            Double otherdeduction = 0.0;

            List<DeductionLoan> employeeDeductionLoad = deductionLoanRepository.getEmployeeDeductionLoan(
                    Long.parseLong(employeeDetails.get("employeeId").toString()),
                    Integer.parseInt(map.get("month").toString()) + 1, Integer.parseInt(map.get("year").toString()));
            List<LinkedCaseInsensitiveMap> jsonForLoanDeduction = new ArrayList<>();

            int idx = -1;
            for (String name : deductionName) {

                idx++;
                if (name.equalsIgnoreCase("Advance")) {
                    Double advanceAmount = 0.0;

                    List<LinkedCaseInsensitiveMap> employeeLoan;
                    if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {
                        employeeLoan = workerLoanRepo.getLoanForSalaryBreakup(
                                Long.parseLong(employeeDetails.get("employeeId").toString()),
                                Long.parseLong(employeeDetails.get("organization_id").toString()),
                                Integer.parseInt(map.get("month").toString()) + 1,
                                Integer.parseInt(map.get("year").toString()));
                    } else {
                        employeeLoan = employeeLoanRepo.getLoanForSalaryBreakup(
                                Long.parseLong(employeeDetails.get("employeeId").toString()),
                                Long.parseLong(employeeDetails.get("organization_id").toString()),
                                Integer.parseInt(map.get("month").toString()) + 1,
                                Integer.parseInt(map.get("year").toString()));
                    }
                    if (!employeeLoan.isEmpty()) {
                        for (LinkedCaseInsensitiveMap emp_loan : employeeLoan) {
                            if (emp_loan.containsKey("remaining_amount") && emp_loan.get("remaining_amount") != null) {
                                if ((Double) emp_loan.get("remaining_amount") > 0) {
                                    if ((Double) emp_loan.get("remaining_amount") > (Double) emp_loan
                                            .get("installment_amount")) {
                                        advanceAmount += (Double) emp_loan.get("installment_amount");
                                    } else {
                                        advanceAmount += (Double) emp_loan.get("remaining_amount");

                                    }
                                } else {

                                    advanceAmount += 0;
                                }
                            }
                        }
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedDeductions.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Advance")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });

                        if (!tempSavedDeductions.isEmpty()) {
                            advanceAmount = tempReimbursement[0];
                        }

                        deduction_payable_amount.add(advanceAmount);
                    } else {
                        Double tempReimbursement[] = new Double[1];
                        tempReimbursement[0] = 0.0;
                        tempSavedDeductions.stream().forEach(data -> {
                            if (data.getName().equalsIgnoreCase("Advance")) {
                                tempReimbursement[0] = data.getAmount();
                            }
                        });
                        if (!tempSavedDeductions.isEmpty()) {
                            deduction_payable_amount.add(tempReimbursement[0]);
                        } else {
                            deduction_payable_amount.add(deductionPayableAmountForAdvance.get(idx));
                        }
                        // deduction_payable_amount.add(0.0);

                    }
                } else if (name.equalsIgnoreCase("Income Tax")) {

                    List<Double> values = (List<Double>) ((HashMap) (salaryData.get("DeductionPayableAmount")))
                            .get("deductionPayableAmount");
                    deduction_payable_amount.add(values.get(idx));
                } else if (name.equalsIgnoreCase("Professional Tax")) {
                    // payableSalary

                    Double profeesionalAmount = employeeService.calculateEmployeeProfessionalTax(gender, payableGross,
                            selectedMonth + 1, fyYear, stateName);
                    if (employeeWorkingDays == 0) {
                        profeesionalAmount = 0.0;
                    }

                    if (ptFlag != null && ptFlag.equalsIgnoreCase("Yes")) {
                        deduction_payable_amount.add(profeesionalAmount);
                    } else {
                        deduction_payable_amount.add(0.0);
                    }

                } else if (name.equalsIgnoreCase("Labour Welfare Fund")) {

                    List<LabourLawDeduction> labourDeduction = labourLawRepo.fetchLabourLawPolicyByStatename(stateName);

                    if (labourDeduction.size() <= 0) {
                        deduction_payable_amount.add(0.0);
                    } else {

                        // org Level condition

                        if (lwfFlag != null && lwfFlag.equalsIgnoreCase("Yes")) {

                            labourDeduction.stream().forEach(data -> {
                                if (data.getStart() <= payableSalary && data.getEnd() >= payableSalary) {
                                    if (data.getFrequencyOfDeduction().equalsIgnoreCase("Monthly")) {
                                        if (data.getPercentageOfSalary() == null
                                                || data.getPercentageOfSalary() == 0.0) {
                                            deduction_payable_amount.add(data.getEmployeeDeduction());
                                        } else {

                                            double deduction = this.calculateDeduction(payableSalary, employeeDetails,
                                                    salaryData, employeeWorkingDays, data);

                                            deduction_payable_amount.add(deduction);

                                        }
                                    } else if (data.getFrequencyOfDeduction().equalsIgnoreCase("Half yearly")) {

                                        if (selectedMonth == 8 || selectedMonth == 2) {
                                            if (data.getPercentageOfSalary() == null
                                                    || data.getPercentageOfSalary() == 0.0) {
                                                deduction_payable_amount.add(data.getEmployeeDeduction());
                                            } else {

                                                double deduction = this.calculateDeduction(payableSalary,
                                                        employeeDetails, salaryData, employeeWorkingDays, data);
                                                deduction_payable_amount.add(deduction);
                                            }
                                        } else {
                                            deduction_payable_amount.add(0.0);
                                        }
                                    } else if (data.getFrequencyOfDeduction().equalsIgnoreCase("Quaterly")) {
                                        if (selectedMonth == 8 || selectedMonth == 2 || selectedMonth == 5
                                                || selectedMonth == 11) {
                                            if (data.getPercentageOfSalary() == null
                                                    || data.getPercentageOfSalary() == 0.0) {
                                                deduction_payable_amount.add(data.getEmployeeDeduction());
                                            } else {

                                                double deduction = this.calculateDeduction(payableSalary,
                                                        employeeDetails, salaryData, employeeWorkingDays, data);
                                                deduction_payable_amount.add(deduction);
                                            }
                                        } else {
                                            deduction_payable_amount.add(0.0);
                                        }
                                    } else if (data.getFrequencyOfDeduction().equalsIgnoreCase("Yearly")) {
                                        if (selectedMonth == 2) {
                                            if (data.getPercentageOfSalary() == null
                                                    || data.getPercentageOfSalary() == 0.0) {
                                                deduction_payable_amount.add(data.getEmployeeDeduction());
                                            } else {

                                                double deduction = this.calculateDeduction(payableSalary,
                                                        employeeDetails, salaryData, employeeWorkingDays, data);

                                                deduction_payable_amount.add(deduction);
                                            }
                                        } else {
                                            deduction_payable_amount.add(0.0);
                                        }
                                    }
                                }
                                // else {
                                // deduction_payable_amount.add(0.0);
                                // }
                            });

                        } else {
                            deduction_payable_amount.add(0.0);
                        }

                    }
                } else {
                    LinkedCaseInsensitiveMap tempdeductionname = tempDeduction.get(idx);

                    if (tempdeductionname.get("type_of_deduction") != null
                            && tempdeductionname.get("type_of_deduction").toString().equalsIgnoreCase("variable")) {
                        deduction_payable_amount
                                .add((deductionAmount.get(idx) * employeeWorkingDays) / companyWorkingDays);
                        if ((!name.equalsIgnoreCase("epf") && !name.equalsIgnoreCase("esic")
                                && !name.equalsIgnoreCase("tds"))) {

                            otherdeduction = otherdeduction
                                    + (deductionAmount.get(idx) * employeeWorkingDays) / companyWorkingDays;
                        }
                    } else {

                        Double[] tempdeduction = new Double[1];
                        tempdeduction[0] = 0.0;

                        // deduction_payable_amount.add(deductionAmount.get(idx));
                        if (!name.equalsIgnoreCase("tds")) {

                            /*
                             * Deduction loan code
                             **/
                            if (!employeeDeductionLoad.isEmpty()) {
                                Double[] multipleLoan = new Double[1];
                                multipleLoan[0] = 0.0;
                                Double[] tempValueOfDeductionLoan = new Double[1];
                                tempValueOfDeductionLoan[0] = 0.0;

                                employeeDeductionLoad.stream().forEach(d -> {

                                    if (d.getLoanType() != null && d.getLoanType().equalsIgnoreCase(name)) {

                                        if (d.getRemainingAmount() > 0) {

                                            if (d.getRemainingAmount() > d.getMonthlyInstallment()) {
                                                multipleLoan[0] = multipleLoan[0] + d.getMonthlyInstallment();
                                                tempValueOfDeductionLoan[0] = d.getMonthlyInstallment();
                                            } else {
                                                multipleLoan[0] = multipleLoan[0] + d.getRemainingAmount();
                                                tempValueOfDeductionLoan[0] = d.getRemainingAmount();
                                            }

                                            LinkedCaseInsensitiveMap json = new LinkedCaseInsensitiveMap();
                                            json.put("deductionId", d.getLoanTypeId());
                                            json.put("primaryKey", d.getDeductionLoanId());
                                            json.put("amount", tempValueOfDeductionLoan[0]);
                                            json.put("employeeId", d.getEmployeeId());

                                            jsonForLoanDeduction.add(json);

                                            tempdeduction[0] = multipleLoan[0];

                                        }

                                    }
                                });

                            }

                            otherdeduction = otherdeduction + deductionAmount.get(idx) + tempdeduction[0];
                        }
                        deduction_payable_amount.add(deductionAmount.get(idx) + tempdeduction[0]);

                        if (name.equalsIgnoreCase("Other Deductions")) {

                            Double tempReimbursement[] = new Double[1];
                            tempReimbursement[0] = 0.0;
                            tempSavedDeductions.stream().forEach(data -> {
                                if (data.getName().equalsIgnoreCase("Other Deductions")) {
                                    tempReimbursement[0] = data.getAmount();
                                }
                            });
                            if (!tempSavedDeductions.isEmpty()) {
                                deduction_payable_amount.set(idx, tempReimbursement[0]);
                                deductionPayableAmountForAdvance.set(idx, tempReimbursement[0]);
                            } else {
                                deduction_payable_amount.set(idx, deductionPayableAmountForAdvance.get(idx));
                            }

                            salaryData.put("otherDeductionValue", deductionPayableAmountForAdvance.get(idx));
                            otherdeduction = otherdeduction + deductionPayableAmountForAdvance.get(idx);
                        }

                    }

                }
            }

            // System.out.println("deduction_payable_amount 1378"
            // +deduction_payable_amount.toString());
            int indexDeduction = deductionName.indexOf("ESIC");
            if (indexDeduction != -1) {
                Double actualEsic = deductionPayableAmountForAdvance.get(indexDeduction);
                if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {
                    if (salaryData.get("salary") != null) {
                        if (employeeDetails.get("is_esic") == null) {
                            if (map.get("orgEsic") != null) {
                                if (map.get("orgEsic").toString().equalsIgnoreCase("Yes")) {
                                    if (Double.parseDouble(salaryData.get("salary").toString()) <= 21000.0) {
                                        Double overtime = salaryData.get("overtime") != null
                                                ? Double.parseDouble(salaryData.get("overtime").toString())
                                                : 0.0;
                                        // Double esic = ((((Double.parseDouble(salaryData.get("salary").toString()) /
                                        // 26) * employeeWorkingDays) ) + overtime) * 0.75 / 100;

                                        Double esic = ((actualEsic / 26) * employeeWorkingDays)
                                                + (overtime * 0.75 / 100);

                                        double rounded = Math.round(esic);
                                        deduction_payable_amount.set(indexDeduction, rounded);
                                    } else {
                                        double rounded = Math.round(0.0);
                                        deduction_payable_amount.set(indexDeduction, rounded);
                                    }
                                } else {
                                    double rounded = Math.round(0.0);
                                    deduction_payable_amount.set(indexDeduction, rounded);
                                }
                            } else {
                                double rounded = Math.round(0.0);
                                deduction_payable_amount.set(indexDeduction, rounded);
                            }
                        } else {
                            if (employeeDetails.get("is_esic").toString().equalsIgnoreCase("Yes")) {
                                Double overtime = salaryData.get("overtime") != null
                                        ? Double.parseDouble(salaryData.get("overtime").toString())
                                        : 0.0;
                                // Double esic = ((((Double.parseDouble(salaryData.get("salary").toString()) /
                                // 26) * employeeWorkingDays) - washingAmount) + overtime) * 0.75 / 100;

                                Double esic = ((actualEsic / 26) * employeeWorkingDays) + (overtime * 0.75 / 100);
                                double rounded = Math.round(esic);
                                deduction_payable_amount.set(indexDeduction, rounded);
                            } else {
                                double rounded = Math.round(0.0);
                                deduction_payable_amount.set(indexDeduction, rounded);
                            }

                        }

                    }

                } else if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Full time")
                        || employeeDetails.get("employee_type").toString().equalsIgnoreCase("Probation")
                        || employeeDetails.get("employee_type").toString().equalsIgnoreCase("Permanent")) {
                    if (salaryData.get("salary") != null) {
                        if (employeeDetails.get("is_esic") == null) {
                            if (map.get("orgEsic") != null) {
                                if (map.get("orgEsic").toString().equalsIgnoreCase("Yes")) {
                                    if (Double.parseDouble(salaryData.get("salary").toString()) <= 21000.0) {
                                        Double overtime = salaryData.get("overtime") != null
                                                ? Double.parseDouble(salaryData.get("overtime").toString())
                                                : 0.0;
                                        // Double esic = ((((Double.parseDouble(salaryData.get("salary").toString()) /
                                        // companyWorkingDays * employeeWorkingDays)) - washingAmount) + overtime) *
                                        // 0.75 / 100;
                                        Double esic = (((actualEsic / companyWorkingDays) * employeeWorkingDays))
                                                + (overtime * 0.75 / 100);
                                        double rounded = Math.round(esic);
                                        deduction_payable_amount.set(indexDeduction, rounded);
                                    } else {
                                        double rounded = Math.round(0.0);
                                        deduction_payable_amount.set(indexDeduction, rounded);
                                    }
                                } else {
                                    double rounded = Math.round(0.0);
                                    deduction_payable_amount.set(indexDeduction, rounded);
                                }

                            } else {
                                double rounded = Math.round(0.0);
                                deduction_payable_amount.set(indexDeduction, rounded);
                            }
                        } else {
                            if (employeeDetails.get("is_esic").toString().equalsIgnoreCase("Yes")) {

                                Double overtime = salaryData.get("overtime") != null
                                        ? Double.parseDouble(salaryData.get("overtime").toString())
                                        : 0.0;
                                // Double esic = ((((Double.parseDouble(salaryData.get("salary").toString()) /
                                // companyWorkingDays * employeeWorkingDays)) - washingAmount) + overtime) *
                                // 0.75 / 100;
                                Double esic = (((actualEsic / companyWorkingDays) * employeeWorkingDays))
                                        + (overtime * 0.75 / 100);
                                double rounded = Math.round(esic);
                                deduction_payable_amount.set(indexDeduction, rounded);
                            } else {
                                double rounded = Math.round(0.0);
                                deduction_payable_amount.set(indexDeduction, rounded);
                            }

                        }

                    }

                }

            }

            int indexEPF = deductionName.indexOf("EPF");

            // List<Double> allowancePayableAmountOfBasic = (List<Double>) ((HashMap)
            // salaryData.get("AllowancePayableAmount")).get("allowancePayableAmount");
            // Double basicSaary= allowancePayableAmountOfBasic.get(basicIndex);
            if (indexEPF != -1) {

                if (map.get("orgEpf") != null) {
                    if (map.get("orgEpf").toString().equalsIgnoreCase("yesepf")) {
                        if (employeeDetails.get("epf") != null
                                && employeeDetails.get("epf").toString().equalsIgnoreCase("Yes")) {

                            if (employeeDetails.get("voluntary_epf") != null
                                    && employeeDetails.get("voluntary_epf").toString().equalsIgnoreCase("No")) {

                                if (employeeDetails.get("baiscda") != null) {

                                    double basicAmount = Double.parseDouble(employeeDetails.get("baiscda").toString());

                                    double basicSaary = (basicAmount / companyWorkingDays) * employeeWorkingDays;

                                    double basicAmount1 = basicSaary >= 15000 ? 15000 : basicSaary;
                                    double result = basicAmount1 * 0.12;
                                    double rounded = Math.round(result * 100.0) / 100.0;
                                    deduction_payable_amount.set(indexEPF, rounded);
                                    salaryData.put("baiscDA", basicAmount1);
                                }

                            } else {
                                if (employeeDetails.get("baiscda") != null) {

                                    double basicAmount = Double.parseDouble(employeeDetails.get("baiscda").toString());
                                    double basicSaary = (basicAmount / companyWorkingDays) * employeeWorkingDays;
                                    salaryData.put("baiscDA", basicSaary);
                                }
                            }

                        }

                    }
                }
            }

            double deductionPayableAmount = 0.0;

            for (int idxx = 0; idxx < deduction_payable_amount.size(); idxx++) {
                double rounded = Math.round(deduction_payable_amount.get(idxx));
                deductionPayableAmount += deduction_payable_amount.get(idxx);
                deduction_payable_amount.set(idxx, rounded);
            }

            salaryData.put("other_deduction", Math.round(otherdeduction));

            d_payable_amount.put("deductionPayableAmount", deduction_payable_amount);
            salaryData.put("DeductionPayableAmount", d_payable_amount);
            salaryData.put("payable_gross", Math.round(payableGross));
            salaryData.put("NetPayableAmount",
                    Math.round(payableGross - deductionPayableAmount + additional_allowance));
            salaryData.put("total_deduction", Math.round(deductionPayableAmount));
            salaryData.put("additionalAllowance", Math.round(additional_allowance));
            salaryData.put("jsonForLoanDeduction", jsonForLoanDeduction);

            resultMap.put("status", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error(
                    "Problem in runPayServiceImpl :: Something went wrong in performing calculationBasedOnWorkingDayOfEmployee => "
                            + ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    public Map calculateAndGetSalaryDetailsOfEmployee(LinkedHashMap employeeDetails, Map map) {
        Map resultMap = new HashMap<>();
        try {
            Double companyWorkingDays = Double.parseDouble(employeeDetails.get("actual_duration").toString());
            Double employeeWorkingDays = Double.parseDouble(employeeDetails.get("value").toString());
            Double absentDays = Double.parseDouble(employeeDetails.get("absentDays").toString());
            Double incetiveWorkingDay = 0.0;
            if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {
                companyWorkingDays = 26.0;
                if (absentDays == 0.0) {
                    incetiveWorkingDay = Double.parseDouble(employeeDetails.get("value").toString())
                            - Integer.parseInt(employeeDetails.get("week_off").toString());
                }
                employeeWorkingDays = employeeWorkingDays
                        - (Integer.parseInt(employeeDetails.get("week_off").toString()));
            }
            String workingDayPolicy = map.get("workingDayPolicy").toString();
            if (workingDayPolicy.equalsIgnoreCase("Attendance & Leave")
                    || workingDayPolicy.equalsIgnoreCase("Attendance")) {
                employeeWorkingDays -= absentDays;
                if (Double.parseDouble(employeeDetails.get("totalAttendnaceCount").toString()) == 0) {
                    employeeWorkingDays = 0.0;
                }
            }

            this.savedEmployeeAllowance(resultMap, Long.parseLong(employeeDetails.get("sid").toString()),
                    employeeDetails.get("employee_type").toString(), incetiveWorkingDay);
            this.savedEmployeeDeductions(resultMap, Long.parseLong(employeeDetails.get("sid").toString()),
                    employeeDetails.get("employee_type").toString(),
                    Long.parseLong(employeeDetails.get("organization_id").toString()));
            this.savedEmployeeOtherAllowances(resultMap, Long.parseLong(employeeDetails.get("sid").toString()));
            this.getSalarydDetails(resultMap, employeeDetails);

            Map calculationResponse = this.performCalculationBasedOnWorkingDayOfEmployees(companyWorkingDays,
                    employeeWorkingDays, absentDays, resultMap, map, employeeDetails);
            if (calculationResponse.get("status").equals("success")) {
                resultMap.put("employee_id", employeeDetails.get("employeeId"));
                resultMap.put(("employeeType"), employeeDetails.get("employee_type"));
                resultMap.put("WorkingDay", employeeWorkingDays);
                resultMap.put("salaryAvailable", "true");
                resultMap.put("isSalaryBreakupSaved", true);
            } else {
                resultMap.put("salaryAvailable", "false");

            }

        } catch (Exception e) {
            e.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.error(
                    "Problem in runPayServiceImpl -> calculateAndGetSalaryDetailsOfEmployee === " + employeeDetails, e);
        }
        logger.info("response from calculateAndGetSalaryDetailsOfEmployee()->" + " " + resultMap.toString());
        return resultMap;
    }

    @Override
    public Map getPayRunData(String data, HttpServletRequest request, String search) {
        Map resultMap = new HashMap<>();
        try {

            Double advance = 0.0;
            List<Long> checkEmployeeIds = new ArrayList();
            double total_esic = 0;
            double total_epf = 0;
            double total_professional_tax = 0;
            double total_tds = 0;
            double total_advance = 0;
            double totalPay = 0.0;
            double totalNetPay = 0.0;
            double taxes = 0.0;
            double preTax = 0.0;

            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.containsKey("organizationId") && map.get("organizationId") != null && map.containsKey("month")
                    && map.get("month") != null && map.containsKey("year") && map.get("year") != null) {
                Long orgId = Long.valueOf(map.get("organizationId").toString());
                int month = Integer.parseInt(map.get("month").toString());
                int year = Integer.parseInt(map.get("year").toString());

                /**
                 * Fetching Pay cycle of an organization.
                 */
                LinkedCaseInsensitiveMap salaryDates = payrollSettingRepo.getSalaryDatesCycle(orgId);
                String start_date = salaryDates.get("start_date") != null ? salaryDates.get("start_date").toString()
                        : "0";
                String end_date = salaryDates.get("end_date").toString() != null
                        ? salaryDates.get("end_date").toString()
                        : "0";

                if (start_date.equals("0") || start_date.equals("0")) {
                    logger.info("Start Date and End Date in missing");
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                    return resultMap;
                }
                map.put("start_date", start_date);
                map.put("end_date", end_date);

                /**
                 * fetching all the employees of a particular organization from
                 * the manage.
                 */
                long t1 = System.currentTimeMillis();
                long t2;
                Map employeeListResp = this.FetchAllEmployeesOfOrganization(header, map, search);
                if (employeeListResp != null && employeeListResp.containsKey("status")
                        && employeeListResp.get("status").equals("success")) {
                    t2 = System.currentTimeMillis();

                    List<LinkedHashMap> employeeList = (ArrayList) employeeListResp.get("employee");
                    List<HashMap> finalEmpList = new ArrayList<>();
                    List<HashMap> finalEmpCalList = new ArrayList<>();

                    /**
                     * Get the list of all the employees whose payroll has been
                     * run and saved to db.
                     */
                    Map runPayData = this.findAllSavedPayRunDataByMonthYearOfEmployees(month + 1, year, orgId, search);
                    List<LinkedCaseInsensitiveMap> SavedRunpay = (List<LinkedCaseInsensitiveMap>) runPayData
                            .get("savedRunPay");
                    finalEmpList = (List<HashMap>) runPayData.get("finalEmpList");

                    totalNetPay = Double.parseDouble(runPayData.get("totalNetPay").toString());
                    taxes = Double.parseDouble(runPayData.get("taxes").toString());
                    total_epf += Double.parseDouble(runPayData.get("total_epf").toString());
                    total_esic += Double.parseDouble(runPayData.get("total_esic").toString());
                    total_advance += Double.parseDouble(runPayData.get("total_advance").toString());
                    total_tds += Double.parseDouble(runPayData.get("total_tds").toString());
                    total_professional_tax += Double.parseDouble(runPayData.get("total_professional_tax").toString());
                    preTax = Double.parseDouble(runPayData.get("preTax").toString());
                    totalPay = Double.parseDouble(runPayData.get("totalPay").toString());
                    /**
                     * Filtering the employees from the original list whose
                     * payroll has not been run.
                     */
                    List<LinkedHashMap> filteredEmployeeList = employeeList.stream()
                            .filter(employee -> SavedRunpay.stream()
                                    .noneMatch(otherEmployee -> Objects.equals(
                                            Long.parseLong(employee.get("employeeId").toString()),
                                            Long.parseLong(otherEmployee.get("employee_id").toString()))))
                            .collect(Collectors.toList());

                    /**
                     * fetching the Standard Salary breakup of all the employee.
                     */
                    List<LinkedCaseInsensitiveMap> getSavedEmployeesSalaryStandards = salaryBreakupRepo
                            .getEmployeesSalaryStandard(orgId, Integer.parseInt(map.get("month").toString()) + 1,
                                    Integer.parseInt(map.get("year").toString()));

                    /**
                     * Removing Standard Salary Breakup of Employee whose Salary
                     * On Hold
                     */
                    getSavedEmployeesSalaryStandards.removeIf(salary -> {
                        Object salaryHold = salary.get("salary_hold");
                        return salaryHold != null && salaryHold.toString().equalsIgnoreCase("Yes");
                    });

                    /**
                     *
                     * fetching save projection of employee month and year wise
                     */
                    List<LinkedCaseInsensitiveMap> salaryBreakupOfAllEmployees = salaryBreakupRepo
                            .getEmployeesSalaryBreakupbasedOnYearAndMonth(orgId,
                                    Integer.parseInt(map.get("month").toString()) + 1,
                                    Integer.parseInt(map.get("year").toString()));

                    Set<Long> uniqueEmployeeIds = salaryBreakupOfAllEmployees.stream()
                            .map(employee -> Long.parseLong(employee.get("employee_id").toString()))
                            .collect(Collectors.toSet());

                    // Add employees from salaryBreakupOfAllEmployees if their employee_id is not in
                    // the unique set
                    getSavedEmployeesSalaryStandards.forEach(employee -> {
                        Long employeeId = Long.parseLong(employee.get("employee_id").toString());
                        if (!uniqueEmployeeIds.contains(employeeId)) {
                            salaryBreakupOfAllEmployees.add(employee);
                            uniqueEmployeeIds.add(employeeId);
                        }
                    });

                    List<LinkedHashMap<String, String>> listMapData = filteredEmployeeList.stream()
                            .flatMap(m1 -> salaryBreakupOfAllEmployees.stream()
                                    .filter(y -> m1.get("employeeId").toString()
                                            .equals(y.get("employee_id").toString()))
                                    .map(m2 -> new LinkedHashMap<String, String>() {
                                        {
                                            putAll(m2);
                                            putAll(m1);

                                        }
                                    }))
                            .collect(Collectors.toList());

                    /**
                     * getting payroll based on which working day policy.
                     */
                    List<LinkedCaseInsensitiveMap> orgSettingData = organizationSetupRepo
                            .fetchWorkingDayAndOrgState1(orgId);
                    String payrollBasedOn = orgSettingData.get(0).get("working_day") != null
                            ? orgSettingData.get(0).get("working_day").toString()
                            : null;

                    Object lwf_flag = orgSettingData.get(0).get("lwf_flag");

                    String lwfFlag = (lwf_flag != null && !lwf_flag.toString().trim().isEmpty())
                            ? lwf_flag.toString().trim()
                            : null;

                    Object pt_flag = orgSettingData.get(0).get("pt_flag");

                    String ptFlag = (pt_flag != null && !pt_flag.toString().trim().isEmpty())
                            ? pt_flag.toString().trim()
                            : null;

                    map.put("workingDayPolicy", payrollBasedOn);
                    map.put("orgState", orgSettingData.get(0).get("org_state"));
                    map.put("orgEsic", orgSettingData.get(0).get("esic"));
                    map.put("orgEpf", orgSettingData.get(0).get("epf"));
                    map.put("orgLwf", lwfFlag);
                    map.put("orgPt", ptFlag);
                    List<LinkedHashMap> listToSend = new ArrayList<>();
                    listMapData.forEach(listData -> {
                        if (listData.get("tds") == null) {
                            if (listData.get("employee_type").toString().equalsIgnoreCase("Full time")
                                    || listData.get("employee_type").toString().equalsIgnoreCase("Probation")
                                    || listData.get("employee_type").toString().equalsIgnoreCase("Permanent")) {
                                listToSend.add(listData);
                            }
                        }
                    });

                    // listToSend.addAll(listMapData);
                    List<Map> taxListTosave = new ArrayList<>();
                    List<LinkedCaseInsensitiveMap> jsonToSaveDeductionLoan = new ArrayList<>();

                    List<Map> taxLists = new ArrayList<>();
                    List<Map> tdsList = new ArrayList<>();
                    List<Map> closingAllowance = new ArrayList<>();
                    Map taxResponse = new HashMap();
                    try {
                        long startTime = System.nanoTime();

                        // Your method logic goes here
                        taxResponse = taxService.calcuteTaxInBulk(listToSend, month + 1, year, orgId);
                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

                        // LOGGER.info("taxcalculation took " + duration + " milliseconds to execute "+"
                        // "+taxResponse.get("closingAllowance")+" "+taxResponse.get("tds"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    taxLists = (List<Map>) taxResponse.get("taxList");
                    tdsList = (List<Map>) taxResponse.get("tds");
                    closingAllowance = (List<Map>) taxResponse.get("overtimeClosingAllowance");
                    taxLists.forEach(taxObject -> {
                        List<Map> taxToSave = (List<Map>) taxObject.get("allList");
                        if (taxToSave.size() > 0) {
                            taxListTosave.addAll(taxToSave);
                        }

                    });

                    /**
                     * calculating the pay run data of the employees whose
                     * payroll has not run.
                     */
                    double tds = 0;
                    for (LinkedHashMap employee : listMapData) {

                        Long orgSetupId = employee.get("orgIds") != null
                                ? Long.parseLong(employee.get("orgIds").toString())
                                : 0;

                        orgSettingData.stream().forEach(ac -> {
                            if (Objects.equals(orgSetupId, Long.parseLong(ac.get("set_up_id").toString()))) {
                                map.put("orgState", ac.get("org_state"));
                                map.put("orgLwf", ac.get("lwf_flag"));
                                map.put("orgPt", ac.get("pt_flag"));
                            }
                        });

                        Long empId = Long.parseLong(employee.get("employeeId").toString());
                        String email = employee.get("email").toString();
                        tdsList.forEach(tdsdData -> {
                            if (Objects.equals(Long.parseLong(tdsdData.get("employee_id").toString()), empId)) {
                                employee.put("tds", tdsdData.get("tds"));
                            }
                        });
                        closingAllowance.forEach(closing -> {
                            if (Objects.equals(Long.parseLong(closing.get("employee_id").toString()), empId)) {
                                if (closing.containsKey("Overtime Closing Allowance")) {
                                    employee.put("Overtime Closing Allowance",
                                            closing.get("Overtime Closing Allowance"));
                                } else if (closing.containsKey("Overtime Closing Allowances")) {
                                    employee.put("Overtime Closing Allowance",
                                            closing.get("Overtime Closing Allowances"));
                                }

                            }
                        });
                        String emp_type = employee.get("employee_type").toString();
                        if (emp_type.equalsIgnoreCase("Probation") || emp_type.equalsIgnoreCase("Permanent")) {
                            emp_type = "Full time";
                            employee.put("employee_type", emp_type);
                        }
                        employee.put("organization_id", orgId);
                        employee.put("annual_ctc", employee.get("annual_ctc") == null ? 0 : employee.get("annual_ctc"));
                        Double companyWorkingDays = Double.parseDouble(employee.get("actual_duration").toString());
                        Double employeeWorkingDays = Double.parseDouble(employee.get("value").toString());
                        Double absentDays = Double.parseDouble(employee.get("absentDays").toString());

                        if (emp_type.equalsIgnoreCase("Worker")) {
                            companyWorkingDays = 26.0;
                            employeeWorkingDays = employeeWorkingDays
                                    - (Integer.parseInt(employee.get("week_off").toString()));
                        }

                        String workingDayPolicy = map.get("workingDayPolicy").toString();
                        if (workingDayPolicy.equalsIgnoreCase("Attendance & Leave")
                                || workingDayPolicy.equalsIgnoreCase("Attendance")) {
                            employeeWorkingDays -= absentDays;
                            if (Double.parseDouble(employee.get("totalAttendnaceCount").toString()) == 0) {
                                employeeWorkingDays = 0.0;
                            }

                        }

                        try {
                            /**
                             * Calculating the salary,Allowance,Deduction and
                             * Tax of an employee.
                             */

                            Map calculatedSalaryOfEmployee = this.calculateAndGetSalaryDetailsOfEmployee(employee, map);

                            if (calculatedSalaryOfEmployee.get("salaryAvailable").toString().equalsIgnoreCase("true")) {
                                Double basicSalary = 0.0;
                                Double payableSalary = 0.0;

                                List<String> allowanceName = (List<String>) ((HashMap) calculatedSalaryOfEmployee
                                        .get("AllowanceName")).get("allowanceName");
                                int idx = -1;
                                for (String name : allowanceName) {
                                    idx++;
                                    if (name.equalsIgnoreCase("Basic Salary")) {
                                        List<Double> amount = (List<Double>) ((HashMap) calculatedSalaryOfEmployee
                                                .get("AllowanceAmount")).get("allowanceAmount");
                                        basicSalary = amount.get(idx);
                                        List<Double> payableAmount = (List<Double>) ((HashMap) calculatedSalaryOfEmployee
                                                .get("AllowancePayableAmount")).get("allowancePayableAmount");
                                        payableSalary = payableAmount.get(idx);
                                        break;
                                    }
                                }

                                Double employeeEpf = 0.0;
                                Double employeeEsic = 0.0;
                                Double labourLaw = 0.0;
                                advance = 0.0;
                                Double professionalTax = 0.0;
                                List<String> deductionName = (List<String>) ((HashMap) calculatedSalaryOfEmployee
                                        .get("DeductionName")).get("deductionName");
                                List<Double> payableAmount = (List<Double>) ((HashMap) calculatedSalaryOfEmployee
                                        .get("DeductionPayableAmount")).get("deductionPayableAmount");

                                /**
                                 *
                                 * Employee Deuction.
                                 */
                                int iddx = -1;
                                for (String name : deductionName) {
                                    iddx++;
                                    if (name.equalsIgnoreCase("EPF")) {
                                        employeeEpf = payableAmount.get(iddx);
                                    } else if (name.equalsIgnoreCase("ESIC")) {
                                        employeeEsic = payableAmount.get(iddx);
                                    } else if (name.equalsIgnoreCase("Advance")) {
                                        advance = payableAmount.get(iddx);
                                    } else if (name.equalsIgnoreCase("Professional Tax")) {
                                        professionalTax = payableAmount.get(iddx);
                                    } else if (name.equalsIgnoreCase("Income Tax")) {

                                        // tds = payableAmount.get(iddx);
                                    } else if (name.equalsIgnoreCase("Labour Welfare Fund")) {
                                        labourLaw = payableAmount.get(iddx);
                                    }

                                }

                                /**
                                 * TDS For consultant and Contract calculation.
                                 */
                                if (emp_type.equalsIgnoreCase("consultant") || emp_type.equalsIgnoreCase("contract")) {
                                    int indexValue = -1;
                                    for (String name : deductionName) {
                                        indexValue++;
                                        if (name.equalsIgnoreCase("tds")) {
                                            employee.put("tds", payableAmount.get(indexValue));
                                            // tds = payableAmount.get(indexValue);
                                        }

                                    }

                                }

                                // end of Code
                                //
                                /**
                                 * Employer deduction EPF,ESIC,GRATUITY and CTC
                                 * calculation.
                                 */
                                Map employerDetails = this.CalculateEmployerDeductionAndCTCOfEmployee(basicSalary,
                                        payableSalary, calculatedSalaryOfEmployee, orgId, empId, emp_type,
                                        companyWorkingDays, employeeWorkingDays, employeeEpf, month, year);
                                Map allowanceNameMap = (Map) calculatedSalaryOfEmployee.get("AllowanceName");
                                Map allowancePayableAmount = (Map) calculatedSalaryOfEmployee
                                        .get("AllowancePayableAmount");

                                if (allowanceNameMap.get("allowanceName") != null) {
                                    List<String> allowancesNames = (List<String>) allowanceNameMap.get("allowanceName");
                                    List<Double> allowancesAmount = (List<Double>) allowancePayableAmount
                                            .get("allowancePayableAmount");
                                    int index = allowancesNames.indexOf("Attendance Incentives");
                                    if (index != -1) {
                                        employee.put("attendanceIncentives", allowancesAmount.get(index));
                                    }

                                }
                                if (calculatedSalaryOfEmployee.get("status").equals("success")) {
                                    double salary = 0;
                                    if (emp_type.equalsIgnoreCase("Worker")) {
                                        salary = Math.round((Double) calculatedSalaryOfEmployee.get("wages") / 26);
                                        // salary = Math.round((Double) calculatedSalaryOfEmployee.get("wages"));
                                    } else {
                                        salary = Math.round((Double) calculatedSalaryOfEmployee.get("salary"));
                                    }

                                    double epf = employeeEpf;
                                    double esic = employeeEsic;
                                    double netSalary = calculatedSalaryOfEmployee.get("NetPayableAmount") != null
                                            ? Double.parseDouble(
                                                    calculatedSalaryOfEmployee.get("NetPayableAmount").toString())
                                            : 0;
                                    double payable = Double
                                            .parseDouble(calculatedSalaryOfEmployee.get("payable_gross").toString());
                                    double attendnace = calculatedSalaryOfEmployee.containsKey("attendannceToMinus")
                                            ? Double.parseDouble(
                                                    calculatedSalaryOfEmployee.get("attendannceToMinus").toString())
                                            : 0.0;
                                    double clbAmount = calculatedSalaryOfEmployee.containsKey("ClbAllowance")
                                            ? Double.parseDouble(
                                                    calculatedSalaryOfEmployee.get("ClbAllowance").toString())
                                            : 0.0;
                                    boolean aiflg = calculatedSalaryOfEmployee.containsKey("attendanceIncentiveFlag")
                                            ? Boolean.valueOf(calculatedSalaryOfEmployee.get("attendanceIncentiveFlag")
                                                    .toString())
                                            : false;

                                    System.out
                                            .println("clbAmount " + clbAmount + " of employee " + employee.get("name"));

                                    if (aiflg) {
                                        payable = payable;
                                        netSalary = netSalary;
                                    } else {
                                        payable = payable - attendnace;
                                        netSalary = netSalary - attendnace;
                                    }

                                    /**
                                     * if payable Salary is 0 then net payable
                                     * and PT will be 0
                                     */
                                    if (Double.parseDouble(
                                            calculatedSalaryOfEmployee.get("payable_gross").toString()) == 0) {
                                        netSalary = 0;
                                        professionalTax = 0.0;
                                    }

                                    List<LinkedCaseInsensitiveMap> deductionLoanData = (List<LinkedCaseInsensitiveMap>) calculatedSalaryOfEmployee
                                            .get("jsonForLoanDeduction");
                                    jsonToSaveDeductionLoan.addAll(deductionLoanData);
                                    /**
                                     * employee bifurcation.
                                     */
                                    // totalNetPay = totalNetPay + netSalary;
                                    totalPay = totalPay + salary;
                                    Double taxByFurgation = employee.get("tds") != null
                                            ? Double.parseDouble(employee.get("tds").toString())
                                            : 0.0;
                                    taxes = taxes + taxByFurgation + epf + esic + advance + professionalTax;
                                    total_epf += epf;
                                    total_esic += esic;
                                    total_advance += advance;
                                    total_tds += taxByFurgation;
                                    total_professional_tax += professionalTax;
                                    preTax = preTax + (epf + esic);
                                    employee.put("runPayRollId", 0);
                                    employee.put("name", employee.get("name"));
                                    employee.put("empStatus", employee.get("status"));
                                    employee.put("salary", salary);

                                    employee.put("payable", payable);
                                    employee.put("epf", employeeEpf);
                                    employee.put("esic", employeeEsic);
                                    checkEmployeeIds.add(employee.get("employeeId") != null
                                            ? Long.parseLong(employee.get("employeeId").toString())
                                            : 0);
                                    // employee.put("tds", calculatedSalaryOfEmployee.containsKey("tds") ?
                                    // calculatedSalaryOfEmployee.get("tds") : 0);
                                    // employee.put("tds", tds);
                                    // tds=0;
                                    employee.put("advance", advance);
                                    employee.put("labourWelfareFund", labourLaw);
                                    employee.put("professional_tax", professionalTax);
                                    employee.put("adhoc",
                                            calculatedSalaryOfEmployee.containsKey("referral")
                                                    ? calculatedSalaryOfEmployee.get("referral")
                                                    : 0);
                                    employee.put("other_deductions",
                                            calculatedSalaryOfEmployee.containsKey("other_deduction")
                                                    ? calculatedSalaryOfEmployee.get("other_deduction")
                                                    : 0);
                                    employee.put("Reimbursement",
                                            calculatedSalaryOfEmployee.containsKey("Reimbursement")
                                                    ? calculatedSalaryOfEmployee.get("Reimbursement")
                                                    : 0);
                                    employee.put("arrears",
                                            calculatedSalaryOfEmployee.containsKey("arrears")
                                                    ? calculatedSalaryOfEmployee.get("arrears")
                                                    : 0);
                                    employee.put("totalHours",
                                            calculatedSalaryOfEmployee.containsKey("totalHours")
                                                    ? calculatedSalaryOfEmployee.get("totalHours")
                                                    : 0);
                                    employee.put("overTime",
                                            calculatedSalaryOfEmployee.containsKey("overTimeHours")
                                                    ? calculatedSalaryOfEmployee.get("overTimeHours")
                                                    : 0);
                                    employee.put("overtimePay",
                                            calculatedSalaryOfEmployee.containsKey("overtime")
                                                    ? Math.round(Double.parseDouble(
                                                            calculatedSalaryOfEmployee.get("overtime").toString()))
                                                    : 0);
                                    employee.put("rate",
                                            calculatedSalaryOfEmployee.containsKey("rate")
                                                    ? calculatedSalaryOfEmployee.get("rate")
                                                    : 0);
                                    employee.put("bonus",
                                            calculatedSalaryOfEmployee.containsKey("bonus")
                                                    ? ((Double) calculatedSalaryOfEmployee.get("bonus")) + clbAmount
                                                    : clbAmount);
                                    employee.put("attendanceIncentives",
                                            calculatedSalaryOfEmployee.containsKey("AttendanceIncentives")
                                                    ? calculatedSalaryOfEmployee.get("AttendanceIncentives")
                                                    : 0);
                                    employee.put("OtherDeductionValues",
                                            calculatedSalaryOfEmployee.containsKey("otherDeductionValue")
                                                    ? calculatedSalaryOfEmployee.get("otherDeductionValue")
                                                    : 0);
                                    employee.put("organizationId", orgId);
                                    employee.put("email", email);
                                    employee.put("employer_epf",
                                            employeeEpf == 0 ? 0 : employerDetails.get("epfValue"));
                                    employee.put("employer_esic", employerDetails.get("esicValue"));
                                    employee.put("gratuity", employerDetails.get("gratuity"));
                                    employee.put("bonus_deduction", employerDetails.get("bonus"));
                                    employee.put("variable", employerDetails.get("variable"));
                                    employee.put("ctc", employerDetails.get("ctc"));
                                    employee.put("isSalaryBreakupSaved",
                                            calculatedSalaryOfEmployee.get("isSalaryBreakupSaved"));
                                    employee.put("working_day", employeeWorkingDays);
                                    employee.put("isSaved", calculatedSalaryOfEmployee.get("isSalaryBreakupSaved"));
                                    employee.put("employee_code",
                                            employee.get("employeeCode") != null ? employee.get("employeeCode")
                                                    : "N/A");
                                    employee.put("projectionMonth", calculatedSalaryOfEmployee.get("projectionMonth"));
                                    employee.put("projectionYear", calculatedSalaryOfEmployee.get("projectionYear"));
                                    employee.put("joiningDate", employee.get("joiningDate"));
                                    employee.put("modeofpayment",
                                            calculatedSalaryOfEmployee.containsKey("modeofpayment")
                                                    ? calculatedSalaryOfEmployee.get("modeofpayment")
                                                    : "Not specified");
                                    employee.put("orgIds",
                                            calculatedSalaryOfEmployee.containsKey("orgIds")
                                                    ? calculatedSalaryOfEmployee.get("orgIds")
                                                    : 0);

                                    if (employee.get("employee_type").toString().equalsIgnoreCase("Full time")
                                            || employee.get("employee_type").toString().equalsIgnoreCase("Permanent")
                                            || employee.get("employee_type").toString().equalsIgnoreCase("Probation")) {
                                        employee.put("net_payable", Math.round(netSalary) - taxByFurgation);
                                        calculatedSalaryOfEmployee.put("NetPayableAmount",
                                                Math.round(netSalary) - taxByFurgation);

                                    } else {
                                        employee.put("net_payable", Math.round(netSalary));
                                        calculatedSalaryOfEmployee.put("NetPayableAmount", Math.round(netSalary));
                                    }

                                    totalNetPay = totalNetPay
                                            + Double.parseDouble(employee.get("net_payable").toString());
                                    employee.put("payRunMonth", month + 1);
                                    calculatedSalaryOfEmployee.put("tds", employee.get("tds"));
                                    calculatedSalaryOfEmployee.put("joiningDate", employee.get("joiningDate"));
                                    calculatedSalaryOfEmployee.put("annual_ctc", employee.get("annual_ctc"));
                                    calculatedSalaryOfEmployee.put("totalDays", companyWorkingDays);
                                    calculatedSalaryOfEmployee.put("overtime",
                                            calculatedSalaryOfEmployee.get("overTimeHours"));
                                    employee.put("payRunYear", year);
                                    employee.put("alreadyRun", false);
                                    calculatedSalaryOfEmployee.put("is_esic", employee.get("is_esic"));
                                    calculatedSalaryOfEmployee.put("voluntaryEpf", employee.get("voluntary_epf"));
                                    calculatedSalaryOfEmployee.put("voluntaryEpfPercentage",
                                            employee.get("voluntary_epf_percentage"));
                                    employee.computeIfAbsent("tds", k -> 0);
                                    calculatedSalaryOfEmployee.put("employeeId", employee.get("employeeId"));
                                    finalEmpCalList.add((HashMap) calculatedSalaryOfEmployee);
                                    HashMap<String, Object> employeeHashMap = new HashMap<>(employee);
                                    finalEmpList.add(employeeHashMap);

                                } else {
                                    logger.debug("Salary breakup is not available :: Emp Id: " + empId + ", Month : "
                                            + month + 1 + " , year :" + year);
                                }

                            } else {

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            logger.info("Problem in SalaryCalculationController -> getSalaryCalculation() :: Emp Id: "
                                    + empId + ", Month : " + month + 1 + " , year :" + year + " =>" + ex);
                        }

                    }
                    /**
                     * end of loop.
                     */

                    List<LinkedCaseInsensitiveMap> alreadySavedProjectionForThisMonth = salaryBreakupRepo
                            .salaryBreakupSavedOfMonth(checkEmployeeIds, year, month + 1);
                    finalEmpList.forEach(employeeData -> {

                        alreadySavedProjectionForThisMonth.forEach(prejection -> {

                            if (employeeData.get("employeeId") != null) {
                                if (Objects.equals(Long.parseLong(employeeData.get("employeeId").toString()),
                                        Long.parseLong(prejection.get("employee_id").toString()))) {
                                    employeeData.put("net_payable", employeeData.get("net_amount"));
                                    employeeData.put("projectionSaved", true);
                                }
                            }

                        });
                    });

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-mm-dd");

                    finalEmpList = finalEmpList.stream()
                            .map(emp -> {
                                if (!emp.containsKey("projectionSaved")) {
                                    emp.put("projectionSaved", false);
                                }
                                return emp;
                            })
                            .collect(Collectors.toList());

                    finalEmpList.sort(Comparator.comparing(emp -> {
                        Object joiningDateObj = emp.get("joiningDate");
                        if (joiningDateObj == null) {
                            return LocalDate.MAX; // Place null entries at the end
                        } else {
                            return LocalDate.parse(joiningDateObj.toString());
                        }
                    }));

                    finalEmpCalList.sort(Comparator.comparing(emp -> {
                        Object joiningDateObj = emp.get("joiningDate");
                        if (joiningDateObj == null) {
                            return LocalDate.MAX; // Place null entries at the end
                        } else {
                            return LocalDate.parse(joiningDateObj.toString());
                        }
                    }));

                    // Collections.reverse(finalEmpList);
                    // finalEmpCalList.sort(Comparator.comparing(emp ->
                    // LocalDate.parse(emp.get("joiningDate").toString())));
                    // Collections.reverse(finalEmpCalList);
                    resultMap.put("totalEPF", total_epf);
                    resultMap.put("totalESIC", total_esic);
                    resultMap.put("totalProfessionalTax", total_professional_tax);
                    resultMap.put("totalTDS", total_tds);
                    resultMap.put("totalAdvance", total_advance);
                    // resultMap.put("tds", tds);
                    resultMap.put("totalPay", Math.round(totalPay));
                    resultMap.put("totalNetPay", Math.round(totalNetPay));
                    resultMap.put("taxes", taxes);
                    resultMap.put("preTaxes", preTax);
                    resultMap.put("data", finalEmpList);
                    resultMap.put("details", finalEmpCalList);
                    resultMap.put("payDay", payrollRepo.findPayDayByOrgId(orgId));
                    resultMap.put("payRunMonth", month + 1);
                    resultMap.put("taxToSave", taxListTosave);
                    resultMap.put("payRunYear", year);
                    resultMap.put("jsonToSaveDeductionLoan", jsonToSaveDeductionLoan);
                    resultMap.put("status", "success");
                    long t3 = System.currentTimeMillis();
                } else {
                    resultMap = employeeListResp;
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid input and key!");
            }
        } //
        catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in SalaryCalculationController -> getSalaryCalculation() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    public Map CalculateEmployerDeductionAndCTCOfEmployee(Double basicSalary, Double payableSalary,
            Map calculatedSalaryOfEmployee, Long organisationId, Long empId, String empType, Double companyWorkingDays,
            Double employeeWorkingDays, Double employeeEpf, int month, int year) {
        Map data = new HashMap();
        double epfValue = 0;
        double esicValue = 0;
        double gratuity = 0;
        double ctc = 0;
        Double payableGrossSalary = Double.parseDouble(calculatedSalaryOfEmployee.get("payable_gross").toString());
        Double additionalAllowance = Double
                .parseDouble(calculatedSalaryOfEmployee.get("additionalAllowance").toString());
        Double bonus = 0.0;
        Double variable = 0.0;
        Long sid = Long.parseLong(calculatedSalaryOfEmployee.get("salary_break_up_id").toString());
        // if (emp_type.equalsIgnoreCase("Worker")) {
        // basicSalary = (basicSalary / 30) * employeeWorkingDays;
        // payableSalary = basicSalary;
        // }

        /**
         * Employer deduction epf,esic and gratuity
         */
        try {
            List<LinkedCaseInsensitiveMap> deduction = deductionRepo.findEmployeeDeductionAccordingToEmployeeType(sid);
            LinkedCaseInsensitiveMap gradPercentage = deductionRepo.getEmployeeBonus(empId);
            for (LinkedCaseInsensitiveMap d : deduction) {
                if (d.get("deduction_name") != null && d.get("employer_percentage") != null) {
                    double percentage = Double.parseDouble(d.get("employer_percentage").toString());
                    if (d.get("deduction_name").toString().equalsIgnoreCase("EPF")) {
                        if (basicSalary > 15000) {
                            epfValue = Math.round((15000 * percentage) / 100);
                            epfValue = (epfValue * employeeWorkingDays) / companyWorkingDays;
                        } else {
                            epfValue = Math.round((payableSalary * percentage) / 100);
                        }
                    } else if (d.get("deduction_name").toString().equalsIgnoreCase("ESIC")) {
                        if ((Double) calculatedSalaryOfEmployee.get("salary") <= 21000) {
                            esicValue = Math.round(payableGrossSalary * percentage) / 100;
                        }
                    }

                }

            }

            bonus = bonusDeductionRepo.getEmployeeBonus(empId, month + 1, year);
            if (bonus == null) {
                bonus = 0.0;
            }

            if (gradPercentage != null && !gradPercentage.isEmpty()) {
                if (gradPercentage.get("employer_percentage") != null) {
                    double percentage = Double.parseDouble(gradPercentage.get("employer_percentage").toString());
                    gratuity = Math.round((payableSalary * percentage) / 100);
                }
            }

            variable = variableDeductionRepo.getEmployeeVariable(empId, month + 1, year);
            if (variable == null) {
                variable = 0.0;
            }

            bonus = (bonus / companyWorkingDays) * employeeWorkingDays;
            variable = (variable / companyWorkingDays) * employeeWorkingDays;

            /**
             * CTC calculation *
             */
            if (employeeEpf == 0) {
                epfValue = 0;
            }
            ctc = payableGrossSalary + epfValue + esicValue + gratuity + +bonus + variable;

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * calculate result *
         */
        data.put("epfValue", Math.round(epfValue));
        data.put("esicValue", Math.round(esicValue));
        data.put("gratuity", Math.round(gratuity));
        data.put("bonus", Math.round(bonus));
        data.put("variable", Math.round(variable));
        data.put("ctc", Math.round(ctc));
        return data;
    }

    public Map getTotalDays(HttpEntity leaveEntity) {
        Map resultMap = new HashMap<>();
        Map getTotalDays;
        Long totalDays = null;
        Long actualDuration = null;
        try {
            getTotalDays = restTemplate.exchange(reimburshment_url + " /payrollleavecount/getcompanyworkingday",
                    HttpMethod.POST, leaveEntity, HashMap.class).getBody();
        } catch (Exception ex) {
            logger.info("Problem getting Total no. of days from timesheet" + ex);
            resultMap.clear();
            resultMap.put("status", "error");
            resultMap.put("msg", "Problem getting Total no. of days");
            return resultMap;
        }
        Map noOfDays = null;
        try {
            noOfDays = mapper.readValue(EncryptDecryptUtils.decrypt(getTotalDays.get("data").toString()),
                    LinkedCaseInsensitiveMap.class);
        } catch (JsonProcessingException ex) {
            logger.info("Problem in RunPayServiceImpl -> getTotalDays() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "error");
            return resultMap;
        }
        if (noOfDays.containsKey("status") && noOfDays.get("status").equals("success")) {
            if (noOfDays.containsKey("value") && noOfDays.get("value") != null) {
                totalDays = Long.parseLong(noOfDays.get("value").toString());
                actualDuration = Long.parseLong(noOfDays.get("actual_duration").toString());
                logger.info("Total No. of Days" + totalDays + " fetching from timesheet");
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("days", totalDays);
                resultMap.put("actual_duration", actualDuration);
            } else {
                logger.info("Problem getting Total No. of Days");
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Problem getting Total No. of Days");
                return resultMap;
            }
        }
        return resultMap;
    }

    @Override
    public Map isPayrollSaved(Integer month, Integer year, Long organizationId) {
        Map resultMap = new HashMap<>();
        try {
            int count = runPayRepo.isPayRollRun(month, year, organizationId);
            if (count > 0) {
                resultMap.clear();
                resultMap.put("isSaved", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("isSaved", false);
                resultMap.put("status", "success");
            }
        } catch (Exception ex) {
            logger.info("Problem in RunPayServiceImpl -> isPayrollSaved() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @Override
    public Map getAllowanceAmount(Integer month, Integer year, Long organizationId, Long employeeId) {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap allowanceAmount = runPayRepo.getAllowanceAmount(month, year, organizationId,
                    employeeId);
            if (allowanceAmount.isEmpty()) {
                resultMap.clear();
                resultMap.put("adhoc", 0);
                resultMap.put("Reimbursement", 0);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("adhoc", allowanceAmount.get("adhoc"));
                resultMap.put("Reimbursement", allowanceAmount.get("Reimbursement"));
                resultMap.put("status", "success");
            }
        } catch (Exception ex) {
            logger.info("Problem in RunPayServiceImpl -> getAllowanceAmount() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @Override
    public Map isSalaryBreakupSavedOfThisMonth(Map map) {
        Map resultMap = new HashMap<>();
        try {
            int month = Integer.parseInt(map.get("month").toString());
            int year = Integer.parseInt(map.get("year").toString());
            Long organizationId = Long.parseLong(map.get("organization_id").toString());
            int count = salaryBreakupRepo.isSaved(month, year, organizationId);
            if (count > 0) {
                resultMap.clear();
                resultMap.put("isSaved", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("isSaved", false);
                resultMap.put("status", "success");
            }
        } catch (Exception ex) {
            logger.info("Problem in RunPayServiceImpl -> isSalaryBreakupSavedOfThisMonth() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    public Map<String, Object> updateAllowanceInBulk(MultipartFile file, Long orgId)
            throws IOException, InvalidFormatException {

        Map<String, Object> resultMap = new HashMap<>();
        List<LinkedCaseInsensitiveMap> empData = new ArrayList();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> getIndexOfEachColumn = new HashMap<>();
            Row headerRow = sheet.getRow(0);

            /**
             * getting all header name and index no of header
             *
             */
            headerRow.forEach(cell -> {

                if (cell.getCellType() == CellType.STRING) {

                    switch (cell.getStringCellValue().toLowerCase().trim()) {

                        case "employee code":
                            getIndexOfEachColumn.put("e_codeIndex", cell.getColumnIndex());
                            break;
                        case "name":
                            getIndexOfEachColumn.put("nameIndex", cell.getColumnIndex());
                            break;
                        case "email":
                            getIndexOfEachColumn.put("emailIndex", cell.getColumnIndex());
                            break;
                        case "employee type":
                            getIndexOfEachColumn.put("emptypeIndex", cell.getColumnIndex());
                            break;
                        case "advance":
                            getIndexOfEachColumn.put("advanceIndex", cell.getColumnIndex());
                            break;
                        case "tds":
                            getIndexOfEachColumn.put("tdsIndex", cell.getColumnIndex());
                            break;
                        case "other deductions":
                            getIndexOfEachColumn.put("ODIndex", cell.getColumnIndex());
                            break;
                        // case "attendance incentives":
                        // getIndexOfEachColumn.put("AIIndex", cell.getColumnIndex());
                        // break;
                        case "reimbursement":
                            getIndexOfEachColumn.put("RAIndex", cell.getColumnIndex());
                            break;
                        case "arrears":
                            getIndexOfEachColumn.put("arrIndex", cell.getColumnIndex());
                            break;
                        // case "referral allowance":
                        // getIndexOfEachColumn.put("refferalAIndex", cell.getColumnIndex());
                        // break;
                        case "bonus/incentive":
                            getIndexOfEachColumn.put("BIIndex", cell.getColumnIndex());
                            break;
                        case "overtime allowance":
                            getIndexOfEachColumn.put("OAIndex", cell.getColumnIndex());
                            break;
                        case "month":
                            getIndexOfEachColumn.put("monthIndex", cell.getColumnIndex());
                            break;
                        case "year":
                            getIndexOfEachColumn.put("yearIndex", cell.getColumnIndex());
                            break;
                        case "status":
                            getIndexOfEachColumn.put("SIndex", cell.getColumnIndex());
                            break;

                        default:
                            break;
                    }
                }

            });

            sheet.removeRow(headerRow);
            /**
             * Putting all excel sheet value into List
             *
             */
            sheet.forEach((Row row) -> {

                LinkedCaseInsensitiveMap mp = new LinkedCaseInsensitiveMap();

                row.forEach(cell -> {

                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("e_codeIndex")) {
                        mp.put("employee code", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("nameIndex")) {
                        mp.put("name", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("emailIndex")) {
                        mp.put("email", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("emptypeIndex")) {
                        mp.put("employee type", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("advanceIndex")) {
                        mp.put("advance", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("tdsIndex")) {
                        mp.put("income tax", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("ODIndex")) {
                        mp.put("other deductions", printCellValue(cell));
                    }
                    // if (cell.getColumnIndex() == getIndexOfEachColumn.get("AIIndex")) {
                    // mp.put("attendance incentives", printCellValue(cell));
                    // }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("RAIndex")) {
                        mp.put("Reimbursement", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("arrIndex")) {
                        mp.put("arrears", printCellValue(cell));
                    }

                    // if (cell.getColumnIndex() == getIndexOfEachColumn.get("refferalAIndex")) {
                    // mp.put("referral allowance", printCellValue(cell));
                    // }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("BIIndex")) {
                        mp.put("bonus/incentive", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("OAIndex")) {
                        mp.put("overtime", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("monthIndex")) {
                        mp.put("month", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("yearIndex")) {
                        mp.put("year", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("SIndex")) {
                        mp.put("status", printCellValue(cell));
                    }

                });

                empData.add(mp);

            });

            /**
             * Removing employee from empData List whose status is no so that we
             * not updates allowance and deduction
             *
             */
            LinkedCaseInsensitiveMap orgSettingData = organizationSetupRepo.fetchWorkingDayAndOrgState(orgId);

            empData.removeIf(filter -> "no".equalsIgnoreCase(filter.get("status").toString()));

            if (empData.isEmpty()) {
                resultMap.put("status", "error");
                resultMap.put("msg", "Can not update! Please check");
                return resultMap;
            }

            List<String> empcodeList = new ArrayList<>();
            int[] month = new int[1];
            month[0] = 0;
            int[] year = new int[1];
            year[0] = 0;

            /**
             * Getting employee code from empData List so that we can get
             * employee_id of employees
             *
             */
            empData.stream().forEach(empcode -> {
                String ecode = empcode.get("employee code").toString();
                month[0] = (int) Double.parseDouble(empcode.get("month").toString());
                year[0] = (int) Double.parseDouble(empcode.get("year").toString());
                empcodeList.add(ecode);
            });

            /**
             * geeting employee id based on employee code
             *
             */
            List<LinkedCaseInsensitiveMap> empid = empdetailsrepo.getEmployeeId(empcodeList, orgId);
            /**
             * merge employee_id of each employee
             *
             */
            empData.forEach(addempId -> {
                String empcode = addempId.get("employee code").toString();

                empid.stream()
                        .filter(action -> action.get("employee_code").toString().equalsIgnoreCase(empcode))
                        .findFirst() // Use findFirst to get the first matching element
                        .ifPresent(matchedEmp -> {
                            // Replace "newKey" and "newValue" with the actual key-value pair you want to
                            // add
                            addempId.put("employee_id", matchedEmp.get("employee_id"));
                            // Additional processing if needed
                        });
            });

            /**
             * getting All employee Id
             *
             */
            List<Long> empidAdd = new ArrayList<>();
            empData.stream().forEach(action -> {
                Long id = Long.parseLong(action.get("employee_id").toString());
                empidAdd.add(id);
            });

            /**
             * getting All Allowances and deductions
             *
             */
            List<LinkedCaseInsensitiveMap> deduction = employeeDeductionRepo.getDeductionsForUpdate(empidAdd, orgId,
                    month[0], year[0]);

            List<EmployeeDeduction> update_Deducton = employeeDeductionRepo.getDeductionsForSaveExac(empidAdd, orgId,
                    month[0], year[0]);

            List<LinkedCaseInsensitiveMap> allowance = employeeAllowanceRepo.getAllowancesForUpdate(empidAdd, orgId,
                    month[0], year[0]);

            List<LinkedCaseInsensitiveMap> oldAllowanceBackup = allowance;

            List<EmployeeAllowance> update_allowance = employeeAllowanceRepo.getAllowancesForUpdateData(empidAdd, orgId,
                    month[0], year[0]);

            /**
             * allowance update
             *
             */
            Double washingAmount[] = new Double[1];
            washingAmount[0] = 0.0;
            empData.stream().forEach(edata -> {

                Long eid = Long.parseLong(edata.get("employee_id").toString());

                allowance.stream().forEach(d -> {
                    Long eids = Long.parseLong(d.get("employee_id").toString());
                    Long ids = Long.parseLong(d.get("id").toString());
                    if (d.get("allowance_name") != null
                            && (d.get("allowance_name").toString().equalsIgnoreCase("Washing")
                                    || d.get("allowance_name").toString().equalsIgnoreCase("Washing Allowance")
                                    || d.get("allowance_name").toString().equalsIgnoreCase("Washing Allowances"))) {
                        washingAmount[0] = Double.parseDouble(d.get("allowance_payable_amount").toString());
                    }
                    if (eids.equals(eid)) {

                        String allowanceName = d.get("allowance_name").toString().toLowerCase();
                        if (allowanceName.equalsIgnoreCase("overtime allowance")) {
                            allowanceName = "overtime";
                        }
                        boolean containsKeyIgnoreCase = edata.containsKey(allowanceName);
                        if (containsKeyIgnoreCase) {

                            Double valueFromDatabase = Double.parseDouble(d.get("allowance_payable_amount").toString());
                            Object value = edata.get(allowanceName);
                            Double valueFromUi = Double.parseDouble(value.toString());
                            if (!valueFromDatabase.equals(valueFromUi)) {
                                update_allowance.stream().forEach(up -> {
                                    if (up.getId().equals(ids)) {
                                        up.setAllowance_payable_amount(valueFromUi);
                                    }

                                });

                            }
                        }
                    }
                });

            });

            /**
             * Removing Employee type worker from empData list for tax
             * calculation
             *
             */
            List<LinkedCaseInsensitiveMap> fullTimeEmployeeList = empData.stream()
                    .filter(e -> {
                        Object employeeType = e.get("employee type");

                        // Check for null and use equalsIgnoreCase for case-insensitive comparison
                        return employeeType != null
                                && ("full time".equalsIgnoreCase(employeeType.toString())
                                        || "permanent".equalsIgnoreCase(employeeType.toString())
                                        || "probation".equalsIgnoreCase(employeeType.toString()));
                    })
                    .collect(Collectors.toList());

            /**
             * Getting Tax Changes employee List
             *
             */
            List<LinkedCaseInsensitiveMap> taxChangableEmployeeList = new ArrayList<>();

            deduction.stream().forEach(d -> {

                String taxName = d.get("deduction_name").toString().toLowerCase();

                Long eId = Long.parseLong(d.get("employee_id").toString());

                Double taxValueFromDatabase = Double.parseDouble(d.get("deduction_payable_amount").toString());

                if (taxName.equalsIgnoreCase("income tax")) {

                    fullTimeEmployeeList.stream().forEach(emp -> {

                        Long eids = Long.parseLong(emp.get("employee_id").toString());

                        if (Objects.equals(eId, eids)) {

                            Double taxValueFromUi = Double.parseDouble(emp.get("income tax").toString());

                            if (!taxValueFromUi.equals(taxValueFromDatabase)) {

                                LinkedCaseInsensitiveMap mp = new LinkedCaseInsensitiveMap();

                                mp.put("employee_id", eids);

                                mp.put("taxChangeableBalue", taxValueFromUi);

                                taxChangableEmployeeList.add(mp);

                            }

                        }

                    });

                }

            });

            /**
             * Tax Calculation start
             *
             */
            Map tax = this.updateTax(fullTimeEmployeeList, oldAllowanceBackup, update_allowance, month[0], year[0],
                    orgId, taxChangableEmployeeList);
            List<LinkedCaseInsensitiveMap> finaTaxValue = (List<LinkedCaseInsensitiveMap>) tax.get("value");

            /**
             * getting salary breakup for update tds and net payable
             *
             */
            List<SalaryBreakUp> salaryBreakup = salaryBreakupRepo.getSalaryBreakupforUpdate(empidAdd, orgId, month[0],
                    year[0]);

            /**
             * update Deduction
             *
             */
            empData.stream().forEach(edata -> {

                Long eid = Long.parseLong(edata.get("employee_id").toString());

                deduction.stream().forEach(d -> {
                    Long eids = Long.parseLong(d.get("employee_id").toString());
                    Long ids = Long.parseLong(d.get("id").toString());
                    if (eids.equals(eid)) {

                        String deductionName = d.get("deduction_name").toString().toLowerCase();
                        boolean containsKeyIgnoreCase = edata.containsKey(deductionName);

                        if (deductionName.equalsIgnoreCase("income tax")) {

                            finaTaxValue.stream().forEach(ac -> {
                                Long empids = (Long) ac.get("employee_id");
                                if (Objects.equals(empids, eids)) {
                                    update_Deducton.stream().forEach(up -> {
                                        if (up.getId() == ids) {
                                            up.setDeduction_payable_amount(
                                                    Double.parseDouble(ac.get("tax").toString()));
                                        }

                                    });
                                    salaryBreakup.stream().forEach(sb -> {
                                        if (Objects.equals(empids, Long.valueOf(sb.getEmployee_id()))) {
                                            sb.setTds(Double.parseDouble(ac.get("tax").toString()));
                                        }
                                    });

                                }
                            });
                        } else if (deductionName.equalsIgnoreCase("esic")) {
                            Double gross = salaryBreakup.stream().filter(
                                    f -> f.getEmployee_id() == Integer.parseInt(d.get("employee_id").toString()))
                                    .mapToDouble(f -> f.getGross_salary()).sum();

                            Double actualDay = salaryBreakup.stream().filter(
                                    f -> f.getEmployee_id() == Integer.parseInt(d.get("employee_id").toString()))
                                    .mapToDouble(f -> f.getTotal_day()).sum();
                            Double workingDay = salaryBreakup.stream().filter(
                                    f -> f.getEmployee_id() == Integer.parseInt(d.get("employee_id").toString()))
                                    .mapToDouble(f -> f.getWorking_day()).sum();
                            update_Deducton.stream().forEach(up -> {
                                if (up.getId() == ids) {

                                    if (edata.get("employee type").toString().equalsIgnoreCase("Worker")) {
                                        if (d.get("is_esic") == null) {
                                            if (orgSettingData.get("esic") != null) {
                                                if (orgSettingData.get("esic").toString().equalsIgnoreCase("Yes")) {
                                                    if (Double
                                                            .parseDouble(d.get("gross_salary").toString()) <= 21000.0) {
                                                        double esic = ((gross / 26) * workingDay
                                                                + Double.parseDouble(edata.get("overtime").toString())
                                                                - washingAmount[0]) * 0.75 / 100;
                                                        double rounded = Math.round(esic);
                                                        up.setDeduction_payable_amount(rounded);
                                                    }
                                                }

                                            }
                                        } else {
                                            if (d.get("is_esic") != null
                                                    && d.get("is_esic").toString().equalsIgnoreCase("Yes")) {
                                                if (orgSettingData.get("esic") != null) {
                                                    double esic = ((gross / 26) * workingDay
                                                            + Double.parseDouble(edata.get("overtime").toString())
                                                            - washingAmount[0]) * 0.75 / 100;
                                                    double rounded = Math.round(esic);
                                                    up.setDeduction_payable_amount(rounded);
                                                }

                                            }

                                        }

                                    } else {

                                        if (d.get("is_esic") == null) {
                                            if (orgSettingData.get("esic") != null) {
                                                if (orgSettingData.get("esic").toString().equalsIgnoreCase("Yes")) {
                                                    if (Double
                                                            .parseDouble(d.get("gross_salary").toString()) <= 21000.0) {
                                                        double esic = ((gross / actualDay) * workingDay
                                                                + Double.parseDouble(edata.get("overtime").toString())
                                                                - washingAmount[0]) * 0.75 / 100;
                                                        double rounded = Math.round(esic);
                                                        up.setDeduction_payable_amount(rounded);
                                                    }
                                                }

                                            }
                                        } else {
                                            if (d.get("is_esic") != null
                                                    && d.get("is_esic").toString().equalsIgnoreCase("Yes")) {
                                                if (orgSettingData.get("esic") != null) {
                                                    double esic = ((gross / actualDay) * workingDay
                                                            + Double.parseDouble(edata.get("overtime").toString())
                                                            - washingAmount[0]) * 0.75 / 100;
                                                    double rounded = Math.round(esic);
                                                    up.setDeduction_payable_amount(rounded);
                                                }

                                            }

                                        }

                                    }

                                }

                            });

                        } else {
                            if (containsKeyIgnoreCase) {
                                Double valueFromDatabase = Double
                                        .parseDouble(d.get("deduction_payable_amount").toString());
                                Object value = edata.get(deductionName);
                                Double valueFromUi = Double.parseDouble(value.toString());
                                if (!valueFromDatabase.equals(valueFromUi)) {
                                    update_Deducton.stream().forEach(up -> {
                                        if (up.getId() == ids) {
                                            up.setDeduction_payable_amount(valueFromUi);
                                        }
                                    });
                                }
                            }
                        }

                    }
                });

            });

            employeeDeductionRepo.saveAll(update_Deducton);
            employeeAllowanceRepo.saveAll(update_allowance);

            /**
             * Net amount calculation
             *
             */
            List<LinkedCaseInsensitiveMap> allowanceForNetPayCalculation = employeeAllowanceRepo
                    .getAllowancesForUpdate(empidAdd, orgId, month[0], year[0]);

            empData.stream().forEach(edata -> {

                Long eid = Long.parseLong(edata.get("employee_id").toString());

                salaryBreakup.stream().forEach(sb -> {
                    int sbempid = sb.getEmployee_id();

                    if (eid == sbempid) {
                        Double[] sumOFDeduction = new Double[1];
                        sumOFDeduction[0] = 0.0;
                        Double[] sumOFAllowance = new Double[1];
                        sumOFAllowance[0] = 0.0;

                        Double[] exWorkingDay = new Double[1];
                        exWorkingDay[0] = 0.0;

                        Double[] emWorkingDay = new Double[1];
                        emWorkingDay[0] = 0.0;
                        Double[] removeAttendanceIncentive = new Double[1];
                        removeAttendanceIncentive[0] = 0.0;

                        Double empWorking_day = sb.getWorking_day();
                        Double exactWorkingDay = sb.getTotal_day();
                        if (sb.getEmployee_type().equalsIgnoreCase("worker")) {
                            exactWorkingDay = 26.0;
                        }
                        exWorkingDay[0] = exactWorkingDay;
                        emWorkingDay[0] = empWorking_day;
                        Double salary = sb.getPayable_salary();
                        salary = (salary / exWorkingDay[0]) * emWorkingDay[0];
                        /**
                         * Sum of all decution
                         *
                         */
                        update_Deducton.stream().forEach(d -> {
                            Long eids = d.getEmployee_id();
                            if (eids.equals(eid)) {

                                Double deduction_payable_amount = d.getDeduction_payable_amount();
                                sumOFDeduction[0] = sumOFDeduction[0] + deduction_payable_amount;

                            }
                        });

                        /**
                         * Sum of all allowance
                         *
                         */
                        allowanceForNetPayCalculation.stream().forEach(ua -> {

                            Long eids = Long.parseLong(ua.get("employee_id").toString());
                            if (eids.equals(eid)) {
                                String allowanceName = ua.get("allowance_name").toString().toLowerCase();
                                if (allowanceName.equalsIgnoreCase("overtime allowance")) {
                                    allowanceName = "overtime";
                                } else if (allowanceName.equalsIgnoreCase("Attendance Incentives")) {
                                    if (Double.parseDouble(ua.get("allowance_payable_amount").toString()) == 0) {
                                        Double valueOfAI = Double.parseDouble(ua.get("allowance_amount").toString())
                                                / 26;
                                        removeAttendanceIncentive[0] = removeAttendanceIncentive[0]
                                                + (valueOfAI * empWorking_day);
                                    }

                                }
                                boolean containsKeyIgnoreCase = edata.containsKey(allowanceName);

                                if (containsKeyIgnoreCase) {
                                    Double valueFromDatabase = Double
                                            .parseDouble(ua.get("allowance_payable_amount").toString());
                                    sumOFAllowance[0] = sumOFAllowance[0] + valueFromDatabase;
                                }

                            }

                        });

                        // netPayable formula
                        double netPayable = salary - removeAttendanceIncentive[0] - sumOFDeduction[0]
                                + sumOFAllowance[0];
                        double roundedNetPayable = Math.round(netPayable);
                        sb.setNet_amount(roundedNetPayable);

                    }
                });

            });
            salaryBreakupRepo.saveAll(salaryBreakup);
            resultMap.put("status", "success");
            resultMap.put("msg", "updated successfully");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }

        return resultMap;
    }

    private static Object printCellValue(Cell cell) {
        Object obj = null;
        // SimpleDateFormat sdfDate2 = new SimpleDateFormat("dd-MMM-yyyy");
        switch (cell.getCellType()) {

            case NUMERIC:
                try {
                    // if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // Date dt = sdfDate2.parse(cell + "");
                    // obj = sdfDate2.format(dt);
                    // } else {
                    obj = cell.getNumericCellValue();
                    // }
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case STRING:

                obj = cell.getStringCellValue();

                break;
            default:

                obj = " ";
        }

        return obj;
    }

    public Map updateTax(List<LinkedCaseInsensitiveMap> emp, List<LinkedCaseInsensitiveMap> oldAllowance,
            List<EmployeeAllowance> updateAllowance, int month, int year, Long orgId,
            List<LinkedCaseInsensitiveMap> taxChangableEmployeeList) {

        Map resultMap = new HashMap<>();

        List<Long> employeeIds = emp.stream()
                .map(map -> ((BigInteger) map.get("employee_id")).longValue())
                .collect(Collectors.toList());

        /**
         * filtering only full time employee in oldAllowance list and
         * updateAllowance list
         *
         */
        List<LinkedCaseInsensitiveMap> filteredOldAllowance = oldAllowance.stream()
                .filter(oa -> employeeIds.contains(Long.parseLong(oa.get("employee_id").toString())))
                .collect(Collectors.toList());

        List<EmployeeAllowance> filterupdateAllowance = updateAllowance.stream()
                .filter(ua -> employeeIds.contains(ua.getEmployee_id()))
                .collect(Collectors.toList());

        /**
         * sum of all allowances by employee wise
         *
         */
        Map<Object, Double> sumOfOldAllowanceByEmployeeId = filteredOldAllowance.stream()
                .collect(Collectors.groupingBy(
                        map -> getLongValue(map.get("employee_id")),
                        Collectors.summingDouble(map -> (Double) map.get("allowance_payable_amount"))));

        Map<Object, Double> sumOfupdateAllowanceByEmployeeId = filterupdateAllowance.stream()
                .collect(Collectors.groupingBy(m -> m.getEmployee_id(),
                        Collectors.summingDouble(map -> map.getAllowance_payable_amount())));

        /**
         * tax calculation
         *
         */
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
        int[] remaingMonthsForTax = new int[1];
        remaingMonthsForTax[0] = remaingMonths;

        Map<Long, Double> taxableIncome = new HashMap<>();
        Map<Long, Double> previosuEmployerTax = new HashMap<>();
        Map<Long, Double> taxDeductedTillDate = new HashMap<>();
        List<LinkedCaseInsensitiveMap> CollectTaxOfthisMonth = new ArrayList();

        List<LinkedCaseInsensitiveMap> employeePreviousAllowances = new ArrayList<>();
        List<LinkedCaseInsensitiveMap> employeeUpdatedallowance = new ArrayList<>();

        /**
         * we can take previous month details salary slip show that we can
         * update the latest updated Allowance
         *
         */
        if (month != 4) {
            employeePreviousAllowances = incomeRepo.previousEmployeeAllowancesInBulk(previousYaer, previousMonth,
                    employeeIds);
        }

        // this block of code check is allowance in present in this month not present in
        // previous month
        Map<Integer, Map<String, Object>> mergedResults = new LinkedHashMap<>();

        for (Map<String, Object> entry : employeePreviousAllowances) {
            Integer employeeId = Integer.parseInt(entry.get("employee_id").toString());

            mergedResults.putIfAbsent(employeeId, new LinkedHashMap<>());
            Map<String, Object> existingMap = mergedResults.get(employeeId);
            existingMap.put("employee_id", employeeId);

            String salaryHraName = (String) entry.get("salary_hra_name");
            Object salaryHraAmount = entry.get("salary_hra_amount");
            existingMap.put(salaryHraName, salaryHraAmount);
        }
        List<LinkedCaseInsensitiveMap> newList = new ArrayList<>();
        List<Map<String, Object>> mergedList = new ArrayList<>(mergedResults.values());

        for (LinkedCaseInsensitiveMap<Object> em : emp) {
            for (Map<String, Object> mp : mergedList) {
                if (Objects.equals(Long.parseLong(em.get("employee_id").toString()),
                        Long.parseLong(mp.get("employee_id").toString()))) {
                    if (em.containsKey("Reimbursement") && !mp.containsKey("Reimbursement")) {
                        LinkedCaseInsensitiveMap newMap = new LinkedCaseInsensitiveMap();
                        newMap.put("employee_id", em.get("employee_id"));
                        newMap.put("salary_hra_name", "Reimbursement");
                        newMap.put("salary_hra_amount", em.get("Reimbursement"));
                        newList.add(newMap);
                    }
                    if (em.containsKey("arrears") && !mp.containsKey("Arrears")) {
                        LinkedCaseInsensitiveMap newMap = new LinkedCaseInsensitiveMap();
                        newMap.put("employee_id", em.get("employee_id"));
                        newMap.put("salary_hra_name", "Arrears");
                        newMap.put("salary_hra_amount", em.get("arrears"));
                        newList.add(newMap);
                    }
                    if (em.containsKey("overtime") && !mp.containsKey("Reimbursement")) {
                        LinkedCaseInsensitiveMap newMap = new LinkedCaseInsensitiveMap();
                        newMap.put("employee_id", em.get("employee_id"));
                        newMap.put("salary_hra_name", "Overtime");
                        newMap.put("salary_hra_amount", em.get("overtime"));
                        newList.add(newMap);
                    }
                    if (em.containsKey("bonus/incentive") && !mp.containsKey("Bonus/Incentive")) {
                        LinkedCaseInsensitiveMap newMap = new LinkedCaseInsensitiveMap();
                        newMap.put("employee_id", em.get("employee_id"));
                        newMap.put("salary_hra_name", "Bonus/Incentive");
                        newMap.put("salary_hra_amount", em.get("bonus/incentive"));
                        newList.add(newMap);
                    }

                }
            }
        }

        // blocks end here
        for (LinkedCaseInsensitiveMap em : emp) {
            Long empid = Long.parseLong(em.get("employee_id").toString());

            // Use a temporary list to store filtered results
            List<LinkedCaseInsensitiveMap> empFilteredList = new ArrayList<>();

            for (LinkedCaseInsensitiveMap epa : employeePreviousAllowances) {
                Long epaEmpId = Long.parseLong(epa.get("employee_id").toString());
                if (empid.equals(epaEmpId)) {
                    empFilteredList.add(epa);
                }
            }
            if (empFilteredList.size() > 0) {

                for (LinkedCaseInsensitiveMap epa : empFilteredList) {

                    LinkedCaseInsensitiveMap list = new LinkedCaseInsensitiveMap<>();

                    Long epaEmpId = Long.parseLong(epa.get("employee_id").toString());
                    String allowanceName = epa.get("salary_hra_name").toString().toLowerCase();
                    if (allowanceName.equalsIgnoreCase("overtime allowance")) {
                        allowanceName = "overtime";
                    }
                    Double amount = Double.parseDouble(epa.get("salary_hra_amount").toString());
                    if (empid.equals(epaEmpId)) {

                        boolean name = em.containsKey(allowanceName);
                        if (name) {
                            Double amountFromUi = Double.parseDouble(em.get(allowanceName).toString());
                            Double finalValue = amount + amountFromUi;
                            if (allowanceName.equalsIgnoreCase("overtime")) {
                                allowanceName = "overtime allowance";
                            }
                            list.put("employee_id", empid);
                            list.put("name", allowanceName);
                            list.put("amount", finalValue);
                        }
                        employeeUpdatedallowance.add(list);

                    }
                }
                // adding allowance from ui to tax if allowance doesnot exist in previous month
                // tax slip
                if (newList.size() != 0) {
                    for (LinkedCaseInsensitiveMap epa : newList) {

                        LinkedCaseInsensitiveMap list = new LinkedCaseInsensitiveMap<>();

                        Long epaEmpId = Long.parseLong(epa.get("employee_id").toString());
                        String allowanceName = epa.get("salary_hra_name").toString().toLowerCase();
                        if (allowanceName.equalsIgnoreCase("overtime allowance")) {
                            allowanceName = "overtime";
                        }
                        Double amount = Double.parseDouble(epa.get("salary_hra_amount").toString());
                        if (empid.equals(epaEmpId)) {

                            boolean name = em.containsKey(allowanceName);
                            if (name) {
                                Double amountFromUi = Double.parseDouble(em.get(allowanceName).toString());
                                Double finalValue = amountFromUi;
                                if (allowanceName.equalsIgnoreCase("overtime")) {
                                    allowanceName = "overtime allowance";
                                }
                                list.put("employee_id", empid);
                                list.put("name", allowanceName);
                                list.put("amount", finalValue);
                            }
                            employeeUpdatedallowance.add(list);

                        }
                    }
                }

            } else {
                LinkedCaseInsensitiveMap Reimburs = new LinkedCaseInsensitiveMap<>();
                LinkedCaseInsensitiveMap Referral_Allowance = new LinkedCaseInsensitiveMap<>();
                LinkedCaseInsensitiveMap Bonus = new LinkedCaseInsensitiveMap<>();
                LinkedCaseInsensitiveMap Overtime = new LinkedCaseInsensitiveMap<>();
                LinkedCaseInsensitiveMap Arrears = new LinkedCaseInsensitiveMap<>();

                Double reimburs = Double.parseDouble(em.get("Reimbursement").toString());
                Double arrears = Double.parseDouble(em.get("arrears").toString());
                // Double referral= Double.parseDouble(em.get("referral allowance").toString());
                Double incentive = Double.parseDouble(em.get("bonus/incentive").toString());
                Double overtime = Double.parseDouble(em.get("overtime").toString());

                Reimburs.put("employee_id", empid);
                Reimburs.put("name", "Reimbursement");
                Reimburs.put("amount", reimburs);
                employeeUpdatedallowance.add(Reimburs);

                Arrears.put("employee_id", empid);
                Arrears.put("name", "arrears");
                Arrears.put("amount", arrears);
                employeeUpdatedallowance.add(Arrears);

                Bonus.put("employee_id", empid);
                Bonus.put("name", "bonus/incentive");
                Bonus.put("amount", incentive);
                employeeUpdatedallowance.add(Bonus);

                Overtime.put("employee_id", empid);
                Overtime.put("name", "overtime allowance");
                Overtime.put("amount", overtime);
                employeeUpdatedallowance.add(Overtime);

            }

        }

        /**
         * getting all details for tax calculation
         *
         */
        List<IncomeTax> incomeTax = incomeRepo.employeeIncomeTaxInBulk(year, month, employeeIds, orgId);

        List<LinkedCaseInsensitiveMap> oldTaxSlab = taxSlabRepo.oldTaxSlab();
        List<LinkedCaseInsensitiveMap> surCharge = incomeRepo.surCharge();
        List<LinkedCaseInsensitiveMap> newTaxSlab = newTaxRegimeSlabRepo.newTaxSlab(startYear);
        LinkedCaseInsensitiveMap reliefList = incomeRepo.relief();
        Relief87ANewRegime relief87ANewRegime = relief87ANewRegimeRepo.relief87ANewReime(startYear);

        List<LinkedCaseInsensitiveMap> investment = investentRepo.employeeInvestmentInBulk(employeeIds, startYear);

        incomeTax.forEach(tax -> {
            Double expense[] = new Double[1];
            expense[0] = 0.0;
            Double previousExpense[] = new Double[1];
            previousExpense[0] = 0.0;

            Long empid = tax.getEmployee_id();

            sumOfOldAllowanceByEmployeeId.forEach((eid, oldallowance) -> {

                if (empid == Long.parseLong(eid.toString())) {

                    sumOfupdateAllowanceByEmployeeId.forEach((eids, updateallowance) -> {

                        if (empid == Long.parseLong(eids.toString())) {

                            /**
                             * setting updated allowance in details salary slip
                             *
                             */
                            employeeUpdatedallowance.stream().forEach(eua -> {
                                if (eua.get("employee_id") != null) {
                                    Long empids = Long.parseLong(eua.get("employee_id").toString());
                                    if (Objects.equals(empid, empids)) {
                                        if (tax.getSalary_hra_name().equalsIgnoreCase(eua.get("name").toString())) {
                                            Double subTotal = Double.parseDouble(tax.getSalary_hra_amount())
                                                    - oldallowance + updateallowance;
                                            tax.setSalary_hra_amount(String.valueOf(
                                                    Math.round(Double.parseDouble(eua.get("amount").toString()))));
                                        }
                                    }
                                }
                            });

                            if (tax.getSalary_hra_name() != null
                                    && tax.getSalary_hra_name().equalsIgnoreCase("Sub Total")) {
                                Double subTotal = Double.parseDouble(tax.getSalary_hra_amount()) - oldallowance
                                        + updateallowance;
                                tax.setSalary_hra_amount(String.valueOf(Math.round(subTotal)));
                            }

                            if (tax.getTax_name() != null
                                    && tax.getTax_name().equalsIgnoreCase("Total Income(rounded off)")) {
                                emp.stream().forEach(eua -> {

                                    if (eua.get("employee_id") != null) {
                                        Long empids = Long.parseLong(eua.get("employee_id").toString());
                                        if (Objects.equals(empid, empids)) {
                                            if (eua.get("Reimbursement") != null) {
                                                expense[0] = Double.parseDouble(eua.get("Reimbursement").toString());
                                            }

                                        }
                                    }

                                });

                                oldAllowance.stream().forEach(eua -> {

                                    if (eua.get("employee_id") != null) {
                                        Long empids = Long.parseLong(eua.get("employee_id").toString());
                                        if (Objects.equals(empid, empids)) {
                                            if (eua.get("allowance_name") != null && eua.get("allowance_name")
                                                    .toString().equalsIgnoreCase("Reimbursement")) {
                                                previousExpense[0] = Double
                                                        .parseDouble(eua.get("allowance_payable_amount").toString());
                                            }

                                        }
                                    }

                                });
                                expense[0] = expense[0] - previousExpense[0];
                                Double totalIncome = Double.parseDouble(tax.getTax_amount()) - (oldallowance)
                                        + (updateallowance - expense[0]);
                                tax.setTax_amount(String.valueOf(Math.round(totalIncome)));
                                taxableIncome.put(empid, totalIncome);
                            }
                            if (tax.getTax_name() != null
                                    && tax.getTax_name().equalsIgnoreCase("Tax Deducted(Previous Employer)")) {

                                previosuEmployerTax.put(empid, Double.parseDouble(tax.getTax_amount()));

                            }
                            if (tax.getTax_name() != null
                                    && tax.getTax_name().equalsIgnoreCase("Total Tax Deducted Till Date")) {

                                taxDeductedTillDate.put(empid, Double.parseDouble(tax.getTax_amount()));

                            }

                        }

                    });

                }

            });

            if (tax.getExemption_name() != null && tax.getExemption_name().equalsIgnoreCase("Reimbursement Exempted")) {
                employeeUpdatedallowance.stream().forEach(eua -> {
                    if (eua.get("employee_id") != null) {
                        Long empids = Long.parseLong(eua.get("employee_id").toString());
                        if (Objects.equals(empid, empids)) {
                            // tax.setSalary_hra_amount(String.valueOf(Math.round(Double.parseDouble(eua.get("amount").toString()))));
                            if (eua.get("name").toString().equalsIgnoreCase("Reimbursement")) {
                                Long amount = Math.round(Double.parseDouble(eua.get("amount").toString()));
                                tax.setExemption_declared_amount(amount.toString());
                            }
                            // }
                        }
                    }
                });

            }

        });

        /**
         * Investment Declaration and surCharge
         *
         */
        taxableIncome.forEach((ids, taxableIncomevalue) -> {

            String[] slabType = new String[1];

            Double taxLiability[] = new Double[1];
            taxLiability[0] = 0.0;
            Double relief[] = new Double[1];
            relief[0] = 0.0;
            Double educationCess[] = new Double[1];
            educationCess[0] = 0.0;
            Double taxThisMonth[] = new Double[1];
            taxThisMonth[0] = 0.0;

            slabType[0] = "NewTaxSlabKey";
            if (investment.size() > 0) {
                investment.stream().forEach(in -> {
                    Long id = Long.parseLong(in.get("employee_id").toString());
                    if (Objects.equals(id, ids)) {
                        slabType[0] = in.get("tax_slab_tpye").toString();
                    }
                });
            }
            if (slabType[0].equalsIgnoreCase("OldTaxSlabKey")) {
                Double reliefAmount = Double.parseDouble(reliefList.get("income").toString());
                for (LinkedCaseInsensitiveMap slab : oldTaxSlab) {
                    Double slabStart = Double.parseDouble(slab.get("start").toString());
                    Double slabEnd = Double.parseDouble(slab.get("end").toString());
                    Double slabRate = Double.parseDouble(slab.get("rate").toString());

                    if (taxableIncomevalue > slabStart) {
                        double currentSlabTaxableIncome = Math.min(taxableIncomevalue, slabEnd) - slabStart;
                        taxLiability[0] += currentSlabTaxableIncome * (slabRate / 100);

                    } else {
                        break; // No need to continue calculating if income doesn't fall into the slab
                    }
                }

                if (taxableIncomevalue > reliefAmount) {

                    educationCess[0] = (taxLiability[0] / 100) * 4;
                } else {
                    relief[0] = taxLiability[0];

                }

            } else {
                for (LinkedCaseInsensitiveMap slab : newTaxSlab) {
                    Double slabStart = Double.parseDouble(slab.get("start").toString());
                    Double slabEnd = Double.parseDouble(slab.get("end").toString());
                    Double slabRate = Double.parseDouble(slab.get("rate").toString());

                    if (taxableIncomevalue > slabStart) {
                        double currentSlabTaxableIncome = Math.min(taxableIncomevalue, slabEnd) - slabStart;
                        taxLiability[0] += currentSlabTaxableIncome * (slabRate / 100);
                    } else {
                        break;
                    }
                }

                if (taxableIncomevalue > relief87ANewRegime.getIncome()) {
                    educationCess[0] = (taxLiability[0] / 100) * 4;

                } else {
                    relief[0] = taxLiability[0];
                }
            }

            long surcharge = 0;
            for (LinkedCaseInsensitiveMap map : surCharge) {

                Double startIncome = Double.parseDouble(map.get("start").toString());
                Double endIncome = Double.parseDouble(map.get("end").toString());
                if (taxableIncomevalue >= startIncome && taxableIncomevalue <= endIncome) {
                    Double rate = Double.parseDouble(map.get("rate").toString());
                    surcharge = Math.round((taxLiability[0] / 100) * rate);

                }

            }

            Long payBleTax = Math.round(educationCess[0] + taxLiability[0] + surcharge);

            Double[] previosuEmployerTax1 = new Double[1];
            previosuEmployerTax1[0] = 0.0;

            Double[] taxDeductedTillDate1 = new Double[1];
            taxDeductedTillDate1[0] = 0.0;

            previosuEmployerTax.forEach((empid, previousEmployerTaxValue) -> {

                if (Objects.equals(empid, ids)) {
                    previosuEmployerTax1[0] = previousEmployerTaxValue;
                }
            });

            taxDeductedTillDate.forEach((empid, taxdeductedtilldatevalue) -> {

                if (Objects.equals(empid, ids)) {
                    taxDeductedTillDate1[0] = taxdeductedtilldatevalue;
                }
            });

            double taxRemaining = (Math.round(payBleTax) - previosuEmployerTax1[0] - taxDeductedTillDate1[0]);

            if (taxRemaining < 0) {
                taxRemaining = 0;
            }

            Double taxForThisMonth = taxRemaining / remaingMonthsForTax[0];
            taxThisMonth[0] = taxForThisMonth;

            Long taxActul = payBleTax;
            if (relief[0] > 0.0) {
                taxActul = 0l;
                taxThisMonth[0] = 0.0;
                taxRemaining = 0;
            }

            Double[] taxRemaining1 = new Double[1];
            taxRemaining1[0] = taxRemaining;

            taxChangableEmployeeList.stream().forEach(tx -> {

                Long eid = Long.parseLong(tx.get("employee_id").toString());

                if (Objects.equals(eid, ids)) {
                    Double taxChangeableValue = Double.parseDouble(tx.get("taxChangeableBalue").toString());
                    // taxRemaining1[0]=taxRemaining1[0]+(taxThisMonth[0]-taxChangeableValue);
                    taxThisMonth[0] = taxChangeableValue;

                }
            });

            /**
             * update value in Details salary slip
             *
             */
            for (IncomeTax incomeUpdate : incomeTax) {

                Long employee_id = incomeUpdate.getEmployee_id();

                if (Objects.equals(ids, employee_id)) {

                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Tax on Total Income")) {
                        incomeUpdate.setTax_amount(String.valueOf(Math.round(taxLiability[0])));
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Surcharge on Income")) {
                        incomeUpdate.setTax_amount(String.valueOf(surcharge));
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Education Cess")) {
                        incomeUpdate.setTax_amount(String.valueOf(Math.round(educationCess[0])));
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Tax Payable")) {
                        incomeUpdate.setTax_amount(payBleTax.toString());
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Relief u/s 89")) {
                        incomeUpdate.setTax_amount(String.valueOf(Math.round(relief[0])));
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Total Tax Liability")) {
                        incomeUpdate.setTax_amount(taxActul.toString());
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Remaining Tax/Remaining months")) {
                        incomeUpdate.setTax_amount(String.valueOf(Math.round(taxRemaining1[0])));
                    }
                    if (incomeUpdate.getTax_name() != null
                            && incomeUpdate.getTax_name().equalsIgnoreCase("Tax Deduction for this month")) {
                        incomeUpdate.setTax_amount(String.valueOf(Math.round(taxThisMonth[0])));
                    }

                }

            }
            LinkedCaseInsensitiveMap taxvalueadd = new LinkedCaseInsensitiveMap();
            taxvalueadd.put("employee_id", ids);
            taxvalueadd.put("tax", Math.round(taxThisMonth[0]));
            CollectTaxOfthisMonth.add(taxvalueadd);
        });

        incomeRepo.saveAll(incomeTax);
        resultMap.put("status", "success");
        resultMap.put("value", CollectTaxOfthisMonth);

        return resultMap;
    }

    private static Long getLongValue(Object value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof BigInteger) {
            return ((BigInteger) value).longValue();
        } else {
            // Handle other cases if needed
            return null;
        }
    }

    private double calculateDeduction(double payableSalary, Map<String, Object> employeeDetails,
            Map<String, Object> salaryData, Double employeeWorkingDays, LabourLawDeduction data) {

        double deduction = 0.0;
        try {
            Double deductAmount = (payableSalary * data.getPercentageOfSalary()) / 100;

            if (deductAmount < data.getEmployeeDeduction()) {
                if (employeeDetails.get("employee_type").toString().equalsIgnoreCase("Worker")) {
                    deduction = (Double.parseDouble(salaryData.get("salary").toString()) * data.getPercentageOfSalary())
                            / 100;
                } else {
                    deduction = (Double.parseDouble(salaryData.get("salary").toString()) * data.getPercentageOfSalary())
                            / 100;
                }
                deduction = Math.round(deduction);

            } else {
                deduction = data.getEmployeeDeduction();
            }
            deduction = Math.round(deduction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deduction;

    }

    @Override
    public Map getCustomPayRunData(String data, HttpServletRequest request, String search) {
        Map resultMap = new HashMap<>();
        try {

            Double advance = 0.0;
            List<Long> checkEmployeeIds = new ArrayList();
            double total_esic = 0;
            double total_epf = 0;
            double total_professional_tax = 0;
            double total_tds = 0;
            double total_advance = 0;
            double totalPay = 0.0;
            double totalNetPay = 0.0;
            double taxes = 0.0;
            double preTax = 0.0;

            String bearerToken = authenticationFilter.getJwtFromRequest(request);
            HttpHeaders header = new HttpHeaders();
            header.setBearerAuth(bearerToken);
            header.setContentType(MediaType.TEXT_PLAIN);

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.containsKey("organizationId") && map.get("organizationId") != null && map.containsKey("month")
                    && map.get("month") != null && map.containsKey("year") && map.get("year") != null) {
                Long orgId = Long.valueOf(map.get("organizationId").toString());
                int month = Integer.parseInt(map.get("month").toString()) + 1;
                int year = Integer.parseInt(map.get("year").toString());
                Long siteId = Long.parseLong(map.get("siteId").toString());

                /**
                 * Fetching Pay cycle of an organization.
                 */
                LinkedCaseInsensitiveMap salaryDates = payrollSettingRepo.getSalaryDatesCycle(orgId);
                String start_date = salaryDates.get("start_date") != null ? salaryDates.get("start_date").toString()
                        : "0";
                String end_date = salaryDates.get("end_date").toString() != null
                        ? salaryDates.get("end_date").toString()
                        : "0";

                if (start_date.equals("0") || start_date.equals("0")) {
                    logger.info("Start Date and End Date in missing");
                    resultMap.put("status", "error");
                    resultMap.put("msg", "Kindly check Start and End Date in PaySchedule");
                    return resultMap;
                }
                map.put("start_date", start_date);
                map.put("end_date", end_date);

                /**
                 * fetching all the employees of a particular organization from
                 * the manage.
                 */
                long t1 = System.currentTimeMillis();
                long t2;
                Map employeeListResp = this.FetchAllEmployeesFromManageWithSite(header, map, search);

                if (employeeListResp != null && employeeListResp.containsKey("status")
                        && employeeListResp.get("status").equals("success")) {
                    t2 = System.currentTimeMillis();

                    double actualDays = Double.parseDouble(employeeListResp.get("daysInMonth").toString());

                    String startDate = employeeListResp.get("startDate").toString();
                    String endDate = employeeListResp.get("endDate").toString();

                    List<LinkedHashMap> employeeList = (ArrayList) employeeListResp.get("data");

                    System.out.println("final employee from manage");
                    System.out.println(employeeList);

                    /**
                     * fetching the Standard Salary breakup of all the employee.
                     */
                    List<CustomRunPayroll> alreadyRunPayroll = customRunPayrollRepository.alreadyRunpayroll(orgId,
                            month, year, siteId);

                    List<Long> employeeIds = alreadyRunPayroll.stream()
                            .map(CustomRunPayroll::getEmployeeId)
                            .collect(Collectors.toList());

                    Set<Long> employeeListIds = employeeList.stream()
                            .map(emp -> Long.valueOf(String.valueOf(emp.get("employeeId"))))
                            .collect(Collectors.toSet());

                    List<LinkedCaseInsensitiveMap> getSavedEmployeesSalaryStandards = salaryBreakupRepo
                            .getEmployeesSalaryStandard1(orgId, siteId);

                    getSavedEmployeesSalaryStandards.removeIf(map2 -> employeeIds.contains(
                            Long.valueOf(String.valueOf(map2.get("employee_id")))));
                    getSavedEmployeesSalaryStandards.removeIf(
                            map2 -> !employeeListIds.contains(Long.valueOf(String.valueOf(map2.get("employee_id")))));
                    if (getSavedEmployeesSalaryStandards == null || getSavedEmployeesSalaryStandards.isEmpty()) {

                        alreadyRunPayroll.removeIf(run -> {
                            Object empIdObj = run.getEmployeeId();

                            if (empIdObj == null) {
                                return true;
                            }

                            String empIdStr = String.valueOf(empIdObj).trim();

                            boolean exists = employeeListIds.stream()
                                    .map(id -> String.valueOf(id).trim())
                                    .anyMatch(idStr -> idStr.equals(empIdStr));

                            return !exists; // remove if id does NOT match
                        });
                    }
                    List<CustomAllowance> allowanceName = customAllowanceRepository.findAllowanceById(orgId);
                    List<CustomDeduction> deductionName = customDeductionRepository.findDeductionById(orgId);

                    List<LinkedCaseInsensitiveMap> payPlandata = PayPlanRepository.findPayPlanByIdAndEmpType(orgId);

                    List<Long> sidList = getSavedEmployeesSalaryStandards.stream()
                            .map(map1 -> {
                                Object sidObj = map1.get("sid");
                                return sidObj != null ? Long.valueOf(sidObj.toString()) : null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    List<EmployeeAllowance> allowance = employeeAllowanceRepo.allowanceForSaved(sidList);
                    List<EmployeeDeduction> deduction = employeeDeductionRepo.deductionForSaved(sidList);
                    List<AdvanceRcm> advanceData = advanceRcmRepository.getAdvanceData(siteId, startDate, endDate);
                    List<CustomAllowanceAmount> allowanceAmountFromDb = customAllowanceAmountRepository
                            .getAllowanceMonthWise(orgId, month, year, siteId);
                    List<AdvanceRcmAdjustment> getAdvanceRcmAdjustmentData = advanceRcmAdjustmentRepository
                            .getAdvanceAdjustment(orgId, siteId);

                    for (LinkedCaseInsensitiveMap<String> salaryStandard : getSavedEmployeesSalaryStandards) {
                        Object employeeId = salaryStandard.get("employee_id");

                        for (LinkedHashMap<String, Object> employees : employeeList) {
                            if (employeeId != null && employeeId.equals(employees.get("employeeId"))) {
                                salaryStandard.put("employeeCode", employees.get("employeeCode").toString());
                                salaryStandard.put("name", employees.get("employeeName").toString());
                                salaryStandard.put("siteName", employees.get("siteName").toString());
                                salaryStandard.put("holidays", employees.get("holidays").toString());
                                salaryStandard.put("lwp", employees.get("absentCount").toString());
                                salaryStandard.put("present_day", employees.get("presentCount").toString());
                                salaryStandard.put("working_day", employees.get("actualWorkingDays").toString());
                                salaryStandard.put("actual_day", String.valueOf(actualDays));
                                salaryStandard.put("rate", employees.get("over_time").toString());
                                salaryStandard.put("gender", employees.get("gender").toString());
                                salaryStandard.put("week_off", employees.get("weekOff").toString());
                                salaryStandard.put("overTimeFormatted",
                                        employees.get("over_time_formatted").toString());
                                // salaryStandard.put("epfWages", employees.get("epf_wages").toString());

                                break; // Break inner loop once match is found
                            }
                        }
                    }

                    // final calculation start over here
                    List<CustomRunPayroll> runPayrollJson = new ArrayList<>();

                    runPayrollJson.addAll(alreadyRunPayroll);

                    for (LinkedCaseInsensitiveMap salaryStandard : getSavedEmployeesSalaryStandards) {

                        Long employeeId = Long.parseLong(salaryStandard.get("employee_id").toString());
                        Long sid = Long.parseLong(salaryStandard.get("sid").toString());
                        String empType = salaryStandard.get("employee_type").toString();
                        Long extualDatess = Double.valueOf(salaryStandard.get("actual_day").toString()).longValue();

                        double[] advanceAmount = new double[1];
                        advanceAmount[0] = 0.0;

                        double[] siteAdvance = new double[1];
                        siteAdvance[0] = 0.0;

                        double[] CurrentAdvanceAmount = new double[1];
                        CurrentAdvanceAmount[0] = 0.0;

                        double[] remainingAdvanceAmount = new double[1];
                        remainingAdvanceAmount[0] = 0.0;

                        advanceData.stream().forEach(action -> {

                            if (Objects.equals(action.getEmployeeId(), employeeId)) {
                                CurrentAdvanceAmount[0] = CurrentAdvanceAmount[0] + action.getHoAmount();
                                // siteAdvance[0]=action.getAmount();
                            }

                        });

                        getAdvanceRcmAdjustmentData.stream().forEach(action -> {

                            if (Objects.equals(action.getEmployeeId(), employeeId)) {
                                remainingAdvanceAmount[0] = remainingAdvanceAmount[0] + action.getRemainingAdvance();
                            }

                        });

                        Double[] payDays = new Double[1];
                        payDays[0] = 0.0;

                        Double[] payRate = new Double[1];
                        payRate[0] = 0.0;

                        Double[] times = new Double[1];
                        times[0] = 0.0;

                        String[] stateFromPlan = new String[1];
                        stateFromPlan[0] = null;

                        String[] weekOffFlag = new String[1];
                        weekOffFlag[0] = null;

                        String[] secondSalaryFlag = new String[1];
                        secondSalaryFlag[0] = null;

                        String[] overtimeDependOn = new String[1];
                        overtimeDependOn[0] = null;

                        String[] ptAmountDependOn = new String[1];
                        ptAmountDependOn[0] = null;

                        Boolean[] singleMode = new Boolean[1];
                        singleMode[0] = false;

                        String[] bonusDependOn = new String[1];
                        bonusDependOn[0] = null;

                        Double[] percentage = new Double[1];
                        percentage[0] = null;

                        Double[] maximumValue = new Double[1];
                        maximumValue[0] = null;

                        Double[] basicStandardRate = new Double[1];
                        basicStandardRate[0] = null;

                        Long payIdFromSalarybreakup = Long.parseLong(salaryStandard.get("payPlanId").toString());

                        System.out.println(payPlandata);

                        payPlandata.stream().forEach(action -> {
                            Long payId = Long.parseLong(action.get("id").toString());
                            if (Objects.equals(payIdFromSalarybreakup, payId)) {
                                payDays[0] = action.get("days") != null
                                        ? Double.parseDouble(action.get("days").toString())
                                        : 0.0;
                                payRate[0] = action.get("rate") != null
                                        ? Double.parseDouble(action.get("rate").toString())
                                        : 0.0;
                                stateFromPlan[0] = action.get("state_name") != null
                                        ? action.get("state_name").toString()
                                        : null;
                                overtimeDependOn[0] = action.get("overtime") != null ? action.get("overtime").toString()
                                        : null;
                                ptAmountDependOn[0] = action.get("ptamount") != null ? action.get("ptamount").toString()
                                        : null;
                                times[0] = action.get("times") != null
                                        ? Double.parseDouble(action.get("times").toString())
                                        : 1;
                                weekOffFlag[0] = action.get("weekoffflag") != null
                                        ? action.get("weekoffflag").toString()
                                        : null;
                                secondSalaryFlag[0] = action.get("secondsalaryflag") != null
                                        ? action.get("secondsalaryflag").toString()
                                        : null;

                                bonusDependOn[0] = action.get("bonus") != null ? action.get("bonus").toString() : null;
                                percentage[0] = action.get("percentage") != null
                                        ? Double.parseDouble(action.get("percentage").toString())
                                        : 0;
                                maximumValue[0] = action.get("maximumvalue") != null
                                        ? Double.parseDouble(action.get("maximumvalue").toString())
                                        : 0;

                                basicStandardRate[0] = action.get("basic_rate") != null
                                        ? Double.parseDouble(action.get("basic_rate").toString())
                                        : 0;

                            }

                        });

                        double week_off = salaryStandard.get("week_off") != null
                                ? Double.parseDouble(salaryStandard.get("week_off").toString())
                                : 0.0;

                        if (weekOffFlag[0] != null && weekOffFlag[0].equalsIgnoreCase("yes")) {
                            extualDatess = extualDatess - (long) week_off;
                        }

                        if (secondSalaryFlag[0] != null && secondSalaryFlag[0].equalsIgnoreCase("yes")) {
                            singleMode[0] = true;
                        }

                        actualDays = payDays[0] >= 31 ? extualDatess : payDays[0];

                        System.out.println(
                                " >>>> Actual Days of Employee " + employeeId + " " + actualDays + " " + payDays[0]);

                        Double[] otherDeductionFromDB = new Double[1];
                        otherDeductionFromDB[0] = 0.0;

                        Double[] overtimeFromDb = new Double[1];
                        overtimeFromDb[0] = 0.0;

                        Double[] addiIncentiveFromDB = new Double[1];
                        addiIncentiveFromDB[0] = 0.0;

                        Double[] epfdaysFromDB = new Double[1];
                        epfdaysFromDB[0] = 0.0;

                        Double[] coupanFromDB = new Double[1];
                        coupanFromDB[0] = 0.0;

                        Double[] gateDeductionFromDB = new Double[1];
                        gateDeductionFromDB[0] = 0.0;

                        Double[] hoAdvanceFromDB = new Double[1];
                        hoAdvanceFromDB[0] = 0.0;

                        allowanceAmountFromDb.stream().forEach(action -> {

                            if (Objects.equals(action.getEmployeeId(), employeeId)) {

                                if (action.getName().equalsIgnoreCase("siteadvance")) {
                                    siteAdvance[0] = action.getAmount();
                                } else if (action.getName().equalsIgnoreCase("otherdeductions")) {
                                    otherDeductionFromDB[0] = action.getAmount();
                                } else if (action.getName().equalsIgnoreCase("overtime")) {
                                    overtimeFromDb[0] = action.getAmount();
                                } else if (action.getName().equalsIgnoreCase("addiIncentive")) {
                                    addiIncentiveFromDB[0] = action.getAmount();
                                } else if (action.getName().equalsIgnoreCase("epfdays")) {
                                    epfdaysFromDB[0] = action.getAmount();
                                } else if (action.getName().equalsIgnoreCase("coupan")) {
                                    coupanFromDB[0] = action.getAmount();
                                }
                                // else if (action.getName().equalsIgnoreCase("gate deduction")) {
                                // gateDeductionFromDB[0] = action.getAmount();
                                // }
                                else if (action.getName().equalsIgnoreCase("ho advance")) {
                                    hoAdvanceFromDB[0] = action.getAmount();
                                }

                            }

                        });

                        double pDays = salaryStandard.get("present_day") != null
                                ? Double.parseDouble(salaryStandard.get("present_day").toString())
                                : 0.0;
                        double working_day = salaryStandard.get("working_day") != null
                                ? Double.parseDouble(salaryStandard.get("working_day").toString())
                                : 0.0;
                        double holidays = salaryStandard.get("holidays") != null
                                ? Double.parseDouble(salaryStandard.get("holidays").toString())
                                : 0.0;
                        double lwp = salaryStandard.get("lwp") != null
                                ? Double.parseDouble(salaryStandard.get("lwp").toString())
                                : 0.0;

                        String gender = salaryStandard.get("gender") != null ? salaryStandard.get("gender").toString()
                                : "";

                        double epfDays = 0.0;
                        double gateBasicSalary = 0.0;
                        double workingDays = 0.0;
                        if (empType.equalsIgnoreCase("worker")) {
                            workingDays = pDays;
                        } else {
                            if (weekOffFlag[0] != null && weekOffFlag[0].equalsIgnoreCase("yes")) {
                                workingDays = (pDays);
                            } else {
                                workingDays = (pDays + week_off);
                            }
                        }

                        epfDays = epfdaysFromDB[0] > 0 ? epfdaysFromDB[0] : workingDays;

                        Double[] totalAdvance = new Double[1];
                        totalAdvance[0] = CurrentAdvanceAmount[0] + remainingAdvanceAmount[0];

                        advanceAmount[0] = hoAdvanceFromDB[0] > 0 ? hoAdvanceFromDB[0]
                                : CurrentAdvanceAmount[0] + remainingAdvanceAmount[0];

                        // Find allowance and deduction IDs only once
                        Long basicSalaryId = allowanceName.stream()
                                .filter(a -> "Basic Salary".equalsIgnoreCase(a.getAllowanceName().trim())
                                        && empType.equalsIgnoreCase(a.getEmployeeType().trim()))
                                .map(CustomAllowance::getId)
                                .findFirst()
                                .orElse(0L);

                        Long grossSalaryId = allowanceName.stream()
                                .filter(a -> "Gross".equalsIgnoreCase(a.getAllowanceName().trim())
                                        && empType.equalsIgnoreCase(a.getEmployeeType().trim()))
                                .map(CustomAllowance::getId)
                                .findFirst()
                                .orElse(0L);

                        Long overtimeId = allowanceName.stream()
                                .filter(a -> "Overtime Allowance".equalsIgnoreCase(a.getAllowanceName().trim())
                                        && empType.equalsIgnoreCase(a.getEmployeeType().trim()))
                                .map(CustomAllowance::getId)
                                .findFirst()
                                .orElse(0L);

                        Long epfId = deductionName.stream()
                                .filter(d -> "epf".equalsIgnoreCase(d.getDeductionName())
                                        && empType.equalsIgnoreCase(d.getEmployeeType()))
                                .map(CustomDeduction::getId)
                                .findFirst()
                                .orElse(0L);

                        Long esicId = deductionName.stream()
                                .filter(d -> "esic".equalsIgnoreCase(d.getDeductionName())
                                        && empType.equalsIgnoreCase(d.getEmployeeType()))
                                .map(CustomDeduction::getId)
                                .findFirst()
                                .orElse(0L);

                        Long advanceId = deductionName.stream()
                                .filter(d -> "advance".equalsIgnoreCase(d.getDeductionName())
                                        && empType.equalsIgnoreCase(d.getEmployeeType()))
                                .map(CustomDeduction::getId)
                                .findFirst()
                                .orElse(0L);

                        Long ptId = deductionName.stream()
                                .filter(d -> "Professional Tax".equalsIgnoreCase(d.getDeductionName())
                                        && empType.equalsIgnoreCase(d.getEmployeeType()))
                                .map(CustomDeduction::getId)
                                .findFirst()
                                .orElse(0L);

                        Long lwfId = deductionName.stream()
                                .filter(d -> "Labour Welfare Fund".equalsIgnoreCase(d.getDeductionName())
                                        && empType.equalsIgnoreCase(d.getEmployeeType()))
                                .map(CustomDeduction::getId)
                                .findFirst()
                                .orElse(0L);

                        Long otherDeductionId = deductionName.stream()
                                .filter(d -> "Other Deductions".equalsIgnoreCase(d.getDeductionName())
                                        && empType.equalsIgnoreCase(d.getEmployeeType()))
                                .map(CustomDeduction::getId)
                                .findFirst()
                                .orElse(0L);

                        Long hraId = allowanceName.stream()
                                .filter(a -> "Hra".equalsIgnoreCase(a.getAllowanceName().trim())
                                        && empType.equalsIgnoreCase(a.getEmployeeType().trim()))
                                .map(CustomAllowance::getId)
                                .findFirst()
                                .orElse(0L);

                        double basicSalary = allowance.stream()
                                .filter(dd -> basicSalaryId.equals(dd.getAllowance_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .map(EmployeeAllowance::getAllowance_amount)
                                .findFirst()
                                .orElse(0.0);

                        final double basicSalaryStandard = basicSalary;

                        Double[] basicSalary2 = new Double[1];
                        basicSalary2[0] = basicSalary / 26;

                        basicSalary = (basicSalary / 26) * epfDays;

                        Double[] basicSalary1 = new Double[1];
                        basicSalary1[0] = basicSalary;

                        allowance.stream()
                                .filter(dd -> basicSalaryId.equals(dd.getAllowance_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .findFirst()
                                .ifPresent(dd -> {
                                    dd.setAllowance_amount(basicSalaryStandard);
                                    dd.setAllowance_payable_amount(basicSalary1[0]); // add this
                                });

                        double gross_salary = salaryStandard.get("gross_salary") != null
                                ? Double.parseDouble(salaryStandard.get("gross_salary").toString())
                                : 0.0;

                        // double otRate= salaryStandard.get("over_time") !=null
                        // ?Double.parseDouble(salaryStandard.get("over_time").toString()) :0.0;
                        double othours = salaryStandard.get("rate") != null
                                ? Double.parseDouble(salaryStandard.get("rate").toString())
                                : 0.0;
                        double otAmountForCalculation = basicSalary;
                        double ptAmountForCalculation = basicStandardRate[0];

                        double getphAmount = 0.0;
                        getphAmount = Math.round((ptAmountForCalculation) * holidays);

                        if (overtimeDependOn[0] != null && overtimeDependOn[0].equalsIgnoreCase("Gross")) {
                            otAmountForCalculation = gross_salary;
                        }

                        if (ptAmountDependOn[0] != null && ptAmountDependOn[0].equalsIgnoreCase("Gross")) {
                            ptAmountForCalculation = gross_salary;
                            getphAmount = Math.round((ptAmountForCalculation / actualDays) * holidays);
                        }

                        System.out.println("otAmountForCalculation " + " " + employeeId + " " + otAmountForCalculation
                                + " " + actualDays + " " + payRate[0] + " " + othours + " " + times[0]);

                        // double otwages = Math.round(Math.round(((otAmountForCalculation / payDays[0])
                        // / payRate[0]) * othours) * 100.00) / 100.00;
                        double otwages = Math.round(
                                Math.round((((otAmountForCalculation / actualDays) / payRate[0]) * othours) * times[0])
                                        * 100.00)
                                / 100.00;

                        Double[] otwages1 = new Double[1];
                        otwages1[0] = 0.0;

                        otwages1[0] = overtimeFromDb[0] > 0 ? overtimeFromDb[0] : otwages;

                        allowance.stream()
                                .filter(dd -> overtimeId.equals(dd.getAllowance_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .findFirst()
                                .ifPresent(dd -> dd.setAllowance_payable_amount(otwages1[0]));

                        Double epfValue = deduction.stream()
                                .filter(dd -> epfId.equals(dd.getDeduction_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .map(EmployeeDeduction::getDeduction_amount)
                                .findFirst()
                                .orElse(0.0);

                        Double esicValue = deduction.stream()
                                .filter(dd -> esicId.equals(dd.getDeduction_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .map(EmployeeDeduction::getDeduction_amount)
                                .findFirst()
                                .orElse(0.0);

                        deduction.stream()
                                .filter(dd -> advanceId.equals(dd.getDeduction_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .findFirst()
                                .ifPresent(dd -> dd.setDeduction_payable_amount(advanceAmount[0]));

                        CustomRunPayroll json = new CustomRunPayroll();

                        LinkedCaseInsensitiveMap orgSettingData = organizationSetupRepo
                                .fetchWorkingDayAndOrgState(orgId);
                        String stateName = orgSettingData.get("org_state") != null
                                ? orgSettingData.get("org_state").toString()
                                : "";

                        // lwf code start from here
                        Double lwfAmount = employeeService.calculateEmployeeLWF(stateFromPlan[0], gross_salary, empType,
                                month);

                        double lwfValue = 0.0;

                        double salaryPayable = 0.0;
                        double totalDeduction = advanceAmount[0] + siteAdvance[0] + otherDeductionFromDB[0];
                        double secondpart = 0.0;

                        double dailyGross = (gross_salary / actualDays) * epfDays;
                        // second part formula

                        if (empType.equalsIgnoreCase("worker")) {

                            secondpart = Math.round(
                                    Math.round((dailyGross - basicSalary)) * 100.00)
                                    / 100.00;

                            // secondpart = Math.round(
                            // Math.round(((dailyGross - basicSalary) * (workingDays)) / payDays[0]) *
                            // 100.00)
                            // / 100.00;

                            salaryPayable = Math.round(Math.round((gross_salary * (workingDays)) / payDays[0]) * 100.00)
                                    / 100.00;

                        } else if (empType.equalsIgnoreCase("full time")) {

                            secondpart = Math.round(
                                    Math.round((dailyGross - basicSalary)) * 100.00)
                                    / 100.00;

                            salaryPayable = Math.round(Math.round((gross_salary / actualDays) * (workingDays)) * 100.00)
                                    / 100.00;

                        }

                        if (singleMode[0]) {
                            System.out.println("inside single mode of employee " + employeeId);
                            secondpart = 0;
                        }

                        // epf days calculation
                        // if (secondpart > totalDeduction) {

                        // epfDays = workingDays;

                        // } else {
                        // double restAmount = totalDeduction - secondpart;
                        // double result = restAmount / gross_salary;
                        // double roundedUp = Math.ceil(result);
                        // if (workingDays > 6) {
                        // epfDays = workingDays - 4 - roundedUp;
                        // epfDays = workingDays - roundedUp;
                        // } else {
                        // epfDays = workingDays - roundedUp;
                        // }

                        // }

                        // if (empType.equalsIgnoreCase("full time")) {
                        gateBasicSalary = Math.round(Math.round(basicSalary) * 100.00)
                                / 100.00;
                        // } else if (empType.equalsIgnoreCase("worker")) {
                        // gateBasicSalary = Math.round(Math.round((basicSalary * (epfDays)) /
                        // payDays[0]) * 100.00)
                        // / 100.00;

                        // }

                        double esicResult1 = 0.0;
                        double epfwages1 = 0.0;
                        double esicWages = 0.0;

                        // esic calculation
                        Optional<PayPlan> PlanData = PayPlanRepository.findById(payIdFromSalarybreakup);

                        // Rest Allowance calculation

                        if (PlanData.isPresent()) {

                            PayPlan PayPlanData = PlanData.get();
                            List<AllowanceTemplatePayPlan> allowanceTemplatePayPlan = PayPlanData
                                    .getAllowanceTemplatePayPlan();

                            for (AllowanceTemplatePayPlan a : allowanceTemplatePayPlan) {

                                // checking for basic salary
                                if (!Objects.equals(a.getAllowanceId(), basicSalaryId)) {

                                    Long aId = a.getAllowanceId();
                                    Long dependentAllowanceId = a.getAllowanceDependOn();

                                    if (a.getAllowanceType().equalsIgnoreCase("variable")) {

                                        // checkinh for gross
                                        if (Objects.equals(dependentAllowanceId, grossSalaryId)) {
                                            // mujhe doubt hai
                                            double amount = Math
                                                    .round((gross_salary / 100) * a.getAmount() * 100.0)
                                                    / 100.0;

                                            allowance.stream()
                                                    .filter(dd -> aId.equals(dd.getAllowance_id())
                                                            && sid.equals(dd.getSalary_breakup_id()))
                                                    .findFirst()
                                                    .ifPresent(dd -> {
                                                        dd.setAllowance_amount(amount);
                                                        dd.setAllowance_payable_amount(amount);
                                                    });

                                        } else {

                                            double dependentAmount = 0.0;
                                            double dependentAmountPayable = 0.0;
                                            for (EmployeeAllowance ea : allowance) {
                                                if (dependentAllowanceId.equals(ea.getAllowance_id())
                                                        && sid.equals(ea.getSalary_breakup_id())) {
                                                    dependentAmount = Math
                                                            .round((ea.getAllowance_amount() / 100)
                                                                    * a.getAmount() * 100.0)
                                                            / 100.0;
                                                    dependentAmountPayable = Math
                                                            .round((ea.getAllowance_payable_amount() / 100)
                                                                    * a.getAmount() * 100.0)
                                                            / 100.0;

                                                    break;
                                                }
                                            }

                                            final double amount = Math.round(dependentAmount);
                                            final double amountPayable = Math.round(dependentAmountPayable);
                                            allowance.stream()
                                                    .filter(dd -> aId.equals(dd.getAllowance_id())
                                                            && sid.equals(dd.getSalary_breakup_id()))
                                                    .findFirst()
                                                    .ifPresent(dd -> {
                                                        dd.setAllowance_amount(amount);
                                                        dd.setAllowance_payable_amount(amountPayable);
                                                    });

                                        }

                                    }
                                    // else if (a.getAllowanceType().equalsIgnoreCase("other")) {

                                    // a.setCalculatedAmount(a.getAmount() * days);
                                    // json.put("allowance_amount", 0);
                                    // json.put("allowance_id", aId);
                                    // json.put("type_of_allowance", a.getAllowanceType());
                                    // json.put("allowance_name", allowanceName);
                                    // json.put("percentage", a.getAmount() * days);
                                    // allowanceList.add(json);
                                    // }
                                }

                            }
                        }

                        for (EmployeeAllowance action : allowance) {

                            if (action.getAllowance_id() != overtimeId
                                    && action.getAllowance_id() != basicSalaryId
                                    && action.getAllowance_id() != hraId) {
                                if (Objects.equals(action.getEmployee_id(), employeeId)) {

                                    // && action.getType_of_allowance().equalsIgnoreCase("Variable")
                                    if (action.getType_of_allowance() != null) {
                                        action.setAllowance_payable_amount(
                                                (action.getAllowance_amount() / actualDays) * workingDays);
                                    }
                                }
                            }

                        }

                        if (PlanData.isPresent()) {

                            PayPlan PayPlanData = PlanData.get();
                            List<DeductionTemplatePayPlan> deductionTemplatePayPlan = PayPlanData
                                    .getDeductionTemplatePayPlan();

                            System.out.println("deductionTemplatePayPlan " + employeeId + " " + payIdFromSalarybreakup
                                    + " " + esicId);
                            System.out.println(deductionTemplatePayPlan);

                            for (DeductionTemplatePayPlan d : deductionTemplatePayPlan) {

                                if (Objects.equals(esicId, d.getDeductionId())) {

                                    if (d.getDeductionType().equalsIgnoreCase("fixed")) {
                                        esicWages = d.getAmount() + getphAmount;
                                        esicResult1 = d.getAmount() + getphAmount;

                                    } else if (d.getDeductionType().equalsIgnoreCase("variable")) {

                                        List<DeductionDependsOnAllowance> deductionDependsOnAllowance = d
                                                .getDeductionDependOn();

                                        List<Long> dependAllowanceId = deductionDependsOnAllowance.stream()
                                                .map(DeductionDependsOnAllowance::getId)
                                                .collect(Collectors.toList());

                                        double totalAmount1 = 0.0;
                                        for (EmployeeAllowance ea : allowance) {
                                            if (dependAllowanceId.contains(ea.getAllowance_id())
                                                    && ea.getEmployee_id().equals(employeeId)) {
                                                double amt = ea.getAllowance_amount();
                                                // if (amt <= 0.0 && ea.getAllowance_payable_amount() != null) {
                                                totalAmount1 += ea.getAllowance_payable_amount();
                                                // } else {
                                                // totalAmount1 += amt;
                                                // }
                                            }
                                        }

                                        boolean ptFlag = false;

                                        if (dependAllowanceId.contains(grossSalaryId)) {
                                            totalAmount1 += gross_salary;
                                            ptFlag = true;
                                        }

                                        if (!ptFlag) {
                                            totalAmount1 = totalAmount1 + getphAmount;
                                        }

                                        totalAmount1 = totalAmount1 > 21000 ? 0 : totalAmount1;
                                        esicWages = (totalAmount1 / 26) * epfDays;
                                        esicWages = Math.round(esicWages);
                                        esicResult1 = (esicWages * d.getAmount()) / 100;

                                    } else if (d.getDeductionType().equalsIgnoreCase("other")) {
                                        esicResult1 = d.getAmount() + getphAmount;
                                        esicWages = d.getAmount() + getphAmount;
                                    }
                                }

                                if (Objects.equals(epfId, d.getDeductionId())) {

                                    if (d.getDeductionType().equalsIgnoreCase("fixed")) {

                                        epfwages1 = d.getAmount() + getphAmount;

                                    } else if (d.getDeductionType().equalsIgnoreCase("variable")) {

                                        List<DeductionDependsOnAllowance> deductionDependsOnAllowance = d
                                                .getDeductionDependOn();

                                        List<Long> dependAllowanceId = deductionDependsOnAllowance.stream()
                                                .map(DeductionDependsOnAllowance::getId)
                                                .collect(Collectors.toList());

                                        double totalAmount1 = 0.0;
                                        for (EmployeeAllowance ea : allowance) {
                                            if (dependAllowanceId.contains(ea.getAllowance_id())
                                                    && ea.getEmployee_id().equals(employeeId)) {
                                                double amt = ea.getAllowance_amount();
                                                // if (amt <= 0.0 && ea.getAllowance_payable_amount() != null) {
                                                totalAmount1 += ea.getAllowance_payable_amount();
                                                // } else {
                                                // System.out.println("4980 " + amt);
                                                // totalAmount1 += amt;
                                                // }
                                            }
                                        }

                                        if (dependAllowanceId.contains(grossSalaryId)) {
                                            totalAmount1 += gross_salary;
                                        }

                                        // totalAmount1=totalAmount1+getphAmount;
                                        // epfwages1=totalAmount1;
                                        epfwages1 = (totalAmount1);
                                        epfwages1 = epfwages1 + getphAmount;

                                    } else if (d.getDeductionType().equalsIgnoreCase("other")) {
                                        epfwages1 = d.getAmount() + getphAmount;
                                    }
                                    System.out.println("epf wages " + employeeId + " " + epfwages1);
                                }

                            }
                        }

                        // double epfWages=gateBasicSalary+getphAmount;

                        double epfWages = epfwages1;

                        epfWages = Math.ceil(epfWages);

                        Double profeesionalAmount = employeeService.calculateEmployeeProfessionalTax(gender, epfWages,
                                month, year, stateFromPlan[0]);

                        if (epfWages > 15000) {
                            epfWages = 15000;
                        }
                        double epfResult = Math.round(Math.round((epfWages) * 0.12) * 100.00) / 100.00;

                        if (esicValue <= 0) {
                            esicResult1 = 0.0;
                        }

                        // double esicResult = Math.round(esicResult1);

                        System.out.println("esicResult " + employeeId + " " + esicResult1);

                        double esicResult = Math.ceil(esicResult1);

                        if (esicValue > 0) {

                            deduction.stream()
                                    .filter(dd -> esicId.equals(dd.getDeduction_id())
                                            && sid.equals(dd.getSalary_breakup_id()))
                                    .findFirst()
                                    .ifPresent(dd -> dd.setDeduction_payable_amount(esicResult));

                        }

                        if (epfValue > 0) {

                            deduction.stream()
                                    .filter(dd -> epfId.equals(dd.getDeduction_id())
                                            && sid.equals(dd.getSalary_breakup_id()))
                                    .findFirst()
                                    .ifPresent(dd -> dd.setDeduction_payable_amount(epfResult));

                        }

                        deduction.stream()
                                .filter(dd -> ptId.equals(dd.getDeduction_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .findFirst()
                                .ifPresent(dd -> dd.setDeduction_payable_amount(profeesionalAmount));

                        // deduction.stream()
                        // .filter(dd -> lwfId.equals(dd.getDeduction_id()) &&
                        // sid.equals(dd.getSalary_breakup_id()))
                        // .findFirst()
                        // .ifPresent(dd -> dd.setDeduction_payable_amount(lwfAmount));
                        lwfValue = deduction.stream()
                                .filter(dd -> lwfId.equals(dd.getDeduction_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .findFirst()
                                .map(dd -> {
                                    dd.setDeduction_payable_amount(lwfAmount);
                                    return lwfAmount;
                                })
                                .orElse(0.0);

                        deduction.stream()
                                .filter(dd -> otherDeductionId.equals(dd.getDeduction_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .findFirst()
                                .ifPresent(dd -> dd.setDeduction_payable_amount(otherDeductionFromDB[0]));

                        Double hraSalary = allowance.stream()
                                .filter(dd -> hraId.equals(dd.getAllowance_id())
                                        && sid.equals(dd.getSalary_breakup_id()))
                                .map(EmployeeAllowance::getAllowance_amount)
                                .findFirst()
                                .orElse(0.0);

                        System.out.println("hraSalary");
                        System.out.println(hraSalary);

                        double paymentGate = 0.0;
                        gateDeductionFromDB[0] = epfResult + esicResult + profeesionalAmount
                                + lwfValue + coupanFromDB[0];
                        paymentGate = (gateBasicSalary + getphAmount) - (epfResult + esicResult + profeesionalAmount
                                + lwfValue + coupanFromDB[0]);

                        // get Ph amount calculation
                        json.setName(salaryStandard.get("name") != null ? salaryStandard.get("name").toString() : "");
                        json.setEmployeeCode(salaryStandard.get("employeeCode") != null
                                ? salaryStandard.get("employeeCode").toString()
                                : "");
                        json.setEmployeeId(employeeId);
                        json.setPresentDay(pDays);
                        json.setWorkingDay(workingDays);
                        json.setGrossGalary(gross_salary);
                        json.setBasicRate(basicSalary2[0]);
                        json.setPh(holidays);
                        json.setOtHours(othours);
                        json.setOverTimeFormatted(salaryStandard.get("overTimeFormatted") != null
                                ? salaryStandard.get("overTimeFormatted").toString()
                                : "00:00");
                        json.setGateBasicRate(basicSalary2[0]);
                        json.setEpfDays(epfDays);
                        json.setGatePh(getphAmount);
                        json.setEpfWages(epfWages);
                        json.setEsicWages(esicWages);
                        json.setBasicSalary(gateBasicSalary);
                        json.setEpf(epfResult);
                        json.setEsic(esicResult);
                        json.setPt(profeesionalAmount);
                        json.setGlwb(lwfValue);
                        json.setCoupan(coupanFromDB[0]);
                        json.setGateDeduction(gateDeductionFromDB[0]);

                        double netPayableForSingleMode = (salaryPayable + otwages1[0] + addiIncentiveFromDB[0])
                                - totalDeduction - gateBasicSalary;
                        netPayableForSingleMode = Math.round(netPayableForSingleMode);

                        System.out.println("new Payable of " + employeeId + " " + netPayableForSingleMode);

                        json.setPaymentGate(singleMode[0] ? (paymentGate + netPayableForSingleMode) : paymentGate);
                        json.setSecondPart(secondpart);
                        json.setSalaryPayable(salaryPayable);
                        json.setOtWages(otwages1[0]);
                        json.setAddiIncentive(addiIncentiveFromDB[0]);
                        json.setHoAdvance(advanceAmount[0]);
                        json.setSiteAdvance(siteAdvance[0]);
                        json.setCurrentMonthAdvance(CurrentAdvanceAmount[0]);
                        json.setRemainingAdvance(remainingAdvanceAmount[0]);
                        json.setTotalAdvance(totalAdvance[0]);
                        json.setOtherDeduction(otherDeductionFromDB[0]);
                        json.setTotalDeduction(totalDeduction);
                        json.setNetPayable(singleMode[0] ? 0 : netPayableForSingleMode);
                        json.setAnnualCtc(salaryStandard.get("annual_ctc") != null
                                ? Double.parseDouble(salaryStandard.get("annual_ctc").toString())
                                : 0);
                        json.setModeOfPayment(salaryStandard.get("modeofpayment") != null
                                ? salaryStandard.get("modeofpayment").toString()
                                : "");
                        json.setPayrollStatus("Not Done");
                        json.setMonth((long) month);
                        json.setYear((long) year);
                        json.setSiteId(Long.parseLong(salaryStandard.get("siteId").toString()));
                        json.setPayPlanId(Long.parseLong(salaryStandard.get("payPlanId").toString()));
                        json.setOrganizationId(orgId);
                        json.setSid(sid);
                        json.setSite(salaryStandard.get("siteName") != null ? salaryStandard.get("siteName").toString()
                                : "");
                        json.setEmployeeType(empType);
                        json.setEpfWages(epfWages);
                        double hra = 0.0;
                        // hra = Math.round((hraSalary / actualDays) * workingDays);
                        json.setHra(hraSalary);

                        for (EmployeeAllowance action : allowance) {

                            if (action.getAllowance_id() != overtimeId
                                    && action.getAllowance_id() != basicSalaryId
                                    && action.getAllowance_id() != hraId) {
                                if (Objects.equals(action.getEmployee_id(), employeeId)) {

                                    // && action.getType_of_allowance().equalsIgnoreCase("Variable")
                                    if (action.getType_of_allowance() != null) {
                                        action.setAllowance_payable_amount(
                                                (action.getAllowance_amount() / actualDays) * workingDays);
                                    }
                                }
                            }

                        }

                        // rest allowance sum

                        double restAllowanceSum = 0.0;

                        // for (EmployeeAllowance action : allowance) {

                        // if (action.getAllowance_id() != overtimeId
                        // && action.getAllowance_id() != basicSalaryId
                        // && action.getAllowance_id() != hraId) {
                        // if (Objects.equals(action.getEmployee_id(), employeeId)) {

                        // if (action.getType_of_allowance() != null) {
                        // restAllowanceSum = restAllowanceSum + action.getAllowance_payable_amount();
                        // }
                        // }
                        // }

                        // }

                        restAllowanceSum = dailyGross - basicSalary1[0] - hraSalary;

                        restAllowanceSum = Math.round(restAllowanceSum);

                        json.setRestAllowance(restAllowanceSum);

                        double grossWages = Math
                                .round(otwages + restAllowanceSum + hraSalary + gateBasicSalary + getphAmount);
                        json.setGrossWages(grossWages);

                        double bonusValue = gateBasicSalary;

                        if (bonusDependOn[0] != null && bonusDependOn[0].equalsIgnoreCase("Gross")) {
                            bonusValue = grossWages - otwages;
                        }

                        System.out.println(
                                "BONUS VALUE " + employeeId + " " + bonusValue + " percenatage " + percentage[0]);

                        double bonusResult = (bonusValue * percentage[0]) / 100;

                        bonusResult = Math.ceil(bonusResult);

                        System.out.println("BONUS Result " + employeeId + " " + bonusResult);

                        json.setBonus(bonusResult);

                        Set<Long> excludedDeductionIds = new HashSet<>(
                                Arrays.asList(epfId, esicId, advanceId, ptId, otherDeductionId));

                        for (EmployeeDeduction action : deduction) {

                            if (Objects.equals(action.getEmployee_id(), employeeId)) {

                                Long dId = action.getDeduction_id();

                                if (!excludedDeductionIds.contains(dId)) {

                                    action.setDeduction_payable_amount(
                                            (action.getDeduction_amount() / actualDays) * workingDays);
                                }
                            }

                        }

                        runPayrollJson.add(json);

                    }

                    Double[] totalGrossSalary = new Double[1];
                    totalGrossSalary[0] = 0.0;
                    Double[] totalNetPayable = new Double[1];
                    totalNetPayable[0] = 0.0;

                    runPayrollJson.stream().forEach(ac -> {

                        totalGrossSalary[0] = totalGrossSalary[0] + ac.getGrossGalary();
                        totalNetPayable[0] = totalNetPayable[0] + ac.getNetPayable();
                    });

                    resultMap.put("status", "success");
                    resultMap.put("data", runPayrollJson);
                    resultMap.put("employeeAllowance", allowance);
                    resultMap.put("employeeDeduction", deduction);
                    resultMap.put("salaryBreakup", getSavedEmployeesSalaryStandards);
                    resultMap.put("totalGrossSalary", totalGrossSalary[0]);
                    resultMap.put("totalNetPayable", totalNetPayable[0]);

                } else {
                    resultMap = employeeListResp;
                }

            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Please provide valid input and key!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in RunPayServiceImpl -> getCustomPayRunData() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map getSavedRunPayroll(Map map) {
        Map resultMap = new HashMap<>();
        try {
            int month = Integer.parseInt(map.get("month").toString());
            int year = Integer.parseInt(map.get("year").toString());
            Long id = Long.parseLong(map.get("id").toString());
            List<LinkedCaseInsensitiveMap> getAllData = salaryBreakupRepo.getAllSavedDta(id, year, month);

            resultMap.put("status", "success");
            resultMap.put("data", getAllData);

        } catch (Exception ex) {
            logger.info("Problem in RunPayServiceImpl -> getSavedRunPayroll() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @Override
    public Map deleteRunPayroll(Map map) {
        Map resultMap = new HashMap<>();
        try {
            int month = Integer.parseInt(map.get("month").toString());
            int year = Integer.parseInt(map.get("year").toString());
            Long id = Long.parseLong(map.get("id").toString());
            Long employeeId = Long.parseLong(map.get("employeeId").toString());
            Long sid = salaryBreakupRepo.getSid(employeeId, month, year, id);

            if (sid != null) {
                salaryBreakupRepo.deleteSalaryBreakup(sid);
                salaryBreakupRepo.deleteRunPayroll(employeeId, id, year, month);
                salaryBreakupRepo.deleteUploadDataMonthwise(employeeId, id, year, month);
            }
            resultMap.put("status", "success");
            resultMap.put("msg", "Delete Successfully");

        } catch (Exception ex) {
            logger.info("Problem in RunPayServiceImpl -> deleteRunPayroll() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
}

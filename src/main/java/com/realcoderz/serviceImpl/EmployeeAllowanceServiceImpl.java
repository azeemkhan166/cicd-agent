/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.realcoderz.config.GcpConfig;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.AdvanceRcmAdjustment;
import com.realcoderz.model.Allowance;
import com.realcoderz.model.BonusDeduction;
import com.realcoderz.model.CustomAllowanceAmount;
import com.realcoderz.model.CustomRunPayroll;
import com.realcoderz.model.Deduction;
import com.realcoderz.model.DeductionLoan;
import com.realcoderz.model.EmployeeAllowance;
import com.realcoderz.model.EmployeeDeduction;
import com.realcoderz.model.EmployeeGratuity;
import com.realcoderz.model.Form16Document;
import com.realcoderz.model.IncomeTax;
import com.realcoderz.model.OtherAllowances;
import com.realcoderz.model.PayrollLogs;
import com.realcoderz.model.SalaryBreakUp;
import com.realcoderz.model.TempararyAllowance;
import com.realcoderz.model.TempararyDeduction;
import com.realcoderz.model.VariableDeduction;
import com.realcoderz.model.employeeDetails;
import com.realcoderz.repository.AdvanceRcmAdjustmentRepository;
import com.realcoderz.repository.AllowanceRepository;
import com.realcoderz.repository.AuthorizatorySetupRepo;
import com.realcoderz.repository.BonusDeductionRepository;
import com.realcoderz.repository.CustomAllowanceAmountRepository;
import com.realcoderz.repository.CustomRunPayrollRepository;
import com.realcoderz.repository.DeductionLoanRepository;
import com.realcoderz.repository.DeductionRepository;
import com.realcoderz.repository.EmployeeAllowanceRepository;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.repository.EmployeeGratuityRepo;
import com.realcoderz.repository.FAFAllowanceRepository;
import com.realcoderz.repository.FAFDeductionRepository;
import com.realcoderz.repository.FAFOtherEarningRepository;
import com.realcoderz.repository.Form16DocumentRepository;
import com.realcoderz.repository.IncomeTaxRepository;
import com.realcoderz.repository.InvestmentDeclarationRepository;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.repository.OtherAllowancesRepository;
import com.realcoderz.repository.PayrollLogsRepository;
import com.realcoderz.repository.PayrollSettingRepository;
import com.realcoderz.repository.PerksandPerquisiteRepository;
import com.realcoderz.repository.RunPayRollRepository;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.repository.TempararyAllowanceRepository;
import com.realcoderz.repository.TempararyDeductionRepository;
import com.realcoderz.repository.VariableDeductionRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.EmployeeAllowanceService;
import com.realcoderz.service.EmployeeIdDatesService;
import com.realcoderz.service.RunPayService;
import org.springframework.context.annotation.Lazy;
import static com.realcoderz.serviceImpl.DeductionServiceImpl.logger;
import static com.realcoderz.serviceImpl.SalaryBreakupServiceImpl.LOGGER;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.http.HttpHeaders;
import net.minidev.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Mayank
 */
@Service
public class EmployeeAllowanceServiceImpl implements EmployeeAllowanceService {

    private static final ObjectMapper mapper = new ObjectMapper();

    private final EmployeeAllowanceRepository allowanceRepo;
    private final EmployeeDeductionRepository deductionRepo;
    private final SalaryBreakuprepo salaryRepo;
    private final OtherAllowancesRepository otherAllowancesRepo;
    private final BonusDeductionRepository bonusdeductionrepo;
    private final VariableDeductionRepository variabledeductionrepo;
    private final employeeDetailsRepository employeedetailsrepo;
    private final PayrollLogsRepository payrollLogsRepo;
    private final IncomeTaxRepository incomeRepo;
    private final PayrollSettingRepository payrollSettingRepo;
    private final PerksandPerquisiteRepository perksRepo;
    private final InvestmentDeclarationRepository investrepo;
    private final RunPayRollRepository runPayrollrepo;
    private final TaxServiceImpl taxserviceimple;
    private final RestTemplate restTemplate;
    private final JWTAuthenticationFilter authenticationFilter;
    private final Form16DocumentRepository form16docRepo;
    private final Storage storages;
    private final GcpConfig gcpConfig;
    private final OrganizationSetUpRepository orgsetuprepo;
    private final FAFAllowanceRepository fafallowancerepo;
    private final FAFOtherEarningRepository fnfotherRepo;
    private final AuthorizatorySetupRepo authoRepo;
    private final FAFDeductionRepository fnfdeductionrepo;
    private final EmployeeIdDatesService employeeIdService;
    private final EmployeeGratuityRepo gratutityRepo;
    private final AllowanceRepository allowanceRepository;
    private final DeductionRepository deductionRepository;
    private final TempararyAllowanceRepository tempararyAllowanceRepository;
    private final TempararyDeductionRepository tempararyDeductionRepository;
    private final RunPayService payrunService;
    private final DeductionLoanRepository deductionLoanRepository;
    private final CustomRunPayrollRepository customRunPayrollRepository;
    private final CustomAllowanceAmountRepository customAllowanceAmountRepository;
    private final AdvanceRcmAdjustmentRepository advanceRcmAdjustmentRepository;

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    @Value("${bucketName}")
    String bucketName;

    @Value("${gcpFilePath}")
    private String gcpFilePath;

    public EmployeeAllowanceServiceImpl(EmployeeAllowanceRepository allowanceRepo,
                                        EmployeeDeductionRepository deductionRepo,
                                        SalaryBreakuprepo salaryRepo,
                                        OtherAllowancesRepository otherAllowancesRepo,
                                        BonusDeductionRepository bonusdeductionrepo,
                                        VariableDeductionRepository variabledeductionrepo,
                                        employeeDetailsRepository employeedetailsrepo,
                                        PayrollLogsRepository payrollLogsRepo,
                                        IncomeTaxRepository incomeRepo,
                                        PayrollSettingRepository payrollSettingRepo,
                                        PerksandPerquisiteRepository perksRepo,
                                        InvestmentDeclarationRepository investrepo,
                                        RunPayRollRepository runPayrollrepo,
                                        TaxServiceImpl taxserviceimple,
                                        RestTemplate restTemplate,
                                        JWTAuthenticationFilter authenticationFilter,
                                        Form16DocumentRepository form16docRepo,
                                        Storage storages,
                                        GcpConfig gcpConfig,
                                        OrganizationSetUpRepository orgsetuprepo,
                                        FAFAllowanceRepository fafallowancerepo,
                                        FAFOtherEarningRepository fnfotherRepo,
                                        AuthorizatorySetupRepo authoRepo,
                                        FAFDeductionRepository fnfdeductionrepo,
                                        EmployeeIdDatesService employeeIdService,
                                        EmployeeGratuityRepo gratutityRepo,
                                        AllowanceRepository allowanceRepository,
                                        DeductionRepository deductionRepository,
                                        TempararyAllowanceRepository tempararyAllowanceRepository,
                                        TempararyDeductionRepository tempararyDeductionRepository,
                                        @Lazy RunPayService payrunService,
                                        DeductionLoanRepository deductionLoanRepository,
                                        CustomRunPayrollRepository customRunPayrollRepository,
                                        CustomAllowanceAmountRepository customAllowanceAmountRepository,
                                        AdvanceRcmAdjustmentRepository advanceRcmAdjustmentRepository,
                                        @Value("${reimburshment_url}") String reimburshment_url,
                                        @Value("${bucketName}") String bucketName,
                                        @Value("${gcpFilePath}") String gcpFilePath) {
        this.allowanceRepo = allowanceRepo;
        this.deductionRepo = deductionRepo;
        this.salaryRepo = salaryRepo;
        this.otherAllowancesRepo = otherAllowancesRepo;
        this.bonusdeductionrepo = bonusdeductionrepo;
        this.variabledeductionrepo = variabledeductionrepo;
        this.employeedetailsrepo = employeedetailsrepo;
        this.payrollLogsRepo = payrollLogsRepo;
        this.incomeRepo = incomeRepo;
        this.payrollSettingRepo = payrollSettingRepo;
        this.perksRepo = perksRepo;
        this.investrepo = investrepo;
        this.runPayrollrepo = runPayrollrepo;
        this.taxserviceimple = taxserviceimple;
        this.restTemplate = restTemplate;
        this.authenticationFilter = authenticationFilter;
        this.form16docRepo = form16docRepo;
        this.storages = storages;
        this.gcpConfig = gcpConfig;
        this.orgsetuprepo = orgsetuprepo;
        this.fafallowancerepo = fafallowancerepo;
        this.fnfotherRepo = fnfotherRepo;
        this.authoRepo = authoRepo;
        this.fnfdeductionrepo = fnfdeductionrepo;
        this.employeeIdService = employeeIdService;
        this.gratutityRepo = gratutityRepo;
        this.allowanceRepository = allowanceRepository;
        this.deductionRepository = deductionRepository;
        this.tempararyAllowanceRepository = tempararyAllowanceRepository;
        this.tempararyDeductionRepository = tempararyDeductionRepository;
        this.payrunService = payrunService;
        this.deductionLoanRepository = deductionLoanRepository;
        this.customRunPayrollRepository = customRunPayrollRepository;
        this.customAllowanceAmountRepository = customAllowanceAmountRepository;
        this.advanceRcmAdjustmentRepository = advanceRcmAdjustmentRepository;
        this.reimburshment_url = reimburshment_url;
        this.bucketName = bucketName;
        this.gcpFilePath = gcpFilePath;
    }

    // Get Employee Allowances
    @Override
    public Map getEmployeeAllowance(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Long emp_id = Long.parseLong(map.get("emp_id").toString());
            Long org_id = Long.parseLong(map.get("org_id").toString());
            // Fetch Allowances By employee and org id
            List<LinkedCaseInsensitiveMap> allowances = allowanceRepo.allowances(emp_id, org_id);
            // Fetch Deductions By employee and org id
            List<LinkedCaseInsensitiveMap> deductions = deductionRepo.deductions(emp_id, org_id);
            // Allowance and Deduction is Empty
            if (!allowances.isEmpty() && !deductions.isEmpty()) {
                deductions.stream().forEach(d -> {
                    resultMap.put(d.get("deduction_name"), d.get("deduction_amount"));
                });
                allowances.stream().forEach(a -> {
                    resultMap.put(a.get("allowance_name"), a.get("allowance_amount"));
                    resultMap.put(a.get("name"), a.get("amount"));

                });
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Allowances and Deductions are not found.!");
            }

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in EmployeeAllowanceServiceImpl -> getEmployeeAllowance() :: ", ex);
        }

        return resultMap;
    }

    public int getIndex(List<SalaryBreakUp> data, int employeeId) {
        return data.stream()
                .mapToInt(SalaryBreakUp::getEmployee_id)
                .boxed()
                .collect(Collectors.toList())
                .indexOf(employeeId);
    }

    // Save Employee Allowances
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public Map saveEmployeeAllowance(Map map, HttpServletRequest request, String data) {
        Map resultMap = new HashMap<>();
        try {

            {
                // employee_type Get Allowances
                List<EmployeeAllowance> empAllowances = mapper.convertValue(map.get("allowance"),
                        new TypeReference<List<EmployeeAllowance>>() {
                        });
                // Get Deductions
                List<EmployeeDeduction> empDeductions = mapper.convertValue(map.get("deduction"),
                        new TypeReference<List<EmployeeDeduction>>() {
                        });
                // Get Other Allowances
                List<OtherAllowances> otherAllowances = mapper.convertValue(map.get("otherAllowances"),
                        new TypeReference<List<OtherAllowances>>() {
                        });
                // Get Salary Breakup
                List<SalaryBreakUp> salaryBreakup = mapper.convertValue(map.get("empSalaryDetails"),
                        new TypeReference<List<SalaryBreakUp>>() {
                        });

                List<BonusDeduction> bonus = mapper.convertValue(map.get("bonus"),
                        new TypeReference<List<BonusDeduction>>() {
                        });
                // Get Variable
                List<VariableDeduction> variable = mapper.convertValue(map.get("variable"),
                        new TypeReference<List<VariableDeduction>>() {
                        });
                // Get Bonus

                List<IncomeTax> taxToSave = mapper.convertValue(map.get("taxToSave"),
                        new TypeReference<List<IncomeTax>>() {
                        });

                List<LinkedCaseInsensitiveMap> jsonForDeductionLoan = mapper.convertValue(
                        map.get("jsonForLoanDeduction"), new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                        });

                salaryBreakup.removeIf(
                        filter -> (filter.getProjectionMonth() != null && filter.getProjectionYear() != null));
                empDeductions.removeIf(
                        filter -> (filter.getProjectionMonth() != null && filter.getProjectionYear() != null));
                empAllowances.removeIf(
                        filter -> (filter.getProjectionMonth() != null && filter.getProjectionYear() != null));
                otherAllowances.removeIf(
                        filter -> (filter.getProjectionMonth() != null && filter.getProjectionYear() != null));

                if (salaryBreakup.isEmpty()) {

                    resultMap.put("status", "success");
                    resultMap.put("msg", "Data Already Saved");

                    return resultMap;
                }

                List<SalaryBreakUp> salaryBreakupData = salaryRepo.saveAll(salaryBreakup);

                for (EmployeeAllowance allowance : empAllowances) {
                    allowance.setSalary_breakup_id(salaryBreakupData
                            .get(this.getIndex(salaryBreakupData, allowance.getEmployee_id().intValue())).getSid());
                }

                for (EmployeeDeduction deduction : empDeductions) {
                    deduction.setSalary_breakup_id(salaryBreakupData
                            .get(this.getIndex(salaryBreakupData, deduction.getEmployee_id().intValue())).getSid());
                }

                for (OtherAllowances other_allowances : otherAllowances) {
                    if (other_allowances != null) {
                        other_allowances.setSalary_breakup_id(salaryBreakupData
                                .get(this.getIndex(salaryBreakupData, other_allowances.getEmployee_id())).getSid());
                    }
                }

                // Deductions is Empty
                if (!empDeductions.isEmpty()) {
                    deductionRepo.saveAll(empDeductions);
                }
                // Allowances is Empty
                if (!empAllowances.isEmpty()) {

                    allowanceRepo.saveAll(empAllowances);
                }
                // Other Allowances is Empty
                if (otherAllowances != null) {
                    otherAllowancesRepo.saveAll(otherAllowances);
                }
                // Bonus Deduction is Empty
                if (bonus != null) {
                    bonusdeductionrepo.saveAll(bonus);
                }
                // Variable Deduction is Empty
                if (variable != null) {
                    variabledeductionrepo.saveAll(variable);
                }
                if (taxToSave != null) {
                    incomeRepo.saveAll(taxToSave);
                }

                /**
                 * Deduction Loan code
                 **/
                if (!jsonForDeductionLoan.isEmpty()) {

                    List<Long> getPrimaryKey = jsonForDeductionLoan.stream()
                            .map(e -> Long.parseLong(e.get("primaryKey").toString()))
                            .collect(Collectors.toList());

                    List<DeductionLoan> deductionLoanList = deductionLoanRepository.getListByIds(getPrimaryKey);

                    deductionLoanList.stream().forEach(e -> {

                        Long primaryKeyOfDeductionLoan = e.getDeductionLoanId();

                        jsonForDeductionLoan.stream().forEach(action -> {

                            Long id = Long.parseLong(action.get("primaryKey").toString());
                            Double amount = Double.parseDouble(action.get("amount").toString());

                            if (Objects.equals(primaryKeyOfDeductionLoan, id)) {
                                e.setRemainingAmount(e.getRemainingAmount() - amount);
                                e.setAmountRepaid(e.getAmountRepaid() + amount);
                                e.setTenure(e.getTenure() - 1);
                            }
                        });

                    });

                    deductionLoanRepository.saveAll(deductionLoanList);
                }

            }

            List<LinkedHashMap> payRunList = (List<LinkedHashMap>) map.get("dataSource");
            List<String> keysToRemove = Arrays.asList("rateOfPaymentPolicy", "voluntary_epf",
                    "voluntary_epf_percentage", "month", "year", "absentDays", "projectionYear", "total_earning",
                    "holidays", "empStatus", "isSaved", "present_day", "over_time", "alreadyRun", "projectionSaved",
                    "total_payable_earning", "is_esic", "totalAttendnaceCount", "total_hours", "isSalaryBreakupSaved",
                    "organization_id", "salary_hold", "status", "lwp", "gender", "sid", "week_off", "employeeCode",
                    "value", "email", "gross_salary", "actual_duration", "projectionMonth", "dateOfBirth",
                    "payable_salary",
                    "total_deduction", "OtherDeductionValues", "net_amount", "approved_leave", "standard_hours",
                    "Overtime Closing Allowance");
            payRunList.forEach(run -> {
                if (run.containsKey("Reimbursement")) {
                    Object value = run.remove("Reimbursement");
                    run.put("reimburs", value);
                }
            });

            payRunList.forEach(run -> keysToRemove.forEach(run::remove));

            Map payRunMap = new HashMap();
            if (map.get("month") != null && (Integer.parseInt(map.get("month").toString()) == 1
                    || Integer.parseInt(map.get("month").toString()) == 2
                    || Integer.parseInt(map.get("month").toString()) == 3)) {
                int year = Integer.parseInt(map.get("year").toString()) + 1;
                payRunMap.put("year", year);
            } else {
                payRunMap.put("year", map.get("year"));
            }
            payRunMap.put("month", map.get("month"));
            payRunMap.put("organizationId", map.get("organizationId"));

            payRunMap.put("netPay", map.get("netPay"));
            payRunMap.put("salaryHold", map.get("salaryHold"));
            payRunMap.put("list", payRunList);
            resultMap = payrunService.saveAll(payRunMap);

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeAllowanceServiceImpl -> saveEmployeeAllowance() :: ", ex);
            throw ex;

        }
        return resultMap;

    }

    // Save Employee Allowances
    @Override
    @Transactional(rollbackFor = { Exception.class })
    public Map savePayrollData(Map map, HttpServletRequest request, String data) {
        Map resultMap = new HashMap<>();
        try {

            {

                // List<CustomRunPayroll> payRunList = (List<LinkedHashMap>)
                // map.get("dataSource");

                List<CustomRunPayroll> payRunList = mapper.convertValue(map.get("dataSource"),
                        new TypeReference<List<CustomRunPayroll>>() {
                        });

                // employee_type Get Allowances
                List<EmployeeAllowance> empAllowances = mapper.convertValue(map.get("employeeAllowance"),
                        new TypeReference<List<EmployeeAllowance>>() {
                        });
                // Get Deductions
                List<EmployeeDeduction> empDeductions = mapper.convertValue(map.get("employeeDeduction"),
                        new TypeReference<List<EmployeeDeduction>>() {
                        });

                // Get Salary Breakup

                payRunList.removeIf(filters -> filters.getPayrollStatus().equalsIgnoreCase("done"));
                int year, month;

                month = Integer.parseInt(map.get("month").toString());
                year = Integer.parseInt(map.get("year").toString());

                if (map.get("month") != null && (Integer.parseInt(map.get("month").toString()) == 1
                        || Integer.parseInt(map.get("month").toString()) == 2
                        || Integer.parseInt(map.get("month").toString()) == 3)) {
                    year = year + 1;

                }

                List<SalaryBreakUp> salaryBreakup = mapper.convertValue(map.get("salaryBreakup"),
                        new TypeReference<List<SalaryBreakUp>>() {
                        });

                for (SalaryBreakUp s : salaryBreakup) {
                    s.setSid(null); // Assuming there's a setSid() method
                    s.setMonth(month);
                    s.setYear(year);
                }

                List<SalaryBreakUp> salaryBreakupData = salaryRepo.saveAll(salaryBreakup);

                for (EmployeeAllowance allowance : empAllowances) {
                    allowance.setSalary_breakup_id(salaryBreakupData
                            .get(this.getIndex(salaryBreakupData, allowance.getEmployee_id().intValue())).getSid());
                    allowance.setMonth(month);
                    allowance.setYear(year);
                    allowance.setId(null);
                }

                for (EmployeeDeduction deduction : empDeductions) {
                    deduction.setSalary_breakup_id(salaryBreakupData
                            .get(this.getIndex(salaryBreakupData, deduction.getEmployee_id().intValue())).getSid());
                    deduction.setMonth(month);
                    deduction.setYear(year);
                    deduction.setId(0);
                }

                // Deductions is Empty
                if (!empDeductions.isEmpty()) {
                    deductionRepo.saveAll(empDeductions);
                }
                // Allowances is Empty
                if (!empAllowances.isEmpty()) {

                    allowanceRepo.saveAll(empAllowances);
                }

                payRunList.forEach(run -> run.setPayrollStatus("Done"));

                List<AdvanceRcmAdjustment> advanceRcm = new ArrayList<>();

                payRunList.stream().forEach(action -> {

                    double ho = action.getHoAdvance();
                    double currentAdvance = action.getCurrentMonthAdvance();
                    double remainingAdvance = action.getRemainingAdvance();
                    double totalAdvance = action.getTotalAdvance();

                    if (currentAdvance > 0) {

                        AdvanceRcmAdjustment json = new AdvanceRcmAdjustment();
                        json.setCurrentMonthAdvance(currentAdvance);
                        json.setEmployeeId(action.getEmployeeId());
                        json.setMonth(action.getMonth());
                        json.setOrganizationId(action.getOrganizationId());
                        json.setRemainingAdvance(totalAdvance - ho);
                        json.setSiteId(action.getSiteId());
                        json.setPaidAdvance(ho);
                        json.setYear(action.getYear());

                        advanceRcm.add(json);
                    }
                    if (currentAdvance <= 0 && remainingAdvance > 0) {

                        AdvanceRcmAdjustment json = new AdvanceRcmAdjustment();
                        json.setCurrentMonthAdvance(currentAdvance);
                        json.setEmployeeId(action.getEmployeeId());
                        json.setMonth(action.getMonth());
                        json.setOrganizationId(action.getOrganizationId());
                        json.setRemainingAdvance(totalAdvance - ho);
                        json.setSiteId(action.getSiteId());
                        json.setPaidAdvance(ho);
                        json.setYear(action.getYear());

                        advanceRcm.add(json);

                    }

                });

                List<CustomRunPayroll> value = customRunPayrollRepository.saveAll(payRunList);
                advanceRcmAdjustmentRepository.saveAll(advanceRcm);

                resultMap.put("status", "success");
                resultMap.put("data", value);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeAllowanceServiceImpl -> saveEmployeeAllowance() :: ", ex);
            throw ex;

        }
        return resultMap;

    }

    @Override
    public Map findByEmployeeId(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Long emp_id = Long.parseLong(map.get("emp_id").toString());
            Long org_id = Long.parseLong(map.get("org_id").toString());
            int month = Integer.parseInt(map.get("month").toString());
            int year = Integer.parseInt(map.get("year").toString());
            List<LinkedCaseInsensitiveMap> employee_allowance = allowanceRepo.getPrimaryKeyOfEmployeeAllowance(emp_id,
                    org_id, month, year);
            List<LinkedCaseInsensitiveMap> employee_deduction = allowanceRepo.getPrimaryKeyOfEmployeeDeduction(emp_id,
                    org_id, month, year);
            List<LinkedCaseInsensitiveMap> salary_breakup = allowanceRepo.getPrimaryKeyOfSalaryBreakUp(emp_id, org_id,
                    month, year);
            List<LinkedCaseInsensitiveMap> other_allowances = allowanceRepo.getPrimaryKeyOfOtherAllowances(emp_id,
                    org_id, month, year);
            resultMap.clear();
            resultMap.put("employee_allowance", employee_allowance);
            resultMap.put("employee_deduction", employee_deduction);
            resultMap.put("salary_breakup", salary_breakup);
            resultMap.put("other_allowances", other_allowances);
            resultMap.put("status", "success");

        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            LOGGER.info("Problem in EmployeeAllowanceServiceImpl -> getEmployeeAllowance() :: ", ex);
        }

        return resultMap;
    }

    // Save Standard Value Of Employee
    @Override
    public Map saveStandardValueOfEmployee(Map map, HttpServletRequest request, String data) {
        Map resultMap = new HashMap<>();
        try {

            System.out.println(" >>>>>>>>... Map values >>>>>>>>>>>>.");
            System.out.println(map);

            LinkedHashMap effectiveDateChange = (LinkedHashMap) map.get("empSalaryDetails");
            String date = effectiveDateChange.get("effective_date").toString();
            String value[] = date.split("-");

            if (value.length == 2) {

                LinkedCaseInsensitiveMap paycycle = payrollSettingRepo
                        .getSalaryDatesCycle(Long.parseLong(effectiveDateChange.get("organization_id").toString()));

                Long startDate = Long.parseLong(paycycle.get("start_date").toString());
                if (startDate > 9) {
                    date = date.concat("-" + startDate);
                } else {
                    date = date.concat("-0" + startDate);
                }

                effectiveDateChange.put("effective_date", date);
            }

            // Get Allowances
            List<EmployeeAllowance> empAllowances = mapper.convertValue(map.get("allowance"),
                    new TypeReference<List<EmployeeAllowance>>() {
                    });
            // Get Deductions
            List<EmployeeDeduction> empDeductions = mapper.convertValue(map.get("deduction"),
                    new TypeReference<List<EmployeeDeduction>>() {
                    });
            // Get Other Allowances
            OtherAllowances otherAllowances = mapper.convertValue(map.get("otherAllowances"), OtherAllowances.class);
            // Get Salary Breakup
            SalaryBreakUp salaryBreakup = mapper.convertValue(map.get("empSalaryDetails"), SalaryBreakUp.class);
            if (salaryBreakup.getEmployee_type().equalsIgnoreCase("Worker")) {
                salaryBreakup.setGross_salary(salaryBreakup.getGross_salary() * 26);
            }

            LinkedCaseInsensitiveMap bonusdeduction = mapper.convertValue(map.get("bonus"),
                    LinkedCaseInsensitiveMap.class);

            LinkedCaseInsensitiveMap variablededuction = mapper.convertValue(map.get("variable"),
                    LinkedCaseInsensitiveMap.class);

            LinkedCaseInsensitiveMap gradtuityDeduction = mapper.convertValue(map.get("gratuityDeduction"),
                    LinkedCaseInsensitiveMap.class);

            // employee Details
            employeeDetails employee = mapper.convertValue(map.get("employeeDetails"), employeeDetails.class);

            // condition to check Stadard value save or Not
            // NEW payroll ke liye comment kiye the
            // List<LinkedCaseInsensitiveMap> isSalaryBreakupSave =
            // salaryRepo.isSalaryBreakupSave(salaryBreakup.getEmployee_id(),
            // salaryBreakup.getGross_salary());

            // save OR Update User Details
            employeeDetails setDetails = employeedetailsrepo.findByEmployeeId(employee.getEmployeeId());

            if (setDetails != null) {

                setDetails.setDob(employee.getDob());
                setDetails.setEmail(employee.getEmail());
                setDetails.setEmpDesingnation(employee.getEmpDesingnation());
                setDetails.setEmployeeCode(employee.getEmployeeCode());
                setDetails.setEmployeeType(employee.getEmployeeType());
                setDetails.setEsic(employee.getEsic());
                setDetails.setGender(employee.getGender());
                setDetails.setJoiningDate(employee.getJoiningDate());
                setDetails.setLin(employee.getLin());
                setDetails.setMobile(employee.getMobile());
                setDetails.setName(employee.getName());
                setDetails.setPanNumber(employee.getPanNumber());
                setDetails.setPf(employee.getPf());
                setDetails.setUan(employee.getUan());
                setDetails.setAadharNumber(employee.getAadharNumber());
                setDetails.setDepartmentName(employee.getDepartmentName());
                setDetails.setIfsc(employee.getIfsc());
                setDetails.setBankAccount(employee.getBankAccount());
                setDetails.setBankName(employee.getBankName());
                setDetails.setBranch(employee.getBranch());
                setDetails.setAddress(employee.getAddress());
                setDetails.setEmployeeWorkLocation(employee.getEmployeeWorkLocation());
                setDetails.setGrade(employee.getGrade());

                employeedetailsrepo.save(setDetails);

            } else {

                employeedetailsrepo.save(employee);
            }
            // end code of save or update User Details

            // if Salary Break Save
            // if (isSalaryBreakupSave.size() > 0) {
            //
            // resultMap.put("status", "success");
            // resultMap.put("msg", "Salary Breakup Already Save!!");
            // } else {

            SalaryBreakUp salarybreakupId = null;

            // save Salary Breakup
            if (salaryBreakup != null) {

                salarybreakupId = salaryRepo.save(salaryBreakup);
            }
            // save Deductions

            if (!(map.get("employee_type").toString()).equalsIgnoreCase("Intern")) {

                if (!empDeductions.isEmpty()) {

                    if (salarybreakupId != null) {

                        for (EmployeeDeduction d : empDeductions) {

                            d.setSalary_breakup_id(salarybreakupId.getSid());
                        }
                        deductionRepo.saveAll(empDeductions);
                    }

                }

                if (bonusdeduction != null) {

                    if (!bonusdeduction.isEmpty()) {

                        BonusDeduction bd = new BonusDeduction();
                        Integer empid = salarybreakupId.getEmployee_id();
                        bd.setEmployee_id(Long.parseLong(empid.toString()));
                        bd.setSalary_breaup_id(salarybreakupId.getSid());
                        bd.setAmount(Double.parseDouble(bonusdeduction.get("deduction_amount").toString()));
                        bd.setOrganization_id(salarybreakupId.getOrganization_id());
                        bd.setEffective_date(salarybreakupId.getEffective_date());

                        bonusdeductionrepo.save(bd);

                    }
                }

                if (variablededuction != null) {

                    if (!variablededuction.isEmpty()) {

                        VariableDeduction vd = new VariableDeduction();
                        Integer empid = salarybreakupId.getEmployee_id();
                        vd.setEmployee_id(Long.parseLong(empid.toString()));
                        vd.setSalary_breaup_id(salarybreakupId.getSid());
                        vd.setOrganization_id(salarybreakupId.getOrganization_id());
                        vd.setAmount(Double.parseDouble(variablededuction.get("deduction_amount").toString()));
                        vd.setEffective_date(salarybreakupId.getEffective_date());
                        variabledeductionrepo.save(vd);
                    }

                }

                if (gradtuityDeduction != null && !gradtuityDeduction.isEmpty()) {
                    EmployeeGratuity gratuity = new EmployeeGratuity();
                    Integer empid = salarybreakupId.getEmployee_id();
                    gratuity.setEmployee_id(Long.parseLong(empid.toString()));
                    gratuity.setSalary_breakup_id(salarybreakupId.getSid());
                    gratuity.setOrganization_id(salarybreakupId.getOrganization_id());
                    Double amount = gradtuityDeduction.get("deduction_amount") != null
                            ? Double.parseDouble(gradtuityDeduction.get("deduction_amount").toString())
                            : 0.0;
                    gratuity.setAmount(amount);
                    Double percentage = gradtuityDeduction.get("employer_percentage") != null
                            ? Double.parseDouble(gradtuityDeduction.get("employer_percentage").toString())
                            : 0.0;
                    gratuity.setEmployerPercentage(percentage);
                    gratuity.setEffective_date(salarybreakupId.getEffective_date());
                    gratutityRepo.save(gratuity);
                }

            }

            // save Allowances
            if (!empAllowances.isEmpty()) {

                if (salarybreakupId != null) {

                    for (EmployeeAllowance a : empAllowances) {

                        a.setSalary_breakup_id(salarybreakupId.getSid());
                    }
                    if ((map.get("employee_type").toString().equalsIgnoreCase("full time")
                            || map.get("employee_type").toString().equalsIgnoreCase("probation")
                            || map.get("employee_type").toString().equalsIgnoreCase("permanent")
                            || map.get("employee_type").toString().equalsIgnoreCase("worker"))) {
                        List<EmployeeAllowance> filteredList = empAllowances.stream()
                                .filter(employeeAllowance -> employeeAllowance.getAllowance_id() != null)
                                .collect(Collectors.toList());
                        allowanceRepo.saveAll(filteredList);
                    } else {
                        allowanceRepo.saveAll(empAllowances);
                    }

                }

            }
            // save Other Allowances

            if (!(map.get("employee_type").toString()).equalsIgnoreCase("Consultant")
                    || !(map.get("employee_type").toString()).equalsIgnoreCase("Intern")) {

                if (otherAllowances != null) {

                    if (salarybreakupId != null) {
                        otherAllowances.setSalary_breakup_id(salarybreakupId.getSid());
                        otherAllowancesRepo.save(otherAllowances);
                    }
                }

            }

            resultMap.put("status", "success");
            resultMap.put("msg", "Data save successfully!!");

            /**
             * save the logs of EPF Updated value *
             */
            if (map.get("epfValueChange") != null) {

                System.out.println(Long.parseLong(map.get("epfValueChange").toString()));
                Long EPFValue = Long.parseLong(map.get("epfValueChange").toString());
                if (EPFValue > 0) {

                    PayrollLogs logs = new PayrollLogs();
                    logs.setColumnName("EPF");
                    logs.setDescription(
                            "You are changing the EPF value outside the government laws. System won’t be responsible for any audits trails");
                    logs.setEmployeeId(employee.getEmployeeId());
                    logs.setOrganizationId(salaryBreakup.getOrganization_id());
                    logs.setUpdatedValue(EPFValue);

                    payrollLogsRepo.save(logs);
                }
            }

            new Thread(() -> {
                try {
                    employeeIdService.saveEmployeeInvestmentDates(
                            Long.valueOf(salaryBreakup.getEmployee_id()),
                            LocalDate.now().getYear(),
                            salaryBreakup.getOrganization_id(),
                            false);
                } catch (Exception e) {
                    // Handle the exception (e.g., log it)
                    e.printStackTrace();
                }
            }).start();

            // }

        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeAllowanceServiceImpl -> saveStandardValueOfEmployee() :: ", ex);

        }
        return resultMap;

    }

    @Override
    public Map gettingAllowances(Long employeeId, Long organizationId, Integer year, Integer month,
            Long salaryBreaupId) {

        Map response = new HashMap();

        try {

            List<LinkedCaseInsensitiveMap> tempAllowance = tempararyAllowanceRepository
                    .getTempararyAllowance(employeeId, month, year);
            List<LinkedCaseInsensitiveMap> tempDeduction = tempararyDeductionRepository
                    .getTempararyDeduction(employeeId, month, year);

            // Handle potential null values
            if (tempAllowance == null) {
                tempAllowance = new ArrayList<>();
            }
            if (tempDeduction == null) {
                tempDeduction = new ArrayList<>();
            }

            // If tempAllowance is empty, retrieve from another source
            if (tempAllowance.isEmpty()) {
                List<LinkedCaseInsensitiveMap> tempAllowance1 = allowanceRepo.empAllowanceBySid(salaryBreaupId);

                // Assign back to tempAllowance if needed
                tempAllowance = tempAllowance1;
            }

            // If tempDeduction is empty, retrieve from another source
            if (tempDeduction.isEmpty()) {
                List<LinkedCaseInsensitiveMap> tempDeduction1 = deductionRepo.getDeductionBySid(salaryBreaupId);

                // Assign back to tempDeduction if needed
                tempDeduction = tempDeduction1;
            }

            response.put("rate", 0);
            response.put("overtime", 0);
            response.put("allowances", tempAllowance);
            response.put("deductions", tempDeduction);
            response.put("status", "success");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public Map updateEmployeeAllowanceAndDeductions(Map map) {

        Map response = new HashMap();

        try {

            Long orgId = Long.parseLong(map.get("organizationId").toString());
            Long employeeId = Long.parseLong(map.get("employeeId").toString());
            String employeeType = map.get("employeeType").toString();
            int month = Integer.parseInt(map.get("month").toString());
            int year = Integer.parseInt(map.get("year").toString());

            /**
             * getting data from Ui
             *
             */
            List<LinkedCaseInsensitiveMap> allowanceFromUi = mapper.convertValue(map.get("allowances"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            allowanceFromUi.removeIf(Objects::isNull);

            List<LinkedCaseInsensitiveMap> deductionromUi = mapper.convertValue(map.get("deductions"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            deductionromUi.removeIf(Objects::isNull);
            /**
             * getting Org level Allowance and Deduction
             *
             */
            List<Allowance> allowanceData = allowanceRepository.findApprovedAllowance(orgId, employeeType);
            List<Deduction> deductionData = deductionRepository.findApprovedDeduction(orgId, employeeType);

            /**
             * getting saved Allowance and Deduction
             *
             */
            List<TempararyAllowance> tempAllowance = tempararyAllowanceRepository
                    .getTempararyAllowanceOfEmployee(employeeId, month, year);
            List<TempararyDeduction> tempDeduction = tempararyDeductionRepository
                    .getTempararyDeductionOfEmployee(employeeId, month, year);

            allowanceData.stream()
                    .forEach(a -> {

                        String allowanceName = a.getAllowance_name().toLowerCase();
                        if (allowanceName.equals("reimbursement")
                                || allowanceName.equals("arrears")
                                || allowanceName.equals("bonus/incentive")
                                || allowanceName.equals("overtime")
                                || allowanceName.equals("overtime allowance")
                                || allowanceName.equals("overtime allowances")) {

                            Optional<TempararyAllowance> existingAllowanceOpt = tempAllowance.stream()
                                    .filter(t -> t.getEmployeeId().equals(employeeId)
                                            && t.getName().equalsIgnoreCase(a.getAllowance_name())
                                            && t.getMonth().equals(month)
                                            && t.getYear().equals(year))
                                    .findFirst();

                            TempararyAllowance temAllowance;

                            if (existingAllowanceOpt.isPresent()) {
                                temAllowance = existingAllowanceOpt.get();
                            } else {

                                temAllowance = new TempararyAllowance();
                                temAllowance.setAllowanceId(a.getAllowance_id());
                                temAllowance.setEmployeeId(employeeId);
                                temAllowance.setMonth(month);
                                temAllowance.setName(a.getAllowance_name());
                                temAllowance.setOrganizationId(orgId);
                                temAllowance.setYear(year);
                                tempAllowance.add(temAllowance); // Add new allowance to the list
                            }

                            // Update the amount based on the allowance name
                            if (allowanceName.equals("reimbursement")) {
                                temAllowance.setAmount(
                                        allowanceFromUi.stream()
                                                .filter(f -> f.get("allowance_name").toString()
                                                        .equalsIgnoreCase("reimbursement"))
                                                .findFirst()
                                                .map(f -> Double
                                                        .parseDouble(f.get("allowance_payable_amount").toString()))
                                                .orElse(0.0));
                            } else if (allowanceName.equals("arrears")) {
                                temAllowance.setAmount(
                                        allowanceFromUi.stream()
                                                .filter(f -> f.get("allowance_name").toString()
                                                        .equalsIgnoreCase("arrears"))
                                                .findFirst()
                                                .map(f -> Double
                                                        .parseDouble(f.get("allowance_payable_amount").toString()))
                                                .orElse(0.0));
                            } else if (allowanceName.equals("bonus/incentive")) {
                                temAllowance.setAmount(
                                        allowanceFromUi.stream()
                                                .filter(f -> f.get("allowance_name").toString()
                                                        .equalsIgnoreCase("bonus/incentive"))
                                                .findFirst()
                                                .map(f -> Double
                                                        .parseDouble(f.get("allowance_payable_amount").toString()))
                                                .orElse(0.0));
                            } else if (allowanceName.equals("overtime")
                                    || allowanceName.equals("overtime allowance")
                                    || allowanceName.equals("overtime allowances")) {
                                temAllowance.setAmount(
                                        allowanceFromUi.stream().filter(
                                                f -> f.get("allowance_name").toString().equalsIgnoreCase("overtime")
                                                        || f.get("allowance_name").toString()
                                                                .equalsIgnoreCase("overtime allowance")
                                                        || f.get("allowance_name").toString()
                                                                .equalsIgnoreCase("overtime allowances"))
                                                .findFirst()
                                                .map(f -> Double
                                                        .parseDouble(f.get("allowance_payable_amount").toString()))
                                                .orElse(0.0));
                            }

                        }
                    });

            /**
             * For Deduction
             *
             */
            deductionData.stream()
                    .forEach(d -> {

                        String deductionName = d.getDeduction_name().toLowerCase();
                        if (deductionName.equals("advance")
                                || deductionName.equals("income tax")
                                || deductionName.equals("other deductions")) {
                            // Check if an allowance record already exists for this employee, allowance
                            // type, month, year, and employee type
                            Optional<TempararyDeduction> existingDeductionOpt = tempDeduction.stream()
                                    .filter(t -> t.getEmployeeId().equals(employeeId)
                                            && t.getName().equalsIgnoreCase(d.getDeduction_name())
                                            && t.getMonth().equals(month)
                                            && t.getYear().equals(year))
                                    .findFirst();

                            TempararyDeduction temDeduction;
                            if (existingDeductionOpt.isPresent()) {
                                temDeduction = existingDeductionOpt.get();
                            } else {
                                temDeduction = new TempararyDeduction();
                                temDeduction.setDeductionId(d.getDeduction_id());
                                temDeduction.setEmployeeId(employeeId);
                                temDeduction.setMonth(month);
                                temDeduction.setName(d.getDeduction_name());
                                temDeduction.setOrganizationId(orgId);
                                temDeduction.setUpdateTds("No");
                                temDeduction.setYear(year);
                                tempDeduction.add(temDeduction);

                            }

                            // Update the amount based on the deduction name
                            if (deductionName.equals("advance")) {

                                temDeduction.setAmount(
                                        deductionromUi.stream()
                                                .filter(f -> f.get("deduction_name").toString()
                                                        .equalsIgnoreCase("advance"))
                                                .findFirst()
                                                .map(f -> Double
                                                        .parseDouble(f.get("deduction_payable_amount").toString()))
                                                .orElse(0.0));

                            } else if (deductionName.equals("income tax")) {

                                temDeduction.setAmount(
                                        deductionromUi.stream()
                                                .filter(f -> f.get("deduction_name").toString()
                                                        .equalsIgnoreCase("income tax"))
                                                .findFirst()
                                                .map(f -> Double.parseDouble(f.get("deduction_amount").toString()))
                                                .orElse(0.0));

                            } else if (deductionName.equals("other deductions")) {

                                temDeduction.setAmount(
                                        deductionromUi.stream()
                                                .filter(f -> f.get("deduction_name").toString()
                                                        .equalsIgnoreCase("other deductions"))
                                                .findFirst()
                                                .map(f -> Double
                                                        .parseDouble(f.get("deduction_payable_amount").toString()))
                                                .orElse(0.0));
                            }
                        }

                    });

            tempararyAllowanceRepository.saveAll(tempAllowance);
            tempararyDeductionRepository.saveAll(tempDeduction);
            response.put("status", "success");
            response.put("msg", "data Saved SuccessFully");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public Map updateTds(Map map) {

        Map response = new HashMap();

        try {

            Long orgId = Long.parseLong(map.get("organizationId").toString());
            Long employeeId = Long.parseLong(map.get("employeeId").toString());
            int month = Integer.parseInt(map.get("month").toString());
            int year = Integer.parseInt(map.get("year").toString());
            Double tds = Double.parseDouble(map.get("tds").toString());

            Deduction deductionData = deductionRepository.approvedIncomeTaxDeduction(orgId);

            if (deductionData == null) {
                response.put("status", "error");
                response.put("msg", "Income Tax Not created");
                return response;
            }
            Optional<TempararyDeduction> existingDeductionOpt = tempararyDeductionRepository.getIncomeTax(employeeId,
                    month, year);

            TempararyDeduction temDeduction;
            if (existingDeductionOpt.isPresent()) {

                temDeduction = existingDeductionOpt.get();
            } else {
                temDeduction = new TempararyDeduction();
                temDeduction.setDeductionId(deductionData.getDeduction_id());
                temDeduction.setEmployeeId(employeeId);
                temDeduction.setMonth(month);
                temDeduction.setName(deductionData.getDeduction_name());
                temDeduction.setOrganizationId(orgId);
                temDeduction.setYear(year);
            }
            temDeduction.setUpdateTds("Yes");
            temDeduction.setAmount(tds);

            tempararyDeductionRepository.save(temDeduction);

            response.put("status", "success");
            response.put("msg", "Tds Updated Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    }

    @Override
    public Map updateemployeedetails(Map map) {
        Map response = new HashMap();
        try {

            // save OR Update User Details
            employeeDetails setDetails = employeedetailsrepo
                    .findByEmployeeId(Long.parseLong(map.get("employeeId").toString()));

            if (setDetails != null) {

                setDetails.setLin(map.get("lin") != null ? map.get("lin").toString() : "");
                setDetails.setPanNumber(map.get("pan") != null ? map.get("pan").toString() : "");
                setDetails.setPf(map.get("pf") != null ? map.get("pf").toString() : "");
                setDetails.setUan(map.get("uan") != null ? map.get("uan").toString() : "");
                setDetails.setAadharNumber(map.get("adhar") != null ? map.get("adhar").toString() : "");
                setDetails.setEsic(map.get("esic") != null ? map.get("esic").toString() : "");
                employeedetailsrepo.save(setDetails);
                response.put("status", "success");
                response.put("msg", "updated successfully!");
            } else {
                response.put("status", "error");
                response.put("msg", "employee Details not available in Payroll");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "exception");
            response.put("msg", "something went wrong");
        }
        return response;
    }

    @Override
    public Map form16Details(Map map, HttpServletRequest request) {

        Map response = new HashMap<>();
        try {

            System.out.println("map >>>>>>>>>>>>" + map);
            List<LinkedCaseInsensitiveMap> getAllowances = allowanceRepo.getSumOfAllowancesForForm16(
                    Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("year").toString()),
                    Integer.parseInt(map.get("year").toString()) + 1);
            List<LinkedCaseInsensitiveMap> getOtherAllowances = otherAllowancesRepo.getOtherAllowancesForFoem16(
                    Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("organization_id").toString()), Integer.parseInt(map.get("year").toString()),
                    Integer.parseInt(map.get("year").toString()) + 1);

            double sum = 0.0;

            for (Map<String, Object> allowance : getOtherAllowances) {
                // Retrieve the value for the particular key you want to sum
                Double value = Double.parseDouble(allowance.get("payable_amount").toString());

                if (value instanceof Number) {
                    sum += ((Number) value).doubleValue();
                }
            }

            LinkedCaseInsensitiveMap otherallwance = new LinkedCaseInsensitiveMap<>();

            otherallwance.put("allowance_name", "Other Allowance");
            otherallwance.put("employee_id", Long.parseLong(map.get("employee_id").toString()));
            otherallwance.put("allowance_payable_amount", Math.round(sum));

            getAllowances.add(otherallwance);

            double YTDAllowanceSum = 0.0;

            for (Map<String, Object> allowance : getAllowances) {
                // Retrieve the value for the particular key you want to sum
                Double value = allowance.get("allowance_payable_amount") != null
                        ? Double.parseDouble(allowance.get("allowance_payable_amount").toString())
                        : 0.0;

                // Check if the value is a number and add it to the sum
                if (value instanceof Number) {
                    YTDAllowanceSum += ((Number) value).doubleValue();
                }
            }

            /**
             * Fetching fnf Details
             *
             */
            Double fafsum = fafallowancerepo.getTotalAllowanceSum(Long.parseLong(map.get("employee_id").toString()));

            if (fafsum != null) {
                YTDAllowanceSum = YTDAllowanceSum + fafsum;
            }

            Double incentive = fnfotherRepo.getIncentive(Long.parseLong(map.get("employee_id").toString()));

            if (incentive != null) {
                YTDAllowanceSum = YTDAllowanceSum + incentive;
            }

            LinkedCaseInsensitiveMap investment = investrepo.getInvestmentForform16(
                    Long.parseLong(map.get("employee_id").toString()), Integer.parseInt(map.get("year").toString()));
            // System.out.println("investment 1045"+" "+investment.toString());
            LinkedCaseInsensitiveMap investmentOfEmployee = investrepo.getInvestmentForform16OFEmployee(
                    Long.parseLong(map.get("employee_id").toString()), Integer.parseInt(map.get("year").toString()));

            LinkedCaseInsensitiveMap lastrunPayroll = runPayrollrepo.getLastRunPayroll(
                    Long.parseLong(map.get("employee_id").toString()), Integer.parseInt(map.get("year").toString()),
                    Integer.parseInt(map.get("year").toString()) + 1);

            if (lastrunPayroll == null) {
                response.put("status", "error");
                response.put("msg", "Run Payroll not done in this financial year");
                return response;
            }

            List<IncomeTax> incometax = incomeRepo.employeeIncomeTax(
                    Integer.parseInt(lastrunPayroll.get("pay_run_year").toString()),
                    Integer.parseInt(lastrunPayroll.get("pay_run_month").toString()),
                    Long.parseLong(map.get("employee_id").toString()));

            /**
             * Section 17 Calculation
             *
             */
            List<LinkedCaseInsensitiveMap> Sec17 = this.sec17Calculation(YTDAllowanceSum, investment, map);

            /**
             * Section 10 Calculation
             *
             */
            double grossSalary = 0.0;

            grossSalary = Double.parseDouble(lastrunPayroll.get("salary").toString());
            List<LinkedCaseInsensitiveMap> sec10 = this.section10(investment, incometax, map, request, grossSalary);

            /**
             * Section 16 Calculation
             *
             */
            List<LinkedCaseInsensitiveMap> sec16 = this.section16(map);

            /**
             * Section 192 Calculation
             *
             */
            List<LinkedCaseInsensitiveMap> sec192 = this.section192(incometax);

            /**
             * chapterVIA Calculation
             *
             */
            List<LinkedCaseInsensitiveMap> chapterVIA = this.sectionchapterVIA(incometax, investment,
                    investmentOfEmployee);

            int years = Integer.parseInt(map.get("year").toString());
            Long emp_id = Long.parseLong(map.get("employee_id").toString());
            Long orgId = Long.parseLong(map.get("organization_id").toString());

            double[] taxDeductedTillDate = new double[1];
            taxDeductedTillDate[0] = 0.0;

            String taxSlabType = (investment != null && investment.get("tax_slab_tpye") != null)
                    ? investment.get("tax_slab_tpye").toString()
                    : "NewTaxSlabKey";
            System.out.println(taxSlabType + " 1098");
            incometax.stream().forEach(action -> {

                if (action.getTax_name() != null
                        && (action.getTax_name().equalsIgnoreCase("Total Tax Deducted Till Date")
                                || action.getTax_name().equalsIgnoreCase("Tax Deduction for this month"))) {
                    taxDeductedTillDate[0] = taxDeductedTillDate[0]
                            + Double.parseDouble(action.getTax_amount().toString());
                }

            });

            Map form16DetailsFromTimesheet = this.FetchFrom16DetailsFromTimesheet(request, map);

            System.out.println("form16DetailsFromTimesheet");
            System.out.println(form16DetailsFromTimesheet);
            LinkedHashMap empData = new LinkedHashMap();
            if (form16DetailsFromTimesheet != null && form16DetailsFromTimesheet.containsKey("status")
                    && form16DetailsFromTimesheet.get("status").equals("success")) {

                if (form16DetailsFromTimesheet.get("empdetails") != null) {

                    empData = (LinkedHashMap) form16DetailsFromTimesheet.get("empdetails");

                }

            }

            Map form16 = this.Form16Calculation(Sec17, sec10, sec16, sec192, chapterVIA, years, taxDeductedTillDate[0],
                    taxSlabType, emp_id, empData, orgId);
            // System.out.println(form16);
            response.put("status", "success");
            response.put("data", form16);
        } catch (Exception e) {

            e.printStackTrace();
            response.put("status", "exception");
            response.put("msg", "something went wrong");
        }

        return response;
    }

    public List<LinkedCaseInsensitiveMap> sec17Calculation(double YTDAllowanceSum, LinkedCaseInsensitiveMap investment,
            Map map) {

        List<LinkedCaseInsensitiveMap> sec17 = new ArrayList<>();

        LinkedCaseInsensitiveMap sec17data = new LinkedCaseInsensitiveMap<>();

        sec17data.put("key", "sec17(1)");
        sec17data.put("amount1", Math.round(YTDAllowanceSum));
        sec17data.put("amount2", "");

        sec17.add(sec17data);

        LinkedCaseInsensitiveMap sec17data1 = new LinkedCaseInsensitiveMap<>();

        LinkedCaseInsensitiveMap getPerks = perksRepo
                .getTotalPerquisiteAmount(Long.parseLong(map.get("employee_id").toString()));

        sec17data1.put("key", "sec17(2)");
        sec17data1.put("amount1",
                getPerks != null && getPerks.get("total_perquisite_amount") != null
                        ? Math.round(Double.parseDouble(getPerks.get("total_perquisite_amount").toString()))
                        : 0);
        sec17data1.put("amount2", "");

        sec17.add(sec17data1);

        LinkedCaseInsensitiveMap sec17data2 = new LinkedCaseInsensitiveMap<>();

        sec17data2.put("key", "sec17(3)");
        sec17data2.put("amount1", 0);
        sec17data2.put("amount2", "");

        sec17.add(sec17data2);

        LinkedCaseInsensitiveMap sec17data3 = new LinkedCaseInsensitiveMap<>();

        sec17data3.put("key", "Total");
        sec17data3.put("amount1", "");
        sec17data3.put("amount2", 0);

        sec17.add(sec17data3);

        LinkedCaseInsensitiveMap sec17data4 = new LinkedCaseInsensitiveMap<>();

        sec17data4.put("key", "reportedTotal");
        sec17data4.put("amount1", "");
        sec17data4.put("amount2",
                investment != null && investment.get("income_from_previous_employer") != null
                        ? Math.round(Double.parseDouble(investment.get("income_from_previous_employer").toString()))
                        : 0);

        sec17.add(sec17data4);

        return sec17;
    }

    public List<LinkedCaseInsensitiveMap> section10(LinkedCaseInsensitiveMap investment, List<IncomeTax> incometax,
            Map map, HttpServletRequest request, double grossSalary) {

        List<LinkedCaseInsensitiveMap> sec10 = new ArrayList<>();

        LinkedCaseInsensitiveMap sec10a = new LinkedCaseInsensitiveMap<>();
        sec10a.put("key", "sec10(5)");
        sec10a.put("amount1", 0);
        sec10a.put("amount2", "");

        sec10.add(sec10a);

        LinkedCaseInsensitiveMap sec10b = new LinkedCaseInsensitiveMap<>();
        sec10b.put("key", "sec10(10)");
        sec10b.put("amount1", 0);
        sec10b.put("amount2", "");

        sec10.add(sec10b);

        LinkedCaseInsensitiveMap sec10c = new LinkedCaseInsensitiveMap<>();
        sec10c.put("key", "sec10(10A)");
        sec10c.put("amount1", 0);
        sec10c.put("amount2", "");

        sec10.add(sec10c);

        LinkedCaseInsensitiveMap sec10d = new LinkedCaseInsensitiveMap<>();
        sec10d.put("key", "sec10(10AA)");

        /**
         * get Form16 Details From time_sheet
         *
         */
        // Double leaveBalance=0.0;
        // Map form16DetailsFromTimesheet= this.FetchFrom16DetailsFromTimesheet(request,
        // map);
        //
        // if (form16DetailsFromTimesheet != null &&
        // form16DetailsFromTimesheet.containsKey("status") &&
        // form16DetailsFromTimesheet.get("status").equals("success")) {
        //
        //
        // if (form16DetailsFromTimesheet.get("leaveEncashment") != null) {
        //
        // LinkedHashMap leaveEncashment = (LinkedHashMap)
        // form16DetailsFromTimesheet.get("leaveEncashment");
        // if(leaveEncashment.get("total_leave_balance") !=null){
        // leaveBalance=Double.parseDouble(leaveEncashment.get("total_leave_balance").toString());
        //
        // DecimalFormat decimalFormat = new DecimalFormat("#.##");
        // leaveBalance = Double.parseDouble(decimalFormat.format(leaveBalance));
        //
        // }
        // }
        //
        // }
        // sec10d.put("amount1", Math.round((grossSalary / 30) * leaveBalance));
        sec10d.put("amount1", 0);
        sec10d.put("amount2", "");

        sec10.add(sec10d);

        LinkedCaseInsensitiveMap sec10e = new LinkedCaseInsensitiveMap<>();
        sec10e.put("key", "sec10(13A)");
        double[] exemptValue = new double[1];
        exemptValue[0] = 0.0;

        Double getHra = fnfdeductionrepo.getHraAmount(Long.parseLong(map.get("employee_id").toString()));
        if (getHra != null) {
            if (getHra > 0) {
                exemptValue[0] = getHra;
            }
        } else {
            incometax.stream().forEach(action -> {

                if (action.getSalary_hra_name().equalsIgnoreCase("Least of above is exempt")) {
                    exemptValue[0] = action.getSalary_hra_amount() != null
                            ? Double.parseDouble(action.getSalary_hra_amount())
                            : 0;
                }

            });
        }
        sec10e.put("amount1", Math.round(exemptValue[0]));
        sec10e.put("amount2", "");

        sec10.add(sec10e);

        LinkedCaseInsensitiveMap sec10f = new LinkedCaseInsensitiveMap<>();
        sec10f.put("key", "amountOfAny");
        sec10f.put("amount1", 0);
        sec10f.put("amount2", "");

        sec10.add(sec10f);

        LinkedCaseInsensitiveMap sec10g = new LinkedCaseInsensitiveMap<>();
        sec10g.put("key", "totalAmountOfAny");
        sec10g.put("amount1", 0);
        sec10g.put("amount2", "");

        sec10.add(sec10g);

        LinkedCaseInsensitiveMap sec10h = new LinkedCaseInsensitiveMap<>();
        sec10h.put("key", "totalAmountOfExemption");
        sec10h.put("amount1", "");
        sec10h.put("amount2", 0);

        sec10.add(sec10h);

        return sec10;

    }

    public List<LinkedCaseInsensitiveMap> section16(Map map) {

        List<LinkedCaseInsensitiveMap> sec16 = new ArrayList<>();

        LinkedCaseInsensitiveMap sec16a = new LinkedCaseInsensitiveMap<>();
        sec16a.put("key", "sec16(ia)");

        sec16a.put("amount1", 50000);
        sec16a.put("amount2", "");
        sec16.add(sec16a);

        LinkedCaseInsensitiveMap sec16b = new LinkedCaseInsensitiveMap<>();
        sec16b.put("key", "sec16(ii)");
        sec16b.put("amount1", 0);
        sec16b.put("amount2", "");
        sec16.add(sec16b);

        LinkedCaseInsensitiveMap sec16c = new LinkedCaseInsensitiveMap<>();
        sec16c.put("key", "sec16(iii)");

        double pt = 0.0;
        LinkedCaseInsensitiveMap professionalTax = deductionRepo.getProfessionalTacForForm16(
                Long.parseLong(map.get("employee_id").toString()), Integer.parseInt(map.get("year").toString()),
                Integer.parseInt(map.get("year").toString()) + 1);

        if (professionalTax != null && professionalTax.get("deduction_payable_amount") != null) {
            pt = Math.round(Double.parseDouble(professionalTax.get("deduction_payable_amount").toString()));
        }
        Double getpt = fnfdeductionrepo.getPT(Long.parseLong(map.get("employee_id").toString()));
        if (getpt != null) {
            pt = pt + getpt;
        }

        sec16c.put("amount1", Math.round(pt));
        sec16c.put("amount2", "");
        sec16.add(sec16c);

        return sec16;

    }

    public List<LinkedCaseInsensitiveMap> section192(List<IncomeTax> incometax) {

        List<LinkedCaseInsensitiveMap> sec192 = new ArrayList<>();

        LinkedCaseInsensitiveMap sec16a = new LinkedCaseInsensitiveMap<>();
        sec16a.put("key", "incomeAdmissible");

        double[] value = new double[1];
        value[0] = 0.0;
        incometax.stream().forEach(action -> {

            if (action.getExemption_name() != null
                    && action.getExemption_name().equalsIgnoreCase("Income / Loss From House Property")) {
                value[0] = action.getExemption_exempted_amount() != null
                        ? Double.parseDouble(action.getExemption_exempted_amount())
                        : 0;
            }

        });

        sec16a.put("amount1", -Math.round(value[0]));
        sec16a.put("amount2", "");
        sec192.add(sec16a);

        LinkedCaseInsensitiveMap sec16b = new LinkedCaseInsensitiveMap<>();
        sec16b.put("key", "incomeUnder");
        sec16b.put("amount1", 0);
        sec16b.put("amount2", "");
        sec192.add(sec16b);

        return sec192;

    }

    public List<LinkedCaseInsensitiveMap> sectionchapterVIA(List<IncomeTax> incometax,
            LinkedCaseInsensitiveMap investment, LinkedCaseInsensitiveMap investmentOfEmployee) {

        List<LinkedCaseInsensitiveMap> chapterVIA = new ArrayList<>();

        LinkedCaseInsensitiveMap chaptera = new LinkedCaseInsensitiveMap<>();
        chaptera.put("key", "sec80c");
        chaptera.put("grossAmount",
                investment != null && investment.get("total_allowances") != null
                        ? Math.round(Double.parseDouble(investment.get("total_allowances").toString()))
                        : 0);

        double[] excepted = new double[1];
        excepted[0] = 0.0;
        incometax.stream().forEach(action -> {

            if (action.getExemption_name() != null
                    && action.getExemption_name().equalsIgnoreCase("Exemption u/s VI A")) {
                excepted[0] = action.getExemption_exempted_amount() != null
                        ? Double.parseDouble(action.getExemption_exempted_amount())
                        : 0;
            }

        });

        chaptera.put("qualifyingAmount", "");
        chaptera.put("deductibleAmount", Math.round(excepted[0]));
        chapterVIA.add(chaptera);

        LinkedCaseInsensitiveMap chapterb = new LinkedCaseInsensitiveMap<>();
        chapterb.put("key", "sec80ccc");
        chapterb.put("grossAmount",
                investmentOfEmployee != null && investmentOfEmployee.get("national_pension_scheme") != null
                        ? Math.round(Double.parseDouble(investmentOfEmployee.get("national_pension_scheme").toString()))
                        : 0);
        chaptera.put("qualifyingAmount", "");
        long national_pension_scheme = investment != null && investment.get("national_pension_scheme") != null
                ? Math.round(Double.parseDouble(investment.get("national_pension_scheme").toString()))
                : Math.round(0.0);
        national_pension_scheme = national_pension_scheme > 50000 ? 50000 : national_pension_scheme;

        chapterb.put("deductibleAmount", national_pension_scheme);
        chapterVIA.add(chapterb);

        LinkedCaseInsensitiveMap chapterc = new LinkedCaseInsensitiveMap<>();
        chapterc.put("key", "80CCD(1)");
        chapterc.put("grossAmount", 0);
        chaptera.put("qualifyingAmount", "");
        chapterc.put("deductibleAmount", 0);
        chapterVIA.add(chapterc);

        LinkedCaseInsensitiveMap chapterd = new LinkedCaseInsensitiveMap<>();
        chapterd.put("key", "TotalDeductionUnder");
        chapterd.put("grossAmount", 0);
        chaptera.put("qualifyingAmount", "");
        chapterd.put("deductibleAmount", 0);
        chapterVIA.add(chapterd);

        LinkedCaseInsensitiveMap chaptere = new LinkedCaseInsensitiveMap<>();
        chaptere.put("key", "80CCD(1B)");
        chaptere.put("grossAmount", 0);
        chaptera.put("qualifyingAmount", "");
        chaptere.put("deductibleAmount", 0);
        chapterVIA.add(chaptere);

        LinkedCaseInsensitiveMap chapterf = new LinkedCaseInsensitiveMap<>();
        chapterf.put("key", "80CCD(2)");
        chapterf.put("grossAmount", 0);
        chaptera.put("qualifyingAmount", "");
        chapterf.put("deductibleAmount", 0);
        chapterVIA.add(chapterf);

        LinkedCaseInsensitiveMap chapterg = new LinkedCaseInsensitiveMap<>();
        chapterg.put("key", "80D");
        long sec80d = investmentOfEmployee != null && investmentOfEmployee.get("sec80d") != null
                ? Math.round(Double.parseDouble(investmentOfEmployee.get("sec80d").toString()))
                : 0;
        long sec80dd = investmentOfEmployee != null && investmentOfEmployee.get("sec80dd") != null
                ? Math.round(Double.parseDouble(investmentOfEmployee.get("sec80dd").toString()))
                : 0;

        chapterg.put("grossAmount", sec80d + sec80dd);
        chaptera.put("qualifyingAmount", "");
        chapterg.put("deductibleAmount", 0);

        if (sec80dd >= 75000) {
            sec80dd = 75000;
        }

        if (investmentOfEmployee != null && investmentOfEmployee.get("sec80d_type") != null) {
            if (investmentOfEmployee.get("sec80d_type").toString().equalsIgnoreCase("parent")) {
                sec80d = sec80d > 50000 ? 50000 : sec80d;
            } else if (investmentOfEmployee.get("sec80d_type").toString().equalsIgnoreCase("self&family")) {
                sec80d = sec80d > 25000 ? 25000 : sec80d;
            }
            chapterg.put("deductibleAmount", sec80d + sec80dd);
        }
        chapterVIA.add(chapterg);

        LinkedCaseInsensitiveMap chapterh = new LinkedCaseInsensitiveMap<>();
        chapterh.put("key", "80E");
        chapterh.put("grossAmount",
                investmentOfEmployee != null && investmentOfEmployee.get("sec80e") != null
                        ? Math.round(Double.parseDouble(investmentOfEmployee.get("sec80e").toString()))
                        : 0);
        chaptera.put("qualifyingAmount", "");
        chapterh.put("deductibleAmount",
                investment != null && investment.get("sec80e") != null
                        ? Math.round(Double.parseDouble(investment.get("sec80e").toString()))
                        : 0);
        chapterVIA.add(chapterh);

        LinkedCaseInsensitiveMap chapteri = new LinkedCaseInsensitiveMap<>();
        chapteri.put("key", "80G");
        chapteri.put("grossAmount",
                investmentOfEmployee != null && investmentOfEmployee.get("sec80g") != null
                        ? Math.round(Double.parseDouble(investmentOfEmployee.get("sec80g").toString()))
                        : 0);
        chapteri.put("qualifyingAmount",
                investment != null && investment.get("sec80g") != null
                        ? Math.round(Double.parseDouble(investment.get("sec80g").toString()))
                        : 0);
        chapteri.put("deductibleAmount",
                investment != null && investment.get("sec80g") != null
                        ? Math.round(Double.parseDouble(investment.get("sec80g").toString()))
                        : 0);

        chapterVIA.add(chapteri);

        LinkedCaseInsensitiveMap chapterj = new LinkedCaseInsensitiveMap<>();
        chapterj.put("key", "80TTA");
        chapterj.put("grossAmount", 0);
        chapterj.put("qualifyingAmount", 0);
        chapterj.put("deductibleAmount", 0);
        chapterVIA.add(chapterj);

        // LinkedCaseInsensitiveMap chapterk = new LinkedCaseInsensitiveMap<>();
        // chapterk.put("key", "amountDeductibleUnder");
        // chapterk.put("grossAmount", "");
        // chapterk.put("qualifyingAmount", "");
        // chapterk.put("deductibleAmount", "");
        // chapterVIA.add(chapterk);
        LinkedCaseInsensitiveMap chapterl = new LinkedCaseInsensitiveMap<>();
        chapterl.put("key", "totalOfAmountDeductible");
        chapterl.put("grossAmount", 0);
        chapterl.put("qualifyingAmount", 0);
        chapterl.put("deductibleAmount", 0);
        chapterVIA.add(chapterl);

        return chapterVIA;

    }

    public Map Form16Calculation(List<LinkedCaseInsensitiveMap> Sec17, List<LinkedCaseInsensitiveMap> sec10,
            List<LinkedCaseInsensitiveMap> sec16, List<LinkedCaseInsensitiveMap> sec192,
            List<LinkedCaseInsensitiveMap> chapterVIA, int year, double taxDeductedTillDate, String taxSlabType,
            Long emp_id, LinkedHashMap empData, Long orgId) {

        Map response = new HashMap<>();
        double[] sum = new double[1];
        sum[0] = 0.0;

        double[] Value1e = new double[1];
        Value1e[0] = 0.0;
        Sec17.stream().forEach(sec17 -> {
            if (sec17.get("key").equals("sec17(1)") || sec17.get("key").equals("sec17(2)")
                    || sec17.get("key").equals("sec17(3)")) {
                sum[0] = sum[0] + Double.parseDouble(sec17.get("amount1").toString());
            }
            if (sec17.get("key").equals("reportedTotal")) {
                Value1e[0] = Value1e[0] + Double.parseDouble(sec17.get("amount2").toString());
            }

        });

        Sec17.stream().forEach(s -> {
            if (s.get("key").equals("Total")) {
                s.put("amount2", Math.round(sum[0]));
            }

        });

        double[] sumofsec10 = new double[1];
        sumofsec10[0] = 0.0;
        sec10.stream().forEach(sec -> {
            if (sec.get("key").equals("sec10(5)") || sec.get("key").equals("sec10(10)")
                    || sec.get("key").equals("sec10(10A)")
                    || sec.get("key").equals("sec10(10AA)") || sec.get("key").equals("sec10(13A)")
                    || sec.get("key").equals("totalAmountOfAny")) {
                sumofsec10[0] = sumofsec10[0] + Double.parseDouble(sec.get("amount1").toString());
            }

        });

        sec10.stream().forEach(s -> {
            if (s.get("key").equals("totalAmountOfExemption")) {
                s.put("amount2", Math.round(sumofsec10[0]));
            }

        });

        LinkedCaseInsensitiveMap Part3 = new LinkedCaseInsensitiveMap();
        Part3.put("key", "totalAmountOfSalary");
        Part3.put("amount", Math.round(sum[0] - sumofsec10[0]));

        double[] sumofsec16 = new double[1];
        sumofsec16[0] = 0.0;
        sec16.stream().forEach(sec -> {
            if (sec.get("key").equals("sec16(ia)") || sec.get("key").equals("sec16(ii)")
                    || sec.get("key").equals("sec16(iii)")) {
                sumofsec16[0] = sumofsec16[0] + Double.parseDouble(sec.get("amount1").toString());
            }

        });

        LinkedCaseInsensitiveMap Part5 = new LinkedCaseInsensitiveMap();
        Part5.put("key", "totalAmountOfDeductions");
        Part5.put("amount", Math.round(sumofsec16[0]));

        double[] value6 = new double[1];
        value6[0] = Math.round(sum[0] - sumofsec10[0] + Value1e[0] - sumofsec16[0]);

        LinkedCaseInsensitiveMap Part6 = new LinkedCaseInsensitiveMap();
        Part6.put("key", "incomeChargeable");
        Part6.put("amount", Math.round(value6[0]));

        double[] sumofsec192 = new double[1];
        sumofsec192[0] = 0.0;
        sec192.stream().forEach(sec -> {
            if (sec.get("key").equals("incomeAdmissible") || sec.get("key").equals("incomeUnder")) {
                sumofsec192[0] = sumofsec192[0] + Double.parseDouble(sec.get("amount1").toString());
            }

        });

        LinkedCaseInsensitiveMap Part8 = new LinkedCaseInsensitiveMap();
        Part8.put("key", "total");
        Part8.put("amount", Math.round(sumofsec192[0]));

        double[] value9 = new double[1];
        value9[0] = Math.round(value6[0] + sumofsec192[0]);

        LinkedCaseInsensitiveMap Part9 = new LinkedCaseInsensitiveMap();
        Part9.put("key", "grossTotal");
        Part9.put("amount", Math.round(value9[0]));

        double[] sumofchapterGross = new double[1];
        sumofchapterGross[0] = 0.0;

        double[] sumofchapterDeductible = new double[1];
        sumofchapterDeductible[0] = 0.0;
        chapterVIA.stream().forEach(sec -> {
            if (sec.get("key").equals("sec80c") || sec.get("key").equals("sec80ccc")
                    || sec.get("key").equals("80CCD(1)")) {
                sumofchapterGross[0] = sumofchapterGross[0] + Double.parseDouble(sec.get("grossAmount").toString());
                sumofchapterDeductible[0] = sumofchapterDeductible[0]
                        + Double.parseDouble(sec.get("deductibleAmount").toString());
            }

        });

        chapterVIA.stream().forEach(s -> {
            if (s.get("key").equals("TotalDeductionUnder")) {
                s.put("grossAmount", Math.round(sumofchapterGross[0]));
                s.put("deductibleAmount", Math.round(sumofchapterDeductible[0]));
            }

        });

        double[] sumofEtoI = new double[1];
        sumofEtoI[0] = 0.0;
        chapterVIA.stream().forEach(sec -> {
            if (sec.get("key").equals("80CCD(1B)") || sec.get("key").equals("80CCD(2)") || sec.get("key").equals("80D")
                    || sec.get("key").equals("80E") || sec.get("key").equals("80G") || sec.get("key").equals("80TTA")
                    || sec.get("key").equals("totalOfAmountDeductible")) {
                sumofEtoI[0] = sumofEtoI[0] + Double.parseDouble(sec.get("deductibleAmount").toString());
            }

        });

        double[] part11 = new double[1];
        part11[0] = sumofchapterDeductible[0] + sumofEtoI[0];

        double[] part12 = new double[1];
        part12[0] = value9[0] - part11[0];

        double txs = 0.0;
        Double gettx = fnfdeductionrepo.getIncomeTax(emp_id);
        if (gettx != null) {
            txs = txs + gettx;
        }

        List<LinkedCaseInsensitiveMap> tax = this.taxCalculationForForm16(part11[0], part12[0], year,
                taxDeductedTillDate, taxSlabType, txs);

        LinkedCaseInsensitiveMap OrgDetails = orgsetuprepo.getOrganizationDetails(orgId);
        LinkedCaseInsensitiveMap authorizatoryDetails = authoRepo.getAuthorizatoryDetails(orgId);

        response.put("Sec17", Sec17);
        response.put("sec10", sec10);
        response.put("sec16", sec16);
        response.put("sec192", sec192);
        response.put("chapterVIA", chapterVIA);
        response.put("year", year);
        response.put("taxDeductedTillDate", taxDeductedTillDate);
        response.put("heading3", Part3);
        response.put("heading5", Part5);
        response.put("heading6", Part6);
        response.put("heading8", Part8);
        response.put("heading9", Part9);
        response.put("taxData", tax);
        response.put("taxSlabType", taxSlabType);
        response.put("employeeDetails", empData);
        response.put("orgDetails", OrgDetails);
        response.put("authorizatoryDetails", authorizatoryDetails);
        response.put("employee_id", emp_id);
        response.put("organization_id", orgId);

        LinkedCaseInsensitiveMap pan = new LinkedCaseInsensitiveMap();

        pan.put("deductorPan",
                OrgDetails != null && OrgDetails.get("organization_pan_no") != null
                        ? OrgDetails.get("organization_pan_no").toString()
                        : "");
        pan.put("deductorTan",
                OrgDetails != null && OrgDetails.get("organization_tan_no") != null
                        ? OrgDetails.get("organization_tan_no").toString()
                        : "");
        pan.put("employeePan", empData != null && empData.get("pan_number") != null ? empData.get("pan_number") : "");

        response.put("pan", pan);

        String citdetails = "The Commissioner of Income Tax(TDS) Aayakar Bhawan, District Centre";
        String city = OrgDetails != null && OrgDetails.get("org_city") != null ? OrgDetails.get("org_city").toString()
                : "";
        String state = OrgDetails != null && OrgDetails.get("org_state") != null
                ? OrgDetails.get("org_state").toString()
                : "";

        LinkedCaseInsensitiveMap cit = new LinkedCaseInsensitiveMap();

        cit.put("tds", citdetails + ", " + city + ", " + state);
        cit.put("assessmentYear", (year + 1) + "-" + (year + 2));
        cit.put("from", "01-Apr-" + year);
        cit.put("to", "31-Mar-" + (year + 1));

        response.put("cit", cit);

        System.out.println("response 1402 " + response.toString());

        return response;

    }

    public List<LinkedCaseInsensitiveMap> taxCalculationForForm16(double part11, double part12, int year,
            double taxDeductedTillDate, String taxSlabType, double txs) {

        List<LinkedCaseInsensitiveMap> tax = new ArrayList<>();

        LinkedCaseInsensitiveMap aggregateOfDeductible = new LinkedCaseInsensitiveMap();
        aggregateOfDeductible.put("key", "aggregateOfDeductible");
        aggregateOfDeductible.put("amount", Math.round(part11));
        tax.add(aggregateOfDeductible);

        LinkedCaseInsensitiveMap totalTaxableIncome = new LinkedCaseInsensitiveMap();
        totalTaxableIncome.put("key", "totalTaxableIncome");
        totalTaxableIncome.put("amount", Math.round(part12));
        tax.add(totalTaxableIncome);

        Map value = taxserviceimple.calucluteCommonTax(part12, taxSlabType, year);
        if (value.get("status").equals("success")) {

            double taxLiability = Double.parseDouble(value.get("taxLiability").toString());
            double surcharge = Double.parseDouble(value.get("surcharge").toString());
            double educationCess = Double.parseDouble(value.get("educationCess").toString());
            double rebate = Double.parseDouble(value.get("rebate").toString());
            System.out.println(rebate);
            LinkedCaseInsensitiveMap taxontotalincome = new LinkedCaseInsensitiveMap();
            taxontotalincome.put("key", "taxOnTotalIncome");
            taxontotalincome.put("amount", Math.round(taxLiability));
            tax.add(taxontotalincome);
            if (taxLiability <= 0) {
                rebate = 0.0;
            }
            if (taxSlabType.equalsIgnoreCase("NewTaxSlabKey")) {
                if (taxLiability <= 25000) {
                    rebate = taxLiability;
                } else {
                    rebate = 0.0;
                }
            } else {

                if (taxLiability <= 12500) {
                    rebate = taxLiability;
                } else {
                    rebate = 0.0;
                }
            }

            LinkedCaseInsensitiveMap rebatekey = new LinkedCaseInsensitiveMap();
            rebatekey.put("key", "rebate");
            rebatekey.put("amount", taxLiability > 0 ? Math.round(rebate) : 0);
            tax.add(rebatekey);

            LinkedCaseInsensitiveMap surchargekey = new LinkedCaseInsensitiveMap();
            surchargekey.put("key", "surcharge");
            surchargekey.put("amount", taxLiability > 0 ? Math.round(surcharge) : 0);
            tax.add(surchargekey);

            LinkedCaseInsensitiveMap educationCessKey = new LinkedCaseInsensitiveMap();
            educationCessKey.put("key", "educationCess");
            educationCessKey.put("amount", taxLiability > 0 ? Math.round(educationCess) : 0);
            tax.add(educationCessKey);

            LinkedCaseInsensitiveMap taxPayableKey = new LinkedCaseInsensitiveMap();
            double taxpayable = taxLiability + surcharge + educationCess - rebate;
            taxPayableKey.put("key", "taxPayable");
            taxPayableKey.put("amount", Math.round(taxpayable));
            tax.add(taxPayableKey);

            LinkedCaseInsensitiveMap lessReliefKey = new LinkedCaseInsensitiveMap();
            lessReliefKey.put("key", "lessRelief");
            lessReliefKey.put("amount", Math.round(taxDeductedTillDate + txs));
            tax.add(lessReliefKey);

            LinkedCaseInsensitiveMap netTaxPayableKey = new LinkedCaseInsensitiveMap();
            netTaxPayableKey.put("key", "netTaxPayable");
            netTaxPayableKey.put("amount", Math.round(taxpayable - (taxDeductedTillDate + txs)));
            tax.add(netTaxPayableKey);
        }
        return tax;
    }

    public Map FetchFrom16DetailsFromTimesheet(HttpServletRequest request, Map map) {

        String bearerToken = authenticationFilter.getJwtFromRequest(request);

        HttpHeaders header = new HttpHeaders();
        header.setBearerAuth(bearerToken);
        header.setContentType(MediaType.TEXT_PLAIN);

        JSONObject payload = new JSONObject();

        payload.put("employee_id", map.get("employee_id").toString());

        logger.info("Payload for Manage api call for Fetch From16 Details FromTimesheet :: ", payload);

        String encryptedPayload = EncryptDecryptUtils.encrypt(payload.toString());

        Map employeeListResp = null;

        HttpEntity<?> entity = new HttpEntity<>(encryptedPayload, header);

        logger.info("entity ", entity.toString());

        Map employeeListReq = restTemplate.exchange(reimburshment_url + "/payrollleavecount/getform16details",
                HttpMethod.POST, entity, HashMap.class).getBody();

        try {
            employeeListResp = mapper.readValue(EncryptDecryptUtils.decrypt(employeeListReq.get("data").toString()),
                    LinkedCaseInsensitiveMap.class);

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Unable to Fetch From16 Details From manage :: ", ex);
        }

        logger.info("From16 Details response from Timesheet ", employeeListResp);

        return employeeListResp;

    }

    @Override
    public Map updateForm16(Map map, HttpServletRequest request) {

        Map response = new HashMap<>();

        try {

            System.out.println("map >>>>>>>>> " + map);

            List<LinkedCaseInsensitiveMap> Sec17 = mapper.convertValue(map.get("Sec17"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            List<LinkedCaseInsensitiveMap> sec10 = mapper.convertValue(map.get("sec10"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            List<LinkedCaseInsensitiveMap> sec16 = mapper.convertValue(map.get("sec16"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            List<LinkedCaseInsensitiveMap> sec192 = mapper.convertValue(map.get("sec192"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            List<LinkedCaseInsensitiveMap> chapterVIA = mapper.convertValue(map.get("chapterVIA"),
                    new TypeReference<List<LinkedCaseInsensitiveMap>>() {
                    });

            int year = Integer.parseInt(map.get("year").toString());
            double taxDeductedTillDate = Double.parseDouble(map.get("taxDeductedTillDate").toString());
            String taxSlabType = map.get("taxSlabType").toString();
            Long emp_id = Long.parseLong(map.get("employee_id").toString());
            Long orgId = Long.parseLong(map.get("organization_id").toString());

            LinkedHashMap empData = (LinkedHashMap) map.get("employeeDetails");

            Map form16 = this.Form16Calculation(Sec17, sec10, sec16, sec192, chapterVIA, year, taxDeductedTillDate,
                    taxSlabType, emp_id, empData, orgId);

            response.put("status", "success");
            response.put("data", form16);
        } catch (Exception ex) {

            ex.printStackTrace();

            response.put("status", "exception");
            response.put("msg", "something went wrong");
            logger.info("Problem in EmployeeAllowanceServiceImpl -> updateForm16() :: ", ex);

        }

        return response;
    }

    @Override
    public Map uploadForm16Document(MultipartFile file, String fileName, Long employeeId, Long organizationId,
            String financialYear) {

        Map resultMap = new HashMap<>();

        try {

            Optional<Form16Document> otherObj = form16docRepo.findFrom16Document(employeeId,
                    Long.parseLong(financialYear));
            if (otherObj.isPresent()) {
                Form16Document obj = otherObj.get();
                obj.setFileUrl(this.uploadFileOnGCP(file, financialYear, employeeId));
                obj.setFileName(fileName);
                form16docRepo.save(obj);
                resultMap.put("msg", "file updated successfully");
                resultMap.put("status", "success");

            } else {
                Form16Document obj = new Form16Document();
                obj.setOrganizationId(organizationId);
                obj.setEmployeeId(employeeId);
                obj.setFinancialYear(financialYear);
                obj.setFileName(fileName);
                obj.setFileUrl(this.uploadFileOnGCP(file, financialYear, employeeId));
                form16docRepo.save(obj);
                resultMap.put("msg", "file uploaded successfully");
                resultMap.put("status", "success");

            }

        } catch (Exception ex) {

            ex.printStackTrace();

            resultMap.put("status", "exception");
            resultMap.put("msg", "something went wrong");
            logger.info("Problem in EmployeeAllowanceServiceImpl -> uploadForm16Document() :: ", ex);

        }

        return resultMap;

    }

    public String uploadFileOnGCP(MultipartFile fileStream, String financeYear, Long employeeId) {

        try {

            String fileObject = "" + fileStream.getOriginalFilename();
            byte[] resfile = fileStream.getBytes();
            BlobId blobId = BlobId.of(bucketName, ("Form/" + financeYear + "/" + employeeId + "/" + fileObject));
            if (fileObject.contains(".pdf")) {
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(fileStream.getContentType())
                        .setCacheControl("Cache-Control: max-age=0, no-cache").build();
                storages.create(blobInfo, resfile);
            } else {
                BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCacheControl("Cache-Control: max-age=0, no-cache")
                        .build();
                storages.create(blobInfo, resfile);
            }

            String path = gcpFilePath + bucketName + "/" + "Form/" + financeYear + "/" + employeeId + "/" + fileObject;
            return path;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Map viewForm16Document(Map map) {

        Map resultMap = new HashMap<>();
        try {

            String docUrl = form16docRepo.findUrl(Long.parseLong(map.get("employee_id").toString()),
                    Long.parseLong(map.get("financial_year").toString()));
            if (docUrl != null) {
                String url = gcpConfig.getSignedUrl(bucketName, docUrl);
                resultMap.put("url", url);
                resultMap.put("status", "success");
            } else {
                resultMap.put("msg", "Document not uploaded");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
            ex.printStackTrace();
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

                    // System.out.println(cell.getStringCellValue().toLowerCase().trim());
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

                        case "updatetds":
                            getIndexOfEachColumn.put("UpdateTdsIndex", cell.getColumnIndex());
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
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("UpdateTdsIndex")) {
                        mp.put("updatetds", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("SIndex")) {
                        mp.put("status", printCellValue(cell));
                    }

                });

                empData.add(mp);

            });

            /**
             * Removing employee from empData List whose status is no
             *
             */
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

            // System.out.println("empData 1958"+" "+empData.toString());
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
            List<LinkedCaseInsensitiveMap> empid = employeedetailsrepo.getEmployeeId(empcodeList, orgId);

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

            List<Allowance> allowanceData = allowanceRepository.approvedAllowance(orgId);
            List<Deduction> deductionData = deductionRepository.approvedDeduction(orgId);

            List<TempararyAllowance> tempAllowance = tempararyAllowanceRepository.getTempararyAllowanceMonthWise(orgId,
                    month[0], year[0]);
            List<TempararyDeduction> tempDeduction = tempararyDeductionRepository.getTempararyDeductionMonthWise(orgId,
                    month[0], year[0]);

            empData.forEach(action -> {
                Long empId = Long.parseLong(action.get("employee_id").toString());
                String employeeType = action.get("employee type").toString();

                /**
                 * For Allowance
                 *
                 */
                allowanceData.stream()
                        .filter(a -> a.getEmployee_type().equals(employeeType)) // Filter by employee type
                        .forEach(a -> {
                            // Handle only specific allowances: Reimbursement, arrears, bonus/incentive,
                            // overtime
                            String allowanceName = a.getAllowance_name().toLowerCase();
                            if (allowanceName.equals("reimbursement")
                                    || allowanceName.equals("arrears")
                                    || allowanceName.equals("bonus/incentive")
                                    || allowanceName.equals("overtime")
                                    || allowanceName.equals("overtime allowance")
                                    || allowanceName.equals("overtime allowances")) {

                                // Check if an allowance record already exists for this employee, allowance
                                // type, month, year, and employee type
                                Optional<TempararyAllowance> existingAllowanceOpt = tempAllowance.stream()
                                        .filter(t -> t.getEmployeeId().equals(empId)
                                                && t.getName().equalsIgnoreCase(a.getAllowance_name())
                                                && t.getMonth().equals(month[0])
                                                && t.getYear().equals(year[0]))
                                        .findFirst();

                                // Create or update allowance based on whether it exists
                                TempararyAllowance temAllowance;
                                if (existingAllowanceOpt.isPresent()) {
                                    temAllowance = existingAllowanceOpt.get();
                                } else {
                                    temAllowance = new TempararyAllowance();
                                    temAllowance.setAllowanceId(a.getAllowance_id());
                                    temAllowance.setEmployeeId(empId);
                                    temAllowance.setMonth(month[0]);
                                    temAllowance.setName(a.getAllowance_name());
                                    temAllowance.setOrganizationId(orgId);
                                    temAllowance.setYear(year[0]);
                                    tempAllowance.add(temAllowance); // Add new allowance to the list
                                }

                                // Update the amount based on the allowance name
                                if (allowanceName.equals("reimbursement")) {
                                    temAllowance.setAmount(Double.parseDouble(action.get("Reimbursement").toString()));
                                } else if (allowanceName.equals("arrears")) {
                                    temAllowance.setAmount(Double.parseDouble(action.get("arrears").toString()));
                                } else if (allowanceName.equals("bonus/incentive")) {
                                    temAllowance
                                            .setAmount(Double.parseDouble(action.get("bonus/incentive").toString()));
                                } else if (allowanceName.equals("overtime")
                                        || allowanceName.equals("overtime allowance")
                                        || allowanceName.equals("overtime allowances")) {
                                    temAllowance.setAmount(Double.parseDouble(action.get("overtime").toString()));
                                }
                            }
                        });

                /**
                 * For Deduction
                 *
                 */
                deductionData.stream().filter(d -> d.getEmployee_type().equals(employeeType))
                        .forEach(d -> {

                            String deductionName = d.getDeduction_name().toLowerCase();
                            if (deductionName.equals("advance")
                                    || deductionName.equals("income tax")
                                    || deductionName.equals("other deductions")) {
                                // Check if an allowance record already exists for this employee, allowance
                                // type, month, year, and employee type
                                Optional<TempararyDeduction> existingDeductionOpt = tempDeduction.stream()
                                        .filter(t -> t.getEmployeeId().equals(empId)
                                                && t.getName().equalsIgnoreCase(d.getDeduction_name())
                                                && t.getMonth().equals(month[0])
                                                && t.getYear().equals(year[0]))
                                        .findFirst();

                                TempararyDeduction temDeduction;
                                if (existingDeductionOpt.isPresent()) {
                                    temDeduction = existingDeductionOpt.get();
                                } else {
                                    temDeduction = new TempararyDeduction();
                                    temDeduction.setDeductionId(d.getDeduction_id());
                                    temDeduction.setEmployeeId(empId);
                                    temDeduction.setMonth(month[0]);
                                    temDeduction.setName(d.getDeduction_name());
                                    temDeduction.setOrganizationId(orgId);
                                    temDeduction.setUpdateTds(action.get("updatetds").toString());
                                    temDeduction.setYear(year[0]);
                                    tempDeduction.add(temDeduction);

                                }

                                // Update the amount based on the deduction name
                                if (deductionName.equals("advance")) {
                                    temDeduction.setAmount(Double.parseDouble(action.get("advance").toString()));
                                } else if (deductionName.equals("income tax")) {
                                    temDeduction.setAmount(Double.parseDouble(action.get("income tax").toString()));
                                } else if (deductionName.equals("other deductions")) {
                                    temDeduction
                                            .setAmount(Double.parseDouble(action.get("other deductions").toString()));
                                }
                            }

                        });

            });

            // System.out.println(tempAllowance);
            // System.out.println(tempDeduction);
            tempararyAllowanceRepository.saveAll(tempAllowance);
            tempararyDeductionRepository.saveAll(tempDeduction);

        } catch (Exception e) {
            e.printStackTrace();
        }

        resultMap.put("status", "success");
        resultMap.put("msg", "file uploaded successfully");

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

    public Map<String, Object> CustomUpdateAllowanceInBulk(MultipartFile file, Long orgId, Long siteId)
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

                    // System.out.println(cell.getStringCellValue().toLowerCase().trim());
                    switch (cell.getStringCellValue().toLowerCase().trim()) {

                        case "employee code":
                            getIndexOfEachColumn.put("e_codeIndex", cell.getColumnIndex());
                            break;
                        case "name":
                            getIndexOfEachColumn.put("nameIndex", cell.getColumnIndex());
                            break;
                        case "employee type":
                            getIndexOfEachColumn.put("emptypeIndex", cell.getColumnIndex());
                            break;

                        case "site":
                            getIndexOfEachColumn.put("siteIndex", cell.getColumnIndex());
                            break;
                        case "epfdays":
                            getIndexOfEachColumn.put("epfdaysIndex", cell.getColumnIndex());
                            break;
                        case "site advance":
                            getIndexOfEachColumn.put("siteadvanceIndex", cell.getColumnIndex());
                            break;
                        case "ho advance":
                            getIndexOfEachColumn.put("hoadvanceIndex", cell.getColumnIndex());
                            break;
                        case "coupan":
                            getIndexOfEachColumn.put("coupanIndex", cell.getColumnIndex());
                            break;
                        // case "gate deduction":
                        // getIndexOfEachColumn.put("gatedeductionIndex", cell.getColumnIndex());
                        // break;

                        case "other deductions":
                            getIndexOfEachColumn.put("ODIndex", cell.getColumnIndex());
                            break;
                        case "addiincentive":
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

                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("emptypeIndex")) {
                        mp.put("employee type", printCellValue(cell));
                    }

                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("siteIndex")) {
                        mp.put("site", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("hoadvanceIndex")) {
                        mp.put("ho advance", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("coupanIndex")) {
                        mp.put("coupan", printCellValue(cell));
                    }
                    // if (cell.getColumnIndex() == getIndexOfEachColumn.get("gatedeductionIndex"))
                    // {
                    // mp.put("gate deduction", printCellValue(cell));
                    // }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("epfdaysIndex")) {
                        mp.put("epfdays", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("siteadvanceIndex")) {
                        mp.put("siteadvance", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("ODIndex")) {
                        mp.put("other deductions", printCellValue(cell));
                    }
                    if (cell.getColumnIndex() == getIndexOfEachColumn.get("BIIndex")) {
                        mp.put("addiincentive", printCellValue(cell));
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
             * Removing employee from empData List whose status is no
             *
             */
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

            // System.out.println("empData 1958"+" "+empData.toString());
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
            List<LinkedCaseInsensitiveMap> empid = employeedetailsrepo.getEmployeeId(empcodeList, orgId);

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

            List<CustomAllowanceAmount> tempAllowance = customAllowanceAmountRepository.getAllowanceMonthWise(orgId,
                    month[0], year[0], siteId);

            empData.forEach(action -> {
                Long empId = Long.parseLong(action.get("employee_id").toString());

                // Create map of allowance types to their values
                Map<String, Double> allowanceMap = new LinkedHashMap<>();
                allowanceMap.put("siteadvance", Double.parseDouble(action.get("siteadvance").toString()));
                allowanceMap.put("otherdeductions", Double.parseDouble(action.get("other deductions").toString()));
                allowanceMap.put("overtime", Double.parseDouble(action.get("overtime").toString()));
                allowanceMap.put("addiIncentive", Double.parseDouble(action.get("addiincentive").toString()));
                allowanceMap.put("epfdays", Double.parseDouble(action.get("epfdays").toString()));
                allowanceMap.put("coupan", Double.parseDouble(action.get("coupan").toString()));
                // allowanceMap.put("gate deduction", Double.parseDouble(action.get("gate
                // deduction").toString()));
                allowanceMap.put("ho advance", Double.parseDouble(action.get("ho advance").toString()));

                allowanceMap.forEach((allowanceName, value) -> {
                    Optional<CustomAllowanceAmount> existingOpt = tempAllowance.stream()
                            .filter(t -> t.getEmployeeId().equals(empId)
                                    && t.getName().equalsIgnoreCase(allowanceName)
                                    && t.getMonth().equals(month[0])
                                    && t.getSiteId().equals(siteId)
                                    && t.getYear().equals(year[0]))
                            .findFirst();

                    CustomAllowanceAmount allowance;
                    if (existingOpt.isPresent()) {
                        allowance = existingOpt.get();
                    } else {
                        allowance = new CustomAllowanceAmount();
                        allowance.setEmployeeId(empId);
                        allowance.setMonth(month[0]);
                        allowance.setName(allowanceName); // use string label as name
                        allowance.setOrganizationId(orgId);
                        allowance.setSiteId(siteId);
                        allowance.setYear(year[0]);
                        tempAllowance.add(allowance);
                    }

                    allowance.setAmount(value);
                });

            });

            customAllowanceAmountRepository.saveAll(tempAllowance);

        } catch (Exception e) {
            e.printStackTrace();
        }

        resultMap.put("status", "success");
        resultMap.put("msg", "file uploaded successfully");

        return resultMap;
    }

}

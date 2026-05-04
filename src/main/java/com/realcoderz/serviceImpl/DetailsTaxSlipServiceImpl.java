/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.common.NumberToWords;
import com.realcoderz.config.GcpConfig;
import com.realcoderz.model.CustomRunPayroll;
import com.realcoderz.model.SalaryBreakUp;
import com.realcoderz.repository.CustomRunPayrollRepository;
import com.realcoderz.repository.EmployeeAllowanceRepository;
import com.realcoderz.repository.EmployeeDeductionRepository;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.repository.RunPayRollRepository;
import com.realcoderz.repository.SalaryBreakuprepo;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.DetailsTaxSlipService;
import com.realcoderz.service.TaxService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.ResponseBody;

import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 *
 * @author tauseef
 */
@Service
public class DetailsTaxSlipServiceImpl implements DetailsTaxSlipService {

    private final TaxService taxService;

    ObjectMapper mapper = new ObjectMapper();

    private final employeeDetailsRepository employeeDetailsRepo;
    private final OrganizationSetUpRepository orgRepo;
    private final GcpConfig gcpConfig;
    private final SalaryBreakupServiceImpl salaryBreakupServiceImpl;
    private final EmployeeAllowanceRepository employeeAllowanceRepo;
    private final EmployeeDeductionRepository employeeDeductionRepo;
    private final SalaryBreakuprepo salaryBreakuprepo;
    private final CustomRunPayrollRepository customRunPayrollRepository;
    private final RunPayRollRepository runPayRepo;

    @Value("${recruitBucketName}")
    private String recruitBucketName;

    public DetailsTaxSlipServiceImpl(TaxService taxService,
                                     employeeDetailsRepository employeeDetailsRepo,
                                     OrganizationSetUpRepository orgRepo,
                                     GcpConfig gcpConfig,
                                     SalaryBreakupServiceImpl salaryBreakupServiceImpl,
                                     EmployeeAllowanceRepository employeeAllowanceRepo,
                                     EmployeeDeductionRepository employeeDeductionRepo,
                                     SalaryBreakuprepo salaryBreakuprepo,
                                     CustomRunPayrollRepository customRunPayrollRepository,
                                     RunPayRollRepository runPayRepo,
                                     @Value("${recruitBucketName}") String recruitBucketName) {
        this.taxService = taxService;
        this.employeeDetailsRepo = employeeDetailsRepo;
        this.orgRepo = orgRepo;
        this.gcpConfig = gcpConfig;
        this.salaryBreakupServiceImpl = salaryBreakupServiceImpl;
        this.employeeAllowanceRepo = employeeAllowanceRepo;
        this.employeeDeductionRepo = employeeDeductionRepo;
        this.salaryBreakuprepo = salaryBreakuprepo;
        this.customRunPayrollRepository = customRunPayrollRepository;
        this.runPayRepo = runPayRepo;
        this.recruitBucketName = recruitBucketName;
    }

    @Override
    public @ResponseBody
    byte[] getDetailSalarySlip(HttpServletRequest request, String data) {

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("map " + " " + map.toString());
            Long employeeId = map.get("emp_id") != null ? Long.parseLong(map.get("emp_id").toString()) : 0;
            int month = map.get("selected_month") != null ? Integer.parseInt(map.get("selected_month").toString()) : 0;
            int year = map.get("selected_year") != null ? Integer.parseInt(map.get("selected_year").toString()) : 0;
            Long orgId = map.get("orgId") != null ? Long.parseLong(map.get("orgId").toString()) : 0;
            LinkedCaseInsensitiveMap workingDayMap = runPayRepo.employeeWorkingDay(employeeId, month, year,orgId);

            LinkedCaseInsensitiveMap employeeType = runPayRepo.employeeTypeOfEmployee(Long.parseLong(map.get("emp_id").toString()), (int) map.get("selected_month"), (int) map.get("selected_year"));
            if (employeeType != null) {
                if (!employeeType.get("employee_type").toString().equalsIgnoreCase("full time")) {
                    byte a[] = new byte[0];
                    return a;
                }
            }

            if (workingDayMap.get("working_day") == null) {

                byte a[] = new byte[0];
                return a;

            } else {

                LinkedCaseInsensitiveMap userDetails = employeeDetailsRepo.getEmployeeDetails(employeeId);
                LinkedCaseInsensitiveMap companyAddressTemplate = orgRepo.getOrganizationAddress(orgId);
                
                Long orgdetails = runPayRepo.getOrgDetailsId(employeeId,
                        orgId,
                        month, year);
            
            System.out.println("orgdetails "+orgdetails);
            
             LinkedCaseInsensitiveMap orgDetailsforEmp=new LinkedCaseInsensitiveMap();
            
            if(orgdetails != null){
             orgDetailsforEmp = orgRepo.getOrganizationAddress2(orgId,orgdetails);
            }
            
            System.out.println("orgDetailsforEmp "+orgDetailsforEmp);
            
            companyAddressTemplate=orgDetailsforEmp.size()>0 ? orgDetailsforEmp :companyAddressTemplate;
           
                
//                  System.out.println("map " + " " + map.toString());
                String name = Optional.ofNullable(userDetails.getOrDefault("name", "-"))
                        .orElse("-")
                        .toString();
                String employee_code = Optional.ofNullable(userDetails.getOrDefault("employee_code", "-"))
                        .orElse("-")
                        .toString();
                String desingnation = Optional.ofNullable(userDetails.getOrDefault("emp_desingnation", "-"))
                        .orElse("-")
                        .toString();
                String joining_date = Optional.ofNullable(userDetails.getOrDefault("joining_date", "-"))
                        .orElse("-")
                        .toString();
                String bankaccount = Optional.ofNullable(userDetails.getOrDefault("bankaccount", "-"))
                        .orElse("-")
                        .toString();
                String location = Optional.ofNullable(userDetails.getOrDefault("location", "-"))
                        .orElse("-")
                        .toString();
                String workingDay = workingDayMap.get("working_day").toString();
                String companyLogo = "";
                try {

                    Map url = salaryBreakupServiceImpl.fetchingSingedUrlFromRecruit(request, Long.parseLong(map.get("orgId").toString()));
                    if (!url.isEmpty()) {

                        if (url.get("status") != null && url.get("status").toString().equalsIgnoreCase("success")) {
                            companyLogo = url.get("orgImage") != null && !url.get("orgImage").equals("") ? url.get("orgImage").toString() : "";
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //companyLogo = companyAddressTemplate.get("company_name")!= null ? gcpConfig.getSignedUrl(recruitBucketName, companyAddressTemplate.get("company_name").toString()) : "";
                String orgName = companyAddressTemplate.get("org_name").toString();

                Map resultMap = taxService.getTaxSlip(employeeId, month, year);
                List<LinkedHashMap> taxListOrder = new ArrayList<>();
                taxListOrder = (List<LinkedHashMap>) resultMap.get("rows");
                StringBuilder taxDetails = new StringBuilder();

                for (LinkedHashMap tax : taxListOrder) {

                    taxDetails.append("<tr>\n");
                    taxDetails.append("<td><b>");
                    taxDetails.append(tax.get("salary_hra_name")).append("</b></td>");

                    taxDetails.append("<td>");
                    taxDetails.append(tax.get("salary_hra_amount").toString()).append("</td>");

                    taxDetails.append("<td><b>");
                    taxDetails.append(tax.get("exemption_name").toString()).append("</b></td>");

                    taxDetails.append("<td><b>");
                    taxDetails.append(tax.get("exemption_declared_amount").toString()).append("</b></td>");

                    taxDetails.append("<td><b>");
                    taxDetails.append(tax.get("exemption_exempted_amount").toString()).append("</b></td>");

                    taxDetails.append("<td><b>");
                    taxDetails.append(tax.get("tax_name").toString()).append("</b></td>");

                    taxDetails.append("<td>");
                    taxDetails.append(tax.get("tax_amount").toString()).append("</td>");

                    taxDetails.append("</tr>");

                }

                // Create a Calendar instance and set the year and month
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1); // Calendar month is 0-based
                calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

                // Format the date using SimpleDateFormat
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
                String formattedDate = sdf.format(calendar.getTime());

                String template = "detailPayslip.html";
                String htmlContent = new Scanner(getClass().getClassLoader().getResourceAsStream(template), "UTF-8")
                        .useDelimiter("\\A")
                        .next();

                htmlContent = htmlContent.replace("$companyLogo", companyLogo);
                htmlContent = htmlContent.replace("$companyName", orgName);
                htmlContent = htmlContent.replace("$name", name)
                        .replace("$employee_code", employee_code)
                        .replace("$desingnation", desingnation)
                        .replace("$joining_date", joining_date)
                        .replace("$bankaccount", bankaccount)
                        .replace("$location", location)
                        .replace("$workingDay", workingDay)
                        .replace("$monthAndYear", formattedDate);
                htmlContent = htmlContent.replace("$details", taxDetails.toString());

                htmlContent = htmlContent.replace("&", "&amp;");

                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    ITextRenderer renderer = new ITextRenderer();
                    renderer.setDocumentFromString(htmlContent);
                    renderer.layout();
                    renderer.createPDF(outputStream);
//                    System.out.println(outputStream.toByteArray() + " output");
                    return outputStream.toByteArray();

//                   String base64Encoded = Base64.getEncoder().encodeToString(outputStream.toByteArray());
//        System.out.println("Base64 Encoded: " + base64Encoded);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            byte a[] = new byte[0];

            return a;
            // return a;
        }

    }

    @Override
    public @ResponseBody
    byte[] generateGateSalaryReport(HttpServletRequest request, String data) {

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("map " + " " + map.toString());
            Long employeeId = map.get("employee_id") != null ? Long.parseLong(map.get("employee_id").toString()) : 0;
            int month = map.get("month") != null ? Integer.parseInt(map.get("month").toString()) : 0;
            int year = map.get("year") != null ? Integer.parseInt(map.get("year").toString()) : 0;
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            Long siteId = map.get("siteId") != null ? Long.parseLong(map.get("siteId").toString()) : 0;

            SalaryBreakUp salarybreakupdata = salaryBreakuprepo.SalaryBreakUpOfEmployeeMonthly(employeeId, month, year, siteId);

            List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.savedAllowancesMonthly(salarybreakupdata.getSid());

            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.savedDeductionMonthly(salarybreakupdata.getSid());

            CustomRunPayroll runPayrolldata = customRunPayrollRepository.getPayrollDateByMonthAndYear(employeeId, month, year, siteId);

            NumberFormat myFormat = NumberFormat.getInstance();

            double currentAllowanceSum = 0.0;
            double currentdeductonsum = 0.0;

            int y = Integer.parseInt(map.get("year").toString());
            int m = Integer.parseInt(map.get("month").toString());
            // Create a Calendar instance and set the year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m - 1); // Calendar month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

            // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            String monthAndYear = sdf.format(calendar.getTime());

            LinkedCaseInsensitiveMap userDetails = employeeDetailsRepo.getEmployeeDetails(employeeId);

            LinkedCaseInsensitiveMap companyAddressTemplate = orgRepo.getOrganizationAddress(orgId);

            String name = Optional.ofNullable(userDetails.getOrDefault("name", "N/A"))
                    .orElse("N/A")
                    .toString();

            String companyName = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_name", ""))
                    .orElse("")
                    .toString();
            String addressOne = Optional.ofNullable(companyAddressTemplate.getOrDefault("organization_address", ""))
                    .orElse("")
                    .toString();
            String addressTwo = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_address_line_two", ""))
                    .orElse("")
                    .toString();
            String pinCode = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_pincode", ""))
                    .orElse("")
                    .toString();
            String city = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_city", ""))
                    .orElse("")
                    .toString();
            String state = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_state", ""))
                    .orElse("")
                    .toString();

            String leave = salarybreakupdata.getApproved_leave() != null
                    ? myFormat.format(salarybreakupdata.getApproved_leave())
                    : "0";

            String weekoff = salarybreakupdata.getWeek_off() != null
                    ? myFormat.format(salarybreakupdata.getWeek_off())
                    : "0";

            String holidays = Optional.ofNullable(myFormat.format(runPayrolldata.getPh()))
                    .orElse("");
            
            String epf_days = Optional.ofNullable(myFormat.format(runPayrolldata.getEpfDays()))
                    .orElse("");
            
//            String basic_salary = Optional.ofNullable(myFormat.format(runPayrolldata.getBasicSalary()))
//                    .orElse("");
            
            String workingDays = Optional.ofNullable(myFormat.format(runPayrolldata.getWorkingDay()))
                    .orElse("");

            String otHours = runPayrolldata.getOverTimeFormatted() != null
                    ? runPayrolldata.getOverTimeFormatted()
                    : "0";

            String addressThree = pinCode + ", " + city + ", " + state;

            StringBuilder tableRows1 = new StringBuilder();

            int count = 4;
            
            for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {

                count++;
                double basicSalary=0.0;
               // Object allowanceName= allowance.getOrDefault("allowance_name", "");
                
                String allowanceNameStr = String.valueOf(allowance.getOrDefault("allowance_name", "")).trim();
                if ("Basic Salary".equalsIgnoreCase(allowanceNameStr)) {
                    basicSalary = runPayrolldata.getBasicSalary();
                }
                
                tableRows1.append("<tr>\n");

                tableRows1.append("<td style=\"width:3%; text-align:center;\">")
                        .append(count)
                        .append("</td>\n");

                tableRows1.append("<td style=\"width:47%;\">")
                        .append(allowance.getOrDefault("allowance_name", ""))
                        .append("</td>\n");

                tableRows1.append("<td>")
                        .append(myFormat.format(basicSalary))
                        .append("</td>\n");

                tableRows1.append("</tr>\n");

                currentAllowanceSum +=basicSalary;
                
            }

            count = count + 1;

            // total earning sum row
            tableRows1.append("<tr>\n");

            tableRows1.append("<td style=\"width:3%; text-align:center;\">")
                    .append(count)
                    .append("</td>\n");

            tableRows1.append("<td style=\"width:47%; font-weight: bold;\">")
                    .append("Total Earning")
                    .append("</td>\n");

            tableRows1.append("<td>")
                    .append(myFormat.format(currentAllowanceSum))
                    .append("</td>\n");

            tableRows1.append("</tr>\n");

            // deduction row   
            for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {

                count++;
              
                Object deductionName= deduction.getOrDefault("deduction_name", "");
               double amount=0.0;
               
               
                tableRows1.append("<tr>\n");

                tableRows1.append("<td style=\"width:3%; text-align:center;\">")
                        .append(count)
                        .append("</td>\n");

                tableRows1.append("<td style=\"width:47%;\">")
                        .append(deduction.getOrDefault("deduction_name", ""))
                        .append("</td>\n");
                
                 if(deductionName.equals("EPF") ||deductionName.equals("ESIC") || deductionName.equals("Professional Tax")){
                        amount=Double.parseDouble(deduction.get("deduction_payable_amount").toString());
                   
                  tableRows1.append("<td>")
                        .append(myFormat.format(deduction.getOrDefault("deduction_payable_amount", "")))
                        .append("</td>\n");
                 }
                 else{
                        tableRows1.append("<td>")
                        .append(myFormat.format(0))
                        .append("</td>\n");
                 }

                tableRows1.append("</tr>\n");

                currentdeductonsum +=amount;
            }

            count = count + 1;

            // total Deduction sum row
            tableRows1.append("<tr>\n");

            tableRows1.append("<td style=\"width:3%; text-align:center;\">")
                    .append(count)
                    .append("</td>\n");

            tableRows1.append("<td style=\"width:47%; font-weight: bold;\">")
                    .append("Total Deductions")
                    .append("</td>\n");

            tableRows1.append("<td>")
                    .append(myFormat.format(currentdeductonsum))
                    .append("</td>\n");

            tableRows1.append("</tr>\n");

            count = count + 1;

            // net salary row
            tableRows1.append("<tr>\n");

            tableRows1.append("<td style=\"width:3%; text-align:center;\">")
                    .append(count)
                    .append("</td>\n");

            tableRows1.append("<td style=\"width:47%; font-weight: bold;\">")
                    .append("Net salary")
                    .append("</td>\n");

            tableRows1.append("<td>")
                    .append(myFormat.format((currentAllowanceSum - currentdeductonsum)))
                    .append("</td>\n");

            tableRows1.append("</tr>\n");

            String template = "customGateSalary.html";
            String htmlContent = new Scanner(getClass().getClassLoader().getResourceAsStream(template), "UTF-8")
                    .useDelimiter("\\A")
                    .next();

            htmlContent = htmlContent.replace("$name", name)
                    .replace("$companyName", companyName)
                    .replace("$addressOne", addressOne)
                    .replace("$addressTwo", addressTwo)
                    .replace("$addressThree", addressThree)
                    .replace("$leave", leave)
                    .replace("$weekoff", weekoff)
                    .replace("$holidays", holidays)
                    .replace("$workingDays", epf_days)
                    .replace("$otHours", otHours);

            htmlContent = htmlContent.replace("$rows", tableRows1.toString());

            htmlContent = htmlContent.replace("&", "&amp;");

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);
                return outputStream.toByteArray();

            }

        } catch (Exception e) {
            e.printStackTrace();
            byte a[] = new byte[0];

            return a;

        }

    }

    @Override
    public @ResponseBody
    byte[] generateMonthlySlip(HttpServletRequest request, String data) {

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class
            );
            System.out.println("map " + " " + map.toString());
            Long employeeId = map.get("employee_id") != null ? Long.parseLong(map.get("employee_id").toString()) : 0;
            int month = map.get("month") != null ? Integer.parseInt(map.get("month").toString()) : 0;
            int year = map.get("year") != null ? Integer.parseInt(map.get("year").toString()) : 0;
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;

            List<SalaryBreakUp> salarybreakupdata = salaryBreakuprepo.SalaryBreakUpMonthly(employeeId, month, year);

            List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.savedAllowancesOfEmployeeMonthly(employeeId, month, year);

            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.savedDeductionOfEmployeeMonthly(employeeId, month, year);

            List<CustomRunPayroll> runPayrolldata = customRunPayrollRepository.getPayrollMonthly(employeeId, month, year);

            SalaryBreakUp salaryData= salarybreakupdata.get(0);
            double actualWorkingDays=salaryData.getActual_day();
            double currentAllowanceSum = 0.0;
            double currentdeductonsum = 0.0;
            double currentAllowancePayableSum = 0.0;
            Double[] totalWorkingdays=new Double[1];
            totalWorkingdays[0]=0.0;
            Double[] totalWeekOff=new Double[1];
            totalWeekOff[0]=0.0;
            
            Double[] totalLwp=new Double[1];
            totalLwp[0]=0.0;
            
            runPayrolldata.stream().forEach(action->{
            
               totalWorkingdays[0] += action.getWorkingDay();
            });
            
            salarybreakupdata.stream().forEach(action->{
            
               totalWeekOff[0] += action.getWeek_off();
               totalLwp[0] += action.getLwp();
            });
            
            NumberFormat myFormat = NumberFormat.getInstance();


            int y = Integer.parseInt(map.get("year").toString());
            int m = Integer.parseInt(map.get("month").toString());
            // Create a Calendar instance and set the year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m - 1); // Calendar month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

            // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            String monthAndYear = sdf.format(calendar.getTime());

            LinkedCaseInsensitiveMap userDetails = employeeDetailsRepo.getEmployeeDetails(employeeId);

            LinkedCaseInsensitiveMap companyAddressTemplate = orgRepo.getOrganizationAddress(orgId);

            int index = 0;
            StringBuilder tableRows1 = new StringBuilder();
            StringBuilder tableRows2 = new StringBuilder();

            if (employeeAllowances.size() > employeeDeductions.size()) {

                for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {

                    currentAllowanceSum += ((Number) allowance.get("allowance_amount")).doubleValue();
                    currentAllowancePayableSum += ((Number) allowance.get("allowance_payable_amount")).doubleValue();

                    tableRows1.append("<tr>\n");

                    tableRows1.append("<td>")
                            .append(allowance.getOrDefault("allowance_name", ""))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(allowance.getOrDefault("allowance_amount", "")))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(allowance.getOrDefault("allowance_name", ""))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(allowance.getOrDefault("allowance_payable_amount", "")))
                            .append("</td>\n");

                    try {

                        LinkedCaseInsensitiveMap deductionValue = employeeDeductions.get(index);
                        currentdeductonsum += ((Number) deductionValue.get("deduction_payable_amount")).doubleValue();

                        tableRows1.append("<td>")
                                .append(deductionValue.getOrDefault("deduction_name", ""))
                                .append("</td>\n");

                        tableRows1.append("<td>")
                                .append(myFormat.format(deductionValue.getOrDefault("deduction_payable_amount", "")))
                                .append("</td>\n");
                    } catch (Exception e) {

                        tableRows1.append("<td>")
                                .append("")
                                .append("</td>\n");

                        tableRows1.append("<td>")
                                .append("")
                                .append("</td>\n");

                    }

                    tableRows1.append("</tr>\n");

                    index++;
                }
            } else {

                for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {

                    currentdeductonsum += ((Number) deduction.get("deduction_payable_amount")).doubleValue();

                    tableRows1.append("<tr>\n");

                    try {
                        
                    LinkedCaseInsensitiveMap allowanceValue = employeeAllowances.get(index);
                    currentAllowanceSum += ((Number) allowanceValue.get("allowance_amount")).doubleValue();
                    currentAllowancePayableSum += ((Number) allowanceValue.get("allowance_payable_amount")).doubleValue();
                      
                    tableRows1.append("<td>")
                            .append(allowanceValue.getOrDefault("allowance_name", ""))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(allowanceValue.getOrDefault("allowance_amount", "")))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(allowanceValue.getOrDefault("allowance_name", ""))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(allowanceValue.getOrDefault("allowance_payable_amount", "")))
                            .append("</td>\n");

                } catch (Exception e) {
                   
                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");
                    
                }
                    tableRows1.append("<td>")
                            .append(deduction.getOrDefault("deduction_name", ""))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(deduction.getOrDefault("deduction_payable_amount", "")))
                            .append("</td>\n");

                    tableRows1.append("</tr>\n");

                    index++;
                }

            }
            // total sum
            tableRows1.append("<tr>\n");

                    tableRows1.append("<td>")
                            .append("Total")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(currentAllowanceSum))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append("Gross")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(currentAllowancePayableSum))
                            .append("</td>\n");
                     tableRows1.append("<td>")
                            .append("Deduction")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(currentdeductonsum))
                            .append("</td>\n");

                    tableRows1.append("</tr>\n");
                    
             // net payable calculation
             
             Double net =currentAllowancePayableSum-currentdeductonsum;
              tableRows1.append("<tr>\n");

                    tableRows1.append("<td colspan=\"4\">")
                            .append(NumberToWords.convertToWords(net.toString()))
                            .append("</td>\n");
             
                    tableRows1.append("<td>")
                            .append("Net Salary")
                            .append("</td>\n");
                     tableRows1.append("<td>")
                            .append(myFormat.format(net))
                            .append("</td>\n");


                    tableRows1.append("</tr>\n");
                    
             
             

            // user Details
            String empCode = Optional.ofNullable(userDetails.getOrDefault("employee_code", "N/A"))
                    .orElse("N/A")
                    .toString();

            String name = Optional.ofNullable(userDetails.getOrDefault("name", "N/A"))
                    .orElse("N/A")
                    .toString();

            String empDesingnation = Optional.ofNullable(userDetails.getOrDefault("emp_desingnation", "N/A"))
                    .orElse("N/A")
                    .toString();

            String joiningDate = Optional.ofNullable(userDetails.getOrDefault("joining_date", "N/A"))
                    .orElse("N/A")
                    .toString();

            String departmentName = Optional.ofNullable(userDetails.getOrDefault("department_name", "N/A"))
                    .orElse("N/A")
                    .toString();

            String grade = Optional.ofNullable(userDetails.getOrDefault("grade", "N/A"))
                    .orElse("N/A")
                    .toString();

            String pf = Optional.ofNullable(userDetails.getOrDefault("pf", "N/A"))
                    .orElse("N/A")
                    .toString();

            String bankaccount = Optional.ofNullable(userDetails.getOrDefault("bankaccount", "N/A"))
                    .orElse("N/A")
                    .toString();

            String bankname = Optional.ofNullable(userDetails.getOrDefault("bankname", "N/A"))
                    .orElse("N/A")
                    .toString();

            String esic = Optional.ofNullable(userDetails.getOrDefault("esic", "N/A"))
                    .orElse("N/A")
                    .toString();

            String uan = Optional.ofNullable(userDetails.getOrDefault("uan", "N/A"))
                    .orElse("N/A")
                    .toString();

            String panNumber = Optional.ofNullable(userDetails.getOrDefault("pan_number", "N/A"))
                    .orElse("N/A")
                    .toString();

            // conpany Details
            String companyName = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_name", ""))
                    .orElse("")
                    .toString();
            String addressOne = Optional.ofNullable(companyAddressTemplate.getOrDefault("organization_address", ""))
                    .orElse("")
                    .toString();
            String addressTwo = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_address_line_two", ""))
                    .orElse("")
                    .toString();
            String pinCode = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_pincode", ""))
                    .orElse("")
                    .toString();
            String city = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_city", ""))
                    .orElse("")
                    .toString();
            String state = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_state", ""))
                    .orElse("")
                    .toString();
            
            String workingdays = Optional.ofNullable(myFormat.format(totalWorkingdays[0]))
                    .orElse("");
            
            String weekOff = Optional.ofNullable(myFormat.format(totalWeekOff[0]))
                    .orElse("");
            
            String lwp = Optional.ofNullable(myFormat.format(totalLwp[0]))
                    .orElse("");
            
            String actualWorkingDay = Optional.ofNullable(myFormat.format(actualWorkingDays))
                    .orElse("");
            
            
            String addressThree = pinCode + ", " + city + ", " + state;

            /**
             * getting company logo from Recruit module
               *
             */
            String companyLogo = "";
            Map url = salaryBreakupServiceImpl.fetchingSingedUrlFromRecruit(request, orgId);
            if (!url.isEmpty()) {

                if (url.get("status") != null && url.get("status").toString().equalsIgnoreCase("success")) {
                    companyLogo = url.get("orgImage") != null && !url.get("orgImage").equals("") ? url.get("orgImage").toString() : "";
                }

            }
            
            /**
             * getting Leave Details from Manage
               *
             */
            
            List<LinkedHashMap> leavaValue=new ArrayList<>();

            
            Map leaveData = salaryBreakupServiceImpl.fetchingLeaveDetailsFromManage(request, orgId,employeeId,month,year);
            
            System.out.println("leaveData");
            System.out.println(leaveData);
            
            if (!leaveData.isEmpty()) {

                if (leaveData.get("status") != null && leaveData.get("status").toString().equalsIgnoreCase("success")) {
                    leavaValue = (List<LinkedHashMap>) leaveData.get("data") ;
                }

            }
            
            System.out.println("823");
            System.out.println(leavaValue);
            
            for (LinkedHashMap value:leavaValue) {
              
                tableRows2.append("<tr>\n");
                
                tableRows2.append("<td style=\"border-left:none;\">")
                        .append(value.get("leave_type_formatted").toString())
                        .append("</td>\n");
                
                 tableRows2.append("<td>")
                        .append(value.get("opening_balance").toString())
                        .append("</td>\n");
                 
                  tableRows2.append("<td>")
                        .append(value.get("leave_taken").toString())
                        .append("</td>\n");
                  
                   tableRows2.append("<td>")
                        .append(value.get("leave_accumulated").toString())
                        .append("</td>\n");
                   
                    tableRows2.append("<td style=\"border-right:none;\">")
                        .append(value.get("current_balance").toString())
                        .append("</td>\n");
                
                tableRows2.append("</tr>\n");
               
            }
            
            
            

            String template = "customMonthlySlip.html";
            String htmlContent = new Scanner(getClass().getClassLoader().getResourceAsStream(template), "UTF-8")
                    .useDelimiter("\\A")
                    .next();
            

            htmlContent = htmlContent.replace("$companyLogo", companyLogo);

            htmlContent = htmlContent.replace("$rows", tableRows1.toString());
            htmlContent = htmlContent.replace("$leaveRows", tableRows2.toString());

            htmlContent = htmlContent.replace("$empCode", empCode)
                    .replace("$name", name)
                    .replace("$empDesingnation", empDesingnation)
                    .replace("$joiningDate", joiningDate)
                    .replace("$departmentName", departmentName)
                    .replace("$grade", grade)
                    .replace("$pf", pf)
                    .replace("$bankaccount", bankaccount)
                    .replace("$bankname", bankname)
                    .replace("$esic", esic)
                    .replace("$uan", uan)
                    .replace("$panNumber", panNumber)
                    .replace("$companyName", companyName)
                    .replace("$addressOne", addressOne)
                    .replace("$addressTwo", addressTwo)
                    .replace("$addressThree", addressThree)
                    .replace("$monthAndYear", monthAndYear)
                    .replace("$workingdays", workingdays)
                    .replace("$weekOff", weekOff)
                    .replace("$lwp", lwp)
                    .replace("$actualWorkingDay", actualWorkingDay)
                    
                    ;

            htmlContent = htmlContent.replace("&", "&amp;");

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);
                return outputStream.toByteArray();

            }

        } catch (Exception e) {
            e.printStackTrace();
            byte a[] = new byte[0];

            return a;

        }

    }
    
        @Override
    public @ResponseBody
    byte[] generateGateSalarySaralReport(HttpServletRequest request, String data) {

        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("map " + " " + map.toString());
            Long employeeId = map.get("employee_id") != null ? Long.parseLong(map.get("employee_id").toString()) : 0;
            int month = map.get("month") != null ? Integer.parseInt(map.get("month").toString()) : 0;
            int year = map.get("year") != null ? Integer.parseInt(map.get("year").toString()) : 0;
            Long orgId = map.get("organization_id") != null ? Long.parseLong(map.get("organization_id").toString()) : 0;
            Long siteId = map.get("siteId") != null ? Long.parseLong(map.get("siteId").toString()) : 0;

            SalaryBreakUp salarybreakupdata = salaryBreakuprepo.SalaryBreakUpOfEmployeeMonthly(employeeId, month, year, siteId);

            List<LinkedCaseInsensitiveMap> employeeAllowances = employeeAllowanceRepo.savedAllowancesMonthly(salarybreakupdata.getSid());

            List<LinkedCaseInsensitiveMap> employeeDeductions = employeeDeductionRepo.savedDeductionMonthly(salarybreakupdata.getSid());

            CustomRunPayroll runPayrolldata = customRunPayrollRepository.getPayrollDateByMonthAndYear(employeeId, month, year, siteId);

            NumberFormat myFormat = NumberFormat.getInstance();

            double currentAllowanceSum = 0.0;
            double currentdeductonsum = 0.0;
            double currentAllowancePayableSum = 0.0;

            int y = Integer.parseInt(map.get("year").toString());
            int m = Integer.parseInt(map.get("month").toString());
            // Create a Calendar instance and set the year and month
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m - 1); // Calendar month is 0-based
            calendar.set(Calendar.DAY_OF_MONTH, 1); // Set the day of the month to 1

            // Format the date using SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
            String monthAndYear = sdf.format(calendar.getTime());

            LinkedCaseInsensitiveMap userDetails = employeeDetailsRepo.getEmployeeDetails(employeeId);

            LinkedCaseInsensitiveMap companyAddressTemplate = orgRepo.getOrganizationAddress(orgId);

            String name = Optional.ofNullable(userDetails.getOrDefault("name", "N/A"))
                    .orElse("N/A")
                    .toString();
            
            String empDesingnation = Optional.ofNullable(userDetails.getOrDefault("emp_desingnation", "N/A"))
                    .orElse("N/A")
                    .toString();


            String pf = Optional.ofNullable(userDetails.getOrDefault("pf", "N/A"))
                    .orElse("N/A")
                    .toString();

            String bankaccount = Optional.ofNullable(userDetails.getOrDefault("bankaccount", "N/A"))
                    .orElse("N/A")
                    .toString();

            String bankname = Optional.ofNullable(userDetails.getOrDefault("bankname", "N/A"))
                    .orElse("N/A")
                    .toString();

            String esic = Optional.ofNullable(userDetails.getOrDefault("esic", "N/A"))
                    .orElse("N/A")
                    .toString();
            

            String companyName = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_name", ""))
                    .orElse("")
                    .toString();
            String addressOne = Optional.ofNullable(companyAddressTemplate.getOrDefault("organization_address", ""))
                    .orElse("")
                    .toString();
            String addressTwo = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_address_line_two", ""))
                    .orElse("")
                    .toString();
            String pinCode = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_pincode", ""))
                    .orElse("")
                    .toString();
            String city = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_city", ""))
                    .orElse("")
                    .toString();
            String state = Optional.ofNullable(companyAddressTemplate.getOrDefault("org_state", ""))
                    .orElse("")
                    .toString();

            String leave = salarybreakupdata.getApproved_leave() != null
                    ? myFormat.format(salarybreakupdata.getApproved_leave())
                    : "0";

            String weekoff = salarybreakupdata.getWeek_off() != null
                    ? myFormat.format(salarybreakupdata.getWeek_off())
                    : "0";

            String holidays = Optional.ofNullable(myFormat.format(runPayrolldata.getPh()))
                    .orElse("");

            String workingDays = Optional.ofNullable(myFormat.format(runPayrolldata.getWorkingDay()))
                    .orElse("");

            String otHours = runPayrolldata.getOverTimeFormatted() != null
                    ? runPayrolldata.getOverTimeFormatted()
                    : "0";
            
            String epf_days = Optional.ofNullable(myFormat.format(runPayrolldata.getEpfDays()))
                    .orElse("");
            
            String addressThree = pinCode + ", " + city + ", " + state;

            StringBuilder tableRows1 = new StringBuilder();

            int count = 0,index=0;
            double unitValue=0.0;
                       
              if (employeeAllowances.size() > employeeDeductions.size()) {

                for (LinkedCaseInsensitiveMap allowance : employeeAllowances) {

                     double basicSalary=0.0;
                                     
                  //  Object allowanceName= allowance.getOrDefault("allowance_name", "");
                   
                String allowanceNameStr = String.valueOf(allowance.getOrDefault("allowance_name", "")).trim();
                if ("Basic Salary".equalsIgnoreCase(allowanceNameStr)) {
                    basicSalary = runPayrolldata.getBasicSalary();
                }

                    currentAllowanceSum +=basicSalary;
                    currentAllowancePayableSum +=basicSalary; 
                    tableRows1.append("<tr>\n");

                    tableRows1.append("<td>")
                            .append(allowance.getOrDefault("allowance_name", ""))
                            .append("</td>\n");
                    
                    tableRows1.append("<td>")
                            .append(myFormat.format(unitValue))
                            .append("</td>\n");
                    

                    tableRows1.append("<td>")
                            .append(myFormat.format(basicSalary))
                            .append("</td>\n");

                    try {

                        LinkedCaseInsensitiveMap deductionValue = employeeDeductions.get(index);
                        
                           Object deductionName= deductionValue.getOrDefault("deduction_name", "");
                           double amount=0.0;
                        
                        if (deductionName.equals("EPF") || deductionName.equals("ESIC") || deductionName.equals("Professional Tax")) {
                            amount = Double.parseDouble(deductionValue.get("deduction_payable_amount").toString());

                        }
                        
                        currentdeductonsum +=amount;
                        
                        tableRows1.append("<td>")
                                .append(deductionValue.getOrDefault("deduction_name", ""))
                                .append("</td>\n");

                        tableRows1.append("<td>")
                                .append(myFormat.format(amount))
                                .append("</td>\n");
                    } catch (Exception e) {

                        tableRows1.append("<td>")
                                .append("")
                                .append("</td>\n");

                        tableRows1.append("<td>")
                                .append("")
                                .append("</td>\n");

                    }

                    tableRows1.append("</tr>\n");

                    index++;
                }
            } else {

                for (LinkedCaseInsensitiveMap deduction : employeeDeductions) {

                    double basicSalary=0.0;
                    double amount=0.0;
                    tableRows1.append("<tr>\n");

                    try {
                        
                    LinkedCaseInsensitiveMap allowanceValue = employeeAllowances.get(index);
                  
                    // Object allowanceName= allowanceValue.getOrDefault("allowance_name", "");
                   
                                  String allowanceNameStr = String.valueOf(allowanceValue.getOrDefault("allowance_name", "")).trim();
                if ("Basic Salary".equalsIgnoreCase(allowanceNameStr)) {
                    basicSalary = runPayrolldata.getBasicSalary();
                }
                     

                    currentAllowanceSum +=basicSalary;
                    currentAllowancePayableSum +=basicSalary; 
                    
                    Object deductionName= deduction.getOrDefault("deduction_name", "");
                   
                        
                        if (deductionName.equals("EPF") || deductionName.equals("ESIC") || deductionName.equals("Professional Tax")) {
                            amount = Double.parseDouble(deduction.get("deduction_payable_amount").toString());

                        }
                                          
                    tableRows1.append("<td>")
                            .append(allowanceValue.getOrDefault("allowance_name", ""))
                            .append("</td>\n");
                    
                    tableRows1.append("<td>")
                            .append(myFormat.format(unitValue))
                            .append("</td>\n");


                    tableRows1.append("<td>")
                            .append(myFormat.format(basicSalary))
                            .append("</td>\n");

                } catch (Exception e) {
                   
                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");
                    
                }
                    tableRows1.append("<td>")
                            .append(deduction.getOrDefault("deduction_name", ""))
                            .append("</td>\n");

                    tableRows1.append("<td>")
                            .append(myFormat.format(amount))
                            .append("</td>\n");

                    tableRows1.append("</tr>\n");
                    currentdeductonsum +=amount;
                    index++;
                    unitValue=0.0;
                    amount=0.0;
                }

            }

              // total earning
              
             tableRows1.append("<tr>\n");
             
              tableRows1.append("<td style=\"font-weight: bold;\">")
                            .append("Total Earnings")
                            .append("</td>\n");

              tableRows1.append("<td>")
                            .append("")
                            .append("</td>\n");
                    
              tableRows1.append("<td style=\"font-weight: bold;\">")
                            .append(myFormat.format(currentAllowancePayableSum))
                            .append("</td>\n");

              tableRows1.append("<td style=\"font-weight: bold;\">")
                            .append("Total Deductions")
                            .append("</td>\n");

              tableRows1.append("<td style=\"font-weight: bold;\">")
                            .append(myFormat.format(currentdeductonsum))
                            .append("</td>\n");

             tableRows1.append("</tr>\n");
             
             // net Payable
              Double net =currentAllowancePayableSum-currentdeductonsum;
              tableRows1.append("<tr>\n");
             
              tableRows1.append("<td>")
                            .append("Net Payable")
                            .append("</td>\n");

              tableRows1.append("<td colspan=\"4\">")
                            .append(myFormat.format(net))
                            .append("</td>\n");
                    

             tableRows1.append("</tr>\n");
             
             // net Pay in word
             
                           tableRows1.append("<tr>\n");
             
              tableRows1.append("<td>")
                            .append("Net Pay in Words")
                            .append("</td>\n");

              tableRows1.append("<td colspan=\"4\">")
                            .append(NumberToWords.convertToWords(net.toString()))
                            .append("</td>\n");
                    
             tableRows1.append("</tr>\n");
             
              
              
            String template = "customGateSalarySaral.html";
            String htmlContent = new Scanner(getClass().getClassLoader().getResourceAsStream(template), "UTF-8")
                    .useDelimiter("\\A")
                    .next();

            htmlContent = htmlContent.replace("$name", name)
                    .replace("$companyName", companyName)
                    .replace("$addressOne", addressOne)
                    .replace("$addressTwo", addressTwo)
                    .replace("$addressThree", addressThree)
                    .replace("$leave", leave)
                    .replace("$weekoff", weekoff)
                    .replace("$holidays", holidays)
                    .replace("$workingDays", epf_days)
                    .replace("$otHours", otHours)
                    .replace("$empDesingnation", empDesingnation)
                    .replace("$pf", pf)
                    .replace("$bankaccount", bankaccount)
                    .replace("$bankname", bankname)
                    .replace("$esic", esic)
                    .replace("$monthAndYear", monthAndYear) ;

            htmlContent = htmlContent.replace("$rows", tableRows1.toString());

            htmlContent = htmlContent.replace("&", "&amp;");

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);
                return outputStream.toByteArray();

            }

        } catch (Exception e) {
            e.printStackTrace();
            byte a[] = new byte[0];

            return a;

        }

    }

}

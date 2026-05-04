/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.EmployeeLoan;
import com.realcoderz.repository.EmployeeLoanRepository;
import com.realcoderz.service.EmployeeLoanService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Mayank
 */
@Service
public class EmployeeLoanServiceImpl implements EmployeeLoanService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(EmployeeLoanServiceImpl.class);

    private final EmployeeLoanRepository employeeLoanRepo;
    private final RestTemplate restTemplate;

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    public EmployeeLoanServiceImpl(EmployeeLoanRepository employeeLoanRepo,
                                   RestTemplate restTemplate,
                                   @Value("${reimburshment_url}") String reimburshment_url) {
        this.employeeLoanRepo = employeeLoanRepo;
        this.restTemplate = restTemplate;
        this.reimburshment_url = reimburshment_url;
    }

    @Override
    public Map getLoanById(Long emp_id, Long org_id, Long loan_id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = employeeLoanRepo.getEmployeeLoanById(emp_id, org_id, loan_id);
            resultMap.clear();
            resultMap.put("list", employee_loan);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> getLoan :: ", ex);
        }
        return resultMap;

    }

    @Override
    public Map saveLoan(Map map) {
        Map resultMap = new HashMap<>();
        try {
            EmployeeLoan employee_loan = mapper.convertValue(map, EmployeeLoan.class);
            employee_loan.setLoan_requested_date(new Date());

            if (employee_loan != null) {
                employeeLoanRepo.save(employee_loan);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Employee Loan");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> saveLoan :: ", ex);
        }

        return resultMap;
    }

    @Override
    public Map calculateMonths(Double loanAmount, Double monthlyInstallment) {
        Map resultMap = new HashMap<>();
        double tenure = 0;
        int month = 0;
        if (monthlyInstallment != 0 && loanAmount != 0) {
            tenure = loanAmount / monthlyInstallment;
            resultMap.put("tenure", tenure);
            if (tenure - Math.floor(tenure) != 0) {
                month = (int) tenure;
                month += 1;
                resultMap.put("tenure", month);
            }
        }
        resultMap.put("status", "success");
        return resultMap;
    }

    @Override
    public Map getLoanByOrgId(Long org_id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = employeeLoanRepo.getLoanByOrgId(org_id);
            resultMap.clear();
            resultMap.put("list", employee_loan);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> getLoanByOrgId :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map remainingAmount(Map map) throws NullPointerException {
        Map resultMap = new HashMap<>();
        EmployeeLoan emp_loan = new EmployeeLoan();
        Map month = new HashMap<>();
        double remainingAmount = 0;
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = employeeLoanRepo.getEmployeeLoan(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()));
            List<EmployeeLoan> loanList = new ArrayList<>();
            if (!employee_loan.isEmpty()) {
                for (LinkedCaseInsensitiveMap l : employee_loan) {
                    if (l.containsKey("remaining_amount") && l.get("remaining_amount") != null) {
                        if (Double.parseDouble(l.get("remaining_amount").toString()) != 0) {
                            int tenure = Integer.parseInt(l.get("tenure").toString());
                            double amount_repaid = Double.parseDouble(l.get("amount_repaid").toString());
                            if (l.containsKey("approved_loan") && l.get("approved_loan") != null) {
                                if (Double.parseDouble(l.get("approved_loan").toString()) == 0) {
                                    remainingAmount = Double.parseDouble(l.get("loan_amount").toString());
                                } else {
                                    if (amount_repaid == 0) {
                                        remainingAmount = Double.parseDouble(l.get("approved_loan").toString()) - Double.parseDouble(map.get("other_deductions").toString());
                                    } else {
                                        remainingAmount = Double.parseDouble(l.get("remaining_amount").toString()) - Double.parseDouble(map.get("other_deductions").toString());
                                    }
                                    amount_repaid += Double.parseDouble(map.get("other_deductions").toString());
                                    month = this.calculateMonths(remainingAmount, Double.parseDouble(l.get("installment_amount").toString()));
                                }
                            }
                            emp_loan = new EmployeeLoan();
                            emp_loan.setEmployee_loan_id(l.get("employee_loan_id") != null ? Long.parseLong(l.get("employee_loan_id").toString()) : null);
                            emp_loan.setOrganization_id(l.get("organization_id") != null ? Long.parseLong(l.get("organization_id").toString()) : null);
                            emp_loan.setDescription(l.get("description") != null ? l.get("description").toString() : null);
                            emp_loan.setEmployee_name(l.get("employee_name") != null ? l.get("employee_name").toString() : null);
                            emp_loan.setLoan_amount(l.get("amount") != null ? Double.parseDouble(l.get("loan_amount").toString()) : 0);
                            emp_loan.setEmployee_id(l.get("employee_id") != null ? Long.parseLong(l.get("employee_id").toString()) : 0);
                            emp_loan.setInstallment_amount(l.get("installment_amount") != null ? Double.parseDouble(l.get("installment_amount").toString()) : 0);
                            emp_loan.setLoan_amount(l.get("loan_amount") != null ? Double.parseDouble(l.get("loan_amount").toString()) : 0);
                            emp_loan.setLoan_approved_amount(l.get("approved_loan") != null ? Double.parseDouble(l.get("approved_loan").toString()) : 0);
                            emp_loan.setLoan_approved_date(l.get("loan_approved_date") != null ? (Date) l.get("loan_approved_date") : null);
                            emp_loan.setLoan_requested_date(l.get("loan_requested_date") != null ? (Date) l.get("loan_requested_date") : null);
                            emp_loan.setStart_date((Date) l.get("start_date"));
                            emp_loan.setSupervisor_status(l.get("status") != null ? l.get("status").toString() : null);
                            emp_loan.setAccountant_status(l.get("accountant_status") != null ? l.get("accountant_status").toString() : null);
                            emp_loan.setTenure(month.get("tenure") != null ? (int) Double.parseDouble(month.get("tenure").toString()) : null);
                            emp_loan.setAmount_repaid(amount_repaid);
                            emp_loan.setRemaining_amount(remainingAmount);
                            emp_loan.setLoanStatus(l.get("loan_status") != null ? l.get("loan_status").toString() : null);
                            loanList.add(emp_loan);
                        }
                        employeeLoanRepo.saveAll(loanList);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Problem in RunPayServiceImpl :: saveAll() => " + ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    }

    @Override
    public Map getLoanForSupervisor(Long org_id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = employeeLoanRepo.getLoanForSupervisor(org_id);
            resultMap.clear();
            resultMap.put("list", employee_loan);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> getLoanByOrgId :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map getEmployeeLoan(Long emp_id, Long org_id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = employeeLoanRepo.getEmployeeLoan(emp_id, org_id);
            List<LinkedCaseInsensitiveMap> emplist = employeeLoanRepo.getFullTimeEmployeeList(org_id);

            resultMap.clear();
            resultMap.put("list", employee_loan);
            resultMap.put("empList", emplist);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> getEmployeeLoan :: ", ex);
        }
        return resultMap;

    }

    @Override
    public Map getFulltimeEmployeeLoan(Long org_id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = employeeLoanRepo.getFullTimeEmployeeLoan(org_id);
            List<LinkedCaseInsensitiveMap> emplist = employeeLoanRepo.getFullTimeEmployeeList(org_id);

            resultMap.clear();
            resultMap.put("list", employee_loan);
            resultMap.put("empList", emplist);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> getEmployeeLoan :: ", ex);
        }
        return resultMap;

    }

    @Override
    public Map getFulltimeEmployeeLoanBySupervisor(Long org_id, Long supervisorId) {

        Map<String, Object> resultMap = new HashMap<>();

        try {
            org.json.JSONObject json = new org.json.JSONObject();
            json.put("orgId", org_id);
            json.put("supervisorId", supervisorId);

            String encryptedPayload = EncryptDecryptUtils.encrypt(json.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);

            HttpEntity<String> entity = new HttpEntity<>(encryptedPayload, headers);

            ResponseEntity<HashMap> response = restTemplate.exchange(
                    reimburshment_url + "/users/getAllEmployeeBySupervisor",
                    HttpMethod.POST,
                    entity,
                    HashMap.class
            );

            Map<String, Object> employeeListReq = response.getBody();
            Map<String, Object> employeeListResp = null;

            if (employeeListReq != null && employeeListReq.get("data") != null) {

                Object dataObj = employeeListReq.get("data");

                try {
                    String decryptedData;

                    if (dataObj instanceof String) {
                        decryptedData = EncryptDecryptUtils.decrypt((String) dataObj);
                    } else {
                        decryptedData = mapper.writeValueAsString(dataObj);
                    }

                    employeeListResp = mapper.readValue(
                            decryptedData,
                            Map.class
                    );

                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error("Unable to decrypt employee list :: ", ex);
                }
            }

            List<LinkedCaseInsensitiveMap> employeeLoan
                    = employeeLoanRepo.getFullTimeEmployeeLoan(org_id);

            List<LinkedCaseInsensitiveMap> empList
                    = employeeLoanRepo.getFullTimeEmployeeList(org_id);

            System.out.println("employeeListResp.get(\"list\") ------------- " + employeeListResp.get("list"));
            List<LinkedCaseInsensitiveMap> filteredEmpList = empList;

            if (employeeListResp != null && employeeListResp.get("list") != null) {

                List<Map<String, Object>> apiEmpList
                        = (List<Map<String, Object>>) employeeListResp.get("list");

                // Extract employee IDs
                Set<Long> allowedEmpIds = apiEmpList.stream()
                        .map(emp -> Long.valueOf(emp.get("employeeId").toString()))
                        .collect(Collectors.toSet());

                // Filter empList
                filteredEmpList = empList.stream()
                        .filter(emp -> {
                            Object idObj = emp.get("employee_id");
                            return idObj != null
                                    && allowedEmpIds.contains(Long.valueOf(idObj.toString()));
                        })
                        .collect(Collectors.toList());
            }

            resultMap.clear();
            resultMap.put("list", employeeLoan);
            resultMap.put("empList", filteredEmpList);
            resultMap.put("status", "success");

        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Problem in getFulltimeEmployeeLoanBySupervisor :: ", ex);

            resultMap.clear();
            resultMap.put("status", "exception");
            resultMap.put("message", ex.getMessage());
        }

        return resultMap;
    }

    @Override
    public Map updateLoanStatus(Long loanId, String loanStatus) {
        Map resultMap = new HashMap<>();
        try {
            int updated = employeeLoanRepo.updateLoanStatus(loanId, loanStatus);
            if (updated > 0) {
                resultMap.put("status", "success");
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "No loan found with given id");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> updateLoanStatus :: ", ex);
        }
        return resultMap;
    }

    @Override
    public ResponseEntity<byte[]> downloadLoanForSupervisorReport(Long org_id) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        try {
            List<LinkedCaseInsensitiveMap> list = employeeLoanRepo.getLoanForSupervisor(org_id);

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Loan For Supervisor");

            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            org.apache.poi.ss.usermodel.CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            int rowNum = 0;

            // Title row
            String[] headers = {"S.No", "Emp Code", "Employee Name", "Department", "Loan Amount",
                "Approved Amount", "Installment", "Amount Repaid", "Remaining Amount",
                "Tenure", "Start Date", "Requested Date", "Supervisor Status", "Loan Status", "Description"};

            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Loan Report For Supervisor");
            titleCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, headers.length - 1));

            // Header row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            int sNo = 1;
            for (LinkedCaseInsensitiveMap row : list) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(sNo++);
                dataRow.createCell(1).setCellValue(row.get("employee_code") != null ? row.get("employee_code").toString() : "");
                dataRow.createCell(2).setCellValue(row.get("employee_name") != null ? row.get("employee_name").toString() : "");
                dataRow.createCell(3).setCellValue(row.get("department_name") != null ? row.get("department_name").toString() : "");
                dataRow.createCell(4).setCellValue(row.get("loan_amount") != null ? Double.parseDouble(row.get("loan_amount").toString()) : 0);
                dataRow.createCell(5).setCellValue(row.get("approved_loan") != null ? Double.parseDouble(row.get("approved_loan").toString()) : 0);
                dataRow.createCell(6).setCellValue(row.get("installment_amount") != null ? Double.parseDouble(row.get("installment_amount").toString()) : 0);
                dataRow.createCell(7).setCellValue(row.get("amount_repaid") != null ? Double.parseDouble(row.get("amount_repaid").toString()) : 0);
                dataRow.createCell(8).setCellValue(row.get("remaining_amount") != null ? Double.parseDouble(row.get("remaining_amount").toString()) : 0);
                dataRow.createCell(9).setCellValue(row.get("tenure") != null ? row.get("tenure").toString() : "");
                dataRow.createCell(10).setCellValue(row.get("start_date") != null ? row.get("start_date").toString() : "");
                dataRow.createCell(11).setCellValue(row.get("loan_requested_date") != null ? row.get("loan_requested_date").toString() : "");
                dataRow.createCell(12).setCellValue(row.get("supervisor_status") != null ? row.get("supervisor_status").toString() : "");
                dataRow.createCell(13).setCellValue(row.get("loan_status") != null ? row.get("loan_status").toString() : "");
                dataRow.createCell(14).setCellValue(row.get("description") != null ? row.get("description").toString() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 256 * 22);
            }

            workbook.write(out);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            httpHeaders.setContentDisposition(org.springframework.http.ContentDisposition.builder("attachment").filename("LoanForSupervisorReport.xlsx").build());
            return new ResponseEntity<>(out.toByteArray(), httpHeaders, org.springframework.http.HttpStatus.OK);

        } catch (Exception ex) {
            logger.info("Problem in EmployeeLoanServiceImpl -> downloadLoanForSupervisorReport :: ", ex);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error".getBytes());
        } finally {
            try { workbook.close(); out.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadDailyAdvancePaymentReport(Long org_id, Date fromDate, Date toDate) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        try {
            List<LinkedCaseInsensitiveMap> list = employeeLoanRepo.getDailyAdvancePaymentReport(org_id, fromDate, toDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String fromStr = sdf.format(fromDate);
            String toStr = sdf.format(toDate);

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Daily Advance Payments");

            // Bold font
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            org.apache.poi.ss.usermodel.CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);

            org.apache.poi.ss.usermodel.CellStyle boldRightStyle = workbook.createCellStyle();
            boldRightStyle.setFont(boldFont);
            boldRightStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.RIGHT);

            int rowNum = 0;
            int totalCols = 5; // S.No, AcDate, Emp Code, Emp Name, Amount

            // Title row
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Daily Advance Payments From " + fromStr + " and " + toStr);
            titleCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, totalCols - 1));

            // Header row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"S.No", "Date", "Emp Code", "Emp Name", "Amount"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(boldStyle);
            }

            // Group data by department_name (preserving order)
            Map<String, List<LinkedCaseInsensitiveMap>> grouped = new LinkedHashMap<>();
            for (LinkedCaseInsensitiveMap row : list) {
                String dept = row.get("department_name") != null ? row.get("department_name").toString() : "";
                grouped.computeIfAbsent(dept, k -> new ArrayList<>()).add(row);
            }

            int sNo = 1;
            double grandTotal = 0;

            for (Map.Entry<String, List<LinkedCaseInsensitiveMap>> entry : grouped.entrySet()) {
                String deptName = entry.getKey();
                List<LinkedCaseInsensitiveMap> deptRows = entry.getValue();

                // Department header row
                org.apache.poi.ss.usermodel.Row deptRow = sheet.createRow(rowNum++);
                org.apache.poi.ss.usermodel.Cell deptCell = deptRow.createCell(0);
                deptCell.setCellValue(deptName);
                deptCell.setCellStyle(boldStyle);

                double deptTotal = 0;

                // Data rows
                for (LinkedCaseInsensitiveMap data : deptRows) {
                    org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(rowNum++);
                    dataRow.createCell(0).setCellValue(sNo++);
                    dataRow.createCell(1).setCellValue(data.get("ac_date") != null ? data.get("ac_date").toString() : "");
                    dataRow.createCell(2).setCellValue(data.get("emp_code") != null ? data.get("emp_code").toString() : "");
                    dataRow.createCell(3).setCellValue(data.get("emp_name") != null ? data.get("emp_name").toString() : "");
                    double amount = data.get("amount") != null ? Double.parseDouble(data.get("amount").toString()) : 0;
                    dataRow.createCell(4).setCellValue(amount);
                    deptTotal += amount;
                }

                grandTotal += deptTotal;

                // Branch Total row
                org.apache.poi.ss.usermodel.Row branchTotalRow = sheet.createRow(rowNum++);
                org.apache.poi.ss.usermodel.Cell branchNameCell = branchTotalRow.createCell(2);
                branchNameCell.setCellValue(deptName);
                branchNameCell.setCellStyle(boldStyle);
                org.apache.poi.ss.usermodel.Cell branchLabelCell = branchTotalRow.createCell(3);
                branchLabelCell.setCellValue("Branch Total");
                branchLabelCell.setCellStyle(boldRightStyle);
                org.apache.poi.ss.usermodel.Cell branchAmtCell = branchTotalRow.createCell(4);
                branchAmtCell.setCellValue(deptTotal);
                branchAmtCell.setCellStyle(boldStyle);
            }

            // Grand Total row
            org.apache.poi.ss.usermodel.Row grandTotalRow = sheet.createRow(rowNum);
            org.apache.poi.ss.usermodel.Cell grandLabelCell = grandTotalRow.createCell(3);
            grandLabelCell.setCellValue("Grand Total");
            grandLabelCell.setCellStyle(boldRightStyle);
            org.apache.poi.ss.usermodel.Cell grandAmtCell = grandTotalRow.createCell(4);
            grandAmtCell.setCellValue(grandTotal);
            grandAmtCell.setCellStyle(boldStyle);

            for (int i = 0; i < totalCols; i++) {
                sheet.setColumnWidth(i, 256 * 22);
            }

            workbook.write(out);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            httpHeaders.setContentDisposition(org.springframework.http.ContentDisposition.builder("attachment").filename("DailyAdvancePaymentReport.xlsx").build());
            return new ResponseEntity<>(out.toByteArray(), httpHeaders, org.springframework.http.HttpStatus.OK);

        } catch (Exception ex) {
            logger.info("Problem in EmployeeLoanServiceImpl -> downloadDailyAdvancePaymentReport :: ", ex);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error".getBytes());
        } finally {
            try { workbook.close(); out.close(); } catch (Exception ignored) {}
        }
    }
}

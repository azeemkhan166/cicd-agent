/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.config.JWTAuthenticationFilter;
import com.realcoderz.model.WorkerLoan;
import com.realcoderz.repository.WorkerLoanRepository;
import com.realcoderz.repository.employeeDetailsRepository;
import com.realcoderz.service.WorkerLoanService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Mayank
 */
@Service
public class WorkerLoanServiceImpl implements WorkerLoanService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(EmployeeLoanServiceImpl.class);

    @Autowired
    private WorkerLoanRepository workerLoanRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JWTAuthenticationFilter authenticationFilter;

    @Value("${reimburshment_url}")
    private String reimburshment_url;

    @Autowired
    private employeeDetailsRepository empDetailsRepo;

    @Override
    public Map getLoanById(Long emp_id, Long org_id, Long loan_id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> worker_loan = workerLoanRepo.getEmployeeLoanById(emp_id, org_id, loan_id);
            resultMap.clear();
            resultMap.put("list", worker_loan);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in WorkerLoanServiceImpl -> getLoanById :: ", ex);
        }
        return resultMap;

    }

    @Override
    public Map saveLoan(Map map) {
        Map resultMap = new HashMap<>();
        try {
            WorkerLoan worker_loan = mapper.convertValue(map, WorkerLoan.class);
            worker_loan.setLoan_requested_date(new Date());

            if (worker_loan != null) {
                workerLoanRepo.save(worker_loan);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Worker Loan");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in WorkerLoanServiceImpl -> saveLoan :: ", ex);
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
            List<LinkedCaseInsensitiveMap> employee_loan = workerLoanRepo.getLoanByOrgId(org_id);
            resultMap.clear();
            resultMap.put("list", employee_loan);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in WorkerLoanServiceImpl -> getLoanByOrgId :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map remainingAmount(Map map) throws NullPointerException {
        Map resultMap = new HashMap<>();
        WorkerLoan worker_loan = new WorkerLoan();
        Map month = new HashMap<>();
        double remainingAmount = 0;
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = workerLoanRepo.getEmployeeLoan(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()));
            List<WorkerLoan> loanList = new ArrayList<>();
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
                            worker_loan = new WorkerLoan();
                            worker_loan.setEmployee_loan_id(l.get("employee_loan_id") != null ? Long.parseLong(l.get("employee_loan_id").toString()) : null);
                            worker_loan.setOrganization_id(l.get("organization_id") != null ? Long.parseLong(l.get("organization_id").toString()) : null);
                            worker_loan.setDescription(l.get("description") != null ? l.get("description").toString() : null);
                            worker_loan.setEmployee_name(l.get("employee_name") != null ? l.get("employee_name").toString() : null);
                            worker_loan.setLoan_amount(l.get("amount") != null ? Double.parseDouble(l.get("loan_amount").toString()) : 0);
                            worker_loan.setEmployee_id(l.get("employee_id") != null ? Long.parseLong(l.get("employee_id").toString()) : 0);
                            worker_loan.setInstallment_amount(l.get("installment_amount") != null ? Double.parseDouble(l.get("installment_amount").toString()) : 0);
                            worker_loan.setLoan_amount(l.get("loan_amount") != null ? Double.parseDouble(l.get("loan_amount").toString()) : 0);
                            worker_loan.setLoan_approved_amount(l.get("approved_loan") != null ? Double.parseDouble(l.get("approved_loan").toString()) : 0);
                            worker_loan.setLoan_approved_date(l.get("loan_approved_date") != null ? (Date) l.get("loan_approved_date") : null);
                            worker_loan.setLoan_requested_date(l.get("loan_requested_date") != null ? (Date) l.get("loan_requested_date") : null);
                            worker_loan.setStart_date((Date) l.get("start_date"));
                            worker_loan.setHr_status(l.get("status") != null ? l.get("status").toString() : null);
                            worker_loan.setTenure(month.get("tenure") != null ? (int) Double.parseDouble(month.get("tenure").toString()) : null);
                            worker_loan.setAmount_repaid(amount_repaid);
                            worker_loan.setRemaining_amount(remainingAmount);
                            loanList.add(worker_loan);
                        }
                        workerLoanRepo.saveAll(loanList);
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
            List<LinkedCaseInsensitiveMap> employee_loan = workerLoanRepo.getLoanForSupervisor(org_id);
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
    public Map getWorkerLoan(Long org_id, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> employee_loan = workerLoanRepo.getWorkerLoan(org_id);
//            JSONObject json = new JSONObject();
//            json.put("id", org_id);
//            String encryptedTest = EncryptDecryptUtils.encrypt(json.toString());
//            HttpHeaders header = new HttpHeaders();
//            String bearerToken = authenticationFilter.getJwtFromRequest(request);
//            header.setBearerAuth(bearerToken);
//            header.setContentType(MediaType.TEXT_PLAIN);
//            HttpEntity<String> entity = new HttpEntity<>(encryptedTest, header);
//
//            Map restResp = restTemplate.exchange(reimburshment_url + "/users/getAllWorker", HttpMethod.POST, entity, HashMap.class).getBody();
//            Map datares = mapper.readValue(EncryptDecryptUtils.decrypt(restResp.get("data").toString()), LinkedCaseInsensitiveMap.class);
//            System.out.println("datares"+datares);
//            List<LinkedHashMap> workers_list=(List<LinkedHashMap>) datares.get("list");
            // List<LinkedHashMap> workers=workers_list.stream().filter(w->w.get("employeeType").toString().equalsIgnoreCase("Worker")).collect(Collectors.toList());
            List<LinkedCaseInsensitiveMap> workers_list = empDetailsRepo.getWorkerEmployeeList(org_id);
            resultMap.clear();
            resultMap.put("workers", workers_list);
            resultMap.put("list", employee_loan);
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
    public Map getWorkerLoanBySupervisor(Long org_id, Long supervisorId) {
        Map resultMap = new HashMap<>();
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

            List<LinkedCaseInsensitiveMap> employee_loan = workerLoanRepo.getWorkerLoan(org_id);
            List<LinkedCaseInsensitiveMap> workers_list = empDetailsRepo.getWorkerEmployeeList(org_id);
            System.out.println("worker employeeListResp.get(\"list\") ------------- " + employeeListResp.get("list"));
            System.out.println("workers_list ------------- " + workers_list);
            
            List<LinkedCaseInsensitiveMap> filteredEmpList = workers_list;

            if (employeeListResp != null && employeeListResp.get("list") != null) {

                List<Map<String, Object>> apiEmpList
                        = (List<Map<String, Object>>) employeeListResp.get("list");

                // Extract employee IDs
                Set<Long> allowedEmpIds = apiEmpList.stream()
                        .map(emp -> Long.valueOf(emp.get("employeeId").toString()))
                        .collect(Collectors.toSet());

                // Filter empList
                filteredEmpList = workers_list.stream()
                        .filter(emp -> {
                            Object idObj = emp.get("employeeId");
                            return idObj != null
                                    && allowedEmpIds.contains(Long.valueOf(idObj.toString()));
                        })
                        .collect(Collectors.toList());
            }

            
            resultMap.clear();
            resultMap.put("workers", filteredEmpList);
            resultMap.put("list", employee_loan);
            resultMap.put("status", "success");
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanServiceImpl -> getEmployeeLoan :: ", ex);
        }
        return resultMap;

    }

}

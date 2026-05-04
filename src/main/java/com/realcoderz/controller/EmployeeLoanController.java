/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.EmployeeLoanService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mayank
 */
@RestController
@RequestMapping("/employeeLoan")
public class EmployeeLoanController {

    static final Logger logger = LoggerFactory.getLogger(EmployeeLoanController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private EmployeeLoanService employeeLoanService;

    @PostMapping("/getLoanById")
    public Map getLoanById(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.getLoanById(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Long.parseLong(map.get("loan_id").toString()));
            logger.info("Employee get the loan successfully with employeeId " + map.get("employee_id"));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getLoanById :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/getEmployeeLoan")
    public Map getEmployeeLoan(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.get("bySupervisor") != null && Boolean.parseBoolean(map.get("bySupervisor").toString())) {
                resultMap = employeeLoanService.getFulltimeEmployeeLoanBySupervisor(Long.parseLong(map.get("organization_id").toString()), Long.parseLong(map.get("supervisorId").toString()));
            } else {
                resultMap = employeeLoanService.getEmployeeLoan(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()));

            }
            logger.info("Employee get the loan successfully with employeeId " + map.get("employee_id"));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getEmployeeLoan :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/getLoanByOrgId")
    public Map getEmployeeLoanByOrgId(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.getLoanByOrgId(Long.parseLong(map.get("organization_id").toString()));
            logger.info("Accountant get the loan successfully");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getEmployeeLoanByOrgId :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/getLoanForSupervisor")
    public Map getEmployeeLoanForSupervisor(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.getLoanForSupervisor(Long.parseLong(map.get("organization_id").toString()));
            logger.info("Supervisor get the loan successfully");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getEmployeeLoanForSupervisor :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/saveLoan")
    public Map saveEmployeeLoan(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.saveLoan(map);
            logger.info("Employee request the loan with employeeId " + map.get("employee_id"));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> saveEmployeeLoan :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/calculateTenure")
    public Map calculateTenure(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.calculateMonths(Double.parseDouble(map.get("loan_amount").toString()), Double.parseDouble(map.get("monthly_installment").toString()));
            logger.info("Calculate Tenure of Loan Amount Successfully with employeeId " + map.get("employee_id"));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> calculateTenure :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/updateLoanStatus")
    public Map updateLoanStatus(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.updateLoanStatus(
                    Long.parseLong(map.get("employee_loan_id").toString()),
                    map.get("loan_status").toString());
            logger.info("Loan status updated for loan id " + map.get("employee_loan_id"));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> updateLoanStatus :: ", ex);
        }
        return resultMap;
    }

    @GetMapping("/downloadLoanForSupervisorReport")
    public ResponseEntity<byte[]> downloadLoanForSupervisorReport(@RequestParam String organizationId) {
        try {
            return employeeLoanService.downloadLoanForSupervisorReport(Long.parseLong(organizationId));
        } catch (Exception ex) {
            logger.info("Problem in EmployeeLoanController -> downloadLoanForSupervisorReport :: ", ex);
            return ResponseEntity.status(500).body("Internal Server Error".getBytes());
        }
    }

    @GetMapping("/downloadDailyAdvancePaymentReport")
    public ResponseEntity<byte[]> downloadDailyAdvancePaymentReport(
            @RequestParam String organizationId,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date from = sdf.parse(fromDate);
            Date to = sdf.parse(toDate);
            return employeeLoanService.downloadDailyAdvancePaymentReport(Long.parseLong(organizationId), from, to);
        } catch (Exception ex) {
            logger.info("Problem in EmployeeLoanController -> downloadDailyAdvancePaymentReport :: ", ex);
            return ResponseEntity.status(500).body("Internal Server Error".getBytes());
        }
    }

    @PostMapping("/getFullTimeEmployeeLoan")
    public Map getFulltimeEmployeeLoan(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = employeeLoanService.getFulltimeEmployeeLoan(Long.parseLong(map.get("organization_id").toString()));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getFulltimeEmployeeLoan :: ", ex);
        }
        return resultMap;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.EmployeeLoanService;
import com.realcoderz.service.WorkerLoanService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mayank
 */
@RestController
@RequestMapping("/workerLoan")
public class WorkerLoanController {

    static final Logger logger = LoggerFactory.getLogger(EmployeeLoanController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WorkerLoanService workerLoanService;

    @PostMapping("/getLoanById")
    public Map getLoanById(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = workerLoanService.getLoanById(Long.parseLong(map.get("employee_id").toString()), Long.parseLong(map.get("organization_id").toString()), Long.parseLong(map.get("loan_id").toString()));
            logger.info("HR get the loan successfully with employeeId " + map.get("employee_id"));
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getLoanById :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/getWorkerLoan")
    public Map getWorkerLoan(@RequestBody String data, HttpServletRequest request) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if (map.get("bySupervisor") != null && Boolean.parseBoolean(map.get("bySupervisor").toString())) {
                resultMap = workerLoanService.getWorkerLoanBySupervisor(Long.parseLong(map.get("organization_id").toString()), Long.parseLong(map.get("supervisorId").toString()));
            } else {
                resultMap = workerLoanService.getWorkerLoan(Long.parseLong(map.get("organization_id").toString()), request);
            }
            logger.info("Supervisor get the loan successfully");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in WorkerLoanController -> getWorkerLoan :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/getLoanByOrgId")
    public Map getEmployeeLoanByOrgId(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = workerLoanService.getLoanByOrgId(Long.parseLong(map.get("organization_id").toString()));
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
            resultMap = workerLoanService.getLoanForSupervisor(Long.parseLong(map.get("organization_id").toString()));
            logger.info("Supervisor get the loan successfully");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in EmployeeLoanController -> getEmployeeLoanForSupervisor :: ", ex);
        }
        return resultMap;
    }

    @PostMapping("/saveLoan")
    public Map saveWorkerLoan(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = workerLoanService.saveLoan(map);
            logger.info("Supervisor request the loan with employeeId " + map.get("employee_id"));
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
            resultMap = workerLoanService.calculateMonths(Double.parseDouble(map.get("loan_amount").toString()), Double.parseDouble(map.get("monthly_installment").toString()));
            logger.info("Calculate Tenure of Loan Amount Successfully");
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in WorkerLoanController -> calculateTenure :: ", ex);
        }
        return resultMap;
    }

}

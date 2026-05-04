/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Mayank
 */
public interface WorkerLoanService {

    public Map getLoanById(Long emp_id, Long org_id, Long loan_id);

    public Map getWorkerLoan(Long org_id, HttpServletRequest request);

    public Map saveLoan(Map map);

    public Map calculateMonths(Double loanAmount, Double monthlyInstallment);

    public Map getLoanByOrgId(Long org_id);

    public Map remainingAmount(Map map);

    public Map getLoanForSupervisor(Long org_id);

    public Map getWorkerLoanBySupervisor(Long org_id, Long supervisorId);

}

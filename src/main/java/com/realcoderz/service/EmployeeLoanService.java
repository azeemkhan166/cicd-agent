/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Date;
import java.util.Map;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author Mayank
 */
public interface EmployeeLoanService {

    public Map getLoanById(Long emp_id, Long org_id, Long loan_id);

    public Map getEmployeeLoan(Long emp_id, Long org_id);

    public Map saveLoan(Map map);

    public Map calculateMonths(Double loanAmount, Double monthlyInstallment);

    public Map getLoanByOrgId(Long org_id);

    public Map remainingAmount(Map map);

    public Map getLoanForSupervisor(Long org_id);

    public Map getFulltimeEmployeeLoan(Long org_id);

    public Map getFulltimeEmployeeLoanBySupervisor(Long org_id, Long supervisorId);

    public ResponseEntity<byte[]> downloadDailyAdvancePaymentReport(Long org_id, Date fromDate, Date toDate);

    public Map updateLoanStatus(Long loanId, String loanStatus);

    public ResponseEntity<byte[]> downloadLoanForSupervisorReport(Long org_id);

}

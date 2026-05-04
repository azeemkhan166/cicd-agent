/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.WorkerLoan;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
public interface WorkerLoanRepository extends JpaRepository<WorkerLoan, Long> {

    @Query(value = "select employee_loan_id as employee_loan_id, employee_id as employee_id,remaining_amount as remaining_amount, loan_approved_amount as approved_loan, employee_name as employee_name, organization_id as organization_id, installment_amount as installment_amount, loan_amount as loan_amount, description as description, tenure as tenure, start_date as start_date, hr_status as status, loan_approved_date as loan_approved_date, loan_requested_date as loan_requested_date,amount_repaid as amount_repaid from worker_loan where employee_id=?1 and organization_id=?2", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getEmployeeLoan(Long employee_id, Long organization_id);
    
    @Query(value = "select employee_loan_id as employee_loan_id, employee_id as employee_id,remaining_amount as remaining_amount, loan_approved_amount as approved_loan, employee_name as employee_name, organization_id as organization_id, installment_amount as installment_amount, loan_amount as loan_amount, description as description, tenure as tenure, start_date as start_date, hr_status as hr_status, loan_approved_date as loan_approved_date, loan_requested_date as loan_requested_date,amount_repaid as amount_repaid from worker_loan where organization_id=?1", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getWorkerLoan(Long organization_id);

    @Query(value = "select employee_loan_id as employee_loan_id, employee_name as employee_name,employee_id as employee_id,remaining_amount as remaining_amount, loan_approved_amount as approved_loan, installment_amount as installment_amount, amount_repaid as amount_repaid, loan_amount as loan_amount, description as description, tenure as tenure, start_date as start_date, supervisor_status as status, loan_requested_date as loan_requested_date from employee_loan where organization_id=?1", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getLoanByOrgId(Long organization_id);

    @Query(value = "select * from employee_loan where employee_id=?1 and organization_id=?2 and supervisor_status='Approved' and start_date<=?3", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> calculateAmountRepaid(Long employee_id, Long organization_id, Date date);

    @Query(value = "select remaining_amount as remaining_amount, installment_amount as installment_amount from employee_loan where employee_id=?1 and organization_id=?2 and month(start_date)<=?3 and year(start_date)<=?4 and supervisor_status='Approved' order by start_date desc limit 1", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getLoanForRunPayroll(Long employee_id, Long organization_id, int month, int year);

    @Query(value = "select employee_name as employee_name,employee_id as employee_id,remaining_amount as remaining_amount, loan_approved_amount as approved_loan, installment_amount as installment_amount, amount_repaid as amount_repaid, loan_amount as loan_amount, description as description, tenure as tenure, start_date as start_date, supervisor_status as status, loan_requested_date as loan_requested_date, employee_loan_id as employee_loan_id, supervisor_status as supervisor_status from employee_loan where organization_id=?1 and accountant_status='Approved'", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getLoanForSupervisor(Long organization_id);

    @Query(value = "select remaining_amount as remaining_amount, installment_amount as installment_amount from worker_loan where employee_id=?1 and organization_id=?2 and hr_status='Approved' and month(start_date)<=?3 and year(start_date)<=?4", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getLoanForSalaryBreakup(Long employee_id, Long organization_id, int month, int year);
    
     @Query(value = "select employee_loan_id as employee_loan_id, employee_id as employee_id,remaining_amount as remaining_amount, loan_approved_amount as approved_loan, employee_name as employee_name, organization_id as organization_id, installment_amount as installment_amount, loan_amount as loan_amount, description as description, hr_status as hr_status, tenure as tenure, start_date as start_date, loan_approved_date as loan_approved_date, loan_requested_date as loan_requested_date,amount_repaid as amount_repaid from worker_loan where employee_id=?1 and organization_id=?2 and employee_loan_id=?3", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getEmployeeLoanById(Long employee_id, Long organization_id, Long loan_id);

    @Query(nativeQuery = true,value="SELECT remaining_amount FROM worker_loan where employee_id=?1 and hr_status='Approved' order by employee_loan_id desc limit 1")
    public LinkedCaseInsensitiveMap getRemainigLoan(Long employee_id);
    
    @Query(nativeQuery = true,value="Select ed.employee_id,ed.employee_code,ed.email,ed.name,hr_status,el.start_date from worker_loan el left join employee_details ed on el.employee_id=ed.employee_id where el.organization_id=:organizationId and hr_status in (:status)")
    public List<LinkedCaseInsensitiveMap> getWorkerLoanForPendingAction(@Param("organizationId") Long organizationId,@Param("status") List<String> status);
}

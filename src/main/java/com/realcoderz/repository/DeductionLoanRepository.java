/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.DeductionLoan;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Repository
public interface DeductionLoanRepository extends JpaRepository<DeductionLoan, Long>{
    
    @Query(nativeQuery = true, value = "select deduction_id,deduction_name from deduction where organization_id=?1 and employee_type='full time' and supervisor_status='Approved' and loan_flag is true")
    public List<LinkedCaseInsensitiveMap> getDeductionList(Long org_id);
    
    @Query(nativeQuery = true,value = "select * from deduction_loan where organization_id=:org_id and (employee_name LIKE CONCAT(:search_string, '%') or loan_type LIKE CONCAT(:search_string, '%'))")
    public List<DeductionLoan> getAllDeductionLoanList (@Param("org_id") Long org_id, @Param("search_string") String search_string);
    
//    @Query(nativeQuery = true,value = "select  * from deduction_loan where supervisor_status='Approved' and employee_id=?1 and month(start_date)<=?2 and year(start_date)<=?3")
//    public List<DeductionLoan> getEmployeeDeductionLoan(Long employee_id,int month, int year);
    
    @Query(nativeQuery = true, value = "SELECT * \n"
            + "FROM deduction_loan \n"
            + "WHERE supervisor_status = 'Approved' \n"
            + "  AND employee_id = ?1 \n"
            + "  AND start_date <= LAST_DAY(STR_TO_DATE(CONCAT(?3, '-', ?2, '-01'), '%Y-%m-%d'))")
    public List<DeductionLoan> getEmployeeDeductionLoan(Long employee_id, int month, int year);
    
    @Query(nativeQuery = true,value = "select * from deduction_loan where deduction_loan_id IN(?1)")
    public List<DeductionLoan> getListByIds(List<Long> id);
    
    @Query(nativeQuery = true,value = "select * from deduction_loan where employee_id=?1")
    public List<DeductionLoan> getAllDeductionLoanOfEmployee(Long employee_id);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update deduction_loan set supervisor_status=?1 where deduction_loan_id=?2")
    public void approvedOrRejectLaon(String supervisor_status,Long deduction_loan_id);
    
    @Query(nativeQuery = true,value="Select ed.employee_id,ed.employee_code,ed.email,ed.name,accountant_status,supervisor_status,el.start_date from deduction_loan el left join employee_details ed on el.employee_id=ed.employee_id where el.organization_id=:organizationId and accountant_status='Approved' and supervisor_status in (:status)")
    public List<LinkedCaseInsensitiveMap> employeeDeductionsLoans(@Param("organizationId") Long organizationId,@Param("status") List<String> status); 

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update deduction_loan set remaining_amount=?1,tenure=?2 where deduction_loan_id=?3")
    public void updateLoanWhileClosedLoan(Double remaining_loan,Double tenure,Long deduction_loan_id);

    @Query(nativeQuery = true,value = "SELECT employee_id,deduction_id,SUM(deduction_payable_amount) AS total_deduction FROM employee_deduction WHERE deduction_id IN(?1) GROUP BY employee_id, deduction_id")
    public List<LinkedCaseInsensitiveMap> getDeductionLoanOfEachEmployee (@Param("deductionId") Set<Long> deductionId);
    

    @Query(nativeQuery = true,value = "SELECT * FROM loan_closer WHERE organization_id=?1")
    public List<LinkedCaseInsensitiveMap> getLoanCloser (@Param("deductionId") Long organization_id);
    
}

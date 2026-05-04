/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.EmployeeAllowance;
import com.realcoderz.model.OtherAllowances;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Astha & Mayank
 */
@Repository
public interface OtherAllowancesRepository extends JpaRepository<OtherAllowances, Long> {

    @Query(nativeQuery = true, value = " select amount, name ,payable_amount from other_allowances  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> fetchDataInPdf(Long employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = " select amount, name ,payable_amount,month ,year from other_allowances  where salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeOtherAllowances(Long salary_break_up_id);

    @Query(nativeQuery = true, value = " select amount, name ,payable_amount,month ,year from other_allowances  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeOtherAllowancesByMonth(Long employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT name, CAST(SUM(payable_amount) AS UNSIGNED) AS payable_amount FROM other_allowances WHERE  employee_id =?1 AND organization_id =?2 AND (month BETWEEN 4 AND ?3 AND year =?4) GROUP BY name")
    public List<LinkedCaseInsensitiveMap> getOtherAllowanceOFCuurentAndPreviousMonth(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT name, CAST(SUM(payable_amount) AS UNSIGNED) AS payable_amount FROM other_allowances WHERE  employee_id =?1 AND organization_id =?2 AND (month BETWEEN 4 AND 12 AND year =?3) GROUP BY name")
    public List<LinkedCaseInsensitiveMap> getOtherAllowanceOFPreviousYear(Long employee_id, Long organization_id, int year);

    @Query(nativeQuery = true, value = "SELECT name,CAST(SUM(payable_amount) AS UNSIGNED) AS payable_amount FROM other_allowances WHERE  employee_id =?1 AND organization_id =?2 AND (month BETWEEN 1 AND ?3 AND year =?4) GROUP BY name")
    public List<LinkedCaseInsensitiveMap> getOtherAllowanceOFJanToMarch(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT payable_amount FROM other_allowances where salary_breakup_id=?")
    public LinkedCaseInsensitiveMap employeeOtherAllowances(Long salaryBreakupId);

    @Query(nativeQuery = true, value = " select amount as allowance_amount, name as allowance_name,payable_amount as allowance_payable_amount from other_allowances  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getOtherAllowane(Long employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "Select * from other_allowances where salary_breakup_id=? ")
    public OtherAllowances employeeOtherAllowancesById(Long salarybreaupId);

    @Query(nativeQuery = true, value = "select * from other_allowances where employee_id In(?1) and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getOtherAllowances(List<Long> employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select * from other_allowances where organization_id=?1 and month=?2 and year=?3")
    public List<LinkedCaseInsensitiveMap> getOtherAllowancesForSalaryReport(Long organization_id, long month, long year);

    @Query(nativeQuery = true, value = "select amount,employee_id,name,salary_breakup_id ,other_allowances_id from other_allowances where salary_breakup_id IN(?1)")
    public List<LinkedCaseInsensitiveMap> getStandardSalary(List<Long> salarybreaupId);

    @Query(nativeQuery = true, value = "SELECT name, CAST(SUM(payable_amount) AS UNSIGNED) AS payable_amount FROM other_allowances WHERE  employee_id =?1 AND organization_id =?2 AND  ((year =?3 AND month BETWEEN 4 AND 12) OR (year =?4 AND month BETWEEN 1 AND 3)) GROUP BY name")
    public List<LinkedCaseInsensitiveMap> getOtherAllowancesForFoem16(Long employee_id, Long organization_id, int year, int nextYear);

    @Query(nativeQuery = true, value = "select * from other_allowances where salary_breakup_id IN(?1)")
    public List<OtherAllowances> otherAllowanceForSaved(List<Long> salarybreaupId);

    @Query(
            nativeQuery = true,
            value = "SELECT * "
            + "FROM other_allowances "
            + "WHERE organization_id = ?1 "
            + "AND STR_TO_DATE(CONCAT(year, '-', month, '-01'), '%Y-%m-%d') "
            + "    BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> getOtherAllowancesForSalaryExcelReport(
            Long organizationId,
            String fromDate, // e.g. "2023-03-31"
            String toDate // e.g. "2024-02-28"
    );

    @Query(
            nativeQuery = true,
            value = "SELECT * "
            + "FROM other_allowances "
            + "WHERE organization_id = ?1 "
            + "AND employee_id = ?4 " // Added employee_id check
            + "AND STR_TO_DATE(CONCAT(year, '-', month, '-01'), '%Y-%m-%d') "
            + "    BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> getOtherAllowancesForSalaryExcelReportByEmpId(
            Long organizationId,
            String fromDate,
            String toDate,
            Long employeeId // Added parameter
    );

}

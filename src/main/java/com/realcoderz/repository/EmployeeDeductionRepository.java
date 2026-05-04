/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.EmployeeDeduction;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Astha
 */
@Repository
public interface EmployeeDeductionRepository extends JpaRepository<EmployeeDeduction, Long> {

    @Query(nativeQuery = true, value = "SELECT distinct ed.deduction_id,d.deduction_name,ed.deduction_amount FROM employee_deduction ed , deduction d where d.deduction_id=ed.deduction_id and ed.employee_id=? and ed.organization_id=?")
    public List<LinkedCaseInsensitiveMap> deductions(Long employee_id, Long organization_id);

    @Query(nativeQuery = true, value = "select ed.deduction_id as deduction_id, ed.deduction_amount, ed.deduction_payable_amount,d.deduction_name, ed.ytd_deduction from employee_deduction ed , deduction d where d.deduction_id=ed.deduction_id and ed.employee_id=?1 and ed.organization_id=?2 and ed.month=?3 and ed.year=?4 and ed.employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetchDataInPdf(Long employeeid, Long organization_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select consultant_deduction_name,consultant_deduction_amount,consultnat_deduction_payable_amount from employee_deduction  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetchDataInPdfforInternDeduction(Long employeeid, Long organization_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select consultant_deduction_name,consultant_deduction_amount,consultnat_deduction_payable_amount from employee_deduction  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetchListDataInPdfforInternDeduction(Long employeeid, Long organization_id, int month, int year, String employee_type);

    //       this repo query  use in tax
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE employee_deduction  SET deduction_payable_amount =:allowance_payable_amount WHERE deduction_id =:deduction_id and employee_id =:employee_id and organization_id=:organization_id and month=:month and year=:year")
    void updateMonthlyTax(@Param("allowance_payable_amount") Double allowance_payable_amount, @Param("deduction_id") Long deduction_id, @Param("employee_id") Long employee_id, @Param("organization_id") Long organization_id, @Param("month") Integer month, @Param("year") Integer year);

    @Query(nativeQuery = true, value = "select ed.deduction_id as deduction_id, ed.deduction_amount,ed.deduction_payable_amount,d.deduction_name,dt.deduction_type as type_of_deduction ,ed.deduction_template_id, ed.employer_percentage,ed.month, ed.year from employee_deduction ed left join deduction d on d.deduction_id=ed.deduction_id left join deduction_template dt on ed.deduction_template_id=dt.deduction_template_id where ed.salary_breakup_id=?;")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeDeductions(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "select consultant_deduction_amount, consultant_deduction_name, consultnat_deduction_payable_amount from employee_deduction where salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedConsultantDeductions(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "select ed.deduction_id as deduction_id, ed.deduction_amount, ed.deduction_payable_amount,d.deduction_name, ed.month, ed.year from employee_deduction ed, deduction d where d.deduction_id=ed.deduction_id and ed.employee_id=?1 and ed.organization_id=?2 and ed.month=?3 and ed.year=?4")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeDeductionsByMonth(Long employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select consultant_deduction_amount, consultant_deduction_name, consultnat_deduction_payable_amount from employee_deduction where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getSavedConsultantDeductionsByMonth(Long employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT CAST(SUM(ed.deduction_payable_amount) AS UNSIGNED) AS deduction_payable_amount,ed.employee_id,ed.deduction_id,d.deduction_name FROM employee_deduction ed LEFT JOIN deduction d ON d.deduction_id = ed.deduction_id WHERE ed.employee_id =?1 AND ed.organization_id =?2 AND (ed.month BETWEEN 4 AND ?3 AND ed.year =?4) GROUP BY ed.deduction_id")
    public List<LinkedCaseInsensitiveMap> getYTDDeductionOFCurrentAndPriviousMonth(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT CAST(SUM(ed.deduction_payable_amount) AS UNSIGNED) AS deduction_payable_amount,ed.employee_id,ed.deduction_id,d.deduction_name FROM employee_deduction ed LEFT JOIN deduction d ON d.deduction_id = ed.deduction_id WHERE ed.employee_id =?1 AND ed.organization_id =?2 AND (ed.month BETWEEN 4 AND 12 AND ed.year =?3) GROUP BY ed.deduction_id")
    public List<LinkedCaseInsensitiveMap> getYTDDeductionOFPreviousYear(Long employee_id, Long organization_id, int year);

    @Query(nativeQuery = true, value = "SELECT CAST(SUM(ed.deduction_payable_amount) AS UNSIGNED) AS deduction_payable_amount,ed.employee_id,ed.deduction_id,d.deduction_name FROM employee_deduction ed LEFT JOIN deduction d ON d.deduction_id = ed.deduction_id WHERE ed.employee_id =?1 AND ed.organization_id =?2 AND (ed.month BETWEEN 1 AND ?3 AND ed.year =?4) GROUP BY ed.deduction_id")
    public List<LinkedCaseInsensitiveMap> getYTDDeductionOFJanToMarch(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT sum(deduction_payable_amount) FROM deduction d, employee_deduction e where d.deduction_name = 'Professional Tax' and e.deduction_id = d.deduction_id and e.employee_id =?1 and d.organization_id =?2 and e.year =?3")
    public String getProfessionalTax(Long employeeid, Long organizationid, int year);

    @Query(nativeQuery = true, value = "SELECT ed.deduction_amount,dt.editable,dt.deduction_template_id,consultant_deduction_name,consultnat_deduction_payable_amount,ed.employer_percentage,ed.deduction_payable_amount,ed.deduction_id,d.deduction_name,d.type_of_deduction FROM employee_deduction ed LEFT JOIN deduction d ON ed.deduction_id=d.deduction_id left join deduction_template dt on ed.deduction_template_id=dt.deduction_template_id  where salary_breakup_id=?;")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeDeduction(Long sid);

    @Query(nativeQuery = true, value = "SELECT * FROM employee_deduction where salary_breakup_id=?;")
    public List<EmployeeDeduction> employeeDeductionsBySalaryBreakupId(Long salaryBreakupId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update employee_deduction set deduction_payable_amount=? where id=?")
    public void updateIncomeTaxInEmployeeDeduction(Long dedcutionPayableAmount, Long employeeDeductionId);

    @Query(nativeQuery = true, value = "SELECT deduction_name,type_of_deduction FROM deduction where organization_id=?1 and employee_type=?2 and supervisor_status='Approved'")
    List<LinkedCaseInsensitiveMap> getTypeOfDeduction(Long organizationid, String employee_type);

    @Query(nativeQuery = true, value = "select ed.employee_id as employee_id,ed.id as id,d.type_of_deduction as deductiontype,sb.actual_day ,ed.deduction_id as deduction_id, ed.deduction_amount, ed.deduction_payable_amount,d.deduction_name,ed.month, ed.year , sb.is_esic,sb.gross_salary from employee_deduction ed join  deduction d on ed.deduction_id=d.deduction_id  join salary_break_up sb on ed.salary_breakup_id=sb.sid where d.deduction_id=ed.deduction_id and ed.employee_id IN(?1) and ed.organization_id=(?2) and ed.month=(?3) and ed.year=(?4);")
    public List<LinkedCaseInsensitiveMap> getDeductionsForUpdate(List<Long> employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select * from employee_deduction where employee_id IN(?1) and organization_id=?2 and month=?3 and year=?4")
    public List<EmployeeDeduction> getDeductionsForSaveExac(List<Long> employeeid, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select ed.employee_id as employee_id,ed.id as id,d.type_of_deduction as deductiontype, ed.deduction_id as deduction_id, ed.deduction_amount, ed.deduction_payable_amount,d.deduction_name, ed.month, ed.year from employee_deduction ed, deduction d where d.deduction_id=ed.deduction_id  and ed.organization_id=?1 and ed.month=?2 and ed.year=?3 and ed.employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getDeductionForSalarySheet(Long organization_id, long month, long year, String employee_type);

    @Query(nativeQuery = true, value = "select ed.deduction_id as deduction_id,ed.id ,ed.deduction_amount,ed.employee_id, ed.deduction_payable_amount,d.deduction_name,d.type_of_deduction, ed.salary_breakup_id from employee_deduction ed, deduction d where d.deduction_id=ed.deduction_id and ed.salary_breakup_id IN(?1)")
    public List<LinkedCaseInsensitiveMap> getEmployeeDeductions(List<Long> salary_break_up_id);

    @Query(nativeQuery = true, value = "SELECT CAST(SUM(ed.deduction_payable_amount) AS UNSIGNED) AS deduction_payable_amount,ed.employee_id,ed.deduction_id,d.deduction_name FROM employee_deduction ed LEFT JOIN deduction d ON d.deduction_id = ed.deduction_id WHERE d.deduction_name='Professional Tax' and ed.employee_id =?1  AND  ((ed.year =?2 AND ed.month BETWEEN 4 AND 12) OR (ed.year =?3 AND ed.month BETWEEN 1 AND 3))   GROUP BY ed.deduction_id")
    public LinkedCaseInsensitiveMap getProfessionalTacForForm16(Long employee_id, int year, int nextYear);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from employee_deduction where deduction_id=? and salary_breakup_id=?")
    public void deleteEmployeeDeductionByDeductionId(Long deductionId, Long salaryBreaupId);

    @Query(nativeQuery = true, value = "SELECT consultant_deduction_amount as deduction_amount,consultant_deduction_name as deduction_name,consultnat_deduction_payable_amount as deduction_payable_amount,employee_id FROM employee_deduction where organization_id=?1 and month=?2 and year=?3 and employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getDeductionForSalarySheetForConsultant(Long organization_id, long month, long year, String employee_type);

    @Query(nativeQuery = true, value = "SELECT DISTINCT  consultant_deduction_name as deduction_name FROM employee_deduction where organization_id=?1 and month=?2 and year=?3 and employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getDeductionNameForSalarySheetForConsultant(Long organization_id, long month, long year, String employee_type);
    
    @Query(nativeQuery = true, value = "select ed.deduction_amount,ed.deduction_id as deductionId,ed.deduction_payable_amount ,d.deduction_name from employee_deduction ed LEFT JOIN deduction d ON ed.deduction_id=d.deduction_id where ed.salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getDeductionBySid(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "Select allowance_id from employee_deduction ed left join deduction_allowance_template_mapping datm on ed.deduction_template_id=datm.deduction_template_id where deduction_id=?")
    public List<Long> mappingAllowanceList(Long allowanceId);

    @Query(nativeQuery = true, value = "select * from employee_deduction where salary_breakup_id IN(?1)")
    public List<EmployeeDeduction> deductionForSaved(List<Long> salary_break_up_id);

    @Query(nativeQuery = true, value = "select deduction_amount,deduction_name from employee_deduction ed Left Join custom_deduction cd ON ed.deduction_id=cd.id where ed.salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> deductionForView(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "select ed.deduction_id as deduction_id, ed.deduction_amount, ed.deduction_payable_amount,d.deduction_name, ed.ytd_deduction from employee_deduction ed , custom_deduction d where d.id=ed.deduction_id and ed.salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> savedDeductionMonthly(Long salary_breakup_id);

    @Query(nativeQuery = true, value = "select ed.deduction_id as deduction_id, ROUND(SUM(ed.deduction_amount)) AS deduction_amount,ROUND(SUM(ed.deduction_payable_amount)) AS deduction_payable_amount ,d.deduction_name from employee_deduction ed , custom_deduction d where d.id=ed.deduction_id and ed.employee_id=?1 and ed.month=?2 and ed.year=?3 group by ed.deduction_id, d.deduction_name")
    public List<LinkedCaseInsensitiveMap> savedDeductionOfEmployeeMonthly(Long employeeId, int month, int year);

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    ed.employee_id AS employee_id, "
            + "    ed.id AS id, "
            + "    d.type_of_deduction AS deductiontype, "
            + "    ed.deduction_id AS deduction_id, "
            + "    ed.deduction_amount, "
            + "    ed.deduction_payable_amount, "
            + "    d.deduction_name, "
            + "    ed.month, "
            + "    ed.year "
            + "FROM "
            + "    employee_deduction ed, "
            + "    deduction d "
            + "WHERE "
            + "    d.deduction_id = ed.deduction_id "
            + "    AND ed.organization_id = ?1 "
            + "    AND ed.employee_id = ?4 " // Position changed from ?5 to ?4
            + "    AND STR_TO_DATE(CONCAT(ed.year, '-', ed.month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d') "
    // Removed: AND ed.employee_type = ?4
    )
    List<LinkedCaseInsensitiveMap> getDeductionForSalaryExcelReport(
            Long organizationId,
            String fromDate,
            String toDate,
            Long employeeId // Position changed from 5th to 4th parameter
    );
}

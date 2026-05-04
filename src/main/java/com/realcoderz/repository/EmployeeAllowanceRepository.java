/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.EmployeeAllowance;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
@Repository
public interface EmployeeAllowanceRepository extends JpaRepository<EmployeeAllowance, Long> {

    //    Fetch Allowances By Employee and Org Id
    @Query(nativeQuery = true, value = "select distinct ea.allowance_id,a.allowance_name,ea.allowance_amount,oa.name,oa.amount from allowance a,employee_allowance ea ,other_allowances oa where  oa.employee_id=ea.employee_id and a.allowance_id=ea.allowance_id and ea.employee_id=? and ea.organization_id=?")
    public List<LinkedCaseInsensitiveMap> allowances(Long employee_id, Long organization_id);

//    Get Primary Keys of Employee Allowance, Employee Deduction, Other Allowances and SalaryBreakup By Employee and OrgId
    @Query(nativeQuery = true, value = "Select ea.id as employee_allowance_id, ed.id as employee_deduction_id, oa.other_allowances_id as other_allowances_id, sb.sid as salary_break_up_id from employee_allowance ea,employee_deduction ed,other_allowances oa,salary_break_up sb where ea.employee_id=ed.employee_id and oa.employee_id=ed.employee_id and sb.employee_id=ed.employee_id and ed.employee_id=?1 and ed.organization_id=?2 and ed.month=?3 and ed.year=?4")
//     @Query(nativeQuery = true, value = "Select ea.id as employee_allowance_id, ed.id as employee_deduction_id, oa.other_allowances_id as other_allowances_id from employee_allowance ea,employee_deduction ed,other_allowances oa where ea.employee_id=ed.employee_id and oa.employee_id=ed.employee_id and ed.employee_id=? and ed.organization_id=?")
    public List<LinkedCaseInsensitiveMap> findEmployeeById(Long employee_id, Long organization_id, int month, int year);

//    @Query(nativeQuery = true, value = "select ea.allowance_id,  ROUND(ea.allowance_amount) as allowance_amount ,  ROUND(ea.allowance_payable_amount) AS allowance_payable_amount,a.allowance_name from employee_allowance ea , allowance a where a.allowance_id=ea.allowance_id and ea.employee_id=?1 and ea.organization_id=?2 and ea.month=?3 and ea.year=?4 and ea.employee_type=?5")
//    public List<LinkedCaseInsensitiveMap> fetchDataInPdf(Long employee_id, Long organization_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value
            = "SELECT ea.allowance_id,  "
            + "ROUND(ea.allowance_amount) AS allowance_amount,  "
            + "ROUND(ea.allowance_payable_amount) AS allowance_payable_amount, "
            + "a.allowance_name "
            + "FROM employee_allowance ea, allowance a "
            + "WHERE a.allowance_id = ea.allowance_id "
            + "AND ea.employee_id = ?1 "
            + "AND ea.organization_id = ?2 "
            + "AND ea.month = ?3 "
            + "AND ea.year = ?4 "
            + "AND ea.employee_type = ?5 "
            + "ORDER BY CASE "
            + "WHEN a.allowance_name = 'Basic Salary' THEN 1 "
            + "WHEN a.allowance_name = 'HRA' THEN 2 "
            + "WHEN a.allowance_name = 'LTA' THEN 3 "
            + "ELSE 4 END")
    public List<LinkedCaseInsensitiveMap> fetchDataInPdf(
            Long employee_id,
            Long organization_id,
            int month,
            int year,
            String employee_type);

    
    @Query(nativeQuery = true, value = "select consultant_allowance_name, consultant_allowance_amount,consultnat_allowance_payable_amount from employee_allowance  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public LinkedCaseInsensitiveMap fetchDataInPdfForConsultantAllowance(Long employee_id, Long organization_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select consultant_allowance_name, consultant_allowance_amount,consultnat_allowance_payable_amount from employee_allowance  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetchListDataInPdfForConsultantAllowance(Long employee_id, Long organization_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select id as employee_allowance_id from employee_allowance where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getPrimaryKeyOfEmployeeAllowance(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select id as employee_deduction_id from employee_deduction where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getPrimaryKeyOfEmployeeDeduction(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select sid as salary_breakup_id from salary_break_up where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getPrimaryKeyOfSalaryBreakUp(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select other_allowances_id as other_allowances_id from other_allowances where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getPrimaryKeyOfOtherAllowances(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = " select ea.allowance_id,ea.allowance_template_id ,ea.allowance_amount,at.ai_flag,at.minimum_working_day, ea.allowance_payable_amount,a.allowance_name, ea.month, ea.year from employee_allowance ea left join allowance a  on ea.allowance_id=a.allowance_id left join allowance_template at on ea.allowance_template_id=at.allowance_template_id where ea.salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeAllowances(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "select consultant_allowance_amount, consultant_allowance_name, consultnat_allowance_payable_amount from employee_allowance where salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> getSavedConsultantAllowances(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "select ea.allowance_id, ea.allowance_amount, ea.allowance_payable_amount,a.allowance_name, ea.month, ea.year from employee_allowance ea , allowance a where a.allowance_id=ea.allowance_id and ea.employee_id=?1 and ea.organization_id=?2 and ea.month=?3 and ea.year=?4")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeAllowancesByMonth(Long employee_id, Long organization_id, int month, int year);

//    query to get employee allowances based on month and year or salary_breakup_id when month and year is null
    @Query(nativeQuery = true, value = "SELECT ea.allowance_id, ea.allowance_amount, ea.allowance_payable_amount, a.allowance_name, ea.month, ea.year "
            + "FROM employee_allowance ea "
            + "JOIN allowance a ON a.allowance_id = ea.allowance_id "
            + "WHERE (ea.employee_id = ?1 AND ea.organization_id = ?2 "
            + "AND ((ea.month = ?3 AND ea.year = ?4) OR (?3 IS NULL AND ?4 IS NULL)) "
            + "OR ea.salary_breakup_id = ?5)")
    public List<LinkedCaseInsensitiveMap> getEmployeeAllowances(Long employee_id, Long organization_id, Integer month, Integer year, Long salary_breakup_id);

    @Query(nativeQuery = true, value = "select consultant_allowance_amount, consultant_allowance_name, consultnat_allowance_payable_amount from employee_allowance where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getSavedConsultantAllowancesByMonth(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT  CAST(SUM(ea.allowance_payable_amount) AS UNSIGNED) AS allowance_payable_amount,ea.employee_id,ea.allowance_id,a.allowance_name FROM employee_allowance ea LEFT JOIN allowance a ON a.allowance_id = ea.allowance_id WHERE ea.employee_id =?1 AND ea.organization_id =?2 AND (ea.month BETWEEN 4 AND ?3 AND ea.year =?4) GROUP BY ea.allowance_id")
    public List<LinkedCaseInsensitiveMap> getYTDAllowanceOFCurrentAndPriviousMonth(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT  CAST(SUM(ea.allowance_payable_amount) AS UNSIGNED) AS allowance_payable_amount,ea.employee_id,ea.allowance_id,a.allowance_name FROM employee_allowance ea LEFT JOIN allowance a ON a.allowance_id = ea.allowance_id WHERE ea.employee_id =?1 AND ea.organization_id =?2 AND (ea.month BETWEEN 4 AND 12 AND ea.year =?3) GROUP BY ea.allowance_id")
    public List<LinkedCaseInsensitiveMap> getYTDAllowanceOFPreviousYear(Long employee_id, Long organization_id, int year);

    @Query(nativeQuery = true, value = "SELECT  CAST(SUM(ea.allowance_payable_amount) AS UNSIGNED) AS allowance_payable_amount,ea.employee_id,ea.allowance_id,a.allowance_name FROM employee_allowance ea LEFT JOIN allowance a ON a.allowance_id = ea.allowance_id WHERE ea.employee_id =?1 AND ea.organization_id =?2 AND (ea.month BETWEEN 1 AND ?3 AND ea.year =?4) GROUP BY ea.allowance_id")
    public List<LinkedCaseInsensitiveMap> getYTDAllowanceOFJanToMarch(Long employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "SELECT ea.allowance_amount,at.editable,at.allowance_template_id,ea.allowance_id,ea.allowance_payable_amount,ea.type_of_allowance,ea.percentage,a.allowance_name FROM allowance a  LEFT JOIN employee_allowance ea  ON ea.allowance_id=a.allowance_id left join allowance_template at on ea.allowance_template_id=at.allowance_template_id  where salary_breakup_id=?;")
    public List<LinkedCaseInsensitiveMap> getSavedEployeeAllwance(Long sid);

    @Query(nativeQuery = true, value = "SELECT allowance_amount,allowance_payable_amount,employee_id,a.allowance_id,month,a.employee_type,allowance_name FROM employee_allowance ea left join allowance a on ea.allowance_id=a.allowance_id where ea.organization_id=? and ((month between 4 and 12 and year =?) or (month between 1 and 3 and year =?)) and ea.allowance_id is not null;")
    public List<LinkedCaseInsensitiveMap> alreadyPaidAllowances(Long organizationId, int startYear, int endYear);

    @Query(nativeQuery = true, value = "SELECT payable_amount as allowance_payable_amount,amount as allowance_amount,employee_id,month,name as allowance_name FROM other_allowances  where organization_id=? and ((month between 4 and 12 and year =?) or (month between 1 and 3 and year =?)) and payable_amount is not null ;")
    public List<LinkedCaseInsensitiveMap> alreadyPaidOtherAllowance(Long organizationId, int month, int year);

    @Query(value = "Select allowance_amount,allowance_payable_amount,ea.percentage,allowance_template_id,employee_id,a.allowance_id,a.employee_type,allowance_name FROM employee_allowance ea left join allowance a on ea.allowance_id=a.allowance_id  where salary_breakup_id in (:sid);", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> allOrganizationStandardAllowance(@Param("sid") List<Long> sid);

    @Query(value = "SELECT payable_amount as allowance_payable_amount,amount as allowance_amount,employee_id,month,name as allowance_name FROM other_allowances where salary_breakup_id in (:sid) and month is null and year is null;", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> allOrganizationStandardOtherAllowance(@Param("sid") List<Long> sid);

    @Query(value = "Select employeeid,tds as tdsPreviousEmployer,total_allowances,tax_slab_tpye,total_rent,income_from_previous_employer,interest_on_housing_loan_before,national_pension_scheme,pf,sec80d,sec80dd,sec80e,sec80u,sec80g,status,sec80d_type from inverstment_declaration id left join other_section_approved oc on id.declaration_id=oc.declaration_id where organizationid=? and fy_year=? and tax_slab_tpye='OldTaxSlabKey';", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> allOrganizationInvesmentDecleration(Long organizationIdt, int year);

    @Query(value = "select sb.gross_salary,sb.net_amount,sb.employee_type,sb.actual_day,sb.working_day,sb.over_time,sb.rate,sb.tds,sb.total_deduction,ea.allowance_payable_amount,ea.allowance_amount,ea.allowance_id,a.allowance_name,ed.deduction_id,ed.deduction_payable_amount,ed.deduction_amount,d.deduction_name from salary_break_up sb left join employee_allowance ea on sb.sid = ea.salary_breakup_id left join allowance a on a.allowance_id=ea.allowance_id left join employee_deduction ed on sb.sid=ed.salary_breakup_id left join deduction d on d.deduction_id=ed.deduction_id where sb.sid=? AND (ea.salary_breakup_id IS NOT NULL OR ed.salary_breakup_id IS NOT NULL );", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> emloyeeAllowancesAndDeductions(Long salaryBreakupId);

    @Query(value = "SELECT * FROM employee_allowance where salary_breakup_id=?;", nativeQuery = true)
    public List<EmployeeAllowance> employeeAllowanceBySalaryBraupId(Long salaryBreakUpId);

    @Query(value = "SELECT s.employee_id,ed.deduction_payable_amount,ed.deduction_id,ed.id,net_amount,s.month,s.year FROM salary_break_up s left join employee_deduction ed on s.sid=ed.salary_breakup_id left join deduction d on ed.deduction_id=d.deduction_id where LOWER(d.deduction_name) = LOWER('Income tax') and ed.salary_breakup_id=?;", nativeQuery = true)
    public LinkedCaseInsensitiveMap employeeNetAmountAndTds(Long salaryBreakUpId);

    @Query(nativeQuery = true, value = "SELECT allowance_amount,allowance_payable_amount,employee_id,a.allowance_id,month,a.employee_type,allowance_name FROM employee_allowance ea left join allowance a on ea.allowance_id=a.allowance_id where ea.employee_id=? and ((month between 4 and 12 and year =?) or (month between 1 and 3 and year =?)) and ea.allowance_id is not null;")
    public List<LinkedCaseInsensitiveMap> alreadyPaidEmployeeAllowances(Long employeeId, int startYear, int endYear);

    @Query(nativeQuery = true, value = "SELECT payable_amount as allowance_payable_amount,amount as allowance_amount,employee_id,month,name as allowance_name FROM other_allowances  where employee_id=? and ((month between 4 and 12 and year =?) or (month between 1 and 3 and year =?)) and payable_amount is not null ;")
    public List<LinkedCaseInsensitiveMap> alreadyPaidOtherployeeAllowance(Long employeeId, int month, int year);

    @Query(nativeQuery = true, value = "SELECT allowance_amount,sb.organization_id,allowance_payable_amount,a.allowance_id,a.employee_type,allowance_name FROM salary_break_up sb LEFT JOIN employee_allowance ea ON sb.sid = ea.salary_breakup_id LEFT JOIN allowance a ON ea.allowance_id = a.allowance_id WHERE sb.employee_id = :employee_id  AND sb.salary_breakup_type = 'Standard' AND sb.sid = ( SELECT MAX(sid) FROM salary_break_up WHERE employee_id =:employee_id AND salary_breakup_type = 'Standard');")
    public List<LinkedCaseInsensitiveMap> employeeApplicableStandard(@Param("employee_id") Long employee_id);

    @Query(nativeQuery = true, value = "SELECT amount,payable_amount as allowance_payable_amount,name FROM salary_break_up sb LEFT JOIN other_allowances ea ON sb.sid = ea.salary_breakup_id  WHERE sb.employee_id = :employee_id AND sb.salary_breakup_type = 'Standard' AND sb.sid = ( SELECT MAX(sid) FROM salary_break_up WHERE employee_id =:employee_id AND salary_breakup_type = 'Standard');")
    public List<LinkedCaseInsensitiveMap> employeeApplicableStandardOther(@Param("employee_id") Long employee_id);

    @Query(value = "Select employeeid,tds as tdsPreviousEmployer,total_allowances,tax_slab_tpye,total_rent,income_from_previous_employer,interest_on_housing_loan_before,national_pension_scheme,pf,sec80d,sec80dd,sec80e,sec80u,sec80g,status,oc.sec80d_type,id.declaration_id from inverstment_declaration id left join other_section_approved oc on id.declaration_id=oc.declaration_id where employeeid=? and fy_year=? and tax_slab_tpye='OldTaxSlabKey';", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getInvesmentDecleration(Long employee_id, int year);

    @Query(nativeQuery = true, value = "select ea.employee_id as employee_id,ea.id as id, ea.allowance_id, ea.allowance_amount, ea.allowance_payable_amount,a.allowance_name, ea.month, ea.year from employee_allowance ea , allowance a where a.allowance_id=ea.allowance_id and ea.employee_id In(?1) and ea.organization_id=?2 and ea.month=?3 and ea.year=?4")
    public List<LinkedCaseInsensitiveMap> getAllowancesForUpdate(List<Long> employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select * from employee_allowance where employee_id In(?1) and organization_id=?2 and month=?3 and year=?4")
    public List<EmployeeAllowance> getAllowancesForUpdateData(List<Long> employee_id, Long organization_id, int month, int year);

    @Query(nativeQuery = true, value = "select ea.employee_id as employee_id,ea.id as id, ea.allowance_id, ea.allowance_amount, ea.allowance_payable_amount,a.allowance_name, ea.month, ea.year from employee_allowance ea , allowance a where a.allowance_id=ea.allowance_id and ea.organization_id=?1 and ea.month=?2 and ea.year=?3 and ea.employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getAllowancesForSalarySheet(Long organization_id, long month, long year, String empType);

    @Query(nativeQuery = true, value = "select ea.allowance_id,ea.id,ea.employee_id ,ea.allowance_amount, ea.allowance_payable_amount,a.allowance_name,ea.salary_breakup_id from employee_allowance ea , allowance a where a.allowance_id=ea.allowance_id and ea.salary_breakup_id IN(?1)")
    public List<LinkedCaseInsensitiveMap> getAllowancesForSalaryStandard(List<Long> salary_break_up_id);

    @Query(nativeQuery = true, value = "SELECT  CAST(SUM(ea.allowance_payable_amount) AS UNSIGNED) AS allowance_payable_amount,\n"
            + "        ea.employee_id,\n"
            + "        ea.allowance_id,\n"
            + "        a.allowance_name \n"
            + "FROM employee_allowance ea \n"
            + "LEFT JOIN allowance a ON a.allowance_id = ea.allowance_id \n"
            + "WHERE ea.employee_id =?1\n"
            + "    AND ea.organization_id =?2\n"
            + "    AND ((ea.year =?3 AND ea.month BETWEEN 4 AND 12) OR (ea.year =?4 AND ea.month BETWEEN 1 AND 3))\n"
            + "GROUP BY ea.allowance_id;")
    public List<LinkedCaseInsensitiveMap> getSumOfAllowancesForForm16(Long employee_id, Long organization_id, int year, int nextYear);

    @Query(value = "Select Sum(allowance_payable_amount) as amount,employee_id  from employee_allowance ea left join allowance a on ea.allowance_id=a.allowance_id where ea.organization_id=?  and ea.month=? and ea.year=? and (a.allowance_name='Basic' or a.allowance_name='Basic Salary' or a.allowance_name='DA' or a.allowance_name='Dearness Allowance') group by ea.employee_id", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> sumOfDaAndBasic(Long organizationId, int month, int year);

    @Query(value = "SELECT SUM(amount) AS total_amount\n"
            + "FROM rent_amount_approved\n"
            + "WHERE rent_month <= ?1 and declaration_id=?2", nativeQuery = true)
    public double getRentAmount(LocalDate date, Long id);

    @Query(nativeQuery = true, value = "SELECT consultant_allowance_amount as allowance_amount,consultant_allowance_name as allowance_name,consultnat_allowance_payable_amount as allowance_payable_amount,employee_id FROM employee_allowance where organization_id=?1 and month=?2 and year=?3 and employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getAllowancesForSalarySheetForConsultant(Long organization_id, long month, long year, String empType);

    @Query(nativeQuery = true, value = "SELECT DISTINCT consultant_allowance_name as allowance_name FROM employee_allowance where organization_id=?1 and month=?2 and year=?3 and employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getAllowancesNameForSalarySheetForConsultant(Long organization_id, long month, long year, String empType);
   
    @Query(value = "select  ea.allowance_amount,ea.allowance_id as allowanceId,ea.allowance_payable_amount,a.allowance_name  from employee_allowance ea LEFT JOIN allowance a ON ea.allowance_id=a.allowance_id where ea.salary_breakup_id=?1", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> empAllowanceBySid(Long salaryBreakupId);

    @Query(value = "SELECT sum(allowance_payable_amount) as allowance_amount,employee_id FROM employee_allowance where allowance_id in (:allowanceIds) and month= :month and year=:year group by employee_id", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> sumOfAllowanceGroupByEmployeeId(@Param("allowanceIds") Set<Long> allowanceIds, @Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT ea.employee_id, SUM(ea.allowance_payable_amount) AS total_allowance_payable_amount FROM employee_deduction ed LEFT JOIN deduction d ON ed.deduction_id = d.deduction_id LEFT JOIN  deduction_allowance_template_mapping datm ON ed.deduction_template_id = datm.deduction_template_id LEFT JOIN employee_allowance ea ON datm.allowance_id = ea.allowance_id AND ed.salary_breakup_id = ea.salary_breakup_id WHERE ed.organization_id = :orgId AND d.deduction_name = 'EPF' AND ed.month = :month AND ed.year = :year and ea.employee_id is not null GROUP BY ea.employee_id", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getEpfWages(@Param("orgId") Long organizationId, @Param("month") int month, @Param("year") int year);

    @Query(value = "Select employee_id ,payable_amount from other_allowances where organization_id=:orgId and month=:month and year=:year", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getOtherAllowances(@Param("orgId") Long organizationId, @Param("month") int month, @Param("year") int year);

    @Query(value = "Select dt.employee_percentage,dt.salary from employee_deduction ed left join deduction d on ed.deduction_id=d.deduction_id left join deduction_allowance_template_mapping datm on ed.deduction_template_id=datm.deduction_template_id left join deduction_template dt on ed.deduction_template_id=dt.deduction_template_id where d.deduction_name='EPF' and  ed.employee_id=:employeeId and month=:month and year=:year", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getEpfInformation(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query(nativeQuery = true, value = "select * from  employee_allowance where salary_breakup_id IN(?1)")
    public List<EmployeeAllowance> allowanceForSaved(List<Long> salary_break_up_id);

    @Query(nativeQuery = true, value = "SELECT \n"
            + "    ea.employee_id as employeeId, \n"
            + "    ea.organization_id as organizationId, \n"
            + "    e.name, \n"
            + "    e.employee_code as employeeCode, \n"
            + "    e.emp_desingnation as empDesingnation, \n"
            + "    e.department_name as departmentName,\n"
            + "    SUM(\n"
            + "        CASE \n"
            + "            WHEN a.allowance_name IN ('Basic','Basic Salary', 'Dearness Allowance','DA') \n"
            + "            THEN COALESCE(ea.allowance_payable_amount, 0) \n"
            + "            ELSE 0 \n"
            + "        END\n"
            + "    ) AS totalBasicDa \n"
            + "FROM employee_allowance ea\n"
            + "INNER JOIN allowance a ON ea.allowance_id = a.allowance_id \n"
            + "LEFT JOIN employee_details e ON ea.employee_id = e.employee_id\n"
            + "WHERE \n"
            + "    ea.organization_id = ?1\n"
            + "    AND (\n"
            + "        (ea.year = ?2 AND ea.month BETWEEN 4 AND 12) \n"
            + "        OR \n"
            + "        (ea.year = ?3 AND ea.month BETWEEN 1 AND 3)\n"
            + "    ) \n"
            + "    AND e.name like CONCAT(?4, '%')  \n"
            + "GROUP BY ea.employee_id, e.name, e.employee_code, e.emp_desingnation, e.department_name;")
    public List<LinkedCaseInsensitiveMap> getBasicAndDASum(Long organizationId, int year, int nextYear, String search);

    @Query(nativeQuery = true, value = "select allowance_amount,allowance_name from employee_allowance ea Left Join custom_allowance ca ON ea.allowance_id=ca.id where ea.salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> customAllowanceForView(Long salary_break_up_id);

    @Query(nativeQuery = true, value = "select ea.allowance_id,  ROUND(ea.allowance_amount) as allowance_amount ,  ROUND(ea.allowance_payable_amount) AS allowance_payable_amount,a.allowance_name from employee_allowance ea , custom_allowance a where a.id=ea.allowance_id and ea.salary_breakup_id=?1")
    public List<LinkedCaseInsensitiveMap> savedAllowancesMonthly(Long salary_breakup_id);

    @Query(nativeQuery = true, value = "select ea.allowance_id,  ROUND(SUM(ea.allowance_amount)) AS allowance_amount,  ROUND(SUM(ea.allowance_payable_amount)) AS allowance_payable_amount,a.allowance_name as allowance_name  from employee_allowance ea , custom_allowance a where a.id=ea.allowance_id and ea.employee_id=?1 and ea.month=?2 and ea.year=?3 group by ea.allowance_id, a.allowance_name")
    public List<LinkedCaseInsensitiveMap> savedAllowancesOfEmployeeMonthly(Long employeeId, int month, int year);

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    ea.employee_id AS employee_id, "
            + "    ea.id AS id, "
            + "    ea.allowance_id, "
            + "    ea.allowance_amount, "
            + "    ea.allowance_payable_amount, "
            + "    a.allowance_name, "
            + "    ea.month, "
            + "    ea.year "
            + "FROM "
            + "    employee_allowance ea, "
            + "    allowance a "
            + "WHERE "
            + "    a.allowance_id = ea.allowance_id "
            + "    AND ea.organization_id = ?1 "
            + "    AND STR_TO_DATE(CONCAT(ea.year, '-', ea.month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> getAllowancesForSalaryExcelSheet(
            Long organizationId,
            String fromDate, // e.g. "2023-03-31"
            String toDate
    );

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    ea.employee_id AS employee_id, "
            + "    ea.id AS id, "
            + "    ea.allowance_id, "
            + "    ea.allowance_amount, "
            + "    ea.allowance_payable_amount, "
            + "    a.allowance_name, "
            + "    ea.month, "
            + "    ea.year "
            + "FROM "
            + "    employee_allowance ea, "
            + "    allowance a "
            + "WHERE "
            + "    a.allowance_id = ea.allowance_id "
            + "    AND ea.organization_id = ?1 "
            + "    AND ea.employee_id = ?4 " // Added employee_id check
            + "    AND STR_TO_DATE(CONCAT(ea.year, '-', ea.month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> getAllowancesForSalaryExcelSheetByEmpId(
            Long organizationId,
            String fromDate,
            String toDate,
            Long employeeId // Added parameter
    );
    
    @Query(nativeQuery = true, value = "select ea.salary_breakup_id,ea.employee_id as employee_id,ea.id as id, ea.allowance_id, ea.allowance_amount, ea.allowance_payable_amount,a.allowance_name, ea.month, ea.year from employee_allowance ea , custom_allowance a where a.id=ea.allowance_id and ea.organization_id=?1 and ea.month=?2 and ea.year=?3 and ea.employee_type=?4")
    public List<LinkedCaseInsensitiveMap> getCustomAllowancesForSalarySheet(Long organization_id, long month, long year, String empType);


    @Query(value = "SELECT ea.employee_id, SUM(ea.allowance_payable_amount) AS total_allowance_payable_amount FROM employee_deduction ed LEFT JOIN custom_deduction d ON ed.deduction_id = d.deduction_id LEFT JOIN  custom_allowance datm ON ed.deduction_template_id = datm.deduction_template_id LEFT JOIN employee_allowance ea ON datm.allowance_id = ea.allowance_id AND ed.salary_breakup_id = ea.salary_breakup_id WHERE ed.organization_id = :orgId AND d.deduction_name = 'EPF' AND ed.month = :month AND ed.year = :year and ea.employee_id is not null GROUP BY ea.employee_id", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getCustomEpfWages(@Param("orgId") Long organizationId, @Param("month") int month, @Param("year") int year);


    
    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    ea.employee_id AS employee_id, "
            + "    ea.id AS ids, "
            + "    ea.allowance_id, "
            + "    ea.allowance_amount, "
            + "    ea.allowance_payable_amount, "
            + "    a.allowance_name, "
            + "    ea.month, "
            + "    ea.year "
            + "FROM "
            + "    employee_allowance ea, "
            + "    custom_allowance a "
            + "WHERE "
            + "    a.id = ea.allowance_id "
            + "    AND ea.organization_id = ?1 "
            + "    AND a.allowance_name = 'Basic Salary' "
            + "    AND STR_TO_DATE(CONCAT(ea.year, '-', ea.month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> getAllowancesForSalaryExcelSheetVedant(
            Long organizationId,
            String fromDate, // e.g. "2023-03-31"
            String toDate
    );
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Allowance;
import com.realcoderz.model.CustomAllowance;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
public interface AllowanceRepository extends JpaRepository<Allowance, Long> {

    //    Fetch All Allowances By Org Id
    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1")
    public List<Allowance> findAllowanceById(Long org_id);

    @Query(nativeQuery = true, value = "Select * from allowance where allowance_id=?1")
    public List<Allowance> findAllowanceName(Long allowance_id);

    //    Fetch All Approved Allowances By Org Id
    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and effective_date <?2 and supervisor_status='Approved' and employee_type=?3")
    public List<Allowance> findApprovedAllowances(Long org_id, Date date, String employee_type);

//    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and effective_date <=?2 and supervisor_status='Approved' and employee_type=?3")
//    public List<Allowance> findApprovedAllowance(Long org_id,LocalDate date,String employee_type);
//  
//    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and effective_date >=?2 and supervisor_status='Approved' and employee_type=?3")
//    public List<Allowance> findApprovedAllowance(Long org_id,String date,String employee_type);
//  
    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and supervisor_status='Approved' and allowance_name != 'Attendance Incentives' and employee_type=?2")
    public List<Allowance> findApprovedAllowance(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and YEAR(DATE(effective_date))=?2 and MONTH(DATE(effective_date))<=?3 and supervisor_status='Approved'")
    public List<Allowance> findApprovedAllowances_effective(Long org_id, int YEAR, int MONTH);
//    

    @Query(nativeQuery = true, value = "Select percentage as percentage,allowance_name as allowance_name from allowance where organization_id=? and supervisor_status='Approved'")
    public List<Allowance> findApproved_fetchSalaryData(Long org_id);

    @Query(nativeQuery = true, value = "select * from allowance where allowance_id= ?")
    public List<Allowance> getAllowanceNameInPDF(Long allowance_id);

    //    Allowance is Exist
    @Query(nativeQuery = true, value = "select count(*) from allowance where allowance_name=?1 and organization_id=?2 and employee_type=?3")
    public Integer isAllowanceExist(String allowance_name, Long organization_id, String employee_type);

    @Query(nativeQuery = true, value = "Select percentage from allowance where lower(allowance_name) = 'basic salary' and organization_id=?1 and effective_date <=?2 and supervisor_status='Approved'")
    public Double findBasicSalaryPercentage(Long org_id, Date date);

    @Query(nativeQuery = true, value = "SELECT allowance_name from allowance where organization_id is null or organization_id=?1 and supervisor_status='Approved' or supervisor_status is null and allowance_type=?2")
    public List<LinkedCaseInsensitiveMap> getAllowanceNames(Long organization_id, String allowance_type);

    @Query(nativeQuery = true, value = "select allowance_name from allowance where organization_id is null and allowance_type=?1")
    public List<LinkedCaseInsensitiveMap> getAllowanceNameForSuperAdmin(String allowance_type);

    @Query(nativeQuery = true, value = "Select allowance_name, allowance_type, allowance_id as allowance_id from allowance where organization_id is null")
    public List<LinkedCaseInsensitiveMap> getAllowancesDataForSuperAdmin();

    @Query(nativeQuery = true, value = "select allowance_name as allowance_name, amount as amount, percentage as percentage, salary as salary, status as status, type_of_allowance as type_of_allowance, allowancedesc as allowancedesc, effective_date as effective_date, allowance_type as allowance_type, allowance as allowance from allowance where allowance_id=?1")
    public List<LinkedCaseInsensitiveMap> getParticularAllowanceDataForSuperAdmin(Long allowance_id);

    @Query(nativeQuery = true, value = "select count(*) from allowance where organization_id is null and allowance_name=?1")
    public Integer isAllowanceExistForSuperAdmin(String allowance_name);

    @Query(nativeQuery = true, value = "select distinct(allowance_name) from allowance where (organization_id is null or organization_id=?1) and allowance_type=?2")
    public List<LinkedCaseInsensitiveMap> getAllowanceNameForOrganization(Long organization_id, String allowance_type);

    @Query(nativeQuery = true, value = "select amount, salary, allowancedesc, type_of_allowance from allowance where allowance_name=?1 and organization_id is null")
    public List<LinkedCaseInsensitiveMap> checkTypeOfAllowance(String allowance_name);

    @Query(nativeQuery = true, value = "select ea.allowance_payable_amount from allowance a, employee_allowance ea where a.allowance_name=\"Basic Salary\" and a.organization_id =?1 and a.employee_type=?5 and ea.employee_id=?2 and ea.allowance_id=a.allowance_id and ea.month=?3 and ea.year=?4")
    public Double getBasicSalary(Long organization_id, Long employee_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "Select allowance_id,allowance_name from allowance where organization_id=?1 and employee_type=?2")
    public List<LinkedCaseInsensitiveMap> getWorkerAllowanceName(Long organization_id, String employee_type);

    @Query(nativeQuery = true, value = "select allowance_id from allowance where allowance_name in ?1 and organization_id=?2 and employee_type=?3")
    public List<LinkedCaseInsensitiveMap> getAllowanceIdFromAllowanceName(List<String> allowance_name, Long organization_id, String employee_type);

    @Query(nativeQuery = true, value = "select standard_hours from allowance where allowance_name=?1 and employee_type=?2 and organization_id=?3")
    public Double getStandardHours(String allowance_name, String employee_type, Long organization_id);

    @Query(nativeQuery = true, value = "select allowance_id,standard_hours,allowance_name from allowance where allowance_name=?1 and organization_id=?2 and employee_type='Worker'")
    public LinkedCaseInsensitiveMap getStandradHoursAndSupMappingId(String name, Long organization_id);

    @Query(nativeQuery = true, value = "Select allowance_id from allowance_sub_mapping where allowance_name=?1 and organization_id=?2 limit 1")
    public Long getAllwanceIdFromSubMapping(String name, Long organization_id);

    @Query(nativeQuery = true, value = "select amount from  allowance where  allowance_id=?1 and organization_id=?2 ")
    public Double getAmount(Long allowance_id, Long organization_id);

    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and effective_date <?2 and supervisor_status='Approved' and employee_type=?3")
    public List<Allowance> findApprovedAllowancess(Long org_id, String date, String employee_type);

    @Query(nativeQuery = true, value = "Select allowance_id,amount as allowance_amount from allowance where organization_id=?1 and supervisor_status='Approved' and allowance_name = 'Attendance Incentives' and employee_type=?2")
    public LinkedCaseInsensitiveMap findAttendanceIncentives(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "SELECT ea.salary_breakup_id,a.allowance_id,ea.allowance_amount AS allowance_amount FROM employee_allowance ea LEFT JOIN allowance a ON ea.allowance_id = a.allowance_id WHERE ea.employee_id = :employeeId AND a.supervisor_status = 'Approved'  AND a.allowance_name = 'Attendance Incentives' AND a.employee_type = :employee_type AND ea.salary_breakup_id = (SELECT MAX(salary_breakup_id) FROM employee_allowance WHERE employee_id = :employeeId AND supervisor_status = 'Approved' AND allowance_id IN (SELECT allowance_id FROM allowance WHERE allowance_name = 'Attendance Incentives'  AND employee_type = :employee_type))")
    public LinkedCaseInsensitiveMap findAttendanceIncentivesSaved(@Param("employeeId") Long employeeId, @Param("employee_type") String employee_type);

    @Query(nativeQuery = true, value = "SELECT *\n"
            + "FROM allowance\n"
            + "WHERE organization_id = ?1\n"
            + "    AND supervisor_status = 'Approved'\n"
            + "    AND allowance_name NOT IN ('Attendance Incentives', 'Overtime Allowance', 'Referral Allowance', 'Bonus/Incentive', 'Reimburs/Arrears', 'Overtime')\n"
            + "    AND employee_type = ?2\n"
            + "ORDER BY \n"
            + "    CASE \n"
            + "        WHEN allowance_name = 'Basic salary' THEN 1\n"
            + "        WHEN allowance_name = 'HRA' THEN 2\n"
            + "        ELSE 3\n"
            + "    END")
    public List<Allowance> findApprovedAllowances(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=? and supervisor_status='Approved' and allowance_name not in ( 'Attendance Incentives' , 'Bonus/Incentive') and employee_type=?")
    public List<Allowance> findApprovedAllowanceForAnnexure(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select at.allowance_template_id,linking_factor,allowance_name,at.maximum_value,at.minimum_value,allowance_mapped_id,supervisor_status,a.allowance_id,effective_date,applicable_for,at.percentage,at.amount,at.applicablity_id,at.type_of_allowance,at.salary,employee_id,at.standard_hours,at.editable from allowance a left join allowance_template at on a.allowance_id=at.allowance_id left join employee_template_mapping etm on at.allowance_template_id = etm.allowance_template_id where organization_id=? and supervisor_status='Approved' and employee_type=?")
    public List<LinkedCaseInsensitiveMap> findApprovedAllowanceWithTemplate(Long org_id, String employee_type);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update allowance set supervisor_status=:supervisor_status where allowance_id=:allowance_id")
    int updateStatus(@Param("supervisor_status") String supervisor_status, @Param("allowance_id") Long allowance_id);

    @Query(nativeQuery = true, value = "SELECT sid FROM salary_break_up where employee_id=:employeeId and salary_breakup_type='Standard' order by sid desc limit 1")
    public LinkedCaseInsensitiveMap findMaxSalaryBreakupId(@Param("employeeId") Long employeeId);

    @Query(nativeQuery = true, value = "SELECT *\n"
            + "FROM allowance\n"
            + "WHERE organization_id = ?1\n"
            + "    AND supervisor_status = 'Approved'\n"
            + "    AND employee_type = ?2\n"
            + "ORDER BY \n"
            + "    CASE \n"
            + "        WHEN allowance_name = 'Basic salary' THEN 1\n"
            + "        WHEN allowance_name = 'HRA' THEN 2\n"
            + "        ELSE 3\n"
            + "    END")
    public List<Allowance> findApprovedAllowancesForSheet(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "SELECT allowance_id,allowance_template_id FROM fulltime_allowance_mapping where allowance_template_id in (:allowance_template_ids)")
    public List<LinkedCaseInsensitiveMap> findMappedAllowances(@Param("allowance_template_ids") List<Long> allowance_template_ids);

    @Query(nativeQuery = true, value = "Select * from allowance where organization_id=?1 and supervisor_status='Approved' and employee_type IN('Full time','Worker')")
    public List<Allowance> approvedAllowance(Long org_id);

    @Query(nativeQuery = true, value = "Select  month,atp.percentage,exclude_amount,include_amount,fam.allowance_id from allowance a left join allowance_payment_months apm on a.allowance_id=apm.allowance_id left join allowance_template atp on a.allowance_id=atp.allowance_id left join fulltime_allowance_mapping fam on atp.allowance_template_id=fam.allowance_template_id where a.allowance_id=?")
    public List<LinkedCaseInsensitiveMap> closingAllowanceData(Long allowanceId);

    @Query(nativeQuery = true, value = "Select allowance_id,employee_type,allowance_name,effective_date,supervisor_status,organization_id from allowance where organization_id=:organization_id and ( allowance_name LIKE CONCAT(:search, '%') or employee_type LIKE CONCAT(:search, '%'))")
    public List<LinkedCaseInsensitiveMap> allowanceForGrid(@Param("organization_id") Long org_id, @Param("search") String search);

    @Query(nativeQuery = true, value = "SELECT *\n"
            + "FROM allowance\n"
            + "WHERE organization_id = ?1\n"
            + "    AND supervisor_status = 'Approved'\n"
            + "ORDER BY \n"
            + "    CASE \n"
            + "        WHEN allowance_name = 'Basic salary' THEN 1\n"
            + "        WHEN allowance_name = 'HRA' THEN 2\n"
            + "        ELSE 3\n"
            + "    END")
    public List<Allowance> findApprovedAllowancesForExcelSheet(Long org_id);
    
    @Query(nativeQuery = true, value = "SELECT \n"
            + "    allowance_name,\n"
            + "    id\n"
            + "FROM custom_allowance\n"
            + "WHERE organization_id = ?1\n"
            + "    AND employee_type = ?2\n"
            + "    AND LOWER(allowance_name) != 'gross'\n"
            + "ORDER BY \n"
            + "    CASE \n"
            + "        WHEN LOWER(allowance_name) = 'basic salary' THEN 1\n"
            + "        WHEN LOWER(allowance_name) = 'hra' THEN 2\n"
            + "        ELSE 3\n"
            + "    END,allowance_name")
    public List<LinkedCaseInsensitiveMap> findCustomAllowancesForSheet(Long org_id, String employee_type);
}

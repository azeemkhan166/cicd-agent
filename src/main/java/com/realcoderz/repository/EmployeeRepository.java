package com.realcoderz.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.realcoderz.model.AccountDetails;

/**
 *
 * @author Lalit Raghav
 */
@Repository
public interface EmployeeRepository extends JpaRepository<AccountDetails, Long> {

        @Query(nativeQuery = true, value = "select a.id as id ,a.bankaccount as bankaccount ,a.bankname as bankname, a.employeeid as employeeId ,e.id as Eid, e.gross_salary  from account_details a join employee e where a.employeeid = e.employee_id and a.organization_id = e.organization_id and a.organization_id = ?1")
        public List<LinkedCaseInsensitiveMap> findAllAccounts(Long organization_id);

        @Query(nativeQuery = true, value = "select distinct employeeid, bankaccount,bankname,ifsc from account_details where organization_id =?1")
        public List<LinkedCaseInsensitiveMap> accountExistByOrganizationId(Long organization_id);

        @Query(nativeQuery = true, value = "Select distinct s.month, s.year, s.effective_date , s.gross_salary as gross_salary ,s.record_id,s.appraisal_salary as appraisal_salary, s.employee_id as employeeId from salary_history_record s where s.organization_id=?1 order by s.record_id desc")
        public List<LinkedCaseInsensitiveMap> findAllSalaryById(Long organization_id);

        @Query(nativeQuery = true, value = "Select distinct a.id as id, a.bankaccount as bankaccount, a.bankname as bankname, a.employeeid as employeeId,a.ifsc as ifsc from account_details a where a.employeeid=?1")
        public List<LinkedCaseInsensitiveMap> findByAccId(Long employeeid);

        @Query(nativeQuery = true, value = "Select employeeid as employeeid from account_details ")
        public List<LinkedCaseInsensitiveMap> filterIDAll();

        @Query(nativeQuery = true, value = "select a.bankaccount as bankaccount ,a.bankname as bankname ,a.ifsc as ifsccode from account_details a where a.employeeid = ?1")
        public List<LinkedCaseInsensitiveMap> fetchOnlyBankDetailsByEmpId(Long employeeid);

        @Query(nativeQuery = true, value = "select count(a.employeeid ) from account_details a where a.employeeid = ?1")
        public Integer fetchBankDetailsByEmpId(Long employeeid);

        @Transactional
        @Modifying
        @Query(nativeQuery = true, value = "UPDATE account_details a SET a.bankname =:bankname,a.bankaccount =:bankaccount,a.ifsc=:ifsc,organization_id =:organization_id,employeeid =:employeeid WHERE a.employeeid =:employeeid")
        void updateAddress(@Param("bankname") String bankname, @Param("bankaccount") String bankaccount,
                        @Param("ifsc") String ifsc, @Param("organization_id") Long organization_id,
                        @Param("employeeid") Long employeeid);

        // @Query(nativeQuery = true, value ="Select employee.gross_salary from employee
        // join account_details where employee.organization_id =
        // account_details.organization_id and
        // employee.employee_id=account_details.employeeid")
        // public List<LinkedCaseInsensitiveMap> findGross(Long organization_id);
        @Query(nativeQuery = true, value = "select employee_id,gross_salary,effective_date,appraisal_salary from salary_history_record  where organization_id=?")
        public List<LinkedCaseInsensitiveMap> findGrossSalary(Long organization_id);

        @Query(nativeQuery = true, value = "select allowance_name,amount,percentage,salary,employee_type from allowance where organization_id=? and employee_type='full time'")
        public List<LinkedCaseInsensitiveMap> findBasicSalary(Long organization_id);

        // @Query(nativeQuery=true,value="select employee_id,epf,coalesce(lwp,0) as
        // lwp,actual_day,working_day,total_day,gross_salary from salary_break_up where
        // organization_id=? and month=? and year=? and actual_day IS NOT NULL and epf
        // IS NOT NULL and total_day IS NOT NULL")
        // public List<LinkedCaseInsensitiveMap> getWorkingDays(Long
        // organization_id,Long month,Long year);
        @Query(nativeQuery = true, value = "select employee_id,epf,coalesce(lwp,0) as lwp,actual_day,working_day,total_day,gross_salary from salary_break_up where organization_id=? and month=? and year=? and epf IS NOT NULL and total_day IS NOT NULL")
        public List<LinkedCaseInsensitiveMap> getWorkingDays(Long organization_id, Long month, Long year);

        @Query(nativeQuery = true, value = "select employee_id,payable,tds, professional_tax from  run_pay_roll  where organization_id=? and pay_run_month=? and pay_run_year=?")
        public List<LinkedCaseInsensitiveMap> getDataFromPayroll(Long organization_id, Long month, Long year);

        @Query(nativeQuery = true, value = "select employee_id,payable,tds,pay_run_month from  run_pay_roll  where organization_id=:organization_id  and pay_run_year=:year and pay_run_month IN(:month) ORDER BY pay_run_month DESC")
        public List<LinkedCaseInsensitiveMap> getDateFromPayrollInBulk(@Param("organization_id") Long organization_id,
                        @Param("year") Integer year, @Param("month") List<Integer> month);

        // @Query(value = "select pay_run_month as month ,pay_run_year as year from
        // run_pay_roll where employee_id=?1 and pay_run_year=?2 and order by
        // month;",nativeQuery = true)
        // public List<LinkedCaseInsensitiveMap> getYearAndMonth(Long employee_id,Long
        // year);
        //
        @Query(value = "SELECT pay_run_month AS month, pay_run_year AS year FROM run_pay_roll WHERE employee_id =?1 AND ((pay_run_year =?2 AND pay_run_month BETWEEN 4 AND 12) OR (pay_run_year =?3 AND pay_run_month BETWEEN 1 AND 3)) ORDER BY year, month;", nativeQuery = true)
        public List<LinkedCaseInsensitiveMap> getYearAndMonth(Long employee_id, Long year, Long nextYear);

        @Query(value = "select employee_id,gross_salary ,annual_ctc,effective_date,salary_hold,sid from salary_break_up where salary_breakup_type='Standard' and organization_id=?", nativeQuery = true)
        public List<LinkedCaseInsensitiveMap> getGrossSalary(Long organization_id);

        @Query(value = "select sid,employee_id,gross_salary,effective_date,coalesce(epf,'N/A') as epf, coalesce(percentage_change,'N/A') as percentage_change from salary_break_up where salary_breakup_type='Standard' and employee_id=?1 and organization_id=?2", nativeQuery = true)
        public List<LinkedCaseInsensitiveMap> getAllAppraisel(Long employee_id, Long orgId);

        @Query(value = "SELECT * FROM run_pay_roll where organization_id=?1 and pay_run_month=?2 and pay_run_year=?3 and employee_id=?4", nativeQuery = true)
        public LinkedCaseInsensitiveMap isPayrollDone(Long organization_id, Long month, Long year, Long employee_id);

        @Transactional
        @Modifying
        @Query(nativeQuery = true, value = "update salary_break_up set salary_hold=?1 where sid=?2")
        void updateSalaryHoldFlag(String flag, Long sid);

        @Query(value = "select sbu.sid,sbu.salary_hold,sbu.employee_id,ed.employee_code,ed.name,ed.email from salary_break_up sbu LEFT JOIN employee_details ed On sbu.employee_id=ed.employee_id where sbu.salary_breakup_type='Standard' and sbu.salary_hold='Yes' and ed.status in ('Active','Offboarding in progress') and sbu.organization_id=?1", nativeQuery = true)
        public List<LinkedCaseInsensitiveMap> getAllEmployeeWhoseSalaryOnHold(Long organization_id);

        @Query(value = "select * from account_details where organization_id=?", nativeQuery = true)
        public List<AccountDetails> getAllBankDetailsByOrgId(Long organization_id);

        @Query(value = "SELECT joining_date FROM employee_details where employee_id=?", nativeQuery = true)
        public LinkedCaseInsensitiveMap employeeJoiningDate(Long employeeId);

        @Query(value = "SELECT distinct ad.employeeid, ad.bankaccount,ad.bankname,ad.ifsc, sb.employee_id,sb.gross_salary ,sb.annual_ctc,sb.effective_date,sb.salary_hold,sid FROM account_details ad LEFT JOIN ( SELECT * FROM salary_break_up WHERE salary_breakup_type = 'Standard') sb ON ad.employeeid = sb.employee_id WHERE ad.employeeid in (:employeeId) AND (sb.sid = ( SELECT MAX(sid) FROM salary_break_up WHERE employee_id = ad.employeeid AND salary_breakup_type = 'Standard') OR sb.sid IS NULL);", nativeQuery = true)
        public List<LinkedCaseInsensitiveMap> accountDetailsWithSalaryBreakUp(
                        @Param("employeeId") List<Long> employeeId);

        @Query(value = "select pay_run_month,pay_run_year from run_pay_roll where employee_id=?1 order by run_pay_roll_id Desc limit 1", nativeQuery = true)
        public LinkedCaseInsensitiveMap getLastRunPayrollOFEmployee(Long employee_id);

        @Query(nativeQuery = true, value = "Select rp.employee_id,rp.esic,rp.payable, COALESCE(ed.name, 'Not available') AS name,\n"
                        + "COALESCE(ed.esic, 'Not available') AS esicNum,rp.working_day,rp.overtime_pay from run_pay_roll rp left join salary_break_up sb on rp.employee_id=sb.employee_id and rp.pay_run_month=sb.month and rp.pay_run_year=sb.year left join employee_details ed on rp.employee_id=ed.employee_id\n"
                        + "where rp.organization_id=?1 and pay_run_month = ?2 and pay_run_year=?3 and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') and sb.is_esic != 'No'")
        public List<LinkedCaseInsensitiveMap> getEmployeeRunPayrollDetails(Long organization_id, Long month, Long year);

        @Query(nativeQuery = true, value = "SELECT allowance_id FROM fulltime_allowance_mapping where allowance_template_id=?1")
        public List<Long> getSubMappingIdOfOverTime(Long allowance_template_id);

        @Query(nativeQuery = true, value = ""
                        + "SELECT "
                        + "    e.department_name AS departmentName, "
                        + "    COUNT(DISTINCT e.employee_id) AS totalEmployees, "
                        + "    SUM(r.working_day) AS totalWorkingDays, "
                        + "    SUM(r.salary) AS totalSalary, "
                        + "    SUM(r.payable) AS totalPayable, "
                        + "    SUM(r.net_payable) AS totalNetPayable, "
                        + "    SUM(r.overtime_pay) AS totalOvertimePay, "
                        + "    SUM(r.over_time) AS totalOvertimeHours "
                        + "FROM employee_details e "
                        + "JOIN run_pay_roll r "
                        + "    ON e.employee_id = r.employee_id "
                        + "WHERE "
                        + "    e.organization_id = :organizationId "
                        + "    AND TRIM(LOWER(e.employee_work_location)) = LOWER(:location) "
                        + "    AND r.organization_id = :organizationId "
                        + "    AND r.pay_run_month BETWEEN MONTH(:fromDate) AND MONTH(:toDate) "
                        + "    AND r.pay_run_year = YEAR(:fromDate) "
                        + "GROUP BY "
                        + "    e.department_name "
                        + "ORDER BY "
                        + "    e.department_name")
        public List<LinkedCaseInsensitiveMap> locationAndDepartmentWiseReport(
                        Long organizationId,
                        String fromDate,
                        String toDate,
                        String location);

        @Query(nativeQuery = true, value = "Select ROW_NUMBER() OVER (ORDER BY rp.site_id) AS uid,rp.gate_ph, rp.site_id,rp.site, rp.employee_id,rp.esic,rp.basic_salary as payable, COALESCE(ed.name, 'Not available') AS name,\n"
                        + "COALESCE(ed.esic, 'Not available') AS esicNum,rp.working_day,rp.ot_wages as overtime_pay from custom_run_payroll rp left join salary_break_up sb on rp.employee_id=sb.employee_id and rp.month=sb.month and rp.year=sb.year and rp.site_id = sb.site_id left join employee_details ed on rp.employee_id=ed.employee_id\n"
                        + "where rp.organization_id=?1 and rp.month = ?2 and rp.year=?3 and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') ORDER BY rp.site_id")
        public List<LinkedCaseInsensitiveMap> getCustomEmployeeRunPayrollDetails(Long organization_id, Long month,
                        Long year);

        @Query(nativeQuery = true, value = "Select ROW_NUMBER() OVER (ORDER BY rp.site_id) AS uid,rp.gate_ph, rp.site_id,rp.site, rp.employee_id,rp.esic,rp.basic_salary as payable, COALESCE(ed.name, 'Not available') AS name,\n"
                        + "COALESCE(ed.esic, 'Not available') AS esicNum,rp.working_day,rp.ot_wages as overtime_pay from custom_run_payroll rp left join salary_break_up sb on rp.employee_id=sb.employee_id and rp.month=sb.month and rp.year=sb.year and rp.site_id = sb.site_id left join employee_details ed on rp.employee_id=ed.employee_id\n"
                        + "where rp.organization_id=?1 and rp.month = ?2 and rp.year=?3 and rp.site_id=?4 and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') ORDER BY rp.site_id")
        public List<LinkedCaseInsensitiveMap> getCustomEmployeeRunPayrollDetailsSiteWise(Long organization_id,
                        Long month, Long year, Long siteId);

        @Query(nativeQuery = true, value = "select ROW_NUMBER() OVER (ORDER BY rp.site_id) AS uid,rp.employee_code,rp.gate_ph,rp.employee_id,basic_salary as payable,pt as professional_tax ,site_id,site,ed.name,pan_number from  custom_run_payroll rp Left Join employee_details ed On rp.employee_id=ed.employee_id  where rp.organization_id=? and month=? and year=? and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') ORDER BY rp.site_id")
        public List<LinkedCaseInsensitiveMap> getPtDataFromCustomPayroll(Long organization_id, Long month, Long year);

        @Query(nativeQuery = true, value = "select ROW_NUMBER() OVER (ORDER BY rp.site_id) AS uid,rp.employee_code,rp.gate_ph,rp.employee_id,basic_salary as payable,pt as professional_tax ,site_id,site,ed.name,pan_number from  custom_run_payroll rp Left Join employee_details ed On rp.employee_id=ed.employee_id where rp.organization_id=? and month=? and year=? and site_id=? and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') ORDER BY rp.site_id")
        public List<LinkedCaseInsensitiveMap> getPtDataFromCustomPayrollSiteWise(Long organization_id, Long month,
                        Long year, Long siteId);

        @Query(nativeQuery = true, value = "select pp.plan_name,sbu.employee_id,sbu.gross_salary,sbu.effective_date,sbu.pay_plan_id from salary_break_up sbu left join pay_plan pp on sbu.pay_plan_id=pp.id  where sbu.site_id=?1 and sbu.organization_id=?2 and salary_breakup_type='Standard'")
        public List<LinkedCaseInsensitiveMap> getAllStandardSiteWise(Long siteId, Long organization_id);
}

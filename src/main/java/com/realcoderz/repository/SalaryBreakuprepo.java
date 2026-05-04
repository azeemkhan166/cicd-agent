/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.SalaryBreakUp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav edited By Astha
 */
@Repository
public interface SalaryBreakuprepo extends JpaRepository<SalaryBreakUp, Long> {

    //---------------------update--------------------
    @Query(nativeQuery = true, value = "Select sid as sid, allowancename as allowancename,basicsalary as basicsalary,deductionname as deductionname,totalsalary as totalsalary from salary_break_up where employeeid=?1")
    public LinkedCaseInsensitiveMap findBySalaryBreakUpId(Long employeeid);

//     @Query(nativeQuery = true, value ="Select sid as sid, allowancename as allowancename,basicsalary as basicsalary,deductionname as deductionname,totalsalary as totalsalary from salary_break_up where employeeid=?1 and effecive_date <=?2")
//    public LinkedCaseInsensitiveMap findBySalaryBreakUpId(Long employeeid,Date date);
    //---------------------fetch--------------------
    @Query(nativeQuery = true, value = "Select sid as sid, allowancename as allowancename,basicsalary as basicsalary,deductionname as deductionname,totalsalary as totalsalary from salary_break_up where employeeid=?")
    public LinkedCaseInsensitiveMap fetchsalarybreakupByEmployeeId(Long employeeid);

    @Query(nativeQuery = true, value = "select lwp,working_day,approved_leave,holidays,rate,present_day,week_off,gross_salary, total_hours, over_time, net_amount,payable_salary, total_deduction, total_earning , total_payable_earning , actual_day,epf ,total_day, ytd_total_deduction, percentage_change  from salary_break_up  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetch(Long employeeid, Long organizationId, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select lwp,working_day,approved_leave,holidays,present_day,week_off,gross_salary , net_amount, total_deduction, total_earning , total_payable_earning , actual_day from salary_break_up where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public LinkedCaseInsensitiveMap fetchConsultantData(Long employeeid, Long organizationId, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select sid, lwp,working_day,approved_leave,holidays,present_day,week_off, gross_salary , net_amount, total_deduction, total_earning , total_payable_earning, actual_day, payable_salary from salary_break_up where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetchListConsultantData(Long employeeid, Long organizationId, int month, int year, String employee_type);

    @Query("FROM SalaryBreakUp where month=:month and year=:year and employee_id=:empId and employee_type=:employee_type")
    public SalaryBreakUp findByMonthAndYearAndEmpId(@Param("month") Integer month, @Param("year") Integer year, @Param("empId") Integer empId, @Param("employee_type") String employee_type);

//    @Query(nativeQuery = true, value = "Select * from salary_break_up where organization_id=1 and effective_date <=?2")
//    public List<SalaryBreakUp> forEffectiveDate(Long org_id,Date date);
    @Query(nativeQuery = true, value = "select total_earning,net_amount from salary_break_up where employee_id=?1 and year=?2 and organization_id=?3")
    public LinkedCaseInsensitiveMap salaryBrakupSaveOrNot(Long employeeid, int month, int year);

    @Query(nativeQuery = true, value = "select employee_id,total_earning,net_amount,gross_salary from salary_break_up where organization_id=?1 and month=?2 and year=?3")
    public List<LinkedCaseInsensitiveMap> isSalaryBreapupExist(Long organizationId, int month, int year);

    @Query(nativeQuery = true, value = "select pdf_url from salary_break_up where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public String getPdfurl(int employee_id, Long organization_id, int month, int year);

    @Transactional
    @Modifying
    @Query(value = "UPDATE salary_break_up s SET s.pdf_url=(:path) where s.employee_id=(:empId) and s.organization_id=(:orgId) and s.month=(:month) and s.year=(:year)", nativeQuery = true)
    public void updateSalaryBreakUp(@Param("empId") int employee_id, @Param("path") String path, @Param("orgId") Long organization_id, @Param("month") int month, @Param("year") int year);

    @Query(nativeQuery = true, value = "select working_day from salary_break_up where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public Double getWorkingDay(Integer employee_id, Long organization_id, int month, int year);

//    @Query(nativeQuery = true, value = "select max(percentage_change) as percentage_change from salary_break_up where employee_id=?1 and organization_id=?2")
//    public Double getPercentage(Long employeeid,Long organization_id);
    @Query(nativeQuery = true, value = "select percentage_change as percentage_change from salary_break_up where employee_id=? and organization_id=? and salary_breakup_type='Standard' order by sid desc limit 1 ;")
    public Double getPercentage(Long employeeid, Long organization_id);

    @Query(nativeQuery = true, value = "Select allowance_id from allowance_sub_mapping where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> getIdOfSubAllowance(Long organization_id);

//    // use in tax for update salaryBreakup update data
//    
//    @Query(nativeQuery = true, value = "select working_day,total_days,payable_gross,payable_basic,employee_type,gross_salary,basic from salary_break_up  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
//    public List<LinkedCaseInsensitiveMap> fetchSalaryBreakupData(Long employeeid, Long organizationId, int month, int year);
    @Query(nativeQuery = true, value = "select working_day,approved_leave,holidays,lwp,actual_day,total_day,epf,present_day,week_off,gross_salary , net_amount, total_deduction, total_earning , payable_salary, total_payable_earning, sid from salary_break_up  where employee_id=?1 and organization_id=?2 and month=?3 and year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> getSavedSalaryBreakupbasedonMonth(Long employeeid, Long organizationId, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select working_day,approved_leave,holidays,lwp ,present_day,week_off,gross_salary , net_amount, total_deduction, total_earning , payable_salary, total_payable_earning, month, year, sid from salary_break_up  where employee_id=?1 and organization_id=?2 and employee_type=?3 and gross_salary=?4 and month is null and year is null")
    public List<LinkedCaseInsensitiveMap> getSavedEmployeeSalaryBreakup(Long employeeid, Long organizationId, String employee_type, Double gross_salary);

//  fetching the list of  salary breakup of a employee whose effective date covers a particular month and year
    @Query(nativeQuery = true, value = "SELECT sbu.joining_date,sbu.employee_id,sbu.tds,sbu.annual_ctc,sbu.modeofpayment,sbu.org_ids as orgIds,sbu.employee_type, sbu.working_day, sbu.approved_leave, sbu.holidays, sbu.lwp, sbu.present_day, sbu.week_off, sbu.gross_salary, sbu.net_amount, sbu.total_deduction, sbu.total_earning, sbu.payable_salary, sbu.total_payable_earning, sbu.month, sbu.year,is_esic, sbu.sid FROM salary_break_up sbu \n" +
"WHERE  organization_id = ?1 AND month = ?2 AND year = ?3 ")
    public List<LinkedCaseInsensitiveMap> getEmployeesSalaryBreakupbasedOnYearAndMonth(Long organizationId, int month, int year);
    
    @Query(nativeQuery = true, value = "SELECT gross_salary FROM salary_break_up where employee_id=?1 AND salary_breakup_type= \"Standard\" AND (YEAR(effective_date) < ?2 OR (YEAR(effective_date) = ?2 AND MONTH(effective_date) <= ?3)) ORDER BY effective_date DESC LIMIT 1 OFFSET 0")
    public LinkedCaseInsensitiveMap getGrossSalary(int employee_id, String year, String month);

    @Query(nativeQuery = true, value="SELECT COUNT(*) FROM salary_break_up WHERE month=?1 and year=?2 and organization_id=?3")
    public int isSaved(int month, int year, Long organization_id);
    
    @Query(nativeQuery = true,value = "select * from  salary_break_up where employee_id=?1 and gross_salary=?2 and salary_breakup_type='Standard'")
    public List<LinkedCaseInsensitiveMap>  isSalaryBreakupSave(int employee_id,double gross_salary);
    
    @Query(nativeQuery = true,value="SELECT sid,employee_id FROM salary_break_up where employee_id in (:empId) and year=:year and month=:month")
    public List<LinkedCaseInsensitiveMap> salaryBreakupSavedOfMonth(@Param("empId") List<Long> empId,@Param("year") Integer year,@Param("month") Integer month );
    // need indexing
    @Query(nativeQuery = true, value = "SELECT sbu.employee_id,sbu.voluntary_epf,sbu.voluntary_epf_percentage,sbu.annual_ctc,sbu.over_time as rate,sbu.salary_hold,sbu.modeofpayment,sbu.employee_type, sbu.working_day, sbu.approved_leave, sbu.holidays, sbu.lwp, sbu.present_day, sbu.week_off, sbu.gross_salary, sbu.net_amount, sbu.total_deduction, sbu.total_earning, sbu.payable_salary, sbu.total_payable_earning, sbu.month, sbu.year, sbu.sid,sbu.is_esic,sbu.epf,sbu.baiscda,sbu.org_ids as orgIds "
        + "FROM (SELECT employee_id,annual_ctc,voluntary_epf,voluntary_epf_percentage,employee_type, working_day,salary_hold,modeofpayment,over_time ,approved_leave, holidays, lwp, present_day, week_off, gross_salary, net_amount, total_deduction, total_earning, payable_salary, total_payable_earning, month, year, sid, created_date,is_esic,epf,baiscda,org_ids, "
        + "ROW_NUMBER() OVER (PARTITION BY employee_id ORDER BY created_date DESC) AS rn "
        + "FROM salary_break_up "
        + "WHERE(salary_breakup_type = 'Standard'  AND organization_id = ?1 AND (YEAR(effective_date) < ?3 OR (YEAR(effective_date) = ?3 AND MONTH(effective_date) <= ?2)))) sbu "
        + "WHERE sbu.rn = 1")
    public List<LinkedCaseInsensitiveMap> getEmployeesSalaryStandard(Long organizationId, int month, int year);

    @Query(nativeQuery = true, value="SELECT gross_salary,annual_ctc FROM salary_break_up where employee_id=?1 and salary_breakup_type='Standard' order by sid desc limit 1")
    public LinkedCaseInsensitiveMap getGrossSalaryOFEmployee(Long employeeId);
    
    @Query(nativeQuery = true,value="SELECT sid,gross_salary,percentage_change,epf,is_esic,modeofpayment,org_ids as orgIds FROM salary_break_up where employee_id=?1 and gross_salary=?2 and salary_breakup_type='Standard'")
    public LinkedCaseInsensitiveMap getSavedStandard(Long employeeId,Double grossSalary);
    
   @Query(nativeQuery = true,value="SELECT * FROM salary_break_up where sid=?;" )
    public SalaryBreakUp employeeSalaryBreakUp(Long salaryBreakUpId);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="update salary_break_up set tds=? , net_amount=? ,over_time=? ,rate=? where sid=?")
    public void updateSalaryBreakupWhileUpdatingAllowances(Long tds,Long netAmount,String overtime,Double rate,Long salaryBreakUpId);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value="update salary_break_up set tds=? , net_amount=? where sid=?")
    public void updateTdsAndNetInSalaryBreakUp(Long tds,Long netAmount,Long salaryBreakUpId);

    @Query(nativeQuery = true,value = "select * from salary_break_up where salary_breakup_type='Standard' and employee_id=?1 order by sid desc limit 1")
    public SalaryBreakUp holdSalaryOfEmployee(Long employee_id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE salary_break_up SET salary_hold = 'Yes' WHERE sid IN (?1)")
    public void holdAllEmployeeSalary(List<Long> sid);
    
    @Query(nativeQuery = true, value = "SELECT sid\n"
            + "FROM salary_break_up AS sb\n"
            + "WHERE sb.salary_breakup_type = 'standard'\n"
            + "  AND sb.employee_id IN (?1)\n"
            + "  AND sb.sid = (\n"
            + "    SELECT MAX(sb2.sid)\n"
            + "    FROM salary_break_up AS sb2\n"
            + "    WHERE sb2.employee_id = sb.employee_id\n"
            + "      AND sb2.salary_breakup_type = 'standard'\n"
            + "  );")
    public List<Long> getSidForSalaryHold(List<Long> employeeIds);
    
    
    @Query(value="Select * from salary_break_up where sid=?;",nativeQuery = true)
    public SalaryBreakUp getEmployeeBreaup(Long sId);
    
    @Query(value="SELECT sid,modeofpayment,org_ids as orgIds FROM salary_break_up where employee_id=? and gross_salary=? and salary_breakup_type='Standard' ;",nativeQuery = true)
    public LinkedCaseInsensitiveMap maxSalaryBreaup(Long employeeId,Double grossSalary);
        
    @Query(nativeQuery = true, value = "select * from salary_break_up where employee_id In(?1) and organization_id=?2 and month=?3 and year=?4")
    public List<SalaryBreakUp> getSalaryBreakupforUpdate(List<Long> employeeid, Long organizationId, int month, int year);

    @Query(nativeQuery = true, value = "SELECT sid,gross_salary,sb.employee_id FROM salary_break_up AS sb left join employee_details ed on sb.employee_id=ed.employee_id WHERE sb.organization_id = ?1 AND sb.employee_type = ?2 AND salary_breakup_type = 'Standard' and ed.status in ('Active','Offboarding in progress') AND (sb.organization_id, sb.employee_id, sb.sid) IN (SELECT organization_id, employee_id, MAX(sid) AS max_sid FROM salary_break_up sbb WHERE organization_id =?1 AND employee_type = ?2 AND salary_breakup_type = 'Standard' GROUP BY organization_id, employee_id )")
    List<LinkedCaseInsensitiveMap> getSalaryStandard(Long organizationId,String employeeType);
    
    @Query(value="SELECT gross_salary,working_day,total_day,sb.employee_type,is_esic,sb.organization_id FROM salary_break_up sb  where  sb.sid=:sid",nativeQuery = true)
    public LinkedCaseInsensitiveMap getGrossInformation(@Param("sid") Long salaryBreaupId);
    
    @Query(value="Select distinct employee_id from salary_break_up where salary_breakup_type='Standard' and organization_id=? and employee_type in ('Full time','Permanent','Probation');",nativeQuery = true)
    public List<Long> getEmployeeIds(Long organizationId);
    
    @Query(value = "SELECT employee_type,sid FROM salary_break_up where employee_id=? and salary_breakup_type='Standard' order by sid desc limit 1;",nativeQuery = true)
    public LinkedCaseInsensitiveMap getEmloyeeTypeFromSalaryBreakUp(Long employeeId);
    
    @Query(value="SELECT deduction_id,is_esic,sb.organization_id FROM salary_break_up sb left join deduction d on sb.organization_id=d.organization_id and sb.employee_type=d.employee_type where d.deduction_name= 'ESIC' and sb.sid=:sid",nativeQuery = true)
    public LinkedCaseInsensitiveMap getESICDeductionId(@Param("sid") Long salaryBreaupId);
    
    @Query(value="Select allowance_payable_amount from employee_allowance ea left join allowance a on ea.allowance_id=a.allowance_id where a.allowance_name in ('Washing','Washing Allowance','Washing Allowances') and ea.salary_breakup_id=?",nativeQuery = true)
    public LinkedCaseInsensitiveMap getWashingAllowance(@Param("sid") Long salaryBreaupId);
    
    @Query(value = "SELECT sid,gross_salary,voluntary_epf_percentage,voluntary_epf,percentage_change,epf,is_esic,modeofpayment,org_ids as orgIds FROM salary_break_up where employee_id=? and salary_breakup_type='Standard' order by sid desc limit 1",nativeQuery = true)
    public LinkedCaseInsensitiveMap getLatestStandardOfEmployee(Long employeeId);
    
    @Query(value="Select * from salary_break_up where sid IN (:sid)",nativeQuery = true)
    public List<SalaryBreakUp> salarybreakupForSave(@Param("sid") List<Long> sid);
    
//    @Query(value="SELECT * FROM salary_break_up where employee_id=? and gross_salary=? and salary_breakup_type='Standard' limit 1",nativeQuery = true)
//    public SalaryBreakUp getDataFromSalaryBreakup(Long employeeId,Double grossSalary);
//    
    @Query(value="SELECT * FROM salary_break_up where employee_id=? and salary_breakup_type='Standard' order by sid desc limit 1",nativeQuery = true)
    public SalaryBreakUp getDataFromSalaryBreakup(Long employeeId,Double grossSalary);
    
     @Query(nativeQuery = true, value = "SELECT sbu.employee_id,sbu.voluntary_epf as voluntaryEpf,sbu.voluntary_epf_percentage as voluntaryEpfPercentage ,sbu.annual_ctc,sbu.over_time ,sbu.salary_hold as salaryHold,sbu.modeofpayment,sbu.employee_type, sbu.working_day, sbu.approved_leave, sbu.holidays, sbu.lwp, sbu.present_day, sbu.week_off, sbu.gross_salary, sbu.net_amount, sbu.total_deduction, sbu.total_earning, sbu.payable_salary, sbu.total_payable_earning, sbu.month, sbu.year, sbu.sid,sbu.is_esic as isEsic,site_id as siteId,pay_plan_id as payPlanId,organization_id "
        + "FROM (SELECT employee_id,annual_ctc,voluntary_epf,voluntary_epf_percentage ,employee_type, working_day,salary_hold,modeofpayment,over_time ,approved_leave, holidays, lwp, present_day, week_off, gross_salary, net_amount, total_deduction, total_earning, payable_salary, total_payable_earning, month, year, sid, created_date,is_esic,site_id,pay_plan_id,organization_id, "
        + "ROW_NUMBER() OVER (PARTITION BY employee_id ORDER BY created_date DESC) AS rn "
        + "FROM salary_break_up "
        + "WHERE(salary_breakup_type = 'Standard'  AND organization_id = ?1 AND site_id=?2 AND (salary_hold IS NULL OR salary_hold != 'Yes') )) sbu "
        + "WHERE sbu.rn = 1")
    public List<LinkedCaseInsensitiveMap> getEmployeesSalaryStandard1(Long organizationId,Long siteId);

    @Query(nativeQuery = true,value="SELECT site_id,gross_salary,effective_date,sid,employee_code,name FROM salary_break_up sb left join employee_details ed on sb.employee_id=ed.employee_id where salary_breakup_type='standard' and sb.employee_id=?1")
    List<LinkedCaseInsensitiveMap> getAllStandard(Long employeeId);
    
    @Query(nativeQuery = true,value = "select * from salary_break_up where employee_id=?1 and month=?2 and year=?3 and site_id=?4")
    SalaryBreakUp SalaryBreakUpOfEmployeeMonthly(Long employeeId,int month,int year,Long site_id);


    @Query(nativeQuery = true,value = "select * from salary_break_up where employee_id=?1 and month=?2 and year=?3")
    List<SalaryBreakUp> SalaryBreakUpMonthly(Long employeeId,int month,int year);

   @Query(nativeQuery = true,value = "SELECT * FROM custom_run_payroll  where site_id=?1 and year=?2 and month=?3")
   List<LinkedCaseInsensitiveMap> getAllSavedDta(Long id,int year,int month);
   
   @Query(nativeQuery = true,value = "select sid from salary_break_up where employee_id=?1 and month=?2 and year=?3 and site_id=?4")
   Long getSid(Long employeeId,int month,int year,Long id);
   
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE sb, ea, ed\n"
            + "FROM salary_break_up sb\n"
            + "LEFT JOIN employee_allowance ea ON ea.salary_breakup_id = sb.sid\n"
            + "LEFT JOIN employee_deduction ed ON ed.salary_breakup_id = sb.sid\n"
            + "WHERE sb.sid =?1")
    public void deleteSalaryBreakup(Long salaryBreakUpId);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from custom_run_payroll where employee_id=?1 and site_id=?2 and year=?3 and month=?4")
    public void deleteRunPayroll(Long employeeId,Long id,int year,int month);
    
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from custom_allowance_amount where employee_id=?1 and site_id=?2 and year=?3 and month=?4")
    public void deleteUploadDataMonthwise(Long employeeId,Long id,int year,int month);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.RunPayRoll;
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
 * @author bipulsingh
 */
@Repository
public interface RunPayRollRepository extends JpaRepository<RunPayRoll, Long> {

    @Query("SELECT COUNT(employeeId) FROM RunPayRoll WHERE  payRunMonth=?1 AND payRunYear=?2 AND employeeId=?3")
    public int havingSameData(Integer month, Integer year, Long employeeId);

    @Query("FROM RunPayRoll rp WHERE rp.payRunMonth=:month AND rp.payRunYear=:year AND rp.employeeId=:employeeId")
    public RunPayRoll findDataByMonthYearAndEmpId(@Param("month") Integer month, @Param("year") Integer year, @Param("employeeId") Long employeeId);
//    @Query("FROM RunPayRoll rp WHERE rp.payRunMonth=:month AND rp.payRunYear=:year AND rp.organizationId=:organizationId")

    @Query(value = "SELECT * FROM run_pay_roll rp WHERE rp.pay_run_month=:month AND rp.pay_run_year=:year AND rp.organization_id=:organizationId and (name LIKE CONCAT(:search_word, '%') OR employee_code LIKE CONCAT(:search_word, '%'))", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> findAllSavedDataByMonthYearOfEmployee(@Param("month") Integer month, @Param("year") Integer year, @Param("organizationId") Long employeeId, @Param("search_word") String search_word);

    @Query("SELECT COALESCE(SUM(rp.net_payable),0) as total FROM RunPayRoll rp WHERE rp.payRunMonth=:month AND rp.payRunYear=:year AND rp.organizationId=:organizationId")
    public int findPreviousPayRunData(@Param("month") Integer month, @Param("year") Integer year, @Param("organizationId") Long organizationId);

    @Query("SELECT count(*) FROM RunPayRoll rp where rp.payRunMonth=:month AND rp.payRunYear=:year AND rp.organizationId=:organizationId")
    public int isPayRollRun(@Param("month") Integer month, @Param("year") Integer year, @Param("organizationId") Long organizationId);

    @Query("SELECT rp.adhoc, rp.reimburs FROM RunPayRoll rp where rp.payRunMonth=:month AND rp.payRunYear=:year AND rp.organizationId=:organizationId AND rp.employeeId=:employeeId")
    public LinkedCaseInsensitiveMap getAllowanceAmount(@Param("month") Integer month, @Param("year") Integer year, @Param("organizationId") Long organizationId, @Param("employeeId") Long employeeId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE run_pay_roll  SET tds =:tds WHERE employee_id =:employee_id and organization_id=:organization_id and pay_run_month=:pay_run_month and pay_run_year=:pay_run_year")
    void updateTDSInRunPayroll(@Param("tds") Double tds, @Param("employee_id") Long employee_id, @Param("organization_id") Long organization_id, @Param("pay_run_month") Long pay_run_month, @Param("pay_run_year") Long pay_run_year);

    @Query(nativeQuery = true, value = "select other_deductions , reimburs from run_pay_roll where employee_id=?1 and organization_id=?2 and pay_run_month=?3 and pay_run_year=?4 and employee_type=?5")
    public List<LinkedCaseInsensitiveMap> fetchArrears(Long employeeid, Long organizationId, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select * from run_pay_roll where employee_id=?1 and organization_id=?2 and pay_run_month=?3 and pay_run_year=?4 and employee_type=?5 limit 1")
    public LinkedCaseInsensitiveMap getPayrollRecord(Long employeeid, Long organizationId, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "select * from run_pay_roll where organization_id=?1 and pay_run_month=?2 and pay_run_year=?3 and (employee_type='Full time' OR employee_type='worker')")
    public List<LinkedCaseInsensitiveMap> isRunPayrollSaved(Long organizationId, int month, int year);

    @Query(nativeQuery = true, value = "SELECT epf,employee_id FROM run_pay_roll where organization_id=?1 and pay_run_month=?2 and pay_run_year=?3 and epf>0")
    public List<LinkedCaseInsensitiveMap> getAllEPFValue(Long organizationId, Long month, Long year);

    @Query(nativeQuery = true, value = "select run_pay_roll_id from run_pay_roll where employee_id=? and  (pay_run_month between 4 and 12 and pay_run_year = ?) or (pay_run_month between 1 and 3 and pay_run_year =? ) ;")
    public List<LinkedCaseInsensitiveMap> employeeGivenPayroll(Long employeeId, int startYear, int endYear);

    @Query(nativeQuery = true, value = "SELECT *, DATE(CONCAT(pay_run_year, '-', LPAD(pay_run_month, 2, '0'), '-01')) AS custom_date\n"
            + "FROM run_pay_roll\n"
            + "WHERE employee_id =?1\n"
            + "  AND (pay_run_year > ?3 OR (pay_run_year =?3 AND pay_run_month >= ?2))\n"
            + "  AND (pay_run_year < ?5 OR (pay_run_year = ?5 AND pay_run_month <= ?4))")
    public List<LinkedCaseInsensitiveMap> getSavedRunPayrollBetweenMonth(Long enployee_id, int startMonth, int Startyear, int endMonth, int endyear);

    @Query(nativeQuery = true, value = "select  COALESCE(SUM(tds), 0) AS taxDeductionTillDate from run_pay_roll where employee_id=?1 and ((pay_run_month between 4 and 12 and pay_run_year=?2)  OR  (pay_run_month between 1 and 3 and pay_run_year=?3))")
    public int taxdeductedTillDate(Long employee_id, int startYear, int endYear);

    @Query(nativeQuery = true, value = "SELECT max(pay_run_month) as maxpayrun FROM run_pay_roll WHERE employee_id =:employeeId  AND ((pay_run_month BETWEEN 4 AND 12 AND pay_run_year = :startYear) OR (pay_run_month BETWEEN 1 AND 3 AND pay_run_year = :endYear)) AND run_pay_roll_id = (SELECT MAX(run_pay_roll_id) FROM run_pay_roll WHERE employee_id =:employeeId  AND ((pay_run_month BETWEEN 4 AND 12 AND pay_run_year = :startYear) OR (pay_run_month BETWEEN 1 AND 3 AND pay_run_year =:endYear )));")
    public LinkedCaseInsensitiveMap maxRunpayRollMonth(@Param("employeeId") Long employeeId, @Param("startYear") int startYear, @Param("endYear") int endYear);

    @Query(value = "Select working_day from run_pay_roll where employee_id=:employeeId and pay_run_month=:month and pay_run_year=:year and organization_id=:organization_id", nativeQuery = true)
    public LinkedCaseInsensitiveMap employeeWorkingDay(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year, @Param("organization_id") Long organization_id);

    @Query(value = "Select employee_type from run_pay_roll where employee_id=:employeeId and pay_run_month=:month and pay_run_year=:year", nativeQuery = true)
    public LinkedCaseInsensitiveMap employeeTypeOfEmployee(@Param("employeeId") Long employeeId, @Param("month") int month, @Param("year") int year);

    @Query(nativeQuery = true, value = "SELECT * FROM run_pay_roll where employee_id=?1 and  ((pay_run_year =?2 AND pay_run_month BETWEEN 4 AND 12) OR (pay_run_year =?3 AND pay_run_month BETWEEN 1 AND 3)) order by run_pay_roll_id desc limit 1")
    public LinkedCaseInsensitiveMap getLastRunPayroll(Long employee_id, int year, int nextyear);

    @Query(nativeQuery = true, value = "Select rp.epf,payable,sb.voluntary_epf,sb.org_ids as orgIds,sb.baiscda,sb.epf as epfFlag,sb.voluntary_epf_percentage,ed.name,uan,sb.lwp,rp.employee_id,sb.total_day,sb.working_day,ed.employee_type from run_pay_roll rp left join employee_details ed on rp.employee_id=ed.employee_id left join salary_break_up sb on rp.employee_id=sb.employee_id and rp.pay_run_month=sb.month and rp.pay_run_year=sb.year where rp.organization_id=? and  pay_run_month = ? and pay_run_year=? and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') and sb.epf != 'No'")
    public List<LinkedCaseInsensitiveMap> getRunpayRollForEpf(Long organizationId, int month, int year);

    @Query(nativeQuery = true, value = "SELECT \n"
            + "    rp.net_payable,\n"
            + "    rp.salary,\n"
            + "    rp.payable,\n"
            + "    rp.employee_id,\n"
            + "    rp.name,\n"
            + "    rp.employee_type,\n"
            + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employee_code,\n"
            + "    COALESCE(NULLIF(ed.employee_work_location, ''), '-') AS employee_work_location,\n"
            + "    rp.working_day,\n"
            + "    COALESCE(NULLIF(ed.department_name, ''), '-') AS department_name,\n"
            + "    COALESCE(NULLIF(ad.bankaccount, ''), '-') AS bank_account,\n"
            + "    COALESCE(NULLIF(ad.ifsc, ''), '-') AS ifsc,\n"
            + "    COALESCE(NULLIF(ed.uan, ''), '-') AS uan,\n"
            + "    rp.pay_run_month,\n"
            + "    rp.pay_run_year,\n"
            + "	   sbu.working_day as days,\n"
            + "    sbu.approved_leave,\n"
            + "    sbu.holidays,\n"
            + "    sbu.present_day,\n"
            + "    sbu.week_off,\n"
            + "    sbu.lwp,\n"
            + "    sbu.actual_day,\n"
            + "    sbu.total_day\n"
            + "FROM \n"
            + "    run_pay_roll rp \n"
            + "LEFT JOIN \n"
            + "    employee_details ed \n"
            + "    ON rp.employee_id = ed.employee_id \n"
            + "LEFT JOIN \n"
            + "    account_details ad \n"
            + "    ON rp.employee_id = ad.employeeid \n"
            + "LEFT JOIN \n"
            + "    salary_break_up sbu \n"
            + "    ON rp.employee_id = sbu.employee_id \n"
            + "    AND rp.pay_run_month = sbu.month \n"
            + "    AND rp.pay_run_year = sbu.year\n"
            + "WHERE \n"
            + "    rp.organization_id =?1 \n"
            + "    AND rp.pay_run_month = ?2 \n"
            + "    AND rp.pay_run_year = ?3 \n"
            + "    AND rp.employee_type = ?4")
    public List<LinkedCaseInsensitiveMap> isRunPayrollSavedForSheet(Long organizationId, long month, long year, String employee_type);

    @Query(nativeQuery = true, value = "SELECT \n"
            + "    rp.net_payable,\n"
            + "    rp.salary,\n"
            + "    rp.payable,\n"
            + "    rp.employee_id,\n"
            + "    rp.name,\n"
            + "    rp.employee_type,\n"
            + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employee_code,\n"
            + "    COALESCE(NULLIF(ed.employee_work_location, ''), '-') AS employee_work_location,\n"
            + "    rp.working_day,\n"
            + "    COALESCE(NULLIF(ed.department_name, ''), '-') AS department_name,\n"
            + "    COALESCE(NULLIF(ad.bankaccount, ''), '-') AS bank_account,\n"
            + "    COALESCE(NULLIF(ad.ifsc, ''), '-') AS ifsc,\n"
            + "    COALESCE(NULLIF(ed.uan, ''), '-') AS uan,\n"
            + "    rp.pay_run_month,\n"
            + "    rp.pay_run_year,\n"
            + "	   sbu.working_day as days,\n"
            + "    sbu.approved_leave,\n"
            + "    sbu.holidays,\n"
            + "    sbu.present_day,\n"
            + "    sbu.week_off,\n"
            + "    sbu.lwp,\n"
            + "    sbu.actual_day,\n"
            + "    sbu.total_day\n"
            + "FROM \n"
            + "    run_pay_roll rp \n"
            + "LEFT JOIN \n"
            + "    employee_details ed \n"
            + "    ON rp.employee_id = ed.employee_id \n"
            + "LEFT JOIN \n"
            + "    account_details ad \n"
            + "    ON rp.employee_id = ad.employeeid \n"
            + "LEFT JOIN \n"
            + "    salary_break_up sbu \n"
            + "    ON rp.employee_id = sbu.employee_id \n"
            + "    AND rp.pay_run_month = sbu.month \n"
            + "    AND rp.pay_run_year = sbu.year\n"
            + "WHERE \n"
            + "    rp.organization_id =?1 \n"
            + "    AND rp.pay_run_month = ?2 \n"
            + "    AND rp.pay_run_year = ?3 \n"
            + "    AND rp.employee_type=?4")
    public List<LinkedCaseInsensitiveMap> isRunPayrollSavedForSheetForConsultant(Long organizationId, long month, long year, String employee_type);

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    rp.net_payable, "
            + "    rp.salary, "
            + "    rp.payable, "
            + "    rp.employee_id, "
            + "    rp.name, "
            + "    rp.employee_type, "
            + "    rp.joining_date, "
            + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employee_code, "
            + "    COALESCE(NULLIF(ed.employee_work_location, ''), '-') AS employee_work_location, "
            + "    rp.working_day, "
            + "    COALESCE(NULLIF(ed.department_name, ''), '-') AS department_name, "
            + "    COALESCE(NULLIF(ad.bankaccount, ''), '-') AS bank_account, "
            + "    COALESCE(NULLIF(ad.ifsc, ''), '-') AS ifsc, "
            + "    COALESCE(NULLIF(ed.uan, ''), '-') AS uan, "
            + "    rp.pay_run_month, "
            + "    rp.pay_run_year, "
            + "    sbu.working_day AS days, "
            + "    sbu.approved_leave, "
            + "    sbu.holidays, "
            + "    sbu.present_day, "
            + "    sbu.week_off, "
            + "    sbu.lwp, "
            + "    sbu.actual_day, "
            + "    sbu.total_day "
            + "FROM "
            + "    run_pay_roll rp "
            + "LEFT JOIN employee_details ed ON rp.employee_id = ed.employee_id "
            + "LEFT JOIN account_details ad ON rp.employee_id = ad.employeeid "
            + "LEFT JOIN salary_break_up sbu "
            + "    ON rp.employee_id = sbu.employee_id "
            + "    AND rp.pay_run_month = sbu.month "
            + "    AND rp.pay_run_year = sbu.year "
            + "WHERE "
            + "    rp.organization_id = ?1 "
            + "    AND STR_TO_DATE(CONCAT(rp.pay_run_year, '-', rp.pay_run_month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> isRunPayrollSavedForExcelSheet(
            Long organizationId,
            String fromDate, // e.g. "2023-03-31"
            String toDate
    );

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    rp.net_payable, "
            + "    rp.salary, "
            + "    rp.payable, "
            + "    rp.employee_id, "
            + "    rp.name, "
            + "    rp.employee_type, "
            + "    rp.joining_date, "
            + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employee_code, "
            + "    COALESCE(NULLIF(ed.employee_work_location, ''), '-') AS employee_work_location, "
            + "    rp.working_day, "
            + "    COALESCE(NULLIF(ed.department_name, ''), '-') AS department_name, "
            + "    COALESCE(NULLIF(ad.bankaccount, ''), '-') AS bank_account, "
            + "    COALESCE(NULLIF(ad.ifsc, ''), '-') AS ifsc, "
            + "    COALESCE(NULLIF(ed.uan, ''), '-') AS uan, "
            + "    rp.pay_run_month, "
            + "    rp.pay_run_year, "
            + "    DATE_FORMAT(STR_TO_DATE(CONCAT(rp.pay_run_year, '-', rp.pay_run_month, '-01'), '%Y-%m-%d'), '%b-%y') AS monthYear, "
            + "    sbu.working_day AS days, "
            + "    sbu.approved_leave, "
            + "    sbu.holidays, "
            + "    sbu.present_day, "
            + "    sbu.week_off, "
            + "    sbu.lwp, "
            + "    sbu.actual_day, "
            + "    sbu.total_day "
            + "FROM "
            + "    run_pay_roll rp "
            + "LEFT JOIN employee_details ed ON rp.employee_id = ed.employee_id "
            + "LEFT JOIN account_details ad ON rp.employee_id = ad.employeeid "
            + "LEFT JOIN salary_break_up sbu "
            + "    ON rp.employee_id = sbu.employee_id "
            + "    AND rp.pay_run_month = sbu.month "
            + "    AND rp.pay_run_year = sbu.year "
            + "WHERE "
            + "    rp.organization_id = ?1 "
            + "    AND rp.employee_id = ?4 " // Added employee_id check
            + "    AND STR_TO_DATE(CONCAT(rp.pay_run_year, '-', rp.pay_run_month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> isRunPayrollSavedForExcelSheetByEmpId(
            Long organizationId,
            String fromDate,
            String toDate,
            Long employeeId // Added parameter
    );

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    rp.employee_id, "
            + "    rp.name, "
            + "    rp.employee_type, "
            + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employeeCode "
            + "FROM "
            + "    run_pay_roll rp "
            + "WHERE "
            + "    rp.organization_id = ?1 "
    )
    List<LinkedCaseInsensitiveMap> getEmployeeDataFromRunPayRoll(Long organizationId);

    @Query(value = "SELECT "
            + "os.org_state AS stateName, "
            + "pt.min_salary, "
            + "pt.max_salary, "
            + "pt.tax_amount, "
            + "COUNT(rp.employee_id) AS totalEmployeesInSlab, "
            + "SUM(pt.tax_amount) AS totalProfessionalTaxForSlab "
            + "FROM run_pay_roll rp "
            + "JOIN organization_set_up os ON os.organization_id = rp.organization_id "
            + "JOIN professional_tax_new pt ON pt.state_name = os.org_state "
            + "AND rp.payable BETWEEN pt.min_salary AND pt.max_salary "
            + "WHERE rp.organization_id = :organizationId "
            + "AND MONTH(rp.created_date) = MONTH(:month) "
            + "AND YEAR(rp.created_date) = YEAR(:month) "
            + "GROUP BY os.org_state, pt.min_salary, pt.max_salary, pt.tax_amount "
            + "ORDER BY os.org_state, pt.min_salary",
            nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getPtData(
            @Param("organizationId") Long organizationId,
            @Param("month") String month
    );
    
        @Query(nativeQuery = true, value = "SELECT \n"
            + "    rp.gross_galary,\n"
            + "    rp.gate_basic_rate,\n"
            + "    rp.ph,\n"
            + "    rp.over_time_formatted,\n"
            + "    rp.epf_days,\n"
            + "    rp.basic_salary,\n"
            + "    rp.gate_ph,\n"
            + "    rp.employee_id,\n"
            + "    rp.epf,\n"
            + "    rp.esic,\n"
            + "    rp.pt,\n"
            + "    rp.glwb,\n"
            + "    rp.coupan,\n"
            + "    rp.name,\n"
            + "    rp.gate_deduction,\n"
            + "    rp.payment_gate,\n"
            + "    rp.second_part,\n"
            + "    rp.salary_payable,\n"
            + "    rp.ot_wages,\n"
            + "    rp.addi_incentive,\n"
            + "    rp.ho_advance,\n"
            + "    rp.site_advance,\n"
            + "    rp.other_deduction,\n"
            + "    rp.total_deduction,\n"
            + "    rp.net_payable,\n"
            + "    rp.annual_ctc,\n"
            + "    rp.mode_of_payment,\n"
            + "    rp.year,\n"
            + "    rp.month,\n"
            + "    rp.site_id,\n"
            + "    rp.site,\n"
            + "    rp.employee_type,\n"
            + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employee_code,\n"
            + "    COALESCE(NULLIF(ed.employee_work_location, ''), '-') AS employee_work_location,\n"
            + "    rp.working_day,\n"
            + "    COALESCE(NULLIF(ed.department_name, ''), '-') AS department_name,\n"
            + "    COALESCE(NULLIF(ad.bankaccount, ''), '-') AS bank_account,\n"
            + "    COALESCE(NULLIF(ad.ifsc, ''), '-') AS ifsc,\n"
            + "    COALESCE(NULLIF(ed.uan, ''), '-') AS uan,\n"
            + "	   sbu.working_day as days,\n"
            + "    sbu.approved_leave,\n"
            + "    sbu.holidays,\n"
            + "    sbu.present_day,\n"
            + "    sbu.week_off,\n"
            + "    sbu.lwp,\n"
            + "    sbu.actual_day,\n"
            + "    sbu.total_day,\n"
            + "    sbu.site_id as siteId,\n"
            + "    sbu.sid\n"       
            + "FROM \n"
            + "    custom_run_payroll rp \n"
            + "LEFT JOIN \n"
            + "    employee_details ed \n"
            + "    ON rp.employee_id = ed.employee_id \n"
            + "LEFT JOIN \n"
            + "    account_details ad \n"
            + "    ON rp.employee_id = ad.employeeid \n"
            + "LEFT JOIN \n"
            + "    salary_break_up sbu \n"
            + "    ON rp.employee_id = sbu.employee_id \n"
            + "    AND rp.month = sbu.month \n"
            + "    AND rp.year = sbu.year\n"
            + "    AND rp.site_id = sbu.site_id\n"
            + "WHERE \n"
            + "    rp.organization_id =?1 \n"
            + "    AND rp.month = ?2 \n"
            + "    AND rp.year = ?3 \n"
            + "    AND rp.employee_type = ?4")
    public List<LinkedCaseInsensitiveMap> isCustomRunPayrollSavedForSheet(Long organizationId, long month, long year,String employee_type);

    @Query(nativeQuery = true, value = "Select ROW_NUMBER() OVER (ORDER BY rp.site_id) AS uid,rp.site_id,rp.site,COALESCE(rp.epf_wages, 0) AS epf_wages,rp.epf,rp.gross_galary ,sb.voluntary_epf,sb.org_ids as orgIds,sb.baiscda,sb.epf as epfFlag,sb.voluntary_epf_percentage,ed.name,uan,sb.lwp,rp.employee_id,sb.actual_day as total_day,sb.working_day,ed.employee_type from custom_run_payroll rp left join employee_details ed on rp.employee_id=ed.employee_id left join salary_break_up sb on rp.employee_id=sb.employee_id and rp.month=sb.month and rp.year=sb.year and rp.site_id = sb.site_id where rp.organization_id=? and  rp.month = ? and rp.year=? and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') order by rp.site_id")
    public List<LinkedCaseInsensitiveMap> getCustomRunpayRollForEpf(Long organizationId, int month, int year);    

    @Query(nativeQuery = true, value = "Select ROW_NUMBER() OVER (ORDER BY rp.site_id) AS uid,rp.site_id,rp.site,COALESCE(rp.epf_wages, 0) AS epf_wages,rp.epf,rp.gross_galary ,sb.voluntary_epf,sb.org_ids as orgIds,sb.baiscda,sb.epf as epfFlag,sb.voluntary_epf_percentage,ed.name,uan,sb.lwp,rp.employee_id,sb.actual_day as total_day,sb.working_day,ed.employee_type from custom_run_payroll rp left join employee_details ed on rp.employee_id=ed.employee_id left join salary_break_up sb on rp.employee_id=sb.employee_id and rp.month=sb.month and rp.year=sb.year and rp.site_id = sb.site_id where rp.organization_id=? and  rp.month = ? and rp.year=? and rp.site_id=? and (ed.employee_type = 'Full time' or  ed.employee_type = 'Worker') and (ed.status = 'Active' or ed.status = 'Offboarding in progress') order by rp.site_id")
    public List<LinkedCaseInsensitiveMap> getCustomRunpayRollForEpfSiteWise(Long organizationId, int month, int year,long siteId);    

    @Query(nativeQuery = true, value = "select org_ids from run_pay_roll where employee_id=?1 and organization_id=?2 and pay_run_month=?3 and pay_run_year=?4")
    public Long getOrgDetailsId(Long employeeid, Long organizationId, int month, int year);
        
    @Query(
    nativeQuery = true,
    value = "SELECT "
    + "    rp.net_payable, "
    + "    rp.basic_salary as salary, "
    + "    rp.basic_salary as payable, "
    + "    rp.pay_plan_id as payPlanId, "
    + "    rp.basic_rate as basicRate, "
    + "    rp.payment_gate, "
    + "    rp.net_payable_status, "
    + "    rp.get_payment_status, "           
    + "    rp.gross_wages, "
    + "    rp.ot_wages, "
    + "    rp.employee_id, "
    + "    rp.name, "
    + "    rp.employee_type, "
    + "    ed.joining_date, "
    + "    ed.emp_desingnation, "
    + "    COALESCE(NULLIF(rp.employee_code, ''), '-') AS employee_code, "
    + "    COALESCE(NULLIF(ed.employee_work_location, ''), '-') AS employee_work_location, "
    + "    rp.working_day, "
    + "    COALESCE(NULLIF(ed.department_name, ''), '-') AS department_name, "
    + "    COALESCE(NULLIF(ad.bankaccount, ''), '-') AS bank_account, "
    + "    COALESCE(NULLIF(ad.ifsc, ''), '-') AS ifsc, "
    + "    COALESCE(NULLIF(ed.uan, ''), '-') AS uan, "
    + "    rp.month as pay_run_month, "
    + "    rp.year as pay_run_year, "
    + "    rp.working_day AS days "
    + "FROM "
    + "    custom_run_payroll rp "
    + "LEFT JOIN employee_details ed ON rp.employee_id = ed.employee_id "
    + "LEFT JOIN account_details ad ON rp.employee_id = ad.employeeid "
    + "WHERE "
    + "    rp.organization_id = ?1 "
    + "    AND STR_TO_DATE(CONCAT(rp.year, '-', rp.month, '-01'), '%Y-%m-%d') "
    + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
)
List<LinkedCaseInsensitiveMap> isRunPayrollSavedForExcelSheetVedant(
        Long organizationId,
        String fromDate,
        String toDate
);

@Query(nativeQuery = true, value = "SELECT crp.paid_status,crp.employee_id,SUM(crp.payment_gate) AS payment_gate,SUM(crp.net_payable) AS net_payable,crp.name,crp.employee_code,ad.bankaccount,ad.ifsc FROM custom_run_payroll crp Left Join account_details ad ON crp.employee_id=ad.employeeid  where month=?1 and year=?2 and crp.organization_id=?3 GROUP BY crp.employee_id, crp.name, crp.employee_code, ad.bankaccount, ad.ifsc,crp.paid_status")
public List<LinkedCaseInsensitiveMap> getRunPayrollSheetData(int month, int year,Long organizationId);

@Query(nativeQuery = true, value = "SELECT crp.net_payable_status,crp.get_payment_status,crp.employee_id, SUM(crp.payment_gate) AS payment_gate,SUM(crp.net_payable) AS net_payable,crp.name,crp.employee_code,ad.bankaccount,ad.ifsc,crp.site_id,crp.month,crp.year FROM custom_run_payroll crp Left Join account_details ad ON crp.employee_id=ad.employeeid  where month=?1 and year=?2 and crp.organization_id=?3 and crp.site_id=?4 GROUP BY crp.employee_id, crp.name, crp.employee_code, ad.bankaccount, ad.ifsc")
public List<LinkedCaseInsensitiveMap> getRunPayrollSheetDataSitewise(int month, int year,Long organizationId,Long siteId);

@Query(nativeQuery = true, value = "SELECT crp.net_payable_status as status,crp.employee_id, crp.payment_gate,crp.net_payable,crp.name,crp.employee_code,ad.bankaccount,ad.ifsc,crp.site_id,crp.month,crp.year FROM custom_run_payroll crp Left Join account_details ad ON crp.employee_id=ad.employeeid  where month=?1 and year=?2 and crp.organization_id=?3 and crp.site_id=?4")
public List<LinkedCaseInsensitiveMap> getRunPayrollSheetForNetPayable(int month, int year,Long organizationId,Long siteId);

@Query(nativeQuery = true, value = "SELECT crp.get_payment_status as status,crp.employee_id, crp.payment_gate,crp.net_payable,crp.name,crp.employee_code,ad.bankaccount,ad.ifsc,crp.site_id,crp.month,crp.year FROM custom_run_payroll crp Left Join account_details ad ON crp.employee_id=ad.employeeid  where month=?1 and year=?2 and crp.organization_id=?3 and crp.site_id=?4")
public List<LinkedCaseInsensitiveMap> getRunPayrollSheetForGetPayment(int month, int year,Long organizationId,Long siteId);

    @Query(
            nativeQuery = true,
            value = "SELECT "
            + "    rp.net_payable, "
            + "    rp.payment_gate, "
            + "    rp.employee_id, "
            + "FROM "
            + "    custom_run_payroll rp "
            + "WHERE "
            + "    rp.organization_id = ?1 "
            + "    AND STR_TO_DATE(CONCAT(rp.year, '-', rp.month, '-01'), '%Y-%m-%d') "
            + "        BETWEEN STR_TO_DATE(?2, '%Y-%m-%d') AND STR_TO_DATE(?3, '%Y-%m-%d')"
    )
    List<LinkedCaseInsensitiveMap> isRunPayrollSavedForFNF(
            Long organizationId,
            String fromDate, // e.g. "2023-03-31"
            String toDate
    );

}

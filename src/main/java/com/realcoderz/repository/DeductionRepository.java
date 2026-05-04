/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Deduction;
import com.realcoderz.model.DeductionAllowanceTemplateMapping;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
public interface DeductionRepository extends JpaRepository<Deduction, Long> {

    //    Fetch All Deductions By Org Id
    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1")

    public List<Deduction> findDeductionById(Long org_id);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and employee_type=\"Full time\"")
    public List<Deduction> findDeductionForFullTime(Long org_id);

    //    Fetch All Approved Deductions By Org Id
    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and effective_date <?2 and supervisor_status='Approved' and employee_type=?3")
    public List<Deduction> findApprovedDeductions(Long org_id, Date date, String employee_type);

    //    Fetch All Approved Deductions By Org Id
//    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and effective_date <=?2 and supervisor_status='Approved' and employee_type=?3")
//    public List<Deduction> findApprovedDeduction(Long org_id, LocalDate date, String employee_type);
//    
//    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and effective_date >=?2 and supervisor_status='Approved' and employee_type=?3")
//    public List<Deduction> findApprovedDeduction(Long org_id, String date, String employee_type);
//   
    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and supervisor_status='Approved' and employee_type=?2")
    public List<Deduction> findApprovedDeduction(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and effective_date <?2 and supervisor_status='Approved' and employee_type=?3 and deduction_name!=\"Gratuity\" ")
    public List<Deduction> findApprovedDeductionsexceptGratuity(Long org_id, Date date, String employee_type);

//    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and effective_date <=?2 and supervisor_status='Approved' and employee_type=?3 and deduction_name!=\"Gratuity\" ")
//    public List<Deduction> findApprovedDeductionsexceptGratuitys(Long org_id, LocalDate date, String employee_type);
//    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and effective_date >=?2 and supervisor_status='Approved' and employee_type=?3 and deduction_name!=\"Gratuity\" ")
//    public List<Deduction> findApprovedDeductionsexceptGratuitys(Long org_id, String date, String employee_type);
    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and supervisor_status='Approved' and employee_type=?2 and deduction_name!=\"Gratuity\" ")
    public List<Deduction> findApprovedDeductionsexceptGratuitys(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and YEAR(DATE(effective_date))=?2 and MONTH(DATE(effective_date))<=?3 and supervisor_status='Approved'")
    public List<Deduction> findApprovedDeductions_effective(Long org_id, int YEAR, int MONTH);

    @Query(nativeQuery = true, value = "select * from deduction where deduction_id= ?")
    public List<Deduction> getDeductionNameInPDF(Long deduction_id);

    //    Deduction is Exist
    @Query(nativeQuery = true, value = "select count(*) from deduction where deduction_name=?1 and organization_id=?2 and employee_type=?3")
    public Integer isDeductionExist(String deduction_name, Long organization_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from deduction where lower(deduction_name)=('epf' or 'esic') and organization_id=?1 and effective_date <=?2 and supervisor_status='Approved'")
    public List<Deduction> findEpfAndEsic(Long org_id, Date date);

    @Query(nativeQuery = true, value = "select deduction_name as deduction_name, deduction_id as deduction_id from deduction where organization_id is null")
    public List<LinkedCaseInsensitiveMap> getDeductionNameForSuperAdmin();

    @Query(nativeQuery = true, value = "select count(*) from deduction where organization_id is null")
    public Integer isDedductionExistForSuperAdmin(String deduction_name);

    @Query(nativeQuery = true, value = "select distinct(deduction_name) from deduction where organization_id is null or organization_id=?1")
    public List<LinkedCaseInsensitiveMap> getDeductionNameForOrganization(Long organization_id);

    @Query(nativeQuery = true, value = "select amount, salary, deductiondesc, type_of_deduction from deduction where deduction_name=?1 and organization_id is null")
    public List<LinkedCaseInsensitiveMap> checkTypeOfDeduction(String deduction_name);

    @Query(nativeQuery = true, value = "select count(*) from deduction where organization_id is null and deduction_name=?1")
    public Integer isDeductionExistForSuperAdmin(String deduction_name);

    @Query(nativeQuery = true, value = "select deduction_name as deduction_name, employer_percentage as employer_percentage from deduction where organization_id=?1 and employee_type=?2")
    public List<LinkedCaseInsensitiveMap> getEmployerPercentage(Long organization_id, String employee_type);

    @Query(nativeQuery = true, value = "select ed.deduction_payable_amount from deduction d, employee_deduction ed where d.deduction_name=\"EPF\" and d.organization_id =?1 and d.employee_type=?5 and ed.employee_id=?2 and ed.deduction_id=d.deduction_id and ed.month=?3 and ed.year=?4")
    public Double getBasicSalary(Long organization_id, Long employee_id, int month, int year, String employee_type);

    @Query(nativeQuery = true, value = "Select deduction_id from deduction where organization_id=?1 and deduction_name=\"Income Tax\"")
    public Long fetchDeductionId(Long organization_id);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and employee_type=?2")
    public List<Deduction> findDeductionAccordingToEmployeeType(Long organization_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and supervisor_status='Approved' and employee_type=?2 and deduction_name Not IN('Bonus','Variable','Gratuity','Advance','Income Tax','Other Deductions')")
    public List<Deduction> findApprovedDeductions(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "SELECT * FROM deduction where organization_id=:organizationId and employee_type=:employeeType and deduction_name=:deductionName")
    public Deduction findDeductionByName(@Param("organizationId") Long organizationId, @Param("employeeType") String employeeType, @Param("deductionName") String deductionName);

    @Query(nativeQuery = true, value = "Select d.deduction_id,d.deduction_name,dt.deduction_template_id,dt.editable,dt.deduction_type,d.applicable_for,dt.employee_percentage,dt.employer_percentage,dt.salary,edtm.employee_id,dt.amount,dt.applicablity_id from deduction d left join deduction_template dt on d.deduction_id=dt.deduction_id left join employee_deduction_template_mapping edtm on dt.deduction_template_id=edtm.deduction_template_id where organization_id=? and supervisor_status='Approved' and employee_type=?")
    public List<LinkedCaseInsensitiveMap> findApprovedDeductionIncludingTemplate(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select d.deduction_id,d.deduction_name,dt.deduction_template_id,dt.editable,dt.deduction_type,d.applicable_for,dt.employee_percentage,dt.employer_percentage,dt.salary,edtm.employee_id,dt.amount,dt.applicablity_id from deduction d left join deduction_template dt on d.deduction_id=dt.deduction_id left join employee_deduction_template_mapping edtm on dt.deduction_template_id=edtm.deduction_template_id where organization_id=? and supervisor_status='Approved' and employee_type=? and d.deduction_name IN('Variable','Bonus','Gratuity','EPF','ESIC')")
    public List<LinkedCaseInsensitiveMap> findApprovedDeductionIncludingTemplateForCTC(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "SELECT * FROM deduction_allowance_template_mapping where deduction_template_id in (:deductionTemplateIds)")
    public List<LinkedCaseInsensitiveMap> getAllowanceDeductionTemplateMapping(@Param("deductionTemplateIds") Set<Long> deductionTemplateIds);

    @Query(nativeQuery = true, value = "Select ed.employer_percentage,d.deduction_name from employee_deduction ed left join deduction d on ed.deduction_id=d.deduction_id where salary_breakup_id=?")
    public List<LinkedCaseInsensitiveMap> findEmployeeDeductionAccordingToEmployeeType(Long salaryBreakupId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update deduction set supervisor_status=:supervisor_status where deduction_id=:deduction_id")
    int updateStatus(@Param("supervisor_status") String supervisor_status, @Param("deduction_id") Long deduction_id);

//    @Query(nativeQuery = true,value="select amount,employer_percentage from employee_gratuity where employee_id=?1 and (YEAR(effective_date) < ?3 OR (YEAR(effective_date) = ?3 AND MONTH(effective_date) <= ?2)) ORDER BY effective_date DESC LIMIT 1 OFFSET 0 ")
//    public LinkedCaseInsensitiveMap getEmployeeBonus(Long employeeId,int month,int year);
    @Query(nativeQuery = true, value = "select amount,employer_percentage from employee_gratuity where employee_id=? order by gratuity_id desc limit 1;")
    public LinkedCaseInsensitiveMap getEmployeeBonus(Long employeeId);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and supervisor_status='Approved' and employee_type=?2 and deduction_name Not IN('Bonus','Variable','Gratuity')")
    public List<Deduction> findApprovedDeductionsForSheet(Long org_id, String employee_type);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and employee_type IN('Full time','Worker') and supervisor_status='Approved'")
    public List<Deduction> approvedDeduction(Long org_id);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and employee_type IN('Full time') and supervisor_status='Approved' and deduction_name='Income Tax' limit 1")
    public Deduction approvedIncomeTaxDeduction(Long org_id);

    @Query(nativeQuery = true, value = "Select deduction_id,deduction_name,supervisor_status,employee_type,organization_id,effective_date from deduction where organization_id=:org_id and ( deduction_name LIKE CONCAT(:search, '%') or employee_type LIKE CONCAT(:search, '%')) ")
    public List<LinkedCaseInsensitiveMap> deductionForGrid(@Param("org_id") Long org_id, @Param("search") String search);

    @Query(nativeQuery = true, value = "Select * from deduction where organization_id=?1 and supervisor_status='Approved' and deduction_name Not IN('Bonus','Variable','Gratuity')")
    public List<Deduction> findApprovedDeductionsForExcelSheet(Long org_id);
}

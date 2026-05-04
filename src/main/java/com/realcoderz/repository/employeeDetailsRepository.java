/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.employeeDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author sharm
 */
@Repository
public interface employeeDetailsRepository extends JpaRepository<employeeDetails, Long>{
    
    
    @Query(nativeQuery=true, value="Select * from  employee_details where employee_id=?1 limit 1")
    public employeeDetails findByEmployeeId(Long employee_id);
    
    @Query(nativeQuery=true, value="select ad.bankaccount,ad.bankname,ad.ifsc,ed.address,ed.aadhar_number,ed.department_name,ed.dob,ed.email,ed.emp_desingnation,ed.employee_code,ed.employee_type,ed.esic,ed.gender,DATE_FORMAT( ed.joining_date,'%b %d, %Y') as joining_date,ed.employee_work_location as location,ed.grade,ed.lin,ed.mobile,ed.name,ed.pan_number,ed.pf,ed.uan from employee_details ed left join account_details ad ON ed.employee_id=ad.employeeid where ed.employee_id=?1 limit 1")
    public LinkedCaseInsensitiveMap getEmployeeDetails(Long employee_id);
    
    @Query(nativeQuery = true,value = "select employee_code,employee_id from employee_details where employee_code IN(?1) and organization_id=?2")
    public List<LinkedCaseInsensitiveMap> getEmployeeId(List<String> empcode,Long organization_id);
    
    @Query(nativeQuery=true, value="select ed.employee_id as employee_id,ad.bankaccount,ad.bankname,ad.ifsc,ed.address,ed.aadhar_number,ed.department_name,ed.dob,ed.email,ed.emp_desingnation,ed.employee_code,ed.employee_type,ed.esic,ed.gender,DATE_FORMAT( ed.joining_date,'%b %d, %Y') as joining_date,ed.employee_work_location as location,ed.grade,ed.lin,ed.mobile,ed.name,ed.pan_number,ed.pf,ed.uan from employee_details ed left join account_details ad ON ed.employee_id=ad.employeeid where ed.organization_id=?1")
    public List<LinkedCaseInsensitiveMap> findEmployeeDetails(Long organization_id);
    
    @Query(nativeQuery=true, value="Select * from  employee_details where employee_id in (:employeeIds)")
    public List<employeeDetails> findByEmployeeIds(@Param("employeeIds") List<Long> employee_ids);

    @Query(nativeQuery = true,value="select email ,name from  employee_details where organization_id=?1 and role IN('A','ACS','Ac')")
    public List<LinkedCaseInsensitiveMap> getAccountantOfOrg(Long organization_id);
    
    @Query(nativeQuery=true, value="select employee_work_location as location,name from employee_details where employee_id=?1 limit 1")
    public LinkedCaseInsensitiveMap getEmployeeDetailsForForm16(Long employee_id);
    
    @Query(nativeQuery = true,value = "SELECT name,employee_id as employeeId,employee_code as employeeCode FROM employee_details where organization_id=?1 and employee_type IN('Worker','worker')")
    public List<LinkedCaseInsensitiveMap> getWorkerEmployeeList(Long organization_id);
    
    @Query(value = "SELECT name,employee_id,employee_code FROM employee_details where organization_id=?1 and employee_type IN('full time','probation','permanent') ;", nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getFullTimeEmployeeList(Long organization_id);
    
    @Query(nativeQuery=true, value="Select * from  employee_details where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> findByOrgId(Long organization_id);
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.repository;

import com.realcoderz.model.EmployeeIdDates;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tause
 */
@Repository
public interface EmployeeIdDatesRepo extends JpaRepository<EmployeeIdDates, Long>
{
    @Query(value="Select * from employee_id_dates where organization_id=? and financial_year=?",nativeQuery = true)
    public List<EmployeeIdDates> employeeList(Long organizationId,Integer fyYear);
    
    @Query(value="Select * from employee_id_dates where employee_id=? and financial_year=?",nativeQuery = true)
    public EmployeeIdDates employeeIdDates(Long employeeId,String financaialYear);
    
    @Query(value="SELECT eid.employee_id,employee_id_dates_id,ed.name,ed.employee_code,eid.status, startdate,end_date,financial_year FROM employee_id_dates eid left join employee_details ed on eid.employee_id=ed.employee_id  where employee_requested = true and eid.organization_id=? and financial_year=?;",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> employeeRequestedWindowList(Long organizationId,Integer fyYear);
    
}

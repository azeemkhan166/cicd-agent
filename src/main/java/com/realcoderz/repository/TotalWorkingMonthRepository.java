/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;


import com.realcoderz.model.TotalWorkingMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lalit Raghav
 */
@Repository
public interface TotalWorkingMonthRepository extends JpaRepository<TotalWorkingMonth, Long>{
 
     @Query(nativeQuery = true,value = "select remain_total_month from total_working_month where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public int getTotalWorking(Long employeeid, Long organization_id, int month, int year);
    
    @Query(nativeQuery = true,value = "select diff_age from total_working_month where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public int getTotalAge(Long employeeid, Long organization_id, int month, int year);
    
    @Query(nativeQuery = true,value = "select count(*) from total_working_month where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public  int getTotalWorkingSaveOrNo(Long employeeid, Long organization_id, int month, int year);
    
    
    
    
}

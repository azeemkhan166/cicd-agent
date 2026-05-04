/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.EmployeeNetPay;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Mayank
 */
public interface EmployeeNetPayRepository extends JpaRepository<EmployeeNetPay, Long> {

    @Query(nativeQuery = true, value = "select net_pay from employee_net_pay where organization_id in(?1) and year in(?2) and month in(?3) order by month desc")
    public List<String> employeeNetPay(Long org_id, List<Integer> year, List<Integer> month);
    
    

}

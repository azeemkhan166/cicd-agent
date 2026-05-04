/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.PayPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Repository
public interface PayPlanRepository extends JpaRepository<PayPlan, Long> {

    @Query(nativeQuery = true, value = "Select site_id as siteId,id,days,employee_type as employeeType ,plan_name as planName, pay_mode as payMode,rate,times from pay_plan where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> findPayPlanById(Long org_id);

    @Query(nativeQuery = true, value = "Select basic_rate,ptamount, bonus,maximumvalue,percentage,secondsalaryflag,weekoffflag,site_id as siteId,id,days,rate,state_name,overtime,times from pay_plan where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> findPayPlanByIdAndEmpType(Long org_id);

    @Query(nativeQuery = true, value = "Select site_id as siteId,id,days,employee_type as employeeType ,plan_name as planName, pay_mode as payMode,rate,times from pay_plan where site_id=?1")
    public List<LinkedCaseInsensitiveMap> findPlanNameById(Long site_id);

    @Query(nativeQuery = true, value = "select id from pay_plan where plan_name=?1")
    public Long getPlanId(String planName);

}

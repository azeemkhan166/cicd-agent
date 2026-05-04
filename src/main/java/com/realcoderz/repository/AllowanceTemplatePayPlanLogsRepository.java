/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.AllowanceTemplatePayPlanLogs;
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
public interface AllowanceTemplatePayPlanLogsRepository extends JpaRepository<AllowanceTemplatePayPlanLogs, Long>{
    
    @Query(nativeQuery = true, value = "Select * from allowance_template_pay_plan_logs where pay_plan_id=?1")
    public List<LinkedCaseInsensitiveMap> findAllowanceLogsById(Long id);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.PayPlanLogs;
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
public interface PayPlanLogsRepository extends JpaRepository<PayPlanLogs, Long>{
    
@Query(
    nativeQuery = true,
    value = "SELECT p.*, " +
            "DATE_FORMAT(" +
            "   CONVERT_TZ(p.login_time, '+00:00', '+05:30'), " +
            "   '%d-%m-%Y %I:%i:%s %p'" +
            ") AS modify_dates " +
            "FROM pay_plan_logs p " +
            "WHERE p.pay_plan_id = ?1 " +
            "ORDER BY p.id DESC"
)
public List<LinkedCaseInsensitiveMap> findPayPlanLogsById(Long id);
}

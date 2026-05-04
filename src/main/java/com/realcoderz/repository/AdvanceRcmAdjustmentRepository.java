/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.AdvanceRcmAdjustment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface AdvanceRcmAdjustmentRepository extends JpaRepository<AdvanceRcmAdjustment, Long>{
    
    @Query(value = "SELECT *\n" +
"FROM (\n" +
"    SELECT ara.*,\n" +
"           ROW_NUMBER() OVER (\n" +
"               PARTITION BY ara.employee_id\n" +
"               ORDER BY ara.created_date DESC\n" +
"           ) AS rn\n" +
"    FROM advance_rcm_adjustment ara\n" +
"    WHERE ara.organization_id = ?1\n" +
"      AND ara.site_id = ?2\n" +
") t\n" +
"WHERE t.rn = 1",nativeQuery = true)
 public List<AdvanceRcmAdjustment> getAdvanceAdjustment(Long orgId,Long siteId  );

}

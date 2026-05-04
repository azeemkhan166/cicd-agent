/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.BonusYearly;
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
public interface BonusAmountRepository extends JpaRepository<BonusYearly, Long>{ 
    
    
    @Query(value="select year,status,bonus,employee_code as employeeCode,employee_id as employeeId,total_bonus_amount as totalBonusAmount,department_name as departmentName,bonus_percentage as bonusPercentage,emp_desingnation as empDesingnation,exgratia,name,organization_id as organizationId,total_basic_da as totalBasicDa,id from bonus_yearly where organization_id=?1 and name like CONCAT(?2, '%')",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getBonusDataByOrgId(Long organizationId,String search);

    @Query(value="select * from bonus_yearly where employee_id=?1 and year=?2 limit 1",nativeQuery = true)
    public BonusYearly bonusAmount(Long employeeId,Integer year);  
}

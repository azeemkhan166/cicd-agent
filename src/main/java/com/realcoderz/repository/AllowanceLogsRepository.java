/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;
import com.realcoderz.model.AllowanceLogs;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Repository
public interface AllowanceLogsRepository extends JpaRepository<AllowanceLogs, Long>
        
{       
        @Query(nativeQuery=true,value="Select * from allowance_logs where allowance_name=?1")
        public List<LinkedCaseInsensitiveMap> fetchDataByAllowanceName(String allowancename);
        
        
        

    
}

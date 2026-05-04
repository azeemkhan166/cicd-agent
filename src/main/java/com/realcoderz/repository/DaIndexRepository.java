/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.DaIndex;
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
public interface DaIndexRepository extends JpaRepository<DaIndex, Long> {

    @Query(nativeQuery = true, value = "select * from linking_factor")
    public List<LinkedCaseInsensitiveMap> getLinkingFactor();

    @Query(nativeQuery = true, value = "SELECT id AS Id, avgcpi AS avgCPI,CONCAT(\n"
            + "        DATE_FORMAT(STR_TO_DATE(CONCAT(consider_from, '-01-2023'), '%m-%d-%Y'), '%b'), \n"
            + "        '-', \n"
            + "        DATE_FORMAT(STR_TO_DATE(CONCAT(consider_to, '-01-2023'), '%m-%d-%Y'), '%b')\n"
            + "    ) AS considerFrom, consider_to AS considerTo, DATE_FORMAT(STR_TO_DATE(CONCAT(effective_from, '-01-2023'), '%m-%d-%Y'), '%M') AS effectiveFrom, DATE_FORMAT(STR_TO_DATE(CONCAT(effective_to, '-01-2023'), '%m-%d-%Y'), '%M') AS effectiveTo, name, year FROM da_index WHERE organization_id = ?1")
    public List<LinkedCaseInsensitiveMap> getAllData(Long id);
    
    @Query(nativeQuery = true,value = "select avgcpi,consider_to from da_index where organization_id=? order by id desc limit 1")
    public LinkedCaseInsensitiveMap getAvgCpi(Long organizationId);
    

}

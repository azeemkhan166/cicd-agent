/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.TravelAllowance;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Bipul Singh
 */
public interface TravelAllowanceRepository extends JpaRepository<TravelAllowance, Long> {

    public List<TravelAllowance> findByOrganizationId(Long organizationId);

    public TravelAllowance findByGroupId(Long groupId);

    @Query("FROM TravelAllowance ta where ta.groupId IN (:list)")
    public List<TravelAllowance> findByGroupIdList(@Param("list") List<Long> gropuIdList);

}
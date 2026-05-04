/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Section80cApproved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Bipul Singh
 */
@Repository
public interface Section80cApprovedRepository extends JpaRepository<Section80cApproved, Long> {

    @Query(nativeQuery = true, value = "Select * from section_c_approved where section_c_id=?1")
    public LinkedCaseInsensitiveMap findSection80cByIds(Long section_c_id);

}

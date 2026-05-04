/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Exemptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 
 * @author Mayank
 * edited By Astha
 */
@Repository
public interface ExemptionsRepository extends JpaRepository<Exemptions, Long> {

}

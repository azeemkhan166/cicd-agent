/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Admin
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class FulltimeAllowanceMapping{
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fulltimeAllowanceMappingId;
    private Long allowanceId;
    
}

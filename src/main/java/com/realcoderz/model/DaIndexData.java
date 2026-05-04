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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Admin
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class DaIndexData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long daId;
    private String consideredMonth;
    private Double indexValue;
    private Double linkingFactor2016;
    private Double linkingFactor2001;
    private Double linkingFactor1982;
    private Double daIndexValue;
    private Long uniqueId;
    
    
    
}

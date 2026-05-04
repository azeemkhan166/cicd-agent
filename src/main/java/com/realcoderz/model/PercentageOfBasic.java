/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Lalit Raghav
 */
@Entity
@Setter
@Getter
@NoArgsConstructor

public class PercentageOfBasic extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "percentageOfBasic_id", length = 5)
    private Long percentage_of_basic_id;
      
    @Column(name = "organization_id", length = 5)
     private Long organization_id;
    
    @Column(name = "metro_basicpercentage", length = 16)
    private String metro_basicpercentage;
    
    @Column(name = "non_metro_basicpercentage", length = 16)
    private String non_metro_basicpercentage;
    
    
    
    
}

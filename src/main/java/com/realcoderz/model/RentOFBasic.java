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
 * @author lalit
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class RentOFBasic extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rent_Of_Basic_id", length = 5)
    private Long rent_Of_Basic_id;
    
    @Column(name = "organization_id", length = 5)
     private Long organization_id;
    
    @Column(name = "basic_percentage", length = 16)
    private String basic_percentage;

    
}

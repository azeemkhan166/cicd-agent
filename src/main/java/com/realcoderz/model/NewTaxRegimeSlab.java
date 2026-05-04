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
import lombok.ToString;

/**
 *
 * @author Lalit Raghav
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@ToString
public class NewTaxRegimeSlab extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "newTaxRegimeSlab_id", length = 7)
    private Long newTaxRegimeSlab_id;

    @Column(name = "start", length = 7)
    private double start;

    @Column(name = "end", length = 7)
    private double end;
    
    @Column(name = "startAge", length = 7)
    private double startAge;
    
    @Column(name = "endAge", length = 7)
    private double endAge;

    @Column(name = "rate", length = 7)
    private double rate;

    @Column(name = "organization_id", length = 7)
    private Long organization_id;
    
     @Column(name = "financialYear", length = 7)
    private Integer financialYear;
    
    
    
    
}

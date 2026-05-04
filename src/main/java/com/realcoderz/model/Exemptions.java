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
 
 * @author Mayank
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Exemptions extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exemptions_id", length = 5)
    private Long exemptions_id;

    @Column(name = "exemptions_under_sec_VIA", length = 7)
    private double exemptions_under_sec_VIA;

    @Column(name = "exemptions_under_sec_10", length = 7)
    private double exemptions_under_sec_10;

    @Column(name = "standard_deduction_under_sec_16", length = 5)
    private double standard_deduction_under_sec_16;

    @Column(name = "income_loss_house_property", length = 7)
    private double income_loss_house_property;

}

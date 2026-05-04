/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author tause
 */
@Entity
@Getter
@Setter
public class ProfessionalTaxNew extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professionalTaxId;
    private Double minSalary;
    private Double maxSalary;
    private String gender;
    private Double taxAmount;
    private String stateName;
    private int startMonth;
    private int endMonth;
    private int financialYear;
    private String frequencyOfDeduction;
    
    
}

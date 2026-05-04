/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author tause
 */

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LabourLawDeduction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labourLawDeductionId;
    private String stateName;
    private String frequencyOfDeduction;
    private Double employeeDeduction;
    private Double employerDeduction;
    private Double totalContribution;
    private Double percentageOfSalary;
    private Double start;
    private Double end;
    
    
}

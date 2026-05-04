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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Data
@Entity
@ToString
@EqualsAndHashCode
public class TempararyDeduction extends Auditable<String>{
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dummyDeductionId;
    
    private String name;
    private Long deductionId;
    private Double amount;
    private Long employeeId;
    private Long organizationId;
    private Integer month;
    private Integer year;
    private String updateTds;
    
}

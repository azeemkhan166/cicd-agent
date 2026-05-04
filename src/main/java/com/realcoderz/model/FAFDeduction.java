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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author sharm
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FAFDeduction extends Auditable<String>{
    
       @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
     
    String deduction_name;
    Long deduction_id;
    Double deduction_amount;
    Double deduction_payable_amount;
    Long employee_id;
    Long organization_id;
    String type_of_deduction;
    Boolean editable;
    Double employer_percentage;
    Long deduction_template_id;
    String consultant_deduction_name;
    Double consultnat_deduction_payable_amount;
    
}

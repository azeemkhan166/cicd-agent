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
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */

@Entity
@Table(name = "deduction_allowance_mapping")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DeductionAllowanceMapping extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deduction_allowance_mapping_id", length = 7)
    private Long deduction_allowance_mapping_id;
    
    @Column(name = "deduction_id", length = 7)
    private Long deduction_id;
    
    @Column(name = "allowance_id", length = 7)
    private Long allowance_id;
    
}

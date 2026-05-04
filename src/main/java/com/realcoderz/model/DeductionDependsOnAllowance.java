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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
public class DeductionDependsOnAllowance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long deductionTemplateAllowanceMappingId;
    private Long id;
    
}

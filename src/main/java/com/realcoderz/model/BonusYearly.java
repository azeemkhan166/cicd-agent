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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BonusYearly  extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String employeeCode;
    private String empDesingnation;
    private String departmentName;
    private Double totalBasicDa;
    private Double totalBonusAmount;
    private Double bonus;
    private Double exgratia;
    private Double bonusPercentage;
    private Long employeeId;
    private Long organizationId;
    private String status;
    private Long year;
}

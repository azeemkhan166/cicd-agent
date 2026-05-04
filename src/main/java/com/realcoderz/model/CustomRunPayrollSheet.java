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
@ToString
@NoArgsConstructor
public class CustomRunPayrollSheet extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   
    private String name;
    private Long employeeId;
    private Long organizationId;
    private String employeeCode;
    private String site;
    private Long siteId;
    private Double netAmount;
    private Integer year;
    private Integer month;
    private String status;
    private String accountNo;
    private String ifsc;
    private String amountDepend;
    
}

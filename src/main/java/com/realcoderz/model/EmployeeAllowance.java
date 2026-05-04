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
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class EmployeeAllowance extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 5)
    private Long id;

    @Column(name = "employee_id", length = 5)
    private Long employee_id;

    @Column(name = "allowance_id", length = 5)
    private Long allowance_id;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

    @Column(name = "allowance_amount", length = 7)
    private double allowance_amount;
    
    @Column(name = "allowance_payable_amount", length = 7)
    private Double allowance_payable_amount;
    
    @Column(name = "month", length = 2)
    private Integer month;
    
    @Column(name = "year", length = 4)
    private Integer year;
    
     @Column(name = "consultant_allowance_name", length = 60)
    private String consultant_allowance_name;
     
    @Column(name = "consultant_allowance_amount", length = 60)
    private Double consultant_allowance_amount;
      
    @Column(name = "consultnat_allowance_payable_amount", length = 7)
    private Double consultnat_allowance_payable_amount;
    
    @Column(name = "employee_type", length = 60)
    private String employee_type;
    
    @Column(name = "salary_breakup_id", length = 60)
    private Long salary_breakup_id;

    @Transient
    private String projectionMonth;
    
    @Transient
    private String projectionYear;
    
    private String type_of_allowance;
     
    private Double percentage;
    
    private Long allowance_template_id;
}


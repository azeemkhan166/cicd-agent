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
 * @author Astha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class EmployeeDeduction extends Auditable<String>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 5)
    private long id;

    @Column(name = "employee_id", length = 5)
    private Long employee_id;

    @Column(name = "deduction_id", length = 5)
    private Long deduction_id;

    @Column(name = "organization_id", length = 5)
    private int organization_id;

    @Column(name = "deduction_amount", length = 7)
    private double deduction_amount;

    @Column(name = "deduction_payable_amount", length = 7)
    private Double deduction_payable_amount;
    
    @Column(name = "month", length = 2)
    private Integer month;
    
    @Column(name = "consultant_deduction_name", length = 60)
    private String consultant_deduction_name;
     
      @Column(name = "consultant_deduction_amount", length = 60)
    private Double consultant_deduction_amount;
    
    @Column(name = "employee_type", length = 60)
    private String employee_type;
    
    @Column(name = "consultnat_deduction_payable_amount", length = 7)
    private Double consultnat_deduction_payable_amount;
    
    @Column(name = "year", length = 4)
    private Integer year;
    
    @Column(name = "ytd_deduction" , length = 9 )
    private Double ytd_deduction;
    
    @Column(name = "salary_breakup_id", length = 60)
    private Long salary_breakup_id;
    
    @Transient
    private String projectionMonth;
    
    @Transient
    private String projectionYear;
    
    private Double employer_percentage;
    
    private Long deduction_template_id;
}

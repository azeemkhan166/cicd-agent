/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
public class DeductionTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long deductionTemplateId;
    private String deductionType;
    private Double employeePercentage;
    private Double employerPercentage;
    private String salary;
    private Double amount;
    private Long applicablityId;
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval = true)
    @JoinColumn(name="deduction_template_id",nullable=false)
    private Set<EmployeeDeductionTemplateMapping> employeeIds; 
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval = true)
    @JoinColumn(name="deduction_template_id",nullable=false)
    private Set<DeductionAllowanceTemplateMapping> deductionAllwanceMapping;
    private Boolean editable;
}

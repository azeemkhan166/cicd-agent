/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */
@Entity
@Table(name = "deduction")
@Getter
@Setter
@EqualsAndHashCode
@ToString

public class Deduction extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deduction_id", length = 5)
    private Long deduction_id;

    @Column(name = "deductiondesc", length = 100)
    private String deductiondesc;

    @Column(name = "deduction_name", length = 30)
    private String deduction_name;

    @Column(name = "amount", length = 7)
    private Double amount;

    @Column(name = "percentage", length = 2)
    private Double percentage;

    @Column(name = "type_of_deduction", length = 10)
    private String type_of_deduction;

    @Column(name = "salary", length = 10)
    private String salary;

    @Column(name = "status", length = 8)
    private String status;

    @Column(name = "deduction", length = 10)
    private String deduction;
    
    @Column(name = "effective_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effective_date;
    
    @Column(name = "supervisor_status", length = 10)
    private String supervisor_status;
    
    @Column(name = "organization_id", length = 5)
    private Long organization_id;
    
    @Column(name = "employee_type", length = 20)
    private String employee_type;
    
    @Column(name="employee_email")
    private String employee_email;
    
    @Column(name="approved_flag")
     private String approved_flag;
    
    @Column(name = "employer_percentage", length = 2)
    private Double employer_percentage;   
    @Transient
    private List<LinkedHashMap> deductionOnAllowance;
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval = true)
    @JoinColumn(name="deduction_id",nullable=false)
    private List<DeductionTemplate> deductionTemplate;
    
    private String applicableFor;
    
    private Boolean loanFlag;
    
}

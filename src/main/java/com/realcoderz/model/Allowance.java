/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.util.Date;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */
@Entity
@Table(name = "allowance")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Allowance extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allowance_id", length = 7)
    private Long allowance_id;

    @Column(name = "allowancedesc", length = 100)
    private String allowancedesc;

    @Column(name = "allowance_name", length = 30)
    private String allowance_name;

    @Column(name = "allowance_type", length = 15)
    private String allowance_type;

    @Column(name = "amount", length = 7)
    private Double amount;

    @Column(name = "percentage", length = 2)
    private Double percentage;

    @Column(name = "type_of_allowance", length = 10)
    private String type_of_allowance;

    @Column(name = "salary", length = 10)
    private String salary;

    @Column(name = "status", length = 8)
    private String status;

    @Column(name = "allowance", length = 10)
    private String allowance;

    @Column(name = "supervisor_status", length = 10)
    private String supervisor_status;

    @Column(name = "effective_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effective_date;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

    @Column(name = "employee_type", length = 20)
    private String employee_type;

    @Column(name = "employee_email")
    private String employee_email;

    @Column(name = "approved_flag")
    private String approved_flag;

    @Column(name = "incentive_min", length = 5)
    private Integer incentive_min;

    @Column(name = "incentive_max", length = 5)
    private Integer incentive_max;
    
    @Column(name="standard_hours", length=4)
    private Double standard_hours;
    
    @Transient
    private Object allowanceOnAllowance;
    
    @Transient
    private Object subAllowanceIds;
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval = true)
    @JoinColumn(name="allowance_id",nullable=false)
    private List<AllowanceTemplate> allowanceTemplate;
    
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name="allowance_id",nullable=false)
    private List<AllowancePaymentMonths> allowancePayableMonths;
    
    private String applicableFor;
    
    private Double excludeAmount;
    private Double includeAmount;


}

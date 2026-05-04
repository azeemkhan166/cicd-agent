/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
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
public class PayPlan extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long organizationId;
    private String employeeType;
    private Long siteId;
    private String payMode;
    private String planName;
    private String description;
    private String skilledLevelType;
    private String overtime;
    private Double times;
    private Double rate;
    private String arrear;
    private String reimb;
    private String incentive;
    private Long days;
    @Transient
    private Double grossSalary;
    @Transient
    private Double netPayable;
    private String ptNumber;
    private String stateName;
    private String modifyBy;
    private String weekoffflag;
    private String secondsalaryflag;
    private String bonus;
    private Double percentage;
    private Double maximumvalue;
    private String ptamount;
    private Double basicRate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "pay_plan_id", nullable = false)
    private List<AllowanceTemplatePayPlan> allowanceTemplatePayPlan;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "pay_plan_id", nullable = false)
    private List<DeductionTemplatePayPlan> deductionTemplatePayPlan;

}

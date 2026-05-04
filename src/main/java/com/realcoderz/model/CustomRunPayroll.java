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
@ToString
@NoArgsConstructor
public class CustomRunPayroll extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long employeeId;
    private Long organizationId;
    private String employeeCode;
    private String site;
    private Double grossGalary;
    private Double basicRate;
    private Double workingDay;
    private Double ph;
    private Double otHours;
    private Double gateBasicRate;
    private Double epfDays;
    private Double gatePh;
    private Double basicSalary;
    private Double epf;
    private Double esic;
    private Double pt;
    private Double glwb;
    private Double coupan;
    private Double gateDeduction;
    private Double paymentGate;
    private Double secondPart;
    private Double salaryPayable;
    private Double otWages;
    private Double addiIncentive;
    private Double hoAdvance;
    private Double siteAdvance;
    private Double otherDeduction;
    private Double totalDeduction;
    private Double netPayable;
    private Double annualCtc;
    private String modeOfPayment;
    private String payrollStatus;
    private Long year;
    private Long month;
    private Long siteId;
    private Long payPlanId;
    private Double presentDay;
    @Transient
    private Long sid;
    private String employeeType;
    @Transient
    private Double currentMonthAdvance;
    @Transient
    private Double remainingAdvance;
    @Transient
    private Double totalAdvance;

    private String overTimeFormatted;
    private Double epfWages;
    private Double hra;
    private Double restAllowance;
    private Double grossWages;
    private Double bonus;
    private String getPaymentStatus;
    private String netPayableStatus;
    private Double esicWages;

}

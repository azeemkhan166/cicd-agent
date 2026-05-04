/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package com.realcoderz.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import javax.persistence.Column;
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
 * @author bipulsingh
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RunPayRoll extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "runPayRollId", length = 20)
    private long runPayRollId;

    private String name;

    private Long employeeId;

    private Long organizationId;

    private Double salary;

    private Double payable;

    private Double epf;

    private Double esic;

    private Double tds;

    private Double employer_epf;

    private Double working_day;

    private Double other_deductions;

    private Double reimburs;

    private Double adhoc;

    private Double overtimePay;

    private Double net_payable;

    private Integer payRunMonth;

    private Integer payRunYear;

    private String totalHours;

    private String overTime;

    private Double bonus;

    private String employee_type;

    private Double Advance;

    private Double professional_tax;

    private String employee_code;

    private Double employer_esic;

    private Double gratuity;

    private Double variable;

    private Double ctc;

    private Double rate;

    private Double bonus_deduction;

    @Transient
    public Long employee_id;

    @Transient
    public String employeeType;
    
    private Double attendanceIncentives;
    
    private Double annual_ctc;
    
    private Date joiningDate;
    
    private String modeofpayment;
    
    private Double labourWelfareFund;
    
    private Double arrears;
    
    @Transient
    private Double baiscda;
    
    private Long orgIds;   
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Lalit Raghav
 */
@ToString
@EqualsAndHashCode
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SalaryBreakUp extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Sid", length = 5)
    private Long Sid;

    @Column(name = "gross_salary", length = 7)
    private double gross_salary;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

    @Column(name = "total_earning", length = 7)
    private Double total_earning;

    @Column(name = "total_payable_earning", length = 7)
    private Double total_payable_earning;

    @Column(name = "payable_salary", length = 7)
    private Double payable_salary;

    @Column(name = "employee_id", length = 5)
    private int employee_id;

    @Column(name = "total_deduction", length = 7)
    private Double total_deduction;

    @Column(name = "net_amount", length = 7)
    private Double net_amount;

    @Column(name = "month", length = 2)
    private Integer month;

    @Column(name = "year", length = 4)
    private Integer year;

    @Column(name = "pdf_url", length = 255)
    private String pdf_url;

    @Column(name = "employee_type", length = 60)
    private String employee_type;

    @Column(name = "working_day", length = 2)
    private Double working_day;

    @Column(name = "present_day", length = 2)
    private Double present_day;

    @Column(name = "approved_leave", length = 2)
    private Double approved_leave;

    @Column(name = "week_off", length = 2)
    private Double week_off;

    @Column(name = "holidays", length = 2)
    private Double holidays;

    @Column(name = "lwp", length = 2)
    private Double lwp;

    @Column(name = "total_hours", length = 5)
    private Double total_hours;

    @Column(name = "over_time", length = 5)
    private Double over_time;

    @Column(name = "actual_day", length = 2)
    private Double actual_day;

    @Column(name = "ytd_total_deduction", length = 7)
    private Double ytd_total_deduction;

    @Column(name = "percentage_change", length = 4)
    private String percentage_change;

    @Column(name = "rate", length = 4)
    private Double rate;

    @Column(name = "epf", length = 4)
    private String epf;

    @Column(name = "total_day", length = 2)
    private Double total_day;

    @Column(name = "salary_breakup_type", length = 10)
    private String salary_breakup_type;

    @Column(name = "effective_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effective_date;

    @Transient
    private String projectionMonth;

    @Transient
    private String projectionYear;

    private String salaryHold;

    private Double tds;

    private Double annual_ctc;

    private Date joiningDate;

    private String modeofpayment;

    private String isEsic;

    private String basicSalary;

    private String voluntaryEpf;

    private Double voluntaryEpfPercentage;

    private Long payPlanId;

    private Long siteId;

    @Transient
    private String name;
    @Transient
    private String employeeCode;

    @Transient
    private String siteName;

    @Transient
    private String gender;

    private Double baiscDA;

    @Transient
    private String overTimeFormatted;

    @Transient
    private String epfWages;

    private Long orgIds;

    @Transient
    private String esicWages;

}

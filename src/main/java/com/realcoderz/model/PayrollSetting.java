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
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Astha 
 */
@Entity
@Table(name = "payroll_setting")
@Getter
@Setter
@NoArgsConstructor
public class PayrollSetting extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payroll_setting_id", length = 5)
    private Long payroll_setting_id;

    @Column(name = "financial_year", length = 10)
    private String financial_year;

    @Column(name = "start_date", length = 15)
    private String start_date;

    @Column(name = "end_date", length = 15)
    private String end_date;

    @Column(name = "payslip_generation_date", length = 2)
    private String payslip_generation_date;

    @Column(name = "weekend", length = 30)
    private String weekend;

    @Column(name = "last_pay_run_month")
    private Integer last_pay_run_month;

    @Column(name = "last_pay_run_year")
    private Integer last_pay_run_year;

    @Column(name = "organization_id", length = 8)
    private Long organization_id;
    
    private String leaveEncashment;

}

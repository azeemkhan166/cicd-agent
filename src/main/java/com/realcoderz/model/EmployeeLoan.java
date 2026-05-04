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
import javax.persistence.Table;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */
@Entity
@Table(name = "employee_loan")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class EmployeeLoan extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_loan_id", length = 7)
    private Long employee_loan_id;
    
    @Column(name="employee_id", length=50)
    private Long employee_id;
    
    @Column(name="description", length=255)
    private String description;
    
    @Column(name="loan_amount", length=7)
    private Double loan_amount;
    
    @Column(name="start_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date start_date;
    
    @Column(name="tenure", length=3)
    private Integer tenure;
    
    @Column(name="installment_amount", length=7)
    private Double installment_amount;
    
    @Column(name="accountant_status", length=10)
    private String accountant_status;
    
    @Column(name="supervisor_status", length=10)
    private String supervisor_status;
    
    @Column(name="organization_id", length=4)
    private Long organization_id;
    
    @Column(name="loan_requested_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date loan_requested_date;
    
    @Column(name="loan_approved_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date loan_approved_date;
    
    @Column(name="loan_approved_amount",length=7)
    private Double loan_approved_amount;
    
    @Column(name="remaining_amount", length=7)
    private Double remaining_amount;
    
    @Column(name="employee_name", length=50)
    private String employee_name;
    
    @Column(name="amount_repaid", length=7)
    private Double amount_repaid;

    @Column(name="loan_status", length=20)
    private String loanStatus;

}

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
import lombok.Data;

/**
 *
 * @author Admin
 */
@Data
@Entity
public class DeductionLoan extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deductionLoanId;
    
    private String employeeName;
    private String description;
    private String loanType;
    private Long loanTypeId;
    private Double demandLoan;
    private String startDate;
    private Double monthlyInstallment;
    private Double remainingAmount;
    private Double amountRepaid;
    private Double tenure;
    private Long employeeId;
    private String  accountantStatus;
    private String  supervisorStatus;
    private Long organizationId;
    @Transient
    private Double discount;
    
    
    
}

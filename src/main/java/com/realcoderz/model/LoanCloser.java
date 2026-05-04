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
public class LoanCloser  extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String closeLoanType;
    private Long deductionLoanId;
    private Double discount;
    private Long employeeId;
    private Double loanRepay;
    private String loanType;
    private Long loanTypeId;
    private String paymentDate;
    private Double principal;
    private Double remainingLoan;
    private String remark;
    private Double tenure;
    private Long closedBy;
    private Long organizationId;
}

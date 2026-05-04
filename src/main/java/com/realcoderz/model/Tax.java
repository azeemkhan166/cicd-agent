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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Mayank
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tax {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_id")
    private Long tax_id;

    @Column(name = "total_income", length = 10)
    private double total_income;

    @Column(name = "tax_total_income", length = 10)
    private double tax_total_income;

    @Column(name = "surcharge_income", length = 10)
    private double surcharge_income;

    @Column(name = "education_cess", length = 7)
    private double education_cess;

    @Column(name = "tax_payable", length = 7)
    private double tax_payable;

    @Column(name = "relief_89", length = 7)
    private double relief_89;

    @Column(name = "total_tax_liability", length = 7)
    private double total_tax_liability;

    @Column(name = "tax_deducted_previous_employer", length = 7)
    private double tax_deducted_previous_employer;

    @Column(name = "total_tax_deducted_till_date", length = 7)
    private double total_tax_deducted_till_date;

    @Column(name = "remaining_tax", length = 7)
    private double remaining_tax;

    @Column(name = "tax_deduction_month", length = 7)
    private double tax_deduction_month;

}

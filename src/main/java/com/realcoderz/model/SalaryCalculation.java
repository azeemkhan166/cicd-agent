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
 * @author Lalit raghav
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SalaryCalculation extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_calculation_id", length = 5)
    private Long salary_calculation_id;

    @Column(name = "sub_total", length = 7)
    private double sub_total;

    @Column(name = "rent_paid", length = 7)
    private double rent_paid;

    @Column(name = "basic", length = 7)
    private double basic;
    
    @Column(name = "hra", length = 7)
    private double hra;
    
    @Column(name = "lta", length = 7)
    private double lta;
    
    @Column(name = "hra_received", length = 7)
    private double hra_received;


    @Column(name = "rent_basic_difference", length = 7)
    private double rent_basic_difference;

    @Column(name = "least_hra", length = 7)
    private double least_hra;

    @Column(name = "taxable_hra", length = 7)
    private double taxable_hra;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

    @Column(name = "employee_id", length = 5)
    private Long employee_id;

}

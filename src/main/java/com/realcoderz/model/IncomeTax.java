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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class IncomeTax extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tax_allowance_id")
    private Long tax_allowance_id;

    @Column(name = "salary_hra_name", length = 100)
    private String salary_hra_name;

    @Column(name = "salary_hra_amount", length = 20)
    private String salary_hra_amount;

    @Column(name = "exemption_name", length = 100)
    private String exemption_name;
    
    @Column(name = "exemption_declared_amount", length = 20)
    private String exemption_declared_amount;
    
    @Column(name = "exemption_exempted_amount", length = 20)
    private String exemption_exempted_amount;
    
    @Column(name = "tax_name", length = 100)
    private String tax_name;
    
    @Column(name = "tax_amount", length = 20)
    private String tax_amount;
    
    @Column(name = "month", length = 2)
    private int month;
    
    @Column(name = "year", length = 4)
    private int year;
    
    @Column(name = "employee_id", length = 10)
    private Long employee_id;
    
    @Column(name = "organization_id", length = 5)
    private Long organization_id;

}

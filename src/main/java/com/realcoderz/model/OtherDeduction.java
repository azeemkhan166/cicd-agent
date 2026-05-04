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
public class OtherDeduction extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "other_deduction_id")
    private Long other_deduction_id;

    @Column(name = "deduction_name", length = 30)
    private String deduction_name;

    @Column(name = "amount", length = 7)
    private Double amount;
    
    @Column(name = "employee_id", length = 5)
    private int employee_id;

    @Column(name = "organization_id", length = 5)
    private int organization_id;
    
    @Column(name = "month", length = 2)
    private Integer month;
    
    @Column(name = "year", length = 4)
    private Integer year;

}

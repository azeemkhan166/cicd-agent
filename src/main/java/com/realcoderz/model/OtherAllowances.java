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
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank & astha
 */
@Entity
@Table(name = "otherAllowances")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OtherAllowances extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "other_allowances_id", length = 5)
    private Long other_allowances_id;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "amount", length = 7)
    private Double amount;
    
    @Column(name = "payable_amount", length = 7)
    private Double payable_amount;

    @Column(name = "employee_id", length = 5)
    private int employee_id;

    @Column(name = "organization_id", length = 5)
    private int organization_id;
    
    @Column(name = "month", length = 2)
    private Integer month;
    
    @Column(name = "year", length = 4)
    private Integer year;
    
    @Column(name = "salary_breakup_id", length = 60)
    private Long salary_breakup_id;
    
    @Transient
    private String projectionMonth;
    
    @Transient
    private String projectionYear;

}

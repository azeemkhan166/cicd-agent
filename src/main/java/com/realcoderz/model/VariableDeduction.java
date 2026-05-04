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
@Table(name = "variable_amount")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class VariableDeduction extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "variable_id", length = 3)
    private Long variable_id;

    @Column(name = "amount", length = 3)
    private Double amount;

    @Column(name = "organization_id", length = 7)
    private Long organization_id;

    @Column(name = "employee_id", length = 7)
    private Long employee_id;

    @Column(name = "month", length = 2)
    private Integer month;

    @Column(name = "year", length = 2)
    private Integer year;

    @Column(name = "salary_breakup_id", length = 5)
    private Long salary_breaup_id;
    
    @Column(name = "effective_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effective_date;

}

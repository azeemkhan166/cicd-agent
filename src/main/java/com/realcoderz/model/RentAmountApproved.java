/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Bipul Singh
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RentAmountApproved extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rent_amount_id", length = 5)
    private Long rent_amount_id;

    @Column(name = "declaration_id", length = 5)
    private Long declaration_id;

    @Column(name = "rent_month")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date rent_month;

    @Column(name = "amount", length = 12)
    private Double amount;

}

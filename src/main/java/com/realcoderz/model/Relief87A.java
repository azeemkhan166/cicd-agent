
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Mayank
 */
@Entity
@Table(name = "relief_87a")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Relief87A extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relief_87a_id", length = 3)
    private Long relief_87a_id;

    @Column(name = "income", length = 7)
    private double income;

    @Column(name = "rate", length = 7)
    private double rate;

    @Column(name = "organization_id", length = 7)
    private Long organization_id;

}

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
@Table(name = "cess")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Cess extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cess_id", length = 3)
    private Long cess_id;

    @Column(name = "rate", length = 3)
    private double rate;

    @Column(name = "organization_id", length = 7)
    private Long organization_id;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
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
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Lalit Raghav
 */
@Entity
@Getter
@Table(name = "inverstment_declaration")
@Setter
@NoArgsConstructor
public class InvestmentDeclaration extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "declaration_id", length = 5)
    private Long declaration_id;

    @Column(name = "employeeid", length = 5)
    private Long employeeid;

    @Column(name = "organizationid", length = 5)
    private Long organizationid;


    @Column(name = "total_allowances", length = 12)
    private double total_allowances;

    @Column(name = "total_rent", length = 12)
    private double total_rent;

     @Column(name = "fy_year", length = 60)
     private int fy_year;

     @Column(name = "taxSlabTpye", length = 60)
     private String taxSlabTpye;

     @Column(name = "taxSave", length = 60)
     private String taxSave;

     private Boolean approvedByAcc;

     private String submittedBy;

}

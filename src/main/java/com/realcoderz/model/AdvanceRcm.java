/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class AdvanceRcm extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long siteId;
    private Long employeeId;
    private Double amount;
    private Double rcmAmount;
    private Double hoAmount;
    private String rcmStatus;
    private String hoStatus;
    private String supervisorStatus;
    private String paid;
    private String date;
    private Long year;
    private String hoDate;
    
}

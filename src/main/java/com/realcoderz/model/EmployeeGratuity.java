/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author tause
 */
@Entity
@Getter
@Setter
public class EmployeeGratuity {
    
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gratuity_id;
    private Double amount;
    private Long organization_id;
    private Long employee_id;
    private Integer month;
    private Double employerPercentage;
    private Integer year;
    private Long salary_breakup_id; 
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effective_date;
    
    
}

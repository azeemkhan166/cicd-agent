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
 * @author lalit Radgav
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SalaryBreakupLogs extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salaryBreakupLogs_id", length = 5)
    private Long salaryBreakupLogs_id;
    
    @Column(name = "employeeid", length = 5)
     private Long employeeid;
    
    @Column(name="organization_id",length =5)
    private Long organization_id;
    
    @Column(name="employee_name")
    private String employee_name;
    
    @Column(name="employee_email")
    private String employee_email;
    
   
    
}

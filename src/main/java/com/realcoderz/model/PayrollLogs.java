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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author sharm
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayrollLogs extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payrollLogId;
    
    private String description;
    
    private String columnName;
    
    private Long employeeId;
    
    private Long updatedValue;
    
    private Long organizationId;
    
}

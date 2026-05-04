/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import lombok.Data;

/**
 *
 * @author Admin
 */
@Entity
@Data
public class AppraisalSalary extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long employeeId;
    private Long organizationId;
    private Double previousAnnualCtc;
    private Double previousMonthlyGross;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date effectiveDate;
    private Double revisedAnnualCtc;
    private Double variablePart;
    private Double employerCost;
    private Double monthlyGross;
    private Double incrementAmount;
    private Double incrementPercent;
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author tause
 */
@Getter
@Setter
@Entity
public class EmployeeIdDates extends Auditable<String>
{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeIdDatesId;
    private Long employeeId;
    private String status;
    private boolean employeeRequested;
    private Integer financialYear;
    private Long organizationId;
    private String startdate;
    private String endDate;
    
}

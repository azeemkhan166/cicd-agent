/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author tause
 */
@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode
public class AllowanceTemplate 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long allowanceTemplateId;
    private Long applicablityId;
    private String typeOfAllowance;
    private String salary;
    private Double percentage;
    private Double amount;
    private Double standardHours;
    private Long allowanceMappedId;
    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER,orphanRemoval = true)
    @JoinColumn(name="allowance_template_id",nullable=false)
    private List<EmployeeTemplateMapping> employeeIds;
    private Boolean editable;
    private Double minimumWorkingDay;
    private Double minimumValue;
    private Double maximumValue;
    private Double linkingFactor;
    private Boolean aiFlag;
    
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name="allowance_template_id")
    private List<FulltimeAllowanceMapping> allowanceIdsForMapping;
    
    

}

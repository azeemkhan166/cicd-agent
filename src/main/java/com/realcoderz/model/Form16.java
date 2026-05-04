package com.realcoderz.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Form16 {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long formId;
    private Long employeeId;
    private Long organizationId;
    
    @Column(name = "year", length = 4)
    private int year;
    private String certificateNo;
    private LocalDateTime lastUpdatedOn = LocalDateTime.now();
    
    private String oneC = "0.00";
    private String twoA = "0.00";
    private String twoB = "0.00";
    private String twoC = "0.00";
    private String twoD = "0.00";
    private String twoF = "0.00";
    private String twoG = "0.00";
    private String fourA = "50000";
    private String fourB = "0.00";
    private String sevenB = "0.00";
    
    private String tenCA = "0.00";
    private String tenCB = "0.00";
    private String tenEA = "0.00";
    private String tenEB = "0.00";
    private String tenFA = "0.00";
    private String tenFB = "0.00";
    private String tenJA = "0.00";
    private String tenJB = "0.00";
    private String tenJC = "0.00";
    private String tenLA = "0.00";
    private String tenLB = "0.00";
    private String tenLC = "0.00";
    
    private String name;
    private String fatherName;
    private String designation;
    private String place;
    
}

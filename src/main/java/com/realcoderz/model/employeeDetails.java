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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author sharm
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class employeeDetails extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long empId;
    
    private String dob;
    private String email;
    private String empDesingnation;
    private String employeeCode;
    private Long employeeId;
    private String employeeType;
    private String esic;
    private String gender;
    private String joiningDate;
    private String lin;
    private Long mobile;
    private String name;
    private String panNumber;
    private String pf;
    private String uan;
    private String aadharNumber;
    private String departmentName;
    private String ifsc;
    private String bankAccount;
    private String bankName;
    private String branch;
    private String address;
    private String employeeWorkLocation;
    private String grade;
    private Long organizationId;
    private String role;
    private String status;
    
}

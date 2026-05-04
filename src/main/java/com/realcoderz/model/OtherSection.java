/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import com.sun.istack.NotNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Lalit Raghav
 */
@Entity
@Setter
@Getter
@Table(name="other_section")
public class OtherSection extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "other_section_id", length = 5)
    private Long other_section_id;
    
    @Column(name = "declaration_id", length = 5)
    private Long declaration_id;
    
    @Column(name = "status", length = 10)
    private String status; 
   
    @Column(name = "interest_on_housing_loan_before", length = 12)
    private double interest_on_housing_loan_before;

    @Column(name = "national_pension_scheme", length = 12) 
    private double national_pension_scheme;
             
    @Column(name = "sec80d", length = 12)       
    private double sec80d;
       
    @Column(name = "sec80d_type", length = 40)       
    private String sec80d_type;
             
    @Column(name = "sec80dd", length = 12)          
    private double sec80dd;
              
     @Column(name = "sec80e", length = 12)
    private double sec80e;
             
    @Column(name = "sec80u", length = 12)
    private double sec80u;
              
    @Column(name = "sec80g", length = 12)
    private double sec80g;
                
     @Column(name = "interest_income_fromsaving", length = 12)           
    private double interest_income_fromsaving;
                 
    @Column(name = "income_fromPrevious_Employer", length = 12)
    private double income_fromPrevious_Employer;
                 
        @Column(name = "pf", length = 12)
        private double pf;
                 
     @Column(name = "professional_tax", length = 12)
    private double professional_tax;
                 
     @Column(name = "tds", length = 12)
    private double tds;


                 
                 
    
    
}

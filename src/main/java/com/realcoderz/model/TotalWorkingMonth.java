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
 * @author lalit
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TotalWorkingMonth extends Auditable<String> {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthId", length = 5)
    private Long monthId;
    
    @Column(name = "employee_id", length = 16)
     private Long employee_id;
    
    @Column(name = "organization_id", length = 16)
     private Long organization_id;
      
     @Column(name = "RemainTotalMonth", length = 16)
     private int RemainTotalMonth;
     
     @Column(name = "month", length = 16)
     private int month;
     
     @Column(name = "year", length = 16)
     private int year;
     
     @Column(name = "diff_age", length = 16)
     private int diff_age;
     
}

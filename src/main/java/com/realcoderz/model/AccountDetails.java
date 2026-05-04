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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Lalit Raghav
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AccountDetails extends Auditable<String>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 5)
    private Long id;
      
    @Column(name = "employeeid", length = 5)
     private Long employeeid;
    
    @Column(name = "organization_id", length = 5)
     private Long organization_id;
    
//    @Column(name = "panno", length = 16)
//    private String panno;

//     @Column(name = "pfaccountno", length = 20)
//    private String pfaccountno;
  
   
    @Column(name = "bankname", length =30)
    private String bankname;
    
    @Column(name = "ifsc", length = 12)
    private String ifsc;
    
//   @Column(name = "aadharno", length = 20)
//    private String aadharno;
    
    
    @Column(name = "bankaccount", length = 20)
    private String bankaccount;
    

   

    
     
     
}

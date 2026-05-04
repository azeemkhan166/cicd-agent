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
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Admin
 */
@Entity
@Table(name = "AuthorizatorySetup")
@Getter
@Setter
@NoArgsConstructor
public class AuthorizatorySetup extends Auditable<String>{
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 5)
    private Long id;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;
 
    @Column(name = "name", length = 55)
    private String name;
    
    @Column(name = "father_name", length = 55)
    private String father_name;
    
    @Column(name = "designation", length = 55)
    private String designation;
    
}

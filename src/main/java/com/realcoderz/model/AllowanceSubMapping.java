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
import lombok.ToString;

/**
 *
 * @author Mayank
 */

@Entity
@Table(name = "allowance_sub_mapping")
@Getter
@Setter
@NoArgsConstructor
public class AllowanceSubMapping extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allowance_sub_mapping_id", length = 7)
    private Long allowance_sub_mapping_id;    
    
    @Column(name="allowance_name", length=25)
    private String allowance_name;
    
    @Column(name="allowance_id", length=7)
    private Long allowance_id;
    
    @Column(name = "organization_id", length = 5)
    private Long organization_id;
    
}

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
@Table(name = "organization_incentive")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrganizationIncentive extends Auditable<String> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organization_incentive_id", length = 7)
    private Long organization_incentive_id;
    
    
    @Column(name = "incentive_min", length = 7)
    private Long incentive_min;
    
    @Column(name = "incentive_max", length = 7)
    private Long incentive_max;
    
    @Column(name = "organization_id", length = 7)
    private Long organization_id;
    
    
}

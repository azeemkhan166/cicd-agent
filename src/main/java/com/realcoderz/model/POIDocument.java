/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Bipul Singh
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class POIDocument extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long declarationId;
    
    private Long employeeId;
    
    private Long organizationId;

    private String investmentName;
    
    private String subInvestmentName;
    
    private String financialYear;

    private String fileUrl;

    private String fileName;

    private Boolean verified;
    
    private Boolean rejected;
}

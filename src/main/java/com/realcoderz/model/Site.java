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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Admin
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Site extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String siteName;
    private String clientName;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String clientEmailId;
    private String serviceOrderRef;
    private String authorizedPerson;
    private Long organizationId;
    private String clientPanNumber;
    private String clientPhoneNumber;
    private String clientRegistrationDate;
    private String clientEndDate;
    private String siteStartDate;
    private String siteEndDate;
    private String advance;
    private String status;

}

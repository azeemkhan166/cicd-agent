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
 * @author Astha
 */
@Entity
@Table(name = "OrganizationSetUp")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationSetUp extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_up_id", length = 5)
    private Long setUp_id;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

    @Column(name = "organization_address", length = 200)
    private String organization_address;

    @Column(name = "organization_cin_no", length = 255)
    private String organization_cin_no;

    @Column(name = "organization_tan_no", length = 55)
    private String organization_tan_no;

    @Column(name = "organization_pan_no", length = 55)
    private String organization_pan_no;

    @Column(name = "status", length = 8)
    private String status;

    @Column(name = "working_day", length = 20)
    private String working_day;

    @Column(name = "epf", length = 12)
    private String epf;

    @Column(name = "epf_registration_no", length = 55)
    private String epf_registration_no;

    @Column(name = "template", length = 10)
    private String template;

    @Column(name = "org_state")
    private String orgState;

    private String orgAddressLineTwo;

    private String orgPincode;

    private String orgCountry;

    private String orgCity;

    private String orgName;
    
    private String companyLogo;
    
    private String esic;
    
    private String esicRegistration;
    
    private String payrollBasedOn;
    
    private Integer bonusPercentage;
    
    private String ptFlag;
    
    private String lwfFlag;
}

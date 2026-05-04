/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.model;

import java.io.Serializable;
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
 * @author Bipul Singh
 */
@Entity
@Setter
@Getter
@Table(name="section_c_approved")
public class Section80cApproved extends Auditable<String> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_c_id", length = 5)
    private Long section_c_id;

    @Column(name = "declaration_id", length = 5)
    private Long declaration_id;

    @Column(name = "organization_id", length = 5)
    private Long organization_id;

    @Column(name = "provident_fund_contribution", length = 12)
    private double provident_fund_contribution;

    @Column(name = "life_insurance_premium", length = 12)
    private double life_insurance_premium;

    @Column(name = "public_provident_fund", length = 12)
    private double public_provident_fund;

    @Column(name = "voluntary_provident_fund", length = 12)
    private double voluntary_provident_fund;

    @Column(name = "pension_fund_contribution", length = 12)
    private double pension_fund_contribution;

    @Column(name = "national_savings_certificate", length = 12)
    private double national_savings_certificate;

    @Column(name = "interest_accrued_on_nsc", length = 12)
    private double interest_accrued_on_nsc;

    @Column(name = "unit_linked_insurance_policy", length = 12)
    private double unit_linked_insurance_policy;

    @Column(name = "mutual_funds", length = 12)
    private double mutual_funds;

    @Column(name = "payment_of_tuition_fees_for_children", length = 12)
    private double payment_of_tuition_fees_for_children;

    @Column(name = "principal_repayment_of_housing_loan", length = 12)
    private double principal_repayment_of_housing_loan;

    @Column(name = "registration_charges_incurred_for_buying_house", length = 12)
    private double registration_charges_incurred_for_buying_house;

    @Column(name = "sukanya_samriddhi_yojana", length = 12)
    private double sukanya_samriddhi_yojana;

    @Column(name = "infrastructure_bonds", length = 12)
    private double infrastructure_bonds;

    @Column(name = "bank_fixed_deposit", length = 12)
    private double bank_fixed_deposit;

    @Column(name = "post_office_term_deposit", length = 12)
    private double post_office_term_deposit;

    @Column(name = "sec80ccc", length = 12)
    private double sec80ccc;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.OtherSectionApproved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Bipul Singh
 */
@Repository
public interface OtherSectionApprovedRepository extends JpaRepository<OtherSectionApproved, Long> {

    @Query(nativeQuery = true, value = "Select * from other_section_approved where other_section_id=:other_section_id")
    public LinkedCaseInsensitiveMap findOtherSectionById(Long other_section_id);

    @Query(nativeQuery = true, value = "select o.interest_on_housing_loan_before from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2")
    public LinkedCaseInsensitiveMap getInterestOnHousingLoan(Long employeeid, Long organizationid);

    @Query(nativeQuery = true, value = "select o.tds from other_section_approved o, inverstment_declaration i where o.declaration_id=i.declaration_id and i.employeeid=?1 and i.organizationid=?2")
    public Double getTdsOfPreviousEmployer(Long employeeid, Long organizationid);
    
    @Query(nativeQuery = true, value = "select o.income_from_previous_employer,o.interest_income_fromsaving,o.national_pension_scheme,o.sec80d,o.sec80dd,o.sec80e,o.sec80g,o.sec80u,o.sec80d_type from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2")
    public LinkedCaseInsensitiveMap getOtherSection(Long employeeid, Long organizationid);
    
    @Query(nativeQuery = true, value = "select o.income_from_previous_employer,o.interest_income_fromsaving,o.national_pension_scheme,o.sec80d,o.sec80dd,o.sec80e,o.sec80g,o.sec80u,o.sec80d_type from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2 and i.fy_year=?3")
    public LinkedCaseInsensitiveMap getOtherSectionPreviousVersion(Long employeeid, Long organizationid, int year);

    @Query(nativeQuery = true,value="select * from other_section_approved where declaration_id=?1")
    public OtherSectionApproved getOtherSectionApprovedDetails(Long declaration_id);
}

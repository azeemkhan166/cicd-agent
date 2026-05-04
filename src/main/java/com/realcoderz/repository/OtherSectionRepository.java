/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.OtherSection;
import com.realcoderz.model.OtherSectionApproved;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Repository
public interface OtherSectionRepository extends JpaRepository<OtherSection, Long> {

    @Query(nativeQuery = true, value = "Select * from other_section where other_section_id=:other_section_id")
    public LinkedCaseInsensitiveMap findOtherSectionById(Long other_section_id);

    @Query(nativeQuery = true, value = "select o.interest_on_housing_loan_before from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2")
    public LinkedCaseInsensitiveMap getInterestOnHousingLoan(Long employeeid, Long organizationid);

    @Query(nativeQuery = true, value = "select o.tds from other_section o, inverstment_declaration i where o.declaration_id=i.declaration_id and i.employeeid=?1 and i.organizationid=?2")
    public Double getTdsOfPreviousEmployer(Long employeeid, Long organizationid);
    
    @Query(nativeQuery = true, value = "select o.income_from_previous_employer from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2")
    public LinkedCaseInsensitiveMap getIncomeFromPreviousEmployer(Long employeeid, Long organizationid);
    
    @Query(nativeQuery = true, value = "select os.sec80g, os.sec80d, os.sec80e, o.income_from_previous_employer, i.total_rent, s.sec80ccc, i.total_allowances, o.interest_on_housing_loan_before from other_section_approved o,inverstment_declaration i, section_c s , other_section os where i.declaration_id=o.declaration_id and i.declaration_id = s.declaration_id and i.declaration_id = os.declaration_id and i.employeeid=?1 and i.organizationid=?2 and i.fy_year =?3")
    public LinkedCaseInsensitiveMap getIncomeFromPreviousEmployer(Long employeeid, Long organizationid, int year);
    
    @Query(nativeQuery = true, value = "select o.interest_on_housing_loan_before from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2 and i.fy_year=?3")
    public LinkedCaseInsensitiveMap getInterestOnHousingLoanPreviousVersion(Long employeeid, Long organizationid, int fy_year);
    
    @Query(nativeQuery = true, value = "select o.tds from other_section o, inverstment_declaration i where o.declaration_id=i.declaration_id and i.employeeid=?1 and i.organizationid=?2 and i.fy_year=?3")
    public Double getTdsOfPreviousEmployerPreviousVersion(Long employeeid, Long organizationid, int year);
    
//     @Query(nativeQuery = true, value = "select o.interest_on_housing_loan_before from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2;")
//    public LinkedCaseInsensitiveMap getInterestOnHousingLoanOfParticularYear(Long employeeid, Long organizationid,int year);
    
    @Query(nativeQuery = true, value = "select o.interest_on_housing_loan_before from other_section_approved o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=?1 and i.organizationid=?2 and i.fy_year=?3")
    public LinkedCaseInsensitiveMap getInterestOnHousingLoanOfParticularYear(Long employeeid, Long organizationid,int year);

    @Query(nativeQuery = true,value="select * from other_section where declaration_id=?1")
    public OtherSection getOtherSectionDetails(Long declaration_id);
}

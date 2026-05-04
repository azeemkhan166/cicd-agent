/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.InvestmentDeclaration;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 */
@Repository
public interface InvestmentDeclarationRepository extends JpaRepository<InvestmentDeclaration, Long> {

    @Query(nativeQuery = true, value ="SELECT i.declaration_id,i.total_rent,i.total_allowances,tax_slab_tpye,i.approved_by_acc FROM inverstment_declaration as i WHERE i.employeeid =? and i.fy_year =?")
    public LinkedCaseInsensitiveMap getchDeclarationDataByIdChanges(Long employeeid,int fy_year);

    @Query(nativeQuery = true, value = "SELECT i.declaration_id,i.total_rent,i.total_allowances,os.status FROM inverstment_declaration i join other_section_approved os WHERE i.employeeid =? and os.declaration_id=i.declaration_id and i.fy_year =?")
    public LinkedCaseInsensitiveMap getchDeclarationDataById(Long employeeid,int fy_year);

    @Query(nativeQuery = true, value = "SELECT rent_amount_id, r.amount,r.rent_month FROM rent_amount as r WHERE  r.declaration_id =?")
    public List<LinkedCaseInsensitiveMap> getRentDataById(Long declaration_id);

    @Query(nativeQuery = true, value = "SELECT s.section_c_id,s.declaration_id, s.bank_fixed_deposit,s.infrastructure_bonds,s.interest_accrued_on_nsc,s.life_insurance_premium,s.mutual_funds,s.national_savings_certificate,s.payment_of_tuition_fees_for_children,s.pension_fund_contribution,s.post_office_term_deposit,s.principal_repayment_of_housing_loan,s.provident_fund_contribution,s.public_provident_fund,s.registration_charges_incurred_for_buying_house,s.sukanya_samriddhi_yojana,s.unit_linked_insurance_policy,s.voluntary_provident_fund,s.sec80ccc FROM section_c as s WHERE  s.declaration_id =?")
    public LinkedCaseInsensitiveMap getSectionCdataById(Long declaration_id);

    @Query(nativeQuery = true, value = "SELECT o.other_section_id,o.status,o.interest_on_housing_loan_before,o.national_pension_scheme,o.sec80d,o.sec80dd,o.sec80e,o.sec80g,o.sec80u,o.pf,o.professional_tax,o.tds,o.income_from_previous_employer,o.interest_income_fromsaving FROM other_section as o WHERE  o.declaration_id =?")
    public LinkedCaseInsensitiveMap getOtherSectiondataById(Long declaration_id);

    @Query(nativeQuery = true, value = "select o.status, i.declaration_id from other_section o,inverstment_declaration i where i.declaration_id=o.declaration_id and i.employeeid=? and i.organizationid=?")
    public LinkedCaseInsensitiveMap getStatusByEmployeeId(Long employeeid, Long organizationid);

    @Query(nativeQuery = true, value = "Select i.total_rent from inverstment_declaration i where i.employeeid=? and i.organizationid=?")
    public LinkedCaseInsensitiveMap getRent(Long employee_id, Long organization_id);

    @Query(nativeQuery = true, value = "select i.total_allowances from inverstment_declaration i where i.employeeid=? and i.organizationid=? and i.fy_year=? ")
    public LinkedCaseInsensitiveMap get80cTotal(Long employee_id, Long organization_id,int year);

    @Query(nativeQuery = true, value = "select r.amount from inverstment_declaration i INNER JOIN rent_amount r ON i.declaration_id =r.declaration_id  where i.employeeid=?1 and i.organizationid=?2 and rent_month >=?3 and rent_month <=?4")
    public LinkedCaseInsensitiveMap getTotalWorkingMonthRent(Long employee_id, Long organization_id, LocalDate previous_monthDate,LocalDate monthEnd);

    @Query(nativeQuery = true, value = "Select i.tax_slab_tpye from inverstment_declaration i where i.employeeid=? and i.fy_year=?")
    public LinkedCaseInsensitiveMap getSlabkey(Long employee_id, Long fy_year);

    @Query(nativeQuery = true, value = "SELECT rent_amount_id, r.amount,r.rent_month FROM rent_amount_approved as r WHERE  r.declaration_id =?")
    public List<LinkedCaseInsensitiveMap> getRentDataByIdForAcc(Long declaration_id);

    @Query(nativeQuery = true, value = "SELECT s.section_c_id,s.declaration_id, s.bank_fixed_deposit,s.infrastructure_bonds,s.interest_accrued_on_nsc,s.life_insurance_premium,s.mutual_funds,s.national_savings_certificate,s.payment_of_tuition_fees_for_children,s.pension_fund_contribution,s.post_office_term_deposit,s.principal_repayment_of_housing_loan,s.provident_fund_contribution,s.public_provident_fund,s.registration_charges_incurred_for_buying_house,s.sukanya_samriddhi_yojana,s.unit_linked_insurance_policy,s.voluntary_provident_fund,s.sec80ccc FROM section_c_approved as s WHERE  s.declaration_id =?")
    public LinkedCaseInsensitiveMap getSectionCdataByIdForAcc(Long declaration_id);

    @Query(nativeQuery = true, value = "SELECT o.other_section_id,o.status,o.interest_on_housing_loan_before,o.national_pension_scheme,o.sec80d,o.sec80dd,o.sec80e,o.sec80g,o.sec80u,o.sec80d_type,o.pf,o.professional_tax,o.tds,o.income_from_previous_employer,o.interest_income_fromsaving FROM other_section_approved as o WHERE  o.declaration_id =?")
    public LinkedCaseInsensitiveMap getOtherSectiondataByIdForAcc(Long declaration_id);

    @Query(nativeQuery = true, value = "select count(*) from inverstment_declaration where employeeid=?1 and organizationid=?2")
    public int isInvestmentDeclared(Long employee_id, Long organization_id);

    @Query(nativeQuery = true, value = "select count(*) from inverstment_declaration where employeeid=?1 and organizationid=?2 and fy_year=?3")
    public int isInvestmentDeclared(Long employee_id, Long organization_id,Integer year);

    @Query(nativeQuery=true,value="SELECT tax_slab_tpye FROM inverstment_declaration where employeeid=? and fy_year=?;")
    public LinkedCaseInsensitiveMap employeeInvestment(Long employeeId,int year);

   @Query(nativeQuery=true,value="SELECT tax_slab_tpye,employeeid as employee_id FROM inverstment_declaration where employeeid IN(?1) and fy_year=?2")
    public List<LinkedCaseInsensitiveMap> employeeInvestmentInBulk(List<Long> employeeId,int year);

    @Query(nativeQuery = true,value="Select employeeid,tds as tdsPreviousEmployer,total_allowances,tax_slab_tpye,total_rent,income_from_previous_employer,interest_on_housing_loan_before,national_pension_scheme,pf,sec80d,sec80dd,sec80e,sec80u,sec80g,status,sec80d_type from inverstment_declaration id left join other_section_approved oc on id.declaration_id=oc.declaration_id where employeeid=?1 and fy_year=?2 and tax_slab_tpye='OldTaxSlabKey'")
    public LinkedCaseInsensitiveMap getInvestmentForform16(Long employee_id,int year);

    @Query(nativeQuery = true,value="Select employeeid,tds as tdsPreviousEmployer,total_allowances,tax_slab_tpye,total_rent,income_from_previous_employer,interest_on_housing_loan_before,national_pension_scheme,pf,sec80d,sec80dd,sec80e,sec80u,sec80g,status,sec80d_type from inverstment_declaration id left join other_section oc on id.declaration_id=oc.declaration_id where employeeid=?1 and fy_year=?2 and tax_slab_tpye='OldTaxSlabKey'")
    public LinkedCaseInsensitiveMap getInvestmentForform16OFEmployee(Long employee_id,int year);

   @Query(nativeQuery = true,value="SELECT ed.employee_id,ed.employee_code,ed.email,id.fy_year,ed.name,CASE WHEN id.approved_by_acc = true THEN 'Approved' ELSE 'Pending' END AS approved_by_acc_status FROM inverstment_declaration id LEFT JOIN employee_details ed ON id.employeeid = ed.employee_id WHERE id.organizationid = ? AND id.approved_by_acc = ?")
   public List<LinkedCaseInsensitiveMap> getOrganizationInvesetmentForPendingItems(Long organizationId,boolean status);

}

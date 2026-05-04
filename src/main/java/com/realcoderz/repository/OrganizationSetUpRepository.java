/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.OrganizationSetUp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;
/**
 *
 * @author Astha
 */
@Repository
public interface OrganizationSetUpRepository extends JpaRepository<OrganizationSetUp , Long>{

   
    public void save(String data);
    
    //Query for checking organization is already exits or not
    @Query("SELECT count(*) from OrganizationSetUp where organization_id =:organization_id")
    public Integer isAlreadyExits(@Param("organization_id") Long id);
    
//     @Query(nativeQuery = true, value = "Select * from OrganizationSetUp where organization_id=?1")
//    public List<OrganizationSetUp> findOrganizationById(Long org_id);
    
    //Query for fetching data of organization on the bases of organization set up Id
    @Query(nativeQuery = true, value = "Select * from organization_set_up where organization_id=?1")
    public List<OrganizationSetUp> findSetUpById(Long organization_id);
    
    @Query(nativeQuery=true, value="Select working_day as working_day from organization_set_up where organization_id=?1")
    public String fetchWorkingDay(Long organization_id);
    
    // query for fetching EPF status while setup of oraganization
     @Query(nativeQuery = true, value = "select o.epf from organization_set_up o where o.organization_id=?1")
    public  LinkedCaseInsensitiveMap fetchEpf(Long organization_id);
    
     // query for fetching ESIC status while setup of oraganization
     @Query(nativeQuery = true, value = "select o.esic from organization_set_up o where o.organization_id=?1")
    public  LinkedCaseInsensitiveMap fetchEsic(Long organization_id);
    
    @Query(nativeQuery=true, value="SELECT organization_address,template, org_address_line_two, org_city, org_country, org_pincode, org_state, org_name FROM organization_set_up where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> getOrganizationData(Long organization_id);

    @Query(nativeQuery = true, value = "Select epf,organization_address,template,org_name,org_address_line_two,company_logo as company_name,org_city,org_pincode,org_state from organization_set_up where organization_id=?1 limit 1")
    public LinkedCaseInsensitiveMap getOrganizationAddress(Long organization_id);
    
    @Query(nativeQuery = true, value = "Select epf,esic from organization_set_up where organization_id=?1 limit 1")
    public LinkedCaseInsensitiveMap getEPFOFOrganizatrion(Long organization_id);
    
    @Query(nativeQuery = true,value = "Select * from organization_set_up where organization_id=?1")
    public OrganizationSetUp saveOrUpdateLogo(Long organization_id);
    
    @Query(nativeQuery = true,value = "Select * from organization_set_up where organization_id=?1")
    public LinkedCaseInsensitiveMap getOrganizationDetails(Long organization_id);
    
    @Query(nativeQuery=true, value="Select working_day as working_day,org_state,esic,epf from organization_set_up where organization_id=?1 limit 1")
    public LinkedCaseInsensitiveMap fetchWorkingDayAndOrgState(Long organization_id);
    
    @Query(nativeQuery = true, value = "Select bonus_percentage from organization_set_up where organization_id=?1 limit 1")
    public LinkedCaseInsensitiveMap getBonusPercentage(Long organization_id);

    @Query(nativeQuery=true, value="Select lwf_flag,pt_flag,working_day as working_day,org_state,esic,epf,set_up_id from organization_set_up where organization_id=?1")
    public List<LinkedCaseInsensitiveMap> fetchWorkingDayAndOrgState1(Long organization_id);
    
    @Query(nativeQuery = true, value = "Select epf,organization_address,template,org_name,org_address_line_two,company_logo as company_name,org_city,org_pincode,org_state from organization_set_up where organization_id=?1 limit 1")
    public List<LinkedCaseInsensitiveMap> getOrganizationAddress1(Long organization_id);
    
    @Query(nativeQuery = true, value = "Select epf,organization_address,template,org_name,org_address_line_two,company_logo as company_name,org_city,org_pincode,org_state from organization_set_up where organization_id=?1 and set_up_id=?2")
    public LinkedCaseInsensitiveMap getOrganizationAddress2(Long organization_id,Long orgDetailsId);
    
    @Query(nativeQuery = true, value = "Select epf,esic from organization_set_up where set_up_id=?1")
    public LinkedCaseInsensitiveMap getEPFOFMultipleOrganizatrion(Long set_up_id);
}
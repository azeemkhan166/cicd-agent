/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.Site;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Admin
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    
     //    Fetch All Allowances By Org Id
    @Query(nativeQuery = true, value = "Select * from site where organization_id=?1")
    public List<Site> findSiteById(Long org_id);
    
    @Query(nativeQuery = true, value = "select s.id as siteId,e.employee_code as employeeCode,e.employee_id,e.name,s.advance,s.advance as rcmAdvance,s.site_name,?2 as date,?3 as year,'Pending' as supervisorStatus from site s left join site_employee_mapping sem on s.id=sem.site_id left join employee_details e on e.employee_id=sem.employee_id where s.id=?1")
    public List<LinkedCaseInsensitiveMap> getAdvanceDetails(Long id,String month,Long year);
    
//    @Query(nativeQuery = true, value = "select s.id as siteId,e.employee_code as employeeCode,e.employee_id,e.name,ar.amount as advance , ar.amount as rcmAdvance,ar.date,ar.year,ar.supervisor_status as supervisorStatus,s.site_name from advance_rcm ar left join employee_details e on e.employee_id=ar.employee_id left join site s on s.id=ar.site_id  where site_id=?1 and date=?2 and year=?3")
//    public List<LinkedCaseInsensitiveMap> getSavedAdvanceDetails(Long id,String month,Long year);
    
    @Query(nativeQuery = true, value = "select ar.site_id as siteId,e.employee_code as employeeCode,ar.employee_id,e.name,ar.amount as advance , ar.amount as rcmAdvance,ar.date,ar.year,ar.supervisor_status as supervisorStatus,?1 as site_name from advance_rcm ar left join employee_details e on e.employee_id=ar.employee_id  where site_id=?2 and date=?3 and year=?4")
    public List<LinkedCaseInsensitiveMap> getSavedAdvanceDetails(String siteName,Long id,String month,Long year);
     
//    @Query(nativeQuery = true, value = "select ar.id,s.id as siteId,e.employee_code as employeeCode,e.employee_id,e.name,ar.amount as advance , ar.rcm_amount as rcmAdvance,ar.ho_amount as hoAdvance,ar.ho_status as hoStatus,ar.date,ar.year,ar.rcm_status as rcmStatus,s.site_name from advance_rcm ar left join employee_details e on e.employee_id=ar.employee_id left join site s on s.id=ar.site_id  where site_id=?1 and date=?2 and year=?3 and ar.supervisor_status='Approved'")
//    public List<LinkedCaseInsensitiveMap> getAllAdvanceForRcm(Long id,String month,Long year);
    
    @Query(nativeQuery = true, value = "select ar.paid,ar.ho_date,ar.id,ar.site_id as siteId,e.employee_code as employeeCode,ar.employee_id,e.name,ar.amount as advance , ar.rcm_amount as rcmAdvance,ar.ho_amount as hoAdvance,ar.ho_status as hoStatus,ar.date,ar.year,ar.rcm_status as rcmStatus,?1 as site_name from advance_rcm ar left join employee_details e on e.employee_id=ar.employee_id where site_id=?2 and MONTH(ar.date)=?3 and year=?4 and ar.supervisor_status='Approved'")
    public List<LinkedCaseInsensitiveMap> getAllAdvanceForRcm(String siteName,Long id,String month,Long year);
       
    @Query(nativeQuery = true, value = "select ar.ho_date,'No' as Flag,ar.paid,ad.bankaccount,ad.ifsc,ar.id,ar.site_id as siteId,e.employee_code as employeeCode,ar.employee_id,e.name,ar.ho_amount as hoAdvance,ar.ho_status as hoStatus,ar.date,ar.year,?1 as site_name from advance_rcm ar left join employee_details e on e.employee_id=ar.employee_id left join account_details ad on e.employee_id=ad.employeeid  where site_id=?2 and MONTH(ar.date)=?3 and year=?4 and ar.ho_status='Approved'")
    public List<LinkedCaseInsensitiveMap> getAllAdvanceForExcel(String siteName,Long id,String month,Long year);
    
    @Query(nativeQuery = true, value = "SELECT distinct date\n"
            + "FROM advance_rcm\n"
            + "WHERE site_id=:id AND  date BETWEEN :year AND :nextYear ")
    public List<LinkedCaseInsensitiveMap> getDateFormAdvance(@Param("id") Long id , @Param("year") String year,@Param("nextYear") String nextYear);
    
}

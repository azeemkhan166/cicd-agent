/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.AdvanceRcm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Admin
 */
@Repository
public interface AdvanceRcmRepository extends JpaRepository<AdvanceRcm, Long>{
    
    @Query(nativeQuery = true, value = "Select * from advance_rcm where id IN(?1)")
    public List<AdvanceRcm> findAdvanceById(List<Long> primaryIds);
    
    @Query(nativeQuery = true, value = "select * from advance_rcm  where site_id=?1 and MONTH(date)=?2 and year=?3 and ho_status='Approved'")
    public List<AdvanceRcm> getAllAdvanceDataForUpdate(Long id,String month,Long year);
    
    @Query(nativeQuery = true, value = "select * from advance_rcm where site_id=?1 and ho_date between ?2 and ?3 and ho_status='Approved' and paid='Paid'")
    public List<AdvanceRcm> getAdvanceData(Long id,String startDate,String endDate);
    
    @Query(nativeQuery = true, value = "select * from advance_rcm  where id IN(?1)")
    public List<AdvanceRcm> getAllAdvanceDataForUpdates(List<Long> id);
}

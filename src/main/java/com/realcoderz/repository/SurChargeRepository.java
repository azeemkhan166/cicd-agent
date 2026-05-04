package com.realcoderz.repository;

import com.realcoderz.model.SurCharge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Mayank
 * edited By Astha
 */
public interface SurChargeRepository extends JpaRepository<SurCharge, Long> {

    @Query(nativeQuery = true, value = "Select * from sur_charge")
    public List<SurCharge> findSurCharge();

     @Query(nativeQuery = true, value ="select surcharge_id,start,end,rate,surcharge_id from sur_charge")
     public List<SurCharge> getSurCharges();

    @Query(nativeQuery = true, value = "Select count(*) from sur_charge where organization_id=?1")
    public Integer existsByOrganization_Id(Long organization_id);
}

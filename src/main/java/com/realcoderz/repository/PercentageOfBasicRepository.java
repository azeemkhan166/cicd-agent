package com.realcoderz.repository;

import com.realcoderz.model.PercentageOfBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Lalit Raghav
 * edited By Astha
 */
@Repository
public interface PercentageOfBasicRepository extends JpaRepository<PercentageOfBasic, Long> {
    
    @Query(nativeQuery = true, value = "SELECT  p.metro_basicpercentage,p.non_metro_basicpercentage FROM percentage_of_basic as p")
    public LinkedCaseInsensitiveMap getPercentageDataById();

    @Query(nativeQuery = true, value = "Select percentage_of_basic_id,metro_basicpercentage,non_metro_basicpercentage from percentage_of_basic")
    public LinkedCaseInsensitiveMap getpercentageOfBasic();

}

package com.realcoderz.repository;

import com.realcoderz.model.PerquisiteData;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Astha
 */
@Repository
public interface PerquisiteDataRepository extends JpaRepository<PerquisiteData,Long> {
    
     @Query(nativeQuery = true, value ="select * from perks_perquisite  where organization_id=?1 ")
    public List<PerquisiteData>  getPerquisite(Long organization_id);
    
}
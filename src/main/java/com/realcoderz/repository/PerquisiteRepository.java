package com.realcoderz.repository;

import com.realcoderz.model.Perquisite;
import com.realcoderz.model.PerquisiteData;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Astha
 */
@Repository
public interface PerquisiteRepository extends JpaRepository <Perquisite, Long> {
   
    @Query(nativeQuery = true, value ="select * from perquisite  where organization_id=?1 ")
    public List<Perquisite>  getPerquisite(Long organization_id);
    
    @Query(nativeQuery = true, value ="select perquisite_name from perquisite  where organization_id=?1 ")
    public List<String>  getPerquisiteName(Long organization_id);
    
    @Query(nativeQuery = true, value ="select perquisite_name from perquisite  where organization_id=?1 ")
    public List<LinkedCaseInsensitiveMap>  getAllPerquisiteName(Long organization_id);

}
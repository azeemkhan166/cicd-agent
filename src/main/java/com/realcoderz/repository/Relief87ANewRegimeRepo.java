
package com.realcoderz.repository;

import com.realcoderz.model.Relief87ANewRegime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tauseef
 */
@Repository
public interface Relief87ANewRegimeRepo extends JpaRepository<Relief87ANewRegime, Long>
{
    @Query(value="SELECT * FROM relief_87a_newregime where year=?",nativeQuery = true)
    public Relief87ANewRegime relief87ANewReime(int year);
    
//    @Query(value="SELECT * FROM relief_87a_newregime where year=?",nativeQuery = true)
//    public List<Relief87ANewRegime> relief87ANewReime(int year);
    
    @Query(value="SELECT * FROM relief_87a_newregime where year=?",nativeQuery = true)
    public LinkedCaseInsensitiveMap relief87ANewReimeRefactor(int year);
    
}

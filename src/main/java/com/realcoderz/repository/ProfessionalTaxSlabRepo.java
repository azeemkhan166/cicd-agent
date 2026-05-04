package com.realcoderz.repository;

import com.realcoderz.model.ProfessionalTaxSlab;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Pooja Gupta
 */
public interface ProfessionalTaxSlabRepo extends JpaRepository<ProfessionalTaxSlab, Long> {

    @Query(value = "select taxAmount from ProfessionalTaxSlab where  ( :salary<= maxSalary and :salary>= minSalary) and orgState= :orgState")
    public Double fecthTaxAmountByStateAndGrossSalary(String orgState, Double salary);

    @Query(value = "From ProfessionalTaxSlab where slab_id= :slabId")
    public ProfessionalTaxSlab findBySlabId(Long slabId);

    List<ProfessionalTaxSlab> findAllByOrderByLastModifiedDateDesc();

}

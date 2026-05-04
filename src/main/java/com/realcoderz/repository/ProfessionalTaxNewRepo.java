/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.repository;

import com.realcoderz.model.ProfessionalTaxNew;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tause
 */
@Repository
public interface ProfessionalTaxNewRepo extends JpaRepository<ProfessionalTaxNew, Long>
{
    @Query(value="SELECT * FROM professional_tax_new WHERE ((start_month <= end_month AND :input_month BETWEEN start_month AND end_month) OR (start_month > end_month AND (:input_month BETWEEN start_month AND 12 OR :input_month BETWEEN 1 AND end_month))) and state_name= :stateName  and (min_salary<= :gross and max_salary>=:gross ) and (gender='All' or gender=:gender) and financial_year=:fyYear",nativeQuery = true)
    public ProfessionalTaxNew getProfessionalTax(@Param("input_month") int input_month,@Param("stateName") String stateName,@Param("gross") Double gross,@Param("gender") String gender,@Param("fyYear") int fyYear);
    
    @Query(value="SELECT * FROM professional_tax_new where financial_year LIKE CONCAT('%', :searchString, '%') or state_name LIKE CONCAT('%', :searchString, '%') or gender like CONCAT('%', :searchString, '%') order by financial_year desc",nativeQuery = true,
    countQuery = "SELECT count(*) FROM professional_tax_new where financial_year LIKE CONCAT('%', :searchString, '%') or state_name LIKE CONCAT('%', :searchString, '%') or gender like CONCAT('%', :searchString, '%')"
    )
    public Page<ProfessionalTaxNew> getAllProfessionalTax(@Param("searchString") String searchString,Pageable pageable);
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.repository;

import com.realcoderz.model.OrganizationIdDates;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tause
 */
@Repository
public interface OrganizationIdDatesRepo extends JpaRepository<OrganizationIdDates, Long>
{
   OrganizationIdDates findByOrganizationIdAndFinancialYear(Long organizationId,Integer fyYear);

}

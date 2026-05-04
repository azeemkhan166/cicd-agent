/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.BonusDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mayank
 */
@Repository
public interface BonusDeductionRepository extends JpaRepository<BonusDeduction, Long>{
    
    @Query(value = "Select amount from bonus_amount where employee_id=?1 and organization_id=?2 and month=?3 and year=?4", nativeQuery = true)
    public Double getBonusByMonth(Long employee_id, Long organization_id, Integer month, Integer year);
    
    @Query(value="select amount from bonus_amount where salary_breakup_id=?1", nativeQuery = true)
    public Double getBonus(Long salary_breakup_id);
    
    @Query(value="select amount from bonus_amount where employee_id=?1 and (YEAR(effective_date) < ?3 OR (YEAR(effective_date) = ?3 AND MONTH(effective_date) <= ?2)) ORDER BY effective_date DESC LIMIT 1 OFFSET 0 ", nativeQuery = true)
    public Double getEmployeeBonus(Long employeeId,int month,int year);
    
    @Query(value="Select * from bonus_amount where salary_breakup_id=?;",nativeQuery = true)
    public BonusDeduction bonusBySalaryBreakup(Long salaryBreaupId);
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.repository;

import com.realcoderz.model.BalanceSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tauseef
 */
@Repository
public interface BalanceSummaryRepo extends JpaRepository<BalanceSummary, Long>
{
    @Query(value="SELECT * FROM balance_summary where organization_id=? and month=? and year=?;",nativeQuery = true)
    public List<BalanceSummary> orgBalanceSummary(Long organizationId,int month,int year);
    
    @Query(value="SELECT bs.employee_id,bs.balance_summary_id,opening_balance,current_month_salary,payment,net_balance,month,year,department_name,name,employee_code FROM balance_summary bs left join employee_details ed on bs.employee_id=ed.employee_id where bs.organization_id=? and bs.month=? and bs.year=?;",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> orgMonthlyBalanceSummary(Long organizationId,int month,int year);
    
    @Query(value="select bs.current_month_salary,bs.balance_summary_id,bs.net_balance,bs.payment,bs.opening_balance,bsh.net_balance as historyNetBalance,bsh.payment_amount,DATE_FORMAT(payment_date, '%m-%d-%Y') as payment_date from balance_summary bs left join balance_summary_history bsh on bs.balance_summary_id=bsh.balance_summary_id where bs.balance_summary_id=?",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getBalanceHistory(Long balanceSummaryId);
    
    @Query(value = "SELECT balance_summary_id FROM balance_summary WHERE employee_id =:employeeId AND balance_summary_id = (SELECT MAX(balance_summary_id) FROM balance_summary WHERE employee_id = :employeeId);",nativeQuery = true)
    LinkedCaseInsensitiveMap getBalanceSummaryId(@Param("employeeId") Long employeeId);
    
}

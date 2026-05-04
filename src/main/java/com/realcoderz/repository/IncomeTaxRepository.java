/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.IncomeTax;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
@Repository
public interface IncomeTaxRepository extends JpaRepository<IncomeTax, Long> {

    @Query(nativeQuery = true, value = "select salary_hra_name as salary_hra_name,salary_hra_amount as salary_hra_amount,exemption_name as exemption_name,exemption_declared_amount as exemption_declared_amount,exemption_exempted_amount as exemption_exempted_amount,tax_name as tax_name,tax_amount as tax_amount from income_tax where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public List<String[]> isTaxSave(Long employeeid, Long organization_id, int month, int year);
    
    @Query(nativeQuery = true, value = "select count(*)from income_tax where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public int isTaxSavedAlready(Long employeeid, Long organization_id, int month, int year);
    
     @Query(nativeQuery = true,value = "select * from income_tax where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public  List<IncomeTax> isTaxSavedAlreadyGet(Long employeeid, Long organization_id, int month, int year);
    
    @Query(nativeQuery = true,value = "select * from income_tax where employee_id=?1 and organization_id=?2 and month=?3 and year=?4")
    public  List<LinkedCaseInsensitiveMap> isTaxSavedAlreadyGetforSubTotal(Long employeeid, Long organization_id, int month, int year);
    
    @Query(nativeQuery = true,value = "SELECT sum(salary_hra_amount) FROM income_tax where organization_id=?1 and employee_id =?2 and salary_hra_name = 'Sub Total' and year = ?3")
    public  String getSubTotal(Long organizationId, Long employeeId, int year);
    
    @Query(nativeQuery = true,value = "SELECT sum(tax_amount) FROM income_tax where organization_id=?1 and employee_id =?2 and tax_name = 'Tax on Total Income' and year = ?3")
    public  String getTaxOnTotalIncome (Long organizationId, Long employeeId, int year);
    
    @Query(nativeQuery = true,value = "SELECT sum(tax_amount) FROM income_tax where organization_id=?1 and employee_id =?2 and tax_name = 'Surcharge on Income ' and year = ?3")
    public  String getSurcharge (Long organizationId, Long employeeId, int year);
    
    @Query(nativeQuery = true,value = "SELECT sum(tax_amount) FROM income_tax where organization_id=?1 and employee_id =?2 and tax_name = 'Relief u/s 89' and year = ?3")
    public  String getRelief89 (Long organizationId, Long employeeId, int year);
    
    @Query(nativeQuery = true,value = "SELECT sum(tax_amount) FROM income_tax where organization_id=?1 and employee_id =?2 and tax_name = 'Education Cess' and year = ?3")
    public  String getHealthAndEducationCess (Long organizationId, Long employeeId, int year);
    
    @Query(nativeQuery = true,value = "SELECT sum(tax_amount) FROM income_tax where organization_id=?1 and employee_id =?2 and tax_name = 'Total Tax Deducted Till Date' and year = ?3")
    public  String getTotalTaxDeductedTillDate(Long organizationId, Long employeeId, int year);
    
     @Query(value="Select end,start,rate from sur_charge",nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> surCharge();
    
    @Query(value="Select income from relief_87a;",nativeQuery = true)
    public LinkedCaseInsensitiveMap relief();
    
    @Query(nativeQuery = true,value="Select sum(tax_amount) as tax_amount,tax_name,employee_id from  income_tax where  organization_id=? and tax_name='Tax Deduction for this month' and ((month between 4 and 12 and year =?) or (month between 1 and 3 and year =?)) GROUP BY employee_id;")
    public List<LinkedCaseInsensitiveMap> totalTaxTillDate(Long organizationId,int startYear,int endYear);
    
    @Query(nativeQuery = true,value="SELECT COALESCE(salary_hra_name, '-') AS salary_hra_name,COALESCE(salary_hra_amount, '-') AS salary_hra_amount,COALESCE(exemption_name, '-') AS exemption_name,COALESCE(exemption_exempted_amount, '-') AS exemption_exempted_amount,COALESCE(exemption_declared_amount, '-') AS exemption_declared_amount,COALESCE(tax_name, '-') AS tax_name,COALESCE(tax_amount, '-') AS tax_amount FROM income_tax WHERE employee_id = ? AND month = ? AND year = ?")
    public List<LinkedCaseInsensitiveMap> getTaxList(Long employeeId,int month,int year);
    
    @Query(nativeQuery = true,value="Select * from income_tax where year=? and month=? and employee_id=?")
    public List<IncomeTax> employeeIncomeTax(int year,int month,Long employeeId);
    
    @Query(nativeQuery = true, value="Select salary_hra_name,salary_hra_amount from income_tax where year=? and month=? and employee_id=? and (salary_hra_name='Bonus/Incentive' or salary_hra_name='Overtime Allowance' or salary_hra_name='Other Allowance' or salary_hra_name='Arrears' or salary_hra_name='Reimbursement')")
    public List<LinkedCaseInsensitiveMap> previousEmployeeAllowances(int year,int month,Long employeeId);
    
    @Query(nativeQuery = true,value="Select * from income_tax where year=?1 and month=?2 and employee_id In(?3) and organization_id=?4")
    public List<IncomeTax> employeeIncomeTaxInBulk(int year,int month,List<Long> employeeId,Long orgId);
    
    @Query(nativeQuery = true, value="Select salary_hra_name,salary_hra_amount,employee_id from income_tax where year=?1 and month=?2 and employee_id IN(?3) and (salary_hra_name='Bonus/Incentive' or salary_hra_name='Overtime Allowance' or salary_hra_name='Other Allowance' or salary_hra_name='Referral Allowance' or salary_hra_name='Reimbursement' or salary_hra_name='Arrears')")
    public List<LinkedCaseInsensitiveMap> previousEmployeeAllowancesInBulk(int year,int month,List<Long> employeeId);
    
}

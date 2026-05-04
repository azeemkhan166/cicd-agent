package com.realcoderz.repository;

import java.util.List;
import javax.transaction.Transactional;
import com.realcoderz.model.SalaryHistoryRecord;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Astha
 */
@Repository
public interface SalaryHistoryRecordRepository extends JpaRepository<SalaryHistoryRecord, Long> {
    
     @Query(nativeQuery = true, value ="select gross_salary,appraisal_salary,effective_date,month,year from salary_history_record where organization_id=?1 and employee_id=?2")
    List<LinkedCaseInsensitiveMap> getSalaryHistoryRecordById(Long organization_id, Long employee_id);
    
    @Query(nativeQuery = true, value ="select gross_salary FROM salary_history_record where employee_id=?1 and organization_id=?2")
    LinkedCaseInsensitiveMap getAlreadySalarySave(Long employee_id,Long organization_id);
    
    @Modifying
    @Transactional
    @Query(value = "update employee set gross_salary = (:appraisal_salary) where id=(:Id) ", nativeQuery = true)
   public void updatingEffectiveDate(@Param("appraisal_salary")Double gross_salary,@Param("Id")Long Id );
   
   @Query(nativeQuery = true, value="select salary_history_record.* from salary_history_record, (select employee_id,max(effective_date) as effective_date from salary_history_record group by employee_id) record where salary_history_record.employee_id=record.employee_id and salary_history_record.effective_date=record.effective_date")
   public List<LinkedCaseInsensitiveMap> getLatestAppraisal();
   
    @Query(nativeQuery = true, value = "Select distinct s.month, s.year, s.effective_date , s.gross_salary as gross_salary ,s.record_id,s.appraisal_salary as appraisal_salary, s.employee_id as employeeId from salary_history_record s where s.employee_id=?1 order by s.record_id desc")
    public List<LinkedCaseInsensitiveMap> findEmployeeSalaryById(Long empId);

}

package com.realcoderz.repository;

import com.realcoderz.model.Employee;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;
/**
 *
 * @author Astha
 */
@Repository
public interface NewEmployeeRepository extends JpaRepository<Employee,Long> {
    
    @Modifying
    @Transactional
    @Query(value = "update employee set gross_salary = (:appraisal_salary) where id=(:Id) ", nativeQuery = true)
    void updatingEffectiveDate(@Param("appraisal_salary")Double gross_salary,@Param("Id")Long Id );

     @Query(nativeQuery = true, value ="Select id, gross_salary from employee where employee_id=?")
    public LinkedCaseInsensitiveMap getSalary(Long employee_id);
    
     @Query(nativeQuery = true, value ="Select id from employee where employee_id=? ")
    public Long getSalaryById(Long employee_id);
    
    @Query(nativeQuery=true, value="Select count(*) from employee where employee_id=?1 and organization_id=?2")
    public int isEmployeeSalaryExist(Long employee_id, Long organization_id);
    
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value="update employee set gross_salary = (:gross_salary) where employee_id=(:employee_id) and organization_id=(:organization_id)")
    public void updateGrossSalary(@Param("gross_salary")Double gross_salary,@Param("employee_id")Long employee_id,@Param("organization_id")Long organization_id);
    
}
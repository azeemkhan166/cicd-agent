/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.repository;

import com.realcoderz.model.POIDocument;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Bipul Singh
 */
@Repository
public interface ProofOfInvestmentRepository extends JpaRepository<POIDocument, Long> {

    public List<POIDocument> findByDeclarationId(Long declarationId);

    public Optional<POIDocument> findByDeclarationIdAndInvestmentNameAndSubInvestmentName(Long declarationId, String investmentName, String subInvestmentName);

    public List<POIDocument> findByEmployeeId(Long employeeId);

    @Query(value="SELECT distinct pd.employeeId FROM inverstment_declaration pd where pd.organizationid=:orgainzationId",nativeQuery = true)
    public Set<Long> findAllEmployeeIds(@Param("orgainzationId")Long orgainzationId);
    
//    @Query("SELECT distinct pd.employeeId FROM POIDocument pd where pd.organizationId=:orgainzationId")
//    public Set<Long> findAllEmployeeIds(@Param("orgainzationId")Long orgainzationId);
    @Query(nativeQuery = true,value = "select file_url from poidocument where id=?1")
    public String findUrl(Long id);
    
    @Query(nativeQuery = true,value="SELECT employee_id, SUM(CASE WHEN verified = 0 AND rejected = 0 THEN 1 ELSE 0 END) AS pending FROM  poidocument where organization_id=?1 GROUP BY employee_id")
    public List<LinkedCaseInsensitiveMap> findPendingDocument(Long orgainzationId);
    
     public List<POIDocument> findByEmployeeIdAndFinancialYear(Long employeeId,String financialYear);
     
    @Query(nativeQuery = true,value="SELECT p.employee_id,SUM(CASE WHEN verified = 0 AND rejected = 0 THEN 1 ELSE 0 END) AS pending FROM poidocument p  WHERE organization_id = ? AND p.financial_year = ? GROUP BY p.employee_id")
    public List<LinkedCaseInsensitiveMap> findPendingDocumentAccordingToFyYear(Long orgainzationId,String fyYear);
    
    @Query(nativeQuery = true,value = "select name, dob, employee_type , pan_number,employee_id from employee_details where employee_id in (:employeeIds)")
    public List<LinkedCaseInsensitiveMap> employeeDetails(@Param("employeeIds") List<Long> employeeIds);
    
    @Query(nativeQuery = true,value = "Select employeeid as employee_id,submitted_by,name, dob, employee_type as employeeType ,email, pan_number from inverstment_declaration id left join employee_details ed on id.employeeid=ed.employee_id where organizationid = ? AND fy_year = ?  ")
    public List<LinkedCaseInsensitiveMap> employeeInvestements(Long employeeId,String financialYear);
}

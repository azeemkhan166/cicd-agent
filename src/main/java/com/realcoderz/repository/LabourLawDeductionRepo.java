/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.repository;

import com.realcoderz.model.LabourLawDeduction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author tause
 */
public interface LabourLawDeductionRepo extends JpaRepository<LabourLawDeduction, Long> {

    @Query(value = "SELECT * FROM labour_law_deduction WHERE  state_name=? ", nativeQuery = true)
    public List<LabourLawDeduction> fetchLabourLawPolicyByStatename(String state);

    public LabourLawDeduction getByStateName(String stateName);

    @Query(value = "SELECT "
            + "rp.name AS employeeName, "
            + "rp.employee_code AS employeeCode, "
            + "os.org_state AS orgState, "
            + "lld.employee_deduction AS employeeDeduction, "
            + "lld.employer_deduction AS employerDeduction, "
            + "(lld.employee_deduction + lld.employer_deduction) AS totalContribution "
            + "FROM run_pay_roll rp "
            + "JOIN organization_set_up os ON os.organization_id = rp.organization_id "
            + "JOIN labour_law_deduction lld ON lld.state_name = os.org_state "
            + "AND lld.employee_deduction = rp.labour_welfare_fund "
            + "WHERE rp.organization_id = :organizationId",
            nativeQuery = true)
    public List<LinkedCaseInsensitiveMap> getLwfReport(@Param("organizationId") Long organizationId);

}

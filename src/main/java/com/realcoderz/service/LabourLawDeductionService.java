/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.realcoderz.service;

import com.realcoderz.model.LabourLawDeduction;
import java.util.Map;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author tause
 */
public interface LabourLawDeductionService {

    public Map getAllStatesDeduction();

    public Map save(LabourLawDeduction deduction);

    public Map getSingleStateData(Long labourLawData);

    public Map validateDataBeforeSave(LabourLawDeduction deduction);

    public ResponseEntity<byte[]> getLwfReport(Long organizationId, String month);

}

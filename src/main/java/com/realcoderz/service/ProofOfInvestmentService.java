/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author bipul
 */
public interface ProofOfInvestmentService {

    public Map findAllByEmployeeId(Map map);

    public Map uploadPOI(MultipartFile file, String fileName, String investmentName, String subInvestmentName, Long declarationId, Long employeeId, Long organizationId, String financialYear);

    public Map fetchDocument(Map map);

    public Map getEmployeeList(String data, HttpServletRequest request);

    public Map rejectOrApproveDocument(Map map);
    
    public Map getPoiAccordingToFyYear(String map);
    
}

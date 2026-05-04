/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.ProofOfInvestmentService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Bipul Singh
 */
@RestController
@RequestMapping(path = "/poi")
public class ProofOfInvestmentController {

    static final Logger logger = LoggerFactory.getLogger(ProofOfInvestmentController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ProofOfInvestmentService poiService;

    @PostMapping("/findAll")
    public Map findAllByDeclarationId(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = poiService.findAllByEmployeeId(map);
        } catch (Exception ex) {
            logger.info("Problem in ProofOfInvestmentController -> findAll() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/upload")
    public Map uploadPOI(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
            @RequestParam("investmentName") String investmentName,@RequestParam("subInvestmentName") String subInvestmentName, @RequestParam("declarationId") Long declarationId,
            @RequestParam("employeeId") Long employeeId, @RequestParam("organizationId") Long organizationId, @RequestParam("financialYear") String financialYear) {
        Map resultMap = new HashMap<>();
        try {
            resultMap = poiService.uploadPOI(file, fileName, investmentName, subInvestmentName, declarationId, employeeId, organizationId, financialYear);
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in ProofOfInvestmentController -> uploadPOI() :: ", ex);
        }

        return resultMap;
    }

    @PostMapping("/fetchdoc")
    public Map fetchDocument(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = poiService.fetchDocument(map);
        } catch (Exception ex) {
            logger.info("Problem in ProofOfInvestmentController -> fetchDocument() :: ", ex);
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }

    @PostMapping("/rejectdoc")
    public Map rejectDocument(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = poiService.rejectOrApproveDocument(map);
        } catch (Exception ex) {
            logger.info("Problem in ProofOfInvestmentController -> rejectDocument() :: ", ex);
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }

    @PostMapping("/employeeList")
    public Map getEmployeeList(@RequestBody String data, HttpServletRequest request) {
       // return poiService.getEmployeeList(data, request);
        return poiService.getPoiAccordingToFyYear(data);
    }

}

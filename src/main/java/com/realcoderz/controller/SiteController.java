/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.realcoderz.controller.AllowanceController.logger;
import com.realcoderz.service.SiteService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.Map;
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
 * @author Admin
 */
@RestController
@RequestMapping("/site")
public class SiteController {
    
    @Autowired
    private SiteService siteService;
    
    ObjectMapper mapper = new ObjectMapper();
        
    @PostMapping("/save")
    public Map saveSite(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = siteService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in saveSite -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
    @PostMapping("/getAllSite")
    public Map getAllSite(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = siteService.getAllSite(map);
        } catch (Exception ex) {
            logger.info("Problem in getAllSite -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/findById")
    public Map findById(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = siteService.findById(map);
        } catch (Exception ex) {
            logger.info("Problem in findById -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/deleteById")
    public Map deleteById(@RequestBody String data) {
        
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = siteService.deleteById(map);
        } catch (Exception ex) {
            logger.info("Problem in siteController -> deleteById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    

    @PostMapping("/upload")
    public Map uploadSite(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName,
            @RequestParam("siteId") Long siteId,
            @RequestParam("organizationId") Long organizationId,
            @RequestParam("siteName") String siteName,
            @RequestParam("complianceName") String complianceName,
            @RequestParam("validTillDate") String validTillDate
            ) {
        Map resultMap = new HashMap<>();
        try {
            resultMap = siteService.uploadSite(file, fileName, siteId, organizationId,siteName,complianceName,validTillDate);
        } catch (Exception ex) {
            logger.info("Problem in SiteController -> uploadSite() :: ", ex);
            resultMap.put("status", "exception");
        }

        return resultMap;
    } 

       @PostMapping("/fetchdoc")
    public Map fetchDocument(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = siteService.fetchDocument(map);
        } catch (Exception ex) {
           
            resultMap.put("status", "exception");
            resultMap.put("msg", ex.getMessage());
        }
        return resultMap;
    }
    
}

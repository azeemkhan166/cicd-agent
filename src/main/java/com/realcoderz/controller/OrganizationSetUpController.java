/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.OrganizationSetUp;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.service.OrganizationSetUpService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Astha
 */
@RestController
@RequestMapping(path = "/orgSetUp")
public class OrganizationSetUpController {

    static final Logger logger = LoggerFactory.getLogger(OrganizationSetUpController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private OrganizationSetUpService orgSetUpService;
    
    @Autowired
    private OrganizationSetUpRepository orgRepo;

    @PostMapping(path = "/saveOrganization")
    public Map<String, Object> saveOrganizationSetUp(@RequestBody String data) {
            Map<String, Object> resultMap = new HashMap<>();
        try {
            
             String decryptedJson = EncryptDecryptUtils.decrypt(data);
            
             List<OrganizationSetUp> orgList =
        mapper.readValue(
                decryptedJson,
                new TypeReference<List<OrganizationSetUp>>() {}
        );
            orgRepo.saveAll(orgList);
            resultMap.put("status", "success");
           } catch (Exception ex) {
               ex.printStackTrace();
            logger.info("Problem in OrganizationSetUpController -> saveOrganizationSetUp() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/getOrganization")
    public Map getOrganization(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
          Map  map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
          resultMap =orgSetUpService.fetchByOrgId(Long.parseLong(map.get("organization_id").toString()));
        } catch (Exception ex) {
            logger.error("Problem in OrganizationSetUpController -> getOrganization() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;

    }

    @PostMapping(path = "/updateOrganization")
    public Map updateorganizationById(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = orgSetUpService.findById(Long.parseLong(map.get("id").toString()));
//           if (org != null) {
//               resultMap.put("list",org);
//               resultMap.put("status","success");
//            }  
        } catch (Exception ex) {
            logger.info("Problem in OrganizationSetUpController -> updateorganizationById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping(path = "/delete")
    public Map deleteorganizationById(@RequestBody String data) {
        Map resultMap = null;
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = orgSetUpService.delete(Long.parseLong(map.get("id").toString()));
        } catch (IOException | NumberFormatException ex) {
            logger.info("Problem in OrganizationController -> deleteById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping(path = "/isAlreadyExit")
    public Map AlreadyExit(@RequestBody String data) {
       Map<String, Object> resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
             resultMap = orgSetUpService.isAlreadyExist(Long.parseLong(map.get("organization_id").toString()));
        } catch (IOException | NumberFormatException ex) {
            logger.info("Problem in OrganizationController -> AlreadyExit() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping(path = "/saveOrUpdateLogo")
    public Map saveORUpdateLogo(@RequestBody String data) {
       Map<String, Object> resultMap = new HashMap<>();
        try {
             resultMap = orgSetUpService.saveOrUpdateLogo(data);
             System.out.println("output "+resultMap);
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping(path = "/getAuthorizatory")
    public Map getAuthorizatory(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
          Map  map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
          resultMap =orgSetUpService.getAuthorizatory(Long.parseLong(map.get("organization_id").toString()));
        } catch (Exception ex) {
            logger.error("Problem in OrganizationSetUpController -> getAuthorizatory() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;

    }
    
     @PostMapping(path = "/saveAuthorizatory")
    public Map saveAuthorizatory(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {

            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = orgSetUpService.saveAuthorizatory(map);
           } catch (Exception ex) {
            logger.info("Problem in OrganizationSetUpController -> saveAuthorizatory() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}

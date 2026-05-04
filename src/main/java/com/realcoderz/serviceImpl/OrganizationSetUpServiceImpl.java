/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.AuthorizatorySetup;
import com.realcoderz.model.OrganizationSetUp;
import com.realcoderz.repository.AuthorizatorySetupRepo;
import com.realcoderz.repository.OrganizationSetUpRepository;
import com.realcoderz.service.OrganizationSetUpService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Astha
 */
@Service
public class OrganizationSetUpServiceImpl implements OrganizationSetUpService {

    static final Logger logger = LoggerFactory.getLogger(OrganizationSetUpServiceImpl.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private OrganizationSetUpRepository orgRepo;
    
    @Autowired
    private AuthorizatorySetupRepo authoRepo;

    //Saving the organization details
    public Map save(Map map) {
         Map resultMap = new HashMap<>();
         try{
        OrganizationSetUp org = mapper.convertValue(map, OrganizationSetUp.class);
        if (org != null) {
            orgRepo.save(org);
            resultMap.clear();
            resultMap.put("status", "success");
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
        }}catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> save() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map fetch() {
        Map resultMap = new HashMap<>();
        try {
             List<OrganizationSetUp> org = orgRepo.findAll();
            if (org != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", org);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "organization is not exit");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> fetch() :: ", ex);
        }
        return resultMap;
    }

    //updating oranization details 
    @Override
    public Map update(Long id, OrganizationSetUp orgs) {
        Map resultMap = new HashMap<>();
        try {
            Optional<OrganizationSetUp> org = orgRepo.findById(id);
            if (org.isPresent()){
                resultMap.clear();
                resultMap.put("list",orgRepo.save(orgs));
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "OrganizationSetUp Can't be updated !");
                }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> update() :: ", ex);
        }
        return resultMap;
    }

    //Deleting organization 
    @Override
    public Map delete(Long id) {
        Map resultMap = new HashMap<>();
        try {
             Optional<OrganizationSetUp> org = orgRepo.findById(id);
            if (org.isPresent()) {
                resultMap.clear();
                orgRepo.delete(org.get());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "OrganizationSetUp is not deleted !");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception found");
            logger.info("Problem in OrganizationSetUpServiceImpl -> delete() :: ", ex);
        }
        return resultMap;
    }

    //Finding set up by thier Id
    @Override
    public Map findById(Long id) {
        Map resultMap = new HashMap();
        try {
            Optional<OrganizationSetUp> setUp = orgRepo.findById(id);
            if (setUp.isPresent()) {
                resultMap.clear();
                resultMap.put("list", setUp.get());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("msg", "OrganizationSetUp is not found");
                resultMap.put("status", "error");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception found");
            logger.info("Problem in OrganizationSetUpServiceImpl -> findById() :: ", ex);
        }
        return resultMap;
    }

    //Already exit the organization or not
    @Override
    public Map isAlreadyExist(Long id) {
         Map resultMap = new HashMap();
        try {
            int org = orgRepo.isAlreadyExits(id);
            if (org > 0) {
                resultMap.clear();
                resultMap.put("return", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("return", false);
                resultMap.put("msg", "OrganizationSetUp is not found !");
                
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> isAlreadyExist() :: ", ex);
        }
        return resultMap;
    }
    
    //fetching details by their organization Id
    @Override
    public Map fetchByOrgId(Long organization_id) {
        Map resultMap = new HashMap<>();
        try {
            List<OrganizationSetUp> org = orgRepo.findSetUpById(organization_id);
            if ( !org.isEmpty()) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", org);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "organization is not available.");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> fetchByOrgId() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map saveOrUpdateLogo(String data) {
        
          Map resultMap = new HashMap();
        try {
            
          Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            System.out.println("payload of saveOrUpdateLogo "+map);
          if(map.get("organization_id") ==null || map.get("organization_id").equals("")){
              resultMap.put("status", "error");
              resultMap.put("msg", "please check your payload");
              return resultMap;
          }

          OrganizationSetUp  orgsetup=orgRepo.saveOrUpdateLogo(Long.parseLong(map.get("organization_id").toString()));
          OrganizationSetUp saveOrgDetails=new OrganizationSetUp();
          if(orgsetup == null){
              saveOrgDetails.setCompanyLogo((map.get("url") != null && !map.get("url").equals("")) ? map.get("url").toString() : null);
              saveOrgDetails.setOrganization_id(Long.parseLong(map.get("organization_id").toString()));
              orgRepo.save(saveOrgDetails);
              resultMap.put("status", "success");
              resultMap.put("msg", "save successfully !!");
          }
          else{
              orgsetup.setCompanyLogo((map.get("url") != null && !map.get("url").equals("")) ? map.get("url").toString() : null);
              orgRepo.save(orgsetup);
              resultMap.put("status", "success");
              resultMap.put("msg", "updated successfully !!");
          }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMap.put("status", "exception");
            resultMap.put("msg", "exception when save or update logo");
        }
        return resultMap;
    }

    @Override
    public Map getAuthorizatory(Long organization_id) {     
        
         Map resultMap = new HashMap<>();
        try {
            
            List<AuthorizatorySetup> authorizatoryDetails=authoRepo.findSetUpById(organization_id);
            
            if ( !authorizatoryDetails.isEmpty()) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", authorizatoryDetails);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "authorizatoryDetails is not available.");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> authorizatoryDetails() :: ", ex);
        }
        return resultMap;
        
     }

    @Override
    public Map saveAuthorizatory(Map map) {
        
         Map resultMap = new HashMap<>();
         try{
        AuthorizatorySetup org = mapper.convertValue(map, AuthorizatorySetup.class);
        if (org != null) {
            authoRepo.save(org);
            resultMap.clear();
            resultMap.put("status", "success");
        } else {
            resultMap.clear();
            resultMap.put("status", "error");
        }}catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in OrganizationSetUpServiceImpl -> save() :: ", ex);

        }
        return resultMap;
               
  }

}
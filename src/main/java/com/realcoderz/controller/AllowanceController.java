/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.AllowanceService;
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

/**
 *
 * @author Mayank
 */
@RestController
@RequestMapping("/allowance")
public class AllowanceController {

    static final Logger logger = LoggerFactory.getLogger(AllowanceController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired

    private AllowanceService allowanceService;

//    Save Allowances
    @PostMapping("/save")
    public Map saveAllowance(@RequestBody String data) {

        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> save() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    Get Allowances
    @PostMapping("/get")
    public Map getAllowances(@RequestBody String data, HttpServletRequest request,@RequestParam(required = false) String search) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            search=search!=null?search:"";
            resultMap = allowanceService.fetch(Long.parseLong(map.get("org_id").toString()), request,search);
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    AllowancesById
    @PostMapping("/findById")
    public Map findById(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.findById(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> findById() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    Delete Allowances
    @PostMapping("/delete")
    public Map deleteAllowanceById(@RequestBody String data) {
        Map resultMap = null;
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.delete(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> deleteAllowanceById() :: ", ex);
        }
        return resultMap;
    }

//    AllowancesByName
    @PostMapping("/findByName")
    public Map findByName(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            resultMap = allowanceService.fetchbyAllowanceName(Long.parseLong(map.get("allowance_id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> findByName() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/findAllowances")
    public Map findAllowances(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);

            resultMap = allowanceService.findAllowanceNameFromAllowanceId(Long.parseLong(map.get("allowance_id").toString()),Long.parseLong(map.get("org_id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> findAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
//    GetApproved Allowances
    @PostMapping("/getApprovedAllowances")
    public Map getApprovedAllowances(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.fetchApprovedAllowances(Long.parseLong(map.get("org_id").toString()), Integer.parseInt(map.get("month").toString()), Integer.parseInt(map.get("year").toString()), map.get("employee_Type").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getApprovedAllowances() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

//    AlreadyExist Allowances
    @PostMapping("/isExist")
    public Map isExist(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.isAlreadyExist(map.get("name").toString(), Long.parseLong(map.get("org_id").toString()), map.get("employee_type").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> isExist() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getForOrgAdmin")
    public Map getAllowancesForOrganizationAdmin(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getAllowanceNames(Long.parseLong(map.get("org_id").toString()),map.get("type").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getAllowancesForOrganizationAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/getForSuperAdmin")
    public Map getAllowancesForSuperAdmin(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getAllowanceNameForSuperAdmin(map.get("type").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getAllowancesForOrganizationAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/getDataForSuperAdmin")
    public Map getAllowancesDataForSuperAdmin() {
        Map resultMap = new HashMap<>();
        try {
            resultMap = allowanceService.getAllowanceDataForSuperAdmin();
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getAllowancesDataForSuperAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/getParticularAllowanceDataForSuperAdmin")
    public Map getParticularAllowanceDataForSuperAdmin(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.findById(Long.parseLong(map.get("id").toString()));
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getParticularAllowanceDataForSuperAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/isExistForSuperAdmin")
    public Map isExistForSuperAdmin(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.isAllowanceExistForSuperAdmin(map.get("name").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> isExistForSuperAdmin() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
     @PostMapping("/getAllowanceNameForOrganization")
    public Map getAllowancesForOrganization(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getAllowanceNameForOrganization(Long.parseLong(map.get("id").toString()),map.get("type").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getAllowancesForOrganization() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
      @PostMapping("/checkAllowanceType")
    public Map checkAllowanceType(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.checkAllowanceType(map.get("name").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> checkAllowanceType() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    @PostMapping("/getWorkerAllowance")
    public Map getWorkerAllowanceName(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            Long orgId=Long.parseLong(map.get("org_id").toString());
            String employeeType=map.get("employeeType").toString();
            resultMap = allowanceService.getWorkerAllowanceName(orgId,employeeType);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in AllowanceController -> getWorkerAllowanceName() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
    @PostMapping("/approvedRejectAllowance")
    public Map approvedRejectAllowance(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.approvedRejectAllowance(Long.parseLong(map.get("id").toString()),map.get("status").toString());
        } catch (Exception ex) {
            logger.info("Problem in AllowanceController -> getAllowancesForOrganization() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
    
      @PostMapping("/getGroupList")
    public Map getGroupList(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getGroupList(Long.parseLong(map.get("organizationId").toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in AllowanceController -> getGroupList() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
        @PostMapping("/getGradeList")
    public Map getGradeList(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getGradeList(Long.parseLong(map.get("organizationId").toString()));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in AllowanceController -> getGradeList() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
            @PostMapping("/getEmployeeByOrgIdAndEmployeeType")
    public Map getEmployeeByOrgIdAndEmployeeType(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            if(map.get("organizationId")!=null && map.get("type")!=null ){
                            resultMap = allowanceService.getEmployeeByOrgIdAndEmployeeType(Long.parseLong(map.get("organizationId").toString()),map.get("type").toString());

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.info("Problem in AllowanceController -> getGradeList() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
                @PostMapping("/getGradeOrGroupList")
    public Map getGradeOrGroupList(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = allowanceService.getGradeOrGroupList(Long.parseLong(map.get("id").toString()),map.get("type").toString(),map.get("employeeType").toString());
        } catch (Exception ex) {
             ex.printStackTrace();
            logger.info("Problem in AllowanceController -> getGradeList() :: ", ex);
            resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }
    
}

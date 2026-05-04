/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Allowance;
import com.realcoderz.model.AllowanceTemplate;
import com.realcoderz.model.Deduction;
import com.realcoderz.model.DeductionTemplate;
import com.realcoderz.repository.DeductionAllowanceMappingRepository;
import com.realcoderz.repository.DeductionRepository;
import org.springframework.stereotype.Service;
import com.realcoderz.service.DeductionService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 */
@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements DeductionService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(DeductionServiceImpl.class);

    private final DeductionRepository deductionRepository;
    private final DeductionAllowanceMappingRepository deductionAllowanceMappingRepository;
    
   
    
   
    
   
//    Deduction d=new Deduction();
//    
//    DeductionAllowanceMapping deduction_allowance_mapping=new DeductionAllowanceMapping();
    
    //    Save Deductions
    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
//            Set<String> allowanceNamesSet=new HashSet<>();
//            List<String> allowance=new ArrayList<>();
//            List l=new ArrayList<>();
            Deduction deduction = mapper.convertValue(map, Deduction.class);
            System.out.println("deduction 82"+" "+deduction.toString());
            if (deduction != null) {
 //              d.setDeduction_id(deduction.getDeduction_id());
//                d.setAmount(deduction.getAmount());
//                d.setDeduction(deduction.getDeduction());
//                d.setDeduction_name(deduction.getDeduction_name());
//                d.setDeductiondesc(deduction.getDeductiondesc());
//                d.setEffective_date(deduction.getEffective_date());
//                d.setEmployee_email(deduction.getEmployee_email());
//                d.setOrganization_id(deduction.getOrganization_id());
//                d.setPercentage(deduction.getPercentage());
//                d.setSalary(deduction.getSalary());
//                d.setStatus(deduction.getStatus());
//                d.setSupervisor_status(deduction.getSupervisor_status());
//                d.setType_of_deduction(deduction.getType_of_deduction());
//                d.setApproved_flag(deduction.getApproved_flag());
//                d.setEmployer_percentage(deduction.getEmployer_percentage());
//                d.setEmployee_type(deduction.getEmployee_type());
                Deduction savedDeduction = deductionRepository.save(deduction);
//               Long deductionId=savedDeduction.getDeduction_id();
//                if(map.get("deductionOnAllowance") !=null){
//                List<LinkedHashMap> allowanceNames=(List<LinkedHashMap>) map.get("deductionOnAllowance");
//                allowanceNames.stream().forEach(a->{
//                allowanceNamesSet.add(a.get("allowance_name").toString());
//                });
//                }
//                allowance.addAll(allowanceNamesSet);
//                List<LinkedCaseInsensitiveMap> allowance_id=allowanceRepository.getAllowanceIdFromAllowanceName(allowance,deduction.getOrganization_id(),deduction.getEmployee_type());
//                for(LinkedCaseInsensitiveMap id :allowance_id){
//                    deduction_allowance_mapping=new DeductionAllowanceMapping();
//                    deduction_allowance_mapping.setDeduction_id(deductionId);
//                    deduction_allowance_mapping.setAllowance_id(Long.parseLong(id.get("allowance_id").toString()));
//                    l.add(deduction_allowance_mapping);
//                }
//                
//                deductionAllowanceMappingRepository.saveAll(l);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction not saved.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> save() :: ", ex);
        }
        return resultMap;

    }

    //    Fetch All Deductions By Org Id
    @Override
    public Map fetch(Long org_id, HttpServletRequest request,String search) {
        Map resultMap = new HashMap<>();
        try {
            logger.info("In Deduction Fetch Method Execution Starts :: with organization id "+org_id);
            
           // List<Deduction> deductions = deductionRepository.findDeductionById(org_id);
           List<LinkedCaseInsensitiveMap> deductions = deductionRepository.deductionForGrid(org_id,search);
           
            if (deductions != null) {
                resultMap.put("status", "success");
                resultMap.put("list", deductions);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction's list is not available.");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> fetch() :: ", ex);
        }
        return resultMap;
    }

    //    Delete Deductions
    @Override
    public Map delete(Long id, String employee_type) {
        Map resultMap = new HashMap<>();
        try {
            Optional<Deduction> deduction = deductionRepository.findById(id);
            if (deduction != null) {
                deductionRepository.delete(deduction.get());
                if(employee_type.equalsIgnoreCase("Worker")){
                    deductionAllowanceMappingRepository.deleteDeduction(deduction.get().getDeduction_id());
                }
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction not deleted.");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> delete() :: ", ex);
        }
        return resultMap;
    }

    //    Fetch Deductions By Id
    @Override
    public Map findById(Long id) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Optional<Deduction> deductions = deductionRepository.findById(id);
            if (deductions.isPresent()) {
              Deduction    deduction = deductions.get();
        Set<DeductionTemplate> uniqueTemplates = new LinkedHashSet<>(deduction.getDeductionTemplate());
           
        deduction.setDeductionTemplate(new ArrayList<>(uniqueTemplates));
                resultMap.clear();
                resultMap.put("list",deduction );
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> findById() :: ", ex);

        }
        return resultMap;
    }

    //    Fetch Deductions By Id
    @Override
    public Map fetchbyDeductionName(Long id) {
        Map resultMap = new HashMap<>();
        try {

            Optional<Deduction> deduction = deductionRepository.findById(id);
            System.out.println(deduction);
            if (deduction.isPresent()) {
                resultMap.clear();
                resultMap.put("list", deduction.get());
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction name  not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> fetchbyDeductionName() :: ", ex);

        }
        return resultMap;
    }

    //    Fetch All Approved Deductions
    @Override
    public Map fetchApprovedDeductions(Long org_id, Integer month, Integer year, String employee_type) {
        Map resultMap = new HashMap<>();
        try {
            List<Deduction> deductions = deductionRepository.findApprovedDeductionsexceptGratuity(org_id, new Date(year-1900,month,1), employee_type);
            System.out.println(deductions);
            if (deductions != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", deductions);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "No Deduction is approved by supervisor..");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> fetchApprovedDeductions() :: ", ex);

        }
        return resultMap;
    }

    //    Deduction is Already Exist
    @Override
    public Map isAlreadyExist(String name, Long id, String employee_type) {
        Map resultMap = new HashMap<>();
        try {
            int allowance = deductionRepository.isDeductionExist(name, id, employee_type);
            if (allowance > 0) {
                resultMap.clear();
                resultMap.put("result", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("result", false);
                resultMap.put("msg", "Deduction not found.!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> isAlreadyExist() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getDeductionForSuperAdmin() {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> deductions = deductionRepository.getDeductionNameForSuperAdmin();
            if (deductions != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", deductions);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction's list is not available.");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> getDeductionForSuperAdmin() :: ", ex);
        }
        return resultMap;
    }

    @Override
    public Map getDeductionNameForOrganization(Long id) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> deductions = deductionRepository.getDeductionNameForOrganization(id);
            if (deductions != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", deductions);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Deduction's list is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> getDeductionNameForOrganization() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map checkDeductionType(String name) {
        Map resultMap = new HashMap<>();
        try {
            List<LinkedCaseInsensitiveMap> deductions = deductionRepository.checkTypeOfDeduction(name);
            resultMap.clear();
            resultMap.put("status", "success");
            resultMap.put("list", deductions);
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> checkDeductionType() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map isDeductionExistForSuperAdmin(String name) {
         Map resultMap = new HashMap<>();
        try {
            int deduction = deductionRepository.isDeductionExistForSuperAdmin(name);
            if (deduction>0) {
                resultMap.clear();
                resultMap.put("result", true);
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("result", false);
                resultMap.put("msg", "Deduction not found.!");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> isDeductionExistForSuperAdmin() :: ", ex);

        }
        return resultMap;
    }
    
     @Override
    public Map approvedRejectDeducion(Long id, String status) {
        
            Map resultMap = new HashMap<>();
        try {
            int deduction = deductionRepository.updateStatus(status,id);
            if (deduction>0) {
                
                resultMap.put("msg", "status Updated Successfully");
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("result", false);
                resultMap.put("msg", "Deduction Status Not Updated");
            }
        } catch (Exception ex) {
            resultMap.put("status", "exception");
            logger.info("Problem in DeductionServiceImpl -> approvedRejectDeducion() :: ", ex);

        }
        return resultMap;
        
    }


}

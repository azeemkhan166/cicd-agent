/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.ProfessionalTaxNew;
import com.realcoderz.repository.ProfessionalTaxNewRepo;
import com.realcoderz.service.ProfessionalTaxServiceNew;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author tause
 */
@Service
public class ProfessionalTaxNewServiceImpl implements ProfessionalTaxServiceNew
{
    @Autowired
    private ProfessionalTaxNewRepo repo;
    
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map saveTaxSlab(String data) {
        Map response=new HashMap();
         try{
             ProfessionalTaxNew map = mapper.readValue(EncryptDecryptUtils.decrypt(data), ProfessionalTaxNew.class);
             if(map.getProfessionalTaxId()==null){
                 repo.save(map);
             response.put("status", "success");
             response.put("msg", "Tax slab saved successfully");
             }else{
                ProfessionalTaxNew tax= repo.findById(map.getProfessionalTaxId()).get(); 
                tax.setMinSalary(map.getMinSalary());
                tax.setMaxSalary(map.getMaxSalary());
                tax.setStartMonth(map.getStartMonth());
                tax.setEndMonth(map.getEndMonth());
                tax.setFrequencyOfDeduction(map.getFrequencyOfDeduction());
                tax.setGender(map.getGender());
                tax.setTaxAmount(map.getTaxAmount());
                repo.save(tax);
             response.put("status", "success");
             response.put("msg", "Tax slab updated successfully");
             }
             
           
             
         }catch(Exception e){
             e.printStackTrace();
             response.put("status", "exception");
             response.put("msg", e.getMessage());
         }
         return response;
    }

    @Override
    public Map getProfessionalTaxSlabs(Integer pageNo, Integer pageSize,String searchWord) {
          Map response=new HashMap();
          try{
              Pageable pageRequest = PageRequest.of(pageNo-1, pageSize);
              Page<ProfessionalTaxNew> slabList=repo.getAllProfessionalTax(searchWord, pageRequest);
            Map metaData = new HashMap();
            metaData.put("totalPages", slabList.getTotalPages());
            metaData.put("currentPage", pageNo);
            metaData.put("pageSize", pageSize);
            metaData.put("totalEntries", slabList.getTotalElements());
            response.put("metadata", metaData);
            response.put("status", "success");
            response.put("data",slabList.getContent());
          }catch(Exception e){
           e.printStackTrace();

          }
          return response;
    }

    @Override
    public Map getSingleProfessionalTaxSlabs(String data) {
        Map response=new HashMap();
        try{
       Long id = mapper.readValue(EncryptDecryptUtils.decrypt(data), Long.class);
       ProfessionalTaxNew tax= repo.findById(id).get();
       response.put("status", "success");
       response.put("data", tax);        
        }catch(Exception e){
           e.printStackTrace();  
        }
        return response;
    }
    
    
    
}

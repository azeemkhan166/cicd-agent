/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.realcoderz.controller;

import com.realcoderz.serviceImpl.ProfessionalTaxNewServiceImpl;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tause
 */
@RestController
@RequestMapping("/professionalTaxNew")
public class ProfessionalTaxSlabNewController {
    
    @Autowired
    private ProfessionalTaxNewServiceImpl profService;
    
    @PostMapping("/save")
    public Map save(@RequestBody String mp){
        Map response=new HashMap();
         try{
             response= profService.saveTaxSlab(mp);
             
         }catch(Exception e){
             e.printStackTrace();
         }
         return response;
    }
    
    @PostMapping("/getAllProfessionalTaxSlab")
    public Map getAllTaxSlab(@RequestParam(required = false)String pageNumber,@RequestParam(required = false)String pageSize,@RequestParam(required = false)String search){
        Map response=new HashMap();
         try{
            Integer page = pageNumber != null ? Integer.parseInt(pageNumber.toString()) : 1;
            Integer size = pageSize != null ? Integer.parseInt(pageSize.toString()) : 100000;
            String searchWord = search != null ? search.toString() : "";
            response= profService.getProfessionalTaxSlabs(page,size,searchWord);
             
         }catch(Exception e){
             e.printStackTrace();
         }
         return response;
    }
    
    @PostMapping("/getSingleTaxSlab")
    public Map getSingleTaxSlab(@RequestBody String mp){
        Map response=new HashMap();
         try{
             response= profService.getSingleProfessionalTaxSlabs(mp);
             
         }catch(Exception e){
             e.printStackTrace();
         }
         return response;
    }
    
    
    
}

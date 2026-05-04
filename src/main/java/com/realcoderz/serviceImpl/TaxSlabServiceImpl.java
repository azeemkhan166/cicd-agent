/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * ijihj
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.SurCharge;
import com.realcoderz.model.TaxSlab;
import com.realcoderz.repository.TaxSlabRepository;
import com.realcoderz.service.TaxSlabService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Mayank
 * edited By Astha
 */
@Service
public class TaxSlabServiceImpl implements TaxSlabService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(TaxSlabServiceImpl.class);

    @Autowired
    TaxSlabRepository taxSlabRepo;

    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
            TaxSlab taxSlab = mapper.convertValue(map, TaxSlab.class);
            if (taxSlab != null) {
                taxSlabRepo.save(taxSlab);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Tax Slab..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in TaxSlabServiceImpl -> save() :: ", ex);

        }
        return resultMap;

    }

    @Override
    public Map getAllTaxSlabs() {
        Map resultMap = new HashMap<>();
        try {
            List<TaxSlab> taxSlab = taxSlabRepo.findTaxSlab();
            if (taxSlab != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", taxSlab);
//                System.out.println(taxSlab);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
            resultMap.put("msg", "taxSlab is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in TaxSlabServiceImpl -> getAllTaxSlabs() :: ", ex);

        }
        
        return resultMap;
    }

    @Override
    public Map saveAllTaxSlabs(Map map) {
        Map resultMap = new HashMap<>();
         try {
     if(map.containsKey("list")){
         List<LinkedHashMap> dataList = (List<LinkedHashMap>) map.get("list");
         if(!dataList.isEmpty()){
//             List<TaxSlab> taxslabList = taxSlabRepo.findTaxSlab(Long.parseLong(dataList.get(0).get("organization_id").toString())); 
         List<TaxSlab> list = new ArrayList<>();
//             dataList.stream().forEach(data -> {
//              TaxSlab allTaxSlab = mapper.convertValue(data, TaxSlab.class);
//              if(taxslabList.isEmpty()){
//                  list.add(allTaxSlab);
//              }else {
//                        Optional<TaxSlab> first = taxslabList.stream().filter(d -> Objects.equals(d.getSlab_id(), allTaxSlab.getSlab_id())).findFirst();
//                        if (first.isPresent()) {
//                            allTaxSlab.setSlab_id(first.get().getSlab_id());
//                            list.add(allTaxSlab);
//                        }
//                    }
//              
//                 });
                   
                 

                 dataList.stream().forEach(data -> {
                TaxSlab allTaxSlab = mapper.convertValue(data, TaxSlab.class);
                 list.add(allTaxSlab);
                 
                 });
                    
                 taxSlabRepo.saveAll(list);
                 resultMap.clear();
                 resultMap.put("status", "success");
//                 resultMap.put("list", taxSlabRepo.saveAll(list));
             } else {
                 resultMap.clear();
                 resultMap.put("status", "error");
                 resultMap.put("msg", "Error while saving tax slab..!!");
             }}
         } catch (Exception ex) {
             resultMap.clear();
             resultMap.put("status", "exception");
             logger.info("Problem in TaxSlabServiceImpl -> save() :: ", ex);

        }
           return resultMap;
    }
    
     @Override
    public Map updateTaxSlabs(Map map) {
        Map resultMap = new HashMap<>();
         try {
        if (map.containsKey("slab_id") && map.get("slab_id") != null && map.containsKey("data") && map.get("data") != null) {
            TaxSlab slab_id = taxSlabRepo.findById(Long.parseLong(map.get("slab_id").toString())).get();
            if (slab_id != null) {
                TaxSlab Slab = mapper.convertValue(map.get("data"), TaxSlab.class);
                Slab.setSlab_id(slab_id.getSlab_id());
                taxSlabRepo.save(Slab);
                resultMap.put("status", "success");
                 resultMap.put("list",  taxSlabRepo.save(Slab));
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "No data found!");
            }}
         } catch (Exception ex) {
             resultMap.clear();
             resultMap.put("status", "exception");
             logger.info("Problem in TaxSlabServiceImpl -> save() :: ", ex);

        }
        return resultMap;
    }

}

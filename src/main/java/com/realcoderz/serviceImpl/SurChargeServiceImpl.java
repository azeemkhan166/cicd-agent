/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.SurCharge;
import com.realcoderz.repository.SurChargeRepository;
import com.realcoderz.service.SurChargeService;
import java.util.ArrayList;
import java.util.HashMap;
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
 * @author Mayank edited By Astha
 */
@Service
public class SurChargeServiceImpl implements SurChargeService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(SurChargeServiceImpl.class);

    @Autowired
    SurChargeRepository surChargeRepo;

    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
            SurCharge surCharge = mapper.convertValue(map, SurCharge.class);
            if (surCharge != null) {
                surChargeRepo.save(surCharge);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Sur Charge..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SurChargeServiceImpl -> save() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getAllSurchages() {
        Map resultMap = new HashMap<>();
        try {
            List<SurCharge> surCharge = surChargeRepo.findSurCharge();
            if (surCharge != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", surCharge);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "surCharge is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SurChargeServiceImpl -> get() :: ", ex);

        }
        return resultMap;

    }

    @Override
    public Map saveAllSurchages(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("list")) {
                List<LinkedHashMap> dataList = (List<LinkedHashMap>) map.get("list");
                if(!dataList.isEmpty()){
                List<SurCharge> surchargeList = surChargeRepo.findSurCharge();
                List<SurCharge> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    SurCharge allSurCharge = mapper.convertValue(data, SurCharge.class);
                    if (surchargeList.isEmpty()) {
                        list.add(allSurCharge);
                    } else {
                        Optional<SurCharge> first = surchargeList.stream().filter(d -> Objects.equals(d.getSurcharge_id(), allSurCharge.getSurcharge_id())).findFirst();
                        if (first.isPresent()) {
                            allSurCharge.setSurcharge_id(first.get().getSurcharge_id());
                            list.add(allSurCharge);
                        }
                    }
                });
                surChargeRepo.saveAll(list);
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", surChargeRepo.saveAll(list));
                }
            } else {
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Surcharge..!!!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in SurChargeServiceImpl -> save() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map updateSurchages(Map map) {
        Map resultMap = new HashMap<>();
        try {
            if (map.containsKey("surcharge_id") && map.get("surcharge_id") != null && map.containsKey("data") && map.get("data") != null) {
                SurCharge surcharge_id = surChargeRepo.findById(Long.parseLong(map.get("surcharge_id").toString())).get();
                if (surcharge_id != null) {
                    SurCharge surchages = mapper.convertValue(map.get("data"), SurCharge.class);
                    surchages.setSurcharge_id(surcharge_id.getSurcharge_id());
                    surChargeRepo.save(surchages);
                    resultMap.put("status", "success");
                    resultMap.put("list", surChargeRepo.save(surchages));
                } else {
                    resultMap.put("status", "error");
                    resultMap.put("msg", "No data found!");
                }
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in TaxSlabServiceImpl -> updateTaxSlabs() :: ", ex);

        }
        return resultMap;
    }
}

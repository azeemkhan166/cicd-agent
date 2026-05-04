/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Relief87A;
import com.realcoderz.repository.Relief87ARepository;
import com.realcoderz.service.Relief87AService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 * edited By Astha
 */
@Service
public class Relief87AServiceImpl implements Relief87AService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(Relief87AServiceImpl.class);

    @Autowired
    Relief87ARepository relief87ARepo;

    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Relief87A relief87A = mapper.convertValue(map, Relief87A.class);
            if (relief87A != null) {
                relief87ARepo.save(relief87A);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Relief u/s 87A..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in Relief87AServiceImpl -> save() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map getRelief87A() {

        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap relief87A = relief87ARepo.getIncomeOfRelief87A();
            if (relief87A != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", relief87A);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Relief u/s 87A is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in Relief87AServiceImpl -> get() :: ", ex);

        }
        return resultMap;
    }

}

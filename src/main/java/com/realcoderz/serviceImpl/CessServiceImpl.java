/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Cess;
import com.realcoderz.repository.CessRepository;
import com.realcoderz.service.CessService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author Mayank
 * edited By Astha
 */
@Service
@RequiredArgsConstructor
public class CessServiceImpl implements CessService {

    ObjectMapper mapper = new ObjectMapper();

    static final Logger logger = LoggerFactory.getLogger(CessServiceImpl.class);

    private final CessRepository cessRepo;

    @Override
    public Map save(Map map) {
        Map resultMap = new HashMap<>();
        try {
            Cess cess = mapper.convertValue(map, Cess.class);
            if (cess != null) {
                cessRepo.save(cess);
                resultMap.clear();
                resultMap.put("status", "success");
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Error while saving Cess..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in CessServiceImpl -> save() :: ", ex);

        }
        return resultMap;
    }

    @Override
    public Map get() {
        Map resultMap = new HashMap<>();
        try {
            LinkedCaseInsensitiveMap cess = cessRepo.getRateOfCess();
            if (cess != null) {
                resultMap.clear();
                resultMap.put("status", "success");
                resultMap.put("list", cess);
            } else {
                resultMap.clear();
                resultMap.put("status", "error");
                resultMap.put("msg", "Cess is not available..!");
            }
        } catch (Exception ex) {
            resultMap.clear();
            resultMap.put("status", "exception");
            logger.info("Problem in CessServiceImpl -> get() :: ", ex);

        }
        return resultMap;
    }

}

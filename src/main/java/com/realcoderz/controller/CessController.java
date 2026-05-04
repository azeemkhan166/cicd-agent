package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.CessService;
import com.realcoderz.util.EncryptDecryptUtils;
import java.util.HashMap;
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
 * @author Mayank
 * edited By Astha
 */
@RestController
@RequestMapping("/cess")
public class CessController {

    static final Logger logger = LoggerFactory.getLogger(CessController.class);

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    CessService cessService;

    @PostMapping("/save")
    public Map saveCess(@RequestBody String data) {

        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = cessService.save(map);
        } catch (Exception ex) {
            logger.info("Problem in CessController -> saveCess() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getCess")
    public Map getCess() {
        Map resultMap = new HashMap<>();
        try {
//            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = cessService.get();
        } catch (Exception ex) {
            logger.info("Problem in CessController -> getCess() :: ", ex);
             resultMap.clear();
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}

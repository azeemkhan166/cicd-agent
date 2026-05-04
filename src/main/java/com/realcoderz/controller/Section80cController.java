/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.Section80cService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lalit Raghav
 */
@RestController
@RequestMapping("/setion80c")
public class Section80cController {

    ObjectMapper mapper = new ObjectMapper();
    static final Logger logger = LoggerFactory.getLogger(Section80cController.class);

    @Autowired
    public Section80cService section80cservice;

    @PostMapping(path = "/getsection80c")
    public Map getSection80c(@RequestBody String data) {
        Map resultMap = new HashMap<>();

        try {
            resultMap = section80cservice.findSectionById(data);
        } catch (Exception ex) {
            logger.info("Problem in Section80cController -> getSection80c() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}

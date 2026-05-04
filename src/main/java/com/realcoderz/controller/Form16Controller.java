package com.realcoderz.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.Form16;
import com.realcoderz.service.Form16Service;
import com.realcoderz.util.EncryptDecryptUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/form16")
@Slf4j
public class Form16Controller {

	@Autowired
	private Form16Service form16Service;

	@Autowired
	private ObjectMapper mapper;
	
	@PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public Map save(@RequestBody String data) {
        Map response = new HashMap();
        try {
        	Form16 map = mapper.readValue(EncryptDecryptUtils.decrypt(data), Form16.class);
        	response = form16Service.save(map);
        	System.out.println(map);
            return response;
        } catch (Exception e) {
            log.error("exception in save() :: " + e.getMessage());
            response.put("status", "exception");
            response.put("msg","Please try again!");
        }
        return response;
    }
	
	@PostMapping(value = "/get", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public Map get(@RequestBody String data) {
        Map response = new HashMap();
        try {
        	Form16 map = mapper.readValue(EncryptDecryptUtils.decrypt(data), Form16.class);
        	response = form16Service.get(map);
            return response;
        } catch (Exception e) {
            log.error("exception in get() :: " + e.getMessage());
            response.put("status", "exception");
            response.put("msg", "Please try again!");
        }
        return response;
    }

}

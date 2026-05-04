package com.realcoderz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.model.ProfessionalTaxSlab;
import com.realcoderz.service.ProfessionalTaxService;
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
 * @author Pooja Gupta
 */
@RestController
@RequestMapping("/professionalTax")
public class ProfessionalTaxSlabController {

    private static final Logger logger = LoggerFactory.getLogger(ProfessionalTaxSlabController.class);

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ProfessionalTaxService professionaltaxService;

    @PostMapping("/get")
    public Map findStateWiseProfessionaTax() {
        Map resultMap = new HashMap<>();
        try {
            resultMap = professionaltaxService.getProfessionalTaxSlabs();
        } catch (Exception ex) {
            logger.info("Problem in ProfessionTaxController -> findStateWiseProfessionaTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/save")
    public Map saveStatewiseProfessionalTax(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Map map = mapper.readValue(EncryptDecryptUtils.decrypt(data), LinkedCaseInsensitiveMap.class);
            resultMap = professionaltaxService.saveProfessionTaxSlabs(map);
        } catch (JsonProcessingException ex) {
            logger.error("Problem in ProfessionTaxController -> saveStatewiseProfessionalTax() :: ", ex);
            resultMap.put("status", "error");
        } catch (Exception ex) {
            logger.warn("Problem in ProfessionTaxController -> saveStatewiseProfessionalTax() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/getBySlabId")
    public Map fetchProfessionalTaxSlabById(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Long slabId = mapper.readValue(EncryptDecryptUtils.decrypt(data), Long.class);
            ProfessionalTaxSlab taxSlab = professionaltaxService.fetchProfessionalTaxSlabById(slabId);
            resultMap.put("data", taxSlab);
            resultMap.put("status", "success");
        } catch (JsonProcessingException ex) {
            logger.error("Problem in ProfessionTaxController -> fetchProfessionalTaxSlabById() :: ", ex);
            resultMap.put("status", "error");
        } catch (Exception ex) {
            logger.warn("Problem in ProfessionTaxController -> fetchProfessionalTaxSlabById() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

    @PostMapping("/removeSlab")
    public Map removeProfessionalTaxSlabById(@RequestBody String data) {
        Map resultMap = new HashMap<>();
        try {
            Long slabId = mapper.readValue(EncryptDecryptUtils.decrypt(data), Long.class);
            professionaltaxService.removeProfessionalTaxSlabById(slabId);
            resultMap.put("status", "success");
        } catch (JsonProcessingException ex) {
            logger.error("Problem in ProfessionTaxController -> removeProfessionalTaxSlabById() :: ", ex);
            resultMap.put("status", "error");
        } catch (Exception ex) {
            logger.warn("Problem in ProfessionTaxController -> removeProfessionalTaxSlabById() :: ", ex);
            resultMap.put("status", "exception");
        }
        return resultMap;
    }

}

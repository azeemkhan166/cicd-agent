package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.TaxSlabService;
import com.realcoderz.util.EncryptDecryptUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = TaxSlabController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class TaxSlabControllerTest {

    static final String TEST_KEY = "TestKey123456789";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaxSlabService taxSlabService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setEncryptionKey() {
        ReflectionTestUtils.setField(EncryptDecryptUtils.class, "encryptionKey", TEST_KEY);
    }

    private String encrypt(Object payload) throws Exception {
        return EncryptDecryptUtils.encrypt(objectMapper.writeValueAsString(payload));
    }

    @Test
    void getTaxSlabs_returnsServiceResponse() throws Exception {
        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("status", "success");
        serviceResponse.put("slabs", Arrays.asList("0-250000:0%", "250001-500000:5%"));
        when(taxSlabService.getAllTaxSlabs()).thenReturn(serviceResponse);

        mockMvc.perform(post("/taxSlab/getTaxSlabs")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void getTaxSlabs_serviceThrowsException_returnsExceptionStatus() throws Exception {
        when(taxSlabService.getAllTaxSlabs()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/taxSlab/getTaxSlabs")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    @Test
    void saveTaxSlabs_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 1L);
        requestData.put("slabs", Collections.emptyList());

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "saved");
        when(taxSlabService.saveAllTaxSlabs(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/taxSlab/saveTaxSlabs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("saved"));
    }

    @Test
    void saveTaxSlabs_invalidData_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/taxSlab/saveTaxSlabs")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not-encrypted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }
}

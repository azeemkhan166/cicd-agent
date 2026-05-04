package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.CessService;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CessController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class CessControllerTest {

    static final String TEST_KEY = "TestKey123456789";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CessService cessService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setEncryptionKey() {
        ReflectionTestUtils.setField(EncryptDecryptUtils.class, "encryptionKey", TEST_KEY);
    }

    private String encrypt(Object payload) throws Exception {
        return EncryptDecryptUtils.encrypt(objectMapper.writeValueAsString(payload));
    }

    @Test
    void getCess_returnsServiceResponse() throws Exception {
        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("status", "success");
        serviceResponse.put("rate", 4.0);
        when(cessService.get()).thenReturn(serviceResponse);

        mockMvc.perform(post("/cess/getCess")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.rate").value(4.0));
    }

    @Test
    void getCess_serviceThrowsException_returnsExceptionStatus() throws Exception {
        when(cessService.get()).thenThrow(new RuntimeException("DB unavailable"));

        mockMvc.perform(post("/cess/getCess")
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    @Test
    void saveCess_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 1L);
        requestData.put("rate", 4.0);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "saved");
        when(cessService.save(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/cess/save")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("saved"));
    }

    @Test
    void saveCess_invalidData_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/cess/save")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("garbage-payload"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }
}

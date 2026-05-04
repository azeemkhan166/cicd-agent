package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.EmployeeNetPayService;
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
        controllers = EmployeeNetPayController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class EmployeeNetPayControllerTest {

    static final String TEST_KEY = "TestKey123456789";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeNetPayService employeeNetPayService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setEncryptionKey() {
        ReflectionTestUtils.setField(EncryptDecryptUtils.class, "encryptionKey", TEST_KEY);
    }

    private String encrypt(Object payload) throws Exception {
        return EncryptDecryptUtils.encrypt(objectMapper.writeValueAsString(payload));
    }

    @Test
    void getEmployeesNetPay_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 1L);
        requestData.put("month", 4);
        requestData.put("year", 2024);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("status", "success");
        serviceResponse.put("netPay", 45000.0);
        when(employeeNetPayService.get(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/employeeNetPay/get")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.netPay").value(45000.0));
    }

    @Test
    void getEmployeesNetPay_invalidEncryptedData_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/employeeNetPay/get")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("bad-encrypted-payload"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    @Test
    void getEmployeesNetPay_emptyOrgId_returnsExceptionStatus() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("month", 4);

        when(employeeNetPayService.get(any(Map.class)))
                .thenThrow(new RuntimeException("org_id is required"));

        mockMvc.perform(post("/employeeNetPay/get")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    @Test
    void getEmployeesNetPay_multipleEmployees_returnsListInResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 2L);
        requestData.put("month", 1);
        requestData.put("year", 2024);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("status", "success");
        serviceResponse.put("count", 5);
        when(employeeNetPayService.get(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/employeeNetPay/get")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }
}

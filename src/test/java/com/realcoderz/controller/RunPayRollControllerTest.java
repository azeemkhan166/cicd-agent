package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.RunPayService;
import com.realcoderz.serviceImpl.RunPayServiceImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = RunPayRollController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class RunPayRollControllerTest {

    static final String TEST_KEY = "TestKey123456789";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RunPayService runPayService;

    @MockBean
    RunPayServiceImpl runPayServiceImpl;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setEncryptionKey() {
        ReflectionTestUtils.setField(EncryptDecryptUtils.class, "encryptionKey", TEST_KEY);
    }

    private String encrypt(Object payload) throws Exception {
        return EncryptDecryptUtils.encrypt(objectMapper.writeValueAsString(payload));
    }

    // ── isPayRunSaved ──────────────────────────────────────────────────────────

    @Test
    void isPayRunSaved_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("month", 4);
        requestData.put("year", 2024);
        requestData.put("org_id", 1L);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("status", "success");
        serviceResponse.put("isSaved", true);

        when(runPayService.isPayrollSaved(4, 2024, 1L)).thenReturn(serviceResponse);

        mockMvc.perform(post("/runpay/isPayRunSaved")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.isSaved").value(true));
    }

    @Test
    void isPayRunSaved_decryptionFails_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/runpay/isPayRunSaved")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid-encrypted-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    // ── isSalaryBreakupSaved ───────────────────────────────────────────────────

    @Test
    void isSalaryBreakupSaved_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 5L);
        requestData.put("month", 3);
        requestData.put("year", 2024);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "saved");
        when(runPayService.isSalaryBreakupSavedOfThisMonth(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/runpay/isSalaryBreakupSaved")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("saved"));
    }

    // ── approveAndSubmit ───────────────────────────────────────────────────────

    @Test
    void approveAndSubmit_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 10L);
        requestData.put("month", 5);
        requestData.put("year", 2024);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "approved");
        when(runPayService.saveAll(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/runpay/approveAndSubmit")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("approved"));
    }

    @Test
    void approveAndSubmit_invalidData_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/runpay/approveAndSubmit")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("bad-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    // ── updateRunPayRoll ───────────────────────────────────────────────────────

    @Test
    void update_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("run_pay_id", 42L);
        requestData.put("status", "updated");

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "success");
        when(runPayService.updateRunPayRoll(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/runpay/update")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    // ── getPreviousPayRunData ──────────────────────────────────────────────────

    @Test
    void prevdata_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 1L);
        requestData.put("month", 2);
        requestData.put("year", 2024);

        Map<String, Object> serviceResponse = new HashMap<>();
        serviceResponse.put("status", "success");
        serviceResponse.put("data", Collections.emptyList());
        when(runPayService.getPreviousPayRunData(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/runpay/prevdata")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    // ── deleteRunPayroll ───────────────────────────────────────────────────────

    @Test
    void deleteRunPayroll_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("run_pay_id", 7L);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "deleted");
        when(runPayService.deleteRunPayroll(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/runpay/deleteRunPayroll")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("deleted"));
    }

    // ── getSavedRunPayroll ─────────────────────────────────────────────────────

    @Test
    void getSavedRunPayroll_invalidData_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/runpay/getSavedRunPayroll")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("garbage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }
}

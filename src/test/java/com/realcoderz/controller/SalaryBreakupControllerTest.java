package com.realcoderz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realcoderz.service.SalaryBreakupService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = SalaryBreakupController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class SalaryBreakupControllerTest {

    static final String TEST_KEY = "TestKey123456789";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SalaryBreakupService salaryBreakupService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setEncryptionKey() {
        ReflectionTestUtils.setField(EncryptDecryptUtils.class, "encryptionKey", TEST_KEY);
    }

    private String encrypt(Object payload) throws Exception {
        return EncryptDecryptUtils.encrypt(objectMapper.writeValueAsString(payload));
    }

    // ── addsalarybreakup ───────────────────────────────────────────────────────

    @Test
    void addSalary_validInput_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 1L);
        requestData.put("employee_id", 101L);
        requestData.put("gross", 80000.0);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "success");
        when(salaryBreakupService.save(any(Map.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/salarybreakup/addsalary")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void addSalary_invalidData_returnsExceptionStatus() throws Exception {
        mockMvc.perform(post("/salarybreakup/addsalary")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("corrupt-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("exception"));
    }

    // ── getCalculatedData ──────────────────────────────────────────────────────

    @Test
    void getCalculatedData_fullTimeEmployee_callsFullTimeService() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("employee_Type", "Full time");
        requestData.put("basic", 50000.0);
        requestData.put("gross", 80000.0);
        requestData.put("org_id", 1L);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "calculated");
        when(salaryBreakupService.calculateSalaryDataNew(anyString(), any())).thenReturn(serviceResponse);

        mockMvc.perform(post("/salarybreakup/getCalculatedData")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("calculated"));
    }

    @Test
    void getCalculatedData_consultantEmployee_callsConsultantService() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("employee_Type", "Consultant");
        requestData.put("basic", 70000.0);
        requestData.put("org_id", 2L);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "consultant-calculated");
        when(salaryBreakupService.SalaryBreakUporConsultant(anyString(), any())).thenReturn(serviceResponse);

        mockMvc.perform(post("/salarybreakup/getCalculatedData")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("consultant-calculated"));
    }

    @Test
    void getCalculatedData_internEmployee_callsInternService() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("employee_Type", "Intern");
        requestData.put("basic", 15000.0);
        requestData.put("org_id", 3L);

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "intern-calculated");
        when(salaryBreakupService.SalaryBreakUporIntern(anyString(), any())).thenReturn(serviceResponse);

        mockMvc.perform(post("/salarybreakup/getCalculatedData")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("intern-calculated"));
    }

    @Test
    void getCalculatedData_invalidEmployeeType_returnsNotValidStatus() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("employee_Type", "Unknown");
        requestData.put("org_id", 1L);

        mockMvc.perform(post("/salarybreakup/getCalculatedData")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Not Valid Employee Type"));
    }

    // ── financialYearDropdown ──────────────────────────────────────────────────

    @Test
    void dropdownForYear_withJoiningDate_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("joining_date", "2022-04-01");

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "success");
        when(salaryBreakupService.financialYearDropdown("2022-04-01")).thenReturn(serviceResponse);

        mockMvc.perform(post("/salarybreakup/dropdownForYear")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void dropdownForYear_withoutJoiningDate_returnsError() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("org_id", 1L);

        mockMvc.perform(post("/salarybreakup/dropdownForYear")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"));
    }

    // ── financialMonthDropdown ─────────────────────────────────────────────────

    @Test
    void dropdownForMonth_withBothParams_returnsServiceResponse() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("joining_date", "2022-04-01");
        requestData.put("year", "2023");

        Map<String, Object> serviceResponse = Collections.singletonMap("status", "success");
        when(salaryBreakupService.financialMonthDropdown("2022-04-01", "2023")).thenReturn(serviceResponse);

        mockMvc.perform(post("/salarybreakup/dropdownForMonth")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void dropdownForMonth_missingYear_returnsError() throws Exception {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("joining_date", "2022-04-01");

        mockMvc.perform(post("/salarybreakup/dropdownForMonth")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(encrypt(requestData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"));
    }
}

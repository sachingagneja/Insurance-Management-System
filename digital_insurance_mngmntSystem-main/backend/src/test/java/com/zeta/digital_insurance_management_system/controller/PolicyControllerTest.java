package com.zeta.digital_insurance_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.digital_insurance_management_system.enums.Category;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.service.policy.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PolicyController.class)
@ContextConfiguration(classes = {PolicyController.class})
@Import(PolicyControllerTest.TestSecurityConfig.class)
public class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PolicyService policyService;

    private Policy testPolicy1;
    private Policy testPolicy2;

    @Configuration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        testPolicy1 = new Policy();
        testPolicy1.setId(1L);
        testPolicy1.setName("Life Insurance Premium");
        testPolicy1.setDescription("Comprehensive life insurance coverage");
        testPolicy1.setPremiumAmount(new BigDecimal("500.00"));
        testPolicy1.setCoverageAmount(new BigDecimal("100000.00"));
        testPolicy1.setDurationMonths(12);
        testPolicy1.setRenewalPremiumRate(new BigDecimal("0.05"));
        testPolicy1.setCreatedAt(LocalDateTime.now());
        testPolicy1.setCategory(Category.LIFE);

        testPolicy2 = new Policy();
        testPolicy2.setId(2L);
        testPolicy2.setName("Health Insurance Basic");
        testPolicy2.setDescription("Basic health insurance coverage");
        testPolicy2.setPremiumAmount(new BigDecimal("300.00"));
        testPolicy2.setCoverageAmount(new BigDecimal("50000.00"));
        testPolicy2.setDurationMonths(12);
        testPolicy2.setRenewalPremiumRate(new BigDecimal("0.03"));
        testPolicy2.setCreatedAt(LocalDateTime.now());
        testPolicy2.setCategory(Category.HEALTH);
    }

    @Test
    void getAllPolicies_shouldReturnListOfPolicies() throws Exception {
        List<Policy> policies = Arrays.asList(testPolicy1, testPolicy2);
        when(policyService.getAllPolicies()).thenReturn(policies);

        mockMvc.perform(get("/policies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(testPolicy1.getId()))
                .andExpect(jsonPath("$[0].name").value(testPolicy1.getName()))
                .andExpect(jsonPath("$[0].category").value(testPolicy1.getCategory().name()))
                .andExpect(jsonPath("$[1].id").value(testPolicy2.getId()))
                .andExpect(jsonPath("$[1].name").value(testPolicy2.getName()))
                .andExpect(jsonPath("$[1].category").value(testPolicy2.getCategory().name()));

        verify(policyService, times(1)).getAllPolicies();
    }

    @Test
    void getAllPolicies_shouldReturnEmptyList_whenNoPolicies() throws Exception {
        when(policyService.getAllPolicies()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/policies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));

        verify(policyService, times(1)).getAllPolicies();
    }

    @Test
    void getPolicyById_shouldReturnPolicy_whenPolicyExists() throws Exception {
        when(policyService.getPolicyById(1L)).thenReturn(testPolicy1);

        mockMvc.perform(get("/policies/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testPolicy1.getId()))
                .andExpect(jsonPath("$.name").value(testPolicy1.getName()))
                .andExpect(jsonPath("$.description").value(testPolicy1.getDescription()))
                .andExpect(jsonPath("$.premiumAmount").value(500.0))
                .andExpect(jsonPath("$.coverageAmount").value(100000.0))
                .andExpect(jsonPath("$.durationMonths").value(testPolicy1.getDurationMonths()))
                .andExpect(jsonPath("$.category").value(testPolicy1.getCategory().name()));

        verify(policyService, times(1)).getPolicyById(1L);
    }

    @Test
    void getPolicyById_shouldReturnNull_whenPolicyNotExists() throws Exception {
        when(policyService.getPolicyById(999L)).thenReturn(null);

        mockMvc.perform(get("/policies/999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(policyService, times(1)).getPolicyById(999L);
    }

    @Test
    void createPolicy_shouldReturnCreatedPolicy() throws Exception {
        Policy inputPolicy = new Policy();
        inputPolicy.setName("Vehicle Insurance");
        inputPolicy.setDescription("Comprehensive vehicle coverage");
        inputPolicy.setPremiumAmount(new BigDecimal("400.00"));
        inputPolicy.setCoverageAmount(new BigDecimal("75000.00"));
        inputPolicy.setDurationMonths(12);
        inputPolicy.setRenewalPremiumRate(new BigDecimal("0.04"));
        inputPolicy.setCategory(Category.VEHICLE);

        Policy createdPolicy = new Policy();
        createdPolicy.setId(3L);
        createdPolicy.setName(inputPolicy.getName());
        createdPolicy.setDescription(inputPolicy.getDescription());
        createdPolicy.setPremiumAmount(inputPolicy.getPremiumAmount());
        createdPolicy.setCoverageAmount(inputPolicy.getCoverageAmount());
        createdPolicy.setDurationMonths(inputPolicy.getDurationMonths());
        createdPolicy.setRenewalPremiumRate(inputPolicy.getRenewalPremiumRate());
        createdPolicy.setCreatedAt(LocalDateTime.now());
        createdPolicy.setCategory(inputPolicy.getCategory());

        when(policyService.createPolicy(any(Policy.class))).thenReturn(createdPolicy);

        mockMvc.perform(post("/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPolicy)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdPolicy.getId()))
                .andExpect(jsonPath("$.name").value(createdPolicy.getName()))
                .andExpect(jsonPath("$.category").value(createdPolicy.getCategory().name()));

        verify(policyService, times(1)).createPolicy(any(Policy.class));
    }

    @Test
    void createPolicy_shouldHandleAllCategories() throws Exception {
        Policy lifePolicy = new Policy();
        lifePolicy.setName("Life Policy");
        lifePolicy.setCategory(Category.LIFE);
        lifePolicy.setPremiumAmount(new BigDecimal("500.00"));

        Policy createdLifePolicy = new Policy();
        createdLifePolicy.setId(1L);
        createdLifePolicy.setName("Life Policy");
        createdLifePolicy.setCategory(Category.LIFE);

        when(policyService.createPolicy(any(Policy.class))).thenReturn(createdLifePolicy);

        mockMvc.perform(post("/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lifePolicy)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("LIFE"));

        verify(policyService, times(1)).createPolicy(any(Policy.class));
    }

    @Test
    void createPolicy_shouldHandleNullValues() throws Exception {
        Policy inputPolicy = new Policy();
        inputPolicy.setName("Minimal Policy");

        Policy createdPolicy = new Policy();
        createdPolicy.setId(1L);
        createdPolicy.setName("Minimal Policy");

        when(policyService.createPolicy(any(Policy.class))).thenReturn(createdPolicy);

        mockMvc.perform(post("/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPolicy)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Minimal Policy"));

        verify(policyService, times(1)).createPolicy(any(Policy.class));
    }

    @Test
    void updatePolicy_shouldReturnUpdatedPolicy() throws Exception {
        Policy updatedPolicyData = new Policy();
        updatedPolicyData.setName("Updated Life Insurance");
        updatedPolicyData.setDescription("Updated comprehensive life insurance");
        updatedPolicyData.setPremiumAmount(new BigDecimal("600.00"));
        updatedPolicyData.setCoverageAmount(new BigDecimal("120000.00"));
        updatedPolicyData.setDurationMonths(24);
        updatedPolicyData.setRenewalPremiumRate(new BigDecimal("0.06"));
        updatedPolicyData.setCategory(Category.LIFE);

        Policy updatedPolicy = new Policy();
        updatedPolicy.setId(1L);
        updatedPolicy.setName(updatedPolicyData.getName());
        updatedPolicy.setDescription(updatedPolicyData.getDescription());
        updatedPolicy.setPremiumAmount(updatedPolicyData.getPremiumAmount());
        updatedPolicy.setCoverageAmount(updatedPolicyData.getCoverageAmount());
        updatedPolicy.setDurationMonths(updatedPolicyData.getDurationMonths());
        updatedPolicy.setRenewalPremiumRate(updatedPolicyData.getRenewalPremiumRate());
        updatedPolicy.setCreatedAt(LocalDateTime.now());
        updatedPolicy.setCategory(updatedPolicyData.getCategory());

        when(policyService.updatePolicy(eq(1L), any(Policy.class))).thenReturn(updatedPolicy);

        mockMvc.perform(put("/policies/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPolicyData)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Life Insurance"))
                .andExpect(jsonPath("$.premiumAmount").value(600.0));

        verify(policyService, times(1)).updatePolicy(eq(1L), any(Policy.class));
    }

    @Test
    void deletePolicy_shouldReturnSuccessMessage() throws Exception {
        doNothing().when(policyService).deletePolicy(1L);

        mockMvc.perform(delete("/policies/delete/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Policy deleted successfully"));

        verify(policyService, times(1)).deletePolicy(1L);
    }

    @Test
    void createPolicy_shouldHandleBigDecimalValues() throws Exception {
        Policy inputPolicy = new Policy();
        inputPolicy.setName("Precision Policy");
        inputPolicy.setPremiumAmount(new BigDecimal("123.456"));
        inputPolicy.setCoverageAmount(new BigDecimal("999999.99"));
        inputPolicy.setCategory(Category.HEALTH);

        Policy createdPolicy = new Policy();
        createdPolicy.setId(1L);
        createdPolicy.setName("Precision Policy");
        createdPolicy.setPremiumAmount(new BigDecimal("123.456"));
        createdPolicy.setCoverageAmount(new BigDecimal("999999.99"));
        createdPolicy.setCategory(Category.HEALTH);

        when(policyService.createPolicy(any(Policy.class))).thenReturn(createdPolicy);

        mockMvc.perform(post("/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputPolicy)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.premiumAmount").value(123.456))
                .andExpect(jsonPath("$.coverageAmount").value(999999.99));

        verify(policyService, times(1)).createPolicy(any(Policy.class));
    }
}
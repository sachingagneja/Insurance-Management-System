package com.zeta.digital_insurance_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.security.jwt.JwtService;
import com.zeta.digital_insurance_management_system.service.PolicyPurchaseService.UserPolicyPurchaseImpl;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserPolicyPurchaseController.class)
public class UserPolicyPurchaseControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserPolicyPurchaseControllerTest.class);

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserPolicyPurchaseImpl userPolicyPurchase;
    @MockBean private UserServiceImpl userServiceImpl;
    @MockBean private JwtService jwtService;

    private UserPolicy samplePolicy;

    @BeforeEach
    void setUp() {
        logger.info("Setting up sample UserPolicy for tests");

        User user = new User();
        user.setId(1L);

        Policy policy = new Policy();
        policy.setId(1L);
        policy.setDurationMonths(12);
        policy.setPremiumAmount(BigDecimal.valueOf(500.00));

        samplePolicy = new UserPolicy();
        samplePolicy.setId(1L);
        samplePolicy.setUser(user);
        samplePolicy.setPolicy(policy);
        samplePolicy.setStartDate(LocalDate.now());
        samplePolicy.setEndDate(LocalDate.now().plusMonths(12));
        samplePolicy.setStatus(PolicyStatus.ACTIVE);
        samplePolicy.setPremiumPaid(BigDecimal.valueOf(500.00));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void purchasePolicy_shouldReturnCreatedPolicy() throws Exception {
        logger.info("Testing purchasePolicy_shouldReturnCreatedPolicy");

        when(userServiceImpl.getCurrentUserId()).thenReturn(1L);
        when(userPolicyPurchase.purchaseAPolicy(eq(1L), eq(1L))).thenReturn(samplePolicy);

        mockMvc.perform(post("/user/policy/1/purchase")
                        .with(csrf())
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(samplePolicy.getId()))
                .andExpect(jsonPath("$.status").value(samplePolicy.getStatus().toString()));

        logger.info("purchasePolicy_shouldReturnCreatedPolicy test passed");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getUserPolicies_shouldReturnListOfPolicies() throws Exception {
        logger.info("Testing getUserPolicies_shouldReturnListOfPolicies");

        when(userServiceImpl.getCurrentUserId()).thenReturn(1L);
        when(userPolicyPurchase.getPurchasedPolicies(1L)).thenReturn(List.of(samplePolicy));

        mockMvc.perform(get("/user/policy")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(samplePolicy.getId()));

        logger.info("getUserPolicies_shouldReturnListOfPolicies test passed");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void updatePolicy_shouldReturnUpdatedPolicy() throws Exception {
        logger.info("Testing updatePolicy_shouldReturnUpdatedPolicy");

        samplePolicy.setStatus(PolicyStatus.CANCELLED);

        when(userServiceImpl.getCurrentUserId()).thenReturn(1L);
        when(userPolicyPurchase.updatePolicy(eq(1L), eq(1L), eq(PolicyStatus.CANCELLED))).thenReturn(samplePolicy);

        mockMvc.perform(put("/user/policy")
                        .with(csrf())
                        .param("policyId", "1")
                        .param("status", PolicyStatus.CANCELLED.toString())
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(samplePolicy.getId()))
                .andExpect(jsonPath("$.status").value(PolicyStatus.CANCELLED.toString()));

        logger.info("updatePolicy_shouldReturnUpdatedPolicy test passed");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getUserPolicies_shouldReturnEmptyListWhenNoPolicies() throws Exception {
        logger.info("Testing getUserPolicies_shouldReturnEmptyListWhenNoPolicies");

        when(userServiceImpl.getCurrentUserId()).thenReturn(1L);
        when(userPolicyPurchase.getPurchasedPolicies(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/policy")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        logger.info("getUserPolicies_shouldReturnEmptyListWhenNoPolicies test passed");
    }
}

package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.dto.renew.RenewablePolicy;
import com.zeta.digital_insurance_management_system.enums.Category;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.UserPolicy;

import com.zeta.digital_insurance_management_system.security.jwt.JwtService;
import com.zeta.digital_insurance_management_system.service.PolicyRenewal.IPolicyRenewalService;

import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PolicyRenewalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyRenewalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private IPolicyRenewalService policyRenewalService;

    private final User user = new User(
            1L,
            "John Doe",
            "john@example.com",
            "hashedasskjhghjkjhword",
            "1234567890",
            "123 Main St",
            Role.USER
    );

    private final Policy policy = new Policy(
            1L,
            "Health Plan",
            "Comprehensive health insurance",
            new BigDecimal("1200"),
            new BigDecimal("10000"),
            12,
            new BigDecimal("0.1"),
            LocalDateTime.now(),
            Category.HEALTH
    );

    private final UserPolicy userPolicy = new UserPolicy(
            1L,
            user,
            policy,
            LocalDate.now().minusYears(1),
            LocalDate.now(),
            PolicyStatus.EXPIRED,
            new BigDecimal("1200")
    );

    @Test
    public void testGetRenewablePolicies() throws Exception {

        RenewablePolicy dto = new RenewablePolicy(
                userPolicy.getId(),
                userPolicy.getPolicy().getName(),
                userPolicy.getEndDate(),
                userPolicy.getPremiumPaid(),
                userPolicy.getPolicy().getRenewalPremiumRate()
        );

        when(userService.getCurrentUserId()).thenReturn(1L);
        when(policyRenewalService.getRenewablePolicies(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/user/policies/renewable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userPolicyId").value(userPolicy.getId()))
                .andExpect(jsonPath("$[0].policyName").value(userPolicy.getPolicy().getName()));
    }

    @Test
    public void testRenewPolicy() throws Exception {
        UserPolicy renewedPolicy = new UserPolicy(
                userPolicy.getId(),
                userPolicy.getUser(),
                userPolicy.getPolicy(),
                LocalDate.now(),
                LocalDate.now().plusMonths(policy.getDurationMonths()),
                PolicyStatus.RENEWED,
                userPolicy.getPremiumPaid().multiply(new BigDecimal("1.1"))
        );
        when(policyRenewalService.renewPolicy(1L)).thenReturn(renewedPolicy);

        mockMvc.perform(post("/policy/1/renew"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(renewedPolicy.getId()))
                .andExpect(jsonPath("$.status").value(renewedPolicy.getStatus().name()));
    }
}

package com.zeta.digital_insurance_management_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeta.digital_insurance_management_system.dto.Claim.AdminClaimStatusUpdateDTO;
import com.zeta.digital_insurance_management_system.dto.Claim.UserClaimDTO;
import com.zeta.digital_insurance_management_system.enums.ClaimStatus;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.security.jwt.JwtService;
import com.zeta.digital_insurance_management_system.service.ClaimManagement.ClaimManagementService;
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
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ClaimManagementController.class)
public class ClaimManagementControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(ClaimManagementControllerTest.class);

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ClaimManagementService claimManagementService;
    @MockBean private UserServiceImpl userServiceImpl;
    @MockBean private JwtService jwtService;

    private Claim claim1;
    private Claim claim2;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for ClaimManagementControllerTest");

        UserDetails mockUserDetails = Mockito.mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("mockUsername");

        when(jwtService.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(jwtService.extractUserName(anyString())).thenReturn("mockUsername");

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setId(1L);

        claim1 = new Claim(1L, userPolicy, LocalDate.now(), new BigDecimal("1000.00"), "Medical", ClaimStatus.PENDING, null, null);
        claim2 = new Claim(2L, userPolicy, LocalDate.now(), new BigDecimal("2000.00"), "Accident", ClaimStatus.PENDING, null, null);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void submitClaim_shouldReturnCreatedClaim() throws Exception {
        logger.info("Running test: submitClaim_shouldReturnCreatedClaim");

        UserClaimDTO dto = new UserClaimDTO(1L, new BigDecimal("1000.00"), "Medical");
        logger.debug("Submitting claim with payload: {}", objectMapper.writeValueAsString(dto));

        when(claimManagementService.submitClaim(any(UserClaimDTO.class))).thenReturn(claim1);

        mockMvc.perform(post("/claim")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(claim1.getId()))
                .andExpect(jsonPath("$.reason").value(claim1.getReason()));

        verify(claimManagementService, times(1)).submitClaim(any(UserClaimDTO.class));

        logger.info("Test passed: submitClaim_shouldReturnCreatedClaim");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getAllClaims_shouldReturnListOfClaims() throws Exception {
        logger.info("Running test: getAllClaims_shouldReturnListOfClaims");

        when(claimManagementService.getAllClaims()).thenReturn(Arrays.asList(claim1, claim2));

        mockMvc.perform(get("/user/claim"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(claim1.getId()))
                .andExpect(jsonPath("$[1].id").value(claim2.getId()));

        logger.info("Test passed: getAllClaims_shouldReturnListOfClaims");
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void getClaimsByUser_shouldReturnUserClaims() throws Exception {
        logger.info("Running test: getClaimsByUser_shouldReturnUserClaims");

        when(userServiceImpl.getCurrentUserId()).thenReturn(1L);
        when(claimManagementService.getClaimsByUser(1L)).thenReturn(Arrays.asList(claim1));

        mockMvc.perform(get("/user/claimById")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(claim1.getId()));

        logger.info("Test passed: getClaimsByUser_shouldReturnUserClaims");
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ADMIN")
    void updateClaimStatus_shouldReturnUpdatedClaim() throws Exception {
        logger.info("Running test: updateClaimStatus_shouldReturnUpdatedClaim");

        AdminClaimStatusUpdateDTO updateDTO = new AdminClaimStatusUpdateDTO(ClaimStatus.APPROVED.name(), "Verified and approved");
        Claim updatedClaim = new Claim(1L, claim1.getUserPolicy(), claim1.getClaimDate(), claim1.getClaimAmount(), claim1.getReason(), ClaimStatus.APPROVED, "Verified and approved", LocalDate.now());

        logger.debug("Updating claim with status: {}, comment: {}", updateDTO.getStatus(), updateDTO.getReviewerComment());

        when(claimManagementService.updateClaimStatus(eq(1L), eq(ClaimStatus.APPROVED.name()), eq("Verified and approved"))).thenReturn(updatedClaim);

        mockMvc.perform(put("/claim/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedClaim.getId()))
                .andExpect(jsonPath("$.status").value(ClaimStatus.APPROVED.name()))
                .andExpect(jsonPath("$.reviewerComment").value("Verified and approved"));

        logger.info("Test passed: updateClaimStatus_shouldReturnUpdatedClaim");
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ADMIN")
    void deleteClaim_shouldDeleteSuccessfully() throws Exception {
        logger.info("Running test: deleteClaim_shouldDeleteSuccessfully");

        doNothing().when(claimManagementService).deleteClaim(1L);

        mockMvc.perform(delete("/claim/1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(claimManagementService, times(1)).deleteClaim(1L);

        logger.info("Test passed: deleteClaim_shouldDeleteSuccessfully");
    }

    @Test
    @WithMockUser(username = "testuser", roles = "ADMIN")
    void getClaimsByUser_shouldReturnEmptyListWhenNoneExist() throws Exception {
        logger.info("Running test: getClaimsByUser_shouldReturnEmptyListWhenNoneExist");

        when(userServiceImpl.getCurrentUserId()).thenReturn(1L);
        when(claimManagementService.getClaimsByUser(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/claimById")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        logger.info("Test passed: getClaimsByUser_shouldReturnEmptyListWhenNoneExist");
    }
}

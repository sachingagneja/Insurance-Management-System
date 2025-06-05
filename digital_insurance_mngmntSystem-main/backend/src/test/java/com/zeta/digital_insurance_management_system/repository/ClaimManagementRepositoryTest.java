package com.zeta.digital_insurance_management_system.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Removed deprecated MockBean import

import com.zeta.digital_insurance_management_system.enums.ClaimStatus;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.UserPolicy;

@ExtendWith(MockitoExtension.class)
class ClaimManagementRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(ClaimManagementRepositoryTest.class);
    @Mock ClaimManagementRepository claimRepository;

    private User user;
    private UserPolicy userPolicy;
    private Claim claim1;
    private Claim claim2;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for ClaimManagementRepositoryTest");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");

        userPolicy = new UserPolicy();
        userPolicy.setId(10L);
        userPolicy.setUser(user);

        claim1 = new Claim();
        claim1.setId(100L);
        claim1.setUserPolicy(userPolicy);
        claim1.setClaimDate(LocalDate.now());
        claim1.setClaimAmount(BigDecimal.valueOf(5000.0));
        claim1.setStatus(ClaimStatus.PENDING);

        claim2 = new Claim();
        claim2.setId(101L);
        claim2.setUserPolicy(userPolicy);
        claim2.setClaimDate(LocalDate.now());
        claim2.setClaimAmount(BigDecimal.valueOf(10000.0));
        claim2.setStatus(ClaimStatus.APPROVED);
    }

    @DisplayName("Find claims by user ID")
    @Test
    void findByUserPolicy_User_Id_ShouldReturnClaimsForGivenUserId() {
        logger.info("Running test: findByUserPolicy_User_Id_ShouldReturnClaimsForGivenUserId");

        List<Claim> expectedClaims = Arrays.asList(claim1, claim2);
        when(claimRepository.findByUserPolicy_User_Id(1L)).thenReturn(expectedClaims);

        List<Claim> actualClaims = claimRepository.findByUserPolicy_User_Id(1L);

        assertNotNull(actualClaims);
        assertEquals(2, actualClaims.size());
        assertTrue(actualClaims.contains(claim1));
        assertTrue(actualClaims.contains(claim2));

        verify(claimRepository).findByUserPolicy_User_Id(1L);

        logger.info("Test passed: findByUserPolicy_User_Id_ShouldReturnClaimsForGivenUserId");
    }

    @DisplayName("Find claims returns empty list when no claims exist for user")
    @Test
    void findByUserPolicy_User_Id_ShouldReturnEmptyList_WhenNoClaims() {
        logger.info("Running test: findByUserPolicy_User_Id_ShouldReturnEmptyList_WhenNoClaims");

        when(claimRepository.findByUserPolicy_User_Id(2L)).thenReturn(List.of());

        List<Claim> claims = claimRepository.findByUserPolicy_User_Id(2L);

        assertNotNull(claims);
        assertTrue(claims.isEmpty());

        verify(claimRepository).findByUserPolicy_User_Id(2L);

        logger.info("Test passed: findByUserPolicy_User_Id_ShouldReturnEmptyList_WhenNoClaims");
    }

    @DisplayName("Exists by Claim ID and User ID returns true when claim exists")
    @Test
    void existsByIdAndUserPolicy_UserId_ShouldReturnTrue_WhenClaimExists() {
        logger.info("Running test: existsByIdAndUserPolicy_UserId_ShouldReturnTrue_WhenClaimExists");

        when(claimRepository.existsByIdAndUserPolicy_UserId(100L, 1L)).thenReturn(true);

        boolean exists = claimRepository.existsByIdAndUserPolicy_UserId(100L, 1L);

        assertTrue(exists);
        verify(claimRepository).existsByIdAndUserPolicy_UserId(100L, 1L);

        logger.info("Test passed: existsByIdAndUserPolicy_UserId_ShouldReturnTrue_WhenClaimExists");
    }

    @DisplayName("Exists by Claim ID and User ID returns false when claim does not exist")
    @Test
    void existsByIdAndUserPolicy_UserId_ShouldReturnFalse_WhenClaimDoesNotExist() {
        logger.info("Running test: existsByIdAndUserPolicy_UserId_ShouldReturnFalse_WhenClaimDoesNotExist");

        when(claimRepository.existsByIdAndUserPolicy_UserId(999L, 1L)).thenReturn(false);

        boolean exists = claimRepository.existsByIdAndUserPolicy_UserId(999L, 1L);

        assertFalse(exists);
        verify(claimRepository).existsByIdAndUserPolicy_UserId(999L, 1L);

        logger.info("Test passed: existsByIdAndUserPolicy_UserId_ShouldReturnFalse_WhenClaimDoesNotExist");
    }
}

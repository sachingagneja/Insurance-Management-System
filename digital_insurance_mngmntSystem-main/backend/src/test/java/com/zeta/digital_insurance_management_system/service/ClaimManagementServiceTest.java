package com.zeta.digital_insurance_management_system.service;

import com.zeta.digital_insurance_management_system.dto.Claim.UserClaimDTO;
import com.zeta.digital_insurance_management_system.enums.ClaimStatus;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.repository.ClaimManagementRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import com.zeta.digital_insurance_management_system.service.ClaimManagement.ClaimManagementServiceImplementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClaimManagementServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ClaimManagementServiceTest.class);

    @Mock
    private ClaimManagementRepository claimRepo;

    @Mock
    private UserPolicyRepository userPolicyRepo;

    @InjectMocks
    private ClaimManagementServiceImplementation claimService;

    @BeforeEach
    void setUp() {
        logger.info("Setting up mocks for ClaimManagementServiceTest");
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void submitClaim_shouldCreateClaimSuccessfully() throws ResourceNotFoundException {
        logger.info("Running test: submitClaim_shouldCreateClaimSuccessfully");

        UserClaimDTO dto = new UserClaimDTO();
        dto.setUserPolicyId(1L);
        dto.setClaimAmount(BigDecimal.valueOf(1000.0));
        dto.setReason("Medical expenses");

        UserPolicy policy = new UserPolicy();
        policy.setStatus(PolicyStatus.ACTIVE);
        when(userPolicyRepo.findById(1L)).thenReturn(Optional.of(policy));

        Claim claim = new Claim();
        when(claimRepo.save(any())).thenReturn(claim);

        Claim result = claimService.submitClaim(dto);

        assertNotNull(result);
        verify(claimRepo, times(1)).save(any(Claim.class));

        logger.info("Test passed: submitClaim_shouldCreateClaimSuccessfully");
    }

    @Test
    void submitClaim_shouldThrowException_whenPolicyNotFound() {
        logger.info("Running test: submitClaim_shouldThrowException_whenPolicyNotFound");

        UserClaimDTO dto = new UserClaimDTO();
        dto.setUserPolicyId(1L);

        when(userPolicyRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> claimService.submitClaim(dto));

        logger.info("Test passed: submitClaim_shouldThrowException_whenPolicyNotFound");
    }

    @Test
    void submitClaim_shouldThrowException_whenPolicyNotActive() {
        logger.info("Running test: submitClaim_shouldThrowException_whenPolicyNotActive");

        UserClaimDTO dto = new UserClaimDTO();
        dto.setUserPolicyId(1L);

        UserPolicy policy = new UserPolicy();
        policy.setStatus(PolicyStatus.EXPIRED);

        when(userPolicyRepo.findById(1L)).thenReturn(Optional.of(policy));

        assertThrows(IllegalArgumentException.class, () -> claimService.submitClaim(dto));

        logger.info("Test passed: submitClaim_shouldThrowException_whenPolicyNotActive");
    }

    @Test
    void getClaimsByUser_shouldReturnClaims() throws ResourceNotFoundException {
        logger.info("Running test: getClaimsByUser_shouldReturnClaims");

        Claim claim = new Claim();
        claim.setId(1L);

        when(claimRepo.findByUserPolicy_User_Id(1L))
                .thenReturn(Collections.singletonList(claim));

        List<Claim> result = claimService.getClaimsByUser(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());

        logger.info("Test passed: getClaimsByUser_shouldReturnClaims");
    }

    @Test
    void getClaimsByUser_shouldThrowException_whenNoClaims() {
        logger.info("Running test: getClaimsByUser_shouldThrowException_whenNoClaims");

        when(claimRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> claimService.getClaimsByUser(1L));

        logger.info("Test passed: getClaimsByUser_shouldThrowException_whenNoClaims");
    }

    @Test
    void getAllClaims_shouldReturnAllClaims() {
        logger.info("Running test: getAllClaims_shouldReturnAllClaims");

        when(claimRepo.findAll()).thenReturn(List.of(new Claim(), new Claim()));

        List<Claim> result = claimService.getAllClaims();

        assertEquals(2, result.size());

        logger.info("Test passed: getAllClaims_shouldReturnAllClaims");
    }

    @Test
    void updateClaimStatus_shouldUpdateStatusSuccessfully() {
        logger.info("Running test: updateClaimStatus_shouldUpdateStatusSuccessfully");

        Claim claim = new Claim();
        when(claimRepo.findById(1L)).thenReturn(Optional.of(claim));
        when(claimRepo.save(any())).thenReturn(claim);

        Claim result = claimService.updateClaimStatus(1L, "APPROVED", "All good");

        assertEquals(ClaimStatus.APPROVED, result.getStatus());
        assertEquals("All good", result.getReviewerComment());
        assertNotNull(result.getResolvedDate());

        logger.info("Test passed: updateClaimStatus_shouldUpdateStatusSuccessfully");
    }

    @Test
    void updateClaimStatus_shouldThrowException_forInvalidStatus() {
        logger.info("Running test: updateClaimStatus_shouldThrowException_forInvalidStatus");

        Claim claim = new Claim();
        when(claimRepo.findById(1L)).thenReturn(Optional.of(claim));

        assertThrows(IllegalArgumentException.class, () -> claimService.updateClaimStatus(1L, "IN_PROGRESS", "Invalid"));

        logger.info("Test passed: updateClaimStatus_shouldThrowException_forInvalidStatus");
    }

    @Test
    void deleteClaim_shouldDeleteSuccessfully() {
        logger.info("Running test: deleteClaim_shouldDeleteSuccessfully");

        when(claimRepo.existsById(1L)).thenReturn(true);

        claimService.deleteClaim(1L);

        verify(claimRepo, times(1)).deleteById(1L);

        logger.info("Test passed: deleteClaim_shouldDeleteSuccessfully");
    }

    @Test
    void deleteClaim_shouldThrowException_whenClaimNotFound() {
        logger.info("Running test: deleteClaim_shouldThrowException_whenClaimNotFound");

        when(claimRepo.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> claimService.deleteClaim(1L));

        logger.info("Test passed: deleteClaim_shouldThrowException_whenClaimNotFound");
    }
}

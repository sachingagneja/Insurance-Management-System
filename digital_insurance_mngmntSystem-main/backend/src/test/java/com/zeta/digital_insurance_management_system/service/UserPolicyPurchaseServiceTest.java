package com.zeta.digital_insurance_management_system.service;

import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.repository.PolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserRepository;
import com.zeta.digital_insurance_management_system.service.PolicyPurchaseService.UserPolicyPurchaseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserPolicyPurchaseServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserPolicyPurchaseServiceTest.class);

    @Mock private PolicyRepository policyRepository;
    @Mock private UserPolicyRepository userPolicyRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private UserPolicyPurchaseImpl userPolicyPurchase;

    @BeforeEach
    void setUp() {
        logger.info("Initializing mocks before each test");
        MockitoAnnotations.openMocks(this);
    }

    // =====================
    // Test: purchaseAPolicy
    // =====================

    @Test
    void purchaseAPolicy_shouldSucceed_whenValidUserAndPolicyAndNotAlreadyPurchased() {
        logger.info("Test: purchaseAPolicy_shouldSucceed_whenValidUserAndPolicyAndNotAlreadyPurchased started");

        Long policyId = 1L;
        Long userId = 2L;

        Policy policy = new Policy();
        policy.setId(policyId);
        policy.setDurationMonths(12);
        policy.setPremiumAmount(BigDecimal.valueOf(5000));

        User user = new User();
        user.setId(userId);

        when(userPolicyRepository.findByUserIdAndPolicyId(userId, policyId)).thenReturn(Optional.empty());
        when(policyRepository.findById(policyId)).thenReturn(Optional.of(policy));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userPolicyRepository.save(any(UserPolicy.class))).thenAnswer(i -> i.getArgument(0));

        UserPolicy result = userPolicyPurchase.purchaseAPolicy(policyId, userId);

        assertNotNull(result);
        assertEquals(policy, result.getPolicy());
        assertEquals(user, result.getUser());
        assertEquals(PolicyStatus.ACTIVE, result.getStatus());
        assertEquals(BigDecimal.valueOf(5000), result.getPremiumPaid());

        logger.info("Test passed: purchaseAPolicy returns a valid UserPolicy with status ACTIVE");
    }

    @Test
    void purchaseAPolicy_shouldFail_whenAlreadyPurchased() {
        logger.info("Test: purchaseAPolicy_shouldFail_whenAlreadyPurchased started");

        when(userPolicyRepository.findByUserIdAndPolicyId(1L, 1L)).thenReturn(Optional.of(new UserPolicy()));

        assertThrows(ResourceNotFoundException.class, () -> userPolicyPurchase.purchaseAPolicy(1L, 1L));

        logger.info("Test passed: purchaseAPolicy throws ResourceNotFoundException when already purchased");
    }

    @Test
    void purchaseAPolicy_shouldFail_whenPolicyNotFound() {
        logger.info("Test: purchaseAPolicy_shouldFail_whenPolicyNotFound started");

        when(userPolicyRepository.findByUserIdAndPolicyId(1L, 1L)).thenReturn(Optional.empty());
        when(policyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPolicyPurchase.purchaseAPolicy(1L, 1L));

        logger.info("Test passed: purchaseAPolicy throws ResourceNotFoundException when policy not found");
    }

    @Test
    void purchaseAPolicy_shouldFail_whenUserNotFound() {
        logger.info("Test: purchaseAPolicy_shouldFail_whenUserNotFound started");

        Policy policy = new Policy();
        policy.setId(1L);
        when(userPolicyRepository.findByUserIdAndPolicyId(1L, 1L)).thenReturn(Optional.empty());
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPolicyPurchase.purchaseAPolicy(1L, 1L));

        logger.info("Test passed: purchaseAPolicy throws ResourceNotFoundException when user not found");
    }

    // ========================
    // Test: getPurchasedPolicies
    // ========================

    @Test
    void getPurchasedPolicies_shouldReturnPolicies_whenUserExists() {
        logger.info("Test: getPurchasedPolicies_shouldReturnPolicies_whenUserExists started");

        User user = new User();
        user.setId(1L);

        UserPolicy policy1 = new UserPolicy();
        policy1.setId(101L);
        UserPolicy policy2 = new UserPolicy();
        policy2.setId(102L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userPolicyRepository.findByUserId(1L)).thenReturn(List.of(policy1, policy2));

        List<UserPolicy> result = userPolicyPurchase.getPurchasedPolicies(1L);

        assertEquals(2, result.size());

        logger.info("Test passed: getPurchasedPolicies returns {} policies", result.size());
    }

    @Test
    void getPurchasedPolicies_shouldFail_whenUserNotFound() {
        logger.info("Test: getPurchasedPolicies_shouldFail_whenUserNotFound started");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPolicyPurchase.getPurchasedPolicies(1L));

        logger.info("Test passed: getPurchasedPolicies throws ResourceNotFoundException when user not found");
    }

    // ========================
    // Test: updatePolicy
    // ========================

    @Test
    void updatePolicy_shouldCancelPolicy() {
        logger.info("Test: updatePolicy_shouldCancelPolicy started");

        Policy policy = new Policy();
        policy.setDurationMonths(12);

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setPolicy(policy);
        userPolicy.setStatus(PolicyStatus.ACTIVE);
        userPolicy.setEndDate(LocalDate.now().plusMonths(12));

        when(userPolicyRepository.findByUserIdAndPolicyId(1L, 1L)).thenReturn(Optional.of(userPolicy));
        when(userPolicyRepository.save(any(UserPolicy.class))).thenAnswer(i -> i.getArgument(0));

        UserPolicy result = userPolicyPurchase.updatePolicy(1L, 1L, PolicyStatus.CANCELLED);

        assertEquals(PolicyStatus.CANCELLED, result.getStatus());
        assertEquals(LocalDate.now(), result.getEndDate());

        logger.info("Test passed: updatePolicy cancels policy and sets end date to today");
    }

    @Test
    void updatePolicy_shouldRenewPolicy() {
        logger.info("Test: updatePolicy_shouldRenewPolicy started");

        Policy policy = new Policy();
        policy.setDurationMonths(6);

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setPolicy(policy);
        userPolicy.setEndDate(LocalDate.of(2025, 1, 1));

        when(userPolicyRepository.findByUserIdAndPolicyId(1L, 1L)).thenReturn(Optional.of(userPolicy));
        when(userPolicyRepository.save(any(UserPolicy.class))).thenAnswer(i -> i.getArgument(0));

        UserPolicy result = userPolicyPurchase.updatePolicy(1L, 1L, PolicyStatus.RENEWED);

        assertEquals(PolicyStatus.RENEWED, result.getStatus());
        assertEquals(LocalDate.of(2025, 1, 1).plusMonths(6), result.getEndDate());

        logger.info("Test passed: updatePolicy renews policy and extends end date correctly");
    }

    @Test
    void updatePolicy_shouldFail_whenUserPolicyNotFound() {
        logger.info("Test: updatePolicy_shouldFail_whenUserPolicyNotFound started");

        when(userPolicyRepository.findByUserIdAndPolicyId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userPolicyPurchase.updatePolicy(1L, 1L, PolicyStatus.CANCELLED));

        logger.info("Test passed: updatePolicy throws ResourceNotFoundException when user policy not found");
    }
}


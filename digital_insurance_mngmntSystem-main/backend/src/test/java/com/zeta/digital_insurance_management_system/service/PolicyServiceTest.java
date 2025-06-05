package com.zeta.digital_insurance_management_system.service;

import com.zeta.digital_insurance_management_system.enums.Category;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.repository.PolicyRepository;
import com.zeta.digital_insurance_management_system.service.policy.PolicyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy testPolicy1;
    private Policy testPolicy2;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDateTime = LocalDateTime.now();

        testPolicy1 = new Policy();
        testPolicy1.setId(1L);
        testPolicy1.setName("Life Insurance Premium");
        testPolicy1.setDescription("Comprehensive life insurance coverage");
        testPolicy1.setPremiumAmount(new BigDecimal("500.00"));
        testPolicy1.setCoverageAmount(new BigDecimal("100000.00"));
        testPolicy1.setDurationMonths(12);
        testPolicy1.setRenewalPremiumRate(new BigDecimal("0.05"));
        testPolicy1.setCreatedAt(testDateTime);
        testPolicy1.setCategory(Category.LIFE);

        testPolicy2 = new Policy();
        testPolicy2.setId(2L);
        testPolicy2.setName("Health Insurance Basic");
        testPolicy2.setDescription("Basic health insurance coverage");
        testPolicy2.setPremiumAmount(new BigDecimal("300.00"));
        testPolicy2.setCoverageAmount(new BigDecimal("50000.00"));
        testPolicy2.setDurationMonths(12);
        testPolicy2.setRenewalPremiumRate(new BigDecimal("0.03"));
        testPolicy2.setCreatedAt(testDateTime.minusDays(1));
        testPolicy2.setCategory(Category.HEALTH);
    }

    @Test
    void createPolicy_shouldSavePolicySuccessfully() {
        Policy inputPolicy = new Policy();
        inputPolicy.setName("Vehicle Insurance");
        inputPolicy.setDescription("Comprehensive vehicle coverage");
        inputPolicy.setPremiumAmount(new BigDecimal("400.00"));
        inputPolicy.setCoverageAmount(new BigDecimal("75000.00"));
        inputPolicy.setDurationMonths(12);
        inputPolicy.setRenewalPremiumRate(new BigDecimal("0.04"));
        inputPolicy.setCategory(Category.VEHICLE);

        Policy savedPolicy = new Policy();
        savedPolicy.setId(3L);
        savedPolicy.setName(inputPolicy.getName());
        savedPolicy.setDescription(inputPolicy.getDescription());
        savedPolicy.setPremiumAmount(inputPolicy.getPremiumAmount());
        savedPolicy.setCoverageAmount(inputPolicy.getCoverageAmount());
        savedPolicy.setDurationMonths(inputPolicy.getDurationMonths());
        savedPolicy.setRenewalPremiumRate(inputPolicy.getRenewalPremiumRate());
        savedPolicy.setCreatedAt(LocalDateTime.now());
        savedPolicy.setCategory(inputPolicy.getCategory());

        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        Policy result = policyService.createPolicy(inputPolicy);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("Vehicle Insurance", result.getName());
        assertEquals("Comprehensive vehicle coverage", result.getDescription());
        assertEquals(new BigDecimal("400.00"), result.getPremiumAmount());
        assertEquals(new BigDecimal("75000.00"), result.getCoverageAmount());
        assertEquals(12, result.getDurationMonths());
        assertEquals(new BigDecimal("0.04"), result.getRenewalPremiumRate());
        assertEquals(Category.VEHICLE, result.getCategory());
        assertNotNull(result.getCreatedAt());

        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void createPolicy_shouldHandleAllCategories() {
        Policy lifePolicy = new Policy();
        lifePolicy.setName("Life Policy");
        lifePolicy.setCategory(Category.LIFE);

        when(policyRepository.save(any(Policy.class))).thenReturn(lifePolicy);

        Policy result = policyService.createPolicy(lifePolicy);
        assertEquals(Category.LIFE, result.getCategory());

        Policy healthPolicy = new Policy();
        healthPolicy.setName("Health Policy");
        healthPolicy.setCategory(Category.HEALTH);

        when(policyRepository.save(any(Policy.class))).thenReturn(healthPolicy);

        result = policyService.createPolicy(healthPolicy);
        assertEquals(Category.HEALTH, result.getCategory());

        Policy vehiclePolicy = new Policy();
        vehiclePolicy.setName("Vehicle Policy");
        vehiclePolicy.setCategory(Category.VEHICLE);

        when(policyRepository.save(any(Policy.class))).thenReturn(vehiclePolicy);

        result = policyService.createPolicy(vehiclePolicy);
        assertEquals(Category.VEHICLE, result.getCategory());

        verify(policyRepository, times(3)).save(any(Policy.class));
    }

    @Test
    void createPolicy_shouldHandleNullValues() {
        Policy policyWithNulls = new Policy();
        policyWithNulls.setName("Test Policy");

        Policy savedPolicy = new Policy();
        savedPolicy.setId(1L);
        savedPolicy.setName("Test Policy");

        when(policyRepository.save(any(Policy.class))).thenReturn(savedPolicy);

        Policy result = policyService.createPolicy(policyWithNulls);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Policy", result.getName());
        assertNull(result.getDescription());
        assertNull(result.getPremiumAmount());
        assertNull(result.getCoverageAmount());

        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void createPolicy_shouldHandleBigDecimalPrecision() {
        Policy policy = new Policy();
        policy.setName("Precision Test Policy");
        policy.setPremiumAmount(new BigDecimal("123.456789"));
        policy.setCoverageAmount(new BigDecimal("999999.99"));
        policy.setRenewalPremiumRate(new BigDecimal("0.123456"));

        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Policy result = policyService.createPolicy(policy);

        assertNotNull(result);
        assertEquals(new BigDecimal("123.456789"), result.getPremiumAmount());
        assertEquals(new BigDecimal("999999.99"), result.getCoverageAmount());
        assertEquals(new BigDecimal("0.123456"), result.getRenewalPremiumRate());

        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void createPolicy_shouldPreserveAllInputFields() {
        LocalDateTime createdTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        Policy inputPolicy = new Policy();
        inputPolicy.setName("Complete Policy");
        inputPolicy.setDescription("Complete description");
        inputPolicy.setPremiumAmount(new BigDecimal("750.50"));
        inputPolicy.setCoverageAmount(new BigDecimal("250000.75"));
        inputPolicy.setDurationMonths(18);
        inputPolicy.setRenewalPremiumRate(new BigDecimal("0.075"));
        inputPolicy.setCreatedAt(createdTime);
        inputPolicy.setCategory(Category.HEALTH);

        when(policyRepository.save(any(Policy.class))).thenReturn(inputPolicy);

        Policy result = policyService.createPolicy(inputPolicy);

        assertEquals("Complete Policy", result.getName());
        assertEquals("Complete description", result.getDescription());
        assertEquals(new BigDecimal("750.50"), result.getPremiumAmount());
        assertEquals(new BigDecimal("250000.75"), result.getCoverageAmount());
        assertEquals(18, result.getDurationMonths());
        assertEquals(new BigDecimal("0.075"), result.getRenewalPremiumRate());
        assertEquals(createdTime, result.getCreatedAt());
        assertEquals(Category.HEALTH, result.getCategory());

        verify(policyRepository, times(1)).save(inputPolicy);
    }

    @Test
    void createPolicy_shouldHandleEdgeCaseValues() {
        Policy edgeCasePolicy = new Policy();
        edgeCasePolicy.setName("");
        edgeCasePolicy.setDescription("");
        edgeCasePolicy.setPremiumAmount(new BigDecimal("0.01"));
        edgeCasePolicy.setCoverageAmount(new BigDecimal("999999999.99"));
        edgeCasePolicy.setDurationMonths(1);
        edgeCasePolicy.setRenewalPremiumRate(new BigDecimal("0.0001"));
        edgeCasePolicy.setCategory(Category.LIFE);

        when(policyRepository.save(any(Policy.class))).thenReturn(edgeCasePolicy);

        Policy result = policyService.createPolicy(edgeCasePolicy);

        assertEquals("", result.getName());
        assertEquals("", result.getDescription());
        assertEquals(new BigDecimal("0.01"), result.getPremiumAmount());
        assertEquals(new BigDecimal("999999999.99"), result.getCoverageAmount());
        assertEquals(1, result.getDurationMonths());
        assertEquals(new BigDecimal("0.0001"), result.getRenewalPremiumRate());

        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    @Test
    void getAllPolicies_shouldReturnAllPolicies() {
        List<Policy> expectedPolicies = Arrays.asList(testPolicy1, testPolicy2);
        when(policyRepository.findAll()).thenReturn(expectedPolicies);

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(2, result.size());
        assertEquals(expectedPolicies, result);
        assertEquals("Life Insurance Premium", result.get(0).getName());
        assertEquals("Health Insurance Basic", result.get(1).getName());
        assertEquals(Category.LIFE, result.get(0).getCategory());
        assertEquals(Category.HEALTH, result.get(1).getCategory());

        verify(policyRepository, times(1)).findAll();
    }

    @Test
    void getAllPolicies_shouldReturnEmptyList_whenNoPolicies() {
        when(policyRepository.findAll()).thenReturn(Collections.emptyList());

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(policyRepository, times(1)).findAll();
    }

    @Test
    void getAllPolicies_shouldHandleLargeDataset() {
        List<Policy> largePolicyList = Arrays.asList(
                testPolicy1, testPolicy2,
                createTestPolicy(3L, "Policy 3", Category.VEHICLE),
                createTestPolicy(4L, "Policy 4", Category.LIFE),
                createTestPolicy(5L, "Policy 5", Category.HEALTH)
        );

        when(policyRepository.findAll()).thenReturn(largePolicyList);

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(5, result.size());
        assertEquals("Life Insurance Premium", result.get(0).getName());
        assertEquals("Health Insurance Basic", result.get(1).getName());
        assertEquals("Policy 3", result.get(2).getName());
        assertEquals(Category.VEHICLE, result.get(2).getCategory());

        verify(policyRepository, times(1)).findAll();
    }

    @Test
    void getAllPolicies_shouldReturnPoliciesInOrder() {
        Policy policy1 = createTestPolicy(1L, "Alpha Policy", Category.LIFE);
        Policy policy2 = createTestPolicy(2L, "Beta Policy", Category.HEALTH);
        Policy policy3 = createTestPolicy(3L, "Gamma Policy", Category.VEHICLE);

        List<Policy> orderedPolicies = Arrays.asList(policy1, policy2, policy3);
        when(policyRepository.findAll()).thenReturn(orderedPolicies);

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(3, result.size());
        assertEquals("Alpha Policy", result.get(0).getName());
        assertEquals("Beta Policy", result.get(1).getName());
        assertEquals("Gamma Policy", result.get(2).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());

        verify(policyRepository, times(1)).findAll();
    }

    @Test
    void getPolicyById_shouldReturnPolicy_whenPolicyExists() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(testPolicy1));

        Policy result = policyService.getPolicyById(1L);

        assertNotNull(result);
        assertEquals(testPolicy1, result);
        assertEquals(1L, result.getId());
        assertEquals("Life Insurance Premium", result.getName());
        assertEquals(Category.LIFE, result.getCategory());
        assertEquals(new BigDecimal("500.00"), result.getPremiumAmount());

        verify(policyRepository, times(1)).findById(1L);
    }

    @Test
    void getPolicyById_shouldReturnNull_whenPolicyNotExists() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        Policy result = policyService.getPolicyById(999L);

        assertNull(result);

        verify(policyRepository, times(1)).findById(999L);
    }

    private Policy createTestPolicy(Long id, String name, Category category) {
        Policy policy = new Policy();
        policy.setId(id);
        policy.setName(name);
        policy.setCategory(category);
        policy.setPremiumAmount(new BigDecimal("100.00"));
        policy.setCoverageAmount(new BigDecimal("10000.00"));
        policy.setDurationMonths(12);
        policy.setRenewalPremiumRate(new BigDecimal("0.05"));
        policy.setCreatedAt(testDateTime);
        return policy;
    }
}
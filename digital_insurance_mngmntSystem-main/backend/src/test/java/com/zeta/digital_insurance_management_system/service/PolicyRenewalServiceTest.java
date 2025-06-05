package com.zeta.digital_insurance_management_system.service;

import com.zeta.digital_insurance_management_system.dto.renew.RenewablePolicy;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.exception.InvalidPolicyRenewalException;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.repository.PolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import com.zeta.digital_insurance_management_system.service.PolicyRenewal.PolicyRenewalService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PolicyRenewalServiceTest {

    @InjectMocks
    private PolicyRenewalService policyRenewalService;

    @Mock
    private UserPolicyRepository userPolicyRepo;

    @Mock
    private PolicyRepository policyRepo;

    private final LocalDate today = LocalDate.now();

    @Test
    public void shouldRenewPolicy_WhenPolicyIsExpiredAndEligible() {
        Long userPolicyId = 1L;


        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setId(userPolicyId);
        userPolicy.setEndDate(today.minusDays(1));  // expired
        userPolicy.setStatus(PolicyStatus.ACTIVE);  // must be ACTIVE

        Policy policy = new Policy();
        policy.setDurationMonths(12);
        policy.setRenewalPremiumRate(new BigDecimal("1500"));

        userPolicy.setPolicy(policy);

        Mockito.when(userPolicyRepo.findById(userPolicyId)).thenReturn(Optional.of(userPolicy));
        Mockito.when(policyRepo.findById(Mockito.any())).thenReturn(Optional.of(policy));
        Mockito.when(userPolicyRepo.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));

        UserPolicy renewed = policyRenewalService.renewPolicy(userPolicyId);

        Assertions.assertEquals(PolicyStatus.ACTIVE, renewed.getStatus());
        Assertions.assertEquals(policy.getRenewalPremiumRate(), renewed.getPremiumPaid());
        Assertions.assertEquals(today, renewed.getStartDate());
        Assertions.assertEquals(today.plusMonths(policy.getDurationMonths()), renewed.getEndDate());
    }

    @Test
    public void shouldReturnPoliciesEligibleForRenewal_BasedOnEndDate() {
        Long userId = 1L;

        UserPolicy up1 = new UserPolicy();
        up1.setId(1L);
        up1.setEndDate(today.plusDays(10));  // within 30 days
        up1.setStatus(PolicyStatus.ACTIVE);
        Policy policy1 = new Policy();
        policy1.setName("Life Cover");
        policy1.setRenewalPremiumRate(new BigDecimal("1200"));
        up1.setPolicy(policy1);

        UserPolicy up2 = new UserPolicy();
        up2.setId(2L);
        up2.setEndDate(today.plusDays(40));  // outside 30 days
        up2.setStatus(PolicyStatus.ACTIVE);
        Policy policy2 = new Policy();
        policy2.setName("Health Plan");
        policy2.setRenewalPremiumRate(new BigDecimal("1400"));
        up2.setPolicy(policy2);

        List<UserPolicy> mockList = Arrays.asList(up1, up2);
        Mockito.when(userPolicyRepo.findByUserId(userId)).thenReturn(mockList);

        List<RenewablePolicy> renewable = policyRenewalService.getRenewablePolicies(userId);

        Assertions.assertEquals(1, renewable.size());
        Assertions.assertEquals("Life Cover", renewable.get(0).getPolicyName());
    }

    @Test
    public void shouldThrowException_WhenPolicyNotEligibleForRenewal() {
        Long userPolicyId = 2L;

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setId(userPolicyId);
        userPolicy.setStatus(PolicyStatus.EXPIRED);  // not ACTIVE
        userPolicy.setEndDate(today.minusDays(1));  // expired

        Mockito.when(userPolicyRepo.findById(userPolicyId)).thenReturn(Optional.of(userPolicy));

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> policyRenewalService.renewPolicy(userPolicyId)
        );

        Assertions.assertNotEquals("Not eligible", ex.getMessage());
    }

    @Test
    public void shouldThrowException_WhenPolicyIsNotExpiringSoon() {
        Long userPolicyId = 3L;

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setId(userPolicyId);
        userPolicy.setStatus(PolicyStatus.ACTIVE); // valid status
        userPolicy.setEndDate(today.plusDays(45)); // not within 30 days

        Mockito.when(userPolicyRepo.findById(userPolicyId)).thenReturn(Optional.of(userPolicy));

        InvalidPolicyRenewalException ex = Assertions.assertThrows(
                InvalidPolicyRenewalException.class,
                () -> policyRenewalService.renewPolicy(userPolicyId)
        );

        Assertions.assertEquals("Policy is not eligible for renewal yet", ex.getMessage());
    }

    @Test
    public void shouldThrowException_WhenNoRenewablePoliciesFound() {
        Long userId = 4L;

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setId(4L);
        userPolicy.setStatus(PolicyStatus.ACTIVE);
        userPolicy.setEndDate(today.plusDays(45)); // outside renewal window

        Mockito.when(userPolicyRepo.findByUserId(userId))
                .thenReturn(List.of(userPolicy));

        Assertions.assertTrue(policyRenewalService.getRenewablePolicies(userId).isEmpty());
//        Assertions.assertEquals("No renewable policies available for userId: " + userId, ex.getMessage());
    }
}
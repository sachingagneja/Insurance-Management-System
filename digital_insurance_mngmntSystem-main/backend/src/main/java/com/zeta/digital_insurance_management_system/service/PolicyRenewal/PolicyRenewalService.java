package com.zeta.digital_insurance_management_system.service.PolicyRenewal;

import com.zeta.digital_insurance_management_system.dto.renew.RenewablePolicy;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.exception.InvalidPolicyRenewalException;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.repository.PolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PolicyRenewalService implements IPolicyRenewalService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyRenewalService.class);

    private final UserPolicyRepository userPolicyRepo;
    private final PolicyRepository policyRepo;

    @Autowired
    public PolicyRenewalService(UserPolicyRepository userPolicyRepo, PolicyRepository policyRepo) {
        this.userPolicyRepo = userPolicyRepo;
        this.policyRepo = policyRepo;
    }

    @Override
    public List<RenewablePolicy> getRenewablePolicies(Long userId) {
        logger.info("Fetching renewable policies for userId: {}", userId);

        List<UserPolicy> userPolicies = userPolicyRepo.findByUserId(userId);

        if (userPolicies.isEmpty()) {
            logger.warn("No active policies found for userId: {}", userId);
            throw new ResourceNotFoundException("No active policies found for userId: " + userId);
        }

        LocalDate current = LocalDate.now();

        //Get policies that are expired or expiring within 30 days and map to RenewablePolicy.
        List<RenewablePolicy> renewablePolicies = userPolicies.stream()
                .filter(up -> {
                    long daysBetween = ChronoUnit.DAYS.between(current, up.getEndDate());
                    boolean isActive = up.getStatus() == PolicyStatus.ACTIVE;

                    boolean isExpiringSoon = daysBetween > 0 && daysBetween <= 30;
                    boolean isExpired = daysBetween <= 0;

                    return (isActive && isExpiringSoon) || isExpired;
                })
                .map(up -> new RenewablePolicy(
                        up.getId(),
                        up.getPolicy().getName(),
                        up.getEndDate(),
                        up.getPremiumPaid(),
                        up.getPolicy().getRenewalPremiumRate()
                ))
                .collect(Collectors.toList());

        if (renewablePolicies.isEmpty()) {
            logger.info("No renewable policies found for userId: {}", userId);
            return renewablePolicies;
        }

        logger.info("Found {} renewable policies for userId: {}", renewablePolicies.size(), userId);
        return renewablePolicies;
    }

    @Override
    public UserPolicy renewPolicy(Long userPolicyId) {
        logger.info("Attempting to renew policy with userPolicyId: {}", userPolicyId);

        UserPolicy userPolicy = userPolicyRepo.findById(userPolicyId)
                .orElseThrow(() -> {
                    logger.error("User policy with id {} not found", userPolicyId);
                    return new ResourceNotFoundException("User policy not found");
                });

        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, userPolicy.getEndDate());

        if (daysUntilExpiry > 30) {
            logger.warn("Policy with id {} is not near expiry (more than 30 days left)", userPolicyId);
            throw new InvalidPolicyRenewalException("Policy is not eligible for renewal yet");
        }

        Policy policy = policyRepo.findById(userPolicy.getPolicy().getId())
                .orElseThrow(() -> {
                    logger.error("Associated master policy not found for userPolicy id {}", userPolicyId);
                    return new ResourceNotFoundException("Associated policy not found");
                });

        userPolicy.setPremiumPaid(policy.getRenewalPremiumRate());
        userPolicy.setStartDate(today);
        userPolicy.setEndDate(today.plusMonths(policy.getDurationMonths()));
        userPolicy.setStatus(PolicyStatus.ACTIVE);

        UserPolicy savedPolicy = userPolicyRepo.save(userPolicy);
        logger.info("Successfully renewed policy with userPolicyId: {}", userPolicyId);
        return savedPolicy;
    }
}
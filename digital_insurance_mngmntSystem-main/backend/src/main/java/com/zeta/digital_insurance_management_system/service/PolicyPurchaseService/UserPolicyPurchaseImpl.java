package com.zeta.digital_insurance_management_system.service.PolicyPurchaseService;

import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.repository.PolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import com.zeta.digital_insurance_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class UserPolicyPurchaseImpl implements UserPolicyPurchase{
    private static final Logger logger = LoggerFactory.getLogger(UserPolicyPurchaseImpl.class);

    @Autowired PolicyRepository policyRepository;
    @Autowired UserPolicyRepository userPolicyRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public UserPolicy purchaseAPolicy(Long policyId, Long  userId) {
        logger.info("Attempting to purchase policy with ID {} for user ID {}", policyId, userId);

        Optional<UserPolicy> existingPolicyOpt = userPolicyRepository.findByUserIdAndPolicyId(userId, policyId);
        if (existingPolicyOpt.isPresent()) {
            logger.warn("User ID {} already has policy ID {}", userId, policyId);
            throw new ResourceNotFoundException("User has already purchased this policy.");
        }

        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> {
                    logger.error("Policy with ID {} not found", policyId);
                    return new ResourceNotFoundException("Policy with ID " + policyId + " not found");
                });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User with ID " + userId + " not found");
                });

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setPolicy(policy);
        userPolicy.setUser(user);
        LocalDate startDate = LocalDate.now();
        userPolicy.setStartDate(startDate);
        userPolicy.setEndDate(userPolicy.getStartDate().plusMonths(policy.getDurationMonths()));
        userPolicy.setStatus(PolicyStatus.ACTIVE);
        userPolicy.setPremiumPaid(policy.getPremiumAmount());

        logger.info("Successfully created UserPolicy for user ID {} with policy ID {}", userId, policyId);
        return userPolicyRepository.save(userPolicy);
    }

    @Override
    public List<UserPolicy> getPurchasedPolicies(Long userId) {
        logger.info("Fetching purchased policies for user ID {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User with ID " + userId + " not found");
                });
        return userPolicyRepository.findByUserId(userId);
    }

    @Override
    public UserPolicy updatePolicy(Long policyId ,Long userId, PolicyStatus status) {
        logger.info("Updating policy ID {} for user ID {} to status {}", policyId, userId, status);

        UserPolicy userPolicy = userPolicyRepository.findByUserIdAndPolicyId(userId,policyId)
                .orElseThrow(() -> {
                    logger.error("User policy with ID {} for user ID {} not found", policyId, userId);
                    return new ResourceNotFoundException("User policy with ID " + policyId + " not found");
                });

        userPolicy.setStatus(status);

        if (status == PolicyStatus.CANCELLED) {
            userPolicy.setEndDate(LocalDate.now());
            logger.info("Policy ID {} for user ID {} cancelled", policyId, userId);

        } else if (status == PolicyStatus.RENEWED) {
            LocalDate newEndDate = userPolicy.getEndDate().plusMonths(userPolicy.getPolicy().getDurationMonths());
            userPolicy.setEndDate(newEndDate);
            logger.info("Policy ID {} for user ID {} renewed until {}", policyId, userId, newEndDate);
        }
        return userPolicyRepository.save(userPolicy);
    }
}
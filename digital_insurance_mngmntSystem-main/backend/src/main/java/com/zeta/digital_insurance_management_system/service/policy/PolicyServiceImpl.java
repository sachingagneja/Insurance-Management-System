package com.zeta.digital_insurance_management_system.service.policy;

import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyServiceImpl implements PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyServiceImpl.class);
    private final PolicyRepository policyRepository;

    public PolicyServiceImpl(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public Policy createPolicy(Policy policy) {
        logger.info("Creating new policy: {}", policy.getName());
        return policyRepository.save(policy);
    }

    @Override
    public List<Policy> getAllPolicies() {
        logger.info("Fetching all policies");
        return policyRepository.findAll();
    }

    @Override
    public Policy getPolicyById(Long id) {
        logger.info("Fetching policy with ID: {}", id);
        return policyRepository.findById(id).orElse(null);
    }

    @Override
    public Policy updatePolicy(Long id, Policy updatedPolicy) {
        logger.info("Updating policy with ID: {}", id);
        Policy existingPolicy = policyRepository.findById(id).orElse(null);
        if (existingPolicy != null) {
            existingPolicy.setName(updatedPolicy.getName());
            existingPolicy.setDescription(updatedPolicy.getDescription());
            existingPolicy.setPremiumAmount(updatedPolicy.getPremiumAmount());
            existingPolicy.setCoverageAmount(updatedPolicy.getCoverageAmount());
            existingPolicy.setDurationMonths(updatedPolicy.getDurationMonths());
            existingPolicy.setRenewalPremiumRate(updatedPolicy.getRenewalPremiumRate());
            existingPolicy.setCreatedAt(updatedPolicy.getCreatedAt());
            existingPolicy.setCategory(updatedPolicy.getCategory());
            logger.info("Policy updated: {}", existingPolicy.getName());
            return policyRepository.save(existingPolicy);
        } else {
            logger.warn("Policy with ID {} not found for update", id);
            return null;
        }
    }

    @Override
    public void deletePolicy(Long id) {
        logger.info("Deleting policy with ID: {}", id);
        policyRepository.deleteById(id);
    }
}

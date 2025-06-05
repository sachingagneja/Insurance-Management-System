package com.zeta.digital_insurance_management_system.service.policy;

import com.zeta.digital_insurance_management_system.model.Policy;

import java.util.List;

public interface PolicyService {
    List<Policy> getAllPolicies();
    Policy createPolicy(Policy policy);
    Policy getPolicyById(Long id);
    Policy updatePolicy(Long id, Policy policy);
    void deletePolicy(Long id);
}

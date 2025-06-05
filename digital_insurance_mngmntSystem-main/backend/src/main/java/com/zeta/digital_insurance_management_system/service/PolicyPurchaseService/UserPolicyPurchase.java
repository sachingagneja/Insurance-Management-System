package com.zeta.digital_insurance_management_system.service.PolicyPurchaseService;

import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.model.UserPolicy;

import java.util.List;

public interface UserPolicyPurchase {
    UserPolicy purchaseAPolicy(Long policyId, Long userId);
    List<UserPolicy> getPurchasedPolicies(Long userId);
    UserPolicy updatePolicy(Long policyId, Long userId, PolicyStatus status);
}

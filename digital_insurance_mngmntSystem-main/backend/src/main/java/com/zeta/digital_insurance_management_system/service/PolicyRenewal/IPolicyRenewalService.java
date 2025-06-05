package com.zeta.digital_insurance_management_system.service.PolicyRenewal;

import com.zeta.digital_insurance_management_system.dto.renew.RenewablePolicy;
import com.zeta.digital_insurance_management_system.model.UserPolicy;

import java.util.List;

public interface IPolicyRenewalService {
    List<RenewablePolicy> getRenewablePolicies(Long userId);
    UserPolicy renewPolicy(Long policyId);
}

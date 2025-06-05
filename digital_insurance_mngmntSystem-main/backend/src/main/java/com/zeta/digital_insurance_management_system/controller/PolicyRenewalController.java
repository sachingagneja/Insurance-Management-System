package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.dto.renew.RenewablePolicy;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.service.PolicyRenewal.IPolicyRenewalService;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class PolicyRenewalController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyRenewalController.class);

    @Autowired
    private IPolicyRenewalService renewalService;

    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/policy/{policyId}/renew")
    public ResponseEntity<UserPolicy> renewPolicy(@PathVariable Long policyId) {
        logger.info("Received request to renew policy with id: {}", policyId);
        UserPolicy renewedPolicy = renewalService.renewPolicy(policyId);
        logger.info("Policy renewal completed for id: {}", policyId);
        return ResponseEntity.ok(renewedPolicy);
    }

    @GetMapping("/user/policies/renewable")
    public ResponseEntity<List<RenewablePolicy>> getRenewablePolicies() {
        Long userId = userService.getCurrentUserId();
        logger.info("Received request to get renewable policies for userId: {}", userId);
        List<RenewablePolicy> policies = renewalService.getRenewablePolicies(userId);
        logger.info("Returning {} renewable policies for userId: {}", policies.size(), userId);
        return ResponseEntity.ok(policies);
    }
}
package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.service.PolicyPurchaseService.UserPolicyPurchaseImpl;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController @RequestMapping("/user/policy")
public class UserPolicyPurchaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserPolicyPurchaseController.class);

    @Autowired UserPolicyPurchaseImpl userPolicyPurchase;
    @Autowired UserServiceImpl userService;

    @PostMapping("/{policyId}/purchase")
    public ResponseEntity<UserPolicy> purchasePolicy(@PathVariable Long policyId, @RequestHeader("Authorization") String token) {
        Long userId = userService.getCurrentUserId();
        logger.info("User ID {} is attempting to purchase policy ID {}", userId, policyId);

        UserPolicy userPolicy = userPolicyPurchase.purchaseAPolicy(policyId, userId);
        logger.info("Policy ID {} successfully purchased by user ID {}", policyId, userId);

        return new ResponseEntity<UserPolicy>(userPolicy, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserPolicy>> getUserPolicies(@RequestHeader("Authorization") String token) {
        Long userId = userService.getCurrentUserId();
        logger.info("Fetching purchased policies for user ID {}", userId);

        List<UserPolicy> policies = userPolicyPurchase.getPurchasedPolicies(userId);
        logger.info("Found {} policies for user ID {}", policies.size(), userId);

        return ResponseEntity.ok(policies);
    }

    @PutMapping
    public ResponseEntity<UserPolicy> putUpdatePolicy(@RequestParam Long policyId, @RequestParam PolicyStatus status, @RequestHeader("Authorization") String token) {
        Long userId = userService.getCurrentUserId();
        logger.info("Updating policy ID {} for user ID {} to status {}", policyId, userId, status);

        UserPolicy updatedPolicy = userPolicyPurchase.updatePolicy(policyId, userId, status);
        logger.info("Successfully updated policy ID {} for user ID {} to status {}", policyId, userId, status);

        return ResponseEntity.ok(updatedPolicy);
    }
}

package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.service.policy.PolicyService;
import com.zeta.digital_insurance_management_system.service.user.UserService;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/policies")
public class PolicyController {

    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);

    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        logger.info("Received request to fetch all policies");
        List<Policy> policies = policyService.getAllPolicies();
        logger.info("Fetched {} policies", policies.size());
        return ResponseEntity.ok(policies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id) {
        logger.info("Received request to fetch policy with id: {}", id);
        Policy policy = policyService.getPolicyById(id);
        logger.info("Fetched policy with id: {}", id);
        return ResponseEntity.ok(policy);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        logger.info("Received request to create a new policy");
        Policy createdPolicy = policyService.createPolicy(policy);
        logger.info("Created policy with id: {}", createdPolicy.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @RequestBody Policy policy) {
        logger.info("Received request to update policy with id: {}", id);
        Policy updatedPolicy = policyService.updatePolicy(id, policy);
        logger.info("Updated policy with id: {}", id);
        return ResponseEntity.ok(updatedPolicy);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long id) {
        logger.info("Received request to delete policy with id: {}", id);
        policyService.deletePolicy(id);
        logger.info("Deleted policy with id: {}", id);
        return ResponseEntity.ok("Policy deleted successfully");
    }
}

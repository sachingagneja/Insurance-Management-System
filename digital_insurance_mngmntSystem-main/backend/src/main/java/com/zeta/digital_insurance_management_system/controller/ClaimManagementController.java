package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.dto.Claim.UserClaimDTO;
import com.zeta.digital_insurance_management_system.dto.Claim.AdminClaimStatusUpdateDTO;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.service.ClaimManagement.ClaimManagementService;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClaimManagementController {

    private static final Logger logger = LoggerFactory.getLogger(ClaimManagementController.class);

    private final ClaimManagementService claimManagementService;
    @Autowired UserServiceImpl userServiceImplementation;
    @Autowired
    public ClaimManagementController(ClaimManagementService claimManagementService) {
        this.claimManagementService = claimManagementService;
    }

    @PostMapping("/claim")
    public ResponseEntity<Claim> submitClaim(@RequestBody UserClaimDTO claimDTO) {
        logger.info("Submitting claim for userPolicyId: {}", claimDTO.getUserPolicyId());
        Claim claim = claimManagementService.submitClaim(claimDTO);
        logger.info("Claim submitted successfully with ID: {}", claim.getId());
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/user/claim")
    public ResponseEntity<List<Claim>> getAllClaims() {
        logger.info("Fetching claims for all users");
        List<Claim> claim = claimManagementService.getAllClaims();
        logger.info("Found all the claims");
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/user/claimById")
    public ResponseEntity<List<Claim>> getClaimsByUser(@RequestHeader("Authorization") String token) {
        Long id = userServiceImplementation.getCurrentUserId();
        logger.info("Fetching claims for userId: {}", id);
        List<Claim> claim = claimManagementService.getClaimsByUser(id);
        logger.info("Found {} claims for userId: {}", claim.size(), id);
        return ResponseEntity.ok(claim);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/claim/{claimId}/status")
    public ResponseEntity<Claim> updateClaimStatus(
            @PathVariable Long claimId,
            @RequestBody AdminClaimStatusUpdateDTO statusUpdateDTO
    ) {
        logger.info("Updating claim status for claimId: {} to {}", claimId, statusUpdateDTO.getStatus());
        Claim updatedClaim = claimManagementService.updateClaimStatus(
                claimId,
                statusUpdateDTO.getStatus(),
                statusUpdateDTO.getReviewerComment()
        );
        logger.info("Claim with ID {} updated to status {}", claimId, updatedClaim.getStatus());
        return ResponseEntity.ok(updatedClaim);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/claim/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable Long id) {
        logger.info("Deleting claim with id: {}", id);
        claimManagementService.deleteClaim(id);
        logger.info("Claim deleted successfully with id: {}", id);
        return ResponseEntity.ok().build();
    }
}

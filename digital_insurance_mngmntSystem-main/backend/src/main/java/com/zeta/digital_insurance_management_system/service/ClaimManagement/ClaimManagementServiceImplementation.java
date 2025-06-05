package com.zeta.digital_insurance_management_system.service.ClaimManagement;

import com.zeta.digital_insurance_management_system.dto.Claim.UserClaimDTO;
import com.zeta.digital_insurance_management_system.enums.ClaimStatus;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import com.zeta.digital_insurance_management_system.repository.ClaimManagementRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaimManagementServiceImplementation implements ClaimManagementService {

    private static final Logger logger = LoggerFactory.getLogger(ClaimManagementServiceImplementation.class);

    private final ClaimManagementRepository claimManagementRepository;
    private final UserPolicyRepository userPolicyRepository;

    @Autowired
    public ClaimManagementServiceImplementation(ClaimManagementRepository claimManagementRepository, UserPolicyRepository userPolicyRepository) {
        this.claimManagementRepository = claimManagementRepository;
        this.userPolicyRepository = userPolicyRepository;
    }

    @Override
    public Claim submitClaim(UserClaimDTO claimDTO) throws ResourceNotFoundException {
        logger.info("Submitting claim for user policy ID: {}", claimDTO.getUserPolicyId());

        UserPolicy userPolicy = userPolicyRepository.findById(claimDTO.getUserPolicyId())
                .orElseThrow(() -> {
                    logger.error("UserPolicy not found with ID: {}", claimDTO.getUserPolicyId());
                    return new ResourceNotFoundException("UserPolicy not found");
                });

        if (!userPolicy.getStatus().equals(PolicyStatus.ACTIVE)) {
            logger.warn("Attempt to submit claim for inactive policy ID: {}", userPolicy.getId());
            throw new IllegalArgumentException("User policy is not active");
        }

        Claim claim = new Claim();
        claim.setUserPolicy(userPolicy);
        claim.setClaimAmount(claimDTO.getClaimAmount());
        claim.setReason(claimDTO.getReason());
        claim.setClaimDate(LocalDate.now());
        claim.setStatus(ClaimStatus.PENDING);

        Claim savedClaim = claimManagementRepository.save(claim);
        logger.info("Claim submitted successfully with ID: {}", savedClaim.getId());

        return savedClaim;
    }

    @Override
    public List<Claim> getClaimsByUser(Long userId) throws ResourceNotFoundException {
        logger.info("Fetching claims for user ID: {}", userId);
        List<Claim> claims = claimManagementRepository.findByUserPolicy_User_Id(userId);
        if (claims.isEmpty()) {
            logger.warn("No claims found for user ID: {}", userId);
            throw new ResourceNotFoundException("No claims found for user ID: " + userId);
        }
        logger.info("Found {} claims for user ID: {}", claims.size(), userId);
        return claims;
    }

    @Override
    public List<Claim> getAllClaims() {
        logger.info("Fetching all claims");
        return claimManagementRepository.findAll();
    }

    @Override
    public Claim getClaimById(Long id) {
        logger.info("Fetching claim by ID: {}", id);
        return claimManagementRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Claim not found with ID: {}", id);
                    return new RuntimeException("Claim not found with id: " + id);
                });
    }

    @Override
    public Claim updateClaimStatus(Long claimId, String status, String reviewerComment) throws IllegalArgumentException {
        logger.info("Updating claim status for claim ID: {}", claimId);
        Claim claimToUpdate = claimManagementRepository.findById(claimId).orElse(null);

        if (claimToUpdate == null) {
            logger.error("Claim not found with ID: {}", claimId);
            throw new IllegalArgumentException("Claim not found with ID: " + claimId);
        }

        if (!List.of("APPROVED", "REJECTED").contains(status.toUpperCase())) {
            logger.warn("Invalid claim status: {}", status);
            throw new IllegalArgumentException("Status must be APPROVED or REJECTED");
        }

        claimToUpdate.setStatus(ClaimStatus.valueOf(status.toUpperCase()));
        claimToUpdate.setReviewerComment(reviewerComment);
        claimToUpdate.setResolvedDate(LocalDate.now());

        Claim updatedClaim = claimManagementRepository.save(claimToUpdate);
        logger.info("Claim ID {} updated with status {}", claimId, status.toUpperCase());

        return updatedClaim;
    }

    @Override
    public void deleteClaim(Long claimId) throws ResourceNotFoundException {
        logger.info("Deleting claim with ID: {}", claimId);
        if (!claimManagementRepository.existsById(claimId)) {
            logger.error("Attempted to delete non-existent claim ID: {}", claimId);
            throw new ResourceNotFoundException("Claim not found");
        }
        claimManagementRepository.deleteById(claimId);
        logger.info("Claim with ID {} deleted successfully", claimId);
    }
}
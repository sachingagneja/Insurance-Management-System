package com.zeta.digital_insurance_management_system.service.ClaimManagement;

import com.zeta.digital_insurance_management_system.dto.Claim.UserClaimDTO;
import com.zeta.digital_insurance_management_system.exception.ResourceNotFoundException;
import com.zeta.digital_insurance_management_system.model.Claim;

import java.util.List;

public interface ClaimManagementService {
    // Create
    public Claim submitClaim(UserClaimDTO claimDTO) throws ResourceNotFoundException;

    // Read
    public List<Claim> getClaimsByUser(Long userId) throws ResourceNotFoundException;
    public List<Claim> getAllClaims();
    public Claim getClaimById(Long id);

    // Update
    public Claim updateClaimStatus(Long claimId, String status, String reviewerComment) throws IllegalArgumentException;

    // Delete
    public void deleteClaim(Long claimId) throws ResourceNotFoundException;
}

package com.zeta.digital_insurance_management_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zeta.digital_insurance_management_system.model.Claim;

@Repository public interface ClaimManagementRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByUserPolicy_User_Id(Long userId);
    boolean existsByIdAndUserPolicy_UserId(Long claimId, Long userId);
}

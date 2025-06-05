package com.zeta.digital_insurance_management_system.repository;

import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPolicyRepository extends JpaRepository<UserPolicy, Long> {
    List<UserPolicy> findByUserIdAndStatus(Long userId, PolicyStatus status);
    List<UserPolicy> findByUserId(Long userId);
    Optional<UserPolicy> findByUserIdAndPolicyId(Long userId, Long policyId);
    boolean existsByUserIdAndPolicyId(Long userId, Long policyId);
}

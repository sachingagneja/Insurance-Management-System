package com.zeta.digital_insurance_management_system.repository;

import com.zeta.digital_insurance_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

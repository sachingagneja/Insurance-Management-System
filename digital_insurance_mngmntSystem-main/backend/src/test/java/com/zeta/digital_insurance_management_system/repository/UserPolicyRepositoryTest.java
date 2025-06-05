package com.zeta.digital_insurance_management_system.repository;

import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.enums.Category;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.UserPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserPolicyRepositoryTest {

    @Autowired
    private UserPolicyRepository userPolicyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyRepository policyRepository;

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("hashedpassword");
        user.setPhone("1234567890");
        user.setAddress("123 Main St");
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    private Policy createPolicy(String name) {
        Policy policy = new Policy();
        policy.setName(name);
        policy.setDescription("Sample description");
        policy.setPremiumAmount(new BigDecimal("1200"));
        policy.setCoverageAmount(new BigDecimal("10000"));
        policy.setDurationMonths(12);
        policy.setRenewalPremiumRate(new BigDecimal("0.1"));
        policy.setCategory(Category.HEALTH);
        return policyRepository.save(policy);
    }


    @Test
    void testFindByUserId() {
        // Arrange
        User user1 = createUser("Nikhil", "nikhil@example.com");
        User user2 = createUser("muneer", "muneer@example.com");

        Policy policyA = createPolicy("Health Plan A");
        Policy policyB = createPolicy("Health Plan B");
        Policy policyC = createPolicy("Health Plan C");

        UserPolicy up1 = new UserPolicy();
        up1.setUser(user1);
        up1.setPolicy(policyA);
        up1.setStartDate(LocalDate.now());
        up1.setEndDate(LocalDate.now().plusYears(1));
        up1.setStatus(PolicyStatus.ACTIVE);
        up1.setPremiumPaid(new BigDecimal("100.00"));
        userPolicyRepository.save(up1);

        UserPolicy up2 = new UserPolicy();
        up2.setUser(user1);
        up2.setPolicy(policyB);
        up2.setStartDate(LocalDate.now());
        up2.setEndDate(LocalDate.now().plusYears(1));
        up2.setStatus(PolicyStatus.ACTIVE);
        up2.setPremiumPaid(new BigDecimal("150.00"));
        userPolicyRepository.save(up2);

        UserPolicy up3 = new UserPolicy();
        up3.setUser(user2);
        up3.setPolicy(policyC);
        up3.setStartDate(LocalDate.now());
        up3.setEndDate(LocalDate.now().plusYears(1));
        up3.setStatus(PolicyStatus.ACTIVE);
        up3.setPremiumPaid(new BigDecimal("200.00"));
        userPolicyRepository.save(up3);

        // Act
        List<UserPolicy> user1Policies = userPolicyRepository.findByUserId(user1.getId());

        // Assert
        assertThat(user1Policies).hasSize(2);
        assertThat(user1Policies).allMatch(up -> up.getUser().getId().equals(user1.getId()));
    }

    @Test
    void testFindByUserIdAndPolicyId() {
        // Arrange
        User user = createUser("bhawnahr", "bhawnahr@example.com");
        Policy policy = createPolicy("Health Plan");

        UserPolicy up = new UserPolicy();
        up.setUser(user);
        up.setPolicy(policy);
        up.setStartDate(LocalDate.now().minusYears(1));
        up.setEndDate(LocalDate.now());
        up.setStatus(PolicyStatus.EXPIRED);
        up.setPremiumPaid(new BigDecimal("1200"));
        userPolicyRepository.save(up);

        // Act
        Optional<UserPolicy> found = userPolicyRepository.findByUserIdAndPolicyId(user.getId(), policy.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(found.get().getPolicy().getId()).isEqualTo(policy.getId());

        // Negative case
        Optional<UserPolicy> notFound = userPolicyRepository.findByUserIdAndPolicyId(user.getId(), 999L);
        assertThat(notFound).isNotPresent();
    }
}
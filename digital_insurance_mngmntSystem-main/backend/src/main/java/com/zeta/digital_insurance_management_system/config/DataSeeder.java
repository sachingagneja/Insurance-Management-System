package com.zeta.digital_insurance_management_system.config;

import com.zeta.digital_insurance_management_system.enums.Category;
import com.zeta.digital_insurance_management_system.enums.ClaimStatus;
import com.zeta.digital_insurance_management_system.enums.PolicyStatus;
import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.model.*;
import com.zeta.digital_insurance_management_system.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {



    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserPolicyRepository userPolicyRepository;

    @Autowired
    private ClaimManagementRepository claimRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) { // Check if data already exists
            logger.info("No existing data found. Seeding initial data...");
            seedUsers();
            seedPolicies();
            seedUserPolicies();
            seedClaims();
            seedSupportTickets();
            logger.info("Initial data seeding completed.");
        } else {
            logger.info("Existing data found. Skipping data seeding.");
        }
    }

    private void seedUsers() {
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole(Role.ADMIN);
        admin.setPhone("0000000000");
        admin.setAddress("Admin Address");
        userRepository.save(admin);
        logger.info("Created admin user: {}", admin.getEmail());

        User user1 = new User();
        user1.setName("Normal User One");
        user1.setEmail("user1@example.com");
        user1.setPassword(passwordEncoder.encode("User@123"));
        user1.setRole(Role.USER);
        user1.setPhone("1111111111");
        user1.setAddress("1 User St");
        userRepository.save(user1);
        logger.info("Created normal user: {}", user1.getEmail());

        User user2 = new User();
        user2.setName("Normal User Two");
        user2.setEmail("user2@example.com");
        user2.setPassword(passwordEncoder.encode("User@456"));
        user2.setRole(Role.USER);
        user2.setPhone("2222222222");
        user2.setAddress("2 User St");
        userRepository.save(user2);
        logger.info("Created normal user: {}", user2.getEmail());

        User user3 = new User();
        user3.setName("Normal User Three");
        user3.setEmail("user3@example.com");
        user3.setPassword(passwordEncoder.encode("User@789"));
        user3.setRole(Role.USER);
        user3.setPhone("3333333333");
        user3.setAddress("3 User St");
        userRepository.save(user3);
        logger.info("Created normal user: {}", user3.getEmail());
    }

    private void seedPolicies() {
        Policy policy1 = new Policy();
        policy1.setName("LifeSecure Plus");
        policy1.setDescription("Comprehensive life coverage for individuals and families.");
        policy1.setPremiumAmount(new BigDecimal("100.00"));
        policy1.setCoverageAmount(new BigDecimal("50000.00"));
        policy1.setDurationMonths(12);
        policy1.setRenewalPremiumRate(new BigDecimal("0.05"));
        policy1.setCategory(Category.LIFE);
        policy1.setCreatedAt(LocalDateTime.now().minusDays(10));
        policyRepository.save(policy1);

        Policy policy2 = new Policy();
        policy2.setName("HealthGuard Basic");
        policy2.setDescription("Essential health protection for everyday medical needs.");
        policy2.setPremiumAmount(new BigDecimal("75.50"));
        policy2.setCoverageAmount(new BigDecimal("25000.00"));
        policy2.setDurationMonths(12);
        policy2.setRenewalPremiumRate(new BigDecimal("0.04"));
        policy2.setCategory(Category.HEALTH);
        policy2.setCreatedAt(LocalDateTime.now().minusDays(20));
        policyRepository.save(policy2);

        Policy policy3 = new Policy();
        policy3.setName("AutoShield Comprehensive");
        policy3.setDescription("Full vehicle protection against damage and theft.");
        policy3.setPremiumAmount(new BigDecimal("120.25"));
        policy3.setCoverageAmount(new BigDecimal("10000.00"));
        policy3.setDurationMonths(6);
        policy3.setRenewalPremiumRate(new BigDecimal("0.06"));
        policy3.setCategory(Category.VEHICLE);
        policy3.setCreatedAt(LocalDateTime.now().minusDays(5));
        policyRepository.save(policy3);

        Policy policy4 = new Policy();
        policy4.setName("TermLife Standard");
        policy4.setDescription("Standard term life insurance with flexible options.");
        policy4.setPremiumAmount(new BigDecimal("50.00"));
        policy4.setCoverageAmount(new BigDecimal("20000.00"));
        policy4.setDurationMonths(24);
        policy4.setRenewalPremiumRate(new BigDecimal("0.03"));
        policy4.setCategory(Category.LIFE);
        policy4.setCreatedAt(LocalDateTime.now().minusDays(15));
        policyRepository.save(policy4);

        Policy policy5 = new Policy();
        policy5.setName("MediCare Advanced");
        policy5.setDescription("Advanced health coverage with add-ons for critical illness.");
        policy5.setPremiumAmount(new BigDecimal("150.00"));
        policy5.setCoverageAmount(new BigDecimal("75000.00"));
        policy5.setDurationMonths(12);
        policy5.setRenewalPremiumRate(new BigDecimal("0.05"));
        policy5.setCategory(Category.HEALTH);
        policy5.setCreatedAt(LocalDateTime.now().minusDays(30));
        policyRepository.save(policy5);
        logger.info("Seeded {} policies.", policyRepository.count());
    }

    private void seedUserPolicies() {
        User user1 = userRepository.findByEmail("user1@example.com");
        User user2 = userRepository.findByEmail("user2@example.com");
        User user3 = userRepository.findByEmail("user3@example.com");

        Policy lifeSecure = policyRepository.findAll().stream().filter(p -> p.getName().equals("LifeSecure Plus")).findFirst().orElse(null);
        Policy healthGuard = policyRepository.findAll().stream().filter(p -> p.getName().equals("HealthGuard Basic")).findFirst().orElse(null);
        Policy autoShield = policyRepository.findAll().stream().filter(p -> p.getName().equals("AutoShield Comprehensive")).findFirst().orElse(null);
        Policy termLife = policyRepository.findAll().stream().filter(p -> p.getName().equals("TermLife Standard")).findFirst().orElse(null);
        Policy mediCare = policyRepository.findAll().stream().filter(p -> p.getName().equals("MediCare Advanced")).findFirst().orElse(null);

        // User 1
        if (user1 != null && lifeSecure != null) {
            // This policy remains active and not immediately renewable
            UserPolicy up1 = new UserPolicy(null, user1, lifeSecure, LocalDate.now().minusMonths(lifeSecure.getDurationMonths()).plusMonths(2), LocalDate.now().plusMonths(10), PolicyStatus.ACTIVE, lifeSecure.getPremiumAmount());
            userPolicyRepository.save(up1);
        }
        if (user1 != null && healthGuard != null) {
            // This policy will expire in 15 days for User 1
            LocalDate healthGuardEndDate = LocalDate.now().plusDays(15);
            LocalDate healthGuardStartDate = healthGuardEndDate.minusMonths(healthGuard.getDurationMonths());
            UserPolicy up2 = new UserPolicy(null, user1, healthGuard, healthGuardStartDate, healthGuardEndDate, PolicyStatus.ACTIVE, healthGuard.getPremiumAmount());
            userPolicyRepository.save(up2);
        }

        // User 2
        if (user2 != null && autoShield != null) {
            // This policy expired 10 days ago for User 2
            LocalDate autoShieldEndDate = LocalDate.now().minusDays(10);
            LocalDate autoShieldStartDate = autoShieldEndDate.minusMonths(autoShield.getDurationMonths());
            UserPolicy up3 = new UserPolicy(null, user2, autoShield, autoShieldStartDate, autoShieldEndDate, PolicyStatus.EXPIRED, autoShield.getPremiumAmount());
            userPolicyRepository.save(up3);
        }
        if (user2 != null && mediCare != null) {
            // This policy is already expired (original logic)
            UserPolicy up4 = new UserPolicy(null, user2, mediCare, LocalDate.now().minusMonths(mediCare.getDurationMonths()), LocalDate.now(), PolicyStatus.EXPIRED, mediCare.getPremiumAmount());
            userPolicyRepository.save(up4);
        }

        // User 3
        if (user3 != null && lifeSecure != null) {
            // This policy will expire in 20 days for User 3
            LocalDate lifeSecureUser3EndDate = LocalDate.now().plusDays(20);
            LocalDate lifeSecureUser3StartDate = lifeSecureUser3EndDate.minusMonths(lifeSecure.getDurationMonths());
            UserPolicy up5 = new UserPolicy(null, user3, lifeSecure, lifeSecureUser3StartDate, lifeSecureUser3EndDate, PolicyStatus.ACTIVE, lifeSecure.getPremiumAmount());
            userPolicyRepository.save(up5);
        }
        if (user3 != null && termLife != null) {
            // This policy remains cancelled
            UserPolicy up6 = new UserPolicy(null, user3, termLife, LocalDate.now().minusDays(5), LocalDate.now().plusMonths(termLife.getDurationMonths()).minusDays(5), PolicyStatus.CANCELLED, termLife.getPremiumAmount());
            userPolicyRepository.save(up6);
        }
        logger.info("Seeded {} user policies.", userPolicyRepository.count());
    }

    private void seedClaims() {
        List<UserPolicy> userPolicies = userPolicyRepository.findAll();
        UserPolicy up1 = userPolicies.stream().filter(up -> up.getUser().getEmail().equals("user1@example.com") && up.getPolicy().getName().equals("LifeSecure Plus")).findFirst().orElse(null);
        UserPolicy up2 = userPolicies.stream().filter(up -> up.getUser().getEmail().equals("user1@example.com") && up.getPolicy().getName().equals("HealthGuard Basic")).findFirst().orElse(null);
        UserPolicy up3 = userPolicies.stream().filter(up -> up.getUser().getEmail().equals("user2@example.com") && up.getPolicy().getName().equals("AutoShield Comprehensive")).findFirst().orElse(null);

        if (up1 != null) {
            Claim claim1 = new Claim(null, up1, LocalDate.now().minusMonths(1), new BigDecimal("500.00"), "Minor hospital visit for consultation.", ClaimStatus.PENDING, null, null);
            claimRepository.save(claim1);
        }
        if (up2 != null) {
            Claim claim2 = new Claim(null, up2, LocalDate.now().minusWeeks(2), new BigDecimal("1200.75"), "Specialist consultation and prescribed medication.", ClaimStatus.APPROVED, "Approved as per policy terms.", LocalDate.now().minusWeeks(1));
            claimRepository.save(claim2);
        }
        if (up3 != null) {
            Claim claim3 = new Claim(null, up3, LocalDate.now().minusDays(10), new BigDecimal("300.00"), "Windshield repair due to road debris.", ClaimStatus.REJECTED, "Damage type not covered.", LocalDate.now().minusDays(5));
            claimRepository.save(claim3);
        }
        logger.info("Seeded {} claims.", claimRepository.count());
    }

    private void seedSupportTickets() {
        User user1 = userRepository.findByEmail("user1@example.com");
        User user2 = userRepository.findByEmail("user2@example.com");
        User user3 = userRepository.findByEmail("user3@example.com");

        Policy lifeSecure = policyRepository.findAll().stream().filter(p -> p.getName().equals("LifeSecure Plus")).findFirst().orElse(null);
        Claim claim3 = claimRepository.findAll().stream()
            .filter(c -> c.getUserPolicy().getUser().getEmail().equals("user2@example.com") && c.getReason().contains("Windshield repair"))
            .findFirst().orElse(null);


        if (user1 != null) {
            SupportTicket st1 = new SupportTicket(null, user1, null, null, "Login Issue", "I am unable to reset my password. The link seems to be expired.", SupportTicketStatus.OPEN, null, LocalDateTime.now().minusDays(5), null);
            supportTicketRepository.save(st1);
        }
        if (user1 != null && lifeSecure != null) {
            SupportTicket st2 = new SupportTicket(null, user1, lifeSecure, null, "Query about Policy Coverage", "What are the specific exclusions for the LifeSecure Plus policy?", SupportTicketStatus.RESOLVED, "Please refer to section 3, subsection B of your policy document.", LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
            supportTicketRepository.save(st2);
        }
        if (user2 != null && claim3 != null && claim3.getUserPolicy().getPolicy() != null) {
             SupportTicket st3 = new SupportTicket(null, user2, claim3.getUserPolicy().getPolicy(), claim3, "Regarding Rejected Claim", "I would like to understand more about why my claim for windshield repair was rejected.", SupportTicketStatus.OPEN, null, LocalDateTime.now().minusDays(2), null);
            supportTicketRepository.save(st3);
        }
         if (user3 != null) {
            SupportTicket st4 = new SupportTicket(null, user3, null, null, "How to update contact information?", "I need to update my registered phone number and address.", SupportTicketStatus.CLOSED, "You can update your contact information via your profile settings.", LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9));
            supportTicketRepository.save(st4);
        }
        logger.info("Seeded {} support tickets.", supportTicketRepository.count());
    }
}

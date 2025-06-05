package com.zeta.digital_insurance_management_system.repository;

import com.zeta.digital_insurance_management_system.enums.Role;
import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.model.SupportTicket;
import com.zeta.digital_insurance_management_system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SupportTicketRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketRepositoryTest.class);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    private User user1;
    private User user2;
    private SupportTicket ticket1;
    private SupportTicket ticket2;
    private SupportTicket ticket3;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test data for SupportTicketRepositoryTest");

        // Create test users
        user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword("password123");
        user1.setPhone("1234567890");
        user1.setAddress("123 Main St");
        user1.setRole(Role.USER);
        
        user2 = new User();
        user2.setName("Jane Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword("password456");
        user2.setPhone("9876543210");
        user2.setAddress("456 Elm St");
        user2.setRole(Role.USER);

        // Persist users to get IDs
        user1 = entityManager.persist(user1);
        user2 = entityManager.persist(user2);
        entityManager.flush();

        // Create test support tickets
        LocalDateTime now = LocalDateTime.now();

        ticket1 = new SupportTicket();
        ticket1.setUser(user1);
        ticket1.setSubject("Policy Question");
        ticket1.setDescription("I have a question about my policy coverage");
        ticket1.setStatus(SupportTicketStatus.OPEN);
        ticket1.setCreatedAt(now.minusDays(5));

        ticket2 = new SupportTicket();
        ticket2.setUser(user1);
        ticket2.setSubject("Claim Issue");
        ticket2.setDescription("My claim was rejected without explanation");
        ticket2.setStatus(SupportTicketStatus.OPEN);
        ticket2.setCreatedAt(now.minusDays(3));
        ticket2.setResponse("We are looking into this issue");

        ticket3 = new SupportTicket();
        ticket3.setUser(user2);
        ticket3.setSubject("Website Error");
        ticket3.setDescription("I can't access my account dashboard");
        ticket3.setStatus(SupportTicketStatus.OPEN);
        ticket3.setCreatedAt(now.minusDays(1));

        // Persist tickets
        entityManager.persist(ticket1);
        entityManager.persist(ticket2);
        entityManager.persist(ticket3);
        entityManager.flush();

        logger.info("Test data setup completed");
    }

    @Test
    @DisplayName("Should find all support tickets")
    void shouldFindAllSupportTickets() {
        logger.info("Running test: shouldFindAllSupportTickets");

        List<SupportTicket> tickets = supportTicketRepository.findAll();

        assertNotNull(tickets);
        assertEquals(3, tickets.size());

        logger.info("Test passed: shouldFindAllSupportTickets");
    }

    @Test
    @DisplayName("Should find tickets by user ID")
    void shouldFindTicketsByUserId() {
        logger.info("Running test: shouldFindTicketsByUserId");

        List<SupportTicket> user1Tickets = supportTicketRepository.findByUserId(user1.getId());
        List<SupportTicket> user2Tickets = supportTicketRepository.findByUserId(user2.getId());

        assertNotNull(user1Tickets);
        assertEquals(2, user1Tickets.size());
        assertTrue(user1Tickets.stream().allMatch(ticket -> ticket.getUser().getId().equals(user1.getId())));

        assertNotNull(user2Tickets);
        assertEquals(1, user2Tickets.size());
        assertTrue(user2Tickets.stream().allMatch(ticket -> ticket.getUser().getId().equals(user2.getId())));

        logger.info("Test passed: shouldFindTicketsByUserId");
    }

    @Test
    @DisplayName("Should return empty list when no tickets exist for user")
    void shouldReturnEmptyListWhenNoTicketsExistForUser() {
        logger.info("Running test: shouldReturnEmptyListWhenNoTicketsExistForUser");

        // Create a user with no tickets
        User user3 = new User();
        user3.setName("Test User");
        user3.setEmail("test.user@example.com");
        user3.setPassword("password789");
        user3.setRole(Role.USER);
        
        user3 = entityManager.persist(user3);
        entityManager.flush();

        List<SupportTicket> user3Tickets = supportTicketRepository.findByUserId(user3.getId());

        assertNotNull(user3Tickets);
        assertTrue(user3Tickets.isEmpty());

        logger.info("Test passed: shouldReturnEmptyListWhenNoTicketsExistForUser");
    }

    @Test
    @DisplayName("Should save new support ticket")
    void shouldSaveNewSupportTicket() {
        logger.info("Running test: shouldSaveNewSupportTicket");

        SupportTicket newTicket = new SupportTicket();
        newTicket.setUser(user2);
        newTicket.setSubject("Billing Question");
        newTicket.setDescription("I was charged twice for my premium");
        newTicket.setStatus(SupportTicketStatus.OPEN);
        newTicket.setCreatedAt(LocalDateTime.now());

        SupportTicket savedTicket = supportTicketRepository.save(newTicket);
        
        assertNotNull(savedTicket.getId());
        assertEquals("Billing Question", savedTicket.getSubject());
        assertEquals(user2.getId(), savedTicket.getUser().getId());

        // Verify it can be retrieved from the repository
        List<SupportTicket> user2Tickets = supportTicketRepository.findByUserId(user2.getId());
        assertEquals(2, user2Tickets.size());

        logger.info("Test passed: shouldSaveNewSupportTicket");
    }

    @Test
    @DisplayName("Should find support ticket by ID")
    void shouldFindSupportTicketById() {
        logger.info("Running test: shouldFindSupportTicketById");

        SupportTicket foundTicket = supportTicketRepository.findById(ticket1.getId()).orElse(null);
        
        assertNotNull(foundTicket);
        assertEquals("Policy Question", foundTicket.getSubject());
        assertEquals(user1.getId(), foundTicket.getUser().getId());

        logger.info("Test passed: shouldFindSupportTicketById");
    }

    @Test
    @DisplayName("Should update support ticket")
    void shouldUpdateSupportTicket() {
        logger.info("Running test: shouldUpdateSupportTicket");

        SupportTicket ticketToUpdate = supportTicketRepository.findById(ticket2.getId()).orElse(null);
        assertNotNull(ticketToUpdate);
        
        ticketToUpdate.setStatus(SupportTicketStatus.RESOLVED);
        ticketToUpdate.setResponse("Issue has been resolved. Your claim is now approved.");
        ticketToUpdate.setResolvedAt(LocalDateTime.now());
        
        SupportTicket updatedTicket = supportTicketRepository.save(ticketToUpdate);
        
        assertEquals(SupportTicketStatus.RESOLVED, updatedTicket.getStatus());
        assertEquals("Issue has been resolved. Your claim is now approved.", updatedTicket.getResponse());
        assertNotNull(updatedTicket.getResolvedAt());

        logger.info("Test passed: shouldUpdateSupportTicket");
    }

    @Test
    @DisplayName("Should delete support ticket")
    void shouldDeleteSupportTicket() {
        logger.info("Running test: shouldDeleteSupportTicket");

        long initialCount = supportTicketRepository.count();
        supportTicketRepository.deleteById(ticket3.getId());
        
        long afterDeleteCount = supportTicketRepository.count();
        assertEquals(initialCount - 1, afterDeleteCount);
        
        assertFalse(supportTicketRepository.existsById(ticket3.getId()));

        logger.info("Test passed: shouldDeleteSupportTicket");
    }
}
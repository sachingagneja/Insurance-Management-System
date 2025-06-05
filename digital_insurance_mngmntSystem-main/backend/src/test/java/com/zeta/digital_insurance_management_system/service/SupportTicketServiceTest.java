package com.zeta.digital_insurance_management_system.service;

import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions.TicketAlreadyClosedException;
import com.zeta.digital_insurance_management_system.exception.SupportTicketExceptions.TicketNotFoundException;
import com.zeta.digital_insurance_management_system.model.SupportTicket;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.repository.SupportTicketRepository;
import com.zeta.digital_insurance_management_system.service.supportTicket.SupportTicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @InjectMocks
    private SupportTicketService supportTicketService;

    private User testUser;
    private Policy testPolicy;
    private Claim testClaim;
    private SupportTicket testTicket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testPolicy = new Policy();
        testPolicy.setId(1L);

        testClaim = new Claim();
        testClaim.setId(1L);

        testTicket = new SupportTicket();
        testTicket.setId(1L);
        testTicket.setUser(testUser);
        testTicket.setPolicy(testPolicy);
        testTicket.setClaim(testClaim);
        testTicket.setSubject("Test Subject");
        testTicket.setDescription("Test Description");
        testTicket.setStatus(SupportTicketStatus.OPEN);
        testTicket.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create Ticket Tests")
    class CreateTicketTests {

        @Test
        @DisplayName("Should create ticket with default status and timestamp")
        void createTicket_shouldSetDefaultsAndSave() {
            // Arrange
            SupportTicket inputTicket = new SupportTicket();
            inputTicket.setUser(testUser);
            inputTicket.setSubject("New Ticket");
            inputTicket.setDescription("Test Description");

            when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SupportTicket saved = supportTicketService.createTicket(inputTicket);

            // Assert
            assertNotNull(saved);
            assertEquals(SupportTicketStatus.OPEN, saved.getStatus());
            assertNotNull(saved.getCreatedAt());
            assertEquals("New Ticket", saved.getSubject());
            assertEquals("Test Description", saved.getDescription());
            assertEquals(testUser, saved.getUser());
            verify(supportTicketRepository, times(1)).save(any(SupportTicket.class));
        }

        @Test
        @DisplayName("Should preserve user and policy data when creating ticket")
        void createTicket_shouldPreserveUserAndPolicyData() {
            // Arrange
            SupportTicket ticket = new SupportTicket();
            ticket.setUser(testUser);
            ticket.setPolicy(testPolicy);
            ticket.setSubject("Policy Related Issue");
            ticket.setDescription("Issue with my policy");

            when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            SupportTicket saved = supportTicketService.createTicket(ticket);

            // Assert
            assertEquals(testUser, saved.getUser());
            assertEquals(testPolicy, saved.getPolicy());
            assertEquals("Policy Related Issue", saved.getSubject());
            assertEquals("Issue with my policy", saved.getDescription());
            assertEquals(SupportTicketStatus.OPEN, saved.getStatus());
        }

        @Test
        @DisplayName("Should handle null input gracefully")
        void createTicket_shouldHandleNullTicket() {
            // This test assumes the service should validate input
            // If null handling is expected, add appropriate validation in service
            assertThrows(NullPointerException.class, () -> {
                supportTicketService.createTicket(null);
            });
        }

    }

    @Nested
    @DisplayName("Update Ticket Tests")
    class UpdateTicketTests {

        @Test
        @DisplayName("Should update ticket and set resolved timestamp for RESOLVED status")
        void updateTicket_shouldUpdateAndSave() {
            // Arrange
            SupportTicket ticket = new SupportTicket();
            ticket.setId(1L);
            ticket.setStatus(SupportTicketStatus.OPEN);

            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);

            // Act
            SupportTicket result = supportTicketService.updateTicket(1L, "Test response", SupportTicketStatus.RESOLVED);

            // Assert
            assertEquals("Test response", result.getResponse());
            assertEquals(SupportTicketStatus.RESOLVED, result.getStatus());
            assertNotNull(result.getResolvedAt());
            verify(supportTicketRepository, times(1)).findById(1L);
            verify(supportTicketRepository, times(1)).save(ticket);
        }

        @Test
        @DisplayName("Should set resolved timestamp when status is CLOSED")
        void updateTicket_shouldSetResolvedAtWhenStatusIsClosed() {
            // Arrange
            SupportTicket ticket = new SupportTicket();
            ticket.setId(1L);
            ticket.setStatus(SupportTicketStatus.OPEN);

            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);

            // Act
            SupportTicket result = supportTicketService.updateTicket(1L, "Closing ticket", SupportTicketStatus.CLOSED);

            // Assert
            assertEquals("Closing ticket", result.getResponse());
            assertEquals(SupportTicketStatus.CLOSED, result.getStatus());
            assertNotNull(result.getResolvedAt());
        }

        @Test
        @DisplayName("Should throw TicketNotFoundException when ticket not found")
        void updateTicket_shouldThrowNotFound() {
            // Arrange
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            TicketNotFoundException exception = assertThrows(TicketNotFoundException.class,
                    () -> supportTicketService.updateTicket(1L, "response", SupportTicketStatus.RESOLVED));

            verify(supportTicketRepository, times(1)).findById(1L);
            verify(supportTicketRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw TicketAlreadyClosedException when updating closed ticket")
        void updateTicket_shouldThrowAlreadyClosedException() {
            // Arrange
            SupportTicket ticket = new SupportTicket();
            ticket.setId(1L);
            ticket.setStatus(SupportTicketStatus.CLOSED);
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            // Act & Assert
            TicketAlreadyClosedException exception = assertThrows(TicketAlreadyClosedException.class,
                    () -> supportTicketService.updateTicket(1L, "response", SupportTicketStatus.RESOLVED));

            verify(supportTicketRepository, times(1)).findById(1L);
            verify(supportTicketRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("Get Ticket Tests")
    class GetTicketTests {

        @Test
        @DisplayName("Should return ticket when found by ID")
        void getTicketById_shouldReturnTicket() {
            // Arrange
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

            // Act
            SupportTicket result = supportTicketService.getTicketById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(testTicket, result);
            assertEquals(1L, result.getId());
            assertEquals("Test Subject", result.getSubject());
            verify(supportTicketRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw TicketNotFoundException when ticket not found")
        void getTicketById_shouldThrowNotFound() {
            // Arrange
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            TicketNotFoundException exception = assertThrows(TicketNotFoundException.class,
                    () -> supportTicketService.getTicketById(1L));

            verify(supportTicketRepository, times(1)).findById(1L);
        }
    }


    @Nested
    @DisplayName("Get All Tickets Tests")
    class GetAllTicketsTests {

        @Test
        @DisplayName("Should return all tickets")
        void getAllTickets_shouldReturnAllTickets() {
            // Arrange
            SupportTicket ticket2 = new SupportTicket();
            ticket2.setId(2L);
            List<SupportTicket> allTickets = Arrays.asList(testTicket, ticket2);
            when(supportTicketRepository.findAll()).thenReturn(allTickets);

            // Act
            List<SupportTicket> result = supportTicketService.getAllTickets();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            assertTrue(result.contains(testTicket));
            assertTrue(result.contains(ticket2));
            verify(supportTicketRepository, times(1)).findAll();
        }

    }

    @Nested
    @DisplayName("Delete Ticket Tests")
    class DeleteTicketTests {

        @Test
        @DisplayName("Should delete ticket successfully when found")
        void deleteTicket_shouldDeleteSuccessfully() {
            // Arrange
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

            // Act
            supportTicketService.deleteTicket(1L);

            // Assert
            verify(supportTicketRepository, times(1)).findById(1L);
            verify(supportTicketRepository, times(1)).delete(testTicket);
        }

        @Test
        @DisplayName("Should throw TicketNotFoundException when ticket not found for deletion")
        void deleteTicket_shouldThrowNotFound() {
            // Arrange
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            TicketNotFoundException exception = assertThrows(TicketNotFoundException.class,
                    () -> supportTicketService.deleteTicket(1L));

            verify(supportTicketRepository, times(1)).findById(1L);
            verify(supportTicketRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should allow OPEN → RESOLVED transition")
        void updateTicket_shouldAllowOpenToResolved() {
            SupportTicket ticket = new SupportTicket();
            ticket.setId(1L);
            ticket.setStatus(SupportTicketStatus.OPEN);

            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(supportTicketRepository.save(any())).thenReturn(ticket);

            SupportTicket result = supportTicketService.updateTicket(1L, "Resolved", SupportTicketStatus.RESOLVED);

            assertEquals(SupportTicketStatus.RESOLVED, result.getStatus());
            assertNotNull(result.getResolvedAt());
        }


        @Test
        @DisplayName("Should enforce business rule: CLOSED tickets cannot be reopened")
        void updateTicket_shouldNotAllowClosedTicketReopening() {
            // Arrange
            SupportTicket closedTicket = new SupportTicket();
            closedTicket.setId(1L);
            closedTicket.setStatus(SupportTicketStatus.CLOSED);
            closedTicket.setResolvedAt(LocalDateTime.now().minusDays(1));
            
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(closedTicket));

            // Act & Assert
            assertThrows(TicketAlreadyClosedException.class,
                () -> supportTicketService.updateTicket(1L, "Reopen request", SupportTicketStatus.OPEN));
        }

        @Test
        @DisplayName("Should auto-set timestamps based on status transitions")
        void updateTicket_shouldManageTimestampsBasedOnStatus() {
            // Test OPEN -> RESOLVED
            SupportTicket ticket = new SupportTicket();
            ticket.setId(1L);
            ticket.setStatus(SupportTicketStatus.OPEN);
            ticket.setCreatedAt(LocalDateTime.now().minusHours(2));
            
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
            when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(ticket);

            // Act
            LocalDateTime beforeUpdate = LocalDateTime.now();
            SupportTicket resolved = supportTicketService.updateTicket(1L, "Fixed", SupportTicketStatus.RESOLVED);
            LocalDateTime afterUpdate = LocalDateTime.now();

            // Assert
            assertNotNull(resolved.getResolvedAt());
            assertTrue(resolved.getResolvedAt().isAfter(beforeUpdate) || resolved.getResolvedAt().isEqual(beforeUpdate));
            assertTrue(resolved.getResolvedAt().isBefore(afterUpdate) || resolved.getResolvedAt().isEqual(afterUpdate));
        }

        @Test
        @DisplayName("Should validate ticket lifecycle: OPEN -> RESOLVED -> CLOSED")
        void updateTicket_shouldFollowProperLifecycle() {
            // Test RESOLVED -> CLOSED transition
            SupportTicket resolvedTicket = new SupportTicket();
            resolvedTicket.setId(1L);
            resolvedTicket.setStatus(SupportTicketStatus.RESOLVED);
            resolvedTicket.setResolvedAt(LocalDateTime.now().minusHours(1));
            
            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(resolvedTicket));
            when(supportTicketRepository.save(any(SupportTicket.class))).thenReturn(resolvedTicket);

            // Act
            SupportTicket closed = supportTicketService.updateTicket(1L, "Customer confirmed", SupportTicketStatus.CLOSED);

            // Assert
            assertEquals(SupportTicketStatus.CLOSED, closed.getStatus());
            assertNotNull(closed.getResolvedAt()); // Should keep original resolved time
        }

        @Test
        @DisplayName("Should reject RESOLVED → RESOLVED update as invalid")
        void updateTicket_shouldRejectRedundantResolvedUpdate() {
            // Arrange
            SupportTicket ticket = new SupportTicket();
            ticket.setId(1L);
            ticket.setStatus(SupportTicketStatus.RESOLVED);

            when(supportTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));

            // Act & Assert
            assertThrows(
                    SupportTicketExceptions.InvalidTicketTransitionException.class,
                    () -> supportTicketService.updateTicket(1L, "Updated response", SupportTicketStatus.RESOLVED)
            );
        }

    }
//
//    @Nested
//    @DisplayName("Data Integrity Tests")
//    class DataIntegrityTests {
//
//        @Test
//        @DisplayName("Should maintain referential integrity with User")
//        void createTicket_shouldMaintainUserReference() {
//            // Arrange
//            SupportTicket ticket = new SupportTicket();
//            ticket.setUser(testUser);
//            ticket.setSubject("User Reference Test");
//
//            when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // Act
//            SupportTicket saved = supportTicketService.createTicket(ticket);
//
//            // Assert
//            assertNotNull(saved.getUser());
//            assertEquals(testUser.getId(), saved.getUser().getId());
//            assertEquals(testUser.getName(), saved.getUser().getName());
//            assertEquals(testUser.getEmail(), saved.getUser().getEmail());
//        }
//
//        @Test
//        @DisplayName("Should handle orphaned tickets when user reference is lost")
//        void getTicketsByUserId_shouldHandleOrphanedTickets() {
//            // This tests the service behavior when tickets exist but user references might be inconsistent
//            // Arrange
//            SupportTicket orphanedTicket = new SupportTicket();
//            orphanedTicket.setId(1L);
//            orphanedTicket.setUser(null); // Simulating data inconsistency
//            orphanedTicket.setSubject("Orphaned Ticket");
//
//            when(supportTicketRepository.findByUserId(999L)).thenReturn(Arrays.asList(orphanedTicket));
//
//            // Act
//            List<SupportTicket> result = supportTicketService.getTicketsByUserId(999L);
//
//            // Assert
//            assertFalse(result.isEmpty());
//            assertEquals(1, result.size());
//            // The service should still return the ticket even if user reference is null
//        }
//
//        @Test
//        @DisplayName("Should handle tickets with partial policy/claim data")
//        void createTicket_shouldHandlePartialAssociations() {
//            // Test creating ticket with policy but no claim
//            SupportTicket policyOnlyTicket = new SupportTicket();
//            policyOnlyTicket.setUser(testUser);
//            policyOnlyTicket.setPolicy(testPolicy);
//            policyOnlyTicket.setClaim(null);
//            policyOnlyTicket.setSubject("Policy Question");
//
//            when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//            // Act
//            SupportTicket saved = supportTicketService.createTicket(policyOnlyTicket);
//
//            // Assert
//            assertNotNull(saved.getPolicy());
//            assertNull(saved.getClaim());
//            assertEquals(SupportTicketStatus.OPEN, saved.getStatus());
//        }
//    }
}
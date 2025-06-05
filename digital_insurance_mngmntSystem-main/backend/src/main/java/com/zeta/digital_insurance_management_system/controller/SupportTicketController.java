package com.zeta.digital_insurance_management_system.controller;

import com.zeta.digital_insurance_management_system.dto.support.CreateSupportTicketRequest;
import com.zeta.digital_insurance_management_system.dto.support.SupportTicketResponse;
import com.zeta.digital_insurance_management_system.dto.support.UpdateSupportTicketRequest;
import com.zeta.digital_insurance_management_system.model.Claim;
import com.zeta.digital_insurance_management_system.model.SupportTicket;
import com.zeta.digital_insurance_management_system.model.User;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.service.ClaimManagement.ClaimManagementService;
import com.zeta.digital_insurance_management_system.service.policy.PolicyService;
import com.zeta.digital_insurance_management_system.service.supportTicket.ISupportTicketService;
import com.zeta.digital_insurance_management_system.service.user.UserServiceImpl;
import com.zeta.digital_insurance_management_system.repository.ClaimManagementRepository;
import com.zeta.digital_insurance_management_system.repository.UserPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/support")
public class SupportTicketController {

    private static final Logger logger = LoggerFactory.getLogger(SupportTicketController.class);

    @Autowired
    private ISupportTicketService supportTicketService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired private PolicyService policyService;
    @Autowired private ClaimManagementService claimService;
    @Autowired private UserPolicyRepository userPolicyRepository;
    @Autowired private ClaimManagementRepository claimRepository;

    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(@RequestBody CreateSupportTicketRequest request) {
        Long userId = userService.getCurrentUserId();
        logger.info("Creating support ticket for user ID: {} with subject: {}", userId, request.getSubject());
        User user = userService.getUserById(userId);

        SupportTicket ticket = new SupportTicket();
        ticket.setUser(user);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());

        if (request.getPolicyId() != null) {
            if (!userPolicyRepository.existsByUserIdAndPolicyId(userId, request.getPolicyId())) {
                logger.warn("User {} attempted to create ticket for policy {} not belonging to them or not found.", userId, request.getPolicyId());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Policy not found or doesn't belong to user");
            }
            Policy policy = policyService.getPolicyById(request.getPolicyId());
            ticket.setPolicy(policy);
            logger.info("Associated policy ID {} with the support ticket.", request.getPolicyId());
        }

        if (request.getClaimId() != null) {
            if (!claimRepository.existsByIdAndUserPolicy_UserId(request.getClaimId(), userId)) {
                logger.warn("User {} attempted to create ticket for claim {} not belonging to them or not found.", userId, request.getClaimId());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Claim not found or doesn't belong to user");
            }
            Claim claim = claimService.getClaimById(request.getClaimId());
            ticket.setClaim(claim);
            logger.info("Associated claim ID {} with the support ticket.", request.getClaimId());
        }

        SupportTicket created = supportTicketService.createTicket(ticket);
        SupportTicketResponse response = new SupportTicketResponse(created);
        logger.info("Successfully created support ticket with ID: {}", created.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<SupportTicketResponse>> getTicketsByUser() {
        Long userId = userService.getCurrentUserId();
        logger.info("Fetching support tickets for user ID: {}", userId);
        List<SupportTicket> tickets = supportTicketService.getTicketsByUserId(userId);
        List<SupportTicketResponse> responses = tickets.stream()
                .map(SupportTicketResponse::new)
                .collect(Collectors.toList());
        logger.info("Found {} support tickets for user ID: {}", responses.size(), userId);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponse> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody UpdateSupportTicketRequest request
    ) {
        logger.info("Admin updating support ticket ID: {} with status: {} and response: {}", ticketId, request.getStatus(), request.getResponse());
        SupportTicket updated = supportTicketService.updateTicket(ticketId, request.getResponse(), request.getStatus());
        SupportTicketResponse response = new SupportTicketResponse(updated);
        logger.info("Successfully updated support ticket ID: {}", ticketId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<SupportTicketResponse>> getAllTickets() {
        logger.info("Admin fetching all support tickets.");
        List<SupportTicket> tickets = supportTicketService.getAllTickets();
        System.out.println(tickets);
        List<SupportTicketResponse> responses = tickets.stream()
                .map(SupportTicketResponse::new)
                .collect(Collectors.toList());
        logger.info("Found {} total support tickets.", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        logger.info("Admin deleting support ticket ID: {}", ticketId);
        supportTicketService.deleteTicket(ticketId);
        logger.info("Successfully deleted support ticket ID: {}", ticketId);
        return ResponseEntity.noContent().build();
    }
}

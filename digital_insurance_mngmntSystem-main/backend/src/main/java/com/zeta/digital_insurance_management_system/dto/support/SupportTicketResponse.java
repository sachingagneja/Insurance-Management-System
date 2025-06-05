package com.zeta.digital_insurance_management_system.dto.support;

import com.zeta.digital_insurance_management_system.dto.Claim.UserClaimDTO;
import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import com.zeta.digital_insurance_management_system.model.Policy;
import com.zeta.digital_insurance_management_system.model.SupportTicket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicketResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Policy policy;
    private UserClaimDTO claim;
    private String subject;
    private String description;
    private SupportTicketStatus status;
    private String response;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public SupportTicketResponse(SupportTicket ticket) {
        this.id = ticket.getId();
        this.userId = ticket.getUser().getId();
        this.userFullName = ticket.getUser().getName();
        this.policy = ticket.getPolicy();

        this.claim = ticket.getClaim() != null ? new UserClaimDTO(ticket.getClaim()) : null;

        this.subject = ticket.getSubject();
        this.description = ticket.getDescription();
        this.status = ticket.getStatus();
        this.response = ticket.getResponse();
        this.createdAt = ticket.getCreatedAt();
        this.resolvedAt = ticket.getResolvedAt();
    }
}
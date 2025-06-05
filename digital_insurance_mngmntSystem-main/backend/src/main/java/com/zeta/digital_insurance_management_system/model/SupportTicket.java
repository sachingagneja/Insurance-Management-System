package com.zeta.digital_insurance_management_system.model;

import com.zeta.digital_insurance_management_system.enums.SupportTicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = true)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @ManyToOne(optional = true)
    @JoinColumn(name = "claim_id")
    private Claim claim;

    private String subject;
    private String description;

    @Enumerated(EnumType.STRING)
    private SupportTicketStatus status;
    private String response;

    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}

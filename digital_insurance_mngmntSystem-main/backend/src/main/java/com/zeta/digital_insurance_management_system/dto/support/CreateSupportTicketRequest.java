package com.zeta.digital_insurance_management_system.dto.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSupportTicketRequest {
    private Long policyId;
    private Long claimId;
    private String subject;
    private String description;
}
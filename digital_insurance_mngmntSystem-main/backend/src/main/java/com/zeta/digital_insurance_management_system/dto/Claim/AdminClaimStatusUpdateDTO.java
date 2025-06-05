package com.zeta.digital_insurance_management_system.dto.Claim;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Data
public class AdminClaimStatusUpdateDTO {
    private String status;
    private String reviewerComment;
}

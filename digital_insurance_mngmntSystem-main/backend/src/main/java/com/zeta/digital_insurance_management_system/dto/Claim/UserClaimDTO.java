package com.zeta.digital_insurance_management_system.dto.Claim;

import com.zeta.digital_insurance_management_system.model.Claim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor @NoArgsConstructor
@Data
public class UserClaimDTO {
    private Long userPolicyId;
    private BigDecimal claimAmount;
    private String reason;

    public UserClaimDTO(Claim claim) {
        this(
                claim.getUserPolicy() != null ? claim.getUserPolicy().getId() : null,
                claim.getClaimAmount(),
                claim.getReason()
        );
    }
}

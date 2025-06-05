package com.zeta.digital_insurance_management_system.dto.renew;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenewablePolicy {
    private Long userPolicyId;
    private String policyName;
    private LocalDate endDate;
    private BigDecimal renewalRate;
    private BigDecimal renewalPremiumRate;
}
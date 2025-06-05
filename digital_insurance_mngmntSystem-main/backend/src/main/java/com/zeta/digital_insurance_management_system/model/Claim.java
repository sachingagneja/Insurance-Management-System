package com.zeta.digital_insurance_management_system.model;


import com.zeta.digital_insurance_management_system.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claim {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_policy_id")
    private UserPolicy userPolicy;

    private LocalDate claimDate;

    private BigDecimal claimAmount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private String reviewerComment;

    private LocalDate resolvedDate;

}


package com.zeta.digital_insurance_management_system.model;

import com.zeta.digital_insurance_management_system.enums.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal premiumAmount;
    private BigDecimal coverageAmount;
    private Integer durationMonths;
    private BigDecimal renewalPremiumRate;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Category category;
}

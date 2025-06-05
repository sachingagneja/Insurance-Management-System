package com.zeta.digital_insurance_management_system.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UserDTO user;
}

package com.zeta.digital_insurance_management_system.dto.auth;

import com.zeta.digital_insurance_management_system.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
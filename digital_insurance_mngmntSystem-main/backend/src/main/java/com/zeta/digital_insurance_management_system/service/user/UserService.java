package com.zeta.digital_insurance_management_system.service.user;

import com.zeta.digital_insurance_management_system.model.User;

import java.util.List;

public interface UserService {
    User register(User user);
    String login(User user);
    List<User> getAllUsers();
    User getUserById(long id);
}

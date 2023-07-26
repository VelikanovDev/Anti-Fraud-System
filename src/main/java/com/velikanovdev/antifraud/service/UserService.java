package com.velikanovdev.antifraud.service;

import com.velikanovdev.antifraud.dto.OperationDTO;
import com.velikanovdev.antifraud.dto.RoleDTO;
import com.velikanovdev.antifraud.dto.UserDTO;
import com.velikanovdev.antifraud.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    ResponseEntity<User> registerUser(UserDTO userDTO);
    Optional<User> getUserByUsername(String username);
    ResponseEntity<?> showAllUsers();
    ResponseEntity<?> deleteUser(String username);
    ResponseEntity<?> setUserRole(RoleDTO roleDTO);
    ResponseEntity<?> setAccountAccess(OperationDTO operationDTO);
}

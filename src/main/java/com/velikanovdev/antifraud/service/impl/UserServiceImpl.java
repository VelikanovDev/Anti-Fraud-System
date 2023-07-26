package com.velikanovdev.antifraud.service.impl;

import com.velikanovdev.antifraud.dto.OperationDTO;
import com.velikanovdev.antifraud.dto.RoleDTO;
import com.velikanovdev.antifraud.dto.UserDTO;
import com.velikanovdev.antifraud.entity.User;
import com.velikanovdev.antifraud.enums.Role;
import com.velikanovdev.antifraud.enums.UserAccess;
import com.velikanovdev.antifraud.repository.UserRepository;
import com.velikanovdev.antifraud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<User> registerUser(UserDTO userDTO) {
        User user = convertToUser(userDTO);

        if (getUserByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        setUserRoleAndEnabled(user);

        // Save the user in the database
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public ResponseEntity<?> showAllUsers() {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(users);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);

        if (userOptional.isPresent()) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("username", username);
            responseBody.put("status", "Deleted successfully!");
            userRepository.delete(userOptional.get());
            return ResponseEntity.ok(responseBody);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Load user details from the database based on the username
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user is locked or unlocked
        boolean accountNonLocked = user.isEnabled(); // Assuming that "isEnabled" indicates the locked/unlocked status

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .accountLocked(!accountNonLocked) // Set the accountLocked flag based on the user's enabled status
                .build();
    }


    @Override
    public ResponseEntity<?> setUserRole(RoleDTO roleDTO) {
        Optional<User> user = getUserByUsername(roleDTO.getUsername());

        if(user.isPresent() && user.get().getRole() == roleDTO.getRole()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else if(user.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else if(roleDTO.getRole() == Role.ADMINISTRATOR) {
            return ResponseEntity.badRequest().build();
        }

        User changedUser = user.get();

        changedUser.setRole(roleDTO.getRole());
        userRepository.save(changedUser);

        return ResponseEntity.ok(changedUser);
    }

    @Override
    public ResponseEntity<?> setAccountAccess(OperationDTO operationDTO) {
        String username = operationDTO.getUsername();
        UserAccess operation = operationDTO.getOperation();
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);

        if(userOptional.isPresent() && userOptional.get().getRole() != Role.ADMINISTRATOR) {
            User user = userOptional.get();
            if(operation == UserAccess.UNLOCK) {
                user.setEnabled(true);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("status", "User " + user.getUsername() + " unlocked!"));
            } else if(operation == UserAccess.LOCK) {
                user.setEnabled(false);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("status", "User " + user.getUsername() + " locked!"));
            }
        }
        return ResponseEntity.badRequest().build();
    }

    private User convertToUser(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return user;
    }

    private void setUserRoleAndEnabled(User user) {
        boolean isAdmin = userRepository.findAll().isEmpty();

        if (isAdmin) {
            user.setRole(Role.ADMINISTRATOR);
            user.setEnabled(true);
        } else {
            user.setRole(Role.MERCHANT);
            user.setEnabled(false);
        }
    }

}

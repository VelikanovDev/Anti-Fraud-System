package com.velikanovdev.antifraud.controller;

import com.velikanovdev.antifraud.dto.OperationDTO;
import com.velikanovdev.antifraud.dto.RoleDTO;
import com.velikanovdev.antifraud.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AdminController {
    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> showAllUsers() {
        return userService.showAllUsers();
    }

    @PutMapping("/role")
    @Transactional
    public ResponseEntity<?> setUserRole(@RequestBody @Valid RoleDTO roleDTO) {
        return userService.setUserRole(roleDTO);
    }

    @DeleteMapping("/user/{username}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return userService.deleteUser(username);
    }

    @PutMapping("/access")
    public ResponseEntity<?> setAccountAccess(@RequestBody @Valid OperationDTO operationDTO) {
        return userService.setAccountAccess(operationDTO);
    }

}

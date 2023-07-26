package com.velikanovdev.antifraud.controller;

import com.velikanovdev.antifraud.dto.UserDTO;
import com.velikanovdev.antifraud.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserDTO userDTO) {
        return userService.registerUser(userDTO);
    }

}

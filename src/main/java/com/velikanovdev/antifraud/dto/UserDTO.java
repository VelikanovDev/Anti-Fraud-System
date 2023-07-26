package com.velikanovdev.antifraud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull(message = "Name is required!")
    @NotBlank(message = "Name cannot be blank!")
    private String name;

    @NotNull(message = "Username is required!")
    @NotBlank(message = "Username is required and cannot be empty!")
    private String username;

    @NotNull(message = "Password is required!")
    @NotBlank(message = "Password cannot be blank!")
    private String password;
}

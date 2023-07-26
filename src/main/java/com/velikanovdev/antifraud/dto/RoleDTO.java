package com.velikanovdev.antifraud.dto;

import com.velikanovdev.antifraud.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    @NotBlank
    private String username;
    @NotNull
    private Role role;
}

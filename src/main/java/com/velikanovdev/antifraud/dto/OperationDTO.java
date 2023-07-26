package com.velikanovdev.antifraud.dto;

import com.velikanovdev.antifraud.enums.UserAccess;
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
public class OperationDTO {
    @NotBlank
    private String username;
    @NotNull
    private UserAccess operation;
}

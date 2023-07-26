package com.velikanovdev.antifraud.dto;

import com.velikanovdev.antifraud.enums.Region;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    @NotNull
    private Long amount;
    @NotBlank
    private String ip;
    @NotBlank
    private String number;
    @NotNull
    private Region region;
    @NotNull
    private LocalDateTime date;
}

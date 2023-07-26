package com.velikanovdev.antifraud.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.velikanovdev.antifraud.enums.Region;
import com.velikanovdev.antifraud.enums.TransactionStatus;
import jakarta.persistence.*;
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
@Entity
@Table(name = "transactionHistory")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull(message = "Amount must be not null")
    private Long amount;

    @NotBlank
    private String ip;

    @NotBlank
    private String number;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Region region;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @NotNull
    private TransactionStatus result;
}

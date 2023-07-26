package com.velikanovdev.antifraud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "suspiciousIps")
@AllArgsConstructor
@NoArgsConstructor
public class SuspiciousIP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String ip;
}

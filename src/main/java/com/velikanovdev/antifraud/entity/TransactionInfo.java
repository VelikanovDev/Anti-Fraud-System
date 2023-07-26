package com.velikanovdev.antifraud.entity;

import com.velikanovdev.antifraud.enums.TransactionStatus;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInfo {
    private TransactionStatus result;
    private String info;
}

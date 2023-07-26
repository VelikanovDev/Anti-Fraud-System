package com.velikanovdev.antifraud.service;

import com.velikanovdev.antifraud.dto.TransactionDTO;
import com.velikanovdev.antifraud.entity.Transaction;
import com.velikanovdev.antifraud.entity.TransactionInfo;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface TransactionService {
    ResponseEntity<TransactionInfo> checkTransactionValidity(TransactionDTO transactionDTO);
    ResponseEntity<List<Transaction>> showAllHistory();
    ResponseEntity<List<Transaction>> showCardHistory(String number);
}

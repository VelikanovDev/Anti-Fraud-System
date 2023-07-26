package com.velikanovdev.antifraud.controller;

import com.velikanovdev.antifraud.dto.TransactionDTO;
import com.velikanovdev.antifraud.entity.StolenCard;
import com.velikanovdev.antifraud.entity.SuspiciousIP;
import com.velikanovdev.antifraud.service.StolenCardService;
import com.velikanovdev.antifraud.service.SuspiciousIPService;
import com.velikanovdev.antifraud.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/antifraud")
public class AntiFraudController {
    private final TransactionService transactionService;
    private final SuspiciousIPService suspiciousIPService;
    private final StolenCardService stolenCardService;

    @Autowired
    public AntiFraudController(TransactionService transactionService, SuspiciousIPService suspiciousIPService,
                               StolenCardService stolenCardService) {
        this.transactionService = transactionService;
        this.suspiciousIPService = suspiciousIPService;
        this.stolenCardService = stolenCardService;
    }

    @PostMapping("/transaction")
    public ResponseEntity<?> prepareTransaction(@RequestBody @Valid TransactionDTO transactionDTO) {
        return transactionService.checkTransactionValidity(transactionDTO);
    }

    @PostMapping("/suspicious-ip")
    public ResponseEntity<SuspiciousIP> addSuspiciousIp(@RequestBody @Valid SuspiciousIP suspiciousIP) {
        return suspiciousIPService.addSuspiciousIP(suspiciousIP);
    }

    @DeleteMapping("/suspicious-ip/{ip}")
    public ResponseEntity<?> deleteSuspiciousIP(@PathVariable String ip) {
        return suspiciousIPService.deleteSuspiciousIP(ip);
    }

    @GetMapping("/suspicious-ip")
    public ResponseEntity<?> getAllSuspiciousIp() {
        return suspiciousIPService.getAllSuspiciousIp();
    }

    @PostMapping("/stolencard")
    public ResponseEntity<?> addStolenCard(@RequestBody @Valid StolenCard stolenCard) {
        return stolenCardService.addStolenCard(stolenCard);
    }

    @DeleteMapping("/stolencard/{number}")
    public ResponseEntity<?> deleteStolenCard(@PathVariable String number) {
        return stolenCardService.deleteStolenCard(number);
    }

    @GetMapping("/stolencard")
    public ResponseEntity<?> getAllStolenCards() {
        return stolenCardService.getAllStolenCards();
    }

    @GetMapping("/history")
    public ResponseEntity<?> showAllHistory() {
        return transactionService.showAllHistory();
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<?> showCardHistory(@PathVariable String number) {
        return transactionService.showCardHistory(number);
    }

}

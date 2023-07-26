package com.velikanovdev.antifraud.service.impl;

import com.velikanovdev.antifraud.dto.TransactionDTO;
import com.velikanovdev.antifraud.entity.Transaction;
import com.velikanovdev.antifraud.entity.TransactionInfo;
import com.velikanovdev.antifraud.enums.Region;
import com.velikanovdev.antifraud.enums.TransactionStatus;
import com.velikanovdev.antifraud.repository.TransactionRepository;
import com.velikanovdev.antifraud.service.StolenCardService;
import com.velikanovdev.antifraud.service.SuspiciousIPService;
import com.velikanovdev.antifraud.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final StolenCardService stolenCardService;
    private final SuspiciousIPService suspiciousIPService;
    private final TransactionRepository transactionRepository;
    private List<String> errors;
    private HttpStatus httpStatus;
    private TransactionStatus transactionStatus;

    @Autowired
    public TransactionServiceImpl(StolenCardService stolenCardService, SuspiciousIPService suspiciousIPService,
                                  TransactionRepository transactionRepository) {
        this.stolenCardService = stolenCardService;
        this.suspiciousIPService = suspiciousIPService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ResponseEntity<TransactionInfo> checkTransactionValidity(TransactionDTO transactionDTO) {
        Transaction transaction = convertToTransaction(transactionDTO);

        errors = new ArrayList<>();
        httpStatus = HttpStatus.OK;
        transactionStatus = TransactionStatus.ALLOWED;

        Long amount = transaction.getAmount();
        String cardNumber = transaction.getNumber();
        Region region = transaction.getRegion();
        String ip = transaction.getIp();

        if(isInvalidIP(ip) || isInvalidCardNumber(cardNumber)){
            return ResponseEntity.badRequest().build();
        }

        // Get distinct IP addresses and regions from transaction history in the last hour
        LocalDateTime startOfPeriod = transaction.getDate().minusHours(1);
        LocalDateTime endOfPeriod = transaction.getDate();

        List<String> distinctIPs = transactionRepository
                .getAllDistinctIpDuringPeriod(cardNumber, startOfPeriod, endOfPeriod)
                .stream()
                .filter(ipAddress -> !ipAddress.equals(ip))
                .toList();

        List<Region> distinctRegions = transactionRepository
                .getAllDistinctRegionDuringPeriod(cardNumber, startOfPeriod, endOfPeriod)
                .stream()
                .filter(reg -> !reg.equals(region))
                .toList();

        checkIPCorrelation(distinctIPs);
        checkRegionCorrelation(distinctRegions);
        checkIfAmountValid(amount);

        transaction.setResult(transactionStatus);

        transactionRepository.save(transaction);
        return new ResponseEntity<>(new TransactionInfo(transactionStatus, getErrorInfo()), httpStatus);
    }

    @Override
    public ResponseEntity<List<Transaction>>  showAllHistory() {
        return ResponseEntity.ok(transactionRepository.findAll());
    }

    @Override
    public ResponseEntity<List<Transaction>> showCardHistory(String number) {
        if(isInvalidCardNumber(number)) {
            return ResponseEntity.badRequest().build();
        }

        List<Transaction> allCardHistory = transactionRepository.findAllByNumber(number);

        return allCardHistory.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(allCardHistory);
    }

    private void checkIfAmountValid(Long amount) {
        if(amount == null || amount < 1) {
            errors.add("amount");
            httpStatus = HttpStatus.BAD_REQUEST;
            transactionStatus = TransactionStatus.PROHIBITED;
        } else if (amount > 1500 ) {
            errors.add("amount");
            transactionStatus = TransactionStatus.PROHIBITED;
        } else if (amount > 200 && transactionStatus != TransactionStatus.PROHIBITED) {
            errors.add("amount");
            transactionStatus = TransactionStatus.MANUAL_PROCESSING;
        }
    }

    private boolean isInvalidIP(String ip) {
        if (suspiciousIPService.isInvalidIp(ip)) {
            errors.add("ip");
            httpStatus = HttpStatus.BAD_REQUEST;
            transactionStatus = TransactionStatus.PROHIBITED;
            return true;
        } else if (suspiciousIPService.suspiciousIPIsPresent(ip)) {
            transactionStatus = TransactionStatus.PROHIBITED;
            errors.add("ip");
        }
        return false;
    }

    private boolean isInvalidCardNumber(String cardNumber) {
        if (stolenCardService.isCardInvalid(cardNumber)) {
            errors.add("card-number");
            httpStatus = HttpStatus.BAD_REQUEST;
            transactionStatus = TransactionStatus.PROHIBITED;
            return true;
        } else if (stolenCardService.stolenCardIsPresent(cardNumber)) {
            transactionStatus = TransactionStatus.PROHIBITED;
            errors.add("card-number");
        }
        return false;
    }

    private void checkRegionCorrelation(List<Region> distinctRegions) {
        int regionCount = distinctRegions.size();

        if (regionCount > 2) {
            errors.add("region-correlation");
            transactionStatus = TransactionStatus.PROHIBITED;
        } else if (regionCount == 2 && transactionStatus != TransactionStatus.PROHIBITED) {
            errors.add("region-correlation");
            transactionStatus = TransactionStatus.MANUAL_PROCESSING;
        }
    }

    private void checkIPCorrelation(List<String> distinctIPs) {
        int ipCount = distinctIPs.size();

        if (ipCount > 2) {
            errors.add("ip-correlation");
            transactionStatus = TransactionStatus.PROHIBITED;
        } else if (ipCount == 2 && transactionStatus != TransactionStatus.PROHIBITED) {
            errors.add("ip-correlation");
            transactionStatus = TransactionStatus.MANUAL_PROCESSING;
        }
    }

    private String getErrorInfo() {
        StringBuilder info = new StringBuilder();
        errors.sort((String::compareToIgnoreCase));
        if(errors.size() > 0) {
            info.append(errors.get(0));
            for (int i = 1; i < errors.size(); i++) {
                info.append(", ").append(errors.get(i));
            }
            return info.toString();
        }
        return "none";
    }

    private Transaction convertToTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setIp(transactionDTO.getIp());
        transaction.setNumber(transactionDTO.getNumber());
        transaction.setRegion(transactionDTO.getRegion());
        transaction.setDate(transactionDTO.getDate());
        return transaction;
    }

}

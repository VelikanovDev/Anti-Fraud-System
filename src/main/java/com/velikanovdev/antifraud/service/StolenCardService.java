package com.velikanovdev.antifraud.service;

import com.velikanovdev.antifraud.entity.StolenCard;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

public interface StolenCardService {
    ResponseEntity<StolenCard> addStolenCard(StolenCard stolenCard);
    ResponseEntity<Map<String, String>> deleteStolenCard(String number);
    ResponseEntity<List<StolenCard>> getAllStolenCards();
    boolean stolenCardIsPresent(String number);
    boolean isCardInvalid(String number);
}

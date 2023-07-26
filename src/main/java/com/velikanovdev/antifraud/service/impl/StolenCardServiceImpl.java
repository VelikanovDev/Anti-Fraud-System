package com.velikanovdev.antifraud.service.impl;

import com.velikanovdev.antifraud.entity.StolenCard;
import com.velikanovdev.antifraud.repository.StolenCardRepository;
import com.velikanovdev.antifraud.service.StolenCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StolenCardServiceImpl implements StolenCardService {
    private final StolenCardRepository stolenCardRepository;

    @Autowired
    public StolenCardServiceImpl(StolenCardRepository stolenCardRepository) {
        this.stolenCardRepository = stolenCardRepository;
    }

    @Override
    public ResponseEntity<StolenCard> addStolenCard(StolenCard stolenCard) {
        String number = stolenCard.getNumber();

        if(isCardInvalid(number)) {
            return ResponseEntity.badRequest().build();
        }

        if(stolenCardRepository.findStolenCardByNumber(number).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        stolenCardRepository.save(stolenCard);

        return ResponseEntity.ok(stolenCard);
    }

    @Override
    public ResponseEntity<Map<String, String>> deleteStolenCard(String number) {

        if(isCardInvalid(number)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<StolenCard> stolenCardOptional = stolenCardRepository.findStolenCardByNumber(number);

        if(stolenCardOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        stolenCardRepository.deleteById(stolenCardOptional.get().getId());

        return ResponseEntity.ok(Map.of("status", "Card " + number + " successfully removed!"));
    }

    @Override
    public ResponseEntity<List<StolenCard>> getAllStolenCards() {
        return ResponseEntity.ok(stolenCardRepository.findAll());
    }

    @Override
    public boolean stolenCardIsPresent(String number) {
        Optional<StolenCard> stolenCard = stolenCardRepository.findStolenCardByNumber(number);
        return stolenCard.isPresent();
    }

    @Override
    public boolean isCardInvalid(String number) {
        if(number == null) return true;

        int[] array = convertToIntArray(number);
        int checksum = array[array.length - 1];
        array[array.length - 1] = 0;
        multiplyOddNumbersByTwo(array);
        subtractNineFromNumbersOverNine(array);
        int sum = addAllNumbers(array);
        return (sum + checksum) % 10 != 0;

    }

    private int addAllNumbers(int[] array) {
        int sum = 0;
        for (int i: array) {
            sum += i;
        }
        return sum;
    }

    private void subtractNineFromNumbersOverNine(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] > 9) {
                array[i] -= 9;
            }
        }
    }

    private void multiplyOddNumbersByTwo(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if ((i+1) % 2 != 0) {
                array[i] *= 2;
            }
        }
    }

    private int[] convertToIntArray(String number) {
        int[] convertedIntArray = new int[number.length()];
        for (int i = 0; i < number.length(); i++) {
            convertedIntArray[i] = number.charAt(i) - 48;
        }
        return convertedIntArray;
    }

}

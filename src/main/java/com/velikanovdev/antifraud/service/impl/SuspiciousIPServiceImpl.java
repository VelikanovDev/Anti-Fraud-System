package com.velikanovdev.antifraud.service.impl;

import com.velikanovdev.antifraud.entity.SuspiciousIP;
import com.velikanovdev.antifraud.repository.SuspiciousIPRepository;
import com.velikanovdev.antifraud.service.SuspiciousIPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SuspiciousIPServiceImpl implements SuspiciousIPService {
    private final SuspiciousIPRepository suspiciousIPRepository;

    @Autowired
    public SuspiciousIPServiceImpl(SuspiciousIPRepository suspiciousIPRepository) {
        this.suspiciousIPRepository = suspiciousIPRepository;
    }

    @Override
    public ResponseEntity<SuspiciousIP> addSuspiciousIP(SuspiciousIP suspiciousIP) {
        String ip = suspiciousIP.getIp();

        if(isInvalidIp(ip)) {
            return ResponseEntity.badRequest().build();
        }

        if(suspiciousIPRepository.findSuspiciousIPByIp(ip).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        suspiciousIPRepository.save(suspiciousIP);

        return ResponseEntity.ok(suspiciousIP);
    }

    @Override
    public ResponseEntity<Map<String,String>> deleteSuspiciousIP(String ip) {

        if(isInvalidIp(ip)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SuspiciousIP> suspiciousIPOptional = suspiciousIPRepository.findSuspiciousIPByIp(ip);

        if(suspiciousIPOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        suspiciousIPRepository.deleteById(suspiciousIPOptional.get().getId());

        return ResponseEntity.ok(Map.of("status", "IP " + ip + " successfully removed!"));

    }

    @Override
    public ResponseEntity<List<SuspiciousIP>> getAllSuspiciousIp() {
        return ResponseEntity.ok(suspiciousIPRepository.findAll());
    }

    @Override
    public boolean suspiciousIPIsPresent(String ip) {
        Optional<SuspiciousIP> suspiciousIP = suspiciousIPRepository.findSuspiciousIPByIp(ip);
        return suspiciousIP.isPresent();
    }

    @Override
    public boolean isInvalidIp(String ip) {
        if(ip == null) return true;

        String[] ipSplitByDot = ip.split("\\.");
        if (ipSplitByDot.length != 4) {
            return true;
        }
        try {
            for (String s : ipSplitByDot) {
                int i = Integer.parseInt(s);
                if (i < 0 || i > 255) {
                    return true;
                }
            }
        } catch (NumberFormatException nfe) {
            System.err.println(nfe.getMessage());
            return true;
        }
        return false;
    }

}

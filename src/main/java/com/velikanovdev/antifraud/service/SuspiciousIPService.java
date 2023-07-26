package com.velikanovdev.antifraud.service;

import com.velikanovdev.antifraud.entity.SuspiciousIP;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

public interface SuspiciousIPService {
    ResponseEntity<SuspiciousIP> addSuspiciousIP(SuspiciousIP suspiciousIP);
    ResponseEntity<Map<String,String>> deleteSuspiciousIP(String ip);
    ResponseEntity<List<SuspiciousIP>> getAllSuspiciousIp();
    boolean suspiciousIPIsPresent(String ip);
    boolean isInvalidIp(String ip);
}

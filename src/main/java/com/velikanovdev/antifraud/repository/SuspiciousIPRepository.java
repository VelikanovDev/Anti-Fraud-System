package com.velikanovdev.antifraud.repository;

import com.velikanovdev.antifraud.entity.SuspiciousIP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SuspiciousIPRepository extends JpaRepository<SuspiciousIP, Long> {
    Optional<SuspiciousIP> findSuspiciousIPByIp(String ip);
}

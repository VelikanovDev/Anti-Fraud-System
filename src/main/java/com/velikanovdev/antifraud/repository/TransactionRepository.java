package com.velikanovdev.antifraud.repository;

import com.velikanovdev.antifraud.entity.Transaction;
import com.velikanovdev.antifraud.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query(value = "SELECT DISTINCT t.ip FROM Transaction t WHERE t.number = :cardNumber AND t.date >= :startOfPeriod AND t.date <= :endOfPeriod")
    List<String> getAllDistinctIpDuringPeriod(@Param("cardNumber") String cardNumber, @Param("startOfPeriod") LocalDateTime startOfPeriod, @Param("endOfPeriod") LocalDateTime endOfPeriod);

    @Query(value = "SELECT DISTINCT t.region FROM Transaction t WHERE t.number = :cardNumber AND t.date >= :startOfPeriod AND t.date <= :endOfPeriod")
    List<Region> getAllDistinctRegionDuringPeriod(@Param("cardNumber") String cardNumber, @Param("startOfPeriod") LocalDateTime startOfPeriod, @Param("endOfPeriod") LocalDateTime endOfPeriod);

    List<Transaction> findAllByNumber(String number);
}

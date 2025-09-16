package com.financiasheet.demo.repository;

import com.financiasheet.demo.entity.Transaction;
import com.financiasheet.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByFingerprint(String fingerprint);
    Page<Transaction> findByUser(User user, Pageable pageable);
}

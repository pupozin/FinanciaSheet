package com.financiasheet.demo.repository;

import com.financiasheet.demo.entity.ImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, UUID> {
}

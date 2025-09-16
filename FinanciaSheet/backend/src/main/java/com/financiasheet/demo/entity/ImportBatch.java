package com.financiasheet.demo.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "import_batch")
public class ImportBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String kind;          // "EXTRATO" | "FATURA"

    @Column(nullable = false)
    private String source;        // "nubank_csv" | "xp_csv" | ...

    @Column(nullable = false)
    private Integer importedCount;

    @Column(nullable = false)
    private Integer duplicateCount;

    @Column(nullable = false)
    private Integer errorCount;

    @Column(nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public ImportBatch() {}

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getImportedCount() { return importedCount; }
    public void setImportedCount(Integer importedCount) { this.importedCount = importedCount; }

    public Integer getDuplicateCount() { return duplicateCount; }
    public void setDuplicateCount(Integer duplicateCount) { this.duplicateCount = duplicateCount; }

    public Integer getErrorCount() { return errorCount; }
    public void setErrorCount(Integer errorCount) { this.errorCount = errorCount; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

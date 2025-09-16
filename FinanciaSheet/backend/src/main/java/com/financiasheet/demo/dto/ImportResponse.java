package com.financiasheet.demo.dto;

import java.util.UUID;

public class ImportResponse {
    private UUID batchId;
    private int imported;
    private int duplicates;
    private int errors;

    public ImportResponse() {}

    public ImportResponse(UUID batchId, int imported, int duplicates, int errors) {
        this.batchId = batchId;
        this.imported = imported;
        this.duplicates = duplicates;
        this.errors = errors;
    }

    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }
    public int getImported() { return imported; }
    public void setImported(int imported) { this.imported = imported; }
    public int getDuplicates() { return duplicates; }
    public void setDuplicates(int duplicates) { this.duplicates = duplicates; }
    public int getErrors() { return errors; }
    public void setErrors(int errors) { this.errors = errors; }
}

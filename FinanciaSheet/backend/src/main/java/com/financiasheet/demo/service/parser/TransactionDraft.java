package com.financiasheet.demo.service.parser;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDraft {
    public LocalDate date;
    public BigDecimal amount;
    public String description;
    public String externalId;   // se existir
}

package com.financiasheet.demo.repository.proj;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyPointProj {
    LocalDate getD(); // alias "d" no SQL
    BigDecimal getReceived();
    BigDecimal getSpent();
    BigDecimal getBalance();
}

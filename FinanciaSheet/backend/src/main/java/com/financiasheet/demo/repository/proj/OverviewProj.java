package com.financiasheet.demo.repository.proj;

import java.math.BigDecimal;

public interface OverviewProj {
    BigDecimal getReceived();
    BigDecimal getSpent();
    BigDecimal getBalance();
    long getTxCount(); // alias tx_count no SQL
}

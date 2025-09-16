package com.financiasheet.demo.repository.proj;

import java.math.BigDecimal;

public interface MonthlyCashflowProj {
    String getMonth();
    BigDecimal getReceived();
    BigDecimal getSpent();
    BigDecimal getBalance();
}

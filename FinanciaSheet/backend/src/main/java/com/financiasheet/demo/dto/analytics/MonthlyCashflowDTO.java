package com.financiasheet.demo.dto.analytics;

import java.math.BigDecimal;

public record MonthlyCashflowDTO(String month, BigDecimal received, BigDecimal spent, BigDecimal balance) {}

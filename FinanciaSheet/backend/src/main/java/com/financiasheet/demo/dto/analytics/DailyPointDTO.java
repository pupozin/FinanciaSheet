package com.financiasheet.demo.dto.analytics;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyPointDTO(LocalDate date, BigDecimal received, BigDecimal spent, BigDecimal balance) {}

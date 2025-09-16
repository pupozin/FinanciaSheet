package com.financiasheet.demo.dto.analytics;

import java.math.BigDecimal;

public record OverviewDTO(BigDecimal received, BigDecimal spent, BigDecimal balance, long txCount) {}

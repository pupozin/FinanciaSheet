package com.financiasheet.demo.dto.analytics;

import java.math.BigDecimal;

public record MerchantTotalDTO(String description, BigDecimal total, long count) {}
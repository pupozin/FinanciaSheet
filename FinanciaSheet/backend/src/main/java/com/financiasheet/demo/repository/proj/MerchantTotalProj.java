package com.financiasheet.demo.repository.proj;

import java.math.BigDecimal;

public interface MerchantTotalProj {
    String getDescription();
    BigDecimal getTotal();
    long getCount();
}

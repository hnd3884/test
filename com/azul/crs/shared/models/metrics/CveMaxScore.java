package com.azul.crs.shared.models.metrics;

import java.math.BigDecimal;

public enum CveMaxScore
{
    CRITICAL, 
    HIGH, 
    MEDIUM, 
    LOW, 
    UNAFFECTED, 
    UNSCANNED;
    
    public static CveMaxScore of(final String value) {
        if (value == null) {
            return CveMaxScore.UNSCANNED;
        }
        final BigDecimal converted = new BigDecimal(value);
        if (BigDecimal.ZERO.compareTo(converted) > 0) {
            return CveMaxScore.UNSCANNED;
        }
        if (BigDecimal.valueOf(10L).compareTo(converted) < 0) {
            throw new IllegalArgumentException("Cve max score cannot be greater than 10: cve max score=" + value);
        }
        if (BigDecimal.valueOf(9L).compareTo(converted) <= 0) {
            return CveMaxScore.CRITICAL;
        }
        if (BigDecimal.valueOf(7L).compareTo(converted) <= 0) {
            return CveMaxScore.HIGH;
        }
        if (BigDecimal.valueOf(4L).compareTo(converted) <= 0) {
            return CveMaxScore.MEDIUM;
        }
        if (BigDecimal.valueOf(0.1).compareTo(converted) <= 0) {
            return CveMaxScore.LOW;
        }
        return CveMaxScore.UNAFFECTED;
    }
}

package org.apache.commons.beanutils.converters;

import java.math.BigDecimal;

public final class BigDecimalConverter extends NumberConverter
{
    public BigDecimalConverter() {
        super(true);
    }
    
    public BigDecimalConverter(final Object defaultValue) {
        super(true, defaultValue);
    }
    
    @Override
    protected Class<BigDecimal> getDefaultType() {
        return BigDecimal.class;
    }
}

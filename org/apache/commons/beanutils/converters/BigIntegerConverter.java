package org.apache.commons.beanutils.converters;

import java.math.BigInteger;

public final class BigIntegerConverter extends NumberConverter
{
    public BigIntegerConverter() {
        super(false);
    }
    
    public BigIntegerConverter(final Object defaultValue) {
        super(false, defaultValue);
    }
    
    @Override
    protected Class<BigInteger> getDefaultType() {
        return BigInteger.class;
    }
}

package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

class YearMonthDurationDV extends DurationDV
{
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content, 1);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "yearMonthDuration" });
        }
    }
    
    @Override
    protected Duration getDuration(final DateTimeData date) {
        int sign = 1;
        if (date.year < 0 || date.month < 0) {
            sign = -1;
        }
        return YearMonthDurationDV.datatypeFactory.newDuration(sign == 1, (date.year != Integer.MIN_VALUE) ? BigInteger.valueOf(sign * date.year) : null, (date.month != Integer.MIN_VALUE) ? BigInteger.valueOf(sign * date.month) : null, null, null, null, null);
    }
}

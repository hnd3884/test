package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

class DayTimeDurationDV extends DurationDV
{
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content, 2);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "dayTimeDuration" });
        }
    }
    
    @Override
    protected Duration getDuration(final DateTimeData date) {
        int sign = 1;
        if (date.day < 0 || date.hour < 0 || date.minute < 0 || date.second < 0.0) {
            sign = -1;
        }
        return DayTimeDurationDV.datatypeFactory.newDuration(sign == 1, null, null, (date.day != Integer.MIN_VALUE) ? BigInteger.valueOf(sign * date.day) : null, (date.hour != Integer.MIN_VALUE) ? BigInteger.valueOf(sign * date.hour) : null, (date.minute != Integer.MIN_VALUE) ? BigInteger.valueOf(sign * date.minute) : null, (date.second != -2.147483648E9) ? new BigDecimal(String.valueOf(sign * date.second)) : null);
    }
}

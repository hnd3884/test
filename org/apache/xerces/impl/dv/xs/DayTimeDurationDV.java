package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

class DayTimeDurationDV extends DurationDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, 2);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "dayTimeDuration" });
        }
    }
    
    protected Duration getDuration(final DateTimeData dateTimeData) {
        int n = 1;
        if (dateTimeData.day < 0 || dateTimeData.hour < 0 || dateTimeData.minute < 0 || dateTimeData.second < 0.0) {
            n = -1;
        }
        return DayTimeDurationDV.datatypeFactory.newDuration(n == 1, null, null, (dateTimeData.day != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.day) : null, (dateTimeData.hour != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.hour) : null, (dateTimeData.minute != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.minute) : null, (dateTimeData.second != -2.147483648E9) ? new BigDecimal(String.valueOf(n * dateTimeData.second)) : null);
    }
}

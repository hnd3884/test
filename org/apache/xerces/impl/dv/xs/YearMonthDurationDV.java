package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

class YearMonthDurationDV extends DurationDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, 1);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "yearMonthDuration" });
        }
    }
    
    protected Duration getDuration(final DateTimeData dateTimeData) {
        int n = 1;
        if (dateTimeData.year < 0 || dateTimeData.month < 0) {
            n = -1;
        }
        return YearMonthDurationDV.datatypeFactory.newDuration(n == 1, (dateTimeData.year != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.year) : null, (dateTimeData.month != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.month) : null, null, null, null, null);
    }
}

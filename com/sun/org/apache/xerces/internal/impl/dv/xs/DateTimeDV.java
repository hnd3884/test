package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class DateTimeDV extends AbstractDateTimeDV
{
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "dateTime" });
        }
    }
    
    protected DateTimeData parse(final String str) throws SchemaDateTimeException {
        final DateTimeData date = new DateTimeData(str, this);
        final int len = str.length();
        final int end = this.indexOf(str, 0, len, 'T');
        final int dateEnd = this.getDate(str, 0, end, date);
        this.getTime(str, end + 1, len, date);
        if (dateEnd != end) {
            throw new RuntimeException(str + " is an invalid dateTime dataype value. Invalid character(s) seprating date and time values.");
        }
        this.validateDateTime(date);
        this.saveUnnormalized(date);
        if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
        }
        return date;
    }
    
    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData date) {
        return DateTimeDV.datatypeFactory.newXMLGregorianCalendar(BigInteger.valueOf(date.unNormYear), date.unNormMonth, date.unNormDay, date.unNormHour, date.unNormMinute, (int)date.unNormSecond, (date.unNormSecond != 0.0) ? this.getFractionalSecondsAsBigDecimal(date) : null, date.hasTimeZone() ? (date.timezoneHr * 60 + date.timezoneMin) : Integer.MIN_VALUE);
    }
}

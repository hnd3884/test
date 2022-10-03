package com.sun.org.apache.xerces.internal.impl.dv.xs;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class TimeDV extends AbstractDateTimeDV
{
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "time" });
        }
    }
    
    protected DateTimeData parse(final String str) throws SchemaDateTimeException {
        final DateTimeData date = new DateTimeData(str, this);
        final int len = str.length();
        date.year = 2000;
        date.month = 1;
        date.day = 15;
        this.getTime(str, 0, len, date);
        this.validateDateTime(date);
        this.saveUnnormalized(date);
        if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
        }
        date.position = 2;
        return date;
    }
    
    @Override
    protected String dateToString(final DateTimeData date) {
        final StringBuffer message = new StringBuffer(16);
        this.append(message, date.hour, 2);
        message.append(':');
        this.append(message, date.minute, 2);
        message.append(':');
        this.append(message, date.second);
        this.append(message, (char)date.utc, 0);
        return message.toString();
    }
    
    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData date) {
        return TimeDV.datatypeFactory.newXMLGregorianCalendar(null, Integer.MIN_VALUE, Integer.MIN_VALUE, date.unNormHour, date.unNormMinute, (int)date.unNormSecond, (date.unNormSecond != 0.0) ? this.getFractionalSecondsAsBigDecimal(date) : null, date.hasTimeZone() ? (date.timezoneHr * 60 + date.timezoneMin) : Integer.MIN_VALUE);
    }
}

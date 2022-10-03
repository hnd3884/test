package com.sun.org.apache.xerces.internal.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class DateDV extends DateTimeDV
{
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "date" });
        }
    }
    
    @Override
    protected DateTimeData parse(final String str) throws SchemaDateTimeException {
        final DateTimeData date = new DateTimeData(str, this);
        final int len = str.length();
        final int end = this.getDate(str, 0, len, date);
        this.parseTimeZone(str, end, len, date);
        this.validateDateTime(date);
        this.saveUnnormalized(date);
        if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
        }
        return date;
    }
    
    @Override
    protected String dateToString(final DateTimeData date) {
        final StringBuffer message = new StringBuffer(25);
        this.append(message, date.year, 4);
        message.append('-');
        this.append(message, date.month, 2);
        message.append('-');
        this.append(message, date.day, 2);
        this.append(message, (char)date.utc, 0);
        return message.toString();
    }
    
    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData date) {
        return DateDV.datatypeFactory.newXMLGregorianCalendar(date.unNormYear, date.unNormMonth, date.unNormDay, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, date.hasTimeZone() ? (date.timezoneHr * 60 + date.timezoneMin) : Integer.MIN_VALUE);
    }
}

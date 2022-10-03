package com.sun.org.apache.xerces.internal.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class DayDV extends AbstractDateTimeDV
{
    private static final int DAY_SIZE = 5;
    
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "gDay" });
        }
    }
    
    protected DateTimeData parse(final String str) throws SchemaDateTimeException {
        final DateTimeData date = new DateTimeData(str, this);
        final int len = str.length();
        if (str.charAt(0) != '-' || str.charAt(1) != '-' || str.charAt(2) != '-') {
            throw new SchemaDateTimeException("Error in day parsing");
        }
        date.year = 2000;
        date.month = 1;
        date.day = this.parseInt(str, 3, 5);
        if (5 < len) {
            if (!this.isNextCharUTCSign(str, 5, len)) {
                throw new SchemaDateTimeException("Error in day parsing");
            }
            this.getTimeZone(str, date, 5, len);
        }
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
        final StringBuffer message = new StringBuffer(6);
        message.append('-');
        message.append('-');
        message.append('-');
        this.append(message, date.day, 2);
        this.append(message, (char)date.utc, 0);
        return message.toString();
    }
    
    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData date) {
        return DayDV.datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, date.unNormDay, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, date.hasTimeZone() ? (date.timezoneHr * 60 + date.timezoneMin) : Integer.MIN_VALUE);
    }
}

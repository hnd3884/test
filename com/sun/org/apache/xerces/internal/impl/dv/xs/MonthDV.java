package com.sun.org.apache.xerces.internal.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class MonthDV extends AbstractDateTimeDV
{
    @Override
    public Object getActualValue(final String content, final ValidationContext context) throws InvalidDatatypeValueException {
        try {
            return this.parse(content);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { content, "gMonth" });
        }
    }
    
    protected DateTimeData parse(final String str) throws SchemaDateTimeException {
        final DateTimeData date = new DateTimeData(str, this);
        final int len = str.length();
        date.year = 2000;
        date.day = 1;
        if (str.charAt(0) != '-' || str.charAt(1) != '-') {
            throw new SchemaDateTimeException("Invalid format for gMonth: " + str);
        }
        int stop = 4;
        date.month = this.parseInt(str, 2, stop);
        if (str.length() >= stop + 2 && str.charAt(stop) == '-' && str.charAt(stop + 1) == '-') {
            stop += 2;
        }
        if (stop < len) {
            if (!this.isNextCharUTCSign(str, stop, len)) {
                throw new SchemaDateTimeException("Error in month parsing: " + str);
            }
            this.getTimeZone(str, date, stop, len);
        }
        this.validateDateTime(date);
        this.saveUnnormalized(date);
        if (date.utc != 0 && date.utc != 90) {
            this.normalize(date);
        }
        date.position = 1;
        return date;
    }
    
    @Override
    protected String dateToString(final DateTimeData date) {
        final StringBuffer message = new StringBuffer(5);
        message.append('-');
        message.append('-');
        this.append(message, date.month, 2);
        this.append(message, (char)date.utc, 0);
        return message.toString();
    }
    
    @Override
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData date) {
        return MonthDV.datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, date.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, date.hasTimeZone() ? (date.timezoneHr * 60 + date.timezoneMin) : Integer.MIN_VALUE);
    }
}

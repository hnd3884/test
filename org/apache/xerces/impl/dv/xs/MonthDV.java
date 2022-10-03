package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class MonthDV extends AbstractDateTimeDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "gMonth" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        final int length = s.length();
        dateTimeData.year = 2000;
        dateTimeData.day = 1;
        if (s.charAt(0) != '-' || s.charAt(1) != '-') {
            throw new SchemaDateTimeException("Invalid format for gMonth: " + s);
        }
        int n = 4;
        dateTimeData.month = this.parseInt(s, 2, n);
        if (s.length() >= n + 2 && s.charAt(n) == '-' && s.charAt(n + 1) == '-') {
            n += 2;
        }
        if (n < length) {
            if (!this.isNextCharUTCSign(s, n, length)) {
                throw new SchemaDateTimeException("Error in month parsing: " + s);
            }
            this.getTimeZone(s, dateTimeData, n, length);
        }
        this.validateDateTime(dateTimeData, b);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 1;
        return dateTimeData;
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(5);
        sb.append('-');
        sb.append('-');
        this.append(sb, dateTimeData.month, 2);
        this.append(sb, (char)dateTimeData.utc, 0);
        return sb.toString();
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return MonthDV.datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, dateTimeData.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? (dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin) : Integer.MIN_VALUE);
    }
}

package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class YearDV extends AbstractDateTimeDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "gYear" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        final int length = s.length();
        int n = 0;
        if (s.charAt(0) == '-') {
            n = 1;
        }
        final int utcSign = this.findUTCSign(s, n, length);
        final int n2 = ((utcSign == -1) ? length : utcSign) - n;
        if (n2 < 4) {
            throw new RuntimeException("Year must have 'CCYY' format");
        }
        if (n2 > 4 && s.charAt(n) == '0') {
            throw new RuntimeException("Leading zeros are required if the year value would otherwise have fewer than four digits; otherwise they are forbidden");
        }
        if (utcSign == -1) {
            dateTimeData.year = this.parseIntYear(s, length);
        }
        else {
            dateTimeData.year = this.parseIntYear(s, utcSign);
            this.getTimeZone(s, dateTimeData, utcSign, length);
        }
        dateTimeData.month = 1;
        dateTimeData.day = 1;
        this.validateDateTime(dateTimeData, b);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 0;
        return dateTimeData;
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(5);
        this.append(sb, dateTimeData.year, 4);
        this.append(sb, (char)dateTimeData.utc, 0);
        return sb.toString();
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return YearDV.datatypeFactory.newXMLGregorianCalendar(dateTimeData.unNormYear, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? (dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin) : Integer.MIN_VALUE);
    }
}

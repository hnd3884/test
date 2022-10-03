package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class DayDV extends AbstractDateTimeDV
{
    private static final int DAY_SIZE = 5;
    
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "gDay" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        final int length = s.length();
        if (s.charAt(0) != '-' || s.charAt(1) != '-' || s.charAt(2) != '-') {
            throw new SchemaDateTimeException("Error in day parsing");
        }
        dateTimeData.year = 2000;
        dateTimeData.month = 1;
        dateTimeData.day = this.parseInt(s, 3, 5);
        if (5 < length) {
            if (!this.isNextCharUTCSign(s, 5, length)) {
                throw new SchemaDateTimeException("Error in day parsing");
            }
            this.getTimeZone(s, dateTimeData, 5, length);
        }
        this.validateDateTime(dateTimeData, b);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 2;
        return dateTimeData;
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(6);
        sb.append('-');
        sb.append('-');
        sb.append('-');
        this.append(sb, dateTimeData.day, 2);
        this.append(sb, (char)dateTimeData.utc, 0);
        return sb.toString();
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return DayDV.datatypeFactory.newXMLGregorianCalendar(Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.unNormDay, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? (dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin) : Integer.MIN_VALUE);
    }
}

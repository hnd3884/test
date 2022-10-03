package org.apache.xerces.impl.dv.xs;

import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class YearMonthDV extends AbstractDateTimeDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "gYearMonth" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        final int length = s.length();
        final int yearMonth = this.getYearMonth(s, 0, length, dateTimeData);
        dateTimeData.day = 1;
        this.parseTimeZone(s, yearMonth, length, dateTimeData);
        this.validateDateTime(dateTimeData, b);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        dateTimeData.position = 0;
        return dateTimeData;
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(25);
        this.append(sb, dateTimeData.year, 4);
        sb.append('-');
        this.append(sb, dateTimeData.month, 2);
        this.append(sb, (char)dateTimeData.utc, 0);
        return sb.toString();
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return YearMonthDV.datatypeFactory.newXMLGregorianCalendar(dateTimeData.unNormYear, dateTimeData.unNormMonth, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.hasTimeZone() ? (dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin) : Integer.MIN_VALUE);
    }
}

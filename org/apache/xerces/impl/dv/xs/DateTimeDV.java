package org.apache.xerces.impl.dv.xs;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class DateTimeDV extends AbstractDateTimeDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "dateTime" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        final int length = s.length();
        final int index = this.indexOf(s, 0, length, 'T');
        final int date = this.getDate(s, 0, index, dateTimeData);
        this.getTime(s, index + 1, length, dateTimeData);
        if (date != index) {
            throw new RuntimeException(s + " is an invalid dateTime dataype value. " + "Invalid character(s) seprating date and time values.");
        }
        this.validateDateTime(dateTimeData, b);
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
        }
        return dateTimeData;
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return DateTimeDV.datatypeFactory.newXMLGregorianCalendar(BigInteger.valueOf(dateTimeData.unNormYear), dateTimeData.unNormMonth, dateTimeData.unNormDay, dateTimeData.unNormHour, dateTimeData.unNormMinute, (int)dateTimeData.unNormSecond, (dateTimeData.unNormSecond != 0.0) ? this.getFractionalSecondsAsBigDecimal(dateTimeData) : null, dateTimeData.hasTimeZone() ? (dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin) : Integer.MIN_VALUE);
    }
}

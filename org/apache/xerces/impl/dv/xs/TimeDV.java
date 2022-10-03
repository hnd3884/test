package org.apache.xerces.impl.dv.xs;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class TimeDV extends AbstractDateTimeDV
{
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, validationContext.getTypeValidatorHelper().isXMLSchema11());
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "time" });
        }
    }
    
    protected DateTimeData parse(final String s, final boolean b) throws SchemaDateTimeException {
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        final int length = s.length();
        dateTimeData.year = 2000;
        dateTimeData.month = 1;
        dateTimeData.day = 15;
        this.getTime(s, 0, length, dateTimeData);
        this.validateDateTime(dateTimeData, b);
        dateTimeData.day = 15;
        this.saveUnnormalized(dateTimeData);
        if (dateTimeData.utc != 0 && dateTimeData.utc != 90) {
            this.normalize(dateTimeData);
            if (!b) {
                dateTimeData.day = 15;
            }
        }
        dateTimeData.position = 2;
        return dateTimeData;
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(16);
        this.append(sb, dateTimeData.hour, 2);
        sb.append(':');
        this.append(sb, dateTimeData.minute, 2);
        sb.append(':');
        this.append(sb, dateTimeData.second);
        this.append(sb, (char)dateTimeData.utc, 0);
        return sb.toString();
    }
    
    protected XMLGregorianCalendar getXMLGregorianCalendar(final DateTimeData dateTimeData) {
        return TimeDV.datatypeFactory.newXMLGregorianCalendar(null, Integer.MIN_VALUE, Integer.MIN_VALUE, dateTimeData.unNormHour, dateTimeData.unNormMinute, (int)dateTimeData.unNormSecond, (dateTimeData.unNormSecond != 0.0) ? this.getFractionalSecondsAsBigDecimal(dateTimeData) : null, dateTimeData.hasTimeZone() ? (dateTimeData.timezoneHr * 60 + dateTimeData.timezoneMin) : Integer.MIN_VALUE);
    }
}

package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.datatype.Duration;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;

public class DurationDV extends AbstractDateTimeDV
{
    public static final int DURATION_TYPE = 0;
    public static final int YEARMONTHDURATION_TYPE = 1;
    public static final int DAYTIMEDURATION_TYPE = 2;
    private static final DateTimeData[] DATETIMES;
    
    public Object getActualValue(final String s, final ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return this.parse(s, 0);
        }
        catch (final Exception ex) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { s, "duration" });
        }
    }
    
    protected DateTimeData parse(final String s, final int n) throws SchemaDateTimeException {
        final int length = s.length();
        final DateTimeData dateTimeData = new DateTimeData(s, this);
        int n2 = 0;
        final char char1 = s.charAt(n2++);
        if (char1 != 'P' && char1 != '-') {
            throw new SchemaDateTimeException();
        }
        dateTimeData.utc = ((char1 == '-') ? 45 : 0);
        if (char1 == '-' && s.charAt(n2++) != 'P') {
            throw new SchemaDateTimeException();
        }
        int n3 = 1;
        if (dateTimeData.utc == 45) {
            n3 = -1;
        }
        boolean b = false;
        int index = this.indexOf(s, n2, length, 'T');
        if (index == -1) {
            index = length;
        }
        else if (n == 1) {
            throw new SchemaDateTimeException();
        }
        final int index2 = this.indexOf(s, n2, index, 'Y');
        if (index2 != -1) {
            if (n == 2) {
                throw new SchemaDateTimeException();
            }
            dateTimeData.year = n3 * this.parseInt(s, n2, index2);
            n2 = index2 + 1;
            b = true;
        }
        final int index3 = this.indexOf(s, n2, index, 'M');
        if (index3 != -1) {
            if (n == 2) {
                throw new SchemaDateTimeException();
            }
            dateTimeData.month = n3 * this.parseInt(s, n2, index3);
            n2 = index3 + 1;
            b = true;
        }
        final int index4 = this.indexOf(s, n2, index, 'D');
        if (index4 != -1) {
            if (n == 1) {
                throw new SchemaDateTimeException();
            }
            dateTimeData.day = n3 * this.parseInt(s, n2, index4);
            n2 = index4 + 1;
            b = true;
        }
        if (length == index && n2 != length) {
            throw new SchemaDateTimeException();
        }
        if (length != index) {
            final int index5 = this.indexOf(s, ++n2, length, 'H');
            if (index5 != -1) {
                dateTimeData.hour = n3 * this.parseInt(s, n2, index5);
                n2 = index5 + 1;
                b = true;
            }
            final int index6 = this.indexOf(s, n2, length, 'M');
            if (index6 != -1) {
                dateTimeData.minute = n3 * this.parseInt(s, n2, index6);
                n2 = index6 + 1;
                b = true;
            }
            final int index7 = this.indexOf(s, n2, length, 'S');
            if (index7 != -1) {
                dateTimeData.second = n3 * this.parseSecond(s, n2, index7);
                n2 = index7 + 1;
                b = true;
            }
            if (n2 != length || s.charAt(--n2) == 'T') {
                throw new SchemaDateTimeException();
            }
        }
        if (!b) {
            throw new SchemaDateTimeException();
        }
        return dateTimeData;
    }
    
    protected short compareDates(final DateTimeData dateTimeData, final DateTimeData dateTimeData2, final boolean b) {
        if (this.compareOrder(dateTimeData, dateTimeData2) == 0) {
            return 0;
        }
        final DateTimeData[] array = { new DateTimeData(null, this), new DateTimeData(null, this) };
        final short compareOrder = this.compareOrder(this.addDuration(dateTimeData, DurationDV.DATETIMES[0], array[0]), this.addDuration(dateTimeData2, DurationDV.DATETIMES[0], array[1]));
        if (compareOrder == 2) {
            return 2;
        }
        final short compareResults = this.compareResults(compareOrder, this.compareOrder(this.addDuration(dateTimeData, DurationDV.DATETIMES[1], array[0]), this.addDuration(dateTimeData2, DurationDV.DATETIMES[1], array[1])), b);
        if (compareResults == 2) {
            return 2;
        }
        final short compareResults2 = this.compareResults(compareResults, this.compareOrder(this.addDuration(dateTimeData, DurationDV.DATETIMES[2], array[0]), this.addDuration(dateTimeData2, DurationDV.DATETIMES[2], array[1])), b);
        if (compareResults2 == 2) {
            return 2;
        }
        return this.compareResults(compareResults2, this.compareOrder(this.addDuration(dateTimeData, DurationDV.DATETIMES[3], array[0]), this.addDuration(dateTimeData2, DurationDV.DATETIMES[3], array[1])), b);
    }
    
    private short compareResults(final short n, final short n2, final boolean b) {
        if (n2 == 2) {
            return 2;
        }
        if (n != n2 && b) {
            return 2;
        }
        if (n == n2 || b) {
            return n;
        }
        if (n != 0 && n2 != 0) {
            return 2;
        }
        return (n != 0) ? n : n2;
    }
    
    private DateTimeData addDuration(final DateTimeData dateTimeData, final DateTimeData dateTimeData2, final DateTimeData dateTimeData3) {
        this.resetDateObj(dateTimeData3);
        final int n = dateTimeData2.month + dateTimeData.month;
        dateTimeData3.month = this.modulo(n, 1, 13);
        dateTimeData3.year = dateTimeData2.year + dateTimeData.year + this.fQuotient(n, 1, 13);
        final double n2 = dateTimeData2.second + dateTimeData.second;
        final int n3 = (int)Math.floor(n2 / 60.0);
        dateTimeData3.second = n2 - n3 * 60;
        final int n4 = dateTimeData2.minute + dateTimeData.minute + n3;
        final int fQuotient = this.fQuotient(n4, 60);
        dateTimeData3.minute = this.mod(n4, 60, fQuotient);
        final int n5 = dateTimeData2.hour + dateTimeData.hour + fQuotient;
        final int fQuotient2 = this.fQuotient(n5, 24);
        dateTimeData3.hour = this.mod(n5, 24, fQuotient2);
        dateTimeData3.day = dateTimeData2.day + dateTimeData.day + fQuotient2;
        while (true) {
            final int maxDayInMonth = this.maxDayInMonthFor(dateTimeData3.year, dateTimeData3.month);
            int n6;
            if (dateTimeData3.day < 1) {
                dateTimeData3.day += this.maxDayInMonthFor(dateTimeData3.year, dateTimeData3.month - 1);
                n6 = -1;
            }
            else {
                if (dateTimeData3.day <= maxDayInMonth) {
                    break;
                }
                dateTimeData3.day -= maxDayInMonth;
                n6 = 1;
            }
            final int n7 = dateTimeData3.month + n6;
            dateTimeData3.month = this.modulo(n7, 1, 13);
            dateTimeData3.year += this.fQuotient(n7, 1, 13);
        }
        dateTimeData3.utc = 90;
        return dateTimeData3;
    }
    
    protected double parseSecond(final String s, final int n, final int n2) throws NumberFormatException {
        int n3 = -1;
        for (int i = n; i < n2; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '.') {
                n3 = i;
            }
            else if (char1 > '9' || char1 < '0') {
                throw new NumberFormatException("'" + s + "' has wrong format");
            }
        }
        if (n3 + 1 == n2) {
            throw new NumberFormatException("'" + s + "' has wrong format");
        }
        final double double1 = Double.parseDouble(s.substring(n, n2));
        if (double1 == Double.POSITIVE_INFINITY) {
            throw new NumberFormatException("'" + s + "' has wrong format");
        }
        return double1;
    }
    
    protected String dateToString(final DateTimeData dateTimeData) {
        final StringBuffer sb = new StringBuffer(30);
        if (dateTimeData.year < 0 || dateTimeData.month < 0 || dateTimeData.day < 0 || dateTimeData.hour < 0 || dateTimeData.minute < 0 || dateTimeData.second < 0.0) {
            sb.append('-');
        }
        sb.append('P');
        sb.append(((dateTimeData.year < 0) ? -1 : 1) * dateTimeData.year);
        sb.append('Y');
        sb.append(((dateTimeData.month < 0) ? -1 : 1) * dateTimeData.month);
        sb.append('M');
        sb.append(((dateTimeData.day < 0) ? -1 : 1) * dateTimeData.day);
        sb.append('D');
        sb.append('T');
        sb.append(((dateTimeData.hour < 0) ? -1 : 1) * dateTimeData.hour);
        sb.append('H');
        sb.append(((dateTimeData.minute < 0) ? -1 : 1) * dateTimeData.minute);
        sb.append('M');
        this.append2(sb, ((dateTimeData.second < 0.0) ? -1 : 1) * dateTimeData.second);
        sb.append('S');
        return sb.toString();
    }
    
    protected Duration getDuration(final DateTimeData dateTimeData) {
        int n = 1;
        if (dateTimeData.year < 0 || dateTimeData.month < 0 || dateTimeData.day < 0 || dateTimeData.hour < 0 || dateTimeData.minute < 0 || dateTimeData.second < 0.0) {
            n = -1;
        }
        return DurationDV.datatypeFactory.newDuration(n == 1, (dateTimeData.year != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.year) : null, (dateTimeData.month != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.month) : null, (dateTimeData.day != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.day) : null, (dateTimeData.hour != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.hour) : null, (dateTimeData.minute != Integer.MIN_VALUE) ? BigInteger.valueOf(n * dateTimeData.minute) : null, (dateTimeData.second != -2.147483648E9) ? new BigDecimal(String.valueOf(n * dateTimeData.second)) : null);
    }
    
    static {
        DATETIMES = new DateTimeData[] { new DateTimeData(1696, 9, 1, 0, 0, 0.0, 90, null, true, null), new DateTimeData(1697, 2, 1, 0, 0, 0.0, 90, null, true, null), new DateTimeData(1903, 3, 1, 0, 0, 0.0, 90, null, true, null), new DateTimeData(1903, 7, 1, 0, 0, 0.0, 90, null, true, null) };
    }
}

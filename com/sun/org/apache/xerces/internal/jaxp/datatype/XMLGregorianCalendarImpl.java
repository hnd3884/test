package com.sun.org.apache.xerces.internal.jaxp.datatype;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarImpl extends XMLGregorianCalendar implements Serializable, Cloneable
{
    private BigInteger eon;
    private int year;
    private int month;
    private int day;
    private int timezone;
    private int hour;
    private int minute;
    private int second;
    private BigDecimal fractionalSecond;
    private static final BigInteger BILLION;
    private static final Date PURE_GREGORIAN_CHANGE;
    private static final int YEAR = 0;
    private static final int MONTH = 1;
    private static final int DAY = 2;
    private static final int HOUR = 3;
    private static final int MINUTE = 4;
    private static final int SECOND = 5;
    private static final int MILLISECOND = 6;
    private static final int TIMEZONE = 7;
    private static final String[] FIELD_NAME;
    private static final long serialVersionUID = 1L;
    public static final XMLGregorianCalendar LEAP_YEAR_DEFAULT;
    private static final BigInteger FOUR;
    private static final BigInteger HUNDRED;
    private static final BigInteger FOUR_HUNDRED;
    private static final BigInteger SIXTY;
    private static final BigInteger TWENTY_FOUR;
    private static final BigInteger TWELVE;
    private static final BigDecimal DECIMAL_ZERO;
    private static final BigDecimal DECIMAL_ONE;
    private static final BigDecimal DECIMAL_SIXTY;
    private static int[] daysInMonth;
    
    protected XMLGregorianCalendarImpl(final String lexicalRepresentation) throws IllegalArgumentException {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
        String format = null;
        final String lexRep = lexicalRepresentation;
        final int NOT_FOUND = -1;
        int lexRepLength = lexRep.length();
        if (lexRep.indexOf(84) != -1) {
            format = "%Y-%M-%DT%h:%m:%s%z";
        }
        else if (lexRepLength >= 3 && lexRep.charAt(2) == ':') {
            format = "%h:%m:%s%z";
        }
        else if (lexRep.startsWith("--")) {
            if (lexRepLength >= 3 && lexRep.charAt(2) == '-') {
                format = "---%D%z";
            }
            else if (lexRepLength == 4 || lexRepLength == 5 || lexRepLength == 10) {
                format = "--%M%z";
            }
            else {
                format = "--%M-%D%z";
            }
        }
        else {
            int countSeparator = 0;
            final int timezoneOffset = lexRep.indexOf(58);
            if (timezoneOffset != -1) {
                lexRepLength -= 6;
            }
            for (int i = 1; i < lexRepLength; ++i) {
                if (lexRep.charAt(i) == '-') {
                    ++countSeparator;
                }
            }
            if (countSeparator == 0) {
                format = "%Y%z";
            }
            else if (countSeparator == 1) {
                format = "%Y-%M%z";
            }
            else {
                format = "%Y-%M-%D%z";
            }
        }
        final Parser p = new Parser(format, lexRep);
        p.parse();
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[] { lexicalRepresentation }));
        }
    }
    
    public XMLGregorianCalendarImpl() {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
    }
    
    protected XMLGregorianCalendarImpl(final BigInteger year, final int month, final int day, final int hour, final int minute, final int second, final BigDecimal fractionalSecond, final int timezone) {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
        this.setYear(year);
        this.setMonth(month);
        this.setDay(day);
        this.setTime(hour, minute, second, fractionalSecond);
        this.setTimezone(timezone);
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-fractional", new Object[] { year, new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), new Integer(second), fractionalSecond, new Integer(timezone) }));
        }
    }
    
    private XMLGregorianCalendarImpl(final int year, final int month, final int day, final int hour, final int minute, final int second, final int millisecond, final int timezone) {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
        this.setYear(year);
        this.setMonth(month);
        this.setDay(day);
        this.setTime(hour, minute, second);
        this.setTimezone(timezone);
        this.setMillisecond(millisecond);
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-milli", new Object[] { new Integer(year), new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), new Integer(second), new Integer(millisecond), new Integer(timezone) }));
        }
    }
    
    public XMLGregorianCalendarImpl(final GregorianCalendar cal) {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
        int year = cal.get(1);
        if (cal.get(0) == 0) {
            year = -year;
        }
        this.setYear(year);
        this.setMonth(cal.get(2) + 1);
        this.setDay(cal.get(5));
        this.setTime(cal.get(11), cal.get(12), cal.get(13), cal.get(14));
        final int offsetInMinutes = (cal.get(15) + cal.get(16)) / 60000;
        this.setTimezone(offsetInMinutes);
    }
    
    public static XMLGregorianCalendar createDateTime(final BigInteger year, final int month, final int day, final int hours, final int minutes, final int seconds, final BigDecimal fractionalSecond, final int timezone) {
        return new XMLGregorianCalendarImpl(year, month, day, hours, minutes, seconds, fractionalSecond, timezone);
    }
    
    public static XMLGregorianCalendar createDateTime(final int year, final int month, final int day, final int hour, final int minute, final int second) {
        return new XMLGregorianCalendarImpl(year, month, day, hour, minute, second, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    
    public static XMLGregorianCalendar createDateTime(final int year, final int month, final int day, final int hours, final int minutes, final int seconds, final int milliseconds, final int timezone) {
        return new XMLGregorianCalendarImpl(year, month, day, hours, minutes, seconds, milliseconds, timezone);
    }
    
    public static XMLGregorianCalendar createDate(final int year, final int month, final int day, final int timezone) {
        return new XMLGregorianCalendarImpl(year, month, day, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, timezone);
    }
    
    public static XMLGregorianCalendar createTime(final int hours, final int minutes, final int seconds, final int timezone) {
        return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, Integer.MIN_VALUE, timezone);
    }
    
    public static XMLGregorianCalendar createTime(final int hours, final int minutes, final int seconds, final BigDecimal fractionalSecond, final int timezone) {
        return new XMLGregorianCalendarImpl(null, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, fractionalSecond, timezone);
    }
    
    public static XMLGregorianCalendar createTime(final int hours, final int minutes, final int seconds, final int milliseconds, final int timezone) {
        return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, hours, minutes, seconds, milliseconds, timezone);
    }
    
    @Override
    public BigInteger getEon() {
        return this.eon;
    }
    
    @Override
    public int getYear() {
        return this.year;
    }
    
    @Override
    public BigInteger getEonAndYear() {
        if (this.year != Integer.MIN_VALUE && this.eon != null) {
            return this.eon.add(BigInteger.valueOf(this.year));
        }
        if (this.year != Integer.MIN_VALUE && this.eon == null) {
            return BigInteger.valueOf(this.year);
        }
        return null;
    }
    
    @Override
    public int getMonth() {
        return this.month;
    }
    
    @Override
    public int getDay() {
        return this.day;
    }
    
    @Override
    public int getTimezone() {
        return this.timezone;
    }
    
    @Override
    public int getHour() {
        return this.hour;
    }
    
    @Override
    public int getMinute() {
        return this.minute;
    }
    
    @Override
    public int getSecond() {
        return this.second;
    }
    
    private BigDecimal getSeconds() {
        if (this.second == Integer.MIN_VALUE) {
            return XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        final BigDecimal result = BigDecimal.valueOf(this.second);
        if (this.fractionalSecond != null) {
            return result.add(this.fractionalSecond);
        }
        return result;
    }
    
    @Override
    public int getMillisecond() {
        if (this.fractionalSecond == null) {
            return Integer.MIN_VALUE;
        }
        return this.fractionalSecond.movePointRight(3).intValue();
    }
    
    @Override
    public BigDecimal getFractionalSecond() {
        return this.fractionalSecond;
    }
    
    @Override
    public void setYear(final BigInteger year) {
        if (year == null) {
            this.eon = null;
            this.year = Integer.MIN_VALUE;
        }
        else {
            final BigInteger temp = year.remainder(XMLGregorianCalendarImpl.BILLION);
            this.year = temp.intValue();
            this.setEon(year.subtract(temp));
        }
    }
    
    @Override
    public void setYear(final int year) {
        if (year == Integer.MIN_VALUE) {
            this.year = Integer.MIN_VALUE;
            this.eon = null;
        }
        else if (Math.abs(year) < XMLGregorianCalendarImpl.BILLION.intValue()) {
            this.year = year;
            this.eon = null;
        }
        else {
            final BigInteger theYear = BigInteger.valueOf(year);
            final BigInteger remainder = theYear.remainder(XMLGregorianCalendarImpl.BILLION);
            this.year = remainder.intValue();
            this.setEon(theYear.subtract(remainder));
        }
    }
    
    private void setEon(final BigInteger eon) {
        if (eon != null && eon.compareTo(BigInteger.ZERO) == 0) {
            this.eon = null;
        }
        else {
            this.eon = eon;
        }
    }
    
    @Override
    public void setMonth(final int month) {
        if ((month < 1 || 12 < month) && month != Integer.MIN_VALUE) {
            this.invalidFieldValue(1, month);
        }
        this.month = month;
    }
    
    @Override
    public void setDay(final int day) {
        if ((day < 1 || 31 < day) && day != Integer.MIN_VALUE) {
            this.invalidFieldValue(2, day);
        }
        this.day = day;
    }
    
    @Override
    public void setTimezone(final int offset) {
        if ((offset < -840 || 840 < offset) && offset != Integer.MIN_VALUE) {
            this.invalidFieldValue(7, offset);
        }
        this.timezone = offset;
    }
    
    @Override
    public void setTime(final int hour, final int minute, final int second) {
        this.setTime(hour, minute, second, null);
    }
    
    private void invalidFieldValue(final int field, final int value) {
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFieldValue", new Object[] { new Integer(value), XMLGregorianCalendarImpl.FIELD_NAME[field] }));
    }
    
    private void testHour() {
        if (this.getHour() == 24) {
            if (this.getMinute() != 0 || this.getSecond() != 0) {
                this.invalidFieldValue(3, this.getHour());
            }
            this.setHour(0, false);
            this.add(new DurationImpl(true, 0, 0, 1, 0, 0, 0));
        }
    }
    
    @Override
    public void setHour(final int hour) {
        this.setHour(hour, true);
    }
    
    private void setHour(final int hour, final boolean validate) {
        if ((hour < 0 || hour > 24) && hour != Integer.MIN_VALUE) {
            this.invalidFieldValue(3, hour);
        }
        this.hour = hour;
        if (validate) {
            this.testHour();
        }
    }
    
    @Override
    public void setMinute(final int minute) {
        if ((minute < 0 || 59 < minute) && minute != Integer.MIN_VALUE) {
            this.invalidFieldValue(4, minute);
        }
        this.minute = minute;
    }
    
    @Override
    public void setSecond(final int second) {
        if ((second < 0 || 60 < second) && second != Integer.MIN_VALUE) {
            this.invalidFieldValue(5, second);
        }
        this.second = second;
    }
    
    @Override
    public void setTime(final int hour, final int minute, final int second, final BigDecimal fractional) {
        this.setHour(hour, false);
        this.setMinute(minute);
        if (second != 60) {
            this.setSecond(second);
        }
        else if ((hour == 23 && minute == 59) || (hour == 0 && minute == 0)) {
            this.setSecond(second);
        }
        else {
            this.invalidFieldValue(5, second);
        }
        this.setFractionalSecond(fractional);
        this.testHour();
    }
    
    @Override
    public void setTime(final int hour, final int minute, final int second, final int millisecond) {
        this.setHour(hour, false);
        this.setMinute(minute);
        if (second != 60) {
            this.setSecond(second);
        }
        else if ((hour == 23 && minute == 59) || (hour == 0 && minute == 0)) {
            this.setSecond(second);
        }
        else {
            this.invalidFieldValue(5, second);
        }
        this.setMillisecond(millisecond);
        this.testHour();
    }
    
    @Override
    public int compare(final XMLGregorianCalendar rhs) {
        final XMLGregorianCalendar lhs = this;
        int result = 2;
        XMLGregorianCalendarImpl P = (XMLGregorianCalendarImpl)lhs;
        XMLGregorianCalendarImpl Q = (XMLGregorianCalendarImpl)rhs;
        if (P.getTimezone() == Q.getTimezone()) {
            return internalCompare(P, Q);
        }
        if (P.getTimezone() != Integer.MIN_VALUE && Q.getTimezone() != Integer.MIN_VALUE) {
            P = (XMLGregorianCalendarImpl)P.normalize();
            Q = (XMLGregorianCalendarImpl)Q.normalize();
            return internalCompare(P, Q);
        }
        if (P.getTimezone() != Integer.MIN_VALUE) {
            if (P.getTimezone() != 0) {
                P = (XMLGregorianCalendarImpl)P.normalize();
            }
            final XMLGregorianCalendar MinQ = Q.normalizeToTimezone(840);
            result = internalCompare(P, MinQ);
            if (result == -1) {
                return result;
            }
            final XMLGregorianCalendar MaxQ = Q.normalizeToTimezone(-840);
            result = internalCompare(P, MaxQ);
            if (result == 1) {
                return result;
            }
            return 2;
        }
        else {
            if (Q.getTimezone() != 0) {
                Q = (XMLGregorianCalendarImpl)Q.normalizeToTimezone(Q.getTimezone());
            }
            final XMLGregorianCalendar MaxP = P.normalizeToTimezone(-840);
            result = internalCompare(MaxP, Q);
            if (result == -1) {
                return result;
            }
            final XMLGregorianCalendar MinP = P.normalizeToTimezone(840);
            result = internalCompare(MinP, Q);
            if (result == 1) {
                return result;
            }
            return 2;
        }
    }
    
    @Override
    public XMLGregorianCalendar normalize() {
        final XMLGregorianCalendar normalized = this.normalizeToTimezone(this.timezone);
        if (this.getTimezone() == Integer.MIN_VALUE) {
            normalized.setTimezone(Integer.MIN_VALUE);
        }
        if (this.getMillisecond() == Integer.MIN_VALUE) {
            normalized.setMillisecond(Integer.MIN_VALUE);
        }
        return normalized;
    }
    
    private XMLGregorianCalendar normalizeToTimezone(final int timezone) {
        int minutes = timezone;
        final XMLGregorianCalendar result = (XMLGregorianCalendar)this.clone();
        minutes = -minutes;
        final Duration d = new DurationImpl(minutes >= 0, 0, 0, 0, 0, (minutes < 0) ? (-minutes) : minutes, 0);
        result.add(d);
        result.setTimezone(0);
        return result;
    }
    
    private static int internalCompare(final XMLGregorianCalendar P, final XMLGregorianCalendar Q) {
        if (P.getEon() == Q.getEon()) {
            final int result = compareField(P.getYear(), Q.getYear());
            if (result != 0) {
                return result;
            }
        }
        else {
            final int result = compareField(P.getEonAndYear(), Q.getEonAndYear());
            if (result != 0) {
                return result;
            }
        }
        int result = compareField(P.getMonth(), Q.getMonth());
        if (result != 0) {
            return result;
        }
        result = compareField(P.getDay(), Q.getDay());
        if (result != 0) {
            return result;
        }
        result = compareField(P.getHour(), Q.getHour());
        if (result != 0) {
            return result;
        }
        result = compareField(P.getMinute(), Q.getMinute());
        if (result != 0) {
            return result;
        }
        result = compareField(P.getSecond(), Q.getSecond());
        if (result != 0) {
            return result;
        }
        result = compareField(P.getFractionalSecond(), Q.getFractionalSecond());
        return result;
    }
    
    private static int compareField(final int Pfield, final int Qfield) {
        if (Pfield == Qfield) {
            return 0;
        }
        if (Pfield == Integer.MIN_VALUE || Qfield == Integer.MIN_VALUE) {
            return 2;
        }
        return (Pfield < Qfield) ? -1 : 1;
    }
    
    private static int compareField(final BigInteger Pfield, final BigInteger Qfield) {
        if (Pfield == null) {
            return (Qfield == null) ? 0 : 2;
        }
        if (Qfield == null) {
            return 2;
        }
        return Pfield.compareTo(Qfield);
    }
    
    private static int compareField(BigDecimal Pfield, BigDecimal Qfield) {
        if (Pfield == Qfield) {
            return 0;
        }
        if (Pfield == null) {
            Pfield = XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        if (Qfield == null) {
            Qfield = XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        return Pfield.compareTo(Qfield);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof XMLGregorianCalendar && this.compare((XMLGregorianCalendar)obj) == 0;
    }
    
    @Override
    public int hashCode() {
        int timezone = this.getTimezone();
        if (timezone == Integer.MIN_VALUE) {
            timezone = 0;
        }
        XMLGregorianCalendar gc = this;
        if (timezone != 0) {
            gc = this.normalizeToTimezone(this.getTimezone());
        }
        return gc.getYear() + gc.getMonth() + gc.getDay() + gc.getHour() + gc.getMinute() + gc.getSecond();
    }
    
    public static XMLGregorianCalendar parse(final String lexicalRepresentation) {
        return new XMLGregorianCalendarImpl(lexicalRepresentation);
    }
    
    @Override
    public String toXMLFormat() {
        final QName typekind = this.getXMLSchemaType();
        String formatString = null;
        if (typekind == DatatypeConstants.DATETIME) {
            formatString = "%Y-%M-%DT%h:%m:%s%z";
        }
        else if (typekind == DatatypeConstants.DATE) {
            formatString = "%Y-%M-%D%z";
        }
        else if (typekind == DatatypeConstants.TIME) {
            formatString = "%h:%m:%s%z";
        }
        else if (typekind == DatatypeConstants.GMONTH) {
            formatString = "--%M%z";
        }
        else if (typekind == DatatypeConstants.GDAY) {
            formatString = "---%D%z";
        }
        else if (typekind == DatatypeConstants.GYEAR) {
            formatString = "%Y%z";
        }
        else if (typekind == DatatypeConstants.GYEARMONTH) {
            formatString = "%Y-%M%z";
        }
        else if (typekind == DatatypeConstants.GMONTHDAY) {
            formatString = "--%M-%D%z";
        }
        return this.format(formatString);
    }
    
    @Override
    public QName getXMLSchemaType() {
        final int mask = ((this.year != Integer.MIN_VALUE) ? 32 : 0) | ((this.month != Integer.MIN_VALUE) ? 16 : 0) | ((this.day != Integer.MIN_VALUE) ? 8 : 0) | ((this.hour != Integer.MIN_VALUE) ? 4 : 0) | ((this.minute != Integer.MIN_VALUE) ? 2 : 0) | ((this.second != Integer.MIN_VALUE) ? 1 : 0);
        switch (mask) {
            case 63: {
                return DatatypeConstants.DATETIME;
            }
            case 56: {
                return DatatypeConstants.DATE;
            }
            case 7: {
                return DatatypeConstants.TIME;
            }
            case 48: {
                return DatatypeConstants.GYEARMONTH;
            }
            case 24: {
                return DatatypeConstants.GMONTHDAY;
            }
            case 32: {
                return DatatypeConstants.GYEAR;
            }
            case 16: {
                return DatatypeConstants.GMONTH;
            }
            case 8: {
                return DatatypeConstants.GDAY;
            }
            default: {
                throw new IllegalStateException(this.getClass().getName() + "#getXMLSchemaType() :" + DatatypeMessageFormatter.formatMessage(null, "InvalidXGCFields", null));
            }
        }
    }
    
    @Override
    public boolean isValid() {
        if (this.getMonth() == 2) {
            int maxDays = 29;
            if (this.eon == null) {
                if (this.year != Integer.MIN_VALUE) {
                    maxDays = maximumDayInMonthFor(this.year, this.getMonth());
                }
            }
            else {
                final BigInteger years = this.getEonAndYear();
                if (years != null) {
                    maxDays = maximumDayInMonthFor(this.getEonAndYear(), 2);
                }
            }
            if (this.getDay() > maxDays) {
                return false;
            }
        }
        if (this.getHour() == 24) {
            if (this.getMinute() != 0) {
                return false;
            }
            if (this.getSecond() != 0) {
                return false;
            }
        }
        if (this.eon == null) {
            if (this.year == 0) {
                return false;
            }
        }
        else {
            final BigInteger yearField = this.getEonAndYear();
            if (yearField != null) {
                final int result = compareField(yearField, BigInteger.ZERO);
                if (result == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public void add(final Duration duration) {
        final boolean[] fieldUndefined = { false, false, false, false, false, false };
        final int signum = duration.getSign();
        int startMonth = this.getMonth();
        if (startMonth == Integer.MIN_VALUE) {
            startMonth = 1;
            fieldUndefined[1] = true;
        }
        final BigInteger dMonths = sanitize(duration.getField(DatatypeConstants.MONTHS), signum);
        BigInteger temp = BigInteger.valueOf(startMonth).add(dMonths);
        this.setMonth(temp.subtract(BigInteger.ONE).mod(XMLGregorianCalendarImpl.TWELVE).intValue() + 1);
        BigInteger carry = new BigDecimal(temp.subtract(BigInteger.ONE)).divide(new BigDecimal(XMLGregorianCalendarImpl.TWELVE), 3).toBigInteger();
        BigInteger startYear = this.getEonAndYear();
        if (startYear == null) {
            fieldUndefined[0] = true;
            startYear = BigInteger.ZERO;
        }
        final BigInteger dYears = sanitize(duration.getField(DatatypeConstants.YEARS), signum);
        final BigInteger endYear = startYear.add(dYears).add(carry);
        this.setYear(endYear);
        BigDecimal startSeconds;
        if (this.getSecond() == Integer.MIN_VALUE) {
            fieldUndefined[5] = true;
            startSeconds = XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        else {
            startSeconds = this.getSeconds();
        }
        final BigDecimal dSeconds = DurationImpl.sanitize((BigDecimal)duration.getField(DatatypeConstants.SECONDS), signum);
        final BigDecimal tempBD = startSeconds.add(dSeconds);
        final BigDecimal fQuotient = new BigDecimal(new BigDecimal(tempBD.toBigInteger()).divide(XMLGregorianCalendarImpl.DECIMAL_SIXTY, 3).toBigInteger());
        final BigDecimal endSeconds = tempBD.subtract(fQuotient.multiply(XMLGregorianCalendarImpl.DECIMAL_SIXTY));
        carry = fQuotient.toBigInteger();
        this.setSecond(endSeconds.intValue());
        final BigDecimal tempFracSeconds = endSeconds.subtract(new BigDecimal(BigInteger.valueOf(this.getSecond())));
        if (tempFracSeconds.compareTo(XMLGregorianCalendarImpl.DECIMAL_ZERO) < 0) {
            this.setFractionalSecond(XMLGregorianCalendarImpl.DECIMAL_ONE.add(tempFracSeconds));
            if (this.getSecond() == 0) {
                this.setSecond(59);
                carry = carry.subtract(BigInteger.ONE);
            }
            else {
                this.setSecond(this.getSecond() - 1);
            }
        }
        else {
            this.setFractionalSecond(tempFracSeconds);
        }
        int startMinutes = this.getMinute();
        if (startMinutes == Integer.MIN_VALUE) {
            fieldUndefined[4] = true;
            startMinutes = 0;
        }
        final BigInteger dMinutes = sanitize(duration.getField(DatatypeConstants.MINUTES), signum);
        temp = BigInteger.valueOf(startMinutes).add(dMinutes).add(carry);
        this.setMinute(temp.mod(XMLGregorianCalendarImpl.SIXTY).intValue());
        carry = new BigDecimal(temp).divide(XMLGregorianCalendarImpl.DECIMAL_SIXTY, 3).toBigInteger();
        int startHours = this.getHour();
        if (startHours == Integer.MIN_VALUE) {
            fieldUndefined[3] = true;
            startHours = 0;
        }
        final BigInteger dHours = sanitize(duration.getField(DatatypeConstants.HOURS), signum);
        temp = BigInteger.valueOf(startHours).add(dHours).add(carry);
        this.setHour(temp.mod(XMLGregorianCalendarImpl.TWENTY_FOUR).intValue(), false);
        carry = new BigDecimal(temp).divide(new BigDecimal(XMLGregorianCalendarImpl.TWENTY_FOUR), 3).toBigInteger();
        int startDay = this.getDay();
        if (startDay == Integer.MIN_VALUE) {
            fieldUndefined[2] = true;
            startDay = 1;
        }
        final BigInteger dDays = sanitize(duration.getField(DatatypeConstants.DAYS), signum);
        final int maxDayInMonth = maximumDayInMonthFor(this.getEonAndYear(), this.getMonth());
        BigInteger tempDays;
        if (startDay > maxDayInMonth) {
            tempDays = BigInteger.valueOf(maxDayInMonth);
        }
        else if (startDay < 1) {
            tempDays = BigInteger.ONE;
        }
        else {
            tempDays = BigInteger.valueOf(startDay);
        }
        BigInteger endDays = tempDays.add(dDays).add(carry);
        while (true) {
            int monthCarry;
            if (endDays.compareTo(BigInteger.ONE) < 0) {
                BigInteger mdimf = null;
                if (this.month >= 2) {
                    mdimf = BigInteger.valueOf(maximumDayInMonthFor(this.getEonAndYear(), this.getMonth() - 1));
                }
                else {
                    mdimf = BigInteger.valueOf(maximumDayInMonthFor(this.getEonAndYear().subtract(BigInteger.valueOf(1L)), 12));
                }
                endDays = endDays.add(mdimf);
                monthCarry = -1;
            }
            else {
                if (endDays.compareTo(BigInteger.valueOf(maximumDayInMonthFor(this.getEonAndYear(), this.getMonth()))) <= 0) {
                    break;
                }
                endDays = endDays.add(BigInteger.valueOf(-maximumDayInMonthFor(this.getEonAndYear(), this.getMonth())));
                monthCarry = 1;
            }
            final int intTemp = this.getMonth() + monthCarry;
            int endMonth = (intTemp - 1) % 12;
            int quotient;
            if (endMonth < 0) {
                endMonth = 12 + endMonth + 1;
                quotient = new BigDecimal(intTemp - 1).divide(new BigDecimal(XMLGregorianCalendarImpl.TWELVE), 0).intValue();
            }
            else {
                quotient = (intTemp - 1) / 12;
                ++endMonth;
            }
            this.setMonth(endMonth);
            if (quotient != 0) {
                this.setYear(this.getEonAndYear().add(BigInteger.valueOf(quotient)));
            }
        }
        this.setDay(endDays.intValue());
        for (int i = 0; i <= 5; ++i) {
            if (fieldUndefined[i]) {
                switch (i) {
                    case 0: {
                        this.setYear(Integer.MIN_VALUE);
                        break;
                    }
                    case 1: {
                        this.setMonth(Integer.MIN_VALUE);
                        break;
                    }
                    case 2: {
                        this.setDay(Integer.MIN_VALUE);
                        break;
                    }
                    case 3: {
                        this.setHour(Integer.MIN_VALUE, false);
                        break;
                    }
                    case 4: {
                        this.setMinute(Integer.MIN_VALUE);
                        break;
                    }
                    case 5: {
                        this.setSecond(Integer.MIN_VALUE);
                        this.setFractionalSecond(null);
                        break;
                    }
                }
            }
        }
    }
    
    private static int maximumDayInMonthFor(final BigInteger year, final int month) {
        if (month != 2) {
            return XMLGregorianCalendarImpl.daysInMonth[month];
        }
        if (year.mod(XMLGregorianCalendarImpl.FOUR_HUNDRED).equals(BigInteger.ZERO) || (!year.mod(XMLGregorianCalendarImpl.HUNDRED).equals(BigInteger.ZERO) && year.mod(XMLGregorianCalendarImpl.FOUR).equals(BigInteger.ZERO))) {
            return 29;
        }
        return XMLGregorianCalendarImpl.daysInMonth[month];
    }
    
    private static int maximumDayInMonthFor(final int year, final int month) {
        if (month != 2) {
            return XMLGregorianCalendarImpl.daysInMonth[month];
        }
        if (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) {
            return 29;
        }
        return XMLGregorianCalendarImpl.daysInMonth[2];
    }
    
    @Override
    public GregorianCalendar toGregorianCalendar() {
        GregorianCalendar result = null;
        final int DEFAULT_TIMEZONE_OFFSET = Integer.MIN_VALUE;
        final TimeZone tz = this.getTimeZone(Integer.MIN_VALUE);
        final Locale locale = this.getDefaultLocale();
        result = new GregorianCalendar(tz, locale);
        result.clear();
        result.setGregorianChange(XMLGregorianCalendarImpl.PURE_GREGORIAN_CHANGE);
        final BigInteger year = this.getEonAndYear();
        if (year != null) {
            result.set(0, (year.signum() != -1) ? 1 : 0);
            result.set(1, year.abs().intValue());
        }
        if (this.month != Integer.MIN_VALUE) {
            result.set(2, this.month - 1);
        }
        if (this.day != Integer.MIN_VALUE) {
            result.set(5, this.day);
        }
        if (this.hour != Integer.MIN_VALUE) {
            result.set(11, this.hour);
        }
        if (this.minute != Integer.MIN_VALUE) {
            result.set(12, this.minute);
        }
        if (this.second != Integer.MIN_VALUE) {
            result.set(13, this.second);
        }
        if (this.fractionalSecond != null) {
            result.set(14, this.getMillisecond());
        }
        return result;
    }
    
    private Locale getDefaultLocale() {
        final String lang = SecuritySupport.getSystemProperty("user.language.format");
        final String country = SecuritySupport.getSystemProperty("user.country.format");
        final String variant = SecuritySupport.getSystemProperty("user.variant.format");
        Locale locale = null;
        if (lang != null) {
            if (country != null) {
                if (variant != null) {
                    locale = new Locale(lang, country, variant);
                }
                else {
                    locale = new Locale(lang, country);
                }
            }
            else {
                locale = new Locale(lang);
            }
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        return locale;
    }
    
    @Override
    public GregorianCalendar toGregorianCalendar(final TimeZone timezone, Locale aLocale, final XMLGregorianCalendar defaults) {
        GregorianCalendar result = null;
        TimeZone tz = timezone;
        if (tz == null) {
            int defaultZoneoffset = Integer.MIN_VALUE;
            if (defaults != null) {
                defaultZoneoffset = defaults.getTimezone();
            }
            tz = this.getTimeZone(defaultZoneoffset);
        }
        if (aLocale == null) {
            aLocale = Locale.getDefault();
        }
        result = new GregorianCalendar(tz, aLocale);
        result.clear();
        result.setGregorianChange(XMLGregorianCalendarImpl.PURE_GREGORIAN_CHANGE);
        final BigInteger year = this.getEonAndYear();
        if (year != null) {
            result.set(0, (year.signum() != -1) ? 1 : 0);
            result.set(1, year.abs().intValue());
        }
        else {
            final BigInteger defaultYear = (defaults != null) ? defaults.getEonAndYear() : null;
            if (defaultYear != null) {
                result.set(0, (defaultYear.signum() != -1) ? 1 : 0);
                result.set(1, defaultYear.abs().intValue());
            }
        }
        if (this.month != Integer.MIN_VALUE) {
            result.set(2, this.month - 1);
        }
        else {
            final int defaultMonth = (defaults != null) ? defaults.getMonth() : Integer.MIN_VALUE;
            if (defaultMonth != Integer.MIN_VALUE) {
                result.set(2, defaultMonth - 1);
            }
        }
        if (this.day != Integer.MIN_VALUE) {
            result.set(5, this.day);
        }
        else {
            final int defaultDay = (defaults != null) ? defaults.getDay() : Integer.MIN_VALUE;
            if (defaultDay != Integer.MIN_VALUE) {
                result.set(5, defaultDay);
            }
        }
        if (this.hour != Integer.MIN_VALUE) {
            result.set(11, this.hour);
        }
        else {
            final int defaultHour = (defaults != null) ? defaults.getHour() : Integer.MIN_VALUE;
            if (defaultHour != Integer.MIN_VALUE) {
                result.set(11, defaultHour);
            }
        }
        if (this.minute != Integer.MIN_VALUE) {
            result.set(12, this.minute);
        }
        else {
            final int defaultMinute = (defaults != null) ? defaults.getMinute() : Integer.MIN_VALUE;
            if (defaultMinute != Integer.MIN_VALUE) {
                result.set(12, defaultMinute);
            }
        }
        if (this.second != Integer.MIN_VALUE) {
            result.set(13, this.second);
        }
        else {
            final int defaultSecond = (defaults != null) ? defaults.getSecond() : Integer.MIN_VALUE;
            if (defaultSecond != Integer.MIN_VALUE) {
                result.set(13, defaultSecond);
            }
        }
        if (this.fractionalSecond != null) {
            result.set(14, this.getMillisecond());
        }
        else {
            final BigDecimal defaultFractionalSecond = (defaults != null) ? defaults.getFractionalSecond() : null;
            if (defaultFractionalSecond != null) {
                result.set(14, defaults.getMillisecond());
            }
        }
        return result;
    }
    
    @Override
    public TimeZone getTimeZone(final int defaultZoneoffset) {
        TimeZone result = null;
        int zoneoffset = this.getTimezone();
        if (zoneoffset == Integer.MIN_VALUE) {
            zoneoffset = defaultZoneoffset;
        }
        if (zoneoffset == Integer.MIN_VALUE) {
            result = TimeZone.getDefault();
        }
        else {
            final char sign = (zoneoffset < 0) ? '-' : '+';
            if (sign == '-') {
                zoneoffset = -zoneoffset;
            }
            final int hour = zoneoffset / 60;
            final int minutes = zoneoffset - hour * 60;
            final StringBuffer customTimezoneId = new StringBuffer(8);
            customTimezoneId.append("GMT");
            customTimezoneId.append(sign);
            customTimezoneId.append(hour);
            if (minutes != 0) {
                if (minutes < 10) {
                    customTimezoneId.append('0');
                }
                customTimezoneId.append(minutes);
            }
            result = TimeZone.getTimeZone(customTimezoneId.toString());
        }
        return result;
    }
    
    @Override
    public Object clone() {
        return new XMLGregorianCalendarImpl(this.getEonAndYear(), this.month, this.day, this.hour, this.minute, this.second, this.fractionalSecond, this.timezone);
    }
    
    @Override
    public void clear() {
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
    }
    
    @Override
    public void setMillisecond(final int millisecond) {
        if (millisecond == Integer.MIN_VALUE) {
            this.fractionalSecond = null;
        }
        else {
            if ((millisecond < 0 || 999 < millisecond) && millisecond != Integer.MIN_VALUE) {
                this.invalidFieldValue(6, millisecond);
            }
            this.fractionalSecond = new BigDecimal((long)millisecond).movePointLeft(3);
        }
    }
    
    @Override
    public void setFractionalSecond(final BigDecimal fractional) {
        if (fractional != null && (fractional.compareTo(XMLGregorianCalendarImpl.DECIMAL_ZERO) < 0 || fractional.compareTo(XMLGregorianCalendarImpl.DECIMAL_ONE) > 0)) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFractional", new Object[] { fractional.toString() }));
        }
        this.fractionalSecond = fractional;
    }
    
    private static boolean isDigit(final char ch) {
        return '0' <= ch && ch <= '9';
    }
    
    private String format(final String format) {
        char[] buf = new char[32];
        int bufPtr = 0;
        int fidx = 0;
        final int flen = format.length();
        while (fidx < flen) {
            final char fch = format.charAt(fidx++);
            if (fch != '%') {
                buf[bufPtr++] = fch;
            }
            else {
                switch (format.charAt(fidx++)) {
                    case 'Y': {
                        if (this.eon == null) {
                            int y = this.getYear();
                            if (y < 0) {
                                buf[bufPtr++] = '-';
                                y = -y;
                            }
                            bufPtr = this.print4Number(buf, bufPtr, y);
                            continue;
                        }
                        final String s = this.getEonAndYear().toString();
                        final char[] n = new char[buf.length + s.length()];
                        System.arraycopy(buf, 0, n, 0, bufPtr);
                        buf = n;
                        for (int i = s.length(); i < 4; ++i) {
                            buf[bufPtr++] = '0';
                        }
                        s.getChars(0, s.length(), buf, bufPtr);
                        bufPtr += s.length();
                        continue;
                    }
                    case 'M': {
                        bufPtr = this.print2Number(buf, bufPtr, this.getMonth());
                        continue;
                    }
                    case 'D': {
                        bufPtr = this.print2Number(buf, bufPtr, this.getDay());
                        continue;
                    }
                    case 'h': {
                        bufPtr = this.print2Number(buf, bufPtr, this.getHour());
                        continue;
                    }
                    case 'm': {
                        bufPtr = this.print2Number(buf, bufPtr, this.getMinute());
                        continue;
                    }
                    case 's': {
                        bufPtr = this.print2Number(buf, bufPtr, this.getSecond());
                        if (this.getFractionalSecond() != null) {
                            String frac = this.getFractionalSecond().toString();
                            int pos = frac.indexOf("E-");
                            if (pos >= 0) {
                                final String zeros = frac.substring(pos + 2);
                                frac = frac.substring(0, pos);
                                pos = frac.indexOf(".");
                                if (pos >= 0) {
                                    frac = frac.substring(0, pos) + frac.substring(pos + 1);
                                }
                                int count = Integer.parseInt(zeros);
                                if (count < 40) {
                                    frac = "00000000000000000000000000000000000000000".substring(0, count - 1) + frac;
                                }
                                else {
                                    while (count > 1) {
                                        frac = "0" + frac;
                                        --count;
                                    }
                                }
                                frac = "0." + frac;
                            }
                            final char[] n2 = new char[buf.length + frac.length()];
                            System.arraycopy(buf, 0, n2, 0, bufPtr);
                            buf = n2;
                            frac.getChars(1, frac.length(), buf, bufPtr);
                            bufPtr += frac.length() - 1;
                            continue;
                        }
                        continue;
                    }
                    case 'z': {
                        int offset = this.getTimezone();
                        if (offset == 0) {
                            buf[bufPtr++] = 'Z';
                            continue;
                        }
                        if (offset != Integer.MIN_VALUE) {
                            if (offset < 0) {
                                buf[bufPtr++] = '-';
                                offset *= -1;
                            }
                            else {
                                buf[bufPtr++] = '+';
                            }
                            bufPtr = this.print2Number(buf, bufPtr, offset / 60);
                            buf[bufPtr++] = ':';
                            bufPtr = this.print2Number(buf, bufPtr, offset % 60);
                            continue;
                        }
                        continue;
                    }
                    default: {
                        throw new InternalError();
                    }
                }
            }
        }
        return new String(buf, 0, bufPtr);
    }
    
    private int print2Number(final char[] out, int bufptr, final int number) {
        out[bufptr++] = (char)(48 + number / 10);
        out[bufptr++] = (char)(48 + number % 10);
        return bufptr;
    }
    
    private int print4Number(final char[] out, final int bufptr, int number) {
        out[bufptr + 3] = (char)(48 + number % 10);
        number /= 10;
        out[bufptr + 2] = (char)(48 + number % 10);
        number /= 10;
        out[bufptr + 1] = (char)(48 + number % 10);
        number /= 10;
        out[bufptr] = (char)(48 + number % 10);
        return bufptr + 4;
    }
    
    static BigInteger sanitize(final Number value, final int signum) {
        if (signum == 0 || value == null) {
            return BigInteger.ZERO;
        }
        return (BigInteger)((signum < 0) ? ((BigInteger)value).negate() : value);
    }
    
    @Override
    public void reset() {
    }
    
    static {
        BILLION = new BigInteger("1000000000");
        PURE_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
        FIELD_NAME = new String[] { "Year", "Month", "Day", "Hour", "Minute", "Second", "Millisecond", "Timezone" };
        LEAP_YEAR_DEFAULT = createDateTime(400, 1, 1, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE);
        FOUR = BigInteger.valueOf(4L);
        HUNDRED = BigInteger.valueOf(100L);
        FOUR_HUNDRED = BigInteger.valueOf(400L);
        SIXTY = BigInteger.valueOf(60L);
        TWENTY_FOUR = BigInteger.valueOf(24L);
        TWELVE = BigInteger.valueOf(12L);
        DECIMAL_ZERO = new BigDecimal("0");
        DECIMAL_ONE = new BigDecimal("1");
        DECIMAL_SIXTY = new BigDecimal("60");
        XMLGregorianCalendarImpl.daysInMonth = new int[] { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    }
    
    private final class Parser
    {
        private final String format;
        private final String value;
        private final int flen;
        private final int vlen;
        private int fidx;
        private int vidx;
        
        private Parser(final String format, final String value) {
            this.format = format;
            this.value = value;
            this.flen = format.length();
            this.vlen = value.length();
        }
        
        public void parse() throws IllegalArgumentException {
            while (this.fidx < this.flen) {
                final char fch = this.format.charAt(this.fidx++);
                if (fch != '%') {
                    this.skip(fch);
                }
                else {
                    switch (this.format.charAt(this.fidx++)) {
                        case 'Y': {
                            this.parseAndSetYear(4);
                            continue;
                        }
                        case 'M': {
                            XMLGregorianCalendarImpl.this.setMonth(this.parseInt(2, 2));
                            continue;
                        }
                        case 'D': {
                            XMLGregorianCalendarImpl.this.setDay(this.parseInt(2, 2));
                            continue;
                        }
                        case 'h': {
                            XMLGregorianCalendarImpl.this.setHour(this.parseInt(2, 2), false);
                            continue;
                        }
                        case 'm': {
                            XMLGregorianCalendarImpl.this.setMinute(this.parseInt(2, 2));
                            continue;
                        }
                        case 's': {
                            XMLGregorianCalendarImpl.this.setSecond(this.parseInt(2, 2));
                            if (this.peek() == '.') {
                                XMLGregorianCalendarImpl.this.setFractionalSecond(this.parseBigDecimal());
                                continue;
                            }
                            continue;
                        }
                        case 'z': {
                            final char vch = this.peek();
                            if (vch == 'Z') {
                                ++this.vidx;
                                XMLGregorianCalendarImpl.this.setTimezone(0);
                                continue;
                            }
                            if (vch == '+' || vch == '-') {
                                ++this.vidx;
                                final int h = this.parseInt(2, 2);
                                this.skip(':');
                                final int m = this.parseInt(2, 2);
                                XMLGregorianCalendarImpl.this.setTimezone((h * 60 + m) * ((vch == '+') ? 1 : -1));
                                continue;
                            }
                            continue;
                        }
                        default: {
                            throw new InternalError();
                        }
                    }
                }
            }
            if (this.vidx != this.vlen) {
                throw new IllegalArgumentException(this.value);
            }
            XMLGregorianCalendarImpl.this.testHour();
        }
        
        private char peek() throws IllegalArgumentException {
            if (this.vidx == this.vlen) {
                return '\uffff';
            }
            return this.value.charAt(this.vidx);
        }
        
        private char read() throws IllegalArgumentException {
            if (this.vidx == this.vlen) {
                throw new IllegalArgumentException(this.value);
            }
            return this.value.charAt(this.vidx++);
        }
        
        private void skip(final char ch) throws IllegalArgumentException {
            if (this.read() != ch) {
                throw new IllegalArgumentException(this.value);
            }
        }
        
        private int parseInt(final int minDigits, final int maxDigits) throws IllegalArgumentException {
            int n = 0;
            final int vstart = this.vidx;
            char ch;
            while (isDigit(ch = this.peek()) && this.vidx - vstart <= maxDigits) {
                ++this.vidx;
                n = n * 10 + ch - 48;
            }
            if (this.vidx - vstart < minDigits) {
                throw new IllegalArgumentException(this.value);
            }
            return n;
        }
        
        private void parseAndSetYear(final int minDigits) throws IllegalArgumentException {
            final int vstart = this.vidx;
            int n = 0;
            boolean neg = false;
            if (this.peek() == '-') {
                ++this.vidx;
                neg = true;
            }
            while (true) {
                final char ch = this.peek();
                if (!isDigit(ch)) {
                    break;
                }
                ++this.vidx;
                n = n * 10 + ch - 48;
            }
            if (this.vidx - vstart < minDigits) {
                throw new IllegalArgumentException(this.value);
            }
            if (this.vidx - vstart < 7) {
                if (neg) {
                    n = -n;
                }
                XMLGregorianCalendarImpl.this.year = n;
                XMLGregorianCalendarImpl.this.eon = null;
            }
            else {
                XMLGregorianCalendarImpl.this.setYear(new BigInteger(this.value.substring(vstart, this.vidx)));
            }
        }
        
        private BigDecimal parseBigDecimal() throws IllegalArgumentException {
            final int vstart = this.vidx;
            if (this.peek() == '.') {
                ++this.vidx;
                while (isDigit(this.peek())) {
                    ++this.vidx;
                }
                return new BigDecimal(this.value.substring(vstart, this.vidx));
            }
            throw new IllegalArgumentException(this.value);
        }
    }
}

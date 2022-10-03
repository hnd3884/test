package org.apache.xerces.jaxp.datatype;

import java.io.IOException;
import java.util.TimeZone;
import javax.xml.namespace.QName;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.apache.xerces.util.DatatypeMessageFormatter;
import java.util.Date;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.Serializable;
import javax.xml.datatype.XMLGregorianCalendar;

class XMLGregorianCalendarImpl extends XMLGregorianCalendar implements Serializable, Cloneable
{
    private static final long serialVersionUID = 3905403108073447394L;
    private BigInteger orig_eon;
    private int orig_year;
    private int orig_month;
    private int orig_day;
    private int orig_hour;
    private int orig_minute;
    private int orig_second;
    private BigDecimal orig_fracSeconds;
    private int orig_timezone;
    private BigInteger eon;
    private int year;
    private int month;
    private int day;
    private int timezone;
    private int hour;
    private int minute;
    private int second;
    private BigDecimal fractionalSecond;
    private static final BigInteger BILLION_B;
    private static final int BILLION_I = 1000000000;
    private static final Date PURE_GREGORIAN_CHANGE;
    private static final int YEAR = 0;
    private static final int MONTH = 1;
    private static final int DAY = 2;
    private static final int HOUR = 3;
    private static final int MINUTE = 4;
    private static final int SECOND = 5;
    private static final int MILLISECOND = 6;
    private static final int TIMEZONE = 7;
    private static final int[] MIN_FIELD_VALUE;
    private static final int[] MAX_FIELD_VALUE;
    private static final String[] FIELD_NAME;
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
    
    protected XMLGregorianCalendarImpl(final String s) throws IllegalArgumentException {
        this.orig_year = Integer.MIN_VALUE;
        this.orig_month = Integer.MIN_VALUE;
        this.orig_day = Integer.MIN_VALUE;
        this.orig_hour = Integer.MIN_VALUE;
        this.orig_minute = Integer.MIN_VALUE;
        this.orig_second = Integer.MIN_VALUE;
        this.orig_timezone = Integer.MIN_VALUE;
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
        int length = s.length();
        String s2 = null;
        Label_0372: {
            if (s.indexOf(84) != -1) {
                s2 = "%Y-%M-%DT%h:%m:%s%z";
            }
            else if (length >= 3 && s.charAt(2) == ':') {
                s2 = "%h:%m:%s%z";
            }
            else if (s.startsWith("--")) {
                if (length >= 3 && s.charAt(2) == '-') {
                    s2 = "---%D%z";
                }
                else {
                    if (length == 4 || (length >= 6 && (s.charAt(4) == '+' || (s.charAt(4) == '-' && (s.charAt(5) == '-' || length == 10))))) {
                        final Parser parser = new Parser("--%M--%z", s);
                        try {
                            parser.parse();
                            if (!this.isValid()) {
                                throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[] { s }));
                            }
                            this.save();
                            return;
                        }
                        catch (final IllegalArgumentException ex) {
                            s2 = "--%M%z";
                            break Label_0372;
                        }
                    }
                    s2 = "--%M-%D%z";
                }
            }
            else {
                int n = 0;
                if (s.indexOf(58) != -1) {
                    length -= 6;
                }
                for (int i = 1; i < length; ++i) {
                    if (s.charAt(i) == '-') {
                        ++n;
                    }
                }
                if (n == 0) {
                    s2 = "%Y%z";
                }
                else if (n == 1) {
                    s2 = "%Y-%M%z";
                }
                else {
                    s2 = "%Y-%M-%D%z";
                }
            }
        }
        new Parser(s2, s).parse();
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCRepresentation", new Object[] { s }));
        }
        this.save();
    }
    
    private void save() {
        this.orig_eon = this.eon;
        this.orig_year = this.year;
        this.orig_month = this.month;
        this.orig_day = this.day;
        this.orig_hour = this.hour;
        this.orig_minute = this.minute;
        this.orig_second = this.second;
        this.orig_fracSeconds = this.fractionalSecond;
        this.orig_timezone = this.timezone;
    }
    
    public XMLGregorianCalendarImpl() {
        this.orig_year = Integer.MIN_VALUE;
        this.orig_month = Integer.MIN_VALUE;
        this.orig_day = Integer.MIN_VALUE;
        this.orig_hour = Integer.MIN_VALUE;
        this.orig_minute = Integer.MIN_VALUE;
        this.orig_second = Integer.MIN_VALUE;
        this.orig_timezone = Integer.MIN_VALUE;
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
    
    protected XMLGregorianCalendarImpl(final BigInteger year, final int month, final int day, final int n, final int n2, final int n3, final BigDecimal bigDecimal, final int timezone) {
        this.orig_year = Integer.MIN_VALUE;
        this.orig_month = Integer.MIN_VALUE;
        this.orig_day = Integer.MIN_VALUE;
        this.orig_hour = Integer.MIN_VALUE;
        this.orig_minute = Integer.MIN_VALUE;
        this.orig_second = Integer.MIN_VALUE;
        this.orig_timezone = Integer.MIN_VALUE;
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
        this.setTime(n, n2, n3, bigDecimal);
        this.setTimezone(timezone);
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-fractional", new Object[] { year, new Integer(month), new Integer(day), new Integer(n), new Integer(n2), new Integer(n3), bigDecimal, new Integer(timezone) }));
        }
        this.save();
    }
    
    private XMLGregorianCalendarImpl(final int year, final int month, final int day, final int n, final int n2, final int n3, final int n4, final int timezone) {
        this.orig_year = Integer.MIN_VALUE;
        this.orig_month = Integer.MIN_VALUE;
        this.orig_day = Integer.MIN_VALUE;
        this.orig_hour = Integer.MIN_VALUE;
        this.orig_minute = Integer.MIN_VALUE;
        this.orig_second = Integer.MIN_VALUE;
        this.orig_timezone = Integer.MIN_VALUE;
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
        this.setTime(n, n2, n3);
        this.setTimezone(timezone);
        BigDecimal value = null;
        if (n4 != Integer.MIN_VALUE) {
            value = BigDecimal.valueOf(n4, 3);
        }
        this.setFractionalSecond(value);
        if (!this.isValid()) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidXGCValue-milli", new Object[] { new Integer(year), new Integer(month), new Integer(day), new Integer(n), new Integer(n2), new Integer(n3), new Integer(n4), new Integer(timezone) }));
        }
        this.save();
    }
    
    public XMLGregorianCalendarImpl(final GregorianCalendar gregorianCalendar) {
        this.orig_year = Integer.MIN_VALUE;
        this.orig_month = Integer.MIN_VALUE;
        this.orig_day = Integer.MIN_VALUE;
        this.orig_hour = Integer.MIN_VALUE;
        this.orig_minute = Integer.MIN_VALUE;
        this.orig_second = Integer.MIN_VALUE;
        this.orig_timezone = Integer.MIN_VALUE;
        this.eon = null;
        this.year = Integer.MIN_VALUE;
        this.month = Integer.MIN_VALUE;
        this.day = Integer.MIN_VALUE;
        this.timezone = Integer.MIN_VALUE;
        this.hour = Integer.MIN_VALUE;
        this.minute = Integer.MIN_VALUE;
        this.second = Integer.MIN_VALUE;
        this.fractionalSecond = null;
        int value = gregorianCalendar.get(1);
        if (gregorianCalendar.get(0) == 0) {
            value = -value;
        }
        this.setYear(value);
        this.setMonth(gregorianCalendar.get(2) + 1);
        this.setDay(gregorianCalendar.get(5));
        this.setTime(gregorianCalendar.get(11), gregorianCalendar.get(12), gregorianCalendar.get(13), gregorianCalendar.get(14));
        this.setTimezone((gregorianCalendar.get(15) + gregorianCalendar.get(16)) / 60000);
        this.save();
    }
    
    public static XMLGregorianCalendar createDateTime(final BigInteger bigInteger, final int n, final int n2, final int n3, final int n4, final int n5, final BigDecimal bigDecimal, final int n6) {
        return new XMLGregorianCalendarImpl(bigInteger, n, n2, n3, n4, n5, bigDecimal, n6);
    }
    
    public static XMLGregorianCalendar createDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return new XMLGregorianCalendarImpl(n, n2, n3, n4, n5, n6, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }
    
    public static XMLGregorianCalendar createDateTime(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        return new XMLGregorianCalendarImpl(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    public static XMLGregorianCalendar createDate(final int n, final int n2, final int n3, final int n4) {
        return new XMLGregorianCalendarImpl(n, n2, n3, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n4);
    }
    
    public static XMLGregorianCalendar createTime(final int n, final int n2, final int n3, final int n4) {
        return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, Integer.MIN_VALUE, n4);
    }
    
    public static XMLGregorianCalendar createTime(final int n, final int n2, final int n3, final BigDecimal bigDecimal, final int n4) {
        return new XMLGregorianCalendarImpl(null, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, bigDecimal, n4);
    }
    
    public static XMLGregorianCalendar createTime(final int n, final int n2, final int n3, final int n4, final int n5) {
        return new XMLGregorianCalendarImpl(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, n, n2, n3, n4, n5);
    }
    
    public BigInteger getEon() {
        return this.eon;
    }
    
    public int getYear() {
        return this.year;
    }
    
    public BigInteger getEonAndYear() {
        if (this.year != Integer.MIN_VALUE && this.eon != null) {
            return this.eon.add(BigInteger.valueOf(this.year));
        }
        if (this.year != Integer.MIN_VALUE && this.eon == null) {
            return BigInteger.valueOf(this.year);
        }
        return null;
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public int getDay() {
        return this.day;
    }
    
    public int getTimezone() {
        return this.timezone;
    }
    
    public int getHour() {
        return this.hour;
    }
    
    public int getMinute() {
        return this.minute;
    }
    
    public int getSecond() {
        return this.second;
    }
    
    private BigDecimal getSeconds() {
        if (this.second == Integer.MIN_VALUE) {
            return XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        final BigDecimal value = BigDecimal.valueOf(this.second);
        if (this.fractionalSecond != null) {
            return value.add(this.fractionalSecond);
        }
        return value;
    }
    
    public int getMillisecond() {
        if (this.fractionalSecond == null) {
            return Integer.MIN_VALUE;
        }
        return this.fractionalSecond.movePointRight(3).intValue();
    }
    
    public BigDecimal getFractionalSecond() {
        return this.fractionalSecond;
    }
    
    public void setYear(final BigInteger bigInteger) {
        if (bigInteger == null) {
            this.eon = null;
            this.year = Integer.MIN_VALUE;
        }
        else {
            final BigInteger remainder = bigInteger.remainder(XMLGregorianCalendarImpl.BILLION_B);
            this.year = remainder.intValue();
            this.setEon(bigInteger.subtract(remainder));
        }
    }
    
    public void setYear(final int year) {
        if (year == Integer.MIN_VALUE) {
            this.year = Integer.MIN_VALUE;
            this.eon = null;
        }
        else if (Math.abs(year) < 1000000000) {
            this.year = year;
            this.eon = null;
        }
        else {
            final BigInteger value = BigInteger.valueOf(year);
            final BigInteger remainder = value.remainder(XMLGregorianCalendarImpl.BILLION_B);
            this.year = remainder.intValue();
            this.setEon(value.subtract(remainder));
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
    
    public void setMonth(final int month) {
        this.checkFieldValueConstraint(1, month);
        this.month = month;
    }
    
    public void setDay(final int day) {
        this.checkFieldValueConstraint(2, day);
        this.day = day;
    }
    
    public void setTimezone(final int timezone) {
        this.checkFieldValueConstraint(7, timezone);
        this.timezone = timezone;
    }
    
    public void setTime(final int n, final int n2, final int n3) {
        this.setTime(n, n2, n3, null);
    }
    
    private void checkFieldValueConstraint(final int n, final int n2) throws IllegalArgumentException {
        if ((n2 < XMLGregorianCalendarImpl.MIN_FIELD_VALUE[n] && n2 != Integer.MIN_VALUE) || n2 > XMLGregorianCalendarImpl.MAX_FIELD_VALUE[n]) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFieldValue", new Object[] { new Integer(n2), XMLGregorianCalendarImpl.FIELD_NAME[n] }));
        }
    }
    
    public void setHour(final int hour) {
        this.checkFieldValueConstraint(3, hour);
        this.hour = hour;
    }
    
    public void setMinute(final int minute) {
        this.checkFieldValueConstraint(4, minute);
        this.minute = minute;
    }
    
    public void setSecond(final int second) {
        this.checkFieldValueConstraint(5, second);
        this.second = second;
    }
    
    public void setTime(final int hour, final int minute, final int second, final BigDecimal fractionalSecond) {
        this.setHour(hour);
        this.setMinute(minute);
        this.setSecond(second);
        this.setFractionalSecond(fractionalSecond);
    }
    
    public void setTime(final int hour, final int minute, final int second, final int millisecond) {
        this.setHour(hour);
        this.setMinute(minute);
        this.setSecond(second);
        this.setMillisecond(millisecond);
    }
    
    public int compare(final XMLGregorianCalendar xmlGregorianCalendar) {
        XMLGregorianCalendarImpl xmlGregorianCalendarImpl = this;
        XMLGregorianCalendar xmlGregorianCalendar2 = xmlGregorianCalendar;
        if (xmlGregorianCalendarImpl.getTimezone() == xmlGregorianCalendar2.getTimezone()) {
            return internalCompare(xmlGregorianCalendarImpl, xmlGregorianCalendar2);
        }
        if (xmlGregorianCalendarImpl.getTimezone() != Integer.MIN_VALUE && xmlGregorianCalendar2.getTimezone() != Integer.MIN_VALUE) {
            return internalCompare(xmlGregorianCalendarImpl.normalize(), xmlGregorianCalendar2.normalize());
        }
        if (xmlGregorianCalendarImpl.getTimezone() != Integer.MIN_VALUE) {
            if (xmlGregorianCalendarImpl.getTimezone() != 0) {
                xmlGregorianCalendarImpl = (XMLGregorianCalendarImpl)xmlGregorianCalendarImpl.normalize();
            }
            final int internalCompare = internalCompare(xmlGregorianCalendarImpl, this.normalizeToTimezone(xmlGregorianCalendar2, 840));
            if (internalCompare == -1) {
                return internalCompare;
            }
            final int internalCompare2 = internalCompare(xmlGregorianCalendarImpl, this.normalizeToTimezone(xmlGregorianCalendar2, -840));
            if (internalCompare2 == 1) {
                return internalCompare2;
            }
            return 2;
        }
        else {
            if (xmlGregorianCalendar2.getTimezone() != 0) {
                xmlGregorianCalendar2 = this.normalizeToTimezone(xmlGregorianCalendar2, xmlGregorianCalendar2.getTimezone());
            }
            final int internalCompare3 = internalCompare(this.normalizeToTimezone(xmlGregorianCalendarImpl, -840), xmlGregorianCalendar2);
            if (internalCompare3 == -1) {
                return internalCompare3;
            }
            final int internalCompare4 = internalCompare(this.normalizeToTimezone(xmlGregorianCalendarImpl, 840), xmlGregorianCalendar2);
            if (internalCompare4 == 1) {
                return internalCompare4;
            }
            return 2;
        }
    }
    
    public XMLGregorianCalendar normalize() {
        final XMLGregorianCalendar normalizeToTimezone = this.normalizeToTimezone(this, this.timezone);
        if (this.getTimezone() == Integer.MIN_VALUE) {
            normalizeToTimezone.setTimezone(Integer.MIN_VALUE);
        }
        if (this.getMillisecond() == Integer.MIN_VALUE) {
            normalizeToTimezone.setMillisecond(Integer.MIN_VALUE);
        }
        return normalizeToTimezone;
    }
    
    private XMLGregorianCalendar normalizeToTimezone(final XMLGregorianCalendar xmlGregorianCalendar, final int n) {
        final XMLGregorianCalendar xmlGregorianCalendar2 = (XMLGregorianCalendar)xmlGregorianCalendar.clone();
        final int n2 = -n;
        xmlGregorianCalendar2.add(new DurationImpl(n2 >= 0, 0, 0, 0, 0, (n2 < 0) ? (-n2) : n2, 0));
        xmlGregorianCalendar2.setTimezone(0);
        return xmlGregorianCalendar2;
    }
    
    private static int internalCompare(final XMLGregorianCalendar xmlGregorianCalendar, final XMLGregorianCalendar xmlGregorianCalendar2) {
        if (xmlGregorianCalendar.getEon() == xmlGregorianCalendar2.getEon()) {
            final int compareField = compareField(xmlGregorianCalendar.getYear(), xmlGregorianCalendar2.getYear());
            if (compareField != 0) {
                return compareField;
            }
        }
        else {
            final int compareField2 = compareField(xmlGregorianCalendar.getEonAndYear(), xmlGregorianCalendar2.getEonAndYear());
            if (compareField2 != 0) {
                return compareField2;
            }
        }
        final int compareField3 = compareField(xmlGregorianCalendar.getMonth(), xmlGregorianCalendar2.getMonth());
        if (compareField3 != 0) {
            return compareField3;
        }
        final int compareField4 = compareField(xmlGregorianCalendar.getDay(), xmlGregorianCalendar2.getDay());
        if (compareField4 != 0) {
            return compareField4;
        }
        final int compareField5 = compareField(xmlGregorianCalendar.getHour(), xmlGregorianCalendar2.getHour());
        if (compareField5 != 0) {
            return compareField5;
        }
        final int compareField6 = compareField(xmlGregorianCalendar.getMinute(), xmlGregorianCalendar2.getMinute());
        if (compareField6 != 0) {
            return compareField6;
        }
        final int compareField7 = compareField(xmlGregorianCalendar.getSecond(), xmlGregorianCalendar2.getSecond());
        if (compareField7 != 0) {
            return compareField7;
        }
        return compareField(xmlGregorianCalendar.getFractionalSecond(), xmlGregorianCalendar2.getFractionalSecond());
    }
    
    private static int compareField(final int n, final int n2) {
        if (n == n2) {
            return 0;
        }
        if (n == Integer.MIN_VALUE || n2 == Integer.MIN_VALUE) {
            return 2;
        }
        return (n < n2) ? -1 : 1;
    }
    
    private static int compareField(final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (bigInteger == null) {
            return (bigInteger2 == null) ? 0 : 2;
        }
        if (bigInteger2 == null) {
            return 2;
        }
        return bigInteger.compareTo(bigInteger2);
    }
    
    private static int compareField(BigDecimal decimal_ZERO, BigDecimal decimal_ZERO2) {
        if (decimal_ZERO == decimal_ZERO2) {
            return 0;
        }
        if (decimal_ZERO == null) {
            decimal_ZERO = XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        if (decimal_ZERO2 == null) {
            decimal_ZERO2 = XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        return decimal_ZERO.compareTo(decimal_ZERO2);
    }
    
    public boolean equals(final Object o) {
        return o == this || (o instanceof XMLGregorianCalendar && this.compare((XMLGregorianCalendar)o) == 0);
    }
    
    public int hashCode() {
        int timezone = this.getTimezone();
        if (timezone == Integer.MIN_VALUE) {
            timezone = 0;
        }
        XMLGregorianCalendar normalizeToTimezone = this;
        if (timezone != 0) {
            normalizeToTimezone = this.normalizeToTimezone(this, this.getTimezone());
        }
        return normalizeToTimezone.getYear() + normalizeToTimezone.getMonth() + normalizeToTimezone.getDay() + normalizeToTimezone.getHour() + normalizeToTimezone.getMinute() + normalizeToTimezone.getSecond();
    }
    
    public static XMLGregorianCalendar parse(final String s) {
        return new XMLGregorianCalendarImpl(s);
    }
    
    public String toXMLFormat() {
        final QName xmlSchemaType = this.getXMLSchemaType();
        String s = null;
        if (xmlSchemaType == DatatypeConstants.DATETIME) {
            s = "%Y-%M-%DT%h:%m:%s%z";
        }
        else if (xmlSchemaType == DatatypeConstants.DATE) {
            s = "%Y-%M-%D%z";
        }
        else if (xmlSchemaType == DatatypeConstants.TIME) {
            s = "%h:%m:%s%z";
        }
        else if (xmlSchemaType == DatatypeConstants.GMONTH) {
            s = "--%M--%z";
        }
        else if (xmlSchemaType == DatatypeConstants.GDAY) {
            s = "---%D%z";
        }
        else if (xmlSchemaType == DatatypeConstants.GYEAR) {
            s = "%Y%z";
        }
        else if (xmlSchemaType == DatatypeConstants.GYEARMONTH) {
            s = "%Y-%M%z";
        }
        else if (xmlSchemaType == DatatypeConstants.GMONTHDAY) {
            s = "--%M-%D%z";
        }
        return this.format(s);
    }
    
    public QName getXMLSchemaType() {
        if (this.year != Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour != Integer.MIN_VALUE && this.minute != Integer.MIN_VALUE && this.second != Integer.MIN_VALUE) {
            return DatatypeConstants.DATETIME;
        }
        if (this.year != Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.DATE;
        }
        if (this.year == Integer.MIN_VALUE && this.month == Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour != Integer.MIN_VALUE && this.minute != Integer.MIN_VALUE && this.second != Integer.MIN_VALUE) {
            return DatatypeConstants.TIME;
        }
        if (this.year != Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GYEARMONTH;
        }
        if (this.year == Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GMONTHDAY;
        }
        if (this.year != Integer.MIN_VALUE && this.month == Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GYEAR;
        }
        if (this.year == Integer.MIN_VALUE && this.month != Integer.MIN_VALUE && this.day == Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GMONTH;
        }
        if (this.year == Integer.MIN_VALUE && this.month == Integer.MIN_VALUE && this.day != Integer.MIN_VALUE && this.hour == Integer.MIN_VALUE && this.minute == Integer.MIN_VALUE && this.second == Integer.MIN_VALUE) {
            return DatatypeConstants.GDAY;
        }
        throw new IllegalStateException(this.getClass().getName() + "#getXMLSchemaType() :" + DatatypeMessageFormatter.formatMessage(null, "InvalidXGCFields", null));
    }
    
    public boolean isValid() {
        if (this.month != Integer.MIN_VALUE && this.day != Integer.MIN_VALUE) {
            if (this.year != Integer.MIN_VALUE) {
                if (this.eon == null) {
                    if (this.day > maximumDayInMonthFor(this.year, this.month)) {
                        return false;
                    }
                }
                else if (this.day > maximumDayInMonthFor(this.getEonAndYear(), this.month)) {
                    return false;
                }
            }
            else if (this.day > maximumDayInMonthFor(2000, this.month)) {
                return false;
            }
        }
        return (this.hour != 24 || (this.minute == 0 && this.second == 0 && (this.fractionalSecond == null || this.fractionalSecond.compareTo(XMLGregorianCalendarImpl.DECIMAL_ZERO) == 0))) && (this.eon != null || this.year != 0);
    }
    
    public void add(final Duration duration) {
        final boolean[] array = { false, false, false, false, false, false };
        final int sign = duration.getSign();
        int month = this.getMonth();
        if (month == Integer.MIN_VALUE) {
            month = XMLGregorianCalendarImpl.MIN_FIELD_VALUE[1];
            array[1] = true;
        }
        final BigInteger add = BigInteger.valueOf(month).add(sanitize(duration.getField(DatatypeConstants.MONTHS), sign));
        this.setMonth(add.subtract(BigInteger.ONE).mod(XMLGregorianCalendarImpl.TWELVE).intValue() + 1);
        final BigInteger bigInteger = new BigDecimal(add.subtract(BigInteger.ONE)).divide(new BigDecimal(XMLGregorianCalendarImpl.TWELVE), 3).toBigInteger();
        BigInteger bigInteger2 = this.getEonAndYear();
        if (bigInteger2 == null) {
            array[0] = true;
            bigInteger2 = BigInteger.ZERO;
        }
        this.setYear(bigInteger2.add(sanitize(duration.getField(DatatypeConstants.YEARS), sign)).add(bigInteger));
        BigDecimal bigDecimal;
        if (this.getSecond() == Integer.MIN_VALUE) {
            array[5] = true;
            bigDecimal = XMLGregorianCalendarImpl.DECIMAL_ZERO;
        }
        else {
            bigDecimal = this.getSeconds();
        }
        final BigDecimal add2 = bigDecimal.add(DurationImpl.sanitize((BigDecimal)duration.getField(DatatypeConstants.SECONDS), sign));
        final BigDecimal bigDecimal2 = new BigDecimal(new BigDecimal(add2.toBigInteger()).divide(XMLGregorianCalendarImpl.DECIMAL_SIXTY, 3).toBigInteger());
        final BigDecimal subtract = add2.subtract(bigDecimal2.multiply(XMLGregorianCalendarImpl.DECIMAL_SIXTY));
        BigInteger bigInteger3 = bigDecimal2.toBigInteger();
        this.setSecond(subtract.intValue());
        final BigDecimal subtract2 = subtract.subtract(new BigDecimal(BigInteger.valueOf(this.getSecond())));
        if (subtract2.compareTo(XMLGregorianCalendarImpl.DECIMAL_ZERO) < 0) {
            this.setFractionalSecond(XMLGregorianCalendarImpl.DECIMAL_ONE.add(subtract2));
            if (this.getSecond() == 0) {
                this.setSecond(59);
                bigInteger3 = bigInteger3.subtract(BigInteger.ONE);
            }
            else {
                this.setSecond(this.getSecond() - 1);
            }
        }
        else {
            this.setFractionalSecond(subtract2);
        }
        int minute = this.getMinute();
        if (minute == Integer.MIN_VALUE) {
            array[4] = true;
            minute = XMLGregorianCalendarImpl.MIN_FIELD_VALUE[4];
        }
        final BigInteger add3 = BigInteger.valueOf(minute).add(sanitize(duration.getField(DatatypeConstants.MINUTES), sign)).add(bigInteger3);
        this.setMinute(add3.mod(XMLGregorianCalendarImpl.SIXTY).intValue());
        final BigInteger bigInteger4 = new BigDecimal(add3).divide(XMLGregorianCalendarImpl.DECIMAL_SIXTY, 3).toBigInteger();
        int hour = this.getHour();
        if (hour == Integer.MIN_VALUE) {
            array[3] = true;
            hour = XMLGregorianCalendarImpl.MIN_FIELD_VALUE[3];
        }
        final BigInteger add4 = BigInteger.valueOf(hour).add(sanitize(duration.getField(DatatypeConstants.HOURS), sign)).add(bigInteger4);
        this.setHour(add4.mod(XMLGregorianCalendarImpl.TWENTY_FOUR).intValue());
        final BigInteger bigInteger5 = new BigDecimal(add4).divide(new BigDecimal(XMLGregorianCalendarImpl.TWENTY_FOUR), 3).toBigInteger();
        int day = this.getDay();
        if (day == Integer.MIN_VALUE) {
            array[2] = true;
            day = XMLGregorianCalendarImpl.MIN_FIELD_VALUE[2];
        }
        final BigInteger sanitize = sanitize(duration.getField(DatatypeConstants.DAYS), sign);
        final int maximumDayInMonth = maximumDayInMonthFor(this.getEonAndYear(), this.getMonth());
        BigInteger bigInteger6;
        if (day > maximumDayInMonth) {
            bigInteger6 = BigInteger.valueOf(maximumDayInMonth);
        }
        else if (day < 1) {
            bigInteger6 = BigInteger.ONE;
        }
        else {
            bigInteger6 = BigInteger.valueOf(day);
        }
        BigInteger bigInteger7 = bigInteger6.add(sanitize).add(bigInteger5);
        while (true) {
            int n;
            if (bigInteger7.compareTo(BigInteger.ONE) < 0) {
                BigInteger bigInteger8;
                if (this.month >= 2) {
                    bigInteger8 = BigInteger.valueOf(maximumDayInMonthFor(this.getEonAndYear(), this.getMonth() - 1));
                }
                else {
                    bigInteger8 = BigInteger.valueOf(maximumDayInMonthFor(this.getEonAndYear().subtract(BigInteger.valueOf(1L)), 12));
                }
                bigInteger7 = bigInteger7.add(bigInteger8);
                n = -1;
            }
            else {
                if (bigInteger7.compareTo(BigInteger.valueOf(maximumDayInMonthFor(this.getEonAndYear(), this.getMonth()))) <= 0) {
                    break;
                }
                bigInteger7 = bigInteger7.add(BigInteger.valueOf(-maximumDayInMonthFor(this.getEonAndYear(), this.getMonth())));
                n = 1;
            }
            final int n2 = this.getMonth() + n;
            int month2 = (n2 - 1) % 12;
            int intValue;
            if (month2 < 0) {
                month2 = 12 + month2 + 1;
                intValue = BigDecimal.valueOf(n2 - 1).divide(new BigDecimal(XMLGregorianCalendarImpl.TWELVE), 0).intValue();
            }
            else {
                intValue = (n2 - 1) / 12;
                ++month2;
            }
            this.setMonth(month2);
            if (intValue != 0) {
                this.setYear(this.getEonAndYear().add(BigInteger.valueOf(intValue)));
            }
        }
        this.setDay(bigInteger7.intValue());
        for (int i = 0; i <= 5; ++i) {
            if (array[i]) {
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
                        this.setHour(Integer.MIN_VALUE);
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
    
    private static int maximumDayInMonthFor(final BigInteger bigInteger, final int n) {
        if (n != 2) {
            return DaysInMonth.table[n];
        }
        if (bigInteger.mod(XMLGregorianCalendarImpl.FOUR_HUNDRED).equals(BigInteger.ZERO) || (!bigInteger.mod(XMLGregorianCalendarImpl.HUNDRED).equals(BigInteger.ZERO) && bigInteger.mod(XMLGregorianCalendarImpl.FOUR).equals(BigInteger.ZERO))) {
            return 29;
        }
        return DaysInMonth.table[n];
    }
    
    private static int maximumDayInMonthFor(final int n, final int n2) {
        if (n2 != 2) {
            return DaysInMonth.table[n2];
        }
        if (n % 400 == 0 || (n % 100 != 0 && n % 4 == 0)) {
            return 29;
        }
        return DaysInMonth.table[2];
    }
    
    public GregorianCalendar toGregorianCalendar() {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(this.getTimeZone(Integer.MIN_VALUE), Locale.getDefault());
        gregorianCalendar.clear();
        gregorianCalendar.setGregorianChange(XMLGregorianCalendarImpl.PURE_GREGORIAN_CHANGE);
        if (this.year != Integer.MIN_VALUE) {
            if (this.eon == null) {
                gregorianCalendar.set(0, (this.year >= 0) ? 1 : 0);
                gregorianCalendar.set(1, Math.abs(this.year));
            }
            else {
                final BigInteger eonAndYear = this.getEonAndYear();
                gregorianCalendar.set(0, (eonAndYear.signum() != -1) ? 1 : 0);
                gregorianCalendar.set(1, eonAndYear.abs().intValue());
            }
        }
        if (this.month != Integer.MIN_VALUE) {
            gregorianCalendar.set(2, this.month - 1);
        }
        if (this.day != Integer.MIN_VALUE) {
            gregorianCalendar.set(5, this.day);
        }
        if (this.hour != Integer.MIN_VALUE) {
            gregorianCalendar.set(11, this.hour);
        }
        if (this.minute != Integer.MIN_VALUE) {
            gregorianCalendar.set(12, this.minute);
        }
        if (this.second != Integer.MIN_VALUE) {
            gregorianCalendar.set(13, this.second);
        }
        if (this.fractionalSecond != null) {
            gregorianCalendar.set(14, this.getMillisecond());
        }
        return gregorianCalendar;
    }
    
    public GregorianCalendar toGregorianCalendar(final TimeZone timeZone, Locale default1, final XMLGregorianCalendar xmlGregorianCalendar) {
        TimeZone timeZone2 = timeZone;
        if (timeZone2 == null) {
            int timezone = Integer.MIN_VALUE;
            if (xmlGregorianCalendar != null) {
                timezone = xmlGregorianCalendar.getTimezone();
            }
            timeZone2 = this.getTimeZone(timezone);
        }
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone2, default1);
        gregorianCalendar.clear();
        gregorianCalendar.setGregorianChange(XMLGregorianCalendarImpl.PURE_GREGORIAN_CHANGE);
        if (this.year != Integer.MIN_VALUE) {
            if (this.eon == null) {
                gregorianCalendar.set(0, (this.year >= 0) ? 1 : 0);
                gregorianCalendar.set(1, Math.abs(this.year));
            }
            else {
                final BigInteger eonAndYear = this.getEonAndYear();
                gregorianCalendar.set(0, (eonAndYear.signum() != -1) ? 1 : 0);
                gregorianCalendar.set(1, eonAndYear.abs().intValue());
            }
        }
        else if (xmlGregorianCalendar != null) {
            final int year = xmlGregorianCalendar.getYear();
            if (year != Integer.MIN_VALUE) {
                if (xmlGregorianCalendar.getEon() == null) {
                    gregorianCalendar.set(0, (year >= 0) ? 1 : 0);
                    gregorianCalendar.set(1, Math.abs(year));
                }
                else {
                    final BigInteger eonAndYear2 = xmlGregorianCalendar.getEonAndYear();
                    gregorianCalendar.set(0, (eonAndYear2.signum() != -1) ? 1 : 0);
                    gregorianCalendar.set(1, eonAndYear2.abs().intValue());
                }
            }
        }
        if (this.month != Integer.MIN_VALUE) {
            gregorianCalendar.set(2, this.month - 1);
        }
        else {
            final int n = (xmlGregorianCalendar != null) ? xmlGregorianCalendar.getMonth() : Integer.MIN_VALUE;
            if (n != Integer.MIN_VALUE) {
                gregorianCalendar.set(2, n - 1);
            }
        }
        if (this.day != Integer.MIN_VALUE) {
            gregorianCalendar.set(5, this.day);
        }
        else {
            final int n2 = (xmlGregorianCalendar != null) ? xmlGregorianCalendar.getDay() : Integer.MIN_VALUE;
            if (n2 != Integer.MIN_VALUE) {
                gregorianCalendar.set(5, n2);
            }
        }
        if (this.hour != Integer.MIN_VALUE) {
            gregorianCalendar.set(11, this.hour);
        }
        else {
            final int n3 = (xmlGregorianCalendar != null) ? xmlGregorianCalendar.getHour() : Integer.MIN_VALUE;
            if (n3 != Integer.MIN_VALUE) {
                gregorianCalendar.set(11, n3);
            }
        }
        if (this.minute != Integer.MIN_VALUE) {
            gregorianCalendar.set(12, this.minute);
        }
        else {
            final int n4 = (xmlGregorianCalendar != null) ? xmlGregorianCalendar.getMinute() : Integer.MIN_VALUE;
            if (n4 != Integer.MIN_VALUE) {
                gregorianCalendar.set(12, n4);
            }
        }
        if (this.second != Integer.MIN_VALUE) {
            gregorianCalendar.set(13, this.second);
        }
        else {
            final int n5 = (xmlGregorianCalendar != null) ? xmlGregorianCalendar.getSecond() : Integer.MIN_VALUE;
            if (n5 != Integer.MIN_VALUE) {
                gregorianCalendar.set(13, n5);
            }
        }
        if (this.fractionalSecond != null) {
            gregorianCalendar.set(14, this.getMillisecond());
        }
        else if (((xmlGregorianCalendar != null) ? xmlGregorianCalendar.getFractionalSecond() : null) != null) {
            gregorianCalendar.set(14, xmlGregorianCalendar.getMillisecond());
        }
        return gregorianCalendar;
    }
    
    public TimeZone getTimeZone(final int n) {
        int timezone = this.getTimezone();
        if (timezone == Integer.MIN_VALUE) {
            timezone = n;
        }
        TimeZone timeZone;
        if (timezone == Integer.MIN_VALUE) {
            timeZone = TimeZone.getDefault();
        }
        else {
            final int n2 = (timezone < 0) ? 45 : 43;
            if (n2 == 45) {
                timezone = -timezone;
            }
            final int n3 = timezone / 60;
            final int n4 = timezone - n3 * 60;
            final StringBuffer sb = new StringBuffer(8);
            sb.append("GMT");
            sb.append((char)n2);
            sb.append(n3);
            if (n4 != 0) {
                if (n4 < 10) {
                    sb.append('0');
                }
                sb.append(n4);
            }
            timeZone = TimeZone.getTimeZone(sb.toString());
        }
        return timeZone;
    }
    
    public Object clone() {
        return new XMLGregorianCalendarImpl(this.getEonAndYear(), this.month, this.day, this.hour, this.minute, this.second, this.fractionalSecond, this.timezone);
    }
    
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
    
    public void setMillisecond(final int n) {
        if (n == Integer.MIN_VALUE) {
            this.fractionalSecond = null;
        }
        else {
            this.checkFieldValueConstraint(6, n);
            this.fractionalSecond = BigDecimal.valueOf(n, 3);
        }
    }
    
    public void setFractionalSecond(final BigDecimal fractionalSecond) {
        if (fractionalSecond != null && (fractionalSecond.compareTo(XMLGregorianCalendarImpl.DECIMAL_ZERO) < 0 || fractionalSecond.compareTo(XMLGregorianCalendarImpl.DECIMAL_ONE) > 0)) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "InvalidFractional", new Object[] { fractionalSecond }));
        }
        this.fractionalSecond = fractionalSecond;
    }
    
    private static boolean isDigit(final char c) {
        return '0' <= c && c <= '9';
    }
    
    private String format(final String s) {
        final StringBuffer sb = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            final char char1 = s.charAt(i++);
            if (char1 != '%') {
                sb.append(char1);
            }
            else {
                switch (s.charAt(i++)) {
                    case 'Y': {
                        if (this.eon == null) {
                            int year = this.year;
                            if (year < 0) {
                                sb.append('-');
                                year = -this.year;
                            }
                            this.printNumber(sb, year, 4);
                            continue;
                        }
                        this.printNumber(sb, this.getEonAndYear(), 4);
                        continue;
                    }
                    case 'M': {
                        this.printNumber(sb, this.getMonth(), 2);
                        continue;
                    }
                    case 'D': {
                        this.printNumber(sb, this.getDay(), 2);
                        continue;
                    }
                    case 'h': {
                        this.printNumber(sb, this.getHour(), 2);
                        continue;
                    }
                    case 'm': {
                        this.printNumber(sb, this.getMinute(), 2);
                        continue;
                    }
                    case 's': {
                        this.printNumber(sb, this.getSecond(), 2);
                        if (this.getFractionalSecond() != null) {
                            final String string = this.toString(this.getFractionalSecond());
                            sb.append(string.substring(1, string.length()));
                            continue;
                        }
                        continue;
                    }
                    case 'z': {
                        int timezone = this.getTimezone();
                        if (timezone == 0) {
                            sb.append('Z');
                            continue;
                        }
                        if (timezone != Integer.MIN_VALUE) {
                            if (timezone < 0) {
                                sb.append('-');
                                timezone *= -1;
                            }
                            else {
                                sb.append('+');
                            }
                            this.printNumber(sb, timezone / 60, 2);
                            sb.append(':');
                            this.printNumber(sb, timezone % 60, 2);
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
        return sb.toString();
    }
    
    private void printNumber(final StringBuffer sb, final int n, final int n2) {
        final String value = String.valueOf(n);
        for (int i = value.length(); i < n2; ++i) {
            sb.append('0');
        }
        sb.append(value);
    }
    
    private void printNumber(final StringBuffer sb, final BigInteger bigInteger, final int n) {
        final String string = bigInteger.toString();
        for (int i = string.length(); i < n; ++i) {
            sb.append('0');
        }
        sb.append(string);
    }
    
    private String toString(final BigDecimal bigDecimal) {
        final String string = bigDecimal.unscaledValue().toString();
        final int scale = bigDecimal.scale();
        if (scale == 0) {
            return string;
        }
        final int n = string.length() - scale;
        if (n == 0) {
            return "0." + string;
        }
        StringBuffer sb;
        if (n > 0) {
            sb = new StringBuffer(string);
            sb.insert(n, '.');
        }
        else {
            sb = new StringBuffer(3 - n + string.length());
            sb.append("0.");
            for (int i = 0; i < -n; ++i) {
                sb.append('0');
            }
            sb.append(string);
        }
        return sb.toString();
    }
    
    static BigInteger sanitize(final Number n, final int n2) {
        if (n2 == 0 || n == null) {
            return BigInteger.ZERO;
        }
        return (BigInteger)((n2 < 0) ? ((BigInteger)n).negate() : n);
    }
    
    public void reset() {
        this.eon = this.orig_eon;
        this.year = this.orig_year;
        this.month = this.orig_month;
        this.day = this.orig_day;
        this.hour = this.orig_hour;
        this.minute = this.orig_minute;
        this.second = this.orig_second;
        this.fractionalSecond = this.orig_fracSeconds;
        this.timezone = this.orig_timezone;
    }
    
    private Object writeReplace() throws IOException {
        return new SerializedXMLGregorianCalendar(this.toXMLFormat());
    }
    
    static {
        BILLION_B = BigInteger.valueOf(1000000000L);
        PURE_GREGORIAN_CHANGE = new Date(Long.MIN_VALUE);
        MIN_FIELD_VALUE = new int[] { Integer.MIN_VALUE, 1, 1, 0, 0, 0, 0, -840 };
        MAX_FIELD_VALUE = new int[] { Integer.MAX_VALUE, 12, 31, 24, 59, 60, 999, 840 };
        FIELD_NAME = new String[] { "Year", "Month", "Day", "Hour", "Minute", "Second", "Millisecond", "Timezone" };
        LEAP_YEAR_DEFAULT = createDateTime(400, 1, 1, 0, 0, 0, Integer.MIN_VALUE, Integer.MIN_VALUE);
        FOUR = BigInteger.valueOf(4L);
        HUNDRED = BigInteger.valueOf(100L);
        FOUR_HUNDRED = BigInteger.valueOf(400L);
        SIXTY = BigInteger.valueOf(60L);
        TWENTY_FOUR = BigInteger.valueOf(24L);
        TWELVE = BigInteger.valueOf(12L);
        DECIMAL_ZERO = BigDecimal.valueOf(0L);
        DECIMAL_ONE = BigDecimal.valueOf(1L);
        DECIMAL_SIXTY = BigDecimal.valueOf(60L);
    }
    
    private static class DaysInMonth
    {
        private static final int[] table;
        
        static {
            table = new int[] { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        }
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
                final char char1 = this.format.charAt(this.fidx++);
                if (char1 != '%') {
                    this.skip(char1);
                }
                else {
                    switch (this.format.charAt(this.fidx++)) {
                        case 'Y': {
                            this.parseYear();
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
                            XMLGregorianCalendarImpl.this.setHour(this.parseInt(2, 2));
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
                            final char peek = this.peek();
                            if (peek == 'Z') {
                                ++this.vidx;
                                XMLGregorianCalendarImpl.this.setTimezone(0);
                                continue;
                            }
                            if (peek == '+' || peek == '-') {
                                ++this.vidx;
                                final int int1 = this.parseInt(2, 2);
                                this.skip(':');
                                XMLGregorianCalendarImpl.this.setTimezone((int1 * 60 + this.parseInt(2, 2)) * ((peek == '+') ? 1 : -1));
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
        
        private void skip(final char c) throws IllegalArgumentException {
            if (this.read() != c) {
                throw new IllegalArgumentException(this.value);
            }
        }
        
        private void parseYear() throws IllegalArgumentException {
            final int vidx = this.vidx;
            int n = 0;
            if (this.peek() == '-') {
                ++this.vidx;
                n = 1;
            }
            while (isDigit(this.peek())) {
                ++this.vidx;
            }
            final int n2 = this.vidx - vidx - n;
            if (n2 < 4) {
                throw new IllegalArgumentException(this.value);
            }
            final String substring = this.value.substring(vidx, this.vidx);
            if (n2 < 10) {
                XMLGregorianCalendarImpl.this.setYear(Integer.parseInt(substring));
            }
            else {
                XMLGregorianCalendarImpl.this.setYear(new BigInteger(substring));
            }
        }
        
        private int parseInt(final int n, final int n2) throws IllegalArgumentException {
            final int vidx = this.vidx;
            while (isDigit(this.peek()) && this.vidx - vidx < n2) {
                ++this.vidx;
            }
            if (this.vidx - vidx < n) {
                throw new IllegalArgumentException(this.value);
            }
            return Integer.parseInt(this.value.substring(vidx, this.vidx));
        }
        
        private BigDecimal parseBigDecimal() throws IllegalArgumentException {
            final int vidx = this.vidx;
            if (this.peek() == '.') {
                ++this.vidx;
                while (isDigit(this.peek())) {
                    ++this.vidx;
                }
                return new BigDecimal(this.value.substring(vidx, this.vidx));
            }
            throw new IllegalArgumentException(this.value);
        }
    }
}

package org.apache.xerces.jaxp.datatype;

import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.apache.xerces.util.DatatypeMessageFormatter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeConstants;
import java.io.Serializable;
import javax.xml.datatype.Duration;

class DurationImpl extends Duration implements Serializable
{
    private static final long serialVersionUID = -2650025807136350131L;
    private static final DatatypeConstants.Field[] FIELDS;
    private static final BigDecimal ZERO;
    private final int signum;
    private final BigInteger years;
    private final BigInteger months;
    private final BigInteger days;
    private final BigInteger hours;
    private final BigInteger minutes;
    private final BigDecimal seconds;
    private static final XMLGregorianCalendar[] TEST_POINTS;
    private static final BigDecimal[] FACTORS;
    
    public int getSign() {
        return this.signum;
    }
    
    private int calcSignum(final boolean b) {
        if ((this.years == null || this.years.signum() == 0) && (this.months == null || this.months.signum() == 0) && (this.days == null || this.days.signum() == 0) && (this.hours == null || this.hours.signum() == 0) && (this.minutes == null || this.minutes.signum() == 0) && (this.seconds == null || this.seconds.signum() == 0)) {
            return 0;
        }
        if (b) {
            return 1;
        }
        return -1;
    }
    
    protected DurationImpl(final boolean b, final BigInteger years, final BigInteger months, final BigInteger days, final BigInteger hours, final BigInteger minutes, final BigDecimal seconds) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.signum = this.calcSignum(b);
        if (years == null && months == null && days == null && hours == null && minutes == null && seconds == null) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "AllFieldsNull", null));
        }
        testNonNegative(years, DatatypeConstants.YEARS);
        testNonNegative(months, DatatypeConstants.MONTHS);
        testNonNegative(days, DatatypeConstants.DAYS);
        testNonNegative(hours, DatatypeConstants.HOURS);
        testNonNegative(minutes, DatatypeConstants.MINUTES);
        testNonNegative(seconds, DatatypeConstants.SECONDS);
    }
    
    private static void testNonNegative(final BigInteger bigInteger, final DatatypeConstants.Field field) {
        if (bigInteger != null && bigInteger.signum() < 0) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[] { field.toString() }));
        }
    }
    
    private static void testNonNegative(final BigDecimal bigDecimal, final DatatypeConstants.Field field) {
        if (bigDecimal != null && bigDecimal.signum() < 0) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[] { field.toString() }));
        }
    }
    
    protected DurationImpl(final boolean b, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this(b, wrap(n), wrap(n2), wrap(n3), wrap(n4), wrap(n5), (n6 != 0) ? BigDecimal.valueOf(n6) : null);
    }
    
    private static BigInteger wrap(final int n) {
        if (n == Integer.MIN_VALUE) {
            return null;
        }
        return BigInteger.valueOf(n);
    }
    
    protected DurationImpl(final long n) {
        boolean b = false;
        long n2 = n;
        if (n2 > 0L) {
            this.signum = 1;
        }
        else if (n2 < 0L) {
            this.signum = -1;
            if (n2 == Long.MIN_VALUE) {
                ++n2;
                b = true;
            }
            n2 *= -1L;
        }
        else {
            this.signum = 0;
        }
        this.years = null;
        this.months = null;
        this.seconds = BigDecimal.valueOf(n2 % 60000L + (long)(b ? 1 : 0), 3);
        final long n3 = n2 / 60000L;
        this.minutes = ((n3 == 0L) ? null : BigInteger.valueOf(n3 % 60L));
        final long n4 = n3 / 60L;
        this.hours = ((n4 == 0L) ? null : BigInteger.valueOf(n4 % 24L));
        final long n5 = n4 / 24L;
        this.days = ((n5 == 0L) ? null : BigInteger.valueOf(n5));
    }
    
    protected DurationImpl(final String s) throws IllegalArgumentException {
        final int[] array = { 0 };
        final int length = s.length();
        boolean b = false;
        if (s == null) {
            throw new NullPointerException();
        }
        array[0] = 0;
        boolean b2;
        if (length != array[0] && s.charAt(array[0]) == '-') {
            final int[] array2 = array;
            final int n = 0;
            ++array2[n];
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (length != array[0] && s.charAt(array[0]++) != 'P') {
            throw new IllegalArgumentException(s);
        }
        int n2 = 0;
        final String[] array3 = new String[3];
        final int[] array4 = new int[3];
        while (length != array[0] && isDigit(s.charAt(array[0])) && n2 < 3) {
            array4[n2] = array[0];
            array3[n2++] = parsePiece(s, array);
        }
        if (length != array[0]) {
            if (s.charAt(array[0]++) != 'T') {
                throw new IllegalArgumentException(s);
            }
            b = true;
        }
        int n3 = 0;
        final String[] array5 = new String[3];
        final int[] array6 = new int[3];
        while (length != array[0] && isDigitOrPeriod(s.charAt(array[0])) && n3 < 3) {
            array6[n3] = array[0];
            array5[n3++] = parsePiece(s, array);
        }
        if (b && n3 == 0) {
            throw new IllegalArgumentException(s);
        }
        if (length != array[0]) {
            throw new IllegalArgumentException(s);
        }
        if (n2 == 0 && n3 == 0) {
            throw new IllegalArgumentException(s);
        }
        organizeParts(s, array3, array4, n2, "YMD");
        organizeParts(s, array5, array6, n3, "HMS");
        this.years = parseBigInteger(s, array3[0], array4[0]);
        this.months = parseBigInteger(s, array3[1], array4[1]);
        this.days = parseBigInteger(s, array3[2], array4[2]);
        this.hours = parseBigInteger(s, array5[0], array6[0]);
        this.minutes = parseBigInteger(s, array5[1], array6[1]);
        this.seconds = parseBigDecimal(s, array5[2], array6[2]);
        this.signum = this.calcSignum(b2);
    }
    
    private static boolean isDigit(final char c) {
        return '0' <= c && c <= '9';
    }
    
    private static boolean isDigitOrPeriod(final char c) {
        return isDigit(c) || c == '.';
    }
    
    private static String parsePiece(final String s, final int[] array) throws IllegalArgumentException {
        final int n = array[0];
        while (array[0] < s.length() && isDigitOrPeriod(s.charAt(array[0]))) {
            final int n2 = 0;
            ++array[n2];
        }
        if (array[0] == s.length()) {
            throw new IllegalArgumentException(s);
        }
        final int n3 = 0;
        ++array[n3];
        return s.substring(n, array[0]);
    }
    
    private static void organizeParts(final String s, final String[] array, final int[] array2, final int n, final String s2) throws IllegalArgumentException {
        int i = s2.length();
        for (int j = n - 1; j >= 0; --j) {
            if (array[j] == null) {
                throw new IllegalArgumentException(s);
            }
            final int lastIndex = s2.lastIndexOf(array[j].charAt(array[j].length() - 1), i - 1);
            if (lastIndex == -1) {
                throw new IllegalArgumentException(s);
            }
            for (int k = lastIndex + 1; k < i; ++k) {
                array[k] = null;
            }
            i = lastIndex;
            array[i] = array[j];
            array2[i] = array2[j];
        }
        --i;
        while (i >= 0) {
            array[i] = null;
            --i;
        }
    }
    
    private static BigInteger parseBigInteger(final String s, String substring, final int n) throws IllegalArgumentException {
        if (substring == null) {
            return null;
        }
        substring = substring.substring(0, substring.length() - 1);
        return new BigInteger(substring);
    }
    
    private static BigDecimal parseBigDecimal(final String s, String substring, final int n) throws IllegalArgumentException {
        if (substring == null) {
            return null;
        }
        substring = substring.substring(0, substring.length() - 1);
        return new BigDecimal(substring);
    }
    
    public int compare(final Duration duration) {
        final BigInteger value = BigInteger.valueOf(2147483647L);
        if (this.years != null && this.years.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), this.years.toString() }));
        }
        if (this.months != null && this.months.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), this.months.toString() }));
        }
        if (this.days != null && this.days.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), this.days.toString() }));
        }
        if (this.hours != null && this.hours.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), this.hours.toString() }));
        }
        if (this.minutes != null && this.minutes.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), this.minutes.toString() }));
        }
        if (this.seconds != null && this.seconds.toBigInteger().compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), this.toString(this.seconds) }));
        }
        final BigInteger bigInteger = (BigInteger)duration.getField(DatatypeConstants.YEARS);
        if (bigInteger != null && bigInteger.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), bigInteger.toString() }));
        }
        final BigInteger bigInteger2 = (BigInteger)duration.getField(DatatypeConstants.MONTHS);
        if (bigInteger2 != null && bigInteger2.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), bigInteger2.toString() }));
        }
        final BigInteger bigInteger3 = (BigInteger)duration.getField(DatatypeConstants.DAYS);
        if (bigInteger3 != null && bigInteger3.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), bigInteger3.toString() }));
        }
        final BigInteger bigInteger4 = (BigInteger)duration.getField(DatatypeConstants.HOURS);
        if (bigInteger4 != null && bigInteger4.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), bigInteger4.toString() }));
        }
        final BigInteger bigInteger5 = (BigInteger)duration.getField(DatatypeConstants.MINUTES);
        if (bigInteger5 != null && bigInteger5.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), bigInteger5.toString() }));
        }
        final BigDecimal bigDecimal = (BigDecimal)duration.getField(DatatypeConstants.SECONDS);
        BigInteger bigInteger6 = null;
        if (bigDecimal != null) {
            bigInteger6 = bigDecimal.toBigInteger();
        }
        if (bigInteger6 != null && bigInteger6.compareTo(value) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), bigInteger6.toString() }));
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
        gregorianCalendar.add(1, this.getYears() * this.getSign());
        gregorianCalendar.add(2, this.getMonths() * this.getSign());
        gregorianCalendar.add(6, this.getDays() * this.getSign());
        gregorianCalendar.add(11, this.getHours() * this.getSign());
        gregorianCalendar.add(12, this.getMinutes() * this.getSign());
        gregorianCalendar.add(13, this.getSeconds() * this.getSign());
        final GregorianCalendar gregorianCalendar2 = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
        gregorianCalendar2.add(1, duration.getYears() * duration.getSign());
        gregorianCalendar2.add(2, duration.getMonths() * duration.getSign());
        gregorianCalendar2.add(6, duration.getDays() * duration.getSign());
        gregorianCalendar2.add(11, duration.getHours() * duration.getSign());
        gregorianCalendar2.add(12, duration.getMinutes() * duration.getSign());
        gregorianCalendar2.add(13, duration.getSeconds() * duration.getSign());
        if (gregorianCalendar.equals(gregorianCalendar2)) {
            return 0;
        }
        return this.compareDates(this, duration);
    }
    
    private int compareDates(final Duration duration, final Duration duration2) {
        final XMLGregorianCalendar xmlGregorianCalendar = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[0].clone();
        final XMLGregorianCalendar xmlGregorianCalendar2 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[0].clone();
        xmlGregorianCalendar.add(duration);
        xmlGregorianCalendar2.add(duration2);
        final int compare = xmlGregorianCalendar.compare(xmlGregorianCalendar2);
        if (compare == 2) {
            return 2;
        }
        final XMLGregorianCalendar xmlGregorianCalendar3 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[1].clone();
        final XMLGregorianCalendar xmlGregorianCalendar4 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[1].clone();
        xmlGregorianCalendar3.add(duration);
        xmlGregorianCalendar4.add(duration2);
        final int compareResults = this.compareResults(compare, xmlGregorianCalendar3.compare(xmlGregorianCalendar4));
        if (compareResults == 2) {
            return 2;
        }
        final XMLGregorianCalendar xmlGregorianCalendar5 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[2].clone();
        final XMLGregorianCalendar xmlGregorianCalendar6 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[2].clone();
        xmlGregorianCalendar5.add(duration);
        xmlGregorianCalendar6.add(duration2);
        final int compareResults2 = this.compareResults(compareResults, xmlGregorianCalendar5.compare(xmlGregorianCalendar6));
        if (compareResults2 == 2) {
            return 2;
        }
        final XMLGregorianCalendar xmlGregorianCalendar7 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[3].clone();
        final XMLGregorianCalendar xmlGregorianCalendar8 = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[3].clone();
        xmlGregorianCalendar7.add(duration);
        xmlGregorianCalendar8.add(duration2);
        return this.compareResults(compareResults2, xmlGregorianCalendar7.compare(xmlGregorianCalendar8));
    }
    
    private int compareResults(final int n, final int n2) {
        if (n2 == 2) {
            return 2;
        }
        if (n != n2) {
            return 2;
        }
        return n;
    }
    
    public int hashCode() {
        final GregorianCalendar gregorianCalendar = DurationImpl.TEST_POINTS[0].toGregorianCalendar();
        this.addTo(gregorianCalendar);
        return (int)getCalendarTimeInMillis(gregorianCalendar);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.signum < 0) {
            sb.append('-');
        }
        sb.append('P');
        if (this.years != null) {
            sb.append(this.years).append('Y');
        }
        if (this.months != null) {
            sb.append(this.months).append('M');
        }
        if (this.days != null) {
            sb.append(this.days).append('D');
        }
        if (this.hours != null || this.minutes != null || this.seconds != null) {
            sb.append('T');
            if (this.hours != null) {
                sb.append(this.hours).append('H');
            }
            if (this.minutes != null) {
                sb.append(this.minutes).append('M');
            }
            if (this.seconds != null) {
                sb.append(this.toString(this.seconds)).append('S');
            }
        }
        return sb.toString();
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
    
    public boolean isSet(final DatatypeConstants.Field field) {
        if (field == null) {
            throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[] { "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)" }));
        }
        if (field == DatatypeConstants.YEARS) {
            return this.years != null;
        }
        if (field == DatatypeConstants.MONTHS) {
            return this.months != null;
        }
        if (field == DatatypeConstants.DAYS) {
            return this.days != null;
        }
        if (field == DatatypeConstants.HOURS) {
            return this.hours != null;
        }
        if (field == DatatypeConstants.MINUTES) {
            return this.minutes != null;
        }
        if (field == DatatypeConstants.SECONDS) {
            return this.seconds != null;
        }
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[] { "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)", field.toString() }));
    }
    
    public Number getField(final DatatypeConstants.Field field) {
        if (field == null) {
            throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[] { "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field) " }));
        }
        if (field == DatatypeConstants.YEARS) {
            return this.years;
        }
        if (field == DatatypeConstants.MONTHS) {
            return this.months;
        }
        if (field == DatatypeConstants.DAYS) {
            return this.days;
        }
        if (field == DatatypeConstants.HOURS) {
            return this.hours;
        }
        if (field == DatatypeConstants.MINUTES) {
            return this.minutes;
        }
        if (field == DatatypeConstants.SECONDS) {
            return this.seconds;
        }
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[] { "javax.xml.datatype.Duration#(getSet(DatatypeConstants.Field field)", field.toString() }));
    }
    
    public int getYears() {
        return this.getInt(DatatypeConstants.YEARS);
    }
    
    public int getMonths() {
        return this.getInt(DatatypeConstants.MONTHS);
    }
    
    public int getDays() {
        return this.getInt(DatatypeConstants.DAYS);
    }
    
    public int getHours() {
        return this.getInt(DatatypeConstants.HOURS);
    }
    
    public int getMinutes() {
        return this.getInt(DatatypeConstants.MINUTES);
    }
    
    public int getSeconds() {
        return this.getInt(DatatypeConstants.SECONDS);
    }
    
    private int getInt(final DatatypeConstants.Field field) {
        final Number field2 = this.getField(field);
        if (field2 == null) {
            return 0;
        }
        return field2.intValue();
    }
    
    public long getTimeInMillis(final Calendar calendar) {
        final Calendar calendar2 = (Calendar)calendar.clone();
        this.addTo(calendar2);
        return getCalendarTimeInMillis(calendar2) - getCalendarTimeInMillis(calendar);
    }
    
    public long getTimeInMillis(final Date time) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(time);
        this.addTo(gregorianCalendar);
        return getCalendarTimeInMillis(gregorianCalendar) - time.getTime();
    }
    
    public Duration normalizeWith(final Calendar calendar) {
        final Calendar calendar2 = (Calendar)calendar.clone();
        calendar2.add(1, this.getYears() * this.signum);
        calendar2.add(2, this.getMonths() * this.signum);
        calendar2.add(5, this.getDays() * this.signum);
        final int n = (int)((getCalendarTimeInMillis(calendar2) - getCalendarTimeInMillis(calendar)) / 86400000L);
        return new DurationImpl(n >= 0, null, null, wrap(Math.abs(n)), (BigInteger)this.getField(DatatypeConstants.HOURS), (BigInteger)this.getField(DatatypeConstants.MINUTES), (BigDecimal)this.getField(DatatypeConstants.SECONDS));
    }
    
    public Duration multiply(final int n) {
        return this.multiply(BigDecimal.valueOf(n));
    }
    
    public Duration multiply(BigDecimal abs) {
        BigDecimal bigDecimal = DurationImpl.ZERO;
        final int signum = abs.signum();
        abs = abs.abs();
        final BigDecimal[] array = new BigDecimal[6];
        for (int i = 0; i < 5; ++i) {
            final BigDecimal add = this.getFieldAsBigDecimal(DurationImpl.FIELDS[i]).multiply(abs).add(bigDecimal);
            array[i] = add.setScale(0, 1);
            final BigDecimal subtract = add.subtract(array[i]);
            if (i == 1) {
                if (subtract.signum() != 0) {
                    throw new IllegalStateException();
                }
                bigDecimal = DurationImpl.ZERO;
            }
            else {
                bigDecimal = subtract.multiply(DurationImpl.FACTORS[i]);
            }
        }
        if (this.seconds != null) {
            array[5] = this.seconds.multiply(abs).add(bigDecimal);
        }
        else {
            array[5] = bigDecimal;
        }
        return new DurationImpl(this.signum * signum >= 0, toBigInteger(array[0], null == this.years), toBigInteger(array[1], null == this.months), toBigInteger(array[2], null == this.days), toBigInteger(array[3], null == this.hours), toBigInteger(array[4], null == this.minutes), (array[5].signum() == 0 && this.seconds == null) ? null : array[5]);
    }
    
    private BigDecimal getFieldAsBigDecimal(final DatatypeConstants.Field field) {
        if (field == DatatypeConstants.SECONDS) {
            if (this.seconds != null) {
                return this.seconds;
            }
            return DurationImpl.ZERO;
        }
        else {
            final BigInteger bigInteger = (BigInteger)this.getField(field);
            if (bigInteger == null) {
                return DurationImpl.ZERO;
            }
            return new BigDecimal(bigInteger);
        }
    }
    
    private static BigInteger toBigInteger(final BigDecimal bigDecimal, final boolean b) {
        if (b && bigDecimal.signum() == 0) {
            return null;
        }
        return bigDecimal.unscaledValue();
    }
    
    public Duration add(final Duration duration) {
        final BigDecimal[] array = { sanitize((BigInteger)this.getField(DatatypeConstants.YEARS), this.getSign()).add(sanitize((BigInteger)duration.getField(DatatypeConstants.YEARS), duration.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.MONTHS), this.getSign()).add(sanitize((BigInteger)duration.getField(DatatypeConstants.MONTHS), duration.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.DAYS), this.getSign()).add(sanitize((BigInteger)duration.getField(DatatypeConstants.DAYS), duration.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.HOURS), this.getSign()).add(sanitize((BigInteger)duration.getField(DatatypeConstants.HOURS), duration.getSign())), sanitize((BigInteger)this.getField(DatatypeConstants.MINUTES), this.getSign()).add(sanitize((BigInteger)duration.getField(DatatypeConstants.MINUTES), duration.getSign())), sanitize((BigDecimal)this.getField(DatatypeConstants.SECONDS), this.getSign()).add(sanitize((BigDecimal)duration.getField(DatatypeConstants.SECONDS), duration.getSign())) };
        alignSigns(array, 0, 2);
        alignSigns(array, 2, 6);
        int signum = 0;
        for (int i = 0; i < 6; ++i) {
            if (signum * array[i].signum() < 0) {
                throw new IllegalStateException();
            }
            if (signum == 0) {
                signum = array[i].signum();
            }
        }
        return new DurationImpl(signum >= 0, toBigInteger(sanitize(array[0], signum), this.getField(DatatypeConstants.YEARS) == null && duration.getField(DatatypeConstants.YEARS) == null), toBigInteger(sanitize(array[1], signum), this.getField(DatatypeConstants.MONTHS) == null && duration.getField(DatatypeConstants.MONTHS) == null), toBigInteger(sanitize(array[2], signum), this.getField(DatatypeConstants.DAYS) == null && duration.getField(DatatypeConstants.DAYS) == null), toBigInteger(sanitize(array[3], signum), this.getField(DatatypeConstants.HOURS) == null && duration.getField(DatatypeConstants.HOURS) == null), toBigInteger(sanitize(array[4], signum), this.getField(DatatypeConstants.MINUTES) == null && duration.getField(DatatypeConstants.MINUTES) == null), (array[5].signum() == 0 && this.getField(DatatypeConstants.SECONDS) == null && duration.getField(DatatypeConstants.SECONDS) == null) ? null : sanitize(array[5], signum));
    }
    
    private static void alignSigns(final BigDecimal[] array, final int n, final int n2) {
        boolean b;
        do {
            b = false;
            int signum = 0;
            for (int i = n; i < n2; ++i) {
                if (signum * array[i].signum() < 0) {
                    b = true;
                    BigDecimal bigDecimal = array[i].abs().divide(DurationImpl.FACTORS[i - 1], 0);
                    if (array[i].signum() > 0) {
                        bigDecimal = bigDecimal.negate();
                    }
                    array[i - 1] = array[i - 1].subtract(bigDecimal);
                    array[i] = array[i].add(bigDecimal.multiply(DurationImpl.FACTORS[i - 1]));
                }
                if (array[i].signum() != 0) {
                    signum = array[i].signum();
                }
            }
        } while (b);
    }
    
    private static BigDecimal sanitize(final BigInteger bigInteger, final int n) {
        if (n == 0 || bigInteger == null) {
            return DurationImpl.ZERO;
        }
        if (n > 0) {
            return new BigDecimal(bigInteger);
        }
        return new BigDecimal(bigInteger.negate());
    }
    
    static BigDecimal sanitize(final BigDecimal bigDecimal, final int n) {
        if (n == 0 || bigDecimal == null) {
            return DurationImpl.ZERO;
        }
        if (n > 0) {
            return bigDecimal;
        }
        return bigDecimal.negate();
    }
    
    public Duration subtract(final Duration duration) {
        return this.add(duration.negate());
    }
    
    public Duration negate() {
        return new DurationImpl(this.signum <= 0, this.years, this.months, this.days, this.hours, this.minutes, this.seconds);
    }
    
    public int signum() {
        return this.signum;
    }
    
    public void addTo(final Calendar calendar) {
        calendar.add(1, this.getYears() * this.signum);
        calendar.add(2, this.getMonths() * this.signum);
        calendar.add(5, this.getDays() * this.signum);
        calendar.add(10, this.getHours() * this.signum);
        calendar.add(12, this.getMinutes() * this.signum);
        calendar.add(13, this.getSeconds() * this.signum);
        if (this.seconds != null) {
            calendar.add(14, this.seconds.subtract(this.seconds.setScale(0, 1)).movePointRight(3).intValue() * this.signum);
        }
    }
    
    public void addTo(final Date time) {
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(time);
        this.addTo(gregorianCalendar);
        time.setTime(getCalendarTimeInMillis(gregorianCalendar));
    }
    
    private static long getCalendarTimeInMillis(final Calendar calendar) {
        return calendar.getTime().getTime();
    }
    
    private Object writeReplace() throws IOException {
        return new SerializedDuration(this.toString());
    }
    
    static {
        FIELDS = new DatatypeConstants.Field[] { DatatypeConstants.YEARS, DatatypeConstants.MONTHS, DatatypeConstants.DAYS, DatatypeConstants.HOURS, DatatypeConstants.MINUTES, DatatypeConstants.SECONDS };
        ZERO = BigDecimal.valueOf(0L);
        TEST_POINTS = new XMLGregorianCalendar[] { XMLGregorianCalendarImpl.parse("1696-09-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1697-02-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-03-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-07-01T00:00:00Z") };
        FACTORS = new BigDecimal[] { BigDecimal.valueOf(12L), null, BigDecimal.valueOf(24L), BigDecimal.valueOf(60L), BigDecimal.valueOf(60L) };
    }
}

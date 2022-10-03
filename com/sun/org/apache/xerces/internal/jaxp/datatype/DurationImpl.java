package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.io.ObjectStreamException;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.DatatypeMessageFormatter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConstants;
import java.io.Serializable;
import javax.xml.datatype.Duration;

class DurationImpl extends Duration implements Serializable
{
    private static final int FIELD_NUM = 6;
    private static final DatatypeConstants.Field[] FIELDS;
    private static final int[] FIELD_IDS;
    private static final TimeZone GMT;
    private static final BigDecimal ZERO;
    protected int signum;
    protected BigInteger years;
    protected BigInteger months;
    protected BigInteger days;
    protected BigInteger hours;
    protected BigInteger minutes;
    protected BigDecimal seconds;
    private static final XMLGregorianCalendar[] TEST_POINTS;
    private static final BigDecimal[] FACTORS;
    private static final long serialVersionUID = 1L;
    
    @Override
    public int getSign() {
        return this.signum;
    }
    
    protected int calcSignum(final boolean isPositive) {
        if ((this.years == null || this.years.signum() == 0) && (this.months == null || this.months.signum() == 0) && (this.days == null || this.days.signum() == 0) && (this.hours == null || this.hours.signum() == 0) && (this.minutes == null || this.minutes.signum() == 0) && (this.seconds == null || this.seconds.signum() == 0)) {
            return 0;
        }
        if (isPositive) {
            return 1;
        }
        return -1;
    }
    
    protected DurationImpl(final boolean isPositive, final BigInteger years, final BigInteger months, final BigInteger days, final BigInteger hours, final BigInteger minutes, final BigDecimal seconds) {
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.signum = this.calcSignum(isPositive);
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
    
    protected static void testNonNegative(final BigInteger n, final DatatypeConstants.Field f) {
        if (n != null && n.signum() < 0) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[] { f.toString() }));
        }
    }
    
    protected static void testNonNegative(final BigDecimal n, final DatatypeConstants.Field f) {
        if (n != null && n.signum() < 0) {
            throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "NegativeField", new Object[] { f.toString() }));
        }
    }
    
    protected DurationImpl(final boolean isPositive, final int years, final int months, final int days, final int hours, final int minutes, final int seconds) {
        this(isPositive, wrap(years), wrap(months), wrap(days), wrap(hours), wrap(minutes), (seconds != Integer.MIN_VALUE) ? new BigDecimal(String.valueOf(seconds)) : null);
    }
    
    protected static BigInteger wrap(final int i) {
        if (i == Integer.MIN_VALUE) {
            return null;
        }
        return new BigInteger(String.valueOf(i));
    }
    
    protected DurationImpl(final long durationInMilliSeconds) {
        long l = durationInMilliSeconds;
        if (l > 0L) {
            this.signum = 1;
        }
        else if (l < 0L) {
            this.signum = -1;
            if (l == Long.MIN_VALUE) {
                ++l;
            }
            l *= -1L;
        }
        else {
            this.signum = 0;
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar(DurationImpl.GMT);
        gregorianCalendar.setTimeInMillis(l);
        long int2long = 0L;
        int2long = gregorianCalendar.get(1) - 1970;
        this.years = BigInteger.valueOf(int2long);
        int2long = gregorianCalendar.get(2);
        this.months = BigInteger.valueOf(int2long);
        int2long = gregorianCalendar.get(5) - 1;
        this.days = BigInteger.valueOf(int2long);
        int2long = gregorianCalendar.get(11);
        this.hours = BigInteger.valueOf(int2long);
        int2long = gregorianCalendar.get(12);
        this.minutes = BigInteger.valueOf(int2long);
        int2long = gregorianCalendar.get(13) * 1000 + gregorianCalendar.get(14);
        this.seconds = BigDecimal.valueOf(int2long, 3);
    }
    
    protected DurationImpl(final String lexicalRepresentation) throws IllegalArgumentException {
        final String s = lexicalRepresentation;
        final int[] idx = { 0 };
        final int length = s.length();
        boolean timeRequired = false;
        if (lexicalRepresentation == null) {
            throw new NullPointerException();
        }
        idx[0] = 0;
        boolean positive;
        if (length != idx[0] && s.charAt(idx[0]) == '-') {
            final int[] array = idx;
            final int n = 0;
            ++array[n];
            positive = false;
        }
        else {
            positive = true;
        }
        if (length != idx[0] && s.charAt(idx[0]++) != 'P') {
            throw new IllegalArgumentException(s);
        }
        int dateLen = 0;
        final String[] dateParts = new String[3];
        final int[] datePartsIndex = new int[3];
        while (length != idx[0] && isDigit(s.charAt(idx[0])) && dateLen < 3) {
            datePartsIndex[dateLen] = idx[0];
            dateParts[dateLen++] = parsePiece(s, idx);
        }
        if (length != idx[0]) {
            if (s.charAt(idx[0]++) != 'T') {
                throw new IllegalArgumentException(s);
            }
            timeRequired = true;
        }
        int timeLen = 0;
        final String[] timeParts = new String[3];
        final int[] timePartsIndex = new int[3];
        while (length != idx[0] && isDigitOrPeriod(s.charAt(idx[0])) && timeLen < 3) {
            timePartsIndex[timeLen] = idx[0];
            timeParts[timeLen++] = parsePiece(s, idx);
        }
        if (timeRequired && timeLen == 0) {
            throw new IllegalArgumentException(s);
        }
        if (length != idx[0]) {
            throw new IllegalArgumentException(s);
        }
        if (dateLen == 0 && timeLen == 0) {
            throw new IllegalArgumentException(s);
        }
        organizeParts(s, dateParts, datePartsIndex, dateLen, "YMD");
        organizeParts(s, timeParts, timePartsIndex, timeLen, "HMS");
        this.years = parseBigInteger(s, dateParts[0], datePartsIndex[0]);
        this.months = parseBigInteger(s, dateParts[1], datePartsIndex[1]);
        this.days = parseBigInteger(s, dateParts[2], datePartsIndex[2]);
        this.hours = parseBigInteger(s, timeParts[0], timePartsIndex[0]);
        this.minutes = parseBigInteger(s, timeParts[1], timePartsIndex[1]);
        this.seconds = parseBigDecimal(s, timeParts[2], timePartsIndex[2]);
        this.signum = this.calcSignum(positive);
    }
    
    private static boolean isDigit(final char ch) {
        return '0' <= ch && ch <= '9';
    }
    
    private static boolean isDigitOrPeriod(final char ch) {
        return isDigit(ch) || ch == '.';
    }
    
    private static String parsePiece(final String whole, final int[] idx) throws IllegalArgumentException {
        final int start = idx[0];
        while (idx[0] < whole.length() && isDigitOrPeriod(whole.charAt(idx[0]))) {
            final int n = 0;
            ++idx[n];
        }
        if (idx[0] == whole.length()) {
            throw new IllegalArgumentException(whole);
        }
        final int n2 = 0;
        ++idx[n2];
        return whole.substring(start, idx[0]);
    }
    
    private static void organizeParts(final String whole, final String[] parts, final int[] partsIndex, final int len, final String tokens) throws IllegalArgumentException {
        int idx = tokens.length();
        for (int i = len - 1; i >= 0; --i) {
            final int nidx = tokens.lastIndexOf(parts[i].charAt(parts[i].length() - 1), idx - 1);
            if (nidx == -1) {
                throw new IllegalArgumentException(whole);
            }
            for (int j = nidx + 1; j < idx; ++j) {
                parts[j] = null;
            }
            idx = nidx;
            parts[idx] = parts[i];
            partsIndex[idx] = partsIndex[i];
        }
        --idx;
        while (idx >= 0) {
            parts[idx] = null;
            --idx;
        }
    }
    
    private static BigInteger parseBigInteger(final String whole, String part, final int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        return new BigInteger(part);
    }
    
    private static BigDecimal parseBigDecimal(final String whole, String part, final int index) throws IllegalArgumentException {
        if (part == null) {
            return null;
        }
        part = part.substring(0, part.length() - 1);
        return new BigDecimal(part);
    }
    
    @Override
    public int compare(final Duration rhs) {
        final BigInteger maxintAsBigInteger = BigInteger.valueOf(2147483647L);
        final BigInteger minintAsBigInteger = BigInteger.valueOf(-2147483648L);
        if (this.years != null && this.years.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), this.years.toString() }));
        }
        if (this.months != null && this.months.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), this.months.toString() }));
        }
        if (this.days != null && this.days.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), this.days.toString() }));
        }
        if (this.hours != null && this.hours.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), this.hours.toString() }));
        }
        if (this.minutes != null && this.minutes.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), this.minutes.toString() }));
        }
        if (this.seconds != null && this.seconds.toBigInteger().compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), this.seconds.toString() }));
        }
        final BigInteger rhsYears = (BigInteger)rhs.getField(DatatypeConstants.YEARS);
        if (rhsYears != null && rhsYears.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.YEARS.toString(), rhsYears.toString() }));
        }
        final BigInteger rhsMonths = (BigInteger)rhs.getField(DatatypeConstants.MONTHS);
        if (rhsMonths != null && rhsMonths.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MONTHS.toString(), rhsMonths.toString() }));
        }
        final BigInteger rhsDays = (BigInteger)rhs.getField(DatatypeConstants.DAYS);
        if (rhsDays != null && rhsDays.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.DAYS.toString(), rhsDays.toString() }));
        }
        final BigInteger rhsHours = (BigInteger)rhs.getField(DatatypeConstants.HOURS);
        if (rhsHours != null && rhsHours.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.HOURS.toString(), rhsHours.toString() }));
        }
        final BigInteger rhsMinutes = (BigInteger)rhs.getField(DatatypeConstants.MINUTES);
        if (rhsMinutes != null && rhsMinutes.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.MINUTES.toString(), rhsMinutes.toString() }));
        }
        final BigDecimal rhsSecondsAsBigDecimal = (BigDecimal)rhs.getField(DatatypeConstants.SECONDS);
        BigInteger rhsSeconds = null;
        if (rhsSecondsAsBigDecimal != null) {
            rhsSeconds = rhsSecondsAsBigDecimal.toBigInteger();
        }
        if (rhsSeconds != null && rhsSeconds.compareTo(maxintAsBigInteger) == 1) {
            throw new UnsupportedOperationException(DatatypeMessageFormatter.formatMessage(null, "TooLarge", new Object[] { this.getClass().getName() + "#compare(Duration duration)" + DatatypeConstants.SECONDS.toString(), rhsSeconds.toString() }));
        }
        final GregorianCalendar lhsCalendar = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
        lhsCalendar.add(1, this.getYears() * this.getSign());
        lhsCalendar.add(2, this.getMonths() * this.getSign());
        lhsCalendar.add(6, this.getDays() * this.getSign());
        lhsCalendar.add(11, this.getHours() * this.getSign());
        lhsCalendar.add(12, this.getMinutes() * this.getSign());
        lhsCalendar.add(13, this.getSeconds() * this.getSign());
        final GregorianCalendar rhsCalendar = new GregorianCalendar(1970, 1, 1, 0, 0, 0);
        rhsCalendar.add(1, rhs.getYears() * rhs.getSign());
        rhsCalendar.add(2, rhs.getMonths() * rhs.getSign());
        rhsCalendar.add(6, rhs.getDays() * rhs.getSign());
        rhsCalendar.add(11, rhs.getHours() * rhs.getSign());
        rhsCalendar.add(12, rhs.getMinutes() * rhs.getSign());
        rhsCalendar.add(13, rhs.getSeconds() * rhs.getSign());
        if (lhsCalendar.equals(rhsCalendar)) {
            return 0;
        }
        return this.compareDates(this, rhs);
    }
    
    private int compareDates(final Duration duration1, final Duration duration2) {
        int resultA = 2;
        int resultB = 2;
        XMLGregorianCalendar tempA = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[0].clone();
        XMLGregorianCalendar tempB = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[0].clone();
        tempA.add(duration1);
        tempB.add(duration2);
        resultA = tempA.compare(tempB);
        if (resultA == 2) {
            return 2;
        }
        tempA = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[1].clone();
        tempB = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[1].clone();
        tempA.add(duration1);
        tempB.add(duration2);
        resultB = tempA.compare(tempB);
        resultA = this.compareResults(resultA, resultB);
        if (resultA == 2) {
            return 2;
        }
        tempA = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[2].clone();
        tempB = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[2].clone();
        tempA.add(duration1);
        tempB.add(duration2);
        resultB = tempA.compare(tempB);
        resultA = this.compareResults(resultA, resultB);
        if (resultA == 2) {
            return 2;
        }
        tempA = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[3].clone();
        tempB = (XMLGregorianCalendar)DurationImpl.TEST_POINTS[3].clone();
        tempA.add(duration1);
        tempB.add(duration2);
        resultB = tempA.compare(tempB);
        resultA = this.compareResults(resultA, resultB);
        return resultA;
    }
    
    private int compareResults(final int resultA, final int resultB) {
        if (resultB == 2) {
            return 2;
        }
        if (resultA != resultB) {
            return 2;
        }
        return resultA;
    }
    
    @Override
    public int hashCode() {
        final Calendar cal = DurationImpl.TEST_POINTS[0].toGregorianCalendar();
        this.addTo(cal);
        return (int)getCalendarTimeInMillis(cal);
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        if (this.signum < 0) {
            buf.append('-');
        }
        buf.append('P');
        if (this.years != null) {
            buf.append(this.years + "Y");
        }
        if (this.months != null) {
            buf.append(this.months + "M");
        }
        if (this.days != null) {
            buf.append(this.days + "D");
        }
        if (this.hours != null || this.minutes != null || this.seconds != null) {
            buf.append('T');
            if (this.hours != null) {
                buf.append(this.hours + "H");
            }
            if (this.minutes != null) {
                buf.append(this.minutes + "M");
            }
            if (this.seconds != null) {
                buf.append(this.toString(this.seconds) + "S");
            }
        }
        return buf.toString();
    }
    
    private String toString(final BigDecimal bd) {
        final String intString = bd.unscaledValue().toString();
        final int scale = bd.scale();
        if (scale == 0) {
            return intString;
        }
        final int insertionPoint = intString.length() - scale;
        if (insertionPoint == 0) {
            return "0." + intString;
        }
        StringBuffer buf;
        if (insertionPoint > 0) {
            buf = new StringBuffer(intString);
            buf.insert(insertionPoint, '.');
        }
        else {
            buf = new StringBuffer(3 - insertionPoint + intString.length());
            buf.append("0.");
            for (int i = 0; i < -insertionPoint; ++i) {
                buf.append('0');
            }
            buf.append(intString);
        }
        return buf.toString();
    }
    
    @Override
    public boolean isSet(final DatatypeConstants.Field field) {
        if (field == null) {
            final String methodName = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
            throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[] { methodName }));
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
        final String methodName = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field)";
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[] { methodName, field.toString() }));
    }
    
    @Override
    public Number getField(final DatatypeConstants.Field field) {
        if (field == null) {
            final String methodName = "javax.xml.datatype.Duration#isSet(DatatypeConstants.Field field) ";
            throw new NullPointerException(DatatypeMessageFormatter.formatMessage(null, "FieldCannotBeNull", new Object[] { methodName }));
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
        final String methodName = "javax.xml.datatype.Duration#(getSet(DatatypeConstants.Field field)";
        throw new IllegalArgumentException(DatatypeMessageFormatter.formatMessage(null, "UnknownField", new Object[] { methodName, field.toString() }));
    }
    
    @Override
    public int getYears() {
        return this.getInt(DatatypeConstants.YEARS);
    }
    
    @Override
    public int getMonths() {
        return this.getInt(DatatypeConstants.MONTHS);
    }
    
    @Override
    public int getDays() {
        return this.getInt(DatatypeConstants.DAYS);
    }
    
    @Override
    public int getHours() {
        return this.getInt(DatatypeConstants.HOURS);
    }
    
    @Override
    public int getMinutes() {
        return this.getInt(DatatypeConstants.MINUTES);
    }
    
    @Override
    public int getSeconds() {
        return this.getInt(DatatypeConstants.SECONDS);
    }
    
    private int getInt(final DatatypeConstants.Field field) {
        final Number n = this.getField(field);
        if (n == null) {
            return 0;
        }
        return n.intValue();
    }
    
    @Override
    public long getTimeInMillis(final Calendar startInstant) {
        final Calendar cal = (Calendar)startInstant.clone();
        this.addTo(cal);
        return getCalendarTimeInMillis(cal) - getCalendarTimeInMillis(startInstant);
    }
    
    @Override
    public long getTimeInMillis(final Date startInstant) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(startInstant);
        this.addTo(cal);
        return getCalendarTimeInMillis(cal) - startInstant.getTime();
    }
    
    @Override
    public Duration normalizeWith(final Calendar startTimeInstant) {
        final Calendar c = (Calendar)startTimeInstant.clone();
        c.add(1, this.getYears() * this.signum);
        c.add(2, this.getMonths() * this.signum);
        c.add(5, this.getDays() * this.signum);
        final long diff = getCalendarTimeInMillis(c) - getCalendarTimeInMillis(startTimeInstant);
        final int days = (int)(diff / 86400000L);
        return new DurationImpl(days >= 0, null, null, wrap(Math.abs(days)), (BigInteger)this.getField(DatatypeConstants.HOURS), (BigInteger)this.getField(DatatypeConstants.MINUTES), (BigDecimal)this.getField(DatatypeConstants.SECONDS));
    }
    
    @Override
    public Duration multiply(final int factor) {
        return this.multiply(BigDecimal.valueOf(factor));
    }
    
    @Override
    public Duration multiply(BigDecimal factor) {
        BigDecimal carry = DurationImpl.ZERO;
        final int factorSign = factor.signum();
        factor = factor.abs();
        final BigDecimal[] buf = new BigDecimal[6];
        for (int i = 0; i < 5; ++i) {
            BigDecimal bd = this.getFieldAsBigDecimal(DurationImpl.FIELDS[i]);
            bd = bd.multiply(factor).add(carry);
            buf[i] = bd.setScale(0, 1);
            bd = bd.subtract(buf[i]);
            if (i == 1) {
                if (bd.signum() != 0) {
                    throw new IllegalStateException();
                }
                carry = DurationImpl.ZERO;
            }
            else {
                carry = bd.multiply(DurationImpl.FACTORS[i]);
            }
        }
        if (this.seconds != null) {
            buf[5] = this.seconds.multiply(factor).add(carry);
        }
        else {
            buf[5] = carry;
        }
        return new DurationImpl(this.signum * factorSign >= 0, toBigInteger(buf[0], null == this.years), toBigInteger(buf[1], null == this.months), toBigInteger(buf[2], null == this.days), toBigInteger(buf[3], null == this.hours), toBigInteger(buf[4], null == this.minutes), (buf[5].signum() == 0 && this.seconds == null) ? null : buf[5]);
    }
    
    private BigDecimal getFieldAsBigDecimal(final DatatypeConstants.Field f) {
        if (f == DatatypeConstants.SECONDS) {
            if (this.seconds != null) {
                return this.seconds;
            }
            return DurationImpl.ZERO;
        }
        else {
            final BigInteger bi = (BigInteger)this.getField(f);
            if (bi == null) {
                return DurationImpl.ZERO;
            }
            return new BigDecimal(bi);
        }
    }
    
    private static BigInteger toBigInteger(final BigDecimal value, final boolean canBeNull) {
        if (canBeNull && value.signum() == 0) {
            return null;
        }
        return value.unscaledValue();
    }
    
    @Override
    public Duration add(final Duration rhs) {
        final Duration lhs = this;
        final BigDecimal[] buf = { sanitize((BigInteger)lhs.getField(DatatypeConstants.YEARS), lhs.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.YEARS), rhs.getSign())), sanitize((BigInteger)lhs.getField(DatatypeConstants.MONTHS), lhs.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.MONTHS), rhs.getSign())), sanitize((BigInteger)lhs.getField(DatatypeConstants.DAYS), lhs.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.DAYS), rhs.getSign())), sanitize((BigInteger)lhs.getField(DatatypeConstants.HOURS), lhs.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.HOURS), rhs.getSign())), sanitize((BigInteger)lhs.getField(DatatypeConstants.MINUTES), lhs.getSign()).add(sanitize((BigInteger)rhs.getField(DatatypeConstants.MINUTES), rhs.getSign())), sanitize((BigDecimal)lhs.getField(DatatypeConstants.SECONDS), lhs.getSign()).add(sanitize((BigDecimal)rhs.getField(DatatypeConstants.SECONDS), rhs.getSign())) };
        alignSigns(buf, 0, 2);
        alignSigns(buf, 2, 6);
        int s = 0;
        for (int i = 0; i < 6; ++i) {
            if (s * buf[i].signum() < 0) {
                throw new IllegalStateException();
            }
            if (s == 0) {
                s = buf[i].signum();
            }
        }
        return new DurationImpl(s >= 0, toBigInteger(sanitize(buf[0], s), lhs.getField(DatatypeConstants.YEARS) == null && rhs.getField(DatatypeConstants.YEARS) == null), toBigInteger(sanitize(buf[1], s), lhs.getField(DatatypeConstants.MONTHS) == null && rhs.getField(DatatypeConstants.MONTHS) == null), toBigInteger(sanitize(buf[2], s), lhs.getField(DatatypeConstants.DAYS) == null && rhs.getField(DatatypeConstants.DAYS) == null), toBigInteger(sanitize(buf[3], s), lhs.getField(DatatypeConstants.HOURS) == null && rhs.getField(DatatypeConstants.HOURS) == null), toBigInteger(sanitize(buf[4], s), lhs.getField(DatatypeConstants.MINUTES) == null && rhs.getField(DatatypeConstants.MINUTES) == null), (buf[5].signum() == 0 && lhs.getField(DatatypeConstants.SECONDS) == null && rhs.getField(DatatypeConstants.SECONDS) == null) ? null : sanitize(buf[5], s));
    }
    
    private static void alignSigns(final BigDecimal[] buf, final int start, final int end) {
        boolean touched;
        do {
            touched = false;
            int s = 0;
            for (int i = start; i < end; ++i) {
                if (s * buf[i].signum() < 0) {
                    touched = true;
                    BigDecimal borrow = buf[i].abs().divide(DurationImpl.FACTORS[i - 1], 0);
                    if (buf[i].signum() > 0) {
                        borrow = borrow.negate();
                    }
                    buf[i - 1] = buf[i - 1].subtract(borrow);
                    buf[i] = buf[i].add(borrow.multiply(DurationImpl.FACTORS[i - 1]));
                }
                if (buf[i].signum() != 0) {
                    s = buf[i].signum();
                }
            }
        } while (touched);
    }
    
    private static BigDecimal sanitize(final BigInteger value, final int signum) {
        if (signum == 0 || value == null) {
            return DurationImpl.ZERO;
        }
        if (signum > 0) {
            return new BigDecimal(value);
        }
        return new BigDecimal(value.negate());
    }
    
    static BigDecimal sanitize(final BigDecimal value, final int signum) {
        if (signum == 0 || value == null) {
            return DurationImpl.ZERO;
        }
        if (signum > 0) {
            return value;
        }
        return value.negate();
    }
    
    @Override
    public Duration subtract(final Duration rhs) {
        return this.add(rhs.negate());
    }
    
    @Override
    public Duration negate() {
        return new DurationImpl(this.signum <= 0, this.years, this.months, this.days, this.hours, this.minutes, this.seconds);
    }
    
    public int signum() {
        return this.signum;
    }
    
    @Override
    public void addTo(final Calendar calendar) {
        calendar.add(1, this.getYears() * this.signum);
        calendar.add(2, this.getMonths() * this.signum);
        calendar.add(5, this.getDays() * this.signum);
        calendar.add(10, this.getHours() * this.signum);
        calendar.add(12, this.getMinutes() * this.signum);
        calendar.add(13, this.getSeconds() * this.signum);
        if (this.seconds != null) {
            final BigDecimal fraction = this.seconds.subtract(this.seconds.setScale(0, 1));
            final int millisec = fraction.movePointRight(3).intValue();
            calendar.add(14, millisec * this.signum);
        }
    }
    
    @Override
    public void addTo(final Date date) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        this.addTo(cal);
        date.setTime(getCalendarTimeInMillis(cal));
    }
    
    private Object writeReplace() throws IOException {
        return new DurationStream(this.toString());
    }
    
    private static long getCalendarTimeInMillis(final Calendar cal) {
        return cal.getTime().getTime();
    }
    
    static {
        FIELDS = new DatatypeConstants.Field[] { DatatypeConstants.YEARS, DatatypeConstants.MONTHS, DatatypeConstants.DAYS, DatatypeConstants.HOURS, DatatypeConstants.MINUTES, DatatypeConstants.SECONDS };
        FIELD_IDS = new int[] { DatatypeConstants.YEARS.getId(), DatatypeConstants.MONTHS.getId(), DatatypeConstants.DAYS.getId(), DatatypeConstants.HOURS.getId(), DatatypeConstants.MINUTES.getId(), DatatypeConstants.SECONDS.getId() };
        GMT = TimeZone.getTimeZone("GMT");
        ZERO = BigDecimal.valueOf(0L);
        TEST_POINTS = new XMLGregorianCalendar[] { XMLGregorianCalendarImpl.parse("1696-09-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1697-02-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-03-01T00:00:00Z"), XMLGregorianCalendarImpl.parse("1903-07-01T00:00:00Z") };
        FACTORS = new BigDecimal[] { BigDecimal.valueOf(12L), null, BigDecimal.valueOf(24L), BigDecimal.valueOf(60L), BigDecimal.valueOf(60L) };
    }
    
    private static class DurationStream implements Serializable
    {
        private final String lexical;
        private static final long serialVersionUID = 1L;
        
        private DurationStream(final String _lexical) {
            this.lexical = _lexical;
        }
        
        private Object readResolve() throws ObjectStreamException {
            return new DurationImpl(this.lexical);
        }
    }
}

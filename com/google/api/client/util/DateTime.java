package com.google.api.client.util;

import java.util.concurrent.TimeUnit;
import java.util.Objects;
import java.util.regex.Matcher;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.TimeZone;
import java.io.Serializable;

public final class DateTime implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final TimeZone GMT;
    private static final String RFC3339_REGEX = "(\\d{4})-(\\d{2})-(\\d{2})([Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.\\d{1,9})?)?([Zz]|([+-])(\\d{2}):(\\d{2}))?";
    private static final Pattern RFC3339_PATTERN;
    private final long value;
    private final boolean dateOnly;
    private final int tzShift;
    
    public DateTime(final Date date, final TimeZone zone) {
        this(false, date.getTime(), (zone == null) ? null : Integer.valueOf(zone.getOffset(date.getTime()) / 60000));
    }
    
    public DateTime(final long value) {
        this(false, value, null);
    }
    
    public DateTime(final Date value) {
        this(value.getTime());
    }
    
    public DateTime(final long value, final int tzShift) {
        this(false, value, tzShift);
    }
    
    public DateTime(final boolean dateOnly, final long value, final Integer tzShift) {
        this.dateOnly = dateOnly;
        this.value = value;
        this.tzShift = (dateOnly ? 0 : ((tzShift == null) ? (TimeZone.getDefault().getOffset(value) / 60000) : tzShift));
    }
    
    public DateTime(final String value) {
        final DateTime dateTime = parseRfc3339(value);
        this.dateOnly = dateTime.dateOnly;
        this.value = dateTime.value;
        this.tzShift = dateTime.tzShift;
    }
    
    public long getValue() {
        return this.value;
    }
    
    public boolean isDateOnly() {
        return this.dateOnly;
    }
    
    public int getTimeZoneShift() {
        return this.tzShift;
    }
    
    public String toStringRfc3339() {
        final StringBuilder sb = new StringBuilder();
        final Calendar dateTime = new GregorianCalendar(DateTime.GMT);
        final long localTime = this.value + this.tzShift * 60000L;
        dateTime.setTimeInMillis(localTime);
        appendInt(sb, dateTime.get(1), 4);
        sb.append('-');
        appendInt(sb, dateTime.get(2) + 1, 2);
        sb.append('-');
        appendInt(sb, dateTime.get(5), 2);
        if (!this.dateOnly) {
            sb.append('T');
            appendInt(sb, dateTime.get(11), 2);
            sb.append(':');
            appendInt(sb, dateTime.get(12), 2);
            sb.append(':');
            appendInt(sb, dateTime.get(13), 2);
            if (dateTime.isSet(14)) {
                sb.append('.');
                appendInt(sb, dateTime.get(14), 3);
            }
            if (this.tzShift == 0) {
                sb.append('Z');
            }
            else {
                int absTzShift = this.tzShift;
                if (this.tzShift > 0) {
                    sb.append('+');
                }
                else {
                    sb.append('-');
                    absTzShift = -absTzShift;
                }
                final int tzHours = absTzShift / 60;
                final int tzMinutes = absTzShift % 60;
                appendInt(sb, tzHours, 2);
                sb.append(':');
                appendInt(sb, tzMinutes, 2);
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return this.toStringRfc3339();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof DateTime)) {
            return false;
        }
        final DateTime other = (DateTime)o;
        return this.dateOnly == other.dateOnly && this.value == other.value && this.tzShift == other.tzShift;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(new long[] { this.value, this.dateOnly ? 1 : 0, this.tzShift });
    }
    
    public static DateTime parseRfc3339(final String str) {
        return parseRfc3339WithNanoSeconds(str).toDateTime();
    }
    
    public static SecondsAndNanos parseRfc3339ToSecondsAndNanos(final String str) {
        final Rfc3339ParseResult time = parseRfc3339WithNanoSeconds(str);
        return time.toSecondsAndNanos();
    }
    
    private static Rfc3339ParseResult parseRfc3339WithNanoSeconds(final String str) throws NumberFormatException {
        final Matcher matcher = DateTime.RFC3339_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new NumberFormatException("Invalid date/time format: " + str);
        }
        final int year = Integer.parseInt(matcher.group(1));
        final int month = Integer.parseInt(matcher.group(2)) - 1;
        final int day = Integer.parseInt(matcher.group(3));
        final boolean isTimeGiven = matcher.group(4) != null;
        final String tzShiftRegexGroup = matcher.group(9);
        final boolean isTzShiftGiven = tzShiftRegexGroup != null;
        int hourOfDay = 0;
        int minute = 0;
        int second = 0;
        int nanoseconds = 0;
        Integer tzShiftInteger = null;
        if (isTzShiftGiven && !isTimeGiven) {
            throw new NumberFormatException("Invalid date/time format, cannot specify time zone shift without specifying time: " + str);
        }
        if (isTimeGiven) {
            hourOfDay = Integer.parseInt(matcher.group(5));
            minute = Integer.parseInt(matcher.group(6));
            second = Integer.parseInt(matcher.group(7));
            if (matcher.group(8) != null) {
                final String fraction = Strings.padEnd(matcher.group(8).substring(1), 9, '0');
                nanoseconds = Integer.parseInt(fraction);
            }
        }
        final Calendar dateTime = new GregorianCalendar(DateTime.GMT);
        dateTime.clear();
        dateTime.set(year, month, day, hourOfDay, minute, second);
        long value = dateTime.getTimeInMillis();
        if (isTimeGiven && isTzShiftGiven) {
            if (Character.toUpperCase(tzShiftRegexGroup.charAt(0)) != 'Z') {
                int tzShift = Integer.parseInt(matcher.group(11)) * 60 + Integer.parseInt(matcher.group(12));
                if (matcher.group(10).charAt(0) == '-') {
                    tzShift = -tzShift;
                }
                value -= tzShift * 60000L;
                tzShiftInteger = tzShift;
            }
            else {
                tzShiftInteger = 0;
            }
        }
        final long secondsSinceEpoch = value / 1000L;
        return new Rfc3339ParseResult(secondsSinceEpoch, nanoseconds, isTimeGiven, tzShiftInteger);
    }
    
    private static void appendInt(final StringBuilder sb, int num, int numDigits) {
        if (num < 0) {
            sb.append('-');
            num = -num;
        }
        for (int x = num; x > 0; x /= 10, --numDigits) {}
        for (int i = 0; i < numDigits; ++i) {
            sb.append('0');
        }
        if (num != 0) {
            sb.append(num);
        }
    }
    
    static {
        GMT = TimeZone.getTimeZone("GMT");
        RFC3339_PATTERN = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})([Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.\\d{1,9})?)?([Zz]|([+-])(\\d{2}):(\\d{2}))?");
    }
    
    public static final class SecondsAndNanos implements Serializable
    {
        private final long seconds;
        private final int nanos;
        
        public static SecondsAndNanos ofSecondsAndNanos(final long seconds, final int nanos) {
            return new SecondsAndNanos(seconds, nanos);
        }
        
        private SecondsAndNanos(final long seconds, final int nanos) {
            this.seconds = seconds;
            this.nanos = nanos;
        }
        
        public long getSeconds() {
            return this.seconds;
        }
        
        public int getNanos() {
            return this.nanos;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final SecondsAndNanos that = (SecondsAndNanos)o;
            return this.seconds == that.seconds && this.nanos == that.nanos;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.seconds, this.nanos);
        }
        
        @Override
        public String toString() {
            return String.format("Seconds: %d, Nanos: %d", this.seconds, this.nanos);
        }
    }
    
    private static class Rfc3339ParseResult implements Serializable
    {
        private final long seconds;
        private final int nanos;
        private final boolean timeGiven;
        private final Integer tzShift;
        
        private Rfc3339ParseResult(final long seconds, final int nanos, final boolean timeGiven, final Integer tzShift) {
            this.seconds = seconds;
            this.nanos = nanos;
            this.timeGiven = timeGiven;
            this.tzShift = tzShift;
        }
        
        private DateTime toDateTime() {
            final long seconds = TimeUnit.SECONDS.toMillis(this.seconds);
            final long nanos = TimeUnit.NANOSECONDS.toMillis(this.nanos);
            return new DateTime(!this.timeGiven, seconds + nanos, this.tzShift);
        }
        
        private SecondsAndNanos toSecondsAndNanos() {
            return new SecondsAndNanos(this.seconds, this.nanos);
        }
    }
}

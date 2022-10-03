package org.apache.commons.net.ntp;

import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;
import java.io.Serializable;

public class TimeStamp implements Serializable, Comparable<TimeStamp>
{
    private static final long serialVersionUID = 8139806907588338737L;
    protected static final long msb0baseTime = 2085978496000L;
    protected static final long msb1baseTime = -2208988800000L;
    public static final String NTP_DATE_FORMAT = "EEE, MMM dd yyyy HH:mm:ss.SSS";
    private final long ntpTime;
    private DateFormat simpleFormatter;
    private DateFormat utcFormatter;
    
    public TimeStamp(final long ntpTime) {
        this.ntpTime = ntpTime;
    }
    
    public TimeStamp(final String hexStamp) throws NumberFormatException {
        this.ntpTime = decodeNtpHexString(hexStamp);
    }
    
    public TimeStamp(final Date d) {
        this.ntpTime = ((d == null) ? 0L : toNtpTime(d.getTime()));
    }
    
    public long ntpValue() {
        return this.ntpTime;
    }
    
    public long getSeconds() {
        return this.ntpTime >>> 32 & 0xFFFFFFFFL;
    }
    
    public long getFraction() {
        return this.ntpTime & 0xFFFFFFFFL;
    }
    
    public long getTime() {
        return getTime(this.ntpTime);
    }
    
    public Date getDate() {
        final long time = getTime(this.ntpTime);
        return new Date(time);
    }
    
    public static long getTime(final long ntpTimeValue) {
        final long seconds = ntpTimeValue >>> 32 & 0xFFFFFFFFL;
        long fraction = ntpTimeValue & 0xFFFFFFFFL;
        fraction = Math.round(1000.0 * fraction / 4.294967296E9);
        final long msb = seconds & 0x80000000L;
        if (msb == 0L) {
            return 2085978496000L + seconds * 1000L + fraction;
        }
        return -2208988800000L + seconds * 1000L + fraction;
    }
    
    public static TimeStamp getNtpTime(final long date) {
        return new TimeStamp(toNtpTime(date));
    }
    
    public static TimeStamp getCurrentTime() {
        return getNtpTime(System.currentTimeMillis());
    }
    
    protected static long decodeNtpHexString(final String hexString) throws NumberFormatException {
        if (hexString == null) {
            throw new NumberFormatException("null");
        }
        final int ind = hexString.indexOf(46);
        if (ind != -1) {
            return Long.parseLong(hexString.substring(0, ind), 16) << 32 | Long.parseLong(hexString.substring(ind + 1), 16);
        }
        if (hexString.length() == 0) {
            return 0L;
        }
        return Long.parseLong(hexString, 16) << 32;
    }
    
    public static TimeStamp parseNtpString(final String s) throws NumberFormatException {
        return new TimeStamp(decodeNtpHexString(s));
    }
    
    protected static long toNtpTime(final long t) {
        final boolean useBase1 = t < 2085978496000L;
        long baseTime;
        if (useBase1) {
            baseTime = t + 2208988800000L;
        }
        else {
            baseTime = t - 2085978496000L;
        }
        long seconds = baseTime / 1000L;
        final long fraction = baseTime % 1000L * 4294967296L / 1000L;
        if (useBase1) {
            seconds |= 0x80000000L;
        }
        final long time = seconds << 32 | fraction;
        return time;
    }
    
    @Override
    public int hashCode() {
        return (int)(this.ntpTime ^ this.ntpTime >>> 32);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TimeStamp && this.ntpTime == ((TimeStamp)obj).ntpValue();
    }
    
    @Override
    public String toString() {
        return toString(this.ntpTime);
    }
    
    private static void appendHexString(final StringBuilder buf, final long l) {
        final String s = Long.toHexString(l);
        for (int i = s.length(); i < 8; ++i) {
            buf.append('0');
        }
        buf.append(s);
    }
    
    public static String toString(final long ntpTime) {
        final StringBuilder buf = new StringBuilder();
        appendHexString(buf, ntpTime >>> 32 & 0xFFFFFFFFL);
        buf.append('.');
        appendHexString(buf, ntpTime & 0xFFFFFFFFL);
        return buf.toString();
    }
    
    public String toDateString() {
        if (this.simpleFormatter == null) {
            (this.simpleFormatter = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss.SSS", Locale.US)).setTimeZone(TimeZone.getDefault());
        }
        final Date ntpDate = this.getDate();
        return this.simpleFormatter.format(ntpDate);
    }
    
    public String toUTCString() {
        if (this.utcFormatter == null) {
            (this.utcFormatter = new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss.SSS 'UTC'", Locale.US)).setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        final Date ntpDate = this.getDate();
        return this.utcFormatter.format(ntpDate);
    }
    
    @Override
    public int compareTo(final TimeStamp anotherTimeStamp) {
        final long thisVal = this.ntpTime;
        final long anotherVal = anotherTimeStamp.ntpTime;
        return (thisVal < anotherVal) ? -1 : ((thisVal == anotherVal) ? 0 : 1);
    }
}

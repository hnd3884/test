package org.apache.commons.net.ftp.parser;

import org.apache.commons.net.ftp.FTPClientConfig;
import java.util.TimeZone;
import java.util.Date;
import java.text.ParsePosition;
import java.text.ParseException;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import org.apache.commons.net.ftp.Configurable;

public class FTPTimestampParserImpl implements FTPTimestampParser, Configurable
{
    private SimpleDateFormat defaultDateFormat;
    private int defaultDateSmallestUnitIndex;
    private SimpleDateFormat recentDateFormat;
    private int recentDateSmallestUnitIndex;
    private boolean lenientFutureDates;
    private static final int[] CALENDAR_UNITS;
    
    private static int getEntry(final SimpleDateFormat dateFormat) {
        if (dateFormat == null) {
            return 0;
        }
        final String FORMAT_CHARS = "SsmHdM";
        final String pattern = dateFormat.toPattern();
        for (final char ch : "SsmHdM".toCharArray()) {
            if (pattern.indexOf(ch) != -1) {
                switch (ch) {
                    case 'S': {
                        return indexOf(14);
                    }
                    case 's': {
                        return indexOf(13);
                    }
                    case 'm': {
                        return indexOf(12);
                    }
                    case 'H': {
                        return indexOf(11);
                    }
                    case 'd': {
                        return indexOf(5);
                    }
                    case 'M': {
                        return indexOf(2);
                    }
                }
            }
        }
        return 0;
    }
    
    private static int indexOf(final int calendarUnit) {
        for (int i = 0; i < FTPTimestampParserImpl.CALENDAR_UNITS.length; ++i) {
            if (calendarUnit == FTPTimestampParserImpl.CALENDAR_UNITS[i]) {
                return i;
            }
        }
        return 0;
    }
    
    private static void setPrecision(final int index, final Calendar working) {
        if (index <= 0) {
            return;
        }
        final int field = FTPTimestampParserImpl.CALENDAR_UNITS[index - 1];
        final int value = working.get(field);
        if (value == 0) {
            working.clear(field);
        }
    }
    
    public FTPTimestampParserImpl() {
        this.lenientFutureDates = false;
        this.setDefaultDateFormat("MMM d yyyy", null);
        this.setRecentDateFormat("MMM d HH:mm", null);
    }
    
    @Override
    public Calendar parseTimestamp(final String timestampStr) throws ParseException {
        final Calendar now = Calendar.getInstance();
        return this.parseTimestamp(timestampStr, now);
    }
    
    public Calendar parseTimestamp(final String timestampStr, final Calendar serverTime) throws ParseException {
        final Calendar working = (Calendar)serverTime.clone();
        working.setTimeZone(this.getServerTimeZone());
        Date parsed = null;
        if (this.recentDateFormat != null) {
            final Calendar now = (Calendar)serverTime.clone();
            now.setTimeZone(this.getServerTimeZone());
            if (this.lenientFutureDates) {
                now.add(5, 1);
            }
            final String year = Integer.toString(now.get(1));
            final String timeStampStrPlusYear = timestampStr + " " + year;
            final SimpleDateFormat hackFormatter = new SimpleDateFormat(this.recentDateFormat.toPattern() + " yyyy", this.recentDateFormat.getDateFormatSymbols());
            hackFormatter.setLenient(false);
            hackFormatter.setTimeZone(this.recentDateFormat.getTimeZone());
            final ParsePosition pp = new ParsePosition(0);
            parsed = hackFormatter.parse(timeStampStrPlusYear, pp);
            if (parsed != null && pp.getIndex() == timeStampStrPlusYear.length()) {
                working.setTime(parsed);
                if (working.after(now)) {
                    working.add(1, -1);
                }
                setPrecision(this.recentDateSmallestUnitIndex, working);
                return working;
            }
        }
        final ParsePosition pp2 = new ParsePosition(0);
        parsed = this.defaultDateFormat.parse(timestampStr, pp2);
        if (parsed != null && pp2.getIndex() == timestampStr.length()) {
            working.setTime(parsed);
            setPrecision(this.defaultDateSmallestUnitIndex, working);
            return working;
        }
        throw new ParseException("Timestamp '" + timestampStr + "' could not be parsed using a server time of " + serverTime.getTime().toString(), pp2.getErrorIndex());
    }
    
    public SimpleDateFormat getDefaultDateFormat() {
        return this.defaultDateFormat;
    }
    
    public String getDefaultDateFormatString() {
        return this.defaultDateFormat.toPattern();
    }
    
    private void setDefaultDateFormat(final String format, final DateFormatSymbols dfs) {
        if (format != null) {
            if (dfs != null) {
                this.defaultDateFormat = new SimpleDateFormat(format, dfs);
            }
            else {
                this.defaultDateFormat = new SimpleDateFormat(format);
            }
            this.defaultDateFormat.setLenient(false);
        }
        else {
            this.defaultDateFormat = null;
        }
        this.defaultDateSmallestUnitIndex = getEntry(this.defaultDateFormat);
    }
    
    public SimpleDateFormat getRecentDateFormat() {
        return this.recentDateFormat;
    }
    
    public String getRecentDateFormatString() {
        return this.recentDateFormat.toPattern();
    }
    
    private void setRecentDateFormat(final String format, final DateFormatSymbols dfs) {
        if (format != null) {
            if (dfs != null) {
                this.recentDateFormat = new SimpleDateFormat(format, dfs);
            }
            else {
                this.recentDateFormat = new SimpleDateFormat(format);
            }
            this.recentDateFormat.setLenient(false);
        }
        else {
            this.recentDateFormat = null;
        }
        this.recentDateSmallestUnitIndex = getEntry(this.recentDateFormat);
    }
    
    public String[] getShortMonths() {
        return this.defaultDateFormat.getDateFormatSymbols().getShortMonths();
    }
    
    public TimeZone getServerTimeZone() {
        return this.defaultDateFormat.getTimeZone();
    }
    
    private void setServerTimeZone(final String serverTimeZoneId) {
        TimeZone serverTimeZone = TimeZone.getDefault();
        if (serverTimeZoneId != null) {
            serverTimeZone = TimeZone.getTimeZone(serverTimeZoneId);
        }
        this.defaultDateFormat.setTimeZone(serverTimeZone);
        if (this.recentDateFormat != null) {
            this.recentDateFormat.setTimeZone(serverTimeZone);
        }
    }
    
    @Override
    public void configure(final FTPClientConfig config) {
        DateFormatSymbols dfs = null;
        final String languageCode = config.getServerLanguageCode();
        final String shortmonths = config.getShortMonthNames();
        if (shortmonths != null) {
            dfs = FTPClientConfig.getDateFormatSymbols(shortmonths);
        }
        else if (languageCode != null) {
            dfs = FTPClientConfig.lookupDateFormatSymbols(languageCode);
        }
        else {
            dfs = FTPClientConfig.lookupDateFormatSymbols("en");
        }
        final String recentFormatString = config.getRecentDateFormatStr();
        this.setRecentDateFormat(recentFormatString, dfs);
        final String defaultFormatString = config.getDefaultDateFormatStr();
        if (defaultFormatString == null) {
            throw new IllegalArgumentException("defaultFormatString cannot be null");
        }
        this.setDefaultDateFormat(defaultFormatString, dfs);
        this.setServerTimeZone(config.getServerTimeZoneId());
        this.lenientFutureDates = config.isLenientFutureDates();
    }
    
    boolean isLenientFutureDates() {
        return this.lenientFutureDates;
    }
    
    void setLenientFutureDates(final boolean lenientFutureDates) {
        this.lenientFutureDates = lenientFutureDates;
    }
    
    static {
        CALENDAR_UNITS = new int[] { 14, 13, 12, 11, 5, 2, 1 };
    }
}

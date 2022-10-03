package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

public final class Era
{
    private final String name;
    private final String abbr;
    private final long since;
    private final CalendarDate sinceDate;
    private final boolean localTime;
    private int hash;
    
    public Era(final String name, final String abbr, final long since, final boolean localTime) {
        this.hash = 0;
        this.name = name;
        this.abbr = abbr;
        this.since = since;
        this.localTime = localTime;
        final Gregorian gregorianCalendar = CalendarSystem.getGregorianCalendar();
        final Gregorian.Date calendarDate = gregorianCalendar.newCalendarDate(null);
        gregorianCalendar.getCalendarDate(since, calendarDate);
        this.sinceDate = new ImmutableGregorianDate(calendarDate);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplayName(final Locale locale) {
        return this.name;
    }
    
    public String getAbbreviation() {
        return this.abbr;
    }
    
    public String getDiaplayAbbreviation(final Locale locale) {
        return this.abbr;
    }
    
    public long getSince(final TimeZone timeZone) {
        if (timeZone == null || !this.localTime) {
            return this.since;
        }
        return this.since - timeZone.getOffset(this.since);
    }
    
    public CalendarDate getSinceDate() {
        return this.sinceDate;
    }
    
    public boolean isLocalTime() {
        return this.localTime;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Era)) {
            return false;
        }
        final Era era = (Era)o;
        return this.name.equals(era.name) && this.abbr.equals(era.abbr) && this.since == era.since && this.localTime == era.localTime;
    }
    
    @Override
    public int hashCode() {
        if (this.hash == 0) {
            this.hash = (this.name.hashCode() ^ this.abbr.hashCode() ^ (int)this.since ^ (int)(this.since >> 32) ^ (this.localTime ? 1 : 0));
        }
        return this.hash;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(this.getName()).append(" (");
        sb.append(this.getAbbreviation()).append(')');
        sb.append(" since ").append(this.getSinceDate());
        if (this.localTime) {
            sb.setLength(sb.length() - 1);
            sb.append(" local time");
        }
        sb.append(']');
        return sb.toString();
    }
}

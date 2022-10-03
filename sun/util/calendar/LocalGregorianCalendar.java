package sun.util.calendar;

import java.util.TimeZone;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.IOException;

public class LocalGregorianCalendar extends BaseCalendar
{
    private String name;
    private Era[] eras;
    
    static LocalGregorianCalendar getLocalGregorianCalendar(final String s) {
        Properties calendarProperties;
        try {
            calendarProperties = CalendarSystem.getCalendarProperties();
        }
        catch (final IOException | IllegalArgumentException ex) {
            throw new InternalError((Throwable)ex);
        }
        final String property = calendarProperties.getProperty("calendar." + s + ".eras");
        if (property == null) {
            return null;
        }
        final ArrayList list = new ArrayList();
        final StringTokenizer stringTokenizer = new StringTokenizer(property, ";");
        while (stringTokenizer.hasMoreTokens()) {
            final StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken().trim(), ",");
            String s2 = null;
            boolean b = true;
            long n = 0L;
            String s3 = null;
            while (stringTokenizer2.hasMoreTokens()) {
                final String nextToken = stringTokenizer2.nextToken();
                final int index = nextToken.indexOf(61);
                if (index == -1) {
                    return null;
                }
                final String substring = nextToken.substring(0, index);
                final String substring2 = nextToken.substring(index + 1);
                if ("name".equals(substring)) {
                    s2 = substring2;
                }
                else if ("since".equals(substring)) {
                    if (substring2.endsWith("u")) {
                        b = false;
                        n = Long.parseLong(substring2.substring(0, substring2.length() - 1));
                    }
                    else {
                        n = Long.parseLong(substring2);
                    }
                }
                else {
                    if (!"abbr".equals(substring)) {
                        throw new RuntimeException("Unknown key word: " + substring);
                    }
                    s3 = substring2;
                }
            }
            list.add(new Era(s2, s3, n, b));
        }
        final Era[] array = new Era[list.size()];
        list.toArray(array);
        return new LocalGregorianCalendar(s, array);
    }
    
    private LocalGregorianCalendar(final String name, final Era[] eras) {
        this.name = name;
        this.setEras(this.eras = eras);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public Date getCalendarDate() {
        return this.getCalendarDate(System.currentTimeMillis(), this.newCalendarDate());
    }
    
    @Override
    public Date getCalendarDate(final long n) {
        return this.getCalendarDate(n, this.newCalendarDate());
    }
    
    @Override
    public Date getCalendarDate(final long n, final TimeZone timeZone) {
        return this.getCalendarDate(n, this.newCalendarDate(timeZone));
    }
    
    @Override
    public Date getCalendarDate(final long n, final CalendarDate calendarDate) {
        final Date date = (Date)super.getCalendarDate(n, calendarDate);
        return this.adjustYear(date, n, date.getZoneOffset());
    }
    
    private Date adjustYear(final Date date, final long n, final int n2) {
        int i;
        for (i = this.eras.length - 1; i >= 0; --i) {
            final Era localEra = this.eras[i];
            long since = localEra.getSince(null);
            if (localEra.isLocalTime()) {
                since -= n2;
            }
            if (n >= since) {
                date.setLocalEra(localEra);
                date.setLocalYear(date.getNormalizedYear() - localEra.getSinceDate().getYear() + 1);
                break;
            }
        }
        if (i < 0) {
            date.setLocalEra(null);
            date.setLocalYear(date.getNormalizedYear());
        }
        date.setNormalized(true);
        return date;
    }
    
    @Override
    public Date newCalendarDate() {
        return new Date();
    }
    
    @Override
    public Date newCalendarDate(final TimeZone timeZone) {
        return new Date(timeZone);
    }
    
    @Override
    public boolean validate(final CalendarDate calendarDate) {
        final Date date = (Date)calendarDate;
        final Era era = date.getEra();
        if (era != null) {
            if (!this.validateEra(era)) {
                return false;
            }
            date.setNormalizedYear(era.getSinceDate().getYear() + date.getYear() - 1);
            final Date calendarDate2 = this.newCalendarDate(calendarDate.getZone());
            calendarDate2.setEra(era).setDate(calendarDate.getYear(), calendarDate.getMonth(), calendarDate.getDayOfMonth());
            this.normalize(calendarDate2);
            if (calendarDate2.getEra() != era) {
                return false;
            }
        }
        else {
            if (calendarDate.getYear() >= this.eras[0].getSinceDate().getYear()) {
                return false;
            }
            date.setNormalizedYear(date.getYear());
        }
        return super.validate(date);
    }
    
    private boolean validateEra(final Era era) {
        for (int i = 0; i < this.eras.length; ++i) {
            if (era == this.eras[i]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean normalize(final CalendarDate calendarDate) {
        if (calendarDate.isNormalized()) {
            return true;
        }
        this.normalizeYear(calendarDate);
        final Date date = (Date)calendarDate;
        super.normalize(date);
        int n = 0;
        long time = 0L;
        final int normalizedYear = date.getNormalizedYear();
        Era localEra = null;
        int i;
        for (i = this.eras.length - 1; i >= 0; --i) {
            localEra = this.eras[i];
            if (localEra.isLocalTime()) {
                final CalendarDate sinceDate = localEra.getSinceDate();
                final int year = sinceDate.getYear();
                if (normalizedYear > year) {
                    break;
                }
                if (normalizedYear == year) {
                    final int month = date.getMonth();
                    final int month2 = sinceDate.getMonth();
                    if (month > month2) {
                        break;
                    }
                    if (month == month2) {
                        final int dayOfMonth = date.getDayOfMonth();
                        final int dayOfMonth2 = sinceDate.getDayOfMonth();
                        if (dayOfMonth > dayOfMonth2) {
                            break;
                        }
                        if (dayOfMonth == dayOfMonth2) {
                            if (date.getTimeOfDay() >= sinceDate.getTimeOfDay()) {
                                break;
                            }
                            --i;
                            break;
                        }
                    }
                }
            }
            else {
                if (n == 0) {
                    time = super.getTime(calendarDate);
                    n = 1;
                }
                if (time >= localEra.getSince(calendarDate.getZone())) {
                    break;
                }
            }
        }
        if (i >= 0) {
            date.setLocalEra(localEra);
            date.setLocalYear(date.getNormalizedYear() - localEra.getSinceDate().getYear() + 1);
        }
        else {
            date.setEra(null);
            date.setLocalYear(normalizedYear);
            date.setNormalizedYear(normalizedYear);
        }
        date.setNormalized(true);
        return true;
    }
    
    @Override
    void normalizeMonth(final CalendarDate calendarDate) {
        this.normalizeYear(calendarDate);
        super.normalizeMonth(calendarDate);
    }
    
    void normalizeYear(final CalendarDate calendarDate) {
        final Date date = (Date)calendarDate;
        final Era era = date.getEra();
        if (era == null || !this.validateEra(era)) {
            date.setNormalizedYear(date.getYear());
        }
        else {
            date.setNormalizedYear(era.getSinceDate().getYear() + date.getYear() - 1);
        }
    }
    
    public boolean isLeapYear(final int n) {
        return CalendarUtils.isGregorianLeapYear(n);
    }
    
    public boolean isLeapYear(final Era era, final int n) {
        if (era == null) {
            return this.isLeapYear(n);
        }
        return this.isLeapYear(era.getSinceDate().getYear() + n - 1);
    }
    
    @Override
    public void getCalendarDateFromFixedDate(final CalendarDate calendarDate, final long n) {
        final Date date = (Date)calendarDate;
        super.getCalendarDateFromFixedDate(date, n);
        this.adjustYear(date, (n - 719163L) * 86400000L, 0);
    }
    
    public static class Date extends BaseCalendar.Date
    {
        private int gregorianYear;
        
        protected Date() {
            this.gregorianYear = Integer.MIN_VALUE;
        }
        
        protected Date(final TimeZone timeZone) {
            super(timeZone);
            this.gregorianYear = Integer.MIN_VALUE;
        }
        
        @Override
        public Date setEra(final Era era) {
            if (this.getEra() != era) {
                super.setEra(era);
                this.gregorianYear = Integer.MIN_VALUE;
            }
            return this;
        }
        
        @Override
        public Date addYear(final int n) {
            super.addYear(n);
            this.gregorianYear += n;
            return this;
        }
        
        @Override
        public Date setYear(final int year) {
            if (this.getYear() != year) {
                super.setYear(year);
                this.gregorianYear = Integer.MIN_VALUE;
            }
            return this;
        }
        
        @Override
        public int getNormalizedYear() {
            return this.gregorianYear;
        }
        
        @Override
        public void setNormalizedYear(final int gregorianYear) {
            this.gregorianYear = gregorianYear;
        }
        
        void setLocalEra(final Era era) {
            super.setEra(era);
        }
        
        void setLocalYear(final int year) {
            super.setYear(year);
        }
        
        @Override
        public String toString() {
            final String string = super.toString();
            final String substring = string.substring(string.indexOf(84));
            final StringBuffer sb = new StringBuffer();
            final Era era = this.getEra();
            if (era != null) {
                final String abbreviation = era.getAbbreviation();
                if (abbreviation != null) {
                    sb.append(abbreviation);
                }
            }
            sb.append(this.getYear()).append('.');
            CalendarUtils.sprintf0d(sb, this.getMonth(), 2).append('.');
            CalendarUtils.sprintf0d(sb, this.getDayOfMonth(), 2);
            sb.append(substring);
            return sb.toString();
        }
    }
}

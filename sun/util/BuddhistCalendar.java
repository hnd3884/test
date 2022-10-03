package sun.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import sun.util.locale.provider.CalendarDataUtility;
import java.util.Locale;
import java.util.TimeZone;
import java.util.GregorianCalendar;

public class BuddhistCalendar extends GregorianCalendar
{
    private static final long serialVersionUID = -8527488697350388578L;
    private static final int BUDDHIST_YEAR_OFFSET = 543;
    private transient int yearOffset;
    
    public BuddhistCalendar() {
        this.yearOffset = 543;
    }
    
    public BuddhistCalendar(final TimeZone timeZone) {
        super(timeZone);
        this.yearOffset = 543;
    }
    
    public BuddhistCalendar(final Locale locale) {
        super(locale);
        this.yearOffset = 543;
    }
    
    public BuddhistCalendar(final TimeZone timeZone, final Locale locale) {
        super(timeZone, locale);
        this.yearOffset = 543;
    }
    
    @Override
    public String getCalendarType() {
        return "buddhist";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof BuddhistCalendar && super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ 0x21F;
    }
    
    @Override
    public int get(final int n) {
        if (n == 1) {
            return super.get(n) + this.yearOffset;
        }
        return super.get(n);
    }
    
    @Override
    public void set(final int n, final int n2) {
        if (n == 1) {
            super.set(n, n2 - this.yearOffset);
        }
        else {
            super.set(n, n2);
        }
    }
    
    @Override
    public void add(final int n, final int n2) {
        final int yearOffset = this.yearOffset;
        this.yearOffset = 0;
        try {
            super.add(n, n2);
        }
        finally {
            this.yearOffset = yearOffset;
        }
    }
    
    @Override
    public void roll(final int n, final int n2) {
        final int yearOffset = this.yearOffset;
        this.yearOffset = 0;
        try {
            super.roll(n, n2);
        }
        finally {
            this.yearOffset = yearOffset;
        }
    }
    
    @Override
    public String getDisplayName(final int n, final int n2, final Locale locale) {
        if (n != 0) {
            return super.getDisplayName(n, n2, locale);
        }
        return CalendarDataUtility.retrieveFieldValueName("buddhist", n, this.get(n), n2, locale);
    }
    
    @Override
    public Map<String, Integer> getDisplayNames(final int n, final int n2, final Locale locale) {
        if (n != 0) {
            return super.getDisplayNames(n, n2, locale);
        }
        return CalendarDataUtility.retrieveFieldValueNames("buddhist", n, n2, locale);
    }
    
    @Override
    public int getActualMaximum(final int n) {
        final int yearOffset = this.yearOffset;
        this.yearOffset = 0;
        try {
            return super.getActualMaximum(n);
        }
        finally {
            this.yearOffset = yearOffset;
        }
    }
    
    @Override
    public String toString() {
        final String string = super.toString();
        if (!this.isSet(1)) {
            return string;
        }
        final int index = string.indexOf("YEAR=");
        if (index == -1) {
            return string;
        }
        int n = index + "YEAR=".length();
        final StringBuilder sb = new StringBuilder(string.substring(0, n));
        while (Character.isDigit(string.charAt(n++))) {}
        sb.append(this.internalGet(1) + 543).append(string.substring(n - 1));
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.yearOffset = 543;
    }
}

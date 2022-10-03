package sun.util.spi;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.spi.LocaleServiceProvider;

public abstract class CalendarProvider extends LocaleServiceProvider
{
    protected CalendarProvider() {
    }
    
    public abstract Calendar getInstance(final TimeZone p0, final Locale p1);
}

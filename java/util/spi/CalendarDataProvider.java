package java.util.spi;

import java.util.Locale;

public abstract class CalendarDataProvider extends LocaleServiceProvider
{
    protected CalendarDataProvider() {
    }
    
    public abstract int getFirstDayOfWeek(final Locale p0);
    
    public abstract int getMinimalDaysInFirstWeek(final Locale p0);
}

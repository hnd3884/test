package java.util.spi;

import java.util.Map;
import java.util.Locale;

public abstract class CalendarNameProvider extends LocaleServiceProvider
{
    protected CalendarNameProvider() {
    }
    
    public abstract String getDisplayName(final String p0, final int p1, final int p2, final int p3, final Locale p4);
    
    public abstract Map<String, Integer> getDisplayNames(final String p0, final int p1, final int p2, final Locale p3);
}

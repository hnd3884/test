package java.util.spi;

import java.util.Locale;

public abstract class TimeZoneNameProvider extends LocaleServiceProvider
{
    protected TimeZoneNameProvider() {
    }
    
    public abstract String getDisplayName(final String p0, final boolean p1, final int p2, final Locale p3);
    
    public String getGenericDisplayName(final String s, final int n, final Locale locale) {
        return null;
    }
}

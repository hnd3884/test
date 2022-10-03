package sun.util.locale.provider;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Locale;
import java.util.Set;
import sun.util.spi.CalendarProvider;

public class CalendarProviderImpl extends CalendarProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public CalendarProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public boolean isSupportedLocale(final Locale locale) {
        return true;
    }
    
    @Override
    public Calendar getInstance(final TimeZone timeZone, final Locale locale) {
        return new Calendar.Builder().setLocale(locale).setTimeZone(timeZone).setInstant(System.currentTimeMillis()).build();
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}

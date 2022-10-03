package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CalendarDataProvider;

public class CalendarDataProviderImpl extends CalendarDataProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public CalendarDataProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public int getFirstDayOfWeek(final Locale locale) {
        return LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getCalendarData("firstDayOfWeek");
    }
    
    @Override
    public int getMinimalDaysInFirstWeek(final Locale locale) {
        return LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getCalendarData("minimalDaysInFirstWeek");
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}

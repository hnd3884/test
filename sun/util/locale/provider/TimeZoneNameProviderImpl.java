package sun.util.locale.provider;

import java.util.Objects;
import java.util.Locale;
import java.util.Set;
import java.util.spi.TimeZoneNameProvider;

public class TimeZoneNameProviderImpl extends TimeZoneNameProvider
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    TimeZoneNameProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public boolean isSupportedLocale(final Locale locale) {
        return LocaleProviderAdapter.isSupportedLocale(locale, this.type, this.langtags);
    }
    
    @Override
    public String getDisplayName(final String s, final boolean b, final int n, final Locale locale) {
        final String[] displayNameArray = this.getDisplayNameArray(s, locale);
        if (!Objects.nonNull(displayNameArray)) {
            return null;
        }
        assert displayNameArray.length >= 7;
        int n2 = b ? 3 : 1;
        if (n == 0) {
            ++n2;
        }
        return displayNameArray[n2];
    }
    
    @Override
    public String getGenericDisplayName(final String s, final int n, final Locale locale) {
        final String[] displayNameArray = this.getDisplayNameArray(s, locale);
        if (!Objects.nonNull(displayNameArray)) {
            return null;
        }
        assert displayNameArray.length >= 7;
        return displayNameArray[(n == 1) ? 5 : 6];
    }
    
    private String[] getDisplayNameArray(final String s, final Locale locale) {
        Objects.requireNonNull(s);
        Objects.requireNonNull(locale);
        return LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getTimeZoneNames(s);
    }
    
    String[][] getZoneStrings(final Locale locale) {
        return LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getZoneStrings();
    }
}

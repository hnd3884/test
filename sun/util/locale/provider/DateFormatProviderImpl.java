package sun.util.locale.provider;

import java.util.Calendar;
import java.util.MissingResourceException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;
import java.util.Set;
import java.text.spi.DateFormatProvider;

public class DateFormatProviderImpl extends DateFormatProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public DateFormatProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
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
    public DateFormat getTimeInstance(final int n, final Locale locale) {
        return this.getInstance(-1, n, locale);
    }
    
    @Override
    public DateFormat getDateInstance(final int n, final Locale locale) {
        return this.getInstance(n, -1, locale);
    }
    
    @Override
    public DateFormat getDateTimeInstance(final int n, final int n2, final Locale locale) {
        return this.getInstance(n, n2, locale);
    }
    
    private DateFormat getInstance(final int n, final int n2, final Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("", locale);
        final Calendar calendar = simpleDateFormat.getCalendar();
        try {
            simpleDateFormat.applyPattern(LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getDateTimePattern(n2, n, calendar));
        }
        catch (final MissingResourceException ex) {
            simpleDateFormat.applyPattern("M/d/yy h:mm a");
        }
        return simpleDateFormat;
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}

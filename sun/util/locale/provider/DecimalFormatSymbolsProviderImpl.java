package sun.util.locale.provider;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Set;
import java.text.spi.DecimalFormatSymbolsProvider;

public class DecimalFormatSymbolsProviderImpl extends DecimalFormatSymbolsProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public DecimalFormatSymbolsProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
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
    public DecimalFormatSymbols getInstance(final Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }
        return new DecimalFormatSymbols(locale);
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}

package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CurrencyNameProvider;

public class CurrencyNameProviderImpl extends CurrencyNameProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public CurrencyNameProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public String getSymbol(final String s, final Locale locale) {
        return this.getString(s.toUpperCase(Locale.ROOT), locale);
    }
    
    @Override
    public String getDisplayName(final String s, final Locale locale) {
        return this.getString(s.toLowerCase(Locale.ROOT), locale);
    }
    
    private String getString(final String s, final Locale locale) {
        if (locale == null) {
            throw new NullPointerException();
        }
        return LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getCurrencyName(s);
    }
}

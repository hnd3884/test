package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.LocaleNameProvider;

public class LocaleNameProviderImpl extends LocaleNameProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public LocaleNameProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
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
    public String getDisplayLanguage(final String s, final Locale locale) {
        return this.getDisplayString(s, locale);
    }
    
    @Override
    public String getDisplayScript(final String s, final Locale locale) {
        return this.getDisplayString(s, locale);
    }
    
    @Override
    public String getDisplayCountry(final String s, final Locale locale) {
        return this.getDisplayString(s, locale);
    }
    
    @Override
    public String getDisplayVariant(final String s, final Locale locale) {
        return this.getDisplayString("%%" + s, locale);
    }
    
    private String getDisplayString(final String s, final Locale locale) {
        if (s == null || locale == null) {
            throw new NullPointerException();
        }
        return LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getLocaleName(s);
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}

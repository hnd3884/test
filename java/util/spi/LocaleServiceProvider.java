package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider
{
    protected LocaleServiceProvider() {
    }
    
    public abstract Locale[] getAvailableLocales();
    
    public boolean isSupportedLocale(Locale stripExtensions) {
        stripExtensions = stripExtensions.stripExtensions();
        final Locale[] availableLocales = this.getAvailableLocales();
        for (int length = availableLocales.length, i = 0; i < length; ++i) {
            if (stripExtensions.equals(availableLocales[i].stripExtensions())) {
                return true;
            }
        }
        return false;
    }
}

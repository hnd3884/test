package java.util.spi;

import java.util.Locale;

public abstract class LocaleNameProvider extends LocaleServiceProvider
{
    protected LocaleNameProvider() {
    }
    
    public abstract String getDisplayLanguage(final String p0, final Locale p1);
    
    public String getDisplayScript(final String s, final Locale locale) {
        return null;
    }
    
    public abstract String getDisplayCountry(final String p0, final Locale p1);
    
    public abstract String getDisplayVariant(final String p0, final Locale p1);
}

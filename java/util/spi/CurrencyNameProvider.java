package java.util.spi;

import java.util.ResourceBundle;
import java.util.Locale;

public abstract class CurrencyNameProvider extends LocaleServiceProvider
{
    protected CurrencyNameProvider() {
    }
    
    public abstract String getSymbol(final String p0, final Locale p1);
    
    public String getDisplayName(final String s, final Locale locale) {
        if (s == null || locale == null) {
            throw new NullPointerException();
        }
        final char[] charArray = s.toCharArray();
        if (charArray.length != 3) {
            throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters.");
        }
        for (final char c : charArray) {
            if (c < 'A' || c > 'Z') {
                throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters.");
            }
        }
        final ResourceBundle.Control noFallbackControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
        final Locale[] availableLocales = this.getAvailableLocales();
        for (int length2 = availableLocales.length, j = 0; j < length2; ++j) {
            if (noFallbackControl.getCandidateLocales("", availableLocales[j]).contains(locale)) {
                return null;
            }
        }
        throw new IllegalArgumentException("The locale is not available");
    }
}

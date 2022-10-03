package sun.util.locale.provider;

import java.util.Currency;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;
import java.text.spi.NumberFormatProvider;

public class NumberFormatProviderImpl extends NumberFormatProvider implements AvailableLanguageTags
{
    private static final int NUMBERSTYLE = 0;
    private static final int CURRENCYSTYLE = 1;
    private static final int PERCENTSTYLE = 2;
    private static final int SCIENTIFICSTYLE = 3;
    private static final int INTEGERSTYLE = 4;
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    
    public NumberFormatProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.forType(this.type).getAvailableLocales();
    }
    
    @Override
    public boolean isSupportedLocale(final Locale locale) {
        return LocaleProviderAdapter.isSupportedLocale(locale, this.type, this.langtags);
    }
    
    @Override
    public NumberFormat getCurrencyInstance(final Locale locale) {
        return this.getInstance(locale, 1);
    }
    
    @Override
    public NumberFormat getIntegerInstance(final Locale locale) {
        return this.getInstance(locale, 4);
    }
    
    @Override
    public NumberFormat getNumberInstance(final Locale locale) {
        return this.getInstance(locale, 0);
    }
    
    @Override
    public NumberFormat getPercentInstance(final Locale locale) {
        return this.getInstance(locale, 2);
    }
    
    private NumberFormat getInstance(final Locale locale, final int n) {
        if (locale == null) {
            throw new NullPointerException();
        }
        final String[] numberPatterns = LocaleProviderAdapter.forType(this.type).getLocaleResources(locale).getNumberPatterns();
        final DecimalFormatSymbols instance = DecimalFormatSymbols.getInstance(locale);
        final DecimalFormat decimalFormat = new DecimalFormat(numberPatterns[(n == 4) ? 0 : n], instance);
        if (n == 4) {
            decimalFormat.setMaximumFractionDigits(0);
            decimalFormat.setDecimalSeparatorAlwaysShown(false);
            decimalFormat.setParseIntegerOnly(true);
        }
        else if (n == 1) {
            adjustForCurrencyDefaultFractionDigits(decimalFormat, instance);
        }
        return decimalFormat;
    }
    
    private static void adjustForCurrencyDefaultFractionDigits(final DecimalFormat decimalFormat, final DecimalFormatSymbols decimalFormatSymbols) {
        Currency currency = decimalFormatSymbols.getCurrency();
        if (currency == null) {
            try {
                currency = Currency.getInstance(decimalFormatSymbols.getInternationalCurrencySymbol());
            }
            catch (final IllegalArgumentException ex) {}
        }
        if (currency != null) {
            final int defaultFractionDigits = currency.getDefaultFractionDigits();
            if (defaultFractionDigits != -1) {
                final int minimumFractionDigits = decimalFormat.getMinimumFractionDigits();
                if (minimumFractionDigits == decimalFormat.getMaximumFractionDigits()) {
                    decimalFormat.setMinimumFractionDigits(defaultFractionDigits);
                    decimalFormat.setMaximumFractionDigits(defaultFractionDigits);
                }
                else {
                    decimalFormat.setMinimumFractionDigits(Math.min(defaultFractionDigits, minimumFractionDigits));
                    decimalFormat.setMaximumFractionDigits(defaultFractionDigits);
                }
            }
        }
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
}

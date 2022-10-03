package java.text.spi;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DecimalFormatSymbolsProvider extends LocaleServiceProvider
{
    protected DecimalFormatSymbolsProvider() {
    }
    
    public abstract DecimalFormatSymbols getInstance(final Locale p0);
}

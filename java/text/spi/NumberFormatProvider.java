package java.text.spi;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class NumberFormatProvider extends LocaleServiceProvider
{
    protected NumberFormatProvider() {
    }
    
    public abstract NumberFormat getCurrencyInstance(final Locale p0);
    
    public abstract NumberFormat getIntegerInstance(final Locale p0);
    
    public abstract NumberFormat getNumberInstance(final Locale p0);
    
    public abstract NumberFormat getPercentInstance(final Locale p0);
}

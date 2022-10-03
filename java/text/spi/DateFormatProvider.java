package java.text.spi;

import java.text.DateFormat;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DateFormatProvider extends LocaleServiceProvider
{
    protected DateFormatProvider() {
    }
    
    public abstract DateFormat getTimeInstance(final int p0, final Locale p1);
    
    public abstract DateFormat getDateInstance(final int p0, final Locale p1);
    
    public abstract DateFormat getDateTimeInstance(final int p0, final int p1, final Locale p2);
}

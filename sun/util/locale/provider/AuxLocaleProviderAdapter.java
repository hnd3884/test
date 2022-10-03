package sun.util.locale.provider;

import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;
import sun.util.spi.CalendarProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CalendarDataProvider;
import java.util.spi.TimeZoneNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.text.spi.NumberFormatProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.BreakIteratorProvider;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;
import java.util.concurrent.ConcurrentMap;

public abstract class AuxLocaleProviderAdapter extends LocaleProviderAdapter
{
    private ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProvider> providersMap;
    private static Locale[] availableLocales;
    private static NullProvider NULL_PROVIDER;
    
    public AuxLocaleProviderAdapter() {
        this.providersMap = new ConcurrentHashMap<Class<? extends LocaleServiceProvider>, LocaleServiceProvider>();
    }
    
    @Override
    public <P extends LocaleServiceProvider> P getLocaleServiceProvider(final Class<P> clazz) {
        LocaleServiceProvider installedProvider = (LocaleServiceProvider)this.providersMap.get(clazz);
        if (installedProvider == null) {
            installedProvider = this.findInstalledProvider((Class<LocaleServiceProvider>)clazz);
            this.providersMap.putIfAbsent(clazz, (installedProvider == null) ? AuxLocaleProviderAdapter.NULL_PROVIDER : installedProvider);
        }
        return (P)installedProvider;
    }
    
    protected abstract <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> p0);
    
    @Override
    public BreakIteratorProvider getBreakIteratorProvider() {
        return this.getLocaleServiceProvider(BreakIteratorProvider.class);
    }
    
    @Override
    public CollatorProvider getCollatorProvider() {
        return this.getLocaleServiceProvider(CollatorProvider.class);
    }
    
    @Override
    public DateFormatProvider getDateFormatProvider() {
        return this.getLocaleServiceProvider(DateFormatProvider.class);
    }
    
    @Override
    public DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
        return this.getLocaleServiceProvider(DateFormatSymbolsProvider.class);
    }
    
    @Override
    public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
        return this.getLocaleServiceProvider(DecimalFormatSymbolsProvider.class);
    }
    
    @Override
    public NumberFormatProvider getNumberFormatProvider() {
        return this.getLocaleServiceProvider(NumberFormatProvider.class);
    }
    
    @Override
    public CurrencyNameProvider getCurrencyNameProvider() {
        return this.getLocaleServiceProvider(CurrencyNameProvider.class);
    }
    
    @Override
    public LocaleNameProvider getLocaleNameProvider() {
        return this.getLocaleServiceProvider(LocaleNameProvider.class);
    }
    
    @Override
    public TimeZoneNameProvider getTimeZoneNameProvider() {
        return this.getLocaleServiceProvider(TimeZoneNameProvider.class);
    }
    
    @Override
    public CalendarDataProvider getCalendarDataProvider() {
        return this.getLocaleServiceProvider(CalendarDataProvider.class);
    }
    
    @Override
    public CalendarNameProvider getCalendarNameProvider() {
        return this.getLocaleServiceProvider(CalendarNameProvider.class);
    }
    
    @Override
    public CalendarProvider getCalendarProvider() {
        return this.getLocaleServiceProvider(CalendarProvider.class);
    }
    
    @Override
    public LocaleResources getLocaleResources(final Locale locale) {
        return null;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        if (AuxLocaleProviderAdapter.availableLocales == null) {
            final HashSet set = new HashSet();
            final Class<LocaleServiceProvider>[] spiClasses = LocaleServiceProviderPool.spiClasses;
            for (int length = spiClasses.length, i = 0; i < length; ++i) {
                final LocaleServiceProvider localeServiceProvider = this.getLocaleServiceProvider(spiClasses[i]);
                if (localeServiceProvider != null) {
                    set.addAll(Arrays.asList(localeServiceProvider.getAvailableLocales()));
                }
            }
            AuxLocaleProviderAdapter.availableLocales = (Locale[])set.toArray(new Locale[0]);
        }
        return AuxLocaleProviderAdapter.availableLocales;
    }
    
    static {
        AuxLocaleProviderAdapter.availableLocales = null;
        AuxLocaleProviderAdapter.NULL_PROVIDER = new NullProvider();
    }
    
    private static class NullProvider extends LocaleServiceProvider
    {
        @Override
        public Locale[] getAvailableLocales() {
            return new Locale[0];
        }
    }
}

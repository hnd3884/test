package sun.util.locale.provider;

import java.util.spi.TimeZoneNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CalendarDataProvider;
import java.text.NumberFormat;
import java.text.spi.NumberFormatProvider;
import java.text.DecimalFormatSymbols;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.DateFormatSymbols;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.DateFormat;
import java.text.spi.DateFormatProvider;
import java.text.Collator;
import java.text.spi.CollatorProvider;
import java.text.BreakIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import java.util.Map;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.security.PrivilegedExceptionAction;
import java.util.spi.LocaleServiceProvider;

public class SPILocaleProviderAdapter extends AuxLocaleProviderAdapter
{
    @Override
    public Type getAdapterType() {
        return Type.SPI;
    }
    
    @Override
    protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> clazz) {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<P>)new PrivilegedExceptionAction<P>() {
                @Override
                public P run() {
                    Object o = null;
                    for (final LocaleServiceProvider localeServiceProvider : ServiceLoader.loadInstalled((Class<LocaleServiceProvider>)clazz)) {
                        if (o == null) {
                            try {
                                o = (LocaleServiceProvider)Class.forName(SPILocaleProviderAdapter.class.getCanonicalName() + "$" + clazz.getSimpleName() + "Delegate").newInstance();
                            }
                            catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                                LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, ((Throwable)ex).toString());
                                return null;
                            }
                        }
                        ((Delegate<LocaleServiceProvider>)o).addImpl(localeServiceProvider);
                    }
                    return (P)o;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            LocaleServiceProviderPool.config(SPILocaleProviderAdapter.class, ex.toString());
            return null;
        }
    }
    
    private static <P extends LocaleServiceProvider> P getImpl(final Map<Locale, P> map, final Locale locale) {
        final Iterator<Locale> iterator = LocaleServiceProviderPool.getLookupLocales(locale).iterator();
        while (iterator.hasNext()) {
            final LocaleServiceProvider localeServiceProvider = map.get(iterator.next());
            if (localeServiceProvider != null) {
                return (P)localeServiceProvider;
            }
        }
        return null;
    }
    
    static class BreakIteratorProviderDelegate extends BreakIteratorProvider implements Delegate<BreakIteratorProvider>
    {
        private ConcurrentMap<Locale, BreakIteratorProvider> map;
        
        BreakIteratorProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, BreakIteratorProvider>();
        }
        
        @Override
        public void addImpl(final BreakIteratorProvider breakIteratorProvider) {
            final Locale[] availableLocales = breakIteratorProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], breakIteratorProvider);
            }
        }
        
        @Override
        public BreakIteratorProvider getImpl(final Locale locale) {
            return (BreakIteratorProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public BreakIterator getWordInstance(final Locale locale) {
            final BreakIteratorProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getWordInstance(locale);
        }
        
        @Override
        public BreakIterator getLineInstance(final Locale locale) {
            final BreakIteratorProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getLineInstance(locale);
        }
        
        @Override
        public BreakIterator getCharacterInstance(final Locale locale) {
            final BreakIteratorProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getCharacterInstance(locale);
        }
        
        @Override
        public BreakIterator getSentenceInstance(final Locale locale) {
            final BreakIteratorProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getSentenceInstance(locale);
        }
    }
    
    static class CollatorProviderDelegate extends CollatorProvider implements Delegate<CollatorProvider>
    {
        private ConcurrentMap<Locale, CollatorProvider> map;
        
        CollatorProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, CollatorProvider>();
        }
        
        @Override
        public void addImpl(final CollatorProvider collatorProvider) {
            final Locale[] availableLocales = collatorProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], collatorProvider);
            }
        }
        
        @Override
        public CollatorProvider getImpl(final Locale locale) {
            return (CollatorProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public Collator getInstance(final Locale locale) {
            final CollatorProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getInstance(locale);
        }
    }
    
    static class DateFormatProviderDelegate extends DateFormatProvider implements Delegate<DateFormatProvider>
    {
        private ConcurrentMap<Locale, DateFormatProvider> map;
        
        DateFormatProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, DateFormatProvider>();
        }
        
        @Override
        public void addImpl(final DateFormatProvider dateFormatProvider) {
            final Locale[] availableLocales = dateFormatProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], dateFormatProvider);
            }
        }
        
        @Override
        public DateFormatProvider getImpl(final Locale locale) {
            return (DateFormatProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public DateFormat getTimeInstance(final int n, final Locale locale) {
            final DateFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getTimeInstance(n, locale);
        }
        
        @Override
        public DateFormat getDateInstance(final int n, final Locale locale) {
            final DateFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDateInstance(n, locale);
        }
        
        @Override
        public DateFormat getDateTimeInstance(final int n, final int n2, final Locale locale) {
            final DateFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDateTimeInstance(n, n2, locale);
        }
    }
    
    static class DateFormatSymbolsProviderDelegate extends DateFormatSymbolsProvider implements Delegate<DateFormatSymbolsProvider>
    {
        private ConcurrentMap<Locale, DateFormatSymbolsProvider> map;
        
        DateFormatSymbolsProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, DateFormatSymbolsProvider>();
        }
        
        @Override
        public void addImpl(final DateFormatSymbolsProvider dateFormatSymbolsProvider) {
            final Locale[] availableLocales = dateFormatSymbolsProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], dateFormatSymbolsProvider);
            }
        }
        
        @Override
        public DateFormatSymbolsProvider getImpl(final Locale locale) {
            return (DateFormatSymbolsProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public DateFormatSymbols getInstance(final Locale locale) {
            final DateFormatSymbolsProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getInstance(locale);
        }
    }
    
    static class DecimalFormatSymbolsProviderDelegate extends DecimalFormatSymbolsProvider implements Delegate<DecimalFormatSymbolsProvider>
    {
        private ConcurrentMap<Locale, DecimalFormatSymbolsProvider> map;
        
        DecimalFormatSymbolsProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, DecimalFormatSymbolsProvider>();
        }
        
        @Override
        public void addImpl(final DecimalFormatSymbolsProvider decimalFormatSymbolsProvider) {
            final Locale[] availableLocales = decimalFormatSymbolsProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], decimalFormatSymbolsProvider);
            }
        }
        
        @Override
        public DecimalFormatSymbolsProvider getImpl(final Locale locale) {
            return (DecimalFormatSymbolsProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public DecimalFormatSymbols getInstance(final Locale locale) {
            final DecimalFormatSymbolsProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getInstance(locale);
        }
    }
    
    static class NumberFormatProviderDelegate extends NumberFormatProvider implements Delegate<NumberFormatProvider>
    {
        private ConcurrentMap<Locale, NumberFormatProvider> map;
        
        NumberFormatProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, NumberFormatProvider>();
        }
        
        @Override
        public void addImpl(final NumberFormatProvider numberFormatProvider) {
            final Locale[] availableLocales = numberFormatProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], numberFormatProvider);
            }
        }
        
        @Override
        public NumberFormatProvider getImpl(final Locale locale) {
            return (NumberFormatProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public NumberFormat getCurrencyInstance(final Locale locale) {
            final NumberFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getCurrencyInstance(locale);
        }
        
        @Override
        public NumberFormat getIntegerInstance(final Locale locale) {
            final NumberFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getIntegerInstance(locale);
        }
        
        @Override
        public NumberFormat getNumberInstance(final Locale locale) {
            final NumberFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getNumberInstance(locale);
        }
        
        @Override
        public NumberFormat getPercentInstance(final Locale locale) {
            final NumberFormatProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getPercentInstance(locale);
        }
    }
    
    static class CalendarDataProviderDelegate extends CalendarDataProvider implements Delegate<CalendarDataProvider>
    {
        private ConcurrentMap<Locale, CalendarDataProvider> map;
        
        CalendarDataProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, CalendarDataProvider>();
        }
        
        @Override
        public void addImpl(final CalendarDataProvider calendarDataProvider) {
            final Locale[] availableLocales = calendarDataProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], calendarDataProvider);
            }
        }
        
        @Override
        public CalendarDataProvider getImpl(final Locale locale) {
            return (CalendarDataProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public int getFirstDayOfWeek(final Locale locale) {
            final CalendarDataProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getFirstDayOfWeek(locale);
        }
        
        @Override
        public int getMinimalDaysInFirstWeek(final Locale locale) {
            final CalendarDataProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getMinimalDaysInFirstWeek(locale);
        }
    }
    
    static class CalendarNameProviderDelegate extends CalendarNameProvider implements Delegate<CalendarNameProvider>
    {
        private ConcurrentMap<Locale, CalendarNameProvider> map;
        
        CalendarNameProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, CalendarNameProvider>();
        }
        
        @Override
        public void addImpl(final CalendarNameProvider calendarNameProvider) {
            final Locale[] availableLocales = calendarNameProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], calendarNameProvider);
            }
        }
        
        @Override
        public CalendarNameProvider getImpl(final Locale locale) {
            return (CalendarNameProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public String getDisplayName(final String s, final int n, final int n2, final int n3, final Locale locale) {
            final CalendarNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayName(s, n, n2, n3, locale);
        }
        
        @Override
        public Map<String, Integer> getDisplayNames(final String s, final int n, final int n2, final Locale locale) {
            final CalendarNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayNames(s, n, n2, locale);
        }
    }
    
    static class CurrencyNameProviderDelegate extends CurrencyNameProvider implements Delegate<CurrencyNameProvider>
    {
        private ConcurrentMap<Locale, CurrencyNameProvider> map;
        
        CurrencyNameProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, CurrencyNameProvider>();
        }
        
        @Override
        public void addImpl(final CurrencyNameProvider currencyNameProvider) {
            final Locale[] availableLocales = currencyNameProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], currencyNameProvider);
            }
        }
        
        @Override
        public CurrencyNameProvider getImpl(final Locale locale) {
            return (CurrencyNameProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public String getSymbol(final String s, final Locale locale) {
            final CurrencyNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getSymbol(s, locale);
        }
        
        @Override
        public String getDisplayName(final String s, final Locale locale) {
            final CurrencyNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayName(s, locale);
        }
    }
    
    static class LocaleNameProviderDelegate extends LocaleNameProvider implements Delegate<LocaleNameProvider>
    {
        private ConcurrentMap<Locale, LocaleNameProvider> map;
        
        LocaleNameProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, LocaleNameProvider>();
        }
        
        @Override
        public void addImpl(final LocaleNameProvider localeNameProvider) {
            final Locale[] availableLocales = localeNameProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], localeNameProvider);
            }
        }
        
        @Override
        public LocaleNameProvider getImpl(final Locale locale) {
            return (LocaleNameProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public String getDisplayLanguage(final String s, final Locale locale) {
            final LocaleNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayLanguage(s, locale);
        }
        
        @Override
        public String getDisplayScript(final String s, final Locale locale) {
            final LocaleNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayScript(s, locale);
        }
        
        @Override
        public String getDisplayCountry(final String s, final Locale locale) {
            final LocaleNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayCountry(s, locale);
        }
        
        @Override
        public String getDisplayVariant(final String s, final Locale locale) {
            final LocaleNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayVariant(s, locale);
        }
    }
    
    static class TimeZoneNameProviderDelegate extends TimeZoneNameProvider implements Delegate<TimeZoneNameProvider>
    {
        private ConcurrentMap<Locale, TimeZoneNameProvider> map;
        
        TimeZoneNameProviderDelegate() {
            this.map = new ConcurrentHashMap<Locale, TimeZoneNameProvider>();
        }
        
        @Override
        public void addImpl(final TimeZoneNameProvider timeZoneNameProvider) {
            final Locale[] availableLocales = timeZoneNameProvider.getAvailableLocales();
            for (int length = availableLocales.length, i = 0; i < length; ++i) {
                this.map.putIfAbsent(availableLocales[i], timeZoneNameProvider);
            }
        }
        
        @Override
        public TimeZoneNameProvider getImpl(final Locale locale) {
            return (TimeZoneNameProvider)getImpl((Map<Locale, LocaleServiceProvider>)this.map, locale);
        }
        
        @Override
        public Locale[] getAvailableLocales() {
            return this.map.keySet().toArray(new Locale[0]);
        }
        
        @Override
        public boolean isSupportedLocale(final Locale locale) {
            return this.map.containsKey(locale);
        }
        
        @Override
        public String getDisplayName(final String s, final boolean b, final int n, final Locale locale) {
            final TimeZoneNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getDisplayName(s, b, n, locale);
        }
        
        @Override
        public String getGenericDisplayName(final String s, final int n, final Locale locale) {
            final TimeZoneNameProvider impl = this.getImpl(locale);
            assert impl != null;
            return impl.getGenericDisplayName(s, n, locale);
        }
    }
    
    interface Delegate<P extends LocaleServiceProvider>
    {
        void addImpl(final P p0);
        
        P getImpl(final Locale p0);
    }
}

package sun.util.locale.provider;

import java.util.Collections;
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
import java.util.IllformedLocaleException;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import sun.util.logging.PlatformLogger;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;
import java.util.concurrent.ConcurrentMap;

public final class LocaleServiceProviderPool
{
    private static ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProviderPool> poolOfPools;
    private ConcurrentMap<LocaleProviderAdapter.Type, LocaleServiceProvider> providers;
    private ConcurrentMap<Locale, List<LocaleProviderAdapter.Type>> providersCache;
    private Set<Locale> availableLocales;
    private Class<? extends LocaleServiceProvider> providerClass;
    static final Class<LocaleServiceProvider>[] spiClasses;
    private static List<LocaleProviderAdapter.Type> NULL_LIST;
    
    public static LocaleServiceProviderPool getPool(final Class<? extends LocaleServiceProvider> clazz) {
        LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.poolOfPools.get(clazz);
        if (localeServiceProviderPool == null) {
            final LocaleServiceProviderPool localeServiceProviderPool2 = new LocaleServiceProviderPool(clazz);
            localeServiceProviderPool = LocaleServiceProviderPool.poolOfPools.putIfAbsent(clazz, localeServiceProviderPool2);
            if (localeServiceProviderPool == null) {
                localeServiceProviderPool = localeServiceProviderPool2;
            }
        }
        return localeServiceProviderPool;
    }
    
    private LocaleServiceProviderPool(final Class<? extends LocaleServiceProvider> providerClass) {
        this.providers = new ConcurrentHashMap<LocaleProviderAdapter.Type, LocaleServiceProvider>();
        this.providersCache = new ConcurrentHashMap<Locale, List<LocaleProviderAdapter.Type>>();
        this.availableLocales = null;
        this.providerClass = providerClass;
        for (final LocaleProviderAdapter.Type type : LocaleProviderAdapter.getAdapterPreference()) {
            final LocaleProviderAdapter forType = LocaleProviderAdapter.forType(type);
            if (forType != null) {
                final LocaleServiceProvider localeServiceProvider = forType.getLocaleServiceProvider(providerClass);
                if (localeServiceProvider == null) {
                    continue;
                }
                this.providers.putIfAbsent(type, localeServiceProvider);
            }
        }
    }
    
    static void config(final Class<?> clazz, final String s) {
        PlatformLogger.getLogger(clazz.getCanonicalName()).config(s);
    }
    
    public static Locale[] getAllAvailableLocales() {
        return AllAvailableLocales.allAvailableLocales.clone();
    }
    
    public Locale[] getAvailableLocales() {
        final HashSet set = new HashSet();
        set.addAll(this.getAvailableLocaleSet());
        set.addAll(Arrays.asList(LocaleProviderAdapter.forJRE().getAvailableLocales()));
        final Locale[] array = new Locale[set.size()];
        set.toArray(array);
        return array;
    }
    
    private synchronized Set<Locale> getAvailableLocaleSet() {
        if (this.availableLocales == null) {
            this.availableLocales = new HashSet<Locale>();
            final Iterator<LocaleServiceProvider> iterator = this.providers.values().iterator();
            while (iterator.hasNext()) {
                final Locale[] availableLocales = iterator.next().getAvailableLocales();
                for (int length = availableLocales.length, i = 0; i < length; ++i) {
                    this.availableLocales.add(getLookupLocale(availableLocales[i]));
                }
            }
        }
        return this.availableLocales;
    }
    
    boolean hasProviders() {
        return this.providers.size() != 1 || (this.providers.get(LocaleProviderAdapter.Type.JRE) == null && this.providers.get(LocaleProviderAdapter.Type.FALLBACK) == null);
    }
    
    public <P extends LocaleServiceProvider, S> S getLocalizedObject(final LocalizedObjectGetter<P, S> localizedObjectGetter, final Locale locale, final Object... array) {
        return this.getLocalizedObjectImpl(localizedObjectGetter, locale, true, null, array);
    }
    
    public <P extends LocaleServiceProvider, S> S getLocalizedObject(final LocalizedObjectGetter<P, S> localizedObjectGetter, final Locale locale, final String s, final Object... array) {
        return this.getLocalizedObjectImpl(localizedObjectGetter, locale, false, s, array);
    }
    
    private <P extends LocaleServiceProvider, S> S getLocalizedObjectImpl(final LocalizedObjectGetter<P, S> localizedObjectGetter, final Locale locale, final boolean b, final String s, final Object... array) {
        if (locale == null) {
            throw new NullPointerException();
        }
        if (!this.hasProviders()) {
            return localizedObjectGetter.getObject((P)this.providers.get(LocaleProviderAdapter.defaultLocaleProviderAdapter), locale, s, array);
        }
        final List<Locale> lookupLocales = getLookupLocales(locale);
        final Set<Locale> availableLocaleSet = this.getAvailableLocaleSet();
        for (final Locale locale2 : lookupLocales) {
            if (availableLocaleSet.contains(locale2)) {
                final Iterator<LocaleProviderAdapter.Type> iterator2 = this.findProviders(locale2).iterator();
                while (iterator2.hasNext()) {
                    final LocaleServiceProvider localeServiceProvider = this.providers.get(iterator2.next());
                    final S object = localizedObjectGetter.getObject((P)localeServiceProvider, locale, s, array);
                    if (object != null) {
                        return object;
                    }
                    if (!b) {
                        continue;
                    }
                    config(LocaleServiceProviderPool.class, "A locale sensitive service provider returned null for a localized objects,  which should not happen.  provider: " + localeServiceProvider + " locale: " + locale);
                }
            }
        }
        return null;
    }
    
    private List<LocaleProviderAdapter.Type> findProviders(final Locale locale) {
        List<?> null_LIST = this.providersCache.get(locale);
        if (null_LIST == null) {
            for (final LocaleProviderAdapter.Type type : LocaleProviderAdapter.getAdapterPreference()) {
                final LocaleServiceProvider localeServiceProvider = this.providers.get(type);
                if (localeServiceProvider != null && localeServiceProvider.isSupportedLocale(locale)) {
                    if (null_LIST == null) {
                        null_LIST = new ArrayList<LocaleProviderAdapter.Type>(2);
                    }
                    null_LIST.add(type);
                }
            }
            if (null_LIST == null) {
                null_LIST = LocaleServiceProviderPool.NULL_LIST;
            }
            final List<LocaleProviderAdapter.Type> list = this.providersCache.putIfAbsent(locale, (List<LocaleProviderAdapter.Type>)null_LIST);
            if (list != null) {
                null_LIST = list;
            }
        }
        return (List<LocaleProviderAdapter.Type>)null_LIST;
    }
    
    static List<Locale> getLookupLocales(final Locale locale) {
        return ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", locale);
    }
    
    static Locale getLookupLocale(final Locale locale) {
        Locale build = locale;
        if (locale.hasExtensions() && !locale.equals(JRELocaleConstants.JA_JP_JP) && !locale.equals(JRELocaleConstants.TH_TH_TH)) {
            final Locale.Builder builder = new Locale.Builder();
            try {
                builder.setLocale(locale);
                builder.clearExtensions();
                build = builder.build();
            }
            catch (final IllformedLocaleException ex) {
                config(LocaleServiceProviderPool.class, "A locale(" + locale + ") has non-empty extensions, but has illformed fields.");
                build = new Locale(locale.getLanguage(), locale.getCountry(), locale.getVariant());
            }
        }
        return build;
    }
    
    static {
        LocaleServiceProviderPool.poolOfPools = new ConcurrentHashMap<Class<? extends LocaleServiceProvider>, LocaleServiceProviderPool>();
        spiClasses = new Class[] { BreakIteratorProvider.class, CollatorProvider.class, DateFormatProvider.class, DateFormatSymbolsProvider.class, DecimalFormatSymbolsProvider.class, NumberFormatProvider.class, CurrencyNameProvider.class, LocaleNameProvider.class, TimeZoneNameProvider.class, CalendarDataProvider.class };
        LocaleServiceProviderPool.NULL_LIST = Collections.emptyList();
    }
    
    private static class AllAvailableLocales
    {
        static final Locale[] allAvailableLocales;
        
        static {
            final HashSet set = new HashSet();
            final Class<LocaleServiceProvider>[] spiClasses = LocaleServiceProviderPool.spiClasses;
            for (int length = spiClasses.length, i = 0; i < length; ++i) {
                set.addAll(LocaleServiceProviderPool.getPool(spiClasses[i]).getAvailableLocaleSet());
            }
            allAvailableLocales = (Locale[])set.toArray(new Locale[0]);
        }
    }
    
    public interface LocalizedObjectGetter<P extends LocaleServiceProvider, S>
    {
        S getObject(final P p0, final Locale p1, final String p2, final Object... p3);
    }
}

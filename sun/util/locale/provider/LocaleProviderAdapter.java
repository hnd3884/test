package sun.util.locale.provider;

import java.util.Collections;
import sun.util.cldr.CLDRLocaleProviderAdapter;
import java.util.ArrayList;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
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
import java.util.Set;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;
import java.util.concurrent.ConcurrentMap;
import java.util.List;

public abstract class LocaleProviderAdapter
{
    private static final List<Type> adapterPreference;
    private static LocaleProviderAdapter jreLocaleProviderAdapter;
    private static LocaleProviderAdapter spiLocaleProviderAdapter;
    private static LocaleProviderAdapter cldrLocaleProviderAdapter;
    private static LocaleProviderAdapter hostLocaleProviderAdapter;
    private static LocaleProviderAdapter fallbackLocaleProviderAdapter;
    static Type defaultLocaleProviderAdapter;
    private static ConcurrentMap<Class<? extends LocaleServiceProvider>, ConcurrentMap<Locale, LocaleProviderAdapter>> adapterCache;
    
    public static LocaleProviderAdapter forType(final Type type) {
        switch (type) {
            case JRE: {
                return LocaleProviderAdapter.jreLocaleProviderAdapter;
            }
            case CLDR: {
                return LocaleProviderAdapter.cldrLocaleProviderAdapter;
            }
            case SPI: {
                return LocaleProviderAdapter.spiLocaleProviderAdapter;
            }
            case HOST: {
                return LocaleProviderAdapter.hostLocaleProviderAdapter;
            }
            case FALLBACK: {
                return LocaleProviderAdapter.fallbackLocaleProviderAdapter;
            }
            default: {
                throw new InternalError("unknown locale data adapter type");
            }
        }
    }
    
    public static LocaleProviderAdapter forJRE() {
        return LocaleProviderAdapter.jreLocaleProviderAdapter;
    }
    
    public static LocaleProviderAdapter getResourceBundleBased() {
        for (final Type type : getAdapterPreference()) {
            if (type == Type.JRE || type == Type.CLDR || type == Type.FALLBACK) {
                return forType(type);
            }
        }
        throw new InternalError();
    }
    
    public static List<Type> getAdapterPreference() {
        return LocaleProviderAdapter.adapterPreference;
    }
    
    public static LocaleProviderAdapter getAdapter(final Class<? extends LocaleServiceProvider> clazz, final Locale locale) {
        ConcurrentMap concurrentMap = LocaleProviderAdapter.adapterCache.get(clazz);
        if (concurrentMap != null) {
            final LocaleProviderAdapter localeProviderAdapter;
            if ((localeProviderAdapter = (LocaleProviderAdapter)concurrentMap.get(locale)) != null) {
                return localeProviderAdapter;
            }
        }
        else {
            concurrentMap = new ConcurrentHashMap();
            LocaleProviderAdapter.adapterCache.putIfAbsent(clazz, concurrentMap);
        }
        final LocaleProviderAdapter adapter = findAdapter(clazz, locale);
        if (adapter != null) {
            concurrentMap.putIfAbsent(locale, adapter);
            return adapter;
        }
        for (final Locale locale2 : ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_DEFAULT).getCandidateLocales("", locale)) {
            if (locale2.equals(locale)) {
                continue;
            }
            final LocaleProviderAdapter adapter2 = findAdapter(clazz, locale2);
            if (adapter2 != null) {
                concurrentMap.putIfAbsent(locale, adapter2);
                return adapter2;
            }
        }
        concurrentMap.putIfAbsent(locale, LocaleProviderAdapter.fallbackLocaleProviderAdapter);
        return LocaleProviderAdapter.fallbackLocaleProviderAdapter;
    }
    
    private static LocaleProviderAdapter findAdapter(final Class<? extends LocaleServiceProvider> clazz, final Locale locale) {
        final Iterator<Type> iterator = getAdapterPreference().iterator();
        while (iterator.hasNext()) {
            final LocaleProviderAdapter forType = forType(iterator.next());
            final LocaleServiceProvider localeServiceProvider = forType.getLocaleServiceProvider(clazz);
            if (localeServiceProvider != null && localeServiceProvider.isSupportedLocale(locale)) {
                return forType;
            }
        }
        return null;
    }
    
    public static boolean isSupportedLocale(Locale stripExtensions, final Type type, final Set<String> set) {
        assert type == Type.FALLBACK;
        if (Locale.ROOT.equals(stripExtensions)) {
            return true;
        }
        if (type == Type.FALLBACK) {
            return false;
        }
        stripExtensions = stripExtensions.stripExtensions();
        if (set.contains(stripExtensions.toLanguageTag())) {
            return true;
        }
        if (type == Type.JRE) {
            final String replace = stripExtensions.toString().replace('_', '-');
            return set.contains(replace) || "ja-JP-JP".equals(replace) || "th-TH-TH".equals(replace) || "no-NO-NY".equals(replace);
        }
        return false;
    }
    
    public static Locale[] toLocaleArray(final Set<String> set) {
        final Locale[] array = new Locale[set.size() + 1];
        int n = 0;
        array[n++] = Locale.ROOT;
        for (final String s2 : set) {
            final String s = s2;
            switch (s2) {
                case "ja-JP-JP": {
                    array[n++] = JRELocaleConstants.JA_JP_JP;
                    continue;
                }
                case "th-TH-TH": {
                    array[n++] = JRELocaleConstants.TH_TH_TH;
                    continue;
                }
                default: {
                    array[n++] = Locale.forLanguageTag(s);
                    continue;
                }
            }
        }
        return array;
    }
    
    public abstract Type getAdapterType();
    
    public abstract <P extends LocaleServiceProvider> P getLocaleServiceProvider(final Class<P> p0);
    
    public abstract BreakIteratorProvider getBreakIteratorProvider();
    
    public abstract CollatorProvider getCollatorProvider();
    
    public abstract DateFormatProvider getDateFormatProvider();
    
    public abstract DateFormatSymbolsProvider getDateFormatSymbolsProvider();
    
    public abstract DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider();
    
    public abstract NumberFormatProvider getNumberFormatProvider();
    
    public abstract CurrencyNameProvider getCurrencyNameProvider();
    
    public abstract LocaleNameProvider getLocaleNameProvider();
    
    public abstract TimeZoneNameProvider getTimeZoneNameProvider();
    
    public abstract CalendarDataProvider getCalendarDataProvider();
    
    public abstract CalendarNameProvider getCalendarNameProvider();
    
    public abstract CalendarProvider getCalendarProvider();
    
    public abstract LocaleResources getLocaleResources(final Locale p0);
    
    public abstract Locale[] getAvailableLocales();
    
    static {
        LocaleProviderAdapter.jreLocaleProviderAdapter = new JRELocaleProviderAdapter();
        LocaleProviderAdapter.spiLocaleProviderAdapter = new SPILocaleProviderAdapter();
        LocaleProviderAdapter.cldrLocaleProviderAdapter = null;
        LocaleProviderAdapter.hostLocaleProviderAdapter = null;
        LocaleProviderAdapter.fallbackLocaleProviderAdapter = null;
        LocaleProviderAdapter.defaultLocaleProviderAdapter = null;
        LocaleProviderAdapter.adapterCache = new ConcurrentHashMap<Class<? extends LocaleServiceProvider>, ConcurrentMap<Locale, LocaleProviderAdapter>>();
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.locale.providers"));
        final ArrayList list = new ArrayList();
        if (s != null && s.length() != 0) {
            for (final String s2 : s.split(",")) {
                try {
                    final Type value = Type.valueOf(s2.trim().toUpperCase(Locale.ROOT));
                    switch (value) {
                        case CLDR: {
                            if (LocaleProviderAdapter.cldrLocaleProviderAdapter == null) {
                                LocaleProviderAdapter.cldrLocaleProviderAdapter = new CLDRLocaleProviderAdapter();
                                break;
                            }
                            break;
                        }
                        case HOST: {
                            if (LocaleProviderAdapter.hostLocaleProviderAdapter == null) {
                                LocaleProviderAdapter.hostLocaleProviderAdapter = new HostLocaleProviderAdapter();
                                break;
                            }
                            break;
                        }
                    }
                    if (!list.contains(value)) {
                        list.add(value);
                    }
                }
                catch (final IllegalArgumentException | UnsupportedOperationException ex) {
                    LocaleServiceProviderPool.config(LocaleProviderAdapter.class, ((Throwable)ex).toString());
                }
            }
        }
        if (!list.isEmpty()) {
            if (!list.contains(Type.JRE)) {
                LocaleProviderAdapter.fallbackLocaleProviderAdapter = new FallbackLocaleProviderAdapter();
                list.add(Type.FALLBACK);
                LocaleProviderAdapter.defaultLocaleProviderAdapter = Type.FALLBACK;
            }
            else {
                LocaleProviderAdapter.defaultLocaleProviderAdapter = Type.JRE;
            }
        }
        else {
            list.add(Type.JRE);
            list.add(Type.SPI);
            LocaleProviderAdapter.defaultLocaleProviderAdapter = Type.JRE;
        }
        adapterPreference = Collections.unmodifiableList((List<?>)list);
    }
    
    public enum Type
    {
        JRE("sun.util.resources", "sun.text.resources"), 
        CLDR("sun.util.resources.cldr", "sun.text.resources.cldr"), 
        SPI, 
        HOST, 
        FALLBACK("sun.util.resources", "sun.text.resources");
        
        private final String UTIL_RESOURCES_PACKAGE;
        private final String TEXT_RESOURCES_PACKAGE;
        
        private Type() {
            this(null, null);
        }
        
        private Type(final String util_RESOURCES_PACKAGE, final String text_RESOURCES_PACKAGE) {
            this.UTIL_RESOURCES_PACKAGE = util_RESOURCES_PACKAGE;
            this.TEXT_RESOURCES_PACKAGE = text_RESOURCES_PACKAGE;
        }
        
        public String getUtilResourcesPackage() {
            return this.UTIL_RESOURCES_PACKAGE;
        }
        
        public String getTextResourcesPackage() {
            return this.TEXT_RESOURCES_PACKAGE;
        }
    }
}

package sun.util.locale.provider;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.File;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Collections;
import java.util.spi.LocaleServiceProvider;
import java.util.concurrent.ConcurrentHashMap;
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
import sun.util.resources.LocaleData;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class JRELocaleProviderAdapter extends LocaleProviderAdapter implements ResourceBundleBasedAdapter
{
    private static final String LOCALE_DATA_JAR_NAME = "localedata.jar";
    private final ConcurrentMap<String, Set<String>> langtagSets;
    private final ConcurrentMap<Locale, LocaleResources> localeResourcesMap;
    private volatile LocaleData localeData;
    private volatile BreakIteratorProvider breakIteratorProvider;
    private volatile CollatorProvider collatorProvider;
    private volatile DateFormatProvider dateFormatProvider;
    private volatile DateFormatSymbolsProvider dateFormatSymbolsProvider;
    private volatile DecimalFormatSymbolsProvider decimalFormatSymbolsProvider;
    private volatile NumberFormatProvider numberFormatProvider;
    private volatile CurrencyNameProvider currencyNameProvider;
    private volatile LocaleNameProvider localeNameProvider;
    private volatile TimeZoneNameProvider timeZoneNameProvider;
    private volatile CalendarDataProvider calendarDataProvider;
    private volatile CalendarNameProvider calendarNameProvider;
    private volatile CalendarProvider calendarProvider;
    private static volatile Boolean isNonENSupported;
    
    public JRELocaleProviderAdapter() {
        this.langtagSets = new ConcurrentHashMap<String, Set<String>>();
        this.localeResourcesMap = new ConcurrentHashMap<Locale, LocaleResources>();
        this.breakIteratorProvider = null;
        this.collatorProvider = null;
        this.dateFormatProvider = null;
        this.dateFormatSymbolsProvider = null;
        this.decimalFormatSymbolsProvider = null;
        this.numberFormatProvider = null;
        this.currencyNameProvider = null;
        this.localeNameProvider = null;
        this.timeZoneNameProvider = null;
        this.calendarDataProvider = null;
        this.calendarNameProvider = null;
        this.calendarProvider = null;
    }
    
    @Override
    public Type getAdapterType() {
        return Type.JRE;
    }
    
    @Override
    public <P extends LocaleServiceProvider> P getLocaleServiceProvider(final Class<P> clazz) {
        final String simpleName = clazz.getSimpleName();
        switch (simpleName) {
            case "BreakIteratorProvider": {
                return (P)this.getBreakIteratorProvider();
            }
            case "CollatorProvider": {
                return (P)this.getCollatorProvider();
            }
            case "DateFormatProvider": {
                return (P)this.getDateFormatProvider();
            }
            case "DateFormatSymbolsProvider": {
                return (P)this.getDateFormatSymbolsProvider();
            }
            case "DecimalFormatSymbolsProvider": {
                return (P)this.getDecimalFormatSymbolsProvider();
            }
            case "NumberFormatProvider": {
                return (P)this.getNumberFormatProvider();
            }
            case "CurrencyNameProvider": {
                return (P)this.getCurrencyNameProvider();
            }
            case "LocaleNameProvider": {
                return (P)this.getLocaleNameProvider();
            }
            case "TimeZoneNameProvider": {
                return (P)this.getTimeZoneNameProvider();
            }
            case "CalendarDataProvider": {
                return (P)this.getCalendarDataProvider();
            }
            case "CalendarNameProvider": {
                return (P)this.getCalendarNameProvider();
            }
            case "CalendarProvider": {
                return (P)this.getCalendarProvider();
            }
            default: {
                throw new InternalError("should not come down here");
            }
        }
    }
    
    @Override
    public BreakIteratorProvider getBreakIteratorProvider() {
        if (this.breakIteratorProvider == null) {
            final BreakIteratorProviderImpl breakIteratorProvider = new BreakIteratorProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
            synchronized (this) {
                if (this.breakIteratorProvider == null) {
                    this.breakIteratorProvider = breakIteratorProvider;
                }
            }
        }
        return this.breakIteratorProvider;
    }
    
    @Override
    public CollatorProvider getCollatorProvider() {
        if (this.collatorProvider == null) {
            final CollatorProviderImpl collatorProvider = new CollatorProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CollationData"));
            synchronized (this) {
                if (this.collatorProvider == null) {
                    this.collatorProvider = collatorProvider;
                }
            }
        }
        return this.collatorProvider;
    }
    
    @Override
    public DateFormatProvider getDateFormatProvider() {
        if (this.dateFormatProvider == null) {
            final DateFormatProviderImpl dateFormatProvider = new DateFormatProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
            synchronized (this) {
                if (this.dateFormatProvider == null) {
                    this.dateFormatProvider = dateFormatProvider;
                }
            }
        }
        return this.dateFormatProvider;
    }
    
    @Override
    public DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
        if (this.dateFormatSymbolsProvider == null) {
            final DateFormatSymbolsProviderImpl dateFormatSymbolsProvider = new DateFormatSymbolsProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
            synchronized (this) {
                if (this.dateFormatSymbolsProvider == null) {
                    this.dateFormatSymbolsProvider = dateFormatSymbolsProvider;
                }
            }
        }
        return this.dateFormatSymbolsProvider;
    }
    
    @Override
    public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
        if (this.decimalFormatSymbolsProvider == null) {
            final DecimalFormatSymbolsProviderImpl decimalFormatSymbolsProvider = new DecimalFormatSymbolsProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
            synchronized (this) {
                if (this.decimalFormatSymbolsProvider == null) {
                    this.decimalFormatSymbolsProvider = decimalFormatSymbolsProvider;
                }
            }
        }
        return this.decimalFormatSymbolsProvider;
    }
    
    @Override
    public NumberFormatProvider getNumberFormatProvider() {
        if (this.numberFormatProvider == null) {
            final NumberFormatProviderImpl numberFormatProvider = new NumberFormatProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
            synchronized (this) {
                if (this.numberFormatProvider == null) {
                    this.numberFormatProvider = numberFormatProvider;
                }
            }
        }
        return this.numberFormatProvider;
    }
    
    @Override
    public CurrencyNameProvider getCurrencyNameProvider() {
        if (this.currencyNameProvider == null) {
            final CurrencyNameProviderImpl currencyNameProvider = new CurrencyNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CurrencyNames"));
            synchronized (this) {
                if (this.currencyNameProvider == null) {
                    this.currencyNameProvider = currencyNameProvider;
                }
            }
        }
        return this.currencyNameProvider;
    }
    
    @Override
    public LocaleNameProvider getLocaleNameProvider() {
        if (this.localeNameProvider == null) {
            final LocaleNameProviderImpl localeNameProvider = new LocaleNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("LocaleNames"));
            synchronized (this) {
                if (this.localeNameProvider == null) {
                    this.localeNameProvider = localeNameProvider;
                }
            }
        }
        return this.localeNameProvider;
    }
    
    @Override
    public TimeZoneNameProvider getTimeZoneNameProvider() {
        if (this.timeZoneNameProvider == null) {
            final TimeZoneNameProviderImpl timeZoneNameProvider = new TimeZoneNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("TimeZoneNames"));
            synchronized (this) {
                if (this.timeZoneNameProvider == null) {
                    this.timeZoneNameProvider = timeZoneNameProvider;
                }
            }
        }
        return this.timeZoneNameProvider;
    }
    
    @Override
    public CalendarDataProvider getCalendarDataProvider() {
        if (this.calendarDataProvider == null) {
            final CalendarDataProviderImpl calendarDataProvider = new CalendarDataProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CalendarData"));
            synchronized (this) {
                if (this.calendarDataProvider == null) {
                    this.calendarDataProvider = calendarDataProvider;
                }
            }
        }
        return this.calendarDataProvider;
    }
    
    @Override
    public CalendarNameProvider getCalendarNameProvider() {
        if (this.calendarNameProvider == null) {
            final CalendarNameProviderImpl calendarNameProvider = new CalendarNameProviderImpl(this.getAdapterType(), this.getLanguageTagSet("FormatData"));
            synchronized (this) {
                if (this.calendarNameProvider == null) {
                    this.calendarNameProvider = calendarNameProvider;
                }
            }
        }
        return this.calendarNameProvider;
    }
    
    @Override
    public CalendarProvider getCalendarProvider() {
        if (this.calendarProvider == null) {
            final CalendarProviderImpl calendarProvider = new CalendarProviderImpl(this.getAdapterType(), this.getLanguageTagSet("CalendarData"));
            synchronized (this) {
                if (this.calendarProvider == null) {
                    this.calendarProvider = calendarProvider;
                }
            }
        }
        return this.calendarProvider;
    }
    
    @Override
    public LocaleResources getLocaleResources(final Locale locale) {
        LocaleResources localeResources = this.localeResourcesMap.get(locale);
        if (localeResources == null) {
            localeResources = new LocaleResources(this, locale);
            final LocaleResources localeResources2 = this.localeResourcesMap.putIfAbsent(locale, localeResources);
            if (localeResources2 != null) {
                localeResources = localeResources2;
            }
        }
        return localeResources;
    }
    
    @Override
    public LocaleData getLocaleData() {
        if (this.localeData == null) {
            synchronized (this) {
                if (this.localeData == null) {
                    this.localeData = new LocaleData(this.getAdapterType());
                }
            }
        }
        return this.localeData;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return AvailableJRELocales.localeList.clone();
    }
    
    public Set<String> getLanguageTagSet(final String s) {
        Set<String> languageTagSet = this.langtagSets.get(s);
        if (languageTagSet == null) {
            languageTagSet = this.createLanguageTagSet(s);
            final Set<String> set = this.langtagSets.putIfAbsent(s, languageTagSet);
            if (set != null) {
                languageTagSet = set;
            }
        }
        return languageTagSet;
    }
    
    protected Set<String> createLanguageTagSet(final String s) {
        final String supportedLocaleString = LocaleDataMetaInfo.getSupportedLocaleString(s);
        if (supportedLocaleString == null) {
            return Collections.emptySet();
        }
        final HashSet set = new HashSet();
        final StringTokenizer stringTokenizer = new StringTokenizer(supportedLocaleString);
        while (stringTokenizer.hasMoreTokens()) {
            final String nextToken = stringTokenizer.nextToken();
            if (nextToken.equals("|")) {
                if (isNonENLangSupported()) {
                    continue;
                }
                break;
            }
            else {
                set.add(nextToken);
            }
        }
        return set;
    }
    
    private static Locale[] createAvailableLocales() {
        final String supportedLocaleString = LocaleDataMetaInfo.getSupportedLocaleString("AvailableLocales");
        if (supportedLocaleString.length() == 0) {
            throw new InternalError("No available locales for JRE");
        }
        final int index = supportedLocaleString.indexOf(124);
        StringTokenizer stringTokenizer;
        if (isNonENLangSupported()) {
            stringTokenizer = new StringTokenizer(supportedLocaleString.substring(0, index) + supportedLocaleString.substring(index + 1));
        }
        else {
            stringTokenizer = new StringTokenizer(supportedLocaleString.substring(0, index));
        }
        final int countTokens = stringTokenizer.countTokens();
        final Locale[] array = new Locale[countTokens + 1];
        array[0] = Locale.ROOT;
        for (int i = 1; i <= countTokens; ++i) {
            final String nextToken;
            final String s = nextToken = stringTokenizer.nextToken();
            switch (nextToken) {
                case "ja-JP-JP": {
                    array[i] = JRELocaleConstants.JA_JP_JP;
                    break;
                }
                case "no-NO-NY": {
                    array[i] = JRELocaleConstants.NO_NO_NY;
                    break;
                }
                case "th-TH-TH": {
                    array[i] = JRELocaleConstants.TH_TH_TH;
                    break;
                }
                default: {
                    array[i] = Locale.forLanguageTag(s);
                    break;
                }
            }
        }
        return array;
    }
    
    private static boolean isNonENLangSupported() {
        if (JRELocaleProviderAdapter.isNonENSupported == null) {
            synchronized (JRELocaleProviderAdapter.class) {
                if (JRELocaleProviderAdapter.isNonENSupported == null) {
                    final String separator = File.separator;
                    JRELocaleProviderAdapter.isNonENSupported = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                        final /* synthetic */ File val$f = new File(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.home")) + separator + "lib" + separator + "ext" + separator + "localedata.jar");
                        
                        @Override
                        public Boolean run() {
                            return this.val$f.exists();
                        }
                    });
                }
            }
        }
        return JRELocaleProviderAdapter.isNonENSupported;
    }
    
    static {
        JRELocaleProviderAdapter.isNonENSupported = null;
    }
    
    private static class AvailableJRELocales
    {
        private static final Locale[] localeList;
        
        static {
            localeList = createAvailableLocales();
        }
    }
}

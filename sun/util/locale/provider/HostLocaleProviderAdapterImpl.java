package sun.util.locale.provider;

import java.util.Collections;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.LocaleNameProvider;
import java.util.Currency;
import java.util.spi.CurrencyNameProvider;
import java.util.Calendar;
import java.util.TimeZone;
import sun.util.spi.CalendarProvider;
import java.util.spi.CalendarDataProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.spi.NumberFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.spi.DateFormatProvider;
import java.util.Set;
import java.text.DecimalFormatSymbols;
import java.text.DateFormatSymbols;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.concurrent.ConcurrentMap;

public class HostLocaleProviderAdapterImpl
{
    private static final int CAT_DISPLAY = 0;
    private static final int CAT_FORMAT = 1;
    private static final int NF_NUMBER = 0;
    private static final int NF_CURRENCY = 1;
    private static final int NF_PERCENT = 2;
    private static final int NF_INTEGER = 3;
    private static final int NF_MAX = 3;
    private static final int CD_FIRSTDAYOFWEEK = 0;
    private static final int CD_MINIMALDAYSINFIRSTWEEK = 1;
    private static final int DN_CURRENCY_NAME = 0;
    private static final int DN_CURRENCY_SYMBOL = 1;
    private static final int DN_LOCALE_LANGUAGE = 2;
    private static final int DN_LOCALE_SCRIPT = 3;
    private static final int DN_LOCALE_REGION = 4;
    private static final int DN_LOCALE_VARIANT = 5;
    private static final String[] calIDToLDML;
    private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> dateFormatCache;
    private static ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> dateFormatSymbolsCache;
    private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> numberFormatCache;
    private static ConcurrentMap<Locale, SoftReference<DecimalFormatSymbols>> decimalFormatSymbolsCache;
    private static final Set<Locale> supportedLocaleSet;
    private static final String nativeDisplayLanguage;
    private static final Locale[] supportedLocale;
    
    public static DateFormatProvider getDateFormatProvider() {
        return new DateFormatProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return getSupportedCalendarLocales();
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return isSupportedCalendarLocale(locale);
            }
            
            @Override
            public DateFormat getDateInstance(final int n, final Locale locale) {
                return new SimpleDateFormat(this.getDateTimePatterns(locale).get(n / 2), getCalendarLocale(locale));
            }
            
            @Override
            public DateFormat getTimeInstance(final int n, final Locale locale) {
                return new SimpleDateFormat(this.getDateTimePatterns(locale).get(n / 2 + 2), getCalendarLocale(locale));
            }
            
            @Override
            public DateFormat getDateTimeInstance(final int n, final int n2, final Locale locale) {
                final AtomicReferenceArray<String> dateTimePatterns = this.getDateTimePatterns(locale);
                return new SimpleDateFormat(dateTimePatterns.get(n / 2) + " " + dateTimePatterns.get(n2 / 2 + 2), getCalendarLocale(locale));
            }
            
            private AtomicReferenceArray<String> getDateTimePatterns(final Locale locale) {
                final SoftReference softReference = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatCache.get(locale);
                AtomicReferenceArray atomicReferenceArray;
                if (softReference == null || (atomicReferenceArray = softReference.get()) == null) {
                    final String languageTag = removeExtensions(locale).toLanguageTag();
                    atomicReferenceArray = new AtomicReferenceArray(4);
                    atomicReferenceArray.compareAndSet(0, null, convertDateTimePattern(getDateTimePattern(1, -1, languageTag)));
                    atomicReferenceArray.compareAndSet(1, null, convertDateTimePattern(getDateTimePattern(3, -1, languageTag)));
                    atomicReferenceArray.compareAndSet(2, null, convertDateTimePattern(getDateTimePattern(-1, 1, languageTag)));
                    atomicReferenceArray.compareAndSet(3, null, convertDateTimePattern(getDateTimePattern(-1, 3, languageTag)));
                    HostLocaleProviderAdapterImpl.dateFormatCache.put(locale, new SoftReference(atomicReferenceArray));
                }
                return atomicReferenceArray;
            }
        };
    }
    
    public static DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
        return new DateFormatSymbolsProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return getSupportedCalendarLocales();
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return isSupportedCalendarLocale(locale);
            }
            
            @Override
            public DateFormatSymbols getInstance(final Locale locale) {
                final SoftReference softReference = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.get(locale);
                DateFormatSymbols dateFormatSymbols;
                if (softReference == null || (dateFormatSymbols = softReference.get()) == null) {
                    dateFormatSymbols = new DateFormatSymbols(locale);
                    final String languageTag = removeExtensions(locale).toLanguageTag();
                    dateFormatSymbols.setAmPmStrings(getAmPmStrings(languageTag, dateFormatSymbols.getAmPmStrings()));
                    dateFormatSymbols.setEras(getEras(languageTag, dateFormatSymbols.getEras()));
                    dateFormatSymbols.setMonths(getMonths(languageTag, dateFormatSymbols.getMonths()));
                    dateFormatSymbols.setShortMonths(getShortMonths(languageTag, dateFormatSymbols.getShortMonths()));
                    dateFormatSymbols.setWeekdays(getWeekdays(languageTag, dateFormatSymbols.getWeekdays()));
                    dateFormatSymbols.setShortWeekdays(getShortWeekdays(languageTag, dateFormatSymbols.getShortWeekdays()));
                    HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.put(locale, new SoftReference(dateFormatSymbols));
                }
                return (DateFormatSymbols)dateFormatSymbols.clone();
            }
        };
    }
    
    public static NumberFormatProvider getNumberFormatProvider() {
        return new NumberFormatProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return getSupportedNativeDigitLocales();
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return isSupportedNativeDigitLocale(locale);
            }
            
            @Override
            public NumberFormat getCurrencyInstance(final Locale locale) {
                return new DecimalFormat(this.getNumberPatterns(locale).get(1), DecimalFormatSymbols.getInstance(locale));
            }
            
            @Override
            public NumberFormat getIntegerInstance(final Locale locale) {
                return new DecimalFormat(this.getNumberPatterns(locale).get(3), DecimalFormatSymbols.getInstance(locale));
            }
            
            @Override
            public NumberFormat getNumberInstance(final Locale locale) {
                return new DecimalFormat(this.getNumberPatterns(locale).get(0), DecimalFormatSymbols.getInstance(locale));
            }
            
            @Override
            public NumberFormat getPercentInstance(final Locale locale) {
                return new DecimalFormat(this.getNumberPatterns(locale).get(2), DecimalFormatSymbols.getInstance(locale));
            }
            
            private AtomicReferenceArray<String> getNumberPatterns(final Locale locale) {
                final SoftReference softReference = (SoftReference)HostLocaleProviderAdapterImpl.numberFormatCache.get(locale);
                AtomicReferenceArray atomicReferenceArray;
                if (softReference == null || (atomicReferenceArray = softReference.get()) == null) {
                    final String languageTag = locale.toLanguageTag();
                    atomicReferenceArray = new AtomicReferenceArray(4);
                    for (int i = 0; i <= 3; ++i) {
                        atomicReferenceArray.compareAndSet(i, null, getNumberPattern(i, languageTag));
                    }
                    HostLocaleProviderAdapterImpl.numberFormatCache.put(locale, new SoftReference(atomicReferenceArray));
                }
                return atomicReferenceArray;
            }
        };
    }
    
    public static DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
        return new DecimalFormatSymbolsProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return getSupportedNativeDigitLocales();
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return isSupportedNativeDigitLocale(locale);
            }
            
            @Override
            public DecimalFormatSymbols getInstance(final Locale locale) {
                final SoftReference softReference = (SoftReference)HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.get(locale);
                DecimalFormatSymbols decimalFormatSymbols;
                if (softReference == null || (decimalFormatSymbols = softReference.get()) == null) {
                    decimalFormatSymbols = new DecimalFormatSymbols(getNumberLocale(locale));
                    final String languageTag = removeExtensions(locale).toLanguageTag();
                    decimalFormatSymbols.setInternationalCurrencySymbol(getInternationalCurrencySymbol(languageTag, decimalFormatSymbols.getInternationalCurrencySymbol()));
                    decimalFormatSymbols.setCurrencySymbol(getCurrencySymbol(languageTag, decimalFormatSymbols.getCurrencySymbol()));
                    decimalFormatSymbols.setDecimalSeparator(getDecimalSeparator(languageTag, decimalFormatSymbols.getDecimalSeparator()));
                    decimalFormatSymbols.setGroupingSeparator(getGroupingSeparator(languageTag, decimalFormatSymbols.getGroupingSeparator()));
                    decimalFormatSymbols.setInfinity(getInfinity(languageTag, decimalFormatSymbols.getInfinity()));
                    decimalFormatSymbols.setMinusSign(getMinusSign(languageTag, decimalFormatSymbols.getMinusSign()));
                    decimalFormatSymbols.setMonetaryDecimalSeparator(getMonetaryDecimalSeparator(languageTag, decimalFormatSymbols.getMonetaryDecimalSeparator()));
                    decimalFormatSymbols.setNaN(getNaN(languageTag, decimalFormatSymbols.getNaN()));
                    decimalFormatSymbols.setPercent(getPercent(languageTag, decimalFormatSymbols.getPercent()));
                    decimalFormatSymbols.setPerMill(getPerMill(languageTag, decimalFormatSymbols.getPerMill()));
                    decimalFormatSymbols.setZeroDigit(getZeroDigit(languageTag, decimalFormatSymbols.getZeroDigit()));
                    HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.put(locale, new SoftReference(decimalFormatSymbols));
                }
                return (DecimalFormatSymbols)decimalFormatSymbols.clone();
            }
        };
    }
    
    public static CalendarDataProvider getCalendarDataProvider() {
        return new CalendarDataProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return getSupportedCalendarLocales();
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return isSupportedCalendarLocale(locale);
            }
            
            @Override
            public int getFirstDayOfWeek(final Locale locale) {
                final int access$3100 = getCalendarDataValue(removeExtensions(locale).toLanguageTag(), 0);
                if (access$3100 != -1) {
                    return (access$3100 + 1) % 7 + 1;
                }
                return 0;
            }
            
            @Override
            public int getMinimalDaysInFirstWeek(final Locale locale) {
                return 0;
            }
        };
    }
    
    public static CalendarProvider getCalendarProvider() {
        return new CalendarProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return getSupportedCalendarLocales();
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return isSupportedCalendarLocale(locale);
            }
            
            @Override
            public Calendar getInstance(final TimeZone timeZone, final Locale locale) {
                return new Calendar.Builder().setLocale(getCalendarLocale(locale)).setTimeZone(timeZone).setInstant(System.currentTimeMillis()).build();
            }
        };
    }
    
    public static CurrencyNameProvider getCurrencyNameProvider() {
        return new CurrencyNameProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return HostLocaleProviderAdapterImpl.supportedLocale;
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(locale.stripExtensions()) && locale.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage);
            }
            
            @Override
            public String getSymbol(final String s, final Locale locale) {
                try {
                    if (Currency.getInstance(locale).getCurrencyCode().equals(s)) {
                        return getDisplayString(locale.toLanguageTag(), 1, s);
                    }
                }
                catch (final IllegalArgumentException ex) {}
                return null;
            }
            
            @Override
            public String getDisplayName(final String s, final Locale locale) {
                try {
                    if (Currency.getInstance(locale).getCurrencyCode().equals(s)) {
                        return getDisplayString(locale.toLanguageTag(), 0, s);
                    }
                }
                catch (final IllegalArgumentException ex) {}
                return null;
            }
        };
    }
    
    public static LocaleNameProvider getLocaleNameProvider() {
        return new LocaleNameProvider() {
            @Override
            public Locale[] getAvailableLocales() {
                return HostLocaleProviderAdapterImpl.supportedLocale;
            }
            
            @Override
            public boolean isSupportedLocale(final Locale locale) {
                return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(locale.stripExtensions()) && locale.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage);
            }
            
            @Override
            public String getDisplayLanguage(final String s, final Locale locale) {
                return getDisplayString(locale.toLanguageTag(), 2, s);
            }
            
            @Override
            public String getDisplayCountry(final String s, final Locale locale) {
                return getDisplayString(locale.toLanguageTag(), 4, HostLocaleProviderAdapterImpl.nativeDisplayLanguage + "-" + s);
            }
            
            @Override
            public String getDisplayScript(final String s, final Locale locale) {
                return null;
            }
            
            @Override
            public String getDisplayVariant(final String s, final Locale locale) {
                return null;
            }
        };
    }
    
    private static String convertDateTimePattern(final String s) {
        return s.replaceAll("dddd", "EEEE").replaceAll("ddd", "EEE").replaceAll("tt", "aa").replaceAll("g", "GG");
    }
    
    private static Locale[] getSupportedCalendarLocales() {
        if (HostLocaleProviderAdapterImpl.supportedLocale.length != 0 && HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(Locale.JAPAN) && isJapaneseCalendar()) {
            final Locale[] array = new Locale[HostLocaleProviderAdapterImpl.supportedLocale.length + 1];
            array[0] = JRELocaleConstants.JA_JP_JP;
            System.arraycopy(HostLocaleProviderAdapterImpl.supportedLocale, 0, array, 1, HostLocaleProviderAdapterImpl.supportedLocale.length);
            return array;
        }
        return HostLocaleProviderAdapterImpl.supportedLocale;
    }
    
    private static boolean isSupportedCalendarLocale(final Locale locale) {
        Locale build = locale;
        if (build.hasExtensions() || build.getVariant() != "") {
            build = new Locale.Builder().setLocale(locale).clearExtensions().build();
        }
        if (!HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(build)) {
            return false;
        }
        final int calendarID = getCalendarID(build.toLanguageTag());
        if (calendarID <= 0 || calendarID >= HostLocaleProviderAdapterImpl.calIDToLDML.length) {
            return false;
        }
        final String unicodeLocaleType = locale.getUnicodeLocaleType("ca");
        final String replaceFirst = HostLocaleProviderAdapterImpl.calIDToLDML[calendarID].replaceFirst("_.*", "");
        if (unicodeLocaleType == null) {
            return Calendar.getAvailableCalendarTypes().contains(replaceFirst);
        }
        return unicodeLocaleType.equals(replaceFirst);
    }
    
    private static Locale[] getSupportedNativeDigitLocales() {
        if (HostLocaleProviderAdapterImpl.supportedLocale.length != 0 && HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(JRELocaleConstants.TH_TH) && isNativeDigit("th-TH")) {
            final Locale[] array = new Locale[HostLocaleProviderAdapterImpl.supportedLocale.length + 1];
            array[0] = JRELocaleConstants.TH_TH_TH;
            System.arraycopy(HostLocaleProviderAdapterImpl.supportedLocale, 0, array, 1, HostLocaleProviderAdapterImpl.supportedLocale.length);
            return array;
        }
        return HostLocaleProviderAdapterImpl.supportedLocale;
    }
    
    private static boolean isSupportedNativeDigitLocale(final Locale locale) {
        if (JRELocaleConstants.TH_TH_TH.equals(locale)) {
            return isNativeDigit("th-TH");
        }
        String unicodeLocaleType = null;
        Locale stripExtensions = locale;
        if (locale.hasExtensions()) {
            unicodeLocaleType = locale.getUnicodeLocaleType("nu");
            stripExtensions = locale.stripExtensions();
        }
        if (HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(stripExtensions)) {
            if (unicodeLocaleType == null || unicodeLocaleType.equals("latn")) {
                return true;
            }
            if (locale.getLanguage().equals("th")) {
                return "thai".equals(unicodeLocaleType) && isNativeDigit(locale.toLanguageTag());
            }
        }
        return false;
    }
    
    private static Locale removeExtensions(final Locale locale) {
        return new Locale.Builder().setLocale(locale).clearExtensions().build();
    }
    
    private static boolean isJapaneseCalendar() {
        return getCalendarID("ja-JP") == 3;
    }
    
    private static Locale getCalendarLocale(final Locale locale) {
        final int calendarID = getCalendarID(locale.toLanguageTag());
        if (calendarID > 0 && calendarID < HostLocaleProviderAdapterImpl.calIDToLDML.length) {
            final Locale.Builder builder = new Locale.Builder();
            final String[] split = HostLocaleProviderAdapterImpl.calIDToLDML[calendarID].split("_");
            if (split.length > 1) {
                builder.setLocale(Locale.forLanguageTag(split[1]));
            }
            else {
                builder.setLocale(locale);
            }
            builder.setUnicodeLocaleKeyword("ca", split[0]);
            return builder.build();
        }
        return locale;
    }
    
    private static Locale getNumberLocale(final Locale locale) {
        if (JRELocaleConstants.TH_TH.equals(locale) && isNativeDigit("th-TH")) {
            final Locale.Builder setLocale = new Locale.Builder().setLocale(locale);
            setLocale.setUnicodeLocaleKeyword("nu", "thai");
            return setLocale.build();
        }
        return locale;
    }
    
    private static native boolean initialize();
    
    private static native String getDefaultLocale(final int p0);
    
    private static native String getDateTimePattern(final int p0, final int p1, final String p2);
    
    private static native int getCalendarID(final String p0);
    
    private static native String[] getAmPmStrings(final String p0, final String[] p1);
    
    private static native String[] getEras(final String p0, final String[] p1);
    
    private static native String[] getMonths(final String p0, final String[] p1);
    
    private static native String[] getShortMonths(final String p0, final String[] p1);
    
    private static native String[] getWeekdays(final String p0, final String[] p1);
    
    private static native String[] getShortWeekdays(final String p0, final String[] p1);
    
    private static native String getNumberPattern(final int p0, final String p1);
    
    private static native boolean isNativeDigit(final String p0);
    
    private static native String getCurrencySymbol(final String p0, final String p1);
    
    private static native char getDecimalSeparator(final String p0, final char p1);
    
    private static native char getGroupingSeparator(final String p0, final char p1);
    
    private static native String getInfinity(final String p0, final String p1);
    
    private static native String getInternationalCurrencySymbol(final String p0, final String p1);
    
    private static native char getMinusSign(final String p0, final char p1);
    
    private static native char getMonetaryDecimalSeparator(final String p0, final char p1);
    
    private static native String getNaN(final String p0, final String p1);
    
    private static native char getPercent(final String p0, final char p1);
    
    private static native char getPerMill(final String p0, final char p1);
    
    private static native char getZeroDigit(final String p0, final char p1);
    
    private static native int getCalendarDataValue(final String p0, final int p1);
    
    private static native String getDisplayString(final String p0, final int p1, final String p2);
    
    static {
        calIDToLDML = new String[] { "", "gregory", "gregory_en-US", "japanese", "roc", "", "islamic", "buddhist", "hebrew", "gregory_fr", "gregory_ar", "gregory_en", "gregory_fr" };
        HostLocaleProviderAdapterImpl.dateFormatCache = new ConcurrentHashMap<Locale, SoftReference<AtomicReferenceArray<String>>>();
        HostLocaleProviderAdapterImpl.dateFormatSymbolsCache = new ConcurrentHashMap<Locale, SoftReference<DateFormatSymbols>>();
        HostLocaleProviderAdapterImpl.numberFormatCache = new ConcurrentHashMap<Locale, SoftReference<AtomicReferenceArray<String>>>();
        HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache = new ConcurrentHashMap<Locale, SoftReference<DecimalFormatSymbols>>();
        final HashSet set = new HashSet();
        if (initialize()) {
            final ResourceBundle.Control noFallbackControl = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
            final String defaultLocale = getDefaultLocale(0);
            final Locale forLanguageTag = Locale.forLanguageTag(defaultLocale.replace('_', '-'));
            set.addAll(noFallbackControl.getCandidateLocales("", forLanguageTag));
            nativeDisplayLanguage = forLanguageTag.getLanguage();
            final String defaultLocale2 = getDefaultLocale(1);
            if (!defaultLocale2.equals(defaultLocale)) {
                set.addAll(noFallbackControl.getCandidateLocales("", Locale.forLanguageTag(defaultLocale2.replace('_', '-'))));
            }
        }
        else {
            nativeDisplayLanguage = "";
        }
        supportedLocaleSet = Collections.unmodifiableSet((Set<?>)set);
        supportedLocale = HostLocaleProviderAdapterImpl.supportedLocaleSet.toArray(new Locale[0]);
    }
}

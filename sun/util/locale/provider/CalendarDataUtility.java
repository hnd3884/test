package sun.util.locale.provider;

import java.util.Map;
import java.util.spi.CalendarNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.CalendarDataProvider;
import java.util.Locale;

public class CalendarDataUtility
{
    public static final String FIRST_DAY_OF_WEEK = "firstDayOfWeek";
    public static final String MINIMAL_DAYS_IN_FIRST_WEEK = "minimalDaysInFirstWeek";
    
    private CalendarDataUtility() {
    }
    
    public static int retrieveFirstDayOfWeek(final Locale locale) {
        final Integer n = LocaleServiceProviderPool.getPool(CalendarDataProvider.class).getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, Integer>)CalendarWeekParameterGetter.INSTANCE, locale, "firstDayOfWeek", new Object[0]);
        return (n != null && n >= 1 && n <= 7) ? n : 1;
    }
    
    public static int retrieveMinimalDaysInFirstWeek(final Locale locale) {
        final Integer n = LocaleServiceProviderPool.getPool(CalendarDataProvider.class).getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, Integer>)CalendarWeekParameterGetter.INSTANCE, locale, "minimalDaysInFirstWeek", new Object[0]);
        return (n != null && n >= 1 && n <= 7) ? n : 1;
    }
    
    public static String retrieveFieldValueName(final String s, final int n, final int n2, final int n3, final Locale locale) {
        return LocaleServiceProviderPool.getPool(CalendarNameProvider.class).getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, String>)CalendarFieldValueNameGetter.INSTANCE, locale, normalizeCalendarType(s), n, n2, n3, false);
    }
    
    public static String retrieveJavaTimeFieldValueName(final String s, final int n, final int n2, final int n3, final Locale locale) {
        final LocaleServiceProviderPool pool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
        String s2 = pool.getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, String>)CalendarFieldValueNameGetter.INSTANCE, locale, normalizeCalendarType(s), n, n2, n3, true);
        if (s2 == null) {
            s2 = pool.getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, String>)CalendarFieldValueNameGetter.INSTANCE, locale, normalizeCalendarType(s), n, n2, n3, false);
        }
        return s2;
    }
    
    public static Map<String, Integer> retrieveFieldValueNames(final String s, final int n, final int n2, final Locale locale) {
        return LocaleServiceProviderPool.getPool(CalendarNameProvider.class).getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, Map<String, Integer>>)CalendarFieldValueNamesMapGetter.INSTANCE, locale, normalizeCalendarType(s), n, n2, false);
    }
    
    public static Map<String, Integer> retrieveJavaTimeFieldValueNames(final String s, final int n, final int n2, final Locale locale) {
        final LocaleServiceProviderPool pool = LocaleServiceProviderPool.getPool(CalendarNameProvider.class);
        Map map = pool.getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, Map>)CalendarFieldValueNamesMapGetter.INSTANCE, locale, normalizeCalendarType(s), n, n2, true);
        if (map == null) {
            map = pool.getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, Map>)CalendarFieldValueNamesMapGetter.INSTANCE, locale, normalizeCalendarType(s), n, n2, false);
        }
        return map;
    }
    
    static String normalizeCalendarType(final String s) {
        String s2;
        if (s.equals("gregorian") || s.equals("iso8601")) {
            s2 = "gregory";
        }
        else if (s.startsWith("islamic")) {
            s2 = "islamic";
        }
        else {
            s2 = s;
        }
        return s2;
    }
    
    private static class CalendarFieldValueNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, String>
    {
        private static final CalendarFieldValueNameGetter INSTANCE;
        
        @Override
        public String getObject(final CalendarNameProvider calendarNameProvider, final Locale locale, final String s, final Object... array) {
            assert array.length == 4;
            final int intValue = (int)array[0];
            final int intValue2 = (int)array[1];
            final int intValue3 = (int)array[2];
            if ((boolean)array[3] && calendarNameProvider instanceof CalendarNameProviderImpl) {
                return ((CalendarNameProviderImpl)calendarNameProvider).getJavaTimeDisplayName(s, intValue, intValue2, intValue3, locale);
            }
            return calendarNameProvider.getDisplayName(s, intValue, intValue2, intValue3, locale);
        }
        
        static {
            INSTANCE = new CalendarFieldValueNameGetter();
        }
    }
    
    private static class CalendarFieldValueNamesMapGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarNameProvider, Map<String, Integer>>
    {
        private static final CalendarFieldValueNamesMapGetter INSTANCE;
        
        @Override
        public Map<String, Integer> getObject(final CalendarNameProvider calendarNameProvider, final Locale locale, final String s, final Object... array) {
            assert array.length == 3;
            final int intValue = (int)array[0];
            final int intValue2 = (int)array[1];
            if ((boolean)array[2] && calendarNameProvider instanceof CalendarNameProviderImpl) {
                return ((CalendarNameProviderImpl)calendarNameProvider).getJavaTimeDisplayNames(s, intValue, intValue2, locale);
            }
            return calendarNameProvider.getDisplayNames(s, intValue, intValue2, locale);
        }
        
        static {
            INSTANCE = new CalendarFieldValueNamesMapGetter();
        }
    }
    
    private static class CalendarWeekParameterGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<CalendarDataProvider, Integer>
    {
        private static final CalendarWeekParameterGetter INSTANCE;
        
        @Override
        public Integer getObject(final CalendarDataProvider calendarDataProvider, final Locale locale, final String s, final Object... array) {
            assert array.length == 0;
            int n2 = 0;
            switch (s) {
                case "firstDayOfWeek": {
                    n2 = calendarDataProvider.getFirstDayOfWeek(locale);
                    break;
                }
                case "minimalDaysInFirstWeek": {
                    n2 = calendarDataProvider.getMinimalDaysInFirstWeek(locale);
                    break;
                }
                default: {
                    throw new InternalError("invalid requestID: " + s);
                }
            }
            return (n2 != 0) ? Integer.valueOf(n2) : null;
        }
        
        static {
            INSTANCE = new CalendarWeekParameterGetter();
        }
    }
}

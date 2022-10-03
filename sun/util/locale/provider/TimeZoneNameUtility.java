package sun.util.locale.provider;

import sun.util.calendar.ZoneInfo;
import java.util.Objects;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import java.util.Map;
import java.lang.ref.SoftReference;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public final class TimeZoneNameUtility
{
    private static ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData;
    private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames;
    
    public static String[][] getZoneStrings(final Locale locale) {
        final SoftReference softReference = TimeZoneNameUtility.cachedZoneData.get(locale);
        String[][] loadZoneStrings;
        if (softReference == null || (loadZoneStrings = (String[][])softReference.get()) == null) {
            loadZoneStrings = loadZoneStrings(locale);
            TimeZoneNameUtility.cachedZoneData.put(locale, new SoftReference<String[][]>(loadZoneStrings));
        }
        return loadZoneStrings;
    }
    
    private static String[][] loadZoneStrings(final Locale locale) {
        final TimeZoneNameProvider timeZoneNameProvider = LocaleProviderAdapter.getAdapter(TimeZoneNameProvider.class, locale).getTimeZoneNameProvider();
        if (timeZoneNameProvider instanceof TimeZoneNameProviderImpl) {
            return ((TimeZoneNameProviderImpl)timeZoneNameProvider).getZoneStrings(locale);
        }
        final Set<String> zoneIDs = LocaleProviderAdapter.forJRE().getLocaleResources(locale).getZoneIDs();
        final LinkedList list = new LinkedList();
        final Iterator<String> iterator = zoneIDs.iterator();
        while (iterator.hasNext()) {
            final String[] retrieveDisplayNamesImpl = retrieveDisplayNamesImpl(iterator.next(), locale);
            if (retrieveDisplayNamesImpl != null) {
                list.add(retrieveDisplayNamesImpl);
            }
        }
        return (String[][])list.toArray(new String[list.size()][]);
    }
    
    public static String[] retrieveDisplayNames(final String s, final Locale locale) {
        Objects.requireNonNull(s);
        Objects.requireNonNull(locale);
        return retrieveDisplayNamesImpl(s, locale);
    }
    
    public static String retrieveGenericDisplayName(final String s, final int n, final Locale locale) {
        final String[] retrieveDisplayNamesImpl = retrieveDisplayNamesImpl(s, locale);
        if (Objects.nonNull(retrieveDisplayNamesImpl)) {
            return retrieveDisplayNamesImpl[6 - n];
        }
        return null;
    }
    
    public static String retrieveDisplayName(final String s, final boolean b, final int n, final Locale locale) {
        final String[] retrieveDisplayNamesImpl = retrieveDisplayNamesImpl(s, locale);
        if (Objects.nonNull(retrieveDisplayNamesImpl)) {
            return retrieveDisplayNamesImpl[(b ? 4 : 2) - n];
        }
        return null;
    }
    
    private static String[] retrieveDisplayNamesImpl(final String s, final Locale locale) {
        final LocaleServiceProviderPool pool = LocaleServiceProviderPool.getPool(TimeZoneNameProvider.class);
        Map<?, ?> map = null;
        final SoftReference softReference = TimeZoneNameUtility.cachedDisplayNames.get(s);
        if (Objects.nonNull(softReference)) {
            map = (Map<?, ?>)softReference.get();
            if (Objects.nonNull(map)) {
                final String[] array = map.get(locale);
                if (Objects.nonNull(array)) {
                    return array;
                }
            }
        }
        final String[] array2 = new String[7];
        array2[0] = s;
        for (int i = 1; i <= 6; ++i) {
            array2[i] = pool.getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, String>)TimeZoneNameGetter.INSTANCE, locale, (i < 5) ? ((i < 3) ? "std" : "dst") : "generic", i % 2, s);
        }
        if (Objects.isNull(map)) {
            map = new ConcurrentHashMap<Object, Object>();
        }
        map.put(locale, array2);
        TimeZoneNameUtility.cachedDisplayNames.put(s, new SoftReference<Map<Locale, String[]>>((Map<Locale, String[]>)map));
        return array2;
    }
    
    private TimeZoneNameUtility() {
    }
    
    static {
        TimeZoneNameUtility.cachedZoneData = new ConcurrentHashMap<Locale, SoftReference<String[][]>>();
        cachedDisplayNames = new ConcurrentHashMap<String, SoftReference<Map<Locale, String[]>>>();
    }
    
    private static class TimeZoneNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider, String>
    {
        private static final TimeZoneNameGetter INSTANCE;
        
        @Override
        public String getObject(final TimeZoneNameProvider timeZoneNameProvider, final Locale locale, final String s, final Object... array) {
            assert array.length == 2;
            final int intValue = (int)array[0];
            final String s2 = (String)array[1];
            String s3 = getName(timeZoneNameProvider, locale, s, intValue, s2);
            if (s3 == null) {
                final Map<String, String> aliasTable = ZoneInfo.getAliasTable();
                if (aliasTable != null) {
                    final String s4 = aliasTable.get(s2);
                    if (s4 != null) {
                        s3 = getName(timeZoneNameProvider, locale, s, intValue, s4);
                    }
                    if (s3 == null) {
                        s3 = examineAliases(timeZoneNameProvider, locale, s, (s4 != null) ? s4 : s2, intValue, aliasTable);
                    }
                }
            }
            return s3;
        }
        
        private static String examineAliases(final TimeZoneNameProvider timeZoneNameProvider, final Locale locale, final String s, final String s2, final int n, final Map<String, String> map) {
            for (final Map.Entry entry : map.entrySet()) {
                if (((String)entry.getValue()).equals(s2)) {
                    final String s3 = (String)entry.getKey();
                    final String name = getName(timeZoneNameProvider, locale, s, n, s3);
                    if (name != null) {
                        return name;
                    }
                    final String examineAliases = examineAliases(timeZoneNameProvider, locale, s, s3, n, map);
                    if (examineAliases != null) {
                        return examineAliases;
                    }
                    continue;
                }
            }
            return null;
        }
        
        private static String getName(final TimeZoneNameProvider timeZoneNameProvider, final Locale locale, final String s, final int n, final String s2) {
            String s3 = null;
            switch (s) {
                case "std": {
                    s3 = timeZoneNameProvider.getDisplayName(s2, false, n, locale);
                    break;
                }
                case "dst": {
                    s3 = timeZoneNameProvider.getDisplayName(s2, true, n, locale);
                    break;
                }
                case "generic": {
                    s3 = timeZoneNameProvider.getGenericDisplayName(s2, n, locale);
                    break;
                }
            }
            return s3;
        }
        
        static {
            INSTANCE = new TimeZoneNameGetter();
        }
    }
}

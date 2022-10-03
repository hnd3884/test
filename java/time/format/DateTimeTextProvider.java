package java.time.format;

import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;
import sun.util.locale.provider.LocaleProviderAdapter;
import java.util.AbstractMap;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import sun.util.locale.provider.CalendarDataUtility;
import java.time.chrono.JapaneseChronology;
import java.time.temporal.ChronoField;
import java.time.chrono.IsoChronology;
import java.time.chrono.Chronology;
import java.util.Comparator;
import java.util.Locale;
import java.time.temporal.TemporalField;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

class DateTimeTextProvider
{
    private static final ConcurrentMap<Map.Entry<TemporalField, Locale>, Object> CACHE;
    private static final Comparator<Map.Entry<String, Long>> COMPARATOR;
    
    static DateTimeTextProvider getInstance() {
        return new DateTimeTextProvider();
    }
    
    public String getText(final TemporalField temporalField, final long n, final TextStyle textStyle, final Locale locale) {
        final Object store = this.findStore(temporalField, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore)store).getText(n, textStyle);
        }
        return null;
    }
    
    public String getText(final Chronology chronology, final TemporalField temporalField, final long n, final TextStyle textStyle, final Locale locale) {
        if (chronology == IsoChronology.INSTANCE || !(temporalField instanceof ChronoField)) {
            return this.getText(temporalField, n, textStyle, locale);
        }
        int n2;
        int n3;
        if (temporalField == ChronoField.ERA) {
            n2 = 0;
            if (chronology == JapaneseChronology.INSTANCE) {
                if (n == -999L) {
                    n3 = 0;
                }
                else {
                    n3 = (int)n + 2;
                }
            }
            else {
                n3 = (int)n;
            }
        }
        else if (temporalField == ChronoField.MONTH_OF_YEAR) {
            n2 = 2;
            n3 = (int)n - 1;
        }
        else if (temporalField == ChronoField.DAY_OF_WEEK) {
            n2 = 7;
            n3 = (int)n + 1;
            if (n3 > 7) {
                n3 = 1;
            }
        }
        else {
            if (temporalField != ChronoField.AMPM_OF_DAY) {
                return null;
            }
            n2 = 9;
            n3 = (int)n;
        }
        return CalendarDataUtility.retrieveJavaTimeFieldValueName(chronology.getCalendarType(), n2, n3, textStyle.toCalendarStyle(), locale);
    }
    
    public Iterator<Map.Entry<String, Long>> getTextIterator(final TemporalField temporalField, final TextStyle textStyle, final Locale locale) {
        final Object store = this.findStore(temporalField, locale);
        if (store instanceof LocaleStore) {
            return ((LocaleStore)store).getTextIterator(textStyle);
        }
        return null;
    }
    
    public Iterator<Map.Entry<String, Long>> getTextIterator(final Chronology chronology, final TemporalField temporalField, final TextStyle textStyle, final Locale locale) {
        if (chronology == IsoChronology.INSTANCE || !(temporalField instanceof ChronoField)) {
            return this.getTextIterator(temporalField, textStyle, locale);
        }
        int n = 0;
        switch ((ChronoField)temporalField) {
            case ERA: {
                n = 0;
                break;
            }
            case MONTH_OF_YEAR: {
                n = 2;
                break;
            }
            case DAY_OF_WEEK: {
                n = 7;
                break;
            }
            case AMPM_OF_DAY: {
                n = 9;
                break;
            }
            default: {
                return null;
            }
        }
        final Map<String, Integer> retrieveJavaTimeFieldValueNames = CalendarDataUtility.retrieveJavaTimeFieldValueNames(chronology.getCalendarType(), n, (textStyle == null) ? 0 : textStyle.toCalendarStyle(), locale);
        if (retrieveJavaTimeFieldValueNames == null) {
            return null;
        }
        final ArrayList list = new ArrayList(retrieveJavaTimeFieldValueNames.size());
        switch (n) {
            case 0: {
                for (final Map.Entry entry : retrieveJavaTimeFieldValueNames.entrySet()) {
                    int intValue = (int)entry.getValue();
                    if (chronology == JapaneseChronology.INSTANCE) {
                        if (intValue == 0) {
                            intValue = -999;
                        }
                        else {
                            intValue -= 2;
                        }
                    }
                    list.add((Object)createEntry(entry.getKey(), (long)intValue));
                }
                break;
            }
            case 2: {
                for (final Map.Entry entry2 : retrieveJavaTimeFieldValueNames.entrySet()) {
                    list.add((Object)createEntry(entry2.getKey(), (long)((int)entry2.getValue() + 1)));
                }
                break;
            }
            case 7: {
                for (final Map.Entry entry3 : retrieveJavaTimeFieldValueNames.entrySet()) {
                    list.add((Object)createEntry(entry3.getKey(), (long)toWeekDay((int)entry3.getValue())));
                }
                break;
            }
            default: {
                for (final Map.Entry entry4 : retrieveJavaTimeFieldValueNames.entrySet()) {
                    list.add((Object)createEntry(entry4.getKey(), (long)(int)entry4.getValue()));
                }
                break;
            }
        }
        return list.iterator();
    }
    
    private Object findStore(final TemporalField temporalField, final Locale locale) {
        final Map.Entry<TemporalField, Locale> entry = createEntry(temporalField, locale);
        Object o = DateTimeTextProvider.CACHE.get(entry);
        if (o == null) {
            DateTimeTextProvider.CACHE.putIfAbsent(entry, this.createStore(temporalField, locale));
            o = DateTimeTextProvider.CACHE.get(entry);
        }
        return o;
    }
    
    private static int toWeekDay(final int n) {
        if (n == 1) {
            return 7;
        }
        return n - 1;
    }
    
    private Object createStore(final TemporalField temporalField, final Locale locale) {
        final HashMap hashMap = new HashMap();
        if (temporalField == ChronoField.ERA) {
            for (final TextStyle textStyle : TextStyle.values()) {
                if (!textStyle.isStandalone()) {
                    final Map<String, Integer> retrieveJavaTimeFieldValueNames = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 0, textStyle.toCalendarStyle(), locale);
                    if (retrieveJavaTimeFieldValueNames != null) {
                        final HashMap hashMap2 = new HashMap();
                        for (final Map.Entry entry : retrieveJavaTimeFieldValueNames.entrySet()) {
                            hashMap2.put((long)(int)entry.getValue(), entry.getKey());
                        }
                        if (!hashMap2.isEmpty()) {
                            hashMap.put(textStyle, hashMap2);
                        }
                    }
                }
            }
            return new LocaleStore(hashMap);
        }
        if (temporalField == ChronoField.MONTH_OF_YEAR) {
            for (final TextStyle textStyle2 : TextStyle.values()) {
                final Map<String, Integer> retrieveJavaTimeFieldValueNames2 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 2, textStyle2.toCalendarStyle(), locale);
                final HashMap hashMap3 = new HashMap();
                if (retrieveJavaTimeFieldValueNames2 != null) {
                    for (final Map.Entry entry2 : retrieveJavaTimeFieldValueNames2.entrySet()) {
                        hashMap3.put((long)((int)entry2.getValue() + 1), entry2.getKey());
                    }
                }
                else {
                    for (int k = 0; k <= 11; ++k) {
                        final String retrieveJavaTimeFieldValueName = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 2, k, textStyle2.toCalendarStyle(), locale);
                        if (retrieveJavaTimeFieldValueName == null) {
                            break;
                        }
                        hashMap3.put((long)(k + 1), retrieveJavaTimeFieldValueName);
                    }
                }
                if (!hashMap3.isEmpty()) {
                    hashMap.put(textStyle2, hashMap3);
                }
            }
            return new LocaleStore(hashMap);
        }
        if (temporalField == ChronoField.DAY_OF_WEEK) {
            for (final TextStyle textStyle3 : TextStyle.values()) {
                final Map<String, Integer> retrieveJavaTimeFieldValueNames3 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 7, textStyle3.toCalendarStyle(), locale);
                final HashMap hashMap4 = new HashMap();
                if (retrieveJavaTimeFieldValueNames3 != null) {
                    for (final Map.Entry entry3 : retrieveJavaTimeFieldValueNames3.entrySet()) {
                        hashMap4.put((long)toWeekDay((int)entry3.getValue()), entry3.getKey());
                    }
                }
                else {
                    for (int n = 1; n <= 7; ++n) {
                        final String retrieveJavaTimeFieldValueName2 = CalendarDataUtility.retrieveJavaTimeFieldValueName("gregory", 7, n, textStyle3.toCalendarStyle(), locale);
                        if (retrieveJavaTimeFieldValueName2 == null) {
                            break;
                        }
                        hashMap4.put((long)toWeekDay(n), retrieveJavaTimeFieldValueName2);
                    }
                }
                if (!hashMap4.isEmpty()) {
                    hashMap.put(textStyle3, hashMap4);
                }
            }
            return new LocaleStore(hashMap);
        }
        if (temporalField == ChronoField.AMPM_OF_DAY) {
            for (final TextStyle textStyle4 : TextStyle.values()) {
                if (!textStyle4.isStandalone()) {
                    final Map<String, Integer> retrieveJavaTimeFieldValueNames4 = CalendarDataUtility.retrieveJavaTimeFieldValueNames("gregory", 9, textStyle4.toCalendarStyle(), locale);
                    if (retrieveJavaTimeFieldValueNames4 != null) {
                        final HashMap hashMap5 = new HashMap();
                        for (final Map.Entry entry4 : retrieveJavaTimeFieldValueNames4.entrySet()) {
                            hashMap5.put((long)(int)entry4.getValue(), entry4.getKey());
                        }
                        if (!hashMap5.isEmpty()) {
                            hashMap.put(textStyle4, hashMap5);
                        }
                    }
                }
            }
            return new LocaleStore(hashMap);
        }
        if (temporalField == IsoFields.QUARTER_OF_YEAR) {
            final String[] array = { "QuarterNames", "standalone.QuarterNames", "QuarterAbbreviations", "standalone.QuarterAbbreviations", "QuarterNarrows", "standalone.QuarterNarrows" };
            for (int n3 = 0; n3 < array.length; ++n3) {
                final String[] array2 = getLocalizedResource(array[n3], locale);
                if (array2 != null) {
                    final HashMap hashMap6 = new HashMap();
                    for (int n4 = 0; n4 < array2.length; ++n4) {
                        hashMap6.put((long)(n4 + 1), array2[n4]);
                    }
                    hashMap.put(TextStyle.values()[n3], hashMap6);
                }
            }
            return new LocaleStore(hashMap);
        }
        return "";
    }
    
    private static <A, B> Map.Entry<A, B> createEntry(final A a, final B b) {
        return new AbstractMap.SimpleImmutableEntry<A, B>(a, b);
    }
    
    static <T> T getLocalizedResource(final String s, final Locale locale) {
        final ResourceBundle javaTimeFormatData = LocaleProviderAdapter.getResourceBundleBased().getLocaleResources(locale).getJavaTimeFormatData();
        return (T)(javaTimeFormatData.containsKey(s) ? javaTimeFormatData.getObject(s) : null);
    }
    
    static {
        CACHE = new ConcurrentHashMap<Map.Entry<TemporalField, Locale>, Object>(16, 0.75f, 2);
        COMPARATOR = new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(final Map.Entry<String, Long> entry, final Map.Entry<String, Long> entry2) {
                return entry2.getKey().length() - entry.getKey().length();
            }
        };
    }
    
    static final class LocaleStore
    {
        private final Map<TextStyle, Map<Long, String>> valueTextMap;
        private final Map<TextStyle, List<Map.Entry<String, Long>>> parsable;
        
        LocaleStore(final Map<TextStyle, Map<Long, String>> valueTextMap) {
            this.valueTextMap = valueTextMap;
            final HashMap parsable = new HashMap();
            final ArrayList list = new ArrayList();
            for (final Map.Entry entry : valueTextMap.entrySet()) {
                final HashMap hashMap = new HashMap();
                for (final Map.Entry entry2 : ((Map)entry.getValue()).entrySet()) {
                    if (hashMap.put(entry2.getValue(), createEntry(entry2.getValue(), entry2.getKey())) != null) {
                        continue;
                    }
                }
                final ArrayList list2 = new ArrayList<Object>(hashMap.values());
                Collections.sort((List<E>)list2, DateTimeTextProvider.COMPARATOR);
                parsable.put(entry.getKey(), list2);
                list.addAll(list2);
                parsable.put(null, list);
            }
            Collections.sort((List<Object>)list, DateTimeTextProvider.COMPARATOR);
            this.parsable = parsable;
        }
        
        String getText(final long n, final TextStyle textStyle) {
            final Map map = this.valueTextMap.get(textStyle);
            return (map != null) ? ((String)map.get(n)) : null;
        }
        
        Iterator<Map.Entry<String, Long>> getTextIterator(final TextStyle textStyle) {
            final List list = this.parsable.get(textStyle);
            return (list != null) ? list.iterator() : null;
        }
    }
}

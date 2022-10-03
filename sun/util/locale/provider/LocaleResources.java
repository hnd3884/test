package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import sun.util.resources.ParallelListResourceBundle;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Iterator;
import sun.util.calendar.ZoneInfo;
import java.util.LinkedHashSet;
import java.util.Set;
import sun.util.resources.TimeZoneNamesBundle;
import java.util.Objects;
import sun.util.resources.OpenListResourceBundle;
import java.util.ResourceBundle;
import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentMap;
import sun.util.resources.LocaleData;
import java.util.Locale;

public class LocaleResources
{
    private final Locale locale;
    private final LocaleData localeData;
    private final LocaleProviderAdapter.Type type;
    private ConcurrentMap<String, ResourceReference> cache;
    private ReferenceQueue<Object> referenceQueue;
    private static final String BREAK_ITERATOR_INFO = "BII.";
    private static final String CALENDAR_DATA = "CALD.";
    private static final String COLLATION_DATA_CACHEKEY = "COLD";
    private static final String DECIMAL_FORMAT_SYMBOLS_DATA_CACHEKEY = "DFSD";
    private static final String CURRENCY_NAMES = "CN.";
    private static final String LOCALE_NAMES = "LN.";
    private static final String TIME_ZONE_NAMES = "TZN.";
    private static final String ZONE_IDS_CACHEKEY = "ZID";
    private static final String CALENDAR_NAMES = "CALN.";
    private static final String NUMBER_PATTERNS_CACHEKEY = "NP";
    private static final String DATE_TIME_PATTERN = "DTP.";
    private static final Object NULLOBJECT;
    
    LocaleResources(final ResourceBundleBasedAdapter resourceBundleBasedAdapter, final Locale locale) {
        this.cache = new ConcurrentHashMap<String, ResourceReference>();
        this.referenceQueue = new ReferenceQueue<Object>();
        this.locale = locale;
        this.localeData = resourceBundleBasedAdapter.getLocaleData();
        this.type = ((LocaleProviderAdapter)resourceBundleBasedAdapter).getAdapterType();
    }
    
    private void removeEmptyReferences() {
        Reference<?> poll;
        while ((poll = this.referenceQueue.poll()) != null) {
            this.cache.remove(((ResourceReference)poll).getCacheKey());
        }
    }
    
    Object getBreakIteratorInfo(final String s) {
        final String string = "BII." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        Object o;
        if (resourceReference == null || (o = resourceReference.get()) == null) {
            o = this.localeData.getBreakIteratorInfo(this.locale).getObject(s);
            this.cache.put(string, new ResourceReference(string, o, this.referenceQueue));
        }
        return o;
    }
    
    int getCalendarData(final String s) {
        final String string = "CALD." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        Integer n;
        if (resourceReference == null || (n = ((SoftReference<Integer>)resourceReference).get()) == null) {
            final ResourceBundle calendarData = this.localeData.getCalendarData(this.locale);
            if (calendarData.containsKey(s)) {
                n = Integer.parseInt(calendarData.getString(s));
            }
            else {
                n = 0;
            }
            this.cache.put(string, new ResourceReference(string, n, this.referenceQueue));
        }
        return n;
    }
    
    public String getCollationData() {
        final String s = "Rule";
        String string = "";
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get("COLD");
        if (resourceReference == null || (string = ((SoftReference<String>)resourceReference).get()) == null) {
            final ResourceBundle collationData = this.localeData.getCollationData(this.locale);
            if (collationData.containsKey(s)) {
                string = collationData.getString(s);
            }
            this.cache.put("COLD", new ResourceReference("COLD", string, this.referenceQueue));
        }
        return string;
    }
    
    public Object[] getDecimalFormatSymbolsData() {
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get("DFSD");
        Object[] array;
        if (resourceReference == null || (array = ((SoftReference<Object[]>)resourceReference).get()) == null) {
            final ResourceBundle numberFormatData = this.localeData.getNumberFormatData(this.locale);
            array = new Object[3];
            final String unicodeLocaleType = this.locale.getUnicodeLocaleType("nu");
            if (unicodeLocaleType != null) {
                final String string = unicodeLocaleType + ".NumberElements";
                if (numberFormatData.containsKey(string)) {
                    array[0] = numberFormatData.getStringArray(string);
                }
            }
            if (array[0] == null && numberFormatData.containsKey("DefaultNumberingSystem")) {
                final String string2 = numberFormatData.getString("DefaultNumberingSystem") + ".NumberElements";
                if (numberFormatData.containsKey(string2)) {
                    array[0] = numberFormatData.getStringArray(string2);
                }
            }
            if (array[0] == null) {
                array[0] = numberFormatData.getStringArray("NumberElements");
            }
            this.cache.put("DFSD", new ResourceReference("DFSD", array, this.referenceQueue));
        }
        return array;
    }
    
    public String getCurrencyName(final String s) {
        Object o = null;
        final String string = "CN." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        if (resourceReference != null && (o = ((SoftReference<String>)resourceReference).get()) != null) {
            if (o.equals(LocaleResources.NULLOBJECT)) {
                o = null;
            }
            return (String)o;
        }
        final OpenListResourceBundle currencyNames = this.localeData.getCurrencyNames(this.locale);
        if (currencyNames.containsKey(s)) {
            o = currencyNames.getObject(s);
            this.cache.put(string, new ResourceReference(string, o, this.referenceQueue));
        }
        return (String)o;
    }
    
    public String getLocaleName(final String s) {
        Object o = null;
        final String string = "LN." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        if (resourceReference != null && (o = ((SoftReference<String>)resourceReference).get()) != null) {
            if (o.equals(LocaleResources.NULLOBJECT)) {
                o = null;
            }
            return (String)o;
        }
        final OpenListResourceBundle localeNames = this.localeData.getLocaleNames(this.locale);
        if (localeNames.containsKey(s)) {
            o = localeNames.getObject(s);
            this.cache.put(string, new ResourceReference(string, o, this.referenceQueue));
        }
        return (String)o;
    }
    
    String[] getTimeZoneNames(final String s) {
        Object stringArray = null;
        final String string = "TZN.." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        if (Objects.isNull(resourceReference) || Objects.isNull(stringArray = ((SoftReference<String[]>)resourceReference).get())) {
            final TimeZoneNamesBundle timeZoneNames = this.localeData.getTimeZoneNames(this.locale);
            if (timeZoneNames.containsKey(s)) {
                stringArray = timeZoneNames.getStringArray(s);
                this.cache.put(string, new ResourceReference(string, stringArray, this.referenceQueue));
            }
        }
        return (String[])stringArray;
    }
    
    Set<String> getZoneIDs() {
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get("ZID");
        Set<String> keySet;
        if (resourceReference == null || (keySet = ((SoftReference<Set<String>>)resourceReference).get()) == null) {
            keySet = this.localeData.getTimeZoneNames(this.locale).keySet();
            this.cache.put("ZID", new ResourceReference("ZID", keySet, this.referenceQueue));
        }
        return keySet;
    }
    
    String[][] getZoneStrings() {
        final TimeZoneNamesBundle timeZoneNames = this.localeData.getTimeZoneNames(this.locale);
        final Set<String> zoneIDs = this.getZoneIDs();
        final LinkedHashSet set = new LinkedHashSet();
        final Iterator iterator = zoneIDs.iterator();
        while (iterator.hasNext()) {
            set.add(timeZoneNames.getStringArray((String)iterator.next()));
        }
        if (this.type == LocaleProviderAdapter.Type.CLDR) {
            final Map<String, String> aliasTable = ZoneInfo.getAliasTable();
            for (final String s : aliasTable.keySet()) {
                if (!zoneIDs.contains(s)) {
                    final String s2 = aliasTable.get(s);
                    if (!zoneIDs.contains(s2)) {
                        continue;
                    }
                    final String[] stringArray = timeZoneNames.getStringArray(s2);
                    stringArray[0] = s;
                    set.add(stringArray);
                }
            }
        }
        return (String[][])set.toArray(new String[0][]);
    }
    
    String[] getCalendarNames(final String s) {
        Object stringArray = null;
        final String string = "CALN." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        if (resourceReference == null || (stringArray = ((SoftReference<String[]>)resourceReference).get()) == null) {
            final ResourceBundle dateFormatData = this.localeData.getDateFormatData(this.locale);
            if (dateFormatData.containsKey(s)) {
                stringArray = dateFormatData.getStringArray(s);
                this.cache.put(string, new ResourceReference(string, stringArray, this.referenceQueue));
            }
        }
        return (String[])stringArray;
    }
    
    String[] getJavaTimeNames(final String s) {
        Object stringArray = null;
        final String string = "CALN." + s;
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string);
        if (resourceReference == null || (stringArray = ((SoftReference<String[]>)resourceReference).get()) == null) {
            final ResourceBundle javaTimeFormatData = this.getJavaTimeFormatData();
            if (javaTimeFormatData.containsKey(s)) {
                stringArray = javaTimeFormatData.getStringArray(s);
                this.cache.put(string, new ResourceReference(string, stringArray, this.referenceQueue));
            }
        }
        return (String[])stringArray;
    }
    
    public String getDateTimePattern(final int n, final int n2, Calendar instance) {
        if (instance == null) {
            instance = Calendar.getInstance(this.locale);
        }
        return this.getDateTimePattern(null, n, n2, instance.getCalendarType());
    }
    
    public String getJavaTimeDateTimePattern(final int n, final int n2, String normalizeCalendarType) {
        normalizeCalendarType = CalendarDataUtility.normalizeCalendarType(normalizeCalendarType);
        String s = this.getDateTimePattern("java.time.", n, n2, normalizeCalendarType);
        if (s == null) {
            s = this.getDateTimePattern(null, n, n2, normalizeCalendarType);
        }
        return s;
    }
    
    private String getDateTimePattern(final String s, final int n, final int n2, final String s2) {
        String s3 = null;
        String s4 = null;
        if (n >= 0) {
            if (s != null) {
                s3 = this.getDateTimePattern(s, "TimePatterns", n, s2);
            }
            if (s3 == null) {
                s3 = this.getDateTimePattern(null, "TimePatterns", n, s2);
            }
        }
        if (n2 >= 0) {
            if (s != null) {
                s4 = this.getDateTimePattern(s, "DatePatterns", n2, s2);
            }
            if (s4 == null) {
                s4 = this.getDateTimePattern(null, "DatePatterns", n2, s2);
            }
        }
        String s7;
        if (n >= 0) {
            if (n2 >= 0) {
                String s5 = null;
                if (s != null) {
                    s5 = this.getDateTimePattern(s, "DateTimePatterns", 0, s2);
                }
                if (s5 == null) {
                    s5 = this.getDateTimePattern(null, "DateTimePatterns", 0, s2);
                }
                final String s6 = s5;
                switch (s6) {
                    case "{1} {0}": {
                        s7 = s4 + " " + s3;
                        break;
                    }
                    case "{0} {1}": {
                        s7 = s3 + " " + s4;
                        break;
                    }
                    default: {
                        s7 = MessageFormat.format(s5, s3, s4);
                        break;
                    }
                }
            }
            else {
                s7 = s3;
            }
        }
        else {
            if (n2 < 0) {
                throw new IllegalArgumentException("No date or time style specified");
            }
            s7 = s4;
        }
        return s7;
    }
    
    public String[] getNumberPatterns() {
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get("NP");
        String[] stringArray;
        if (resourceReference == null || (stringArray = ((SoftReference<String[]>)resourceReference).get()) == null) {
            stringArray = this.localeData.getNumberFormatData(this.locale).getStringArray("NumberPatterns");
            this.cache.put("NP", new ResourceReference("NP", stringArray, this.referenceQueue));
        }
        return stringArray;
    }
    
    public ResourceBundle getJavaTimeFormatData() {
        final ResourceBundle dateFormatData = this.localeData.getDateFormatData(this.locale);
        if (dateFormatData instanceof ParallelListResourceBundle) {
            this.localeData.setSupplementary((ParallelListResourceBundle)dateFormatData);
        }
        return dateFormatData;
    }
    
    private String getDateTimePattern(final String s, final String s2, final int n, final String s3) {
        final StringBuilder sb = new StringBuilder();
        if (s != null) {
            sb.append(s);
        }
        if (!"gregory".equals(s3)) {
            sb.append(s3).append('.');
        }
        sb.append(s2);
        final String string = sb.toString();
        final String string2 = sb.insert(0, "DTP.").toString();
        this.removeEmptyReferences();
        final ResourceReference resourceReference = this.cache.get(string2);
        Object o = LocaleResources.NULLOBJECT;
        if (resourceReference == null || (o = resourceReference.get()) == null) {
            final ResourceBundle resourceBundle = (s != null) ? this.getJavaTimeFormatData() : this.localeData.getDateFormatData(this.locale);
            if (resourceBundle.containsKey(string)) {
                o = resourceBundle.getStringArray(string);
            }
            else {
                assert !string.equals(s2);
                if (resourceBundle.containsKey(s2)) {
                    o = resourceBundle.getStringArray(s2);
                }
            }
            this.cache.put(string2, new ResourceReference(string2, o, this.referenceQueue));
        }
        if (o != LocaleResources.NULLOBJECT) {
            return ((String[])o)[n];
        }
        assert s != null;
        return null;
    }
    
    static {
        NULLOBJECT = new Object();
    }
    
    private static class ResourceReference extends SoftReference<Object>
    {
        private final String cacheKey;
        
        ResourceReference(final String cacheKey, final Object o, final ReferenceQueue<Object> referenceQueue) {
            super(o, referenceQueue);
            this.cacheKey = cacheKey;
        }
        
        String getCacheKey() {
            return this.cacheKey;
        }
    }
}

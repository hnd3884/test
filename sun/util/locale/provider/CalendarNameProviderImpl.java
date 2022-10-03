package sun.util.locale.provider;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;
import sun.util.calendar.Era;
import sun.util.calendar.CalendarSystem;
import java.util.Locale;
import java.util.Set;
import java.util.spi.CalendarNameProvider;

public class CalendarNameProviderImpl extends CalendarNameProvider implements AvailableLanguageTags
{
    private final LocaleProviderAdapter.Type type;
    private final Set<String> langtags;
    private static int[] REST_OF_STYLES;
    
    public CalendarNameProviderImpl(final LocaleProviderAdapter.Type type, final Set<String> langtags) {
        this.type = type;
        this.langtags = langtags;
    }
    
    @Override
    public String getDisplayName(final String s, final int n, final int n2, final int n3, final Locale locale) {
        return this.getDisplayNameImpl(s, n, n2, n3, locale, false);
    }
    
    public String getJavaTimeDisplayName(final String s, final int n, final int n2, final int n3, final Locale locale) {
        return this.getDisplayNameImpl(s, n, n2, n3, locale, true);
    }
    
    public String getDisplayNameImpl(final String s, final int n, int n2, final int n3, final Locale locale, final boolean b) {
        String displayName = null;
        final String resourceKey = this.getResourceKey(s, n, n3, b);
        if (resourceKey != null) {
            final LocaleResources localeResources = LocaleProviderAdapter.forType(this.type).getLocaleResources(locale);
            String[] array = b ? localeResources.getJavaTimeNames(resourceKey) : localeResources.getCalendarNames(resourceKey);
            if (array != null && array.length > 0) {
                if (n == 7 || n == 1) {
                    --n2;
                }
                if (n2 < 0) {
                    return null;
                }
                if (n2 >= array.length) {
                    if (n != 0 || !"japanese".equals(s)) {
                        return null;
                    }
                    final Era[] eras = CalendarSystem.forName("japanese").getEras();
                    if (n2 > eras.length) {
                        return null;
                    }
                    if (this.type == LocaleProviderAdapter.Type.CLDR) {
                        final LocaleResources localeResources2 = LocaleProviderAdapter.forJRE().getLocaleResources(locale);
                        final String resourceKey2 = getResourceKeyFor(LocaleProviderAdapter.Type.JRE, s, n, n3, b);
                        array = (b ? localeResources2.getJavaTimeNames(resourceKey2) : localeResources2.getCalendarNames(resourceKey2));
                    }
                    if (array == null || n2 >= array.length) {
                        final Era era = eras[n2 - 1];
                        if (b) {
                            return (getBaseStyle(n3) == 4) ? era.getAbbreviation() : era.getName();
                        }
                        return ((n3 & 0x2) != 0x0) ? era.getName() : era.getAbbreviation();
                    }
                }
                displayName = array[n2];
                if (displayName.length() == 0 && (n3 == 32769 || n3 == 32770 || n3 == 32772)) {
                    displayName = this.getDisplayName(s, n, n2, getBaseStyle(n3), locale);
                }
            }
        }
        return displayName;
    }
    
    @Override
    public Map<String, Integer> getDisplayNames(final String s, final int n, final int n2, final Locale locale) {
        Map<String, Integer> map;
        if (n2 == 0) {
            map = this.getDisplayNamesImpl(s, n, 1, locale, false);
            final int[] rest_OF_STYLES = CalendarNameProviderImpl.REST_OF_STYLES;
            for (int length = rest_OF_STYLES.length, i = 0; i < length; ++i) {
                map.putAll(this.getDisplayNamesImpl(s, n, rest_OF_STYLES[i], locale, false));
            }
        }
        else {
            map = this.getDisplayNamesImpl(s, n, n2, locale, false);
        }
        return map.isEmpty() ? null : map;
    }
    
    public Map<String, Integer> getJavaTimeDisplayNames(final String s, final int n, final int n2, final Locale locale) {
        final Map<String, Integer> displayNamesImpl = this.getDisplayNamesImpl(s, n, n2, locale, true);
        return displayNamesImpl.isEmpty() ? null : displayNamesImpl;
    }
    
    private Map<String, Integer> getDisplayNamesImpl(final String s, final int n, final int n2, final Locale locale, final boolean b) {
        final String resourceKey = this.getResourceKey(s, n, n2, b);
        final TreeMap treeMap = new TreeMap((Comparator<? super K>)LengthBasedComparator.INSTANCE);
        if (resourceKey != null) {
            final LocaleResources localeResources = LocaleProviderAdapter.forType(this.type).getLocaleResources(locale);
            final String[] array = b ? localeResources.getJavaTimeNames(resourceKey) : localeResources.getCalendarNames(resourceKey);
            if (array != null && !this.hasDuplicates(array)) {
                if (n == 1) {
                    if (array.length > 0) {
                        treeMap.put(array[0], 1);
                    }
                }
                else {
                    final int n3 = (n == 7) ? 1 : 0;
                    for (int i = 0; i < array.length; ++i) {
                        final String s2 = array[i];
                        if (s2.length() != 0) {
                            treeMap.put(s2, n3 + i);
                        }
                    }
                }
            }
        }
        return treeMap;
    }
    
    private static int getBaseStyle(final int n) {
        return n & 0xFFFF7FFF;
    }
    
    @Override
    public Locale[] getAvailableLocales() {
        return LocaleProviderAdapter.toLocaleArray(this.langtags);
    }
    
    @Override
    public boolean isSupportedLocale(Locale stripExtensions) {
        if (Locale.ROOT.equals(stripExtensions)) {
            return true;
        }
        String unicodeLocaleType = null;
        if (stripExtensions.hasExtensions()) {
            unicodeLocaleType = stripExtensions.getUnicodeLocaleType("ca");
            stripExtensions = stripExtensions.stripExtensions();
        }
        if (unicodeLocaleType != null) {
            final String s = unicodeLocaleType;
            switch (s) {
                case "buddhist":
                case "japanese":
                case "gregory":
                case "islamic":
                case "roc": {
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return this.langtags.contains(stripExtensions.toLanguageTag()) || (this.type == LocaleProviderAdapter.Type.JRE && this.langtags.contains(stripExtensions.toString().replace('_', '-')));
    }
    
    @Override
    public Set<String> getAvailableLanguageTags() {
        return this.langtags;
    }
    
    private boolean hasDuplicates(final String[] array) {
        for (int length = array.length, i = 0; i < length - 1; ++i) {
            final String s = array[i];
            if (s != null) {
                for (int j = i + 1; j < length; ++j) {
                    if (s.equals(array[j])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private String getResourceKey(final String s, final int n, final int n2, final boolean b) {
        return getResourceKeyFor(this.type, s, n, n2, b);
    }
    
    private static String getResourceKeyFor(final LocaleProviderAdapter.Type type, String s, final int n, final int n2, final boolean b) {
        final int baseStyle = getBaseStyle(n2);
        final boolean b2 = n2 != baseStyle;
        if ("gregory".equals(s)) {
            s = null;
        }
        final boolean b3 = baseStyle == 4;
        final StringBuilder sb = new StringBuilder();
        if (b) {
            sb.append("java.time.");
        }
        switch (n) {
            case 0: {
                if (s != null) {
                    sb.append(s).append('.');
                }
                if (b3) {
                    sb.append("narrow.");
                }
                else if (type == LocaleProviderAdapter.Type.JRE) {
                    if (b && baseStyle == 2) {
                        sb.append("long.");
                    }
                    if (baseStyle == 1) {
                        sb.append("short.");
                    }
                }
                else if (baseStyle == 2) {
                    sb.append("long.");
                }
                sb.append("Eras");
                break;
            }
            case 1: {
                if (!b3) {
                    sb.append(s).append(".FirstYear");
                    break;
                }
                break;
            }
            case 2: {
                if ("islamic".equals(s)) {
                    sb.append(s).append('.');
                }
                if (b2) {
                    sb.append("standalone.");
                }
                sb.append("Month").append(toStyleName(baseStyle));
                break;
            }
            case 7: {
                if (b2 && b3) {
                    sb.append("standalone.");
                }
                sb.append("Day").append(toStyleName(baseStyle));
                break;
            }
            case 9: {
                if (b3) {
                    sb.append("narrow.");
                }
                sb.append("AmPmMarkers");
                break;
            }
        }
        return (sb.length() > 0) ? sb.toString() : null;
    }
    
    private static String toStyleName(final int n) {
        switch (n) {
            case 1: {
                return "Abbreviations";
            }
            case 4: {
                return "Narrows";
            }
            default: {
                return "Names";
            }
        }
    }
    
    static {
        CalendarNameProviderImpl.REST_OF_STYLES = new int[] { 32769, 2, 32770, 4, 32772 };
    }
    
    private static class LengthBasedComparator implements Comparator<String>
    {
        private static final LengthBasedComparator INSTANCE;
        
        @Override
        public int compare(final String s, final String s2) {
            final int n = s2.length() - s.length();
            return (n == 0) ? s.compareTo(s2) : n;
        }
        
        static {
            INSTANCE = new LengthBasedComparator();
        }
    }
}

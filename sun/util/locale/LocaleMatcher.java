package sun.util.locale;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.List;

public final class LocaleMatcher
{
    public static List<Locale> filter(final List<Locale.LanguageRange> list, final Collection<Locale> collection, final Locale.FilteringMode filteringMode) {
        if (list.isEmpty() || collection.isEmpty()) {
            return new ArrayList<Locale>();
        }
        final ArrayList list2 = new ArrayList();
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            list2.add(((Locale)iterator.next()).toLanguageTag());
        }
        final List<String> filterTags = filterTags(list, list2, filteringMode);
        final ArrayList list3 = new ArrayList(filterTags.size());
        final Iterator iterator2 = filterTags.iterator();
        while (iterator2.hasNext()) {
            list3.add((Object)Locale.forLanguageTag((String)iterator2.next()));
        }
        return (List<Locale>)list3;
    }
    
    public static List<String> filterTags(final List<Locale.LanguageRange> list, final Collection<String> collection, final Locale.FilteringMode filteringMode) {
        if (list.isEmpty() || collection.isEmpty()) {
            return new ArrayList<String>();
        }
        if (filteringMode == Locale.FilteringMode.EXTENDED_FILTERING) {
            return filterExtended(list, collection);
        }
        final ArrayList list2 = new ArrayList();
        for (final Locale.LanguageRange languageRange : list) {
            final String range = languageRange.getRange();
            if (range.startsWith("*-") || range.indexOf("-*") != -1) {
                if (filteringMode == Locale.FilteringMode.AUTOSELECT_FILTERING) {
                    return filterExtended(list, collection);
                }
                if (filteringMode == Locale.FilteringMode.MAP_EXTENDED_RANGES) {
                    String replaceAll;
                    if (range.charAt(0) == '*') {
                        replaceAll = "*";
                    }
                    else {
                        replaceAll = range.replaceAll("-[*]", "");
                    }
                    list2.add(new Locale.LanguageRange(replaceAll, languageRange.getWeight()));
                }
                else {
                    if (filteringMode == Locale.FilteringMode.REJECT_EXTENDED_RANGES) {
                        throw new IllegalArgumentException("An extended range \"" + range + "\" found in REJECT_EXTENDED_RANGES mode.");
                    }
                    continue;
                }
            }
            else {
                list2.add(languageRange);
            }
        }
        return filterBasic(list2, collection);
    }
    
    private static List<String> filterBasic(final List<Locale.LanguageRange> list, final Collection<String> collection) {
        final ArrayList list2 = new ArrayList();
        final Iterator<Locale.LanguageRange> iterator = list.iterator();
        while (iterator.hasNext()) {
            final String range = iterator.next().getRange();
            if (range.equals("*")) {
                return new ArrayList<String>(collection);
            }
            final Iterator<String> iterator2 = collection.iterator();
            while (iterator2.hasNext()) {
                final String lowerCase = iterator2.next().toLowerCase();
                if (lowerCase.startsWith(range)) {
                    final int length = range.length();
                    if ((lowerCase.length() != length && lowerCase.charAt(length) != '-') || list2.contains(lowerCase)) {
                        continue;
                    }
                    list2.add(lowerCase);
                }
            }
        }
        return list2;
    }
    
    private static List<String> filterExtended(final List<Locale.LanguageRange> list, final Collection<String> collection) {
        final ArrayList list2 = new ArrayList();
        final Iterator<Locale.LanguageRange> iterator = list.iterator();
        while (iterator.hasNext()) {
            final String range = iterator.next().getRange();
            if (range.equals("*")) {
                return new ArrayList<String>(collection);
            }
            final String[] split = range.split("-");
            final Iterator<String> iterator2 = collection.iterator();
            while (iterator2.hasNext()) {
                final String lowerCase = iterator2.next().toLowerCase();
                final String[] split2 = lowerCase.split("-");
                if (!split[0].equals(split2[0]) && !split[0].equals("*")) {
                    continue;
                }
                int n = 1;
                int n2 = 1;
                while (n < split.length && n2 < split2.length) {
                    if (split[n].equals("*")) {
                        ++n;
                    }
                    else if (split[n].equals(split2[n2])) {
                        ++n;
                        ++n2;
                    }
                    else {
                        if (split2[n2].length() == 1 && !split2[n2].equals("*")) {
                            break;
                        }
                        ++n2;
                    }
                }
                if (split.length != n || list2.contains(lowerCase)) {
                    continue;
                }
                list2.add(lowerCase);
            }
        }
        return list2;
    }
    
    public static Locale lookup(final List<Locale.LanguageRange> list, final Collection<Locale> collection) {
        if (list.isEmpty() || collection.isEmpty()) {
            return null;
        }
        final ArrayList list2 = new ArrayList();
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            list2.add(((Locale)iterator.next()).toLanguageTag());
        }
        final String lookupTag = lookupTag(list, list2);
        if (lookupTag == null) {
            return null;
        }
        return Locale.forLanguageTag(lookupTag);
    }
    
    public static String lookupTag(final List<Locale.LanguageRange> list, final Collection<String> collection) {
        if (list.isEmpty() || collection.isEmpty()) {
            return null;
        }
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            final String range = ((Locale.LanguageRange)iterator.next()).getRange();
            if (range.equals("*")) {
                continue;
            }
            String s = range.replaceAll("\\x2A", "\\\\p{Alnum}*");
            while (s.length() > 0) {
                final Iterator iterator2 = collection.iterator();
                while (iterator2.hasNext()) {
                    final String lowerCase = ((String)iterator2.next()).toLowerCase();
                    if (lowerCase.matches(s)) {
                        return lowerCase;
                    }
                }
                final int lastIndex = s.lastIndexOf(45);
                if (lastIndex >= 0) {
                    s = s.substring(0, lastIndex);
                    if (s.lastIndexOf(45) != s.length() - 2) {
                        continue;
                    }
                    s = s.substring(0, s.length() - 2);
                }
                else {
                    s = "";
                }
            }
        }
        return null;
    }
    
    public static List<Locale.LanguageRange> parse(String s) {
        s = s.replaceAll(" ", "").toLowerCase();
        if (s.startsWith("accept-language:")) {
            s = s.substring(16);
        }
        final String[] split = s.split(",");
        final ArrayList list = new ArrayList(split.length);
        final ArrayList list2 = new ArrayList();
        int n = 0;
        for (final String s2 : split) {
            int index;
            String substring;
            double double1;
            if ((index = s2.indexOf(";q=")) == -1) {
                substring = s2;
                double1 = 1.0;
            }
            else {
                substring = s2.substring(0, index);
                index += 3;
                try {
                    double1 = Double.parseDouble(s2.substring(index));
                }
                catch (final Exception ex) {
                    throw new IllegalArgumentException("weight=\"" + s2.substring(index) + "\" for language range \"" + substring + "\"");
                }
                if (double1 < 0.0 || double1 > 1.0) {
                    throw new IllegalArgumentException("weight=" + double1 + " for language range \"" + substring + "\". It must be between " + 0.0 + " and " + 1.0 + ".");
                }
            }
            if (!list2.contains(substring)) {
                final Locale.LanguageRange languageRange = new Locale.LanguageRange(substring, double1);
                int n2 = n;
                for (int j = 0; j < n; ++j) {
                    if (((Locale.LanguageRange)list.get(j)).getWeight() < double1) {
                        n2 = j;
                        break;
                    }
                }
                list.add(n2, (Object)languageRange);
                ++n;
                list2.add(substring);
                final String equivalentForRegionAndVariant;
                if ((equivalentForRegionAndVariant = getEquivalentForRegionAndVariant(substring)) != null && !list2.contains(equivalentForRegionAndVariant)) {
                    list.add(n2 + 1, (Object)new Locale.LanguageRange(equivalentForRegionAndVariant, double1));
                    ++n;
                    list2.add(equivalentForRegionAndVariant);
                }
                final String[] equivalentsForLanguage;
                if ((equivalentsForLanguage = getEquivalentsForLanguage(substring)) != null) {
                    for (final String s3 : equivalentsForLanguage) {
                        if (!list2.contains(s3)) {
                            list.add(n2 + 1, (Object)new Locale.LanguageRange(s3, double1));
                            ++n;
                            list2.add(s3);
                        }
                        final String equivalentForRegionAndVariant2 = getEquivalentForRegionAndVariant(s3);
                        if (equivalentForRegionAndVariant2 != null && !list2.contains(equivalentForRegionAndVariant2)) {
                            list.add(n2 + 1, (Object)new Locale.LanguageRange(equivalentForRegionAndVariant2, double1));
                            ++n;
                            list2.add(equivalentForRegionAndVariant2);
                        }
                    }
                }
            }
        }
        return (List<Locale.LanguageRange>)list;
    }
    
    private static String[] getEquivalentsForLanguage(final String s) {
        int lastIndex;
        for (String substring = s; substring.length() > 0; substring = substring.substring(0, lastIndex)) {
            if (LocaleEquivalentMaps.singleEquivMap.containsKey(substring)) {
                return new String[] { s.replaceFirst(substring, LocaleEquivalentMaps.singleEquivMap.get(substring)) };
            }
            if (LocaleEquivalentMaps.multiEquivsMap.containsKey(substring)) {
                final String[] array = LocaleEquivalentMaps.multiEquivsMap.get(substring);
                for (int i = 0; i < array.length; ++i) {
                    array[i] = s.replaceFirst(substring, array[i]);
                }
                return array;
            }
            lastIndex = substring.lastIndexOf(45);
            if (lastIndex == -1) {
                break;
            }
        }
        return null;
    }
    
    private static String getEquivalentForRegionAndVariant(final String s) {
        final int extentionKeyIndex = getExtentionKeyIndex(s);
        for (final String s2 : LocaleEquivalentMaps.regionVariantEquivMap.keySet()) {
            final int index;
            if ((index = s.indexOf(s2)) != -1) {
                if (extentionKeyIndex != Integer.MIN_VALUE && index > extentionKeyIndex) {
                    continue;
                }
                final int n = index + s2.length();
                if (s.length() == n || s.charAt(n) == '-') {
                    return s.replaceFirst(s2, LocaleEquivalentMaps.regionVariantEquivMap.get(s2));
                }
                continue;
            }
        }
        return null;
    }
    
    private static int getExtentionKeyIndex(final String s) {
        final char[] charArray = s.toCharArray();
        int n = Integer.MIN_VALUE;
        for (int i = 1; i < charArray.length; ++i) {
            if (charArray[i] == '-') {
                if (i - n == 2) {
                    return n;
                }
                n = i;
            }
        }
        return Integer.MIN_VALUE;
    }
    
    public static List<Locale.LanguageRange> mapEquivalents(final List<Locale.LanguageRange> list, final Map<String, List<String>> map) {
        if (list.isEmpty()) {
            return new ArrayList<Locale.LanguageRange>();
        }
        if (map == null || map.isEmpty()) {
            return new ArrayList<Locale.LanguageRange>(list);
        }
        final HashMap hashMap = new HashMap();
        for (final String s : map.keySet()) {
            hashMap.put(s.toLowerCase(), s);
        }
        final ArrayList list2 = new ArrayList();
        for (final Locale.LanguageRange languageRange : list) {
            String s3;
            final String s2 = s3 = languageRange.getRange();
            boolean b = false;
            while (s3.length() > 0) {
                if (hashMap.containsKey(s3)) {
                    b = true;
                    final List list3 = map.get(hashMap.get(s3));
                    if (list3 != null) {
                        final int length = s3.length();
                        final Iterator iterator3 = list3.iterator();
                        while (iterator3.hasNext()) {
                            list2.add(new Locale.LanguageRange(((String)iterator3.next()).toLowerCase() + s2.substring(length), languageRange.getWeight()));
                        }
                        break;
                    }
                    break;
                }
                else {
                    final int lastIndex = s3.lastIndexOf(45);
                    if (lastIndex == -1) {
                        break;
                    }
                    s3 = s3.substring(0, lastIndex);
                }
            }
            if (!b) {
                list2.add(languageRange);
            }
        }
        return list2;
    }
    
    private LocaleMatcher() {
    }
}

package java.lang;

import sun.text.Normalizer;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.HashSet;
import java.util.Hashtable;

final class ConditionalSpecialCasing
{
    static final int FINAL_CASED = 1;
    static final int AFTER_SOFT_DOTTED = 2;
    static final int MORE_ABOVE = 3;
    static final int AFTER_I = 4;
    static final int NOT_BEFORE_DOT = 5;
    static final int COMBINING_CLASS_ABOVE = 230;
    static Entry[] entry;
    static Hashtable<Integer, HashSet<Entry>> entryTable;
    
    static int toLowerCaseEx(final String s, final int n, final Locale locale) {
        final char[] lookUpTable = lookUpTable(s, n, locale, true);
        if (lookUpTable == null) {
            return Character.toLowerCase(s.codePointAt(n));
        }
        if (lookUpTable.length == 1) {
            return lookUpTable[0];
        }
        return -1;
    }
    
    static int toUpperCaseEx(final String s, final int n, final Locale locale) {
        final char[] lookUpTable = lookUpTable(s, n, locale, false);
        if (lookUpTable == null) {
            return Character.toUpperCaseEx(s.codePointAt(n));
        }
        if (lookUpTable.length == 1) {
            return lookUpTable[0];
        }
        return -1;
    }
    
    static char[] toLowerCaseCharArray(final String s, final int n, final Locale locale) {
        return lookUpTable(s, n, locale, true);
    }
    
    static char[] toUpperCaseCharArray(final String s, final int n, final Locale locale) {
        final char[] lookUpTable = lookUpTable(s, n, locale, false);
        if (lookUpTable != null) {
            return lookUpTable;
        }
        return Character.toUpperCaseCharArray(s.codePointAt(n));
    }
    
    private static char[] lookUpTable(final String s, final int n, final Locale locale, final boolean b) {
        final HashSet set = ConditionalSpecialCasing.entryTable.get(new Integer(s.codePointAt(n)));
        char[] array = null;
        if (set != null) {
            final Iterator iterator = set.iterator();
            final String language = locale.getLanguage();
            while (iterator.hasNext()) {
                final Entry entry = (Entry)iterator.next();
                final String language2 = entry.getLanguage();
                if ((language2 == null || language2.equals(language)) && isConditionMet(s, n, locale, entry.getCondition())) {
                    array = (b ? entry.getLowerCase() : entry.getUpperCase());
                    if (language2 != null) {
                        break;
                    }
                    continue;
                }
            }
        }
        return array;
    }
    
    private static boolean isConditionMet(final String s, final int n, final Locale locale, final int n2) {
        switch (n2) {
            case 1: {
                return isFinalCased(s, n, locale);
            }
            case 2: {
                return isAfterSoftDotted(s, n);
            }
            case 3: {
                return isMoreAbove(s, n);
            }
            case 4: {
                return isAfterI(s, n);
            }
            case 5: {
                return !isBeforeDot(s, n);
            }
            default: {
                return true;
            }
        }
    }
    
    private static boolean isFinalCased(final String text, final int n, final Locale locale) {
        final BreakIterator wordInstance = BreakIterator.getWordInstance(locale);
        wordInstance.setText(text);
        int codePointBefore;
        for (int n2 = n; n2 >= 0 && !wordInstance.isBoundary(n2); n2 -= Character.charCount(codePointBefore)) {
            codePointBefore = text.codePointBefore(n2);
            if (isCased(codePointBefore)) {
                int codePoint;
                for (int length = text.length(), n3 = n + Character.charCount(text.codePointAt(n)); n3 < length && !wordInstance.isBoundary(n3); n3 += Character.charCount(codePoint)) {
                    codePoint = text.codePointAt(n3);
                    if (isCased(codePoint)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    private static boolean isAfterI(final String s, final int n) {
        int codePointBefore;
        for (int i = n; i > 0; i -= Character.charCount(codePointBefore)) {
            codePointBefore = s.codePointBefore(i);
            if (codePointBefore == 73) {
                return true;
            }
            final int combiningClass = Normalizer.getCombiningClass(codePointBefore);
            if (combiningClass == 0 || combiningClass == 230) {
                return false;
            }
        }
        return false;
    }
    
    private static boolean isAfterSoftDotted(final String s, final int n) {
        int codePointBefore;
        for (int i = n; i > 0; i -= Character.charCount(codePointBefore)) {
            codePointBefore = s.codePointBefore(i);
            if (isSoftDotted(codePointBefore)) {
                return true;
            }
            final int combiningClass = Normalizer.getCombiningClass(codePointBefore);
            if (combiningClass == 0 || combiningClass == 230) {
                return false;
            }
        }
        return false;
    }
    
    private static boolean isMoreAbove(final String s, final int n) {
        int codePoint;
        for (int length = s.length(), i = n + Character.charCount(s.codePointAt(n)); i < length; i += Character.charCount(codePoint)) {
            codePoint = s.codePointAt(i);
            final int combiningClass = Normalizer.getCombiningClass(codePoint);
            if (combiningClass == 230) {
                return true;
            }
            if (combiningClass == 0) {
                return false;
            }
        }
        return false;
    }
    
    private static boolean isBeforeDot(final String s, final int n) {
        int codePoint;
        for (int length = s.length(), i = n + Character.charCount(s.codePointAt(n)); i < length; i += Character.charCount(codePoint)) {
            codePoint = s.codePointAt(i);
            if (codePoint == 775) {
                return true;
            }
            final int combiningClass = Normalizer.getCombiningClass(codePoint);
            if (combiningClass == 0 || combiningClass == 230) {
                return false;
            }
        }
        return false;
    }
    
    private static boolean isCased(final int n) {
        final int type = Character.getType(n);
        return type == 2 || type == 1 || type == 3 || (n >= 688 && n <= 696) || (n >= 704 && n <= 705) || (n >= 736 && n <= 740) || n == 837 || n == 890 || (n >= 7468 && n <= 7521) || (n >= 8544 && n <= 8575) || (n >= 9398 && n <= 9449);
    }
    
    private static boolean isSoftDotted(final int n) {
        switch (n) {
            case 105:
            case 106:
            case 303:
            case 616:
            case 1110:
            case 1112:
            case 7522:
            case 7725:
            case 7883:
            case 8305: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        ConditionalSpecialCasing.entry = new Entry[] { new Entry(931, new char[] { '\u03c2' }, new char[] { '\u03a3' }, null, 1), new Entry(304, new char[] { 'i', '\u0307' }, new char[] { '\u0130' }, null, 0), new Entry(775, new char[] { '\u0307' }, new char[0], "lt", 2), new Entry(73, new char[] { 'i', '\u0307' }, new char[] { 'I' }, "lt", 3), new Entry(74, new char[] { 'j', '\u0307' }, new char[] { 'J' }, "lt", 3), new Entry(302, new char[] { '\u012f', '\u0307' }, new char[] { '\u012e' }, "lt", 3), new Entry(204, new char[] { 'i', '\u0307', '\u0300' }, new char[] { '\u00cc' }, "lt", 0), new Entry(205, new char[] { 'i', '\u0307', '\u0301' }, new char[] { '\u00cd' }, "lt", 0), new Entry(296, new char[] { 'i', '\u0307', '\u0303' }, new char[] { '\u0128' }, "lt", 0), new Entry(304, new char[] { 'i' }, new char[] { '\u0130' }, "tr", 0), new Entry(304, new char[] { 'i' }, new char[] { '\u0130' }, "az", 0), new Entry(775, new char[0], new char[] { '\u0307' }, "tr", 4), new Entry(775, new char[0], new char[] { '\u0307' }, "az", 4), new Entry(73, new char[] { '\u0131' }, new char[] { 'I' }, "tr", 5), new Entry(73, new char[] { '\u0131' }, new char[] { 'I' }, "az", 5), new Entry(105, new char[] { 'i' }, new char[] { '\u0130' }, "tr", 0), new Entry(105, new char[] { 'i' }, new char[] { '\u0130' }, "az", 0) };
        ConditionalSpecialCasing.entryTable = new Hashtable<Integer, HashSet<Entry>>();
        for (int i = 0; i < ConditionalSpecialCasing.entry.length; ++i) {
            final Entry entry = ConditionalSpecialCasing.entry[i];
            final Integer n = new Integer(entry.getCodePoint());
            HashSet<Entry> set = ConditionalSpecialCasing.entryTable.get(n);
            if (set == null) {
                set = new HashSet<Entry>();
            }
            set.add(entry);
            ConditionalSpecialCasing.entryTable.put(n, set);
        }
    }
    
    static class Entry
    {
        int ch;
        char[] lower;
        char[] upper;
        String lang;
        int condition;
        
        Entry(final int ch, final char[] lower, final char[] upper, final String lang, final int condition) {
            this.ch = ch;
            this.lower = lower;
            this.upper = upper;
            this.lang = lang;
            this.condition = condition;
        }
        
        int getCodePoint() {
            return this.ch;
        }
        
        char[] getLowerCase() {
            return this.lower;
        }
        
        char[] getUpperCase() {
            return this.upper;
        }
        
        String getLanguage() {
            return this.lang;
        }
        
        int getCondition() {
            return this.condition;
        }
    }
}

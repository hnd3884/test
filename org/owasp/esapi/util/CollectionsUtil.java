package org.owasp.esapi.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectionsUtil
{
    private static final char[] EMPTY_CHAR_ARRAY;
    
    public static Set<Character> arrayToSet(final char... array) {
        if (array == null) {
            return new HashSet<Character>();
        }
        final Set<Character> toReturn = new HashSet<Character>(array.length);
        for (final char c : array) {
            toReturn.add(c);
        }
        return toReturn;
    }
    
    public static Set<Character> arrayToUnmodifiableSet(final char... array) {
        if (array == null) {
            return Collections.emptySet();
        }
        if (array.length == 1) {
            return Collections.singleton(array[0]);
        }
        return Collections.unmodifiableSet((Set<? extends Character>)arrayToSet(array));
    }
    
    public static char[] strToChars(final String str) {
        if (str == null) {
            return CollectionsUtil.EMPTY_CHAR_ARRAY;
        }
        final int len = str.length();
        final char[] ret = new char[len];
        str.getChars(0, len, ret, 0);
        return ret;
    }
    
    public static Set<Character> strToSet(final String str) {
        if (str == null) {
            return new HashSet<Character>();
        }
        final Set<Character> set = new HashSet<Character>(str.length());
        for (int i = 0; i < str.length(); ++i) {
            set.add(str.charAt(i));
        }
        return set;
    }
    
    public static Set<Character> strToUnmodifiableSet(final String str) {
        if (str == null) {
            return Collections.emptySet();
        }
        if (str.length() == 1) {
            return Collections.singleton(str.charAt(0));
        }
        return Collections.unmodifiableSet((Set<? extends Character>)strToSet(str));
    }
    
    private CollectionsUtil() {
    }
    
    static {
        EMPTY_CHAR_ARRAY = new char[0];
    }
}

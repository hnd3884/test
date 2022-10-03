package org.jfree.util;

import java.util.Map;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class AttributedStringUtilities
{
    private AttributedStringUtilities() {
    }
    
    public static boolean equal(final AttributedString s1, final AttributedString s2) {
        if (s1 == null) {
            return s2 == null;
        }
        if (s2 == null) {
            return false;
        }
        final AttributedCharacterIterator it1 = s1.getIterator();
        final AttributedCharacterIterator it2 = s2.getIterator();
        char c1 = it1.first();
        char c2 = it2.first();
        int start = 0;
        while (c1 != '\uffff') {
            final int limit1 = it1.getRunLimit();
            final int limit2 = it2.getRunLimit();
            if (limit1 != limit2) {
                return false;
            }
            final Map m1 = it1.getAttributes();
            final Map m2 = it2.getAttributes();
            if (!m1.equals(m2)) {
                return false;
            }
            for (int i = start; i < limit1; ++i) {
                if (c1 != c2) {
                    return false;
                }
                c1 = it1.next();
                c2 = it2.next();
            }
            start = limit1;
        }
        return c2 == '\uffff';
    }
}

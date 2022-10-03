package org.apache.commons.collections4;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Iterator;
import org.apache.commons.collections4.iterators.EnumerationIterator;
import java.util.List;
import java.util.Enumeration;

public class EnumerationUtils
{
    private EnumerationUtils() {
    }
    
    public static <T> T get(final Enumeration<T> e, final int index) {
        int i = index;
        CollectionUtils.checkIndexBounds(i);
        while (e.hasMoreElements()) {
            if (--i == -1) {
                return e.nextElement();
            }
            e.nextElement();
        }
        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }
    
    public static <E> List<E> toList(final Enumeration<? extends E> enumeration) {
        return IteratorUtils.toList((Iterator<? extends E>)new EnumerationIterator<E>(enumeration));
    }
    
    public static List<String> toList(final StringTokenizer stringTokenizer) {
        final List<String> result = new ArrayList<String>(stringTokenizer.countTokens());
        while (stringTokenizer.hasMoreTokens()) {
            result.add(stringTokenizer.nextToken());
        }
        return result;
    }
}

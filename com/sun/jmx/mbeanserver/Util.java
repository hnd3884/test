package com.sun.jmx.mbeanserver;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.IdentityHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Util
{
    public static ObjectName newObjectName(final String s) {
        try {
            return new ObjectName(s);
        }
        catch (final MalformedObjectNameException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    static <K, V> Map<K, V> newMap() {
        return new HashMap<K, V>();
    }
    
    static <K, V> Map<K, V> newSynchronizedMap() {
        return Collections.synchronizedMap((Map<K, V>)newMap());
    }
    
    static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }
    
    static <K, V> Map<K, V> newSynchronizedIdentityHashMap() {
        return Collections.synchronizedMap((Map<K, V>)newIdentityHashMap());
    }
    
    static <K, V> SortedMap<K, V> newSortedMap() {
        return new TreeMap<K, V>();
    }
    
    static <K, V> SortedMap<K, V> newSortedMap(final Comparator<? super K> comparator) {
        return new TreeMap<K, V>(comparator);
    }
    
    static <K, V> Map<K, V> newInsertionOrderMap() {
        return new LinkedHashMap<K, V>();
    }
    
    static <E> Set<E> newSet() {
        return new HashSet<E>();
    }
    
    static <E> Set<E> newSet(final Collection<E> collection) {
        return new HashSet<E>((Collection<? extends E>)collection);
    }
    
    static <E> List<E> newList() {
        return new ArrayList<E>();
    }
    
    static <E> List<E> newList(final Collection<E> collection) {
        return new ArrayList<E>((Collection<? extends E>)collection);
    }
    
    public static <T> T cast(final Object o) {
        return (T)o;
    }
    
    public static int hashCode(final String[] array, final Object[] array2) {
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            final Object o = array2[i];
            int n2;
            if (o == null) {
                n2 = 0;
            }
            else if (o instanceof Object[]) {
                n2 = Arrays.deepHashCode((Object[])o);
            }
            else if (((Object[])o).getClass().isArray()) {
                n2 = Arrays.deepHashCode(new Object[] { o }) - 31;
            }
            else {
                n2 = o.hashCode();
            }
            n += (array[i].toLowerCase().hashCode() ^ n2);
        }
        return n;
    }
    
    private static boolean wildmatch(final String s, final String s2, int n, final int n2, int n3, final int n4) {
        int n6;
        int n5 = n6 = -1;
        while (true) {
            if (n3 < n4) {
                final char char1 = s2.charAt(n3);
                switch (char1) {
                    case 63: {
                        if (n == n2) {
                            break;
                        }
                        ++n;
                        ++n3;
                        continue;
                    }
                    case 42: {
                        n5 = ++n3;
                        n6 = n;
                        continue;
                    }
                    default: {
                        if (n < n2 && s.charAt(n) == char1) {
                            ++n;
                            ++n3;
                            continue;
                        }
                        break;
                    }
                }
            }
            else if (n == n2) {
                return true;
            }
            if (n5 < 0 || n6 == n2) {
                return false;
            }
            n3 = n5;
            n = ++n6;
        }
    }
    
    public static boolean wildmatch(final String s, final String s2) {
        return wildmatch(s, s2, 0, s.length(), 0, s2.length());
    }
}

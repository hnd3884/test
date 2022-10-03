package com.theorem.radius3;

import java.util.Collections;
import java.util.Iterator;
import com.theorem.radius3.dictionary.RADIUSDictionary;
import java.util.Set;
import java.util.HashSet;

public final class Dict
{
    private static HashSet a;
    private static Set b;
    
    public static void addDictionary(final RADIUSDictionary radiusDictionary) {
        if (hasDictionary(radiusDictionary)) {
            return;
        }
        synchronized (Dict.b) {
            Dict.a.add(radiusDictionary);
        }
    }
    
    public static boolean hasDictionary(final RADIUSDictionary radiusDictionary) {
        synchronized (Dict.b) {
            final Iterator iterator = Dict.a.iterator();
            while (iterator.hasNext()) {
                if (radiusDictionary.equals(iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static RADIUSDictionary[] getDictionaries() {
        synchronized (Dict.b) {
            return (RADIUSDictionary[])Dict.a.toArray(new RADIUSDictionary[Dict.a.size()]);
        }
    }
    
    static {
        Dict.a = new HashSet();
        Dict.b = Collections.synchronizedSet((Set<Object>)Dict.a);
    }
}

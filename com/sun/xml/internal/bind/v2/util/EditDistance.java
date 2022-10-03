package com.sun.xml.internal.bind.v2.util;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.AbstractMap;
import java.util.WeakHashMap;

public class EditDistance
{
    private static final WeakHashMap<AbstractMap.SimpleEntry<String, String>, Integer> CACHE;
    private int[] cost;
    private int[] back;
    private final String a;
    private final String b;
    
    public static int editDistance(final String a, final String b) {
        final AbstractMap.SimpleEntry<String, String> entry = new AbstractMap.SimpleEntry<String, String>(a, b);
        Integer result = null;
        if (EditDistance.CACHE.containsKey(entry)) {
            result = EditDistance.CACHE.get(entry);
        }
        if (result == null) {
            result = new EditDistance(a, b).calc();
            EditDistance.CACHE.put(entry, result);
        }
        return result;
    }
    
    public static String findNearest(final String key, final String[] group) {
        return findNearest(key, Arrays.asList(group));
    }
    
    public static String findNearest(final String key, final Collection<String> group) {
        int c = Integer.MAX_VALUE;
        String r = null;
        for (final String s : group) {
            final int ed = editDistance(key, s);
            if (c > ed) {
                c = ed;
                r = s;
            }
        }
        return r;
    }
    
    private EditDistance(final String a, final String b) {
        this.a = a;
        this.b = b;
        this.cost = new int[a.length() + 1];
        this.back = new int[a.length() + 1];
        for (int i = 0; i <= a.length(); ++i) {
            this.cost[i] = i;
        }
    }
    
    private void flip() {
        final int[] t = this.cost;
        this.cost = this.back;
        this.back = t;
    }
    
    private int min(final int a, final int b, final int c) {
        return Math.min(a, Math.min(b, c));
    }
    
    private int calc() {
        for (int j = 0; j < this.b.length(); ++j) {
            this.flip();
            this.cost[0] = j + 1;
            for (int i = 0; i < this.a.length(); ++i) {
                final int match = (this.a.charAt(i) != this.b.charAt(j)) ? 1 : 0;
                this.cost[i + 1] = this.min(this.back[i] + match, this.cost[i] + 1, this.back[i + 1] + 1);
            }
        }
        return this.cost[this.a.length()];
    }
    
    static {
        CACHE = new WeakHashMap<AbstractMap.SimpleEntry<String, String>, Integer>();
    }
}

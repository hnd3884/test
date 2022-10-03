package com.sun.xml.internal.ws.transport;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

public class Headers extends TreeMap<String, List<String>>
{
    private static final InsensitiveComparator INSTANCE;
    
    public Headers() {
        super(Headers.INSTANCE);
    }
    
    public void add(final String key, final String value) {
        List<String> list = ((TreeMap<K, List<String>>)this).get(key);
        if (list == null) {
            list = new LinkedList<String>();
            this.put(key, list);
        }
        list.add(value);
    }
    
    public String getFirst(final String key) {
        final List<String> l = ((TreeMap<K, List<String>>)this).get(key);
        return (l == null) ? null : l.get(0);
    }
    
    public void set(final String key, final String value) {
        final LinkedList<String> l = new LinkedList<String>();
        l.add(value);
        ((TreeMap<String, LinkedList<String>>)this).put(key, l);
    }
    
    static {
        INSTANCE = new InsensitiveComparator();
    }
    
    private static final class InsensitiveComparator implements Comparator<String>, Serializable
    {
        @Override
        public int compare(final String o1, final String o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.compareToIgnoreCase(o2);
        }
    }
}

package com.sun.net.httpserver;

import java.util.Collection;
import java.util.Set;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;
import jdk.Exported;
import java.util.List;
import java.util.Map;

@Exported
public class Headers implements Map<String, List<String>>
{
    HashMap<String, List<String>> map;
    
    public Headers() {
        this.map = new HashMap<String, List<String>>(32);
    }
    
    private String normalize(final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        if (length == 0) {
            return s;
        }
        final char[] charArray = s.toCharArray();
        if (charArray[0] >= 'a' && charArray[0] <= 'z') {
            charArray[0] -= ' ';
        }
        else if (charArray[0] == '\r' || charArray[0] == '\n') {
            throw new IllegalArgumentException("illegal character in key");
        }
        for (int i = 1; i < length; ++i) {
            if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                charArray[i] += ' ';
            }
            else if (charArray[i] == '\r' || charArray[i] == '\n') {
                throw new IllegalArgumentException("illegal character in key");
            }
        }
        return new String(charArray);
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return o != null && o instanceof String && this.map.containsKey(this.normalize((String)o));
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.map.containsValue(o);
    }
    
    @Override
    public List<String> get(final Object o) {
        return this.map.get(this.normalize((String)o));
    }
    
    public String getFirst(final String s) {
        final List list = this.map.get(this.normalize(s));
        if (list == null) {
            return null;
        }
        return (String)list.get(0);
    }
    
    @Override
    public List<String> put(final String s, final List<String> list) {
        final Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            checkValue(iterator.next());
        }
        return this.map.put(this.normalize(s), list);
    }
    
    public void add(final String s, final String s2) {
        checkValue(s2);
        final String normalize = this.normalize(s);
        List list = this.map.get(normalize);
        if (list == null) {
            list = new LinkedList();
            this.map.put(normalize, list);
        }
        list.add(s2);
    }
    
    private static void checkValue(final String s) {
        for (int length = s.length(), i = 0; i < length; ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '\r') {
                if (i >= length - 2) {
                    throw new IllegalArgumentException("Illegal CR found in header");
                }
                final char char2 = s.charAt(i + 1);
                final char char3 = s.charAt(i + 2);
                if (char2 != '\n') {
                    throw new IllegalArgumentException("Illegal char found after CR in header");
                }
                if (char3 != ' ' && char3 != '\t') {
                    throw new IllegalArgumentException("No whitespace found after CRLF in header");
                }
                i += 2;
            }
            else if (char1 == '\n') {
                throw new IllegalArgumentException("Illegal LF found in header");
            }
        }
    }
    
    public void set(final String s, final String s2) {
        final LinkedList list = new LinkedList();
        list.add(s2);
        this.put(s, (List<String>)list);
    }
    
    @Override
    public List<String> remove(final Object o) {
        return this.map.remove(this.normalize((String)o));
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends List<String>> map) {
        this.map.putAll(map);
    }
    
    @Override
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public Set<String> keySet() {
        return this.map.keySet();
    }
    
    @Override
    public Collection<List<String>> values() {
        return this.map.values();
    }
    
    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return this.map.entrySet();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.map.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
}

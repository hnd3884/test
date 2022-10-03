package com.zoho.security.wrapper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.List;
import java.util.HashMap;

public class RegexHashMap<K extends String, V> extends HashMap<K, V>
{
    private static final long serialVersionUID = -3498411871333835955L;
    private List<Pattern> patterns;
    
    public RegexHashMap() {
        this.patterns = new LinkedList<Pattern>();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        if (super.containsKey(key)) {
            return true;
        }
        for (final Pattern pattern : this.patterns) {
            if (pattern.matcher(key.toString()).matches()) {
                return true;
            }
        }
        return false;
    }
    
    public V get(final Object key, final boolean includePattern) {
        V value = this.get(key);
        if (includePattern && value == null) {
            for (final Pattern pattern : this.patterns) {
                if (pattern.matcher(key.toString()).matches()) {
                    value = this.get(pattern.toString());
                    break;
                }
            }
        }
        return value;
    }
    
    public V put(final K key, final V value, final Pattern keyPattern) {
        final V oldValue = this.put(key, value);
        if (oldValue == null) {
            this.patterns.add(keyPattern);
        }
        return oldValue;
    }
    
    @Override
    public V remove(final Object key) {
        final V oldValue = super.remove(key);
        if (oldValue != null) {
            for (final Pattern pattern : this.patterns) {
                if (key.equals(pattern.toString())) {
                    this.patterns.remove(pattern);
                    break;
                }
            }
        }
        return oldValue;
    }
}

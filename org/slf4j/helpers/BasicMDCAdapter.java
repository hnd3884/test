package org.slf4j.helpers;

import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.spi.MDCAdapter;

public class BasicMDCAdapter implements MDCAdapter
{
    private InheritableThreadLocal<Map<String, String>> inheritableThreadLocal;
    static boolean IS_JDK14;
    
    public BasicMDCAdapter() {
        this.inheritableThreadLocal = new InheritableThreadLocal<Map<String, String>>();
    }
    
    static boolean isJDK14() {
        try {
            final String javaVersion = System.getProperty("java.version");
            return javaVersion.startsWith("1.4");
        }
        catch (final SecurityException se) {
            return false;
        }
    }
    
    public void put(final String key, final String val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Map<String, String> map = this.inheritableThreadLocal.get();
        if (map == null) {
            map = Collections.synchronizedMap(new HashMap<String, String>());
            this.inheritableThreadLocal.set(map);
        }
        map.put(key, val);
    }
    
    public String get(final String key) {
        final Map<String, String> Map = this.inheritableThreadLocal.get();
        if (Map != null && key != null) {
            return Map.get(key);
        }
        return null;
    }
    
    public void remove(final String key) {
        final Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            map.remove(key);
        }
    }
    
    public void clear() {
        final Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            map.clear();
            if (isJDK14()) {
                this.inheritableThreadLocal.set(null);
            }
            else {
                this.inheritableThreadLocal.remove();
            }
        }
    }
    
    public Set<String> getKeys() {
        final Map<String, String> map = this.inheritableThreadLocal.get();
        if (map != null) {
            return map.keySet();
        }
        return null;
    }
    
    public Map<String, String> getCopyOfContextMap() {
        final Map<String, String> oldMap = this.inheritableThreadLocal.get();
        if (oldMap != null) {
            final Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
            return newMap;
        }
        return null;
    }
    
    public void setContextMap(final Map<String, String> contextMap) {
        final Map<String, String> map = Collections.synchronizedMap(new HashMap<String, String>(contextMap));
        this.inheritableThreadLocal.set(map);
    }
    
    static {
        BasicMDCAdapter.IS_JDK14 = isJDK14();
    }
}

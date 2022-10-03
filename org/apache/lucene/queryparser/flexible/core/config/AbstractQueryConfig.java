package org.apache.lucene.queryparser.flexible.core.config;

import java.util.HashMap;

public abstract class AbstractQueryConfig
{
    private final HashMap<ConfigurationKey<?>, Object> configMap;
    
    AbstractQueryConfig() {
        this.configMap = new HashMap<ConfigurationKey<?>, Object>();
    }
    
    public <T> T get(final ConfigurationKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return (T)this.configMap.get(key);
    }
    
    public <T> boolean has(final ConfigurationKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return this.configMap.containsKey(key);
    }
    
    public <T> void set(final ConfigurationKey<T> key, final T value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        if (value == null) {
            this.unset(key);
        }
        else {
            this.configMap.put(key, value);
        }
    }
    
    public <T> boolean unset(final ConfigurationKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return this.configMap.remove(key) != null;
    }
}

package org.apache.commons.beanutils;

import java.util.WeakHashMap;
import java.util.Map;

public class ContextClassLoaderLocal<T>
{
    private final Map<ClassLoader, T> valueByClassLoader;
    private boolean globalValueInitialized;
    private T globalValue;
    
    public ContextClassLoaderLocal() {
        this.valueByClassLoader = new WeakHashMap<ClassLoader, T>();
        this.globalValueInitialized = false;
    }
    
    protected T initialValue() {
        return null;
    }
    
    public synchronized T get() {
        this.valueByClassLoader.isEmpty();
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                T value = this.valueByClassLoader.get(contextClassLoader);
                if (value == null && !this.valueByClassLoader.containsKey(contextClassLoader)) {
                    value = this.initialValue();
                    this.valueByClassLoader.put(contextClassLoader, value);
                }
                return value;
            }
        }
        catch (final SecurityException ex) {}
        if (!this.globalValueInitialized) {
            this.globalValue = this.initialValue();
            this.globalValueInitialized = true;
        }
        return this.globalValue;
    }
    
    public synchronized void set(final T value) {
        this.valueByClassLoader.isEmpty();
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (contextClassLoader != null) {
                this.valueByClassLoader.put(contextClassLoader, value);
                return;
            }
        }
        catch (final SecurityException ex) {}
        this.globalValue = value;
        this.globalValueInitialized = true;
    }
    
    public synchronized void unset() {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            this.unset(contextClassLoader);
        }
        catch (final SecurityException ex) {}
    }
    
    public synchronized void unset(final ClassLoader classLoader) {
        this.valueByClassLoader.remove(classLoader);
    }
}

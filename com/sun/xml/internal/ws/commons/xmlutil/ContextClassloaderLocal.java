package com.sun.xml.internal.ws.commons.xmlutil;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

abstract class ContextClassloaderLocal<V>
{
    private static final String FAILED_TO_CREATE_NEW_INSTANCE = "FAILED_TO_CREATE_NEW_INSTANCE";
    private WeakHashMap<ClassLoader, V> CACHE;
    
    ContextClassloaderLocal() {
        this.CACHE = new WeakHashMap<ClassLoader, V>();
    }
    
    public V get() throws Error {
        final ClassLoader tccl = getContextClassLoader();
        V instance = this.CACHE.get(tccl);
        if (instance == null) {
            instance = this.createNewInstance();
            this.CACHE.put(tccl, instance);
        }
        return instance;
    }
    
    public void set(final V instance) {
        this.CACHE.put(getContextClassLoader(), instance);
    }
    
    protected abstract V initialValue() throws Exception;
    
    private V createNewInstance() {
        try {
            return this.initialValue();
        }
        catch (final Exception e) {
            throw new Error(format("FAILED_TO_CREATE_NEW_INSTANCE", this.getClass().getName()), e);
        }
    }
    
    private static String format(final String property, final Object... args) {
        final String text = ResourceBundle.getBundle(ContextClassloaderLocal.class.getName()).getString(property);
        return MessageFormat.format(text, args);
    }
    
    private static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (final SecurityException ex) {}
                return cl;
            }
        });
    }
}

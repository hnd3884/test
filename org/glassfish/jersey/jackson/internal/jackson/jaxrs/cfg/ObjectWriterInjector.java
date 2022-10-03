package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectWriterInjector
{
    protected static final ThreadLocal<ObjectWriterModifier> _threadLocal;
    protected static final AtomicBoolean _hasBeenSet;
    
    private ObjectWriterInjector() {
    }
    
    public static void set(final ObjectWriterModifier mod) {
        ObjectWriterInjector._hasBeenSet.set(true);
        ObjectWriterInjector._threadLocal.set(mod);
    }
    
    public static ObjectWriterModifier get() {
        return ObjectWriterInjector._hasBeenSet.get() ? ObjectWriterInjector._threadLocal.get() : null;
    }
    
    public static ObjectWriterModifier getAndClear() {
        final ObjectWriterModifier mod = get();
        if (mod != null) {
            ObjectWriterInjector._threadLocal.remove();
        }
        return mod;
    }
    
    static {
        _threadLocal = new ThreadLocal<ObjectWriterModifier>();
        _hasBeenSet = new AtomicBoolean(false);
    }
}

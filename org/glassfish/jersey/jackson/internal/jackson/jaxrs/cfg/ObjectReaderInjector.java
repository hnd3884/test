package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectReaderInjector
{
    protected static final ThreadLocal<ObjectReaderModifier> _threadLocal;
    protected static final AtomicBoolean _hasBeenSet;
    
    private ObjectReaderInjector() {
    }
    
    public static void set(final ObjectReaderModifier mod) {
        ObjectReaderInjector._hasBeenSet.set(true);
        ObjectReaderInjector._threadLocal.set(mod);
    }
    
    public static ObjectReaderModifier get() {
        return ObjectReaderInjector._hasBeenSet.get() ? ObjectReaderInjector._threadLocal.get() : null;
    }
    
    public static ObjectReaderModifier getAndClear() {
        final ObjectReaderModifier mod = get();
        if (mod != null) {
            ObjectReaderInjector._threadLocal.remove();
        }
        return mod;
    }
    
    static {
        _threadLocal = new ThreadLocal<ObjectReaderModifier>();
        _hasBeenSet = new AtomicBoolean(false);
    }
}

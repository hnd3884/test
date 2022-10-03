package com.sun.org.glassfish.gmbal.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.Constructor;

public class GenericConstructor<T>
{
    private final Object lock;
    private String typeName;
    private Class<T> resultType;
    private Class<?> type;
    private Class<?>[] signature;
    private Constructor constructor;
    
    public GenericConstructor(final Class<T> type, final String className, final Class<?>... signature) {
        this.lock = new Object();
        this.resultType = type;
        this.typeName = className;
        this.signature = signature.clone();
    }
    
    private void getConstructor() {
        synchronized (this.lock) {
            if (this.type != null) {
                if (this.constructor != null) {
                    return;
                }
            }
            try {
                this.type = Class.forName(this.typeName);
                this.constructor = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor>)new PrivilegedExceptionAction<Constructor>() {
                    @Override
                    public Constructor run() throws Exception {
                        synchronized (GenericConstructor.this.lock) {
                            return GenericConstructor.this.type.getDeclaredConstructor((Class[])GenericConstructor.this.signature);
                        }
                    }
                });
            }
            catch (final Exception exc) {
                Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.FINE, "Failure in getConstructor", exc);
            }
        }
    }
    
    public synchronized T create(final Object... args) {
        synchronized (this.lock) {
            T result = null;
            int ctr = 0;
            while (ctr <= 1) {
                this.getConstructor();
                if (this.constructor == null) {
                    break;
                }
                try {
                    result = this.resultType.cast(this.constructor.newInstance(args));
                }
                catch (final Exception exc) {
                    this.constructor = null;
                    Logger.getLogger("com.sun.org.glassfish.gmbal.util").log(Level.WARNING, "Error invoking constructor", exc);
                    ++ctr;
                    continue;
                }
                break;
            }
            return result;
        }
    }
}

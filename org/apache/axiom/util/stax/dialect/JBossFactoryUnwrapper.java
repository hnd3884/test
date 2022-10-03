package org.apache.axiom.util.stax.dialect;

import org.apache.commons.logging.LogFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Field;
import org.apache.commons.logging.Log;

final class JBossFactoryUnwrapper
{
    private static final Log log;
    private final Class wrapperClass;
    private final Field actual;
    
    private JBossFactoryUnwrapper(final Class factoryType) throws Exception {
        this.wrapperClass = Class.forName("__redirected.__" + factoryType.getSimpleName());
        try {
            this.actual = this.wrapperClass.getDeclaredField("actual");
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                public Object run() {
                    JBossFactoryUnwrapper.this.actual.setAccessible(true);
                    return null;
                }
            });
        }
        catch (final Exception ex) {
            JBossFactoryUnwrapper.log.error((Object)("Found JBoss wrapper class for " + factoryType.getSimpleName() + ", but unwrapping is not supported"), (Throwable)ex);
            throw ex;
        }
    }
    
    static JBossFactoryUnwrapper create(final Class factoryType) {
        try {
            return new JBossFactoryUnwrapper(factoryType);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    Object unwrap(final Object factory) {
        if (this.wrapperClass.isInstance(factory)) {
            try {
                return this.actual.get(factory);
            }
            catch (final IllegalAccessException ex) {
                throw new IllegalAccessError(ex.getMessage());
            }
        }
        return factory;
    }
    
    static {
        log = LogFactory.getLog((Class)JBossFactoryUnwrapper.class);
    }
}

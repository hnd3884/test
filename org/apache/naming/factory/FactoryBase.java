package org.apache.naming.factory;

import javax.naming.RefAddr;
import javax.naming.NamingException;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import org.apache.naming.StringManager;
import javax.naming.spi.ObjectFactory;

public abstract class FactoryBase implements ObjectFactory
{
    private static final StringManager sm;
    
    @Override
    public final Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable<?, ?> environment) throws Exception {
        if (!this.isReferenceTypeSupported(obj)) {
            return null;
        }
        final Reference ref = (Reference)obj;
        final Object linked = this.getLinked(ref);
        if (linked != null) {
            return linked;
        }
        ObjectFactory factory = null;
        final RefAddr factoryRefAddr = ref.get("factory");
        if (factoryRefAddr != null) {
            final String factoryClassName = factoryRefAddr.getContent().toString();
            final ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            Class<?> factoryClass = null;
            try {
                if (tcl != null) {
                    factoryClass = tcl.loadClass(factoryClassName);
                }
                else {
                    factoryClass = Class.forName(factoryClassName);
                }
            }
            catch (final ClassNotFoundException e) {
                final NamingException ex = new NamingException(FactoryBase.sm.getString("factoryBase.factoryClassError"));
                ex.initCause(e);
                throw ex;
            }
            try {
                factory = (ObjectFactory)factoryClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (final Throwable t) {
                if (t instanceof NamingException) {
                    throw (NamingException)t;
                }
                if (t instanceof ThreadDeath) {
                    throw (ThreadDeath)t;
                }
                if (t instanceof VirtualMachineError) {
                    throw (VirtualMachineError)t;
                }
                final NamingException ex = new NamingException(FactoryBase.sm.getString("factoryBase.factoryCreationError"));
                ex.initCause(t);
                throw ex;
            }
        }
        else {
            factory = this.getDefaultFactory(ref);
        }
        if (factory != null) {
            return factory.getObjectInstance(obj, name, nameCtx, environment);
        }
        throw new NamingException(FactoryBase.sm.getString("factoryBase.instanceCreationError"));
    }
    
    protected abstract boolean isReferenceTypeSupported(final Object p0);
    
    protected abstract ObjectFactory getDefaultFactory(final Reference p0) throws NamingException;
    
    protected abstract Object getLinked(final Reference p0) throws NamingException;
    
    static {
        sm = StringManager.getManager(FactoryBase.class);
    }
}

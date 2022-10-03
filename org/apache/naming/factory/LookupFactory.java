package org.apache.naming.factory;

import java.util.HashSet;
import org.apache.juli.logging.LogFactory;
import javax.naming.RefAddr;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import org.apache.naming.LookupRef;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.util.Set;
import org.apache.naming.StringManager;
import org.apache.juli.logging.Log;
import javax.naming.spi.ObjectFactory;

public class LookupFactory implements ObjectFactory
{
    private static final Log log;
    private static final StringManager sm;
    private static final ThreadLocal<Set<String>> names;
    
    @Override
    public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable<?, ?> environment) throws Exception {
        String lookupName = null;
        Object result = null;
        if (obj instanceof LookupRef) {
            final Reference ref = (Reference)obj;
            ObjectFactory factory = null;
            final RefAddr lookupNameRefAddr = ref.get("lookup-name");
            if (lookupNameRefAddr != null) {
                lookupName = lookupNameRefAddr.getContent().toString();
            }
            try {
                if (lookupName != null && !LookupFactory.names.get().add(lookupName)) {
                    final String msg = LookupFactory.sm.getString("lookupFactory.circularReference", lookupName);
                    final NamingException ne = new NamingException(msg);
                    LookupFactory.log.warn((Object)msg, (Throwable)ne);
                    throw ne;
                }
                final RefAddr factoryRefAddr = ref.get("factory");
                if (factoryRefAddr != null) {
                    final String factoryClassName = factoryRefAddr.getContent().toString();
                    final ClassLoader tcl = Thread.currentThread().getContextClassLoader();
                    Class<?> factoryClass = null;
                    Label_0237: {
                        if (tcl != null) {
                            try {
                                factoryClass = tcl.loadClass(factoryClassName);
                                break Label_0237;
                            }
                            catch (final ClassNotFoundException e) {
                                final NamingException ex = new NamingException(LookupFactory.sm.getString("lookupFactory.loadFailed"));
                                ex.initCause(e);
                                throw ex;
                            }
                        }
                        try {
                            factoryClass = Class.forName(factoryClassName);
                        }
                        catch (final ClassNotFoundException e) {
                            final NamingException ex = new NamingException(LookupFactory.sm.getString("lookupFactory.loadFailed"));
                            ex.initCause(e);
                            throw ex;
                        }
                    }
                    if (factoryClass != null) {
                        try {
                            factory = (ObjectFactory)factoryClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                        }
                        catch (final Throwable t) {
                            if (t instanceof NamingException) {
                                throw (NamingException)t;
                            }
                            final NamingException ex = new NamingException(LookupFactory.sm.getString("lookupFactory.createFailed"));
                            ex.initCause(t);
                            throw ex;
                        }
                    }
                }
                if (factory != null) {
                    result = factory.getObjectInstance(obj, name, nameCtx, environment);
                }
                else {
                    if (lookupName == null) {
                        throw new NamingException(LookupFactory.sm.getString("lookupFactory.createFailed"));
                    }
                    result = new InitialContext().lookup(lookupName);
                }
                final Class<?> clazz = Class.forName(ref.getClassName());
                if (result != null && !clazz.isAssignableFrom(result.getClass())) {
                    final String msg2 = LookupFactory.sm.getString("lookupFactory.typeMismatch", name, ref.getClassName(), lookupName, result.getClass().getName());
                    final NamingException ne2 = new NamingException(msg2);
                    LookupFactory.log.warn((Object)msg2, (Throwable)ne2);
                    if (result instanceof AutoCloseable) {
                        try {
                            ((AutoCloseable)result).close();
                        }
                        catch (final Exception ex2) {}
                    }
                    throw ne2;
                }
            }
            finally {
                LookupFactory.names.get().remove(lookupName);
            }
        }
        return result;
    }
    
    static {
        log = LogFactory.getLog((Class)LookupFactory.class);
        sm = StringManager.getManager(LookupFactory.class);
        names = new ThreadLocal<Set<String>>() {
            @Override
            protected Set<String> initialValue() {
                return new HashSet<String>();
            }
        };
    }
}

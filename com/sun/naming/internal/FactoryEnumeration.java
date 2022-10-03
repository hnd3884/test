package com.sun.naming.internal;

import javax.naming.NamingException;
import java.util.List;

public final class FactoryEnumeration
{
    private List<NamedWeakReference<Object>> factories;
    private int posn;
    private ClassLoader loader;
    
    FactoryEnumeration(final List<NamedWeakReference<Object>> factories, final ClassLoader loader) {
        this.posn = 0;
        this.factories = factories;
        this.loader = loader;
    }
    
    public Object next() throws NamingException {
        synchronized (this.factories) {
            final NamedWeakReference namedWeakReference = this.factories.get(this.posn++);
            Object o = namedWeakReference.get();
            if (o != null && !(o instanceof Class)) {
                return o;
            }
            final String name = namedWeakReference.getName();
            try {
                if (o == null) {
                    o = Class.forName(name, true, this.loader);
                }
                o = ((Class<Object>)o).newInstance();
                this.factories.set(this.posn - 1, new NamedWeakReference<Object>(o, name));
                return o;
            }
            catch (final ClassNotFoundException rootCause) {
                final NamingException ex = new NamingException("No longer able to load " + name);
                ex.setRootCause(rootCause);
                throw ex;
            }
            catch (final InstantiationException rootCause2) {
                final NamingException ex2 = new NamingException("Cannot instantiate " + o);
                ex2.setRootCause(rootCause2);
                throw ex2;
            }
            catch (final IllegalAccessException rootCause3) {
                final NamingException ex3 = new NamingException("Cannot access " + o);
                ex3.setRootCause(rootCause3);
                throw ex3;
            }
        }
    }
    
    public boolean hasMore() {
        synchronized (this.factories) {
            return this.posn < this.factories.size();
        }
    }
}

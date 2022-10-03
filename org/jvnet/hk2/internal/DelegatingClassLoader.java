package org.jvnet.hk2.internal;

import org.glassfish.hk2.utilities.reflection.Pretty;
import java.net.URL;

class DelegatingClassLoader extends ClassLoader
{
    private final ClassLoader[] delegates;
    
    DelegatingClassLoader(final ClassLoader parent, final ClassLoader... classLoaderDelegates) {
        super(parent);
        this.delegates = classLoaderDelegates;
    }
    
    @Override
    public Class<?> loadClass(final String clazz) throws ClassNotFoundException {
        if (this.getParent() != null) {
            try {
                return this.getParent().loadClass(clazz);
            }
            catch (final ClassNotFoundException ex) {}
        }
        ClassNotFoundException firstFail = null;
        final ClassLoader[] delegates = this.delegates;
        final int length = delegates.length;
        int i = 0;
        while (i < length) {
            final ClassLoader delegate = delegates[i];
            try {
                return delegate.loadClass(clazz);
            }
            catch (final ClassNotFoundException ncfe) {
                if (firstFail == null) {
                    firstFail = ncfe;
                }
                ++i;
                continue;
            }
            break;
        }
        if (firstFail != null) {
            throw firstFail;
        }
        throw new ClassNotFoundException("Could not find " + clazz);
    }
    
    @Override
    public URL getResource(final String resource) {
        if (this.getParent() != null) {
            final URL u = this.getParent().getResource(resource);
            if (u != null) {
                return u;
            }
        }
        for (final ClassLoader delegate : this.delegates) {
            final URL u2 = delegate.getResource(resource);
            if (u2 != null) {
                return u2;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "DelegatingClassLoader(" + this.getParent() + "," + Pretty.array((Object[])this.delegates) + "," + System.identityHashCode(this) + ")";
    }
}

package org.apache.catalina.tribes.io;

import java.lang.reflect.Proxy;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;
import org.apache.catalina.tribes.util.StringManager;
import java.io.ObjectInputStream;

public final class ReplicationStream extends ObjectInputStream
{
    static final StringManager sm;
    private ClassLoader[] classLoaders;
    
    public ReplicationStream(final InputStream stream, final ClassLoader[] classLoaders) throws IOException {
        super(stream);
        this.classLoaders = null;
        this.classLoaders = classLoaders;
    }
    
    public Class<?> resolveClass(final ObjectStreamClass classDesc) throws ClassNotFoundException, IOException {
        final String name = classDesc.getName();
        try {
            return this.resolveClass(name);
        }
        catch (final ClassNotFoundException e) {
            return super.resolveClass(classDesc);
        }
    }
    
    public Class<?> resolveClass(final String name) throws ClassNotFoundException {
        final boolean tryRepFirst = name.startsWith("org.apache.catalina.tribes");
        try {
            if (tryRepFirst) {
                return this.findReplicationClass(name);
            }
            return this.findExternalClass(name);
        }
        catch (final Exception x) {
            if (tryRepFirst) {
                return this.findExternalClass(name);
            }
            return this.findReplicationClass(name);
        }
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] interfaces) throws IOException, ClassNotFoundException {
        ClassLoader latestLoader;
        if (this.classLoaders != null && this.classLoaders.length > 0) {
            latestLoader = this.classLoaders[0];
        }
        else {
            latestLoader = null;
        }
        ClassLoader nonPublicLoader = null;
        boolean hasNonPublicInterface = false;
        final Class<?>[] classObjs = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; ++i) {
            final Class<?> cl = this.resolveClass(interfaces[i]);
            if (latestLoader == null) {
                latestLoader = cl.getClassLoader();
            }
            if ((cl.getModifiers() & 0x1) == 0x0) {
                if (hasNonPublicInterface) {
                    if (nonPublicLoader != cl.getClassLoader()) {
                        throw new IllegalAccessError(ReplicationStream.sm.getString("replicationStream.conflict"));
                    }
                }
                else {
                    nonPublicLoader = cl.getClassLoader();
                    hasNonPublicInterface = true;
                }
            }
            classObjs[i] = cl;
        }
        try {
            return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : latestLoader, classObjs);
        }
        catch (final IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }
    
    public Class<?> findReplicationClass(final String name) throws ClassNotFoundException {
        final Class<?> clazz = Class.forName(name, false, this.getClass().getClassLoader());
        return clazz;
    }
    
    public Class<?> findExternalClass(final String name) throws ClassNotFoundException {
        ClassNotFoundException cnfe = null;
        final ClassLoader[] arr$ = this.classLoaders;
        final int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            final ClassLoader classLoader = arr$[i$];
            try {
                final Class<?> clazz = Class.forName(name, false, classLoader);
                return clazz;
            }
            catch (final ClassNotFoundException x) {
                cnfe = x;
                ++i$;
                continue;
            }
            break;
        }
        if (cnfe != null) {
            throw cnfe;
        }
        throw new ClassNotFoundException(name);
    }
    
    @Override
    public void close() throws IOException {
        this.classLoaders = null;
        super.close();
    }
    
    static {
        sm = StringManager.getManager(ReplicationStream.class);
    }
}

package org.glassfish.jersey.server.internal.scanning;

import java.util.NoSuchElementException;
import java.io.InputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Iterator;
import java.lang.reflect.Method;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.glassfish.jersey.server.ResourceFinder;
import java.net.URI;
import java.util.Set;

final class VfsSchemeResourceFinderFactory implements UriSchemeResourceFinderFactory
{
    private static final Set<String> SCHEMES;
    
    @Override
    public Set<String> getSchemes() {
        return VfsSchemeResourceFinderFactory.SCHEMES;
    }
    
    @Override
    public ResourceFinder create(final URI uri, final boolean recursive) {
        return new VfsResourceFinder(uri, recursive);
    }
    
    static {
        SCHEMES = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("vfsfile", "vfszip", "vfs")));
    }
    
    private static class VfsResourceFinder extends AbstractResourceFinderAdapter
    {
        private Object current;
        private Object next;
        private final Method openStream;
        private final Method getName;
        private final Method isLeaf;
        private final Iterator<?> iterator;
        
        public VfsResourceFinder(final URI uri, final boolean recursive) {
            final Object directory = this.bindDirectory(uri);
            this.openStream = this.bindMethod(directory, "openStream");
            this.getName = this.bindMethod(directory, "getName");
            this.isLeaf = this.bindMethod(directory, "isLeaf");
            this.iterator = this.getChildren(directory, recursive);
        }
        
        private Iterator<?> getChildren(final Object directory, final boolean recursive) {
            final Method getChildren = this.bindMethod(directory, recursive ? "getChildrenRecursively" : "getChildren");
            final List<?> list = this.invoke(directory, getChildren, List.class);
            if (list == null) {
                throw new ResourceFinderException("VFS object returned null when accessing children");
            }
            return list.iterator();
        }
        
        private Method bindMethod(final Object object, final String name) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Method>() {
                    @Override
                    public Method run() {
                        return VfsResourceFinder.this.bindMethod0(object, name);
                    }
                });
            }
            return this.bindMethod0(object, name);
        }
        
        private <T> T invoke(final Object instance, final Method method, final Class<T> type) {
            try {
                return type.cast(method.invoke(instance, new Object[0]));
            }
            catch (final Exception e) {
                throw new ResourceFinderException("VFS object could not be invoked upon");
            }
        }
        
        private Method bindMethod0(final Object object, final String name) {
            final Class<?> clazz = object.getClass();
            try {
                return clazz.getMethod(name, (Class<?>[])new Class[0]);
            }
            catch (final NoSuchMethodException e) {
                throw new ResourceFinderException("VFS object did not have a valid signature");
            }
        }
        
        private Object bindDirectory(final URI uri) {
            Object directory = null;
            try {
                directory = uri.toURL().getContent();
            }
            catch (final IOException ex) {}
            if (directory == null || !directory.getClass().getSimpleName().equals("VirtualFile")) {
                throw new ResourceFinderException("VFS URL did not map to a valid VFS object");
            }
            return directory;
        }
        
        @Override
        public InputStream open() {
            final Object current = this.current;
            if (current == null) {
                throw new IllegalStateException("next() must be called before open()");
            }
            return this.invoke(current, this.openStream, InputStream.class);
        }
        
        @Override
        public void reset() {
            throw new UnsupportedOperationException();
        }
        
        public boolean advance() {
            while (this.iterator.hasNext()) {
                final Object next = this.iterator.next();
                if (this.invoke(next, this.isLeaf, Boolean.class)) {
                    this.next = next;
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null || this.advance();
        }
        
        @Override
        public String next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.current = this.next;
            this.next = null;
            return this.invoke(this.current, this.getName, String.class);
        }
    }
}

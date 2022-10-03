package org.apache.tika.fork;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.Set;

class ClassLoaderProxy extends ClassLoader implements ForkProxy
{
    private static final long serialVersionUID = -7303109260448540420L;
    private final Set<String> notFound;
    private final int resource;
    private transient DataInputStream input;
    private transient DataOutputStream output;
    
    public ClassLoaderProxy(final int resource) {
        this.notFound = new HashSet<String>();
        this.resource = resource;
    }
    
    @Override
    public void init(final DataInputStream input, final DataOutputStream output) {
        this.input = input;
        this.output = output;
    }
    
    @Override
    protected synchronized URL findResource(final String name) {
        if (this.notFound.contains(name)) {
            return null;
        }
        try {
            this.output.write(3);
            this.output.write(this.resource);
            this.output.write(1);
            this.output.writeUTF(name);
            this.output.flush();
            if (this.input.readBoolean()) {
                return MemoryURLStreamHandler.createURL(this.readStream());
            }
            this.notFound.add(name);
            return null;
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    @Override
    protected synchronized Enumeration<URL> findResources(final String name) throws IOException {
        this.output.write(3);
        this.output.write(this.resource);
        this.output.write(2);
        this.output.writeUTF(name);
        this.output.flush();
        final List<URL> resources = new ArrayList<URL>();
        while (this.input.readBoolean()) {
            resources.add(MemoryURLStreamHandler.createURL(this.readStream()));
        }
        return Collections.enumeration(resources);
    }
    
    @Override
    protected synchronized Class<?> findClass(final String name) throws ClassNotFoundException {
        try {
            this.output.write(3);
            this.output.write(this.resource);
            this.output.write(1);
            this.output.writeUTF(name.replace('.', '/') + ".class");
            this.output.flush();
            if (this.input.readBoolean()) {
                final byte[] data = this.readStream();
                final Class<?> clazz = this.defineClass(name, data, 0, data.length);
                this.definePackageIfNecessary(name, clazz);
                return clazz;
            }
            throw new ClassNotFoundException("Unable to find class " + name);
        }
        catch (final IOException e) {
            throw new ClassNotFoundException("Unable to load class " + name, e);
        }
    }
    
    private void definePackageIfNecessary(final String className, final Class<?> clazz) {
        final String packageName = this.toPackageName(className);
        if (packageName != null && this.getPackage(packageName) == null) {
            this.definePackage(packageName, null, null, null, null, null, null, null);
        }
    }
    
    private String toPackageName(final String className) {
        final int packageEndIndex = className.lastIndexOf(46);
        if (packageEndIndex > 0) {
            return className.substring(0, packageEndIndex);
        }
        return null;
    }
    
    private byte[] readStream() throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final byte[] buffer = new byte[65535];
        int n;
        while ((n = this.input.readUnsignedShort()) > 0) {
            this.input.readFully(buffer, 0, n);
            stream.write(buffer, 0, n);
        }
        return stream.toByteArray();
    }
}

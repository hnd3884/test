package org.apache.tomcat.util.scan;

import java.util.jar.Manifest;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import org.apache.tomcat.Jar;

public class ReferenceCountedJar implements Jar
{
    private final URL url;
    private Jar wrappedJar;
    private int referenceCount;
    
    public ReferenceCountedJar(final URL url) throws IOException {
        this.referenceCount = 0;
        this.url = url;
        this.open();
    }
    
    private synchronized ReferenceCountedJar open() throws IOException {
        if (this.wrappedJar == null) {
            this.wrappedJar = JarFactory.newInstance(this.url);
        }
        ++this.referenceCount;
        return this;
    }
    
    public synchronized void close() {
        --this.referenceCount;
        if (this.referenceCount == 0) {
            this.wrappedJar.close();
            this.wrappedJar = null;
        }
    }
    
    public URL getJarFileURL() {
        return this.url;
    }
    
    public InputStream getInputStream(final String name) throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.getInputStream(name);
        }
    }
    
    public long getLastModified(final String name) throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.getLastModified(name);
        }
    }
    
    public boolean exists(final String name) throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.exists(name);
        }
    }
    
    public void nextEntry() {
        try (final ReferenceCountedJar jar = this.open()) {
            jar.wrappedJar.nextEntry();
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
    
    public String getEntryName() {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.getEntryName();
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
    
    public InputStream getEntryInputStream() throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.getEntryInputStream();
        }
    }
    
    public String getURL(final String entry) {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.getURL(entry);
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }
    
    public Manifest getManifest() throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.getManifest();
        }
    }
    
    public void reset() throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            jar.wrappedJar.reset();
        }
    }
    
    @Deprecated
    public boolean entryExists(final String name) throws IOException {
        try (final ReferenceCountedJar jar = this.open()) {
            return jar.wrappedJar.entryExists(name);
        }
    }
}

package org.glassfish.jersey.server.internal.scanning;

import java.util.NoSuchElementException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;
import org.glassfish.jersey.server.internal.AbstractResourceFinderAdapter;

public final class JarFileScanner extends AbstractResourceFinderAdapter
{
    private static final Logger LOGGER;
    private static final char JAR_FILE_SEPARATOR = '/';
    private final JarInputStream jarInputStream;
    private final String parent;
    private final boolean recursive;
    private JarEntry next;
    
    public JarFileScanner(final InputStream inputStream, final String parent, final boolean recursive) throws IOException {
        this.next = null;
        this.jarInputStream = new JarInputStream(inputStream);
        this.parent = ((parent.isEmpty() || parent.endsWith(String.valueOf('/'))) ? parent : (parent + '/'));
        this.recursive = recursive;
    }
    
    @Override
    public boolean hasNext() {
        if (this.next == null) {
            try {
                do {
                    this.next = this.jarInputStream.getNextJarEntry();
                    if (this.next == null) {
                        break;
                    }
                } while (this.next.isDirectory() || !this.next.getName().startsWith(this.parent) || (!this.recursive && this.next.getName().substring(this.parent.length()).indexOf(47) != -1));
            }
            catch (final IOException | SecurityException e) {
                JarFileScanner.LOGGER.log(Level.CONFIG, LocalizationMessages.JAR_SCANNER_UNABLE_TO_READ_ENTRY(), e);
                return false;
            }
        }
        if (this.next == null) {
            this.close();
            return false;
        }
        return true;
    }
    
    @Override
    public String next() {
        if (this.next != null || this.hasNext()) {
            final String name = this.next.getName();
            this.next = null;
            return name;
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public void reset() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public InputStream open() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return JarFileScanner.this.jarInputStream.read();
            }
            
            @Override
            public int read(final byte[] bytes) throws IOException {
                return JarFileScanner.this.jarInputStream.read(bytes);
            }
            
            @Override
            public int read(final byte[] bytes, final int i, final int i2) throws IOException {
                return JarFileScanner.this.jarInputStream.read(bytes, i, i2);
            }
            
            @Override
            public long skip(final long l) throws IOException {
                return JarFileScanner.this.jarInputStream.skip(l);
            }
            
            @Override
            public int available() throws IOException {
                return JarFileScanner.this.jarInputStream.available();
            }
            
            @Override
            public void close() throws IOException {
                JarFileScanner.this.jarInputStream.closeEntry();
            }
            
            @Override
            public synchronized void mark(final int i) {
                JarFileScanner.this.jarInputStream.mark(i);
            }
            
            @Override
            public synchronized void reset() throws IOException {
                JarFileScanner.this.jarInputStream.reset();
            }
            
            @Override
            public boolean markSupported() {
                return JarFileScanner.this.jarInputStream.markSupported();
            }
        };
    }
    
    @Override
    public void close() {
        try {
            this.jarInputStream.close();
        }
        catch (final IOException ioe) {
            JarFileScanner.LOGGER.log(Level.FINE, LocalizationMessages.JAR_SCANNER_UNABLE_TO_CLOSE_FILE(), ioe);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(JarFileScanner.class.getName());
    }
}

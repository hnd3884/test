package sun.misc;

import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import sun.nio.ByteBuffered;
import java.nio.ByteBuffer;
import java.io.EOFException;
import java.util.Arrays;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;

public abstract class Resource
{
    private InputStream cis;
    
    public abstract String getName();
    
    public abstract URL getURL();
    
    public abstract URL getCodeSourceURL();
    
    public abstract InputStream getInputStream() throws IOException;
    
    public abstract int getContentLength() throws IOException;
    
    private synchronized InputStream cachedInputStream() throws IOException {
        if (this.cis == null) {
            this.cis = this.getInputStream();
        }
        return this.cis;
    }
    
    public byte[] getBytes() throws IOException {
        final InputStream cachedInputStream = this.cachedInputStream();
        boolean interrupted = Thread.interrupted();
        int contentLength;
        while (true) {
            try {
                contentLength = this.getContentLength();
            }
            catch (final InterruptedIOException ex) {
                Thread.interrupted();
                interrupted = true;
                continue;
            }
            break;
        }
        byte[] array;
        try {
            array = new byte[0];
            if (contentLength == -1) {
                contentLength = Integer.MAX_VALUE;
            }
            int i = 0;
            while (i < contentLength) {
                int min;
                if (i >= array.length) {
                    min = Math.min(contentLength - i, array.length + 1024);
                    if (array.length < i + min) {
                        array = Arrays.copyOf(array, i + min);
                    }
                }
                else {
                    min = array.length - i;
                }
                int read = 0;
                try {
                    read = cachedInputStream.read(array, i, min);
                }
                catch (final InterruptedIOException ex2) {
                    Thread.interrupted();
                    interrupted = true;
                }
                if (read < 0) {
                    if (contentLength != Integer.MAX_VALUE) {
                        throw new EOFException("Detect premature EOF");
                    }
                    if (array.length != i) {
                        array = Arrays.copyOf(array, i);
                        break;
                    }
                    break;
                }
                else {
                    i += read;
                }
            }
        }
        finally {
            try {
                cachedInputStream.close();
            }
            catch (final InterruptedIOException ex3) {
                interrupted = true;
            }
            catch (final IOException ex4) {}
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        return array;
    }
    
    public ByteBuffer getByteBuffer() throws IOException {
        final InputStream cachedInputStream = this.cachedInputStream();
        if (cachedInputStream instanceof ByteBuffered) {
            return ((ByteBuffered)cachedInputStream).getByteBuffer();
        }
        return null;
    }
    
    public Manifest getManifest() throws IOException {
        return null;
    }
    
    public Certificate[] getCertificates() {
        return null;
    }
    
    public CodeSigner[] getCodeSigners() {
        return null;
    }
}

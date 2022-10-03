package org.jcp.xml.dsig.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.MessageDigest;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import java.io.OutputStream;

public class DigesterOutputStream extends OutputStream
{
    private boolean buffer;
    private UnsyncByteArrayOutputStream bos;
    private final MessageDigest md;
    private static Logger log;
    
    public DigesterOutputStream(final MessageDigest messageDigest) {
        this(messageDigest, false);
    }
    
    public DigesterOutputStream(final MessageDigest md, final boolean buffer) {
        this.buffer = false;
        this.md = md;
        this.buffer = buffer;
        if (buffer) {
            this.bos = new UnsyncByteArrayOutputStream();
        }
    }
    
    public void write(final byte[] array) {
        this.write(array, 0, array.length);
    }
    
    public void write(final int n) {
        if (this.buffer) {
            this.bos.write(n);
        }
        this.md.update((byte)n);
    }
    
    public void write(final byte[] array, final int n, final int n2) {
        if (this.buffer) {
            this.bos.write(array, n, n2);
        }
        if (DigesterOutputStream.log.isLoggable(Level.FINER)) {
            DigesterOutputStream.log.log(Level.FINER, "Pre-digested input:");
            final StringBuffer sb = new StringBuffer(n2);
            for (int i = n; i < n + n2; ++i) {
                sb.append((char)array[i]);
            }
            DigesterOutputStream.log.log(Level.FINER, sb.toString());
        }
        this.md.update(array, n, n2);
    }
    
    public byte[] getDigestValue() {
        return this.md.digest();
    }
    
    public InputStream getInputStream() {
        if (this.buffer) {
            return new ByteArrayInputStream(this.bos.toByteArray());
        }
        return null;
    }
    
    static {
        DigesterOutputStream.log = Logger.getLogger("org.jcp.xml.dsig.internal");
    }
}

package org.apache.axiom.om.util;

import org.apache.commons.logging.LogFactory;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import org.apache.axiom.attachments.impl.BufferUtils;
import org.apache.axiom.attachments.utils.BAAOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.axiom.attachments.utils.BAAInputStream;
import org.apache.commons.logging.Log;
import java.io.FilterInputStream;

public class DetachableInputStream extends FilterInputStream
{
    private static final Log log;
    private long count;
    BAAInputStream localStream;
    boolean isClosed;
    
    public DetachableInputStream(final InputStream in) {
        super(in);
        this.count = 0L;
        this.localStream = null;
        this.isClosed = false;
        this.count = 0L;
    }
    
    public long length() throws IOException {
        if (this.localStream == null) {
            this.detach();
        }
        return this.count;
    }
    
    public void detach() throws IOException {
        if (this.localStream == null && !this.isClosed) {
            final BAAOutputStream baaos = new BAAOutputStream();
            try {
                BufferUtils.inputStream2OutputStream(this.in, baaos);
                super.close();
            }
            catch (final Throwable t) {
                if (DetachableInputStream.log.isDebugEnabled()) {
                    DetachableInputStream.log.debug((Object)("detach caught exception.  Processing continues:" + t));
                    DetachableInputStream.log.debug((Object)("  " + stackToString(t)));
                }
            }
            finally {
                this.in = null;
            }
            this.localStream = new BAAInputStream(baaos.buffers(), baaos.length());
            if (DetachableInputStream.log.isDebugEnabled()) {
                DetachableInputStream.log.debug((Object)("The local stream built from the detached stream has a length of:" + baaos.length()));
            }
            this.count += baaos.length();
        }
    }
    
    @Override
    public int available() throws IOException {
        if (this.localStream != null) {
            return this.localStream.available();
        }
        return super.available();
    }
    
    @Override
    public void close() throws IOException {
        this.isClosed = true;
        if (this.localStream != null) {
            this.localStream.close();
        }
        else {
            super.close();
        }
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int read() throws IOException {
        if (this.localStream == null) {
            final int rc = super.read();
            if (rc != -1) {
                ++this.count;
            }
            return rc;
        }
        return this.localStream.read();
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.localStream == null) {
            final int rc = super.read(b, off, len);
            if (rc > 0) {
                this.count += rc;
            }
            return rc;
        }
        return this.localStream.read(b, off, len);
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        if (this.localStream == null) {
            final int rc = super.read(b);
            if (rc > 0) {
                this.count += rc;
            }
            return rc;
        }
        return this.localStream.read(b);
    }
    
    @Override
    public synchronized void reset() throws IOException {
        throw new IOException();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (this.localStream == null) {
            final long rc = super.skip(n);
            if (rc > 0L) {
                this.count += rc;
            }
            return rc;
        }
        return this.localStream.skip(n);
    }
    
    private static String stackToString(final Throwable e) {
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);
        final PrintWriter pw = new PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        final String text = sw.getBuffer().toString();
        return text;
    }
    
    static {
        log = LogFactory.getLog((Class)DetachableInputStream.class);
    }
}

package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;

class Wire
{
    public static Wire HEADER_WIRE;
    public static Wire CONTENT_WIRE;
    private Log log;
    
    private Wire(final Log log) {
        this.log = log;
    }
    
    private void wire(final String header, final InputStream instream) throws IOException {
        final StringBuffer buffer = new StringBuffer();
        int ch;
        while ((ch = instream.read()) != -1) {
            if (ch == 13) {
                buffer.append("[\\r]");
            }
            else if (ch == 10) {
                buffer.append("[\\n]\"");
                buffer.insert(0, "\"");
                buffer.insert(0, header);
                this.log.debug((Object)buffer.toString());
                buffer.setLength(0);
            }
            else if (ch < 32 || ch > 127) {
                buffer.append("[0x");
                buffer.append(Integer.toHexString(ch));
                buffer.append("]");
            }
            else {
                buffer.append((char)ch);
            }
        }
        if (buffer.length() > 0) {
            buffer.append("\"");
            buffer.insert(0, "\"");
            buffer.insert(0, header);
            this.log.debug((Object)buffer.toString());
        }
    }
    
    public boolean enabled() {
        return this.log.isDebugEnabled();
    }
    
    public void output(final InputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        this.wire(">> ", outstream);
    }
    
    public void input(final InputStream instream) throws IOException {
        if (instream == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        this.wire("<< ", instream);
    }
    
    public void output(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        this.wire(">> ", new ByteArrayInputStream(b, off, len));
    }
    
    public void input(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        this.wire("<< ", new ByteArrayInputStream(b, off, len));
    }
    
    public void output(final byte[] b) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        this.wire(">> ", new ByteArrayInputStream(b));
    }
    
    public void input(final byte[] b) throws IOException {
        if (b == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        this.wire("<< ", new ByteArrayInputStream(b));
    }
    
    public void output(final int b) throws IOException {
        this.output(new byte[] { (byte)b });
    }
    
    public void input(final int b) throws IOException {
        this.input(new byte[] { (byte)b });
    }
    
    public void output(final String s) throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Output may not be null");
        }
        this.output(s.getBytes());
    }
    
    public void input(final String s) throws IOException {
        if (s == null) {
            throw new IllegalArgumentException("Input may not be null");
        }
        this.input(s.getBytes());
    }
    
    static {
        Wire.HEADER_WIRE = new Wire(LogFactory.getLog("httpclient.wire.header"));
        Wire.CONTENT_WIRE = new Wire(LogFactory.getLog("httpclient.wire.content"));
    }
}

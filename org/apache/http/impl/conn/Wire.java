package org.apache.http.impl.conn;

import java.io.ByteArrayInputStream;
import org.apache.http.util.Args;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class Wire
{
    private final Log log;
    private final String id;
    
    public Wire(final Log log, final String id) {
        this.log = log;
        this.id = id;
    }
    
    public Wire(final Log log) {
        this(log, "");
    }
    
    private void wire(final String header, final InputStream inStream) throws IOException {
        final StringBuilder buffer = new StringBuilder();
        int ch;
        while ((ch = inStream.read()) != -1) {
            if (ch == 13) {
                buffer.append("[\\r]");
            }
            else if (ch == 10) {
                buffer.append("[\\n]\"");
                buffer.insert(0, "\"");
                buffer.insert(0, header);
                this.log.debug((Object)(this.id + " " + buffer.toString()));
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
            buffer.append('\"');
            buffer.insert(0, '\"');
            buffer.insert(0, header);
            this.log.debug((Object)(this.id + " " + buffer.toString()));
        }
    }
    
    public boolean enabled() {
        return this.log.isDebugEnabled();
    }
    
    public void output(final InputStream outStream) throws IOException {
        Args.notNull((Object)outStream, "Output");
        this.wire(">> ", outStream);
    }
    
    public void input(final InputStream inStream) throws IOException {
        Args.notNull((Object)inStream, "Input");
        this.wire("<< ", inStream);
    }
    
    public void output(final byte[] b, final int off, final int len) throws IOException {
        Args.notNull((Object)b, "Output");
        this.wire(">> ", new ByteArrayInputStream(b, off, len));
    }
    
    public void input(final byte[] b, final int off, final int len) throws IOException {
        Args.notNull((Object)b, "Input");
        this.wire("<< ", new ByteArrayInputStream(b, off, len));
    }
    
    public void output(final byte[] b) throws IOException {
        Args.notNull((Object)b, "Output");
        this.wire(">> ", new ByteArrayInputStream(b));
    }
    
    public void input(final byte[] b) throws IOException {
        Args.notNull((Object)b, "Input");
        this.wire("<< ", new ByteArrayInputStream(b));
    }
    
    public void output(final int b) throws IOException {
        this.output(new byte[] { (byte)b });
    }
    
    public void input(final int b) throws IOException {
        this.input(new byte[] { (byte)b });
    }
    
    public void output(final String s) throws IOException {
        Args.notNull((Object)s, "Output");
        this.output(s.getBytes());
    }
    
    public void input(final String s) throws IOException {
        Args.notNull((Object)s, "Input");
        this.input(s.getBytes());
    }
}

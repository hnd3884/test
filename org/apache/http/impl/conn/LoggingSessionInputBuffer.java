package org.apache.http.impl.conn;

import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.util.CharArrayBuffer;
import java.io.IOException;
import org.apache.http.Consts;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.io.EofSensor;
import org.apache.http.io.SessionInputBuffer;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class LoggingSessionInputBuffer implements SessionInputBuffer, EofSensor
{
    private final SessionInputBuffer in;
    private final EofSensor eofSensor;
    private final Wire wire;
    private final String charset;
    
    public LoggingSessionInputBuffer(final SessionInputBuffer in, final Wire wire, final String charset) {
        this.in = in;
        this.eofSensor = ((in instanceof EofSensor) ? in : null);
        this.wire = wire;
        this.charset = ((charset != null) ? charset : Consts.ASCII.name());
    }
    
    public LoggingSessionInputBuffer(final SessionInputBuffer in, final Wire wire) {
        this(in, wire, null);
    }
    
    public boolean isDataAvailable(final int timeout) throws IOException {
        return this.in.isDataAvailable(timeout);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int readLen = this.in.read(b, off, len);
        if (this.wire.enabled() && readLen > 0) {
            this.wire.input(b, off, readLen);
        }
        return readLen;
    }
    
    public int read() throws IOException {
        final int b = this.in.read();
        if (this.wire.enabled() && b != -1) {
            this.wire.input(b);
        }
        return b;
    }
    
    public int read(final byte[] b) throws IOException {
        final int readLen = this.in.read(b);
        if (this.wire.enabled() && readLen > 0) {
            this.wire.input(b, 0, readLen);
        }
        return readLen;
    }
    
    public String readLine() throws IOException {
        final String s = this.in.readLine();
        if (this.wire.enabled() && s != null) {
            final String tmp = s + "\r\n";
            this.wire.input(tmp.getBytes(this.charset));
        }
        return s;
    }
    
    public int readLine(final CharArrayBuffer buffer) throws IOException {
        final int readLen = this.in.readLine(buffer);
        if (this.wire.enabled() && readLen >= 0) {
            final int pos = buffer.length() - readLen;
            final String s = new String(buffer.buffer(), pos, readLen);
            final String tmp = s + "\r\n";
            this.wire.input(tmp.getBytes(this.charset));
        }
        return readLen;
    }
    
    public HttpTransportMetrics getMetrics() {
        return this.in.getMetrics();
    }
    
    public boolean isEof() {
        return this.eofSensor != null && this.eofSensor.isEof();
    }
}

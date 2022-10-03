package org.apache.axiom.om.util;

import java.io.IOException;
import org.apache.commons.logging.Log;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream
{
    private byte[] temp;
    private boolean isDebugEnabled;
    private long count;
    private Log log;
    private int BUFFER_LEN;
    private byte[] buffer;
    private int bufferIndex;
    private int limit;
    
    public LogOutputStream(final Log log, final int limit) {
        this.temp = new byte[1];
        this.isDebugEnabled = false;
        this.count = 0L;
        this.BUFFER_LEN = 4096;
        this.buffer = new byte[this.BUFFER_LEN];
        this.bufferIndex = 0;
        this.isDebugEnabled = log.isDebugEnabled();
        this.log = log;
        this.limit = limit;
    }
    
    public long getLength() {
        return this.count;
    }
    
    @Override
    public void close() throws IOException {
        if (this.bufferIndex > 0) {
            this.log.debug((Object)new String(this.buffer, 0, this.bufferIndex));
            this.bufferIndex = 0;
        }
        this.buffer = null;
        this.temp = null;
        this.log = null;
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void write(final byte[] b, final int off, int len) throws IOException {
        if (this.count >= this.limit) {
            this.count += len;
            return;
        }
        if (this.count + len >= this.limit) {
            this.count += len;
            len -= (int)(this.limit - this.count);
        }
        else {
            this.count += len;
        }
        if (this.isDebugEnabled) {
            if (len + this.bufferIndex < this.BUFFER_LEN) {
                System.arraycopy(b, off, this.buffer, this.bufferIndex, len);
                this.bufferIndex += len;
            }
            else {
                if (this.bufferIndex > 0) {
                    this.log.debug((Object)new String(this.buffer, 0, this.bufferIndex));
                    this.bufferIndex = 0;
                }
                if (len + this.bufferIndex < this.BUFFER_LEN) {
                    System.arraycopy(b, off, this.buffer, this.bufferIndex, len);
                    this.bufferIndex += len;
                }
                else {
                    this.log.debug((Object)new String(b, off, len));
                }
            }
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.temp[0] = (byte)b;
        this.write(this.temp, 0, 1);
    }
}

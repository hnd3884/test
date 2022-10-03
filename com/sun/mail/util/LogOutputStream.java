package com.sun.mail.util;

import java.io.IOException;
import java.util.logging.Level;
import java.io.OutputStream;

public class LogOutputStream extends OutputStream
{
    protected MailLogger logger;
    protected Level level;
    private int lastb;
    private byte[] buf;
    private int pos;
    
    public LogOutputStream(final MailLogger logger) {
        this.lastb = -1;
        this.buf = new byte[80];
        this.pos = 0;
        this.logger = logger;
        this.level = Level.FINEST;
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (!this.logger.isLoggable(this.level)) {
            return;
        }
        if (b == 13) {
            this.logBuf();
        }
        else if (b == 10) {
            if (this.lastb != 13) {
                this.logBuf();
            }
        }
        else {
            this.expandCapacity(1);
            this.buf[this.pos++] = (byte)b;
        }
        this.lastb = b;
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, int len) throws IOException {
        int start = off;
        if (!this.logger.isLoggable(this.level)) {
            return;
        }
        len += off;
        for (int i = start; i < len; ++i) {
            if (b[i] == 13) {
                this.expandCapacity(i - start);
                System.arraycopy(b, start, this.buf, this.pos, i - start);
                this.pos += i - start;
                this.logBuf();
                start = i + 1;
            }
            else if (b[i] == 10) {
                if (this.lastb != 13) {
                    this.expandCapacity(i - start);
                    System.arraycopy(b, start, this.buf, this.pos, i - start);
                    this.pos += i - start;
                    this.logBuf();
                }
                start = i + 1;
            }
            this.lastb = b[i];
        }
        if (len - start > 0) {
            this.expandCapacity(len - start);
            System.arraycopy(b, start, this.buf, this.pos, len - start);
            this.pos += len - start;
        }
    }
    
    protected void log(final String msg) {
        this.logger.log(this.level, msg);
    }
    
    private void logBuf() {
        final String msg = new String(this.buf, 0, this.pos);
        this.pos = 0;
        this.log(msg);
    }
    
    private void expandCapacity(final int len) {
        while (this.pos + len > this.buf.length) {
            final byte[] nb = new byte[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nb, 0, this.pos);
            this.buf = nb;
        }
    }
}

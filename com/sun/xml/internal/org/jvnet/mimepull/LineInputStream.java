package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.io.InputStream;
import java.io.FilterInputStream;

final class LineInputStream extends FilterInputStream
{
    private char[] lineBuffer;
    private static int MAX_INCR;
    
    public LineInputStream(final InputStream in) {
        super(in);
        this.lineBuffer = null;
    }
    
    public String readLine() throws IOException {
        char[] buf = this.lineBuffer;
        if (buf == null) {
            final char[] lineBuffer = new char[128];
            this.lineBuffer = lineBuffer;
            buf = lineBuffer;
        }
        int room = buf.length;
        int offset = 0;
        int c1;
        while ((c1 = this.in.read()) != -1) {
            if (c1 == 10) {
                break;
            }
            if (c1 == 13) {
                boolean twoCRs = false;
                if (this.in.markSupported()) {
                    this.in.mark(2);
                }
                int c2 = this.in.read();
                if (c2 == 13) {
                    twoCRs = true;
                    c2 = this.in.read();
                }
                if (c2 == 10) {
                    break;
                }
                if (this.in.markSupported()) {
                    this.in.reset();
                    break;
                }
                if (!(this.in instanceof PushbackInputStream)) {
                    this.in = new PushbackInputStream(this.in, 2);
                }
                if (c2 != -1) {
                    ((PushbackInputStream)this.in).unread(c2);
                }
                if (twoCRs) {
                    ((PushbackInputStream)this.in).unread(13);
                    break;
                }
                break;
            }
            else {
                if (--room < 0) {
                    if (buf.length < LineInputStream.MAX_INCR) {
                        buf = new char[buf.length * 2];
                    }
                    else {
                        buf = new char[buf.length + LineInputStream.MAX_INCR];
                    }
                    room = buf.length - offset - 1;
                    System.arraycopy(this.lineBuffer, 0, buf, 0, offset);
                    this.lineBuffer = buf;
                }
                buf[offset++] = (char)c1;
            }
        }
        if (c1 == -1 && offset == 0) {
            return null;
        }
        return String.copyValueOf(buf, 0, offset);
    }
    
    static {
        LineInputStream.MAX_INCR = 1048576;
    }
}

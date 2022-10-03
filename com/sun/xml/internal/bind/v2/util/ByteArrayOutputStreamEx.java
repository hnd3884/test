package com.sun.xml.internal.bind.v2.util;

import java.io.IOException;
import java.io.InputStream;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import java.io.ByteArrayOutputStream;

public final class ByteArrayOutputStreamEx extends ByteArrayOutputStream
{
    public ByteArrayOutputStreamEx() {
    }
    
    public ByteArrayOutputStreamEx(final int size) {
        super(size);
    }
    
    public void set(final Base64Data dt, final String mimeType) {
        dt.set(this.buf, this.count, mimeType);
    }
    
    public byte[] getBuffer() {
        return this.buf;
    }
    
    public void readFrom(final InputStream is) throws IOException {
        while (true) {
            if (this.count == this.buf.length) {
                final byte[] data = new byte[this.buf.length * 2];
                System.arraycopy(this.buf, 0, data, 0, this.buf.length);
                this.buf = data;
            }
            final int sz = is.read(this.buf, this.count, this.buf.length - this.count);
            if (sz < 0) {
                break;
            }
            this.count += sz;
        }
    }
}

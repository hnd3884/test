package com.sun.mail.util;

import javax.mail.util.SharedByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

public class SharedByteArrayOutputStream extends ByteArrayOutputStream
{
    public SharedByteArrayOutputStream(final int size) {
        super(size);
    }
    
    public InputStream toStream() {
        return new SharedByteArrayInputStream(this.buf, 0, this.count);
    }
}

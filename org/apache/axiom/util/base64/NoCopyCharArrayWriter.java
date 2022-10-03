package org.apache.axiom.util.base64;

import java.io.CharArrayWriter;

class NoCopyCharArrayWriter extends CharArrayWriter
{
    NoCopyCharArrayWriter(final int expectedSize) {
        super(expectedSize);
    }
    
    @Override
    public char[] toCharArray() {
        return (this.count == this.buf.length) ? this.buf : super.toCharArray();
    }
}

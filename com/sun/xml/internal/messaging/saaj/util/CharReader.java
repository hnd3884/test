package com.sun.xml.internal.messaging.saaj.util;

import java.io.CharArrayReader;

public class CharReader extends CharArrayReader
{
    public CharReader(final char[] buf, final int length) {
        super(buf, 0, length);
    }
    
    public CharReader(final char[] buf, final int offset, final int length) {
        super(buf, offset, length);
    }
    
    public char[] getChars() {
        return this.buf;
    }
    
    public int getCount() {
        return this.count;
    }
}

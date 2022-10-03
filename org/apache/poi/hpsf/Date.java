package org.apache.poi.hpsf;

import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.util.Internal;

@Internal
public class Date
{
    private static final int SIZE = 8;
    private final byte[] _value;
    
    public Date() {
        this._value = new byte[8];
    }
    
    public void read(final LittleEndianByteArrayInputStream lei) {
        lei.readFully(this._value);
    }
}

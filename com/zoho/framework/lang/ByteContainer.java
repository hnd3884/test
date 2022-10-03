package com.zoho.framework.lang;

import java.nio.ByteBuffer;

public class ByteContainer
{
    private static final byte[] ESCAPE_BYTE;
    ByteBuffer buff;
    
    public ByteContainer(final byte... bytes) {
        (this.buff = ByteBuffer.wrap(new byte[5])).put(ByteContainer.ESCAPE_BYTE);
        this.buff.put(bytes);
    }
    
    public byte[] getBytes() {
        return this.buff.array();
    }
    
    static {
        ESCAPE_BYTE = new byte[] { 92, 92 };
    }
}

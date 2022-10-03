package com.sun.security.jgss;

import sun.misc.HexDumpEncoder;
import jdk.Exported;

@Exported
public final class AuthorizationDataEntry
{
    private final int type;
    private final byte[] data;
    
    public AuthorizationDataEntry(final int type, final byte[] array) {
        this.type = type;
        this.data = array.clone();
    }
    
    public int getType() {
        return this.type;
    }
    
    public byte[] getData() {
        return this.data.clone();
    }
    
    @Override
    public String toString() {
        return "AuthorizationDataEntry: type=" + this.type + ", data=" + this.data.length + " bytes:\n" + new HexDumpEncoder().encodeBuffer(this.data);
    }
}

package com.maverick.ssh2;

public class GlobalRequest
{
    String b;
    byte[] c;
    
    public GlobalRequest(final String b, final byte[] c) {
        this.b = b;
        this.c = c;
    }
    
    public String getName() {
        return this.b;
    }
    
    public byte[] getData() {
        return this.c;
    }
    
    public void setData(final byte[] c) {
        this.c = c;
    }
}

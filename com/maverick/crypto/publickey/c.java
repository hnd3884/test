package com.maverick.crypto.publickey;

class c
{
    private byte[] c;
    private int b;
    
    public c(final byte[] c, final int b) {
        this.c = c;
        this.b = b;
    }
    
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof c)) {
            return false;
        }
        final c c = (c)o;
        if (c.b != this.b) {
            return false;
        }
        if (c.c.length != this.c.length) {
            return false;
        }
        for (int i = 0; i != c.c.length; ++i) {
            if (c.c[i] != this.c[i]) {
                return false;
            }
        }
        return true;
    }
}

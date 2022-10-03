package com.maverick.crypto.publickey;

import java.math.BigInteger;

class b
{
    private BigInteger c;
    private BigInteger d;
    private BigInteger e;
    private c b;
    
    public b(final BigInteger e, final BigInteger d, final BigInteger c, final c b) {
        this.c = c;
        this.e = e;
        this.d = d;
        this.b = b;
    }
    
    public BigInteger c() {
        return this.e;
    }
    
    public BigInteger b() {
        return this.d;
    }
    
    public BigInteger d() {
        return this.c;
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof b)) {
            return false;
        }
        final b b = (b)o;
        return b.c().equals(this.e) && b.b().equals(this.d) && b.d().equals(this.c);
    }
}

package org.bouncycastle.crypto.params;

public class GOST3410ValidationParameters
{
    private int x0;
    private int c;
    private long x0L;
    private long cL;
    
    public GOST3410ValidationParameters(final int x0, final int c) {
        this.x0 = x0;
        this.c = c;
    }
    
    public GOST3410ValidationParameters(final long x0L, final long cl) {
        this.x0L = x0L;
        this.cL = cl;
    }
    
    public int getC() {
        return this.c;
    }
    
    public int getX0() {
        return this.x0;
    }
    
    public long getCL() {
        return this.cL;
    }
    
    public long getX0L() {
        return this.x0L;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GOST3410ValidationParameters)) {
            return false;
        }
        final GOST3410ValidationParameters gost3410ValidationParameters = (GOST3410ValidationParameters)o;
        return gost3410ValidationParameters.c == this.c && gost3410ValidationParameters.x0 == this.x0 && gost3410ValidationParameters.cL == this.cL && gost3410ValidationParameters.x0L == this.x0L;
    }
    
    @Override
    public int hashCode() {
        return this.x0 ^ this.c ^ (int)this.x0L ^ (int)(this.x0L >> 32) ^ (int)this.cL ^ (int)(this.cL >> 32);
    }
}

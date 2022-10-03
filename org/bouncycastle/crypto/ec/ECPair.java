package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECPoint;

public class ECPair
{
    private final ECPoint x;
    private final ECPoint y;
    
    public ECPair(final ECPoint x, final ECPoint y) {
        this.x = x;
        this.y = y;
    }
    
    public ECPoint getX() {
        return this.x;
    }
    
    public ECPoint getY() {
        return this.y;
    }
    
    public boolean equals(final ECPair ecPair) {
        return ecPair.getX().equals(this.getX()) && ecPair.getY().equals(this.getY());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ECPair && this.equals((ECPair)o);
    }
    
    @Override
    public int hashCode() {
        return this.x.hashCode() + 37 * this.y.hashCode();
    }
}

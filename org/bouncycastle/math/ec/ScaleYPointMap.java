package org.bouncycastle.math.ec;

public class ScaleYPointMap implements ECPointMap
{
    protected final ECFieldElement scale;
    
    public ScaleYPointMap(final ECFieldElement scale) {
        this.scale = scale;
    }
    
    public ECPoint map(final ECPoint ecPoint) {
        return ecPoint.scaleY(this.scale);
    }
}

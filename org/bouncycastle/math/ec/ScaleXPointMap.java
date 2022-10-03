package org.bouncycastle.math.ec;

public class ScaleXPointMap implements ECPointMap
{
    protected final ECFieldElement scale;
    
    public ScaleXPointMap(final ECFieldElement scale) {
        this.scale = scale;
    }
    
    public ECPoint map(final ECPoint ecPoint) {
        return ecPoint.scaleX(this.scale);
    }
}

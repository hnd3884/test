package sun.java2d.marlin;

import sun.awt.geom.PathConsumer2D;

final class CollinearSimplifier implements PathConsumer2D
{
    static final float EPS = 1.0E-4f;
    PathConsumer2D delegate;
    SimplifierState state;
    float px1;
    float py1;
    float px2;
    float py2;
    float pslope;
    
    public CollinearSimplifier init(final PathConsumer2D delegate) {
        this.delegate = delegate;
        this.state = SimplifierState.Empty;
        return this;
    }
    
    @Override
    public void pathDone() {
        this.emitStashedLine();
        this.state = SimplifierState.Empty;
        this.delegate.pathDone();
    }
    
    @Override
    public void closePath() {
        this.emitStashedLine();
        this.state = SimplifierState.Empty;
        this.delegate.closePath();
    }
    
    @Override
    public long getNativeConsumer() {
        return 0L;
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float px1, final float py1) {
        this.emitStashedLine();
        this.delegate.quadTo(n, n2, px1, py1);
        this.state = SimplifierState.PreviousPoint;
        this.px1 = px1;
        this.py1 = py1;
    }
    
    @Override
    public void curveTo(final float n, final float n2, final float n3, final float n4, final float px1, final float py1) {
        this.emitStashedLine();
        this.delegate.curveTo(n, n2, n3, n4, px1, py1);
        this.state = SimplifierState.PreviousPoint;
        this.px1 = px1;
        this.py1 = py1;
    }
    
    @Override
    public void moveTo(final float px1, final float py1) {
        this.emitStashedLine();
        this.delegate.moveTo(px1, py1);
        this.state = SimplifierState.PreviousPoint;
        this.px1 = px1;
        this.py1 = py1;
    }
    
    @Override
    public void lineTo(final float n, final float n2) {
        switch (this.state) {
            case Empty: {
                this.delegate.lineTo(n, n2);
                this.state = SimplifierState.PreviousPoint;
                this.px1 = n;
                this.py1 = n2;
                return;
            }
            case PreviousPoint: {
                this.state = SimplifierState.PreviousLine;
                this.px2 = n;
                this.py2 = n2;
                this.pslope = getSlope(this.px1, this.py1, n, n2);
                return;
            }
            case PreviousLine: {
                final float slope = getSlope(this.px2, this.py2, n, n2);
                if (slope == this.pslope || Math.abs(this.pslope - slope) < 1.0E-4f) {
                    this.px2 = n;
                    this.py2 = n2;
                    return;
                }
                this.delegate.lineTo(this.px2, this.py2);
                this.px1 = this.px2;
                this.py1 = this.py2;
                this.px2 = n;
                this.py2 = n2;
                this.pslope = slope;
            }
            default: {}
        }
    }
    
    private void emitStashedLine() {
        if (this.state == SimplifierState.PreviousLine) {
            this.delegate.lineTo(this.px2, this.py2);
        }
    }
    
    private static float getSlope(final float n, final float n2, final float n3, final float n4) {
        final float n5 = n4 - n2;
        if (n5 == 0.0f) {
            return (n3 > n) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        return (n3 - n) / n5;
    }
    
    enum SimplifierState
    {
        Empty, 
        PreviousPoint, 
        PreviousLine;
    }
}

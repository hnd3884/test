package sun.java2d.marlin;

final class DCollinearSimplifier implements DPathConsumer2D
{
    static final double EPS = 1.0E-4;
    DPathConsumer2D delegate;
    SimplifierState state;
    double px1;
    double py1;
    double px2;
    double py2;
    double pslope;
    
    public DCollinearSimplifier init(final DPathConsumer2D delegate) {
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
    public void quadTo(final double n, final double n2, final double px1, final double py1) {
        this.emitStashedLine();
        this.delegate.quadTo(n, n2, px1, py1);
        this.state = SimplifierState.PreviousPoint;
        this.px1 = px1;
        this.py1 = py1;
    }
    
    @Override
    public void curveTo(final double n, final double n2, final double n3, final double n4, final double px1, final double py1) {
        this.emitStashedLine();
        this.delegate.curveTo(n, n2, n3, n4, px1, py1);
        this.state = SimplifierState.PreviousPoint;
        this.px1 = px1;
        this.py1 = py1;
    }
    
    @Override
    public void moveTo(final double px1, final double py1) {
        this.emitStashedLine();
        this.delegate.moveTo(px1, py1);
        this.state = SimplifierState.PreviousPoint;
        this.px1 = px1;
        this.py1 = py1;
    }
    
    @Override
    public void lineTo(final double n, final double n2) {
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
                final double slope = getSlope(this.px2, this.py2, n, n2);
                if (slope == this.pslope || Math.abs(this.pslope - slope) < 1.0E-4) {
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
    
    private static double getSlope(final double n, final double n2, final double n3, final double n4) {
        final double n5 = n4 - n2;
        if (n5 == 0.0) {
            return (n3 > n) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
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

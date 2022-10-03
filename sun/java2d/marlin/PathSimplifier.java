package sun.java2d.marlin;

import sun.awt.geom.PathConsumer2D;

final class PathSimplifier implements PathConsumer2D
{
    private static final float PIX_THRESHOLD;
    private static final float SQUARE_TOLERANCE;
    private PathConsumer2D delegate;
    private float cx;
    private float cy;
    
    PathSimplifier init(final PathConsumer2D delegate) {
        this.delegate = delegate;
        return this;
    }
    
    @Override
    public void pathDone() {
        this.delegate.pathDone();
    }
    
    @Override
    public void closePath() {
        this.delegate.closePath();
    }
    
    @Override
    public long getNativeConsumer() {
        return 0L;
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float cx, final float cy) {
        final float n3 = cx - this.cx;
        final float n4 = cy - this.cy;
        if (n3 * n3 + n4 * n4 <= PathSimplifier.SQUARE_TOLERANCE) {
            final float n5 = n - this.cx;
            final float n6 = n2 - this.cy;
            if (n5 * n5 + n6 * n6 <= PathSimplifier.SQUARE_TOLERANCE) {
                return;
            }
        }
        this.delegate.quadTo(n, n2, cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    @Override
    public void curveTo(final float n, final float n2, final float n3, final float n4, final float cx, final float cy) {
        final float n5 = cx - this.cx;
        final float n6 = cy - this.cy;
        if (n5 * n5 + n6 * n6 <= PathSimplifier.SQUARE_TOLERANCE) {
            final float n7 = n - this.cx;
            final float n8 = n2 - this.cy;
            if (n7 * n7 + n8 * n8 <= PathSimplifier.SQUARE_TOLERANCE) {
                final float n9 = n3 - this.cx;
                final float n10 = n4 - this.cy;
                if (n9 * n9 + n10 * n10 <= PathSimplifier.SQUARE_TOLERANCE) {
                    return;
                }
            }
        }
        this.delegate.curveTo(n, n2, n3, n4, cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    @Override
    public void moveTo(final float cx, final float cy) {
        this.delegate.moveTo(cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    @Override
    public void lineTo(final float cx, final float cy) {
        final float n = cx - this.cx;
        final float n2 = cy - this.cy;
        if (n * n + n2 * n2 <= PathSimplifier.SQUARE_TOLERANCE) {
            return;
        }
        this.delegate.lineTo(cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    static {
        PIX_THRESHOLD = MarlinProperties.getPathSimplifierPixelTolerance();
        SQUARE_TOLERANCE = PathSimplifier.PIX_THRESHOLD * PathSimplifier.PIX_THRESHOLD;
    }
}

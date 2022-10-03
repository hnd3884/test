package sun.java2d.marlin;

final class DPathSimplifier implements DPathConsumer2D
{
    private static final double PIX_THRESHOLD;
    private static final double SQUARE_TOLERANCE;
    private DPathConsumer2D delegate;
    private double cx;
    private double cy;
    
    DPathSimplifier init(final DPathConsumer2D delegate) {
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
    public void quadTo(final double n, final double n2, final double cx, final double cy) {
        final double n3 = cx - this.cx;
        final double n4 = cy - this.cy;
        if (n3 * n3 + n4 * n4 <= DPathSimplifier.SQUARE_TOLERANCE) {
            final double n5 = n - this.cx;
            final double n6 = n2 - this.cy;
            if (n5 * n5 + n6 * n6 <= DPathSimplifier.SQUARE_TOLERANCE) {
                return;
            }
        }
        this.delegate.quadTo(n, n2, cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    @Override
    public void curveTo(final double n, final double n2, final double n3, final double n4, final double cx, final double cy) {
        final double n5 = cx - this.cx;
        final double n6 = cy - this.cy;
        if (n5 * n5 + n6 * n6 <= DPathSimplifier.SQUARE_TOLERANCE) {
            final double n7 = n - this.cx;
            final double n8 = n2 - this.cy;
            if (n7 * n7 + n8 * n8 <= DPathSimplifier.SQUARE_TOLERANCE) {
                final double n9 = n3 - this.cx;
                final double n10 = n4 - this.cy;
                if (n9 * n9 + n10 * n10 <= DPathSimplifier.SQUARE_TOLERANCE) {
                    return;
                }
            }
        }
        this.delegate.curveTo(n, n2, n3, n4, cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    @Override
    public void moveTo(final double cx, final double cy) {
        this.delegate.moveTo(cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    @Override
    public void lineTo(final double cx, final double cy) {
        final double n = cx - this.cx;
        final double n2 = cy - this.cy;
        if (n * n + n2 * n2 <= DPathSimplifier.SQUARE_TOLERANCE) {
            return;
        }
        this.delegate.lineTo(cx, cy);
        this.cx = cx;
        this.cy = cy;
    }
    
    static {
        PIX_THRESHOLD = MarlinProperties.getPathSimplifierPixelTolerance();
        SQUARE_TOLERANCE = DPathSimplifier.PIX_THRESHOLD * DPathSimplifier.PIX_THRESHOLD;
    }
}

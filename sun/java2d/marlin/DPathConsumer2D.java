package sun.java2d.marlin;

public interface DPathConsumer2D
{
    void moveTo(final double p0, final double p1);
    
    void lineTo(final double p0, final double p1);
    
    void quadTo(final double p0, final double p1, final double p2, final double p3);
    
    void curveTo(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5);
    
    void closePath();
    
    void pathDone();
    
    long getNativeConsumer();
}

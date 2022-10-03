package sun.awt.geom;

public interface PathConsumer2D
{
    void moveTo(final float p0, final float p1);
    
    void lineTo(final float p0, final float p1);
    
    void quadTo(final float p0, final float p1, final float p2, final float p3);
    
    void curveTo(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    void closePath();
    
    void pathDone();
    
    long getNativeConsumer();
}

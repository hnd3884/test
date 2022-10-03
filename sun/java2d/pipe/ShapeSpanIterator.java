package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.geom.PathIterator;
import sun.awt.geom.PathConsumer2D;

public final class ShapeSpanIterator implements SpanIterator, PathConsumer2D
{
    long pData;
    
    public static native void initIDs();
    
    public ShapeSpanIterator(final boolean normalize) {
        this.setNormalize(normalize);
    }
    
    public void appendPath(final PathIterator pathIterator) {
        final float[] array = new float[6];
        this.setRule(pathIterator.getWindingRule());
        while (!pathIterator.isDone()) {
            this.addSegment(pathIterator.currentSegment(array), array);
            pathIterator.next();
        }
        this.pathDone();
    }
    
    public native void appendPoly(final int[] p0, final int[] p1, final int p2, final int p3, final int p4);
    
    private native void setNormalize(final boolean p0);
    
    public void setOutputAreaXYWH(final int n, final int n2, final int n3, final int n4) {
        this.setOutputAreaXYXY(n, n2, Region.dimAdd(n, n3), Region.dimAdd(n2, n4));
    }
    
    public native void setOutputAreaXYXY(final int p0, final int p1, final int p2, final int p3);
    
    public void setOutputArea(final Rectangle rectangle) {
        this.setOutputAreaXYWH(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public void setOutputArea(final Region region) {
        this.setOutputAreaXYXY(region.lox, region.loy, region.hix, region.hiy);
    }
    
    public native void setRule(final int p0);
    
    public native void addSegment(final int p0, final float[] p1);
    
    @Override
    public native void getPathBox(final int[] p0);
    
    @Override
    public native void intersectClipBox(final int p0, final int p1, final int p2, final int p3);
    
    @Override
    public native boolean nextSpan(final int[] p0);
    
    @Override
    public native void skipDownTo(final int p0);
    
    @Override
    public native long getNativeIterator();
    
    public native void dispose();
    
    @Override
    public native void moveTo(final float p0, final float p1);
    
    @Override
    public native void lineTo(final float p0, final float p1);
    
    @Override
    public native void quadTo(final float p0, final float p1, final float p2, final float p3);
    
    @Override
    public native void curveTo(final float p0, final float p1, final float p2, final float p3, final float p4, final float p5);
    
    @Override
    public native void closePath();
    
    @Override
    public native void pathDone();
    
    @Override
    public native long getNativeConsumer();
    
    static {
        initIDs();
    }
}

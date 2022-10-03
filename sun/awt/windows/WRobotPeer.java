package sun.awt.windows;

import java.awt.Rectangle;
import java.awt.GraphicsDevice;
import java.awt.peer.RobotPeer;

final class WRobotPeer extends WObjectPeer implements RobotPeer
{
    WRobotPeer() {
        this.create();
    }
    
    WRobotPeer(final GraphicsDevice graphicsDevice) {
        this.create();
    }
    
    private synchronized native void _dispose();
    
    @Override
    protected void disposeImpl() {
        this._dispose();
    }
    
    public native void create();
    
    public native void mouseMoveImpl(final int p0, final int p1);
    
    @Override
    public void mouseMove(final int n, final int n2) {
        this.mouseMoveImpl(n, n2);
    }
    
    @Override
    public native void mousePress(final int p0);
    
    @Override
    public native void mouseRelease(final int p0);
    
    @Override
    public native void mouseWheel(final int p0);
    
    @Override
    public native void keyPress(final int p0);
    
    @Override
    public native void keyRelease(final int p0);
    
    @Override
    public int getRGBPixel(final int n, final int n2) {
        return this.getRGBPixels(new Rectangle(n, n2, 1, 1))[0];
    }
    
    @Override
    public int[] getRGBPixels(final Rectangle rectangle) {
        final int[] array = new int[rectangle.width * rectangle.height];
        this.getRGBPixels(rectangle.x, rectangle.y, rectangle.width, rectangle.height, array);
        return array;
    }
    
    private native void getRGBPixels(final int p0, final int p1, final int p2, final int p3, final int[] p4);
}

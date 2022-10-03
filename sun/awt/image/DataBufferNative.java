package sun.awt.image;

import sun.java2d.SurfaceData;
import java.awt.image.DataBuffer;

public class DataBufferNative extends DataBuffer
{
    protected SurfaceData surfaceData;
    protected int width;
    
    public DataBufferNative(final SurfaceData surfaceData, final int n, final int width, final int n2) {
        super(n, width * n2);
        this.width = width;
        this.surfaceData = surfaceData;
    }
    
    protected native int getElem(final int p0, final int p1, final SurfaceData p2);
    
    @Override
    public int getElem(final int n, final int n2) {
        return this.getElem(n2 % this.width, n2 / this.width, this.surfaceData);
    }
    
    protected native void setElem(final int p0, final int p1, final int p2, final SurfaceData p3);
    
    @Override
    public void setElem(final int n, final int n2, final int n3) {
        this.setElem(n2 % this.width, n2 / this.width, n3, this.surfaceData);
    }
}

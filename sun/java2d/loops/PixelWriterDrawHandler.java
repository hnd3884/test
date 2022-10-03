package sun.java2d.loops;

import sun.java2d.pipe.Region;
import sun.java2d.SurfaceData;

class PixelWriterDrawHandler extends ProcessPath.DrawHandler
{
    PixelWriter pw;
    SurfaceData sData;
    Region clip;
    
    public PixelWriterDrawHandler(final SurfaceData sData, final PixelWriter pw, final Region clip, final int n) {
        super(clip.getLoX(), clip.getLoY(), clip.getHiX(), clip.getHiY(), n);
        this.sData = sData;
        this.pw = pw;
        this.clip = clip;
    }
    
    @Override
    public void drawLine(final int n, final int n2, final int n3, final int n4) {
        GeneralRenderer.doDrawLine(this.sData, this.pw, null, this.clip, n, n2, n3, n4);
    }
    
    @Override
    public void drawPixel(final int n, final int n2) {
        GeneralRenderer.doSetRect(this.sData, this.pw, n, n2, n + 1, n2 + 1);
    }
    
    @Override
    public void drawScanline(final int n, final int n2, final int n3) {
        GeneralRenderer.doSetRect(this.sData, this.pw, n, n3, n2 + 1, n3 + 1);
    }
}

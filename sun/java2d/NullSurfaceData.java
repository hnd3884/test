package sun.java2d;

import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.GraphicsConfiguration;
import java.awt.image.ColorModel;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.NullPipe;

public class NullSurfaceData extends SurfaceData
{
    public static final SurfaceData theInstance;
    private static final NullPipe nullpipe;
    
    private NullSurfaceData() {
        super(StateTrackable.State.IMMUTABLE, SurfaceType.Any, ColorModel.getRGBdefault());
    }
    
    @Override
    public void invalidate() {
    }
    
    @Override
    public SurfaceData getReplacement() {
        return this;
    }
    
    @Override
    public void validatePipe(final SunGraphics2D sunGraphics2D) {
        sunGraphics2D.drawpipe = NullSurfaceData.nullpipe;
        sunGraphics2D.fillpipe = NullSurfaceData.nullpipe;
        sunGraphics2D.shapepipe = NullSurfaceData.nullpipe;
        sunGraphics2D.textpipe = NullSurfaceData.nullpipe;
        sunGraphics2D.imagepipe = NullSurfaceData.nullpipe;
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }
    
    @Override
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        throw new InvalidPipeException("should be NOP");
    }
    
    @Override
    public boolean useTightBBoxes() {
        return false;
    }
    
    @Override
    public int pixelFor(final int n) {
        return n;
    }
    
    @Override
    public int rgbFor(final int n) {
        return n;
    }
    
    @Override
    public Rectangle getBounds() {
        return new Rectangle();
    }
    
    @Override
    protected void checkCustomComposite() {
    }
    
    @Override
    public boolean copyArea(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        return true;
    }
    
    @Override
    public Object getDestination() {
        return null;
    }
    
    static {
        theInstance = new NullSurfaceData();
        nullpipe = new NullPipe();
    }
}

package sun.java2d.loops;

import sun.java2d.SunCompositeContext;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import sun.java2d.SurfaceData;
import java.awt.Color;
import java.awt.Composite;

public final class XORComposite implements Composite
{
    Color xorColor;
    int xorPixel;
    int alphaMask;
    
    public XORComposite(final Color xorColor, final SurfaceData surfaceData) {
        this.xorColor = xorColor;
        final SurfaceType surfaceType = surfaceData.getSurfaceType();
        this.xorPixel = surfaceData.pixelFor(xorColor.getRGB());
        this.alphaMask = surfaceType.getAlphaMask();
    }
    
    public Color getXorColor() {
        return this.xorColor;
    }
    
    public int getXorPixel() {
        return this.xorPixel;
    }
    
    public int getAlphaMask() {
        return this.alphaMask;
    }
    
    @Override
    public CompositeContext createContext(final ColorModel colorModel, final ColorModel colorModel2, final RenderingHints renderingHints) {
        return new SunCompositeContext(this, colorModel, colorModel2);
    }
}

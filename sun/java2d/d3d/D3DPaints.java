package sun.java2d.d3d;

import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import sun.java2d.SurfaceData;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Image;
import sun.java2d.loops.CompositeType;
import java.awt.TexturePaint;
import java.util.HashMap;
import sun.java2d.SunGraphics2D;
import java.util.Map;

abstract class D3DPaints
{
    private static Map<Integer, D3DPaints> impls;
    
    static boolean isValid(final SunGraphics2D sunGraphics2D) {
        final D3DPaints d3DPaints = D3DPaints.impls.get(sunGraphics2D.paintState);
        return d3DPaints != null && d3DPaints.isPaintValid(sunGraphics2D);
    }
    
    abstract boolean isPaintValid(final SunGraphics2D p0);
    
    static {
        (D3DPaints.impls = new HashMap<Integer, D3DPaints>(4, 1.0f)).put(2, new Gradient());
        D3DPaints.impls.put(3, new LinearGradient());
        D3DPaints.impls.put(4, new RadialGradient());
        D3DPaints.impls.put(5, new Texture());
    }
    
    private static class Gradient extends D3DPaints
    {
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            return ((D3DGraphicsDevice)((D3DSurfaceData)sunGraphics2D.surfaceData).getDeviceConfiguration().getDevice()).isCapPresent(65536);
        }
    }
    
    private static class Texture extends D3DPaints
    {
        public boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            final TexturePaint texturePaint = (TexturePaint)sunGraphics2D.paint;
            final D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)sunGraphics2D.surfaceData;
            final BufferedImage image = texturePaint.getImage();
            final D3DGraphicsDevice d3DGraphicsDevice = (D3DGraphicsDevice)d3DSurfaceData.getDeviceConfiguration().getDevice();
            final int width = image.getWidth();
            final int height = image.getHeight();
            if (!d3DGraphicsDevice.isCapPresent(32) && ((width & width - 1) != 0x0 || (height & height - 1) != 0x0)) {
                return false;
            }
            if (!d3DGraphicsDevice.isCapPresent(64) && width != height) {
                return false;
            }
            SurfaceData surfaceData = d3DSurfaceData.getSourceSurfaceData(image, 0, CompositeType.SrcOver, null);
            if (!(surfaceData instanceof D3DSurfaceData)) {
                surfaceData = d3DSurfaceData.getSourceSurfaceData(image, 0, CompositeType.SrcOver, null);
                if (!(surfaceData instanceof D3DSurfaceData)) {
                    return false;
                }
            }
            return ((D3DSurfaceData)surfaceData).getType() == 3;
        }
    }
    
    private abstract static class MultiGradient extends D3DPaints
    {
        public static final int MULTI_MAX_FRACTIONS_D3D = 8;
        
        protected MultiGradient() {
        }
        
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            return ((MultipleGradientPaint)sunGraphics2D.paint).getFractions().length <= 8 && ((D3DGraphicsDevice)((D3DSurfaceData)sunGraphics2D.surfaceData).getDeviceConfiguration().getDevice()).isCapPresent(65536);
        }
    }
    
    private static class LinearGradient extends MultiGradient
    {
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            final LinearGradientPaint linearGradientPaint = (LinearGradientPaint)sunGraphics2D.paint;
            return (linearGradientPaint.getFractions().length == 2 && linearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT && linearGradientPaint.getColorSpace() != MultipleGradientPaint.ColorSpaceType.LINEAR_RGB && ((D3DGraphicsDevice)((D3DSurfaceData)sunGraphics2D.surfaceData).getDeviceConfiguration().getDevice()).isCapPresent(65536)) || super.isPaintValid(sunGraphics2D);
        }
    }
    
    private static class RadialGradient extends MultiGradient
    {
    }
}

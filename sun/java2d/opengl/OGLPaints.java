package sun.java2d.opengl;

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

abstract class OGLPaints
{
    private static Map<Integer, OGLPaints> impls;
    
    static boolean isValid(final SunGraphics2D sunGraphics2D) {
        final OGLPaints oglPaints = OGLPaints.impls.get(sunGraphics2D.paintState);
        return oglPaints != null && oglPaints.isPaintValid(sunGraphics2D);
    }
    
    abstract boolean isPaintValid(final SunGraphics2D p0);
    
    static {
        (OGLPaints.impls = new HashMap<Integer, OGLPaints>(4, 1.0f)).put(2, new Gradient());
        OGLPaints.impls.put(3, new LinearGradient());
        OGLPaints.impls.put(4, new RadialGradient());
        OGLPaints.impls.put(5, new Texture());
    }
    
    private static class Gradient extends OGLPaints
    {
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            return true;
        }
    }
    
    private static class Texture extends OGLPaints
    {
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            final TexturePaint texturePaint = (TexturePaint)sunGraphics2D.paint;
            final OGLSurfaceData oglSurfaceData = (OGLSurfaceData)sunGraphics2D.surfaceData;
            final BufferedImage image = texturePaint.getImage();
            if (!oglSurfaceData.isTexNonPow2Available()) {
                final int width = image.getWidth();
                final int height = image.getHeight();
                if ((width & width - 1) != 0x0 || (height & height - 1) != 0x0) {
                    return false;
                }
            }
            SurfaceData surfaceData = oglSurfaceData.getSourceSurfaceData(image, 0, CompositeType.SrcOver, null);
            if (!(surfaceData instanceof OGLSurfaceData)) {
                surfaceData = oglSurfaceData.getSourceSurfaceData(image, 0, CompositeType.SrcOver, null);
                if (!(surfaceData instanceof OGLSurfaceData)) {
                    return false;
                }
            }
            return ((OGLSurfaceData)surfaceData).getType() == 3;
        }
    }
    
    private abstract static class MultiGradient extends OGLPaints
    {
        protected MultiGradient() {
        }
        
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            return ((MultipleGradientPaint)sunGraphics2D.paint).getFractions().length <= 12 && ((OGLSurfaceData)sunGraphics2D.surfaceData).getOGLGraphicsConfig().isCapPresent(524288);
        }
    }
    
    private static class LinearGradient extends MultiGradient
    {
        @Override
        boolean isPaintValid(final SunGraphics2D sunGraphics2D) {
            final LinearGradientPaint linearGradientPaint = (LinearGradientPaint)sunGraphics2D.paint;
            return (linearGradientPaint.getFractions().length == 2 && linearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT && linearGradientPaint.getColorSpace() != MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) || super.isPaintValid(sunGraphics2D);
        }
    }
    
    private static class RadialGradient extends MultiGradient
    {
    }
}

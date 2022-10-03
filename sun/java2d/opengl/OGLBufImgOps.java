package sun.java2d.opengl;

import sun.java2d.SurfaceData;
import java.awt.Color;
import java.awt.Image;
import sun.java2d.loops.CompositeType;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.awt.image.ConvolveOp;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.BufferedBufImgOps;

class OGLBufImgOps extends BufferedBufImgOps
{
    static boolean renderImageWithOp(final SunGraphics2D sunGraphics2D, final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (bufferedImageOp instanceof ConvolveOp) {
            if (!BufferedBufImgOps.isConvolveOpValid((ConvolveOp)bufferedImageOp)) {
                return false;
            }
        }
        else if (bufferedImageOp instanceof RescaleOp) {
            if (!BufferedBufImgOps.isRescaleOpValid((RescaleOp)bufferedImageOp, bufferedImage)) {
                return false;
            }
        }
        else {
            if (!(bufferedImageOp instanceof LookupOp)) {
                return false;
            }
            if (!BufferedBufImgOps.isLookupOpValid((LookupOp)bufferedImageOp, bufferedImage)) {
                return false;
            }
        }
        final SurfaceData surfaceData = sunGraphics2D.surfaceData;
        if (!(surfaceData instanceof OGLSurfaceData) || sunGraphics2D.interpolationType == 3 || sunGraphics2D.compositeState > 1) {
            return false;
        }
        SurfaceData surfaceData2 = surfaceData.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
        if (!(surfaceData2 instanceof OGLSurfaceData)) {
            surfaceData2 = surfaceData.getSourceSurfaceData(bufferedImage, 0, CompositeType.SrcOver, null);
            if (!(surfaceData2 instanceof OGLSurfaceData)) {
                return false;
            }
        }
        final OGLSurfaceData oglSurfaceData = (OGLSurfaceData)surfaceData2;
        final OGLGraphicsConfig oglGraphicsConfig = oglSurfaceData.getOGLGraphicsConfig();
        if (oglSurfaceData.getType() != 3 || !oglGraphicsConfig.isCapPresent(262144)) {
            return false;
        }
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        OGLBlitLoops.IsoBlit(surfaceData2, surfaceData, bufferedImage, bufferedImageOp, sunGraphics2D.composite, sunGraphics2D.getCompClip(), sunGraphics2D.transform, sunGraphics2D.interpolationType, 0, 0, width, height, n, n2, n + width, n2 + height, true);
        return true;
    }
}

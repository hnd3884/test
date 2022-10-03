package sun.java2d.opengl;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;
import sun.java2d.loops.TransformBlit;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.Image;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.DrawImage;

public class OGLDrawImage extends DrawImage
{
    @Override
    protected void renderImageXform(final SunGraphics2D sunGraphics2D, final Image image, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final Color color) {
        if (n != 3) {
            final SurfaceData surfaceData = sunGraphics2D.surfaceData;
            final SurfaceData sourceSurfaceData = surfaceData.getSourceSurfaceData(image, 4, sunGraphics2D.imageComp, color);
            if (sourceSurfaceData != null && !DrawImage.isBgOperation(sourceSurfaceData, color) && (sourceSurfaceData.getSurfaceType() == OGLSurfaceData.OpenGLTexture || sourceSurfaceData.getSurfaceType() == OGLSurfaceData.OpenGLSurfaceRTT || n == 1)) {
                final TransformBlit fromCache = TransformBlit.getFromCache(sourceSurfaceData.getSurfaceType(), sunGraphics2D.imageComp, surfaceData.getSurfaceType());
                if (fromCache != null) {
                    fromCache.Transform(sourceSurfaceData, surfaceData, sunGraphics2D.composite, sunGraphics2D.getCompClip(), affineTransform, n, n2, n3, 0, 0, n4 - n2, n5 - n3);
                    return;
                }
            }
        }
        super.renderImageXform(sunGraphics2D, image, affineTransform, n, n2, n3, n4, n5, color);
    }
    
    @Override
    public void transformImage(final SunGraphics2D sunGraphics2D, BufferedImage filter, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (bufferedImageOp != null) {
            if (bufferedImageOp instanceof AffineTransformOp) {
                final AffineTransformOp affineTransformOp = (AffineTransformOp)bufferedImageOp;
                this.transformImage(sunGraphics2D, filter, n, n2, affineTransformOp.getTransform(), affineTransformOp.getInterpolationType());
                return;
            }
            if (OGLBufImgOps.renderImageWithOp(sunGraphics2D, filter, bufferedImageOp, n, n2)) {
                return;
            }
            filter = bufferedImageOp.filter(filter, null);
        }
        this.copyImage(sunGraphics2D, filter, n, n2, null);
    }
}

package sun.java2d.opengl;

import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.TransformBlit;

class OGLTextureToSurfaceTransform extends TransformBlit
{
    OGLTextureToSurfaceTransform() {
        super(OGLSurfaceData.OpenGLTexture, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
    }
    
    @Override
    public void Transform(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        OGLBlitLoops.IsoBlit(surfaceData, surfaceData2, null, null, composite, region, affineTransform, n, n2, n3, n2 + n6, n3 + n7, n4, n5, n4 + n6, n5 + n7, true);
    }
}

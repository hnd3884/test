package sun.java2d.opengl;

import sun.java2d.pipe.BufferedBufImgOps;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.SunGraphics2D;
import java.awt.Paint;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.pipe.RenderBuffer;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.TransformBlit;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.loops.SurfaceType;

final class OGLBlitLoops
{
    private static final int OFFSET_SRCTYPE = 16;
    private static final int OFFSET_HINT = 8;
    private static final int OFFSET_TEXTURE = 3;
    private static final int OFFSET_RTT = 2;
    private static final int OFFSET_XFORM = 1;
    private static final int OFFSET_ISOBLIT = 0;
    
    static void register() {
        final OGLSwToSurfaceBlit oglSwToSurfaceBlit = new OGLSwToSurfaceBlit(SurfaceType.IntArgbPre, 1);
        final OGLSwToTextureBlit oglSwToTextureBlit = new OGLSwToTextureBlit(SurfaceType.IntArgbPre, 1);
        final OGLSwToSurfaceTransform oglSwToSurfaceTransform = new OGLSwToSurfaceTransform(SurfaceType.IntArgbPre, 1);
        final OGLSurfaceToSwBlit oglSurfaceToSwBlit = new OGLSurfaceToSwBlit(SurfaceType.IntArgbPre, 1);
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new OGLSurfaceToSurfaceBlit(), new OGLSurfaceToSurfaceScale(), new OGLSurfaceToSurfaceTransform(), new OGLRTTSurfaceToSurfaceBlit(), new OGLRTTSurfaceToSurfaceScale(), new OGLRTTSurfaceToSurfaceTransform(), new OGLSurfaceToSwBlit(SurfaceType.IntArgb, 0), oglSurfaceToSwBlit, oglSwToSurfaceBlit, new OGLSwToSurfaceBlit(SurfaceType.IntRgb, 2), new OGLSwToSurfaceBlit(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceBlit(SurfaceType.IntBgr, 4), new OGLSwToSurfaceBlit(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceBlit(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceBlit(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceBlit(SurfaceType.ByteGray, 9), new OGLSwToSurfaceBlit(SurfaceType.UshortGray, 10), new OGLGeneralBlit(OGLSurfaceData.OpenGLSurface, CompositeType.AnyAlpha, oglSwToSurfaceBlit), new OGLAnyCompositeBlit(OGLSurfaceData.OpenGLSurface, oglSurfaceToSwBlit, oglSurfaceToSwBlit, oglSwToSurfaceBlit), new OGLAnyCompositeBlit(SurfaceType.Any, null, oglSurfaceToSwBlit, oglSwToSurfaceBlit), new OGLSwToSurfaceScale(SurfaceType.IntRgb, 2), new OGLSwToSurfaceScale(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceScale(SurfaceType.IntBgr, 4), new OGLSwToSurfaceScale(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceScale(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceScale(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceScale(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceScale(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceScale(SurfaceType.ByteGray, 9), new OGLSwToSurfaceScale(SurfaceType.UshortGray, 10), new OGLSwToSurfaceScale(SurfaceType.IntArgbPre, 1), new OGLSwToSurfaceTransform(SurfaceType.IntRgb, 2), new OGLSwToSurfaceTransform(SurfaceType.IntRgbx, 3), new OGLSwToSurfaceTransform(SurfaceType.IntBgr, 4), new OGLSwToSurfaceTransform(SurfaceType.IntBgrx, 5), new OGLSwToSurfaceTransform(SurfaceType.ThreeByteBgr, 11), new OGLSwToSurfaceTransform(SurfaceType.Ushort565Rgb, 6), new OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgb, 7), new OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgbx, 8), new OGLSwToSurfaceTransform(SurfaceType.ByteGray, 9), new OGLSwToSurfaceTransform(SurfaceType.UshortGray, 10), oglSwToSurfaceTransform, new OGLGeneralTransformedBlit(oglSwToSurfaceTransform), new OGLTextureToSurfaceBlit(), new OGLTextureToSurfaceScale(), new OGLTextureToSurfaceTransform(), oglSwToTextureBlit, new OGLSwToTextureBlit(SurfaceType.IntRgb, 2), new OGLSwToTextureBlit(SurfaceType.IntRgbx, 3), new OGLSwToTextureBlit(SurfaceType.IntBgr, 4), new OGLSwToTextureBlit(SurfaceType.IntBgrx, 5), new OGLSwToTextureBlit(SurfaceType.ThreeByteBgr, 11), new OGLSwToTextureBlit(SurfaceType.Ushort565Rgb, 6), new OGLSwToTextureBlit(SurfaceType.Ushort555Rgb, 7), new OGLSwToTextureBlit(SurfaceType.Ushort555Rgbx, 8), new OGLSwToTextureBlit(SurfaceType.ByteGray, 9), new OGLSwToTextureBlit(SurfaceType.UshortGray, 10), new OGLGeneralBlit(OGLSurfaceData.OpenGLTexture, CompositeType.SrcNoEa, oglSwToTextureBlit) });
    }
    
    private static int createPackedParams(final boolean b, final boolean b2, final boolean b3, final boolean b4, final int n, final int n2) {
        return n2 << 16 | n << 8 | (b2 ? 1 : 0) << 3 | (b3 ? 1 : 0) << 2 | (b4 ? 1 : 0) << 1 | (b ? 1 : 0) << 0;
    }
    
    private static void enqueueBlit(final RenderQueue renderQueue, final SurfaceData surfaceData, final SurfaceData surfaceData2, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final double n8, final double n9) {
        final RenderBuffer buffer = renderQueue.getBuffer();
        renderQueue.ensureCapacityAndAlignment(72, 24);
        buffer.putInt(31);
        buffer.putInt(n);
        buffer.putInt(n2).putInt(n3);
        buffer.putInt(n4).putInt(n5);
        buffer.putDouble(n6).putDouble(n7);
        buffer.putDouble(n8).putDouble(n9);
        buffer.putLong(surfaceData.getNativeOps());
        buffer.putLong(surfaceData2.getNativeOps());
    }
    
    static void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final double n8, final double n9, final int n10, final boolean b) {
        int n11 = 0;
        if (surfaceData.getTransparency() == 1) {
            n11 |= 0x1;
        }
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            instance.addReference(surfaceData);
            final OGLSurfaceData oglSurfaceData = (OGLSurfaceData)surfaceData2;
            if (b) {
                OGLContext.setScratchSurface(oglSurfaceData.getOGLGraphicsConfig());
            }
            else {
                BufferedContext.validateContext(oglSurfaceData, oglSurfaceData, region, composite, affineTransform, null, null, n11);
            }
            enqueueBlit(instance, surfaceData, surfaceData2, createPackedParams(false, b, false, affineTransform != null, n, n10), n2, n3, n4, n5, n6, n7, n8, n9);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
    }
    
    static void IsoBlit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final double n8, final double n9, final boolean b) {
        int n10 = 0;
        if (surfaceData.getTransparency() == 1) {
            n10 |= 0x1;
        }
        final OGLRenderQueue instance = OGLRenderQueue.getInstance();
        instance.lock();
        try {
            final OGLSurfaceData oglSurfaceData = (OGLSurfaceData)surfaceData;
            final OGLSurfaceData oglSurfaceData2 = (OGLSurfaceData)surfaceData2;
            final int type = oglSurfaceData.getType();
            boolean b2;
            OGLSurfaceData oglSurfaceData3;
            if (type == 3) {
                b2 = false;
                oglSurfaceData3 = oglSurfaceData2;
            }
            else {
                b2 = true;
                if (type == 5) {
                    oglSurfaceData3 = oglSurfaceData2;
                }
                else {
                    oglSurfaceData3 = oglSurfaceData;
                }
            }
            BufferedContext.validateContext(oglSurfaceData3, oglSurfaceData2, region, composite, affineTransform, null, null, n10);
            if (bufferedImageOp != null) {
                BufferedBufImgOps.enableBufImgOp(instance, oglSurfaceData, bufferedImage, bufferedImageOp);
            }
            enqueueBlit(instance, surfaceData, surfaceData2, createPackedParams(true, b, b2, affineTransform != null, n, 0), n2, n3, n4, n5, n6, n7, n8, n9);
            if (bufferedImageOp != null) {
                BufferedBufImgOps.disableBufImgOp(instance, bufferedImageOp);
            }
            if (b2 && oglSurfaceData2.isOnScreen()) {
                instance.flushNow();
            }
        }
        finally {
            instance.unlock();
        }
    }
}

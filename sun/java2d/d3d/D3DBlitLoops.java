package sun.java2d.d3d;

import sun.java2d.pipe.BufferedBufImgOps;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.ScreenUpdateManager;
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

final class D3DBlitLoops
{
    private static final int OFFSET_SRCTYPE = 16;
    private static final int OFFSET_HINT = 8;
    private static final int OFFSET_TEXTURE = 3;
    private static final int OFFSET_RTT = 2;
    private static final int OFFSET_XFORM = 1;
    private static final int OFFSET_ISOBLIT = 0;
    
    static void register() {
        final D3DSwToSurfaceBlit d3DSwToSurfaceBlit = new D3DSwToSurfaceBlit(SurfaceType.IntArgbPre, 1);
        final D3DSwToTextureBlit d3DSwToTextureBlit = new D3DSwToTextureBlit(SurfaceType.IntArgbPre, 1);
        final D3DSwToSurfaceTransform d3DSwToSurfaceTransform = new D3DSwToSurfaceTransform(SurfaceType.IntArgbPre, 1);
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new D3DSurfaceToGDIWindowSurfaceBlit(), new D3DSurfaceToGDIWindowSurfaceScale(), new D3DSurfaceToGDIWindowSurfaceTransform(), new D3DSurfaceToSurfaceBlit(), new D3DSurfaceToSurfaceScale(), new D3DSurfaceToSurfaceTransform(), new D3DRTTSurfaceToSurfaceBlit(), new D3DRTTSurfaceToSurfaceScale(), new D3DRTTSurfaceToSurfaceTransform(), new D3DSurfaceToSwBlit(SurfaceType.IntArgb, 0), d3DSwToSurfaceBlit, new D3DSwToSurfaceBlit(SurfaceType.IntArgb, 0), new D3DSwToSurfaceBlit(SurfaceType.IntRgb, 3), new D3DSwToSurfaceBlit(SurfaceType.IntBgr, 4), new D3DSwToSurfaceBlit(SurfaceType.ThreeByteBgr, 9), new D3DSwToSurfaceBlit(SurfaceType.Ushort565Rgb, 5), new D3DSwToSurfaceBlit(SurfaceType.Ushort555Rgb, 6), new D3DSwToSurfaceBlit(SurfaceType.ByteIndexed, 7), new D3DGeneralBlit(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, d3DSwToSurfaceBlit), new D3DSwToSurfaceScale(SurfaceType.IntArgb, 0), new D3DSwToSurfaceScale(SurfaceType.IntArgbPre, 1), new D3DSwToSurfaceScale(SurfaceType.IntRgb, 3), new D3DSwToSurfaceScale(SurfaceType.IntBgr, 4), new D3DSwToSurfaceScale(SurfaceType.ThreeByteBgr, 9), new D3DSwToSurfaceScale(SurfaceType.Ushort565Rgb, 5), new D3DSwToSurfaceScale(SurfaceType.Ushort555Rgb, 6), new D3DSwToSurfaceScale(SurfaceType.ByteIndexed, 7), new D3DSwToSurfaceTransform(SurfaceType.IntArgb, 0), new D3DSwToSurfaceTransform(SurfaceType.IntRgb, 3), new D3DSwToSurfaceTransform(SurfaceType.IntBgr, 4), new D3DSwToSurfaceTransform(SurfaceType.ThreeByteBgr, 9), new D3DSwToSurfaceTransform(SurfaceType.Ushort565Rgb, 5), new D3DSwToSurfaceTransform(SurfaceType.Ushort555Rgb, 6), new D3DSwToSurfaceTransform(SurfaceType.ByteIndexed, 7), d3DSwToSurfaceTransform, new D3DGeneralTransformedBlit(d3DSwToSurfaceTransform), new D3DTextureToSurfaceBlit(), new D3DTextureToSurfaceScale(), new D3DTextureToSurfaceTransform(), d3DSwToTextureBlit, new D3DSwToTextureBlit(SurfaceType.IntRgb, 3), new D3DSwToTextureBlit(SurfaceType.IntArgb, 0), new D3DSwToTextureBlit(SurfaceType.IntBgr, 4), new D3DSwToTextureBlit(SurfaceType.ThreeByteBgr, 9), new D3DSwToTextureBlit(SurfaceType.Ushort565Rgb, 5), new D3DSwToTextureBlit(SurfaceType.Ushort555Rgb, 6), new D3DSwToTextureBlit(SurfaceType.ByteIndexed, 7), new D3DGeneralBlit(D3DSurfaceData.D3DTexture, CompositeType.SrcNoEa, d3DSwToTextureBlit) });
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
        final D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)surfaceData2;
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        instance.lock();
        try {
            instance.addReference(surfaceData);
            if (b) {
                D3DContext.setScratchSurface(d3DSurfaceData.getContext());
            }
            else {
                BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, region, composite, affineTransform, null, null, n11);
            }
            enqueueBlit(instance, surfaceData, surfaceData2, createPackedParams(false, b, false, affineTransform != null, n, n10), n2, n3, n4, n5, n6, n7, n8, n9);
            instance.flushNow();
        }
        finally {
            instance.unlock();
        }
        if (d3DSurfaceData.getType() == 1) {
            ((D3DScreenUpdateManager)ScreenUpdateManager.getInstance()).runUpdateNow();
        }
    }
    
    static void IsoBlit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final BufferedImage bufferedImage, final BufferedImageOp bufferedImageOp, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final double n8, final double n9, final boolean b) {
        int n10 = 0;
        if (surfaceData.getTransparency() == 1) {
            n10 |= 0x1;
        }
        final D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)surfaceData2;
        final D3DRenderQueue instance = D3DRenderQueue.getInstance();
        boolean b2 = false;
        instance.lock();
        try {
            final D3DSurfaceData d3DSurfaceData2 = (D3DSurfaceData)surfaceData;
            final int type = d3DSurfaceData2.getType();
            final D3DSurfaceData d3DSurfaceData3 = d3DSurfaceData2;
            b2 = (type != 3);
            BufferedContext.validateContext(d3DSurfaceData3, d3DSurfaceData, region, composite, affineTransform, null, null, n10);
            if (bufferedImageOp != null) {
                BufferedBufImgOps.enableBufImgOp(instance, d3DSurfaceData2, bufferedImage, bufferedImageOp);
            }
            enqueueBlit(instance, surfaceData, surfaceData2, createPackedParams(true, b, b2, affineTransform != null, n, 0), n2, n3, n4, n5, n6, n7, n8, n9);
            if (bufferedImageOp != null) {
                BufferedBufImgOps.disableBufImgOp(instance, bufferedImageOp);
            }
        }
        finally {
            instance.unlock();
        }
        if (b2 && d3DSurfaceData.getType() == 1) {
            ((D3DScreenUpdateManager)ScreenUpdateManager.getInstance()).runUpdateNow();
        }
    }
}

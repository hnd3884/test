package sun.java2d.loops;

import java.awt.image.ColorModel;
import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;
import java.awt.image.WritableRaster;
import sun.java2d.pipe.Region;
import sun.java2d.SurfaceData;

public final class GeneralRenderer
{
    static final int OUTCODE_TOP = 1;
    static final int OUTCODE_BOTTOM = 2;
    static final int OUTCODE_LEFT = 4;
    static final int OUTCODE_RIGHT = 8;
    
    public static void register() {
        final Class<GeneralRenderer> clazz = GeneralRenderer.class;
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new GraphicsPrimitiveProxy(clazz, "SetFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "SetDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorFillRectANY", FillRect.methodSignature, FillRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorFillPathANY", FillPath.methodSignature, FillPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorFillSpansANY", FillSpans.methodSignature, FillSpans.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawLineANY", DrawLine.methodSignature, DrawLine.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawPolygonsANY", DrawPolygons.methodSignature, DrawPolygons.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawPathANY", DrawPath.methodSignature, DrawPath.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawRectANY", DrawRect.methodSignature, DrawRect.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawGlyphListANY", DrawGlyphList.methodSignature, DrawGlyphList.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorDrawGlyphListAAANY", DrawGlyphListAA.methodSignature, DrawGlyphListAA.primTypeID, SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any) });
    }
    
    static void doDrawPoly(final SurfaceData surfaceData, final PixelWriter pixelWriter, final int[] array, final int[] array2, int n, int n2, final Region region, final int n3, final int n4, final boolean b) {
        int[] doDrawLine = null;
        if (n2 <= 0) {
            return;
        }
        final int n6;
        int n5 = n6 = array[n] + n3;
        final int n8;
        int n7 = n8 = array2[n] + n4;
        while (--n2 > 0) {
            ++n;
            final int n9 = array[n] + n3;
            final int n10 = array2[n] + n4;
            doDrawLine = doDrawLine(surfaceData, pixelWriter, doDrawLine, region, n5, n7, n9, n10);
            n5 = n9;
            n7 = n10;
        }
        if (b && (n5 != n6 || n7 != n8)) {
            doDrawLine(surfaceData, pixelWriter, doDrawLine, region, n5, n7, n6, n8);
        }
    }
    
    static void doSetRect(final SurfaceData surfaceData, final PixelWriter pixelWriter, final int n, int i, final int n2, final int n3) {
        pixelWriter.setRaster((WritableRaster)surfaceData.getRaster(n, i, n2 - n, n3 - i));
        while (i < n3) {
            for (int j = n; j < n2; ++j) {
                pixelWriter.writePixel(j, i);
            }
            ++i;
        }
    }
    
    static int[] doDrawLine(final SurfaceData surfaceData, final PixelWriter pixelWriter, int[] array, final Region region, final int n, final int n2, final int n3, final int n4) {
        if (array == null) {
            array = new int[8];
        }
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = n4;
        if (!adjustLine(array, region.getLoX(), region.getLoY(), region.getHiX(), region.getHiY())) {
            return array;
        }
        int n5 = array[0];
        int n6 = array[1];
        final int n7 = array[2];
        final int n8 = array[3];
        pixelWriter.setRaster((WritableRaster)surfaceData.getRaster(Math.min(n5, n7), Math.min(n6, n8), Math.abs(n5 - n7) + 1, Math.abs(n6 - n8) + 1));
        if (n5 == n7) {
            if (n6 > n8) {
                do {
                    pixelWriter.writePixel(n5, n6);
                } while (--n6 >= n8);
            }
            else {
                do {
                    pixelWriter.writePixel(n5, n6);
                } while (++n6 <= n8);
            }
        }
        else if (n6 == n8) {
            if (n5 > n7) {
                do {
                    pixelWriter.writePixel(n5, n6);
                } while (--n5 >= n7);
            }
            else {
                do {
                    pixelWriter.writePixel(n5, n6);
                } while (++n5 <= n7);
            }
        }
        else {
            final int n9 = array[4];
            final int n10 = array[5];
            int n11 = array[6];
            int n12 = array[7];
            boolean b;
            int n13;
            int n14;
            int n15;
            int n16;
            int n17;
            if (n11 >= n12) {
                b = true;
                n13 = n12 * 2;
                n14 = n11 * 2;
                n15 = ((n9 < 0) ? -1 : 1);
                n16 = ((n10 < 0) ? -1 : 1);
                n11 = -n11;
                n17 = n7 - n5;
            }
            else {
                b = false;
                n13 = n11 * 2;
                n14 = n12 * 2;
                n15 = ((n10 < 0) ? -1 : 1);
                n16 = ((n9 < 0) ? -1 : 1);
                n12 = -n12;
                n17 = n8 - n6;
            }
            int n18 = -(n14 / 2);
            if (n6 != n2) {
                int n19 = n6 - n2;
                if (n19 < 0) {
                    n19 = -n19;
                }
                n18 += n19 * n11 * 2;
            }
            if (n5 != n) {
                int n20 = n5 - n;
                if (n20 < 0) {
                    n20 = -n20;
                }
                n18 += n20 * n12 * 2;
            }
            if (n17 < 0) {
                n17 = -n17;
            }
            if (b) {
                do {
                    pixelWriter.writePixel(n5, n6);
                    n5 += n15;
                    n18 += n13;
                    if (n18 >= 0) {
                        n6 += n16;
                        n18 -= n14;
                    }
                } while (--n17 >= 0);
            }
            else {
                do {
                    pixelWriter.writePixel(n5, n6);
                    n6 += n15;
                    n18 += n13;
                    if (n18 >= 0) {
                        n5 += n16;
                        n18 -= n14;
                    }
                } while (--n17 >= 0);
            }
        }
        return array;
    }
    
    public static void doDrawRect(final PixelWriter pixelWriter, final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
        if (n3 < 0 || n4 < 0) {
            return;
        }
        final int dimAdd = Region.dimAdd(Region.dimAdd(n, n3), 1);
        final int dimAdd2 = Region.dimAdd(Region.dimAdd(n2, n4), 1);
        final Region boundsIntersectionXYXY = sunGraphics2D.getCompClip().getBoundsIntersectionXYXY(n, n2, dimAdd, dimAdd2);
        if (boundsIntersectionXYXY.isEmpty()) {
            return;
        }
        final int loX = boundsIntersectionXYXY.getLoX();
        final int loY = boundsIntersectionXYXY.getLoY();
        final int hiX = boundsIntersectionXYXY.getHiX();
        final int hiY = boundsIntersectionXYXY.getHiY();
        if (n3 < 2 || n4 < 2) {
            doSetRect(surfaceData, pixelWriter, loX, loY, hiX, hiY);
            return;
        }
        if (loY == n2) {
            doSetRect(surfaceData, pixelWriter, loX, loY, hiX, loY + 1);
        }
        if (loX == n) {
            doSetRect(surfaceData, pixelWriter, loX, loY + 1, loX + 1, hiY - 1);
        }
        if (hiX == dimAdd) {
            doSetRect(surfaceData, pixelWriter, hiX - 1, loY + 1, hiX, hiY - 1);
        }
        if (hiY == dimAdd2) {
            doSetRect(surfaceData, pixelWriter, loX, hiY - 1, hiX, hiY);
        }
    }
    
    static void doDrawGlyphList(final SurfaceData surfaceData, final PixelWriter pixelWriter, final GlyphList list, final Region region) {
        final int[] bounds = list.getBounds();
        region.clipBoxToBounds(bounds);
        final int n = bounds[0];
        final int n2 = bounds[1];
        final int n3 = bounds[2];
        final int n4 = bounds[3];
        pixelWriter.setRaster((WritableRaster)surfaceData.getRaster(n, n2, n3 - n, n4 - n2));
        for (int numGlyphs = list.getNumGlyphs(), i = 0; i < numGlyphs; ++i) {
            list.setGlyphIndex(i);
            final int[] metrics = list.getMetrics();
            int n5 = metrics[0];
            int n6 = metrics[1];
            final int n7 = metrics[2];
            int n8 = n5 + n7;
            int n9 = n6 + metrics[3];
            int n10 = 0;
            if (n5 < n) {
                n10 = n - n5;
                n5 = n;
            }
            if (n6 < n2) {
                n10 += (n2 - n6) * n7;
                n6 = n2;
            }
            if (n8 > n3) {
                n8 = n3;
            }
            if (n9 > n4) {
                n9 = n4;
            }
            if (n8 > n5 && n9 > n6) {
                final byte[] grayBits = list.getGrayBits();
                final int n11 = n7 - (n8 - n5);
                for (int j = n6; j < n9; ++j) {
                    for (int k = n5; k < n8; ++k) {
                        if (grayBits[n10++] < 0) {
                            pixelWriter.writePixel(k, j);
                        }
                    }
                    n10 += n11;
                }
            }
        }
    }
    
    static int outcode(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        int n7;
        if (n2 < n4) {
            n7 = 1;
        }
        else if (n2 > n6) {
            n7 = 2;
        }
        else {
            n7 = 0;
        }
        if (n < n3) {
            n7 |= 0x4;
        }
        else if (n > n5) {
            n7 |= 0x8;
        }
        return n7;
    }
    
    public static boolean adjustLine(final int[] array, final int n, final int n2, final int n3, final int n4) {
        final int n5 = n3 - 1;
        final int n6 = n4 - 1;
        int n7 = array[0];
        int n8 = array[1];
        int n9 = array[2];
        int n10 = array[3];
        if (n5 < n || n6 < n2) {
            return false;
        }
        if (n7 == n9) {
            if (n7 < n || n7 > n5) {
                return false;
            }
            if (n8 > n10) {
                final int n11 = n8;
                n8 = n10;
                n10 = n11;
            }
            if (n8 < n2) {
                n8 = n2;
            }
            if (n10 > n6) {
                n10 = n6;
            }
            if (n8 > n10) {
                return false;
            }
            array[1] = n8;
            array[3] = n10;
        }
        else if (n8 == n10) {
            if (n8 < n2 || n8 > n6) {
                return false;
            }
            if (n7 > n9) {
                final int n12 = n7;
                n7 = n9;
                n9 = n12;
            }
            if (n7 < n) {
                n7 = n;
            }
            if (n9 > n5) {
                n9 = n5;
            }
            if (n7 > n9) {
                return false;
            }
            array[0] = n7;
            array[2] = n9;
        }
        else {
            final int n13 = n9 - n7;
            final int n14 = n10 - n8;
            final int n15 = (n13 < 0) ? (-n13) : n13;
            final int n16 = (n14 < 0) ? (-n14) : n14;
            final boolean b = n15 >= n16;
            int n17 = outcode(n7, n8, n, n2, n5, n6);
            int n18 = outcode(n9, n10, n, n2, n5, n6);
            while ((n17 | n18) != 0x0) {
                if ((n17 & n18) != 0x0) {
                    return false;
                }
                if (n17 != 0) {
                    if (0x0 != (n17 & 0x3)) {
                        if (0x0 != (n17 & 0x1)) {
                            n8 = n2;
                        }
                        else {
                            n8 = n6;
                        }
                        int n19 = n8 - array[1];
                        if (n19 < 0) {
                            n19 = -n19;
                        }
                        int n20 = 2 * n19 * n15 + n16;
                        if (b) {
                            n20 += n16 - n15 - 1;
                        }
                        int n21 = n20 / (2 * n16);
                        if (n13 < 0) {
                            n21 = -n21;
                        }
                        n7 = array[0] + n21;
                    }
                    else if (0x0 != (n17 & 0xC)) {
                        if (0x0 != (n17 & 0x4)) {
                            n7 = n;
                        }
                        else {
                            n7 = n5;
                        }
                        int n22 = n7 - array[0];
                        if (n22 < 0) {
                            n22 = -n22;
                        }
                        int n23 = 2 * n22 * n16 + n15;
                        if (!b) {
                            n23 += n15 - n16 - 1;
                        }
                        int n24 = n23 / (2 * n15);
                        if (n14 < 0) {
                            n24 = -n24;
                        }
                        n8 = array[1] + n24;
                    }
                    n17 = outcode(n7, n8, n, n2, n5, n6);
                }
                else {
                    if (0x0 != (n18 & 0x3)) {
                        if (0x0 != (n18 & 0x1)) {
                            n10 = n2;
                        }
                        else {
                            n10 = n6;
                        }
                        int n25 = n10 - array[3];
                        if (n25 < 0) {
                            n25 = -n25;
                        }
                        int n26 = 2 * n25 * n15 + n16;
                        if (b) {
                            n26 += n16 - n15;
                        }
                        else {
                            --n26;
                        }
                        int n27 = n26 / (2 * n16);
                        if (n13 > 0) {
                            n27 = -n27;
                        }
                        n9 = array[2] + n27;
                    }
                    else if (0x0 != (n18 & 0xC)) {
                        if (0x0 != (n18 & 0x4)) {
                            n9 = n;
                        }
                        else {
                            n9 = n5;
                        }
                        int n28 = n9 - array[2];
                        if (n28 < 0) {
                            n28 = -n28;
                        }
                        int n29 = 2 * n28 * n16 + n15;
                        if (b) {
                            --n29;
                        }
                        else {
                            n29 += n15 - n16;
                        }
                        int n30 = n29 / (2 * n15);
                        if (n14 > 0) {
                            n30 = -n30;
                        }
                        n10 = array[3] + n30;
                    }
                    n18 = outcode(n9, n10, n, n2, n5, n6);
                }
            }
            array[0] = n7;
            array[1] = n8;
            array[2] = n9;
            array[3] = n10;
            array[4] = n13;
            array[5] = n14;
            array[6] = n15;
            array[7] = n16;
        }
        return true;
    }
    
    static PixelWriter createSolidPixelWriter(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData) {
        return new SolidPixelWriter(surfaceData.getColorModel().getDataElements(sunGraphics2D.eargb, null));
    }
    
    static PixelWriter createXorPixelWriter(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData) {
        final ColorModel colorModel = surfaceData.getColorModel();
        final Object dataElements = colorModel.getDataElements(sunGraphics2D.eargb, null);
        final Object dataElements2 = colorModel.getDataElements(((XORComposite)sunGraphics2D.getComposite()).getXorColor().getRGB(), null);
        switch (colorModel.getTransferType()) {
            case 0: {
                return new XorPixelWriter.ByteData(dataElements, dataElements2);
            }
            case 1:
            case 2: {
                return new XorPixelWriter.ShortData(dataElements, dataElements2);
            }
            case 3: {
                return new XorPixelWriter.IntData(dataElements, dataElements2);
            }
            case 4: {
                return new XorPixelWriter.FloatData(dataElements, dataElements2);
            }
            case 5: {
                return new XorPixelWriter.DoubleData(dataElements, dataElements2);
            }
            default: {
                throw new InternalError("Unsupported XOR pixel type");
            }
        }
    }
}

package sun.java2d.pipe;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImageOp;
import java.awt.Graphics;
import sun.awt.image.ToolkitImage;
import sun.java2d.loops.ScaledBlit;
import sun.java2d.loops.BlitBg;
import java.awt.image.WritableRaster;
import sun.awt.image.BytePackedRaster;
import java.awt.image.IndexColorModel;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import sun.awt.image.SurfaceManager;
import sun.java2d.InvalidPipeException;
import sun.java2d.loops.Blit;
import sun.java2d.loops.CompositeType;
import sun.java2d.SurfaceData;
import sun.java2d.loops.MaskBlit;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.TransformHelper;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.awt.Color;
import java.awt.Image;
import sun.java2d.SunGraphics2D;

public class DrawImage implements DrawImagePipe
{
    private static final double MAX_TX_ERROR = 1.0E-4;
    
    public boolean copyImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final Color color) {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (isSimpleTranslate(sunGraphics2D)) {
            return this.renderImageCopy(sunGraphics2D, image, color, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, 0, 0, width, height);
        }
        AffineTransform transform = sunGraphics2D.transform;
        if ((n | n2) != 0x0) {
            transform = new AffineTransform(transform);
            transform.translate(n, n2);
        }
        this.transformImage(sunGraphics2D, image, transform, sunGraphics2D.interpolationType, 0, 0, width, height, color);
        return true;
    }
    
    public boolean copyImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Color color) {
        if (isSimpleTranslate(sunGraphics2D)) {
            return this.renderImageCopy(sunGraphics2D, image, color, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4, n5, n6);
        }
        this.scaleImage(sunGraphics2D, image, n, n2, n + n5, n2 + n6, n3, n4, n3 + n5, n4 + n6, color);
        return true;
    }
    
    public boolean scaleImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final Color color) {
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        if (n3 > 0 && n4 > 0 && isSimpleTranslate(sunGraphics2D)) {
            final double n5 = n + sunGraphics2D.transX;
            final double n6 = n2 + sunGraphics2D.transY;
            if (this.renderImageScale(sunGraphics2D, image, color, sunGraphics2D.interpolationType, 0, 0, width, height, n5, n6, n5 + n3, n6 + n4)) {
                return true;
            }
        }
        AffineTransform transform = sunGraphics2D.transform;
        if ((n | n2) != 0x0 || n3 != width || n4 != height) {
            transform = new AffineTransform(transform);
            transform.translate(n, n2);
            transform.scale(n3 / (double)width, n4 / (double)height);
        }
        this.transformImage(sunGraphics2D, image, transform, sunGraphics2D.interpolationType, 0, 0, width, height, color);
        return true;
    }
    
    protected void transformImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final AffineTransform affineTransform, final int n3) {
        final int type = affineTransform.getType();
        final int width = image.getWidth(null);
        final int height = image.getHeight(null);
        boolean b;
        if (sunGraphics2D.transformState <= 2 && (type == 0 || type == 1)) {
            final double translateX = affineTransform.getTranslateX();
            final double translateY = affineTransform.getTranslateY();
            final double n4 = translateX + sunGraphics2D.transform.getTranslateX();
            final double n5 = translateY + sunGraphics2D.transform.getTranslateY();
            final int n6 = (int)Math.floor(n4 + 0.5);
            final int n7 = (int)Math.floor(n5 + 0.5);
            if (n3 == 1 || (closeToInteger(n6, n4) && closeToInteger(n7, n5))) {
                this.renderImageCopy(sunGraphics2D, image, null, n + n6, n2 + n7, 0, 0, width, height);
                return;
            }
            b = false;
        }
        else if (sunGraphics2D.transformState <= 3 && (type & 0x78) == 0x0) {
            final double[] array = { 0.0, 0.0, width, height };
            affineTransform.transform(array, 0, array, 0, 2);
            final double[] array2 = array;
            final int n8 = 0;
            array2[n8] += n;
            final double[] array3 = array;
            final int n9 = 1;
            array3[n9] += n2;
            final double[] array4 = array;
            final int n10 = 2;
            array4[n10] += n;
            final double[] array5 = array;
            final int n11 = 3;
            array5[n11] += n2;
            sunGraphics2D.transform.transform(array, 0, array, 0, 2);
            if (this.tryCopyOrScale(sunGraphics2D, image, 0, 0, width, height, null, n3, array)) {
                return;
            }
            b = false;
        }
        else {
            b = true;
        }
        final AffineTransform affineTransform2 = new AffineTransform(sunGraphics2D.transform);
        affineTransform2.translate(n, n2);
        affineTransform2.concatenate(affineTransform);
        if (b) {
            this.transformImage(sunGraphics2D, image, affineTransform2, n3, 0, 0, width, height, null);
        }
        else {
            this.renderImageXform(sunGraphics2D, image, affineTransform2, n3, 0, 0, width, height, null);
        }
    }
    
    protected void transformImage(final SunGraphics2D sunGraphics2D, final Image image, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final Color color) {
        final double[] array = { 0.0, 0.0, n4 - n2, 0.0, 0.0, 0.0 };
        array[3] = (array[5] = n5 - n3);
        affineTransform.transform(array, 0, array, 0, 3);
        if (Math.abs(array[0] - array[4]) < 1.0E-4 && Math.abs(array[3] - array[5]) < 1.0E-4 && this.tryCopyOrScale(sunGraphics2D, image, n2, n3, n4, n5, color, n, array)) {
            return;
        }
        this.renderImageXform(sunGraphics2D, image, affineTransform, n, n2, n3, n4, n5, color);
    }
    
    protected boolean tryCopyOrScale(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final int n5, final double[] array) {
        final double n6 = array[0];
        final double n7 = array[1];
        final double n8 = array[2];
        final double n9 = array[3];
        final double n10 = n8 - n6;
        final double n11 = n9 - n7;
        if (n6 < -2.147483648E9 || n6 > 2.147483647E9 || n7 < -2.147483648E9 || n7 > 2.147483647E9 || n8 < -2.147483648E9 || n8 > 2.147483647E9 || n9 < -2.147483648E9 || n9 > 2.147483647E9) {
            return false;
        }
        if (closeToInteger(n3 - n, n10) && closeToInteger(n4 - n2, n11)) {
            final int n12 = (int)Math.floor(n6 + 0.5);
            final int n13 = (int)Math.floor(n7 + 0.5);
            if (n5 == 1 || (closeToInteger(n12, n6) && closeToInteger(n13, n7))) {
                this.renderImageCopy(sunGraphics2D, image, color, n12, n13, n, n2, n3 - n, n4 - n2);
                return true;
            }
        }
        return n10 > 0.0 && n11 > 0.0 && this.renderImageScale(sunGraphics2D, image, color, n5, n, n2, n3, n4, n6, n7, n8, n9);
    }
    
    BufferedImage makeBufferedImage(final Image image, final Color color, final int n, final int n2, final int n3, final int n4, final int n5) {
        final int n6 = n4 - n2;
        final int n7 = n5 - n3;
        final BufferedImage bufferedImage = new BufferedImage(n6, n7, n);
        final SunGraphics2D sunGraphics2D = (SunGraphics2D)bufferedImage.createGraphics();
        sunGraphics2D.setComposite(AlphaComposite.Src);
        bufferedImage.setAccelerationPriority(0.0f);
        if (color != null) {
            sunGraphics2D.setColor(color);
            sunGraphics2D.fillRect(0, 0, n6, n7);
            sunGraphics2D.setComposite(AlphaComposite.SrcOver);
        }
        sunGraphics2D.copyImage(image, 0, 0, n2, n3, n6, n7, null, null);
        sunGraphics2D.dispose();
        return bufferedImage;
    }
    
    protected void renderImageXform(final SunGraphics2D sunGraphics2D, Image image, final AffineTransform affineTransform, final int n, int n2, int n3, int n4, int n5, final Color color) {
        AffineTransform inverse;
        try {
            inverse = affineTransform.createInverse();
        }
        catch (final NoninvertibleTransformException ex) {
            return;
        }
        final double[] array = new double[8];
        array[2] = (array[6] = n4 - n2);
        array[5] = (array[7] = n5 - n3);
        affineTransform.transform(array, 0, array, 0, 4);
        double n7;
        double n6 = n7 = array[0];
        double n9;
        double n8 = n9 = array[1];
        for (int i = 2; i < array.length; i += 2) {
            final double n10 = array[i];
            if (n7 > n10) {
                n7 = n10;
            }
            else if (n6 < n10) {
                n6 = n10;
            }
            final double n11 = array[i + 1];
            if (n9 > n11) {
                n9 = n11;
            }
            else if (n8 < n11) {
                n8 = n11;
            }
        }
        final Region compClip = sunGraphics2D.getCompClip();
        final int max = Math.max((int)Math.floor(n7), compClip.lox);
        final int max2 = Math.max((int)Math.floor(n9), compClip.loy);
        final int min = Math.min((int)Math.ceil(n6), compClip.hix);
        final int min2 = Math.min((int)Math.ceil(n8), compClip.hiy);
        if (min <= max || min2 <= max2) {
            return;
        }
        final SurfaceData surfaceData = sunGraphics2D.surfaceData;
        SurfaceData surfaceData2 = surfaceData.getSourceSurfaceData(image, 4, sunGraphics2D.imageComp, color);
        if (surfaceData2 == null) {
            image = this.getBufferedImage(image);
            surfaceData2 = surfaceData.getSourceSurfaceData(image, 4, sunGraphics2D.imageComp, color);
            if (surfaceData2 == null) {
                return;
            }
        }
        if (isBgOperation(surfaceData2, color)) {
            image = this.makeBufferedImage(image, color, 1, n2, n3, n4, n5);
            n4 -= n2;
            n5 -= n3;
            n3 = (n2 = 0);
            surfaceData2 = surfaceData.getSourceSurfaceData(image, 4, sunGraphics2D.imageComp, color);
        }
        TransformHelper transformHelper = TransformHelper.getFromCache(surfaceData2.getSurfaceType());
        if (transformHelper == null) {
            final BufferedImage bufferedImage = this.makeBufferedImage(image, null, (surfaceData2.getTransparency() == 1) ? 1 : 2, n2, n3, n4, n5);
            n4 -= n2;
            n5 -= n3;
            n3 = (n2 = 0);
            surfaceData2 = surfaceData.getSourceSurfaceData(bufferedImage, 4, sunGraphics2D.imageComp, null);
            transformHelper = TransformHelper.getFromCache(surfaceData2.getSurfaceType());
        }
        final SurfaceType surfaceType = surfaceData.getSurfaceType();
        if (sunGraphics2D.compositeState <= 1) {
            final MaskBlit fromCache = MaskBlit.getFromCache(SurfaceType.IntArgbPre, sunGraphics2D.imageComp, surfaceType);
            if (fromCache.getNativePrim() != 0L) {
                transformHelper.Transform(fromCache, surfaceData2, surfaceData, sunGraphics2D.composite, compClip, inverse, n, n2, n3, n4, n5, max, max2, min, min2, null, 0, 0);
                return;
            }
        }
        final int n12 = min - max;
        final int n13 = min2 - max2;
        final SurfaceData primarySurfaceData = SurfaceData.getPrimarySurfaceData(new BufferedImage(n12, n13, 3));
        final SurfaceType surfaceType2 = primarySurfaceData.getSurfaceType();
        final MaskBlit fromCache2 = MaskBlit.getFromCache(SurfaceType.IntArgbPre, CompositeType.SrcNoEa, surfaceType2);
        final int[] array2 = new int[n13 * 2 + 2];
        transformHelper.Transform(fromCache2, surfaceData2, primarySurfaceData, AlphaComposite.Src, null, inverse, n, n2, n3, n4, n5, 0, 0, n12, n13, array2, max, max2);
        Blit.getFromCache(surfaceType2, sunGraphics2D.imageComp, surfaceType).Blit(primarySurfaceData, surfaceData, sunGraphics2D.composite, compClip.getIntersection(Region.getInstance(max, max2, min, min2, array2)), 0, 0, max, max2, n12, n13);
    }
    
    protected boolean renderImageCopy(final SunGraphics2D sunGraphics2D, final Image image, final Color color, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        Region region = sunGraphics2D.getCompClip();
        SurfaceData surfaceData = sunGraphics2D.surfaceData;
        int n7 = 0;
        while (true) {
            final SurfaceData sourceSurfaceData = surfaceData.getSourceSurfaceData(image, 0, sunGraphics2D.imageComp, color);
            if (sourceSurfaceData == null) {
                break;
            }
            try {
                this.blitSurfaceData(sunGraphics2D, region, sourceSurfaceData, surfaceData, sourceSurfaceData.getSurfaceType(), surfaceData.getSurfaceType(), n3, n4, n, n2, n5, n6, color);
                return true;
            }
            catch (final NullPointerException ex) {
                if (!SurfaceData.isNull(surfaceData) && !SurfaceData.isNull(sourceSurfaceData)) {
                    throw ex;
                }
                return false;
            }
            catch (final InvalidPipeException ex2) {
                ++n7;
                region = sunGraphics2D.getCompClip();
                surfaceData = sunGraphics2D.surfaceData;
                if (SurfaceData.isNull(surfaceData) || SurfaceData.isNull(sourceSurfaceData) || n7 > 1) {
                    return false;
                }
                continue;
            }
        }
        return false;
    }
    
    protected boolean renderImageScale(final SunGraphics2D sunGraphics2D, final Image image, final Color color, final int n, final int n2, final int n3, final int n4, final int n5, final double n6, final double n7, final double n8, final double n9) {
        if (n != 1) {
            return false;
        }
        Region region = sunGraphics2D.getCompClip();
        SurfaceData surfaceData = sunGraphics2D.surfaceData;
        int n10 = 0;
        while (true) {
            final SurfaceData sourceSurfaceData = surfaceData.getSourceSurfaceData(image, 3, sunGraphics2D.imageComp, color);
            if (sourceSurfaceData == null || isBgOperation(sourceSurfaceData, color)) {
                break;
            }
            try {
                return this.scaleSurfaceData(sunGraphics2D, region, sourceSurfaceData, surfaceData, sourceSurfaceData.getSurfaceType(), surfaceData.getSurfaceType(), n2, n3, n4, n5, n6, n7, n8, n9);
            }
            catch (final NullPointerException ex) {
                if (!SurfaceData.isNull(surfaceData)) {
                    throw ex;
                }
                return false;
            }
            catch (final InvalidPipeException ex2) {
                ++n10;
                region = sunGraphics2D.getCompClip();
                surfaceData = sunGraphics2D.surfaceData;
                if (SurfaceData.isNull(surfaceData) || SurfaceData.isNull(sourceSurfaceData) || n10 > 1) {
                    return false;
                }
                continue;
            }
        }
        return false;
    }
    
    public boolean scaleImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color) {
        boolean b = false;
        boolean b2 = false;
        boolean b3 = false;
        boolean b4 = false;
        int n9;
        int n10;
        if (n7 > n5) {
            n9 = n7 - n5;
            n10 = n5;
        }
        else {
            b = true;
            n9 = n5 - n7;
            n10 = n7;
        }
        int n11;
        int n12;
        if (n8 > n6) {
            n11 = n8 - n6;
            n12 = n6;
        }
        else {
            b2 = true;
            n11 = n6 - n8;
            n12 = n8;
        }
        int n13;
        int n14;
        if (n3 > n) {
            n13 = n3 - n;
            n14 = n;
        }
        else {
            n13 = n - n3;
            b3 = true;
            n14 = n3;
        }
        int n15;
        int n16;
        if (n4 > n2) {
            n15 = n4 - n2;
            n16 = n2;
        }
        else {
            n15 = n2 - n4;
            b4 = true;
            n16 = n4;
        }
        if (n9 <= 0 || n11 <= 0) {
            return true;
        }
        if (b == b3 && b2 == b4 && isSimpleTranslate(sunGraphics2D)) {
            final double n17 = n14 + sunGraphics2D.transX;
            final double n18 = n16 + sunGraphics2D.transY;
            if (this.renderImageScale(sunGraphics2D, image, color, sunGraphics2D.interpolationType, n10, n12, n10 + n9, n12 + n11, n17, n18, n17 + n13, n18 + n15)) {
                return true;
            }
        }
        final AffineTransform affineTransform = new AffineTransform(sunGraphics2D.transform);
        affineTransform.translate(n, n2);
        affineTransform.scale((n3 - n) / (double)(n7 - n5), (n4 - n2) / (double)(n8 - n6));
        affineTransform.translate(n10 - n5, n12 - n6);
        final int imageScale = SurfaceManager.getImageScale(image);
        final int n19 = image.getWidth(null) * imageScale;
        final int n20 = image.getHeight(null) * imageScale;
        int n21 = n9 + n10;
        int n22 = n11 + n12;
        if (n21 > n19) {
            n21 = n19;
        }
        if (n22 > n20) {
            n22 = n20;
        }
        if (n10 < 0) {
            affineTransform.translate(-n10, 0.0);
            n10 = 0;
        }
        if (n12 < 0) {
            affineTransform.translate(0.0, -n12);
            n12 = 0;
        }
        if (n10 >= n21 || n12 >= n22) {
            return true;
        }
        this.transformImage(sunGraphics2D, image, affineTransform, sunGraphics2D.interpolationType, n10, n12, n21, n22, color);
        return true;
    }
    
    public static boolean closeToInteger(final int n, final double n2) {
        return Math.abs(n2 - n) < 1.0E-4;
    }
    
    public static boolean isSimpleTranslate(final SunGraphics2D sunGraphics2D) {
        final int transformState = sunGraphics2D.transformState;
        return transformState <= 1 || (transformState < 3 && sunGraphics2D.interpolationType == 1);
    }
    
    protected static boolean isBgOperation(final SurfaceData surfaceData, final Color color) {
        return surfaceData == null || (color != null && surfaceData.getTransparency() != 1);
    }
    
    protected BufferedImage getBufferedImage(final Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        return ((VolatileImage)image).getSnapshot();
    }
    
    private ColorModel getTransformColorModel(final SunGraphics2D sunGraphics2D, final BufferedImage bufferedImage, final AffineTransform affineTransform) {
        ColorModel colorModel2;
        final ColorModel colorModel = colorModel2 = bufferedImage.getColorModel();
        if (affineTransform.isIdentity()) {
            return colorModel2;
        }
        final int type = affineTransform.getType();
        boolean b = (type & 0x38) != 0x0;
        if (!b && type != 1 && type != 0) {
            final double[] array = new double[4];
            affineTransform.getMatrix(array);
            b = (array[0] != (int)array[0] || array[3] != (int)array[3]);
        }
        if (sunGraphics2D.renderHint != 2) {
            if (colorModel instanceof IndexColorModel) {
                final WritableRaster raster = bufferedImage.getRaster();
                final IndexColorModel indexColorModel = (IndexColorModel)colorModel;
                if (b && colorModel.getTransparency() == 1) {
                    if (raster instanceof BytePackedRaster) {
                        colorModel2 = ColorModel.getRGBdefault();
                    }
                    else {
                        final double[] array2 = new double[6];
                        affineTransform.getMatrix(array2);
                        if (array2[1] != 0.0 || array2[2] != 0.0 || array2[4] != 0.0 || array2[5] != 0.0) {
                            final int mapSize = indexColorModel.getMapSize();
                            if (mapSize < 256) {
                                final int[] array3 = new int[mapSize + 1];
                                indexColorModel.getRGBs(array3);
                                array3[mapSize] = 0;
                                colorModel2 = new IndexColorModel(indexColorModel.getPixelSize(), mapSize + 1, array3, 0, true, mapSize, 0);
                            }
                            else {
                                colorModel2 = ColorModel.getRGBdefault();
                            }
                        }
                    }
                }
            }
            else if (b && colorModel.getTransparency() == 1) {
                colorModel2 = ColorModel.getRGBdefault();
            }
        }
        else if (colorModel instanceof IndexColorModel || (b && colorModel.getTransparency() == 1)) {
            colorModel2 = ColorModel.getRGBdefault();
        }
        return colorModel2;
    }
    
    protected void blitSurfaceData(final SunGraphics2D sunGraphics2D, final Region region, final SurfaceData surfaceData, final SurfaceData surfaceData2, final SurfaceType surfaceType, final SurfaceType surfaceType2, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Color color) {
        if (n5 <= 0 || n6 <= 0) {
            return;
        }
        CompositeType compositeType = sunGraphics2D.imageComp;
        if (CompositeType.SrcOverNoEa.equals(compositeType) && (surfaceData.getTransparency() == 1 || (color != null && color.getTransparency() == 1))) {
            compositeType = CompositeType.SrcNoEa;
        }
        if (!isBgOperation(surfaceData, color)) {
            Blit.getFromCache(surfaceType, compositeType, surfaceType2).Blit(surfaceData, surfaceData2, sunGraphics2D.composite, region, n, n2, n3, n4, n5, n6);
        }
        else {
            BlitBg.getFromCache(surfaceType, compositeType, surfaceType2).BlitBg(surfaceData, surfaceData2, sunGraphics2D.composite, region, color.getRGB(), n, n2, n3, n4, n5, n6);
        }
    }
    
    protected boolean scaleSurfaceData(final SunGraphics2D sunGraphics2D, final Region region, final SurfaceData surfaceData, final SurfaceData surfaceData2, final SurfaceType surfaceType, final SurfaceType surfaceType2, final int n, final int n2, final int n3, final int n4, final double n5, final double n6, final double n7, final double n8) {
        CompositeType compositeType = sunGraphics2D.imageComp;
        if (CompositeType.SrcOverNoEa.equals(compositeType) && surfaceData.getTransparency() == 1) {
            compositeType = CompositeType.SrcNoEa;
        }
        final ScaledBlit fromCache = ScaledBlit.getFromCache(surfaceType, compositeType, surfaceType2);
        if (fromCache != null) {
            fromCache.Scale(surfaceData, surfaceData2, sunGraphics2D.composite, region, n, n2, n3, n4, n5, n6, n7, n8);
            return true;
        }
        return false;
    }
    
    protected static boolean imageReady(final ToolkitImage toolkitImage, final ImageObserver imageObserver) {
        if (toolkitImage.hasError()) {
            if (imageObserver != null) {
                imageObserver.imageUpdate(toolkitImage, 192, -1, -1, -1, -1);
            }
            return false;
        }
        return true;
    }
    
    @Override
    public boolean copyImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final Color color, final ImageObserver imageObserver) {
        if (!(image instanceof ToolkitImage)) {
            return this.copyImage(sunGraphics2D, image, n, n2, color);
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        return imageReady(toolkitImage, imageObserver) && toolkitImage.getImageRep().drawToBufImage(sunGraphics2D, toolkitImage, n, n2, color, imageObserver);
    }
    
    @Override
    public boolean copyImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Color color, final ImageObserver imageObserver) {
        if (!(image instanceof ToolkitImage)) {
            return this.copyImage(sunGraphics2D, image, n, n2, n3, n4, n5, n6, color);
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        return imageReady(toolkitImage, imageObserver) && toolkitImage.getImageRep().drawToBufImage(sunGraphics2D, toolkitImage, n, n2, n + n5, n2 + n6, n3, n4, n3 + n5, n4 + n6, color, imageObserver);
    }
    
    @Override
    public boolean scaleImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final Color color, final ImageObserver imageObserver) {
        if (!(image instanceof ToolkitImage)) {
            return this.scaleImage(sunGraphics2D, image, n, n2, n3, n4, color);
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        return imageReady(toolkitImage, imageObserver) && toolkitImage.getImageRep().drawToBufImage(sunGraphics2D, toolkitImage, n, n2, n3, n4, color, imageObserver);
    }
    
    @Override
    public boolean scaleImage(final SunGraphics2D sunGraphics2D, final Image image, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final Color color, final ImageObserver imageObserver) {
        if (!(image instanceof ToolkitImage)) {
            return this.scaleImage(sunGraphics2D, image, n, n2, n3, n4, n5, n6, n7, n8, color);
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        return imageReady(toolkitImage, imageObserver) && toolkitImage.getImageRep().drawToBufImage(sunGraphics2D, toolkitImage, n, n2, n3, n4, n5, n6, n7, n8, color, imageObserver);
    }
    
    @Override
    public boolean transformImage(final SunGraphics2D sunGraphics2D, final Image image, final AffineTransform affineTransform, final ImageObserver imageObserver) {
        if (!(image instanceof ToolkitImage)) {
            this.transformImage(sunGraphics2D, image, 0, 0, affineTransform, sunGraphics2D.interpolationType);
            return true;
        }
        final ToolkitImage toolkitImage = (ToolkitImage)image;
        return imageReady(toolkitImage, imageObserver) && toolkitImage.getImageRep().drawToBufImage(sunGraphics2D, toolkitImage, affineTransform, imageObserver);
    }
    
    @Override
    public void transformImage(final SunGraphics2D sunGraphics2D, BufferedImage filter, final BufferedImageOp bufferedImageOp, final int n, final int n2) {
        if (bufferedImageOp != null) {
            if (bufferedImageOp instanceof AffineTransformOp) {
                final AffineTransformOp affineTransformOp = (AffineTransformOp)bufferedImageOp;
                this.transformImage(sunGraphics2D, filter, n, n2, affineTransformOp.getTransform(), affineTransformOp.getInterpolationType());
                return;
            }
            filter = bufferedImageOp.filter(filter, null);
        }
        this.copyImage(sunGraphics2D, filter, n, n2, null);
    }
}

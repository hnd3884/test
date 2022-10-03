package sun.java2d.pipe;

import java.awt.MultipleGradientPaint;
import java.awt.geom.Rectangle2D;
import sun.java2d.SurfaceData;
import java.awt.Image;
import sun.java2d.loops.CompositeType;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ColorModel;
import sun.awt.image.PixelConverter;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.TexturePaint;
import java.awt.RadialGradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.GradientPaint;
import java.awt.Paint;
import sun.java2d.SunGraphics2D;

public class BufferedPaints
{
    public static final int MULTI_MAX_FRACTIONS = 12;
    
    static void setPaint(final RenderQueue renderQueue, final SunGraphics2D sunGraphics2D, final Paint paint, final int n) {
        if (sunGraphics2D.paintState <= 1) {
            setColor(renderQueue, sunGraphics2D.pixel);
        }
        else {
            final boolean b = (n & 0x2) != 0x0;
            switch (sunGraphics2D.paintState) {
                case 2: {
                    setGradientPaint(renderQueue, sunGraphics2D, (GradientPaint)paint, b);
                    break;
                }
                case 3: {
                    setLinearGradientPaint(renderQueue, sunGraphics2D, (LinearGradientPaint)paint, b);
                    break;
                }
                case 4: {
                    setRadialGradientPaint(renderQueue, sunGraphics2D, (RadialGradientPaint)paint, b);
                    break;
                }
                case 5: {
                    setTexturePaint(renderQueue, sunGraphics2D, (TexturePaint)paint, b);
                    break;
                }
            }
        }
    }
    
    static void resetPaint(final RenderQueue renderQueue) {
        renderQueue.ensureCapacity(4);
        renderQueue.getBuffer().putInt(100);
    }
    
    private static void setColor(final RenderQueue renderQueue, final int n) {
        renderQueue.ensureCapacity(8);
        final RenderBuffer buffer = renderQueue.getBuffer();
        buffer.putInt(101);
        buffer.putInt(n);
    }
    
    private static void setGradientPaint(final RenderQueue renderQueue, final AffineTransform affineTransform, final Color color, final Color color2, final Point2D point2D, final Point2D point2D2, final boolean b, final boolean b2) {
        final PixelConverter instance = PixelConverter.ArgbPre.instance;
        final int rgbToPixel = instance.rgbToPixel(color.getRGB(), null);
        final int rgbToPixel2 = instance.rgbToPixel(color2.getRGB(), null);
        final double x = point2D.getX();
        final double y = point2D.getY();
        affineTransform.translate(x, y);
        final double n = point2D2.getX() - x;
        final double n2 = point2D2.getY() - y;
        final double sqrt = Math.sqrt(n * n + n2 * n2);
        affineTransform.rotate(n, n2);
        affineTransform.scale(2.0 * sqrt, 1.0);
        affineTransform.translate(-0.25, 0.0);
        double scaleX;
        double shearX;
        double translateX;
        try {
            affineTransform.invert();
            scaleX = affineTransform.getScaleX();
            shearX = affineTransform.getShearX();
            translateX = affineTransform.getTranslateX();
        }
        catch (final NoninvertibleTransformException ex) {
            shearX = (scaleX = (translateX = 0.0));
        }
        renderQueue.ensureCapacityAndAlignment(44, 12);
        final RenderBuffer buffer = renderQueue.getBuffer();
        buffer.putInt(102);
        buffer.putInt(b2 ? 1 : 0);
        buffer.putInt(b ? 1 : 0);
        buffer.putDouble(scaleX).putDouble(shearX).putDouble(translateX);
        buffer.putInt(rgbToPixel).putInt(rgbToPixel2);
    }
    
    private static void setGradientPaint(final RenderQueue renderQueue, final SunGraphics2D sunGraphics2D, final GradientPaint gradientPaint, final boolean b) {
        setGradientPaint(renderQueue, (AffineTransform)sunGraphics2D.transform.clone(), gradientPaint.getColor1(), gradientPaint.getColor2(), gradientPaint.getPoint1(), gradientPaint.getPoint2(), gradientPaint.isCyclic(), b);
    }
    
    private static void setTexturePaint(final RenderQueue renderQueue, final SunGraphics2D sunGraphics2D, final TexturePaint texturePaint, final boolean b) {
        final SurfaceData sourceSurfaceData = sunGraphics2D.surfaceData.getSourceSurfaceData(texturePaint.getImage(), 0, CompositeType.SrcOver, null);
        final int n = (sunGraphics2D.interpolationType != 1) ? 1 : 0;
        final AffineTransform affineTransform = (AffineTransform)sunGraphics2D.transform.clone();
        final Rectangle2D anchorRect = texturePaint.getAnchorRect();
        affineTransform.translate(anchorRect.getX(), anchorRect.getY());
        affineTransform.scale(anchorRect.getWidth(), anchorRect.getHeight());
        double scaleX;
        double shearX;
        double translateX;
        double shearY;
        double scaleY;
        double translateY;
        try {
            affineTransform.invert();
            scaleX = affineTransform.getScaleX();
            shearX = affineTransform.getShearX();
            translateX = affineTransform.getTranslateX();
            shearY = affineTransform.getShearY();
            scaleY = affineTransform.getScaleY();
            translateY = affineTransform.getTranslateY();
        }
        catch (final NoninvertibleTransformException ex) {
            shearX = (scaleX = (translateX = (shearY = (scaleY = (translateY = 0.0)))));
        }
        renderQueue.ensureCapacityAndAlignment(68, 12);
        final RenderBuffer buffer = renderQueue.getBuffer();
        buffer.putInt(105);
        buffer.putInt(b ? 1 : 0);
        buffer.putInt(n);
        buffer.putLong(sourceSurfaceData.getNativeOps());
        buffer.putDouble(scaleX).putDouble(shearX).putDouble(translateX);
        buffer.putDouble(shearY).putDouble(scaleY).putDouble(translateY);
    }
    
    public static int convertSRGBtoLinearRGB(final int n) {
        final float n2 = n / 255.0f;
        float n3;
        if (n2 <= 0.04045f) {
            n3 = n2 / 12.92f;
        }
        else {
            n3 = (float)Math.pow((n2 + 0.055) / 1.055, 2.4);
        }
        return Math.round(n3 * 255.0f);
    }
    
    private static int colorToIntArgbPrePixel(final Color color, final boolean b) {
        final int rgb = color.getRGB();
        if (!b && rgb >> 24 == -1) {
            return rgb;
        }
        final int n = rgb >>> 24;
        int convertSRGBtoLinearRGB = rgb >> 16 & 0xFF;
        int convertSRGBtoLinearRGB2 = rgb >> 8 & 0xFF;
        int convertSRGBtoLinearRGB3 = rgb & 0xFF;
        if (b) {
            convertSRGBtoLinearRGB = convertSRGBtoLinearRGB(convertSRGBtoLinearRGB);
            convertSRGBtoLinearRGB2 = convertSRGBtoLinearRGB(convertSRGBtoLinearRGB2);
            convertSRGBtoLinearRGB3 = convertSRGBtoLinearRGB(convertSRGBtoLinearRGB3);
        }
        final int n2 = n + (n >> 7);
        return n << 24 | convertSRGBtoLinearRGB * n2 >> 8 << 16 | convertSRGBtoLinearRGB2 * n2 >> 8 << 8 | convertSRGBtoLinearRGB3 * n2 >> 8;
    }
    
    private static int[] convertToIntArgbPrePixels(final Color[] array, final boolean b) {
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = colorToIntArgbPrePixel(array[i], b);
        }
        return array2;
    }
    
    private static void setLinearGradientPaint(final RenderQueue renderQueue, final SunGraphics2D sunGraphics2D, final LinearGradientPaint linearGradientPaint, final boolean b) {
        final int n = (linearGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) ? 1 : 0;
        final Color[] colors = linearGradientPaint.getColors();
        final int length = colors.length;
        final Point2D startPoint = linearGradientPaint.getStartPoint();
        final Point2D endPoint = linearGradientPaint.getEndPoint();
        final AffineTransform transform = linearGradientPaint.getTransform();
        transform.preConcatenate(sunGraphics2D.transform);
        if (n == 0 && length == 2 && linearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.REPEAT) {
            setGradientPaint(renderQueue, transform, colors[0], colors[1], startPoint, endPoint, linearGradientPaint.getCycleMethod() != MultipleGradientPaint.CycleMethod.NO_CYCLE, b);
            return;
        }
        final int ordinal = linearGradientPaint.getCycleMethod().ordinal();
        final float[] fractions = linearGradientPaint.getFractions();
        final int[] convertToIntArgbPrePixels = convertToIntArgbPrePixels(colors, (boolean)(n != 0));
        final double x = startPoint.getX();
        final double y = startPoint.getY();
        transform.translate(x, y);
        final double n2 = endPoint.getX() - x;
        final double n3 = endPoint.getY() - y;
        final double sqrt = Math.sqrt(n2 * n2 + n3 * n3);
        transform.rotate(n2, n3);
        transform.scale(sqrt, 1.0);
        float n4;
        float n5;
        float n6;
        try {
            transform.invert();
            n4 = (float)transform.getScaleX();
            n5 = (float)transform.getShearX();
            n6 = (float)transform.getTranslateX();
        }
        catch (final NoninvertibleTransformException ex) {
            n5 = (n4 = (n6 = 0.0f));
        }
        renderQueue.ensureCapacity(32 + length * 4 * 2);
        final RenderBuffer buffer = renderQueue.getBuffer();
        buffer.putInt(103);
        buffer.putInt(b ? 1 : 0);
        buffer.putInt(n);
        buffer.putInt(ordinal);
        buffer.putInt(length);
        buffer.putFloat(n4);
        buffer.putFloat(n5);
        buffer.putFloat(n6);
        buffer.put(fractions);
        buffer.put(convertToIntArgbPrePixels);
    }
    
    private static void setRadialGradientPaint(final RenderQueue renderQueue, final SunGraphics2D sunGraphics2D, final RadialGradientPaint radialGradientPaint, final boolean b) {
        final int n = (radialGradientPaint.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) ? 1 : 0;
        final int ordinal = radialGradientPaint.getCycleMethod().ordinal();
        final float[] fractions = radialGradientPaint.getFractions();
        final Color[] colors = radialGradientPaint.getColors();
        final int length = colors.length;
        final int[] convertToIntArgbPrePixels = convertToIntArgbPrePixels(colors, (boolean)(n != 0));
        final Point2D centerPoint = radialGradientPaint.getCenterPoint();
        final Point2D focusPoint = radialGradientPaint.getFocusPoint();
        final float radius = radialGradientPaint.getRadius();
        final double x = centerPoint.getX();
        final double y = centerPoint.getY();
        final double x2 = focusPoint.getX();
        final double y2 = focusPoint.getY();
        final AffineTransform transform = radialGradientPaint.getTransform();
        transform.preConcatenate(sunGraphics2D.transform);
        final Point2D transform2 = transform.transform(focusPoint, focusPoint);
        transform.translate(x, y);
        transform.rotate(x2 - x, y2 - y);
        transform.scale(radius, radius);
        try {
            transform.invert();
        }
        catch (final Exception ex) {
            transform.setToScale(0.0, 0.0);
        }
        final double min = Math.min(transform.transform(transform2, transform2).getX(), 0.99);
        renderQueue.ensureCapacity(48 + length * 4 * 2);
        final RenderBuffer buffer = renderQueue.getBuffer();
        buffer.putInt(104);
        buffer.putInt(b ? 1 : 0);
        buffer.putInt(n);
        buffer.putInt(length);
        buffer.putInt(ordinal);
        buffer.putFloat((float)transform.getScaleX());
        buffer.putFloat((float)transform.getShearX());
        buffer.putFloat((float)transform.getTranslateX());
        buffer.putFloat((float)transform.getShearY());
        buffer.putFloat((float)transform.getScaleY());
        buffer.putFloat((float)transform.getTranslateY());
        buffer.putFloat((float)min);
        buffer.put(fractions);
        buffer.put(convertToIntArgbPrePixels);
    }
}

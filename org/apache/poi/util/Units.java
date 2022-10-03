package org.apache.poi.util;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;

public class Units
{
    public static final int EMU_PER_PIXEL = 9525;
    public static final int EMU_PER_POINT = 12700;
    public static final int EMU_PER_CENTIMETER = 360000;
    public static final int MASTER_DPI = 576;
    public static final int PIXEL_DPI = 96;
    public static final int POINT_DPI = 72;
    public static final float DEFAULT_CHARACTER_WIDTH = 7.0017f;
    public static final int EMU_PER_CHARACTER = 66691;
    
    public static int toEMU(final double points) {
        return (int)Math.rint(12700.0 * points);
    }
    
    public static int pixelToEMU(final int pixels) {
        return pixels * 9525;
    }
    
    public static double toPoints(final long emu) {
        return emu / 12700.0;
    }
    
    public static double fixedPointToDouble(final int fixedPoint) {
        final int i = fixedPoint >> 16;
        final int f = fixedPoint & 0xFFFF;
        return i + f / 65536.0;
    }
    
    public static int doubleToFixedPoint(final double floatPoint) {
        final double fractionalPart = floatPoint % 1.0;
        final double integralPart = floatPoint - fractionalPart;
        final int i = (int)Math.floor(integralPart);
        final int f = (int)Math.rint(fractionalPart * 65536.0);
        return i << 16 | (f & 0xFFFF);
    }
    
    public static double masterToPoints(final int masterDPI) {
        double points = masterDPI;
        points *= 72.0;
        points /= 576.0;
        return points;
    }
    
    public static int pointsToMaster(double points) {
        points *= 576.0;
        points /= 72.0;
        return (int)Math.rint(points);
    }
    
    public static int pointsToPixel(double points) {
        points *= 96.0;
        points /= 72.0;
        return (int)Math.rint(points);
    }
    
    public static double pixelToPoints(final double pixel) {
        double points = pixel;
        points *= 72.0;
        points /= 96.0;
        return points;
    }
    
    public static Dimension2D pointsToPixel(final Dimension2D pointsDim) {
        final double width = pointsDim.getWidth() * 96.0 / 72.0;
        final double height = pointsDim.getHeight() * 96.0 / 72.0;
        return new Dimension2DDouble(width, height);
    }
    
    public static Dimension2D pixelToPoints(final Dimension2D pointsDim) {
        final double width = pointsDim.getWidth() * 72.0 / 96.0;
        final double height = pointsDim.getHeight() * 72.0 / 96.0;
        return new Dimension2DDouble(width, height);
    }
    
    public static Rectangle2D pointsToPixel(final Rectangle2D pointsDim) {
        final double x = pointsDim.getX() * 96.0 / 72.0;
        final double y = pointsDim.getY() * 96.0 / 72.0;
        final double width = pointsDim.getWidth() * 96.0 / 72.0;
        final double height = pointsDim.getHeight() * 96.0 / 72.0;
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    public static Rectangle2D pixelToPoints(final Rectangle2D pointsDim) {
        final double x = pointsDim.getX() * 72.0 / 96.0;
        final double y = pointsDim.getY() * 72.0 / 96.0;
        final double width = pointsDim.getWidth() * 72.0 / 96.0;
        final double height = pointsDim.getHeight() * 72.0 / 96.0;
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    public static int charactersToEMU(final double characters) {
        return (int)characters * 66691;
    }
    
    public static int columnWidthToEMU(final int columnWidth) {
        return charactersToEMU(columnWidth / 256.0);
    }
    
    public static int TwipsToEMU(final short twips) {
        return (int)(twips / 20.0 * 12700.0);
    }
}

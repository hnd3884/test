package java.awt;

import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public final class LinearGradientPaint extends MultipleGradientPaint
{
    private final Point2D start;
    private final Point2D end;
    
    public LinearGradientPaint(final float n, final float n2, final float n3, final float n4, final float[] array, final Color[] array2) {
        this(new Point2D.Float(n, n2), new Point2D.Float(n3, n4), array, array2, CycleMethod.NO_CYCLE);
    }
    
    public LinearGradientPaint(final float n, final float n2, final float n3, final float n4, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(new Point2D.Float(n, n2), new Point2D.Float(n3, n4), array, array2, cycleMethod);
    }
    
    public LinearGradientPaint(final Point2D point2D, final Point2D point2D2, final float[] array, final Color[] array2) {
        this(point2D, point2D2, array, array2, CycleMethod.NO_CYCLE);
    }
    
    public LinearGradientPaint(final Point2D point2D, final Point2D point2D2, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(point2D, point2D2, array, array2, cycleMethod, ColorSpaceType.SRGB, new AffineTransform());
    }
    
    @ConstructorProperties({ "startPoint", "endPoint", "fractions", "colors", "cycleMethod", "colorSpace", "transform" })
    public LinearGradientPaint(final Point2D point2D, final Point2D point2D2, final float[] array, final Color[] array2, final CycleMethod cycleMethod, final ColorSpaceType colorSpaceType, final AffineTransform affineTransform) {
        super(array, array2, cycleMethod, colorSpaceType, affineTransform);
        if (point2D == null || point2D2 == null) {
            throw new NullPointerException("Start and end points must benon-null");
        }
        if (point2D.equals(point2D2)) {
            throw new IllegalArgumentException("Start point cannot equalendpoint");
        }
        this.start = new Point2D.Double(point2D.getX(), point2D.getY());
        this.end = new Point2D.Double(point2D2.getX(), point2D2.getY());
    }
    
    @Override
    public PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, AffineTransform affineTransform, final RenderingHints renderingHints) {
        affineTransform = new AffineTransform(affineTransform);
        affineTransform.concatenate(this.gradientTransform);
        if (this.fractions.length == 2 && this.cycleMethod != CycleMethod.REPEAT && this.colorSpace == ColorSpaceType.SRGB) {
            return new GradientPaintContext(colorModel, this.start, this.end, affineTransform, this.colors[0], this.colors[1], this.cycleMethod != CycleMethod.NO_CYCLE);
        }
        return new LinearGradientPaintContext(this, colorModel, rectangle, rectangle2D, affineTransform, renderingHints, this.start, this.end, this.fractions, this.colors, this.cycleMethod, this.colorSpace);
    }
    
    public Point2D getStartPoint() {
        return new Point2D.Double(this.start.getX(), this.start.getY());
    }
    
    public Point2D getEndPoint() {
        return new Point2D.Double(this.end.getX(), this.end.getY());
    }
}

package java.awt;

import java.awt.image.ColorModel;
import java.awt.geom.Rectangle2D;
import java.beans.ConstructorProperties;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public final class RadialGradientPaint extends MultipleGradientPaint
{
    private final Point2D focus;
    private final Point2D center;
    private final float radius;
    
    public RadialGradientPaint(final float n, final float n2, final float n3, final float[] array, final Color[] array2) {
        this(n, n2, n3, n, n2, array, array2, CycleMethod.NO_CYCLE);
    }
    
    public RadialGradientPaint(final Point2D point2D, final float n, final float[] array, final Color[] array2) {
        this(point2D, n, point2D, array, array2, CycleMethod.NO_CYCLE);
    }
    
    public RadialGradientPaint(final float n, final float n2, final float n3, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(n, n2, n3, n, n2, array, array2, cycleMethod);
    }
    
    public RadialGradientPaint(final Point2D point2D, final float n, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(point2D, n, point2D, array, array2, cycleMethod);
    }
    
    public RadialGradientPaint(final float n, final float n2, final float n3, final float n4, final float n5, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(new Point2D.Float(n, n2), n3, new Point2D.Float(n4, n5), array, array2, cycleMethod);
    }
    
    public RadialGradientPaint(final Point2D point2D, final float n, final Point2D point2D2, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(point2D, n, point2D2, array, array2, cycleMethod, ColorSpaceType.SRGB, new AffineTransform());
    }
    
    @ConstructorProperties({ "centerPoint", "radius", "focusPoint", "fractions", "colors", "cycleMethod", "colorSpace", "transform" })
    public RadialGradientPaint(final Point2D point2D, final float radius, final Point2D point2D2, final float[] array, final Color[] array2, final CycleMethod cycleMethod, final ColorSpaceType colorSpaceType, final AffineTransform affineTransform) {
        super(array, array2, cycleMethod, colorSpaceType, affineTransform);
        if (point2D == null) {
            throw new NullPointerException("Center point must be non-null");
        }
        if (point2D2 == null) {
            throw new NullPointerException("Focus point must be non-null");
        }
        if (radius <= 0.0f) {
            throw new IllegalArgumentException("Radius must be greater than zero");
        }
        this.center = new Point2D.Double(point2D.getX(), point2D.getY());
        this.focus = new Point2D.Double(point2D2.getX(), point2D2.getY());
        this.radius = radius;
    }
    
    public RadialGradientPaint(final Rectangle2D rectangle2D, final float[] array, final Color[] array2, final CycleMethod cycleMethod) {
        this(new Point2D.Double(rectangle2D.getCenterX(), rectangle2D.getCenterY()), 1.0f, new Point2D.Double(rectangle2D.getCenterX(), rectangle2D.getCenterY()), array, array2, cycleMethod, ColorSpaceType.SRGB, createGradientTransform(rectangle2D));
        if (rectangle2D.isEmpty()) {
            throw new IllegalArgumentException("Gradient bounds must be non-empty");
        }
    }
    
    private static AffineTransform createGradientTransform(final Rectangle2D rectangle2D) {
        final double centerX = rectangle2D.getCenterX();
        final double centerY = rectangle2D.getCenterY();
        final AffineTransform translateInstance = AffineTransform.getTranslateInstance(centerX, centerY);
        translateInstance.scale(rectangle2D.getWidth() / 2.0, rectangle2D.getHeight() / 2.0);
        translateInstance.translate(-centerX, -centerY);
        return translateInstance;
    }
    
    @Override
    public PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, AffineTransform affineTransform, final RenderingHints renderingHints) {
        affineTransform = new AffineTransform(affineTransform);
        affineTransform.concatenate(this.gradientTransform);
        return new RadialGradientPaintContext(this, colorModel, rectangle, rectangle2D, affineTransform, renderingHints, (float)this.center.getX(), (float)this.center.getY(), this.radius, (float)this.focus.getX(), (float)this.focus.getY(), this.fractions, this.colors, this.cycleMethod, this.colorSpace);
    }
    
    public Point2D getCenterPoint() {
        return new Point2D.Double(this.center.getX(), this.center.getY());
    }
    
    public Point2D getFocusPoint() {
        return new Point2D.Double(this.focus.getX(), this.focus.getY());
    }
    
    public float getRadius() {
        return this.radius;
    }
}

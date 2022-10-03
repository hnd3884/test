package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.beans.ConstructorProperties;
import java.awt.geom.Point2D;

public class GradientPaint implements Paint
{
    Point2D.Float p1;
    Point2D.Float p2;
    Color color1;
    Color color2;
    boolean cyclic;
    
    public GradientPaint(final float n, final float n2, final Color color1, final float n3, final float n4, final Color color2) {
        if (color1 == null || color2 == null) {
            throw new NullPointerException("Colors cannot be null");
        }
        this.p1 = new Point2D.Float(n, n2);
        this.p2 = new Point2D.Float(n3, n4);
        this.color1 = color1;
        this.color2 = color2;
    }
    
    public GradientPaint(final Point2D point2D, final Color color1, final Point2D point2D2, final Color color2) {
        if (color1 == null || color2 == null || point2D == null || point2D2 == null) {
            throw new NullPointerException("Colors and points should be non-null");
        }
        this.p1 = new Point2D.Float((float)point2D.getX(), (float)point2D.getY());
        this.p2 = new Point2D.Float((float)point2D2.getX(), (float)point2D2.getY());
        this.color1 = color1;
        this.color2 = color2;
    }
    
    public GradientPaint(final float n, final float n2, final Color color, final float n3, final float n4, final Color color2, final boolean cyclic) {
        this(n, n2, color, n3, n4, color2);
        this.cyclic = cyclic;
    }
    
    @ConstructorProperties({ "point1", "color1", "point2", "color2", "cyclic" })
    public GradientPaint(final Point2D point2D, final Color color, final Point2D point2D2, final Color color2, final boolean cyclic) {
        this(point2D, color, point2D2, color2);
        this.cyclic = cyclic;
    }
    
    public Point2D getPoint1() {
        return new Point2D.Float(this.p1.x, this.p1.y);
    }
    
    public Color getColor1() {
        return this.color1;
    }
    
    public Point2D getPoint2() {
        return new Point2D.Float(this.p2.x, this.p2.y);
    }
    
    public Color getColor2() {
        return this.color2;
    }
    
    public boolean isCyclic() {
        return this.cyclic;
    }
    
    @Override
    public PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints) {
        return new GradientPaintContext(colorModel, this.p1, this.p2, affineTransform, this.color1, this.color2, this.cyclic);
    }
    
    @Override
    public int getTransparency() {
        return ((this.color1.getAlpha() & this.color2.getAlpha()) == 0xFF) ? 1 : 3;
    }
}

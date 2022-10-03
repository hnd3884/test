package org.jfree.util;

import java.awt.geom.PathIterator;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.awt.Polygon;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import org.jfree.ui.RectangleAnchor;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;

public class ShapeUtilities
{
    private static final float SQRT2;
    
    static {
        SQRT2 = (float)Math.pow(2.0, 0.5);
    }
    
    private ShapeUtilities() {
    }
    
    public static Shape clone(final Shape shape) {
        if (shape instanceof Cloneable) {
            try {
                return (Shape)ObjectUtilities.clone(shape);
            }
            catch (final CloneNotSupportedException ex) {}
        }
        final Shape result = null;
        return null;
    }
    
    public static boolean contains(final Rectangle2D rect1, final Rectangle2D rect2) {
        final double x0 = rect1.getX();
        final double y0 = rect1.getY();
        final double x2 = rect2.getX();
        final double y2 = rect2.getY();
        final double w = rect2.getWidth();
        final double h = rect2.getHeight();
        return x2 >= x0 && y2 >= y0 && x2 + w <= x0 + rect1.getWidth() && y2 + h <= y0 + rect1.getHeight();
    }
    
    public static Shape createDiagonalCross(final float l, final float t) {
        final GeneralPath p0 = new GeneralPath();
        p0.moveTo(-l - t, -l + t);
        p0.lineTo(-l + t, -l - t);
        p0.lineTo(0.0f, -t * ShapeUtilities.SQRT2);
        p0.lineTo(l - t, -l - t);
        p0.lineTo(l + t, -l + t);
        p0.lineTo(t * ShapeUtilities.SQRT2, 0.0f);
        p0.lineTo(l + t, l - t);
        p0.lineTo(l - t, l + t);
        p0.lineTo(0.0f, t * ShapeUtilities.SQRT2);
        p0.lineTo(-l + t, l + t);
        p0.lineTo(-l - t, l - t);
        p0.lineTo(-t * ShapeUtilities.SQRT2, 0.0f);
        p0.closePath();
        return p0;
    }
    
    public static Shape createDiamond(final float s) {
        final GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, -s);
        p0.lineTo(s, 0.0f);
        p0.lineTo(0.0f, s);
        p0.lineTo(-s, 0.0f);
        p0.closePath();
        return p0;
    }
    
    public static Shape createDownTriangle(final float s) {
        final GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, s);
        p0.lineTo(s, -s);
        p0.lineTo(-s, -s);
        p0.closePath();
        return p0;
    }
    
    public static Shape createLineRegion(final Line2D line, final float width) {
        final GeneralPath result = new GeneralPath();
        final float x1 = (float)line.getX1();
        final float x2 = (float)line.getX2();
        final float y1 = (float)line.getY1();
        final float y2 = (float)line.getY2();
        if (x2 - x1 != 0.0) {
            final double theta = Math.atan((y2 - y1) / (x2 - x1));
            final float dx = (float)Math.sin(theta) * width;
            final float dy = (float)Math.cos(theta) * width;
            result.moveTo(x1 - dx, y1 + dy);
            result.lineTo(x1 + dx, y1 - dy);
            result.lineTo(x2 + dx, y2 - dy);
            result.lineTo(x2 - dx, y2 + dy);
            result.closePath();
        }
        else {
            result.moveTo(x1 - width / 2.0f, y1);
            result.lineTo(x1 + width / 2.0f, y1);
            result.lineTo(x2 + width / 2.0f, y2);
            result.lineTo(x2 - width / 2.0f, y2);
            result.closePath();
        }
        return result;
    }
    
    public static Shape createRegularCross(final float l, final float t) {
        final GeneralPath p0 = new GeneralPath();
        p0.moveTo(-l, t);
        p0.lineTo(-t, t);
        p0.lineTo(-t, l);
        p0.lineTo(t, l);
        p0.lineTo(t, t);
        p0.lineTo(l, t);
        p0.lineTo(l, -t);
        p0.lineTo(t, -t);
        p0.lineTo(t, -l);
        p0.lineTo(-t, -l);
        p0.lineTo(-t, -t);
        p0.lineTo(-l, -t);
        p0.closePath();
        return p0;
    }
    
    public static Shape createTranslatedShape(final Shape shape, final double transX, final double transY) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        final AffineTransform transform = AffineTransform.getTranslateInstance(transX, transY);
        return transform.createTransformedShape(shape);
    }
    
    public static Shape createTranslatedShape(final Shape shape, final RectangleAnchor anchor, final double locationX, final double locationY) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");
        }
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        final Point2D anchorPoint = RectangleAnchor.coordinates(shape.getBounds2D(), anchor);
        final AffineTransform transform = AffineTransform.getTranslateInstance(locationX - anchorPoint.getX(), locationY - anchorPoint.getY());
        return transform.createTransformedShape(shape);
    }
    
    public static Shape createUpTriangle(final float s) {
        final GeneralPath p0 = new GeneralPath();
        p0.moveTo(0.0f, -s);
        p0.lineTo(s, s);
        p0.lineTo(-s, s);
        p0.closePath();
        return p0;
    }
    
    public static void drawRotatedShape(final Graphics2D g2, final Shape shape, final double angle, final float x, final float y) {
        final AffineTransform saved = g2.getTransform();
        final AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
        g2.transform(rotate);
        g2.draw(shape);
        g2.setTransform(saved);
    }
    
    public static boolean equal(final Polygon p1, final Polygon p2) {
        if (p1 == null) {
            return p2 == null;
        }
        return p2 != null && p1.npoints == p2.npoints && Arrays.equals(p1.xpoints, p2.xpoints) && Arrays.equals(p1.ypoints, p2.ypoints);
    }
    
    public static boolean equal(final Shape s1, final Shape s2) {
        if (s1 instanceof Line2D && s2 instanceof Line2D) {
            return equal((Line2D)s1, (Line2D)s2);
        }
        if (s1 instanceof Ellipse2D && s2 instanceof Ellipse2D) {
            return equal((Ellipse2D)s1, (Ellipse2D)s2);
        }
        if (s1 instanceof Arc2D && s2 instanceof Arc2D) {
            return equal((Arc2D)s1, (Arc2D)s2);
        }
        if (s1 instanceof Polygon && s2 instanceof Polygon) {
            return equal((Polygon)s1, (Polygon)s2);
        }
        if (s1 instanceof GeneralPath && s2 instanceof GeneralPath) {
            return equal((GeneralPath)s1, (GeneralPath)s2);
        }
        return ObjectUtilities.equal(s1, s2);
    }
    
    public static boolean equal(final Arc2D a1, final Arc2D a2) {
        if (a1 == null) {
            return a2 == null;
        }
        return a2 != null && a1.getFrame().equals(a2.getFrame()) && a1.getAngleStart() == a2.getAngleStart() && a1.getAngleExtent() == a2.getAngleExtent() && a1.getArcType() == a2.getArcType();
    }
    
    public static boolean equal(final Ellipse2D e1, final Ellipse2D e2) {
        if (e1 == null) {
            return e2 == null;
        }
        return e2 != null && e1.getFrame().equals(e2.getFrame());
    }
    
    public static boolean equal(final GeneralPath p1, final GeneralPath p2) {
        if (p1 == null) {
            return p2 == null;
        }
        if (p2 == null) {
            return false;
        }
        if (p1.getWindingRule() != p2.getWindingRule()) {
            return false;
        }
        final PathIterator iterator1 = p1.getPathIterator(null);
        final PathIterator iterator2 = p1.getPathIterator(null);
        final double[] d1 = new double[6];
        final double[] d2 = new double[6];
        for (boolean done = iterator1.isDone() && iterator2.isDone(); !done; done = (iterator1.isDone() && iterator2.isDone())) {
            if (iterator1.isDone() != iterator2.isDone()) {
                return false;
            }
            final int seg1 = iterator1.currentSegment(d1);
            final int seg2 = iterator2.currentSegment(d2);
            if (seg1 != seg2) {
                return false;
            }
            if (!Arrays.equals(d1, d2)) {
                return false;
            }
            iterator1.next();
            iterator2.next();
        }
        return true;
    }
    
    public static boolean equal(final Line2D l1, final Line2D l2) {
        if (l1 == null) {
            return l2 == null;
        }
        return l2 != null && l1.getP1().equals(l2.getP1()) && l1.getP2().equals(l2.getP2());
    }
    
    public static Point2D getPointInRectangle(double x, double y, final Rectangle2D area) {
        x = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        y = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(x, y);
    }
    
    public static boolean intersects(final Rectangle2D rect1, final Rectangle2D rect2) {
        final double x0 = rect1.getX();
        final double y0 = rect1.getY();
        final double x2 = rect2.getX();
        final double width = rect2.getWidth();
        final double y2 = rect2.getY();
        final double height = rect2.getHeight();
        return x2 + width >= x0 && y2 + height >= y0 && x2 <= x0 + rect1.getWidth() && y2 <= y0 + rect1.getHeight();
    }
    
    public static Shape rotateShape(final Shape base, final double angle, final float x, final float y) {
        if (base == null) {
            return null;
        }
        final AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
        final Shape result = rotate.createTransformedShape(base);
        return result;
    }
}

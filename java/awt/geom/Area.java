package java.awt.geom;

import sun.awt.geom.Crossings;
import java.awt.Rectangle;
import java.util.Enumeration;
import sun.awt.geom.AreaOp;
import sun.awt.geom.Curve;
import java.util.Vector;
import java.awt.Shape;

public class Area implements Shape, Cloneable
{
    private static Vector EmptyCurves;
    private Vector curves;
    private Rectangle2D cachedBounds;
    
    public Area() {
        this.curves = Area.EmptyCurves;
    }
    
    public Area(final Shape shape) {
        if (shape instanceof Area) {
            this.curves = ((Area)shape).curves;
        }
        else {
            this.curves = pathToCurves(shape.getPathIterator(null));
        }
    }
    
    private static Vector pathToCurves(final PathIterator pathIterator) {
        final Vector vector = new Vector();
        final int windingRule = pathIterator.getWindingRule();
        final double[] array = new double[23];
        double n = 0.0;
        double n2 = 0.0;
        double n3 = 0.0;
        double n4 = 0.0;
        while (!pathIterator.isDone()) {
            switch (pathIterator.currentSegment(array)) {
                case 0: {
                    Curve.insertLine(vector, n3, n4, n, n2);
                    n = (n3 = array[0]);
                    n2 = (n4 = array[1]);
                    Curve.insertMove(vector, n, n2);
                    break;
                }
                case 1: {
                    final double n5 = array[0];
                    final double n6 = array[1];
                    Curve.insertLine(vector, n3, n4, n5, n6);
                    n3 = n5;
                    n4 = n6;
                    break;
                }
                case 2: {
                    final double n7 = array[2];
                    final double n8 = array[3];
                    Curve.insertQuad(vector, n3, n4, array);
                    n3 = n7;
                    n4 = n8;
                    break;
                }
                case 3: {
                    final double n9 = array[4];
                    final double n10 = array[5];
                    Curve.insertCubic(vector, n3, n4, array);
                    n3 = n9;
                    n4 = n10;
                    break;
                }
                case 4: {
                    Curve.insertLine(vector, n3, n4, n, n2);
                    n3 = n;
                    n4 = n2;
                    break;
                }
            }
            pathIterator.next();
        }
        Curve.insertLine(vector, n3, n4, n, n2);
        AreaOp areaOp;
        if (windingRule == 0) {
            areaOp = new AreaOp.EOWindOp();
        }
        else {
            areaOp = new AreaOp.NZWindOp();
        }
        return areaOp.calculate(vector, Area.EmptyCurves);
    }
    
    public void add(final Area area) {
        this.curves = new AreaOp.AddOp().calculate(this.curves, area.curves);
        this.invalidateBounds();
    }
    
    public void subtract(final Area area) {
        this.curves = new AreaOp.SubOp().calculate(this.curves, area.curves);
        this.invalidateBounds();
    }
    
    public void intersect(final Area area) {
        this.curves = new AreaOp.IntOp().calculate(this.curves, area.curves);
        this.invalidateBounds();
    }
    
    public void exclusiveOr(final Area area) {
        this.curves = new AreaOp.XorOp().calculate(this.curves, area.curves);
        this.invalidateBounds();
    }
    
    public void reset() {
        this.curves = new Vector();
        this.invalidateBounds();
    }
    
    public boolean isEmpty() {
        return this.curves.size() == 0;
    }
    
    public boolean isPolygonal() {
        final Enumeration elements = this.curves.elements();
        while (elements.hasMoreElements()) {
            if (((Curve)elements.nextElement()).getOrder() > 1) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isRectangular() {
        final int size = this.curves.size();
        if (size == 0) {
            return true;
        }
        if (size > 3) {
            return false;
        }
        final Curve curve = this.curves.get(1);
        final Curve curve2 = this.curves.get(2);
        return curve.getOrder() == 1 && curve2.getOrder() == 1 && curve.getXTop() == curve.getXBot() && curve2.getXTop() == curve2.getXBot() && curve.getYTop() == curve2.getYTop() && curve.getYBot() == curve2.getYBot();
    }
    
    public boolean isSingular() {
        if (this.curves.size() < 3) {
            return true;
        }
        final Enumeration elements = this.curves.elements();
        elements.nextElement();
        while (elements.hasMoreElements()) {
            if (((Curve)elements.nextElement()).getOrder() == 0) {
                return false;
            }
        }
        return true;
    }
    
    private void invalidateBounds() {
        this.cachedBounds = null;
    }
    
    private Rectangle2D getCachedBounds() {
        if (this.cachedBounds != null) {
            return this.cachedBounds;
        }
        final Rectangle2D.Double cachedBounds = new Rectangle2D.Double();
        if (this.curves.size() > 0) {
            final Curve curve = this.curves.get(0);
            cachedBounds.setRect(curve.getX0(), curve.getY0(), 0.0, 0.0);
            for (int i = 1; i < this.curves.size(); ++i) {
                ((Curve)this.curves.get(i)).enlarge(cachedBounds);
            }
        }
        return this.cachedBounds = cachedBounds;
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getCachedBounds().getBounds2D();
    }
    
    @Override
    public Rectangle getBounds() {
        return this.getCachedBounds().getBounds();
    }
    
    public Object clone() {
        return new Area(this);
    }
    
    public boolean equals(final Area area) {
        return area == this || (area != null && new AreaOp.XorOp().calculate(this.curves, area.curves).isEmpty());
    }
    
    public void transform(final AffineTransform affineTransform) {
        if (affineTransform == null) {
            throw new NullPointerException("transform must not be null");
        }
        this.curves = pathToCurves(this.getPathIterator(affineTransform));
        this.invalidateBounds();
    }
    
    public Area createTransformedArea(final AffineTransform affineTransform) {
        final Area area = new Area(this);
        area.transform(affineTransform);
        return area;
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        if (!this.getCachedBounds().contains(n, n2)) {
            return false;
        }
        final Enumeration elements = this.curves.elements();
        int n3 = 0;
        while (elements.hasMoreElements()) {
            n3 += ((Curve)elements.nextElement()).crossingsFor(n, n2);
        }
        return (n3 & 0x1) == 0x1;
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return this.contains(point2D.getX(), point2D.getY());
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        if (n3 < 0.0 || n4 < 0.0) {
            return false;
        }
        if (!this.getCachedBounds().contains(n, n2, n3, n4)) {
            return false;
        }
        final Crossings crossings = Crossings.findCrossings(this.curves, n, n2, n + n3, n2 + n4);
        return crossings != null && crossings.covers(n2, n2 + n4);
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return this.contains(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        if (n3 < 0.0 || n4 < 0.0) {
            return false;
        }
        if (!this.getCachedBounds().intersects(n, n2, n3, n4)) {
            return false;
        }
        final Crossings crossings = Crossings.findCrossings(this.curves, n, n2, n + n3, n2 + n4);
        return crossings == null || !crossings.isEmpty();
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return this.intersects(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new AreaIterator(this.curves, affineTransform);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return new FlatteningPathIterator(this.getPathIterator(affineTransform), n);
    }
    
    static {
        Area.EmptyCurves = new Vector();
    }
}

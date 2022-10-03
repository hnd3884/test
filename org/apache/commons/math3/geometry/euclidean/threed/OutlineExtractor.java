package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.partitioning.AbstractRegion;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.Point;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class OutlineExtractor
{
    private Vector3D u;
    private Vector3D v;
    private Vector3D w;
    
    public OutlineExtractor(final Vector3D u, final Vector3D v) {
        this.u = u;
        this.v = v;
        this.w = Vector3D.crossProduct(u, v);
    }
    
    public Vector2D[][] getOutline(final PolyhedronsSet polyhedronsSet) {
        final BoundaryProjector projector = new BoundaryProjector(polyhedronsSet.getTolerance());
        ((AbstractRegion<Euclidean3D, T>)polyhedronsSet).getTree(true).visit(projector);
        final PolygonsSet projected = projector.getProjected();
        final Vector2D[][] outline = projected.getVertices();
        for (int i = 0; i < outline.length; ++i) {
            final Vector2D[] rawLoop = outline[i];
            int end = rawLoop.length;
            int j = 0;
            while (j < end) {
                if (this.pointIsBetween(rawLoop, end, j)) {
                    for (int k = j; k < end - 1; ++k) {
                        rawLoop[k] = rawLoop[k + 1];
                    }
                    --end;
                }
                else {
                    ++j;
                }
            }
            if (end != rawLoop.length) {
                System.arraycopy(rawLoop, 0, outline[i] = new Vector2D[end], 0, end);
            }
        }
        return outline;
    }
    
    private boolean pointIsBetween(final Vector2D[] loop, final int n, final int i) {
        final Vector2D previous = loop[(i + n - 1) % n];
        final Vector2D current = loop[i];
        final Vector2D next = loop[(i + 1) % n];
        final double dx1 = current.getX() - previous.getX();
        final double dy1 = current.getY() - previous.getY();
        final double dx2 = next.getX() - current.getX();
        final double dy2 = next.getY() - current.getY();
        final double cross = dx1 * dy2 - dx2 * dy1;
        final double dot = dx1 * dx2 + dy1 * dy2;
        final double d1d2 = FastMath.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2));
        return FastMath.abs(cross) <= 1.0E-6 * d1d2 && dot >= 0.0;
    }
    
    private class BoundaryProjector implements BSPTreeVisitor<Euclidean3D>
    {
        private PolygonsSet projected;
        private final double tolerance;
        
        BoundaryProjector(final double tolerance) {
            this.projected = new PolygonsSet(new BSPTree<Euclidean2D>(Boolean.FALSE), tolerance);
            this.tolerance = tolerance;
        }
        
        public Order visitOrder(final BSPTree<Euclidean3D> node) {
            return Order.MINUS_SUB_PLUS;
        }
        
        public void visitInternalNode(final BSPTree<Euclidean3D> node) {
            final BoundaryAttribute<Euclidean3D> attribute = (BoundaryAttribute<Euclidean3D>)node.getAttribute();
            if (attribute.getPlusOutside() != null) {
                this.addContribution(attribute.getPlusOutside(), false);
            }
            if (attribute.getPlusInside() != null) {
                this.addContribution(attribute.getPlusInside(), true);
            }
        }
        
        public void visitLeafNode(final BSPTree<Euclidean3D> node) {
        }
        
        private void addContribution(final SubHyperplane<Euclidean3D> facet, final boolean reversed) {
            final AbstractSubHyperplane<Euclidean3D, Euclidean2D> absFacet = (AbstractSubHyperplane)facet;
            final Plane plane = (Plane)facet.getHyperplane();
            final double scal = plane.getNormal().dotProduct(OutlineExtractor.this.w);
            if (FastMath.abs(scal) > 0.001) {
                Vector2D[][] vertices = ((PolygonsSet)absFacet.getRemainingRegion()).getVertices();
                if (scal < 0.0 ^ reversed) {
                    final Vector2D[][] newVertices = new Vector2D[vertices.length][];
                    for (int i = 0; i < vertices.length; ++i) {
                        final Vector2D[] loop = vertices[i];
                        final Vector2D[] newLoop = new Vector2D[loop.length];
                        if (loop[0] == null) {
                            newLoop[0] = null;
                            for (int j = 1; j < loop.length; ++j) {
                                newLoop[j] = loop[loop.length - j];
                            }
                        }
                        else {
                            for (int j = 0; j < loop.length; ++j) {
                                newLoop[j] = loop[loop.length - (j + 1)];
                            }
                        }
                        newVertices[i] = newLoop;
                    }
                    vertices = newVertices;
                }
                final ArrayList<SubHyperplane<Euclidean2D>> edges = new ArrayList<SubHyperplane<Euclidean2D>>();
                for (final Vector2D[] loop2 : vertices) {
                    final boolean closed = loop2[0] != null;
                    int previous = closed ? (loop2.length - 1) : 1;
                    Vector3D previous3D = plane.toSpace((Point<Euclidean2D>)loop2[previous]);
                    int current = (previous + 1) % loop2.length;
                    Vector2D pPoint = new Vector2D(previous3D.dotProduct(OutlineExtractor.this.u), previous3D.dotProduct(OutlineExtractor.this.v));
                    while (current < loop2.length) {
                        final Vector3D current3D = plane.toSpace((Point<Euclidean2D>)loop2[current]);
                        final Vector2D cPoint = new Vector2D(current3D.dotProduct(OutlineExtractor.this.u), current3D.dotProduct(OutlineExtractor.this.v));
                        final Line line = new Line(pPoint, cPoint, this.tolerance);
                        SubHyperplane<Euclidean2D> edge = line.wholeHyperplane();
                        if (closed || previous != 1) {
                            final double angle = line.getAngle() + 1.5707963267948966;
                            final Line l = new Line(pPoint, angle, this.tolerance);
                            edge = edge.split(l).getPlus();
                        }
                        if (closed || current != loop2.length - 1) {
                            final double angle = line.getAngle() + 1.5707963267948966;
                            final Line l = new Line(cPoint, angle, this.tolerance);
                            edge = edge.split(l).getMinus();
                        }
                        edges.add(edge);
                        previous = current++;
                        previous3D = current3D;
                        pPoint = cPoint;
                    }
                }
                final PolygonsSet projectedFacet = new PolygonsSet(edges, this.tolerance);
                this.projected = (PolygonsSet)new RegionFactory().union((Region)this.projected, (Region)projectedFacet);
            }
        }
        
        public PolygonsSet getProjected() {
            return this.projected;
        }
    }
}

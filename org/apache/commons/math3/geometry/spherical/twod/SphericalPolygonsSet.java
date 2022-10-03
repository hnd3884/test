package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import org.apache.commons.math3.geometry.partitioning.BoundaryProjection;
import org.apache.commons.math3.geometry.enclosing.SupportBallGenerator;
import org.apache.commons.math3.geometry.euclidean.threed.Euclidean3D;
import org.apache.commons.math3.geometry.enclosing.WelzlEncloser;
import org.apache.commons.math3.geometry.euclidean.threed.SphereGenerator;
import org.apache.commons.math3.geometry.enclosing.EnclosingBall;
import java.util.Collections;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import java.util.Iterator;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Point;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import java.util.List;
import org.apache.commons.math3.geometry.spherical.oned.Sphere1D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class SphericalPolygonsSet extends AbstractRegion<Sphere2D, Sphere1D>
{
    private List<Vertex> loops;
    
    public SphericalPolygonsSet(final double tolerance) {
        super(tolerance);
    }
    
    public SphericalPolygonsSet(final Vector3D pole, final double tolerance) {
        super(new BSPTree(new Circle(pole, tolerance).wholeHyperplane(), new BSPTree(Boolean.FALSE), new BSPTree(Boolean.TRUE), null), tolerance);
    }
    
    public SphericalPolygonsSet(final Vector3D center, final Vector3D meridian, final double outsideRadius, final int n, final double tolerance) {
        this(tolerance, createRegularPolygonVertices(center, meridian, outsideRadius, n));
    }
    
    public SphericalPolygonsSet(final BSPTree<Sphere2D> tree, final double tolerance) {
        super(tree, tolerance);
    }
    
    public SphericalPolygonsSet(final Collection<SubHyperplane<Sphere2D>> boundary, final double tolerance) {
        super(boundary, tolerance);
    }
    
    public SphericalPolygonsSet(final double hyperplaneThickness, final S2Point... vertices) {
        super(verticesToTree(hyperplaneThickness, vertices), hyperplaneThickness);
    }
    
    private static S2Point[] createRegularPolygonVertices(final Vector3D center, final Vector3D meridian, final double outsideRadius, final int n) {
        final S2Point[] array = new S2Point[n];
        final Rotation r0 = new Rotation(Vector3D.crossProduct(center, meridian), outsideRadius, RotationConvention.VECTOR_OPERATOR);
        array[0] = new S2Point(r0.applyTo(center));
        final Rotation r2 = new Rotation(center, 6.283185307179586 / n, RotationConvention.VECTOR_OPERATOR);
        for (int i = 1; i < n; ++i) {
            array[i] = new S2Point(r2.applyTo(array[i - 1].getVector()));
        }
        return array;
    }
    
    private static BSPTree<Sphere2D> verticesToTree(final double hyperplaneThickness, final S2Point... vertices) {
        final int n = vertices.length;
        if (n == 0) {
            return new BSPTree<Sphere2D>(Boolean.TRUE);
        }
        final Vertex[] vArray = new Vertex[n];
        for (int i = 0; i < n; ++i) {
            vArray[i] = new Vertex(vertices[i]);
        }
        final List<Edge> edges = new ArrayList<Edge>(n);
        Vertex end = vArray[n - 1];
        for (int j = 0; j < n; ++j) {
            final Vertex start = end;
            end = vArray[j];
            Circle circle = start.sharedCircleWith(end);
            if (circle == null) {
                circle = new Circle(start.getLocation(), end.getLocation(), hyperplaneThickness);
            }
            edges.add(new Edge(start, end, Vector3D.angle(start.getLocation().getVector(), end.getLocation().getVector()), circle));
            for (final Vertex vertex : vArray) {
                if (vertex != start && vertex != end && FastMath.abs(circle.getOffset(vertex.getLocation())) <= hyperplaneThickness) {
                    vertex.bindWith(circle);
                }
            }
        }
        final BSPTree<Sphere2D> tree = new BSPTree<Sphere2D>();
        insertEdges(hyperplaneThickness, tree, edges);
        return tree;
    }
    
    private static void insertEdges(final double hyperplaneThickness, final BSPTree<Sphere2D> node, final List<Edge> edges) {
        int index;
        Edge inserted;
        for (index = 0, inserted = null; inserted == null && index < edges.size(); inserted = null) {
            inserted = edges.get(index++);
            if (!node.insertCut(inserted.getCircle())) {}
        }
        if (inserted == null) {
            final BSPTree<Sphere2D> parent = node.getParent();
            if (parent == null || node == parent.getMinus()) {
                node.setAttribute(Boolean.TRUE);
            }
            else {
                node.setAttribute(Boolean.FALSE);
            }
            return;
        }
        final List<Edge> outsideList = new ArrayList<Edge>();
        final List<Edge> insideList = new ArrayList<Edge>();
        for (final Edge edge : edges) {
            if (edge != inserted) {
                edge.split(inserted.getCircle(), outsideList, insideList);
            }
        }
        if (!outsideList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getPlus(), outsideList);
        }
        else {
            node.getPlus().setAttribute(Boolean.FALSE);
        }
        if (!insideList.isEmpty()) {
            insertEdges(hyperplaneThickness, node.getMinus(), insideList);
        }
        else {
            node.getMinus().setAttribute(Boolean.TRUE);
        }
    }
    
    @Override
    public SphericalPolygonsSet buildNew(final BSPTree<Sphere2D> tree) {
        return new SphericalPolygonsSet(tree, this.getTolerance());
    }
    
    @Override
    protected void computeGeometricalProperties() throws MathIllegalStateException {
        final BSPTree<Sphere2D> tree = ((AbstractRegion<Sphere2D, T>)this).getTree(true);
        if (tree.getCut() == null) {
            if (tree.getCut() == null && (boolean)tree.getAttribute()) {
                this.setSize(12.566370614359172);
                ((AbstractRegion<Sphere2D, T>)this).setBarycenter(new S2Point(0.0, 0.0));
            }
            else {
                this.setSize(0.0);
                ((AbstractRegion<Sphere2D, T>)this).setBarycenter(S2Point.NaN);
            }
        }
        else {
            final PropertiesComputer pc = new PropertiesComputer(this.getTolerance());
            tree.visit(pc);
            this.setSize(pc.getArea());
            ((AbstractRegion<Sphere2D, T>)this).setBarycenter(pc.getBarycenter());
        }
    }
    
    public List<Vertex> getBoundaryLoops() throws MathIllegalStateException {
        if (this.loops == null) {
            if (((AbstractRegion<Sphere2D, T>)this).getTree(false).getCut() == null) {
                this.loops = Collections.emptyList();
            }
            else {
                final BSPTree<Sphere2D> root = ((AbstractRegion<Sphere2D, T>)this).getTree(true);
                final EdgesBuilder visitor = new EdgesBuilder(root, this.getTolerance());
                root.visit(visitor);
                final List<Edge> edges = visitor.getEdges();
                this.loops = new ArrayList<Vertex>();
                while (!edges.isEmpty()) {
                    Edge edge = edges.get(0);
                    final Vertex startVertex = edge.getStart();
                    this.loops.add(startVertex);
                    do {
                        final Iterator<Edge> iterator = edges.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next() == edge) {
                                iterator.remove();
                                break;
                            }
                        }
                        edge = edge.getEnd().getOutgoing();
                    } while (edge.getStart() != startVertex);
                }
            }
        }
        return Collections.unmodifiableList((List<? extends Vertex>)this.loops);
    }
    
    public EnclosingBall<Sphere2D, S2Point> getEnclosingCap() {
        if (this.isEmpty()) {
            return new EnclosingBall<Sphere2D, S2Point>(S2Point.PLUS_K, Double.NEGATIVE_INFINITY, new S2Point[0]);
        }
        if (this.isFull()) {
            return new EnclosingBall<Sphere2D, S2Point>(S2Point.PLUS_K, Double.POSITIVE_INFINITY, new S2Point[0]);
        }
        final BSPTree<Sphere2D> root = ((AbstractRegion<Sphere2D, T>)this).getTree(false);
        if (((AbstractRegion<Sphere2D, T>)this).isEmpty(root.getMinus()) && ((AbstractRegion<Sphere2D, T>)this).isFull(root.getPlus())) {
            final Circle circle = (Circle)root.getCut().getHyperplane();
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(circle.getPole()).negate(), 1.5707963267948966, new S2Point[0]);
        }
        if (((AbstractRegion<Sphere2D, T>)this).isFull(root.getMinus()) && ((AbstractRegion<Sphere2D, T>)this).isEmpty(root.getPlus())) {
            final Circle circle = (Circle)root.getCut().getHyperplane();
            return new EnclosingBall<Sphere2D, S2Point>(new S2Point(circle.getPole()), 1.5707963267948966, new S2Point[0]);
        }
        final List<Vector3D> points = this.getInsidePoints();
        final List<Vertex> boundary = this.getBoundaryLoops();
        for (final Vertex loopStart : boundary) {
            int count = 0;
            for (Vertex v = loopStart; count == 0 || v != loopStart; v = v.getOutgoing().getEnd()) {
                ++count;
                points.add(v.getLocation().getVector());
            }
        }
        final SphereGenerator generator = new SphereGenerator();
        final WelzlEncloser<Euclidean3D, Vector3D> encloser = new WelzlEncloser<Euclidean3D, Vector3D>(this.getTolerance(), generator);
        final EnclosingBall<Euclidean3D, Vector3D> enclosing3D = encloser.enclose(points);
        final Vector3D[] support3D = enclosing3D.getSupport();
        final double r = enclosing3D.getRadius();
        final double h = enclosing3D.getCenter().getNorm();
        if (h < this.getTolerance()) {
            EnclosingBall<Sphere2D, S2Point> enclosingS2 = new EnclosingBall<Sphere2D, S2Point>(S2Point.PLUS_K, Double.POSITIVE_INFINITY, new S2Point[0]);
            for (final Vector3D outsidePoint : this.getOutsidePoints()) {
                final S2Point outsideS2 = new S2Point(outsidePoint);
                final BoundaryProjection<Sphere2D> projection = ((AbstractRegion<Sphere2D, T>)this).projectToBoundary(outsideS2);
                if (3.141592653589793 - projection.getOffset() < enclosingS2.getRadius()) {
                    enclosingS2 = new EnclosingBall<Sphere2D, S2Point>(outsideS2.negate(), 3.141592653589793 - projection.getOffset(), new S2Point[] { (S2Point)projection.getProjected() });
                }
            }
            return enclosingS2;
        }
        final S2Point[] support = new S2Point[support3D.length];
        for (int i = 0; i < support3D.length; ++i) {
            support[i] = new S2Point(support3D[i]);
        }
        final EnclosingBall<Sphere2D, S2Point> enclosingS3 = new EnclosingBall<Sphere2D, S2Point>(new S2Point(enclosing3D.getCenter()), FastMath.acos((1.0 + h * h - r * r) / (2.0 * h)), support);
        return enclosingS3;
    }
    
    private List<Vector3D> getInsidePoints() {
        final PropertiesComputer pc = new PropertiesComputer(this.getTolerance());
        ((AbstractRegion<Sphere2D, T>)this).getTree(true).visit(pc);
        return pc.getConvexCellsInsidePoints();
    }
    
    private List<Vector3D> getOutsidePoints() {
        final SphericalPolygonsSet complement = (SphericalPolygonsSet)new RegionFactory().getComplement((Region)this);
        final PropertiesComputer pc = new PropertiesComputer(this.getTolerance());
        ((AbstractRegion<Sphere2D, T>)complement).getTree(true).visit(pc);
        return pc.getConvexCellsInsidePoints();
    }
}

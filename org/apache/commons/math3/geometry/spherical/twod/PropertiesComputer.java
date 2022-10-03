package org.apache.commons.math3.geometry.spherical.twod;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;

class PropertiesComputer implements BSPTreeVisitor<Sphere2D>
{
    private final double tolerance;
    private double summedArea;
    private Vector3D summedBarycenter;
    private final List<Vector3D> convexCellsInsidePoints;
    
    PropertiesComputer(final double tolerance) {
        this.tolerance = tolerance;
        this.summedArea = 0.0;
        this.summedBarycenter = Vector3D.ZERO;
        this.convexCellsInsidePoints = new ArrayList<Vector3D>();
    }
    
    public Order visitOrder(final BSPTree<Sphere2D> node) {
        return Order.MINUS_SUB_PLUS;
    }
    
    public void visitInternalNode(final BSPTree<Sphere2D> node) {
    }
    
    public void visitLeafNode(final BSPTree<Sphere2D> node) {
        if (node.getAttribute()) {
            final SphericalPolygonsSet convex = new SphericalPolygonsSet(node.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null), this.tolerance);
            final List<Vertex> boundary = convex.getBoundaryLoops();
            if (boundary.size() != 1) {
                throw new MathInternalError();
            }
            final double area = this.convexCellArea(boundary.get(0));
            final Vector3D barycenter = this.convexCellBarycenter(boundary.get(0));
            this.convexCellsInsidePoints.add(barycenter);
            this.summedArea += area;
            this.summedBarycenter = new Vector3D(1.0, this.summedBarycenter, area, barycenter);
        }
    }
    
    private double convexCellArea(final Vertex start) {
        int n = 0;
        double sum = 0.0;
        for (Edge e = start.getOutgoing(); n == 0 || e.getStart() != start; ++n, e = e.getEnd().getOutgoing()) {
            final Vector3D previousPole = e.getCircle().getPole();
            final Vector3D nextPole = e.getEnd().getOutgoing().getCircle().getPole();
            final Vector3D point = e.getEnd().getLocation().getVector();
            double alpha = FastMath.atan2(Vector3D.dotProduct(nextPole, Vector3D.crossProduct(point, previousPole)), -Vector3D.dotProduct(nextPole, previousPole));
            if (alpha < 0.0) {
                alpha += 6.283185307179586;
            }
            sum += alpha;
        }
        return sum - (n - 2) * 3.141592653589793;
    }
    
    private Vector3D convexCellBarycenter(final Vertex start) {
        int n = 0;
        Vector3D sumB = Vector3D.ZERO;
        for (Edge e = start.getOutgoing(); n == 0 || e.getStart() != start; ++n, e = e.getEnd().getOutgoing()) {
            sumB = new Vector3D(1.0, sumB, e.getLength(), e.getCircle().getPole());
        }
        return sumB.normalize();
    }
    
    public double getArea() {
        return this.summedArea;
    }
    
    public S2Point getBarycenter() {
        if (this.summedBarycenter.getNormSq() == 0.0) {
            return S2Point.NaN;
        }
        return new S2Point(this.summedBarycenter);
    }
    
    public List<Vector3D> getConvexCellsInsidePoints() {
        return this.convexCellsInsidePoints;
    }
}

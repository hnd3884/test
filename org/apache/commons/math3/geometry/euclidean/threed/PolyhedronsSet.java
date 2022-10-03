package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;
import org.apache.commons.math3.geometry.euclidean.twod.SubLine;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.partitioning.Transform;
import org.apache.commons.math3.geometry.partitioning.BoundaryAttribute;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.BSPTreeVisitor;
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import java.util.Iterator;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import java.util.ArrayList;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.partitioning.RegionFactory;
import java.util.List;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class PolyhedronsSet extends AbstractRegion<Euclidean3D, Euclidean2D>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    
    public PolyhedronsSet(final double tolerance) {
        super(tolerance);
    }
    
    public PolyhedronsSet(final BSPTree<Euclidean3D> tree, final double tolerance) {
        super(tree, tolerance);
    }
    
    public PolyhedronsSet(final Collection<SubHyperplane<Euclidean3D>> boundary, final double tolerance) {
        super(boundary, tolerance);
    }
    
    public PolyhedronsSet(final List<Vector3D> vertices, final List<int[]> facets, final double tolerance) {
        super(buildBoundary(vertices, facets, tolerance), tolerance);
    }
    
    public PolyhedronsSet(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax, final double tolerance) {
        super(buildBoundary(xMin, xMax, yMin, yMax, zMin, zMax, tolerance), tolerance);
    }
    
    @Deprecated
    public PolyhedronsSet() {
        this(1.0E-10);
    }
    
    @Deprecated
    public PolyhedronsSet(final BSPTree<Euclidean3D> tree) {
        this(tree, 1.0E-10);
    }
    
    @Deprecated
    public PolyhedronsSet(final Collection<SubHyperplane<Euclidean3D>> boundary) {
        this(boundary, 1.0E-10);
    }
    
    @Deprecated
    public PolyhedronsSet(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax) {
        this(xMin, xMax, yMin, yMax, zMin, zMax, 1.0E-10);
    }
    
    private static BSPTree<Euclidean3D> buildBoundary(final double xMin, final double xMax, final double yMin, final double yMax, final double zMin, final double zMax, final double tolerance) {
        if (xMin >= xMax - tolerance || yMin >= yMax - tolerance || zMin >= zMax - tolerance) {
            return new BSPTree<Euclidean3D>(Boolean.FALSE);
        }
        final Plane pxMin = new Plane(new Vector3D(xMin, 0.0, 0.0), Vector3D.MINUS_I, tolerance);
        final Plane pxMax = new Plane(new Vector3D(xMax, 0.0, 0.0), Vector3D.PLUS_I, tolerance);
        final Plane pyMin = new Plane(new Vector3D(0.0, yMin, 0.0), Vector3D.MINUS_J, tolerance);
        final Plane pyMax = new Plane(new Vector3D(0.0, yMax, 0.0), Vector3D.PLUS_J, tolerance);
        final Plane pzMin = new Plane(new Vector3D(0.0, 0.0, zMin), Vector3D.MINUS_K, tolerance);
        final Plane pzMax = new Plane(new Vector3D(0.0, 0.0, zMax), Vector3D.PLUS_K, tolerance);
        final Region<Euclidean3D> boundary = new RegionFactory<Euclidean3D>().buildConvex(pxMin, pxMax, pyMin, pyMax, pzMin, pzMax);
        return boundary.getTree(false);
    }
    
    private static List<SubHyperplane<Euclidean3D>> buildBoundary(final List<Vector3D> vertices, final List<int[]> facets, final double tolerance) {
        for (int i = 0; i < vertices.size() - 1; ++i) {
            final Vector3D vi = vertices.get(i);
            for (int j = i + 1; j < vertices.size(); ++j) {
                if (Vector3D.distance(vi, vertices.get(j)) <= tolerance) {
                    throw new MathIllegalArgumentException(LocalizedFormats.CLOSE_VERTICES, new Object[] { vi.getX(), vi.getY(), vi.getZ() });
                }
            }
        }
        final int[][] references = findReferences(vertices, facets);
        final int[][] successors = successors(vertices, facets, references);
        for (int vA = 0; vA < vertices.size(); ++vA) {
            for (final int vB : successors[vA]) {
                if (vB >= 0) {
                    boolean found = false;
                    for (final int v : successors[vB]) {
                        found = (found || v == vA);
                    }
                    if (!found) {
                        final Vector3D start = vertices.get(vA);
                        final Vector3D end = vertices.get(vB);
                        throw new MathIllegalArgumentException(LocalizedFormats.EDGE_CONNECTED_TO_ONE_FACET, new Object[] { start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ() });
                    }
                }
            }
        }
        final List<SubHyperplane<Euclidean3D>> boundary = new ArrayList<SubHyperplane<Euclidean3D>>();
        for (final int[] facet : facets) {
            final Plane plane = new Plane(vertices.get(facet[0]), vertices.get(facet[1]), vertices.get(facet[2]), tolerance);
            final Vector2D[] two2Points = new Vector2D[facet.length];
            for (int k = 0; k < facet.length; ++k) {
                final Vector3D v2 = vertices.get(facet[k]);
                if (!plane.contains(v2)) {
                    throw new MathIllegalArgumentException(LocalizedFormats.OUT_OF_PLANE, new Object[] { v2.getX(), v2.getY(), v2.getZ() });
                }
                two2Points[k] = plane.toSubSpace(v2);
            }
            boundary.add(new SubPlane(plane, new PolygonsSet(tolerance, two2Points)));
        }
        return boundary;
    }
    
    private static int[][] findReferences(final List<Vector3D> vertices, final List<int[]> facets) {
        final int[] nbFacets = new int[vertices.size()];
        int maxFacets = 0;
        for (final int[] facet : facets) {
            if (facet.length < 3) {
                throw new NumberIsTooSmallException(LocalizedFormats.WRONG_NUMBER_OF_POINTS, 3, facet.length, true);
            }
            for (final int index : facet) {
                maxFacets = FastMath.max(maxFacets, ++nbFacets[index]);
            }
        }
        final int[][] arr$2;
        final int[][] references = arr$2 = new int[vertices.size()][maxFacets];
        for (final int[] r : arr$2) {
            Arrays.fill(r, -1);
        }
        for (int f = 0; f < facets.size(); ++f) {
            for (final int v : facets.get(f)) {
                int k;
                for (k = 0; k < maxFacets && references[v][k] >= 0; ++k) {}
                references[v][k] = f;
            }
        }
        return references;
    }
    
    private static int[][] successors(final List<Vector3D> vertices, final List<int[]> facets, final int[][] references) {
        final int[][] arr$;
        final int[][] successors = arr$ = new int[vertices.size()][references[0].length];
        for (final int[] s : arr$) {
            Arrays.fill(s, -1);
        }
        for (int v = 0; v < vertices.size(); ++v) {
            for (int k = 0; k < successors[v].length && references[v][k] >= 0; ++k) {
                int[] facet;
                int i;
                for (facet = facets.get(references[v][k]), i = 0; i < facet.length && facet[i] != v; ++i) {}
                successors[v][k] = facet[(i + 1) % facet.length];
                for (int l = 0; l < k; ++l) {
                    if (successors[v][l] == successors[v][k]) {
                        final Vector3D start = vertices.get(v);
                        final Vector3D end = vertices.get(successors[v][k]);
                        throw new MathIllegalArgumentException(LocalizedFormats.FACET_ORIENTATION_MISMATCH, new Object[] { start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ() });
                    }
                }
            }
        }
        return successors;
    }
    
    @Override
    public PolyhedronsSet buildNew(final BSPTree<Euclidean3D> tree) {
        return new PolyhedronsSet(tree, this.getTolerance());
    }
    
    @Override
    protected void computeGeometricalProperties() {
        ((AbstractRegion<Euclidean3D, T>)this).getTree(true).visit(new FacetsContributionVisitor());
        if (this.getSize() < 0.0) {
            this.setSize(Double.POSITIVE_INFINITY);
            ((AbstractRegion<Euclidean3D, T>)this).setBarycenter((Point<Euclidean3D>)Vector3D.NaN);
        }
        else {
            this.setSize(this.getSize() / 3.0);
            ((AbstractRegion<Euclidean3D, T>)this).setBarycenter((Point<Euclidean3D>)new Vector3D(1.0 / (4.0 * this.getSize()), (Vector3D)this.getBarycenter()));
        }
    }
    
    public SubHyperplane<Euclidean3D> firstIntersection(final Vector3D point, final Line line) {
        return this.recurseFirstIntersection(((AbstractRegion<Euclidean3D, T>)this).getTree(true), point, line);
    }
    
    private SubHyperplane<Euclidean3D> recurseFirstIntersection(final BSPTree<Euclidean3D> node, final Vector3D point, final Line line) {
        final SubHyperplane<Euclidean3D> cut = node.getCut();
        if (cut == null) {
            return null;
        }
        final BSPTree<Euclidean3D> minus = node.getMinus();
        final BSPTree<Euclidean3D> plus = node.getPlus();
        final Plane plane = (Plane)cut.getHyperplane();
        final double offset = plane.getOffset((Point<Euclidean3D>)point);
        final boolean in = FastMath.abs(offset) < this.getTolerance();
        BSPTree<Euclidean3D> near;
        BSPTree<Euclidean3D> far;
        if (offset < 0.0) {
            near = minus;
            far = plus;
        }
        else {
            near = plus;
            far = minus;
        }
        if (in) {
            final SubHyperplane<Euclidean3D> facet = this.boundaryFacet(point, node);
            if (facet != null) {
                return facet;
            }
        }
        final SubHyperplane<Euclidean3D> crossed = this.recurseFirstIntersection(near, point, line);
        if (crossed != null) {
            return crossed;
        }
        if (!in) {
            final Vector3D hit3D = plane.intersection(line);
            if (hit3D != null && line.getAbscissa(hit3D) > line.getAbscissa(point)) {
                final SubHyperplane<Euclidean3D> facet2 = this.boundaryFacet(hit3D, node);
                if (facet2 != null) {
                    return facet2;
                }
            }
        }
        return this.recurseFirstIntersection(far, point, line);
    }
    
    private SubHyperplane<Euclidean3D> boundaryFacet(final Vector3D point, final BSPTree<Euclidean3D> node) {
        final Vector2D point2D = ((Plane)node.getCut().getHyperplane()).toSubSpace((Point<Euclidean3D>)point);
        final BoundaryAttribute<Euclidean3D> attribute = (BoundaryAttribute<Euclidean3D>)node.getAttribute();
        if (attribute.getPlusOutside() != null && ((AbstractSubHyperplane<S, Euclidean2D>)attribute.getPlusOutside()).getRemainingRegion().checkPoint(point2D) == Region.Location.INSIDE) {
            return attribute.getPlusOutside();
        }
        if (attribute.getPlusInside() != null && ((AbstractSubHyperplane<S, Euclidean2D>)attribute.getPlusInside()).getRemainingRegion().checkPoint(point2D) == Region.Location.INSIDE) {
            return attribute.getPlusInside();
        }
        return null;
    }
    
    public PolyhedronsSet rotate(final Vector3D center, final Rotation rotation) {
        return (PolyhedronsSet)this.applyTransform(new RotationTransform(center, rotation));
    }
    
    public PolyhedronsSet translate(final Vector3D translation) {
        return (PolyhedronsSet)this.applyTransform(new TranslationTransform(translation));
    }
    
    private class FacetsContributionVisitor implements BSPTreeVisitor<Euclidean3D>
    {
        FacetsContributionVisitor() {
            PolyhedronsSet.this.setSize(0.0);
            ((AbstractRegion<Euclidean3D, T>)PolyhedronsSet.this).setBarycenter(new Vector3D(0.0, 0.0, 0.0));
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
            final Region<Euclidean2D> polygon = ((AbstractSubHyperplane<S, Euclidean2D>)facet).getRemainingRegion();
            final double area = polygon.getSize();
            if (Double.isInfinite(area)) {
                AbstractRegion.this.setSize(Double.POSITIVE_INFINITY);
                AbstractRegion.this.setBarycenter(Vector3D.NaN);
            }
            else {
                final Plane plane = (Plane)facet.getHyperplane();
                final Vector3D facetB = plane.toSpace(polygon.getBarycenter());
                double scaled = area * facetB.dotProduct(plane.getNormal());
                if (reversed) {
                    scaled = -scaled;
                }
                AbstractRegion.this.setSize(PolyhedronsSet.this.getSize() + scaled);
                AbstractRegion.this.setBarycenter(new Vector3D(1.0, (Vector3D)PolyhedronsSet.this.getBarycenter(), scaled, facetB));
            }
        }
    }
    
    private static class RotationTransform implements Transform<Euclidean3D, Euclidean2D>
    {
        private Vector3D center;
        private Rotation rotation;
        private Plane cachedOriginal;
        private Transform<Euclidean2D, Euclidean1D> cachedTransform;
        
        RotationTransform(final Vector3D center, final Rotation rotation) {
            this.center = center;
            this.rotation = rotation;
        }
        
        public Vector3D apply(final Point<Euclidean3D> point) {
            final Vector3D delta = ((Vector3D)point).subtract((Vector<Euclidean3D>)this.center);
            return new Vector3D(1.0, this.center, 1.0, this.rotation.applyTo(delta));
        }
        
        public Plane apply(final Hyperplane<Euclidean3D> hyperplane) {
            return ((Plane)hyperplane).rotate(this.center, this.rotation);
        }
        
        public SubHyperplane<Euclidean2D> apply(final SubHyperplane<Euclidean2D> sub, final Hyperplane<Euclidean3D> original, final Hyperplane<Euclidean3D> transformed) {
            if (original != this.cachedOriginal) {
                final Plane oPlane = (Plane)original;
                final Plane tPlane = (Plane)transformed;
                final Vector3D p00 = oPlane.getOrigin();
                final Vector3D p2 = oPlane.toSpace((Point<Euclidean2D>)new Vector2D(1.0, 0.0));
                final Vector3D p3 = oPlane.toSpace((Point<Euclidean2D>)new Vector2D(0.0, 1.0));
                final Vector2D tP00 = tPlane.toSubSpace((Point<Euclidean3D>)this.apply((Point<Euclidean3D>)p00));
                final Vector2D tP2 = tPlane.toSubSpace((Point<Euclidean3D>)this.apply((Point<Euclidean3D>)p2));
                final Vector2D tP3 = tPlane.toSubSpace((Point<Euclidean3D>)this.apply((Point<Euclidean3D>)p3));
                this.cachedOriginal = (Plane)original;
                this.cachedTransform = org.apache.commons.math3.geometry.euclidean.twod.Line.getTransform(tP2.getX() - tP00.getX(), tP2.getY() - tP00.getY(), tP3.getX() - tP00.getX(), tP3.getY() - tP00.getY(), tP00.getX(), tP00.getY());
            }
            return ((SubLine)sub).applyTransform(this.cachedTransform);
        }
    }
    
    private static class TranslationTransform implements Transform<Euclidean3D, Euclidean2D>
    {
        private Vector3D translation;
        private Plane cachedOriginal;
        private Transform<Euclidean2D, Euclidean1D> cachedTransform;
        
        TranslationTransform(final Vector3D translation) {
            this.translation = translation;
        }
        
        public Vector3D apply(final Point<Euclidean3D> point) {
            return new Vector3D(1.0, (Vector3D)point, 1.0, this.translation);
        }
        
        public Plane apply(final Hyperplane<Euclidean3D> hyperplane) {
            return ((Plane)hyperplane).translate(this.translation);
        }
        
        public SubHyperplane<Euclidean2D> apply(final SubHyperplane<Euclidean2D> sub, final Hyperplane<Euclidean3D> original, final Hyperplane<Euclidean3D> transformed) {
            if (original != this.cachedOriginal) {
                final Plane oPlane = (Plane)original;
                final Plane tPlane = (Plane)transformed;
                final Vector2D shift = tPlane.toSubSpace((Point<Euclidean3D>)this.apply((Point<Euclidean3D>)oPlane.getOrigin()));
                this.cachedOriginal = (Plane)original;
                this.cachedTransform = org.apache.commons.math3.geometry.euclidean.twod.Line.getTransform(1.0, 0.0, 0.0, 1.0, shift.getX(), shift.getY());
            }
            return ((SubLine)sub).applyTransform(this.cachedTransform);
        }
    }
}

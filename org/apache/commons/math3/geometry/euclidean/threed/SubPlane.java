package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.euclidean.oned.Vector1D;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
import org.apache.commons.math3.geometry.partitioning.AbstractSubHyperplane;

public class SubPlane extends AbstractSubHyperplane<Euclidean3D, Euclidean2D>
{
    public SubPlane(final Hyperplane<Euclidean3D> hyperplane, final Region<Euclidean2D> remainingRegion) {
        super(hyperplane, remainingRegion);
    }
    
    @Override
    protected AbstractSubHyperplane<Euclidean3D, Euclidean2D> buildNew(final Hyperplane<Euclidean3D> hyperplane, final Region<Euclidean2D> remainingRegion) {
        return new SubPlane(hyperplane, remainingRegion);
    }
    
    @Override
    public SubHyperplane.SplitSubHyperplane<Euclidean3D> split(final Hyperplane<Euclidean3D> hyperplane) {
        final Plane otherPlane = (Plane)hyperplane;
        final Plane thisPlane = (Plane)this.getHyperplane();
        final org.apache.commons.math3.geometry.euclidean.threed.Line inter = otherPlane.intersection(thisPlane);
        final double tolerance = thisPlane.getTolerance();
        if (inter != null) {
            Vector2D p = thisPlane.toSubSpace((Point<Euclidean3D>)inter.toSpace((Point<Euclidean1D>)Vector1D.ZERO));
            Vector2D q = thisPlane.toSubSpace((Point<Euclidean3D>)inter.toSpace((Point<Euclidean1D>)Vector1D.ONE));
            final Vector3D crossP = Vector3D.crossProduct(inter.getDirection(), thisPlane.getNormal());
            if (crossP.dotProduct(otherPlane.getNormal()) < 0.0) {
                final Vector2D tmp = p;
                p = q;
                q = tmp;
            }
            final SubHyperplane<Euclidean2D> l2DMinus = new Line(p, q, tolerance).wholeHyperplane();
            final SubHyperplane<Euclidean2D> l2DPlus = new Line(q, p, tolerance).wholeHyperplane();
            final BSPTree<Euclidean2D> splitTree = ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().getTree(false).split(l2DMinus);
            final BSPTree<Euclidean2D> plusTree = ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().isEmpty(splitTree.getPlus()) ? new BSPTree<Euclidean2D>(Boolean.FALSE) : new BSPTree<Euclidean2D>(l2DPlus, new BSPTree<Euclidean2D>(Boolean.FALSE), splitTree.getPlus(), null);
            final BSPTree<Euclidean2D> minusTree = ((AbstractSubHyperplane<S, Euclidean2D>)this).getRemainingRegion().isEmpty(splitTree.getMinus()) ? new BSPTree<Euclidean2D>(Boolean.FALSE) : new BSPTree<Euclidean2D>(l2DMinus, new BSPTree<Euclidean2D>(Boolean.FALSE), splitTree.getMinus(), null);
            return new SubHyperplane.SplitSubHyperplane<Euclidean3D>(new SubPlane(thisPlane.copySelf(), new PolygonsSet(plusTree, tolerance)), new SubPlane(thisPlane.copySelf(), new PolygonsSet(minusTree, tolerance)));
        }
        final double global = otherPlane.getOffset(thisPlane);
        if (global < -tolerance) {
            return new SubHyperplane.SplitSubHyperplane<Euclidean3D>(null, this);
        }
        if (global > tolerance) {
            return new SubHyperplane.SplitSubHyperplane<Euclidean3D>(this, null);
        }
        return new SubHyperplane.SplitSubHyperplane<Euclidean3D>(null, null);
    }
}

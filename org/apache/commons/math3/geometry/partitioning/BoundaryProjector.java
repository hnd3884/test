package org.apache.commons.math3.geometry.partitioning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

class BoundaryProjector<S extends Space, T extends Space> implements BSPTreeVisitor<S>
{
    private final Point<S> original;
    private Point<S> projected;
    private BSPTree<S> leaf;
    private double offset;
    
    BoundaryProjector(final Point<S> original) {
        this.original = original;
        this.projected = null;
        this.leaf = null;
        this.offset = Double.POSITIVE_INFINITY;
    }
    
    public Order visitOrder(final BSPTree<S> node) {
        if (node.getCut().getHyperplane().getOffset(this.original) <= 0.0) {
            return Order.MINUS_SUB_PLUS;
        }
        return Order.PLUS_SUB_MINUS;
    }
    
    public void visitInternalNode(final BSPTree<S> node) {
        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        final double signedOffset = hyperplane.getOffset(this.original);
        if (FastMath.abs(signedOffset) < this.offset) {
            final Point<S> regular = hyperplane.project(this.original);
            final List<Region<T>> boundaryParts = this.boundaryRegions(node);
            boolean regularFound = false;
            for (final Region<T> part : boundaryParts) {
                if (!regularFound && this.belongsToPart(regular, hyperplane, part)) {
                    this.projected = regular;
                    this.offset = FastMath.abs(signedOffset);
                    regularFound = true;
                }
            }
            if (!regularFound) {
                for (final Region<T> part : boundaryParts) {
                    final Point<S> spI = this.singularProjection(regular, hyperplane, part);
                    if (spI != null) {
                        final double distance = this.original.distance(spI);
                        if (distance >= this.offset) {
                            continue;
                        }
                        this.projected = spI;
                        this.offset = distance;
                    }
                }
            }
        }
    }
    
    public void visitLeafNode(final BSPTree<S> node) {
        if (this.leaf == null) {
            this.leaf = node;
        }
    }
    
    public BoundaryProjection<S> getProjection() {
        this.offset = FastMath.copySign(this.offset, ((boolean)this.leaf.getAttribute()) ? -1.0 : 1.0);
        return new BoundaryProjection<S>(this.original, this.projected, this.offset);
    }
    
    private List<Region<T>> boundaryRegions(final BSPTree<S> node) {
        final List<Region<T>> regions = new ArrayList<Region<T>>(2);
        final BoundaryAttribute<S> ba = (BoundaryAttribute<S>)node.getAttribute();
        this.addRegion(ba.getPlusInside(), regions);
        this.addRegion(ba.getPlusOutside(), regions);
        return regions;
    }
    
    private void addRegion(final SubHyperplane<S> sub, final List<Region<T>> list) {
        if (sub != null) {
            final Region<T> region = ((AbstractSubHyperplane)sub).getRemainingRegion();
            if (region != null) {
                list.add(region);
            }
        }
    }
    
    private boolean belongsToPart(final Point<S> point, final Hyperplane<S> hyperplane, final Region<T> part) {
        final Embedding<S, T> embedding = (Embedding)hyperplane;
        return part.checkPoint(embedding.toSubSpace(point)) != Region.Location.OUTSIDE;
    }
    
    private Point<S> singularProjection(final Point<S> point, final Hyperplane<S> hyperplane, final Region<T> part) {
        final Embedding<S, T> embedding = (Embedding)hyperplane;
        final BoundaryProjection<T> bp = part.projectToBoundary(embedding.toSubSpace(point));
        return (bp.getProjected() == null) ? null : embedding.toSpace(bp.getProjected());
    }
}

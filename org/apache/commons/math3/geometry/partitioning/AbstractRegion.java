package org.apache.commons.math3.geometry.partitioning;

import java.util.Map;
import java.util.HashMap;
import org.apache.commons.math3.geometry.Vector;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Collection;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public abstract class AbstractRegion<S extends Space, T extends Space> implements Region<S>
{
    private BSPTree<S> tree;
    private final double tolerance;
    private double size;
    private Point<S> barycenter;
    
    protected AbstractRegion(final double tolerance) {
        this.tree = new BSPTree<S>(Boolean.TRUE);
        this.tolerance = tolerance;
    }
    
    protected AbstractRegion(final BSPTree<S> tree, final double tolerance) {
        this.tree = tree;
        this.tolerance = tolerance;
    }
    
    protected AbstractRegion(final Collection<SubHyperplane<S>> boundary, final double tolerance) {
        this.tolerance = tolerance;
        if (boundary.size() == 0) {
            this.tree = new BSPTree<S>(Boolean.TRUE);
        }
        else {
            final TreeSet<SubHyperplane<S>> ordered = new TreeSet<SubHyperplane<S>>(new Comparator<SubHyperplane<S>>() {
                public int compare(final SubHyperplane<S> o1, final SubHyperplane<S> o2) {
                    final double size1 = o1.getSize();
                    final double size2 = o2.getSize();
                    return (size2 < size1) ? -1 : ((o1 == o2) ? 0 : 1);
                }
            });
            ordered.addAll(boundary);
            this.insertCuts(this.tree = new BSPTree<S>(), ordered);
            this.tree.visit(new BSPTreeVisitor<S>() {
                public Order visitOrder(final BSPTree<S> node) {
                    return Order.PLUS_SUB_MINUS;
                }
                
                public void visitInternalNode(final BSPTree<S> node) {
                }
                
                public void visitLeafNode(final BSPTree<S> node) {
                    if (node.getParent() == null || node == node.getParent().getMinus()) {
                        node.setAttribute(Boolean.TRUE);
                    }
                    else {
                        node.setAttribute(Boolean.FALSE);
                    }
                }
            });
        }
    }
    
    public AbstractRegion(final Hyperplane<S>[] hyperplanes, final double tolerance) {
        this.tolerance = tolerance;
        if (hyperplanes == null || hyperplanes.length == 0) {
            this.tree = new BSPTree<S>(Boolean.FALSE);
        }
        else {
            this.tree = hyperplanes[0].wholeSpace().getTree(false);
            BSPTree<S> node = this.tree;
            node.setAttribute(Boolean.TRUE);
            for (final Hyperplane<S> hyperplane : hyperplanes) {
                if (node.insertCut(hyperplane)) {
                    node.setAttribute(null);
                    node.getPlus().setAttribute(Boolean.FALSE);
                    node = node.getMinus();
                    node.setAttribute(Boolean.TRUE);
                }
            }
        }
    }
    
    public abstract AbstractRegion<S, T> buildNew(final BSPTree<S> p0);
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    private void insertCuts(final BSPTree<S> node, final Collection<SubHyperplane<S>> boundary) {
        Iterator<SubHyperplane<S>> iterator;
        Hyperplane<S> inserted;
        for (iterator = boundary.iterator(), inserted = null; inserted == null && iterator.hasNext(); inserted = null) {
            inserted = iterator.next().getHyperplane();
            if (!node.insertCut(inserted.copySelf())) {}
        }
        if (!iterator.hasNext()) {
            return;
        }
        final ArrayList<SubHyperplane<S>> plusList = new ArrayList<SubHyperplane<S>>();
        final ArrayList<SubHyperplane<S>> minusList = new ArrayList<SubHyperplane<S>>();
        while (iterator.hasNext()) {
            final SubHyperplane<S> other = iterator.next();
            final SubHyperplane.SplitSubHyperplane<S> split = other.split(inserted);
            switch (split.getSide()) {
                case PLUS: {
                    plusList.add(other);
                    continue;
                }
                case MINUS: {
                    minusList.add(other);
                    continue;
                }
                case BOTH: {
                    plusList.add(split.getPlus());
                    minusList.add(split.getMinus());
                    continue;
                }
            }
        }
        this.insertCuts(node.getPlus(), plusList);
        this.insertCuts(node.getMinus(), minusList);
    }
    
    public AbstractRegion<S, T> copySelf() {
        return this.buildNew(this.tree.copySelf());
    }
    
    public boolean isEmpty() {
        return this.isEmpty(this.tree);
    }
    
    public boolean isEmpty(final BSPTree<S> node) {
        if (node.getCut() == null) {
            return !(boolean)node.getAttribute();
        }
        return this.isEmpty(node.getMinus()) && this.isEmpty(node.getPlus());
    }
    
    public boolean isFull() {
        return this.isFull(this.tree);
    }
    
    public boolean isFull(final BSPTree<S> node) {
        if (node.getCut() == null) {
            return (boolean)node.getAttribute();
        }
        return this.isFull(node.getMinus()) && this.isFull(node.getPlus());
    }
    
    public boolean contains(final Region<S> region) {
        return new RegionFactory<S>().difference(region, this).isEmpty();
    }
    
    public BoundaryProjection<S> projectToBoundary(final Point<S> point) {
        final BoundaryProjector<S, T> projector = new BoundaryProjector<S, T>(point);
        this.getTree(true).visit(projector);
        return projector.getProjection();
    }
    
    public Location checkPoint(final Vector<S> point) {
        return this.checkPoint((Point<S>)point);
    }
    
    public Location checkPoint(final Point<S> point) {
        return this.checkPoint(this.tree, point);
    }
    
    protected Location checkPoint(final BSPTree<S> node, final Vector<S> point) {
        return this.checkPoint(node, (Point<S>)point);
    }
    
    protected Location checkPoint(final BSPTree<S> node, final Point<S> point) {
        final BSPTree<S> cell = node.getCell(point, this.tolerance);
        if (cell.getCut() == null) {
            return cell.getAttribute() ? Location.INSIDE : Location.OUTSIDE;
        }
        final Location minusCode = this.checkPoint(cell.getMinus(), point);
        final Location plusCode = this.checkPoint(cell.getPlus(), point);
        return (minusCode == plusCode) ? minusCode : Location.BOUNDARY;
    }
    
    public BSPTree<S> getTree(final boolean includeBoundaryAttributes) {
        if (includeBoundaryAttributes && this.tree.getCut() != null && this.tree.getAttribute() == null) {
            this.tree.visit(new BoundaryBuilder<S>());
        }
        return this.tree;
    }
    
    public double getBoundarySize() {
        final BoundarySizeVisitor<S> visitor = new BoundarySizeVisitor<S>();
        this.getTree(true).visit(visitor);
        return visitor.getSize();
    }
    
    public double getSize() {
        if (this.barycenter == null) {
            this.computeGeometricalProperties();
        }
        return this.size;
    }
    
    protected void setSize(final double size) {
        this.size = size;
    }
    
    public Point<S> getBarycenter() {
        if (this.barycenter == null) {
            this.computeGeometricalProperties();
        }
        return this.barycenter;
    }
    
    protected void setBarycenter(final Vector<S> barycenter) {
        this.setBarycenter((Point<S>)barycenter);
    }
    
    protected void setBarycenter(final Point<S> barycenter) {
        this.barycenter = barycenter;
    }
    
    protected abstract void computeGeometricalProperties();
    
    @Deprecated
    public Side side(final Hyperplane<S> hyperplane) {
        final InsideFinder<S> finder = new InsideFinder<S>(this);
        finder.recurseSides(this.tree, hyperplane.wholeHyperplane());
        return finder.plusFound() ? (finder.minusFound() ? Side.BOTH : Side.PLUS) : (finder.minusFound() ? Side.MINUS : Side.HYPER);
    }
    
    public SubHyperplane<S> intersection(final SubHyperplane<S> sub) {
        return this.recurseIntersection(this.tree, sub);
    }
    
    private SubHyperplane<S> recurseIntersection(final BSPTree<S> node, final SubHyperplane<S> sub) {
        if (node.getCut() == null) {
            return node.getAttribute() ? sub.copySelf() : null;
        }
        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
        if (split.getPlus() != null) {
            if (split.getMinus() == null) {
                return this.recurseIntersection(node.getPlus(), sub);
            }
            final SubHyperplane<S> plus = this.recurseIntersection(node.getPlus(), split.getPlus());
            final SubHyperplane<S> minus = this.recurseIntersection(node.getMinus(), split.getMinus());
            if (plus == null) {
                return minus;
            }
            if (minus == null) {
                return plus;
            }
            return plus.reunite(minus);
        }
        else {
            if (split.getMinus() != null) {
                return this.recurseIntersection(node.getMinus(), sub);
            }
            return this.recurseIntersection(node.getPlus(), this.recurseIntersection(node.getMinus(), sub));
        }
    }
    
    public AbstractRegion<S, T> applyTransform(final Transform<S, T> transform) {
        final Map<BSPTree<S>, BSPTree<S>> map = new HashMap<BSPTree<S>, BSPTree<S>>();
        final BSPTree<S> transformedTree = this.recurseTransform(this.getTree(false), transform, map);
        for (final Map.Entry<BSPTree<S>, BSPTree<S>> entry : map.entrySet()) {
            if (entry.getKey().getCut() != null) {
                final BoundaryAttribute<S> original = (BoundaryAttribute<S>)entry.getKey().getAttribute();
                if (original == null) {
                    continue;
                }
                final BoundaryAttribute<S> transformed = (BoundaryAttribute<S>)entry.getValue().getAttribute();
                for (final BSPTree<S> splitter : original.getSplitters()) {
                    transformed.getSplitters().add(map.get(splitter));
                }
            }
        }
        return this.buildNew(transformedTree);
    }
    
    private BSPTree<S> recurseTransform(final BSPTree<S> node, final Transform<S, T> transform, final Map<BSPTree<S>, BSPTree<S>> map) {
        BSPTree<S> transformedNode;
        if (node.getCut() == null) {
            transformedNode = new BSPTree<S>(node.getAttribute());
        }
        else {
            final SubHyperplane<S> sub = node.getCut();
            final SubHyperplane<S> tSub = ((AbstractSubHyperplane)sub).applyTransform(transform);
            BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
            if (attribute != null) {
                final SubHyperplane<S> tPO = (attribute.getPlusOutside() == null) ? null : ((AbstractSubHyperplane)attribute.getPlusOutside()).applyTransform(transform);
                final SubHyperplane<S> tPI = (attribute.getPlusInside() == null) ? null : ((AbstractSubHyperplane)attribute.getPlusInside()).applyTransform(transform);
                attribute = new BoundaryAttribute<S>(tPO, tPI, new NodesSet<S>());
            }
            transformedNode = new BSPTree<S>(tSub, this.recurseTransform(node.getPlus(), transform, map), this.recurseTransform(node.getMinus(), transform, map), attribute);
        }
        map.put(node, transformedNode);
        return transformedNode;
    }
}

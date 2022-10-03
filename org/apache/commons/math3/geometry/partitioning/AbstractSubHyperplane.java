package org.apache.commons.math3.geometry.partitioning;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.math3.geometry.Space;

public abstract class AbstractSubHyperplane<S extends Space, T extends Space> implements SubHyperplane<S>
{
    private final Hyperplane<S> hyperplane;
    private final Region<T> remainingRegion;
    
    protected AbstractSubHyperplane(final Hyperplane<S> hyperplane, final Region<T> remainingRegion) {
        this.hyperplane = hyperplane;
        this.remainingRegion = remainingRegion;
    }
    
    protected abstract AbstractSubHyperplane<S, T> buildNew(final Hyperplane<S> p0, final Region<T> p1);
    
    public AbstractSubHyperplane<S, T> copySelf() {
        return this.buildNew(this.hyperplane.copySelf(), this.remainingRegion);
    }
    
    public Hyperplane<S> getHyperplane() {
        return this.hyperplane;
    }
    
    public Region<T> getRemainingRegion() {
        return this.remainingRegion;
    }
    
    public double getSize() {
        return this.remainingRegion.getSize();
    }
    
    public AbstractSubHyperplane<S, T> reunite(final SubHyperplane<S> other) {
        final AbstractSubHyperplane<S, T> o = (AbstractSubHyperplane)other;
        return this.buildNew(this.hyperplane, new RegionFactory<T>().union(this.remainingRegion, o.remainingRegion));
    }
    
    public AbstractSubHyperplane<S, T> applyTransform(final Transform<S, T> transform) {
        final Hyperplane<S> tHyperplane = transform.apply(this.hyperplane);
        final Map<BSPTree<T>, BSPTree<T>> map = new HashMap<BSPTree<T>, BSPTree<T>>();
        final BSPTree<T> tTree = this.recurseTransform(this.remainingRegion.getTree(false), tHyperplane, transform, map);
        for (final Map.Entry<BSPTree<T>, BSPTree<T>> entry : map.entrySet()) {
            if (entry.getKey().getCut() != null) {
                final BoundaryAttribute<T> original = (BoundaryAttribute<T>)entry.getKey().getAttribute();
                if (original == null) {
                    continue;
                }
                final BoundaryAttribute<T> transformed = (BoundaryAttribute<T>)entry.getValue().getAttribute();
                for (final BSPTree<T> splitter : original.getSplitters()) {
                    transformed.getSplitters().add(map.get(splitter));
                }
            }
        }
        return this.buildNew(tHyperplane, this.remainingRegion.buildNew(tTree));
    }
    
    private BSPTree<T> recurseTransform(final BSPTree<T> node, final Hyperplane<S> transformed, final Transform<S, T> transform, final Map<BSPTree<T>, BSPTree<T>> map) {
        BSPTree<T> transformedNode;
        if (node.getCut() == null) {
            transformedNode = new BSPTree<T>(node.getAttribute());
        }
        else {
            BoundaryAttribute<T> attribute = (BoundaryAttribute<T>)node.getAttribute();
            if (attribute != null) {
                final SubHyperplane<T> tPO = (attribute.getPlusOutside() == null) ? null : transform.apply(attribute.getPlusOutside(), this.hyperplane, transformed);
                final SubHyperplane<T> tPI = (attribute.getPlusInside() == null) ? null : transform.apply(attribute.getPlusInside(), this.hyperplane, transformed);
                attribute = new BoundaryAttribute<T>(tPO, tPI, new NodesSet<T>());
            }
            transformedNode = new BSPTree<T>(transform.apply(node.getCut(), this.hyperplane, transformed), this.recurseTransform(node.getPlus(), transformed, transform, map), this.recurseTransform(node.getMinus(), transformed, transform, map), attribute);
        }
        map.put(node, transformedNode);
        return transformedNode;
    }
    
    @Deprecated
    public Side side(final Hyperplane<S> hyper) {
        return this.split(hyper).getSide();
    }
    
    public abstract SplitSubHyperplane<S> split(final Hyperplane<S> p0);
    
    public boolean isEmpty() {
        return this.remainingRegion.isEmpty();
    }
}

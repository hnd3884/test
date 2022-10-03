package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

public class BoundaryAttribute<S extends Space>
{
    private final SubHyperplane<S> plusOutside;
    private final SubHyperplane<S> plusInside;
    private final NodesSet<S> splitters;
    
    @Deprecated
    public BoundaryAttribute(final SubHyperplane<S> plusOutside, final SubHyperplane<S> plusInside) {
        this(plusOutside, plusInside, null);
    }
    
    BoundaryAttribute(final SubHyperplane<S> plusOutside, final SubHyperplane<S> plusInside, final NodesSet<S> splitters) {
        this.plusOutside = plusOutside;
        this.plusInside = plusInside;
        this.splitters = splitters;
    }
    
    public SubHyperplane<S> getPlusOutside() {
        return this.plusOutside;
    }
    
    public SubHyperplane<S> getPlusInside() {
        return this.plusInside;
    }
    
    public NodesSet<S> getSplitters() {
        return this.splitters;
    }
}

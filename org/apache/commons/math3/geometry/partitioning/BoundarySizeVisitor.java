package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

class BoundarySizeVisitor<S extends Space> implements BSPTreeVisitor<S>
{
    private double boundarySize;
    
    BoundarySizeVisitor() {
        this.boundarySize = 0.0;
    }
    
    public Order visitOrder(final BSPTree<S> node) {
        return Order.MINUS_SUB_PLUS;
    }
    
    public void visitInternalNode(final BSPTree<S> node) {
        final BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
        if (attribute.getPlusOutside() != null) {
            this.boundarySize += attribute.getPlusOutside().getSize();
        }
        if (attribute.getPlusInside() != null) {
            this.boundarySize += attribute.getPlusInside().getSize();
        }
    }
    
    public void visitLeafNode(final BSPTree<S> node) {
    }
    
    public double getSize() {
        return this.boundarySize;
    }
}

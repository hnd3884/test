package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

class BoundaryBuilder<S extends Space> implements BSPTreeVisitor<S>
{
    public Order visitOrder(final BSPTree<S> node) {
        return Order.PLUS_MINUS_SUB;
    }
    
    public void visitInternalNode(final BSPTree<S> node) {
        SubHyperplane<S> plusOutside = null;
        SubHyperplane<S> plusInside = null;
        NodesSet<S> splitters = null;
        final Characterization<S> plusChar = new Characterization<S>(node.getPlus(), node.getCut().copySelf());
        if (plusChar.touchOutside()) {
            final Characterization<S> minusChar = new Characterization<S>(node.getMinus(), plusChar.outsideTouching());
            if (minusChar.touchInside()) {
                plusOutside = minusChar.insideTouching();
                splitters = new NodesSet<S>();
                splitters.addAll(minusChar.getInsideSplitters());
                splitters.addAll(plusChar.getOutsideSplitters());
            }
        }
        if (plusChar.touchInside()) {
            final Characterization<S> minusChar = new Characterization<S>(node.getMinus(), plusChar.insideTouching());
            if (minusChar.touchOutside()) {
                plusInside = minusChar.outsideTouching();
                if (splitters == null) {
                    splitters = new NodesSet<S>();
                }
                splitters.addAll(minusChar.getOutsideSplitters());
                splitters.addAll(plusChar.getInsideSplitters());
            }
        }
        if (splitters != null) {
            for (BSPTree<S> up = node.getParent(); up != null; up = up.getParent()) {
                splitters.add(up);
            }
        }
        node.setAttribute(new BoundaryAttribute((SubHyperplane<Space>)plusOutside, (SubHyperplane<Space>)plusInside, (NodesSet<Space>)splitters));
    }
    
    public void visitLeafNode(final BSPTree<S> node) {
    }
}

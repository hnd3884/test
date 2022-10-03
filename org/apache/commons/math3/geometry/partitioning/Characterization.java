package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.exception.MathInternalError;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.geometry.Space;

class Characterization<S extends Space>
{
    private SubHyperplane<S> outsideTouching;
    private SubHyperplane<S> insideTouching;
    private final NodesSet<S> outsideSplitters;
    private final NodesSet<S> insideSplitters;
    
    Characterization(final BSPTree<S> node, final SubHyperplane<S> sub) {
        this.outsideTouching = null;
        this.insideTouching = null;
        this.outsideSplitters = new NodesSet<S>();
        this.insideSplitters = new NodesSet<S>();
        this.characterize(node, sub, new ArrayList<BSPTree<S>>());
    }
    
    private void characterize(final BSPTree<S> node, final SubHyperplane<S> sub, final List<BSPTree<S>> splitters) {
        if (node.getCut() == null) {
            final boolean inside = (boolean)node.getAttribute();
            if (inside) {
                this.addInsideTouching(sub, splitters);
            }
            else {
                this.addOutsideTouching(sub, splitters);
            }
        }
        else {
            final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
            final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
            switch (split.getSide()) {
                case PLUS: {
                    this.characterize(node.getPlus(), sub, splitters);
                    break;
                }
                case MINUS: {
                    this.characterize(node.getMinus(), sub, splitters);
                    break;
                }
                case BOTH: {
                    splitters.add(node);
                    this.characterize(node.getPlus(), split.getPlus(), splitters);
                    this.characterize(node.getMinus(), split.getMinus(), splitters);
                    splitters.remove(splitters.size() - 1);
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
        }
    }
    
    private void addOutsideTouching(final SubHyperplane<S> sub, final List<BSPTree<S>> splitters) {
        if (this.outsideTouching == null) {
            this.outsideTouching = sub;
        }
        else {
            this.outsideTouching = this.outsideTouching.reunite(sub);
        }
        this.outsideSplitters.addAll(splitters);
    }
    
    private void addInsideTouching(final SubHyperplane<S> sub, final List<BSPTree<S>> splitters) {
        if (this.insideTouching == null) {
            this.insideTouching = sub;
        }
        else {
            this.insideTouching = this.insideTouching.reunite(sub);
        }
        this.insideSplitters.addAll(splitters);
    }
    
    public boolean touchOutside() {
        return this.outsideTouching != null && !this.outsideTouching.isEmpty();
    }
    
    public SubHyperplane<S> outsideTouching() {
        return this.outsideTouching;
    }
    
    public NodesSet<S> getOutsideSplitters() {
        return this.outsideSplitters;
    }
    
    public boolean touchInside() {
        return this.insideTouching != null && !this.insideTouching.isEmpty();
    }
    
    public SubHyperplane<S> insideTouching() {
        return this.insideTouching;
    }
    
    public NodesSet<S> getInsideSplitters() {
        return this.insideSplitters;
    }
}

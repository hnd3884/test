package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Space;

class InsideFinder<S extends Space>
{
    private final Region<S> region;
    private boolean plusFound;
    private boolean minusFound;
    
    InsideFinder(final Region<S> region) {
        this.region = region;
        this.plusFound = false;
        this.minusFound = false;
    }
    
    public void recurseSides(final BSPTree<S> node, final SubHyperplane<S> sub) {
        if (node.getCut() == null) {
            if (node.getAttribute()) {
                this.plusFound = true;
                this.minusFound = true;
            }
            return;
        }
        final Hyperplane<S> hyperplane = node.getCut().getHyperplane();
        final SubHyperplane.SplitSubHyperplane<S> split = sub.split(hyperplane);
        switch (split.getSide()) {
            case PLUS: {
                if (node.getCut().split(sub.getHyperplane()).getSide() == Side.PLUS) {
                    if (!this.region.isEmpty(node.getMinus())) {
                        this.plusFound = true;
                    }
                }
                else if (!this.region.isEmpty(node.getMinus())) {
                    this.minusFound = true;
                }
                if (!this.plusFound || !this.minusFound) {
                    this.recurseSides(node.getPlus(), sub);
                    break;
                }
                break;
            }
            case MINUS: {
                if (node.getCut().split(sub.getHyperplane()).getSide() == Side.PLUS) {
                    if (!this.region.isEmpty(node.getPlus())) {
                        this.plusFound = true;
                    }
                }
                else if (!this.region.isEmpty(node.getPlus())) {
                    this.minusFound = true;
                }
                if (!this.plusFound || !this.minusFound) {
                    this.recurseSides(node.getMinus(), sub);
                    break;
                }
                break;
            }
            case BOTH: {
                this.recurseSides(node.getPlus(), split.getPlus());
                if (!this.plusFound || !this.minusFound) {
                    this.recurseSides(node.getMinus(), split.getMinus());
                    break;
                }
                break;
            }
            default: {
                if (node.getCut().getHyperplane().sameOrientationAs(sub.getHyperplane())) {
                    if (node.getPlus().getCut() != null || (boolean)node.getPlus().getAttribute()) {
                        this.plusFound = true;
                    }
                    if (node.getMinus().getCut() != null || (boolean)node.getMinus().getAttribute()) {
                        this.minusFound = true;
                        break;
                    }
                    break;
                }
                else {
                    if (node.getPlus().getCut() != null || (boolean)node.getPlus().getAttribute()) {
                        this.minusFound = true;
                    }
                    if (node.getMinus().getCut() != null || (boolean)node.getMinus().getAttribute()) {
                        this.plusFound = true;
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    public boolean plusFound() {
        return this.plusFound;
    }
    
    public boolean minusFound() {
        return this.minusFound;
    }
}

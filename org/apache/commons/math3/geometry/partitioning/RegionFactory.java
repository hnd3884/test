package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.geometry.Space;

public class RegionFactory<S extends Space>
{
    private final NodesCleaner nodeCleaner;
    
    public RegionFactory() {
        this.nodeCleaner = new NodesCleaner();
    }
    
    public Region<S> buildConvex(final Hyperplane<S>... hyperplanes) {
        if (hyperplanes == null || hyperplanes.length == 0) {
            return null;
        }
        final Region<S> region = hyperplanes[0].wholeSpace();
        BSPTree<S> node = region.getTree(false);
        node.setAttribute(Boolean.TRUE);
        for (final Hyperplane<S> hyperplane : hyperplanes) {
            if (node.insertCut(hyperplane)) {
                node.setAttribute(null);
                node.getPlus().setAttribute(Boolean.FALSE);
                node = node.getMinus();
                node.setAttribute(Boolean.TRUE);
            }
            else {
                SubHyperplane<S> s = hyperplane.wholeHyperplane();
                for (BSPTree<S> tree = node; tree.getParent() != null && s != null; tree = tree.getParent()) {
                    final Hyperplane<S> other = tree.getParent().getCut().getHyperplane();
                    final SubHyperplane.SplitSubHyperplane<S> split = s.split(other);
                    switch (split.getSide()) {
                        case HYPER: {
                            if (!hyperplane.sameOrientationAs(other)) {
                                return this.getComplement(hyperplanes[0].wholeSpace());
                            }
                            break;
                        }
                        case PLUS: {
                            throw new MathIllegalArgumentException(LocalizedFormats.NOT_CONVEX_HYPERPLANES, new Object[0]);
                        }
                        default: {
                            s = split.getMinus();
                            break;
                        }
                    }
                }
            }
        }
        return region;
    }
    
    public Region<S> union(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new UnionMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> intersection(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new IntersectionMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> xor(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new XorMerger());
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> difference(final Region<S> region1, final Region<S> region2) {
        final BSPTree<S> tree = region1.getTree(false).merge(region2.getTree(false), new DifferenceMerger(region1, region2));
        tree.visit(this.nodeCleaner);
        return region1.buildNew(tree);
    }
    
    public Region<S> getComplement(final Region<S> region) {
        return region.buildNew(this.recurseComplement(region.getTree(false)));
    }
    
    private BSPTree<S> recurseComplement(final BSPTree<S> node) {
        final Map<BSPTree<S>, BSPTree<S>> map = new HashMap<BSPTree<S>, BSPTree<S>>();
        final BSPTree<S> transformedTree = this.recurseComplement(node, map);
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
        return transformedTree;
    }
    
    private BSPTree<S> recurseComplement(final BSPTree<S> node, final Map<BSPTree<S>, BSPTree<S>> map) {
        BSPTree<S> transformedNode;
        if (node.getCut() == null) {
            transformedNode = new BSPTree<S>(node.getAttribute() ? Boolean.FALSE : Boolean.TRUE);
        }
        else {
            BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
            if (attribute != null) {
                final SubHyperplane<S> plusOutside = (attribute.getPlusInside() == null) ? null : attribute.getPlusInside().copySelf();
                final SubHyperplane<S> plusInside = (attribute.getPlusOutside() == null) ? null : attribute.getPlusOutside().copySelf();
                attribute = new BoundaryAttribute<S>(plusOutside, plusInside, new NodesSet<S>());
            }
            transformedNode = new BSPTree<S>(node.getCut().copySelf(), this.recurseComplement(node.getPlus(), map), this.recurseComplement(node.getMinus(), map), attribute);
        }
        map.put(node, transformedNode);
        return transformedNode;
    }
    
    private class UnionMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            if (leaf.getAttribute()) {
                leaf.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(true));
                return leaf;
            }
            tree.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(false));
            return tree;
        }
    }
    
    private class IntersectionMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            if (leaf.getAttribute()) {
                tree.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(true));
                return tree;
            }
            leaf.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(false));
            return leaf;
        }
    }
    
    private class XorMerger implements BSPTree.LeafMerger<S>
    {
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            BSPTree<S> t = tree;
            if (leaf.getAttribute()) {
                t = RegionFactory.this.recurseComplement(t);
            }
            t.insertInTree(parentTree, isPlusChild, new VanishingToLeaf(true));
            return t;
        }
    }
    
    private class DifferenceMerger implements BSPTree.LeafMerger<S>, BSPTree.VanishingCutHandler<S>
    {
        private final Region<S> region1;
        private final Region<S> region2;
        
        DifferenceMerger(final Region<S> region1, final Region<S> region2) {
            this.region1 = region1.copySelf();
            this.region2 = region2.copySelf();
        }
        
        public BSPTree<S> merge(final BSPTree<S> leaf, final BSPTree<S> tree, final BSPTree<S> parentTree, final boolean isPlusChild, final boolean leafFromInstance) {
            if (leaf.getAttribute()) {
                final BSPTree<S> argTree = RegionFactory.this.recurseComplement(leafFromInstance ? tree : leaf);
                argTree.insertInTree(parentTree, isPlusChild, this);
                return argTree;
            }
            final BSPTree<S> instanceTree = leafFromInstance ? leaf : tree;
            instanceTree.insertInTree(parentTree, isPlusChild, this);
            return instanceTree;
        }
        
        public BSPTree<S> fixNode(final BSPTree<S> node) {
            final BSPTree<S> cell = node.pruneAroundConvexCell(Boolean.TRUE, Boolean.FALSE, null);
            final Region<S> r = this.region1.buildNew(cell);
            final Point<S> p = r.getBarycenter();
            return new BSPTree<S>(this.region1.checkPoint(p) == Region.Location.INSIDE && this.region2.checkPoint(p) == Region.Location.OUTSIDE);
        }
    }
    
    private class NodesCleaner implements BSPTreeVisitor<S>
    {
        public Order visitOrder(final BSPTree<S> node) {
            return Order.PLUS_SUB_MINUS;
        }
        
        public void visitInternalNode(final BSPTree<S> node) {
            node.setAttribute(null);
        }
        
        public void visitLeafNode(final BSPTree<S> node) {
        }
    }
    
    private class VanishingToLeaf implements BSPTree.VanishingCutHandler<S>
    {
        private final boolean inside;
        
        VanishingToLeaf(final boolean inside) {
            this.inside = inside;
        }
        
        public BSPTree<S> fixNode(final BSPTree<S> node) {
            if (node.getPlus().getAttribute().equals(node.getMinus().getAttribute())) {
                return new BSPTree<S>(node.getPlus().getAttribute());
            }
            return new BSPTree<S>(this.inside);
        }
    }
}

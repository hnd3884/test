package org.apache.commons.math3.geometry.euclidean.oned;

import java.util.NoSuchElementException;
import org.apache.commons.math3.geometry.partitioning.Region;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.partitioning.BoundaryProjection;
import java.util.Iterator;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class IntervalsSet extends AbstractRegion<Euclidean1D, Euclidean1D> implements Iterable<double[]>
{
    private static final double DEFAULT_TOLERANCE = 1.0E-10;
    
    public IntervalsSet(final double tolerance) {
        super(tolerance);
    }
    
    public IntervalsSet(final double lower, final double upper, final double tolerance) {
        super(buildTree(lower, upper, tolerance), tolerance);
    }
    
    public IntervalsSet(final BSPTree<Euclidean1D> tree, final double tolerance) {
        super(tree, tolerance);
    }
    
    public IntervalsSet(final Collection<SubHyperplane<Euclidean1D>> boundary, final double tolerance) {
        super(boundary, tolerance);
    }
    
    @Deprecated
    public IntervalsSet() {
        this(1.0E-10);
    }
    
    @Deprecated
    public IntervalsSet(final double lower, final double upper) {
        this(lower, upper, 1.0E-10);
    }
    
    @Deprecated
    public IntervalsSet(final BSPTree<Euclidean1D> tree) {
        this(tree, 1.0E-10);
    }
    
    @Deprecated
    public IntervalsSet(final Collection<SubHyperplane<Euclidean1D>> boundary) {
        this(boundary, 1.0E-10);
    }
    
    private static BSPTree<Euclidean1D> buildTree(final double lower, final double upper, final double tolerance) {
        if (Double.isInfinite(lower) && lower < 0.0) {
            if (Double.isInfinite(upper) && upper > 0.0) {
                return new BSPTree<Euclidean1D>(Boolean.TRUE);
            }
            final SubHyperplane<Euclidean1D> upperCut = new OrientedPoint(new Vector1D(upper), true, tolerance).wholeHyperplane();
            return new BSPTree<Euclidean1D>(upperCut, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(Boolean.TRUE), null);
        }
        else {
            final SubHyperplane<Euclidean1D> lowerCut = new OrientedPoint(new Vector1D(lower), false, tolerance).wholeHyperplane();
            if (Double.isInfinite(upper) && upper > 0.0) {
                return new BSPTree<Euclidean1D>(lowerCut, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(Boolean.TRUE), null);
            }
            final SubHyperplane<Euclidean1D> upperCut2 = new OrientedPoint(new Vector1D(upper), true, tolerance).wholeHyperplane();
            return new BSPTree<Euclidean1D>(lowerCut, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(upperCut2, new BSPTree<Euclidean1D>(Boolean.FALSE), new BSPTree<Euclidean1D>(Boolean.TRUE), null), null);
        }
    }
    
    @Override
    public IntervalsSet buildNew(final BSPTree<Euclidean1D> tree) {
        return new IntervalsSet(tree, this.getTolerance());
    }
    
    @Override
    protected void computeGeometricalProperties() {
        if (((AbstractRegion<Euclidean1D, T>)this).getTree(false).getCut() == null) {
            ((AbstractRegion<Euclidean1D, T>)this).setBarycenter((Point<Euclidean1D>)Vector1D.NaN);
            this.setSize(((boolean)((AbstractRegion<Euclidean1D, T>)this).getTree(false).getAttribute()) ? Double.POSITIVE_INFINITY : 0.0);
        }
        else {
            double size = 0.0;
            double sum = 0.0;
            for (final Interval interval : this.asList()) {
                size += interval.getSize();
                sum += interval.getSize() * interval.getBarycenter();
            }
            this.setSize(size);
            if (Double.isInfinite(size)) {
                ((AbstractRegion<Euclidean1D, T>)this).setBarycenter((Point<Euclidean1D>)Vector1D.NaN);
            }
            else if (size >= Precision.SAFE_MIN) {
                ((AbstractRegion<Euclidean1D, T>)this).setBarycenter((Point<Euclidean1D>)new Vector1D(sum / size));
            }
            else {
                ((AbstractRegion<Euclidean1D, T>)this).setBarycenter((Point<Euclidean1D>)((OrientedPoint)((AbstractRegion<Euclidean1D, T>)this).getTree(false).getCut().getHyperplane()).getLocation());
            }
        }
    }
    
    public double getInf() {
        BSPTree<Euclidean1D> node = ((AbstractRegion<Euclidean1D, T>)this).getTree(false);
        double inf = Double.POSITIVE_INFINITY;
        while (node.getCut() != null) {
            final OrientedPoint op = (OrientedPoint)node.getCut().getHyperplane();
            inf = op.getLocation().getX();
            node = (op.isDirect() ? node.getMinus() : node.getPlus());
        }
        return node.getAttribute() ? Double.NEGATIVE_INFINITY : inf;
    }
    
    public double getSup() {
        BSPTree<Euclidean1D> node = ((AbstractRegion<Euclidean1D, T>)this).getTree(false);
        double sup = Double.NEGATIVE_INFINITY;
        while (node.getCut() != null) {
            final OrientedPoint op = (OrientedPoint)node.getCut().getHyperplane();
            sup = op.getLocation().getX();
            node = (op.isDirect() ? node.getPlus() : node.getMinus());
        }
        return node.getAttribute() ? Double.POSITIVE_INFINITY : sup;
    }
    
    @Override
    public BoundaryProjection<Euclidean1D> projectToBoundary(final Point<Euclidean1D> point) {
        final double x = ((Vector1D)point).getX();
        double previous = Double.NEGATIVE_INFINITY;
        for (final double[] a : this) {
            if (x < a[0]) {
                final double previousOffset = x - previous;
                final double currentOffset = a[0] - x;
                if (previousOffset < currentOffset) {
                    return new BoundaryProjection<Euclidean1D>(point, this.finiteOrNullPoint(previous), previousOffset);
                }
                return new BoundaryProjection<Euclidean1D>(point, this.finiteOrNullPoint(a[0]), currentOffset);
            }
            else if (x <= a[1]) {
                final double offset0 = a[0] - x;
                final double offset2 = x - a[1];
                if (offset0 < offset2) {
                    return new BoundaryProjection<Euclidean1D>(point, this.finiteOrNullPoint(a[1]), offset2);
                }
                return new BoundaryProjection<Euclidean1D>(point, this.finiteOrNullPoint(a[0]), offset0);
            }
            else {
                previous = a[1];
            }
        }
        return new BoundaryProjection<Euclidean1D>(point, this.finiteOrNullPoint(previous), x - previous);
    }
    
    private Vector1D finiteOrNullPoint(final double x) {
        return Double.isInfinite(x) ? null : new Vector1D(x);
    }
    
    public List<Interval> asList() {
        final List<Interval> list = new ArrayList<Interval>();
        for (final double[] a : this) {
            list.add(new Interval(a[0], a[1]));
        }
        return list;
    }
    
    private BSPTree<Euclidean1D> getFirstLeaf(final BSPTree<Euclidean1D> root) {
        if (root.getCut() == null) {
            return root;
        }
        BSPTree<Euclidean1D> smallest = null;
        for (BSPTree<Euclidean1D> n = root; n != null; n = this.previousInternalNode(n)) {
            smallest = n;
        }
        return this.leafBefore(smallest);
    }
    
    private BSPTree<Euclidean1D> getFirstIntervalBoundary() {
        BSPTree<Euclidean1D> node = ((AbstractRegion<Euclidean1D, T>)this).getTree(false);
        if (node.getCut() == null) {
            return null;
        }
        for (node = this.getFirstLeaf(node).getParent(); node != null && !this.isIntervalStart(node) && !this.isIntervalEnd(node); node = this.nextInternalNode(node)) {}
        return node;
    }
    
    private boolean isIntervalStart(final BSPTree<Euclidean1D> node) {
        return !(boolean)this.leafBefore(node).getAttribute() && (boolean)this.leafAfter(node).getAttribute();
    }
    
    private boolean isIntervalEnd(final BSPTree<Euclidean1D> node) {
        return (boolean)this.leafBefore(node).getAttribute() && !(boolean)this.leafAfter(node).getAttribute();
    }
    
    private BSPTree<Euclidean1D> nextInternalNode(BSPTree<Euclidean1D> node) {
        if (this.childAfter(node).getCut() != null) {
            return this.leafAfter(node).getParent();
        }
        while (this.isAfterParent(node)) {
            node = node.getParent();
        }
        return node.getParent();
    }
    
    private BSPTree<Euclidean1D> previousInternalNode(BSPTree<Euclidean1D> node) {
        if (this.childBefore(node).getCut() != null) {
            return this.leafBefore(node).getParent();
        }
        while (this.isBeforeParent(node)) {
            node = node.getParent();
        }
        return node.getParent();
    }
    
    private BSPTree<Euclidean1D> leafBefore(BSPTree<Euclidean1D> node) {
        for (node = this.childBefore(node); node.getCut() != null; node = this.childAfter(node)) {}
        return node;
    }
    
    private BSPTree<Euclidean1D> leafAfter(BSPTree<Euclidean1D> node) {
        for (node = this.childAfter(node); node.getCut() != null; node = this.childBefore(node)) {}
        return node;
    }
    
    private boolean isBeforeParent(final BSPTree<Euclidean1D> node) {
        final BSPTree<Euclidean1D> parent = node.getParent();
        return parent != null && node == this.childBefore(parent);
    }
    
    private boolean isAfterParent(final BSPTree<Euclidean1D> node) {
        final BSPTree<Euclidean1D> parent = node.getParent();
        return parent != null && node == this.childAfter(parent);
    }
    
    private BSPTree<Euclidean1D> childBefore(final BSPTree<Euclidean1D> node) {
        if (this.isDirect(node)) {
            return node.getMinus();
        }
        return node.getPlus();
    }
    
    private BSPTree<Euclidean1D> childAfter(final BSPTree<Euclidean1D> node) {
        if (this.isDirect(node)) {
            return node.getPlus();
        }
        return node.getMinus();
    }
    
    private boolean isDirect(final BSPTree<Euclidean1D> node) {
        return ((OrientedPoint)node.getCut().getHyperplane()).isDirect();
    }
    
    private double getAngle(final BSPTree<Euclidean1D> node) {
        return ((OrientedPoint)node.getCut().getHyperplane()).getLocation().getX();
    }
    
    public Iterator<double[]> iterator() {
        return new SubIntervalsIterator();
    }
    
    private class SubIntervalsIterator implements Iterator<double[]>
    {
        private BSPTree<Euclidean1D> current;
        private double[] pending;
        
        SubIntervalsIterator() {
            this.current = IntervalsSet.this.getFirstIntervalBoundary();
            if (this.current == null) {
                if (IntervalsSet.this.getFirstLeaf(IntervalsSet.this.getTree(false)).getAttribute()) {
                    this.pending = new double[] { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
                }
                else {
                    this.pending = null;
                }
            }
            else if (IntervalsSet.this.isIntervalEnd(this.current)) {
                this.pending = new double[] { Double.NEGATIVE_INFINITY, IntervalsSet.this.getAngle(this.current) };
            }
            else {
                this.selectPending();
            }
        }
        
        private void selectPending() {
            BSPTree<Euclidean1D> start;
            for (start = this.current; start != null && !IntervalsSet.this.isIntervalStart(start); start = IntervalsSet.this.nextInternalNode(start)) {}
            if (start == null) {
                this.current = null;
                this.pending = null;
                return;
            }
            BSPTree<Euclidean1D> end;
            for (end = start; end != null && !IntervalsSet.this.isIntervalEnd(end); end = IntervalsSet.this.nextInternalNode(end)) {}
            if (end != null) {
                this.pending = new double[] { IntervalsSet.this.getAngle(start), IntervalsSet.this.getAngle(end) };
                this.current = end;
            }
            else {
                this.pending = new double[] { IntervalsSet.this.getAngle(start), Double.POSITIVE_INFINITY };
                this.current = null;
            }
        }
        
        public boolean hasNext() {
            return this.pending != null;
        }
        
        public double[] next() {
            if (this.pending == null) {
                throw new NoSuchElementException();
            }
            final double[] next = this.pending;
            this.selectPending();
            return next;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

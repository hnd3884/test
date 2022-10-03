package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.util.NoSuchElementException;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.geometry.partitioning.Hyperplane;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.geometry.partitioning.Side;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.partitioning.BoundaryProjection;
import java.util.Iterator;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.geometry.partitioning.SubHyperplane;
import java.util.Collection;
import org.apache.commons.math3.geometry.partitioning.BSPTree;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.geometry.partitioning.AbstractRegion;

public class ArcsSet extends AbstractRegion<Sphere1D, Sphere1D> implements Iterable<double[]>
{
    public ArcsSet(final double tolerance) {
        super(tolerance);
    }
    
    public ArcsSet(final double lower, final double upper, final double tolerance) throws NumberIsTooLargeException {
        super(buildTree(lower, upper, tolerance), tolerance);
    }
    
    public ArcsSet(final BSPTree<Sphere1D> tree, final double tolerance) throws InconsistentStateAt2PiWrapping {
        super(tree, tolerance);
        this.check2PiConsistency();
    }
    
    public ArcsSet(final Collection<SubHyperplane<Sphere1D>> boundary, final double tolerance) throws InconsistentStateAt2PiWrapping {
        super(boundary, tolerance);
        this.check2PiConsistency();
    }
    
    private static BSPTree<Sphere1D> buildTree(final double lower, final double upper, final double tolerance) throws NumberIsTooLargeException {
        if (Precision.equals(lower, upper, 0) || upper - lower >= 6.283185307179586) {
            return new BSPTree<Sphere1D>(Boolean.TRUE);
        }
        if (lower > upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, lower, upper, true);
        }
        final double normalizedLower = MathUtils.normalizeAngle(lower, 3.141592653589793);
        final double normalizedUpper = normalizedLower + (upper - lower);
        final SubHyperplane<Sphere1D> lowerCut = new LimitAngle(new S1Point(normalizedLower), false, tolerance).wholeHyperplane();
        if (normalizedUpper <= 6.283185307179586) {
            final SubHyperplane<Sphere1D> upperCut = new LimitAngle(new S1Point(normalizedUpper), true, tolerance).wholeHyperplane();
            return new BSPTree<Sphere1D>(lowerCut, new BSPTree<Sphere1D>(Boolean.FALSE), new BSPTree<Sphere1D>(upperCut, new BSPTree<Sphere1D>(Boolean.FALSE), new BSPTree<Sphere1D>(Boolean.TRUE), null), null);
        }
        final SubHyperplane<Sphere1D> upperCut = new LimitAngle(new S1Point(normalizedUpper - 6.283185307179586), true, tolerance).wholeHyperplane();
        return new BSPTree<Sphere1D>(lowerCut, new BSPTree<Sphere1D>(upperCut, new BSPTree<Sphere1D>(Boolean.FALSE), new BSPTree<Sphere1D>(Boolean.TRUE), null), new BSPTree<Sphere1D>(Boolean.TRUE), null);
    }
    
    private void check2PiConsistency() throws InconsistentStateAt2PiWrapping {
        final BSPTree<Sphere1D> root = ((AbstractRegion<Sphere1D, T>)this).getTree(false);
        if (root.getCut() == null) {
            return;
        }
        final Boolean stateBefore = (Boolean)this.getFirstLeaf(root).getAttribute();
        final Boolean stateAfter = (Boolean)this.getLastLeaf(root).getAttribute();
        if (stateBefore ^ stateAfter) {
            throw new InconsistentStateAt2PiWrapping();
        }
    }
    
    private BSPTree<Sphere1D> getFirstLeaf(final BSPTree<Sphere1D> root) {
        if (root.getCut() == null) {
            return root;
        }
        BSPTree<Sphere1D> smallest = null;
        for (BSPTree<Sphere1D> n = root; n != null; n = this.previousInternalNode(n)) {
            smallest = n;
        }
        return this.leafBefore(smallest);
    }
    
    private BSPTree<Sphere1D> getLastLeaf(final BSPTree<Sphere1D> root) {
        if (root.getCut() == null) {
            return root;
        }
        BSPTree<Sphere1D> largest = null;
        for (BSPTree<Sphere1D> n = root; n != null; n = this.nextInternalNode(n)) {
            largest = n;
        }
        return this.leafAfter(largest);
    }
    
    private BSPTree<Sphere1D> getFirstArcStart() {
        BSPTree<Sphere1D> node = ((AbstractRegion<Sphere1D, T>)this).getTree(false);
        if (node.getCut() == null) {
            return null;
        }
        for (node = this.getFirstLeaf(node).getParent(); node != null && !this.isArcStart(node); node = this.nextInternalNode(node)) {}
        return node;
    }
    
    private boolean isArcStart(final BSPTree<Sphere1D> node) {
        return !(boolean)this.leafBefore(node).getAttribute() && (boolean)this.leafAfter(node).getAttribute();
    }
    
    private boolean isArcEnd(final BSPTree<Sphere1D> node) {
        return (boolean)this.leafBefore(node).getAttribute() && !(boolean)this.leafAfter(node).getAttribute();
    }
    
    private BSPTree<Sphere1D> nextInternalNode(BSPTree<Sphere1D> node) {
        if (this.childAfter(node).getCut() != null) {
            return this.leafAfter(node).getParent();
        }
        while (this.isAfterParent(node)) {
            node = node.getParent();
        }
        return node.getParent();
    }
    
    private BSPTree<Sphere1D> previousInternalNode(BSPTree<Sphere1D> node) {
        if (this.childBefore(node).getCut() != null) {
            return this.leafBefore(node).getParent();
        }
        while (this.isBeforeParent(node)) {
            node = node.getParent();
        }
        return node.getParent();
    }
    
    private BSPTree<Sphere1D> leafBefore(BSPTree<Sphere1D> node) {
        for (node = this.childBefore(node); node.getCut() != null; node = this.childAfter(node)) {}
        return node;
    }
    
    private BSPTree<Sphere1D> leafAfter(BSPTree<Sphere1D> node) {
        for (node = this.childAfter(node); node.getCut() != null; node = this.childBefore(node)) {}
        return node;
    }
    
    private boolean isBeforeParent(final BSPTree<Sphere1D> node) {
        final BSPTree<Sphere1D> parent = node.getParent();
        return parent != null && node == this.childBefore(parent);
    }
    
    private boolean isAfterParent(final BSPTree<Sphere1D> node) {
        final BSPTree<Sphere1D> parent = node.getParent();
        return parent != null && node == this.childAfter(parent);
    }
    
    private BSPTree<Sphere1D> childBefore(final BSPTree<Sphere1D> node) {
        if (this.isDirect(node)) {
            return node.getMinus();
        }
        return node.getPlus();
    }
    
    private BSPTree<Sphere1D> childAfter(final BSPTree<Sphere1D> node) {
        if (this.isDirect(node)) {
            return node.getPlus();
        }
        return node.getMinus();
    }
    
    private boolean isDirect(final BSPTree<Sphere1D> node) {
        return ((LimitAngle)node.getCut().getHyperplane()).isDirect();
    }
    
    private double getAngle(final BSPTree<Sphere1D> node) {
        return ((LimitAngle)node.getCut().getHyperplane()).getLocation().getAlpha();
    }
    
    @Override
    public ArcsSet buildNew(final BSPTree<Sphere1D> tree) {
        return new ArcsSet(tree, this.getTolerance());
    }
    
    @Override
    protected void computeGeometricalProperties() {
        if (((AbstractRegion<Sphere1D, T>)this).getTree(false).getCut() == null) {
            ((AbstractRegion<Sphere1D, T>)this).setBarycenter(S1Point.NaN);
            this.setSize(((boolean)((AbstractRegion<Sphere1D, T>)this).getTree(false).getAttribute()) ? 6.283185307179586 : 0.0);
        }
        else {
            double size = 0.0;
            double sum = 0.0;
            for (final double[] a : this) {
                final double length = a[1] - a[0];
                size += length;
                sum += length * (a[0] + a[1]);
            }
            this.setSize(size);
            if (Precision.equals(size, 6.283185307179586, 0)) {
                ((AbstractRegion<Sphere1D, T>)this).setBarycenter(S1Point.NaN);
            }
            else if (size >= Precision.SAFE_MIN) {
                ((AbstractRegion<Sphere1D, T>)this).setBarycenter(new S1Point(sum / (2.0 * size)));
            }
            else {
                final LimitAngle limit = (LimitAngle)((AbstractRegion<Sphere1D, T>)this).getTree(false).getCut().getHyperplane();
                ((AbstractRegion<Sphere1D, T>)this).setBarycenter(limit.getLocation());
            }
        }
    }
    
    @Override
    public BoundaryProjection<Sphere1D> projectToBoundary(final Point<Sphere1D> point) {
        final double alpha = ((S1Point)point).getAlpha();
        boolean wrapFirst = false;
        double first = Double.NaN;
        double previous = Double.NaN;
        for (final double[] a : this) {
            if (Double.isNaN(first)) {
                first = a[0];
            }
            if (!wrapFirst) {
                if (alpha < a[0]) {
                    if (Double.isNaN(previous)) {
                        wrapFirst = true;
                    }
                    else {
                        final double previousOffset = alpha - previous;
                        final double currentOffset = a[0] - alpha;
                        if (previousOffset < currentOffset) {
                            return new BoundaryProjection<Sphere1D>(point, new S1Point(previous), previousOffset);
                        }
                        return new BoundaryProjection<Sphere1D>(point, new S1Point(a[0]), currentOffset);
                    }
                }
                else if (alpha <= a[1]) {
                    final double offset0 = a[0] - alpha;
                    final double offset2 = alpha - a[1];
                    if (offset0 < offset2) {
                        return new BoundaryProjection<Sphere1D>(point, new S1Point(a[1]), offset2);
                    }
                    return new BoundaryProjection<Sphere1D>(point, new S1Point(a[0]), offset0);
                }
            }
            previous = a[1];
        }
        if (Double.isNaN(previous)) {
            return new BoundaryProjection<Sphere1D>(point, null, 6.283185307179586);
        }
        if (wrapFirst) {
            final double previousOffset2 = alpha - (previous - 6.283185307179586);
            final double currentOffset2 = first - alpha;
            if (previousOffset2 < currentOffset2) {
                return new BoundaryProjection<Sphere1D>(point, new S1Point(previous), previousOffset2);
            }
            return new BoundaryProjection<Sphere1D>(point, new S1Point(first), currentOffset2);
        }
        else {
            final double previousOffset2 = alpha - previous;
            final double currentOffset2 = first + 6.283185307179586 - alpha;
            if (previousOffset2 < currentOffset2) {
                return new BoundaryProjection<Sphere1D>(point, new S1Point(previous), previousOffset2);
            }
            return new BoundaryProjection<Sphere1D>(point, new S1Point(first), currentOffset2);
        }
    }
    
    public List<Arc> asList() {
        final List<Arc> list = new ArrayList<Arc>();
        for (final double[] a : this) {
            list.add(new Arc(a[0], a[1], this.getTolerance()));
        }
        return list;
    }
    
    public Iterator<double[]> iterator() {
        return new SubArcsIterator();
    }
    
    @Deprecated
    public Side side(final Arc arc) {
        return this.split(arc).getSide();
    }
    
    public Split split(final Arc arc) {
        final List<Double> minus = new ArrayList<Double>();
        final List<Double> plus = new ArrayList<Double>();
        final double reference = 3.141592653589793 + arc.getInf();
        final double arcLength = arc.getSup() - arc.getInf();
        for (final double[] a : this) {
            final double syncedStart = MathUtils.normalizeAngle(a[0], reference) - arc.getInf();
            final double arcOffset = a[0] - syncedStart;
            final double syncedEnd = a[1] - arcOffset;
            if (syncedStart < arcLength) {
                minus.add(a[0]);
                if (syncedEnd > arcLength) {
                    final double minusToPlus = arcLength + arcOffset;
                    minus.add(minusToPlus);
                    plus.add(minusToPlus);
                    if (syncedEnd > 6.283185307179586) {
                        final double plusToMinus = 6.283185307179586 + arcOffset;
                        plus.add(plusToMinus);
                        minus.add(plusToMinus);
                        minus.add(a[1]);
                    }
                    else {
                        plus.add(a[1]);
                    }
                }
                else {
                    minus.add(a[1]);
                }
            }
            else {
                plus.add(a[0]);
                if (syncedEnd > 6.283185307179586) {
                    final double plusToMinus2 = 6.283185307179586 + arcOffset;
                    plus.add(plusToMinus2);
                    minus.add(plusToMinus2);
                    if (syncedEnd > 6.283185307179586 + arcLength) {
                        final double minusToPlus2 = 6.283185307179586 + arcLength + arcOffset;
                        minus.add(minusToPlus2);
                        plus.add(minusToPlus2);
                        plus.add(a[1]);
                    }
                    else {
                        minus.add(a[1]);
                    }
                }
                else {
                    plus.add(a[1]);
                }
            }
        }
        return new Split(this.createSplitPart(plus), this.createSplitPart(minus));
    }
    
    private void addArcLimit(final BSPTree<Sphere1D> tree, final double alpha, final boolean isStart) {
        final LimitAngle limit = new LimitAngle(new S1Point(alpha), !isStart, this.getTolerance());
        final BSPTree<Sphere1D> node = tree.getCell(limit.getLocation(), this.getTolerance());
        if (node.getCut() != null) {
            throw new MathInternalError();
        }
        node.insertCut(limit);
        node.setAttribute(null);
        node.getPlus().setAttribute(Boolean.FALSE);
        node.getMinus().setAttribute(Boolean.TRUE);
    }
    
    private ArcsSet createSplitPart(final List<Double> limits) {
        if (limits.isEmpty()) {
            return null;
        }
        for (int i = 0; i < limits.size(); ++i) {
            final int j = (i + 1) % limits.size();
            final double lA = limits.get(i);
            final double lB = MathUtils.normalizeAngle(limits.get(j), lA);
            if (FastMath.abs(lB - lA) <= this.getTolerance()) {
                if (j > 0) {
                    limits.remove(j);
                    limits.remove(i);
                    --i;
                }
                else {
                    final double lEnd = limits.remove(limits.size() - 1);
                    final double lStart = limits.remove(0);
                    if (limits.isEmpty()) {
                        if (lEnd - lStart > 3.141592653589793) {
                            return new ArcsSet(new BSPTree<Sphere1D>(Boolean.TRUE), this.getTolerance());
                        }
                        return null;
                    }
                    else {
                        limits.add(limits.remove(0) + 6.283185307179586);
                    }
                }
            }
        }
        final BSPTree<Sphere1D> tree = new BSPTree<Sphere1D>(Boolean.FALSE);
        for (int k = 0; k < limits.size() - 1; k += 2) {
            this.addArcLimit(tree, limits.get(k), true);
            this.addArcLimit(tree, limits.get(k + 1), false);
        }
        if (tree.getCut() == null) {
            return null;
        }
        return new ArcsSet(tree, this.getTolerance());
    }
    
    private class SubArcsIterator implements Iterator<double[]>
    {
        private final BSPTree<Sphere1D> firstStart;
        private BSPTree<Sphere1D> current;
        private double[] pending;
        
        SubArcsIterator() {
            this.firstStart = ArcsSet.this.getFirstArcStart();
            this.current = this.firstStart;
            if (this.firstStart == null) {
                if (ArcsSet.this.getFirstLeaf(ArcsSet.this.getTree(false)).getAttribute()) {
                    this.pending = new double[] { 0.0, 6.283185307179586 };
                }
                else {
                    this.pending = null;
                }
            }
            else {
                this.selectPending();
            }
        }
        
        private void selectPending() {
            BSPTree<Sphere1D> start;
            for (start = this.current; start != null && !ArcsSet.this.isArcStart(start); start = ArcsSet.this.nextInternalNode(start)) {}
            if (start == null) {
                this.current = null;
                this.pending = null;
                return;
            }
            BSPTree<Sphere1D> end;
            for (end = start; end != null && !ArcsSet.this.isArcEnd(end); end = ArcsSet.this.nextInternalNode(end)) {}
            if (end != null) {
                this.pending = new double[] { ArcsSet.this.getAngle(start), ArcsSet.this.getAngle(end) };
                this.current = end;
            }
            else {
                for (end = this.firstStart; end != null && !ArcsSet.this.isArcEnd(end); end = ArcsSet.this.previousInternalNode(end)) {}
                if (end == null) {
                    throw new MathInternalError();
                }
                this.pending = new double[] { ArcsSet.this.getAngle(start), ArcsSet.this.getAngle(end) + 6.283185307179586 };
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
    
    public static class Split
    {
        private final ArcsSet plus;
        private final ArcsSet minus;
        
        private Split(final ArcsSet plus, final ArcsSet minus) {
            this.plus = plus;
            this.minus = minus;
        }
        
        public ArcsSet getPlus() {
            return this.plus;
        }
        
        public ArcsSet getMinus() {
            return this.minus;
        }
        
        public Side getSide() {
            if (this.plus != null) {
                if (this.minus != null) {
                    return Side.BOTH;
                }
                return Side.PLUS;
            }
            else {
                if (this.minus != null) {
                    return Side.MINUS;
                }
                return Side.HYPER;
            }
        }
    }
    
    public static class InconsistentStateAt2PiWrapping extends MathIllegalArgumentException
    {
        private static final long serialVersionUID = 20140107L;
        
        public InconsistentStateAt2PiWrapping() {
            super(LocalizedFormats.INCONSISTENT_STATE_AT_2_PI_WRAPPING, new Object[0]);
        }
    }
}

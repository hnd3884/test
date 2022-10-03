package org.apache.lucene.facet.range;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

final class LongRangeCounter
{
    final LongRangeNode root;
    final long[] boundaries;
    final int[] leafCounts;
    private int leafUpto;
    private int missingCount;
    
    public LongRangeCounter(final LongRange[] ranges) {
        final Map<Long, Integer> endsMap = new HashMap<Long, Integer>();
        endsMap.put(Long.MIN_VALUE, 1);
        endsMap.put(Long.MAX_VALUE, 2);
        for (final LongRange range : ranges) {
            Integer cur = endsMap.get(range.minIncl);
            if (cur == null) {
                endsMap.put(range.minIncl, 1);
            }
            else {
                endsMap.put(range.minIncl, cur | 0x1);
            }
            cur = endsMap.get(range.maxIncl);
            if (cur == null) {
                endsMap.put(range.maxIncl, 2);
            }
            else {
                endsMap.put(range.maxIncl, cur | 0x2);
            }
        }
        final List<Long> endsList = new ArrayList<Long>(endsMap.keySet());
        Collections.sort(endsList);
        final List<InclusiveRange> elementaryIntervals = new ArrayList<InclusiveRange>();
        int upto0 = 1;
        long v = endsList.get(0);
        long prev;
        if (endsMap.get(v) == 3) {
            elementaryIntervals.add(new InclusiveRange(v, v));
            prev = v + 1L;
        }
        else {
            prev = v;
        }
        while (upto0 < endsList.size()) {
            v = endsList.get(upto0);
            final int flags = endsMap.get(v);
            if (flags == 3) {
                if (v > prev) {
                    elementaryIntervals.add(new InclusiveRange(prev, v - 1L));
                }
                elementaryIntervals.add(new InclusiveRange(v, v));
                prev = v + 1L;
            }
            else if (flags == 1) {
                if (v > prev) {
                    elementaryIntervals.add(new InclusiveRange(prev, v - 1L));
                }
                prev = v;
            }
            else {
                assert flags == 2;
                elementaryIntervals.add(new InclusiveRange(prev, v));
                prev = v + 1L;
            }
            ++upto0;
        }
        this.root = split(0, elementaryIntervals.size(), elementaryIntervals);
        for (int i = 0; i < ranges.length; ++i) {
            this.root.addOutputs(i, ranges[i]);
        }
        this.boundaries = new long[elementaryIntervals.size()];
        for (int i = 0; i < this.boundaries.length; ++i) {
            this.boundaries[i] = elementaryIntervals.get(i).end;
        }
        this.leafCounts = new int[this.boundaries.length];
    }
    
    public void add(final long v) {
        int lo = 0;
        int hi = this.boundaries.length - 1;
        while (true) {
            final int mid = lo + hi >>> 1;
            if (v <= this.boundaries[mid]) {
                if (mid == 0) {
                    final int[] leafCounts = this.leafCounts;
                    final int n = 0;
                    ++leafCounts[n];
                    return;
                }
                hi = mid - 1;
            }
            else {
                if (v <= this.boundaries[mid + 1]) {
                    final int[] leafCounts2 = this.leafCounts;
                    final int n2 = mid + 1;
                    ++leafCounts2[n2];
                    return;
                }
                lo = mid + 1;
            }
        }
    }
    
    public int fillCounts(final int[] counts) {
        this.missingCount = 0;
        this.leafUpto = 0;
        this.rollup(this.root, counts, false);
        return this.missingCount;
    }
    
    private int rollup(final LongRangeNode node, final int[] counts, boolean sawOutputs) {
        sawOutputs |= (node.outputs != null);
        int count;
        if (node.left != null) {
            count = this.rollup(node.left, counts, sawOutputs);
            count += this.rollup(node.right, counts, sawOutputs);
        }
        else {
            count = this.leafCounts[this.leafUpto];
            ++this.leafUpto;
            if (!sawOutputs) {
                this.missingCount += count;
            }
        }
        if (node.outputs != null) {
            for (final int intValue : node.outputs) {
                final int rangeIndex = intValue;
                counts[intValue] += count;
            }
        }
        return count;
    }
    
    private static LongRangeNode split(final int start, final int end, final List<InclusiveRange> elementaryIntervals) {
        if (start == end - 1) {
            final InclusiveRange range = elementaryIntervals.get(start);
            return new LongRangeNode(range.start, range.end, null, null, start);
        }
        final int mid = start + end >>> 1;
        final LongRangeNode left = split(start, mid, elementaryIntervals);
        final LongRangeNode right = split(mid, end, elementaryIntervals);
        return new LongRangeNode(left.start, right.end, left, right, -1);
    }
    
    private static final class InclusiveRange
    {
        public final long start;
        public final long end;
        
        public InclusiveRange(final long start, final long end) {
            assert end >= start;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString() {
            return this.start + " to " + this.end;
        }
    }
    
    public static final class LongRangeNode
    {
        final LongRangeNode left;
        final LongRangeNode right;
        final long start;
        final long end;
        final int leafIndex;
        List<Integer> outputs;
        
        public LongRangeNode(final long start, final long end, final LongRangeNode left, final LongRangeNode right, final int leafIndex) {
            this.start = start;
            this.end = end;
            this.left = left;
            this.right = right;
            this.leafIndex = leafIndex;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            this.toString(sb, 0);
            return sb.toString();
        }
        
        static void indent(final StringBuilder sb, final int depth) {
            for (int i = 0; i < depth; ++i) {
                sb.append("  ");
            }
        }
        
        void addOutputs(final int index, final LongRange range) {
            if (this.start >= range.minIncl && this.end <= range.maxIncl) {
                if (this.outputs == null) {
                    this.outputs = new ArrayList<Integer>();
                }
                this.outputs.add(index);
            }
            else if (this.left != null) {
                assert this.right != null;
                this.left.addOutputs(index, range);
                this.right.addOutputs(index, range);
            }
        }
        
        void toString(final StringBuilder sb, final int depth) {
            indent(sb, depth);
            if (this.left == null) {
                assert this.right == null;
                sb.append("leaf: " + this.start + " to " + this.end);
            }
            else {
                sb.append("node: " + this.start + " to " + this.end);
            }
            if (this.outputs != null) {
                sb.append(" outputs=");
                sb.append(this.outputs);
            }
            sb.append('\n');
            if (this.left != null) {
                assert this.right != null;
                this.left.toString(sb, depth + 1);
                this.right.toString(sb, depth + 1);
            }
        }
    }
}

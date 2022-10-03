package org.apache.lucene.rangetree;

import org.apache.lucene.util.DocIdSetBuilder;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.index.SortedNumericDocValues;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Accountable;

final class RangeTreeReader implements Accountable
{
    private final long[] blockFPs;
    private final long[] blockMinValues;
    final IndexInput in;
    final long globalMaxValue;
    final int approxDocsPerBlock;
    
    public RangeTreeReader(final IndexInput in) throws IOException {
        final int numLeaves = in.readVInt();
        this.approxDocsPerBlock = in.readVInt();
        this.blockMinValues = new long[numLeaves];
        for (int i = 0; i < numLeaves; ++i) {
            this.blockMinValues[i] = in.readLong();
        }
        this.blockFPs = new long[numLeaves];
        for (int i = 0; i < numLeaves; ++i) {
            this.blockFPs[i] = in.readVLong();
        }
        this.globalMaxValue = in.readLong();
        this.in = in;
    }
    
    public long getMinValue() {
        return this.blockMinValues[0];
    }
    
    public long getMaxValue() {
        return this.globalMaxValue;
    }
    
    public DocIdSet intersect(final long minIncl, final long maxIncl, final SortedNumericDocValues sndv, final int maxDoc) throws IOException {
        if (minIncl > maxIncl) {
            return DocIdSet.EMPTY;
        }
        if (minIncl > this.globalMaxValue || maxIncl < this.blockMinValues[0]) {
            return DocIdSet.EMPTY;
        }
        final QueryState state = new QueryState(this.in.clone(), maxDoc, minIncl, maxIncl, sndv);
        int startBlockIncl = Arrays.binarySearch(this.blockMinValues, minIncl);
        if (startBlockIncl >= 0) {
            while (startBlockIncl > 0 && this.blockMinValues[startBlockIncl] == minIncl) {
                --startBlockIncl;
            }
        }
        else {
            startBlockIncl = Math.max(-startBlockIncl - 2, 0);
        }
        int endBlockIncl = Arrays.binarySearch(this.blockMinValues, maxIncl);
        if (endBlockIncl >= 0) {
            while (endBlockIncl < this.blockMinValues.length - 1 && this.blockMinValues[endBlockIncl] == maxIncl) {
                ++endBlockIncl;
            }
        }
        else {
            endBlockIncl = Math.max(-endBlockIncl - 2, 0);
        }
        assert startBlockIncl <= endBlockIncl;
        state.in.seek(this.blockFPs[startBlockIncl]);
        state.docs.grow(this.approxDocsPerBlock * (endBlockIncl - startBlockIncl + 1));
        int hitCount = 0;
        for (int block = startBlockIncl; block <= endBlockIncl; ++block) {
            final boolean doFilter = this.blockMinValues[block] <= minIncl || block == this.blockMinValues.length - 1 || this.blockMinValues[block + 1] >= maxIncl;
            int newCount;
            if (doFilter) {
                newCount = this.addSome(state);
            }
            else {
                newCount = this.addAll(state);
            }
            hitCount += newCount;
        }
        return state.docs.build((long)hitCount);
    }
    
    private int addAll(final QueryState state) throws IOException {
        final int count = state.in.readVInt();
        state.docs.grow(count);
        for (int i = 0; i < count; ++i) {
            final int docID = state.in.readInt();
            state.docs.add(docID);
        }
        return count;
    }
    
    private int addSome(final QueryState state) throws IOException {
        int hitCount = 0;
        final int count = state.in.readVInt();
        state.docs.grow(count);
        for (int i = 0; i < count; ++i) {
            final int docID = state.in.readInt();
            state.sndv.setDocument(docID);
            for (int docValueCount = state.sndv.count(), j = 0; j < docValueCount; ++j) {
                final long value = state.sndv.valueAt(j);
                if (value >= state.minValueIncl && value <= state.maxValueIncl) {
                    state.docs.add(docID);
                    ++hitCount;
                    break;
                }
            }
        }
        return hitCount;
    }
    
    public long ramBytesUsed() {
        return this.blockMinValues.length * 8 + this.blockFPs.length * 8;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    private static final class QueryState
    {
        final IndexInput in;
        final DocIdSetBuilder docs;
        final long minValueIncl;
        final long maxValueIncl;
        final SortedNumericDocValues sndv;
        
        public QueryState(final IndexInput in, final int maxDoc, final long minValueIncl, final long maxValueIncl, final SortedNumericDocValues sndv) {
            this.in = in;
            this.docs = new DocIdSetBuilder(maxDoc);
            this.minValueIncl = minValueIncl;
            this.maxValueIncl = maxValueIncl;
            this.sndv = sndv;
        }
    }
}

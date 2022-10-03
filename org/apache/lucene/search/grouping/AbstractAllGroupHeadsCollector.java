package org.apache.lucene.search.grouping;

import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.search.SimpleCollector;

public abstract class AbstractAllGroupHeadsCollector<GH extends GroupHead> extends SimpleCollector
{
    protected final int[] reversed;
    protected final int compIDXEnd;
    protected final TemporalResult temporalResult;
    
    protected AbstractAllGroupHeadsCollector(final int numberOfSorts) {
        this.reversed = new int[numberOfSorts];
        this.compIDXEnd = numberOfSorts - 1;
        this.temporalResult = new TemporalResult();
    }
    
    public FixedBitSet retrieveGroupHeads(final int maxDoc) {
        final FixedBitSet bitSet = new FixedBitSet(maxDoc);
        final Collection<GH> groupHeads = this.getCollectedGroupHeads();
        for (final GroupHead groupHead : groupHeads) {
            bitSet.set(groupHead.doc);
        }
        return bitSet;
    }
    
    public int[] retrieveGroupHeads() {
        final Collection<GH> groupHeads = this.getCollectedGroupHeads();
        final int[] docHeads = new int[groupHeads.size()];
        int i = 0;
        for (final GroupHead groupHead : groupHeads) {
            docHeads[i++] = groupHead.doc;
        }
        return docHeads;
    }
    
    public int groupHeadsSize() {
        return this.getCollectedGroupHeads().size();
    }
    
    protected abstract void retrieveGroupHeadAndAddIfNotExist(final int p0) throws IOException;
    
    protected abstract Collection<GH> getCollectedGroupHeads();
    
    public void collect(final int doc) throws IOException {
        this.retrieveGroupHeadAndAddIfNotExist(doc);
        if (this.temporalResult.stop) {
            return;
        }
        final GH groupHead = this.temporalResult.groupHead;
        int compIDX = 0;
        while (true) {
            final int c = this.reversed[compIDX] * groupHead.compare(compIDX, doc);
            if (c < 0) {
                return;
            }
            if (c > 0) {
                groupHead.updateDocHead(doc);
                return;
            }
            if (compIDX == this.compIDXEnd) {
                return;
            }
            ++compIDX;
        }
    }
    
    protected class TemporalResult
    {
        public GH groupHead;
        public boolean stop;
    }
    
    public abstract static class GroupHead<GROUP_VALUE_TYPE>
    {
        public final GROUP_VALUE_TYPE groupValue;
        public int doc;
        
        protected GroupHead(final GROUP_VALUE_TYPE groupValue, final int doc) {
            this.groupValue = groupValue;
            this.doc = doc;
        }
        
        protected abstract int compare(final int p0, final int p1) throws IOException;
        
        protected abstract void updateDocHead(final int p0) throws IOException;
    }
}

package org.apache.lucene.search.join;

import org.apache.lucene.search.Scorer;
import java.io.IOException;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.search.Collector;

final class GlobalOrdinalsCollector implements Collector
{
    final String field;
    final LongBitSet collectedOrds;
    final MultiDocValues.OrdinalMap ordinalMap;
    
    GlobalOrdinalsCollector(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount) {
        this.field = field;
        this.ordinalMap = ordinalMap;
        this.collectedOrds = new LongBitSet(valueCount);
    }
    
    public LongBitSet getCollectorOrdinals() {
        return this.collectedOrds;
    }
    
    public boolean needsScores() {
        return false;
    }
    
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        final SortedDocValues docTermOrds = DocValues.getSorted(context.reader(), this.field);
        if (this.ordinalMap != null) {
            final LongValues segmentOrdToGlobalOrdLookup = this.ordinalMap.getGlobalOrds(context.ord);
            return (LeafCollector)new OrdinalMapCollector(docTermOrds, segmentOrdToGlobalOrdLookup);
        }
        return (LeafCollector)new SegmentOrdinalCollector(docTermOrds);
    }
    
    final class OrdinalMapCollector implements LeafCollector
    {
        private final SortedDocValues docTermOrds;
        private final LongValues segmentOrdToGlobalOrdLookup;
        
        OrdinalMapCollector(final SortedDocValues docTermOrds, final LongValues segmentOrdToGlobalOrdLookup) {
            this.docTermOrds = docTermOrds;
            this.segmentOrdToGlobalOrdLookup = segmentOrdToGlobalOrdLookup;
        }
        
        public void collect(final int doc) throws IOException {
            final long segmentOrd = this.docTermOrds.getOrd(doc);
            if (segmentOrd != -1L) {
                final long globalOrd = this.segmentOrdToGlobalOrdLookup.get(segmentOrd);
                GlobalOrdinalsCollector.this.collectedOrds.set(globalOrd);
            }
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
        }
    }
    
    final class SegmentOrdinalCollector implements LeafCollector
    {
        private final SortedDocValues docTermOrds;
        
        SegmentOrdinalCollector(final SortedDocValues docTermOrds) {
            this.docTermOrds = docTermOrds;
        }
        
        public void collect(final int doc) throws IOException {
            final long segmentOrd = this.docTermOrds.getOrd(doc);
            if (segmentOrd != -1L) {
                GlobalOrdinalsCollector.this.collectedOrds.set(segmentOrd);
            }
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
        }
    }
}

package org.apache.lucene.index;

import java.util.Collections;
import java.util.Arrays;
import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.util.packed.PackedLongValues;
import org.apache.lucene.util.Accountable;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import java.util.List;

public class MultiDocValues
{
    private MultiDocValues() {
    }
    
    public static NumericDocValues getNormValues(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getNormValues(field);
        }
        final FieldInfo fi = MultiFields.getMergedFieldInfos(r).fieldInfo(field);
        if (fi == null || !fi.hasNorms()) {
            return null;
        }
        boolean anyReal = false;
        final NumericDocValues[] values = new NumericDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            NumericDocValues v = context.reader().getNormValues(field);
            if (v == null) {
                v = DocValues.emptyNumeric();
            }
            else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        assert anyReal;
        return new NumericDocValues() {
            @Override
            public long get(final int docID) {
                final int subIndex = ReaderUtil.subIndex(docID, starts);
                return values[subIndex].get(docID - starts[subIndex]);
            }
        };
    }
    
    public static NumericDocValues getNumericValues(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getNumericDocValues(field);
        }
        boolean anyReal = false;
        final NumericDocValues[] values = new NumericDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            NumericDocValues v = context.reader().getNumericDocValues(field);
            if (v == null) {
                v = DocValues.emptyNumeric();
            }
            else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        return new NumericDocValues() {
            @Override
            public long get(final int docID) {
                final int subIndex = ReaderUtil.subIndex(docID, starts);
                return values[subIndex].get(docID - starts[subIndex]);
            }
        };
    }
    
    public static Bits getDocsWithField(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getDocsWithField(field);
        }
        boolean anyReal = false;
        boolean anyMissing = false;
        final Bits[] values = new Bits[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            Bits v = context.reader().getDocsWithField(field);
            if (v == null) {
                v = new Bits.MatchNoBits(context.reader().maxDoc());
                anyMissing = true;
            }
            else {
                anyReal = true;
                if (!(v instanceof Bits.MatchAllBits)) {
                    anyMissing = true;
                }
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        if (!anyMissing) {
            return new Bits.MatchAllBits(r.maxDoc());
        }
        return new MultiBits(values, starts, false);
    }
    
    public static BinaryDocValues getBinaryValues(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getBinaryDocValues(field);
        }
        boolean anyReal = false;
        final BinaryDocValues[] values = new BinaryDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            BinaryDocValues v = context.reader().getBinaryDocValues(field);
            if (v == null) {
                v = DocValues.emptyBinary();
            }
            else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        return new BinaryDocValues() {
            @Override
            public BytesRef get(final int docID) {
                final int subIndex = ReaderUtil.subIndex(docID, starts);
                return values[subIndex].get(docID - starts[subIndex]);
            }
        };
    }
    
    public static SortedNumericDocValues getSortedNumericValues(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getSortedNumericDocValues(field);
        }
        boolean anyReal = false;
        final SortedNumericDocValues[] values = new SortedNumericDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            SortedNumericDocValues v = context.reader().getSortedNumericDocValues(field);
            if (v == null) {
                v = DocValues.emptySortedNumeric(context.reader().maxDoc());
            }
            else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        return new SortedNumericDocValues() {
            SortedNumericDocValues current;
            
            @Override
            public void setDocument(final int doc) {
                final int subIndex = ReaderUtil.subIndex(doc, starts);
                (this.current = values[subIndex]).setDocument(doc - starts[subIndex]);
            }
            
            @Override
            public long valueAt(final int index) {
                return this.current.valueAt(index);
            }
            
            @Override
            public int count() {
                return this.current.count();
            }
        };
    }
    
    public static SortedDocValues getSortedValues(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getSortedDocValues(field);
        }
        boolean anyReal = false;
        final SortedDocValues[] values = new SortedDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            SortedDocValues v = context.reader().getSortedDocValues(field);
            if (v == null) {
                v = DocValues.emptySorted();
            }
            else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        final OrdinalMap mapping = OrdinalMap.build(r.getCoreCacheKey(), values, 0.25f);
        return new MultiSortedDocValues(values, starts, mapping);
    }
    
    public static SortedSetDocValues getSortedSetValues(final IndexReader r, final String field) throws IOException {
        final List<LeafReaderContext> leaves = r.leaves();
        final int size = leaves.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return leaves.get(0).reader().getSortedSetDocValues(field);
        }
        boolean anyReal = false;
        final SortedSetDocValues[] values = new SortedSetDocValues[size];
        final int[] starts = new int[size + 1];
        for (int i = 0; i < size; ++i) {
            final LeafReaderContext context = leaves.get(i);
            SortedSetDocValues v = context.reader().getSortedSetDocValues(field);
            if (v == null) {
                v = DocValues.emptySortedSet();
            }
            else {
                anyReal = true;
            }
            values[i] = v;
            starts[i] = context.docBase;
        }
        starts[size] = r.maxDoc();
        if (!anyReal) {
            return null;
        }
        final OrdinalMap mapping = OrdinalMap.build(r.getCoreCacheKey(), values, 0.25f);
        return new MultiSortedSetDocValues(values, starts, mapping);
    }
    
    public static class OrdinalMap implements Accountable
    {
        private static final long BASE_RAM_BYTES_USED;
        final Object owner;
        final PackedLongValues globalOrdDeltas;
        final PackedLongValues firstSegments;
        final LongValues[] segmentToGlobalOrds;
        final SegmentMap segmentMap;
        final long ramBytesUsed;
        
        public static OrdinalMap build(final Object owner, final SortedDocValues[] values, final float acceptableOverheadRatio) throws IOException {
            final TermsEnum[] subs = new TermsEnum[values.length];
            final long[] weights = new long[values.length];
            for (int i = 0; i < values.length; ++i) {
                subs[i] = values[i].termsEnum();
                weights[i] = values[i].getValueCount();
            }
            return build(owner, subs, weights, acceptableOverheadRatio);
        }
        
        public static OrdinalMap build(final Object owner, final SortedSetDocValues[] values, final float acceptableOverheadRatio) throws IOException {
            final TermsEnum[] subs = new TermsEnum[values.length];
            final long[] weights = new long[values.length];
            for (int i = 0; i < values.length; ++i) {
                subs[i] = values[i].termsEnum();
                weights[i] = values[i].getValueCount();
            }
            return build(owner, subs, weights, acceptableOverheadRatio);
        }
        
        public static OrdinalMap build(final Object owner, final TermsEnum[] subs, final long[] weights, final float acceptableOverheadRatio) throws IOException {
            if (subs.length != weights.length) {
                throw new IllegalArgumentException("subs and weights must have the same length");
            }
            final SegmentMap segmentMap = new SegmentMap(weights);
            return new OrdinalMap(owner, subs, segmentMap, acceptableOverheadRatio);
        }
        
        OrdinalMap(final Object owner, final TermsEnum[] subs, final SegmentMap segmentMap, final float acceptableOverheadRatio) throws IOException {
            this.owner = owner;
            this.segmentMap = segmentMap;
            final PackedLongValues.Builder globalOrdDeltas = PackedLongValues.monotonicBuilder(0.0f);
            final PackedLongValues.Builder firstSegments = PackedLongValues.packedBuilder(0.0f);
            final PackedLongValues.Builder[] ordDeltas = new PackedLongValues.Builder[subs.length];
            for (int i = 0; i < ordDeltas.length; ++i) {
                ordDeltas[i] = PackedLongValues.monotonicBuilder(acceptableOverheadRatio);
            }
            final long[] ordDeltaBits = new long[subs.length];
            final long[] segmentOrds = new long[subs.length];
            final ReaderSlice[] slices = new ReaderSlice[subs.length];
            final MultiTermsEnum.TermsEnumIndex[] indexes = new MultiTermsEnum.TermsEnumIndex[slices.length];
            for (int j = 0; j < slices.length; ++j) {
                slices[j] = new ReaderSlice(0, 0, j);
                indexes[j] = new MultiTermsEnum.TermsEnumIndex(subs[segmentMap.newToOld(j)], j);
            }
            final MultiTermsEnum mte = new MultiTermsEnum(slices);
            mte.reset(indexes);
            long globalOrd = 0L;
            while (mte.next() != null) {
                final MultiTermsEnum.TermsEnumWithSlice[] matches = mte.getMatchArray();
                int firstSegmentIndex = Integer.MAX_VALUE;
                long globalOrdDelta = Long.MAX_VALUE;
                for (int k = 0; k < mte.getMatchCount(); ++k) {
                    final int segmentIndex = matches[k].index;
                    final long segmentOrd = matches[k].terms.ord();
                    final long delta = globalOrd - segmentOrd;
                    if (segmentIndex < firstSegmentIndex) {
                        firstSegmentIndex = segmentIndex;
                        globalOrdDelta = delta;
                    }
                    while (segmentOrds[segmentIndex] <= segmentOrd) {
                        final long[] array = ordDeltaBits;
                        final int n = segmentIndex;
                        array[n] |= delta;
                        ordDeltas[segmentIndex].add(delta);
                        final long[] array2 = segmentOrds;
                        final int n2 = segmentIndex;
                        ++array2[n2];
                    }
                }
                assert firstSegmentIndex < segmentOrds.length;
                firstSegments.add(firstSegmentIndex);
                globalOrdDeltas.add(globalOrdDelta);
                ++globalOrd;
            }
            this.firstSegments = firstSegments.build();
            this.globalOrdDeltas = globalOrdDeltas.build();
            this.segmentToGlobalOrds = new LongValues[subs.length];
            long ramBytesUsed = OrdinalMap.BASE_RAM_BYTES_USED + this.globalOrdDeltas.ramBytesUsed() + this.firstSegments.ramBytesUsed() + RamUsageEstimator.shallowSizeOf(this.segmentToGlobalOrds) + segmentMap.ramBytesUsed();
            for (int l = 0; l < ordDeltas.length; ++l) {
                final PackedLongValues deltas = ordDeltas[l].build();
                if (ordDeltaBits[l] == 0L) {
                    this.segmentToGlobalOrds[l] = LongValues.IDENTITY;
                }
                else {
                    final int bitsRequired = (ordDeltaBits[l] < 0L) ? 64 : PackedInts.bitsRequired(ordDeltaBits[l]);
                    final long monotonicBits = deltas.ramBytesUsed() * 8L;
                    final long packedBits = bitsRequired * deltas.size();
                    if (deltas.size() <= 2147483647L && packedBits <= monotonicBits * (1.0f + acceptableOverheadRatio)) {
                        final int size = (int)deltas.size();
                        final PackedInts.Mutable newDeltas = PackedInts.getMutable(size, bitsRequired, acceptableOverheadRatio);
                        final PackedLongValues.Iterator it = deltas.iterator();
                        for (int ord = 0; ord < size; ++ord) {
                            newDeltas.set(ord, it.next());
                        }
                        assert !it.hasNext();
                        this.segmentToGlobalOrds[l] = new LongValues() {
                            @Override
                            public long get(final long ord) {
                                return ord + newDeltas.get((int)ord);
                            }
                        };
                        ramBytesUsed += newDeltas.ramBytesUsed();
                    }
                    else {
                        this.segmentToGlobalOrds[l] = new LongValues() {
                            @Override
                            public long get(final long ord) {
                                return ord + deltas.get(ord);
                            }
                        };
                        ramBytesUsed += deltas.ramBytesUsed();
                    }
                    ramBytesUsed += RamUsageEstimator.shallowSizeOf(this.segmentToGlobalOrds[l]);
                }
            }
            this.ramBytesUsed = ramBytesUsed;
        }
        
        public LongValues getGlobalOrds(final int segmentIndex) {
            return this.segmentToGlobalOrds[this.segmentMap.oldToNew(segmentIndex)];
        }
        
        public long getFirstSegmentOrd(final long globalOrd) {
            return globalOrd - this.globalOrdDeltas.get(globalOrd);
        }
        
        public int getFirstSegmentNumber(final long globalOrd) {
            return this.segmentMap.newToOld((int)this.firstSegments.get(globalOrd));
        }
        
        public long getValueCount() {
            return this.globalOrdDeltas.size();
        }
        
        @Override
        public long ramBytesUsed() {
            return this.ramBytesUsed;
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            final List<Accountable> resources = new ArrayList<Accountable>();
            resources.add(Accountables.namedAccountable("global ord deltas", this.globalOrdDeltas));
            resources.add(Accountables.namedAccountable("first segments", this.firstSegments));
            resources.add(Accountables.namedAccountable("segment map", this.segmentMap));
            return resources;
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(OrdinalMap.class);
        }
        
        private static class SegmentMap implements Accountable
        {
            private static final long BASE_RAM_BYTES_USED;
            private final int[] newToOld;
            private final int[] oldToNew;
            
            private static int[] map(final long[] weights) {
                final int[] newToOld = new int[weights.length];
                for (int i = 0; i < weights.length; ++i) {
                    newToOld[i] = i;
                }
                new InPlaceMergeSorter() {
                    @Override
                    protected void swap(final int i, final int j) {
                        final int tmp = newToOld[i];
                        newToOld[i] = newToOld[j];
                        newToOld[j] = tmp;
                    }
                    
                    @Override
                    protected int compare(final int i, final int j) {
                        return Long.compare(weights[newToOld[j]], weights[newToOld[i]]);
                    }
                }.sort(0, weights.length);
                return newToOld;
            }
            
            private static int[] inverse(final int[] map) {
                final int[] inverse = new int[map.length];
                for (int i = 0; i < map.length; ++i) {
                    inverse[map[i]] = i;
                }
                return inverse;
            }
            
            SegmentMap(final long[] weights) {
                this.newToOld = map(weights);
                this.oldToNew = inverse(this.newToOld);
                assert Arrays.equals(this.newToOld, inverse(this.oldToNew));
            }
            
            int newToOld(final int segment) {
                return this.newToOld[segment];
            }
            
            int oldToNew(final int segment) {
                return this.oldToNew[segment];
            }
            
            @Override
            public long ramBytesUsed() {
                return SegmentMap.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(this.newToOld) + RamUsageEstimator.sizeOf(this.oldToNew);
            }
            
            @Override
            public Collection<Accountable> getChildResources() {
                return (Collection<Accountable>)Collections.emptyList();
            }
            
            static {
                BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(SegmentMap.class);
            }
        }
    }
    
    public static class MultiSortedDocValues extends SortedDocValues
    {
        public final int[] docStarts;
        public final SortedDocValues[] values;
        public final OrdinalMap mapping;
        
        MultiSortedDocValues(final SortedDocValues[] values, final int[] docStarts, final OrdinalMap mapping) throws IOException {
            assert docStarts.length == values.length + 1;
            this.values = values;
            this.docStarts = docStarts;
            this.mapping = mapping;
        }
        
        @Override
        public int getOrd(final int docID) {
            final int subIndex = ReaderUtil.subIndex(docID, this.docStarts);
            final int segmentOrd = this.values[subIndex].getOrd(docID - this.docStarts[subIndex]);
            return (segmentOrd == -1) ? segmentOrd : ((int)this.mapping.getGlobalOrds(subIndex).get(segmentOrd));
        }
        
        @Override
        public BytesRef lookupOrd(final int ord) {
            final int subIndex = this.mapping.getFirstSegmentNumber(ord);
            final int segmentOrd = (int)this.mapping.getFirstSegmentOrd(ord);
            return this.values[subIndex].lookupOrd(segmentOrd);
        }
        
        @Override
        public int getValueCount() {
            return (int)this.mapping.getValueCount();
        }
    }
    
    public static class MultiSortedSetDocValues extends SortedSetDocValues
    {
        public final int[] docStarts;
        public final SortedSetDocValues[] values;
        public final OrdinalMap mapping;
        int currentSubIndex;
        LongValues currentGlobalOrds;
        
        MultiSortedSetDocValues(final SortedSetDocValues[] values, final int[] docStarts, final OrdinalMap mapping) throws IOException {
            assert docStarts.length == values.length + 1;
            this.values = values;
            this.docStarts = docStarts;
            this.mapping = mapping;
        }
        
        @Override
        public long nextOrd() {
            final long segmentOrd = this.values[this.currentSubIndex].nextOrd();
            if (segmentOrd == -1L) {
                return segmentOrd;
            }
            return this.currentGlobalOrds.get(segmentOrd);
        }
        
        @Override
        public void setDocument(final int docID) {
            this.currentSubIndex = ReaderUtil.subIndex(docID, this.docStarts);
            this.currentGlobalOrds = this.mapping.getGlobalOrds(this.currentSubIndex);
            this.values[this.currentSubIndex].setDocument(docID - this.docStarts[this.currentSubIndex]);
        }
        
        @Override
        public BytesRef lookupOrd(final long ord) {
            final int subIndex = this.mapping.getFirstSegmentNumber(ord);
            final long segmentOrd = this.mapping.getFirstSegmentOrd(ord);
            return this.values[subIndex].lookupOrd(segmentOrd);
        }
        
        @Override
        public long getValueCount() {
            return this.mapping.getValueCount();
        }
    }
}

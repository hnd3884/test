package org.apache.lucene.index;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.util.ArrayUtil;
import java.util.Arrays;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.PackedLongValues;
import org.apache.lucene.util.BytesRefHash;

class SortedSetDocValuesWriter extends DocValuesWriter
{
    final BytesRefHash hash;
    private PackedLongValues.Builder pending;
    private PackedLongValues.Builder pendingCounts;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private final FieldInfo fieldInfo;
    private int currentDoc;
    private int[] currentValues;
    private int currentUpto;
    private int maxCount;
    
    public SortedSetDocValuesWriter(final FieldInfo fieldInfo, final Counter iwBytesUsed) {
        this.currentValues = new int[8];
        this.currentUpto = 0;
        this.maxCount = 0;
        this.fieldInfo = fieldInfo;
        this.iwBytesUsed = iwBytesUsed;
        this.hash = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectTrackingAllocator(iwBytesUsed)), 16, new BytesRefHash.DirectBytesStartArray(16, iwBytesUsed));
        this.pending = PackedLongValues.packedBuilder(0.0f);
        this.pendingCounts = PackedLongValues.deltaPackedBuilder(0.0f);
        iwBytesUsed.addAndGet(this.bytesUsed = this.pending.ramBytesUsed() + this.pendingCounts.ramBytesUsed());
    }
    
    public void addValue(final int docID, final BytesRef value) {
        if (value == null) {
            throw new IllegalArgumentException("field \"" + this.fieldInfo.name + "\": null value not allowed");
        }
        if (value.length > 32766) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" is too large, must be <= " + 32766);
        }
        if (docID != this.currentDoc) {
            this.finishCurrentDoc();
        }
        while (this.currentDoc < docID) {
            this.pendingCounts.add(0L);
            ++this.currentDoc;
        }
        this.addOneValue(value);
        this.updateBytesUsed();
    }
    
    private void finishCurrentDoc() {
        Arrays.sort(this.currentValues, 0, this.currentUpto);
        int lastValue = -1;
        int count = 0;
        for (int i = 0; i < this.currentUpto; ++i) {
            final int termID = this.currentValues[i];
            if (termID != lastValue) {
                this.pending.add(termID);
                ++count;
            }
            lastValue = termID;
        }
        this.pendingCounts.add(count);
        this.maxCount = Math.max(this.maxCount, count);
        this.currentUpto = 0;
        ++this.currentDoc;
    }
    
    public void finish(final int maxDoc) {
        this.finishCurrentDoc();
        for (int i = this.currentDoc; i < maxDoc; ++i) {
            this.pendingCounts.add(0L);
        }
    }
    
    private void addOneValue(final BytesRef value) {
        int termID = this.hash.add(value);
        if (termID < 0) {
            termID = -termID - 1;
        }
        else {
            this.iwBytesUsed.addAndGet(8L);
        }
        if (this.currentUpto == this.currentValues.length) {
            this.currentValues = ArrayUtil.grow(this.currentValues, this.currentValues.length + 1);
            this.iwBytesUsed.addAndGet((this.currentValues.length - this.currentUpto) * 2 * 4);
        }
        this.currentValues[this.currentUpto] = termID;
        ++this.currentUpto;
    }
    
    private void updateBytesUsed() {
        final long newBytesUsed = this.pending.ramBytesUsed() + this.pendingCounts.ramBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }
    
    public void flush(final SegmentWriteState state, final DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        final int maxCountPerDoc = this.maxCount;
        assert this.pendingCounts.size() == maxDoc;
        final int valueCount = this.hash.size();
        final PackedLongValues ords = this.pending.build();
        final PackedLongValues ordCounts = this.pendingCounts.build();
        final int[] sortedValues = this.hash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
        final int[] ordMap = new int[valueCount];
        for (int ord = 0; ord < valueCount; ++ord) {
            ordMap[sortedValues[ord]] = ord;
        }
        dvConsumer.addSortedSetField(this.fieldInfo, new Iterable<BytesRef>() {
            @Override
            public Iterator<BytesRef> iterator() {
                return new ValuesIterator(sortedValues, valueCount, SortedSetDocValuesWriter.this.hash);
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new OrdCountIterator(maxDoc, ordCounts);
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new OrdsIterator(ordMap, maxCountPerDoc, ords, ordCounts);
            }
        });
    }
    
    private static class ValuesIterator implements Iterator<BytesRef>
    {
        final int[] sortedValues;
        final BytesRefHash hash;
        final BytesRef scratch;
        final int valueCount;
        int ordUpto;
        
        ValuesIterator(final int[] sortedValues, final int valueCount, final BytesRefHash hash) {
            this.scratch = new BytesRef();
            this.sortedValues = sortedValues;
            this.valueCount = valueCount;
            this.hash = hash;
        }
        
        @Override
        public boolean hasNext() {
            return this.ordUpto < this.valueCount;
        }
        
        @Override
        public BytesRef next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.hash.get(this.sortedValues[this.ordUpto], this.scratch);
            ++this.ordUpto;
            return this.scratch;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class OrdsIterator implements Iterator<Number>
    {
        final PackedLongValues.Iterator iter;
        final PackedLongValues.Iterator counts;
        final int[] ordMap;
        final long numOrds;
        long ordUpto;
        final int[] currentDoc;
        int currentUpto;
        int currentLength;
        
        OrdsIterator(final int[] ordMap, final int maxCount, final PackedLongValues ords, final PackedLongValues ordCounts) {
            this.currentDoc = new int[maxCount];
            this.ordMap = ordMap;
            this.numOrds = ords.size();
            this.iter = ords.iterator();
            this.counts = ordCounts.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.ordUpto < this.numOrds;
        }
        
        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            while (this.currentUpto == this.currentLength) {
                this.currentUpto = 0;
                this.currentLength = (int)this.counts.next();
                for (int i = 0; i < this.currentLength; ++i) {
                    this.currentDoc[i] = this.ordMap[(int)this.iter.next()];
                }
                Arrays.sort(this.currentDoc, 0, this.currentLength);
            }
            final int ord = this.currentDoc[this.currentUpto];
            ++this.currentUpto;
            ++this.ordUpto;
            return ord;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class OrdCountIterator implements Iterator<Number>
    {
        final PackedLongValues.Iterator iter;
        final int maxDoc;
        int docUpto;
        
        OrdCountIterator(final int maxDoc, final PackedLongValues ordCounts) {
            this.maxDoc = maxDoc;
            assert ordCounts.size() == maxDoc;
            this.iter = ordCounts.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.docUpto < this.maxDoc;
        }
        
        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            ++this.docUpto;
            return this.iter.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

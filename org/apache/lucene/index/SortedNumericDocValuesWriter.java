package org.apache.lucene.index;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.ArrayUtil;
import java.util.Arrays;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.PackedLongValues;

class SortedNumericDocValuesWriter extends DocValuesWriter
{
    private PackedLongValues.Builder pending;
    private PackedLongValues.Builder pendingCounts;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private final FieldInfo fieldInfo;
    private int currentDoc;
    private long[] currentValues;
    private int currentUpto;
    
    public SortedNumericDocValuesWriter(final FieldInfo fieldInfo, final Counter iwBytesUsed) {
        this.currentValues = new long[8];
        this.currentUpto = 0;
        this.fieldInfo = fieldInfo;
        this.iwBytesUsed = iwBytesUsed;
        this.pending = PackedLongValues.deltaPackedBuilder(0.0f);
        this.pendingCounts = PackedLongValues.deltaPackedBuilder(0.0f);
        iwBytesUsed.addAndGet(this.bytesUsed = this.pending.ramBytesUsed() + this.pendingCounts.ramBytesUsed());
    }
    
    public void addValue(final int docID, final long value) {
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
        for (int i = 0; i < this.currentUpto; ++i) {
            this.pending.add(this.currentValues[i]);
        }
        this.pendingCounts.add(this.currentUpto);
        this.currentUpto = 0;
        ++this.currentDoc;
    }
    
    public void finish(final int maxDoc) {
        this.finishCurrentDoc();
        for (int i = this.currentDoc; i < maxDoc; ++i) {
            this.pendingCounts.add(0L);
        }
    }
    
    private void addOneValue(final long value) {
        if (this.currentUpto == this.currentValues.length) {
            this.currentValues = ArrayUtil.grow(this.currentValues, this.currentValues.length + 1);
        }
        this.currentValues[this.currentUpto] = value;
        ++this.currentUpto;
    }
    
    private void updateBytesUsed() {
        final long newBytesUsed = this.pending.ramBytesUsed() + this.pendingCounts.ramBytesUsed() + RamUsageEstimator.sizeOf(this.currentValues);
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }
    
    public void flush(final SegmentWriteState state, final DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        assert this.pendingCounts.size() == maxDoc;
        final PackedLongValues values = this.pending.build();
        final PackedLongValues valueCounts = this.pendingCounts.build();
        dvConsumer.addSortedNumericField(this.fieldInfo, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new CountIterator(valueCounts);
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new ValuesIterator(values);
            }
        });
    }
    
    private static class ValuesIterator implements Iterator<Number>
    {
        final PackedLongValues.Iterator iter;
        
        ValuesIterator(final PackedLongValues values) {
            this.iter = values.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.iter.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class CountIterator implements Iterator<Number>
    {
        final PackedLongValues.Iterator iter;
        
        CountIterator(final PackedLongValues valueCounts) {
            this.iter = valueCounts.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.iter.next();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

package org.apache.lucene.index;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.PackedLongValues;
import org.apache.lucene.util.BytesRefHash;

class SortedDocValuesWriter extends DocValuesWriter
{
    final BytesRefHash hash;
    private PackedLongValues.Builder pending;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private final FieldInfo fieldInfo;
    private static final int EMPTY_ORD = -1;
    
    public SortedDocValuesWriter(final FieldInfo fieldInfo, final Counter iwBytesUsed) {
        this.fieldInfo = fieldInfo;
        this.iwBytesUsed = iwBytesUsed;
        this.hash = new BytesRefHash(new ByteBlockPool(new ByteBlockPool.DirectTrackingAllocator(iwBytesUsed)), 16, new BytesRefHash.DirectBytesStartArray(16, iwBytesUsed));
        this.pending = PackedLongValues.deltaPackedBuilder(0.0f);
        iwBytesUsed.addAndGet(this.bytesUsed = this.pending.ramBytesUsed());
    }
    
    public void addValue(final int docID, final BytesRef value) {
        if (docID < this.pending.size()) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
        }
        if (value == null) {
            throw new IllegalArgumentException("field \"" + this.fieldInfo.name + "\": null value not allowed");
        }
        if (value.length > 32766) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" is too large, must be <= " + 32766);
        }
        while (this.pending.size() < docID) {
            this.pending.add(-1L);
        }
        this.addOneValue(value);
    }
    
    public void finish(final int maxDoc) {
        while (this.pending.size() < maxDoc) {
            this.pending.add(-1L);
        }
        this.updateBytesUsed();
    }
    
    private void addOneValue(final BytesRef value) {
        int termID = this.hash.add(value);
        if (termID < 0) {
            termID = -termID - 1;
        }
        else {
            this.iwBytesUsed.addAndGet(8L);
        }
        this.pending.add(termID);
        this.updateBytesUsed();
    }
    
    private void updateBytesUsed() {
        final long newBytesUsed = this.pending.ramBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }
    
    public void flush(final SegmentWriteState state, final DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        assert this.pending.size() == maxDoc;
        final int valueCount = this.hash.size();
        final PackedLongValues ords = this.pending.build();
        final int[] sortedValues = this.hash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
        final int[] ordMap = new int[valueCount];
        for (int ord = 0; ord < valueCount; ++ord) {
            ordMap[sortedValues[ord]] = ord;
        }
        dvConsumer.addSortedField(this.fieldInfo, new Iterable<BytesRef>() {
            @Override
            public Iterator<BytesRef> iterator() {
                return new ValuesIterator(sortedValues, valueCount, SortedDocValuesWriter.this.hash);
            }
        }, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new OrdsIterator(ordMap, maxDoc, ords);
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
        final int[] ordMap;
        final int maxDoc;
        int docUpto;
        
        OrdsIterator(final int[] ordMap, final int maxDoc, final PackedLongValues ords) {
            this.ordMap = ordMap;
            this.maxDoc = maxDoc;
            assert ords.size() == maxDoc;
            this.iter = ords.iterator();
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
            final int ord = (int)this.iter.next();
            ++this.docUpto;
            return (ord == -1) ? ord : this.ordMap[ord];
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

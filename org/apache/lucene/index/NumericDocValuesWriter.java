package org.apache.lucene.index;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.PackedLongValues;

class NumericDocValuesWriter extends DocValuesWriter
{
    private static final long MISSING = 0L;
    private PackedLongValues.Builder pending;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private FixedBitSet docsWithField;
    private final FieldInfo fieldInfo;
    
    public NumericDocValuesWriter(final FieldInfo fieldInfo, final Counter iwBytesUsed) {
        this.pending = PackedLongValues.deltaPackedBuilder(0.0f);
        this.docsWithField = new FixedBitSet(64);
        this.bytesUsed = this.pending.ramBytesUsed() + this.docsWithFieldBytesUsed();
        this.fieldInfo = fieldInfo;
        (this.iwBytesUsed = iwBytesUsed).addAndGet(this.bytesUsed);
    }
    
    public void addValue(final int docID, final long value) {
        if (docID < this.pending.size()) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
        }
        for (int i = (int)this.pending.size(); i < docID; ++i) {
            this.pending.add(0L);
        }
        this.pending.add(value);
        (this.docsWithField = FixedBitSet.ensureCapacity(this.docsWithField, docID)).set(docID);
        this.updateBytesUsed();
    }
    
    private long docsWithFieldBytesUsed() {
        return RamUsageEstimator.sizeOf(this.docsWithField.getBits()) + 64L;
    }
    
    private void updateBytesUsed() {
        final long newBytesUsed = this.pending.ramBytesUsed() + this.docsWithFieldBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }
    
    public void finish(final int maxDoc) {
    }
    
    public void flush(final SegmentWriteState state, final DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        final PackedLongValues values = this.pending.build();
        dvConsumer.addNumericField(this.fieldInfo, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new NumericIterator(maxDoc, values, NumericDocValuesWriter.this.docsWithField);
            }
        });
    }
    
    private static class NumericIterator implements Iterator<Number>
    {
        final PackedLongValues.Iterator iter;
        final FixedBitSet docsWithField;
        final int size;
        final int maxDoc;
        int upto;
        
        NumericIterator(final int maxDoc, final PackedLongValues values, final FixedBitSet docsWithFields) {
            this.maxDoc = maxDoc;
            this.iter = values.iterator();
            this.size = (int)values.size();
            this.docsWithField = docsWithFields;
        }
        
        @Override
        public boolean hasNext() {
            return this.upto < this.maxDoc;
        }
        
        @Override
        public Number next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            Long value;
            if (this.upto < this.size) {
                final long v = this.iter.next();
                if (this.docsWithField.get(this.upto)) {
                    value = v;
                }
                else {
                    value = null;
                }
            }
            else {
                value = null;
            }
            ++this.upto;
            return value;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

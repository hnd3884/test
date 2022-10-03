package org.apache.lucene.index;

import java.util.NoSuchElementException;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.codecs.NormsConsumer;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.packed.PackedLongValues;

class NormValuesWriter
{
    private static final long MISSING = 0L;
    private PackedLongValues.Builder pending;
    private final Counter iwBytesUsed;
    private long bytesUsed;
    private final FieldInfo fieldInfo;
    
    public NormValuesWriter(final FieldInfo fieldInfo, final Counter iwBytesUsed) {
        this.pending = PackedLongValues.deltaPackedBuilder(0.0f);
        this.bytesUsed = this.pending.ramBytesUsed();
        this.fieldInfo = fieldInfo;
        (this.iwBytesUsed = iwBytesUsed).addAndGet(this.bytesUsed);
    }
    
    public void addValue(final int docID, final long value) {
        for (int i = (int)this.pending.size(); i < docID; ++i) {
            this.pending.add(0L);
        }
        this.pending.add(value);
        this.updateBytesUsed();
    }
    
    private void updateBytesUsed() {
        final long newBytesUsed = this.pending.ramBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }
    
    public void finish(final int maxDoc) {
    }
    
    public void flush(final SegmentWriteState state, final NormsConsumer normsConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        final PackedLongValues values = this.pending.build();
        normsConsumer.addNormsField(this.fieldInfo, new Iterable<Number>() {
            @Override
            public Iterator<Number> iterator() {
                return new NumericIterator(maxDoc, values);
            }
        });
    }
    
    private static class NumericIterator implements Iterator<Number>
    {
        final PackedLongValues.Iterator iter;
        final int size;
        final int maxDoc;
        int upto;
        
        NumericIterator(final int maxDoc, final PackedLongValues values) {
            this.maxDoc = maxDoc;
            this.iter = values.iterator();
            this.size = (int)values.size();
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
                value = this.iter.next();
            }
            else {
                value = 0L;
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

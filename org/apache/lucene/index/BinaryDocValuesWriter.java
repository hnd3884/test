package org.apache.lucene.index;

import java.util.NoSuchElementException;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.ArrayUtil;
import java.util.Iterator;
import org.apache.lucene.codecs.DocValuesConsumer;
import org.apache.lucene.util.RamUsageEstimator;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.util.packed.PackedLongValues;
import org.apache.lucene.util.Counter;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.PagedBytes;

class BinaryDocValuesWriter extends DocValuesWriter
{
    private static final int MAX_LENGTH;
    private static final int BLOCK_BITS = 15;
    private final PagedBytes bytes;
    private final DataOutput bytesOut;
    private final Counter iwBytesUsed;
    private final PackedLongValues.Builder lengths;
    private FixedBitSet docsWithField;
    private final FieldInfo fieldInfo;
    private int addedValues;
    private long bytesUsed;
    
    public BinaryDocValuesWriter(final FieldInfo fieldInfo, final Counter iwBytesUsed) {
        this.fieldInfo = fieldInfo;
        this.bytes = new PagedBytes(15);
        this.bytesOut = this.bytes.getDataOutput();
        this.lengths = PackedLongValues.deltaPackedBuilder(0.0f);
        this.iwBytesUsed = iwBytesUsed;
        this.docsWithField = new FixedBitSet(64);
        iwBytesUsed.addAndGet(this.bytesUsed = this.docsWithFieldBytesUsed());
    }
    
    public void addValue(final int docID, final BytesRef value) {
        if (docID < this.addedValues) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" appears more than once in this document (only one value is allowed per field)");
        }
        if (value == null) {
            throw new IllegalArgumentException("field=\"" + this.fieldInfo.name + "\": null value not allowed");
        }
        if (value.length > BinaryDocValuesWriter.MAX_LENGTH) {
            throw new IllegalArgumentException("DocValuesField \"" + this.fieldInfo.name + "\" is too large, must be <= " + BinaryDocValuesWriter.MAX_LENGTH);
        }
        while (this.addedValues < docID) {
            ++this.addedValues;
            this.lengths.add(0L);
        }
        ++this.addedValues;
        this.lengths.add(value.length);
        try {
            this.bytesOut.writeBytes(value.bytes, value.offset, value.length);
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        (this.docsWithField = FixedBitSet.ensureCapacity(this.docsWithField, docID)).set(docID);
        this.updateBytesUsed();
    }
    
    private long docsWithFieldBytesUsed() {
        return RamUsageEstimator.sizeOf(this.docsWithField.getBits()) + 64L;
    }
    
    private void updateBytesUsed() {
        final long newBytesUsed = this.lengths.ramBytesUsed() + this.bytes.ramBytesUsed() + this.docsWithFieldBytesUsed();
        this.iwBytesUsed.addAndGet(newBytesUsed - this.bytesUsed);
        this.bytesUsed = newBytesUsed;
    }
    
    public void finish(final int maxDoc) {
    }
    
    public void flush(final SegmentWriteState state, final DocValuesConsumer dvConsumer) throws IOException {
        final int maxDoc = state.segmentInfo.maxDoc();
        this.bytes.freeze(false);
        final PackedLongValues lengths = this.lengths.build();
        dvConsumer.addBinaryField(this.fieldInfo, new Iterable<BytesRef>() {
            @Override
            public Iterator<BytesRef> iterator() {
                return new BytesIterator(maxDoc, lengths);
            }
        });
    }
    
    static {
        MAX_LENGTH = ArrayUtil.MAX_ARRAY_LENGTH;
    }
    
    private class BytesIterator implements Iterator<BytesRef>
    {
        final BytesRefBuilder value;
        final PackedLongValues.Iterator lengthsIterator;
        final DataInput bytesIterator;
        final int size;
        final int maxDoc;
        int upto;
        
        BytesIterator(final int maxDoc, final PackedLongValues lengths) {
            this.value = new BytesRefBuilder();
            this.bytesIterator = BinaryDocValuesWriter.this.bytes.getDataInput();
            this.size = (int)BinaryDocValuesWriter.this.lengths.size();
            this.maxDoc = maxDoc;
            this.lengthsIterator = lengths.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return this.upto < this.maxDoc;
        }
        
        @Override
        public BytesRef next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            BytesRef v;
            if (this.upto < this.size) {
                final int length = (int)this.lengthsIterator.next();
                this.value.grow(length);
                this.value.setLength(length);
                try {
                    this.bytesIterator.readBytes(this.value.bytes(), 0, this.value.length());
                }
                catch (final IOException ioe) {
                    throw new RuntimeException(ioe);
                }
                if (BinaryDocValuesWriter.this.docsWithField.get(this.upto)) {
                    v = this.value.get();
                }
                else {
                    v = null;
                }
            }
            else {
                v = null;
            }
            ++this.upto;
            return v;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

package org.apache.lucene.index;

import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.packed.PagedGrowableWriter;
import org.apache.lucene.util.packed.PagedMutable;

class BinaryDocValuesFieldUpdates extends DocValuesFieldUpdates
{
    private PagedMutable docs;
    private PagedGrowableWriter offsets;
    private PagedGrowableWriter lengths;
    private BytesRefBuilder values;
    private int size;
    private final int bitsPerValue;
    
    public BinaryDocValuesFieldUpdates(final String field, final int maxDoc) {
        super(field, DocValuesType.BINARY);
        this.bitsPerValue = PackedInts.bitsRequired(maxDoc - 1);
        this.docs = new PagedMutable(1L, 1024, this.bitsPerValue, 0.0f);
        this.offsets = new PagedGrowableWriter(1L, 1024, 1, 0.5f);
        this.lengths = new PagedGrowableWriter(1L, 1024, 1, 0.5f);
        this.values = new BytesRefBuilder();
        this.size = 0;
    }
    
    @Override
    public void add(final int doc, final Object value) {
        if (this.size == Integer.MAX_VALUE) {
            throw new IllegalStateException("cannot support more than Integer.MAX_VALUE doc/value entries");
        }
        final BytesRef val = (BytesRef)value;
        if (this.docs.size() == this.size) {
            this.docs = this.docs.grow(this.size + 1);
            this.offsets = this.offsets.grow(this.size + 1);
            this.lengths = this.lengths.grow(this.size + 1);
        }
        this.docs.set(this.size, doc);
        this.offsets.set(this.size, this.values.length());
        this.lengths.set(this.size, val.length);
        this.values.append(val);
        ++this.size;
    }
    
    @Override
    public Iterator iterator() {
        final PagedMutable docs = this.docs;
        final PagedGrowableWriter offsets = this.offsets;
        final PagedGrowableWriter lengths = this.lengths;
        final BytesRef values = this.values.get();
        new InPlaceMergeSorter() {
            @Override
            protected void swap(final int i, final int j) {
                final long tmpDoc = docs.get(j);
                docs.set(j, docs.get(i));
                docs.set(i, tmpDoc);
                final long tmpOffset = offsets.get(j);
                offsets.set(j, offsets.get(i));
                offsets.set(i, tmpOffset);
                final long tmpLength = lengths.get(j);
                lengths.set(j, lengths.get(i));
                lengths.set(i, tmpLength);
            }
            
            @Override
            protected int compare(final int i, final int j) {
                final int x = (int)docs.get(i);
                final int y = (int)docs.get(j);
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        }.sort(0, this.size);
        return new Iterator(this.size, offsets, lengths, docs, values);
    }
    
    @Override
    public void merge(final DocValuesFieldUpdates other) {
        final BinaryDocValuesFieldUpdates otherUpdates = (BinaryDocValuesFieldUpdates)other;
        if (otherUpdates.size > Integer.MAX_VALUE - this.size) {
            throw new IllegalStateException("cannot support more than Integer.MAX_VALUE doc/value entries; size=" + this.size + " other.size=" + otherUpdates.size);
        }
        final int newSize = this.size + otherUpdates.size;
        this.docs = this.docs.grow(newSize);
        this.offsets = this.offsets.grow(newSize);
        this.lengths = this.lengths.grow(newSize);
        for (int i = 0; i < otherUpdates.size; ++i) {
            final int doc = (int)otherUpdates.docs.get(i);
            this.docs.set(this.size, doc);
            this.offsets.set(this.size, this.values.length() + otherUpdates.offsets.get(i));
            this.lengths.set(this.size, otherUpdates.lengths.get(i));
            ++this.size;
        }
        this.values.append(otherUpdates.values);
    }
    
    @Override
    public boolean any() {
        return this.size > 0;
    }
    
    @Override
    public long ramBytesPerDoc() {
        long bytesPerDoc = (long)Math.ceil(this.bitsPerValue / 8.0);
        final int capacity = DocValuesFieldUpdates.estimateCapacity(this.size);
        bytesPerDoc += (long)Math.ceil(this.offsets.ramBytesUsed() / (double)capacity);
        bytesPerDoc += (long)Math.ceil(this.lengths.ramBytesUsed() / (double)capacity);
        bytesPerDoc += (long)Math.ceil(this.values.length() / (double)this.size);
        return bytesPerDoc;
    }
    
    static final class Iterator extends DocValuesFieldUpdates.Iterator
    {
        private final PagedGrowableWriter offsets;
        private final int size;
        private final PagedGrowableWriter lengths;
        private final PagedMutable docs;
        private long idx;
        private int doc;
        private final BytesRef value;
        private int offset;
        private int length;
        
        Iterator(final int size, final PagedGrowableWriter offsets, final PagedGrowableWriter lengths, final PagedMutable docs, final BytesRef values) {
            this.idx = 0L;
            this.doc = -1;
            this.offsets = offsets;
            this.size = size;
            this.lengths = lengths;
            this.docs = docs;
            this.value = values.clone();
        }
        
        @Override
        BytesRef value() {
            this.value.offset = this.offset;
            this.value.length = this.length;
            return this.value;
        }
        
        @Override
        int nextDoc() {
            if (this.idx >= this.size) {
                this.offset = -1;
                return this.doc = Integer.MAX_VALUE;
            }
            this.doc = (int)this.docs.get(this.idx);
            ++this.idx;
            while (this.idx < this.size && this.docs.get(this.idx) == this.doc) {
                ++this.idx;
            }
            final long prevIdx = this.idx - 1L;
            this.offset = (int)this.offsets.get(prevIdx);
            this.length = (int)this.lengths.get(prevIdx);
            return this.doc;
        }
        
        @Override
        int doc() {
            return this.doc;
        }
        
        @Override
        void reset() {
            this.doc = -1;
            this.offset = -1;
            this.idx = 0L;
        }
    }
}

package org.apache.lucene.index;

import org.apache.lucene.util.InPlaceMergeSorter;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.packed.PagedGrowableWriter;
import org.apache.lucene.util.packed.PagedMutable;

class NumericDocValuesFieldUpdates extends DocValuesFieldUpdates
{
    private final int bitsPerValue;
    private PagedMutable docs;
    private PagedGrowableWriter values;
    private int size;
    
    public NumericDocValuesFieldUpdates(final String field, final int maxDoc) {
        super(field, DocValuesType.NUMERIC);
        this.bitsPerValue = PackedInts.bitsRequired(maxDoc - 1);
        this.docs = new PagedMutable(1L, 1024, this.bitsPerValue, 0.0f);
        this.values = new PagedGrowableWriter(1L, 1024, 1, 0.5f);
        this.size = 0;
    }
    
    @Override
    public void add(final int doc, final Object value) {
        if (this.size == Integer.MAX_VALUE) {
            throw new IllegalStateException("cannot support more than Integer.MAX_VALUE doc/value entries");
        }
        final Long val = (Long)value;
        if (this.docs.size() == this.size) {
            this.docs = this.docs.grow(this.size + 1);
            this.values = this.values.grow(this.size + 1);
        }
        this.docs.set(this.size, doc);
        this.values.set(this.size, val);
        ++this.size;
    }
    
    @Override
    public Iterator iterator() {
        final PagedMutable docs = this.docs;
        final PagedGrowableWriter values = this.values;
        new InPlaceMergeSorter() {
            @Override
            protected void swap(final int i, final int j) {
                final long tmpDoc = docs.get(j);
                docs.set(j, docs.get(i));
                docs.set(i, tmpDoc);
                final long tmpVal = values.get(j);
                values.set(j, values.get(i));
                values.set(i, tmpVal);
            }
            
            @Override
            protected int compare(final int i, final int j) {
                final int x = (int)docs.get(i);
                final int y = (int)docs.get(j);
                return (x < y) ? -1 : ((x == y) ? 0 : 1);
            }
        }.sort(0, this.size);
        return new Iterator(this.size, values, docs);
    }
    
    @Override
    public void merge(final DocValuesFieldUpdates other) {
        assert other instanceof NumericDocValuesFieldUpdates;
        final NumericDocValuesFieldUpdates otherUpdates = (NumericDocValuesFieldUpdates)other;
        if (otherUpdates.size > Integer.MAX_VALUE - this.size) {
            throw new IllegalStateException("cannot support more than Integer.MAX_VALUE doc/value entries; size=" + this.size + " other.size=" + otherUpdates.size);
        }
        this.docs = this.docs.grow(this.size + otherUpdates.size);
        this.values = this.values.grow(this.size + otherUpdates.size);
        for (int i = 0; i < otherUpdates.size; ++i) {
            final int doc = (int)otherUpdates.docs.get(i);
            this.docs.set(this.size, doc);
            this.values.set(this.size, otherUpdates.values.get(i));
            ++this.size;
        }
    }
    
    @Override
    public boolean any() {
        return this.size > 0;
    }
    
    @Override
    public long ramBytesPerDoc() {
        long bytesPerDoc = (long)Math.ceil(this.bitsPerValue / 8.0);
        final int capacity = DocValuesFieldUpdates.estimateCapacity(this.size);
        bytesPerDoc += (long)Math.ceil(this.values.ramBytesUsed() / (double)capacity);
        return bytesPerDoc;
    }
    
    static final class Iterator extends DocValuesFieldUpdates.Iterator
    {
        private final int size;
        private final PagedGrowableWriter values;
        private final PagedMutable docs;
        private long idx;
        private int doc;
        private Long value;
        
        Iterator(final int size, final PagedGrowableWriter values, final PagedMutable docs) {
            this.idx = 0L;
            this.doc = -1;
            this.value = null;
            this.size = size;
            this.values = values;
            this.docs = docs;
        }
        
        @Override
        Long value() {
            return this.value;
        }
        
        @Override
        int nextDoc() {
            if (this.idx >= this.size) {
                this.value = null;
                return this.doc = Integer.MAX_VALUE;
            }
            this.doc = (int)this.docs.get(this.idx);
            ++this.idx;
            while (this.idx < this.size && this.docs.get(this.idx) == this.doc) {
                ++this.idx;
            }
            this.value = this.values.get(this.idx - 1L);
            return this.doc;
        }
        
        @Override
        int doc() {
            return this.doc;
        }
        
        @Override
        void reset() {
            this.doc = -1;
            this.value = null;
            this.idx = 0L;
        }
    }
}

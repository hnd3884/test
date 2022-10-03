package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.RamUsageEstimator;

abstract class DocValuesUpdate
{
    private static final int RAW_SIZE_IN_BYTES;
    final DocValuesType type;
    final Term term;
    final String field;
    final Object value;
    int docIDUpto;
    
    protected DocValuesUpdate(final DocValuesType type, final Term term, final String field, final Object value) {
        this.docIDUpto = -1;
        this.type = type;
        this.term = term;
        this.field = field;
        this.value = value;
    }
    
    abstract long valueSizeInBytes();
    
    final int sizeInBytes() {
        int sizeInBytes = DocValuesUpdate.RAW_SIZE_IN_BYTES;
        sizeInBytes += this.term.field.length() * 2;
        sizeInBytes += this.term.bytes.bytes.length;
        sizeInBytes += this.field.length() * 2;
        sizeInBytes += (int)this.valueSizeInBytes();
        return sizeInBytes;
    }
    
    @Override
    public String toString() {
        return "term=" + this.term + ",field=" + this.field + ",value=" + this.value + ",docIDUpto=" + this.docIDUpto;
    }
    
    static {
        RAW_SIZE_IN_BYTES = 8 * RamUsageEstimator.NUM_BYTES_OBJECT_HEADER + 8 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 32;
    }
    
    static final class BinaryDocValuesUpdate extends DocValuesUpdate
    {
        private static final long RAW_VALUE_SIZE_IN_BYTES;
        
        BinaryDocValuesUpdate(final Term term, final String field, final BytesRef value) {
            super(DocValuesType.BINARY, term, field, value);
        }
        
        @Override
        long valueSizeInBytes() {
            return BinaryDocValuesUpdate.RAW_VALUE_SIZE_IN_BYTES + ((BytesRef)this.value).bytes.length;
        }
        
        static {
            RAW_VALUE_SIZE_IN_BYTES = RamUsageEstimator.NUM_BYTES_ARRAY_HEADER + 8 + RamUsageEstimator.NUM_BYTES_OBJECT_REF;
        }
    }
    
    static final class NumericDocValuesUpdate extends DocValuesUpdate
    {
        NumericDocValuesUpdate(final Term term, final String field, final Long value) {
            super(DocValuesType.NUMERIC, term, field, value);
        }
        
        @Override
        long valueSizeInBytes() {
            return 8L;
        }
    }
}

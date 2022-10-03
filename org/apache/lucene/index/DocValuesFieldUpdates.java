package org.apache.lucene.index;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

abstract class DocValuesFieldUpdates
{
    protected static final int PAGE_SIZE = 1024;
    final String field;
    final DocValuesType type;
    
    protected DocValuesFieldUpdates(final String field, final DocValuesType type) {
        this.field = field;
        if (type == null) {
            throw new NullPointerException("DocValuesType cannot be null");
        }
        this.type = type;
    }
    
    protected static int estimateCapacity(final int size) {
        return (int)Math.ceil(size / 1024.0) * 1024;
    }
    
    public abstract void add(final int p0, final Object p1);
    
    public abstract Iterator iterator();
    
    public abstract void merge(final DocValuesFieldUpdates p0);
    
    public abstract boolean any();
    
    public abstract long ramBytesPerDoc();
    
    abstract static class Iterator
    {
        abstract int nextDoc();
        
        abstract int doc();
        
        abstract Object value();
        
        abstract void reset();
    }
    
    static class Container
    {
        final Map<String, NumericDocValuesFieldUpdates> numericDVUpdates;
        final Map<String, BinaryDocValuesFieldUpdates> binaryDVUpdates;
        
        Container() {
            this.numericDVUpdates = new HashMap<String, NumericDocValuesFieldUpdates>();
            this.binaryDVUpdates = new HashMap<String, BinaryDocValuesFieldUpdates>();
        }
        
        boolean any() {
            for (final NumericDocValuesFieldUpdates updates : this.numericDVUpdates.values()) {
                if (updates.any()) {
                    return true;
                }
            }
            for (final BinaryDocValuesFieldUpdates updates2 : this.binaryDVUpdates.values()) {
                if (updates2.any()) {
                    return true;
                }
            }
            return false;
        }
        
        int size() {
            return this.numericDVUpdates.size() + this.binaryDVUpdates.size();
        }
        
        long ramBytesPerDoc() {
            long ramBytesPerDoc = 0L;
            for (final NumericDocValuesFieldUpdates updates : this.numericDVUpdates.values()) {
                ramBytesPerDoc += updates.ramBytesPerDoc();
            }
            for (final BinaryDocValuesFieldUpdates updates2 : this.binaryDVUpdates.values()) {
                ramBytesPerDoc += updates2.ramBytesPerDoc();
            }
            return ramBytesPerDoc;
        }
        
        DocValuesFieldUpdates getUpdates(final String field, final DocValuesType type) {
            switch (type) {
                case NUMERIC: {
                    return this.numericDVUpdates.get(field);
                }
                case BINARY: {
                    return this.binaryDVUpdates.get(field);
                }
                default: {
                    throw new IllegalArgumentException("unsupported type: " + type);
                }
            }
        }
        
        DocValuesFieldUpdates newUpdates(final String field, final DocValuesType type, final int maxDoc) {
            switch (type) {
                case NUMERIC: {
                    assert this.numericDVUpdates.get(field) == null;
                    final NumericDocValuesFieldUpdates numericUpdates = new NumericDocValuesFieldUpdates(field, maxDoc);
                    this.numericDVUpdates.put(field, numericUpdates);
                    return numericUpdates;
                }
                case BINARY: {
                    assert this.binaryDVUpdates.get(field) == null;
                    final BinaryDocValuesFieldUpdates binaryUpdates = new BinaryDocValuesFieldUpdates(field, maxDoc);
                    this.binaryDVUpdates.put(field, binaryUpdates);
                    return binaryUpdates;
                }
                default: {
                    throw new IllegalArgumentException("unsupported type: " + type);
                }
            }
        }
        
        @Override
        public String toString() {
            return "numericDVUpdates=" + this.numericDVUpdates + " binaryDVUpdates=" + this.binaryDVUpdates;
        }
    }
}

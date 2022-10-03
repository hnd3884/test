package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.util.PriorityQueue;

public abstract class FieldValueHitQueue<T extends Entry> extends PriorityQueue<T>
{
    protected final SortField[] fields;
    protected final FieldComparator<?>[] comparators;
    protected final int[] reverseMul;
    
    private FieldValueHitQueue(final SortField[] fields, final int size) throws IOException {
        super(size);
        this.fields = fields;
        final int numComparators = fields.length;
        this.comparators = new FieldComparator[numComparators];
        this.reverseMul = new int[numComparators];
        for (int i = 0; i < numComparators; ++i) {
            final SortField field = fields[i];
            this.reverseMul[i] = (field.reverse ? -1 : 1);
            this.comparators[i] = field.getComparator(size, i);
        }
    }
    
    public static <T extends Entry> FieldValueHitQueue<T> create(final SortField[] fields, final int size) throws IOException {
        if (fields.length == 0) {
            throw new IllegalArgumentException("Sort must contain at least one field");
        }
        if (fields.length == 1) {
            return new OneComparatorFieldValueHitQueue<T>(fields, size);
        }
        return new MultiComparatorsFieldValueHitQueue<T>(fields, size);
    }
    
    public FieldComparator<?>[] getComparators() {
        return this.comparators;
    }
    
    public int[] getReverseMul() {
        return this.reverseMul;
    }
    
    public LeafFieldComparator[] getComparators(final LeafReaderContext context) throws IOException {
        final LeafFieldComparator[] comparators = new LeafFieldComparator[this.comparators.length];
        for (int i = 0; i < comparators.length; ++i) {
            comparators[i] = this.comparators[i].getLeafComparator(context);
        }
        return comparators;
    }
    
    @Override
    protected abstract boolean lessThan(final Entry p0, final Entry p1);
    
    FieldDoc fillFields(final Entry entry) {
        final int n = this.comparators.length;
        final Object[] fields = new Object[n];
        for (int i = 0; i < n; ++i) {
            fields[i] = this.comparators[i].value(entry.slot);
        }
        return new FieldDoc(entry.doc, entry.score, fields);
    }
    
    SortField[] getFields() {
        return this.fields;
    }
    
    public static class Entry extends ScoreDoc
    {
        public int slot;
        
        public Entry(final int slot, final int doc, final float score) {
            super(doc, score);
            this.slot = slot;
        }
        
        @Override
        public String toString() {
            return "slot:" + this.slot + " " + super.toString();
        }
    }
    
    private static final class OneComparatorFieldValueHitQueue<T extends Entry> extends FieldValueHitQueue<T>
    {
        private final int oneReverseMul;
        private final FieldComparator<?> oneComparator;
        
        public OneComparatorFieldValueHitQueue(final SortField[] fields, final int size) throws IOException {
            super(fields, size, null);
            assert fields.length == 1;
            this.oneComparator = this.comparators[0];
            this.oneReverseMul = this.reverseMul[0];
        }
        
        @Override
        protected boolean lessThan(final Entry hitA, final Entry hitB) {
            assert hitA != hitB;
            assert hitA.slot != hitB.slot;
            final int c = this.oneReverseMul * this.oneComparator.compare(hitA.slot, hitB.slot);
            if (c != 0) {
                return c > 0;
            }
            return hitA.doc > hitB.doc;
        }
    }
    
    private static final class MultiComparatorsFieldValueHitQueue<T extends Entry> extends FieldValueHitQueue<T>
    {
        public MultiComparatorsFieldValueHitQueue(final SortField[] fields, final int size) throws IOException {
            super(fields, size, null);
        }
        
        @Override
        protected boolean lessThan(final Entry hitA, final Entry hitB) {
            assert hitA != hitB;
            assert hitA.slot != hitB.slot;
            for (int numComparators = this.comparators.length, i = 0; i < numComparators; ++i) {
                final int c = this.reverseMul[i] * this.comparators[i].compare(hitA.slot, hitB.slot);
                if (c != 0) {
                    return c > 0;
                }
            }
            return hitA.doc > hitB.doc;
        }
    }
}

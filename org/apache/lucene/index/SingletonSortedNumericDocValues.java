package org.apache.lucene.index;

import org.apache.lucene.util.Bits;

final class SingletonSortedNumericDocValues extends SortedNumericDocValues
{
    private final NumericDocValues in;
    private final Bits docsWithField;
    private long value;
    private int count;
    
    public SingletonSortedNumericDocValues(final NumericDocValues in, final Bits docsWithField) {
        this.in = in;
        this.docsWithField = ((docsWithField instanceof Bits.MatchAllBits) ? null : docsWithField);
    }
    
    public NumericDocValues getNumericDocValues() {
        return this.in;
    }
    
    public Bits getDocsWithField() {
        return this.docsWithField;
    }
    
    @Override
    public void setDocument(final int doc) {
        this.value = this.in.get(doc);
        if (this.docsWithField != null && this.value == 0L && !this.docsWithField.get(doc)) {
            this.count = 0;
        }
        else {
            this.count = 1;
        }
    }
    
    @Override
    public long valueAt(final int index) {
        return this.value;
    }
    
    @Override
    public int count() {
        return this.count;
    }
}

package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;

final class SingletonSortedSetDocValues extends RandomAccessOrds
{
    private final SortedDocValues in;
    private long currentOrd;
    private long ord;
    
    public SingletonSortedSetDocValues(final SortedDocValues in) {
        this.in = in;
    }
    
    public SortedDocValues getSortedDocValues() {
        return this.in;
    }
    
    @Override
    public long nextOrd() {
        final long v = this.currentOrd;
        this.currentOrd = -1L;
        return v;
    }
    
    @Override
    public void setDocument(final int docID) {
        final long n = this.in.getOrd(docID);
        this.ord = n;
        this.currentOrd = n;
    }
    
    @Override
    public BytesRef lookupOrd(final long ord) {
        return this.in.lookupOrd((int)ord);
    }
    
    @Override
    public long getValueCount() {
        return this.in.getValueCount();
    }
    
    @Override
    public long lookupTerm(final BytesRef key) {
        return this.in.lookupTerm(key);
    }
    
    @Override
    public long ordAt(final int index) {
        return this.ord;
    }
    
    @Override
    public int cardinality() {
        return (int)(this.ord >>> 63) ^ 0x1;
    }
    
    @Override
    public TermsEnum termsEnum() {
        return this.in.termsEnum();
    }
}

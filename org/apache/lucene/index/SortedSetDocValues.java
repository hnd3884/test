package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;

public abstract class SortedSetDocValues
{
    public static final long NO_MORE_ORDS = -1L;
    
    protected SortedSetDocValues() {
    }
    
    public abstract long nextOrd();
    
    public abstract void setDocument(final int p0);
    
    public abstract BytesRef lookupOrd(final long p0);
    
    public abstract long getValueCount();
    
    public long lookupTerm(final BytesRef key) {
        long low = 0L;
        long high = this.getValueCount() - 1L;
        while (low <= high) {
            final long mid = low + high >>> 1;
            final BytesRef term = this.lookupOrd(mid);
            final int cmp = term.compareTo(key);
            if (cmp < 0) {
                low = mid + 1L;
            }
            else {
                if (cmp <= 0) {
                    return mid;
                }
                high = mid - 1L;
            }
        }
        return -(low + 1L);
    }
    
    public TermsEnum termsEnum() {
        return new SortedSetDocValuesTermsEnum(this);
    }
}

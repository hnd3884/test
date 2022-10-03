package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;

public abstract class SortedDocValues extends BinaryDocValues
{
    private final BytesRef empty;
    
    protected SortedDocValues() {
        this.empty = new BytesRef();
    }
    
    public abstract int getOrd(final int p0);
    
    public abstract BytesRef lookupOrd(final int p0);
    
    public abstract int getValueCount();
    
    @Override
    public BytesRef get(final int docID) {
        final int ord = this.getOrd(docID);
        if (ord == -1) {
            return this.empty;
        }
        return this.lookupOrd(ord);
    }
    
    public int lookupTerm(final BytesRef key) {
        int low = 0;
        int high = this.getValueCount() - 1;
        while (low <= high) {
            final int mid = low + high >>> 1;
            final BytesRef term = this.lookupOrd(mid);
            final int cmp = term.compareTo(key);
            if (cmp < 0) {
                low = mid + 1;
            }
            else {
                if (cmp <= 0) {
                    return mid;
                }
                high = mid - 1;
            }
        }
        return -(low + 1);
    }
    
    public TermsEnum termsEnum() {
        return new SortedDocValuesTermsEnum(this);
    }
}

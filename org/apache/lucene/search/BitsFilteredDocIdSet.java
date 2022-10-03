package org.apache.lucene.search;

import java.util.Objects;
import org.apache.lucene.util.Bits;

@Deprecated
public final class BitsFilteredDocIdSet extends FilteredDocIdSet
{
    private final Bits acceptDocs;
    
    public static DocIdSet wrap(final DocIdSet set, final Bits acceptDocs) {
        return (set == null || acceptDocs == null) ? set : new BitsFilteredDocIdSet(set, acceptDocs);
    }
    
    public BitsFilteredDocIdSet(final DocIdSet innerSet, final Bits acceptDocs) {
        super(innerSet);
        this.acceptDocs = Objects.requireNonNull(acceptDocs, "Bits must not be null");
    }
    
    @Override
    protected boolean match(final int docid) {
        return this.acceptDocs.get(docid);
    }
}

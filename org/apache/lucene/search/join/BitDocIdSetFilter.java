package org.apache.lucene.search.join;

import java.util.Objects;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.search.BitsFilteredDocIdSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.Filter;

@Deprecated
public abstract class BitDocIdSetFilter extends Filter implements BitSetProducer
{
    protected BitDocIdSetFilter() {
    }
    
    public abstract BitDocIdSet getDocIdSet(final LeafReaderContext p0) throws IOException;
    
    public final DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        return BitsFilteredDocIdSet.wrap((DocIdSet)this.getDocIdSet(context), acceptDocs);
    }
    
    public final BitSet getBitSet(final LeafReaderContext context) throws IOException {
        final BitDocIdSet set = this.getDocIdSet(context);
        if (set == null) {
            return null;
        }
        final BitSet bits = set.bits();
        return Objects.requireNonNull(bits);
    }
}

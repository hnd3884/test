package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;

@Deprecated
public class MultiTermQueryWrapperFilter<Q extends MultiTermQuery> extends Filter
{
    protected final Q query;
    
    protected MultiTermQueryWrapperFilter(final Q query) {
        this.query = query;
    }
    
    @Override
    public String toString(final String field) {
        return this.query.toString(field);
    }
    
    @Override
    public final boolean equals(final Object o) {
        return o == this || (super.equals(o) && this.query.equals(((MultiTermQueryWrapperFilter)o).query));
    }
    
    @Override
    public final int hashCode() {
        return 31 * super.hashCode() + this.query.hashCode();
    }
    
    public final String getField() {
        return this.query.getField();
    }
    
    @Override
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        final Terms terms = context.reader().terms(this.query.field);
        if (terms == null) {
            return null;
        }
        final TermsEnum termsEnum = this.query.getTermsEnum(terms);
        assert termsEnum != null;
        final BitDocIdSet.Builder builder = new BitDocIdSet.Builder(context.reader().maxDoc());
        PostingsEnum docs = null;
        while (termsEnum.next() != null) {
            docs = termsEnum.postings(docs, 0);
            builder.or(docs);
        }
        return BitsFilteredDocIdSet.wrap(builder.build(), acceptDocs);
    }
}

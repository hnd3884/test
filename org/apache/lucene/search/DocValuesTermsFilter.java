package org.apache.lucene.search;

import java.util.Arrays;
import java.io.IOException;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.FixedBitSet;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.BytesRef;

@Deprecated
public class DocValuesTermsFilter extends Filter
{
    private String field;
    private BytesRef[] terms;
    
    public DocValuesTermsFilter(final String field, final BytesRef... terms) {
        this.field = field;
        this.terms = terms;
    }
    
    public DocValuesTermsFilter(final String field, final String... terms) {
        this.field = field;
        this.terms = new BytesRef[terms.length];
        for (int i = 0; i < terms.length; ++i) {
            this.terms[i] = new BytesRef((CharSequence)terms[i]);
        }
    }
    
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        final SortedDocValues fcsi = DocValues.getSorted(context.reader(), this.field);
        final FixedBitSet bits = new FixedBitSet(fcsi.getValueCount());
        for (int i = 0; i < this.terms.length; ++i) {
            final int ord = fcsi.lookupTerm(this.terms[i]);
            if (ord >= 0) {
                bits.set(ord);
            }
        }
        return (DocIdSet)new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
            protected final boolean matchDoc(final int doc) {
                final int ord = fcsi.getOrd(doc);
                return ord != -1 && bits.get(ord);
            }
        };
    }
    
    public String toString(final String defaultField) {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.field).append(": [");
        for (final BytesRef term : this.terms) {
            sb.append(term).append(", ");
        }
        if (this.terms.length > 0) {
            sb.setLength(sb.length() - 2);
        }
        return sb.append(']').toString();
    }
    
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final DocValuesTermsFilter other = (DocValuesTermsFilter)obj;
        return this.field.equals(other.field) && Arrays.equals(this.terms, other.terms);
    }
    
    public int hashCode() {
        int h = super.hashCode();
        h = 31 * h + this.field.hashCode();
        h = 31 * h + Arrays.hashCode(this.terms);
        return h;
    }
}

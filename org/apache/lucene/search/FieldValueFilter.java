package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.BitDocIdSet;
import org.apache.lucene.util.BitSet;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;

@Deprecated
public class FieldValueFilter extends Filter
{
    private final String field;
    private final boolean negate;
    
    public FieldValueFilter(final String field) {
        this(field, false);
    }
    
    public FieldValueFilter(final String field, final boolean negate) {
        this.field = field;
        this.negate = negate;
    }
    
    public String field() {
        return this.field;
    }
    
    public boolean negate() {
        return this.negate;
    }
    
    @Override
    public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
        final Bits docsWithField = DocValues.getDocsWithField(context.reader(), this.field);
        if (this.negate) {
            if (docsWithField instanceof Bits.MatchAllBits) {
                return null;
            }
            return new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                @Override
                protected final boolean matchDoc(final int doc) {
                    return !docsWithField.get(doc);
                }
            };
        }
        else {
            if (docsWithField instanceof Bits.MatchNoBits) {
                return null;
            }
            if (docsWithField instanceof BitSet) {
                return BitsFilteredDocIdSet.wrap(new BitDocIdSet((BitSet)docsWithField), acceptDocs);
            }
            return new DocValuesDocIdSet(context.reader().maxDoc(), acceptDocs) {
                @Override
                protected final boolean matchDoc(final int doc) {
                    return docsWithField.get(doc);
                }
            };
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = 31 * result + ((this.field == null) ? 0 : this.field.hashCode());
        result = 31 * result + (this.negate ? 1231 : 1237);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final FieldValueFilter other = (FieldValueFilter)obj;
        if (this.field == null) {
            if (other.field != null) {
                return false;
            }
        }
        else if (!this.field.equals(other.field)) {
            return false;
        }
        return this.negate == other.negate;
    }
    
    @Override
    public String toString(final String defaultField) {
        return "FieldValueFilter [field=" + this.field + ", negate=" + this.negate + "]";
    }
}

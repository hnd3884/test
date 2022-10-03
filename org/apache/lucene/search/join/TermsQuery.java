package org.apache.lucene.search.join;

import java.util.Comparator;
import org.apache.lucene.index.FilteredTermsEnum;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.search.MultiTermQuery;

class TermsQuery extends MultiTermQuery
{
    private final BytesRefHash terms;
    private final int[] ords;
    private final Query fromQuery;
    
    TermsQuery(final String field, final Query fromQuery, final BytesRefHash terms) {
        super(field);
        this.fromQuery = fromQuery;
        this.terms = terms;
        this.ords = terms.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
    }
    
    protected TermsEnum getTermsEnum(final Terms terms, final AttributeSource atts) throws IOException {
        if (this.terms.size() == 0) {
            return TermsEnum.EMPTY;
        }
        return (TermsEnum)new SeekingTermSetTermsEnum(terms.iterator(), this.terms, this.ords);
    }
    
    public String toString(final String string) {
        return "TermsQuery{field=" + this.field + '}' + ToStringUtils.boost(this.getBoost());
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TermsQuery other = (TermsQuery)obj;
        return this.fromQuery.equals((Object)other.fromQuery);
    }
    
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result += 31 * this.fromQuery.hashCode();
        return result;
    }
    
    static class SeekingTermSetTermsEnum extends FilteredTermsEnum
    {
        private final BytesRefHash terms;
        private final int[] ords;
        private final int lastElement;
        private final BytesRef lastTerm;
        private final BytesRef spare;
        private final Comparator<BytesRef> comparator;
        private BytesRef seekTerm;
        private int upto;
        
        SeekingTermSetTermsEnum(final TermsEnum tenum, final BytesRefHash terms, final int[] ords) {
            super(tenum);
            this.spare = new BytesRef();
            this.upto = 0;
            this.terms = terms;
            this.ords = ords;
            this.comparator = BytesRef.getUTF8SortedAsUnicodeComparator();
            this.lastElement = terms.size() - 1;
            this.lastTerm = terms.get(ords[this.lastElement], new BytesRef());
            this.seekTerm = terms.get(ords[this.upto], this.spare);
        }
        
        protected BytesRef nextSeekTerm(final BytesRef currentTerm) throws IOException {
            final BytesRef temp = this.seekTerm;
            this.seekTerm = null;
            return temp;
        }
        
        protected FilteredTermsEnum.AcceptStatus accept(final BytesRef term) throws IOException {
            if (this.comparator.compare(term, this.lastTerm) > 0) {
                return FilteredTermsEnum.AcceptStatus.END;
            }
            final BytesRef currentTerm = this.terms.get(this.ords[this.upto], this.spare);
            if (this.comparator.compare(term, currentTerm) == 0) {
                if (this.upto == this.lastElement) {
                    return FilteredTermsEnum.AcceptStatus.YES;
                }
                this.seekTerm = this.terms.get(this.ords[++this.upto], this.spare);
                return FilteredTermsEnum.AcceptStatus.YES_AND_SEEK;
            }
            else {
                if (this.upto == this.lastElement) {
                    return FilteredTermsEnum.AcceptStatus.NO;
                }
                while (this.upto != this.lastElement) {
                    this.seekTerm = this.terms.get(this.ords[++this.upto], this.spare);
                    final int cmp;
                    if ((cmp = this.comparator.compare(this.seekTerm, term)) >= 0) {
                        if (cmp != 0) {
                            return FilteredTermsEnum.AcceptStatus.NO_AND_SEEK;
                        }
                        if (this.upto == this.lastElement) {
                            return FilteredTermsEnum.AcceptStatus.YES;
                        }
                        this.seekTerm = this.terms.get(this.ords[++this.upto], this.spare);
                        return FilteredTermsEnum.AcceptStatus.YES_AND_SEEK;
                    }
                }
                return FilteredTermsEnum.AcceptStatus.NO;
            }
        }
    }
}

package org.apache.lucene.search;

import org.apache.lucene.util.BytesRef;

@Deprecated
public class TermRangeFilter extends MultiTermQueryWrapperFilter<TermRangeQuery>
{
    public TermRangeFilter(final String fieldName, final BytesRef lowerTerm, final BytesRef upperTerm, final boolean includeLower, final boolean includeUpper) {
        super(new TermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper));
    }
    
    public static TermRangeFilter newStringRange(final String field, final String lowerTerm, final String upperTerm, final boolean includeLower, final boolean includeUpper) {
        final BytesRef lower = (lowerTerm == null) ? null : new BytesRef(lowerTerm);
        final BytesRef upper = (upperTerm == null) ? null : new BytesRef(upperTerm);
        return new TermRangeFilter(field, lower, upper, includeLower, includeUpper);
    }
    
    public static TermRangeFilter Less(final String fieldName, final BytesRef upperTerm) {
        return new TermRangeFilter(fieldName, null, upperTerm, false, true);
    }
    
    public static TermRangeFilter More(final String fieldName, final BytesRef lowerTerm) {
        return new TermRangeFilter(fieldName, lowerTerm, null, true, false);
    }
    
    public BytesRef getLowerTerm() {
        return ((TermRangeQuery)this.query).getLowerTerm();
    }
    
    public BytesRef getUpperTerm() {
        return ((TermRangeQuery)this.query).getUpperTerm();
    }
    
    public boolean includesLower() {
        return ((TermRangeQuery)this.query).includesLower();
    }
    
    public boolean includesUpper() {
        return ((TermRangeQuery)this.query).includesUpper();
    }
}

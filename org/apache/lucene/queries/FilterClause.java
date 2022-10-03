package org.apache.lucene.queries;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.BooleanClause;

public final class FilterClause
{
    private final BooleanClause.Occur occur;
    private final Filter filter;
    
    public FilterClause(final Filter filter, final BooleanClause.Occur occur) {
        this.occur = occur;
        this.filter = filter;
    }
    
    public Filter getFilter() {
        return this.filter;
    }
    
    public BooleanClause.Occur getOccur() {
        return this.occur;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof FilterClause)) {
            return false;
        }
        final FilterClause other = (FilterClause)o;
        return this.filter.equals((Object)other.filter) && this.occur == other.occur;
    }
    
    @Override
    public int hashCode() {
        return this.filter.hashCode() ^ this.occur.hashCode();
    }
    
    public String toString(final String field) {
        return this.occur.toString() + this.filter.toString(field);
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
}

package org.apache.lucene.queries;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.QueryWrapperFilter;

@Deprecated
public class TermFilter extends QueryWrapperFilter
{
    public TermFilter(final Term term) {
        super((Query)new TermQuery(term));
    }
    
    public String toString(final String field) {
        return this.getQuery().toString();
    }
}

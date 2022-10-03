package org.apache.lucene.facet.range;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.Query;

public abstract class Range
{
    public final String label;
    
    protected Range(final String label) {
        if (label == null) {
            throw new NullPointerException("label cannot be null");
        }
        this.label = label;
    }
    
    public abstract Query getQuery(final Query p0, final ValueSource p1);
    
    public Query getQuery(final ValueSource valueSource) {
        return this.getQuery(null, valueSource);
    }
    
    protected void failNoMatch() {
        throw new IllegalArgumentException("range \"" + this.label + "\" matches nothing");
    }
}

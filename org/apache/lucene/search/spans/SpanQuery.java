package org.apache.lucene.search.spans;

import org.apache.lucene.search.Weight;
import org.apache.lucene.index.IndexReader;
import java.util.Iterator;
import java.util.Collection;
import java.util.TreeMap;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Term;
import java.util.Map;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

public abstract class SpanQuery extends Query
{
    public abstract String getField();
    
    @Override
    public abstract SpanWeight createWeight(final IndexSearcher p0, final boolean p1) throws IOException;
    
    public static Map<Term, TermContext> getTermContexts(final SpanWeight... weights) {
        final Map<Term, TermContext> terms = new TreeMap<Term, TermContext>();
        for (final SpanWeight w : weights) {
            w.extractTermContexts(terms);
        }
        return terms;
    }
    
    public static Map<Term, TermContext> getTermContexts(final Collection<SpanWeight> weights) {
        final Map<Term, TermContext> terms = new TreeMap<Term, TermContext>();
        for (final SpanWeight w : weights) {
            w.extractTermContexts(terms);
        }
        return terms;
    }
    
    @Override
    public Query rewrite(final IndexReader reader) throws IOException {
        if (super.getBoost() != 1.0f) {
            final SpanQuery rewritten = (SpanQuery)this.clone();
            rewritten.setBoost(1.0f);
            return new SpanBoostQuery(rewritten, super.getBoost());
        }
        return this;
    }
}

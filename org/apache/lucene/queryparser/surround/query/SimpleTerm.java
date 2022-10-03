package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;

public abstract class SimpleTerm extends SrndQuery implements DistanceSubQuery, Comparable<SimpleTerm>
{
    private boolean quoted;
    
    public SimpleTerm(final boolean q) {
        this.quoted = q;
    }
    
    boolean isQuoted() {
        return this.quoted;
    }
    
    public String getQuote() {
        return "\"";
    }
    
    public String getFieldOperator() {
        return "/";
    }
    
    public abstract String toStringUnquoted();
    
    @Deprecated
    @Override
    public int compareTo(final SimpleTerm ost) {
        return this.toStringUnquoted().compareTo(ost.toStringUnquoted());
    }
    
    protected void suffixToString(final StringBuilder r) {
    }
    
    @Override
    public String toString() {
        final StringBuilder r = new StringBuilder();
        if (this.isQuoted()) {
            r.append(this.getQuote());
        }
        r.append(this.toStringUnquoted());
        if (this.isQuoted()) {
            r.append(this.getQuote());
        }
        this.suffixToString(r);
        this.weightToString(r);
        return r.toString();
    }
    
    public abstract void visitMatchingTerms(final IndexReader p0, final String p1, final MatchingTermVisitor p2) throws IOException;
    
    @Override
    public String distanceSubQueryNotAllowed() {
        return null;
    }
    
    @Override
    public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
        this.visitMatchingTerms(sncf.getIndexReader(), sncf.getFieldName(), new MatchingTermVisitor() {
            @Override
            public void visitMatchingTerm(final Term term) throws IOException {
                sncf.addTermWeighted(term, SimpleTerm.this.getWeight());
            }
        });
    }
    
    @Override
    public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
        return new SimpleTermRewriteQuery(this, fieldName, qf);
    }
    
    public interface MatchingTermVisitor
    {
        void visitMatchingTerm(final Term p0) throws IOException;
    }
}

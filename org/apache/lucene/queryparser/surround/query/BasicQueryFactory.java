package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;

public class BasicQueryFactory
{
    private int maxBasicQueries;
    private int queriesMade;
    
    public BasicQueryFactory(final int maxBasicQueries) {
        this.maxBasicQueries = maxBasicQueries;
        this.queriesMade = 0;
    }
    
    public BasicQueryFactory() {
        this(1024);
    }
    
    public int getNrQueriesMade() {
        return this.queriesMade;
    }
    
    public int getMaxBasicQueries() {
        return this.maxBasicQueries;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "(maxBasicQueries: " + this.maxBasicQueries + ", queriesMade: " + this.queriesMade + ")";
    }
    
    private boolean atMax() {
        return this.queriesMade >= this.maxBasicQueries;
    }
    
    protected synchronized void checkMax() throws TooManyBasicQueries {
        if (this.atMax()) {
            throw new TooManyBasicQueries(this.getMaxBasicQueries());
        }
        ++this.queriesMade;
    }
    
    public TermQuery newTermQuery(final Term term) throws TooManyBasicQueries {
        this.checkMax();
        return new TermQuery(term);
    }
    
    public SpanTermQuery newSpanTermQuery(final Term term) throws TooManyBasicQueries {
        this.checkMax();
        return new SpanTermQuery(term);
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ (this.atMax() ? 7 : 992);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof BasicQueryFactory)) {
            return false;
        }
        final BasicQueryFactory other = (BasicQueryFactory)obj;
        return this.atMax() == other.atMax();
    }
}

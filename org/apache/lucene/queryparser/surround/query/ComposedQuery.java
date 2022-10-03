package org.apache.lucene.queryparser.surround.query;

import java.util.ArrayList;
import org.apache.lucene.search.Query;
import java.util.Iterator;
import java.util.List;

public abstract class ComposedQuery extends SrndQuery
{
    protected String opName;
    protected List<SrndQuery> queries;
    private boolean operatorInfix;
    
    public ComposedQuery(final List<SrndQuery> qs, final boolean operatorInfix, final String opName) {
        this.recompose(qs);
        this.operatorInfix = operatorInfix;
        this.opName = opName;
    }
    
    protected void recompose(final List<SrndQuery> queries) {
        if (queries.size() < 2) {
            throw new AssertionError((Object)"Too few subqueries");
        }
        this.queries = queries;
    }
    
    public String getOperatorName() {
        return this.opName;
    }
    
    public Iterator<SrndQuery> getSubQueriesIterator() {
        return this.queries.listIterator();
    }
    
    public int getNrSubQueries() {
        return this.queries.size();
    }
    
    public SrndQuery getSubQuery(final int qn) {
        return this.queries.get(qn);
    }
    
    public boolean isOperatorInfix() {
        return this.operatorInfix;
    }
    
    public List<Query> makeLuceneSubQueriesField(final String fn, final BasicQueryFactory qf) {
        final List<Query> luceneSubQueries = new ArrayList<Query>();
        final Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            luceneSubQueries.add(sqi.next().makeLuceneQueryField(fn, qf));
        }
        return luceneSubQueries;
    }
    
    @Override
    public String toString() {
        final StringBuilder r = new StringBuilder();
        if (this.isOperatorInfix()) {
            this.infixToString(r);
        }
        else {
            this.prefixToString(r);
        }
        this.weightToString(r);
        return r.toString();
    }
    
    protected String getPrefixSeparator() {
        return ", ";
    }
    
    protected String getBracketOpen() {
        return "(";
    }
    
    protected String getBracketClose() {
        return ")";
    }
    
    protected void infixToString(final StringBuilder r) {
        final Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        r.append(this.getBracketOpen());
        if (sqi.hasNext()) {
            r.append(sqi.next().toString());
            while (sqi.hasNext()) {
                r.append(" ");
                r.append(this.getOperatorName());
                r.append(" ");
                r.append(sqi.next().toString());
            }
        }
        r.append(this.getBracketClose());
    }
    
    protected void prefixToString(final StringBuilder r) {
        final Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        r.append(this.getOperatorName());
        r.append(this.getBracketOpen());
        if (sqi.hasNext()) {
            r.append(sqi.next().toString());
            while (sqi.hasNext()) {
                r.append(this.getPrefixSeparator());
                r.append(sqi.next().toString());
            }
        }
        r.append(this.getBracketClose());
    }
    
    @Override
    public boolean isFieldsSubQueryAcceptable() {
        final Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            if (sqi.next().isFieldsSubQueryAcceptable()) {
                return true;
            }
        }
        return false;
    }
}

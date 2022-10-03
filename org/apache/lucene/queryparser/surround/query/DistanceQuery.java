package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.MatchNoDocsQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.index.IndexReader;
import java.io.IOException;
import org.apache.lucene.search.Query;
import java.util.Iterator;
import java.util.List;

public class DistanceQuery extends ComposedQuery implements DistanceSubQuery
{
    private int opDistance;
    private boolean ordered;
    
    public DistanceQuery(final List<SrndQuery> queries, final boolean infix, final int opDistance, final String opName, final boolean ordered) {
        super(queries, infix, opName);
        this.opDistance = opDistance;
        this.ordered = ordered;
    }
    
    public int getOpDistance() {
        return this.opDistance;
    }
    
    public boolean subQueriesOrdered() {
        return this.ordered;
    }
    
    @Override
    public String distanceSubQueryNotAllowed() {
        final Iterator<?> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            final Object leq = sqi.next();
            if (!(leq instanceof DistanceSubQuery)) {
                return "Operator " + this.getOperatorName() + " does not allow subquery " + leq.toString();
            }
            final DistanceSubQuery dsq = (DistanceSubQuery)leq;
            final String m = dsq.distanceSubQueryNotAllowed();
            if (m != null) {
                return m;
            }
        }
        return null;
    }
    
    @Override
    public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
        final Query snq = this.getSpanNearQuery(sncf.getIndexReader(), sncf.getFieldName(), sncf.getBasicQueryFactory());
        sncf.addSpanQuery(snq);
    }
    
    public Query getSpanNearQuery(final IndexReader reader, final String fieldName, final BasicQueryFactory qf) throws IOException {
        final SpanQuery[] spanClauses = new SpanQuery[this.getNrSubQueries()];
        final Iterator<?> sqi = this.getSubQueriesIterator();
        int qi = 0;
        while (sqi.hasNext()) {
            final SpanNearClauseFactory sncf = new SpanNearClauseFactory(reader, fieldName, qf);
            ((DistanceSubQuery)sqi.next()).addSpanQueries(sncf);
            if (sncf.size() == 0) {
                while (sqi.hasNext()) {
                    ((DistanceSubQuery)sqi.next()).addSpanQueries(sncf);
                    sncf.clear();
                }
                return (Query)new MatchNoDocsQuery();
            }
            spanClauses[qi] = sncf.makeSpanClause();
            ++qi;
        }
        return (Query)new SpanNearQuery(spanClauses, this.getOpDistance() - 1, this.subQueriesOrdered());
    }
    
    @Override
    public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
        return new DistanceRewriteQuery(this, fieldName, qf);
    }
}

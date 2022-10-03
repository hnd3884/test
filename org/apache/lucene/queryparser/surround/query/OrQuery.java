package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import java.util.List;

public class OrQuery extends ComposedQuery implements DistanceSubQuery
{
    public OrQuery(final List<SrndQuery> queries, final boolean infix, final String opName) {
        super(queries, infix, opName);
    }
    
    @Override
    public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
        return SrndBooleanQuery.makeBooleanQuery(this.makeLuceneSubQueriesField(fieldName, qf), BooleanClause.Occur.SHOULD);
    }
    
    @Override
    public String distanceSubQueryNotAllowed() {
        final Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            final SrndQuery leq = sqi.next();
            if (!(leq instanceof DistanceSubQuery)) {
                return "subquery not allowed: " + leq.toString();
            }
            final String m = ((DistanceSubQuery)leq).distanceSubQueryNotAllowed();
            if (m != null) {
                return m;
            }
        }
        return null;
    }
    
    @Override
    public void addSpanQueries(final SpanNearClauseFactory sncf) throws IOException {
        final Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            final SrndQuery s = sqi.next();
            ((DistanceSubQuery)s).addSpanQueries(sncf);
        }
    }
}

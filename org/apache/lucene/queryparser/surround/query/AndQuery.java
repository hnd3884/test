package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import java.util.List;

public class AndQuery extends ComposedQuery
{
    public AndQuery(final List<SrndQuery> queries, final boolean inf, final String opName) {
        super(queries, inf, opName);
    }
    
    @Override
    public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
        return SrndBooleanQuery.makeBooleanQuery(this.makeLuceneSubQueriesField(fieldName, qf), BooleanClause.Occur.MUST);
    }
}

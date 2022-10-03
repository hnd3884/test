package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import java.util.List;

public class NotQuery extends ComposedQuery
{
    public NotQuery(final List<SrndQuery> queries, final String opName) {
        super(queries, true, opName);
    }
    
    @Override
    public Query makeLuceneQueryFieldNoBoost(final String fieldName, final BasicQueryFactory qf) {
        final List<Query> luceneSubQueries = this.makeLuceneSubQueriesField(fieldName, qf);
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        bq.add((Query)luceneSubQueries.get(0), BooleanClause.Occur.MUST);
        SrndBooleanQuery.addQueriesToBoolean(bq, luceneSubQueries.subList(1, luceneSubQueries.size()), BooleanClause.Occur.MUST_NOT);
        return (Query)bq.build();
    }
}

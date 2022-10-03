package org.apache.lucene.queryparser.surround.query;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import java.util.List;
import org.apache.lucene.search.BooleanQuery;

class SrndBooleanQuery
{
    public static void addQueriesToBoolean(final BooleanQuery.Builder bq, final List<Query> queries, final BooleanClause.Occur occur) {
        for (int i = 0; i < queries.size(); ++i) {
            bq.add((Query)queries.get(i), occur);
        }
    }
    
    public static Query makeBooleanQuery(final List<Query> queries, final BooleanClause.Occur occur) {
        if (queries.size() <= 1) {
            throw new AssertionError((Object)("Too few subqueries: " + queries.size()));
        }
        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        addQueriesToBoolean(bq, queries.subList(0, queries.size()), occur);
        return (Query)bq.build();
    }
}

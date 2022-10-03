package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.IndexReader;

class DistanceRewriteQuery extends RewriteQuery<DistanceQuery>
{
    DistanceRewriteQuery(final DistanceQuery srndQuery, final String fieldName, final BasicQueryFactory qf) {
        super(srndQuery, fieldName, qf);
    }
    
    public Query rewrite(final IndexReader reader) throws IOException {
        if (this.getBoost() != 1.0f) {
            return super.rewrite(reader);
        }
        return ((DistanceQuery)this.srndQuery).getSpanNearQuery(reader, this.fieldName, this.qf);
    }
}

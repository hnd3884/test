package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;

public interface DistanceSubQuery
{
    String distanceSubQueryNotAllowed();
    
    void addSpanQueries(final SpanNearClauseFactory p0) throws IOException;
}

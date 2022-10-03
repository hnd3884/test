package org.apache.lucene.search;

import java.io.IOException;

public abstract class Rescorer
{
    public abstract TopDocs rescore(final IndexSearcher p0, final TopDocs p1, final int p2) throws IOException;
    
    public abstract Explanation explain(final IndexSearcher p0, final Explanation p1, final int p2) throws IOException;
}

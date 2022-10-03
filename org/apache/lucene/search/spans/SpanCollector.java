package org.apache.lucene.search.spans;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.PostingsEnum;

public interface SpanCollector
{
    void collectLeaf(final PostingsEnum p0, final int p1, final Term p2) throws IOException;
    
    void reset();
}

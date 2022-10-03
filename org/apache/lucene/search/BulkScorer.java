package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.Bits;

public abstract class BulkScorer
{
    public void score(final LeafCollector collector, final Bits acceptDocs) throws IOException {
        final int next = this.score(collector, acceptDocs, 0, Integer.MAX_VALUE);
        assert next == Integer.MAX_VALUE;
    }
    
    public abstract int score(final LeafCollector p0, final Bits p1, final int p2, final int p3) throws IOException;
    
    public abstract long cost();
}

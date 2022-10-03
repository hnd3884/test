package org.apache.lucene.search.join;

import java.io.IOException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.Bits;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Scorer;
import java.util.Iterator;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Weight;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

public class ToParentBlockJoinIndexSearcher extends IndexSearcher
{
    public ToParentBlockJoinIndexSearcher(final IndexReader r, final ExecutorService executor) {
        super(r, executor);
    }
    
    public ToParentBlockJoinIndexSearcher(final IndexReader r) {
        super(r);
    }
    
    protected void search(final List<LeafReaderContext> leaves, final Weight weight, final Collector collector) throws IOException {
        for (final LeafReaderContext ctx : leaves) {
            final Scorer scorer = weight.scorer(ctx);
            if (scorer != null) {
                final LeafCollector leafCollector = collector.getLeafCollector(ctx);
                leafCollector.setScorer(scorer);
                final Bits liveDocs = ctx.reader().getLiveDocs();
                final DocIdSetIterator it = scorer.iterator();
                for (int doc = it.nextDoc(); doc != Integer.MAX_VALUE; doc = it.nextDoc()) {
                    if (liveDocs == null || liveDocs.get(doc)) {
                        leafCollector.collect(doc);
                    }
                }
            }
        }
    }
}

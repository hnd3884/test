package org.apache.lucene.search.suggest.document;

import org.apache.lucene.search.BulkScorer;
import java.util.Iterator;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.CollectionTerminatedException;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

public class SuggestIndexSearcher extends IndexSearcher
{
    public SuggestIndexSearcher(final IndexReader reader) {
        super(reader);
    }
    
    public TopSuggestDocs suggest(final CompletionQuery query, final int n) throws IOException {
        final TopSuggestDocsCollector collector = new TopSuggestDocsCollector(n);
        this.suggest(query, collector);
        return collector.get();
    }
    
    public void suggest(CompletionQuery query, final TopSuggestDocsCollector collector) throws IOException {
        query = (CompletionQuery)query.rewrite(this.getIndexReader());
        final Weight weight = query.createWeight((IndexSearcher)this, collector.needsScores());
        for (final LeafReaderContext context : this.getIndexReader().leaves()) {
            final BulkScorer scorer = weight.bulkScorer(context);
            if (scorer != null) {
                try {
                    scorer.score(collector.getLeafCollector(context), context.reader().getLiveDocs());
                }
                catch (final CollectionTerminatedException ex) {}
            }
        }
    }
}

package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public class PositiveScoresOnlyCollector extends FilterCollector
{
    public PositiveScoresOnlyCollector(final Collector in) {
        super(in);
    }
    
    @Override
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        return new FilterLeafCollector(super.getLeafCollector(context)) {
            private Scorer scorer;
            
            @Override
            public void setScorer(final Scorer scorer) throws IOException {
                this.scorer = new ScoreCachingWrappingScorer(scorer);
                this.in.setScorer(this.scorer);
            }
            
            @Override
            public void collect(final int doc) throws IOException {
                if (this.scorer.score() > 0.0f) {
                    this.in.collect(doc);
                }
            }
        };
    }
}

package org.apache.lucene.search.join;

import org.apache.lucene.util.BytesRefHash;
import java.io.IOException;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.SortedSetDocValues;

final class GenericTermsCollectorFactory
{
    private GenericTermsCollectorFactory() {
    }
    
    static GenericTermsCollector createCollectorMV(final DocValuesTermsCollector.Function<SortedSetDocValues> mvFunction, final ScoreMode mode) {
        switch (mode) {
            case None: {
                return wrap(new TermsCollector.MV(mvFunction));
            }
            case Avg: {
                return new TermsWithScoreCollector.MV.Avg(mvFunction);
            }
            default: {
                return new TermsWithScoreCollector.MV(mvFunction, mode);
            }
        }
    }
    
    static GenericTermsCollector createCollectorSV(final DocValuesTermsCollector.Function<BinaryDocValues> svFunction, final ScoreMode mode) {
        switch (mode) {
            case None: {
                return wrap(new TermsCollector.SV(svFunction));
            }
            case Avg: {
                return new TermsWithScoreCollector.SV.Avg(svFunction);
            }
            default: {
                return new TermsWithScoreCollector.SV(svFunction, mode);
            }
        }
    }
    
    static GenericTermsCollector wrap(final TermsCollector<?> collector) {
        return new GenericTermsCollector() {
            public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
                return collector.getLeafCollector(context);
            }
            
            public boolean needsScores() {
                return collector.needsScores();
            }
            
            @Override
            public BytesRefHash getCollectedTerms() {
                return collector.getCollectorTerms();
            }
            
            @Override
            public float[] getScoresPerTerm() {
                throw new UnsupportedOperationException("scores are not available for " + collector);
            }
        };
    }
}

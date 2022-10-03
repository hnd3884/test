package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;

final class DisjunctionSumScorer extends DisjunctionScorer
{
    private final float[] coord;
    
    DisjunctionSumScorer(final Weight weight, final List<Scorer> subScorers, final float[] coord, final boolean needsScores) {
        super(weight, subScorers, needsScores);
        this.coord = coord;
    }
    
    @Override
    protected float score(final DisiWrapper topList) throws IOException {
        double score = 0.0;
        int freq = 0;
        for (DisiWrapper w = topList; w != null; w = w.next) {
            score += w.scorer.score();
            ++freq;
        }
        return (float)score * this.coord[freq];
    }
}

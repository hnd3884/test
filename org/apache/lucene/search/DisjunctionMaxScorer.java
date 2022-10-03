package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;

final class DisjunctionMaxScorer extends DisjunctionScorer
{
    private final float tieBreakerMultiplier;
    
    DisjunctionMaxScorer(final Weight weight, final float tieBreakerMultiplier, final List<Scorer> subScorers, final boolean needsScores) {
        super(weight, subScorers, needsScores);
        this.tieBreakerMultiplier = tieBreakerMultiplier;
    }
    
    @Override
    protected float score(final DisiWrapper topList) throws IOException {
        float scoreSum = 0.0f;
        float scoreMax = 0.0f;
        for (DisiWrapper w = topList; w != null; w = w.next) {
            final float subScore = w.scorer.score();
            scoreSum += subScore;
            if (subScore > scoreMax) {
                scoreMax = subScore;
            }
        }
        return scoreMax + (scoreSum - scoreMax) * this.tieBreakerMultiplier;
    }
}

package org.apache.lucene.queries;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.lucene.search.Explanation;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;

public class CustomScoreProvider
{
    protected final LeafReaderContext context;
    
    public CustomScoreProvider(final LeafReaderContext context) {
        this.context = context;
    }
    
    public float customScore(final int doc, final float subQueryScore, final float[] valSrcScores) throws IOException {
        if (valSrcScores.length == 1) {
            return this.customScore(doc, subQueryScore, valSrcScores[0]);
        }
        if (valSrcScores.length == 0) {
            return this.customScore(doc, subQueryScore, 1.0f);
        }
        float score = subQueryScore;
        for (final float valSrcScore : valSrcScores) {
            score *= valSrcScore;
        }
        return score;
    }
    
    public float customScore(final int doc, final float subQueryScore, final float valSrcScore) throws IOException {
        return subQueryScore * valSrcScore;
    }
    
    public Explanation customExplain(final int doc, final Explanation subQueryExpl, final Explanation[] valSrcExpls) throws IOException {
        if (valSrcExpls.length == 1) {
            return this.customExplain(doc, subQueryExpl, valSrcExpls[0]);
        }
        if (valSrcExpls.length == 0) {
            return subQueryExpl;
        }
        float valSrcScore = 1.0f;
        for (final Explanation valSrcExpl : valSrcExpls) {
            valSrcScore *= valSrcExpl.getValue();
        }
        final List<Explanation> subs = new ArrayList<Explanation>();
        subs.add(subQueryExpl);
        for (final Explanation valSrcExpl2 : valSrcExpls) {
            subs.add(valSrcExpl2);
        }
        return Explanation.match(valSrcScore * subQueryExpl.getValue(), "custom score: product of:", (Collection)subs);
    }
    
    public Explanation customExplain(final int doc, final Explanation subQueryExpl, final Explanation valSrcExpl) throws IOException {
        float valSrcScore = 1.0f;
        if (valSrcExpl != null) {
            valSrcScore *= valSrcExpl.getValue();
        }
        return Explanation.match(valSrcScore * subQueryExpl.getValue(), "custom score: product of:", new Explanation[] { subQueryExpl, valSrcExpl });
    }
}

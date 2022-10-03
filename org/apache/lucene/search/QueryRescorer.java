package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Arrays;
import java.util.Comparator;

public abstract class QueryRescorer extends Rescorer
{
    private final Query query;
    
    public QueryRescorer(final Query query) {
        this.query = query;
    }
    
    protected abstract float combine(final float p0, final boolean p1, final float p2);
    
    @Override
    public TopDocs rescore(final IndexSearcher searcher, final TopDocs firstPassTopDocs, final int topN) throws IOException {
        ScoreDoc[] hits = firstPassTopDocs.scoreDocs.clone();
        Arrays.sort(hits, new Comparator<ScoreDoc>() {
            @Override
            public int compare(final ScoreDoc a, final ScoreDoc b) {
                return a.doc - b.doc;
            }
        });
        final List<LeafReaderContext> leaves = searcher.getIndexReader().leaves();
        final Weight weight = searcher.createNormalizedWeight(this.query, true);
        int hitUpto = 0;
        int readerUpto = -1;
        int endDoc = 0;
        int docBase = 0;
        Scorer scorer = null;
        while (hitUpto < hits.length) {
            final ScoreDoc hit = hits[hitUpto];
            int docID;
            LeafReaderContext readerContext;
            for (docID = hit.doc, readerContext = null; docID >= endDoc; endDoc = readerContext.docBase + readerContext.reader().maxDoc()) {
                ++readerUpto;
                readerContext = leaves.get(readerUpto);
            }
            if (readerContext != null) {
                docBase = readerContext.docBase;
                scorer = weight.scorer(readerContext);
            }
            if (scorer != null) {
                final int targetDoc = docID - docBase;
                int actualDoc = scorer.docID();
                if (actualDoc < targetDoc) {
                    actualDoc = scorer.iterator().advance(targetDoc);
                }
                if (actualDoc == targetDoc) {
                    hit.score = this.combine(hit.score, true, scorer.score());
                }
                else {
                    assert actualDoc > targetDoc;
                    hit.score = this.combine(hit.score, false, 0.0f);
                }
            }
            else {
                hit.score = this.combine(hit.score, false, 0.0f);
            }
            ++hitUpto;
        }
        Arrays.sort(hits, new Comparator<ScoreDoc>() {
            @Override
            public int compare(final ScoreDoc a, final ScoreDoc b) {
                if (a.score > b.score) {
                    return -1;
                }
                if (a.score < b.score) {
                    return 1;
                }
                return a.doc - b.doc;
            }
        });
        if (topN < hits.length) {
            final ScoreDoc[] subset = new ScoreDoc[topN];
            System.arraycopy(hits, 0, subset, 0, topN);
            hits = subset;
        }
        return new TopDocs(firstPassTopDocs.totalHits, hits, hits[0].score);
    }
    
    @Override
    public Explanation explain(final IndexSearcher searcher, final Explanation firstPassExplanation, final int docID) throws IOException {
        final Explanation secondPassExplanation = searcher.explain(this.query, docID);
        final Float secondPassScore = secondPassExplanation.isMatch() ? Float.valueOf(secondPassExplanation.getValue()) : null;
        float score;
        if (secondPassScore == null) {
            score = this.combine(firstPassExplanation.getValue(), false, 0.0f);
        }
        else {
            score = this.combine(firstPassExplanation.getValue(), true, secondPassScore);
        }
        final Explanation first = Explanation.match(firstPassExplanation.getValue(), "first pass score", firstPassExplanation);
        Explanation second;
        if (secondPassScore == null) {
            second = Explanation.noMatch("no second pass score", new Explanation[0]);
        }
        else {
            second = Explanation.match(secondPassScore, "second pass score", secondPassExplanation);
        }
        return Explanation.match(score, "combined first and second pass score using " + this.getClass(), first, second);
    }
    
    public static TopDocs rescore(final IndexSearcher searcher, final TopDocs topDocs, final Query query, final double weight, final int topN) throws IOException {
        return new QueryRescorer(query) {
            @Override
            protected float combine(final float firstPassScore, final boolean secondPassMatches, final float secondPassScore) {
                float score = firstPassScore;
                if (secondPassMatches) {
                    score += (float)(weight * secondPassScore);
                }
                return score;
            }
        }.rescore(searcher, topDocs, topN);
    }
}

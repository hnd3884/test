package org.apache.lucene.search.join;

import java.util.Arrays;
import org.apache.lucene.search.Scorer;
import java.io.IOException;
import org.apache.lucene.util.LongValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.index.MultiDocValues;
import org.apache.lucene.search.Collector;

abstract class GlobalOrdinalsWithScoreCollector implements Collector
{
    final String field;
    final boolean doMinMax;
    final int min;
    final int max;
    final MultiDocValues.OrdinalMap ordinalMap;
    final LongBitSet collectedOrds;
    protected final Scores scores;
    protected final Occurrences occurrences;
    static final int arraySize = 4096;
    
    GlobalOrdinalsWithScoreCollector(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount, final ScoreMode scoreMode, final int min, final int max) {
        if (valueCount > 2147483647L) {
            throw new IllegalStateException("Can't collect more than [2147483647] ids");
        }
        this.field = field;
        this.doMinMax = (min > 0 || max != Integer.MAX_VALUE);
        this.min = min;
        this.max = max;
        this.ordinalMap = ordinalMap;
        this.collectedOrds = new LongBitSet(valueCount);
        if (scoreMode != ScoreMode.None) {
            this.scores = new Scores(valueCount, this.unset());
        }
        else {
            this.scores = null;
        }
        if (scoreMode == ScoreMode.Avg || this.doMinMax) {
            this.occurrences = new Occurrences(valueCount);
        }
        else {
            this.occurrences = null;
        }
    }
    
    public boolean match(final int globalOrd) {
        if (!this.collectedOrds.get((long)globalOrd)) {
            return false;
        }
        if (this.doMinMax) {
            final int occurrence = this.occurrences.getOccurrence(globalOrd);
            return occurrence >= this.min && occurrence <= this.max;
        }
        return true;
    }
    
    public float score(final int globalOrdinal) {
        return this.scores.getScore(globalOrdinal);
    }
    
    protected abstract void doScore(final int p0, final float p1, final float p2);
    
    protected abstract float unset();
    
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        final SortedDocValues docTermOrds = DocValues.getSorted(context.reader(), this.field);
        if (this.ordinalMap != null) {
            final LongValues segmentOrdToGlobalOrdLookup = this.ordinalMap.getGlobalOrds(context.ord);
            return (LeafCollector)new OrdinalMapCollector(docTermOrds, segmentOrdToGlobalOrdLookup);
        }
        return (LeafCollector)new SegmentOrdinalCollector(docTermOrds);
    }
    
    public boolean needsScores() {
        return true;
    }
    
    final class OrdinalMapCollector implements LeafCollector
    {
        private final SortedDocValues docTermOrds;
        private final LongValues segmentOrdToGlobalOrdLookup;
        private Scorer scorer;
        
        OrdinalMapCollector(final SortedDocValues docTermOrds, final LongValues segmentOrdToGlobalOrdLookup) {
            this.docTermOrds = docTermOrds;
            this.segmentOrdToGlobalOrdLookup = segmentOrdToGlobalOrdLookup;
        }
        
        public void collect(final int doc) throws IOException {
            final long segmentOrd = this.docTermOrds.getOrd(doc);
            if (segmentOrd != -1L) {
                final int globalOrd = (int)this.segmentOrdToGlobalOrdLookup.get(segmentOrd);
                GlobalOrdinalsWithScoreCollector.this.collectedOrds.set((long)globalOrd);
                final float existingScore = GlobalOrdinalsWithScoreCollector.this.scores.getScore(globalOrd);
                final float newScore = this.scorer.score();
                GlobalOrdinalsWithScoreCollector.this.doScore(globalOrd, existingScore, newScore);
                if (GlobalOrdinalsWithScoreCollector.this.occurrences != null) {
                    GlobalOrdinalsWithScoreCollector.this.occurrences.increment(globalOrd);
                }
            }
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
            this.scorer = scorer;
        }
    }
    
    final class SegmentOrdinalCollector implements LeafCollector
    {
        private final SortedDocValues docTermOrds;
        private Scorer scorer;
        
        SegmentOrdinalCollector(final SortedDocValues docTermOrds) {
            this.docTermOrds = docTermOrds;
        }
        
        public void collect(final int doc) throws IOException {
            final int segmentOrd = this.docTermOrds.getOrd(doc);
            if (segmentOrd != -1) {
                GlobalOrdinalsWithScoreCollector.this.collectedOrds.set((long)segmentOrd);
                final float existingScore = GlobalOrdinalsWithScoreCollector.this.scores.getScore(segmentOrd);
                final float newScore = this.scorer.score();
                GlobalOrdinalsWithScoreCollector.this.doScore(segmentOrd, existingScore, newScore);
                if (GlobalOrdinalsWithScoreCollector.this.occurrences != null) {
                    GlobalOrdinalsWithScoreCollector.this.occurrences.increment(segmentOrd);
                }
            }
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
            this.scorer = scorer;
        }
    }
    
    static final class Min extends GlobalOrdinalsWithScoreCollector
    {
        public Min(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount, final int min, final int max) {
            super(field, ordinalMap, valueCount, ScoreMode.Min, min, max);
        }
        
        @Override
        protected void doScore(final int globalOrd, final float existingScore, final float newScore) {
            this.scores.setScore(globalOrd, Math.min(existingScore, newScore));
        }
        
        @Override
        protected float unset() {
            return Float.POSITIVE_INFINITY;
        }
    }
    
    static final class Max extends GlobalOrdinalsWithScoreCollector
    {
        public Max(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount, final int min, final int max) {
            super(field, ordinalMap, valueCount, ScoreMode.Max, min, max);
        }
        
        @Override
        protected void doScore(final int globalOrd, final float existingScore, final float newScore) {
            this.scores.setScore(globalOrd, Math.max(existingScore, newScore));
        }
        
        @Override
        protected float unset() {
            return Float.NEGATIVE_INFINITY;
        }
    }
    
    static final class Sum extends GlobalOrdinalsWithScoreCollector
    {
        public Sum(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount, final int min, final int max) {
            super(field, ordinalMap, valueCount, ScoreMode.Total, min, max);
        }
        
        @Override
        protected void doScore(final int globalOrd, final float existingScore, final float newScore) {
            this.scores.setScore(globalOrd, existingScore + newScore);
        }
        
        @Override
        protected float unset() {
            return 0.0f;
        }
    }
    
    static final class Avg extends GlobalOrdinalsWithScoreCollector
    {
        public Avg(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount, final int min, final int max) {
            super(field, ordinalMap, valueCount, ScoreMode.Avg, min, max);
        }
        
        @Override
        protected void doScore(final int globalOrd, final float existingScore, final float newScore) {
            this.scores.setScore(globalOrd, existingScore + newScore);
        }
        
        @Override
        public float score(final int globalOrdinal) {
            return this.scores.getScore(globalOrdinal) / this.occurrences.getOccurrence(globalOrdinal);
        }
        
        @Override
        protected float unset() {
            return 0.0f;
        }
    }
    
    static final class NoScore extends GlobalOrdinalsWithScoreCollector
    {
        public NoScore(final String field, final MultiDocValues.OrdinalMap ordinalMap, final long valueCount, final int min, final int max) {
            super(field, ordinalMap, valueCount, ScoreMode.None, min, max);
        }
        
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
            final SortedDocValues docTermOrds = DocValues.getSorted(context.reader(), this.field);
            if (this.ordinalMap != null) {
                final LongValues segmentOrdToGlobalOrdLookup = this.ordinalMap.getGlobalOrds(context.ord);
                return (LeafCollector)new LeafCollector() {
                    public void setScorer(final Scorer scorer) throws IOException {
                    }
                    
                    public void collect(final int doc) throws IOException {
                        final long segmentOrd = docTermOrds.getOrd(doc);
                        if (segmentOrd != -1L) {
                            final int globalOrd = (int)segmentOrdToGlobalOrdLookup.get(segmentOrd);
                            NoScore.this.collectedOrds.set((long)globalOrd);
                            NoScore.this.occurrences.increment(globalOrd);
                        }
                    }
                };
            }
            return (LeafCollector)new LeafCollector() {
                public void setScorer(final Scorer scorer) throws IOException {
                }
                
                public void collect(final int doc) throws IOException {
                    final int segmentOrd = docTermOrds.getOrd(doc);
                    if (segmentOrd != -1) {
                        NoScore.this.collectedOrds.set((long)segmentOrd);
                        NoScore.this.occurrences.increment(segmentOrd);
                    }
                }
            };
        }
        
        @Override
        protected void doScore(final int globalOrd, final float existingScore, final float newScore) {
        }
        
        @Override
        public float score(final int globalOrdinal) {
            return 1.0f;
        }
        
        @Override
        protected float unset() {
            return 0.0f;
        }
        
        @Override
        public boolean needsScores() {
            return false;
        }
    }
    
    static final class Scores
    {
        final float[][] blocks;
        final float unset;
        
        private Scores(final long valueCount, final float unset) {
            final long blockSize = valueCount + 4096L - 1L;
            this.blocks = new float[(int)(blockSize / 4096L)][];
            this.unset = unset;
        }
        
        public void setScore(final int globalOrdinal, final float score) {
            final int block = globalOrdinal / 4096;
            final int offset = globalOrdinal % 4096;
            float[] scores = this.blocks[block];
            if (scores == null) {
                scores = (this.blocks[block] = new float[4096]);
                if (this.unset != 0.0f) {
                    Arrays.fill(scores, this.unset);
                }
            }
            scores[offset] = score;
        }
        
        public float getScore(final int globalOrdinal) {
            final int block = globalOrdinal / 4096;
            final int offset = globalOrdinal % 4096;
            final float[] scores = this.blocks[block];
            float score;
            if (scores != null) {
                score = scores[offset];
            }
            else {
                score = this.unset;
            }
            return score;
        }
    }
    
    static final class Occurrences
    {
        final int[][] blocks;
        
        private Occurrences(final long valueCount) {
            final long blockSize = valueCount + 4096L - 1L;
            this.blocks = new int[(int)(blockSize / 4096L)][];
        }
        
        public void increment(final int globalOrdinal) {
            final int block = globalOrdinal / 4096;
            final int offset = globalOrdinal % 4096;
            int[] occurrences = this.blocks[block];
            if (occurrences == null) {
                occurrences = (this.blocks[block] = new int[4096]);
            }
            final int[] array = occurrences;
            final int n = offset;
            ++array[n];
        }
        
        public int getOccurrence(final int globalOrdinal) {
            final int block = globalOrdinal / 4096;
            final int offset = globalOrdinal % 4096;
            final int[] occurrences = this.blocks[block];
            return occurrences[offset];
        }
    }
}

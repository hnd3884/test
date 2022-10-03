package org.apache.lucene.search;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.util.PriorityQueue;

public abstract class TopFieldCollector extends TopDocsCollector<FieldValueHitQueue.Entry>
{
    private static final ScoreDoc[] EMPTY_SCOREDOCS;
    private final boolean fillFields;
    float maxScore;
    final int numHits;
    FieldValueHitQueue.Entry bottom;
    boolean queueFull;
    int docBase;
    final boolean needsScores;
    
    private TopFieldCollector(final PriorityQueue<FieldValueHitQueue.Entry> pq, final int numHits, final boolean fillFields, final boolean needsScores) {
        super(pq);
        this.maxScore = Float.NaN;
        this.bottom = null;
        this.needsScores = needsScores;
        this.numHits = numHits;
        this.fillFields = fillFields;
    }
    
    @Override
    public boolean needsScores() {
        return this.needsScores;
    }
    
    public static TopFieldCollector create(final Sort sort, final int numHits, final boolean fillFields, final boolean trackDocScores, final boolean trackMaxScore) throws IOException {
        return create(sort, numHits, null, fillFields, trackDocScores, trackMaxScore);
    }
    
    public static TopFieldCollector create(final Sort sort, final int numHits, final FieldDoc after, final boolean fillFields, final boolean trackDocScores, final boolean trackMaxScore) throws IOException {
        if (sort.fields.length == 0) {
            throw new IllegalArgumentException("Sort must contain at least one field");
        }
        if (numHits <= 0) {
            throw new IllegalArgumentException("numHits must be > 0; please use TotalHitCountCollector if you just need the total hit count");
        }
        final FieldValueHitQueue<FieldValueHitQueue.Entry> queue = FieldValueHitQueue.create(sort.fields, numHits);
        if (after == null) {
            return new SimpleFieldCollector(sort, queue, numHits, fillFields, trackDocScores, trackMaxScore);
        }
        if (after.fields == null) {
            throw new IllegalArgumentException("after.fields wasn't set; you must pass fillFields=true for the previous search");
        }
        if (after.fields.length != sort.getSort().length) {
            throw new IllegalArgumentException("after.fields has " + after.fields.length + " values but sort has " + sort.getSort().length);
        }
        return new PagingFieldCollector(sort, queue, after, numHits, fillFields, trackDocScores, trackMaxScore);
    }
    
    final void add(final int slot, final int doc, final float score) {
        this.bottom = (FieldValueHitQueue.Entry)this.pq.add((T)new FieldValueHitQueue.Entry(slot, this.docBase + doc, score));
        this.queueFull = (this.totalHits == this.numHits);
    }
    
    final void updateBottom(final int doc) {
        this.bottom.doc = this.docBase + doc;
        this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
    }
    
    final void updateBottom(final int doc, final float score) {
        this.bottom.doc = this.docBase + doc;
        this.bottom.score = score;
        this.bottom = (FieldValueHitQueue.Entry)this.pq.updateTop();
    }
    
    @Override
    protected void populateResults(final ScoreDoc[] results, final int howMany) {
        if (this.fillFields) {
            final FieldValueHitQueue<FieldValueHitQueue.Entry> queue = (FieldValueHitQueue)this.pq;
            for (int i = howMany - 1; i >= 0; --i) {
                results[i] = queue.fillFields(queue.pop());
            }
        }
        else {
            for (int j = howMany - 1; j >= 0; --j) {
                final FieldValueHitQueue.Entry entry = (FieldValueHitQueue.Entry)this.pq.pop();
                results[j] = new FieldDoc(entry.doc, entry.score);
            }
        }
    }
    
    @Override
    protected TopDocs newTopDocs(ScoreDoc[] results, final int start) {
        if (results == null) {
            results = TopFieldCollector.EMPTY_SCOREDOCS;
            this.maxScore = Float.NaN;
        }
        return new TopFieldDocs(this.totalHits, results, ((FieldValueHitQueue)this.pq).getFields(), this.maxScore);
    }
    
    @Override
    public TopFieldDocs topDocs() {
        return (TopFieldDocs)super.topDocs();
    }
    
    static {
        EMPTY_SCOREDOCS = new ScoreDoc[0];
    }
    
    private abstract static class OneComparatorLeafCollector implements LeafCollector
    {
        final LeafFieldComparator comparator;
        final int reverseMul;
        final boolean mayNeedScoresTwice;
        Scorer scorer;
        
        OneComparatorLeafCollector(final LeafFieldComparator comparator, final int reverseMul, final boolean mayNeedScoresTwice) {
            this.comparator = comparator;
            this.reverseMul = reverseMul;
            this.mayNeedScoresTwice = mayNeedScoresTwice;
        }
        
        @Override
        public void setScorer(Scorer scorer) throws IOException {
            if (this.mayNeedScoresTwice && !(scorer instanceof ScoreCachingWrappingScorer)) {
                scorer = new ScoreCachingWrappingScorer(scorer);
            }
            this.scorer = scorer;
            this.comparator.setScorer(scorer);
        }
    }
    
    private abstract static class MultiComparatorLeafCollector implements LeafCollector
    {
        final LeafFieldComparator[] comparators;
        final int[] reverseMul;
        final LeafFieldComparator firstComparator;
        final int firstReverseMul;
        final boolean mayNeedScoresTwice;
        Scorer scorer;
        
        MultiComparatorLeafCollector(final LeafFieldComparator[] comparators, final int[] reverseMul, final boolean mayNeedScoresTwice) {
            this.comparators = comparators;
            this.reverseMul = reverseMul;
            this.firstComparator = comparators[0];
            this.firstReverseMul = reverseMul[0];
            this.mayNeedScoresTwice = mayNeedScoresTwice;
        }
        
        protected final int compareBottom(final int doc) throws IOException {
            int cmp = this.firstReverseMul * this.firstComparator.compareBottom(doc);
            if (cmp != 0) {
                return cmp;
            }
            for (int i = 1; i < this.comparators.length; ++i) {
                cmp = this.reverseMul[i] * this.comparators[i].compareBottom(doc);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
        
        protected final void copy(final int slot, final int doc) throws IOException {
            for (final LeafFieldComparator comparator : this.comparators) {
                comparator.copy(slot, doc);
            }
        }
        
        protected final void setBottom(final int slot) {
            for (final LeafFieldComparator comparator : this.comparators) {
                comparator.setBottom(slot);
            }
        }
        
        protected final int compareTop(final int doc) throws IOException {
            int cmp = this.firstReverseMul * this.firstComparator.compareTop(doc);
            if (cmp != 0) {
                return cmp;
            }
            for (int i = 1; i < this.comparators.length; ++i) {
                cmp = this.reverseMul[i] * this.comparators[i].compareTop(doc);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
        
        @Override
        public void setScorer(Scorer scorer) throws IOException {
            this.scorer = scorer;
            if (this.mayNeedScoresTwice && !(scorer instanceof ScoreCachingWrappingScorer)) {
                scorer = new ScoreCachingWrappingScorer(scorer);
            }
            for (final LeafFieldComparator comparator : this.comparators) {
                comparator.setScorer(scorer);
            }
        }
    }
    
    private static class SimpleFieldCollector extends TopFieldCollector
    {
        final FieldValueHitQueue<FieldValueHitQueue.Entry> queue;
        final boolean trackDocScores;
        final boolean trackMaxScore;
        final boolean mayNeedScoresTwice;
        
        public SimpleFieldCollector(final Sort sort, final FieldValueHitQueue<FieldValueHitQueue.Entry> queue, final int numHits, final boolean fillFields, final boolean trackDocScores, final boolean trackMaxScore) {
            super(queue, numHits, fillFields, sort.needsScores() || trackDocScores || trackMaxScore, null);
            this.queue = queue;
            if (trackMaxScore) {
                this.maxScore = Float.NEGATIVE_INFINITY;
            }
            this.trackDocScores = trackDocScores;
            this.trackMaxScore = trackMaxScore;
            this.mayNeedScoresTwice = (sort.needsScores() && (trackDocScores || trackMaxScore));
        }
        
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
            this.docBase = context.docBase;
            final LeafFieldComparator[] comparators = this.queue.getComparators(context);
            final int[] reverseMul = this.queue.getReverseMul();
            if (comparators.length == 1) {
                return new OneComparatorLeafCollector(comparators[0], reverseMul[0], this.mayNeedScoresTwice) {
                    @Override
                    public void collect(final int doc) throws IOException {
                        float score = Float.NaN;
                        if (SimpleFieldCollector.this.trackMaxScore) {
                            score = this.scorer.score();
                            if (score > SimpleFieldCollector.this.maxScore) {
                                SimpleFieldCollector.this.maxScore = score;
                            }
                        }
                        final SimpleFieldCollector this$0 = SimpleFieldCollector.this;
                        ++this$0.totalHits;
                        if (SimpleFieldCollector.this.queueFull) {
                            if (this.reverseMul * this.comparator.compareBottom(doc) <= 0) {
                                return;
                            }
                            if (SimpleFieldCollector.this.trackDocScores && !SimpleFieldCollector.this.trackMaxScore) {
                                score = this.scorer.score();
                            }
                            this.comparator.copy(SimpleFieldCollector.this.bottom.slot, doc);
                            SimpleFieldCollector.this.updateBottom(doc, score);
                            this.comparator.setBottom(SimpleFieldCollector.this.bottom.slot);
                        }
                        else {
                            final int slot = SimpleFieldCollector.this.totalHits - 1;
                            if (SimpleFieldCollector.this.trackDocScores && !SimpleFieldCollector.this.trackMaxScore) {
                                score = this.scorer.score();
                            }
                            this.comparator.copy(slot, doc);
                            SimpleFieldCollector.this.add(slot, doc, score);
                            if (SimpleFieldCollector.this.queueFull) {
                                this.comparator.setBottom(SimpleFieldCollector.this.bottom.slot);
                            }
                        }
                    }
                };
            }
            return new MultiComparatorLeafCollector(comparators, reverseMul, this.mayNeedScoresTwice) {
                @Override
                public void collect(final int doc) throws IOException {
                    float score = Float.NaN;
                    if (SimpleFieldCollector.this.trackMaxScore) {
                        score = this.scorer.score();
                        if (score > SimpleFieldCollector.this.maxScore) {
                            SimpleFieldCollector.this.maxScore = score;
                        }
                    }
                    final SimpleFieldCollector this$0 = SimpleFieldCollector.this;
                    ++this$0.totalHits;
                    if (SimpleFieldCollector.this.queueFull) {
                        if (this.compareBottom(doc) <= 0) {
                            return;
                        }
                        if (SimpleFieldCollector.this.trackDocScores && !SimpleFieldCollector.this.trackMaxScore) {
                            score = this.scorer.score();
                        }
                        this.copy(SimpleFieldCollector.this.bottom.slot, doc);
                        SimpleFieldCollector.this.updateBottom(doc, score);
                        this.setBottom(SimpleFieldCollector.this.bottom.slot);
                    }
                    else {
                        final int slot = SimpleFieldCollector.this.totalHits - 1;
                        if (SimpleFieldCollector.this.trackDocScores && !SimpleFieldCollector.this.trackMaxScore) {
                            score = this.scorer.score();
                        }
                        this.copy(slot, doc);
                        SimpleFieldCollector.this.add(slot, doc, score);
                        if (SimpleFieldCollector.this.queueFull) {
                            this.setBottom(SimpleFieldCollector.this.bottom.slot);
                        }
                    }
                }
            };
        }
    }
    
    private static final class PagingFieldCollector extends TopFieldCollector
    {
        int collectedHits;
        final FieldValueHitQueue<FieldValueHitQueue.Entry> queue;
        final boolean trackDocScores;
        final boolean trackMaxScore;
        final FieldDoc after;
        final boolean mayNeedScoresTwice;
        
        public PagingFieldCollector(final Sort sort, final FieldValueHitQueue<FieldValueHitQueue.Entry> queue, final FieldDoc after, final int numHits, final boolean fillFields, final boolean trackDocScores, final boolean trackMaxScore) {
            super(queue, numHits, fillFields, trackDocScores || trackMaxScore || sort.needsScores(), null);
            this.queue = queue;
            this.trackDocScores = trackDocScores;
            this.trackMaxScore = trackMaxScore;
            this.after = after;
            this.mayNeedScoresTwice = (sort.needsScores() && (trackDocScores || trackMaxScore));
            this.maxScore = Float.NEGATIVE_INFINITY;
            final FieldComparator<?>[] comparators = queue.comparators;
            for (int i = 0; i < comparators.length; ++i) {
                final FieldComparator<Object> comparator = (FieldComparator<Object>)comparators[i];
                comparator.setTopValue(after.fields[i]);
            }
        }
        
        @Override
        public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
            this.docBase = context.docBase;
            final int afterDoc = this.after.doc - this.docBase;
            return new MultiComparatorLeafCollector(this.queue.getComparators(context), this.queue.getReverseMul(), this.mayNeedScoresTwice) {
                @Override
                public void collect(final int doc) throws IOException {
                    final PagingFieldCollector this$0 = PagingFieldCollector.this;
                    ++this$0.totalHits;
                    float score = Float.NaN;
                    if (PagingFieldCollector.this.trackMaxScore) {
                        score = this.scorer.score();
                        if (score > PagingFieldCollector.this.maxScore) {
                            PagingFieldCollector.this.maxScore = score;
                        }
                    }
                    if (PagingFieldCollector.this.queueFull) {
                        final int cmp = this.compareBottom(doc);
                        if (cmp <= 0) {
                            return;
                        }
                    }
                    final int topCmp = this.compareTop(doc);
                    if (topCmp > 0 || (topCmp == 0 && doc <= afterDoc)) {
                        return;
                    }
                    if (PagingFieldCollector.this.queueFull) {
                        this.copy(PagingFieldCollector.this.bottom.slot, doc);
                        if (PagingFieldCollector.this.trackDocScores && !PagingFieldCollector.this.trackMaxScore) {
                            score = this.scorer.score();
                        }
                        PagingFieldCollector.this.updateBottom(doc, score);
                        this.setBottom(PagingFieldCollector.this.bottom.slot);
                    }
                    else {
                        final PagingFieldCollector this$2 = PagingFieldCollector.this;
                        ++this$2.collectedHits;
                        final int slot = PagingFieldCollector.this.collectedHits - 1;
                        this.copy(slot, doc);
                        if (PagingFieldCollector.this.trackDocScores && !PagingFieldCollector.this.trackMaxScore) {
                            score = this.scorer.score();
                        }
                        PagingFieldCollector.this.bottom = (FieldValueHitQueue.Entry)PagingFieldCollector.this.pq.add((T)new FieldValueHitQueue.Entry(slot, PagingFieldCollector.this.docBase + doc, score));
                        PagingFieldCollector.this.queueFull = (PagingFieldCollector.this.collectedHits == PagingFieldCollector.this.numHits);
                        if (PagingFieldCollector.this.queueFull) {
                            this.setBottom(PagingFieldCollector.this.bottom.slot);
                        }
                    }
                }
            };
        }
    }
}

package org.apache.lucene.search;

import java.util.Arrays;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.util.PriorityQueue;
import java.util.Collection;

final class BooleanScorer extends BulkScorer
{
    static final int SHIFT = 11;
    static final int SIZE = 2048;
    static final int MASK = 2047;
    static final int SET_SIZE = 32;
    static final int SET_MASK = 31;
    final Bucket[] buckets;
    final long[] matching;
    final float[] coordFactors;
    final BulkScorerAndDoc[] leads;
    final HeadPriorityQueue head;
    final TailPriorityQueue tail;
    final FakeScorer fakeScorer;
    final int minShouldMatch;
    final long cost;
    final OrCollector orCollector;
    
    private static long cost(final Collection<BulkScorer> scorers, final int minShouldMatch) {
        final PriorityQueue<BulkScorer> pq = new PriorityQueue<BulkScorer>(scorers.size() - minShouldMatch + 1) {
            @Override
            protected boolean lessThan(final BulkScorer a, final BulkScorer b) {
                return a.cost() > b.cost();
            }
        };
        for (final BulkScorer scorer : scorers) {
            pq.insertWithOverflow(scorer);
        }
        long cost = 0L;
        for (BulkScorer scorer2 = pq.pop(); scorer2 != null; scorer2 = pq.pop()) {
            cost += scorer2.cost();
        }
        return cost;
    }
    
    BooleanScorer(final BooleanWeight weight, final boolean disableCoord, final int maxCoord, final Collection<BulkScorer> scorers, final int minShouldMatch, final boolean needsScores) {
        this.buckets = new Bucket[2048];
        this.matching = new long[32];
        this.fakeScorer = new FakeScorer();
        this.orCollector = new OrCollector();
        if (minShouldMatch < 1 || minShouldMatch > scorers.size()) {
            throw new IllegalArgumentException("minShouldMatch should be within 1..num_scorers. Got " + minShouldMatch);
        }
        if (scorers.size() <= 1) {
            throw new IllegalArgumentException("This scorer can only be used with two scorers or more, got " + scorers.size());
        }
        for (int i = 0; i < this.buckets.length; ++i) {
            this.buckets[i] = new Bucket();
        }
        this.leads = new BulkScorerAndDoc[scorers.size()];
        this.head = new HeadPriorityQueue(scorers.size() - minShouldMatch + 1);
        this.tail = new TailPriorityQueue(minShouldMatch - 1);
        this.minShouldMatch = minShouldMatch;
        for (BulkScorer scorer : scorers) {
            if (!needsScores) {
                scorer = BooleanWeight.disableScoring(scorer);
            }
            final BulkScorerAndDoc evicted = this.tail.insertWithOverflow(new BulkScorerAndDoc(scorer));
            if (evicted != null) {
                this.head.add(evicted);
            }
        }
        this.cost = cost(scorers, minShouldMatch);
        this.coordFactors = new float[scorers.size() + 1];
        for (int i = 0; i < this.coordFactors.length; ++i) {
            this.coordFactors[i] = (disableCoord ? 1.0f : weight.coord(i, maxCoord));
        }
    }
    
    @Override
    public long cost() {
        return this.cost;
    }
    
    private void scoreDocument(final LeafCollector collector, final int base, final int i) throws IOException {
        final FakeScorer fakeScorer = this.fakeScorer;
        final Bucket bucket = this.buckets[i];
        if (bucket.freq >= this.minShouldMatch) {
            fakeScorer.freq = bucket.freq;
            fakeScorer.score = (float)bucket.score * this.coordFactors[bucket.freq];
            final int doc = base | i;
            collector.collect(fakeScorer.doc = doc);
        }
        bucket.freq = 0;
        bucket.score = 0.0;
    }
    
    private void scoreMatches(final LeafCollector collector, final int base) throws IOException {
        final long[] matching = this.matching;
        for (int idx = 0; idx < matching.length; ++idx) {
            int ntz;
            for (long bits = matching[idx]; bits != 0L; bits ^= 1L << ntz) {
                ntz = Long.numberOfTrailingZeros(bits);
                final int doc = idx << 6 | ntz;
                this.scoreDocument(collector, base, doc);
            }
        }
    }
    
    private void scoreWindowIntoBitSetAndReplay(final LeafCollector collector, final Bits acceptDocs, final int base, final int min, final int max, final BulkScorerAndDoc[] scorers, final int numScorers) throws IOException {
        for (final BulkScorerAndDoc scorer : scorers) {
            assert scorer.next < max;
            scorer.score(this.orCollector, acceptDocs, min, max);
        }
        this.scoreMatches(collector, base);
        Arrays.fill(this.matching, 0L);
    }
    
    private BulkScorerAndDoc advance(final int min) throws IOException {
        assert this.tail.size() == this.minShouldMatch - 1;
        final HeadPriorityQueue head = this.head;
        final TailPriorityQueue tail = this.tail;
        BulkScorerAndDoc headTop = head.top();
        BulkScorerAndDoc tailTop = tail.top();
        while (headTop.next < min) {
            if (tailTop == null || headTop.cost <= tailTop.cost) {
                headTop.advance(min);
                headTop = head.updateTop();
            }
            else {
                final BulkScorerAndDoc previousHeadTop = headTop;
                tailTop.advance(min);
                headTop = head.updateTop(tailTop);
                tailTop = tail.updateTop(previousHeadTop);
            }
        }
        return headTop;
    }
    
    private void scoreWindowMultipleScorers(final LeafCollector collector, final Bits acceptDocs, final int windowBase, final int windowMin, final int windowMax, int maxFreq) throws IOException {
        while (maxFreq < this.minShouldMatch && maxFreq + this.tail.size() >= this.minShouldMatch) {
            final BulkScorerAndDoc candidate = this.tail.pop();
            candidate.advance(windowMin);
            if (candidate.next < windowMax) {
                this.leads[maxFreq++] = candidate;
            }
            else {
                this.head.add(candidate);
            }
        }
        if (maxFreq >= this.minShouldMatch) {
            for (int i = 0; i < this.tail.size(); ++i) {
                this.leads[maxFreq++] = this.tail.get(i);
            }
            this.tail.clear();
            this.scoreWindowIntoBitSetAndReplay(collector, acceptDocs, windowBase, windowMin, windowMax, this.leads, maxFreq);
        }
        for (int i = 0; i < maxFreq; ++i) {
            final BulkScorerAndDoc evicted = this.head.insertWithOverflow(this.leads[i]);
            if (evicted != null) {
                this.tail.add(evicted);
            }
        }
    }
    
    private void scoreWindowSingleScorer(final BulkScorerAndDoc bulkScorer, final LeafCollector collector, final LeafCollector singleClauseCollector, final Bits acceptDocs, final int windowMin, final int windowMax, final int max) throws IOException {
        assert this.tail.size() == 0;
        final int nextWindowBase = this.head.top().next & 0xFFFFF800;
        final int end = Math.max(windowMax, Math.min(max, nextWindowBase));
        bulkScorer.score(singleClauseCollector, acceptDocs, windowMin, end);
        collector.setScorer(this.fakeScorer);
    }
    
    private BulkScorerAndDoc scoreWindow(final BulkScorerAndDoc top, final LeafCollector collector, final LeafCollector singleClauseCollector, final Bits acceptDocs, final int min, final int max) throws IOException {
        final int windowBase = top.next & 0xFFFFF800;
        final int windowMin = Math.max(min, windowBase);
        final int windowMax = Math.min(max, windowBase + 2048);
        this.leads[0] = this.head.pop();
        int maxFreq = 1;
        while (this.head.size() > 0 && this.head.top().next < windowMax) {
            this.leads[maxFreq++] = this.head.pop();
        }
        if (this.minShouldMatch == 1 && maxFreq == 1) {
            final BulkScorerAndDoc bulkScorer = this.leads[0];
            this.scoreWindowSingleScorer(bulkScorer, collector, singleClauseCollector, acceptDocs, windowMin, windowMax, max);
            return this.head.add(bulkScorer);
        }
        this.scoreWindowMultipleScorers(collector, acceptDocs, windowBase, windowMin, windowMax, maxFreq);
        return this.head.top();
    }
    
    @Override
    public int score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
        this.fakeScorer.doc = -1;
        collector.setScorer(this.fakeScorer);
        LeafCollector singleClauseCollector;
        if (this.coordFactors[1] == 1.0f) {
            singleClauseCollector = collector;
        }
        else {
            singleClauseCollector = new FilterLeafCollector(collector) {
                @Override
                public void setScorer(final Scorer scorer) throws IOException {
                    super.setScorer(new BooleanTopLevelScorers.BoostedScorer(scorer, BooleanScorer.this.coordFactors[1]));
                }
            };
        }
        BulkScorerAndDoc top;
        for (top = this.advance(min); top.next < max; top = this.scoreWindow(top, collector, singleClauseCollector, acceptDocs, min, max)) {}
        return top.next;
    }
    
    static class Bucket
    {
        double score;
        int freq;
    }
    
    private class BulkScorerAndDoc
    {
        final BulkScorer scorer;
        final long cost;
        int next;
        
        BulkScorerAndDoc(final BulkScorer scorer) {
            this.scorer = scorer;
            this.cost = scorer.cost();
            this.next = -1;
        }
        
        void advance(final int min) throws IOException {
            this.score(BooleanScorer.this.orCollector, null, min, min);
        }
        
        void score(final LeafCollector collector, final Bits acceptDocs, final int min, final int max) throws IOException {
            this.next = this.scorer.score(collector, acceptDocs, min, max);
        }
    }
    
    static final class HeadPriorityQueue extends PriorityQueue<BulkScorerAndDoc>
    {
        public HeadPriorityQueue(final int maxSize) {
            super(maxSize);
        }
        
        @Override
        protected boolean lessThan(final BulkScorerAndDoc a, final BulkScorerAndDoc b) {
            return a.next < b.next;
        }
    }
    
    static final class TailPriorityQueue extends PriorityQueue<BulkScorerAndDoc>
    {
        public TailPriorityQueue(final int maxSize) {
            super(maxSize);
        }
        
        @Override
        protected boolean lessThan(final BulkScorerAndDoc a, final BulkScorerAndDoc b) {
            return a.cost < b.cost;
        }
        
        public BulkScorerAndDoc get(final int i) {
            if (i < 0 || i >= this.size()) {
                throw new IndexOutOfBoundsException();
            }
            return (BulkScorerAndDoc)this.getHeapArray()[1 + i];
        }
    }
    
    final class OrCollector implements LeafCollector
    {
        Scorer scorer;
        
        @Override
        public void setScorer(final Scorer scorer) {
            this.scorer = scorer;
        }
        
        @Override
        public void collect(final int doc) throws IOException {
            final int i = doc & 0x7FF;
            final int idx = i >>> 6;
            final long[] matching = BooleanScorer.this.matching;
            final int n = idx;
            matching[n] |= 1L << i;
            final Bucket bucket2;
            final Bucket bucket = bucket2 = BooleanScorer.this.buckets[i];
            ++bucket2.freq;
            final Bucket bucket3 = bucket;
            bucket3.score += this.scorer.score();
        }
    }
}

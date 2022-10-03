package org.apache.lucene.search.join;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.util.ArrayUtil;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;
import org.apache.lucene.search.ScoreCachingWrappingScorer;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import java.util.HashMap;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldValueHitQueue;
import org.apache.lucene.search.Query;
import java.util.Map;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.Collector;

public class ToParentBlockJoinCollector implements Collector
{
    private final Sort sort;
    private final Map<Query, Integer> joinQueryID;
    private final int numParentHits;
    private final FieldValueHitQueue<OneGroup> queue;
    private final FieldComparator<?>[] comparators;
    private final boolean trackMaxScore;
    private final boolean trackScores;
    private ToParentBlockJoinQuery.BlockJoinScorer[] joinScorers;
    private boolean queueFull;
    private OneGroup bottom;
    private int totalHitCount;
    private float maxScore;
    private OneGroup[] sortedGroups;
    
    public ToParentBlockJoinCollector(final Sort sort, final int numParentHits, final boolean trackScores, final boolean trackMaxScore) throws IOException {
        this.joinQueryID = new HashMap<Query, Integer>();
        this.joinScorers = new ToParentBlockJoinQuery.BlockJoinScorer[0];
        this.maxScore = Float.NaN;
        this.sort = sort;
        this.trackMaxScore = trackMaxScore;
        if (trackMaxScore) {
            this.maxScore = Float.MIN_VALUE;
        }
        this.trackScores = trackScores;
        this.numParentHits = numParentHits;
        this.queue = (FieldValueHitQueue<OneGroup>)FieldValueHitQueue.create(sort.getSort(), numParentHits);
        this.comparators = (FieldComparator<?>[])this.queue.getComparators();
    }
    
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        final LeafFieldComparator[] comparators = this.queue.getComparators(context);
        final int[] reverseMul = this.queue.getReverseMul();
        final int docBase = context.docBase;
        return (LeafCollector)new LeafCollector() {
            private Scorer scorer;
            
            public void setScorer(Scorer scorer) throws IOException {
                if (!(scorer instanceof ScoreCachingWrappingScorer)) {
                    scorer = (Scorer)new ScoreCachingWrappingScorer(scorer);
                }
                this.scorer = scorer;
                for (final LeafFieldComparator comparator : comparators) {
                    comparator.setScorer(scorer);
                }
                Arrays.fill(ToParentBlockJoinCollector.this.joinScorers, null);
                final Queue<Scorer> queue = new LinkedList<Scorer>();
                queue.add(scorer);
                while ((scorer = queue.poll()) != null) {
                    if (scorer instanceof ToParentBlockJoinQuery.BlockJoinScorer) {
                        ToParentBlockJoinCollector.this.enroll((ToParentBlockJoinQuery)scorer.getWeight().getQuery(), (ToParentBlockJoinQuery.BlockJoinScorer)scorer);
                    }
                    for (final Scorer.ChildScorer sub : scorer.getChildren()) {
                        queue.add(sub.child);
                    }
                }
            }
            
            public void collect(final int parentDoc) throws IOException {
                ToParentBlockJoinCollector.this.totalHitCount++;
                float score = Float.NaN;
                if (ToParentBlockJoinCollector.this.trackMaxScore) {
                    score = this.scorer.score();
                    ToParentBlockJoinCollector.this.maxScore = Math.max(ToParentBlockJoinCollector.this.maxScore, score);
                }
                if (ToParentBlockJoinCollector.this.queueFull) {
                    int c = 0;
                    for (int i = 0; i < comparators.length; ++i) {
                        c = reverseMul[i] * comparators[i].compareBottom(parentDoc);
                        if (c != 0) {
                            break;
                        }
                    }
                    if (c <= 0) {
                        return;
                    }
                    for (final LeafFieldComparator comparator : comparators) {
                        comparator.copy(ToParentBlockJoinCollector.this.bottom.slot, parentDoc);
                    }
                    if (!ToParentBlockJoinCollector.this.trackMaxScore && ToParentBlockJoinCollector.this.trackScores) {
                        score = this.scorer.score();
                    }
                    ToParentBlockJoinCollector.this.bottom.doc = docBase + parentDoc;
                    ToParentBlockJoinCollector.this.bottom.readerContext = context;
                    ToParentBlockJoinCollector.this.bottom.score = score;
                    this.copyGroups(ToParentBlockJoinCollector.this.bottom);
                    ToParentBlockJoinCollector.this.bottom = (OneGroup)ToParentBlockJoinCollector.this.queue.updateTop();
                    for (final LeafFieldComparator comparator : comparators) {
                        comparator.setBottom(ToParentBlockJoinCollector.this.bottom.slot);
                    }
                }
                else {
                    final int comparatorSlot = ToParentBlockJoinCollector.this.totalHitCount - 1;
                    for (final LeafFieldComparator comparator : comparators) {
                        comparator.copy(comparatorSlot, parentDoc);
                    }
                    if (!ToParentBlockJoinCollector.this.trackMaxScore && ToParentBlockJoinCollector.this.trackScores) {
                        score = this.scorer.score();
                    }
                    final OneGroup og = new OneGroup(comparatorSlot, docBase + parentDoc, score, ToParentBlockJoinCollector.this.joinScorers.length, ToParentBlockJoinCollector.this.trackScores);
                    og.readerContext = context;
                    this.copyGroups(og);
                    ToParentBlockJoinCollector.this.bottom = (OneGroup)ToParentBlockJoinCollector.this.queue.add((Object)og);
                    ToParentBlockJoinCollector.this.queueFull = (ToParentBlockJoinCollector.this.totalHitCount == ToParentBlockJoinCollector.this.numParentHits);
                    if (ToParentBlockJoinCollector.this.queueFull) {
                        for (final LeafFieldComparator comparator2 : comparators) {
                            comparator2.setBottom(ToParentBlockJoinCollector.this.bottom.slot);
                        }
                    }
                }
            }
            
            private void copyGroups(final OneGroup og) {
                final int numSubScorers = ToParentBlockJoinCollector.this.joinScorers.length;
                if (og.docs.length < numSubScorers) {
                    og.docs = ArrayUtil.grow(og.docs);
                }
                if (og.counts.length < numSubScorers) {
                    og.counts = ArrayUtil.grow(og.counts);
                }
                if (ToParentBlockJoinCollector.this.trackScores && og.scores.length < numSubScorers) {
                    og.scores = ArrayUtil.grow(og.scores);
                }
                for (int scorerIDX = 0; scorerIDX < numSubScorers; ++scorerIDX) {
                    final ToParentBlockJoinQuery.BlockJoinScorer joinScorer = ToParentBlockJoinCollector.this.joinScorers[scorerIDX];
                    if (joinScorer != null && docBase + joinScorer.getParentDoc() == og.doc) {
                        og.counts[scorerIDX] = joinScorer.getChildCount();
                        og.docs[scorerIDX] = joinScorer.swapChildDocs(og.docs[scorerIDX]);
                        assert og.docs[scorerIDX].length >= og.counts[scorerIDX] : "length=" + og.docs[scorerIDX].length + " vs count=" + og.counts[scorerIDX];
                        if (ToParentBlockJoinCollector.this.trackScores) {
                            og.scores[scorerIDX] = joinScorer.swapChildScores(og.scores[scorerIDX]);
                            assert og.scores[scorerIDX].length >= og.counts[scorerIDX] : "length=" + og.scores[scorerIDX].length + " vs count=" + og.counts[scorerIDX];
                        }
                    }
                    else {
                        og.counts[scorerIDX] = 0;
                    }
                }
            }
        };
    }
    
    private void enroll(final ToParentBlockJoinQuery query, final ToParentBlockJoinQuery.BlockJoinScorer scorer) {
        scorer.trackPendingChildHits();
        final Integer slot = this.joinQueryID.get(query);
        if (slot == null) {
            this.joinQueryID.put(query, this.joinScorers.length);
            final ToParentBlockJoinQuery.BlockJoinScorer[] newArray = new ToParentBlockJoinQuery.BlockJoinScorer[1 + this.joinScorers.length];
            System.arraycopy(this.joinScorers, 0, newArray, 0, this.joinScorers.length);
            (this.joinScorers = newArray)[this.joinScorers.length - 1] = scorer;
        }
        else {
            this.joinScorers[slot] = scorer;
        }
    }
    
    private void sortQueue() {
        this.sortedGroups = new OneGroup[this.queue.size()];
        for (int downTo = this.queue.size() - 1; downTo >= 0; --downTo) {
            this.sortedGroups[downTo] = (OneGroup)this.queue.pop();
        }
    }
    
    public TopGroups<Integer> getTopGroups(final ToParentBlockJoinQuery query, final Sort withinGroupSort, final int offset, final int maxDocsPerGroup, final int withinGroupOffset, final boolean fillSortFields) throws IOException {
        final Integer _slot = this.joinQueryID.get(query);
        if (_slot == null && this.totalHitCount == 0) {
            return null;
        }
        if (this.sortedGroups == null) {
            if (offset >= this.queue.size()) {
                return null;
            }
            this.sortQueue();
        }
        else if (offset > this.sortedGroups.length) {
            return null;
        }
        return this.accumulateGroups((_slot == null) ? -1 : ((int)_slot), offset, maxDocsPerGroup, withinGroupOffset, withinGroupSort, fillSortFields);
    }
    
    private TopGroups<Integer> accumulateGroups(final int slot, final int offset, final int maxDocsPerGroup, final int withinGroupOffset, final Sort withinGroupSort, final boolean fillSortFields) throws IOException {
        final GroupDocs<Integer>[] groups = (GroupDocs<Integer>[])new GroupDocs[this.sortedGroups.length - offset];
        final FakeScorer fakeScorer = new FakeScorer();
        int totalGroupedHitCount = 0;
        for (int groupIDX = offset; groupIDX < this.sortedGroups.length; ++groupIDX) {
            final OneGroup og = this.sortedGroups[groupIDX];
            int numChildDocs;
            if (slot == -1 || slot >= og.counts.length) {
                numChildDocs = 0;
            }
            else {
                numChildDocs = og.counts[slot];
            }
            final int numDocsInGroup = Math.max(1, Math.min(numChildDocs, maxDocsPerGroup));
            TopDocsCollector<?> collector;
            if (withinGroupSort == null) {
                if (!this.trackScores) {
                    throw new IllegalArgumentException("cannot sort by relevance within group: trackScores=false");
                }
                collector = (TopDocsCollector<?>)TopScoreDocCollector.create(numDocsInGroup);
            }
            else {
                collector = (TopDocsCollector<?>)TopFieldCollector.create(withinGroupSort, numDocsInGroup, fillSortFields, this.trackScores, this.trackMaxScore);
            }
            final LeafCollector leafCollector = collector.getLeafCollector(og.readerContext);
            leafCollector.setScorer((Scorer)fakeScorer);
            for (int docIDX = 0; docIDX < numChildDocs; ++docIDX) {
                final int doc = og.docs[slot][docIDX];
                fakeScorer.doc = doc;
                if (this.trackScores) {
                    fakeScorer.score = og.scores[slot][docIDX];
                }
                leafCollector.collect(doc);
            }
            totalGroupedHitCount += numChildDocs;
            Object[] groupSortValues;
            if (fillSortFields) {
                groupSortValues = new Object[this.comparators.length];
                for (int sortFieldIDX = 0; sortFieldIDX < this.comparators.length; ++sortFieldIDX) {
                    groupSortValues[sortFieldIDX] = this.comparators[sortFieldIDX].value(og.slot);
                }
            }
            else {
                groupSortValues = null;
            }
            final TopDocs topDocs = collector.topDocs(withinGroupOffset, numDocsInGroup);
            groups[groupIDX - offset] = (GroupDocs<Integer>)new GroupDocs(og.score, topDocs.getMaxScore(), numChildDocs, topDocs.scoreDocs, (Object)og.doc, groupSortValues);
        }
        return (TopGroups<Integer>)new TopGroups(new TopGroups(this.sort.getSort(), (SortField[])((withinGroupSort == null) ? null : withinGroupSort.getSort()), 0, totalGroupedHitCount, (GroupDocs[])groups, this.maxScore), Integer.valueOf(this.totalHitCount));
    }
    
    public TopGroups<Integer> getTopGroupsWithAllChildDocs(final ToParentBlockJoinQuery query, final Sort withinGroupSort, final int offset, final int withinGroupOffset, final boolean fillSortFields) throws IOException {
        return this.getTopGroups(query, withinGroupSort, offset, Integer.MAX_VALUE, withinGroupOffset, fillSortFields);
    }
    
    public float getMaxScore() {
        return this.maxScore;
    }
    
    public boolean needsScores() {
        return true;
    }
    
    private static final class OneGroup extends FieldValueHitQueue.Entry
    {
        LeafReaderContext readerContext;
        int[][] docs;
        float[][] scores;
        int[] counts;
        
        public OneGroup(final int comparatorSlot, final int parentDoc, final float parentScore, final int numJoins, final boolean doScores) {
            super(comparatorSlot, parentDoc, parentScore);
            this.docs = new int[numJoins][];
            for (int joinID = 0; joinID < numJoins; ++joinID) {
                this.docs[joinID] = new int[5];
            }
            if (doScores) {
                this.scores = new float[numJoins][];
                for (int joinID = 0; joinID < numJoins; ++joinID) {
                    this.scores[joinID] = new float[5];
                }
            }
            this.counts = new int[numJoins];
        }
    }
}

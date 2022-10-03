package org.apache.lucene.search.grouping;

import org.apache.lucene.util.PriorityQueue;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import java.io.IOException;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SimpleCollector;

public class BlockGroupingCollector extends SimpleCollector
{
    private int[] pendingSubDocs;
    private float[] pendingSubScores;
    private int subDocUpto;
    private final Sort groupSort;
    private final int topNGroups;
    private final Weight lastDocPerGroup;
    private final boolean needsScores;
    private final FieldComparator<?>[] comparators;
    private final LeafFieldComparator[] leafComparators;
    private final int[] reversed;
    private final int compIDXEnd;
    private int bottomSlot;
    private boolean queueFull;
    private LeafReaderContext currentReaderContext;
    private int topGroupDoc;
    private int totalHitCount;
    private int totalGroupCount;
    private int docBase;
    private int groupEndDocID;
    private DocIdSetIterator lastDocPerGroupBits;
    private Scorer scorer;
    private final GroupQueue groupQueue;
    private boolean groupCompetes;
    
    private void processGroup() {
        ++this.totalGroupCount;
        if (this.groupCompetes) {
            if (!this.queueFull) {
                final OneGroup og = new OneGroup();
                og.count = this.subDocUpto;
                og.topGroupDoc = this.docBase + this.topGroupDoc;
                og.docs = this.pendingSubDocs;
                this.pendingSubDocs = new int[10];
                if (this.needsScores) {
                    og.scores = this.pendingSubScores;
                    this.pendingSubScores = new float[10];
                }
                og.readerContext = this.currentReaderContext;
                og.comparatorSlot = this.bottomSlot;
                final OneGroup bottomGroup = (OneGroup)this.groupQueue.add((Object)og);
                this.queueFull = (this.groupQueue.size() == this.topNGroups);
                if (this.queueFull) {
                    this.bottomSlot = bottomGroup.comparatorSlot;
                    for (int i = 0; i < this.comparators.length; ++i) {
                        this.leafComparators[i].setBottom(this.bottomSlot);
                    }
                }
                else {
                    this.bottomSlot = this.groupQueue.size();
                }
            }
            else {
                final OneGroup og = (OneGroup)this.groupQueue.top();
                assert og != null;
                og.count = this.subDocUpto;
                og.topGroupDoc = this.docBase + this.topGroupDoc;
                final int[] savDocs = og.docs;
                og.docs = this.pendingSubDocs;
                this.pendingSubDocs = savDocs;
                if (this.needsScores) {
                    final float[] savScores = og.scores;
                    og.scores = this.pendingSubScores;
                    this.pendingSubScores = savScores;
                }
                og.readerContext = this.currentReaderContext;
                this.bottomSlot = ((OneGroup)this.groupQueue.updateTop()).comparatorSlot;
                for (int i = 0; i < this.comparators.length; ++i) {
                    this.leafComparators[i].setBottom(this.bottomSlot);
                }
            }
        }
        this.subDocUpto = 0;
    }
    
    public BlockGroupingCollector(final Sort groupSort, final int topNGroups, final boolean needsScores, final Weight lastDocPerGroup) throws IOException {
        if (topNGroups < 1) {
            throw new IllegalArgumentException("topNGroups must be >= 1 (got " + topNGroups + ")");
        }
        this.groupQueue = new GroupQueue(topNGroups);
        this.pendingSubDocs = new int[10];
        if (needsScores) {
            this.pendingSubScores = new float[10];
        }
        this.needsScores = needsScores;
        this.lastDocPerGroup = lastDocPerGroup;
        this.groupSort = groupSort;
        this.topNGroups = topNGroups;
        final SortField[] sortFields = groupSort.getSort();
        this.comparators = (FieldComparator<?>[])new FieldComparator[sortFields.length];
        this.leafComparators = new LeafFieldComparator[sortFields.length];
        this.compIDXEnd = this.comparators.length - 1;
        this.reversed = new int[sortFields.length];
        for (int i = 0; i < sortFields.length; ++i) {
            final SortField sortField = sortFields[i];
            this.comparators[i] = (FieldComparator<?>)sortField.getComparator(topNGroups, i);
            this.reversed[i] = (sortField.getReverse() ? -1 : 1);
        }
    }
    
    public TopGroups<?> getTopGroups(final Sort withinGroupSort, final int groupOffset, final int withinGroupOffset, final int maxDocsPerGroup, final boolean fillSortFields) throws IOException {
        if (this.subDocUpto != 0) {
            this.processGroup();
        }
        if (groupOffset >= this.groupQueue.size()) {
            return null;
        }
        int totalGroupedHitCount = 0;
        final FakeScorer fakeScorer = new FakeScorer();
        float maxScore = Float.MIN_VALUE;
        final GroupDocs<Object>[] groups = new GroupDocs[this.groupQueue.size() - groupOffset];
        for (int downTo = this.groupQueue.size() - groupOffset - 1; downTo >= 0; --downTo) {
            final OneGroup og = (OneGroup)this.groupQueue.pop();
            TopDocsCollector<?> collector;
            if (withinGroupSort.equals((Object)Sort.RELEVANCE)) {
                if (!this.needsScores) {
                    throw new IllegalArgumentException("cannot sort by relevance within group: needsScores=false");
                }
                collector = (TopDocsCollector<?>)TopScoreDocCollector.create(maxDocsPerGroup);
            }
            else {
                collector = (TopDocsCollector<?>)TopFieldCollector.create(withinGroupSort, maxDocsPerGroup, fillSortFields, this.needsScores, this.needsScores);
            }
            final LeafCollector leafCollector = collector.getLeafCollector(og.readerContext);
            leafCollector.setScorer((Scorer)fakeScorer);
            for (int docIDX = 0; docIDX < og.count; ++docIDX) {
                final int doc = og.docs[docIDX];
                fakeScorer.doc = doc;
                if (this.needsScores) {
                    fakeScorer.score = og.scores[docIDX];
                }
                leafCollector.collect(doc);
            }
            totalGroupedHitCount += og.count;
            Object[] groupSortValues;
            if (fillSortFields) {
                groupSortValues = new Comparable[this.comparators.length];
                for (int sortFieldIDX = 0; sortFieldIDX < this.comparators.length; ++sortFieldIDX) {
                    groupSortValues[sortFieldIDX] = this.comparators[sortFieldIDX].value(og.comparatorSlot);
                }
            }
            else {
                groupSortValues = null;
            }
            final TopDocs topDocs = collector.topDocs(withinGroupOffset, maxDocsPerGroup);
            groups[downTo] = new GroupDocs<Object>(Float.NaN, topDocs.getMaxScore(), og.count, topDocs.scoreDocs, null, groupSortValues);
            maxScore = Math.max(maxScore, topDocs.getMaxScore());
        }
        return new TopGroups<Object>(new TopGroups<Object>(this.groupSort.getSort(), withinGroupSort.getSort(), this.totalHitCount, totalGroupedHitCount, groups, maxScore), this.totalGroupCount);
    }
    
    public void setScorer(final Scorer scorer) throws IOException {
        this.scorer = scorer;
        for (final LeafFieldComparator comparator : this.leafComparators) {
            comparator.setScorer(scorer);
        }
    }
    
    public void collect(final int doc) throws IOException {
        if (doc > this.groupEndDocID) {
            if (this.subDocUpto != 0) {
                this.processGroup();
            }
            this.groupEndDocID = this.lastDocPerGroupBits.advance(doc);
            this.subDocUpto = 0;
            this.groupCompetes = !this.queueFull;
        }
        ++this.totalHitCount;
        if (this.subDocUpto == this.pendingSubDocs.length) {
            this.pendingSubDocs = ArrayUtil.grow(this.pendingSubDocs);
        }
        this.pendingSubDocs[this.subDocUpto] = doc;
        if (this.needsScores) {
            if (this.subDocUpto == this.pendingSubScores.length) {
                this.pendingSubScores = ArrayUtil.grow(this.pendingSubScores);
            }
            this.pendingSubScores[this.subDocUpto] = this.scorer.score();
        }
        ++this.subDocUpto;
        if (this.groupCompetes) {
            if (this.subDocUpto == 1) {
                assert !this.queueFull;
                for (final LeafFieldComparator fc : this.leafComparators) {
                    fc.copy(this.bottomSlot, doc);
                    fc.setBottom(this.bottomSlot);
                }
                this.topGroupDoc = doc;
            }
            else {
                int compIDX = 0;
                while (true) {
                    final int c = this.reversed[compIDX] * this.leafComparators[compIDX].compareBottom(doc);
                    if (c < 0) {
                        return;
                    }
                    if (c > 0) {
                        for (final LeafFieldComparator fc : this.leafComparators) {
                            fc.copy(this.bottomSlot, doc);
                            fc.setBottom(this.bottomSlot);
                        }
                        this.topGroupDoc = doc;
                        break;
                    }
                    if (compIDX == this.compIDXEnd) {
                        return;
                    }
                    ++compIDX;
                }
            }
        }
        else {
            int compIDX = 0;
            while (true) {
                final int c = this.reversed[compIDX] * this.leafComparators[compIDX].compareBottom(doc);
                if (c < 0) {
                    return;
                }
                if (c > 0) {
                    this.groupCompetes = true;
                    for (final LeafFieldComparator fc : this.leafComparators) {
                        fc.copy(this.bottomSlot, doc);
                        fc.setBottom(this.bottomSlot);
                    }
                    this.topGroupDoc = doc;
                    break;
                }
                if (compIDX == this.compIDXEnd) {
                    return;
                }
                ++compIDX;
            }
        }
    }
    
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        if (this.subDocUpto != 0) {
            this.processGroup();
        }
        this.subDocUpto = 0;
        this.docBase = readerContext.docBase;
        final Scorer s = this.lastDocPerGroup.scorer(readerContext);
        if (s == null) {
            this.lastDocPerGroupBits = null;
        }
        else {
            this.lastDocPerGroupBits = s.iterator();
        }
        this.groupEndDocID = -1;
        this.currentReaderContext = readerContext;
        for (int i = 0; i < this.comparators.length; ++i) {
            this.leafComparators[i] = this.comparators[i].getLeafComparator(readerContext);
        }
    }
    
    public boolean needsScores() {
        return this.needsScores;
    }
    
    private static final class OneGroup
    {
        LeafReaderContext readerContext;
        int topGroupDoc;
        int[] docs;
        float[] scores;
        int count;
        int comparatorSlot;
    }
    
    private final class GroupQueue extends PriorityQueue<OneGroup>
    {
        public GroupQueue(final int size) {
            super(size);
        }
        
        protected boolean lessThan(final OneGroup group1, final OneGroup group2) {
            assert group1 != group2;
            assert group1.comparatorSlot != group2.comparatorSlot;
            for (int numComparators = BlockGroupingCollector.this.comparators.length, compIDX = 0; compIDX < numComparators; ++compIDX) {
                final int c = BlockGroupingCollector.this.reversed[compIDX] * BlockGroupingCollector.this.comparators[compIDX].compare(group1.comparatorSlot, group2.comparatorSlot);
                if (c != 0) {
                    return c > 0;
                }
            }
            return group1.topGroupDoc > group2.topGroupDoc;
        }
    }
}

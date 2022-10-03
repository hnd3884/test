package org.apache.lucene.search.grouping;

import org.apache.lucene.util.PriorityQueue;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.NavigableSet;
import java.util.Comparator;
import org.apache.lucene.search.Scorer;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.search.SimpleCollector;

public abstract class AbstractGroupFacetCollector extends SimpleCollector
{
    protected final String groupField;
    protected final String facetField;
    protected final BytesRef facetPrefix;
    protected final List<SegmentResult> segmentResults;
    protected int[] segmentFacetCounts;
    protected int segmentTotalCount;
    protected int startFacetOrd;
    protected int endFacetOrd;
    
    protected AbstractGroupFacetCollector(final String groupField, final String facetField, final BytesRef facetPrefix) {
        this.groupField = groupField;
        this.facetField = facetField;
        this.facetPrefix = facetPrefix;
        this.segmentResults = new ArrayList<SegmentResult>();
    }
    
    public GroupedFacetResult mergeSegmentResults(final int size, final int minCount, final boolean orderByCount) throws IOException {
        if (this.segmentFacetCounts != null) {
            this.segmentResults.add(this.createSegmentResult());
            this.segmentFacetCounts = null;
        }
        int totalCount = 0;
        int missingCount = 0;
        final SegmentResultPriorityQueue segments = new SegmentResultPriorityQueue(this.segmentResults.size());
        for (final SegmentResult segmentResult : this.segmentResults) {
            missingCount += segmentResult.missing;
            if (segmentResult.mergePos >= segmentResult.maxTermPos) {
                continue;
            }
            totalCount += segmentResult.total;
            segments.add((Object)segmentResult);
        }
        final GroupedFacetResult facetResult = new GroupedFacetResult(size, minCount, orderByCount, totalCount, missingCount);
        while (segments.size() > 0) {
            SegmentResult segmentResult = (SegmentResult)segments.top();
            final BytesRef currentFacetValue = BytesRef.deepCopyOf(segmentResult.mergeTerm);
            int count = 0;
            do {
                count += segmentResult.counts[segmentResult.mergePos++];
                if (segmentResult.mergePos < segmentResult.maxTermPos) {
                    segmentResult.nextTerm();
                    segmentResult = (SegmentResult)segments.updateTop();
                }
                else {
                    segments.pop();
                    segmentResult = (SegmentResult)segments.top();
                    if (segmentResult == null) {
                        break;
                    }
                    continue;
                }
            } while (currentFacetValue.equals((Object)segmentResult.mergeTerm));
            facetResult.addFacetCount(currentFacetValue, count);
        }
        return facetResult;
    }
    
    protected abstract SegmentResult createSegmentResult() throws IOException;
    
    public void setScorer(final Scorer scorer) throws IOException {
    }
    
    public boolean needsScores() {
        return false;
    }
    
    public static class GroupedFacetResult
    {
        private static final Comparator<FacetEntry> orderByCountAndValue;
        private static final Comparator<FacetEntry> orderByValue;
        private final int maxSize;
        private final NavigableSet<FacetEntry> facetEntries;
        private final int totalMissingCount;
        private final int totalCount;
        private int currentMin;
        
        public GroupedFacetResult(final int size, final int minCount, final boolean orderByCount, final int totalCount, final int totalMissingCount) {
            this.facetEntries = new TreeSet<FacetEntry>(orderByCount ? GroupedFacetResult.orderByCountAndValue : GroupedFacetResult.orderByValue);
            this.totalMissingCount = totalMissingCount;
            this.totalCount = totalCount;
            this.maxSize = size;
            this.currentMin = minCount;
        }
        
        public void addFacetCount(final BytesRef facetValue, final int count) {
            if (count < this.currentMin) {
                return;
            }
            final FacetEntry facetEntry = new FacetEntry(facetValue, count);
            if (this.facetEntries.size() == this.maxSize) {
                if (this.facetEntries.higher(facetEntry) == null) {
                    return;
                }
                this.facetEntries.pollLast();
            }
            this.facetEntries.add(facetEntry);
            if (this.facetEntries.size() == this.maxSize) {
                this.currentMin = this.facetEntries.last().count;
            }
        }
        
        public List<FacetEntry> getFacetEntries(final int offset, final int limit) {
            final List<FacetEntry> entries = new LinkedList<FacetEntry>();
            int skipped = 0;
            int included = 0;
            for (final FacetEntry facetEntry : this.facetEntries) {
                if (skipped < offset) {
                    ++skipped;
                }
                else {
                    if (included++ >= limit) {
                        break;
                    }
                    entries.add(facetEntry);
                }
            }
            return entries;
        }
        
        public int getTotalCount() {
            return this.totalCount;
        }
        
        public int getTotalMissingCount() {
            return this.totalMissingCount;
        }
        
        static {
            orderByCountAndValue = new Comparator<FacetEntry>() {
                @Override
                public int compare(final FacetEntry a, final FacetEntry b) {
                    final int cmp = b.count - a.count;
                    if (cmp != 0) {
                        return cmp;
                    }
                    return a.value.compareTo(b.value);
                }
            };
            orderByValue = new Comparator<FacetEntry>() {
                @Override
                public int compare(final FacetEntry a, final FacetEntry b) {
                    return a.value.compareTo(b.value);
                }
            };
        }
    }
    
    public static class FacetEntry
    {
        private final BytesRef value;
        private final int count;
        
        public FacetEntry(final BytesRef value, final int count) {
            this.value = value;
            this.count = count;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final FacetEntry that = (FacetEntry)o;
            return this.count == that.count && this.value.equals((Object)that.value);
        }
        
        @Override
        public int hashCode() {
            int result = this.value.hashCode();
            result = 31 * result + this.count;
            return result;
        }
        
        @Override
        public String toString() {
            return "FacetEntry{value=" + this.value.utf8ToString() + ", count=" + this.count + '}';
        }
        
        public BytesRef getValue() {
            return this.value;
        }
        
        public int getCount() {
            return this.count;
        }
    }
    
    protected abstract static class SegmentResult
    {
        protected final int[] counts;
        protected final int total;
        protected final int missing;
        protected final int maxTermPos;
        protected BytesRef mergeTerm;
        protected int mergePos;
        
        protected SegmentResult(final int[] counts, final int total, final int missing, final int maxTermPos) {
            this.counts = counts;
            this.total = total;
            this.missing = missing;
            this.maxTermPos = maxTermPos;
        }
        
        protected abstract void nextTerm() throws IOException;
    }
    
    private static class SegmentResultPriorityQueue extends PriorityQueue<SegmentResult>
    {
        SegmentResultPriorityQueue(final int maxSize) {
            super(maxSize);
        }
        
        protected boolean lessThan(final SegmentResult a, final SegmentResult b) {
            return a.mergeTerm.compareTo(b.mergeTerm) < 0;
        }
    }
}

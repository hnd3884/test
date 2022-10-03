package org.apache.lucene.search.grouping.term;

import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.TermsEnum;
import java.util.Iterator;
import org.apache.lucene.util.UnicodeUtil;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.util.SentinelIntSet;
import java.util.List;
import org.apache.lucene.search.grouping.AbstractGroupFacetCollector;

public abstract class TermGroupFacetCollector extends AbstractGroupFacetCollector
{
    final List<GroupedFacetHit> groupedFacetHits;
    final SentinelIntSet segmentGroupedFacetHits;
    SortedDocValues groupFieldTermsIndex;
    
    public static TermGroupFacetCollector createTermGroupFacetCollector(final String groupField, final String facetField, final boolean facetFieldMultivalued, final BytesRef facetPrefix, final int initialSize) {
        if (facetFieldMultivalued) {
            return new MV(groupField, facetField, facetPrefix, initialSize);
        }
        return new SV(groupField, facetField, facetPrefix, initialSize);
    }
    
    TermGroupFacetCollector(final String groupField, final String facetField, final BytesRef facetPrefix, final int initialSize) {
        super(groupField, facetField, facetPrefix);
        this.groupedFacetHits = new ArrayList<GroupedFacetHit>(initialSize);
        this.segmentGroupedFacetHits = new SentinelIntSet(initialSize, Integer.MIN_VALUE);
    }
    
    static class SV extends TermGroupFacetCollector
    {
        private SortedDocValues facetFieldTermsIndex;
        
        SV(final String groupField, final String facetField, final BytesRef facetPrefix, final int initialSize) {
            super(groupField, facetField, facetPrefix, initialSize);
        }
        
        public void collect(final int doc) throws IOException {
            final int facetOrd = this.facetFieldTermsIndex.getOrd(doc);
            if (facetOrd < this.startFacetOrd || facetOrd >= this.endFacetOrd) {
                return;
            }
            final int groupOrd = this.groupFieldTermsIndex.getOrd(doc);
            final int segmentGroupedFacetsIndex = groupOrd * (this.facetFieldTermsIndex.getValueCount() + 1) + facetOrd;
            if (this.segmentGroupedFacetHits.exists(segmentGroupedFacetsIndex)) {
                return;
            }
            ++this.segmentTotalCount;
            final int[] segmentFacetCounts = this.segmentFacetCounts;
            final int n = facetOrd + 1;
            ++segmentFacetCounts[n];
            this.segmentGroupedFacetHits.put(segmentGroupedFacetsIndex);
            BytesRef groupKey;
            if (groupOrd == -1) {
                groupKey = null;
            }
            else {
                groupKey = BytesRef.deepCopyOf(this.groupFieldTermsIndex.lookupOrd(groupOrd));
            }
            BytesRef facetKey;
            if (facetOrd == -1) {
                facetKey = null;
            }
            else {
                facetKey = BytesRef.deepCopyOf(this.facetFieldTermsIndex.lookupOrd(facetOrd));
            }
            this.groupedFacetHits.add(new GroupedFacetHit(groupKey, facetKey));
        }
        
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            if (this.segmentFacetCounts != null) {
                this.segmentResults.add(this.createSegmentResult());
            }
            this.groupFieldTermsIndex = DocValues.getSorted(context.reader(), this.groupField);
            this.facetFieldTermsIndex = DocValues.getSorted(context.reader(), this.facetField);
            this.segmentFacetCounts = new int[this.facetFieldTermsIndex.getValueCount() + 1];
            this.segmentTotalCount = 0;
            this.segmentGroupedFacetHits.clear();
            for (final GroupedFacetHit groupedFacetHit : this.groupedFacetHits) {
                final int facetOrd = (groupedFacetHit.facetValue == null) ? -1 : this.facetFieldTermsIndex.lookupTerm(groupedFacetHit.facetValue);
                if (groupedFacetHit.facetValue != null && facetOrd < 0) {
                    continue;
                }
                final int groupOrd = (groupedFacetHit.groupValue == null) ? -1 : this.groupFieldTermsIndex.lookupTerm(groupedFacetHit.groupValue);
                if (groupedFacetHit.groupValue != null && groupOrd < 0) {
                    continue;
                }
                final int segmentGroupedFacetsIndex = groupOrd * (this.facetFieldTermsIndex.getValueCount() + 1) + facetOrd;
                this.segmentGroupedFacetHits.put(segmentGroupedFacetsIndex);
            }
            if (this.facetPrefix != null) {
                this.startFacetOrd = this.facetFieldTermsIndex.lookupTerm(this.facetPrefix);
                if (this.startFacetOrd < 0) {
                    this.startFacetOrd = -this.startFacetOrd - 1;
                }
                final BytesRefBuilder facetEndPrefix = new BytesRefBuilder();
                facetEndPrefix.append(this.facetPrefix);
                facetEndPrefix.append(UnicodeUtil.BIG_TERM);
                this.endFacetOrd = this.facetFieldTermsIndex.lookupTerm(facetEndPrefix.get());
                assert this.endFacetOrd < 0;
                this.endFacetOrd = -this.endFacetOrd - 1;
            }
            else {
                this.startFacetOrd = -1;
                this.endFacetOrd = this.facetFieldTermsIndex.getValueCount();
            }
        }
        
        @Override
        protected SegmentResult createSegmentResult() throws IOException {
            return new SegmentResult(this.segmentFacetCounts, this.segmentTotalCount, this.facetFieldTermsIndex.termsEnum(), this.startFacetOrd, this.endFacetOrd);
        }
        
        private static class SegmentResult extends AbstractGroupFacetCollector.SegmentResult
        {
            final TermsEnum tenum;
            
            SegmentResult(final int[] counts, final int total, final TermsEnum tenum, final int startFacetOrd, final int endFacetOrd) throws IOException {
                super(counts, total - counts[0], counts[0], endFacetOrd + 1);
                this.tenum = tenum;
                this.mergePos = ((startFacetOrd == -1) ? 1 : (startFacetOrd + 1));
                if (this.mergePos < this.maxTermPos) {
                    assert tenum != null;
                    tenum.seekExact((startFacetOrd == -1) ? 0L : ((long)startFacetOrd));
                    this.mergeTerm = tenum.term();
                }
            }
            
            @Override
            protected void nextTerm() throws IOException {
                this.mergeTerm = this.tenum.next();
            }
        }
    }
    
    static class MV extends TermGroupFacetCollector
    {
        private SortedSetDocValues facetFieldDocTermOrds;
        private TermsEnum facetOrdTermsEnum;
        private int facetFieldNumTerms;
        
        MV(final String groupField, final String facetField, final BytesRef facetPrefix, final int initialSize) {
            super(groupField, facetField, facetPrefix, initialSize);
        }
        
        public void collect(final int doc) throws IOException {
            final int groupOrd = this.groupFieldTermsIndex.getOrd(doc);
            if (this.facetFieldNumTerms != 0) {
                this.facetFieldDocTermOrds.setDocument(doc);
                boolean empty = true;
                long ord;
                while ((ord = this.facetFieldDocTermOrds.nextOrd()) != -1L) {
                    this.process(groupOrd, (int)ord);
                    empty = false;
                }
                if (empty) {
                    this.process(groupOrd, this.facetFieldNumTerms);
                }
                return;
            }
            final int segmentGroupedFacetsIndex = groupOrd * (this.facetFieldNumTerms + 1);
            if (this.facetPrefix != null || this.segmentGroupedFacetHits.exists(segmentGroupedFacetsIndex)) {
                return;
            }
            ++this.segmentTotalCount;
            final int[] segmentFacetCounts = this.segmentFacetCounts;
            final int facetFieldNumTerms = this.facetFieldNumTerms;
            ++segmentFacetCounts[facetFieldNumTerms];
            this.segmentGroupedFacetHits.put(segmentGroupedFacetsIndex);
            BytesRef groupKey;
            if (groupOrd == -1) {
                groupKey = null;
            }
            else {
                groupKey = BytesRef.deepCopyOf(this.groupFieldTermsIndex.lookupOrd(groupOrd));
            }
            this.groupedFacetHits.add(new GroupedFacetHit(groupKey, null));
        }
        
        private void process(final int groupOrd, final int facetOrd) {
            if (facetOrd < this.startFacetOrd || facetOrd >= this.endFacetOrd) {
                return;
            }
            final int segmentGroupedFacetsIndex = groupOrd * (this.facetFieldNumTerms + 1) + facetOrd;
            if (this.segmentGroupedFacetHits.exists(segmentGroupedFacetsIndex)) {
                return;
            }
            ++this.segmentTotalCount;
            final int[] segmentFacetCounts = this.segmentFacetCounts;
            ++segmentFacetCounts[facetOrd];
            this.segmentGroupedFacetHits.put(segmentGroupedFacetsIndex);
            BytesRef groupKey;
            if (groupOrd == -1) {
                groupKey = null;
            }
            else {
                groupKey = BytesRef.deepCopyOf(this.groupFieldTermsIndex.lookupOrd(groupOrd));
            }
            BytesRef facetValue;
            if (facetOrd == this.facetFieldNumTerms) {
                facetValue = null;
            }
            else {
                facetValue = BytesRef.deepCopyOf(this.facetFieldDocTermOrds.lookupOrd((long)facetOrd));
            }
            this.groupedFacetHits.add(new GroupedFacetHit(groupKey, facetValue));
        }
        
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            if (this.segmentFacetCounts != null) {
                this.segmentResults.add(this.createSegmentResult());
            }
            this.groupFieldTermsIndex = DocValues.getSorted(context.reader(), this.groupField);
            this.facetFieldDocTermOrds = DocValues.getSortedSet(context.reader(), this.facetField);
            this.facetFieldNumTerms = (int)this.facetFieldDocTermOrds.getValueCount();
            if (this.facetFieldNumTerms == 0) {
                this.facetOrdTermsEnum = null;
            }
            else {
                this.facetOrdTermsEnum = this.facetFieldDocTermOrds.termsEnum();
            }
            this.segmentFacetCounts = new int[this.facetFieldNumTerms + 1];
            this.segmentTotalCount = 0;
            this.segmentGroupedFacetHits.clear();
            for (final GroupedFacetHit groupedFacetHit : this.groupedFacetHits) {
                final int groupOrd = (groupedFacetHit.groupValue == null) ? -1 : this.groupFieldTermsIndex.lookupTerm(groupedFacetHit.groupValue);
                if (groupedFacetHit.groupValue != null && groupOrd < 0) {
                    continue;
                }
                int facetOrd;
                if (groupedFacetHit.facetValue != null) {
                    if (this.facetOrdTermsEnum == null) {
                        continue;
                    }
                    if (!this.facetOrdTermsEnum.seekExact(groupedFacetHit.facetValue)) {
                        continue;
                    }
                    facetOrd = (int)this.facetOrdTermsEnum.ord();
                }
                else {
                    facetOrd = this.facetFieldNumTerms;
                }
                final int segmentGroupedFacetsIndex = groupOrd * (this.facetFieldNumTerms + 1) + facetOrd;
                this.segmentGroupedFacetHits.put(segmentGroupedFacetsIndex);
            }
            if (this.facetPrefix != null) {
                TermsEnum.SeekStatus seekStatus;
                if (this.facetOrdTermsEnum != null) {
                    seekStatus = this.facetOrdTermsEnum.seekCeil(this.facetPrefix);
                }
                else {
                    seekStatus = TermsEnum.SeekStatus.END;
                }
                if (seekStatus == TermsEnum.SeekStatus.END) {
                    this.startFacetOrd = 0;
                    this.endFacetOrd = 0;
                    return;
                }
                this.startFacetOrd = (int)this.facetOrdTermsEnum.ord();
                final BytesRefBuilder facetEndPrefix = new BytesRefBuilder();
                facetEndPrefix.append(this.facetPrefix);
                facetEndPrefix.append(UnicodeUtil.BIG_TERM);
                seekStatus = this.facetOrdTermsEnum.seekCeil(facetEndPrefix.get());
                if (seekStatus != TermsEnum.SeekStatus.END) {
                    this.endFacetOrd = (int)this.facetOrdTermsEnum.ord();
                }
                else {
                    this.endFacetOrd = this.facetFieldNumTerms;
                }
            }
            else {
                this.startFacetOrd = 0;
                this.endFacetOrd = this.facetFieldNumTerms + 1;
            }
        }
        
        @Override
        protected SegmentResult createSegmentResult() throws IOException {
            return new SegmentResult(this.segmentFacetCounts, this.segmentTotalCount, this.facetFieldNumTerms, this.facetOrdTermsEnum, this.startFacetOrd, this.endFacetOrd);
        }
        
        private static class SegmentResult extends AbstractGroupFacetCollector.SegmentResult
        {
            final TermsEnum tenum;
            
            SegmentResult(final int[] counts, final int total, final int missingCountIndex, final TermsEnum tenum, final int startFacetOrd, final int endFacetOrd) throws IOException {
                super(counts, total - counts[missingCountIndex], counts[missingCountIndex], (endFacetOrd == missingCountIndex + 1) ? missingCountIndex : endFacetOrd);
                this.tenum = tenum;
                this.mergePos = startFacetOrd;
                if (tenum != null) {
                    tenum.seekExact((long)this.mergePos);
                    this.mergeTerm = tenum.term();
                }
            }
            
            @Override
            protected void nextTerm() throws IOException {
                this.mergeTerm = this.tenum.next();
            }
        }
    }
}

package org.apache.lucene.search.grouping.term;

import org.apache.lucene.util.BytesRefBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.util.SentinelIntSet;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.LeafFieldComparator;
import java.util.Iterator;
import org.apache.lucene.index.DocValues;
import java.util.Collection;
import java.io.IOException;
import java.util.HashMap;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.util.BytesRef;
import java.util.Map;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.Sort;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.search.grouping.AbstractAllGroupHeadsCollector;

public abstract class TermAllGroupHeadsCollector<GH extends GroupHead<?>> extends AbstractAllGroupHeadsCollector<GH>
{
    private static final int DEFAULT_INITIAL_SIZE = 128;
    final String groupField;
    SortedDocValues groupIndex;
    LeafReaderContext readerContext;
    
    protected TermAllGroupHeadsCollector(final String groupField, final int numberOfSorts) {
        super(numberOfSorts);
        this.groupField = groupField;
    }
    
    public static AbstractAllGroupHeadsCollector<?> create(final String groupField, final Sort sortWithinGroup) {
        return create(groupField, sortWithinGroup, 128);
    }
    
    public static AbstractAllGroupHeadsCollector<?> create(final String groupField, final Sort sortWithinGroup, final int initialSize) {
        boolean sortAllScore = true;
        boolean sortAllFieldValue = true;
        for (final SortField sortField : sortWithinGroup.getSort()) {
            if (sortField.getType() == SortField.Type.SCORE) {
                sortAllFieldValue = false;
            }
            else {
                if (needGeneralImpl(sortField)) {
                    return new GeneralAllGroupHeadsCollector(groupField, sortWithinGroup);
                }
                sortAllScore = false;
            }
        }
        if (sortAllScore) {
            return new ScoreAllGroupHeadsCollector(groupField, sortWithinGroup, initialSize);
        }
        if (sortAllFieldValue) {
            return new OrdAllGroupHeadsCollector(groupField, sortWithinGroup, initialSize);
        }
        return new OrdScoreAllGroupHeadsCollector(groupField, sortWithinGroup, initialSize);
    }
    
    private static boolean needGeneralImpl(final SortField sortField) {
        final SortField.Type sortType = sortField.getType();
        return sortType != SortField.Type.STRING_VAL && sortType != SortField.Type.STRING && sortType != SortField.Type.SCORE;
    }
    
    static class GeneralAllGroupHeadsCollector extends TermAllGroupHeadsCollector<GroupHead>
    {
        private final Sort sortWithinGroup;
        private final Map<BytesRef, GroupHead> groups;
        Scorer scorer;
        
        GeneralAllGroupHeadsCollector(final String groupField, final Sort sortWithinGroup) {
            super(groupField, sortWithinGroup.getSort().length);
            this.sortWithinGroup = sortWithinGroup;
            this.groups = new HashMap<BytesRef, GroupHead>();
            final SortField[] sortFields = sortWithinGroup.getSort();
            for (int i = 0; i < sortFields.length; ++i) {
                this.reversed[i] = (sortFields[i].getReverse() ? -1 : 1);
            }
        }
        
        @Override
        protected void retrieveGroupHeadAndAddIfNotExist(final int doc) throws IOException {
            final int ord = this.groupIndex.getOrd(doc);
            BytesRef groupValue;
            if (ord == -1) {
                groupValue = null;
            }
            else {
                groupValue = this.groupIndex.lookupOrd(ord);
            }
            GroupHead groupHead = this.groups.get(groupValue);
            if (groupHead == null) {
                groupValue = ((groupValue == null) ? null : BytesRef.deepCopyOf(groupValue));
                groupHead = new GroupHead(groupValue, this.sortWithinGroup, doc);
                this.groups.put(groupValue, groupHead);
                this.temporalResult.stop = true;
            }
            else {
                this.temporalResult.stop = false;
            }
            this.temporalResult.groupHead = (GH)groupHead;
        }
        
        @Override
        protected Collection<GroupHead> getCollectedGroupHeads() {
            return this.groups.values();
        }
        
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            this.readerContext = context;
            this.groupIndex = DocValues.getSorted(context.reader(), this.groupField);
            for (final GroupHead groupHead : this.groups.values()) {
                for (int i = 0; i < groupHead.comparators.length; ++i) {
                    groupHead.leafComparators[i] = groupHead.comparators[i].getLeafComparator(context);
                }
            }
        }
        
        public boolean needsScores() {
            return this.sortWithinGroup.needsScores();
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
            this.scorer = scorer;
            for (final GroupHead groupHead : this.groups.values()) {
                for (final LeafFieldComparator comparator : groupHead.leafComparators) {
                    comparator.setScorer(scorer);
                }
            }
        }
        
        class GroupHead extends AbstractAllGroupHeadsCollector.GroupHead<BytesRef>
        {
            final FieldComparator[] comparators;
            final LeafFieldComparator[] leafComparators;
            
            GroupHead(final BytesRef groupValue, final Sort sort, final int doc) throws IOException {
                super(groupValue, doc + GeneralAllGroupHeadsCollector.this.readerContext.docBase);
                final SortField[] sortFields = sort.getSort();
                this.comparators = new FieldComparator[sortFields.length];
                this.leafComparators = new LeafFieldComparator[sortFields.length];
                for (int i = 0; i < sortFields.length; ++i) {
                    this.comparators[i] = sortFields[i].getComparator(1, i);
                    (this.leafComparators[i] = this.comparators[i].getLeafComparator(GeneralAllGroupHeadsCollector.this.readerContext)).setScorer(GeneralAllGroupHeadsCollector.this.scorer);
                    this.leafComparators[i].copy(0, doc);
                    this.leafComparators[i].setBottom(0);
                }
            }
            
            public int compare(final int compIDX, final int doc) throws IOException {
                return this.leafComparators[compIDX].compareBottom(doc);
            }
            
            public void updateDocHead(final int doc) throws IOException {
                for (final LeafFieldComparator comparator : this.leafComparators) {
                    comparator.copy(0, doc);
                    comparator.setBottom(0);
                }
                this.doc = doc + GeneralAllGroupHeadsCollector.this.readerContext.docBase;
            }
        }
    }
    
    static class OrdScoreAllGroupHeadsCollector extends TermAllGroupHeadsCollector<GroupHead>
    {
        private final SentinelIntSet ordSet;
        private final List<GroupHead> collectedGroups;
        final SortField[] fields;
        SortedDocValues[] sortsIndex;
        Scorer scorer;
        private GroupHead[] segmentGroupHeads;
        
        OrdScoreAllGroupHeadsCollector(final String groupField, final Sort sortWithinGroup, final int initialSize) {
            super(groupField, sortWithinGroup.getSort().length);
            this.ordSet = new SentinelIntSet(initialSize, -2);
            this.collectedGroups = new ArrayList<GroupHead>(initialSize);
            final SortField[] sortFields = sortWithinGroup.getSort();
            this.fields = new SortField[sortFields.length];
            this.sortsIndex = new SortedDocValues[sortFields.length];
            for (int i = 0; i < sortFields.length; ++i) {
                this.reversed[i] = (sortFields[i].getReverse() ? -1 : 1);
                this.fields[i] = sortFields[i];
            }
        }
        
        @Override
        protected Collection<GroupHead> getCollectedGroupHeads() {
            return this.collectedGroups;
        }
        
        public boolean needsScores() {
            return true;
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
            this.scorer = scorer;
        }
        
        @Override
        protected void retrieveGroupHeadAndAddIfNotExist(final int doc) throws IOException {
            final int key = this.groupIndex.getOrd(doc);
            GroupHead groupHead;
            if (!this.ordSet.exists(key)) {
                this.ordSet.put(key);
                BytesRef term;
                if (key == -1) {
                    term = null;
                }
                else {
                    term = BytesRef.deepCopyOf(this.groupIndex.lookupOrd(key));
                }
                groupHead = new GroupHead(doc, term);
                this.collectedGroups.add(groupHead);
                this.segmentGroupHeads[key + 1] = groupHead;
                this.temporalResult.stop = true;
            }
            else {
                this.temporalResult.stop = false;
                groupHead = this.segmentGroupHeads[key + 1];
            }
            this.temporalResult.groupHead = (GH)groupHead;
        }
        
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            this.readerContext = context;
            this.groupIndex = DocValues.getSorted(context.reader(), this.groupField);
            for (int i = 0; i < this.fields.length; ++i) {
                if (this.fields[i].getType() != SortField.Type.SCORE) {
                    this.sortsIndex[i] = DocValues.getSorted(context.reader(), this.fields[i].getField());
                }
            }
            this.ordSet.clear();
            this.segmentGroupHeads = new GroupHead[this.groupIndex.getValueCount() + 1];
            for (final GroupHead collectedGroup : this.collectedGroups) {
                int ord;
                if (collectedGroup.groupValue == null) {
                    ord = -1;
                }
                else {
                    ord = this.groupIndex.lookupTerm((BytesRef)collectedGroup.groupValue);
                }
                if (collectedGroup.groupValue == null || ord >= 0) {
                    this.ordSet.put(ord);
                    this.segmentGroupHeads[ord + 1] = collectedGroup;
                    for (int j = 0; j < this.sortsIndex.length; ++j) {
                        if (this.fields[j].getType() != SortField.Type.SCORE) {
                            int sortOrd;
                            if (collectedGroup.sortValues[j] == null) {
                                sortOrd = -1;
                            }
                            else {
                                sortOrd = this.sortsIndex[j].lookupTerm(collectedGroup.sortValues[j].get());
                            }
                            collectedGroup.sortOrds[j] = sortOrd;
                        }
                    }
                }
            }
        }
        
        class GroupHead extends AbstractAllGroupHeadsCollector.GroupHead<BytesRef>
        {
            BytesRefBuilder[] sortValues;
            int[] sortOrds;
            float[] scores;
            
            GroupHead(final int doc, final BytesRef groupValue) throws IOException {
                super(groupValue, doc + OrdScoreAllGroupHeadsCollector.this.readerContext.docBase);
                this.sortValues = new BytesRefBuilder[OrdScoreAllGroupHeadsCollector.this.sortsIndex.length];
                this.sortOrds = new int[OrdScoreAllGroupHeadsCollector.this.sortsIndex.length];
                this.scores = new float[OrdScoreAllGroupHeadsCollector.this.sortsIndex.length];
                for (int i = 0; i < OrdScoreAllGroupHeadsCollector.this.sortsIndex.length; ++i) {
                    if (OrdScoreAllGroupHeadsCollector.this.fields[i].getType() == SortField.Type.SCORE) {
                        this.scores[i] = OrdScoreAllGroupHeadsCollector.this.scorer.score();
                    }
                    else {
                        this.sortOrds[i] = OrdScoreAllGroupHeadsCollector.this.sortsIndex[i].getOrd(doc);
                        this.sortValues[i] = new BytesRefBuilder();
                        if (this.sortOrds[i] != -1) {
                            this.sortValues[i].copyBytes(OrdScoreAllGroupHeadsCollector.this.sortsIndex[i].get(doc));
                        }
                    }
                }
            }
            
            public int compare(final int compIDX, final int doc) throws IOException {
                if (OrdScoreAllGroupHeadsCollector.this.fields[compIDX].getType() == SortField.Type.SCORE) {
                    final float score = OrdScoreAllGroupHeadsCollector.this.scorer.score();
                    if (this.scores[compIDX] < score) {
                        return 1;
                    }
                    if (this.scores[compIDX] > score) {
                        return -1;
                    }
                    return 0;
                }
                else {
                    if (this.sortOrds[compIDX] < 0) {
                        final BytesRef term = OrdScoreAllGroupHeadsCollector.this.sortsIndex[compIDX].get(doc);
                        return this.sortValues[compIDX].get().compareTo(term);
                    }
                    return this.sortOrds[compIDX] - OrdScoreAllGroupHeadsCollector.this.sortsIndex[compIDX].getOrd(doc);
                }
            }
            
            public void updateDocHead(final int doc) throws IOException {
                for (int i = 0; i < OrdScoreAllGroupHeadsCollector.this.sortsIndex.length; ++i) {
                    if (OrdScoreAllGroupHeadsCollector.this.fields[i].getType() == SortField.Type.SCORE) {
                        this.scores[i] = OrdScoreAllGroupHeadsCollector.this.scorer.score();
                    }
                    else {
                        this.sortOrds[i] = OrdScoreAllGroupHeadsCollector.this.sortsIndex[i].getOrd(doc);
                        this.sortValues[i].copyBytes(OrdScoreAllGroupHeadsCollector.this.sortsIndex[i].get(doc));
                    }
                }
                this.doc = doc + OrdScoreAllGroupHeadsCollector.this.readerContext.docBase;
            }
        }
    }
    
    static class OrdAllGroupHeadsCollector extends TermAllGroupHeadsCollector<GroupHead>
    {
        private final SentinelIntSet ordSet;
        private final List<GroupHead> collectedGroups;
        private final SortField[] fields;
        SortedDocValues[] sortsIndex;
        GroupHead[] segmentGroupHeads;
        
        OrdAllGroupHeadsCollector(final String groupField, final Sort sortWithinGroup, final int initialSize) {
            super(groupField, sortWithinGroup.getSort().length);
            this.ordSet = new SentinelIntSet(initialSize, -2);
            this.collectedGroups = new ArrayList<GroupHead>(initialSize);
            final SortField[] sortFields = sortWithinGroup.getSort();
            this.fields = new SortField[sortFields.length];
            this.sortsIndex = new SortedDocValues[sortFields.length];
            for (int i = 0; i < sortFields.length; ++i) {
                this.reversed[i] = (sortFields[i].getReverse() ? -1 : 1);
                this.fields[i] = sortFields[i];
            }
        }
        
        @Override
        protected Collection<GroupHead> getCollectedGroupHeads() {
            return this.collectedGroups;
        }
        
        public boolean needsScores() {
            return false;
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
        }
        
        @Override
        protected void retrieveGroupHeadAndAddIfNotExist(final int doc) throws IOException {
            final int key = this.groupIndex.getOrd(doc);
            GroupHead groupHead;
            if (!this.ordSet.exists(key)) {
                this.ordSet.put(key);
                BytesRef term;
                if (key == -1) {
                    term = null;
                }
                else {
                    term = BytesRef.deepCopyOf(this.groupIndex.lookupOrd(key));
                }
                groupHead = new GroupHead(doc, term);
                this.collectedGroups.add(groupHead);
                this.segmentGroupHeads[key + 1] = groupHead;
                this.temporalResult.stop = true;
            }
            else {
                this.temporalResult.stop = false;
                groupHead = this.segmentGroupHeads[key + 1];
            }
            this.temporalResult.groupHead = (GH)groupHead;
        }
        
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            this.readerContext = context;
            this.groupIndex = DocValues.getSorted(context.reader(), this.groupField);
            for (int i = 0; i < this.fields.length; ++i) {
                this.sortsIndex[i] = DocValues.getSorted(context.reader(), this.fields[i].getField());
            }
            this.ordSet.clear();
            this.segmentGroupHeads = new GroupHead[this.groupIndex.getValueCount() + 1];
            for (final GroupHead collectedGroup : this.collectedGroups) {
                int groupOrd;
                if (collectedGroup.groupValue == null) {
                    groupOrd = -1;
                }
                else {
                    groupOrd = this.groupIndex.lookupTerm((BytesRef)collectedGroup.groupValue);
                }
                if (collectedGroup.groupValue == null || groupOrd >= 0) {
                    this.ordSet.put(groupOrd);
                    this.segmentGroupHeads[groupOrd + 1] = collectedGroup;
                    for (int j = 0; j < this.sortsIndex.length; ++j) {
                        int sortOrd;
                        if (collectedGroup.sortOrds[j] == -1) {
                            sortOrd = -1;
                        }
                        else {
                            sortOrd = this.sortsIndex[j].lookupTerm(collectedGroup.sortValues[j].get());
                        }
                        collectedGroup.sortOrds[j] = sortOrd;
                    }
                }
            }
        }
        
        class GroupHead extends AbstractAllGroupHeadsCollector.GroupHead<BytesRef>
        {
            BytesRefBuilder[] sortValues;
            int[] sortOrds;
            
            GroupHead(final int doc, final BytesRef groupValue) {
                super(groupValue, doc + OrdAllGroupHeadsCollector.this.readerContext.docBase);
                this.sortValues = new BytesRefBuilder[OrdAllGroupHeadsCollector.this.sortsIndex.length];
                this.sortOrds = new int[OrdAllGroupHeadsCollector.this.sortsIndex.length];
                for (int i = 0; i < OrdAllGroupHeadsCollector.this.sortsIndex.length; ++i) {
                    this.sortOrds[i] = OrdAllGroupHeadsCollector.this.sortsIndex[i].getOrd(doc);
                    (this.sortValues[i] = new BytesRefBuilder()).copyBytes(OrdAllGroupHeadsCollector.this.sortsIndex[i].get(doc));
                }
            }
            
            public int compare(final int compIDX, final int doc) throws IOException {
                if (this.sortOrds[compIDX] < 0) {
                    final BytesRef term = OrdAllGroupHeadsCollector.this.sortsIndex[compIDX].get(doc);
                    return this.sortValues[compIDX].get().compareTo(term);
                }
                return this.sortOrds[compIDX] - OrdAllGroupHeadsCollector.this.sortsIndex[compIDX].getOrd(doc);
            }
            
            public void updateDocHead(final int doc) throws IOException {
                for (int i = 0; i < OrdAllGroupHeadsCollector.this.sortsIndex.length; ++i) {
                    this.sortOrds[i] = OrdAllGroupHeadsCollector.this.sortsIndex[i].getOrd(doc);
                    this.sortValues[i].copyBytes(OrdAllGroupHeadsCollector.this.sortsIndex[i].get(doc));
                }
                this.doc = doc + OrdAllGroupHeadsCollector.this.readerContext.docBase;
            }
        }
    }
    
    static class ScoreAllGroupHeadsCollector extends TermAllGroupHeadsCollector<GroupHead>
    {
        final SentinelIntSet ordSet;
        final List<GroupHead> collectedGroups;
        final SortField[] fields;
        Scorer scorer;
        GroupHead[] segmentGroupHeads;
        
        ScoreAllGroupHeadsCollector(final String groupField, final Sort sortWithinGroup, final int initialSize) {
            super(groupField, sortWithinGroup.getSort().length);
            this.ordSet = new SentinelIntSet(initialSize, -2);
            this.collectedGroups = new ArrayList<GroupHead>(initialSize);
            final SortField[] sortFields = sortWithinGroup.getSort();
            this.fields = new SortField[sortFields.length];
            for (int i = 0; i < sortFields.length; ++i) {
                this.reversed[i] = (sortFields[i].getReverse() ? -1 : 1);
                this.fields[i] = sortFields[i];
            }
        }
        
        @Override
        protected Collection<GroupHead> getCollectedGroupHeads() {
            return this.collectedGroups;
        }
        
        public boolean needsScores() {
            return true;
        }
        
        public void setScorer(final Scorer scorer) throws IOException {
            this.scorer = scorer;
        }
        
        @Override
        protected void retrieveGroupHeadAndAddIfNotExist(final int doc) throws IOException {
            final int key = this.groupIndex.getOrd(doc);
            GroupHead groupHead;
            if (!this.ordSet.exists(key)) {
                this.ordSet.put(key);
                BytesRef term;
                if (key == -1) {
                    term = null;
                }
                else {
                    term = BytesRef.deepCopyOf(this.groupIndex.lookupOrd(key));
                }
                groupHead = new GroupHead(doc, term);
                this.collectedGroups.add(groupHead);
                this.segmentGroupHeads[key + 1] = groupHead;
                this.temporalResult.stop = true;
            }
            else {
                this.temporalResult.stop = false;
                groupHead = this.segmentGroupHeads[key + 1];
            }
            this.temporalResult.groupHead = (GH)groupHead;
        }
        
        protected void doSetNextReader(final LeafReaderContext context) throws IOException {
            this.readerContext = context;
            this.groupIndex = DocValues.getSorted(context.reader(), this.groupField);
            this.ordSet.clear();
            this.segmentGroupHeads = new GroupHead[this.groupIndex.getValueCount() + 1];
            for (final GroupHead collectedGroup : this.collectedGroups) {
                int ord;
                if (collectedGroup.groupValue == null) {
                    ord = -1;
                }
                else {
                    ord = this.groupIndex.lookupTerm((BytesRef)collectedGroup.groupValue);
                }
                if (collectedGroup.groupValue == null || ord >= 0) {
                    this.ordSet.put(ord);
                    this.segmentGroupHeads[ord + 1] = collectedGroup;
                }
            }
        }
        
        class GroupHead extends AbstractAllGroupHeadsCollector.GroupHead<BytesRef>
        {
            float[] scores;
            
            GroupHead(final int doc, final BytesRef groupValue) throws IOException {
                super(groupValue, doc + ScoreAllGroupHeadsCollector.this.readerContext.docBase);
                this.scores = new float[ScoreAllGroupHeadsCollector.this.fields.length];
                final float score = ScoreAllGroupHeadsCollector.this.scorer.score();
                for (int i = 0; i < this.scores.length; ++i) {
                    this.scores[i] = score;
                }
            }
            
            public int compare(final int compIDX, final int doc) throws IOException {
                final float score = ScoreAllGroupHeadsCollector.this.scorer.score();
                if (this.scores[compIDX] < score) {
                    return 1;
                }
                if (this.scores[compIDX] > score) {
                    return -1;
                }
                return 0;
            }
            
            public void updateDocHead(final int doc) throws IOException {
                final float score = ScoreAllGroupHeadsCollector.this.scorer.score();
                for (int i = 0; i < this.scores.length; ++i) {
                    this.scores[i] = score;
                }
                this.doc = doc + ScoreAllGroupHeadsCollector.this.readerContext.docBase;
            }
        }
    }
}

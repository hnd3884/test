package org.apache.lucene.search.grouping.function;

import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.LeafFieldComparator;
import java.util.Iterator;
import java.util.Collection;
import java.io.IOException;
import org.apache.lucene.search.SortField;
import java.util.HashMap;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.search.Sort;
import org.apache.lucene.util.mutable.MutableValue;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.grouping.AbstractAllGroupHeadsCollector;

public class FunctionAllGroupHeadsCollector extends AbstractAllGroupHeadsCollector<GroupHead>
{
    private final ValueSource groupBy;
    private final Map<?, ?> vsContext;
    private final Map<MutableValue, GroupHead> groups;
    private final Sort sortWithinGroup;
    private FunctionValues.ValueFiller filler;
    private MutableValue mval;
    private LeafReaderContext readerContext;
    private Scorer scorer;
    
    public FunctionAllGroupHeadsCollector(final ValueSource groupBy, final Map<?, ?> vsContext, final Sort sortWithinGroup) {
        super(sortWithinGroup.getSort().length);
        this.groups = new HashMap<MutableValue, GroupHead>();
        this.sortWithinGroup = sortWithinGroup;
        this.groupBy = groupBy;
        this.vsContext = vsContext;
        final SortField[] sortFields = sortWithinGroup.getSort();
        for (int i = 0; i < sortFields.length; ++i) {
            this.reversed[i] = (sortFields[i].getReverse() ? -1 : 1);
        }
    }
    
    @Override
    protected void retrieveGroupHeadAndAddIfNotExist(final int doc) throws IOException {
        this.filler.fillValue(doc);
        GroupHead groupHead = this.groups.get(this.mval);
        if (groupHead == null) {
            final MutableValue groupValue = this.mval.duplicate();
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
    
    public void setScorer(final Scorer scorer) throws IOException {
        this.scorer = scorer;
        for (final GroupHead groupHead : this.groups.values()) {
            for (final LeafFieldComparator comparator : groupHead.leafComparators) {
                comparator.setScorer(scorer);
            }
        }
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        this.readerContext = context;
        final FunctionValues values = this.groupBy.getValues((Map)this.vsContext, context);
        this.filler = values.getValueFiller();
        this.mval = this.filler.getValue();
        for (final GroupHead groupHead : this.groups.values()) {
            for (int i = 0; i < groupHead.comparators.length; ++i) {
                groupHead.leafComparators[i] = groupHead.comparators[i].getLeafComparator(context);
            }
        }
    }
    
    public boolean needsScores() {
        return this.sortWithinGroup.needsScores();
    }
    
    public class GroupHead extends AbstractAllGroupHeadsCollector.GroupHead<MutableValue>
    {
        final FieldComparator<?>[] comparators;
        final LeafFieldComparator[] leafComparators;
        
        private GroupHead(final MutableValue groupValue, final Sort sort, final int doc) throws IOException {
            super(groupValue, doc + FunctionAllGroupHeadsCollector.this.readerContext.docBase);
            final SortField[] sortFields = sort.getSort();
            this.comparators = (FieldComparator<?>[])new FieldComparator[sortFields.length];
            this.leafComparators = new LeafFieldComparator[sortFields.length];
            for (int i = 0; i < sortFields.length; ++i) {
                this.comparators[i] = (FieldComparator<?>)sortFields[i].getComparator(1, i);
                (this.leafComparators[i] = this.comparators[i].getLeafComparator(FunctionAllGroupHeadsCollector.this.readerContext)).setScorer(FunctionAllGroupHeadsCollector.this.scorer);
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
            this.doc = doc + FunctionAllGroupHeadsCollector.this.readerContext.docBase;
        }
    }
}

package org.apache.lucene.search.grouping.function;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.grouping.SearchGroup;
import java.util.Collection;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.search.grouping.AbstractSecondPassGroupingCollector;

public class FunctionSecondPassGroupingCollector extends AbstractSecondPassGroupingCollector<MutableValue>
{
    private final ValueSource groupByVS;
    private final Map<?, ?> vsContext;
    private FunctionValues.ValueFiller filler;
    private MutableValue mval;
    
    public FunctionSecondPassGroupingCollector(final Collection<SearchGroup<MutableValue>> searchGroups, final Sort groupSort, final Sort withinGroupSort, final int maxDocsPerGroup, final boolean getScores, final boolean getMaxScores, final boolean fillSortFields, final ValueSource groupByVS, final Map<?, ?> vsContext) throws IOException {
        super(searchGroups, groupSort, withinGroupSort, maxDocsPerGroup, getScores, getMaxScores, fillSortFields);
        this.groupByVS = groupByVS;
        this.vsContext = vsContext;
    }
    
    @Override
    protected SearchGroupDocs<MutableValue> retrieveGroup(final int doc) throws IOException {
        this.filler.fillValue(doc);
        return (SearchGroupDocs)this.groupMap.get(this.mval);
    }
    
    @Override
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        super.doSetNextReader(readerContext);
        final FunctionValues values = this.groupByVS.getValues((Map)this.vsContext, readerContext);
        this.filler = values.getValueFiller();
        this.mval = this.filler.getValue();
    }
}

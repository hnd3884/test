package org.apache.lucene.search.grouping.function;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.apache.lucene.search.grouping.SearchGroup;
import java.util.Collection;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.queries.function.ValueSource;
import java.util.Map;
import org.apache.lucene.search.grouping.AbstractDistinctValuesCollector;

public class FunctionDistinctValuesCollector extends AbstractDistinctValuesCollector<GroupCount>
{
    private final Map<?, ?> vsContext;
    private final ValueSource groupSource;
    private final ValueSource countSource;
    private final Map<MutableValue, GroupCount> groupMap;
    private FunctionValues.ValueFiller groupFiller;
    private FunctionValues.ValueFiller countFiller;
    private MutableValue groupMval;
    private MutableValue countMval;
    
    public FunctionDistinctValuesCollector(final Map<?, ?> vsContext, final ValueSource groupSource, final ValueSource countSource, final Collection<SearchGroup<MutableValue>> groups) {
        this.vsContext = vsContext;
        this.groupSource = groupSource;
        this.countSource = countSource;
        this.groupMap = new LinkedHashMap<MutableValue, GroupCount>();
        for (final SearchGroup<MutableValue> group : groups) {
            this.groupMap.put(group.groupValue, new GroupCount(group.groupValue));
        }
    }
    
    @Override
    public List<GroupCount> getGroups() {
        return new ArrayList<GroupCount>(this.groupMap.values());
    }
    
    public void collect(final int doc) throws IOException {
        this.groupFiller.fillValue(doc);
        final GroupCount groupCount = this.groupMap.get(this.groupMval);
        if (groupCount != null) {
            this.countFiller.fillValue(doc);
            groupCount.uniqueValues.add((GROUP_VALUE_TYPE)this.countMval.duplicate());
        }
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        FunctionValues values = this.groupSource.getValues((Map)this.vsContext, context);
        this.groupFiller = values.getValueFiller();
        this.groupMval = this.groupFiller.getValue();
        values = this.countSource.getValues((Map)this.vsContext, context);
        this.countFiller = values.getValueFiller();
        this.countMval = this.countFiller.getValue();
    }
    
    public static class GroupCount extends AbstractDistinctValuesCollector.GroupCount<MutableValue>
    {
        GroupCount(final MutableValue groupValue) {
            super(groupValue);
        }
    }
}

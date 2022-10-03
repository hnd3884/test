package org.apache.lucene.search.grouping.function;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.SortedSet;
import org.apache.lucene.queries.function.ValueSource;
import java.util.Map;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.search.grouping.AbstractAllGroupsCollector;

public class FunctionAllGroupsCollector extends AbstractAllGroupsCollector<MutableValue>
{
    private final Map<?, ?> vsContext;
    private final ValueSource groupBy;
    private final SortedSet<MutableValue> groups;
    private FunctionValues.ValueFiller filler;
    private MutableValue mval;
    
    public FunctionAllGroupsCollector(final ValueSource groupBy, final Map<?, ?> vsContext) {
        this.groups = new TreeSet<MutableValue>();
        this.vsContext = vsContext;
        this.groupBy = groupBy;
    }
    
    @Override
    public Collection<MutableValue> getGroups() {
        return this.groups;
    }
    
    public void collect(final int doc) throws IOException {
        this.filler.fillValue(doc);
        if (!this.groups.contains(this.mval)) {
            this.groups.add(this.mval.duplicate());
        }
    }
    
    protected void doSetNextReader(final LeafReaderContext context) throws IOException {
        final FunctionValues values = this.groupBy.getValues((Map)this.vsContext, context);
        this.filler = values.getValueFiller();
        this.mval = this.filler.getValue();
    }
}

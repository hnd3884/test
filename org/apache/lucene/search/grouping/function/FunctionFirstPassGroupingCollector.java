package org.apache.lucene.search.grouping.function;

import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.queries.function.FunctionValues;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.search.grouping.AbstractFirstPassGroupingCollector;

public class FunctionFirstPassGroupingCollector extends AbstractFirstPassGroupingCollector<MutableValue>
{
    private final ValueSource groupByVS;
    private final Map<?, ?> vsContext;
    private FunctionValues.ValueFiller filler;
    private MutableValue mval;
    
    public FunctionFirstPassGroupingCollector(final ValueSource groupByVS, final Map<?, ?> vsContext, final Sort groupSort, final int topNGroups) throws IOException {
        super(groupSort, topNGroups);
        this.groupByVS = groupByVS;
        this.vsContext = vsContext;
    }
    
    @Override
    protected MutableValue getDocGroupValue(final int doc) {
        this.filler.fillValue(doc);
        return this.mval;
    }
    
    @Override
    protected MutableValue copyDocGroupValue(final MutableValue groupValue, final MutableValue reuse) {
        if (reuse != null) {
            reuse.copy(groupValue);
            return reuse;
        }
        return groupValue.duplicate();
    }
    
    @Override
    protected void doSetNextReader(final LeafReaderContext readerContext) throws IOException {
        super.doSetNextReader(readerContext);
        final FunctionValues values = this.groupByVS.getValues((Map)this.vsContext, readerContext);
        this.filler = values.getValueFiller();
        this.mval = this.filler.getValue();
    }
}

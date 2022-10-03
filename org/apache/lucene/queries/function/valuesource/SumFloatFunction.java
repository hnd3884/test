package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public class SumFloatFunction extends MultiFloatFunction
{
    public SumFloatFunction(final ValueSource[] sources) {
        super(sources);
    }
    
    @Override
    protected String name() {
        return "sum";
    }
    
    @Override
    protected float func(final int doc, final FunctionValues[] valsArr) {
        float val = 0.0f;
        for (final FunctionValues vals : valsArr) {
            val += vals.floatVal(doc);
        }
        return val;
    }
}

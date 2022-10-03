package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public class MaxFloatFunction extends MultiFloatFunction
{
    public MaxFloatFunction(final ValueSource[] sources) {
        super(sources);
    }
    
    @Override
    protected String name() {
        return "max";
    }
    
    @Override
    protected float func(final int doc, final FunctionValues[] valsArr) {
        if (!this.exists(doc, valsArr)) {
            return 0.0f;
        }
        float val = Float.NEGATIVE_INFINITY;
        for (final FunctionValues vals : valsArr) {
            if (vals.exists(doc)) {
                val = Math.max(vals.floatVal(doc), val);
            }
        }
        return val;
    }
    
    @Override
    protected boolean exists(final int doc, final FunctionValues[] valsArr) {
        return MultiFunction.anyExists(doc, valsArr);
    }
}

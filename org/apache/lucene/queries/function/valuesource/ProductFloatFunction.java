package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public class ProductFloatFunction extends MultiFloatFunction
{
    public ProductFloatFunction(final ValueSource[] sources) {
        super(sources);
    }
    
    @Override
    protected String name() {
        return "product";
    }
    
    @Override
    protected float func(final int doc, final FunctionValues[] valsArr) {
        float val = 1.0f;
        for (final FunctionValues vals : valsArr) {
            val *= vals.floatVal(doc);
        }
        return val;
    }
}

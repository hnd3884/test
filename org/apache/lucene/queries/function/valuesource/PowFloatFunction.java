package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public class PowFloatFunction extends DualFloatFunction
{
    public PowFloatFunction(final ValueSource a, final ValueSource b) {
        super(a, b);
    }
    
    @Override
    protected String name() {
        return "pow";
    }
    
    @Override
    protected float func(final int doc, final FunctionValues aVals, final FunctionValues bVals) {
        return (float)Math.pow(aVals.floatVal(doc), bVals.floatVal(doc));
    }
}

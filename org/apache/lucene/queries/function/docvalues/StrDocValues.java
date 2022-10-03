package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueStr;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class StrDocValues extends FunctionValues
{
    protected final ValueSource vs;
    
    public StrDocValues(final ValueSource vs) {
        this.vs = vs;
    }
    
    @Override
    public abstract String strVal(final int p0);
    
    @Override
    public Object objectVal(final int doc) {
        return this.exists(doc) ? this.strVal(doc) : null;
    }
    
    @Override
    public boolean boolVal(final int doc) {
        return this.exists(doc);
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + "='" + this.strVal(doc) + "'";
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueStr mval = new MutableValueStr();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.exists = StrDocValues.this.bytesVal(doc, this.mval.value);
            }
        };
    }
}

package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueBool;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class BoolDocValues extends FunctionValues
{
    protected final ValueSource vs;
    
    public BoolDocValues(final ValueSource vs) {
        this.vs = vs;
    }
    
    @Override
    public abstract boolean boolVal(final int p0);
    
    @Override
    public byte byteVal(final int doc) {
        return (byte)(this.boolVal(doc) ? 1 : 0);
    }
    
    @Override
    public short shortVal(final int doc) {
        return (short)(this.boolVal(doc) ? 1 : 0);
    }
    
    @Override
    public float floatVal(final int doc) {
        return this.boolVal(doc) ? 1.0f : 0.0f;
    }
    
    @Override
    public int intVal(final int doc) {
        return this.boolVal(doc) ? 1 : 0;
    }
    
    @Override
    public long longVal(final int doc) {
        return this.boolVal(doc) ? 1 : 0;
    }
    
    @Override
    public double doubleVal(final int doc) {
        return this.boolVal(doc) ? 1.0 : 0.0;
    }
    
    @Override
    public String strVal(final int doc) {
        return Boolean.toString(this.boolVal(doc));
    }
    
    @Override
    public Object objectVal(final int doc) {
        return this.exists(doc) ? Boolean.valueOf(this.boolVal(doc)) : null;
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueBool mval = new MutableValueBool();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.value = BoolDocValues.this.boolVal(doc);
                this.mval.exists = BoolDocValues.this.exists(doc);
            }
        };
    }
}

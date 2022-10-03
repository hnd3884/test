package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class FloatDocValues extends FunctionValues
{
    protected final ValueSource vs;
    
    public FloatDocValues(final ValueSource vs) {
        this.vs = vs;
    }
    
    @Override
    public byte byteVal(final int doc) {
        return (byte)this.floatVal(doc);
    }
    
    @Override
    public short shortVal(final int doc) {
        return (short)this.floatVal(doc);
    }
    
    @Override
    public abstract float floatVal(final int p0);
    
    @Override
    public int intVal(final int doc) {
        return (int)this.floatVal(doc);
    }
    
    @Override
    public long longVal(final int doc) {
        return (long)this.floatVal(doc);
    }
    
    @Override
    public double doubleVal(final int doc) {
        return this.floatVal(doc);
    }
    
    @Override
    public String strVal(final int doc) {
        return Float.toString(this.floatVal(doc));
    }
    
    @Override
    public Object objectVal(final int doc) {
        return this.exists(doc) ? Float.valueOf(this.floatVal(doc)) : null;
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueFloat mval = new MutableValueFloat();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.value = FloatDocValues.this.floatVal(doc);
                this.mval.exists = FloatDocValues.this.exists(doc);
            }
        };
    }
}

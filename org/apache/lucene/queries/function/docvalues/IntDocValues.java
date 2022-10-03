package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueInt;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class IntDocValues extends FunctionValues
{
    protected final ValueSource vs;
    
    public IntDocValues(final ValueSource vs) {
        this.vs = vs;
    }
    
    @Override
    public byte byteVal(final int doc) {
        return (byte)this.intVal(doc);
    }
    
    @Override
    public short shortVal(final int doc) {
        return (short)this.intVal(doc);
    }
    
    @Override
    public float floatVal(final int doc) {
        return (float)this.intVal(doc);
    }
    
    @Override
    public abstract int intVal(final int p0);
    
    @Override
    public long longVal(final int doc) {
        return this.intVal(doc);
    }
    
    @Override
    public double doubleVal(final int doc) {
        return this.intVal(doc);
    }
    
    @Override
    public String strVal(final int doc) {
        return Integer.toString(this.intVal(doc));
    }
    
    @Override
    public Object objectVal(final int doc) {
        return this.exists(doc) ? Integer.valueOf(this.intVal(doc)) : null;
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }
    
    @Override
    public ValueSourceScorer getRangeScorer(final IndexReader reader, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
        int lower;
        if (lowerVal == null) {
            lower = Integer.MIN_VALUE;
        }
        else {
            lower = Integer.parseInt(lowerVal);
            if (!includeLower && lower < Integer.MAX_VALUE) {
                ++lower;
            }
        }
        int upper;
        if (upperVal == null) {
            upper = Integer.MAX_VALUE;
        }
        else {
            upper = Integer.parseInt(upperVal);
            if (!includeUpper && upper > Integer.MIN_VALUE) {
                --upper;
            }
        }
        final int ll = lower;
        final int uu = upper;
        return new ValueSourceScorer(reader, this) {
            @Override
            public boolean matches(final int doc) {
                final int val = IntDocValues.this.intVal(doc);
                return val >= ll && val <= uu;
            }
        };
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueInt mval = new MutableValueInt();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.value = IntDocValues.this.intVal(doc);
                this.mval.exists = IntDocValues.this.exists(doc);
            }
        };
    }
}

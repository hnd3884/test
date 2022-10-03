package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueLong;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class LongDocValues extends FunctionValues
{
    protected final ValueSource vs;
    
    public LongDocValues(final ValueSource vs) {
        this.vs = vs;
    }
    
    @Override
    public byte byteVal(final int doc) {
        return (byte)this.longVal(doc);
    }
    
    @Override
    public short shortVal(final int doc) {
        return (short)this.longVal(doc);
    }
    
    @Override
    public float floatVal(final int doc) {
        return (float)this.longVal(doc);
    }
    
    @Override
    public int intVal(final int doc) {
        return (int)this.longVal(doc);
    }
    
    @Override
    public abstract long longVal(final int p0);
    
    @Override
    public double doubleVal(final int doc) {
        return (double)this.longVal(doc);
    }
    
    @Override
    public boolean boolVal(final int doc) {
        return this.longVal(doc) != 0L;
    }
    
    @Override
    public String strVal(final int doc) {
        return Long.toString(this.longVal(doc));
    }
    
    @Override
    public Object objectVal(final int doc) {
        return this.exists(doc) ? Long.valueOf(this.longVal(doc)) : null;
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }
    
    protected long externalToLong(final String extVal) {
        return Long.parseLong(extVal);
    }
    
    @Override
    public ValueSourceScorer getRangeScorer(final IndexReader reader, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
        long lower;
        if (lowerVal == null) {
            lower = Long.MIN_VALUE;
        }
        else {
            lower = this.externalToLong(lowerVal);
            if (!includeLower && lower < Long.MAX_VALUE) {
                ++lower;
            }
        }
        long upper;
        if (upperVal == null) {
            upper = Long.MAX_VALUE;
        }
        else {
            upper = this.externalToLong(upperVal);
            if (!includeUpper && upper > Long.MIN_VALUE) {
                --upper;
            }
        }
        final long ll = lower;
        final long uu = upper;
        return new ValueSourceScorer(reader, this) {
            @Override
            public boolean matches(final int doc) {
                final long val = LongDocValues.this.longVal(doc);
                return val >= ll && val <= uu;
            }
        };
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueLong mval = new MutableValueLong();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.value = LongDocValues.this.longVal(doc);
                this.mval.exists = LongDocValues.this.exists(doc);
            }
        };
    }
}

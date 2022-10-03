package org.apache.lucene.queries.function.docvalues;

import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueDouble;
import org.apache.lucene.queries.function.ValueSourceScorer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.FunctionValues;

public abstract class DoubleDocValues extends FunctionValues
{
    protected final ValueSource vs;
    
    public DoubleDocValues(final ValueSource vs) {
        this.vs = vs;
    }
    
    @Override
    public byte byteVal(final int doc) {
        return (byte)this.doubleVal(doc);
    }
    
    @Override
    public short shortVal(final int doc) {
        return (short)this.doubleVal(doc);
    }
    
    @Override
    public float floatVal(final int doc) {
        return (float)this.doubleVal(doc);
    }
    
    @Override
    public int intVal(final int doc) {
        return (int)this.doubleVal(doc);
    }
    
    @Override
    public long longVal(final int doc) {
        return (long)this.doubleVal(doc);
    }
    
    @Override
    public boolean boolVal(final int doc) {
        return this.doubleVal(doc) != 0.0;
    }
    
    @Override
    public abstract double doubleVal(final int p0);
    
    @Override
    public String strVal(final int doc) {
        return Double.toString(this.doubleVal(doc));
    }
    
    @Override
    public Object objectVal(final int doc) {
        return this.exists(doc) ? Double.valueOf(this.doubleVal(doc)) : null;
    }
    
    @Override
    public String toString(final int doc) {
        return this.vs.description() + '=' + this.strVal(doc);
    }
    
    @Override
    public ValueSourceScorer getRangeScorer(final IndexReader reader, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
        double lower;
        if (lowerVal == null) {
            lower = Double.NEGATIVE_INFINITY;
        }
        else {
            lower = Double.parseDouble(lowerVal);
        }
        double upper;
        if (upperVal == null) {
            upper = Double.POSITIVE_INFINITY;
        }
        else {
            upper = Double.parseDouble(upperVal);
        }
        final double l = lower;
        final double u = upper;
        if (includeLower && includeUpper) {
            return new ValueSourceScorer(reader, this) {
                @Override
                public boolean matches(final int doc) {
                    final double docVal = DoubleDocValues.this.doubleVal(doc);
                    return docVal >= l && docVal <= u;
                }
            };
        }
        if (includeLower && !includeUpper) {
            return new ValueSourceScorer(reader, this) {
                @Override
                public boolean matches(final int doc) {
                    final double docVal = DoubleDocValues.this.doubleVal(doc);
                    return docVal >= l && docVal < u;
                }
            };
        }
        if (!includeLower && includeUpper) {
            return new ValueSourceScorer(reader, this) {
                @Override
                public boolean matches(final int doc) {
                    final double docVal = DoubleDocValues.this.doubleVal(doc);
                    return docVal > l && docVal <= u;
                }
            };
        }
        return new ValueSourceScorer(reader, this) {
            @Override
            public boolean matches(final int doc) {
                final double docVal = DoubleDocValues.this.doubleVal(doc);
                return docVal > l && docVal < u;
            }
        };
    }
    
    @Override
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueDouble mval = new MutableValueDouble();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.value = DoubleDocValues.this.doubleVal(doc);
                this.mval.exists = DoubleDocValues.this.exists(doc);
            }
        };
    }
}

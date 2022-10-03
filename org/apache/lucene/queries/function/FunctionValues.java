package org.apache.lucene.queries.function;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;
import org.apache.lucene.util.BytesRefBuilder;

public abstract class FunctionValues
{
    public byte byteVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public short shortVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public float floatVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public int intVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public long longVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public double doubleVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public String strVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public boolean boolVal(final int doc) {
        return this.intVal(doc) != 0;
    }
    
    public boolean bytesVal(final int doc, final BytesRefBuilder target) {
        final String s = this.strVal(doc);
        if (s == null) {
            target.clear();
            return false;
        }
        target.copyChars((CharSequence)s);
        return true;
    }
    
    public Object objectVal(final int doc) {
        return this.floatVal(doc);
    }
    
    public boolean exists(final int doc) {
        return true;
    }
    
    public int ordVal(final int doc) {
        throw new UnsupportedOperationException();
    }
    
    public int numOrd() {
        throw new UnsupportedOperationException();
    }
    
    public abstract String toString(final int p0);
    
    public ValueFiller getValueFiller() {
        return new ValueFiller() {
            private final MutableValueFloat mval = new MutableValueFloat();
            
            @Override
            public MutableValue getValue() {
                return (MutableValue)this.mval;
            }
            
            @Override
            public void fillValue(final int doc) {
                this.mval.value = FunctionValues.this.floatVal(doc);
            }
        };
    }
    
    public void byteVal(final int doc, final byte[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public void shortVal(final int doc, final short[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public void floatVal(final int doc, final float[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public void intVal(final int doc, final int[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public void longVal(final int doc, final long[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public void doubleVal(final int doc, final double[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public void strVal(final int doc, final String[] vals) {
        throw new UnsupportedOperationException();
    }
    
    public Explanation explain(final int doc) {
        return Explanation.match(this.floatVal(doc), this.toString(doc), new Explanation[0]);
    }
    
    public ValueSourceScorer getScorer(final IndexReader reader) {
        return new ValueSourceScorer(reader, this) {
            @Override
            public boolean matches(final int doc) {
                return true;
            }
        };
    }
    
    public ValueSourceScorer getRangeScorer(final IndexReader reader, final String lowerVal, final String upperVal, final boolean includeLower, final boolean includeUpper) {
        float lower;
        if (lowerVal == null) {
            lower = Float.NEGATIVE_INFINITY;
        }
        else {
            lower = Float.parseFloat(lowerVal);
        }
        float upper;
        if (upperVal == null) {
            upper = Float.POSITIVE_INFINITY;
        }
        else {
            upper = Float.parseFloat(upperVal);
        }
        final float l = lower;
        final float u = upper;
        if (includeLower && includeUpper) {
            return new ValueSourceScorer(reader, this) {
                @Override
                public boolean matches(final int doc) {
                    final float docVal = FunctionValues.this.floatVal(doc);
                    return docVal >= l && docVal <= u;
                }
            };
        }
        if (includeLower && !includeUpper) {
            return new ValueSourceScorer(reader, this) {
                @Override
                public boolean matches(final int doc) {
                    final float docVal = FunctionValues.this.floatVal(doc);
                    return docVal >= l && docVal < u;
                }
            };
        }
        if (!includeLower && includeUpper) {
            return new ValueSourceScorer(reader, this) {
                @Override
                public boolean matches(final int doc) {
                    final float docVal = FunctionValues.this.floatVal(doc);
                    return docVal > l && docVal <= u;
                }
            };
        }
        return new ValueSourceScorer(reader, this) {
            @Override
            public boolean matches(final int doc) {
                final float docVal = FunctionValues.this.floatVal(doc);
                return docVal > l && docVal < u;
            }
        };
    }
    
    public abstract static class ValueFiller
    {
        public abstract MutableValue getValue();
        
        public abstract void fillValue(final int p0);
    }
}

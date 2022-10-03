package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.IntDocValues;

class ConstIntDocValues extends IntDocValues
{
    final int ival;
    final float fval;
    final double dval;
    final long lval;
    final String sval;
    final ValueSource parent;
    
    ConstIntDocValues(final int val, final ValueSource parent) {
        super(parent);
        this.ival = val;
        this.fval = (float)val;
        this.dval = val;
        this.lval = val;
        this.sval = Integer.toString(val);
        this.parent = parent;
    }
    
    @Override
    public float floatVal(final int doc) {
        return this.fval;
    }
    
    @Override
    public int intVal(final int doc) {
        return this.ival;
    }
    
    @Override
    public long longVal(final int doc) {
        return this.lval;
    }
    
    @Override
    public double doubleVal(final int doc) {
        return this.dval;
    }
    
    @Override
    public String strVal(final int doc) {
        return this.sval;
    }
    
    @Override
    public String toString(final int doc) {
        return this.parent.description() + '=' + this.sval;
    }
}

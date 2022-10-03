package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;

public class ConstValueSource extends ConstNumberSource
{
    final float constant;
    private final double dv;
    
    public ConstValueSource(final float constant) {
        this.constant = constant;
        this.dv = constant;
    }
    
    @Override
    public String description() {
        return "const(" + this.constant + ")";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return ConstValueSource.this.constant;
            }
            
            @Override
            public int intVal(final int doc) {
                return (int)ConstValueSource.this.constant;
            }
            
            @Override
            public long longVal(final int doc) {
                return (long)ConstValueSource.this.constant;
            }
            
            @Override
            public double doubleVal(final int doc) {
                return ConstValueSource.this.dv;
            }
            
            @Override
            public String toString(final int doc) {
                return ConstValueSource.this.description();
            }
            
            @Override
            public Object objectVal(final int doc) {
                return ConstValueSource.this.constant;
            }
            
            @Override
            public boolean boolVal(final int doc) {
                return ConstValueSource.this.constant != 0.0f;
            }
        };
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.constant) * 31;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ConstValueSource)) {
            return false;
        }
        final ConstValueSource other = (ConstValueSource)o;
        return this.constant == other.constant;
    }
    
    @Override
    public int getInt() {
        return (int)this.constant;
    }
    
    @Override
    public long getLong() {
        return (long)this.constant;
    }
    
    @Override
    public float getFloat() {
        return this.constant;
    }
    
    @Override
    public double getDouble() {
        return this.dv;
    }
    
    @Override
    public Number getNumber() {
        return this.constant;
    }
    
    @Override
    public boolean getBool() {
        return this.constant != 0.0f;
    }
}

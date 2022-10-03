package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public abstract class SimpleFloatFunction extends SingleFunction
{
    public SimpleFloatFunction(final ValueSource source) {
        super(source);
    }
    
    protected abstract float func(final int p0, final FunctionValues p1);
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return SimpleFloatFunction.this.func(doc, vals);
            }
            
            @Override
            public String toString(final int doc) {
                return SimpleFloatFunction.this.name() + '(' + vals.toString(doc) + ')';
            }
        };
    }
}

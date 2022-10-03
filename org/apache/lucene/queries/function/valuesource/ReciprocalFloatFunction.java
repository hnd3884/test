package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class ReciprocalFloatFunction extends ValueSource
{
    protected final ValueSource source;
    protected final float m;
    protected final float a;
    protected final float b;
    
    public ReciprocalFloatFunction(final ValueSource source, final float m, final float a, final float b) {
        this.source = source;
        this.m = m;
        this.a = a;
        this.b = b;
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return ReciprocalFloatFunction.this.a / (ReciprocalFloatFunction.this.m * vals.floatVal(doc) + ReciprocalFloatFunction.this.b);
            }
            
            @Override
            public boolean exists(final int doc) {
                return vals.exists(doc);
            }
            
            @Override
            public String toString(final int doc) {
                return Float.toString(ReciprocalFloatFunction.this.a) + "/(" + ReciprocalFloatFunction.this.m + "*float(" + vals.toString(doc) + ')' + '+' + ReciprocalFloatFunction.this.b + ')';
            }
        };
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
    
    @Override
    public String description() {
        return Float.toString(this.a) + "/(" + this.m + "*float(" + this.source.description() + ")" + "+" + this.b + ')';
    }
    
    @Override
    public int hashCode() {
        int h = Float.floatToIntBits(this.a) + Float.floatToIntBits(this.m);
        h ^= (h << 13 | h >>> 20);
        return h + Float.floatToIntBits(this.b) + this.source.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (ReciprocalFloatFunction.class != o.getClass()) {
            return false;
        }
        final ReciprocalFloatFunction other = (ReciprocalFloatFunction)o;
        return this.m == other.m && this.a == other.a && this.b == other.b && this.source.equals(other.source);
    }
}

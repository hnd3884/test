package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public abstract class DualFloatFunction extends ValueSource
{
    protected final ValueSource a;
    protected final ValueSource b;
    
    public DualFloatFunction(final ValueSource a, final ValueSource b) {
        this.a = a;
        this.b = b;
    }
    
    protected abstract String name();
    
    protected abstract float func(final int p0, final FunctionValues p1, final FunctionValues p2);
    
    @Override
    public String description() {
        return this.name() + "(" + this.a.description() + "," + this.b.description() + ")";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues aVals = this.a.getValues(context, readerContext);
        final FunctionValues bVals = this.b.getValues(context, readerContext);
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return DualFloatFunction.this.func(doc, aVals, bVals);
            }
            
            @Override
            public boolean exists(final int doc) {
                return MultiFunction.allExists(doc, aVals, bVals);
            }
            
            @Override
            public String toString(final int doc) {
                return DualFloatFunction.this.name() + '(' + aVals.toString(doc) + ',' + bVals.toString(doc) + ')';
            }
        };
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.a.createWeight(context, searcher);
        this.b.createWeight(context, searcher);
    }
    
    @Override
    public int hashCode() {
        int h = this.a.hashCode();
        h ^= (h << 13 | h >>> 20);
        h += this.b.hashCode();
        h ^= (h << 23 | h >>> 10);
        h += this.name().hashCode();
        return h;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final DualFloatFunction other = (DualFloatFunction)o;
        return this.a.equals(other.a) && this.b.equals(other.b);
    }
}

package org.apache.lucene.queries.function.valuesource;

import java.util.Arrays;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public abstract class MultiFloatFunction extends ValueSource
{
    protected final ValueSource[] sources;
    
    public MultiFloatFunction(final ValueSource[] sources) {
        this.sources = sources;
    }
    
    protected abstract String name();
    
    protected abstract float func(final int p0, final FunctionValues[] p1);
    
    protected boolean exists(final int doc, final FunctionValues[] valsArr) {
        return MultiFunction.allExists(doc, valsArr);
    }
    
    @Override
    public String description() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.name()).append('(');
        boolean firstTime = true;
        for (final ValueSource source : this.sources) {
            if (firstTime) {
                firstTime = false;
            }
            else {
                sb.append(',');
            }
            sb.append(source);
        }
        sb.append(')');
        return sb.toString();
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues[] valsArr = new FunctionValues[this.sources.length];
        for (int i = 0; i < this.sources.length; ++i) {
            valsArr[i] = this.sources[i].getValues(context, readerContext);
        }
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return MultiFloatFunction.this.func(doc, valsArr);
            }
            
            @Override
            public boolean exists(final int doc) {
                return MultiFloatFunction.this.exists(doc, valsArr);
            }
            
            @Override
            public String toString(final int doc) {
                return MultiFunction.toString(MultiFloatFunction.this.name(), valsArr, doc);
            }
        };
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        for (final ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.sources) + this.name().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final MultiFloatFunction other = (MultiFloatFunction)o;
        return this.name().equals(other.name()) && Arrays.equals(this.sources, other.sources);
    }
}

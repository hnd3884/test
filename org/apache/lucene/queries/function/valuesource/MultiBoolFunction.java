package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import java.util.Iterator;
import org.apache.lucene.queries.function.docvalues.BoolDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import java.util.List;

public abstract class MultiBoolFunction extends BoolFunction
{
    protected final List<ValueSource> sources;
    
    public MultiBoolFunction(final List<ValueSource> sources) {
        this.sources = sources;
    }
    
    protected abstract String name();
    
    protected abstract boolean func(final int p0, final FunctionValues[] p1);
    
    @Override
    public BoolDocValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues[] vals = new FunctionValues[this.sources.size()];
        int i = 0;
        for (final ValueSource source : this.sources) {
            vals[i++] = source.getValues(context, readerContext);
        }
        return new BoolDocValues(this) {
            @Override
            public boolean boolVal(final int doc) {
                return MultiBoolFunction.this.func(doc, vals);
            }
            
            @Override
            public String toString(final int doc) {
                final StringBuilder sb = new StringBuilder(MultiBoolFunction.this.name());
                sb.append('(');
                boolean first = true;
                for (final FunctionValues dv : vals) {
                    if (first) {
                        first = false;
                    }
                    else {
                        sb.append(',');
                    }
                    sb.append(dv.toString(doc));
                }
                return sb.toString();
            }
        };
    }
    
    @Override
    public String description() {
        final StringBuilder sb = new StringBuilder(this.name());
        sb.append('(');
        boolean first = true;
        for (final ValueSource source : this.sources) {
            if (first) {
                first = false;
            }
            else {
                sb.append(',');
            }
            sb.append(source.description());
        }
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.sources.hashCode() + this.name().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final MultiBoolFunction other = (MultiBoolFunction)o;
        return this.sources.equals(other.sources);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        for (final ValueSource source : this.sources) {
            source.createWeight(context, searcher);
        }
    }
}

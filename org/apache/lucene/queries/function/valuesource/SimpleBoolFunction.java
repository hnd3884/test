package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.docvalues.BoolDocValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;

public abstract class SimpleBoolFunction extends BoolFunction
{
    protected final ValueSource source;
    
    public SimpleBoolFunction(final ValueSource source) {
        this.source = source;
    }
    
    protected abstract String name();
    
    protected abstract boolean func(final int p0, final FunctionValues p1);
    
    @Override
    public BoolDocValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final FunctionValues vals = this.source.getValues(context, readerContext);
        return new BoolDocValues(this) {
            @Override
            public boolean boolVal(final int doc) {
                return SimpleBoolFunction.this.func(doc, vals);
            }
            
            @Override
            public String toString(final int doc) {
                return SimpleBoolFunction.this.name() + '(' + vals.toString(doc) + ')';
            }
        };
    }
    
    @Override
    public String description() {
        return this.name() + '(' + this.source.description() + ')';
    }
    
    @Override
    public int hashCode() {
        return this.source.hashCode() + this.name().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final SimpleBoolFunction other = (SimpleBoolFunction)o;
        return this.source.equals(other.source);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
}

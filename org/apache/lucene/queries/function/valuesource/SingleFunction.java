package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public abstract class SingleFunction extends ValueSource
{
    protected final ValueSource source;
    
    public SingleFunction(final ValueSource source) {
        this.source = source;
    }
    
    protected abstract String name();
    
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
        final SingleFunction other = (SingleFunction)o;
        return this.name().equals(other.name()) && this.source.equals(other.source);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        this.source.createWeight(context, searcher);
    }
}

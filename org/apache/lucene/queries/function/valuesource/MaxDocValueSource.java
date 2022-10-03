package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class MaxDocValueSource extends ValueSource
{
    public String name() {
        return "maxdoc";
    }
    
    @Override
    public String description() {
        return this.name() + "()";
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        context.put("searcher", searcher);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final IndexSearcher searcher = context.get("searcher");
        return new ConstIntDocValues(searcher.getIndexReader().maxDoc(), this);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.getClass() == o.getClass();
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }
}

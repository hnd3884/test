package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.Weight;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.search.Query;
import org.apache.lucene.queries.function.ValueSource;

public class QueryValueSource extends ValueSource
{
    final Query q;
    final float defVal;
    
    public QueryValueSource(final Query q, final float defVal) {
        this.q = q;
        this.defVal = defVal;
    }
    
    public Query getQuery() {
        return this.q;
    }
    
    public float getDefaultValue() {
        return this.defVal;
    }
    
    @Override
    public String description() {
        return "query(" + this.q + ",def=" + this.defVal + ")";
    }
    
    @Override
    public FunctionValues getValues(final Map fcontext, final LeafReaderContext readerContext) throws IOException {
        return new QueryDocValues(this, readerContext, fcontext);
    }
    
    @Override
    public int hashCode() {
        return this.q.hashCode() * 29;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (QueryValueSource.class != o.getClass()) {
            return false;
        }
        final QueryValueSource other = (QueryValueSource)o;
        return this.q.equals((Object)other.q) && this.defVal == other.defVal;
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        final Weight w = searcher.createNormalizedWeight(this.q, true);
        context.put(this, w);
    }
}

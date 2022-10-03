package org.apache.lucene.queries.function.valuesource;

import java.util.Iterator;
import org.apache.lucene.queries.function.docvalues.LongDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.queries.function.ValueSource;

public class TotalTermFreqValueSource extends ValueSource
{
    protected final String field;
    protected final String indexedField;
    protected final String val;
    protected final BytesRef indexedBytes;
    
    public TotalTermFreqValueSource(final String field, final String val, final String indexedField, final BytesRef indexedBytes) {
        this.field = field;
        this.val = val;
        this.indexedField = indexedField;
        this.indexedBytes = indexedBytes;
    }
    
    public String name() {
        return "totaltermfreq";
    }
    
    @Override
    public String description() {
        return this.name() + '(' + this.field + ',' + this.val + ')';
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        return context.get(this);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        long totalTermFreq = 0L;
        for (final LeafReaderContext readerContext : searcher.getTopReaderContext().leaves()) {
            final long val = readerContext.reader().totalTermFreq(new Term(this.indexedField, this.indexedBytes));
            if (val == -1L) {
                totalTermFreq = -1L;
                break;
            }
            totalTermFreq += val;
        }
        final long ttf = totalTermFreq;
        context.put(this, new LongDocValues(this) {
            @Override
            public long longVal(final int doc) {
                return ttf;
            }
        });
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.indexedField.hashCode() * 29 + this.indexedBytes.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final TotalTermFreqValueSource other = (TotalTermFreqValueSource)o;
        return this.indexedField.equals(other.indexedField) && this.indexedBytes.equals((Object)other.indexedBytes);
    }
}

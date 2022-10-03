package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.index.Terms;
import java.util.Iterator;
import org.apache.lucene.queries.function.docvalues.LongDocValues;
import org.apache.lucene.search.IndexSearcher;
import java.io.IOException;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class SumTotalTermFreqValueSource extends ValueSource
{
    protected final String indexedField;
    
    public SumTotalTermFreqValueSource(final String indexedField) {
        this.indexedField = indexedField;
    }
    
    public String name() {
        return "sumtotaltermfreq";
    }
    
    @Override
    public String description() {
        return this.name() + '(' + this.indexedField + ')';
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        return context.get(this);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        long sumTotalTermFreq = 0L;
        for (final LeafReaderContext readerContext : searcher.getTopReaderContext().leaves()) {
            final Terms terms = readerContext.reader().terms(this.indexedField);
            if (terms == null) {
                continue;
            }
            final long v = terms.getSumTotalTermFreq();
            if (v == -1L) {
                sumTotalTermFreq = -1L;
                break;
            }
            sumTotalTermFreq += v;
        }
        final long ttf = sumTotalTermFreq;
        context.put(this, new LongDocValues(this) {
            @Override
            public long longVal(final int doc) {
                return ttf;
            }
        });
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.indexedField.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.getClass() != o.getClass()) {
            return false;
        }
        final SumTotalTermFreqValueSource other = (SumTotalTermFreqValueSource)o;
        return this.indexedField.equals(other.indexedField);
    }
}

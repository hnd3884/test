package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.io.IOException;
import org.apache.lucene.search.IndexSearcher;
import java.util.Map;
import org.apache.lucene.queries.function.ValueSource;

public class NormValueSource extends ValueSource
{
    protected final String field;
    
    public NormValueSource(final String field) {
        this.field = field;
    }
    
    public String name() {
        return "norm";
    }
    
    @Override
    public String description() {
        return this.name() + '(' + this.field + ')';
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        context.put("searcher", searcher);
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final IndexSearcher searcher = context.get("searcher");
        final TFIDFSimilarity similarity = IDFValueSource.asTFIDF(searcher.getSimilarity(true), this.field);
        if (similarity == null) {
            throw new UnsupportedOperationException("requires a TFIDFSimilarity (such as DefaultSimilarity)");
        }
        final NumericDocValues norms = readerContext.reader().getNormValues(this.field);
        if (norms == null) {
            return new ConstDoubleDocValues(0.0, this);
        }
        return new FloatDocValues(this) {
            @Override
            public float floatVal(final int doc) {
                return similarity.decodeNormValue(norms.get(doc));
            }
        };
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.getClass() == o.getClass() && this.field.equals(((NormValueSource)o).field);
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() + this.field.hashCode();
    }
}

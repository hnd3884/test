package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.search.similarities.PerFieldSimilarityWrapper;
import org.apache.lucene.search.similarities.Similarity;
import java.io.IOException;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.util.BytesRef;

public class IDFValueSource extends DocFreqValueSource
{
    public IDFValueSource(final String field, final String val, final String indexedField, final BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }
    
    @Override
    public String name() {
        return "idf";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final IndexSearcher searcher = context.get("searcher");
        final TFIDFSimilarity sim = asTFIDF(searcher.getSimilarity(true), this.field);
        if (sim == null) {
            throw new UnsupportedOperationException("requires a TFIDFSimilarity (such as DefaultSimilarity)");
        }
        final int docfreq = searcher.getIndexReader().docFreq(new Term(this.indexedField, this.indexedBytes));
        final float idf = sim.idf((long)docfreq, (long)searcher.getIndexReader().maxDoc());
        return new ConstDoubleDocValues(idf, this);
    }
    
    static TFIDFSimilarity asTFIDF(Similarity sim, final String field) {
        while (sim instanceof PerFieldSimilarityWrapper) {
            sim = ((PerFieldSimilarityWrapper)sim).get(field);
        }
        if (sim instanceof TFIDFSimilarity) {
            return (TFIDFSimilarity)sim;
        }
        return null;
    }
}

package org.apache.lucene.queries.function.valuesource;

import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.queries.function.ValueSource;

public class DocFreqValueSource extends ValueSource
{
    protected final String field;
    protected final String indexedField;
    protected final String val;
    protected final BytesRef indexedBytes;
    
    public DocFreqValueSource(final String field, final String val, final String indexedField, final BytesRef indexedBytes) {
        this.field = field;
        this.val = val;
        this.indexedField = indexedField;
        this.indexedBytes = indexedBytes;
    }
    
    public String name() {
        return "docfreq";
    }
    
    @Override
    public String description() {
        return this.name() + '(' + this.field + ',' + this.val + ')';
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final IndexSearcher searcher = context.get("searcher");
        final int docfreq = searcher.getIndexReader().docFreq(new Term(this.indexedField, this.indexedBytes));
        return new ConstIntDocValues(docfreq, this);
    }
    
    @Override
    public void createWeight(final Map context, final IndexSearcher searcher) throws IOException {
        context.put("searcher", searcher);
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
        final DocFreqValueSource other = (DocFreqValueSource)o;
        return this.indexedField.equals(other.indexedField) && this.indexedBytes.equals((Object)other.indexedBytes);
    }
}

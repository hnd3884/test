package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.TermsEnum;
import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.util.BytesRef;

public class TFValueSource extends TermFreqValueSource
{
    public TFValueSource(final String field, final String val, final String indexedField, final BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }
    
    @Override
    public String name() {
        return "tf";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final Fields fields = readerContext.reader().fields();
        final Terms terms = fields.terms(this.indexedField);
        final IndexSearcher searcher = context.get("searcher");
        final TFIDFSimilarity similarity = IDFValueSource.asTFIDF(searcher.getSimilarity(true), this.indexedField);
        if (similarity == null) {
            throw new UnsupportedOperationException("requires a TFIDFSimilarity (such as DefaultSimilarity)");
        }
        return new FloatDocValues(this) {
            PostingsEnum docs;
            int atDoc;
            int lastDocRequested = -1;
            
            {
                this.reset();
            }
            
            public void reset() throws IOException {
                if (terms != null) {
                    final TermsEnum termsEnum = terms.iterator();
                    if (termsEnum.seekExact(TFValueSource.this.indexedBytes)) {
                        this.docs = termsEnum.postings((PostingsEnum)null);
                    }
                    else {
                        this.docs = null;
                    }
                }
                else {
                    this.docs = null;
                }
                if (this.docs == null) {
                    this.docs = new PostingsEnum() {
                        public int freq() {
                            return 0;
                        }
                        
                        public int nextPosition() throws IOException {
                            return -1;
                        }
                        
                        public int startOffset() throws IOException {
                            return -1;
                        }
                        
                        public int endOffset() throws IOException {
                            return -1;
                        }
                        
                        public BytesRef getPayload() throws IOException {
                            return null;
                        }
                        
                        public int docID() {
                            return Integer.MAX_VALUE;
                        }
                        
                        public int nextDoc() {
                            return Integer.MAX_VALUE;
                        }
                        
                        public int advance(final int target) {
                            return Integer.MAX_VALUE;
                        }
                        
                        public long cost() {
                            return 0L;
                        }
                    };
                }
                this.atDoc = -1;
            }
            
            @Override
            public float floatVal(final int doc) {
                try {
                    if (doc < this.lastDocRequested) {
                        this.reset();
                    }
                    if (this.atDoc < (this.lastDocRequested = doc)) {
                        this.atDoc = this.docs.advance(doc);
                    }
                    if (this.atDoc > doc) {
                        return similarity.tf(0.0f);
                    }
                    return similarity.tf((float)this.docs.freq());
                }
                catch (final IOException e) {
                    throw new RuntimeException("caught exception in function " + TFValueSource.this.description() + " : doc=" + doc, e);
                }
            }
        };
    }
}

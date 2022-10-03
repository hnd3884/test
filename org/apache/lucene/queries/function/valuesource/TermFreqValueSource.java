package org.apache.lucene.queries.function.valuesource;

import org.apache.lucene.index.Fields;
import org.apache.lucene.index.TermsEnum;
import java.io.IOException;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.queries.function.docvalues.IntDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.index.LeafReaderContext;
import java.util.Map;
import org.apache.lucene.util.BytesRef;

public class TermFreqValueSource extends DocFreqValueSource
{
    public TermFreqValueSource(final String field, final String val, final String indexedField, final BytesRef indexedBytes) {
        super(field, val, indexedField, indexedBytes);
    }
    
    @Override
    public String name() {
        return "termfreq";
    }
    
    @Override
    public FunctionValues getValues(final Map context, final LeafReaderContext readerContext) throws IOException {
        final Fields fields = readerContext.reader().fields();
        final Terms terms = fields.terms(this.indexedField);
        return new IntDocValues(this) {
            PostingsEnum docs;
            int atDoc;
            int lastDocRequested = -1;
            
            {
                this.reset();
            }
            
            public void reset() throws IOException {
                if (terms != null) {
                    final TermsEnum termsEnum = terms.iterator();
                    if (termsEnum.seekExact(TermFreqValueSource.this.indexedBytes)) {
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
                            throw new UnsupportedOperationException();
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
            public int intVal(final int doc) {
                try {
                    if (doc < this.lastDocRequested) {
                        this.reset();
                    }
                    if (this.atDoc < (this.lastDocRequested = doc)) {
                        this.atDoc = this.docs.advance(doc);
                    }
                    if (this.atDoc > doc) {
                        return 0;
                    }
                    return this.docs.freq();
                }
                catch (final IOException e) {
                    throw new RuntimeException("caught exception in function " + TermFreqValueSource.this.description() + " : doc=" + doc, e);
                }
            }
        };
    }
}

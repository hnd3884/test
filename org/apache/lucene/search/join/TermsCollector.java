package org.apache.lucene.search.join;

import org.apache.lucene.index.BinaryDocValues;
import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.util.BytesRefHash;

abstract class TermsCollector<DV> extends DocValuesTermsCollector<DV>
{
    final BytesRefHash collectorTerms;
    
    TermsCollector(final Function<DV> docValuesCall) {
        super(docValuesCall);
        this.collectorTerms = new BytesRefHash();
    }
    
    public BytesRefHash getCollectorTerms() {
        return this.collectorTerms;
    }
    
    static TermsCollector<?> create(final String field, final boolean multipleValuesPerDocument) {
        return (TermsCollector<?>)(multipleValuesPerDocument ? new MV(DocValuesTermsCollector.sortedSetDocValues(field)) : new SV(DocValuesTermsCollector.binaryDocValues(field)));
    }
    
    public boolean needsScores() {
        return false;
    }
    
    static class MV extends TermsCollector<SortedSetDocValues>
    {
        MV(final Function<SortedSetDocValues> docValuesCall) {
            super(docValuesCall);
        }
        
        public void collect(final int doc) throws IOException {
            ((SortedSetDocValues)this.docValues).setDocument(doc);
            long ord;
            while ((ord = ((SortedSetDocValues)this.docValues).nextOrd()) != -1L) {
                final BytesRef term = ((SortedSetDocValues)this.docValues).lookupOrd(ord);
                this.collectorTerms.add(term);
            }
        }
    }
    
    static class SV extends TermsCollector<BinaryDocValues>
    {
        SV(final Function<BinaryDocValues> docValuesCall) {
            super(docValuesCall);
        }
        
        public void collect(final int doc) throws IOException {
            final BytesRef term = ((BinaryDocValues)this.docValues).get(doc);
            this.collectorTerms.add(term);
        }
    }
}

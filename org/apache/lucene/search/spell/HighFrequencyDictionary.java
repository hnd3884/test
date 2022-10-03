package org.apache.lucene.search.spell;

import java.util.Set;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRefBuilder;
import java.io.IOException;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.index.IndexReader;

public class HighFrequencyDictionary implements Dictionary
{
    private IndexReader reader;
    private String field;
    private float thresh;
    
    public HighFrequencyDictionary(final IndexReader reader, final String field, final float thresh) {
        this.reader = reader;
        this.field = field;
        this.thresh = thresh;
    }
    
    @Override
    public final InputIterator getEntryIterator() throws IOException {
        return new HighFrequencyIterator();
    }
    
    final class HighFrequencyIterator implements InputIterator
    {
        private final BytesRefBuilder spare;
        private final TermsEnum termsEnum;
        private int minNumDocs;
        private long freq;
        
        HighFrequencyIterator() throws IOException {
            this.spare = new BytesRefBuilder();
            final Terms terms = MultiFields.getTerms(HighFrequencyDictionary.this.reader, HighFrequencyDictionary.this.field);
            if (terms != null) {
                this.termsEnum = terms.iterator();
            }
            else {
                this.termsEnum = null;
            }
            this.minNumDocs = (int)(HighFrequencyDictionary.this.thresh * HighFrequencyDictionary.this.reader.numDocs());
        }
        
        private boolean isFrequent(final int freq) {
            return freq >= this.minNumDocs;
        }
        
        @Override
        public long weight() {
            return this.freq;
        }
        
        public BytesRef next() throws IOException {
            if (this.termsEnum != null) {
                BytesRef next;
                while ((next = this.termsEnum.next()) != null) {
                    if (this.isFrequent(this.termsEnum.docFreq())) {
                        this.freq = this.termsEnum.docFreq();
                        this.spare.copyBytes(next);
                        return this.spare.get();
                    }
                }
            }
            return null;
        }
        
        @Override
        public BytesRef payload() {
            return null;
        }
        
        @Override
        public boolean hasPayloads() {
            return false;
        }
        
        @Override
        public Set<BytesRef> contexts() {
            return null;
        }
        
        @Override
        public boolean hasContexts() {
            return false;
        }
    }
}

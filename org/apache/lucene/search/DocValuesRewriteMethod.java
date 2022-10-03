package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.util.LongBitSet;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.index.SortedSetDocValues;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.util.Bits;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.IndexReader;

public final class DocValuesRewriteMethod extends MultiTermQuery.RewriteMethod
{
    @Override
    public Query rewrite(final IndexReader reader, final MultiTermQuery query) {
        return new ConstantScoreQuery(new MultiTermQueryDocValuesWrapper(query));
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() == obj.getClass());
    }
    
    @Override
    public int hashCode() {
        return 641;
    }
    
    static class MultiTermQueryDocValuesWrapper extends Query
    {
        protected final MultiTermQuery query;
        
        protected MultiTermQueryDocValuesWrapper(final MultiTermQuery query) {
            this.query = query;
        }
        
        @Override
        public String toString(final String field) {
            return this.query.toString(field);
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (!super.equals(o)) {
                return false;
            }
            final MultiTermQueryDocValuesWrapper that = (MultiTermQueryDocValuesWrapper)o;
            return this.query.equals(that.query);
        }
        
        @Override
        public final int hashCode() {
            return 31 * super.hashCode() + this.query.hashCode();
        }
        
        public final String getField() {
            return this.query.getField();
        }
        
        @Override
        public Weight createWeight(final IndexSearcher searcher, final boolean needsScores) throws IOException {
            return new RandomAccessWeight(this) {
                @Override
                protected Bits getMatchingDocs(final LeafReaderContext context) throws IOException {
                    final SortedSetDocValues fcsi = DocValues.getSortedSet(context.reader(), MultiTermQueryDocValuesWrapper.this.query.field);
                    final TermsEnum termsEnum = MultiTermQueryDocValuesWrapper.this.query.getTermsEnum(new Terms() {
                        @Override
                        public TermsEnum iterator() {
                            return fcsi.termsEnum();
                        }
                        
                        @Override
                        public long getSumTotalTermFreq() {
                            return -1L;
                        }
                        
                        @Override
                        public long getSumDocFreq() {
                            return -1L;
                        }
                        
                        @Override
                        public int getDocCount() {
                            return -1;
                        }
                        
                        @Override
                        public long size() {
                            return -1L;
                        }
                        
                        @Override
                        public boolean hasFreqs() {
                            return false;
                        }
                        
                        @Override
                        public boolean hasOffsets() {
                            return false;
                        }
                        
                        @Override
                        public boolean hasPositions() {
                            return false;
                        }
                        
                        @Override
                        public boolean hasPayloads() {
                            return false;
                        }
                    });
                    assert termsEnum != null;
                    if (termsEnum.next() == null) {
                        return null;
                    }
                    final LongBitSet termSet = new LongBitSet(fcsi.getValueCount());
                    do {
                        final long ord = termsEnum.ord();
                        if (ord >= 0L) {
                            termSet.set(ord);
                        }
                    } while (termsEnum.next() != null);
                    return new Bits() {
                        @Override
                        public boolean get(final int doc) {
                            fcsi.setDocument(doc);
                            for (long ord = fcsi.nextOrd(); ord != -1L; ord = fcsi.nextOrd()) {
                                if (termSet.get(ord)) {
                                    return true;
                                }
                            }
                            return false;
                        }
                        
                        @Override
                        public int length() {
                            return context.reader().maxDoc();
                        }
                    };
                }
            };
        }
    }
}

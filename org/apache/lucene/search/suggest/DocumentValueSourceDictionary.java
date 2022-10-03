package org.apache.lucene.search.suggest;

import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.document.Document;
import java.util.Map;
import java.util.HashMap;
import org.apache.lucene.index.LeafReaderContext;
import java.util.List;
import org.apache.lucene.queries.function.FunctionValues;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.function.ValueSource;

public class DocumentValueSourceDictionary extends DocumentDictionary
{
    private final ValueSource weightsValueSource;
    
    public DocumentValueSourceDictionary(final IndexReader reader, final String field, final ValueSource weightsValueSource, final String payload, final String contexts) {
        super(reader, field, null, payload, contexts);
        this.weightsValueSource = weightsValueSource;
    }
    
    public DocumentValueSourceDictionary(final IndexReader reader, final String field, final ValueSource weightsValueSource, final String payload) {
        super(reader, field, null, payload);
        this.weightsValueSource = weightsValueSource;
    }
    
    public DocumentValueSourceDictionary(final IndexReader reader, final String field, final ValueSource weightsValueSource) {
        super(reader, field, null, null);
        this.weightsValueSource = weightsValueSource;
    }
    
    @Override
    public InputIterator getEntryIterator() throws IOException {
        return new DocumentValueSourceInputIterator(this.payloadField != null, this.contextsField != null);
    }
    
    final class DocumentValueSourceInputIterator extends DocumentInputIterator
    {
        private FunctionValues currentWeightValues;
        private final List<LeafReaderContext> leaves;
        private final int[] starts;
        private int currentLeafIndex;
        
        public DocumentValueSourceInputIterator(final boolean hasPayloads, final boolean hasContexts) throws IOException {
            super(hasPayloads, hasContexts);
            this.currentLeafIndex = 0;
            this.leaves = DocumentValueSourceDictionary.this.reader.leaves();
            this.starts = new int[this.leaves.size() + 1];
            for (int i = 0; i < this.leaves.size(); ++i) {
                this.starts[i] = this.leaves.get(i).docBase;
            }
            this.starts[this.leaves.size()] = DocumentValueSourceDictionary.this.reader.maxDoc();
            this.currentWeightValues = ((this.leaves.size() > 0) ? DocumentValueSourceDictionary.this.weightsValueSource.getValues((Map)new HashMap(), (LeafReaderContext)this.leaves.get(this.currentLeafIndex)) : null);
        }
        
        @Override
        protected long getWeight(final Document doc, final int docId) {
            if (this.currentWeightValues == null) {
                return 0L;
            }
            final int subIndex = ReaderUtil.subIndex(docId, this.starts);
            if (subIndex != this.currentLeafIndex) {
                this.currentLeafIndex = subIndex;
                try {
                    this.currentWeightValues = DocumentValueSourceDictionary.this.weightsValueSource.getValues((Map)new HashMap(), (LeafReaderContext)this.leaves.get(this.currentLeafIndex));
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return this.currentWeightValues.longVal(docId - this.starts[subIndex]);
        }
    }
}

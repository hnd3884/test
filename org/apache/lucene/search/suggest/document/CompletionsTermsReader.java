package org.apache.lucene.search.suggest.document;

import java.util.Collections;
import java.util.Collection;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.util.Accountable;

public final class CompletionsTermsReader implements Accountable
{
    public final long minWeight;
    public final long maxWeight;
    public final byte type;
    private final IndexInput dictIn;
    private final long offset;
    private NRTSuggester suggester;
    
    CompletionsTermsReader(final IndexInput dictIn, final long offset, final long minWeight, final long maxWeight, final byte type) throws IOException {
        assert minWeight <= maxWeight;
        assert offset >= 0L && offset < dictIn.length();
        this.dictIn = dictIn;
        this.offset = offset;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.type = type;
    }
    
    public synchronized NRTSuggester suggester() throws IOException {
        if (this.suggester == null) {
            try (final IndexInput dictClone = this.dictIn.clone()) {
                dictClone.seek(this.offset);
                this.suggester = NRTSuggester.load(dictClone);
            }
        }
        return this.suggester;
    }
    
    public long ramBytesUsed() {
        return (this.suggester != null) ? this.suggester.ramBytesUsed() : 0L;
    }
    
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
}

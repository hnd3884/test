package org.apache.lucene.search.suggest.document;

import java.io.IOException;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.FilterLeafReader;

public final class CompletionTerms extends FilterLeafReader.FilterTerms
{
    private final CompletionsTermsReader reader;
    
    CompletionTerms(final Terms in, final CompletionsTermsReader reader) {
        super(in);
        this.reader = reader;
    }
    
    public byte getType() {
        return (byte)((this.reader != null) ? this.reader.type : 0);
    }
    
    public long getMinWeight() {
        return (this.reader != null) ? this.reader.minWeight : 0L;
    }
    
    public long getMaxWeight() {
        return (this.reader != null) ? this.reader.maxWeight : 0L;
    }
    
    public NRTSuggester suggester() throws IOException {
        return (this.reader != null) ? this.reader.suggester() : null;
    }
}

package org.apache.lucene.analysis.standard;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;

public class StandardFilter extends TokenFilter
{
    public StandardFilter(final TokenStream in) {
        super(in);
    }
    
    public final boolean incrementToken() throws IOException {
        return this.input.incrementToken();
    }
}

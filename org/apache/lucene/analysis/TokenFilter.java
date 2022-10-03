package org.apache.lucene.analysis;

import java.io.IOException;
import org.apache.lucene.util.AttributeSource;

public abstract class TokenFilter extends TokenStream
{
    protected final TokenStream input;
    
    protected TokenFilter(final TokenStream input) {
        super(input);
        this.input = input;
    }
    
    @Override
    public void end() throws IOException {
        this.input.end();
    }
    
    @Override
    public void close() throws IOException {
        this.input.close();
    }
    
    @Override
    public void reset() throws IOException {
        this.input.reset();
    }
}

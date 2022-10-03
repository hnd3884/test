package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.TokenStream;

public class PrefixAndSuffixAwareTokenFilter extends TokenStream
{
    private PrefixAwareTokenFilter suffix;
    
    public PrefixAndSuffixAwareTokenFilter(TokenStream prefix, final TokenStream input, final TokenStream suffix) {
        super((AttributeSource)suffix);
        prefix = new PrefixAwareTokenFilter(prefix, input) {
            @Override
            public Token updateSuffixToken(final Token suffixToken, final Token lastInputToken) {
                return PrefixAndSuffixAwareTokenFilter.this.updateInputToken(suffixToken, lastInputToken);
            }
        };
        this.suffix = new PrefixAwareTokenFilter(prefix, suffix) {
            @Override
            public Token updateSuffixToken(final Token suffixToken, final Token lastInputToken) {
                return PrefixAndSuffixAwareTokenFilter.this.updateSuffixToken(suffixToken, lastInputToken);
            }
        };
    }
    
    public Token updateInputToken(final Token inputToken, final Token lastPrefixToken) {
        inputToken.setOffset(lastPrefixToken.endOffset() + inputToken.startOffset(), lastPrefixToken.endOffset() + inputToken.endOffset());
        return inputToken;
    }
    
    public Token updateSuffixToken(final Token suffixToken, final Token lastInputToken) {
        suffixToken.setOffset(lastInputToken.endOffset() + suffixToken.startOffset(), lastInputToken.endOffset() + suffixToken.endOffset());
        return suffixToken;
    }
    
    public final boolean incrementToken() throws IOException {
        return this.suffix.incrementToken();
    }
    
    public void reset() throws IOException {
        this.suffix.reset();
    }
    
    public void close() throws IOException {
        this.suffix.close();
    }
    
    public void end() throws IOException {
        this.suffix.end();
    }
}

package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;

@Deprecated
public final class SingleTokenTokenStream extends TokenStream
{
    private boolean exhausted;
    private Token singleToken;
    private final AttributeImpl tokenAtt;
    
    public SingleTokenTokenStream(final Token token) {
        super(Token.TOKEN_ATTRIBUTE_FACTORY);
        this.exhausted = false;
        assert token != null;
        this.singleToken = token.clone();
        this.tokenAtt = (AttributeImpl)this.addAttribute((Class)CharTermAttribute.class);
        assert this.tokenAtt instanceof Token;
    }
    
    public final boolean incrementToken() {
        if (this.exhausted) {
            return false;
        }
        this.clearAttributes();
        this.singleToken.copyTo(this.tokenAtt);
        return this.exhausted = true;
    }
    
    public void reset() {
        this.exhausted = false;
    }
    
    public Token getToken() {
        return this.singleToken.clone();
    }
    
    public void setToken(final Token token) {
        this.singleToken = token.clone();
    }
}

package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CommonToken;

public class TokenTagToken extends CommonToken
{
    private final String tokenName;
    private final String label;
    
    public TokenTagToken(final String tokenName, final int type) {
        this(tokenName, type, null);
    }
    
    public TokenTagToken(final String tokenName, final int type, final String label) {
        super(type);
        this.tokenName = tokenName;
        this.label = label;
    }
    
    public final String getTokenName() {
        return this.tokenName;
    }
    
    public final String getLabel() {
        return this.label;
    }
    
    @Override
    public String getText() {
        if (this.label != null) {
            return "<" + this.label + ":" + this.tokenName + ">";
        }
        return "<" + this.tokenName + ">";
    }
    
    @Override
    public String toString() {
        return this.tokenName + ":" + this.type;
    }
}

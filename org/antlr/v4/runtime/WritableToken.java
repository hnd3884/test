package org.antlr.v4.runtime;

public interface WritableToken extends Token
{
    void setText(final String p0);
    
    void setType(final int p0);
    
    void setLine(final int p0);
    
    void setCharPositionInLine(final int p0);
    
    void setChannel(final int p0);
    
    void setTokenIndex(final int p0);
}

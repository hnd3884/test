package org.w3c.tidy;

public interface StreamIn
{
    public static final int END_OF_STREAM = -1;
    
    int getCurcol();
    
    int getCurline();
    
    int readCharFromStream();
    
    int readChar();
    
    void ungetChar(final int p0);
    
    boolean isEndOfStream();
    
    void setLexer(final Lexer p0);
}

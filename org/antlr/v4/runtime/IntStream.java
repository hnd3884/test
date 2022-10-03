package org.antlr.v4.runtime;

public interface IntStream
{
    public static final int EOF = -1;
    public static final String UNKNOWN_SOURCE_NAME = "<unknown>";
    
    void consume();
    
    int LA(final int p0);
    
    int mark();
    
    void release(final int p0);
    
    int index();
    
    void seek(final int p0);
    
    int size();
    
    String getSourceName();
}

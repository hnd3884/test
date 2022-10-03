package org.apache.lucene.queryparser.classic;

import java.io.IOException;

public interface CharStream
{
    char readChar() throws IOException;
    
    @Deprecated
    int getColumn();
    
    @Deprecated
    int getLine();
    
    @Deprecated
    int getEndColumn();
    
    int getEndLine();
    
    int getBeginColumn();
    
    int getBeginLine();
    
    void backup(final int p0);
    
    char BeginToken() throws IOException;
    
    String GetImage();
    
    char[] GetSuffix(final int p0);
    
    void Done();
}

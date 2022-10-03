package org.antlr.v4.runtime;

public interface Vocabulary
{
    int getMaxTokenType();
    
    String getLiteralName(final int p0);
    
    String getSymbolicName(final int p0);
    
    String getDisplayName(final int p0);
}

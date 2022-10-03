package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;

public interface Fragmenter
{
    void start(final String p0, final TokenStream p1);
    
    boolean isNewFragment();
}

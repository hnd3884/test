package org.apache.lucene.search.highlight;

import org.apache.lucene.analysis.TokenStream;

public class NullFragmenter implements Fragmenter
{
    @Override
    public void start(final String s, final TokenStream tokenStream) {
    }
    
    @Override
    public boolean isNewFragment() {
        return false;
    }
}

package org.apache.lucene.search.highlight;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;

public interface Scorer
{
    TokenStream init(final TokenStream p0) throws IOException;
    
    void startFragment(final TextFragment p0);
    
    float getTokenScore();
    
    float getFragmentScore();
}

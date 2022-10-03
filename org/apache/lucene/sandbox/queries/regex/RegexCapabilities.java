package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.util.BytesRef;

@Deprecated
public interface RegexCapabilities
{
    RegexMatcher compile(final String p0);
    
    @Deprecated
    public interface RegexMatcher
    {
        boolean match(final BytesRef p0);
        
        String prefix();
    }
}

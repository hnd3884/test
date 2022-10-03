package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;

public final class EmptyTokenStream extends TokenStream
{
    public final boolean incrementToken() {
        return false;
    }
}

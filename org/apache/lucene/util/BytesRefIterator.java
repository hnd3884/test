package org.apache.lucene.util;

import java.io.IOException;

public interface BytesRefIterator
{
    public static final BytesRefIterator EMPTY = new BytesRefIterator() {
        @Override
        public BytesRef next() {
            return null;
        }
    };
    
    BytesRef next() throws IOException;
}

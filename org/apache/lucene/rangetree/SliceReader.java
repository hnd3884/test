package org.apache.lucene.rangetree;

import java.io.IOException;
import java.io.Closeable;

interface SliceReader extends Closeable
{
    boolean next() throws IOException;
    
    long value();
    
    long ord();
    
    int docID();
}

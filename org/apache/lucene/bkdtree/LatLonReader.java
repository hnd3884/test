package org.apache.lucene.bkdtree;

import java.io.IOException;
import java.io.Closeable;

interface LatLonReader extends Closeable
{
    boolean next() throws IOException;
    
    int latEnc();
    
    int lonEnc();
    
    long ord();
    
    int docID();
}

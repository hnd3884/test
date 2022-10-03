package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.Fields;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

public abstract class TermVectorsReader implements Cloneable, Closeable, Accountable
{
    protected TermVectorsReader() {
    }
    
    public abstract Fields get(final int p0) throws IOException;
    
    public abstract void checkIntegrity() throws IOException;
    
    public abstract TermVectorsReader clone();
    
    public TermVectorsReader getMergeInstance() throws IOException {
        return this;
    }
}

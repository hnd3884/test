package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

public abstract class NormsProducer implements Closeable, Accountable
{
    protected NormsProducer() {
    }
    
    public abstract NumericDocValues getNorms(final FieldInfo p0) throws IOException;
    
    public abstract void checkIntegrity() throws IOException;
    
    public NormsProducer getMergeInstance() throws IOException {
        return this;
    }
}

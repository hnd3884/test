package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;
import org.apache.lucene.index.Fields;

public abstract class FieldsProducer extends Fields implements Closeable, Accountable
{
    protected FieldsProducer() {
    }
    
    @Override
    public abstract void close() throws IOException;
    
    public abstract void checkIntegrity() throws IOException;
    
    public FieldsProducer getMergeInstance() throws IOException {
        return this;
    }
}

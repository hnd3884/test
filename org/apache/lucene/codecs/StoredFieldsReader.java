package org.apache.lucene.codecs;

import java.io.IOException;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.util.Accountable;
import java.io.Closeable;

public abstract class StoredFieldsReader implements Cloneable, Closeable, Accountable
{
    protected StoredFieldsReader() {
    }
    
    public abstract void visitDocument(final int p0, final StoredFieldVisitor p1) throws IOException;
    
    public abstract StoredFieldsReader clone();
    
    public abstract void checkIntegrity() throws IOException;
    
    public StoredFieldsReader getMergeInstance() throws IOException {
        return this;
    }
}

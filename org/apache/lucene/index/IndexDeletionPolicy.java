package org.apache.lucene.index;

import java.io.IOException;
import java.util.List;

public abstract class IndexDeletionPolicy
{
    protected IndexDeletionPolicy() {
    }
    
    public abstract void onInit(final List<? extends IndexCommit> p0) throws IOException;
    
    public abstract void onCommit(final List<? extends IndexCommit> p0) throws IOException;
}

package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.util.InfoStream;
import java.io.Closeable;

public abstract class MergeScheduler implements Closeable
{
    protected InfoStream infoStream;
    
    protected MergeScheduler() {
    }
    
    public abstract void merge(final IndexWriter p0, final MergeTrigger p1, final boolean p2) throws IOException;
    
    @Override
    public abstract void close() throws IOException;
    
    final void setInfoStream(final InfoStream infoStream) {
        this.infoStream = infoStream;
    }
    
    protected boolean verbose() {
        return this.infoStream != null && this.infoStream.isEnabled("MS");
    }
    
    protected void message(final String message) {
        this.infoStream.message("MS", message);
    }
}

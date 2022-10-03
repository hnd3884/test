package org.apache.lucene.store;

import java.io.IOException;

public abstract class ChecksumIndexInput extends IndexInput
{
    protected ChecksumIndexInput(final String resourceDescription) {
        super(resourceDescription);
    }
    
    public abstract long getChecksum() throws IOException;
    
    @Override
    public void seek(final long pos) throws IOException {
        final long curFP = this.getFilePointer();
        final long skip = pos - curFP;
        if (skip < 0L) {
            throw new IllegalStateException(this.getClass() + " cannot seek backwards (pos=" + pos + " getFilePointer()=" + curFP + ")");
        }
        this.skipBytes(skip);
    }
}

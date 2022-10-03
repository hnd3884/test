package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import java.io.IOException;

@Deprecated
public abstract class DocsEnum extends PostingsEnum
{
    public static final int FLAG_NONE = 0;
    public static final int FLAG_FREQS = 1;
    
    protected DocsEnum() {
    }
    
    @Override
    public int nextPosition() throws IOException {
        return -1;
    }
    
    @Override
    public int startOffset() throws IOException {
        return -1;
    }
    
    @Override
    public int endOffset() throws IOException {
        return -1;
    }
    
    @Override
    public BytesRef getPayload() throws IOException {
        return null;
    }
}

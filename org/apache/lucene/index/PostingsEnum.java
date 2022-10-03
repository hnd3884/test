package org.apache.lucene.index;

import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.search.DocIdSetIterator;

public abstract class PostingsEnum extends DocIdSetIterator
{
    public static final short NONE = 0;
    public static final short FREQS = 8;
    public static final short POSITIONS = 24;
    public static final short OFFSETS = 56;
    public static final short PAYLOADS = 88;
    public static final short ALL = 120;
    private AttributeSource atts;
    
    public static boolean featureRequested(final int flags, final short feature) {
        return (flags & feature) == feature;
    }
    
    protected PostingsEnum() {
        this.atts = null;
    }
    
    public abstract int freq() throws IOException;
    
    public AttributeSource attributes() {
        if (this.atts == null) {
            this.atts = new AttributeSource();
        }
        return this.atts;
    }
    
    public abstract int nextPosition() throws IOException;
    
    public abstract int startOffset() throws IOException;
    
    public abstract int endOffset() throws IOException;
    
    public abstract BytesRef getPayload() throws IOException;
}

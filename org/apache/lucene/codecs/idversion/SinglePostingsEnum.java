package org.apache.lucene.codecs.idversion;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.PostingsEnum;

class SinglePostingsEnum extends PostingsEnum
{
    private int doc;
    private int pos;
    private int singleDocID;
    private long version;
    private final BytesRef payload;
    
    public SinglePostingsEnum() {
        this.payload = new BytesRef(8);
        this.payload.length = 8;
    }
    
    public void reset(final int singleDocID, final long version) {
        this.doc = -1;
        this.singleDocID = singleDocID;
        this.version = version;
    }
    
    public int nextDoc() {
        if (this.doc == -1) {
            this.doc = this.singleDocID;
        }
        else {
            this.doc = Integer.MAX_VALUE;
        }
        this.pos = -1;
        return this.doc;
    }
    
    public int docID() {
        return this.doc;
    }
    
    public int advance(final int target) {
        if (this.doc == -1 && target <= this.singleDocID) {
            this.doc = this.singleDocID;
            this.pos = -1;
        }
        else {
            this.doc = Integer.MAX_VALUE;
        }
        return this.doc;
    }
    
    public long cost() {
        return 1L;
    }
    
    public int freq() {
        return 1;
    }
    
    public int nextPosition() {
        assert this.pos == -1;
        this.pos = 0;
        IDVersionPostingsFormat.longToBytes(this.version, this.payload);
        return this.pos;
    }
    
    public BytesRef getPayload() {
        return this.payload;
    }
    
    public int startOffset() {
        return -1;
    }
    
    public int endOffset() {
        return -1;
    }
}

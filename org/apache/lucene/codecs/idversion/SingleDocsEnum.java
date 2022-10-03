package org.apache.lucene.codecs.idversion;

import org.apache.lucene.util.BytesRef;
import java.io.IOException;
import org.apache.lucene.index.PostingsEnum;

class SingleDocsEnum extends PostingsEnum
{
    private int doc;
    private int singleDocID;
    
    public void reset(final int singleDocID) {
        this.doc = -1;
        this.singleDocID = singleDocID;
    }
    
    public int nextDoc() {
        if (this.doc == -1) {
            this.doc = this.singleDocID;
        }
        else {
            this.doc = Integer.MAX_VALUE;
        }
        return this.doc;
    }
    
    public int docID() {
        return this.doc;
    }
    
    public int advance(final int target) {
        if (this.doc == -1 && target <= this.singleDocID) {
            this.doc = this.singleDocID;
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
    
    public int nextPosition() throws IOException {
        return -1;
    }
    
    public int startOffset() throws IOException {
        return -1;
    }
    
    public int endOffset() throws IOException {
        return -1;
    }
    
    public BytesRef getPayload() throws IOException {
        throw new UnsupportedOperationException();
    }
}

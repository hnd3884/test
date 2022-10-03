package org.apache.lucene.index;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.IntBlockPool;

abstract class TermsHash
{
    final TermsHash nextTermsHash;
    final IntBlockPool intPool;
    final ByteBlockPool bytePool;
    ByteBlockPool termBytePool;
    final Counter bytesUsed;
    final DocumentsWriterPerThread.DocState docState;
    final boolean trackAllocations;
    
    TermsHash(final DocumentsWriterPerThread docWriter, final boolean trackAllocations, final TermsHash nextTermsHash) {
        this.docState = docWriter.docState;
        this.trackAllocations = trackAllocations;
        this.nextTermsHash = nextTermsHash;
        this.bytesUsed = (trackAllocations ? docWriter.bytesUsed : Counter.newCounter());
        this.intPool = new IntBlockPool(docWriter.intBlockAllocator);
        this.bytePool = new ByteBlockPool(docWriter.byteBlockAllocator);
        if (nextTermsHash != null) {
            this.termBytePool = this.bytePool;
            nextTermsHash.termBytePool = this.bytePool;
        }
    }
    
    public void abort() {
        try {
            this.reset();
        }
        finally {
            if (this.nextTermsHash != null) {
                this.nextTermsHash.abort();
            }
        }
    }
    
    void reset() {
        this.intPool.reset(false, false);
        this.bytePool.reset(false, false);
    }
    
    void flush(final Map<String, TermsHashPerField> fieldsToFlush, final SegmentWriteState state) throws IOException {
        if (this.nextTermsHash != null) {
            final Map<String, TermsHashPerField> nextChildFields = new HashMap<String, TermsHashPerField>();
            for (final Map.Entry<String, TermsHashPerField> entry : fieldsToFlush.entrySet()) {
                nextChildFields.put(entry.getKey(), entry.getValue().nextPerField);
            }
            this.nextTermsHash.flush(nextChildFields, state);
        }
    }
    
    abstract TermsHashPerField addField(final FieldInvertState p0, final FieldInfo p1);
    
    void finishDocument() throws IOException {
        if (this.nextTermsHash != null) {
            this.nextTermsHash.finishDocument();
        }
    }
    
    void startDocument() throws IOException {
        if (this.nextTermsHash != null) {
            this.nextTermsHash.startDocument();
        }
    }
}

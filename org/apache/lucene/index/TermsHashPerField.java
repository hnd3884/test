package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Counter;
import org.apache.lucene.util.BytesRefHash;
import org.apache.lucene.util.ByteBlockPool;
import org.apache.lucene.util.IntBlockPool;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;

abstract class TermsHashPerField implements Comparable<TermsHashPerField>
{
    private static final int HASH_INIT_SIZE = 4;
    final TermsHash termsHash;
    final TermsHashPerField nextPerField;
    protected final DocumentsWriterPerThread.DocState docState;
    protected final FieldInvertState fieldState;
    TermToBytesRefAttribute termAtt;
    final IntBlockPool intPool;
    final ByteBlockPool bytePool;
    final ByteBlockPool termBytePool;
    final int streamCount;
    final int numPostingInt;
    protected final FieldInfo fieldInfo;
    final BytesRefHash bytesHash;
    ParallelPostingsArray postingsArray;
    private final Counter bytesUsed;
    int[] sortedTermIDs;
    private boolean doNextCall;
    int[] intUptos;
    int intUptoStart;
    
    public TermsHashPerField(final int streamCount, final FieldInvertState fieldState, final TermsHash termsHash, final TermsHashPerField nextPerField, final FieldInfo fieldInfo) {
        this.intPool = termsHash.intPool;
        this.bytePool = termsHash.bytePool;
        this.termBytePool = termsHash.termBytePool;
        this.docState = termsHash.docState;
        this.termsHash = termsHash;
        this.bytesUsed = termsHash.bytesUsed;
        this.fieldState = fieldState;
        this.streamCount = streamCount;
        this.numPostingInt = 2 * streamCount;
        this.fieldInfo = fieldInfo;
        this.nextPerField = nextPerField;
        final PostingsBytesStartArray byteStarts = new PostingsBytesStartArray(this, this.bytesUsed);
        this.bytesHash = new BytesRefHash(this.termBytePool, 4, byteStarts);
    }
    
    void reset() {
        this.bytesHash.clear(false);
        if (this.nextPerField != null) {
            this.nextPerField.reset();
        }
    }
    
    public void initReader(final ByteSliceReader reader, final int termID, final int stream) {
        assert stream < this.streamCount;
        final int intStart = this.postingsArray.intStarts[termID];
        final int[] ints = this.intPool.buffers[intStart >> 13];
        final int upto = intStart & 0x1FFF;
        reader.init(this.bytePool, this.postingsArray.byteStarts[termID] + stream * ByteBlockPool.FIRST_LEVEL_SIZE, ints[upto + stream]);
    }
    
    public int[] sortPostings() {
        return this.sortedTermIDs = this.bytesHash.sort(BytesRef.getUTF8SortedAsUnicodeComparator());
    }
    
    public void add(final int textStart) throws IOException {
        int termID = this.bytesHash.addByPoolOffset(textStart);
        if (termID >= 0) {
            if (this.numPostingInt + this.intPool.intUpto > 8192) {
                this.intPool.nextBuffer();
            }
            if (32768 - this.bytePool.byteUpto < this.numPostingInt * ByteBlockPool.FIRST_LEVEL_SIZE) {
                this.bytePool.nextBuffer();
            }
            this.intUptos = this.intPool.buffer;
            this.intUptoStart = this.intPool.intUpto;
            final IntBlockPool intPool = this.intPool;
            intPool.intUpto += this.streamCount;
            this.postingsArray.intStarts[termID] = this.intUptoStart + this.intPool.intOffset;
            for (int i = 0; i < this.streamCount; ++i) {
                final int upto = this.bytePool.newSlice(ByteBlockPool.FIRST_LEVEL_SIZE);
                this.intUptos[this.intUptoStart + i] = upto + this.bytePool.byteOffset;
            }
            this.postingsArray.byteStarts[termID] = this.intUptos[this.intUptoStart];
            this.newTerm(termID);
        }
        else {
            termID = -termID - 1;
            final int intStart = this.postingsArray.intStarts[termID];
            this.intUptos = this.intPool.buffers[intStart >> 13];
            this.intUptoStart = (intStart & 0x1FFF);
            this.addTerm(termID);
        }
    }
    
    void add() throws IOException {
        int termID = this.bytesHash.add(this.termAtt.getBytesRef());
        if (termID >= 0) {
            this.bytesHash.byteStart(termID);
            if (this.numPostingInt + this.intPool.intUpto > 8192) {
                this.intPool.nextBuffer();
            }
            if (32768 - this.bytePool.byteUpto < this.numPostingInt * ByteBlockPool.FIRST_LEVEL_SIZE) {
                this.bytePool.nextBuffer();
            }
            this.intUptos = this.intPool.buffer;
            this.intUptoStart = this.intPool.intUpto;
            final IntBlockPool intPool = this.intPool;
            intPool.intUpto += this.streamCount;
            this.postingsArray.intStarts[termID] = this.intUptoStart + this.intPool.intOffset;
            for (int i = 0; i < this.streamCount; ++i) {
                final int upto = this.bytePool.newSlice(ByteBlockPool.FIRST_LEVEL_SIZE);
                this.intUptos[this.intUptoStart + i] = upto + this.bytePool.byteOffset;
            }
            this.postingsArray.byteStarts[termID] = this.intUptos[this.intUptoStart];
            this.newTerm(termID);
        }
        else {
            termID = -termID - 1;
            final int intStart = this.postingsArray.intStarts[termID];
            this.intUptos = this.intPool.buffers[intStart >> 13];
            this.intUptoStart = (intStart & 0x1FFF);
            this.addTerm(termID);
        }
        if (this.doNextCall) {
            this.nextPerField.add(this.postingsArray.textStarts[termID]);
        }
    }
    
    void writeByte(final int stream, final byte b) {
        final int upto = this.intUptos[this.intUptoStart + stream];
        byte[] bytes = this.bytePool.buffers[upto >> 15];
        assert bytes != null;
        int offset = upto & 0x7FFF;
        if (bytes[offset] != 0) {
            offset = this.bytePool.allocSlice(bytes, offset);
            bytes = this.bytePool.buffer;
            this.intUptos[this.intUptoStart + stream] = offset + this.bytePool.byteOffset;
        }
        bytes[offset] = b;
        final int[] intUptos = this.intUptos;
        final int n = this.intUptoStart + stream;
        ++intUptos[n];
    }
    
    public void writeBytes(final int stream, final byte[] b, final int offset, final int len) {
        for (int end = offset + len, i = offset; i < end; ++i) {
            this.writeByte(stream, b[i]);
        }
    }
    
    void writeVInt(final int stream, int i) {
        assert stream < this.streamCount;
        while ((i & 0xFFFFFF80) != 0x0) {
            this.writeByte(stream, (byte)((i & 0x7F) | 0x80));
            i >>>= 7;
        }
        this.writeByte(stream, (byte)i);
    }
    
    @Override
    public int compareTo(final TermsHashPerField other) {
        return this.fieldInfo.name.compareTo(other.fieldInfo.name);
    }
    
    void finish() throws IOException {
        if (this.nextPerField != null) {
            this.nextPerField.finish();
        }
    }
    
    boolean start(final IndexableField field, final boolean first) {
        this.termAtt = this.fieldState.termAttribute;
        if (this.nextPerField != null) {
            this.doNextCall = this.nextPerField.start(field, first);
        }
        return true;
    }
    
    abstract void newTerm(final int p0) throws IOException;
    
    abstract void addTerm(final int p0) throws IOException;
    
    abstract void newPostingsArray();
    
    abstract ParallelPostingsArray createPostingsArray(final int p0);
    
    private static final class PostingsBytesStartArray extends BytesRefHash.BytesStartArray
    {
        private final TermsHashPerField perField;
        private final Counter bytesUsed;
        
        private PostingsBytesStartArray(final TermsHashPerField perField, final Counter bytesUsed) {
            this.perField = perField;
            this.bytesUsed = bytesUsed;
        }
        
        @Override
        public int[] init() {
            if (this.perField.postingsArray == null) {
                this.perField.postingsArray = this.perField.createPostingsArray(2);
                this.perField.newPostingsArray();
                this.bytesUsed.addAndGet(this.perField.postingsArray.size * this.perField.postingsArray.bytesPerPosting());
            }
            return this.perField.postingsArray.textStarts;
        }
        
        @Override
        public int[] grow() {
            ParallelPostingsArray postingsArray = this.perField.postingsArray;
            final int oldSize = this.perField.postingsArray.size;
            final TermsHashPerField perField = this.perField;
            final ParallelPostingsArray grow = postingsArray.grow();
            perField.postingsArray = grow;
            postingsArray = grow;
            this.perField.newPostingsArray();
            this.bytesUsed.addAndGet(postingsArray.bytesPerPosting() * (postingsArray.size - oldSize));
            return postingsArray.textStarts;
        }
        
        @Override
        public int[] clear() {
            if (this.perField.postingsArray != null) {
                this.bytesUsed.addAndGet(-(this.perField.postingsArray.size * this.perField.postingsArray.bytesPerPosting()));
                this.perField.postingsArray = null;
                this.perField.newPostingsArray();
            }
            return null;
        }
        
        @Override
        public Counter bytesUsed() {
            return this.bytesUsed;
        }
    }
}

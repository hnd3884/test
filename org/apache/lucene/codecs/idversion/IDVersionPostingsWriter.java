package org.apache.lucene.codecs.idversion;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.util.Bits;
import org.apache.lucene.codecs.PushPostingsWriterBase;

final class IDVersionPostingsWriter extends PushPostingsWriterBase
{
    static final String TERMS_CODEC = "IDVersionPostingsWriterTerms";
    static final int VERSION_START = 1;
    static final int VERSION_CURRENT = 1;
    static final IDVersionTermState emptyState;
    IDVersionTermState lastState;
    int lastDocID;
    private int lastPosition;
    private long lastVersion;
    private final Bits liveDocs;
    private long lastEncodedVersion;
    
    public IDVersionPostingsWriter(final Bits liveDocs) {
        this.liveDocs = liveDocs;
    }
    
    public BlockTermState newTermState() {
        return new IDVersionTermState();
    }
    
    public void init(final IndexOutput termsOut, final SegmentWriteState state) throws IOException {
        CodecUtil.writeIndexHeader((DataOutput)termsOut, "IDVersionPostingsWriterTerms", 1, state.segmentInfo.getId(), state.segmentSuffix);
    }
    
    public int setField(final FieldInfo fieldInfo) {
        super.setField(fieldInfo);
        if (fieldInfo.getIndexOptions() != IndexOptions.DOCS_AND_FREQS_AND_POSITIONS) {
            throw new IllegalArgumentException("field must be index using IndexOptions.DOCS_AND_FREQS_AND_POSITIONS");
        }
        if (fieldInfo.hasVectors()) {
            throw new IllegalArgumentException("field cannot index term vectors: CheckIndex will report this as index corruption");
        }
        this.lastState = IDVersionPostingsWriter.emptyState;
        return 0;
    }
    
    public void startTerm() {
        this.lastDocID = -1;
    }
    
    public void startDoc(final int docID, final int termDocFreq) throws IOException {
        if (this.liveDocs != null && !this.liveDocs.get(docID)) {
            return;
        }
        if (this.lastDocID != -1) {
            throw new IllegalArgumentException("term appears in more than one document");
        }
        if (termDocFreq != 1) {
            throw new IllegalArgumentException("term appears more than once in the document");
        }
        this.lastDocID = docID;
        this.lastPosition = -1;
        this.lastVersion = -1L;
    }
    
    public void addPosition(final int position, final BytesRef payload, final int startOffset, final int endOffset) throws IOException {
        if (this.lastDocID == -1) {
            return;
        }
        if (this.lastPosition != -1) {
            throw new IllegalArgumentException("term appears more than once in document");
        }
        this.lastPosition = position;
        if (payload == null) {
            throw new IllegalArgumentException("token doens't have a payload");
        }
        if (payload.length != 8) {
            throw new IllegalArgumentException("payload.length != 8 (got " + payload.length + ")");
        }
        this.lastVersion = IDVersionPostingsFormat.bytesToLong(payload);
        if (this.lastVersion < 0L) {
            throw new IllegalArgumentException("version must be >= MIN_VERSION=0 (got: " + this.lastVersion + "; payload=" + payload + ")");
        }
        if (this.lastVersion > 4611686018427387903L) {
            throw new IllegalArgumentException("version must be <= MAX_VERSION=4611686018427387903 (got: " + this.lastVersion + "; payload=" + payload + ")");
        }
    }
    
    public void finishDoc() throws IOException {
        if (this.lastDocID == -1) {
            return;
        }
        if (this.lastPosition == -1) {
            throw new IllegalArgumentException("missing addPosition");
        }
    }
    
    public void finishTerm(final BlockTermState _state) throws IOException {
        if (this.lastDocID == -1) {
            return;
        }
        final IDVersionTermState state = (IDVersionTermState)_state;
        assert state.docFreq > 0;
        state.docID = this.lastDocID;
        state.idVersion = this.lastVersion;
    }
    
    public void encodeTerm(final long[] longs, final DataOutput out, final FieldInfo fieldInfo, final BlockTermState _state, final boolean absolute) throws IOException {
        final IDVersionTermState state = (IDVersionTermState)_state;
        out.writeVInt(state.docID);
        if (absolute) {
            out.writeVLong(state.idVersion);
        }
        else {
            final long delta = state.idVersion - this.lastEncodedVersion;
            out.writeZLong(delta);
        }
        this.lastEncodedVersion = state.idVersion;
    }
    
    public void close() throws IOException {
    }
    
    static {
        emptyState = new IDVersionTermState();
    }
}

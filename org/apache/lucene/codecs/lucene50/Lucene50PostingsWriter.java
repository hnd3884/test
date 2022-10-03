package org.apache.lucene.codecs.lucene50;

import org.apache.lucene.codecs.BlockTermState;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.FieldInfo;
import java.io.IOException;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentWriteState;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.PushPostingsWriterBase;

public final class Lucene50PostingsWriter extends PushPostingsWriterBase
{
    IndexOutput docOut;
    IndexOutput posOut;
    IndexOutput payOut;
    static final Lucene50PostingsFormat.IntBlockTermState EMPTY_STATE;
    Lucene50PostingsFormat.IntBlockTermState lastState;
    private long docStartFP;
    private long posStartFP;
    private long payStartFP;
    final int[] docDeltaBuffer;
    final int[] freqBuffer;
    private int docBufferUpto;
    final int[] posDeltaBuffer;
    final int[] payloadLengthBuffer;
    final int[] offsetStartDeltaBuffer;
    final int[] offsetLengthBuffer;
    private int posBufferUpto;
    private byte[] payloadBytes;
    private int payloadByteUpto;
    private int lastBlockDocID;
    private long lastBlockPosFP;
    private long lastBlockPayFP;
    private int lastBlockPosBufferUpto;
    private int lastBlockPayloadByteUpto;
    private int lastDocID;
    private int lastPosition;
    private int lastStartOffset;
    private int docCount;
    final byte[] encoded;
    private final ForUtil forUtil;
    private final Lucene50SkipWriter skipWriter;
    
    public Lucene50PostingsWriter(final SegmentWriteState state) throws IOException {
        final float acceptableOverheadRatio = 0.0f;
        final String docFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "zdoc");
        this.docOut = state.directory.createOutput(docFileName, state.context);
        IndexOutput posOut = null;
        IndexOutput payOut = null;
        boolean success = false;
        try {
            CodecUtil.writeIndexHeader((DataOutput)this.docOut, "Lucene50PostingsWriterDoc", 0, state.segmentInfo.getId(), state.segmentSuffix);
            this.forUtil = new ForUtil(0.0f, (DataOutput)this.docOut);
            if (state.fieldInfos.hasProx()) {
                this.posDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
                final String posFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "pos");
                posOut = state.directory.createOutput(posFileName, state.context);
                CodecUtil.writeIndexHeader((DataOutput)posOut, "Lucene50PostingsWriterPos", 0, state.segmentInfo.getId(), state.segmentSuffix);
                if (state.fieldInfos.hasPayloads()) {
                    this.payloadBytes = new byte[128];
                    this.payloadLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
                }
                else {
                    this.payloadBytes = null;
                    this.payloadLengthBuffer = null;
                }
                if (state.fieldInfos.hasOffsets()) {
                    this.offsetStartDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
                    this.offsetLengthBuffer = new int[ForUtil.MAX_DATA_SIZE];
                }
                else {
                    this.offsetStartDeltaBuffer = null;
                    this.offsetLengthBuffer = null;
                }
                if (state.fieldInfos.hasPayloads() || state.fieldInfos.hasOffsets()) {
                    final String payFileName = IndexFileNames.segmentFileName(state.segmentInfo.name, state.segmentSuffix, "pay");
                    payOut = state.directory.createOutput(payFileName, state.context);
                    CodecUtil.writeIndexHeader((DataOutput)payOut, "Lucene50PostingsWriterPay", 0, state.segmentInfo.getId(), state.segmentSuffix);
                }
            }
            else {
                this.posDeltaBuffer = null;
                this.payloadLengthBuffer = null;
                this.offsetStartDeltaBuffer = null;
                this.offsetLengthBuffer = null;
                this.payloadBytes = null;
            }
            this.payOut = payOut;
            this.posOut = posOut;
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.docOut, (Closeable)posOut, (Closeable)payOut });
            }
        }
        this.docDeltaBuffer = new int[ForUtil.MAX_DATA_SIZE];
        this.freqBuffer = new int[ForUtil.MAX_DATA_SIZE];
        this.skipWriter = new Lucene50SkipWriter(10, 128, state.segmentInfo.maxDoc(), this.docOut, posOut, payOut);
        this.encoded = new byte[512];
    }
    
    public Lucene50PostingsFormat.IntBlockTermState newTermState() {
        return new Lucene50PostingsFormat.IntBlockTermState();
    }
    
    public void init(final IndexOutput termsOut, final SegmentWriteState state) throws IOException {
        CodecUtil.writeIndexHeader((DataOutput)termsOut, "Lucene50PostingsWriterTerms", 0, state.segmentInfo.getId(), state.segmentSuffix);
        termsOut.writeVInt(128);
    }
    
    public int setField(final FieldInfo fieldInfo) {
        super.setField(fieldInfo);
        this.skipWriter.setField(this.writePositions, this.writeOffsets, this.writePayloads);
        this.lastState = Lucene50PostingsWriter.EMPTY_STATE;
        if (!this.writePositions) {
            return 1;
        }
        if (this.writePayloads || this.writeOffsets) {
            return 3;
        }
        return 2;
    }
    
    public void startTerm() {
        this.docStartFP = this.docOut.getFilePointer();
        if (this.writePositions) {
            this.posStartFP = this.posOut.getFilePointer();
            if (this.writePayloads || this.writeOffsets) {
                this.payStartFP = this.payOut.getFilePointer();
            }
        }
        this.lastDocID = 0;
        this.lastBlockDocID = -1;
        this.skipWriter.resetSkip();
    }
    
    public void startDoc(final int docID, final int termDocFreq) throws IOException {
        if (this.lastBlockDocID != -1 && this.docBufferUpto == 0) {
            this.skipWriter.bufferSkip(this.lastBlockDocID, this.docCount, this.lastBlockPosFP, this.lastBlockPayFP, this.lastBlockPosBufferUpto, this.lastBlockPayloadByteUpto);
        }
        final int docDelta = docID - this.lastDocID;
        if (docID < 0 || (this.docCount > 0 && docDelta <= 0)) {
            throw new CorruptIndexException("docs out of order (" + docID + " <= " + this.lastDocID + " )", (DataOutput)this.docOut);
        }
        this.docDeltaBuffer[this.docBufferUpto] = docDelta;
        if (this.writeFreqs) {
            this.freqBuffer[this.docBufferUpto] = termDocFreq;
        }
        ++this.docBufferUpto;
        ++this.docCount;
        if (this.docBufferUpto == 128) {
            this.forUtil.writeBlock(this.docDeltaBuffer, this.encoded, this.docOut);
            if (this.writeFreqs) {
                this.forUtil.writeBlock(this.freqBuffer, this.encoded, this.docOut);
            }
        }
        this.lastDocID = docID;
        this.lastPosition = 0;
        this.lastStartOffset = 0;
    }
    
    public void addPosition(final int position, final BytesRef payload, final int startOffset, final int endOffset) throws IOException {
        if (position > 2147483519) {
            throw new CorruptIndexException("position=" + position + " is too large (> IndexWriter.MAX_POSITION=" + 2147483519 + ")", (DataOutput)this.docOut);
        }
        if (position < 0) {
            throw new CorruptIndexException("position=" + position + " is < 0", (DataOutput)this.docOut);
        }
        this.posDeltaBuffer[this.posBufferUpto] = position - this.lastPosition;
        if (this.writePayloads) {
            if (payload == null || payload.length == 0) {
                this.payloadLengthBuffer[this.posBufferUpto] = 0;
            }
            else {
                this.payloadLengthBuffer[this.posBufferUpto] = payload.length;
                if (this.payloadByteUpto + payload.length > this.payloadBytes.length) {
                    this.payloadBytes = ArrayUtil.grow(this.payloadBytes, this.payloadByteUpto + payload.length);
                }
                System.arraycopy(payload.bytes, payload.offset, this.payloadBytes, this.payloadByteUpto, payload.length);
                this.payloadByteUpto += payload.length;
            }
        }
        if (this.writeOffsets) {
            assert startOffset >= this.lastStartOffset;
            assert endOffset >= startOffset;
            this.offsetStartDeltaBuffer[this.posBufferUpto] = startOffset - this.lastStartOffset;
            this.offsetLengthBuffer[this.posBufferUpto] = endOffset - startOffset;
            this.lastStartOffset = startOffset;
        }
        ++this.posBufferUpto;
        this.lastPosition = position;
        if (this.posBufferUpto == 128) {
            this.forUtil.writeBlock(this.posDeltaBuffer, this.encoded, this.posOut);
            if (this.writePayloads) {
                this.forUtil.writeBlock(this.payloadLengthBuffer, this.encoded, this.payOut);
                this.payOut.writeVInt(this.payloadByteUpto);
                this.payOut.writeBytes(this.payloadBytes, 0, this.payloadByteUpto);
                this.payloadByteUpto = 0;
            }
            if (this.writeOffsets) {
                this.forUtil.writeBlock(this.offsetStartDeltaBuffer, this.encoded, this.payOut);
                this.forUtil.writeBlock(this.offsetLengthBuffer, this.encoded, this.payOut);
            }
            this.posBufferUpto = 0;
        }
    }
    
    public void finishDoc() throws IOException {
        if (this.docBufferUpto == 128) {
            this.lastBlockDocID = this.lastDocID;
            if (this.posOut != null) {
                if (this.payOut != null) {
                    this.lastBlockPayFP = this.payOut.getFilePointer();
                }
                this.lastBlockPosFP = this.posOut.getFilePointer();
                this.lastBlockPosBufferUpto = this.posBufferUpto;
                this.lastBlockPayloadByteUpto = this.payloadByteUpto;
            }
            this.docBufferUpto = 0;
        }
    }
    
    public void finishTerm(final BlockTermState blockTermState) throws IOException {
        final Lucene50PostingsFormat.IntBlockTermState state = (Lucene50PostingsFormat.IntBlockTermState)blockTermState;
        assert state.docFreq > 0;
        assert state.docFreq == this.docCount : state.docFreq + " vs " + this.docCount;
        int singletonDocID;
        if (state.docFreq == 1) {
            singletonDocID = this.docDeltaBuffer[0];
        }
        else {
            singletonDocID = -1;
            for (int i = 0; i < this.docBufferUpto; ++i) {
                final int docDelta = this.docDeltaBuffer[i];
                final int freq = this.freqBuffer[i];
                if (!this.writeFreqs) {
                    this.docOut.writeVInt(docDelta);
                }
                else if (this.freqBuffer[i] == 1) {
                    this.docOut.writeVInt(docDelta << 1 | 0x1);
                }
                else {
                    this.docOut.writeVInt(docDelta << 1);
                    this.docOut.writeVInt(freq);
                }
            }
        }
        long lastPosBlockOffset;
        if (this.writePositions) {
            assert state.totalTermFreq != -1L;
            if (state.totalTermFreq > 128L) {
                lastPosBlockOffset = this.posOut.getFilePointer() - this.posStartFP;
            }
            else {
                lastPosBlockOffset = -1L;
            }
            if (this.posBufferUpto > 0) {
                int lastPayloadLength = -1;
                int lastOffsetLength = -1;
                int payloadBytesReadUpto = 0;
                for (int j = 0; j < this.posBufferUpto; ++j) {
                    final int posDelta = this.posDeltaBuffer[j];
                    if (this.writePayloads) {
                        final int payloadLength = this.payloadLengthBuffer[j];
                        if (payloadLength != lastPayloadLength) {
                            lastPayloadLength = payloadLength;
                            this.posOut.writeVInt(posDelta << 1 | 0x1);
                            this.posOut.writeVInt(payloadLength);
                        }
                        else {
                            this.posOut.writeVInt(posDelta << 1);
                        }
                        if (payloadLength != 0) {
                            this.posOut.writeBytes(this.payloadBytes, payloadBytesReadUpto, payloadLength);
                            payloadBytesReadUpto += payloadLength;
                        }
                    }
                    else {
                        this.posOut.writeVInt(posDelta);
                    }
                    if (this.writeOffsets) {
                        final int delta = this.offsetStartDeltaBuffer[j];
                        final int length = this.offsetLengthBuffer[j];
                        if (length == lastOffsetLength) {
                            this.posOut.writeVInt(delta << 1);
                        }
                        else {
                            this.posOut.writeVInt(delta << 1 | 0x1);
                            this.posOut.writeVInt(length);
                            lastOffsetLength = length;
                        }
                    }
                }
                if (this.writePayloads) {
                    assert payloadBytesReadUpto == this.payloadByteUpto;
                    this.payloadByteUpto = 0;
                }
            }
        }
        else {
            lastPosBlockOffset = -1L;
        }
        long skipOffset;
        if (this.docCount > 128) {
            skipOffset = this.skipWriter.writeSkip(this.docOut) - this.docStartFP;
        }
        else {
            skipOffset = -1L;
        }
        state.docStartFP = this.docStartFP;
        state.posStartFP = this.posStartFP;
        state.payStartFP = this.payStartFP;
        state.singletonDocID = singletonDocID;
        state.skipOffset = skipOffset;
        state.lastPosBlockOffset = lastPosBlockOffset;
        this.docBufferUpto = 0;
        this.posBufferUpto = 0;
        this.lastDocID = 0;
        this.docCount = 0;
    }
    
    public void encodeTerm(final long[] longs, final DataOutput out, final FieldInfo fieldInfo, final BlockTermState blockTermState, final boolean absolute) throws IOException {
        final Lucene50PostingsFormat.IntBlockTermState state = (Lucene50PostingsFormat.IntBlockTermState)blockTermState;
        if (absolute) {
            this.lastState = Lucene50PostingsWriter.EMPTY_STATE;
        }
        longs[0] = state.docStartFP - this.lastState.docStartFP;
        if (this.writePositions) {
            longs[1] = state.posStartFP - this.lastState.posStartFP;
            if (this.writePayloads || this.writeOffsets) {
                longs[2] = state.payStartFP - this.lastState.payStartFP;
            }
        }
        if (state.singletonDocID != -1) {
            out.writeVInt(state.singletonDocID);
        }
        if (this.writePositions && state.lastPosBlockOffset != -1L) {
            out.writeVLong(state.lastPosBlockOffset);
        }
        if (state.skipOffset != -1L) {
            out.writeVLong(state.skipOffset);
        }
        this.lastState = state;
    }
    
    public void close() throws IOException {
        boolean success = false;
        try {
            if (this.docOut != null) {
                CodecUtil.writeFooter(this.docOut);
            }
            if (this.posOut != null) {
                CodecUtil.writeFooter(this.posOut);
            }
            if (this.payOut != null) {
                CodecUtil.writeFooter(this.payOut);
            }
            success = true;
        }
        finally {
            if (success) {
                IOUtils.close(new Closeable[] { (Closeable)this.docOut, (Closeable)this.posOut, (Closeable)this.payOut });
            }
            else {
                IOUtils.closeWhileHandlingException(new Closeable[] { (Closeable)this.docOut, (Closeable)this.posOut, (Closeable)this.payOut });
            }
            final IndexOutput docOut = null;
            this.payOut = docOut;
            this.posOut = docOut;
            this.docOut = docOut;
        }
    }
    
    static {
        Lucene50PostingsWriter.$assertionsDisabled = !Lucene50PostingsWriter.class.desiredAssertionStatus();
        Lucene50PostingsWriter.EMPTY_STATE = new Lucene50PostingsFormat.IntBlockTermState();
    }
}

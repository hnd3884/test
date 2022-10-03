package org.apache.lucene.codecs.lucene50;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.codecs.MultiLevelSkipListWriter;

final class Lucene50SkipWriter extends MultiLevelSkipListWriter
{
    private int[] lastSkipDoc;
    private long[] lastSkipDocPointer;
    private long[] lastSkipPosPointer;
    private long[] lastSkipPayPointer;
    private int[] lastPayloadByteUpto;
    private final IndexOutput docOut;
    private final IndexOutput posOut;
    private final IndexOutput payOut;
    private int curDoc;
    private long curDocPointer;
    private long curPosPointer;
    private long curPayPointer;
    private int curPosBufferUpto;
    private int curPayloadByteUpto;
    private boolean fieldHasPositions;
    private boolean fieldHasOffsets;
    private boolean fieldHasPayloads;
    private boolean initialized;
    long lastDocFP;
    long lastPosFP;
    long lastPayFP;
    
    public Lucene50SkipWriter(final int maxSkipLevels, final int blockSize, final int docCount, final IndexOutput docOut, final IndexOutput posOut, final IndexOutput payOut) {
        super(blockSize, 8, maxSkipLevels, docCount);
        this.docOut = docOut;
        this.posOut = posOut;
        this.payOut = payOut;
        this.lastSkipDoc = new int[maxSkipLevels];
        this.lastSkipDocPointer = new long[maxSkipLevels];
        if (posOut != null) {
            this.lastSkipPosPointer = new long[maxSkipLevels];
            if (payOut != null) {
                this.lastSkipPayPointer = new long[maxSkipLevels];
            }
            this.lastPayloadByteUpto = new int[maxSkipLevels];
        }
    }
    
    public void setField(final boolean fieldHasPositions, final boolean fieldHasOffsets, final boolean fieldHasPayloads) {
        this.fieldHasPositions = fieldHasPositions;
        this.fieldHasOffsets = fieldHasOffsets;
        this.fieldHasPayloads = fieldHasPayloads;
    }
    
    public void resetSkip() {
        this.lastDocFP = this.docOut.getFilePointer();
        if (this.fieldHasPositions) {
            this.lastPosFP = this.posOut.getFilePointer();
            if (this.fieldHasOffsets || this.fieldHasPayloads) {
                this.lastPayFP = this.payOut.getFilePointer();
            }
        }
        this.initialized = false;
    }
    
    public void initSkip() {
        if (!this.initialized) {
            super.resetSkip();
            Arrays.fill(this.lastSkipDoc, 0);
            Arrays.fill(this.lastSkipDocPointer, this.lastDocFP);
            if (this.fieldHasPositions) {
                Arrays.fill(this.lastSkipPosPointer, this.lastPosFP);
                if (this.fieldHasPayloads) {
                    Arrays.fill(this.lastPayloadByteUpto, 0);
                }
                if (this.fieldHasOffsets || this.fieldHasPayloads) {
                    Arrays.fill(this.lastSkipPayPointer, this.lastPayFP);
                }
            }
            this.initialized = true;
        }
    }
    
    public void bufferSkip(final int doc, final int numDocs, final long posFP, final long payFP, final int posBufferUpto, final int payloadByteUpto) throws IOException {
        this.initSkip();
        this.curDoc = doc;
        this.curDocPointer = this.docOut.getFilePointer();
        this.curPosPointer = posFP;
        this.curPayPointer = payFP;
        this.curPosBufferUpto = posBufferUpto;
        this.curPayloadByteUpto = payloadByteUpto;
        this.bufferSkip(numDocs);
    }
    
    @Override
    protected void writeSkipData(final int level, final IndexOutput skipBuffer) throws IOException {
        final int delta = this.curDoc - this.lastSkipDoc[level];
        skipBuffer.writeVInt(delta);
        this.lastSkipDoc[level] = this.curDoc;
        skipBuffer.writeVLong(this.curDocPointer - this.lastSkipDocPointer[level]);
        this.lastSkipDocPointer[level] = this.curDocPointer;
        if (this.fieldHasPositions) {
            skipBuffer.writeVLong(this.curPosPointer - this.lastSkipPosPointer[level]);
            this.lastSkipPosPointer[level] = this.curPosPointer;
            skipBuffer.writeVInt(this.curPosBufferUpto);
            if (this.fieldHasPayloads) {
                skipBuffer.writeVInt(this.curPayloadByteUpto);
            }
            if (this.fieldHasOffsets || this.fieldHasPayloads) {
                skipBuffer.writeVLong(this.curPayPointer - this.lastSkipPayPointer[level]);
                this.lastSkipPayPointer[level] = this.curPayPointer;
            }
        }
    }
}

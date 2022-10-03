package org.apache.lucene.codecs.lucene50;

import java.io.IOException;
import java.util.Arrays;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.codecs.MultiLevelSkipListReader;

final class Lucene50SkipReader extends MultiLevelSkipListReader
{
    private long[] docPointer;
    private long[] posPointer;
    private long[] payPointer;
    private int[] posBufferUpto;
    private int[] payloadByteUpto;
    private long lastPosPointer;
    private long lastPayPointer;
    private int lastPayloadByteUpto;
    private long lastDocPointer;
    private int lastPosBufferUpto;
    
    public Lucene50SkipReader(final IndexInput skipStream, final int maxSkipLevels, final boolean hasPos, final boolean hasOffsets, final boolean hasPayloads) {
        super(skipStream, maxSkipLevels, 128, 8);
        this.docPointer = new long[maxSkipLevels];
        if (hasPos) {
            this.posPointer = new long[maxSkipLevels];
            this.posBufferUpto = new int[maxSkipLevels];
            if (hasPayloads) {
                this.payloadByteUpto = new int[maxSkipLevels];
            }
            else {
                this.payloadByteUpto = null;
            }
            if (hasOffsets || hasPayloads) {
                this.payPointer = new long[maxSkipLevels];
            }
            else {
                this.payPointer = null;
            }
        }
        else {
            this.posPointer = null;
        }
    }
    
    protected int trim(final int df) {
        return (df % 128 == 0) ? (df - 1) : df;
    }
    
    public void init(final long skipPointer, final long docBasePointer, final long posBasePointer, final long payBasePointer, final int df) throws IOException {
        super.init(skipPointer, this.trim(df));
        this.lastDocPointer = docBasePointer;
        this.lastPosPointer = posBasePointer;
        this.lastPayPointer = payBasePointer;
        Arrays.fill(this.docPointer, docBasePointer);
        if (this.posPointer != null) {
            Arrays.fill(this.posPointer, posBasePointer);
            if (this.payPointer != null) {
                Arrays.fill(this.payPointer, payBasePointer);
            }
        }
        else {
            assert posBasePointer == 0L;
        }
    }
    
    public long getDocPointer() {
        return this.lastDocPointer;
    }
    
    public long getPosPointer() {
        return this.lastPosPointer;
    }
    
    public int getPosBufferUpto() {
        return this.lastPosBufferUpto;
    }
    
    public long getPayPointer() {
        return this.lastPayPointer;
    }
    
    public int getPayloadByteUpto() {
        return this.lastPayloadByteUpto;
    }
    
    public int getNextSkipDoc() {
        return this.skipDoc[0];
    }
    
    @Override
    protected void seekChild(final int level) throws IOException {
        super.seekChild(level);
        this.docPointer[level] = this.lastDocPointer;
        if (this.posPointer != null) {
            this.posPointer[level] = this.lastPosPointer;
            this.posBufferUpto[level] = this.lastPosBufferUpto;
            if (this.payloadByteUpto != null) {
                this.payloadByteUpto[level] = this.lastPayloadByteUpto;
            }
            if (this.payPointer != null) {
                this.payPointer[level] = this.lastPayPointer;
            }
        }
    }
    
    @Override
    protected void setLastSkipData(final int level) {
        super.setLastSkipData(level);
        this.lastDocPointer = this.docPointer[level];
        if (this.posPointer != null) {
            this.lastPosPointer = this.posPointer[level];
            this.lastPosBufferUpto = this.posBufferUpto[level];
            if (this.payPointer != null) {
                this.lastPayPointer = this.payPointer[level];
            }
            if (this.payloadByteUpto != null) {
                this.lastPayloadByteUpto = this.payloadByteUpto[level];
            }
        }
    }
    
    @Override
    protected int readSkipData(final int level, final IndexInput skipStream) throws IOException {
        final int delta = skipStream.readVInt();
        final long[] docPointer = this.docPointer;
        docPointer[level] += skipStream.readVLong();
        if (this.posPointer != null) {
            final long[] posPointer = this.posPointer;
            posPointer[level] += skipStream.readVLong();
            this.posBufferUpto[level] = skipStream.readVInt();
            if (this.payloadByteUpto != null) {
                this.payloadByteUpto[level] = skipStream.readVInt();
            }
            if (this.payPointer != null) {
                final long[] payPointer = this.payPointer;
                payPointer[level] += skipStream.readVLong();
            }
        }
        return delta;
    }
}

package org.apache.lucene.codecs.compressing;

import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.packed.PackedInts;
import org.apache.lucene.util.BitUtil;
import java.io.IOException;
import org.apache.lucene.store.IndexOutput;
import java.io.Closeable;

public final class CompressingStoredFieldsIndexWriter implements Closeable
{
    final IndexOutput fieldsIndexOut;
    final int blockSize;
    int totalDocs;
    int blockDocs;
    int blockChunks;
    long firstStartPointer;
    long maxStartPointer;
    final int[] docBaseDeltas;
    final long[] startPointerDeltas;
    
    CompressingStoredFieldsIndexWriter(final IndexOutput indexOutput, final int blockSize) throws IOException {
        if (blockSize <= 0) {
            throw new IllegalArgumentException("blockSize must be positive");
        }
        this.blockSize = blockSize;
        this.fieldsIndexOut = indexOutput;
        this.reset();
        this.totalDocs = 0;
        this.docBaseDeltas = new int[blockSize];
        this.startPointerDeltas = new long[blockSize];
        this.fieldsIndexOut.writeVInt(2);
    }
    
    private void reset() {
        this.blockChunks = 0;
        this.blockDocs = 0;
        this.firstStartPointer = -1L;
    }
    
    private void writeBlock() throws IOException {
        assert this.blockChunks > 0;
        this.fieldsIndexOut.writeVInt(this.blockChunks);
        int avgChunkDocs;
        if (this.blockChunks == 1) {
            avgChunkDocs = 0;
        }
        else {
            avgChunkDocs = Math.round((this.blockDocs - this.docBaseDeltas[this.blockChunks - 1]) / (float)(this.blockChunks - 1));
        }
        this.fieldsIndexOut.writeVInt(this.totalDocs - this.blockDocs);
        this.fieldsIndexOut.writeVInt(avgChunkDocs);
        int docBase = 0;
        long maxDelta = 0L;
        for (int i = 0; i < this.blockChunks; ++i) {
            final int delta = docBase - avgChunkDocs * i;
            maxDelta |= BitUtil.zigZagEncode(delta);
            docBase += this.docBaseDeltas[i];
        }
        final int bitsPerDocBase = PackedInts.bitsRequired(maxDelta);
        this.fieldsIndexOut.writeVInt(bitsPerDocBase);
        PackedInts.Writer writer = PackedInts.getWriterNoHeader(this.fieldsIndexOut, PackedInts.Format.PACKED, this.blockChunks, bitsPerDocBase, 1);
        docBase = 0;
        for (int j = 0; j < this.blockChunks; ++j) {
            final long delta2 = docBase - avgChunkDocs * j;
            assert PackedInts.bitsRequired(BitUtil.zigZagEncode(delta2)) <= writer.bitsPerValue();
            writer.add(BitUtil.zigZagEncode(delta2));
            docBase += this.docBaseDeltas[j];
        }
        writer.finish();
        this.fieldsIndexOut.writeVLong(this.firstStartPointer);
        long avgChunkSize;
        if (this.blockChunks == 1) {
            avgChunkSize = 0L;
        }
        else {
            avgChunkSize = (this.maxStartPointer - this.firstStartPointer) / (this.blockChunks - 1);
        }
        this.fieldsIndexOut.writeVLong(avgChunkSize);
        long startPointer = 0L;
        maxDelta = 0L;
        for (int k = 0; k < this.blockChunks; ++k) {
            startPointer += this.startPointerDeltas[k];
            final long delta3 = startPointer - avgChunkSize * k;
            maxDelta |= BitUtil.zigZagEncode(delta3);
        }
        final int bitsPerStartPointer = PackedInts.bitsRequired(maxDelta);
        this.fieldsIndexOut.writeVInt(bitsPerStartPointer);
        writer = PackedInts.getWriterNoHeader(this.fieldsIndexOut, PackedInts.Format.PACKED, this.blockChunks, bitsPerStartPointer, 1);
        startPointer = 0L;
        for (int l = 0; l < this.blockChunks; ++l) {
            startPointer += this.startPointerDeltas[l];
            final long delta4 = startPointer - avgChunkSize * l;
            assert PackedInts.bitsRequired(BitUtil.zigZagEncode(delta4)) <= writer.bitsPerValue();
            writer.add(BitUtil.zigZagEncode(delta4));
        }
        writer.finish();
    }
    
    void writeIndex(final int numDocs, final long startPointer) throws IOException {
        if (this.blockChunks == this.blockSize) {
            this.writeBlock();
            this.reset();
        }
        if (this.firstStartPointer == -1L) {
            this.maxStartPointer = startPointer;
            this.firstStartPointer = startPointer;
        }
        assert this.firstStartPointer > 0L && startPointer >= this.firstStartPointer;
        this.docBaseDeltas[this.blockChunks] = numDocs;
        this.startPointerDeltas[this.blockChunks] = startPointer - this.maxStartPointer;
        ++this.blockChunks;
        this.blockDocs += numDocs;
        this.totalDocs += numDocs;
        this.maxStartPointer = startPointer;
    }
    
    void finish(final int numDocs, final long maxPointer) throws IOException {
        if (numDocs != this.totalDocs) {
            throw new IllegalStateException("Expected " + numDocs + " docs, but got " + this.totalDocs);
        }
        if (this.blockChunks > 0) {
            this.writeBlock();
        }
        this.fieldsIndexOut.writeVInt(0);
        this.fieldsIndexOut.writeVLong(maxPointer);
        CodecUtil.writeFooter(this.fieldsIndexOut);
    }
    
    @Override
    public void close() throws IOException {
        this.fieldsIndexOut.close();
    }
}

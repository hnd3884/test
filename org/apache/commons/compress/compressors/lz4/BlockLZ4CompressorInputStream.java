package org.apache.commons.compress.compressors.lz4;

import org.apache.commons.compress.utils.ByteUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.lz77support.AbstractLZ77CompressorInputStream;

public class BlockLZ4CompressorInputStream extends AbstractLZ77CompressorInputStream
{
    static final int WINDOW_SIZE = 65536;
    static final int SIZE_BITS = 4;
    static final int BACK_REFERENCE_SIZE_MASK = 15;
    static final int LITERAL_SIZE_MASK = 240;
    private int nextBackReferenceSize;
    private State state;
    
    public BlockLZ4CompressorInputStream(final InputStream is) throws IOException {
        super(is, 65536);
        this.state = State.NO_BLOCK;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        switch (this.state) {
            case EOF: {
                return -1;
            }
            case NO_BLOCK: {
                this.readSizes();
            }
            case IN_LITERAL: {
                final int litLen = this.readLiteral(b, off, len);
                if (!this.hasMoreDataInBlock()) {
                    this.state = State.LOOKING_FOR_BACK_REFERENCE;
                }
                return (litLen > 0) ? litLen : this.read(b, off, len);
            }
            case LOOKING_FOR_BACK_REFERENCE: {
                if (!this.initializeBackReference()) {
                    this.state = State.EOF;
                    return -1;
                }
            }
            case IN_BACK_REFERENCE: {
                final int backReferenceLen = this.readBackReference(b, off, len);
                if (!this.hasMoreDataInBlock()) {
                    this.state = State.NO_BLOCK;
                }
                return (backReferenceLen > 0) ? backReferenceLen : this.read(b, off, len);
            }
            default: {
                throw new IOException("Unknown stream state " + this.state);
            }
        }
    }
    
    private void readSizes() throws IOException {
        final int nextBlock = this.readOneByte();
        if (nextBlock == -1) {
            throw new IOException("Premature end of stream while looking for next block");
        }
        this.nextBackReferenceSize = (nextBlock & 0xF);
        long literalSizePart = (nextBlock & 0xF0) >> 4;
        if (literalSizePart == 15L) {
            literalSizePart += this.readSizeBytes();
        }
        if (literalSizePart < 0L) {
            throw new IOException("Illegal block with a negative literal size found");
        }
        this.startLiteral(literalSizePart);
        this.state = State.IN_LITERAL;
    }
    
    private long readSizeBytes() throws IOException {
        long accum = 0L;
        int nextByte;
        do {
            nextByte = this.readOneByte();
            if (nextByte == -1) {
                throw new IOException("Premature end of stream while parsing length");
            }
            accum += nextByte;
        } while (nextByte == 255);
        return accum;
    }
    
    private boolean initializeBackReference() throws IOException {
        int backReferenceOffset = 0;
        try {
            backReferenceOffset = (int)ByteUtils.fromLittleEndian(this.supplier, 2);
        }
        catch (final IOException ex) {
            if (this.nextBackReferenceSize == 0) {
                return false;
            }
            throw ex;
        }
        long backReferenceSize = this.nextBackReferenceSize;
        if (this.nextBackReferenceSize == 15) {
            backReferenceSize += this.readSizeBytes();
        }
        if (backReferenceSize < 0L) {
            throw new IOException("Illegal block with a negative match length found");
        }
        try {
            this.startBackReference(backReferenceOffset, backReferenceSize + 4L);
        }
        catch (final IllegalArgumentException ex2) {
            throw new IOException("Illegal block with bad offset found", ex2);
        }
        this.state = State.IN_BACK_REFERENCE;
        return true;
    }
    
    private enum State
    {
        NO_BLOCK, 
        IN_LITERAL, 
        LOOKING_FOR_BACK_REFERENCE, 
        IN_BACK_REFERENCE, 
        EOF;
    }
}

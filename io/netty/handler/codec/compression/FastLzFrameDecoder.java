package io.netty.handler.codec.compression;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.zip.Checksum;
import java.util.zip.Adler32;
import io.netty.handler.codec.ByteToMessageDecoder;

public class FastLzFrameDecoder extends ByteToMessageDecoder
{
    private State currentState;
    private final ByteBufChecksum checksum;
    private int chunkLength;
    private int originalLength;
    private boolean isCompressed;
    private boolean hasChecksum;
    private int currentChecksum;
    
    public FastLzFrameDecoder() {
        this(false);
    }
    
    public FastLzFrameDecoder(final boolean validateChecksums) {
        this(validateChecksums ? new Adler32() : null);
    }
    
    public FastLzFrameDecoder(final Checksum checksum) {
        this.currentState = State.INIT_BLOCK;
        this.checksum = ((checksum == null) ? null : ByteBufChecksum.wrapChecksum(checksum));
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        try {
            switch (this.currentState) {
                case INIT_BLOCK: {
                    if (in.readableBytes() < 4) {
                        break;
                    }
                    final int magic = in.readUnsignedMedium();
                    if (magic != 4607066) {
                        throw new DecompressionException("unexpected block identifier");
                    }
                    final byte options = in.readByte();
                    this.isCompressed = ((options & 0x1) == 0x1);
                    this.hasChecksum = ((options & 0x10) == 0x10);
                    this.currentState = State.INIT_BLOCK_PARAMS;
                }
                case INIT_BLOCK_PARAMS: {
                    if (in.readableBytes() < 2 + (this.isCompressed ? 2 : 0) + (this.hasChecksum ? 4 : 0)) {
                        break;
                    }
                    this.currentChecksum = (this.hasChecksum ? in.readInt() : 0);
                    this.chunkLength = in.readUnsignedShort();
                    this.originalLength = (this.isCompressed ? in.readUnsignedShort() : this.chunkLength);
                    this.currentState = State.DECOMPRESS_DATA;
                }
                case DECOMPRESS_DATA: {
                    final int chunkLength = this.chunkLength;
                    if (in.readableBytes() < chunkLength) {
                        break;
                    }
                    final int idx = in.readerIndex();
                    final int originalLength = this.originalLength;
                    ByteBuf output = null;
                    try {
                        if (this.isCompressed) {
                            output = ctx.alloc().buffer(originalLength);
                            final int outputOffset = output.writerIndex();
                            final int decompressedBytes = FastLz.decompress(in, idx, chunkLength, output, outputOffset, originalLength);
                            if (originalLength != decompressedBytes) {
                                throw new DecompressionException(String.format("stream corrupted: originalLength(%d) and actual length(%d) mismatch", originalLength, decompressedBytes));
                            }
                            output.writerIndex(output.writerIndex() + decompressedBytes);
                        }
                        else {
                            output = in.retainedSlice(idx, chunkLength);
                        }
                        final ByteBufChecksum checksum = this.checksum;
                        if (this.hasChecksum && checksum != null) {
                            checksum.reset();
                            checksum.update(output, output.readerIndex(), output.readableBytes());
                            final int checksumResult = (int)checksum.getValue();
                            if (checksumResult != this.currentChecksum) {
                                throw new DecompressionException(String.format("stream corrupted: mismatching checksum: %d (expected: %d)", checksumResult, this.currentChecksum));
                            }
                        }
                        if (output.readableBytes() > 0) {
                            out.add(output);
                        }
                        else {
                            output.release();
                        }
                        output = null;
                        in.skipBytes(chunkLength);
                        this.currentState = State.INIT_BLOCK;
                    }
                    finally {
                        if (output != null) {
                            output.release();
                        }
                    }
                    break;
                }
                case CORRUPTED: {
                    in.skipBytes(in.readableBytes());
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        catch (final Exception e) {
            this.currentState = State.CORRUPTED;
            throw e;
        }
    }
    
    private enum State
    {
        INIT_BLOCK, 
        INIT_BLOCK_PARAMS, 
        DECOMPRESS_DATA, 
        CORRUPTED;
    }
}

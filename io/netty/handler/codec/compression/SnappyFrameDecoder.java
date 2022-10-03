package io.netty.handler.codec.compression;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class SnappyFrameDecoder extends ByteToMessageDecoder
{
    private static final int SNAPPY_IDENTIFIER_LEN = 6;
    private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
    private static final int MAX_DECOMPRESSED_DATA_SIZE = 65536;
    private static final int MAX_COMPRESSED_CHUNK_SIZE = 16777215;
    private final Snappy snappy;
    private final boolean validateChecksums;
    private boolean started;
    private boolean corrupted;
    private int numBytesToSkip;
    
    public SnappyFrameDecoder() {
        this(false);
    }
    
    public SnappyFrameDecoder(final boolean validateChecksums) {
        this.snappy = new Snappy();
        this.validateChecksums = validateChecksums;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (this.corrupted) {
            in.skipBytes(in.readableBytes());
            return;
        }
        if (this.numBytesToSkip != 0) {
            final int skipBytes = Math.min(this.numBytesToSkip, in.readableBytes());
            in.skipBytes(skipBytes);
            this.numBytesToSkip -= skipBytes;
            return;
        }
        try {
            final int idx = in.readerIndex();
            final int inSize = in.readableBytes();
            if (inSize < 4) {
                return;
            }
            final int chunkTypeVal = in.getUnsignedByte(idx);
            final ChunkType chunkType = mapChunkType((byte)chunkTypeVal);
            final int chunkLength = in.getUnsignedMediumLE(idx + 1);
            switch (chunkType) {
                case STREAM_IDENTIFIER: {
                    if (chunkLength != 6) {
                        throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
                    }
                    if (inSize < 10) {
                        break;
                    }
                    in.skipBytes(4);
                    int offset = in.readerIndex();
                    in.skipBytes(6);
                    checkByte(in.getByte(offset++), (byte)115);
                    checkByte(in.getByte(offset++), (byte)78);
                    checkByte(in.getByte(offset++), (byte)97);
                    checkByte(in.getByte(offset++), (byte)80);
                    checkByte(in.getByte(offset++), (byte)112);
                    checkByte(in.getByte(offset), (byte)89);
                    this.started = true;
                    break;
                }
                case RESERVED_SKIPPABLE: {
                    if (!this.started) {
                        throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                    }
                    in.skipBytes(4);
                    final int skipBytes2 = Math.min(chunkLength, in.readableBytes());
                    in.skipBytes(skipBytes2);
                    if (skipBytes2 != chunkLength) {
                        this.numBytesToSkip = chunkLength - skipBytes2;
                        break;
                    }
                    break;
                }
                case RESERVED_UNSKIPPABLE: {
                    throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(chunkTypeVal));
                }
                case UNCOMPRESSED_DATA: {
                    if (!this.started) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 65540) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    if (this.validateChecksums) {
                        final int checksum = in.readIntLE();
                        Snappy.validateChecksum(checksum, in, in.readerIndex(), chunkLength - 4);
                    }
                    else {
                        in.skipBytes(4);
                    }
                    out.add(in.readRetainedSlice(chunkLength - 4));
                    break;
                }
                case COMPRESSED_DATA: {
                    if (!this.started) {
                        throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 16777215) {
                        throw new DecompressionException("Received COMPRESSED_DATA that contains chunk that exceeds 16777215 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    final int checksum = in.readIntLE();
                    final int uncompressedSize = this.snappy.getPreamble(in);
                    if (uncompressedSize > 65536) {
                        throw new DecompressionException("Received COMPRESSED_DATA that contains uncompressed data that exceeds 65536 bytes");
                    }
                    ByteBuf uncompressed = ctx.alloc().buffer(uncompressedSize, 65536);
                    try {
                        if (this.validateChecksums) {
                            final int oldWriterIndex = in.writerIndex();
                            try {
                                in.writerIndex(in.readerIndex() + chunkLength - 4);
                                this.snappy.decode(in, uncompressed);
                            }
                            finally {
                                in.writerIndex(oldWriterIndex);
                            }
                            Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
                        }
                        else {
                            this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
                        }
                        out.add(uncompressed);
                        uncompressed = null;
                    }
                    finally {
                        if (uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                    this.snappy.reset();
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.corrupted = true;
            throw e;
        }
    }
    
    private static void checkByte(final byte actual, final byte expect) {
        if (actual != expect) {
            throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
        }
    }
    
    private static ChunkType mapChunkType(final byte type) {
        if (type == 0) {
            return ChunkType.COMPRESSED_DATA;
        }
        if (type == 1) {
            return ChunkType.UNCOMPRESSED_DATA;
        }
        if (type == -1) {
            return ChunkType.STREAM_IDENTIFIER;
        }
        if ((type & 0x80) == 0x80) {
            return ChunkType.RESERVED_SKIPPABLE;
        }
        return ChunkType.RESERVED_UNSKIPPABLE;
    }
    
    private enum ChunkType
    {
        STREAM_IDENTIFIER, 
        COMPRESSED_DATA, 
        UNCOMPRESSED_DATA, 
        RESERVED_UNSKIPPABLE, 
        RESERVED_SKIPPABLE;
    }
}

package io.netty.handler.codec.compression;

import com.ning.compress.lzf.LZFChunk;
import com.ning.compress.lzf.LZFEncoder;
import io.netty.channel.ChannelHandlerContext;
import com.ning.compress.lzf.util.ChunkEncoderFactory;
import com.ning.compress.BufferRecycler;
import com.ning.compress.lzf.ChunkEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

public class LzfEncoder extends MessageToByteEncoder<ByteBuf>
{
    private static final int MIN_BLOCK_TO_COMPRESS = 16;
    private final int compressThreshold;
    private final ChunkEncoder encoder;
    private final BufferRecycler recycler;
    
    public LzfEncoder() {
        this(false);
    }
    
    public LzfEncoder(final boolean safeInstance) {
        this(safeInstance, 65535);
    }
    
    public LzfEncoder(final boolean safeInstance, final int totalLength) {
        this(safeInstance, totalLength, 16);
    }
    
    public LzfEncoder(final int totalLength) {
        this(false, totalLength);
    }
    
    public LzfEncoder(final boolean safeInstance, final int totalLength, final int compressThreshold) {
        super(false);
        if (totalLength < 16 || totalLength > 65535) {
            throw new IllegalArgumentException("totalLength: " + totalLength + " (expected: " + 16 + '-' + 65535 + ')');
        }
        if (compressThreshold < 16) {
            throw new IllegalArgumentException("compressThreshold:" + compressThreshold + " expected >=" + 16);
        }
        this.compressThreshold = compressThreshold;
        this.encoder = (safeInstance ? ChunkEncoderFactory.safeNonAllocatingInstance(totalLength) : ChunkEncoderFactory.optimalNonAllocatingInstance(totalLength));
        this.recycler = BufferRecycler.instance();
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf in, final ByteBuf out) throws Exception {
        final int length = in.readableBytes();
        final int idx = in.readerIndex();
        byte[] input;
        int inputPtr;
        if (in.hasArray()) {
            input = in.array();
            inputPtr = in.arrayOffset() + idx;
        }
        else {
            input = this.recycler.allocInputBuffer(length);
            in.getBytes(idx, input, 0, length);
            inputPtr = 0;
        }
        final int maxOutputLength = LZFEncoder.estimateMaxWorkspaceSize(length) + 1;
        out.ensureWritable(maxOutputLength);
        byte[] output;
        int outputPtr;
        if (out.hasArray()) {
            output = out.array();
            outputPtr = out.arrayOffset() + out.writerIndex();
        }
        else {
            output = new byte[maxOutputLength];
            outputPtr = 0;
        }
        int outputLength;
        if (length >= this.compressThreshold) {
            outputLength = this.encodeCompress(input, inputPtr, length, output, outputPtr);
        }
        else {
            outputLength = encodeNonCompress(input, inputPtr, length, output, outputPtr);
        }
        if (out.hasArray()) {
            out.writerIndex(out.writerIndex() + outputLength);
        }
        else {
            out.writeBytes(output, 0, outputLength);
        }
        in.skipBytes(length);
        if (!in.hasArray()) {
            this.recycler.releaseInputBuffer(input);
        }
    }
    
    private int encodeCompress(final byte[] input, final int inputPtr, final int length, final byte[] output, final int outputPtr) {
        return LZFEncoder.appendEncoded(this.encoder, input, inputPtr, length, output, outputPtr) - outputPtr;
    }
    
    private static int lzfEncodeNonCompress(final byte[] input, int inputPtr, final int length, final byte[] output, int outputPtr) {
        int left = length;
        int chunkLen = Math.min(65535, left);
        outputPtr = LZFChunk.appendNonCompressed(input, inputPtr, chunkLen, output, outputPtr);
        left -= chunkLen;
        if (left < 1) {
            return outputPtr;
        }
        inputPtr += chunkLen;
        do {
            chunkLen = Math.min(left, 65535);
            outputPtr = LZFChunk.appendNonCompressed(input, inputPtr, chunkLen, output, outputPtr);
            inputPtr += chunkLen;
            left -= chunkLen;
        } while (left > 0);
        return outputPtr;
    }
    
    private static int encodeNonCompress(final byte[] input, final int inputPtr, final int length, final byte[] output, final int outputPtr) {
        return lzfEncodeNonCompress(input, inputPtr, length, output, outputPtr) - outputPtr;
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        this.encoder.close();
        super.handlerRemoved(ctx);
    }
}

package io.netty.handler.codec.compression;

import io.netty.util.ReferenceCountUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import com.aayushatharva.brotli4j.encoder.Encoder;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public final class BrotliEncoder extends MessageToByteEncoder<ByteBuf>
{
    private final Encoder.Parameters parameters;
    
    public BrotliEncoder() {
        this(BrotliOptions.DEFAULT);
    }
    
    public BrotliEncoder(final Encoder.Parameters parameters) {
        this.parameters = ObjectUtil.checkNotNull(parameters, "Parameters");
    }
    
    public BrotliEncoder(final BrotliOptions brotliOptions) {
        this(brotliOptions.parameters());
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
    }
    
    @Override
    protected ByteBuf allocateBuffer(final ChannelHandlerContext ctx, final ByteBuf msg, final boolean preferDirect) throws Exception {
        if (!msg.isReadable()) {
            return Unpooled.EMPTY_BUFFER;
        }
        try {
            final byte[] uncompressed = ByteBufUtil.getBytes(msg, msg.readerIndex(), msg.readableBytes(), false);
            final byte[] compressed = Encoder.compress(uncompressed, this.parameters);
            if (preferDirect) {
                final ByteBuf out = ctx.alloc().ioBuffer(compressed.length);
                out.writeBytes(compressed);
                return out;
            }
            return Unpooled.wrappedBuffer(compressed);
        }
        catch (final Exception e) {
            ReferenceCountUtil.release(msg);
            throw e;
        }
    }
}

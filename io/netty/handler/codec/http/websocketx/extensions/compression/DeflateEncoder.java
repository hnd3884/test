package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.CompositeByteBuf;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.CodecException;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;

abstract class DeflateEncoder extends WebSocketExtensionEncoder
{
    private final int compressionLevel;
    private final int windowSize;
    private final boolean noContext;
    private final WebSocketExtensionFilter extensionEncoderFilter;
    private EmbeddedChannel encoder;
    
    DeflateEncoder(final int compressionLevel, final int windowSize, final boolean noContext, final WebSocketExtensionFilter extensionEncoderFilter) {
        this.compressionLevel = compressionLevel;
        this.windowSize = windowSize;
        this.noContext = noContext;
        this.extensionEncoderFilter = ObjectUtil.checkNotNull(extensionEncoderFilter, "extensionEncoderFilter");
    }
    
    protected WebSocketExtensionFilter extensionEncoderFilter() {
        return this.extensionEncoderFilter;
    }
    
    protected abstract int rsv(final WebSocketFrame p0);
    
    protected abstract boolean removeFrameTail(final WebSocketFrame p0);
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final WebSocketFrame msg, final List<Object> out) throws Exception {
        ByteBuf compressedContent;
        if (msg.content().isReadable()) {
            compressedContent = this.compressContent(ctx, msg);
        }
        else {
            if (!msg.isFinalFragment()) {
                throw new CodecException("cannot compress content buffer");
            }
            compressedContent = PerMessageDeflateDecoder.EMPTY_DEFLATE_BLOCK.duplicate();
        }
        WebSocketFrame outMsg;
        if (msg instanceof TextWebSocketFrame) {
            outMsg = new TextWebSocketFrame(msg.isFinalFragment(), this.rsv(msg), compressedContent);
        }
        else if (msg instanceof BinaryWebSocketFrame) {
            outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), this.rsv(msg), compressedContent);
        }
        else {
            if (!(msg instanceof ContinuationWebSocketFrame)) {
                throw new CodecException("unexpected frame type: " + msg.getClass().getName());
            }
            outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), this.rsv(msg), compressedContent);
        }
        out.add(outMsg);
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved(ctx);
    }
    
    private ByteBuf compressContent(final ChannelHandlerContext ctx, final WebSocketFrame msg) {
        if (this.encoder == null) {
            this.encoder = new EmbeddedChannel(new ChannelHandler[] { ZlibCodecFactory.newZlibEncoder(ZlibWrapper.NONE, this.compressionLevel, this.windowSize, 8) });
        }
        this.encoder.writeOutbound(msg.content().retain());
        final CompositeByteBuf fullCompressedContent = ctx.alloc().compositeBuffer();
        while (true) {
            final ByteBuf partCompressedContent = this.encoder.readOutbound();
            if (partCompressedContent == null) {
                break;
            }
            if (!partCompressedContent.isReadable()) {
                partCompressedContent.release();
            }
            else {
                fullCompressedContent.addComponent(true, partCompressedContent);
            }
        }
        if (fullCompressedContent.numComponents() <= 0) {
            fullCompressedContent.release();
            throw new CodecException("cannot read compressed buffer");
        }
        if (msg.isFinalFragment() && this.noContext) {
            this.cleanup();
        }
        ByteBuf compressedContent;
        if (this.removeFrameTail(msg)) {
            final int realLength = fullCompressedContent.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.readableBytes();
            compressedContent = fullCompressedContent.slice(0, realLength);
        }
        else {
            compressedContent = fullCompressedContent;
        }
        return compressedContent;
    }
    
    private void cleanup() {
        if (this.encoder != null) {
            this.encoder.finishAndReleaseAll();
            this.encoder = null;
        }
    }
}

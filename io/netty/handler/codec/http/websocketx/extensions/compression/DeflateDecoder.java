package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.Unpooled;
import io.netty.buffer.CompositeByteBuf;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;

abstract class DeflateDecoder extends WebSocketExtensionDecoder
{
    static final ByteBuf FRAME_TAIL;
    static final ByteBuf EMPTY_DEFLATE_BLOCK;
    private final boolean noContext;
    private final WebSocketExtensionFilter extensionDecoderFilter;
    private EmbeddedChannel decoder;
    
    DeflateDecoder(final boolean noContext, final WebSocketExtensionFilter extensionDecoderFilter) {
        this.noContext = noContext;
        this.extensionDecoderFilter = ObjectUtil.checkNotNull(extensionDecoderFilter, "extensionDecoderFilter");
    }
    
    protected WebSocketExtensionFilter extensionDecoderFilter() {
        return this.extensionDecoderFilter;
    }
    
    protected abstract boolean appendFrameTail(final WebSocketFrame p0);
    
    protected abstract int newRsv(final WebSocketFrame p0);
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame msg, final List<Object> out) throws Exception {
        final ByteBuf decompressedContent = this.decompressContent(ctx, msg);
        WebSocketFrame outMsg;
        if (msg instanceof TextWebSocketFrame) {
            outMsg = new TextWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
        }
        else if (msg instanceof BinaryWebSocketFrame) {
            outMsg = new BinaryWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
        }
        else {
            if (!(msg instanceof ContinuationWebSocketFrame)) {
                throw new CodecException("unexpected frame type: " + msg.getClass().getName());
            }
            outMsg = new ContinuationWebSocketFrame(msg.isFinalFragment(), this.newRsv(msg), decompressedContent);
        }
        out.add(outMsg);
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.handlerRemoved(ctx);
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.cleanup();
        super.channelInactive(ctx);
    }
    
    private ByteBuf decompressContent(final ChannelHandlerContext ctx, final WebSocketFrame msg) {
        if (this.decoder == null) {
            if (!(msg instanceof TextWebSocketFrame) && !(msg instanceof BinaryWebSocketFrame)) {
                throw new CodecException("unexpected initial frame type: " + msg.getClass().getName());
            }
            this.decoder = new EmbeddedChannel(new ChannelHandler[] { ZlibCodecFactory.newZlibDecoder(ZlibWrapper.NONE) });
        }
        final boolean readable = msg.content().isReadable();
        final boolean emptyDeflateBlock = DeflateDecoder.EMPTY_DEFLATE_BLOCK.equals(msg.content());
        this.decoder.writeInbound(msg.content().retain());
        if (this.appendFrameTail(msg)) {
            this.decoder.writeInbound(DeflateDecoder.FRAME_TAIL.duplicate());
        }
        final CompositeByteBuf compositeDecompressedContent = ctx.alloc().compositeBuffer();
        while (true) {
            final ByteBuf partUncompressedContent = this.decoder.readInbound();
            if (partUncompressedContent == null) {
                break;
            }
            if (!partUncompressedContent.isReadable()) {
                partUncompressedContent.release();
            }
            else {
                compositeDecompressedContent.addComponent(true, partUncompressedContent);
            }
        }
        if (!emptyDeflateBlock && readable && compositeDecompressedContent.numComponents() <= 0 && !(msg instanceof ContinuationWebSocketFrame)) {
            compositeDecompressedContent.release();
            throw new CodecException("cannot read uncompressed buffer");
        }
        if (msg.isFinalFragment() && this.noContext) {
            this.cleanup();
        }
        return compositeDecompressedContent;
    }
    
    private void cleanup() {
        if (this.decoder != null) {
            this.decoder.finishAndReleaseAll();
            this.decoder = null;
        }
    }
    
    static {
        FRAME_TAIL = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[] { 0, 0, -1, -1 })).asReadOnly();
        EMPTY_DEFLATE_BLOCK = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[] { 0 })).asReadOnly();
    }
}

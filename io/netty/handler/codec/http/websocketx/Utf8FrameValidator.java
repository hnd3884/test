package io.netty.handler.codec.http.websocketx;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Utf8FrameValidator extends ChannelInboundHandlerAdapter
{
    private int fragmentedFramesCount;
    private Utf8Validator utf8Validator;
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            final WebSocketFrame frame = (WebSocketFrame)msg;
            try {
                if (((WebSocketFrame)msg).isFinalFragment()) {
                    if (!(frame instanceof PingWebSocketFrame)) {
                        this.fragmentedFramesCount = 0;
                        if (frame instanceof TextWebSocketFrame || (this.utf8Validator != null && this.utf8Validator.isChecking())) {
                            this.checkUTF8String(frame.content());
                            this.utf8Validator.finish();
                        }
                    }
                }
                else {
                    if (this.fragmentedFramesCount == 0) {
                        if (frame instanceof TextWebSocketFrame) {
                            this.checkUTF8String(frame.content());
                        }
                    }
                    else if (this.utf8Validator != null && this.utf8Validator.isChecking()) {
                        this.checkUTF8String(frame.content());
                    }
                    ++this.fragmentedFramesCount;
                }
            }
            catch (final CorruptedWebSocketFrameException e) {
                frame.release();
                throw e;
            }
        }
        super.channelRead(ctx, msg);
    }
    
    private void checkUTF8String(final ByteBuf buffer) {
        if (this.utf8Validator == null) {
            this.utf8Validator = new Utf8Validator();
        }
        this.utf8Validator.check(buffer);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause instanceof CorruptedFrameException && ctx.channel().isOpen()) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE);
        }
        super.exceptionCaught(ctx, cause);
    }
}

package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpServerExpectContinueHandler extends ChannelInboundHandlerAdapter
{
    private static final FullHttpResponse EXPECTATION_FAILED;
    private static final FullHttpResponse ACCEPT;
    
    protected HttpResponse acceptMessage(final HttpRequest request) {
        return HttpServerExpectContinueHandler.ACCEPT.retainedDuplicate();
    }
    
    protected HttpResponse rejectResponse(final HttpRequest request) {
        return HttpServerExpectContinueHandler.EXPECTATION_FAILED.retainedDuplicate();
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            final HttpRequest req = (HttpRequest)msg;
            if (HttpUtil.is100ContinueExpected(req)) {
                final HttpResponse accept = this.acceptMessage(req);
                if (accept == null) {
                    final HttpResponse rejection = this.rejectResponse(req);
                    ReferenceCountUtil.release(msg);
                    ctx.writeAndFlush(rejection).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
                    return;
                }
                ctx.writeAndFlush(accept).addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
                req.headers().remove(HttpHeaderNames.EXPECT);
            }
        }
        super.channelRead(ctx, msg);
    }
    
    static {
        EXPECTATION_FAILED = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
        ACCEPT = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
        HttpServerExpectContinueHandler.EXPECTATION_FAILED.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
        HttpServerExpectContinueHandler.ACCEPT.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0);
    }
}

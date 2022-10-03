package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.channel.ChannelPromise;
import java.util.Iterator;
import java.util.ArrayList;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.channel.ChannelHandlerContext;
import java.util.Arrays;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import io.netty.channel.ChannelDuplexHandler;

public class WebSocketServerExtensionHandler extends ChannelDuplexHandler
{
    private final List<WebSocketServerExtensionHandshaker> extensionHandshakers;
    private List<WebSocketServerExtension> validExtensions;
    
    public WebSocketServerExtensionHandler(final WebSocketServerExtensionHandshaker... extensionHandshakers) {
        this.extensionHandshakers = Arrays.asList((WebSocketServerExtensionHandshaker[])ObjectUtil.checkNonEmpty((T[])extensionHandshakers, "extensionHandshakers"));
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            final HttpRequest request = (HttpRequest)msg;
            if (WebSocketExtensionUtil.isWebsocketUpgrade(request.headers())) {
                final String extensionsHeader = request.headers().getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
                if (extensionsHeader != null) {
                    final List<WebSocketExtensionData> extensions = WebSocketExtensionUtil.extractExtensions(extensionsHeader);
                    int rsv = 0;
                    for (final WebSocketExtensionData extensionData : extensions) {
                        Iterator<WebSocketServerExtensionHandshaker> extensionHandshakersIterator;
                        WebSocketServerExtension validExtension;
                        WebSocketServerExtensionHandshaker extensionHandshaker;
                        for (extensionHandshakersIterator = this.extensionHandshakers.iterator(), validExtension = null; validExtension == null && extensionHandshakersIterator.hasNext(); validExtension = extensionHandshaker.handshakeExtension(extensionData)) {
                            extensionHandshaker = extensionHandshakersIterator.next();
                        }
                        if (validExtension != null && (validExtension.rsv() & rsv) == 0x0) {
                            if (this.validExtensions == null) {
                                this.validExtensions = new ArrayList<WebSocketServerExtension>(1);
                            }
                            rsv |= validExtension.rsv();
                            this.validExtensions.add(validExtension);
                        }
                    }
                }
            }
        }
        super.channelRead(ctx, msg);
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof HttpResponse) {
            final HttpResponse httpResponse = (HttpResponse)msg;
            if (HttpResponseStatus.SWITCHING_PROTOCOLS.equals(httpResponse.status())) {
                this.handlePotentialUpgrade(ctx, promise, httpResponse);
            }
        }
        super.write(ctx, msg, promise);
    }
    
    private void handlePotentialUpgrade(final ChannelHandlerContext ctx, final ChannelPromise promise, final HttpResponse httpResponse) {
        final HttpHeaders headers = httpResponse.headers();
        if (WebSocketExtensionUtil.isWebsocketUpgrade(headers)) {
            if (this.validExtensions != null) {
                final String headerValue = headers.getAsString(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS);
                final List<WebSocketExtensionData> extraExtensions = new ArrayList<WebSocketExtensionData>(this.extensionHandshakers.size());
                for (final WebSocketServerExtension extension : this.validExtensions) {
                    extraExtensions.add(extension.newReponseData());
                }
                final String newHeaderValue = WebSocketExtensionUtil.computeMergeExtensionsHeaderValue(headerValue, extraExtensions);
                promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture future) {
                        if (future.isSuccess()) {
                            for (final WebSocketServerExtension extension : WebSocketServerExtensionHandler.this.validExtensions) {
                                final WebSocketExtensionDecoder decoder = extension.newExtensionDecoder();
                                final WebSocketExtensionEncoder encoder = extension.newExtensionEncoder();
                                final String name = ctx.name();
                                ctx.pipeline().addAfter(name, decoder.getClass().getName(), decoder).addAfter(name, encoder.getClass().getName(), encoder);
                            }
                        }
                    }
                });
                if (newHeaderValue != null) {
                    headers.set(HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS, newHeaderValue);
                }
            }
            promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                @Override
                public void operationComplete(final ChannelFuture future) {
                    if (future.isSuccess()) {
                        ctx.pipeline().remove(WebSocketServerExtensionHandler.this);
                    }
                }
            });
        }
    }
}

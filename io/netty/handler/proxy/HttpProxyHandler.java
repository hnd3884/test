package io.netty.handler.proxy;

import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.HttpUtil;
import java.net.InetSocketAddress;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.handler.codec.base64.Base64;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpHeaders;

public final class HttpProxyHandler extends ProxyHandler
{
    private static final String PROTOCOL = "http";
    private static final String AUTH_BASIC = "basic";
    private final HttpClientCodecWrapper codecWrapper;
    private final String username;
    private final String password;
    private final CharSequence authorization;
    private final HttpHeaders outboundHeaders;
    private final boolean ignoreDefaultPortsInConnectHostHeader;
    private HttpResponseStatus status;
    private HttpHeaders inboundHeaders;
    
    public HttpProxyHandler(final SocketAddress proxyAddress) {
        this(proxyAddress, null);
    }
    
    public HttpProxyHandler(final SocketAddress proxyAddress, final HttpHeaders headers) {
        this(proxyAddress, headers, false);
    }
    
    public HttpProxyHandler(final SocketAddress proxyAddress, final HttpHeaders headers, final boolean ignoreDefaultPortsInConnectHostHeader) {
        super(proxyAddress);
        this.codecWrapper = new HttpClientCodecWrapper();
        this.username = null;
        this.password = null;
        this.authorization = null;
        this.outboundHeaders = headers;
        this.ignoreDefaultPortsInConnectHostHeader = ignoreDefaultPortsInConnectHostHeader;
    }
    
    public HttpProxyHandler(final SocketAddress proxyAddress, final String username, final String password) {
        this(proxyAddress, username, password, null);
    }
    
    public HttpProxyHandler(final SocketAddress proxyAddress, final String username, final String password, final HttpHeaders headers) {
        this(proxyAddress, username, password, headers, false);
    }
    
    public HttpProxyHandler(final SocketAddress proxyAddress, final String username, final String password, final HttpHeaders headers, final boolean ignoreDefaultPortsInConnectHostHeader) {
        super(proxyAddress);
        this.codecWrapper = new HttpClientCodecWrapper();
        this.username = ObjectUtil.checkNotNull(username, "username");
        this.password = ObjectUtil.checkNotNull(password, "password");
        final ByteBuf authz = Unpooled.copiedBuffer(username + ':' + password, CharsetUtil.UTF_8);
        ByteBuf authzBase64;
        try {
            authzBase64 = Base64.encode(authz, false);
        }
        finally {
            authz.release();
        }
        try {
            this.authorization = new AsciiString("Basic " + authzBase64.toString(CharsetUtil.US_ASCII));
        }
        finally {
            authzBase64.release();
        }
        this.outboundHeaders = headers;
        this.ignoreDefaultPortsInConnectHostHeader = ignoreDefaultPortsInConnectHostHeader;
    }
    
    @Override
    public String protocol() {
        return "http";
    }
    
    @Override
    public String authScheme() {
        return (this.authorization != null) ? "basic" : "none";
    }
    
    public String username() {
        return this.username;
    }
    
    public String password() {
        return this.password;
    }
    
    @Override
    protected void addCodec(final ChannelHandlerContext ctx) throws Exception {
        final ChannelPipeline p = ctx.pipeline();
        final String name = ctx.name();
        p.addBefore(name, null, this.codecWrapper);
    }
    
    @Override
    protected void removeEncoder(final ChannelHandlerContext ctx) throws Exception {
        this.codecWrapper.codec.removeOutboundHandler();
    }
    
    @Override
    protected void removeDecoder(final ChannelHandlerContext ctx) throws Exception {
        this.codecWrapper.codec.removeInboundHandler();
    }
    
    @Override
    protected Object newInitialMessage(final ChannelHandlerContext ctx) throws Exception {
        final InetSocketAddress raddr = this.destinationAddress();
        final String hostString = HttpUtil.formatHostnameForHttp(raddr);
        final int port = raddr.getPort();
        final String url = hostString + ":" + port;
        final String hostHeader = (this.ignoreDefaultPortsInConnectHostHeader && (port == 80 || port == 443)) ? hostString : url;
        final FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, url, Unpooled.EMPTY_BUFFER, false);
        req.headers().set(HttpHeaderNames.HOST, hostHeader);
        if (this.authorization != null) {
            req.headers().set(HttpHeaderNames.PROXY_AUTHORIZATION, this.authorization);
        }
        if (this.outboundHeaders != null) {
            req.headers().add(this.outboundHeaders);
        }
        return req;
    }
    
    @Override
    protected boolean handleResponse(final ChannelHandlerContext ctx, final Object response) throws Exception {
        if (response instanceof HttpResponse) {
            if (this.status != null) {
                throw new HttpProxyConnectException(this.exceptionMessage("too many responses"), (HttpHeaders)null);
            }
            final HttpResponse res = (HttpResponse)response;
            this.status = res.status();
            this.inboundHeaders = res.headers();
        }
        final boolean finished = response instanceof LastHttpContent;
        if (finished) {
            if (this.status == null) {
                throw new HttpProxyConnectException(this.exceptionMessage("missing response"), this.inboundHeaders);
            }
            if (this.status.code() != 200) {
                throw new HttpProxyConnectException(this.exceptionMessage("status: " + this.status), this.inboundHeaders);
            }
        }
        return finished;
    }
    
    public static final class HttpProxyConnectException extends ProxyConnectException
    {
        private static final long serialVersionUID = -8824334609292146066L;
        private final HttpHeaders headers;
        
        public HttpProxyConnectException(final String message, final HttpHeaders headers) {
            super(message);
            this.headers = headers;
        }
        
        public HttpHeaders headers() {
            return this.headers;
        }
    }
    
    private static final class HttpClientCodecWrapper implements ChannelInboundHandler, ChannelOutboundHandler
    {
        final HttpClientCodec codec;
        
        private HttpClientCodecWrapper() {
            this.codec = new HttpClientCodec();
        }
        
        @Override
        public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
            this.codec.handlerAdded(ctx);
        }
        
        @Override
        public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
            this.codec.handlerRemoved(ctx);
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
            this.codec.exceptionCaught(ctx, cause);
        }
        
        @Override
        public void channelRegistered(final ChannelHandlerContext ctx) throws Exception {
            this.codec.channelRegistered(ctx);
        }
        
        @Override
        public void channelUnregistered(final ChannelHandlerContext ctx) throws Exception {
            this.codec.channelUnregistered(ctx);
        }
        
        @Override
        public void channelActive(final ChannelHandlerContext ctx) throws Exception {
            this.codec.channelActive(ctx);
        }
        
        @Override
        public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
            this.codec.channelInactive(ctx);
        }
        
        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            this.codec.channelRead(ctx, msg);
        }
        
        @Override
        public void channelReadComplete(final ChannelHandlerContext ctx) throws Exception {
            this.codec.channelReadComplete(ctx);
        }
        
        @Override
        public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
            this.codec.userEventTriggered(ctx, evt);
        }
        
        @Override
        public void channelWritabilityChanged(final ChannelHandlerContext ctx) throws Exception {
            this.codec.channelWritabilityChanged(ctx);
        }
        
        @Override
        public void bind(final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
            this.codec.bind(ctx, localAddress, promise);
        }
        
        @Override
        public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
            this.codec.connect(ctx, remoteAddress, localAddress, promise);
        }
        
        @Override
        public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
            this.codec.disconnect(ctx, promise);
        }
        
        @Override
        public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
            this.codec.close(ctx, promise);
        }
        
        @Override
        public void deregister(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
            this.codec.deregister(ctx, promise);
        }
        
        @Override
        public void read(final ChannelHandlerContext ctx) throws Exception {
            this.codec.read(ctx);
        }
        
        @Override
        public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
            this.codec.write(ctx, msg, promise);
        }
        
        @Override
        public void flush(final ChannelHandlerContext ctx) throws Exception {
            this.codec.flush(ctx);
        }
    }
}

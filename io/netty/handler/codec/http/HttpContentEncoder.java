package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderResult;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayDeque;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Queue;
import io.netty.handler.codec.MessageToMessageCodec;

public abstract class HttpContentEncoder extends MessageToMessageCodec<HttpRequest, HttpObject>
{
    private static final CharSequence ZERO_LENGTH_HEAD;
    private static final CharSequence ZERO_LENGTH_CONNECT;
    private static final int CONTINUE_CODE;
    private final Queue<CharSequence> acceptEncodingQueue;
    private EmbeddedChannel encoder;
    private State state;
    
    public HttpContentEncoder() {
        this.acceptEncodingQueue = new ArrayDeque<CharSequence>();
        this.state = State.AWAIT_HEADERS;
    }
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return msg instanceof HttpContent || msg instanceof HttpResponse;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final HttpRequest msg, final List<Object> out) throws Exception {
        final List<String> acceptEncodingHeaders = msg.headers().getAll(HttpHeaderNames.ACCEPT_ENCODING);
        CharSequence acceptEncoding = null;
        switch (acceptEncodingHeaders.size()) {
            case 0: {
                acceptEncoding = HttpContentDecoder.IDENTITY;
                break;
            }
            case 1: {
                acceptEncoding = acceptEncodingHeaders.get(0);
                break;
            }
            default: {
                acceptEncoding = StringUtil.join(",", acceptEncodingHeaders);
                break;
            }
        }
        final HttpMethod method = msg.method();
        if (HttpMethod.HEAD.equals(method)) {
            acceptEncoding = HttpContentEncoder.ZERO_LENGTH_HEAD;
        }
        else if (HttpMethod.CONNECT.equals(method)) {
            acceptEncoding = HttpContentEncoder.ZERO_LENGTH_CONNECT;
        }
        this.acceptEncodingQueue.add(acceptEncoding);
        out.add(ReferenceCountUtil.retain(msg));
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final HttpObject msg, final List<Object> out) throws Exception {
        final boolean isFull = msg instanceof HttpResponse && msg instanceof LastHttpContent;
        switch (this.state) {
            case AWAIT_HEADERS: {
                ensureHeaders(msg);
                assert this.encoder == null;
                final HttpResponse res = (HttpResponse)msg;
                final int code = res.status().code();
                CharSequence acceptEncoding;
                if (code == HttpContentEncoder.CONTINUE_CODE) {
                    acceptEncoding = null;
                }
                else {
                    acceptEncoding = this.acceptEncodingQueue.poll();
                    if (acceptEncoding == null) {
                        throw new IllegalStateException("cannot send more responses than requests");
                    }
                }
                if (isPassthru(res.protocolVersion(), code, acceptEncoding)) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        break;
                    }
                    out.add(ReferenceCountUtil.retain(res));
                    this.state = State.PASS_THROUGH;
                    break;
                }
                else {
                    if (isFull && !((ByteBufHolder)res).content().isReadable()) {
                        out.add(ReferenceCountUtil.retain(res));
                        break;
                    }
                    final Result result = this.beginEncode(res, acceptEncoding.toString());
                    if (result == null) {
                        if (isFull) {
                            out.add(ReferenceCountUtil.retain(res));
                            break;
                        }
                        out.add(ReferenceCountUtil.retain(res));
                        this.state = State.PASS_THROUGH;
                        break;
                    }
                    else {
                        this.encoder = result.contentEncoder();
                        res.headers().set(HttpHeaderNames.CONTENT_ENCODING, result.targetContentEncoding());
                        if (isFull) {
                            final HttpResponse newRes = new DefaultHttpResponse(res.protocolVersion(), res.status());
                            newRes.headers().set(res.headers());
                            out.add(newRes);
                            ensureContent(res);
                            this.encodeFullResponse(newRes, (HttpContent)res, out);
                            break;
                        }
                        res.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
                        res.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                        out.add(ReferenceCountUtil.retain(res));
                        this.state = State.AWAIT_CONTENT;
                        if (!(msg instanceof HttpContent)) {
                            break;
                        }
                    }
                }
                break;
            }
            case AWAIT_CONTENT: {
                ensureContent(msg);
                if (this.encodeContent((HttpContent)msg, out)) {
                    this.state = State.AWAIT_HEADERS;
                    break;
                }
                break;
            }
            case PASS_THROUGH: {
                ensureContent(msg);
                out.add(ReferenceCountUtil.retain(msg));
                if (msg instanceof LastHttpContent) {
                    this.state = State.AWAIT_HEADERS;
                    break;
                }
                break;
            }
        }
    }
    
    private void encodeFullResponse(final HttpResponse newRes, final HttpContent content, final List<Object> out) {
        final int existingMessages = out.size();
        this.encodeContent(content, out);
        if (HttpUtil.isContentLengthSet(newRes)) {
            int messageSize = 0;
            for (int i = existingMessages; i < out.size(); ++i) {
                final Object item = out.get(i);
                if (item instanceof HttpContent) {
                    messageSize += ((HttpContent)item).content().readableBytes();
                }
            }
            HttpUtil.setContentLength(newRes, messageSize);
        }
        else {
            newRes.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        }
    }
    
    private static boolean isPassthru(final HttpVersion version, final int code, final CharSequence httpMethod) {
        return code < 200 || code == 204 || code == 304 || httpMethod == HttpContentEncoder.ZERO_LENGTH_HEAD || (httpMethod == HttpContentEncoder.ZERO_LENGTH_CONNECT && code == 200) || version == HttpVersion.HTTP_1_0;
    }
    
    private static void ensureHeaders(final HttpObject msg) {
        if (!(msg instanceof HttpResponse)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
        }
    }
    
    private static void ensureContent(final HttpObject msg) {
        if (!(msg instanceof HttpContent)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
        }
    }
    
    private boolean encodeContent(final HttpContent c, final List<Object> out) {
        final ByteBuf content = c.content();
        this.encode(content, out);
        if (c instanceof LastHttpContent) {
            this.finishEncode(out);
            final LastHttpContent last = (LastHttpContent)c;
            final HttpHeaders headers = last.trailingHeaders();
            if (headers.isEmpty()) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            }
            else {
                out.add(new ComposedLastHttpContent(headers, DecoderResult.SUCCESS));
            }
            return true;
        }
        return false;
    }
    
    protected abstract Result beginEncode(final HttpResponse p0, final String p1) throws Exception;
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        this.cleanupSafely(ctx);
        super.handlerRemoved(ctx);
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        this.cleanupSafely(ctx);
        super.channelInactive(ctx);
    }
    
    private void cleanup() {
        if (this.encoder != null) {
            this.encoder.finishAndReleaseAll();
            this.encoder = null;
        }
    }
    
    private void cleanupSafely(final ChannelHandlerContext ctx) {
        try {
            this.cleanup();
        }
        catch (final Throwable cause) {
            ctx.fireExceptionCaught(cause);
        }
    }
    
    private void encode(final ByteBuf in, final List<Object> out) {
        this.encoder.writeOutbound(in.retain());
        this.fetchEncoderOutput(out);
    }
    
    private void finishEncode(final List<Object> out) {
        if (this.encoder.finish()) {
            this.fetchEncoderOutput(out);
        }
        this.encoder = null;
    }
    
    private void fetchEncoderOutput(final List<Object> out) {
        while (true) {
            final ByteBuf buf = this.encoder.readOutbound();
            if (buf == null) {
                break;
            }
            if (!buf.isReadable()) {
                buf.release();
            }
            else {
                out.add(new DefaultHttpContent(buf));
            }
        }
    }
    
    static {
        ZERO_LENGTH_HEAD = "HEAD";
        ZERO_LENGTH_CONNECT = "CONNECT";
        CONTINUE_CODE = HttpResponseStatus.CONTINUE.code();
    }
    
    private enum State
    {
        PASS_THROUGH, 
        AWAIT_HEADERS, 
        AWAIT_CONTENT;
    }
    
    public static final class Result
    {
        private final String targetContentEncoding;
        private final EmbeddedChannel contentEncoder;
        
        public Result(final String targetContentEncoding, final EmbeddedChannel contentEncoder) {
            this.targetContentEncoding = ObjectUtil.checkNotNull(targetContentEncoding, "targetContentEncoding");
            this.contentEncoder = ObjectUtil.checkNotNull(contentEncoder, "contentEncoder");
        }
        
        public String targetContentEncoding() {
            return this.targetContentEncoding;
        }
        
        public EmbeddedChannel contentEncoder() {
            return this.contentEncoder;
        }
    }
}

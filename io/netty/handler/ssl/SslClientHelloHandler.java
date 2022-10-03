package io.netty.handler.ssl;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.channel.ChannelPromise;
import java.net.SocketAddress;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.handler.codec.DecoderException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.buffer.ByteBufUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;

public abstract class SslClientHelloHandler<T> extends ByteToMessageDecoder implements ChannelOutboundHandler
{
    private static final InternalLogger logger;
    private boolean handshakeFailed;
    private boolean suppressRead;
    private boolean readPending;
    private ByteBuf handshakeBuffer;
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (!this.suppressRead && !this.handshakeFailed) {
            try {
                int readerIndex = in.readerIndex();
                int readableBytes = in.readableBytes();
                int handshakeLength = -1;
                while (readableBytes >= 5) {
                    final int contentType = in.getUnsignedByte(readerIndex);
                    switch (contentType) {
                        case 20:
                        case 21: {
                            final int len = SslUtils.getEncryptedPacketLength(in, readerIndex);
                            if (len == -2) {
                                this.handshakeFailed = true;
                                final NotSslRecordException e = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(in));
                                in.skipBytes(in.readableBytes());
                                ctx.fireUserEventTriggered((Object)new SniCompletionEvent(e));
                                SslUtils.handleHandshakeFailure(ctx, e, true);
                                throw e;
                            }
                            if (len == -1) {
                                return;
                            }
                            this.select(ctx, null);
                            return;
                        }
                        case 22: {
                            final int majorVersion = in.getUnsignedByte(readerIndex + 1);
                            if (majorVersion != 3) {
                                break;
                            }
                            int packetLength = in.getUnsignedShort(readerIndex + 3) + 5;
                            if (readableBytes < packetLength) {
                                return;
                            }
                            if (packetLength == 5) {
                                this.select(ctx, null);
                                return;
                            }
                            final int endOffset = readerIndex + packetLength;
                            if (handshakeLength == -1) {
                                if (readerIndex + 4 > endOffset) {
                                    return;
                                }
                                final int handshakeType = in.getUnsignedByte(readerIndex + 5);
                                if (handshakeType != 1) {
                                    this.select(ctx, null);
                                    return;
                                }
                                handshakeLength = in.getUnsignedMedium(readerIndex + 5 + 1);
                                readerIndex += 4;
                                packetLength -= 4;
                                if (handshakeLength + 4 + 5 <= packetLength) {
                                    readerIndex += 5;
                                    this.select(ctx, in.retainedSlice(readerIndex, handshakeLength));
                                    return;
                                }
                                if (this.handshakeBuffer == null) {
                                    this.handshakeBuffer = ctx.alloc().buffer(handshakeLength);
                                }
                                else {
                                    this.handshakeBuffer.clear();
                                }
                            }
                            this.handshakeBuffer.writeBytes(in, readerIndex + 5, packetLength - 5);
                            readerIndex += packetLength;
                            readableBytes -= packetLength;
                            if (handshakeLength <= this.handshakeBuffer.readableBytes()) {
                                final ByteBuf clientHello = this.handshakeBuffer.setIndex(0, handshakeLength);
                                this.handshakeBuffer = null;
                                this.select(ctx, clientHello);
                                return;
                            }
                            continue;
                        }
                    }
                    this.select(ctx, null);
                }
            }
            catch (final NotSslRecordException e2) {
                throw e2;
            }
            catch (final Exception e3) {
                if (SslClientHelloHandler.logger.isDebugEnabled()) {
                    SslClientHelloHandler.logger.debug("Unexpected client hello packet: " + ByteBufUtil.hexDump(in), e3);
                }
                this.select(ctx, null);
            }
        }
    }
    
    private void releaseHandshakeBuffer() {
        releaseIfNotNull(this.handshakeBuffer);
        this.handshakeBuffer = null;
    }
    
    private static void releaseIfNotNull(final ByteBuf buffer) {
        if (buffer != null) {
            buffer.release();
        }
    }
    
    private void select(final ChannelHandlerContext ctx, ByteBuf clientHello) throws Exception {
        try {
            final Future<T> future = this.lookup(ctx, clientHello);
            if (future.isDone()) {
                this.onLookupComplete(ctx, future);
            }
            else {
                this.suppressRead = true;
                final ByteBuf finalClientHello = clientHello;
                future.addListener(new FutureListener<T>() {
                    @Override
                    public void operationComplete(final Future<T> future) {
                        releaseIfNotNull(finalClientHello);
                        try {
                            SslClientHelloHandler.this.suppressRead = false;
                            try {
                                SslClientHelloHandler.this.onLookupComplete(ctx, future);
                            }
                            catch (final DecoderException err) {
                                ctx.fireExceptionCaught((Throwable)err);
                            }
                            catch (final Exception cause) {
                                ctx.fireExceptionCaught((Throwable)new DecoderException(cause));
                            }
                            catch (final Throwable cause2) {
                                ctx.fireExceptionCaught(cause2);
                            }
                        }
                        finally {
                            if (SslClientHelloHandler.this.readPending) {
                                SslClientHelloHandler.this.readPending = false;
                                ctx.read();
                            }
                        }
                    }
                });
                clientHello = null;
            }
        }
        catch (final Throwable cause) {
            PlatformDependent.throwException(cause);
        }
        finally {
            releaseIfNotNull(clientHello);
        }
    }
    
    @Override
    protected void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
        this.releaseHandshakeBuffer();
        super.handlerRemoved0(ctx);
    }
    
    protected abstract Future<T> lookup(final ChannelHandlerContext p0, final ByteBuf p1) throws Exception;
    
    protected abstract void onLookupComplete(final ChannelHandlerContext p0, final Future<T> p1) throws Exception;
    
    @Override
    public void read(final ChannelHandlerContext ctx) throws Exception {
        if (this.suppressRead) {
            this.readPending = true;
        }
        else {
            ctx.read();
        }
    }
    
    @Override
    public void bind(final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }
    
    @Override
    public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }
    
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }
    
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.close(promise);
    }
    
    @Override
    public void deregister(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        ctx.write(msg, promise);
    }
    
    @Override
    public void flush(final ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(SslClientHelloHandler.class);
    }
}
